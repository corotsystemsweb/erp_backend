package com.sms.util;

import com.sms.model.StudentDetails;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    //check that file is of excel type or not
    public static boolean checkExcelFormat(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }

    }

    //convert excel to list of products
    public static List<StudentDetails> convertExcelToListOfStudentPersonalDetails(InputStream is) {
        List<StudentDetails> list = new ArrayList<>();

        try {


            XSSFWorkbook workbook = new XSSFWorkbook(is);

            XSSFSheet sheet = workbook.getSheet("StudentPersonalDetails");

            int rowNumber = 0;
            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row row = iterator.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cells = row.iterator();

                int cid = 0;

                StudentDetails sd = new StudentDetails();

                while (cells.hasNext()) {
                    Cell cell = cells.next();

                    switch (cid) {
                       /* case 0:
                            sd.setId((int)cell.getNumericCellValue());
                            break;
                        case 1:
                            sd.setStudentId((int) cell.getNumericCellValue());
                            break;*/
                        case 0:
                            sd.setSchoolId((int)cell.getNumericCellValue());
                            break;
                        case 1:
                            sd.setFirstName(cell.getStringCellValue());
                            break;
                        case 2:
                            sd.setLastName(cell.getStringCellValue());
                            break;
                        case 3:
                            sd.setBloodGroup(cell.getStringCellValue());
                            break;
                        case 4:
                            sd.setGender(cell.getStringCellValue());
                            break;
                        case 5:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sd.setAadharNumber(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sd.setAadharNumber(cell.getStringCellValue());
                            }
                            break;
                        case 6:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sd.setPhoneNumber(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sd.setPhoneNumber(cell.getStringCellValue());
                            }
                            break;
                        case 7:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sd.setEmergencyPhoneNumber(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sd.setEmergencyPhoneNumber(cell.getStringCellValue());
                            }
                            break;
                        case 8:
                            sd.setEmailAddress(cell.getStringCellValue());
                            break;
                        case 9:
                            sd.setFatherName(cell.getStringCellValue());
                            break;
                        case 10:
                            sd.setFatherOccupation(cell.getStringCellValue());
                            break;
                        case 11:
                            sd.setMotherName(cell.getStringCellValue());
                            break;
                        case 12:
                            sd.setMotherOccupation(cell.getStringCellValue());
                            break;
                        case 13:
                            sd.setDob(cell.getDateCellValue());
                            break;
                        case 14:
                            sd.setReligion(cell.getStringCellValue());
                            break;
                        case 15:
                            sd.setNationality(cell.getStringCellValue());
                            break;
                        case 16:
                            sd.setCurrentAddress(cell.getStringCellValue());
                            break;
                        case 17:
                            sd.setCurrentCity(cell.getStringCellValue());
                            break;
                        case 18:
                            sd.setCurrentState(cell.getStringCellValue());
                            break;
                        case 19:
                            sd.setCurrentZipCode((int)cell.getNumericCellValue());
                            break;
                        case 20:
                            sd.setPermanentAddress(cell.getStringCellValue());
                            break;
                        case 21:
                            sd.setPermanentCity(cell.getStringCellValue());
                            break;
                        case 22:
                            sd.setPermanentState(cell.getStringCellValue());
                            break;
                        case 23:
                            sd.setPermanentZipCode((int)cell.getNumericCellValue());
                            break;
                        case 24:
                            sd.setStudentCountry(cell.getStringCellValue());
                            break;
                        case 25:
                            sd.setCurrentStatus(cell.getStringCellValue());
                            break;
                        case 26:
                            sd.setCurrentStatusComment(cell.getStringCellValue());
                            break;
                        case 27:
                            sd.setUpdatedBy((int)cell.getNumericCellValue());
                            break;
                        case 28:
                            sd.setUpdatedDate(cell.getDateCellValue());
                            break;
                        case 29:
                            sd.setCreateDate(cell.getDateCellValue());
                            break;
                        case 30:
                            sd.setValidityStartDate(cell.getDateCellValue());
                            break;
                        case 31:
                            sd.setValidityEndDate(cell.getDateCellValue());
                            break;
                        case 32:
                            sd.setStudentPhoto(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cid++;

                }

                list.add(sd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }
    public static List<StudentDetails> convertExcelToListOfStudentAcademicDetails(InputStream is){
        List<StudentDetails> list = new ArrayList<>();

        try {


            XSSFWorkbook workbook = new XSSFWorkbook(is);

            XSSFSheet sheet = workbook.getSheet("StudentAcademicDetails");

            int rowNumber = 0;
            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row row = iterator.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cells = row.iterator();

                int cid = 0;

                StudentDetails sd = new StudentDetails();
                while(cells.hasNext()){
                    Cell cell = cells.next();
                    switch(cid){
                        case 0:
                            sd.setStudentId((int)cell.getNumericCellValue());
                            break;
                        case 1:
                            sd.setSchoolId((int)cell.getNumericCellValue());
                            break;
                        case 2:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sd.setRegistrationNumber(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sd.setRegistrationNumber(cell.getStringCellValue());
                            }
                            break;
                        case 3:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sd.setRollNumber(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sd.setRollNumber(cell.getStringCellValue());
                            }
                            break;
                        case 4:
                            sd.setSessionId((int)cell.getNumericCellValue());
                            break;
                        case 5:
                            sd.setStudentClassId((int)cell.getNumericCellValue());
                        case 6:
                            sd.setStudentSectionId((int)cell.getNumericCellValue());
                            break;
                        case 7:
                            sd.setSessionStatus(cell.getStringCellValue());
                            break;
                        case 8:
                            sd.setSessionStatusComment(cell.getStringCellValue());
                            break;

                        case 9:
                            sd.setUpdatedBy((int)cell.getNumericCellValue());
                            break;
                        case 10:
                            sd.setUpdatedDate(cell.getDateCellValue());
                            break;
                        case 11:
                            sd.setCreateDate(cell.getDateCellValue());
                            break;
                        case 12:
                            sd.setValidityStartDate(cell.getDateCellValue());
                        case 13:
                            sd.setValidityEndDate(cell.getDateCellValue());
                            break;
                    }
                    cid++;
                }
                list.add(sd);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
