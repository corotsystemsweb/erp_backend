package com.sms.service;

import com.sms.model.HostelDetails;
import java.util.List;

public interface HostelService {
    HostelDetails addHostel(HostelDetails hostelDetails, String schoolCode) throws Exception;
    HostelDetails getHostelById(String schoolCode, int hostelId) throws Exception;
    List<HostelDetails> getAllHostels(String schoolCode) throws Exception;
    HostelDetails updateHostel(HostelDetails hostelDetails, String schoolCode) throws Exception;
    boolean deleteHostel(String schoolCode, int hostelId) throws Exception;
}