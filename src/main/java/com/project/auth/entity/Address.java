package com.project.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "address",schema = "auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pincode", nullable = false)
    private String pincode;

    @Column(name = "village", nullable = false)
    private String village;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}

