package com.sms.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class RegistrationDetails {
    private int userId;
    private String schoolName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
    private String schoolCode;
    private String token;
    private Integer[] roleId;
    private List<String> role;
    private Date createdDate;
    private String accessToken;
    private String refreshToken;
    private boolean isActive;
}
