package com.project.auth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class CreateAddressDto {
    private String pincode;
    private String village;
    private String city;
    private String district;
    private String state;
    private String country;
}
