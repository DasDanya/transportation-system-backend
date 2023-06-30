package ru.pin120.transystem.payload.request;

//import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String username;


    private String password;

}
