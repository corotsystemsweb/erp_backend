package com.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentFullResponseDetails {
    private List<ParentDetails> parentDetails;
    private StudentDetails studentPersonalDetails;
    private StudentDetails studentAcademicDetails;
    private StudentTransportDetails studentTransportDetails;
    private List<TransportFeeDue> transportFeeDue;
}
