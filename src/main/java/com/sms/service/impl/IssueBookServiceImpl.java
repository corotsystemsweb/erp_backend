package com.sms.service.impl;

import com.sms.dao.IssueBookDao;
import com.sms.model.IssueBookDetails;
import com.sms.service.IssueBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueBookServiceImpl implements IssueBookService {
    @Autowired
    private IssueBookDao issueBookDao;
    @Override
    public IssueBookDetails addIssueBookDetails(IssueBookDetails issueBookDetails, String schoolCode) throws Exception {
        return issueBookDao.addIssueBookDetails(issueBookDetails,schoolCode);
    }

    @Override
    public List<IssueBookDetails> getIssueBookDetails(String schoolCode) throws Exception {
        return issueBookDao.getIssueBookDetails(schoolCode);
    }

    @Override
    public IssueBookDetails getIssueBookDetailsById(int issueBookId, String schoolCode) throws Exception {
        return issueBookDao.getIssueBookDetailsById(issueBookId,schoolCode);
    }

    @Override
    public IssueBookDetails updateIssueBookDetails(IssueBookDetails issueBookDetails, int issueBookId, String schoolCode) throws Exception {
        return issueBookDao.updateIssueBookDetails(issueBookDetails,issueBookId,schoolCode);
    }

    @Override
    public boolean issueBookSoftDelete(int issueBookId, String schoolCode) throws Exception {
        return issueBookDao.issueBookSoftDelete(issueBookId,schoolCode);
    }

    @Override
    public List<IssueBookDetails> getIssueBookDetailsbBySearchText(String searchText, String schoolCode) throws Exception {
        return issueBookDao.getIssueBookDetailsbBySearchText(searchText,schoolCode);
    }
}
