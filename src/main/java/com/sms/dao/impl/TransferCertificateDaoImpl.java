
package com.sms.dao.impl;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.sms.dao.TransferCertificateDao;
import com.sms.model.SchoolDetails;
import com.sms.model.TransferCertificateDetails;
import com.sms.service.SchoolService;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Map;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.List;  // Correct import for Java's List
// Import ListItem separately
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

@Repository
public class TransferCertificateDaoImpl implements TransferCertificateDao {
    private final JdbcTemplate jdbcTemplate;

    public TransferCertificateDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Autowired
    private SchoolService schoolService;


//    @Override
//    public TransferCertificateDetails createTC(TransferCertificateDetails tc, String schoolCode) {
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//        String sql = "INSERT INTO transfer_certificates (admission_no, student_id, student_name, dob, dob_in_words, proof_of_dob, pen_no, apaar_id, father_name, mother_name, guardian_name, class_at_leaving, section_at_leaving, last_exam_passed, last_class_studied, total_working_days, total_presence, subjects_studied, category, fee_concession, extracurricular, school_category, games_activities, date_of_first_admission, date_of_application, date_struck_off, date_of_issue, reason_for_leaving, fee_dues_status, remarks, issued_by, school_id, school_code, session_id, academic_session, tc_data) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)";
//        try {
//            KeyHolder keyHolder = new GeneratedKeyHolder();
//            jdbcTemplate.update(connection -> {
//                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//                ps.setString(1, tc.getAdmissionNo());
//                ps.setLong(2, tc.getStudentId());
//                ps.setString(3, tc.getStudentName());
//                ps.setDate(4, tc.getDob());
//                ps.setString(5, tc.getDobInWords());
//                ps.setString(6, tc.getProofOfDob()); // ENUM stored as String in DB
//                ps.setString(7, tc.getPenNo());
//                ps.setString(8, tc.getApaarId());
//                ps.setString(9, tc.getFatherName());
//                ps.setString(10, tc.getMotherName());
//                ps.setString(11, tc.getGuardianName());
//                ps.setString(12, tc.getClassAtLeaving());
//                ps.setString(13, tc.getSectionAtLeaving());
//                ps.setString(14, tc.getLastExamPassed());
//                ps.setString(15, tc.getLastClassStudied());
//                ps.setInt(16, tc.getTotalWorkingDays());
//                ps.setInt(17, tc.getTotalPresence());
//                ps.setString(18, tc.getSubjectsStudied());
//                ps.setString(19, tc.getCategory()); // ENUM stored as String in DB
//                ps.setString(20, tc.getFeeConcession());
//                ps.setString(21, tc.getExtracurricular());
//                ps.setString(22, tc.getSchoolCategory()); // ENUM stored as String in DB
//                ps.setString(23, tc.getGamesActivities());
//                ps.setDate(24, tc.getDateOfFirstAdmission());
//                ps.setDate(25, tc.getDateOfApplication());
//                ps.setDate(26, tc.getDateStruckOff());
//                ps.setDate(27, tc.getDateOfIssue());
//                ps.setString(28, tc.getReasonForLeaving());
//                ps.setString(29, tc.getFeeDuesStatus()); // ENUM stored as String in DB
//                ps.setString(30, tc.getRemarks());
//                ps.setInt(31, tc.getIssuedBy());
//                ps.setInt(32, tc.getSchoolId());
//                ps.setString(33, tc.getSchoolCode());
//                ps.setInt(34, tc.getSessionId());
//                ps.setString(35, tc.getAcademicSession());
//                ps.setString(36, tc.getTcData());
//
//                return ps;
//            }, keyHolder);
//
//            Map<String, Object> keys = keyHolder.getKeys();
//            if (keys != null && keys.containsKey("tc_id")) {
//                Long generatedKey = ((Number) keys.get("tc_id")).longValue();
//                tc.setTcId(generatedKey);
//            }
//            return tc;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            DatabaseUtil.closeDataSource(jdbcTemplate);
//        }
//    }

