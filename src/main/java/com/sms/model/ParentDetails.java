package com.sms.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ParentDetails {
    private Integer parentId;
    private int schoolId;
    private String uuid;
    private String firstName;
    private String lastName;
    private Date dob;
    private String phoneNumber;
    private String emergencyPhoneNumber;
    private String whatsappNumber;
    private String email;
    private String gender;
    private String parentType;
    private String parentImage;
    private String qualification;
    private String aadharNumber;
    private String companyName;
    private String designation;
    private String companyAddress;
    private String companyPhone;
    private String address;
    private String city;
    private String state;
    private int zipcode;
    private int updatedBy;
    private Date updatedDate;
    private Date createDate;
    private Map<String, String> parentImages;
    @JsonAnyGetter
    public Map<String, String> getParentImages() {
        if (this.parentImages == null) {
            this.parentImages = new java.util.HashMap<>();
        }
        return this.parentImages;
    }

}