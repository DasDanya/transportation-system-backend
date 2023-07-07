package ru.pin120.transystem.payload.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    @Size(min = 4, max = 8, message = "Длина логина должна быть не менее 4 и не более 8 символов")
    private String username;

    @Email(message = "Введенная почта является недействительной")
    private String email;


    @Size(min= 8, max = 50, message="Длина пароля должна быть не менее 8 и не более 50 символов")
    private String password;


    private Set<String> roles;
}
