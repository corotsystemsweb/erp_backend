package com.sms.service.impl;

import com.sms.dao.BookCategoryDao;
import com.sms.model.BookCategroyDetails;
import com.sms.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCategoryServiceImpl implements BookCategoryService {
    @Autowired
    private BookCategoryDao bookCategoryDao;

    @Override
    public BookCategroyDetails addBookCategory(BookCategroyDetails bookCategroyDetails, String schoolCode) throws Exception {
        return bookCategoryDao.addBookCategory(bookCategroyDetails,schoolCode);
    }

    @Override
    public BookCategroyDetails getBookCategoryById(int bookCategoryId, String schoolCode) throws Exception {
        return bookCategoryDao.getBookCategoryById(bookCategoryId,schoolCode);
    }

    @Override
    public List<BookCategroyDetails> getBookCategory(String schoolCode) throws Exception {
        return bookCategoryDao.getBookCategory(schoolCode);
    }

    @Override
    public BookCategroyDetails updateById(BookCategroyDetails bookCategroyDetails, int bookCategoryId, String schoolCode) throws Exception {
        return bookCategoryDao.updateById(bookCategroyDetails,bookCategoryId,schoolCode);
    }

    @Override
    public Boolean deleteBookCategory(int bookCategoryId, String schoolCode) throws Exception {
        return bookCategoryDao.deleteBookCategory(bookCategoryId,schoolCode);
    }

    @Override
    public List<BookCategroyDetails> getBookCategoryBySearchText(String searchText, String schoolCode) throws Exception {
        return bookCategoryDao.getBookCategoryBySearchText(searchText,schoolCode);
    }
}
