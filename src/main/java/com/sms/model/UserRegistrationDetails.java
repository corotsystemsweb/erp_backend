package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserRegistrationDetails {
    private int userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
    private String confirmPassword;
    private List<String> role;
    private Integer[] roleId;
    private int allRoleId;
    private String allRole;
    private String schoolCode;
    private Date createdDate;
    private boolean isActive;
}
