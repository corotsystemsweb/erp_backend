package com.sms.controller;

import com.sms.model.BookDamageLossReport;
import com.sms.service.BookDamageLossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library/book-report")
public class BookDamageLossController {

    @Autowired
    private BookDamageLossService service;

    @PostMapping("/add/{schoolCode}")
    public BookDamageLossReport addReport(@RequestBody BookDamageLossReport report,
                                          @PathVariable String schoolCode) throws Exception {
        return service.addReport(report, schoolCode);
    }

    @GetMapping("/all/{schoolCode}")
    public List<BookDamageLossReport> getAll(@PathVariable String schoolCode) throws Exception {
        return service.getAllReports(schoolCode);
    }
}