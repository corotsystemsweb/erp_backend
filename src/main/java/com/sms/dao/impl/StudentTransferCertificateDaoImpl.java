package com.sms.dao.impl;

import com.sms.dao.StudentTransferCertificateDao;
import com.sms.model.SchoolDetails;
import com.sms.model.StudentTransferCertificateDetails;
import com.sms.service.SchoolService;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
// Correct import for Java's List
// Import ListItem separately
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Repository
public class StudentTransferCertificateDaoImpl implements StudentTransferCertificateDao {
    private final JdbcTemplate jdbcTemplate;

    public StudentTransferCertificateDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Autowired
    private SchoolService schoolService;

    @Override
    public StudentTransferCertificateDetails createTC(StudentTransferCertificateDetails tc, String schoolCode) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = "INSERT INTO transfer_certificates (admission_no, student_name, dob, dob_in_words, proof_of_dob, father_name, mother_name, guardian_name, class_at_leaving, section_at_leaving, last_exam_passed, last_class_studied, total_working_days, total_presence, subjects_studied, category, fee_concession, extracurricular, school_category, games_activities, date_of_first_admission, date_of_application, date_struck_off, date_of_issue, reason_for_leaving, fee_dues_status, remarks, issued_by, school_id, session_id, tc_data) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, tc.getAdmissionNo());
                ps.setString(2, tc.getStudentName());
                ps.setDate(3, tc.getDob());
                ps.setString(4, tc.getDobInWords());
                ps.setString(5, tc.getProofOfDob()); // ENUM stored as String in DB
                ps.setString(6, tc.getFatherName());
                ps.setString(7, tc.getMotherName());
                ps.setString(8, tc.getGuardianName());
                ps.setString(9, tc.getClassAtLeaving());
                ps.setString(10, tc.getSectionAtLeaving());
                ps.setString(11, tc.getLastExamPassed());
                ps.setString(12, tc.getLastClassStudied());
                ps.setInt(13, tc.getTotalWorkingDays());
                ps.setInt(14, tc.getTotalPresence());
                ps.setString(15, tc.getSubjectsStudied());
                ps.setString(16, tc.getCategory()); // ENUM stored as String in DB
                ps.setString(17, tc.getFeeConcession());
                ps.setString(18, tc.getExtracurricular());
                ps.setString(19, tc.getSchoolCategory()); // ENUM stored as String in DB
                ps.setString(20, tc.getGamesActivities());
                ps.setDate(21, tc.getDateOfFirstAdmission());
                ps.setDate(22, tc.getDateOfApplication());
                ps.setDate(23, tc.getDateStruckOff());
                ps.setDate(24, tc.getDateOfIssue());
                ps.setString(25, tc.getReasonForLeaving());
                ps.setString(26, tc.getFeeDuesStatus()); // ENUM stored as String in DB
                ps.setString(27, tc.getRemarks());
                ps.setInt(28, tc.getIssuedBy());
                ps.setInt(29, tc.getSchoolId());
                ps.setInt(30, tc.getSessionId());
                ps.setString(31, tc.getTcData());

                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("tc_id")) {
                Long generatedKey = ((Number) keys.get("tc_id")).longValue();
                tc.setTcId(generatedKey);
            }
            return tc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }


    @Override
    public byte[] fetchTCPdf(Long tcId, String schoolCode) {
        System.out.println(tcId);
        try {
            // Fetch Transfer Certificate details from DB
            StudentTransferCertificateDetails tcDetails = getTC(tcId, schoolCode);
            if (tcDetails == null) {
                System.out.println("No Transfer Certificate found for ID: " + tcId);
                return null;
            }

            // Fetch School Details
            List<SchoolDetails> schoolDetailsList = schoolService.getAllSchoolDetails(schoolCode);
            SchoolDetails schoolDetails = schoolDetailsList.isEmpty() ? null : schoolDetailsList.get(0);
            SchoolDetails imageData = null;
            try {
                imageData = schoolService.getImage(schoolCode, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] imageBytes = null;
            if (imageData != null && imageData.getSchoolImageString() != null) {
                try {
                    imageBytes = Base64.getDecoder().decode(imageData.getSchoolImageString());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 25f, 25f, 25f, 25f);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            BackgroundImagePageEvent event = new BackgroundImagePageEvent(imageBytes);
            writer.setPageEvent(event);

            document.open();

            // Font configuration
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

            // 1️⃣ School Header with Logo
            if (schoolDetails != null) {
                PdfPTable headerTable = new PdfPTable(imageBytes != null ? 2 : 1);
                headerTable.setWidthPercentage(100);
                headerTable.setWidths(new float[]{1.2f, 4.8f}); // Adjust logo width
                headerTable.setSpacingAfter(5f);

                // Add logo if available
                if (imageBytes != null) {
                    try {
                        Image logo = Image.getInstance(imageBytes);
                        logo.scaleToFit(70, 70);
                        PdfPCell logoCell = new PdfPCell(logo);
                        logoCell.setBorder(PdfPCell.NO_BORDER);
                        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        logoCell.setVerticalAlignment(Element.ALIGN_CENTER);
                        logoCell.setPaddingRight(5f); // Reduced right padding
                        logoCell.setPaddingBottom(0f);
                        logoCell.setPaddingTop(10f);
                        headerTable.addCell(logoCell);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // School details cell
                PdfPCell detailsCell = new PdfPCell();
                detailsCell.setBorder(PdfPCell.NO_BORDER);
                detailsCell.setPaddingLeft(0f);
                detailsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                detailsCell.setVerticalAlignment(Element.ALIGN_TOP);

                Paragraph schoolHeader = new Paragraph(schoolDetails.getSchoolName(), titleFont);
                schoolHeader.setAlignment(Element.ALIGN_LEFT);
                schoolHeader.setSpacingAfter(5f);

                Paragraph affiliation = new Paragraph(
                        "AFFILIATED TO THE CENTRAL BOARD OF SECONDARY EDUCATION",
                        normalFont
                );
                affiliation.setAlignment(Element.ALIGN_LEFT);
                affiliation.setSpacingAfter(2f);

                Paragraph schoolInfo = new Paragraph(
                        schoolDetails.getSchoolAddress() + "\n" +
                                "Phone: " + schoolDetails.getPhoneNumber() + "\n" +
                                "CBSE Affl No: " + schoolDetails.getSchoolAffiliationNo() +
                                "  School Code: " + schoolDetails.getSchoolCode(),
                        normalFont
                );
                schoolInfo.setAlignment(Element.ALIGN_LEFT);
                schoolInfo.setSpacingAfter(10f);

                detailsCell.addElement(schoolHeader);
                detailsCell.addElement(affiliation);
                detailsCell.addElement(schoolInfo);
                headerTable.addCell(detailsCell);

                document.add(headerTable);
            }

            // 2️⃣ Transfer Certificate Title
            Paragraph title = new Paragraph("TRANSFER CERTIFICATE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            document.add(title);

            // 3️⃣ Certificate Details
            Paragraph details = new Paragraph(
                    "Book No: _________                    " +
                            "Sl. No: _________                    " +
                            "Admission No: _________",
                    normalFont
            );
            details.setSpacingAfter(10f);
            document.add(details);

            // 4️⃣ Student Details
            addStudentDetail(document, "1. Name of the Student: ", tcDetails.getStudentName(), normalFont);
            addStudentDetail(document, "2. Mother's Name: ", tcDetails.getMotherName(), normalFont);
            addStudentDetail(document, "3. Father's/Guardian's Name: ", tcDetails.getFatherName(), normalFont);
            addStudentDetail(document, "4. Date of birth (in Christian Era) according to Admission Register (in figures) DD-MM-YYYY: ",
                    String.valueOf(tcDetails.getDob()), normalFont);
            addStudentDetail(document, "5. (in words): ", tcDetails.getDobInWords(), normalFont);
            addStudentDetail(document, "6. Proof for Date of Birth submitted at the time of admission: ",
                    tcDetails.getProofOfDob(), normalFont);
            addStudentDetail(document, "7. Nationality: ", "Indian", normalFont);
            addStudentDetail(document, "8. Whether the candidate belongs to Scheduled Caste or Scheduled Tribe or OBC: ",
                    tcDetails.getCategory(), normalFont);
            addStudentDetail(document, "9. Date of First admission in the school with class DD-MM-YYYY: ",
                    String.valueOf(tcDetails.getDateOfFirstAdmission()), normalFont);
            addStudentDetail(document, "10. Class in which the pupil last Studied (in Figures): ",
                    tcDetails.getClassAtLeaving(), normalFont);
            addStudentDetail(document, "11. School/Board annual examination last taken with result: ",
                    tcDetails.getLastExamPassed(), normalFont);
            addStudentDetail(document, "12. Whether failed, if so once/twice in the same class: ",
                    tcDetails.getLastExamPassed(), normalFont);
            addStudentDetail(document, "13. Subjects Studied: ", tcDetails.getSubjectsStudied(), normalFont);
            addStudentDetail(document, "14. Total no. of working days in the academic session: ",
                    String.valueOf(tcDetails.getTotalWorkingDays()), normalFont);
            addStudentDetail(document, "15. Total no. of presence in the academic session: ",
                    String.valueOf(tcDetails.getTotalPresence()), normalFont);
            addStudentDetail(document, "16. Month up to Which the pupil has paid school dues: ",
                    tcDetails.getFeeDuesStatus(), normalFont);
            addStudentDetail(document, "17. Any fee concession availed of, if so, the nature of such Concession: ",
                    tcDetails.getFeeConcession(), normalFont);
            addStudentDetail(document, "18. Whether NCC cadet/Boy Scout/Girl Guide (details may be given): ",
                    tcDetails.getExtracurricular(), normalFont);
            addStudentDetail(document, "19. Whether school is under Govt./Minority/Independent Category: ",
                    tcDetails.getSchoolCategory(), normalFont);
            addStudentDetail(document, "20. Games/Extra-curricular Activities: ",
                    tcDetails.getGamesActivities(), normalFont);
            addStudentDetail(document, "21. Reason for Leaving: ", tcDetails.getReasonForLeaving(), normalFont);
            addStudentDetail(document, "22. Date of Application for Certificate: ",
                    String.valueOf(tcDetails.getDateOfApplication()), normalFont);
            addStudentDetail(document, "23. Date Struck Off Rolls: ",
                    String.valueOf(tcDetails.getDateStruckOff()), normalFont);
            addStudentDetail(document, "24. Date of Issue of Certificate: ",
                    String.valueOf(tcDetails.getDateOfIssue()), normalFont);

            // 5️⃣ Declaration and Signature
            Paragraph declaration = new Paragraph(
                    "I hereby declare that the above information including Name of the Candidate, " +
                            "Father’s Name, Mother’s Name and Date of Birth furnished above is correct as per school records.",
                    normalFont
            );
            declaration.setSpacingAfter(10f);
            document.add(declaration);

            Paragraph signatureDate = new Paragraph("Date: ___________", normalFont);
            signatureDate.setAlignment(Element.ALIGN_LEFT);
            document.add(signatureDate);

            Paragraph signature = new Paragraph("Signature of the Principal", boldFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            signature.setSpacingBefore(10f);
            document.add(signature);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addStudentDetail(Document document, String label, String value, Font font) throws DocumentException {
        Paragraph p = new Paragraph(label + value, font);
        p.setSpacingAfter(4f);
        document.add(p);
    }

    // Background Image Event Handler
    class BackgroundImagePageEvent extends PdfPageEventHelper {
        private Image backgroundImage;

        public BackgroundImagePageEvent(byte[] imageBytes) {
            try {
                if (imageBytes != null) {
                    this.backgroundImage = Image.getInstance(imageBytes);
                    this.backgroundImage.scaleToFit(300, 300);
                    this.backgroundImage.setAbsolutePosition(
                            (PageSize.A4.getWidth() - backgroundImage.getScaledWidth()) / 2,
                            (PageSize.A4.getHeight() - backgroundImage.getScaledHeight()) / 2
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            if (backgroundImage != null) {
                PdfContentByte canvas = writer.getDirectContentUnder();
                PdfGState gState = new PdfGState();
                gState.setFillOpacity(0.05f);
                canvas.setGState(gState);
                try {
                    canvas.addImage(backgroundImage);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public StudentTransferCertificateDetails getTC(Long tcId, String schoolCode) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = "SELECT * FROM transfer_certificates WHERE tc_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{tcId}, (rs, rowNum) -> {
                StudentTransferCertificateDetails tc = new StudentTransferCertificateDetails();
                tc.setTcId(rs.getLong("tc_id"));
                tc.setAdmissionNo(rs.getString("admission_no"));
                tc.setStudentName(rs.getString("student_name"));
                tc.setDob(rs.getDate("dob"));
                tc.setDobInWords(rs.getString("dob_in_words"));
                tc.setFatherName(rs.getString("father_name"));
                tc.setMotherName(rs.getString("mother_name"));
                tc.setGuardianName(rs.getString("guardian_name"));
                tc.setClassAtLeaving(rs.getString("class_at_leaving"));
                tc.setSectionAtLeaving(rs.getString("section_at_leaving"));
                tc.setLastExamPassed(rs.getString("last_exam_passed"));
                tc.setSubjectsStudied(rs.getString("subjects_studied"));
                tc.setReasonForLeaving(rs.getString("reason_for_leaving"));
                tc.setFeeDuesStatus(rs.getString("fee_dues_status"));
                tc.setRemarks(rs.getString("remarks"));
                tc.setIssuedBy(rs.getInt("issued_by"));
                tc.setSchoolId(rs.getInt("school_id"));
                tc.setSessionId(rs.getInt("session_id"));
                tc.setTcData(rs.getString("tc_data"));
                tc.setProofOfDob(rs.getString("proof_of_dob"));
                tc.setCategory(rs.getString("category"));
                tc.setDateOfFirstAdmission(rs.getDate("date_of_first_admission"));
                tc.setTotalWorkingDays(rs.getInt("total_working_days"));
                tc.setTotalPresence(rs.getInt("total_presence"));
                tc.setFeeConcession(rs.getString("fee_concession"));
                tc.setExtracurricular(rs.getString("extracurricular"));
                tc.setSchoolCategory(rs.getString("school_category"));
                tc.setGamesActivities(rs.getString("games_activities"));
                tc.setDateOfApplication(rs.getDate("date_of_application"));
                tc.setDateStruckOff(rs.getDate("date_struck_off"));
                tc.setDateOfIssue(rs.getDate("date_of_issue"));
                tc.setLastClassStudied(rs.getString("last_class_studied"));
                return tc;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<StudentTransferCertificateDetails> getAllTc(int sessionId, String schoolCode) {

        String sql = "SELECT * FROM transfer_certificates WHERE session_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            return jdbcTemplate.query(sql, new Object[]{sessionId}, (rs, rowNum) -> {
                StudentTransferCertificateDetails tc = new StudentTransferCertificateDetails();
                tc.setTcId(rs.getLong("tc_id"));
                tc.setAdmissionNo(rs.getString("admission_no"));
                tc.setStudentName(rs.getString("student_name"));
                tc.setDob(rs.getDate("dob"));
                tc.setDobInWords(rs.getString("dob_in_words"));
                tc.setFatherName(rs.getString("father_name"));
                tc.setMotherName(rs.getString("mother_name"));
                tc.setGuardianName(rs.getString("guardian_name"));
                tc.setClassAtLeaving(rs.getString("class_at_leaving"));
                tc.setSectionAtLeaving(rs.getString("section_at_leaving"));
                tc.setLastExamPassed(rs.getString("last_exam_passed"));
                tc.setSubjectsStudied(rs.getString("subjects_studied"));
                tc.setReasonForLeaving(rs.getString("reason_for_leaving"));
                tc.setFeeDuesStatus(rs.getString("fee_dues_status"));
                tc.setRemarks(rs.getString("remarks"));
                tc.setIssuedBy(rs.getInt("issued_by"));
                tc.setSchoolId(rs.getInt("school_id"));
                tc.setSessionId(rs.getInt("session_id"));
                tc.setTcData(rs.getString("tc_data"));
                return tc;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }


    public StudentTransferCertificateDetails updateTC(StudentTransferCertificateDetails tc, Long tcId, String schoolCode) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        // SQL ordered EXACTLY as per your table structure (excluding tc_id)
        String sql = "UPDATE transfer_certificates SET "
                + "school_id=?, session_id=?, issued_date=?, issued_by=?, "
                + "admission_no=?, student_name=?, dob=?, dob_in_words=?, proof_of_dob=?, "
                + "father_name=?, mother_name=?, guardian_name=?, "
                + "class_at_leaving=?, section_at_leaving=?, last_exam_passed=?, last_class_studied=?, "
                + "total_working_days=?, total_presence=?, subjects_studied=?, "
                + "category=?, fee_concession=?, extracurricular=?, school_category=?, games_activities=?, "
                + "date_of_first_admission=?, date_of_application=?, date_struck_off=?, date_of_issue=?, "
                + "reason_for_leaving=?, fee_dues_status=?, remarks=?, tc_data=?::jsonb "
                + "WHERE tc_id=?";
        System.out.println(sql);
        try {
            int rowsUpdated = jdbcTemplate.update(sql,
                    // Column order matches table structure:
                    tc.getSchoolId(),
                    tc.getSessionId(),
                    tc.getIssuedDate(),    // Added missing issued_date
                    tc.getIssuedBy(),
                    tc.getAdmissionNo(),
                    tc.getStudentName(),
                    tc.getDob(),
                    tc.getDobInWords(),
                    tc.getProofOfDob(),
                    tc.getFatherName(),
                    tc.getMotherName(),
                    tc.getGuardianName(),
                    tc.getClassAtLeaving(),
                    tc.getSectionAtLeaving(),
                    tc.getLastExamPassed(),
                    tc.getLastClassStudied(),
                    tc.getTotalWorkingDays(),
                    tc.getTotalPresence(),
                    tc.getSubjectsStudied(),
                    tc.getCategory(),
                    tc.getFeeConcession(),
                    tc.getExtracurricular(),
                    tc.getSchoolCategory(),
                    tc.getGamesActivities(),
                    tc.getDateOfFirstAdmission(),
                    tc.getDateOfApplication(),
                    tc.getDateStruckOff(),
                    tc.getDateOfIssue(),
                    tc.getReasonForLeaving(),
                    tc.getFeeDuesStatus(),
                    tc.getRemarks(),
                    tc.getTcData(),      // JSON data
                    tcId                 // WHERE clause
            );

            if (rowsUpdated == 0) {
                throw new DataAccessException("No Transfer Certificate found with ID: " + tcId) {};
            }
            return tc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Error updating Transfer Certificate", e) {};
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
