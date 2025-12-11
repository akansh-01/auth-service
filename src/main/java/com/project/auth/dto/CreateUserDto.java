package com.project.auth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreateUserDto {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String password;
    private String role;
}
