package com.sms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class FeeDetail {
    @JsonProperty("type")
    private String type;
    @JsonProperty("fee_type")
    private String feeType;

    @JsonProperty("due_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dueDate;

    @JsonProperty("original_amount")
    private Double originalAmount;

    @JsonProperty("discount_description")
    private String discountDescription;

    @JsonProperty("discount_rate")
    private String discountRate;

    @JsonProperty("discount_amount")
    private Double discountAmount;

    @JsonProperty("penalty_amount")
    private Double penaltyAmount;

    @JsonProperty("amount_paid")
    private Double amountPaid;

    @JsonProperty("balance")
    private Double balance;
    @JsonProperty("additional_discount")
    private Double additionalDiscount;
}