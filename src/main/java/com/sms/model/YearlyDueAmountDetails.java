package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YearlyDueAmountDetails {
    private double totalFee;
    private double totalDiscount;
    private double totalPenalty;
    private double feeAfterDiscount;
    private double exactFeeAfterDiscountAndPenalty;
    private String year;
    private double totalDeposit;
    private double dueAmount;
    private double todayCollection;


    public YearlyDueAmountDetails(String year, double totalFee, double totalDiscount, double totalPenalty, double feeAfterDiscount, double exactFeeAfterDiscountAndPenalty, double totalDeposit, double dueAmount, double todayCollection) {
        this.year = year;
        this.totalFee = totalFee;
        this.totalDiscount = totalDiscount;
        this.totalPenalty = totalPenalty;
        this.feeAfterDiscount = feeAfterDiscount;
        this.exactFeeAfterDiscountAndPenalty = exactFeeAfterDiscountAndPenalty;
        this.totalDeposit = totalDeposit;
        this.dueAmount = dueAmount;
        this.todayCollection = todayCollection;
    }
}
