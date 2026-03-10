package com.sms.service;

import com.sms.model.TransportFeeDue;

import java.util.List;

public interface TransportFeeDueService {
    public String addBulkTransportFeeDue(List<TransportFeeDue> transportFeeDuesList, String schoolCode) throws Exception;
    String updateBulkTransportFeeDue(List<TransportFeeDue> list, String schoolCode) throws Exception;
    String closeTransportFeeMonths(List<Integer> tfDueIds, String schoolCode) throws Exception;
    //int countRemainingActiveMonths(int studentTransportId, String schoolCode) throws Exception;
}
