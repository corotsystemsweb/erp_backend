package com.sms.service;

import com.sms.model.TransportAllocationDetails;

import java.util.List;

public interface TransportAllocationService {
    public TransportAllocationDetails addTransportAllocation(TransportAllocationDetails transportAllocationDetails, String schoolCode) throws Exception;
    public TransportAllocationDetails getTransportAllocationById(int taId, String schoolCode) throws Exception;
    public List<TransportAllocationDetails> getAllTransportAllocation(String schoolCode) throws Exception;
    public TransportAllocationDetails updateTransportAllocation(TransportAllocationDetails transportAllocationDetails, int taId, String schoolCode) throws Exception;
    public boolean deleteTransportAllocation(int taId, String schoolCode) throws Exception;
    public List<TransportAllocationDetails> getTransportAllocationDetailsBySearchText(String searchText, String schoolCode) throws Exception;
}
