package com.sms.dao;

import com.sms.model.ReturnBookDetails;

import java.util.List;

public interface ReturnBookDao {
    ReturnBookDetails addReturnBook(ReturnBookDetails details, String schoolCode) throws Exception;
    List<ReturnBookDetails> getReturnBookDetails(String schoolCode) throws Exception;
    ReturnBookDetails updateReturnBook(ReturnBookDetails details, int returnBookId, String schoolCode) throws Exception;

}
