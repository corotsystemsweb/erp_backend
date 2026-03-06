package com.sms.dao;

import com.sms.model.BookCategroyDetails;

import java.util.List;

public interface BookCategoryDao {
    public BookCategroyDetails addBookCategory(BookCategroyDetails bookCategroyDetails, String schoolCode) throws Exception;
    public BookCategroyDetails getBookCategoryById(int bookCategoryId,String schoolCode) throws Exception;
    public List<BookCategroyDetails> getBookCategory(String schoolCode) throws Exception;
    public BookCategroyDetails updateById(BookCategroyDetails bookCategroyDetails,int bookCategoryId,String schoolCode) throws  Exception;
    public Boolean deleteBookCategory(int bookCategoryId,String schoolCode) throws  Exception;

    public List<BookCategroyDetails> getBookCategoryBySearchText(String searchText,String schoolCode) throws Exception;
}
