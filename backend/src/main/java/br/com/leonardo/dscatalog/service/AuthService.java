package br.com.leonardo.dscatalog.service;

import br.com.leonardo.dscatalog.dto.EmailDTO;
import br.com.leonardo.dscatalog.dto.NewPasswordDTO;
import br.com.leonardo.dscatalog.entities.PasswordRecover;
import br.com.leonardo.dscatalog.entities.User;
import br.com.leonardo.dscatalog.repositories.PasswordRecoverRepository;
import br.com.leonardo.dscatalog.repositories.UserRepository;
import br.com.leonardo.dscatalog.service.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ResolutionException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Value(value = "${email.password-recover.token.minutes}")
    private Long tokenMinutes;
    @Value(value = "${email.password-recover.uri}")
    private String recoverUri;

    private final UserRepository userRepository;
    private final PasswordRecoverRepository passwordRecoverRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordRecoverRepository passwordRecoverRepository,
                       EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordRecoverRepository = passwordRecoverRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createRecoverToken(EmailDTO body) {
        User user = userRepository.findByEmail(body.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("Email não encontrado");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover recover = new PasswordRecover();
        recover.setEmail(body.getEmail());
        recover.setToken(token);
        recover.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60));
        recover = passwordRecoverRepository.save(recover);
        String text = "Acesse o link para definir uma nova senha\n\n"
                + recoverUri + token + ". Validade de " + tokenMinutes + " minutos";
        emailService.sendEmail(body.getEmail(), "Recuperação de Senha", text);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO body) {
        List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(body.getToken(), Instant.now());
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Token inválido");
        }

        User user = userRepository.findByEmail(result.getFirst().getEmail());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        user = userRepository.save(user);
    }
}
