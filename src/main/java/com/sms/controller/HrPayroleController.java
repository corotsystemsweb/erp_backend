package com.sms.controller;
import com.sms.model.*;
import com.sms.service.HrPayroleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.sms.appenum.Message.*;


@RestController
@RequestMapping("/api/salary")
public class HrPayroleController {
    @Autowired
    private HrPayroleService hrPayroleService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addSalary(@RequestBody HrPayroleDetails hrPayroleDetails, @PathVariable String schoolCode) throws Exception{
        HrPayroleDetails result = null;
        try{
            result = hrPayroleService.addSalary(hrPayroleDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get/{ssId}/{schoolCode}")
    public ResponseEntity<Object> getAllSalaryDetailsById(@PathVariable int ssId,@PathVariable String schoolCode) throws Exception {
        HrPayroleDetails result = null;
        try {
            result = hrPayroleService.getAllSalaryById(ssId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{schoolCode}")
    public ResponseEntity<Object> getAllSalaryDetails(@PathVariable String schoolCode) throws Exception {
        List<HrPayroleDetails> result = null;
        try {
            result = hrPayroleService.getAllSalary(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{ssId}/{schoolCode}")
    public ResponseEntity<Object> updateSalaryDetails(@RequestBody HrPayroleDetails hrPayroleDetails, @PathVariable int ssId, @PathVariable String schoolCode) throws Exception{
        HrPayroleDetails result = null;
        try{
            result = hrPayroleService.updateSalaryDetails(hrPayroleDetails,ssId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/soft/delete")
    public ResponseEntity<Object> softDeleteSalary(@RequestParam("ssId") int ssId,@RequestParam("schoolCode") String schoolCode) throws Exception {
        boolean result = hrPayroleService.softDeletdSalary(ssId,schoolCode);
        if(result){
            HrPayroleResponse response = new HrPayroleResponse(result, 200 ,DELETE_STAFF_SALARY_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            HrPayroleResponse response = new HrPayroleResponse(result, 400 ,DELETE_STAFF_SALARY_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/search/text")
    public ResponseEntity<Object> getSalaryDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<HrPayroleDetails> result = hrPayroleService.getSalaryDetailsBySearchText(searchText, schoolCode);
            if(result.isEmpty()){
                return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(EmptyResultDataAccessException e){
            return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("No records found!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
