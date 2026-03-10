package com.sms.service.impl;

import com.sms.dao.ImageDao;
import com.sms.model.ImageDetails;
import com.sms.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageDao imageDao;
    @Override
    public boolean addImage(MultipartFile file, int id) throws Exception {
        return imageDao.addImage(file,id);
    }

    /*@Override
    public byte[] getImage(String filename) throws Exception {
        return imageDao.getImage(filename);
    }*/
   /* @Override
    public String getImage(int id) throws Exception {
        return imageDao.getImage(id);
    }*/
    @Override
    public ImageDetails getImage(int id) throws Exception{
        return imageDao.getImage(id);
    }

}
