package com.sms.controller;

import com.sms.model.AddBookDetails;
import com.sms.model.IssueBookDetails;
import com.sms.model.IssueBookResponse;
import com.sms.model.StudentResponse;
import com.sms.service.IssueBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class IssueBookController {
    @Autowired
    private IssueBookService issueBookService;
    @PostMapping("/issue/book/add/{schoolCode}")
    public ResponseEntity<Object> addIssueBookDetails(@RequestBody IssueBookDetails issueBookDetails, @PathVariable String schoolCode)throws Exception
    {
        IssueBookDetails result=null;
        try{
            result=issueBookService.addIssueBookDetails(issueBookDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/issue/book/get/{schoolCode}")
    public ResponseEntity<Object> getIssueBookDetails(@PathVariable String schoolCode) throws Exception {
        List<IssueBookDetails> result=null;
        try {
            result =issueBookService.getIssueBookDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/issue/book/{issueBookId}/{schoolCode}")
    public ResponseEntity<Object> getIssueBookDetailsById(@PathVariable int issueBookId, @PathVariable String schoolCode) throws Exception {
        IssueBookDetails result=null;
        try {
            result =issueBookService.getIssueBookDetailsById(issueBookId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/issue/book/update/{issueBookId}/{schoolCode}")
    public ResponseEntity<Object> updateBookCategoryDetails(@RequestBody IssueBookDetails issueBookDetails, @PathVariable int issueBookId, @PathVariable String schoolCode) throws Exception {
        IssueBookDetails result = null;
        try {
            result = issueBookService.updateIssueBookDetails(issueBookDetails,issueBookId,schoolCode);
                return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/issue/book/soft/delete")
    public ResponseEntity<Object> deleteStudent(@RequestParam("issueBookId") int issueBookId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        boolean result = issueBookService.issueBookSoftDelete(issueBookId,schoolCode);
        if (result) {
            IssueBookResponse response = new IssueBookResponse(result, 200, DELETE_ISSUE_BOOK_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            StudentResponse response = new StudentResponse(result, 400, DELETE_ISSUE_BOOK_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/issue/book/search/get")
    public ResponseEntity<Object> getIssueBookDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<IssueBookDetails> result= issueBookService.getIssueBookDetailsbBySearchText(searchText,schoolCode);
            if(result.isEmpty()){
                return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(EmptyResultDataAccessException e){
            return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("No records found!", HttpStatus.UNAUTHORIZED);
        }
    }
}
