package br.com.leonardo.dscatalog.dto;

import br.com.leonardo.dscatalog.entities.User;

public class UserInsertDTO extends UserDTO{

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
