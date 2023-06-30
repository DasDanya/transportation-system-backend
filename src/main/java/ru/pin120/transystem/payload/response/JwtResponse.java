package ru.pin120.transystem.payload.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {

    private Integer id;

    private String username;
    private String email;
    private String token;
    private String type = "Bearer";

    private List<String> roles;

    public JwtResponse(Integer id, String username, String email, String token, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.token = token;
        this.roles = roles;
    }



}
