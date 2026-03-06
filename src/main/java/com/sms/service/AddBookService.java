package com.sms.service;

import com.sms.model.AddBookDetails;

import java.util.List;

public interface AddBookService {
    public AddBookDetails addNewBookDetails(AddBookDetails addBookDetails, String schoolCode) throws  Exception;
    public AddBookDetails getNewBookById(int bookId, String schoolCode) throws Exception;
    public List<AddBookDetails> getAllBook(String schoolCode) throws Exception;
    public AddBookDetails updateById(AddBookDetails addBookDetails,int bookId,String schoolCode) throws Exception;
    public boolean softDeleteBook(int bookId, String schoolCode)throws Exception;
    public List<AddBookDetails> getAllBookBySearchText(String searchText,String schoolCode) throws Exception;

}
