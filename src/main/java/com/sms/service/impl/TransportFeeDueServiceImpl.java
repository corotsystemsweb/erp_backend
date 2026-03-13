package com.sms.service.impl;

import com.sms.dao.TransportFeeDueDao;
import com.sms.model.TransportFeeDue;
import com.sms.service.TransportFeeDueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportFeeDueServiceImpl implements TransportFeeDueService {
    @Autowired
    private TransportFeeDueDao transportFeeDueDao;

    @Override
    public String addBulkTransportFeeDue(List<TransportFeeDue> transportFeeDuesList, String schoolCode) throws Exception {
        return transportFeeDueDao.addBulkTransportFeeDue(transportFeeDuesList, schoolCode);
    }

    @Override
    public String updateBulkTransportFeeDue(List<TransportFeeDue> list, String schoolCode) throws Exception {
        return transportFeeDueDao.updateBulkTransportFeeDue(list, schoolCode);
    }

    @Override
    public String closeTransportFeeMonths(List<Integer> tfDueIds, String schoolCode) throws Exception {
        return transportFeeDueDao.closeTransportFeeMonths(tfDueIds, schoolCode);
    }

//    @Override
//    public int countRemainingActiveMonths(int studentTransportId, String schoolCode) throws Exception {
//        return transportFeeDueDao.countRemainingActiveMonths(studentTransportId, schoolCode);
//    }

}
