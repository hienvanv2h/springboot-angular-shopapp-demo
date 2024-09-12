package com.example.shopapp.responses.user;

import com.example.shopapp.models.Role;
import com.example.shopapp.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    private Long facebookAccountId;

    @JsonProperty("google_account_id")
    private Long googleAccountId;

    @JsonProperty("role")
    private Role role;

    public static UserDetailsResponse fromUser(User user) {
        return UserDetailsResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .isActive(user.isEnabled())
                .dateOfBirth(user.getDateOfBirth())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .role(user.getRole())
                .build();
    }
}
