package com.sms.service.impl;

import com.sms.dao.ReturnBookDao;
import com.sms.model.ReturnBookDetails;
import com.sms.service.ReturnBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sms.dao.IssueBookDao;
import com.sms.model.IssueBookDetails;

import java.util.List;

@Service
public class ReturnBookServiceImpl implements ReturnBookService {

    @Autowired
    private ReturnBookDao returnBookDao;

    // 🔹 ADD RETURN BOOK
//    @Override
//    public ReturnBookDetails addReturnBook(ReturnBookDetails details, String schoolCode) throws Exception {
//        return returnBookDao.addReturnBook(details, schoolCode);
//    }

    @Autowired
    private IssueBookDao issueBookDao;

    @Override
    public ReturnBookDetails addReturnBook(ReturnBookDetails details, String schoolCode) throws Exception {

        // ✅ STEP 1: fetch issue_book
        IssueBookDetails issueBook = issueBookDao.getIssueBookDetailsById(
                details.getIssueBookId(), schoolCode
        );

        if (issueBook == null) {
            throw new RuntimeException("Issue book not found");
        }

        // ❌ STEP 2: check already returned
        if ("returned".equalsIgnoreCase(issueBook.getStatus())) {
            throw new RuntimeException("Book already returned");
        }

        // ✅ STEP 3: insert into return_book
        ReturnBookDetails saved = returnBookDao.addReturnBook(details, schoolCode);

        // ✅ STEP 4: update issue_book status
        issueBook.setStatus("returned");
        issueBook.setDueDate(issueBook.getDueDate());
        issueBook.setUpdatedBy(details.getUpdatedBy());
        issueBook.setUpdateDateTime(new java.sql.Timestamp(System.currentTimeMillis()));

        issueBookDao.updateIssueBookDetails(
                issueBook,
                details.getIssueBookId(),
                schoolCode
        );

        return saved;
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