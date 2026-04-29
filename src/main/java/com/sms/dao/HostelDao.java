package com.sms.dao;

import com.sms.model.*;
import java.util.List;

public interface HostelDao {
    HostelDetails addHostel(HostelDetails hostelDetails, String schoolCode) throws Exception;
    HostelDetails getHostelById(String schoolCode, int hostelId) throws Exception;
    List<HostelDetails> getAllHostels(String schoolCode) throws Exception;
    HostelDetails updateHostel(HostelDetails hostelDetails, String schoolCode) throws Exception;
    boolean deleteHostel(String schoolCode, int hostelId) throws Exception;
    RoomDetails addRoom(AddRoomRequest request, String schoolCode) throws Exception;
    List<RoomDetails> getRoomsByHostel(String schoolCode, int hostelId) throws Exception;
    HostelCapacityStatus getHostelCapacityStatus(String schoolCode, int hostelId) throws Exception;
    HostelFeesDetails addHostelFees(AddHostelFeesRequest request, String schoolCode) throws Exception;
    List<HostelFeesDetails> getHostelFeesByHostel(String schoolCode, int hostelId) throws Exception;
    HostelFeesDetails getHostelFeesByHostelAndRoomType(String schoolCode, int hostelId, String roomType) throws Exception;
}