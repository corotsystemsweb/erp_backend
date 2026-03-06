package com.sms.dao;

import com.sms.model.BookStockDetails;

import java.util.List;

public interface BookStockDao {
    public BookStockDetails addBookStock(BookStockDetails bookStockDetails, String schoolCode)throws  Exception;
    public List<BookStockDetails> getAllBookStock(String schoolCode) throws Exception;
    public List<BookStockDetails> getAllBookStockBySearchText(String searchText,String schoolCode) throws Exception;
}
