package com.sms.dao;

import com.sms.model.StudentExcelDetails;

public interface StudentExcelDao {
   public StudentExcelDetails addData(StudentExcelDetails studentExcelDetails,String schoolCode) throws Exception;
   void truncateStudentExcelTable(String schoolCode);
}
