package com.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouteGroupDto {
    private String hashValue;
    private Integer vehicleId;
    private String vehicleNumber;
    private String boardingPoint;
    private String destination;
    private BigDecimal maxFee;

    private List<RouteStopDTO> stops;
//    -------------------------------------------------added by karan------------------
    private String routeName;
    private String routeCode;
    private Double totalDistance;
    private Integer estimatedTime;



}
