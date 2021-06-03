package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserModel {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    private String description;

}
