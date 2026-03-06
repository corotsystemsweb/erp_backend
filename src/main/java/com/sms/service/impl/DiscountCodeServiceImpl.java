package com.sms.service.impl;

import com.sms.dao.DiscountCodeDao;
import com.sms.model.DiscountCodeDetails;
import com.sms.service.DiscountCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountCodeServiceImpl implements DiscountCodeService {
    @Autowired
    private DiscountCodeDao discountCodeDao;
    @Override
    public DiscountCodeDetails addDiscount(DiscountCodeDetails discountCodeDetails, String schoolCode) throws Exception {
        return discountCodeDao.addDiscount(discountCodeDetails, schoolCode);
    }

    @Override
    public DiscountCodeDetails getDiscountById(int dcId, String schoolCode) throws Exception {
        return discountCodeDao.getDiscountById(dcId, schoolCode);
    }

    @Override
    public List<DiscountCodeDetails> getAllDiscount(String schoolCode) throws Exception {
        return discountCodeDao.getAllDiscount(schoolCode);
    }

    @Override
    public DiscountCodeDetails updateDiscountById(DiscountCodeDetails discountCodeDetails, int dcId, String schoolCode) throws Exception {
        return discountCodeDao.updateDiscountById(discountCodeDetails, dcId, schoolCode);
    }

    @Override
    public boolean deleteDiscount(int dcId, String schoolCode) throws Exception {
        return discountCodeDao.deleteDiscount(dcId, schoolCode);
    }
}
