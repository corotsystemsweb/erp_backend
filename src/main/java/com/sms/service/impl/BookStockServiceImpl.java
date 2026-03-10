package com.sms.service.impl;

import com.sms.dao.BookStockDao;
import com.sms.model.BookStockDetails;
import com.sms.service.BookStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookStockServiceImpl implements BookStockService {
    @Autowired
   private BookStockDao bookStockDao;
    @Override
    public BookStockDetails addBookStock(BookStockDetails bookStockDetails, String schoolCode) throws Exception {
        return bookStockDao.addBookStock(bookStockDetails,schoolCode);
    }

    @Override
    public List<BookStockDetails> getAllBookStock(String schoolCode) throws Exception {
        return bookStockDao.getAllBookStock(schoolCode);
    }

    @Override
    public List<BookStockDetails> getAllBookStockBySearchText(String searchText, String schoolCode) throws Exception {
        return bookStockDao.getAllBookStockBySearchText(searchText,schoolCode);
    }
}
