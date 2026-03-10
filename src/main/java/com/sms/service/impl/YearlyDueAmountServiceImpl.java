package com.sms.service.impl;

import com.sms.model.FeeDepositDetails;
import com.sms.model.FeeDueDateDetails;
import com.sms.model.YearlyDueAmountDetails;
import com.sms.service.FeeDepositService;
import com.sms.service.FeeDueDateService;
import com.sms.service.YearlyDueAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class YearlyDueAmountServiceImpl implements YearlyDueAmountService {
    @Autowired
    private FeeDepositService feeDepositService;
    @Autowired
    private FeeDueDateService feeDueDateService;
    @Override
    public List<YearlyDueAmountDetails> calculateYearlyDueAmounts(int sessionId, String schoolCode) throws Exception {
        List<FeeDepositDetails> yearlyDeposits = feeDepositService.getYearlyTotalDeposit(sessionId, schoolCode);
        List<FeeDueDateDetails> exactFees = feeDueDateService.getExactFee(sessionId, schoolCode);

        Map<String, Double> depositMap = new HashMap<>();
        for(FeeDepositDetails deposit : yearlyDeposits){
            depositMap.put(deposit.getYear(), deposit.getTotalYearlyAmountDeposit());
        }
        List<YearlyDueAmountDetails> dueAmounts = new ArrayList<>();
        for(FeeDueDateDetails exactFee : exactFees){
            String year = exactFee.getYear();
            //double exactFeeAmount = exactFee.getExactFee();
            double todayCollection=exactFee.getTodayCollection();
            double totalFee = exactFee.getTotalFee();
            double totalDiscount = exactFee.getTotalDiscount();
            double totalPenalty = exactFee.getTotalPenalty();
            double feeAfterDiscount = exactFee.getFeeAfterDiscount();
            double exactFeeAfterDiscountAndPenalty = exactFee.getExactFeeAfterDiscountAndPenalty();
            double totalDeposit = depositMap.getOrDefault(year, 0.0);
            //double dueAmount = exactFeeAmount - totalDeposit;
            double dueAmount = exactFeeAfterDiscountAndPenalty - totalDeposit;

            YearlyDueAmountDetails yearlyDueAmountDetails = new YearlyDueAmountDetails(year, totalFee, totalDiscount, totalPenalty, feeAfterDiscount, exactFeeAfterDiscountAndPenalty, totalDeposit, dueAmount,todayCollection);
            dueAmounts.add(yearlyDueAmountDetails);
            System.out.println(yearlyDueAmountDetails);
        }
        return dueAmounts;
    }
}
