package com.sms.service.impl;

import com.sms.dao.StudentTransportDetailsDao;
import com.sms.model.StudentTransportDetails;
import com.sms.model.TransportCloseRequest;
import com.sms.model.TransportFeeDue;
import com.sms.service.StudentTransportService;
import com.sms.service.TransportFeeDueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentTransportServiceImpl implements StudentTransportService {
    @Autowired
    private StudentTransportDetailsDao studentTransportDetailsDao;

    @Autowired
    private TransportFeeDueService transportFeeDueService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentTransportDetails addStudentTransport(StudentTransportDetails details, List<TransportFeeDue> feeDueList, String schoolCode) throws Exception {

        //Insert transport
        StudentTransportDetails savedTransport = studentTransportDetailsDao.addStudentTransportDetails(details, schoolCode);

        //Insert transport fee due (optional)
        if (feeDueList != null && !feeDueList.isEmpty()) {

            for (TransportFeeDue feeDue : feeDueList) {
                feeDue.setStudentTransportId(savedTransport.getStudentTransportId());
                feeDue.setSchoolId(details.getSchoolId());
            }

            transportFeeDueService.addBulkTransportFeeDue(feeDueList, schoolCode);
        }

        return savedTransport;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentTransportDetails updateStudentTransport(StudentTransportDetails details, List<TransportFeeDue> feeDueList, String schoolCode) throws Exception {
        //Update Transport
        StudentTransportDetails updated = studentTransportDetailsDao.updateStudentTransportDetails(details, schoolCode);
        if (updated == null)
            throw new RuntimeException("Transport update failed");
        //Update Fee Due
        if (feeDueList != null && !feeDueList.isEmpty()) {

            List<TransportFeeDue> existingFeeDue = new ArrayList<>();
            List<TransportFeeDue> newFeeDue = new ArrayList<>();

            for(TransportFeeDue t : feeDueList){
                t.setStudentTransportId(details.getStudentTransportId());
                t.setSchoolId(details.getSchoolId());

                if(t.getTfDueId() != null && t.getTfDueId() > 0){
                    existingFeeDue.add(t);
                } else {
                    newFeeDue.add(t);
                }
            }
            //update transport fee due
            if(!existingFeeDue.isEmpty()){
                transportFeeDueService.updateBulkTransportFeeDue(existingFeeDue, schoolCode);
            }
            //add new transport fee due
            if(!newFeeDue.isEmpty()){
                transportFeeDueService.addBulkTransportFeeDue(newFeeDue, schoolCode);
            }
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String closeTransPortFee(TransportCloseRequest request, String schoolCode) throws Exception {
        if(request.getStudentTransportId() == null)
            throw new RuntimeException("Student Transport Id is required");

        if(request.getTfDueIds() == null || request.getTfDueIds().isEmpty())
            throw new RuntimeException("No months selected to close");

        //Close selected months (ONLY non-deposited months will be updated)
        transportFeeDueService.closeTransportFeeMonths(request.getTfDueIds(), schoolCode);

//        //Check remaining unpaid & non-deposited months
//        int remainingMonths = transportFeeDueService.countRemainingActiveMonths(request.getStudentTransportId(), schoolCode);
//
//        //If no remaining months → deactivate transport
//        if(remainingMonths <= 0){
//            studentTransportDetailsDao.deactivateStudentTransport(request.getStudentTransportId(), schoolCode);
//        }
        return "Transport fee closed successfully";
    }

}
