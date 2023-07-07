package ru.pin120.transystem.payload.request;


import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Size(min = 4, max = 8, message = "Длина логина должна быть не менее 4 и не более 8 символов")
    private String username;

    @Size(min= 8, max = 50, message="Длина пароля должна быть не менее 8 и не более 50 символов")
    private String password;

}
