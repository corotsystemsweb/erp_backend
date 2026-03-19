package com.sms.dao;

import com.sms.model.TransportFeeDue;

import java.util.List;

public interface TransportFeeDueDao {
    public String addBulkTransportFeeDue(List<TransportFeeDue> transportFeeDuesList, String schoolCode) throws Exception;
    String updateBulkTransportFeeDue(List<TransportFeeDue> list, String schoolCode) throws Exception;
    public String closeTransportFeeMonths(List<Integer> tfDueIds, String schoolCode) throws Exception;
    //public int countRemainingActiveMonths(int studentTransportId, String schoolCode) throws Exception;
}
