package com.sms.dao;

import com.sms.model.DiscountCodeDetails;
import java.util.List;

public interface DiscountCodeDao {
    public DiscountCodeDetails addDiscount(DiscountCodeDetails discountCodeDetails, String schoolCode) throws Exception;
    public DiscountCodeDetails getDiscountById(int dcId, String schoolCode) throws Exception;
    public List<DiscountCodeDetails> getAllDiscount(String schoolCode) throws Exception;
    public DiscountCodeDetails updateDiscountById(DiscountCodeDetails discountCodeDetails, int dcId, String schoolCode) throws Exception;
    public boolean deleteDiscount(int dcId, String schoolCode) throws Exception;
}
