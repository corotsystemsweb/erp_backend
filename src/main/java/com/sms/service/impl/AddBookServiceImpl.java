package com.sms.service.impl;

import com.sms.dao.AddBookDao;
import com.sms.model.AddBookDetails;
import com.sms.service.AddBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddBookServiceImpl implements AddBookService {
    @Autowired
    private AddBookDao addBookDao;
    @Override
    public AddBookDetails addNewBookDetails(AddBookDetails addBookDetails, String schoolCode) throws Exception {
        return addBookDao.addNewBookDetails(addBookDetails,schoolCode);
    }

    @Override
    public AddBookDetails getNewBookById(int bookId, String schoolCode) throws Exception {
        return addBookDao.getNewBookById(bookId,schoolCode);
    }

    @Override
    public List<AddBookDetails> getAllBook(String schoolCode) throws Exception {
        return addBookDao.getAllBook(schoolCode);
    }

    @Override
    public AddBookDetails updateById(AddBookDetails addBookDetails, int bookId, String schoolCode) throws Exception {
        return addBookDao.updateById(addBookDetails,bookId,schoolCode);
    }

    @Override
    public boolean softDeleteBook(int bookId, String schoolCode) throws Exception {
        return addBookDao.softDeleteBook(bookId,schoolCode);
    }

    @Override
    public List<AddBookDetails> getAllBookBySearchText(String searchText, String schoolCode) throws Exception {
        return addBookDao.getAllBookBySearchText(searchText,schoolCode);
    }
}
