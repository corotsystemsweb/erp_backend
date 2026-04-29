package com.sms.service.impl;

import com.sms.dao.BookDamageLossDao;
import com.sms.model.BookDamageLossReport;
import com.sms.service.BookDamageLossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookDamageLossServiceImpl implements BookDamageLossService {

    @Autowired
    private BookDamageLossDao dao;

    @Override
    public BookDamageLossReport addReport(BookDamageLossReport report, String schoolCode) throws Exception {

        if (report.getMemberId() == 0) {
            throw new RuntimeException("Member ID required");
        }

        if (report.getBookId() == 0) {
            throw new RuntimeException("Book ID required");
        }

        if (report.getReportType() == null || report.getReportType().isEmpty()) {
            throw new RuntimeException("Report type required");
        }

        return dao.addBookDamageLossReport(report, schoolCode);
    }

    @Override
    public List<BookDamageLossReport> getAllReports(String schoolCode) throws Exception {
        return dao.getAllReports(schoolCode);
    }
}