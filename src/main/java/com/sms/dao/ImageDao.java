package com.sms.dao;

import com.sms.model.ImageDetails;
import org.springframework.web.multipart.MultipartFile;

public interface ImageDao {
    public boolean addImage(MultipartFile file, int id) throws Exception;

    //public byte[] getImage(String fileName) throws Exception;
   // public String getImage(int id) throws Exception;
    public ImageDetails getImage(int id) throws Exception;
}
