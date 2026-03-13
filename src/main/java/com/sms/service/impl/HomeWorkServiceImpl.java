
package com.sms.service.impl;

import com.sms.dao.HomeWorkDao;
import com.sms.model.HomeWorkDetails;
import com.sms.service.HomeWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class HomeWorkServiceImpl implements HomeWorkService {
    @Autowired
    private HomeWorkDao homeWorkDao;
   /* @Override
    public boolean addPDF(MultipartFile file,int hdId) throws Exception {
        return homeWorkDao.addPDF(file,hdId);
    }*/
    @Override
    public boolean addPDF(MultipartFile file, String schoolCode, int hwId) throws Exception {
        return homeWorkDao.addPDF(file, schoolCode, hwId);
    }

    @Override
    public HomeWorkDetails addHomeWork(HomeWorkDetails homeWorkDetails,String schoolCode) throws Exception {
        return homeWorkDao.addHomeWork(homeWorkDetails,schoolCode);
    }
   /* @Override
    public byte[] getPDFBytes(int hwId) throws Exception {
        return homeWorkDao.getPDFBytes(hwId);
    }*/
    @Override
    public byte[] getPDFBytes(String scholCode, int hwId) throws Exception{
        return homeWorkDao.getPDFBytes(scholCode, hwId);
    }
    @Override
    public List<HomeWorkDetails> getAssignHomeWork(String schoolCode) throws Exception {
        return homeWorkDao.getAssignHomeWork(schoolCode);
    }

    @Override
    public HomeWorkDetails getAssignHomeWorkById(int hwId, String schoolCode) throws Exception {
        return homeWorkDao.getAssignHomeWorkById(hwId,schoolCode);
    }

    @Override
    public HomeWorkDetails updateAssignHomeWorkById(HomeWorkDetails homeWorkDetails, int hwId, String schoolCode) throws Exception {
        return homeWorkDao.updateAssignHomeWorkById(homeWorkDetails,hwId, schoolCode);
    }

    @Override
    public boolean deleteAssignHomeWorkById(int hwId, String schoolCode) throws Exception {
        return homeWorkDao.deleteAssignHomeWorkById(hwId,schoolCode);
    }
}



