package com.sms.service.impl;

import com.sms.dao.TransportAllocationDao;
import com.sms.model.TransportAllocationDetails;
import com.sms.service.TransportAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportAllocationServiceImpl implements TransportAllocationService {
    @Autowired
    private TransportAllocationDao transportAllocationDao;

    @Override
    public TransportAllocationDetails addTransportAllocation(TransportAllocationDetails transportAllocationDetails, String schoolCode) throws Exception {
        return transportAllocationDao.addTransportAllocation(transportAllocationDetails, schoolCode);
    }

    @Override
    public TransportAllocationDetails getTransportAllocationById(int taId, String schoolCode) throws Exception {
        return transportAllocationDao.getTransportAllocationById(taId, schoolCode);
    }

    @Override
    public List<TransportAllocationDetails> getAllTransportAllocation(String schoolCode) throws Exception {
        return transportAllocationDao.getAllTransportAllocation(schoolCode);
    }

    @Override
    public TransportAllocationDetails updateTransportAllocation(TransportAllocationDetails transportAllocationDetails, int taId, String schoolCode) throws Exception {
        return transportAllocationDao.updateTransportAllocation(transportAllocationDetails, taId, schoolCode);
    }

    @Override
    public boolean deleteTransportAllocation(int taId, String schoolCode) throws Exception {
        return transportAllocationDao.deleteTransportAllocation(taId, schoolCode);
    }

    @Override
    public List<TransportAllocationDetails> getTransportAllocationDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return transportAllocationDao.getTransportAllocationDetailsBySearchText(searchText, schoolCode);
    }
}
