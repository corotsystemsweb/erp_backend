package com.sms.service.impl;

import com.sms.dao.FeeDepositDao;
import com.sms.model.FeeDepositDetails;
import com.sms.service.FeeDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class FeeDepositServiceImpl implements FeeDepositService {
    @Autowired
    private FeeDepositDao feeDepositDao;
//    @Override
//    public FeeDepositDetails addFeeDeposit(FeeDepositDetails feeDepositDetails, String schoolCode) throws Exception {
//        return feeDepositDao.addFeeDeposit(feeDepositDetails, schoolCode);
//    }

    @Override
    public FeeDepositDetails addFeeDeposit(FeeDepositDetails feeDepositDetails, String schoolCode) throws Exception {
        // Validate payment date
        if (feeDepositDetails.getPaymentDate() == null) {
            throw new IllegalArgumentException("Payment date is required");
        }

        // Convert to LocalDate for accurate date comparison
        LocalDate paymentDate = feeDepositDetails.getPaymentDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();

        if (paymentDate.isAfter(currentDate)) {
            throw new IllegalArgumentException("Payment date cannot be in the future");
        }

        return feeDepositDao.addFeeDeposit(feeDepositDetails, schoolCode);
    }

    @Override
    public FeeDepositDetails getFeeDepositById(int fdId, String schoolCode) throws Exception {
        return feeDepositDao.getFeeDepositById(fdId, schoolCode);
    }

    @Override
    public List<FeeDepositDetails> getAllFeeDeposit(String schoolCode) throws Exception {
        return feeDepositDao.getAllFeeDeposit(schoolCode);
    }

    @Override
    public FeeDepositDetails updateFeeDepositById(FeeDepositDetails feeDepositDetails, int fdId, String schoolCode) throws Exception {
        return feeDepositDao.updateFeeDepositById(feeDepositDetails, fdId, schoolCode);
    }

    @Override
    public double getTotalFeeDeposit(String schoolCode) throws Exception {
        return feeDepositDao.getTotalFeeDeposit(schoolCode);
    }

    @Override
    public List<FeeDepositDetails> getTotalAmountPaidByStudents(String schoolCode) throws Exception {
        return feeDepositDao.getTotalAmountPaidByStudents(schoolCode);
    }

    @Override
    public FeeDepositDetails getTotalAmountPaidByParticularStudent(int studentId, String schoolCode) throws Exception {
        return feeDepositDao.getTotalAmountPaidByParticularStudent(studentId, schoolCode);
    }

    @Override
    public List<FeeDepositDetails> getYearlyTotalDeposit(int sessionId, String schoolCode) throws Exception {
        return feeDepositDao.getYearlyTotalDeposit(sessionId, schoolCode);
    }

    @Override
    public List<FeeDepositDetails> getUnsettledFeesByClassSectionSessionAndStudentId(int classId, int sectionId, int sessionId, int studentId, String schoolCode) throws Exception {
        return feeDepositDao.getUnsettledFeesByClassSectionSessionAndStudentId(classId, sectionId, sessionId, studentId, schoolCode);
    }

}
