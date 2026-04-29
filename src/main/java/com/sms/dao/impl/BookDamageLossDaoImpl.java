package com.sms.dao.impl;

import com.sms.dao.BookDamageLossDao;
import com.sms.model.BookDamageLossReport;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class BookDamageLossDaoImpl implements BookDamageLossDao {

    @Override
    public BookDamageLossReport addBookDamageLossReport(BookDamageLossReport report, String schoolCode) throws Exception {

        String sql = "INSERT INTO book_report " +
                "(school_id, session_id, member_id, book_id, report_type, fine_amount, report_date, reason, updated_by, update_date_time) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
//                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"report_id"});

                ps.setInt(1, report.getSchoolId());
                ps.setInt(2, report.getSessionId());
                ps.setInt(3, report.getMemberId());
                ps.setInt(4, report.getBookId());
                ps.setString(5, report.getReportType());

                ps.setObject(6, report.getFineAmount());

                ps.setDate(7, report.getReportDate() != null
                        ? new java.sql.Date(report.getReportDate().getTime())
                        : null);

                ps.setString(8, report.getReason());

                ps.setObject(9, report.getUpdatedBy());
                ps.setTimestamp(10, report.getUpdateDateTime());

                return ps;
            }, keyHolder);
            // to get the report_id after adding a report
            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                report.setReportId(generatedId.intValue());
            }



            return report;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<BookDamageLossReport> getAllReports(String schoolCode) throws Exception {

        String sql = "SELECT br.report_id, br.school_id, br.session_id, br.member_id, br.book_id, " +
                "br.report_type, br.fine_amount, br.report_date, br.reason, " +
                "br.updated_by, br.update_date_time, br.deleted " +
                "FROM book_report br " +
                "WHERE br.deleted IS NOT TRUE " +
                "ORDER BY br.report_id DESC";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BookDamageLossReport r = new BookDamageLossReport();

            r.setReportId(rs.getInt("report_id"));
            r.setSchoolId(rs.getInt("school_id"));
            r.setSessionId(rs.getInt("session_id"));
            r.setMemberId(rs.getInt("member_id"));
            r.setBookId(rs.getInt("book_id"));
            r.setReportType(rs.getString("report_type"));
            r.setFineAmount(rs.getDouble("fine_amount"));
            r.setReportDate(rs.getDate("report_date"));
            r.setReason(rs.getString("reason"));
            r.setUpdatedBy(rs.getInt("updated_by"));
            r.setUpdateDateTime(rs.getTimestamp("update_date_time"));
            r.setDeleted(rs.getBoolean("deleted"));

            return r;
        });
    }
}