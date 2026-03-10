package com.sms.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
    private static final SimpleDateFormat EXCEL_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    private static final List<String> DATE_PATTERNS = Arrays.asList(
            "MM/dd/yyyy", "dd-MM-yyyy", "yyyy/MM/dd",
            "dd MMM yyyy", "dd/MM/yyyy", "yyyy-MM-dd"
    );

    public static final LocalDate NOW_LOCAL_DATE() {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter);
    }

    public static final LocalDate NEXT_MONTH_DATE() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter);
    }

    public static final String getDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static LocalTime parseTime(String timeStr) {
        DateTimeFormatter timeFormatterWithSeconds = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter timeFormatterWithoutSeconds = DateTimeFormatter.ofPattern("HH:mm");
        try {
            return LocalTime.parse(timeStr, timeFormatterWithSeconds);
        } catch (DateTimeParseException e) {
            return LocalTime.parse(timeStr, timeFormatterWithoutSeconds);
        }
    }

// In DateUtil.java

    public static boolean isCellDateFormatted(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            // Use Apache POI's DateUtil instead of recursive call
            return org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell);
        }

        if (cell.getCellType() == CellType.STRING) {
            return isStringDate(cell.getStringCellValue());
        }

        return false;
    }

    public static Date getJavaDate(double numericValue) {
        // Use Apache POI's DateUtil to convert Excel numeric date to Java Date
        return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(numericValue);
    }
    private static boolean isStringDate(String value) {
        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                sdf.parse(value.trim());
                return true;
            } catch (ParseException ignored) {}
        }
        return false;
    }

    /*public static Date getJavaDate(double numericValue) {
        return DateUtil.getJavaDate(numericValue);
    }*/
}
