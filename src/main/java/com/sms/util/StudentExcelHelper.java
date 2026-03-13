/*
package com.sms.util;
import com.sms.model.StudentExcelDetails;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.DateUtil;


public class StudentExcelHelper {
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
    public static List<StudentExcelDetails> convertExcelToListOfStudentExcelDetails(InputStream is) {
        List<StudentExcelDetails> list = new ArrayList<>();

        try {


            XSSFWorkbook workbook = new XSSFWorkbook(is);

            XSSFSheet sheet = workbook.getSheet("Sheet1");
            System.out.println("sheet-name"+sheet);

            int rowNumber = 0;
            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row row = iterator.next();

                if (rowNumber < 2) {
                    rowNumber++;
                    continue;
                }

                // Check if the row is empty and skip it
                boolean isEmptyRow = true;
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        isEmptyRow = false;
                        break;
                    }
                }
                if (isEmptyRow) {
                    continue;
                }

                Iterator<Cell> cells = row.iterator();

                int cid = 0;

                StudentExcelDetails sed = new StudentExcelDetails();

                while (cells.hasNext()) {
                    Cell cell = cells.next();

                    switch (cid) {
                        case 0:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setApaarId(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setApaarId(cell.getStringCellValue());
                            }
                            break;
                        case 1:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setSession(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setSession(cell.getStringCellValue());
                            }
                            break;
                        case 2:
                            sed.setStudentFirstName(cell.getStringCellValue());
                            break;
                        case 3:
                            sed.setStudentLastName(cell.getStringCellValue());
                            break;
                        case 4:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setStudentClass(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setStudentClass(cell.getStringCellValue());
                            }
                            break;
                        case 5:
                            sed.setStudentSection(cell.getStringCellValue());
                            break;
                        case 6:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert to string, then to integer
                                sed.setRollNo(Integer.parseInt(String.format("%.0f", cell.getNumericCellValue())));
                            } else if (cell.getCellType() == CellType.STRING) {
                                String cellValue = cell.getStringCellValue();
                                // Check if the value is numeric
                                if (cellValue.matches("-?\\d+")) {  // Matches integer values
                                    sed.setRollNo(Integer.parseInt(cellValue));
                                } else {
                                    // Handle non-numeric values here, e.g., set a default or log an error
                                    sed.setRollNo(0);  // Default value or error handling
                                }
                            }
                            break;

                        case 7:
                            sed.setMedium(cell.getStringCellValue());
                            break;
                        case 8:
                            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                sed.setDob(date);
                            } else if (cell.getCellType() == CellType.STRING) {
                                // The cell contains a date as a string (e.g., "12/27/2014")
                                String dateStr = cell.getStringCellValue();
                                try {
                                    // Parse the string into a java.util.Date
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                    Date date = sdf.parse(dateStr);
                                    sed.setDob(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException("Invalid date format in cell: " + dateStr);
                                }
                            } else {
                                throw new RuntimeException("Cell does not contain a valid date: " + cell);
                            }
                            break;
                        case 9:
                            sed.setReligion(cell.getStringCellValue());
                            break;
                        case 10:
                            sed.setNationality(cell.getStringCellValue());
                            break;
                        case 11:
                            sed.setGender(cell.getStringCellValue());
                            System.out.println("case10"+cell.getStringCellValue());
                            break;
                        case 12:
//                            System.out.println("case11U"+cell.getStringCellValue());
//                            sed.setMobileNumber(cell.getStringCellValue());
//                            System.out.println("case11"+cell.getStringCellValue());
                            if (cell.getCellType() == CellType.NUMERIC) {
                                sed.setMobileNumber(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setMobileNumber(cell.getStringCellValue());
                            }
                            break;
                        case 13:
                            sed.setEmail(cell.getStringCellValue());
                            break;
                        case 14:
                            sed.setAddress(cell.getStringCellValue());
                            break;
                        case 15:
                            sed.setCity(cell.getStringCellValue());
                            break;
                        case 16:
                            sed.setState(cell.getStringCellValue());
                            break;
                        case 17:
                            sed.setCountry(cell.getStringCellValue());
                            break;
                        case 18:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                sed.setPincode(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setPincode(cell.getStringCellValue());
                            }
                            break;
                        case 19:
                            sed.setType(cell.getStringCellValue());
                            break;
                        case 20:
                           sed.setRteStudent(cell.getBooleanCellValue());
                            break;
                        case 21:
                            sed.setStudentMotherName(cell.getStringCellValue());
                            break;
                        case 22:
                            sed.setStudentFatherName(cell.getStringCellValue());
                            break;
                        case 23:
                            sed.setGuardianName(cell.getStringCellValue());
                            break;
                        case 24:
                            sed.setAdmissionDate(cell.getDateCellValue());
                            break;
                        case 25:
                            sed.setEnrolledSession(cell.getStringCellValue());
                            break;
                        case 26:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setEnrolledYear(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setEnrolledYear(cell.getStringCellValue());
                            }
                            break;
                        case 27:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setEnrolledClass(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setEnrolledClass(cell.getStringCellValue());
                            }
                            break;
                        case 28:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setPenNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setPenNo(cell.getStringCellValue());
                            }
                            break;
                        case 29:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setAdmissionNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setAdmissionNo(cell.getStringCellValue());
                            }
                            break;
                        case 30:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setRegistrationNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setRegistrationNo(cell.getStringCellValue());
                            }
                            break;
                        case 31:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setEnrollmentNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setEnrollmentNo(cell.getStringCellValue());
                            }
                            break;
                        case 32:
                            sed.setStream(cell.getStringCellValue());
                            break;
                        case 33:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setWhatsapp(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setWhatsapp(cell.getStringCellValue());
                            }
                            break;
                        case 34:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setAlternateNumber(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setAlternateNumber(cell.getStringCellValue());
                            }
                            break;
                        case 35:
                            sed.setBloodGroup(cell.getStringCellValue());
                            break;
                        case 36:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setAadharNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setAadharNo(cell.getStringCellValue());
                            }
                            break;
                        case 37:
                            sed.setCaste(cell.getStringCellValue());
                            break;
                        case 38:
                            sed.setCategory(cell.getStringCellValue());
                            break;
                        case 39:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setRteApplicationNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setRteApplicationNo(cell.getStringCellValue());
                            }
                            break;
                        case 40:
                            sed.setAttendedSchool(cell.getStringCellValue());
                            break;
                        case 41:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setAttendedClass(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setAttendedClass(cell.getStringCellValue());
                            }
                            break;
                        case 42:
                            sed.setSchoolAffiliated(cell.getStringCellValue());
                            break;
                        case 43:
                            sed.setLastSession(cell.getStringCellValue());
                            break;
                        case 44:
                            sed.setMotherQualification(cell.getStringCellValue());
                            break;
                        case 45:
                            sed.setFatherQualification(cell.getStringCellValue());
                            break;
                        case 46:
                            sed.setGuardianQualification(cell.getStringCellValue());
                            break;
                        case 47:
                            sed.setMotherOccupation(cell.getStringCellValue());
                            break;
                        case 48:
                            sed.setFatherOccupation(cell.getStringCellValue());
                            break;
                        case 49:
                            sed.setGuardianOccupation(cell.getStringCellValue());
                            break;
                        case 50:
                            sed.setMotherResidentialAddress(cell.getStringCellValue());
                            break;
                        case 51:
                            sed.setFatherResidentialAddress(cell.getStringCellValue());
                            break;
                        case 52:
                            sed.setGuardianResidentialAddress(cell.getStringCellValue());
                            break;
                        case 53:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setMotherIncome(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setMotherIncome(cell.getStringCellValue());
                            }
                            break;
                        case 54:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setFatherIncome(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setFatherIncome(cell.getStringCellValue());
                            }
                            break;
                        case 55:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setGuardianIncome(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setGuardianIncome(cell.getStringCellValue());
                            }
                            break;
                        case 56:
                            sed.setMotherEmail(cell.getStringCellValue());
                            break;
                        case 57:
                            sed.setFatherEmail(cell.getStringCellValue());
                            break;
                        case 58:
                            sed.setGuardianEmail(cell.getStringCellValue());
                            break;
                        case 59:
                            sed.setMotherMobile(cell.getStringCellValue());
                            break;
                        case 60:
                            sed.setFatherMobile(cell.getStringCellValue());
                            break;
                        case 61:
                            sed.setGuardianMobile(cell.getStringCellValue());
                            break;
                        case 62:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setTcNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setTcNo(cell.getStringCellValue());
                            }
                            break;
                        case 63:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setTcDate(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setTcDate(cell.getStringCellValue());
                            }
                            break;
                        case 64:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setScholarshipId(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setScholarshipId(cell.getStringCellValue());
                            }
                            break;
                        case 65:
                            sed.setScholarshipPassword(cell.getStringCellValue());
                            break;
                        case 66:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setDomicileApplicationNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setDomicileApplicationNo(cell.getStringCellValue());
                            }
                            break;
                        case 67:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setIncomeApplicationNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setIncomeApplicationNo(cell.getStringCellValue());
                            }
                            break;
                        case 68:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setCasteApplicationNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setCasteApplicationNo(cell.getStringCellValue());
                            }
                            break;
                        case 69:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setDobApplicationNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setDobApplicationNo(cell.getStringCellValue());
                            }
                            break;
                        case 70:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setMotherAadharNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setMotherAadharNo(cell.getStringCellValue());
                            }
                            break;
                        case 71:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setFatherAadharNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setFatherAadharNo(cell.getStringCellValue());
                            }
                            break;
                        case 72:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setGuardianAadharNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setGuardianAadharNo(cell.getStringCellValue());
                            }
                            break;
                        case 73:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setHeight(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setHeight(cell.getStringCellValue());
                            }
                            break;
                        case 74:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setWeight(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setWeight(cell.getStringCellValue());
                            }
                            break;
                        case 75:
                            sed.setBankName(cell.getStringCellValue());
                            break;
                        case 76:
                            sed.setBranchName(cell.getStringCellValue());
                            break;
                        case 77:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setBankAccountNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setBankAccountNo(cell.getStringCellValue());
                            }
                            break;
                        case 78:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setBankIfsc(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setBankIfsc(cell.getStringCellValue());
                            }
                            break;
                        case 79:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setPanNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setPanNo(cell.getStringCellValue());
                            }
                            break;
                        case 80:
                            sed.setReference(cell.getStringCellValue());
                            break;
                        case 81:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setGovtStudentId(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setGovtStudentId(cell.getStringCellValue());
                            }
                            break;
                        case 82:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setGovtFamilyId(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setGovtFamilyId(cell.getStringCellValue());
                            }
                            break;
                        case 83:
                            sed.setDropout(cell.getBooleanCellValue());
                            break;
                        case 84:
                            sed.setDropoutReason(cell.getStringCellValue());
                            break;
                        case 85:
                            sed.setDropoutDate(cell.getDateCellValue());
                            break;
                        case 86:
                            sed.setPreviousQualification(cell.getStringCellValue());
                            break;
                        case 87:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setPreviousPassYear(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setPreviousPassYear(cell.getStringCellValue());
                            }
                            break;
                        case 88:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setPreviousRollNo(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setPreviousRollNo(cell.getStringCellValue());
                            }
                            break;
                        case 89:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setPreviousObtMarks(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setPreviousObtMarks(cell.getStringCellValue());
                            }
                            break;
                        case 90:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                // Forcibly convert it to string
                                sed.setPreviousPercentage(String.format("%.0f", cell.getNumericCellValue()));
                            } else if (cell.getCellType() == CellType.STRING) {
                                sed.setPreviousPercentage(cell.getStringCellValue());
                            }
                            break;
                        case 91:
                            sed.setPreviousSubjects(cell.getStringCellValue());
                            break;
                        case 92:
                            sed.setPreviousSchoolName(cell.getStringCellValue());
                            break;
                    }
                    cid++;

                }

                list.add(sed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }
}
*/

