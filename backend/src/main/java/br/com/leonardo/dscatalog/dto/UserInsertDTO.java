package br.com.leonardo.dscatalog.dto;

import br.com.leonardo.dscatalog.entities.User;
import br.com.leonardo.dscatalog.service.validation.UserInsertValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@UserInsertValid
public class UserInsertDTO extends UserDTO{

    @Size(min = 8, message = "Deve ter no mínimo 8 caracteres")
    @NotBlank(message = "Campo obrigatório")
    private String password;

    public UserInsertDTO() {
    }

    public UserInsertDTO(Long id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email);
        this.password = password;
    }

    public UserInsertDTO(User entity, String password) {
        super(entity);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
