package br.com.leonardo.dscatalog.service.validation;

import java.util.ArrayList;
import java.util.List;

import br.com.leonardo.dscatalog.entities.User;
import br.com.leonardo.dscatalog.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import br.com.leonardo.dscatalog.dto.FieldMessage;
import br.com.leonardo.dscatalog.dto.UserInsertDTO;
import org.springframework.beans.factory.annotation.Autowired;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    private final UserRepository repository;

    @Autowired
    public UserInsertValidator(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        User user = repository.findByEmail(dto.getEmail());
        if(user != null) {
            list.add(new FieldMessage("email", "Esse email já existe"));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}