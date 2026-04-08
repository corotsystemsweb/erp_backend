package com.sms.model;

import lombok.*;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LibraryMemberDetails {

    private int memberId;

    private int schoolId;
    private int sessionId;

    private String memberType;

    private Integer studentId;
    private Integer staffId;

    private Integer classId;
    private Integer sectionId;

    private String memberName;

    private String email;
    private String phone;

    private Date validUntil;

    private int updatedBy;
    private Timestamp updateDateTime;

    private boolean deleted;
}