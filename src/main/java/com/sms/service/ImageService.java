package com.sms.service;

import com.sms.model.ImageDetails;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    public boolean addImage(MultipartFile file, int id) throws Exception;
    //public byte[] getImage(String filename) throws Exception;
    //public String getImage(int id) throws Exception;
    public ImageDetails getImage(int id) throws Exception;
}
