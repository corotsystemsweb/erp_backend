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
public class StudentTransportRequest {
    private StudentTransportDetails transportDetails;
    private List<TransportFeeDue> transportFeeDue;
}
