package com.sms.service;

import com.sms.model.BookDamageLossReport;
import java.util.List;

public interface BookDamageLossService {

    BookDamageLossReport addReport(BookDamageLossReport report, String schoolCode) throws Exception;

    List<BookDamageLossReport> getAllReports(String schoolCode) throws Exception;
}