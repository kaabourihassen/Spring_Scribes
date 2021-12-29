package com.socialmedia.scribes.entities;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String fullName;
    private String email;
    private String password;

    public RegistrationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }





}

