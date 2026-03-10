package com.sms.service;

import com.sms.model.HomeWorkDetails;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;


public interface HomeWorkService {
    //public boolean addPDF(MultipartFile file,int hdId) throws Exception;
    public boolean addPDF(MultipartFile file, String schoolCode, int hwId) throws Exception;
    public HomeWorkDetails addHomeWork(HomeWorkDetails homeWorkDetails, String schoolCode) throws Exception;
    //byte[] getPDFBytes(int hwId) throws Exception;
    byte[] getPDFBytes(String schoolCode, int hwId) throws Exception;
    public List<HomeWorkDetails> getAssignHomeWork(String schoolCode) throws Exception;

    public HomeWorkDetails getAssignHomeWorkById(int hwId, String schoolCode) throws Exception;

    public HomeWorkDetails updateAssignHomeWorkById(HomeWorkDetails homeWorkDetails, int hwId, String schoolCode) throws Exception;

    public boolean deleteAssignHomeWorkById(int hwId,String schoolCode) throws Exception;
}
