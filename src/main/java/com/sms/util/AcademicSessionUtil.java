package com.sms.util;

import java.time.LocalDate;

public class AcademicSessionUtil {
    private static final LocalDate currentDate = LocalDate.now();
    // Check if the current month is before or after September (month 9)
    private static final int startYear = currentDate.getYear();
    // Academic year format: startYear-endYear (e.g., 2020-2021)
    private static final int endYear = startYear + 1;
    private static final String academicYear = startYear + "-" + endYear;

    public static final String getCurrentAcademicYear() {
        return academicYear;
    }
}
