package com.example.shopapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "social_accounts")
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", length = 20, nullable = false)
    private String provider;

    @Column(name = "provider_id", length = 50, nullable = false)
    private String providerId;

    @Column(name = "email", length = 150, nullable = false)
    private String email;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