package com.sms.util;

import com.monitorjbl.xlsx.StreamingReader;
import com.sms.model.StudentExcelDetails;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;



public class StudentExcelHelper {

    private static final int TOTAL_COLUMNS = 92;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public static boolean checkExcelFormat(MultipartFile file) {
        return file.getContentType() != null &&
                file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public static List<StudentExcelDetails> convertExcelToListOfStudentExcelDetails(InputStream is) {
        List<StudentExcelDetails> list = new ArrayList<>();

        try (Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is)) {

            // Get all sheets in the workbook
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();

            while(sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                System.out.println("Processing sheet: " + sheet.getSheetName());

                int rowNumber = 0;
                Iterator<Row> rowIterator = sheet.iterator();

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // Skip first two rows in every sheet
                    if (rowNumber < 2) {
                        rowNumber++;
                        continue;
                    }

                    if (isRowEmpty(row)) continue;

                    StudentExcelDetails student = processRow(row);
                    list.add(student);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing Excel file: " + e.getMessage(), e);
        }
        return list;
    }

    private static boolean isRowEmpty(Row row) {
        for (int cid = 0; cid <= TOTAL_COLUMNS; cid++) {
            Cell cell = row.getCell(cid, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (!isCellEmpty(cell)) return false;
        }
        return true;
    }

    private static boolean isCellEmpty(Cell cell) {
        if (cell.getCellType() == CellType.BLANK) return true;
        if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) return true;
        return false;
    }

    private static StudentExcelDetails processRow(Row row) {
        StudentExcelDetails student = new StudentExcelDetails();

        for (int cid = 0; cid <= TOTAL_COLUMNS; cid++) {
            Cell cell = row.getCell(cid, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            try {
                processCell(cid, cell, student);
            } catch (Exception e) {
                throw new RuntimeException("Error processing cell [" + (cid+1) + "] in row [" + row.getRowNum() + "]: " + e.getMessage(), e);
            }
        }
        return student;
    }

    private static void processCell(int columnIndex, Cell cell, StudentExcelDetails student) {
        switch (columnIndex) {
            case 0 -> setStringValue(cell, student::setApaarId);
            case 1 -> setStringValue(cell, student::setSession);
            case 2 -> student.setStudentFirstName(getStringValue(cell));
            case 3 -> student.setStudentLastName(getStringValue(cell));
            case 4 -> student.setStudentType(getStringValue(cell));
            case 5 -> setStringValue(cell, student::setStudentClass);
            case 6 -> student.setStudentSection(getStringValue(cell));
            case 7 -> setIntegerValue(cell, student::setRollNo);
            case 8 -> student.setMedium(getStringValue(cell));
            case 9 -> setDateValue(cell, student::setDob);
            case 10 -> student.setReligion(getStringValue(cell));
            case 11 -> student.setNationality(getStringValue(cell));
            case 12 -> student.setGender(getStringValue(cell));
            case 13 -> setStringValue(cell, student::setMobileNumber);
            case 14 -> student.setEmail(getStringValue(cell));
            case 15 -> student.setAddress(getStringValue(cell));
            case 16 -> student.setCity(getStringValue(cell));
            case 17 -> student.setState(getStringValue(cell));
            case 18 -> student.setCountry(getStringValue(cell));
            case 19 -> setStringValue(cell, student::setPincode);
            //case 20 -> student.setType(getStringValue(cell));
            case 20 -> setBooleanValue(cell, student::setRteStudent);
            case 21 -> student.setStudentMotherName(getStringValue(cell));
            case 22 -> student.setStudentFatherName(getStringValue(cell));
            case 23 -> student.setGuardianName(getStringValue(cell));
            case 24 -> setDateValue(cell, student::setAdmissionDate);
            case 25 -> student.setEnrolledSession(getStringValue(cell));
            case 26 -> setStringValue(cell, student::setEnrolledYear);
            case 27 -> setStringValue(cell, student::setEnrolledClass);
            case 28 -> setStringValue(cell, student::setPenNo);
            case 29 -> setStringValue(cell, student::setAdmissionNo);
            case 30 -> setStringValue(cell, student::setRegistrationNo);
            case 31 -> setStringValue(cell, student::setEnrollmentNo);
            case 32 -> student.setStream(getStringValue(cell));
            case 33 -> setStringValue(cell, student::setWhatsapp);
            case 34 -> setStringValue(cell, student::setAlternateNumber);
            case 35 -> student.setBloodGroup(getStringValue(cell));
            case 36 -> setStringValue(cell, student::setAadharNo);
            case 37 -> student.setCaste(getStringValue(cell));
            case 38 -> student.setCategory(getStringValue(cell));
            case 39 -> setStringValue(cell, student::setRteApplicationNo);
            case 40 -> student.setAttendedSchool(getStringValue(cell));
            case 41 -> setStringValue(cell, student::setAttendedClass);
            case 42 -> student.setSchoolAffiliated(getStringValue(cell));
            case 43 -> student.setLastSession(getStringValue(cell));
            case 44 -> student.setMotherQualification(getStringValue(cell));
            case 45 -> student.setFatherQualification(getStringValue(cell));
            case 46 -> student.setGuardianQualification(getStringValue(cell));
            case 47 -> student.setMotherOccupation(getStringValue(cell));
            case 48 -> student.setFatherOccupation(getStringValue(cell));
            case 49 -> student.setGuardianOccupation(getStringValue(cell));
            case 50 -> student.setMotherResidentialAddress(getStringValue(cell));
            case 51 -> student.setFatherResidentialAddress(getStringValue(cell));
            case 52 -> student.setGuardianResidentialAddress(getStringValue(cell));
            case 53 -> setStringValue(cell, student::setMotherIncome);
            case 54 -> setStringValue(cell, student::setFatherIncome);
            case 55 -> setStringValue(cell, student::setGuardianIncome);
            case 56 -> student.setMotherEmail(getStringValue(cell));
            case 57 -> student.setFatherEmail(getStringValue(cell));
            case 58 -> student.setGuardianEmail(getStringValue(cell));
            case 59 -> student.setMotherMobile(getStringValue(cell));
            case 60 -> student.setFatherMobile(getStringValue(cell));
            case 61 -> student.setGuardianMobile(getStringValue(cell));
            case 62 -> setStringValue(cell, student::setTcNo);
          //  case 63 -> setStringValue(cell, student::setTcDate);
            // In your StudentExcelDetails processing code (case 63):
            case 63 -> {
                if (cell.getCellType() == CellType.NUMERIC) {
                    student.setTcDate(String.valueOf((int) cell.getNumericCellValue()));
                } else {
                    String tcDate = cell.getStringCellValue().trim();
                    // Handle empty/missing values
                    student.setTcDate(tcDate.isEmpty() ? "0" : tcDate);
                }
            }
            case 64 -> setStringValue(cell, student::setScholarshipId);
            case 65 -> student.setScholarshipPassword(getStringValue(cell));
            case 66 -> setStringValue(cell, student::setDomicileApplicationNo);
            case 67 -> setStringValue(cell, student::setIncomeApplicationNo);
            case 68 -> setStringValue(cell, student::setCasteApplicationNo);
            case 69 -> setStringValue(cell, student::setDobApplicationNo);
            case 70 -> setStringValue(cell, student::setMotherAadharNo);
            case 71 -> setStringValue(cell, student::setFatherAadharNo);
            case 72 -> setStringValue(cell, student::setGuardianAadharNo);
            case 73 -> setStringValue(cell, student::setHeight);
            case 74 -> setStringValue(cell, student::setWeight);
            case 75 -> student.setBankName(getStringValue(cell));
            case 76 -> student.setBranchName(getStringValue(cell));
            case 77 -> setStringValue(cell, student::setBankAccountNo);
            case 78 -> setStringValue(cell, student::setBankIfsc);
            case 79 -> setStringValue(cell, student::setPanNo);
            case 80 -> student.setReference(getStringValue(cell));
            case 81 -> setStringValue(cell, student::setGovtStudentId);
            case 82 -> setStringValue(cell, student::setGovtFamilyId);
            case 83 -> setBooleanValue(cell, student::setDropout);
            case 84 -> student.setDropoutReason(getStringValue(cell));
            case 85 -> setDateValue(cell, student::setDropoutDate);
            case 86 -> student.setPreviousQualification(getStringValue(cell));
            case 87 -> setStringValue(cell, student::setPreviousPassYear);
            case 88 -> setStringValue(cell, student::setPreviousRollNo);
            case 89 -> setStringValue(cell, student::setPreviousObtMarks);
            case 90 -> setStringValue(cell, student::setPreviousPercentage);
            case 91 -> student.setPreviousSubjects(getStringValue(cell));
            case 92 -> student.setPreviousSchoolName(getStringValue(cell));
            default -> throw new IllegalArgumentException("Unexpected column index: " + columnIndex);
        }
    }

    // Helper methods
    private static String getStringValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.format("%.0f", cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private static void setStringValue(Cell cell, Consumer<String> setter) {
        setter.accept(getStringValue(cell));
    }

    private static void setIntegerValue(Cell cell, Consumer<Integer> setter) {
        try {
            int value = (int) (cell.getCellType() == CellType.NUMERIC
                    ? cell.getNumericCellValue()
                    : Double.parseDouble(cell.getStringCellValue()));
            setter.accept(value);
        } catch (NumberFormatException e) {
            setter.accept(0);
        }
    }

    private static void setBooleanValue(Cell cell, Consumer<Boolean> setter) {
        if (cell.getCellType() == CellType.BOOLEAN) {
            setter.accept(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue().trim().toLowerCase();
            setter.accept(value.equals("true") || value.equals("yes") || value.equals("1"));
        } else {
            setter.accept(false);
        }
    }


    private static void setDateValue(Cell cell, Consumer<Date> setter) {
        try {
            if (cell == null) {
                setter.accept(null);
                return;
            }

            // Handle numeric dates (Excel date format)
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    setter.accept(cell.getDateCellValue());
                } else {
                    // Handle numeric values that aren't Excel-formatted dates
                    double numericValue = cell.getNumericCellValue();
                    Date javaDate = DateUtil.getJavaDate(numericValue);
                    setter.accept(javaDate);
                }
                return;
            }

            // Handle string dates
            if (cell.getCellType() == CellType.STRING) {
                String dateString = cell.getStringCellValue().trim();
                if (dateString.isEmpty()) {
                    setter.accept(null);
                    return;
                }

                Date parsedDate = parseFlexibleDate(dateString);
                if (parsedDate != null) {
                    setter.accept(parsedDate);
                } else {
                    logDateError(cell, dateString);
                    setter.accept(null);
                }
                return;
            }

            // Handle blank cells
            setter.accept(null);

        } catch (Exception e) {
            logDateError(cell, "Unexpected error: " + e.getMessage());
            setter.accept(null);
        }
    }

    private static Date parseFlexibleDate(String dateString) {
        String[] formats = {
                "MM/dd/yyyy", "dd-MM-yyyy", "yyyy/MM/dd",
                "dd MMM yyyy", "dd/MM/yyyy", "yyyy-MM-dd",
                "MMddyyyy", "ddMMyyyy", "yyyyMMdd"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                return sdf.parse(dateString);
            } catch (ParseException ignored) {}
        }
        return null;
    }

    private static void logDateError(Cell cell, String value) {
        String cellAddress = cell != null ? cell.getAddress().formatAsString() : "unknown";
        String errorMsg = String.format(
                "Date parse error at %s. Value: '%s'. Expected formats: MM/dd/yyyy, dd-MM-yyyy, etc.",
                cellAddress,
                value
        );
        System.err.println(errorMsg);
    }
}
