package br.com.leonardo.dscatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewPasswordDTO {

    @NotBlank(message = "Campo obrigatório")
    private String token;
    @Size(min = 8, message = "Deve ter no mínimo 8 caracteres")
    @NotBlank(message = "Campo obrigatório")
    private String password;

    public NewPasswordDTO() {}

    public NewPasswordDTO(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }
}
