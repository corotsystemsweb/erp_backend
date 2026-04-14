package com.sms.dao;

import com.sms.model.BookDamageLossReport;
import java.util.List;

public interface BookDamageLossDao {

    BookDamageLossReport addBookDamageLossReport(BookDamageLossReport report, String schoolCode) throws Exception;

    List<BookDamageLossReport> getAllReports(String schoolCode) throws Exception;
}