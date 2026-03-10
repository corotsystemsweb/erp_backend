package com.sms.service;

import com.sms.model.BookStockDetails;

import java.util.List;

public interface BookStockService {
    public BookStockDetails addBookStock(BookStockDetails bookStockDetails, String schoolCode)throws  Exception;
    public List<BookStockDetails> getAllBookStock(String schoolCode) throws Exception;
    public List<BookStockDetails> getAllBookStockBySearchText(String searchText,String schoolCode) throws Exception;

}
