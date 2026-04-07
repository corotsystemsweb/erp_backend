package com.sms.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnBookDetails {
    private int returnBookId;
    private int schoolId;
    private int sessionId;
    private int classId;
    private int sectionId;

    private int studentId;
    private int bookId;
    private int issueBookId;

    private Date returnDate;
    private String bookCondition;
    private Double fineAmount;
    private String remarks;

    private int updatedBy;
    private Timestamp updateDateTime;

    private boolean deleted;

    private String studentName;
    private String bookName;
    private String isbn;
    private String authorName;
}
