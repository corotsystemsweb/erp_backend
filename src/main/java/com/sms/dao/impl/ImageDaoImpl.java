package com.sms.dao.impl;

import com.sms.dao.ImageDao;
import com.sms.model.ImageDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Repository
public class ImageDaoImpl implements ImageDao {
    @Value("${school.img.local.path}")
    private String FOLDER_PATH;
    //private final String FOLDER_PATH = "C:\\Users\\sbhowmik\\Desktop\\images\\student_images\\";
    //private final String FOLDER_PATH = "/home/ubuntu/metapro/data/image/student";
    /*
    @method addAImage
    @param file, id
    @throws Exception
    @description adding student image in a specific folder with the name of student id
    @developer Sukhendu Bhowmik
    */
   /* @Override
    public boolean addImage(MultipartFile file, int id) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileExtention = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String photoPath = FOLDER_PATH + id + "." + fileExtention;
        file.transferTo(new File(photoPath));
        return true;
    }*/
    @Override
    public boolean addImage(MultipartFile file, int id) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileExtention = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String photoPath = FOLDER_PATH + id + "." + "png";
        file.transferTo(new File(photoPath));
        return true;
    }
   /* @Override
    public boolean addImage(MultipartFile file, int id) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String photoPath = FOLDER_PATH + id + "." + fileExtension;

        // Convert image bytes to Base64 string
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        // Save Base64 string to file
        Files.write(Paths.get(photoPath), base64Image.getBytes());
        return true;
    }*/
    /*
    @method fetchImage
    @param fileName
    @throws Exception
    @description fetching student image in a specific folder
    @developer Sukhendu Bhowmik
    */

    /*@Override
    public byte[] getImage(String fileName) throws Exception {
        String imagePath = FOLDER_PATH + fileName;
        Path path = Paths.get(imagePath);
        return Files.readAllBytes(path);
    }*/
   /* @Override
    public byte[] getImage(String fileName) throws Exception {
        String imagePath = FOLDER_PATH + fileName;

        // Read Base64 string from file
        String base64Image = new String(Files.readAllBytes(Paths.get(imagePath)));

        // Decode Base64 string to bytes
        return Base64.getDecoder().decode(base64Image);
    }*/
   /* @Override
    public String getImage(int id) throws Exception {////correct one
        String fileName  = id + ".png";
        String imagePath = FOLDER_PATH + fileName;
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return base64Image;
    }*/
    public ImageDetails getImage(int id) throws Exception {
        String fileName = id + ".png";
        String imagePath = FOLDER_PATH + fileName;
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        ImageDetails imageDetails = new ImageDetails();
        imageDetails.setImageString(base64Image);

        return imageDetails;
    }

}
