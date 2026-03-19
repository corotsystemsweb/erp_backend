package com.sms.dao;

import com.sms.model.IssueBookDetails;

import java.util.List;

public interface IssueBookDao {
    public IssueBookDetails addIssueBookDetails(IssueBookDetails issueBookDetails, String schoolCode) throws Exception;

    public List<IssueBookDetails> getIssueBookDetails(String schoolCode) throws Exception;
    public IssueBookDetails getIssueBookDetailsById(int issueBookId ,String schoolCode) throws Exception;
    public IssueBookDetails updateIssueBookDetails(IssueBookDetails issueBookDetails,int issueBookId, String schoolCode) throws Exception;
    public boolean issueBookSoftDelete(int issueBookId,String schoolCode) throws Exception;

    public List<IssueBookDetails> getIssueBookDetailsbBySearchText(String searchText, String schoolCode) throws Exception;
}
