package com.sms.service.impl;

import com.sms.dao.ReturnBookDao;
import com.sms.model.ReturnBookDetails;
import com.sms.service.ReturnBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReturnBookServiceImpl implements ReturnBookService {

    @Autowired
    private ReturnBookDao returnBookDao;

    // 🔹 ADD RETURN BOOK
    @Override
    public ReturnBookDetails addReturnBook(ReturnBookDetails details, String schoolCode) throws Exception {
        return returnBookDao.addReturnBook(details, schoolCode);
    }

    // 🔹 GET ALL RETURN BOOKS
    @Override
    public List<ReturnBookDetails> getReturnBookDetails(String schoolCode) throws Exception {
        return returnBookDao.getReturnBookDetails(schoolCode);
    }

    // 🔹 UPDATE RETURN BOOKS
    @Override
    public ReturnBookDetails updateReturnBook(ReturnBookDetails details, int returnBookId, String schoolCode) throws Exception {
        return returnBookDao.updateReturnBook(details, returnBookId, schoolCode);
    }
}