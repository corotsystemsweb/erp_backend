package com.sms.service.impl;

import com.sms.dao.HostelDao;
import com.sms.model.*;
import com.sms.service.HostelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HostelServiceImpl implements HostelService {

    @Autowired
    private final HostelDao hostelDao;

    public HostelServiceImpl(HostelDao hostelDao) {
        this.hostelDao = hostelDao;
    }

    @Override
    public HostelDetails addHostel(HostelDetails hostelDetails, String schoolCode) throws Exception {
        return hostelDao.addHostel(hostelDetails, schoolCode);
    }

    @Override
    public HostelDetails getHostelById(String schoolCode, int hostelId) throws Exception {
        return hostelDao.getHostelById(schoolCode, hostelId);
    }

    @Override
    public List<HostelDetails> getAllHostels(String schoolCode) throws Exception {
        return hostelDao.getAllHostels(schoolCode);
    }

    @Override
    public HostelDetails updateHostel(HostelDetails hostelDetails, String schoolCode) throws Exception {
        return hostelDao.updateHostel(hostelDetails, schoolCode);
    }

    @Override
    public boolean deleteHostel(String schoolCode, int hostelId) throws Exception {
        return hostelDao.deleteHostel(schoolCode, hostelId);
    }

    @Override
    public RoomDetails addRoom(AddRoomRequest request, String schoolCode) throws Exception {
        return hostelDao.addRoom(request, schoolCode);
    }

    @Override
    public List<RoomDetails> getRoomsByHostel(String schoolCode, int hostelId) throws Exception {
        return hostelDao.getRoomsByHostel(schoolCode, hostelId);
    }

    @Override
    public HostelCapacityStatus getHostelCapacityStatus(String schoolCode, int hostelId) throws Exception {
        return hostelDao.getHostelCapacityStatus(schoolCode, hostelId);
    }
}