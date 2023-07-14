package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class SignupRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String email;

    private Set<String> role;

    @NotBlank
    private String password;
}