    @Override
    public TransferCertificateDetails createTC(TransferCertificateDetails tc, String schoolCode) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = "INSERT INTO transfer_certificates_alumni (school_id, school_code, school_category, session_id, academic_session, student_id, admission_no, student_name, dob, dob_in_words, proof_of_dob, pen_no, apaar_id, father_name, mother_name, is_student_failed, subjects_offered, is_promoted_to_next_class, no_of_meetings, total_present, class_at_leaving, section_at_leaving, last_annual_exam_result, last_studied_class, category, fee_due_status, extracurricular, games_activities, general_conduct, reason_for_leaving, date_of_issue, date_struck_off, remarks, issued_date, issued_by, sr_number, tc_data) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, 1);
                ps.setString(2, tc.getSchoolCode());
                ps.setString(3, tc.getSchoolCategory());
                ps.setInt(4, tc.getSessionId());
                ps.setString(5, tc.getAcademicSession());
                ps.setLong(6, tc.getStudentId());
                ps.setString(7, tc.getAdmissionNo());
                ps.setString(8, tc.getStudentName());
                ps.setDate(9, tc.getDob());
                ps.setString(10, tc.getDobInWords());
                ps.setString(11, tc.getProofOfDob()); // ENUM stored as String in DB
                ps.setString(12, tc.getPenNo());
                ps.setString(13, tc.getApaarId());
                ps.setString(14, tc.getFatherName());
                ps.setString(15, tc.getMotherName());
                ps.setString(16, tc.getIsStudentFailed());
                ps.setString(17, tc.getSubjectsOffered());
                ps.setString(18, tc.getIsPromotedToNextClass());
                ps.setInt(19, tc.getNoOfMeetings());
                ps.setInt(20, tc.getTotalPresent());
                ps.setString(21, tc.getClassAtLeaving());
                ps.setString(22, tc.getSectionAtLeaving());
                ps.setString(23, tc.getLastAnnualExamResult());
                ps.setString(24, tc.getLastStudiedClass());
                ps.setString(25, tc.getCategory());
                ps.setString(26, tc.getFeeDueStatus());
                ps.setString(27, tc.getExtracurricular());
                ps.setString(28, tc.getGamesActivities());
                ps.setString(29, tc.getGeneralConduct());
                ps.setString(30, tc.getReasonForLeaving());
                ps.setDate(31, tc.getDateOfIssue());
                ps.setDate(32, tc.getDateStruckOff());
                ps.setString(33, tc.getRemarks());
                ps.setTimestamp(34, tc.getIssuedDate());
                ps.setInt(35, tc.getIssuedBy());
                ps.setString(36, tc.getSrNumber());
                ps.setString(37, tc.getTcData());
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


//    @Override
//    public byte[] fetchTCPdf(Long tcId, String schoolCode) {
//        System.out.println(tcId);
//        try {
//            // Fetch Transfer Certificate details from DB
//            TransferCertificateDetails tcDetails = getTC(tcId, schoolCode);
//            if (tcDetails == null) {
//                System.out.println("No Transfer Certificate found for ID: " + tcId);
//                return null;
//            }
//
//            // Fetch School Details
//            List<SchoolDetails> schoolDetailsList = schoolService.getAllSchoolDetails(schoolCode);
//            SchoolDetails schoolDetails = schoolDetailsList.isEmpty() ? null : schoolDetailsList.get(0);
//            SchoolDetails imageData = null;
//            try {
//                imageData = schoolService.getImage(schoolCode, 1);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            byte[] imageBytes = null;
//            if (imageData != null && imageData.getSchoolImageString() != null) {
//                try {
//                    imageBytes = Base64.getDecoder().decode(imageData.getSchoolImageString());
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            Document document = new Document(PageSize.A4, 25f, 25f, 25f, 25f);
//            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
//
//            BackgroundImagePageEvent event = new BackgroundImagePageEvent(imageBytes);
//            writer.setPageEvent(event);
//
//            document.open();
//
//            // Font configuration
//            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
//            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
//            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
//
//            // 1️⃣ School Header with Logo
//            if (schoolDetails != null) {
//                PdfPTable headerTable = new PdfPTable(imageBytes != null ? 2 : 1);
//                headerTable.setWidthPercentage(100);
//                headerTable.setWidths(new float[]{1.2f, 4.8f}); // Adjust logo width
//                headerTable.setSpacingAfter(5f);
//
//                // Add logo if available
//                if (imageBytes != null) {
//                    try {
//                        Image logo = Image.getInstance(imageBytes);
//                        logo.scaleToFit(70, 70);
//                        PdfPCell logoCell = new PdfPCell(logo);
//                        logoCell.setBorder(PdfPCell.NO_BORDER);
//                        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                        logoCell.setVerticalAlignment(Element.ALIGN_CENTER);
//                        logoCell.setPaddingRight(5f); // Reduced right padding
//                        logoCell.setPaddingBottom(0f);
//                        logoCell.setPaddingTop(10f);
//                        headerTable.addCell(logoCell);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                // School details cell
//                PdfPCell detailsCell = new PdfPCell();
//                detailsCell.setBorder(PdfPCell.NO_BORDER);
//                detailsCell.setPaddingLeft(0f);
//                detailsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                detailsCell.setVerticalAlignment(Element.ALIGN_TOP);
//
//                Paragraph schoolHeader = new Paragraph(schoolDetails.getSchoolName(), titleFont);
//                schoolHeader.setAlignment(Element.ALIGN_LEFT);
//                schoolHeader.setSpacingAfter(5f);
//
//                Paragraph affiliation = new Paragraph(
//                        "AFFILIATED TO THE CENTRAL BOARD OF SECONDARY EDUCATION",
//                        normalFont
//                );
//                affiliation.setAlignment(Element.ALIGN_LEFT);
//                affiliation.setSpacingAfter(2f);
//
//                Paragraph schoolInfo = new Paragraph(
//                        schoolDetails.getSchoolAddress() + "\n" +
//                                "Phone: " + schoolDetails.getPhoneNumber() + "\n" +
//                                "CBSE Affl No: " + schoolDetails.getSchoolAffiliationNo() +
//                                "  School Code: " + schoolDetails.getSchoolCode(),
//                        normalFont
//                );
//                schoolInfo.setAlignment(Element.ALIGN_LEFT);
//                schoolInfo.setSpacingAfter(10f);
//
//                detailsCell.addElement(schoolHeader);
//                detailsCell.addElement(affiliation);
//                detailsCell.addElement(schoolInfo);
//                headerTable.addCell(detailsCell);
//
//                document.add(headerTable);
//            }
//
//            // 2️⃣ Transfer Certificate Title
//            Paragraph title = new Paragraph("TRANSFER CERTIFICATE", titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            title.setSpacingAfter(10f);
//            document.add(title);
//
////            // 3️⃣ Certificate Details
////            Paragraph details = new Paragraph(
////                    "Book No: _________                    " +
////                            "SR. No: _________                    " +
////                            "Pen No: _________                    " +
////                            "Admission No: _________",
////                    normalFont
////            );
////            details.setSpacingAfter(10f);
////            document.add(details);
//            // 3️⃣ Certificate Details using PdfPTable for proper alignment
//            PdfPTable detailsTable = new PdfPTable(3); // 4 columns
//            detailsTable.setWidthPercentage(100);
//            detailsTable.setSpacingAfter(10f);
//            detailsTable.setWidths(new float[]{2f, 2f, 3f}); // equal widths
//
//            detailsTable.addCell(new PdfPCell(new Phrase("SR. No: " + tcDetails.getTcId(), normalFont)) {{
//                setBorder(PdfPCell.NO_BORDER);
//            }});
//            detailsTable.addCell(new PdfPCell(new Phrase("Pen No: " + tcDetails.getPenNo(), normalFont)) {{
//                setBorder(PdfPCell.NO_BORDER);
//            }});
//            detailsTable.addCell(new PdfPCell(new Phrase("Admission No: " + tcDetails.getAdmissionNo(), normalFont)) {{
//                setBorder(PdfPCell.NO_BORDER);
//            }});
//
//            document.add(detailsTable);
//
//            // 4️⃣ Student Details
//            addStudentDetail(document, "1. Name of the Student: ", tcDetails.getStudentName(), normalFont);
//            addStudentDetail(document, "2. Mother's Name: ", tcDetails.getMotherName(), normalFont);
//            addStudentDetail(document, "3. Father's/Guardian's Name: ", tcDetails.getFatherName(), normalFont);
//            addStudentDetail(document, "4. Date of birth (in Christian Era) according to Admission Register (in figures) DD-MM-YYYY: ",
//                    String.valueOf(tcDetails.getDob()), normalFont);
//            addStudentDetail(document, "5. (in words): ", tcDetails.getDobInWords(), normalFont);
//            addStudentDetail(document, "6. Proof for Date of Birth submitted at the time of admission: ",
//                    tcDetails.getProofOfDob(), normalFont);
//            addStudentDetail(document, "7. Nationality: ", "Indian", normalFont);
//            addStudentDetail(document, "8. Whether the candidate belongs to Scheduled Caste or Scheduled Tribe or OBC: ",
//                    tcDetails.getCategory(), normalFont);
//            addStudentDetail(document, "9. Date of First admission in the school with class DD-MM-YYYY: ",
//                    String.valueOf(tcDetails.getDateOfFirstAdmission()), normalFont);
//            addStudentDetail(document, "10. Class in which the pupil last Studied (in Figures): ",
//                    tcDetails.getClassAtLeaving(), normalFont);
//            addStudentDetail(document, "11. School/Board annual examination last taken with result: ",
//                    tcDetails.getLastExamPassed(), normalFont);
//            addStudentDetail(document, "12. Whether failed, if so once/twice in the same class: ",
//                    tcDetails.getLastExamPassed(), normalFont);
//            addStudentDetail(document, "13. Subjects Studied: ", tcDetails.getSubjectsStudied(), normalFont);
//            addStudentDetail(document, "14. Total no. of working days in the academic session: ",
//                    String.valueOf(tcDetails.getTotalWorkingDays()), normalFont);
//            addStudentDetail(document, "15. Total no. of presence in the academic session: ",
//                    String.valueOf(tcDetails.getTotalPresence()), normalFont);
//            addStudentDetail(document, "16. Month up to Which the pupil has paid school dues: ",
//                    tcDetails.getFeeDuesStatus(), normalFont);
//            addStudentDetail(document, "17. Any fee concession availed of, if so, the nature of such Concession: ",
//                    tcDetails.getFeeConcession(), normalFont);
//            addStudentDetail(document, "18. Whether NCC cadet/Boy Scout/Girl Guide (details may be given): ",
//                    tcDetails.getExtracurricular(), normalFont);
//            addStudentDetail(document, "19. Whether school is under Govt./Minority/Independent Category: ",
//                    tcDetails.getSchoolCategory(), normalFont);
//            addStudentDetail(document, "20. Games/Extra-curricular Activities: ",
//                    tcDetails.getGamesActivities(), normalFont);
//            addStudentDetail(document, "21. Reason for Leaving: ", tcDetails.getReasonForLeaving(), normalFont);
//            addStudentDetail(document, "22. Date of Application for Certificate: ",
//                    String.valueOf(tcDetails.getDateOfApplication()), normalFont);
//            addStudentDetail(document, "23. Date Struck Off Rolls: ",
//                    String.valueOf(tcDetails.getDateStruckOff()), normalFont);
//            addStudentDetail(document, "24. Date of Issue of Certificate: ",
//                    String.valueOf(tcDetails.getDateOfIssue()), normalFont);
//
//            // 5️⃣ Declaration and Signature
//            Paragraph declaration = new Paragraph(
//                    "I hereby declare that the above information including Name of the Candidate, " +
//                            "Father’s Name, Mother’s Name and Date of Birth furnished above is correct as per school records.",
//                    normalFont
//            );
//            declaration.setSpacingAfter(10f);
//            document.add(declaration);
//
//            Paragraph signatureDate = new Paragraph("Date: ___________", normalFont);
//            signatureDate.setAlignment(Element.ALIGN_LEFT);
//            document.add(signatureDate);
//
//            Paragraph signature = new Paragraph("Signature of the Principal", boldFont);
//            signature.setAlignment(Element.ALIGN_RIGHT);
//            signature.setSpacingBefore(10f);
//            document.add(signature);
//
//            document.close();
//            return outputStream.toByteArray();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private void addStudentDetail(Document document, String label, String value, Font font) throws DocumentException {
//        Paragraph p = new Paragraph(label + value, font);
//        p.setSpacingAfter(4f);
//        document.add(p);
//    }
//
//    // Background Image Event Handler
//    class BackgroundImagePageEvent extends PdfPageEventHelper {
//        private Image backgroundImage;
//
//        public BackgroundImagePageEvent(byte[] imageBytes) {
//            try {
//                if (imageBytes != null) {
//                    this.backgroundImage = Image.getInstance(imageBytes);
//                    this.backgroundImage.scaleToFit(300, 300);
//                    this.backgroundImage.setAbsolutePosition(
//                            (PageSize.A4.getWidth() - backgroundImage.getScaledWidth()) / 2,
//                            (PageSize.A4.getHeight() - backgroundImage.getScaledHeight()) / 2
//                    );
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onEndPage(PdfWriter writer, Document document) {
//            if (backgroundImage != null) {
//                PdfContentByte canvas = writer.getDirectContentUnder();
//                PdfGState gState = new PdfGState();
//                gState.setFillOpacity(0.05f);
//                canvas.setGState(gState);
//                try {
//                    canvas.addImage(backgroundImage);
//                } catch (DocumentException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


@Override
public byte[] fetchTCPdf(Long tcId, String schoolCode) {
    try {
        TransferCertificateDetails tcDetails = getTC(tcId, schoolCode);
        if (tcDetails == null) return null;

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
        Document document = new Document(PageSize.A4, 40f, 40f, 30f, 35f);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        writer.setPageEvent(new BackgroundImagePageEvent(imageBytes));

        document.open();

        // ---------- FONT SETTINGS ----------
        String fontPath = "src/main/resources/fonts/NotoSansDevanagari-Regular.ttf";

        BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font largeHeaderFont = new Font(baseFont, 16, Font.BOLD);
        Font titleFont = new Font(baseFont, 14, Font.BOLD);
        Font boldFont = new Font(baseFont, 10, Font.BOLD);
        Font normalFont = new Font(baseFont, 10);
        Font smallBoldFont = new Font(baseFont, 9, Font.BOLD);
        Font smallFont = new Font(baseFont, 9);

        // ---------- HEADER ----------
        if (schoolDetails != null) {
            PdfPTable headerTable = new PdfPTable(imageBytes != null ? 2 : 1);
            headerTable.setWidthPercentage(100);
            if (imageBytes != null) headerTable.setWidths(new float[]{1.2f, 4.8f});

            if (imageBytes != null) {
                try {
                    Image logo = Image.getInstance(imageBytes);
                    logo.scaleToFit(70, 70);
                    PdfPCell logoCell = new PdfPCell(logo);
                    logoCell.setBorder(PdfPCell.NO_BORDER);
                    logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerTable.addCell(logoCell);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            PdfPCell schoolInfo = new PdfPCell();
            schoolInfo.setBorder(PdfPCell.NO_BORDER);

            Paragraph schoolName = new Paragraph(safe(schoolDetails.getSchoolName()), largeHeaderFont);
            schoolName.setAlignment(Element.ALIGN_CENTER);

            Paragraph address = new Paragraph(safe(schoolDetails.getSchoolAddress()), boldFont);
            address.setAlignment(Element.ALIGN_CENTER);

            String contactLine = "Ph: " + safe(schoolDetails.getPhoneNumber()) +
                    "   Email: " + safe(schoolDetails.getEmailAddress()) +
                    "   Website: ";
            Paragraph contact = new Paragraph(contactLine, boldFont);
            contact.setAlignment(Element.ALIGN_CENTER);

            schoolInfo.addElement(schoolName);
            schoolInfo.addElement(address);
            schoolInfo.addElement(contact);
            headerTable.addCell(schoolInfo);
            document.add(headerTable);
        }

        addGap(document, 4f);

        // ---------- TITLE ----------
        Paragraph titlePara = new Paragraph("स्थानांतरण प्रमाणपत्र / TRANSFER CERTIFICATE", titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);

        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(75);
        titleTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell pillCell = new PdfPCell(titlePara);
        pillCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pillCell.setPadding(6f);
        pillCell.setBorder(PdfPCell.NO_BORDER);
        pillCell.setCellEvent(new RoundedCell());
        pillCell.setBackgroundColor(new BaseColor(240, 240, 240));
        titleTable.addCell(pillCell);
        document.add(titleTable);

        addGap(document, 4f);

        // ---------- BASIC INFO ----------
        PdfPTable infoBlock1 = new PdfPTable(3);
        infoBlock1.setWidthPercentage(100);
        infoBlock1.setWidths(new float[]{3.5f, 3.5f, 3f});
        infoBlock1.addCell(createNoBorderCell("विद्यालय कोड / School Code: " + safe(schoolDetails.getSchoolCode()), smallBoldFont));
        infoBlock1.addCell(createNoBorderCell("पेन नंबर / Pen No: " + safe(tcDetails.getPenNo()), smallBoldFont));
        infoBlock1.addCell(createNoBorderCell("क्रम संख्या / S.R. No:", smallBoldFont));
        document.add(infoBlock1);

        PdfPTable infoBlock2 = new PdfPTable(3);
        infoBlock2.setWidthPercentage(100);
        infoBlock2.setWidths(new float[]{3.5f, 3.5f, 3f});
        infoBlock2.addCell(createNoBorderCell("प्रवेश संख्या / Admission No: " + safe(tcDetails.getAdmissionNo()), smallBoldFont));
        infoBlock2.addCell(createNoBorderCell("आपार आईडी / Aapar Id: " + safe(tcDetails.getApaarId()), smallBoldFont));
        infoBlock2.addCell(createNoBorderCell("", smallBoldFont));
        document.add(infoBlock2);

        PdfPTable infoBlock3 = new PdfPTable(1);
        infoBlock3.setWidthPercentage(100);
        infoBlock3.addCell(createNoBorderCell("विद्यालय की स्थिति / Status of School: " + safe(tcDetails.getSchoolCategory()), smallBoldFont));
        document.add(infoBlock3);

        addGap(document, 6f);

        // ---------- STUDENT DETAILS ----------
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{6.2f, 4.8f});

        addRow(table, "1. विद्यार्थी का नाम / Name of the pupil", tcDetails.getStudentName(), normalFont, normalFont);
        addRow(table, "2. माता का नाम / Mother's Name", tcDetails.getMotherName(), normalFont, normalFont);
        addRow(table, "3. पिता का नाम / Father's Name", tcDetails.getFatherName(), normalFont, normalFont);
        addRow(table, "4. राष्ट्रीयता / Nationality", "India", normalFont, normalFont);

        addMultiLineRow(table, "5. क्या अनु० जाति/ज० जाति/पिछड़ा वर्ग या से सम्बंधित है",
                "  Whether the pupil belongs to SC/ST/OBC Category", safe(tcDetails.getCategory()), normalFont, normalFont);
        addMultiLineRow(table, "6. प्रवेश पुस्तिका के अनुसार जन्म तिथि (अंकों में)",
                "  Date of Birth according to Admission Register (in figures)", safe(tcDetails.getDob()), normalFont, normalFont);
        addMultiLineRow(table, "7. जन्म तिथि (शब्दों में)",
                "  Date of Birth (in words)", safe(tcDetails.getDobInWords()), normalFont, normalFont);
        addMultiLineRow(table, "8. क्या विद्यार्थी का परीक्षा अनुत्तीर्ण है",
                "  Whether the student is failed", safe(tcDetails.getIsStudentFailed()), normalFont, normalFont);
        addMultiLineRow(table, "9. प्रस्तावित विषय",
                "  Subjects Offered", safe(tcDetails.getSubjectsOffered()), normalFont, normalFont);
        addMultiLineRow(table, "10. पिछली कक्षा जिसमें विद्यार्थी अध्ययनरत था (शब्दों में)",
                "    Class in which the pupil last studied(in words)", safe(tcDetails.getLastStudiedClass()), normalFont, normalFont);
        addMultiLineRow(table, "11. पिछले विद्यालय/बोर्ड परीक्षा का परिणाम",
                "    School/Board Annual examination last taken with result", safe(tcDetails.getLastAnnualExamResult()), normalFont, normalFont);
        addMultiLineRow(table, "12. क्या उच्च कक्षा में पदोन्नत का अधिकारी है",
                "    Whether qualified for promotion to the next higher class", safe(tcDetails.getIsPromotedToNextClass()), normalFont, normalFont);
        addMultiLineRow(table, "13. क्या विद्यालय ने विद्यार्थी की सभी देय राशि का भुगतान कर दिया है",
                "    Whether the pupil has paid all dues to the School", safe(tcDetails.getFeeDueStatus()), normalFont, normalFont);
        addMultiLineRow(table, "14. विद्यालय से विद्यार्थी के नाम काटे जाने की तिथि",
                "    Date on which pupil's name was struck off the rolls\n  of the school", safe(tcDetails.getDateStruckOff()), normalFont, normalFont);
        addMultiLineRow(table, "15. अंतिम तिथि तक बैठकों की कुल संख्या",
                "    No. of meetings upto date", safe(tcDetails.getNoOfMeetings()), normalFont, normalFont);
        addMultiLineRow(table, "16. विद्यालय में उपस्थित दिनों की कुल संख्या",
                "    No. of school days the pupil attended", safe(tcDetails.getTotalPresent()), normalFont, normalFont);
        addRow(table, "17. सामान्य आचरण / General Conduct", safe(tcDetails.getGeneralConduct()), normalFont, normalFont);
        addRow(table, "18. विद्यालय छोड़ने का कारण / Reason for leaving the school", safe(tcDetails.getReasonForLeaving()), normalFont, normalFont);
        addRow(table, "19. कोई अन्य टिप्पणी / Any other remarks", safe(tcDetails.getRemarks()), normalFont, normalFont);
        addRow(table, "20. प्रमाण पत्र जारी करने की तिथि / Date of issue certificate", safe(tcDetails.getDateOfIssue()), normalFont, normalFont);
        addMultiLineRow(table, "21. क्या विद्यालय सरकारी / अल्पसंख्यक / स्वतन्त्र श्रेणी में है",
                "    Whether school is under Govt./Minority/Independent \n  Category", safe(tcDetails.getSchoolCategory()), normalFont, normalFont);

        document.add(table);

        addGap(document, 6f);

        // ---------- SIGNATURE AREA ----------
        PdfPTable signTable = new PdfPTable(3);
        signTable.setWidthPercentage(100);
        signTable.setWidths(new float[]{3f, 3f, 3f});
        signTable.addCell(createSignCell("Prepared by\nName & Designation", boldFont));
        signTable.addCell(createSignCell("Checked by\nName & Designation", boldFont));
        signTable.addCell(createSignCell("Sign of Principal\nwith Original Seal", boldFont));
        document.add(signTable);

        document.close();
        return outputStream.toByteArray();

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
    private void addMultiLineRow(PdfPTable table, String hindiLabel, String englishLabel, String value, Font labelFont, Font valueFont) throws DocumentException {
        Paragraph labelParagraph = new Paragraph();
        labelParagraph.add(new Chunk(hindiLabel + "\n", labelFont));

        // Handle multi-line English label properly
        if (englishLabel.contains("\n")) {
            String[] englishLines = englishLabel.split("\n");
            for (int i = 0; i < englishLines.length; i++) {
                String indent = i == 0 ? "  " : "    "; // 2 spaces for first line, 4 spaces for subsequent lines
                labelParagraph.add(new Chunk(indent + englishLines[i] + (i < englishLines.length - 1 ? "\n" : ""), labelFont));
            }
        } else {
            labelParagraph.add(new Chunk("  " + englishLabel, labelFont));
        }

        labelParagraph.setLeading(12f);

        PdfPCell labelCell = new PdfPCell(labelParagraph);
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPaddingBottom(3f);
        labelCell.setVerticalAlignment(Element.ALIGN_TOP);

        // Create a nested table for the value to control indentation
        PdfPTable valueTable = new PdfPTable(2);
        valueTable.setWidthPercentage(100);
        valueTable.setWidths(new float[]{0.3f, 9.7f});

        // First cell with just colon
        PdfPCell colonCell = new PdfPCell(new Phrase(":", valueFont));
        colonCell.setBorder(PdfPCell.NO_BORDER);
        colonCell.setPaddingBottom(3f);
        colonCell.setVerticalAlignment(Element.ALIGN_TOP);

        // Second cell with the actual value
        PdfPCell actualValueCell = new PdfPCell(new Phrase(safe(value), valueFont));
        actualValueCell.setBorder(PdfPCell.NO_BORDER);
        actualValueCell.setPaddingBottom(3f);
        actualValueCell.setVerticalAlignment(Element.ALIGN_TOP);
        actualValueCell.setPaddingLeft(2f);

        valueTable.addCell(colonCell);
        valueTable.addCell(actualValueCell);

        PdfPCell valueCell = new PdfPCell(valueTable);
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPaddingBottom(3f);
        valueCell.setVerticalAlignment(Element.ALIGN_TOP);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) throws DocumentException {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPaddingBottom(3f);
        labelCell.setVerticalAlignment(Element.ALIGN_TOP);

        // Create a nested table for the value
        PdfPTable valueTable = new PdfPTable(2);
        valueTable.setWidthPercentage(100);
        valueTable.setWidths(new float[]{0.3f, 9.7f});

        PdfPCell colonCell = new PdfPCell(new Phrase(":", valueFont));
        colonCell.setBorder(PdfPCell.NO_BORDER);
        colonCell.setPaddingBottom(3f);
        colonCell.setVerticalAlignment(Element.ALIGN_TOP);

        PdfPCell actualValueCell = new PdfPCell(new Phrase(safe(value), valueFont));
        actualValueCell.setBorder(PdfPCell.NO_BORDER);
        actualValueCell.setPaddingBottom(3f);
        actualValueCell.setVerticalAlignment(Element.ALIGN_TOP);
        actualValueCell.setPaddingLeft(2f);

        valueTable.addCell(colonCell);
        valueTable.addCell(actualValueCell);

        PdfPCell valueCell = new PdfPCell(valueTable);
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPaddingBottom(3f);
        valueCell.setVerticalAlignment(Element.ALIGN_TOP);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private PdfPCell createNoBorderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private void addGap(Document document, float height) throws DocumentException {
        Paragraph gap = new Paragraph(" ");
        gap.setSpacingBefore(height);
        document.add(gap);
    }

    private String safe(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private PdfPCell createSignCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingTop(10f);
        return cell;
    }

    class RoundedCell implements PdfPCellEvent {
        @Override
        public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] canvas) {
            PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
            cb.roundRectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight(), 10);
            cb.stroke();
        }
    }

    class BackgroundImagePageEvent extends PdfPageEventHelper {
        private Image backgroundImage;

        public BackgroundImagePageEvent(byte[] imageBytes) {
            try {
                if (imageBytes != null) {
                    this.backgroundImage = Image.getInstance(imageBytes);
                    this.backgroundImage.scaleToFit(350, 350);
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
                gState.setFillOpacity(0.08f);
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
    public TransferCertificateDetails getTC(Long tcId, String schoolCode) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = "SELECT * FROM transfer_certificates_alumni WHERE tc_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{tcId}, (rs, rowNum) -> {
                TransferCertificateDetails tc = new TransferCertificateDetails();
                tc.setTcId(rs.getLong("tc_id"));
                tc.setSchoolId(rs.getInt("school_id"));
                tc.setSchoolCode(rs.getString("school_code"));
                tc.setSchoolCategory(rs.getString("school_category"));
                tc.setSessionId(rs.getInt("session_id"));
                tc.setAcademicSession(rs.getString("academic_session"));
                tc.setStudentId(rs.getLong("student_id"));
                tc.setAdmissionNo(rs.getString("admission_no"));
                tc.setStudentName(rs.getString("student_name"));
                tc.setDob(rs.getDate("dob"));
                tc.setDobInWords(rs.getString("dob_in_words"));
                tc.setProofOfDob(rs.getString("proof_of_dob"));
                tc.setPenNo(rs.getString("pen_no"));
                tc.setApaarId(rs.getString("apaar_id"));
                tc.setFatherName(rs.getString("father_name"));
                tc.setMotherName(rs.getString("mother_name"));
                tc.setIsStudentFailed(rs.getString("is_student_failed"));
                tc.setSubjectsOffered(rs.getString("subjects_offered"));
                tc.setIsPromotedToNextClass(rs.getString("is_promoted_to_next_class"));
                tc.setNoOfMeetings(rs.getInt("no_of_meetings"));
                tc.setTotalPresent(rs.getInt("total_present"));
                tc.setClassAtLeaving(rs.getString("class_at_leaving"));
                tc.setSectionAtLeaving(rs.getString("section_at_leaving"));
                tc.setLastAnnualExamResult(rs.getString("last_annual_exam_result"));
                tc.setLastStudiedClass(rs.getString("last_studied_class"));
                tc.setCategory(rs.getString("category"));
                tc.setFeeDueStatus(rs.getString("fee_due_status"));
                tc.setExtracurricular(rs.getString("extracurricular"));
                tc.setGamesActivities(rs.getString("games_activities"));
                tc.setGeneralConduct(rs.getString("general_conduct"));
                tc.setReasonForLeaving(rs.getString("reason_for_leaving"));
                tc.setDateOfIssue(rs.getDate("date_of_issue"));
                tc.setDateStruckOff(rs.getDate("date_struck_off"));
                tc.setRemarks(rs.getString("remarks"));
                tc.setIssuedDate(rs.getTimestamp("issued_date"));
                tc.setIssuedBy(rs.getInt("issued_by"));
                tc.setSrNumber(rs.getString("sr_number"));
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

    @Override
    public List<TransferCertificateDetails> getAllTc(int sessionId, String schoolCode) {

        String sql = "SELECT * FROM transfer_certificates_alumni WHERE session_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            return jdbcTemplate.query(sql, new Object[]{sessionId}, (rs, rowNum) -> {
                TransferCertificateDetails tc = new TransferCertificateDetails();
                tc.setTcId(rs.getLong("tc_id"));
                tc.setSchoolId(rs.getInt("school_id"));
                tc.setSchoolCode(rs.getString("school_code"));
                tc.setSchoolCategory(rs.getString("school_category"));
                tc.setSessionId(rs.getInt("session_id"));
                tc.setAcademicSession(rs.getString("academic_session"));
                tc.setStudentId(rs.getLong("student_id"));
                tc.setAdmissionNo(rs.getString("admission_no"));
                tc.setStudentName(rs.getString("student_name"));
                tc.setDob(rs.getDate("dob"));
                tc.setDobInWords(rs.getString("dob_in_words"));
                tc.setProofOfDob(rs.getString("proof_of_dob"));
                tc.setPenNo(rs.getString("pen_no"));
                tc.setApaarId(rs.getString("apaar_id"));
                tc.setFatherName(rs.getString("father_name"));
                tc.setMotherName(rs.getString("mother_name"));
                tc.setIsStudentFailed(rs.getString("is_student_failed"));
                tc.setSubjectsOffered(rs.getString("subjects_offered"));
                tc.setIsPromotedToNextClass(rs.getString("is_promoted_to_next_class"));
                tc.setNoOfMeetings(rs.getInt("no_of_meetings"));
                tc.setTotalPresent(rs.getInt("total_present"));
                tc.setClassAtLeaving(rs.getString("class_at_leaving"));
                tc.setSectionAtLeaving(rs.getString("section_at_leaving"));
                tc.setLastAnnualExamResult(rs.getString("last_annual_exam_result"));
                tc.setLastStudiedClass(rs.getString("last_studied_class"));
                tc.setCategory(rs.getString("category"));
                tc.setFeeDueStatus(rs.getString("fee_due_status"));
                tc.setExtracurricular(rs.getString("extracurricular"));
                tc.setGamesActivities(rs.getString("games_activities"));
                tc.setGeneralConduct(rs.getString("general_conduct"));
                tc.setReasonForLeaving(rs.getString("reason_for_leaving"));
                tc.setDateOfIssue(rs.getDate("date_of_issue"));
                tc.setDateStruckOff(rs.getDate("date_struck_off"));
                tc.setRemarks(rs.getString("remarks"));
                tc.setIssuedDate(rs.getTimestamp("issued_date"));
                tc.setIssuedBy(rs.getInt("issued_by"));
                tc.setSrNumber(rs.getString("sr_number"));
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


  public TransferCertificateDetails updateTC(TransferCertificateDetails tc, Long tcId, String schoolCode) {
      JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

      // SQL ordered EXACTLY as per your table structure (excluding tc_id)
      String sql = "UPDATE transfer_certificates_alumni SET "
              + "school_id=?, school_code=?, school_category=?, session_id=?, academic_session=?, issued_date=?, issued_by=?, "
              + "student_id=?, admission_no=?, student_name=?, dob=?, dob_in_words=?, proof_of_dob=?, pen_no=?, apaar_id=?, "
              + "father_name=?, mother_name=?, is_student_failed=?, subjects_offered=?, is_promoted_to_next_class=?, "
              + "no_of_meetings=?, total_present=?, class_at_leaving=?, section_at_leaving=?, last_annual_exam_result=?, "
              + "last_studied_class=?, category=?, fee_due_status=?, extracurricular=?, games_activities=?, "
              + "general_conduct=?, reason_for_leaving=?, date_of_issue=?, date_struck_off=?, remarks=?, sr_number = ?, tc_data=?::jsonb "
              + "WHERE tc_id=?";
      System.out.println(sql);
      try {
          int rowsUpdated = jdbcTemplate.update(sql,
                  // Column order matches table structure:
                  tc.getSchoolId(),
                  tc.getSchoolCode(),
                  tc.getSchoolCategory(),
                  tc.getSessionId(),
                  tc.getAcademicSession(),
                  tc.getIssuedDate(),
                  tc.getIssuedBy(),
                  tc.getStudentId(),
                  tc.getAdmissionNo(),
                  tc.getStudentName(),
                  tc.getDob(),
                  tc.getDobInWords(),
                  tc.getProofOfDob(),
                  tc.getPenNo(),
                  tc.getApaarId(),
                  tc.getFatherName(),
                  tc.getMotherName(),
                  tc.getIsStudentFailed(),
                  tc.getSubjectsOffered(),
                  tc.getIsPromotedToNextClass(),
                  tc.getNoOfMeetings(),
                  tc.getTotalPresent(),
                  tc.getClassAtLeaving(),
                  tc.getSectionAtLeaving(),
                  tc.getLastAnnualExamResult(),
                  tc.getLastStudiedClass(),
                  tc.getCategory(),
                  tc.getFeeDueStatus(),
                  tc.getExtracurricular(),
                  tc.getGamesActivities(),
                  tc.getGeneralConduct(),
                  tc.getReasonForLeaving(),
                  tc.getDateOfIssue(),
                  tc.getDateStruckOff(),
                  tc.getRemarks(),
                  tc.getSrNumber(),
                  tc.getTcData(),
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

    @Override
    public TransferCertificateDetails getTcByAdmissionNo(String admissionNo, String schoolCode) {
        String sql = "SELECT tc_id, school_id, school_code, school_category, session_id, academic_session, student_id, " +
                "admission_no, student_name, dob, dob_in_words, proof_of_dob, pen_no, apaar_id, father_name, mother_name, " +
                "is_student_failed, subjects_offered, is_promoted_to_next_class, no_of_meetings, total_present, class_at_leaving, " +
                "section_at_leaving, last_annual_exam_result, last_studied_class, category, fee_due_status, extracurricular, " +
                "games_activities, general_conduct, reason_for_leaving, date_of_issue, date_struck_off, remarks, issued_date, issued_by, " +
                "sr_number, tc_data FROM transfer_certificates_alumni WHERE admission_no = ?";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            return jdbcTemplate.queryForObject(sql, new RowMapper<TransferCertificateDetails>() {
                @Override
                public TransferCertificateDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TransferCertificateDetails tc = new TransferCertificateDetails();
                    tc.setTcId(rs.getLong("tc_id"));
                    tc.setSchoolId(rs.getInt("school_id"));
                    tc.setSchoolCode(rs.getString("school_code"));
                    tc.setSchoolCategory(rs.getString("school_category"));
                    tc.setSessionId(rs.getInt("session_id"));
                    tc.setAcademicSession(rs.getString("academic_session"));
                    tc.setStudentId(rs.getLong("student_id"));
                    tc.setAdmissionNo(rs.getString("admission_no"));
                    tc.setStudentName(rs.getString("student_name"));
                    tc.setDob(rs.getDate("dob"));
                    tc.setDobInWords(rs.getString("dob_in_words"));
                    tc.setProofOfDob(rs.getString("proof_of_dob"));
                    tc.setPenNo(rs.getString("pen_no"));
                    tc.setApaarId(rs.getString("apaar_id"));
                    tc.setFatherName(rs.getString("father_name"));
                    tc.setMotherName(rs.getString("mother_name"));
                    tc.setIsStudentFailed(rs.getString("is_student_failed"));
                    tc.setSubjectsOffered(rs.getString("subjects_offered"));
                    tc.setIsPromotedToNextClass(rs.getString("is_promoted_to_next_class"));
                    tc.setNoOfMeetings(rs.getInt("no_of_meetings"));
                    tc.setTotalPresent(rs.getInt("total_present"));
                    tc.setClassAtLeaving(rs.getString("class_at_leaving"));
                    tc.setSectionAtLeaving(rs.getString("section_at_leaving"));
                    tc.setLastAnnualExamResult(rs.getString("last_annual_exam_result"));
                    tc.setLastStudiedClass(rs.getString("last_studied_class"));
                    tc.setCategory(rs.getString("category"));
                    tc.setFeeDueStatus(rs.getString("fee_due_status"));
                    tc.setExtracurricular(rs.getString("extracurricular"));
                    tc.setGamesActivities(rs.getString("games_activities"));
                    tc.setGeneralConduct(rs.getString("general_conduct"));
                    tc.setReasonForLeaving(rs.getString("reason_for_leaving"));
                    tc.setDateOfIssue(rs.getDate("date_of_issue"));
                    tc.setDateStruckOff(rs.getDate("date_struck_off"));
                    tc.setRemarks(rs.getString("remarks"));
                    tc.setIssuedDate(rs.getTimestamp("issued_date"));
                    tc.setIssuedBy(rs.getInt("issued_by"));
                    tc.setSrNumber(rs.getString("sr_number"));
                    tc.setTcData(rs.getString("tc_data"));
                    return tc;
                }
            }, admissionNo);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

}
