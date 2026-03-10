--/*DDL for insert_student_details*/
--CREATE OR REPLACE FUNCTION medhapro.insert_student_details(schema_name TEXT)
--RETURNS TEXT AS $$
--DECLARE
--   ddl_statement TEXT;
--   parent_insert_statement TEXT ;
--studentp_insert_statement TEXT ;
--studenta_insert_statement TEXT ;
--BEGIN
--
--   parent_insert_statement := '
--       INSERT INTO ' || schema_name || '.parent_details (
--           uuid, school_id, first_name, last_name, dob, phone_number, emergency_phone_number,
--           whatsapp_no, email_address, gender, parent_type, qualification, aadhar_number,
--           company_name, designation, company_address, company_phone, address, city, state,
--           zipcode, updated_by, updated_date, create_date, deleted
--       )
--       SELECT
--           se.uu_id,
--           1 AS school_id,
--           se.student_mother_name AS first_name,
--           NULL AS last_name,
--           se.dob AS dob,
--
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_mobile
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
--               ELSE se.guardian_mobile
--           END AS phone_number,
--
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.alternate_number
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
--               ELSE se.guardian_mobile
--           END AS emergency_phone_number,
--
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.whatsapp
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
--               ELSE se.guardian_mobile
--           END AS whatsapp_no,
--
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_email
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_email
--               ELSE se.guardian_email
--           END AS email_address,
--           se.gender AS gender,
--           ''Mother'' AS parent_type,
--
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_occupation
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_occupation
--               ELSE se.guardian_occupation
--           END AS qualification,
--
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_aadhar_no
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_aadhar_no
--               ELSE se.guardian_aadhar_no
--           END AS aadhar_number,
--           NULL AS company_name,
--           NULL AS designation,
--           NULL AS company_address,
--           NULL AS company_phone,
--
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_residential_address
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_residential_address
--               ELSE se.guardian_residential_address
--           END AS address,
--           se.city AS city,
--           se.state AS state,
--           CAST(se.pincode AS INT) AS zipcode,
--           1 AS updated_by,
--           CURRENT_TIMESTAMP AS updated_date,
--           CURRENT_TIMESTAMP AS create_date,
--           FALSE AS deleted
--       FROM
--           ' || schema_name || ' .student_excel se
--       WHERE
--           se.student_mother_name IS NOT NULL AND se.student_mother_name != ''''
--       UNION
--       SELECT
--           se.uu_id,
--           1 AS school_id,
--           se.student_father_name AS first_name,
--           se.student_last_name AS last_name,
--           se.dob AS dob,
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_mobile
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
--               ELSE se.guardian_mobile
--           END AS phone_number,
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.alternate_number
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
--               ELSE se.guardian_mobile
--           END AS emergency_phone_number,
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.whatsapp
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
--               ELSE se.mother_mobile
--           END AS whatsapp_no,
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_email
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_email
--               ELSE se.guardian_email
--           END AS email_address,
--           se.gender AS gender,
--           ''Father'' AS parent_type,
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_occupation
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_occupation
--               ELSE se.guardian_occupation
--           END AS qualification,
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_aadhar_no
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_aadhar_no
--               ELSE se.guardian_aadhar_no
--           END AS aadhar_number,
--           NULL AS company_name,
--           NULL AS designation,
--           NULL AS company_address,
--           NULL AS company_phone,
--           CASE
--               WHEN se.student_father_name IS NOT NULL THEN se.father_residential_address
--               WHEN se.student_mother_name IS NOT NULL THEN se.mother_residential_address
--               ELSE se.guardian_residential_address
--           END AS address,
--           se.city AS city,
--           se.state AS state,
--           CAST(se.pincode AS INT) AS zipcode,
--           1 AS updated_by,
--           CURRENT_TIMESTAMP AS updated_date,
--           CURRENT_TIMESTAMP AS create_date,
--           FALSE AS deleted
--       FROM
--           ' || schema_name || ' .student_excel se
--       WHERE
--           se.student_father_name IS NOT NULL AND se.student_father_name != ''''
--UNION
--SELECT
--           se.uu_id ,
--           1 AS school_id,
--           se.student_father_name  AS first_name,
--           se.student_last_name AS last_name,
--           se.dob AS dob,
--       CASE
--          WHEN se.student_father_name IS NOT NULL THEN se.father_mobile
--          WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
--          ELSE se.guardian_mobile
--       END AS phone_number,
--       CASE
--          WHEN se.student_father_name IS NOT NULL THEN se.alternate_number
--          WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
--          ELSE se.guardian_mobile
--       END AS emergency_phone_number,
--       CASE
--          WHEN se.student_father_name IS NOT NULL THEN se.whatsapp
--          WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
--          ELSE se.guardian_mobile
--       END AS whatsapp_no,
--       CASE
--          WHEN se.student_father_name IS NOT NULL THEN se.father_email
--          WHEN se.student_mother_name IS NOT NULL THEN se.mother_email
--          ELSE se.guardian_email
--       END AS email_address,
--       se.gender AS gender,
--       ''Gaurdian'' as  parent_type,
--       CASE
--          WHEN se.student_father_name IS NOT NULL THEN se.father_occupation
--          WHEN se.student_mother_name IS NOT NULL THEN se.mother_occupation
--          ELSE se.guardian_occupation
--          END AS qualification,
--       CASE
--          WHEN se.student_father_name IS NOT NULL THEN se.father_aadhar_no
--          WHEN se.student_mother_name IS NOT NULL THEN se.mother_aadhar_no
--          ELSE se.guardian_aadhar_no
--          END AS aadhar_number,
--          NULL AS company_name,
--          NULL AS designation,
--          NULL AS company_address,
--          NULL AS company_phone,
--       CASE
--          WHEN se.student_father_name IS NOT NULL THEN se.father_residential_address
--          WHEN se.student_mother_name IS NOT NULL THEN se.mother_residential_address
--          ELSE se.guardian_residential_address
--       END AS address,
--       se.city AS city,
--       se.state AS state,
--       CAST(se.pincode AS INT) AS zipcode,
--       1 AS updated_by,
--       CURRENT_TIMESTAMP AS updated_date,
--       CURRENT_TIMESTAMP AS create_date,
--       FALSE AS deleted
--   FROM
--       ' || schema_name || ' .student_excel se
--   WHERE
--     se.guardian_name IS NOT NULL AND se.guardian_name != ''''
--   ';
--   EXECUTE parent_insert_statement;
--
--   studentp_insert_statement := '
--       INSERT INTO ' || schema_name || '.student_personal_details (
--           student_id,school_id,uu_id,parent_id,father_name,father_occupation,mother_name,
--           mother_occupation,first_name,last_name,blood_group,gender,height,weight,aadhar_number,phone_number,emergency_phone_number,
--           whatsapp_no,email_address,dob,dob_cirtificate_no,income_app_no,caste_app_no,domicile_app_no,govt_student_id_on_portal,
--           govt_family_id_on_portal,bank_name,branch_name,ifsc_code,account_no,pan_no,religion,nationality,category,caste,current_address,
--           current_city,current_state,current_zipcode,permanent_address,permanent_city,permanent_state,permanent_zipcode,student_country,
--           current_status,current_status_comment,updated_by,updated_date,create_date,validity_start_date,validity_end_date
--       )
--       SELECT
--   ROW_NUMBER() OVER () +
--   (SELECT COALESCE(MAX(current_value), 0)
--    FROM ' || schema_name || '.master_sequence_controller
--    WHERE school_id = 1) AS student_id,
--   1 AS school_id,
--   se.uu_id,
--   ARRAY_AGG(pd.parent_id) AS parent_id,
--   se.student_father_name AS father_name,
--   se.father_occupation,
--   se.student_mother_name AS mother_name,
--   se.mother_occupation,
--   se.student_first_name AS first_name,
--   se.student_last_name AS last_name,
--   se.blood_group,
--   se.gender,
--   se.height,
--   se.weight,
--   se.aadhar_no AS aadhar_number,
--   se.mobile_number AS phone_number,
--    se.mobile_number AS emergency_phone_number,
--   se.whatsapp as whatsapp_no ,
--   se.email  AS email_address,
--   se.dob,
--   se.dob_application_no as dob_cirtificate_no ,
--   se.income_application_no as income_app_no,
--   se.caste_application_no as caste_app_no ,
--   se.domicile_application_no as domicile_app_no ,
--   se.govt_student_id as govt_student_id_on_portal,
--   se.govt_family_id  as govt_family_id_on_portal,
--   se.bank_name,
--   se.branch_name,
--   se.bank_ifsc as ifsc_code,
--   se.bank_account_no as account_no,
--   se.pan_no,
--   se.religion,
--   se.nationality,
--   se.category,
--   se.caste,
--   se.address AS current_address,
--   se.city AS current_city,
--   se.state AS current_state,
--   CAST(se.pincode AS INT) AS current_zipcode,
--   se.address AS permanent_address,
--   se.city AS permanent_city,
--   se.state AS permanent_state,
--   CAST(se.pincode AS INT) AS permanent_zipcode,
--   se.country AS student_country,
--   ''active'' AS current_status,
--   '''' AS current_status_comment,
--   1 AS updated_by,
--   CURRENT_TIMESTAMP AS updated_date,
--   CURRENT_TIMESTAMP AS create_date,
--   CURRENT_TIMESTAMP AS validity_start_date,
--   ''9999-12-31 00:00:00.000''::timestamp AS validity_end_date
--FROM
--   ' || schema_name || ' .student_excel se
--INNER JOIN
--   ' || schema_name || ' .parent_details pd
--on
--   se.uu_id = pd.uuid
--   where pd.first_name is not null
--   group by  2 ,3 ,5 ,6 ,7 ,8 ,9 ,10 ,11 ,12 ,13 ,14 ,15 ,16 ,17 ,18 ,19 ,20 ,21 ,22 ,23 ,24 ,25 ,26 ,27 ,28 ,29 ,30 ,31 ,32 ,33 ,34 ,35 ,36 ,37 ,38 ,39 ,40 ,41 ,42 ,43 ,44 ,45 ,46 ,47 ,48 ,49
--   ';
--   EXECUTE studentp_insert_statement;
--studenta_insert_statement := '
--       INSERT INTO ' || schema_name || '.student_academic_details (
--           student_id, school_id, uu_id, pen_no, admission_no, admission_date, registration_number, roll_number, session_id, student_class_id,
--           student_section_id, stream, education_medium, referred_by, is_rte_student, rte_application_no, enrolled_session, enrolled_class,
--           enrolled_year, transfer_cirti_no, date_of_issue, scholarship_id, scholarship_password, lst_school_name,   lst_school_addrs,
--           lst_attended_class, lst_scl_aff_to, lst_session, is_dropout,  dropout_date, dropout_reason, student_type, student_addmission_type, session_status,
--           session_status_comment, updated_by, updated_date, create_date, validity_start_date, validity_end_date )
--       SELECT
--   spd.student_id,
--   1 AS school_id,
--   se.uu_id,
--   se.pen_no,
--   se.admission_no,
--   se.admission_date,
--   se.registration_no,
--   se.roll_no,
--   s.session_id,
--   mc.class_id AS student_class_id,
--   ms.section_id AS student_section_id,
--   se.stream,
--   se.medium AS education_medium,
--   se.reference AS referred_by,
--   se.isRteStudent AS is_rte_student,
--   se.rte_application_no,
--   se.enrolled_session,
--   se.enrolled_class,
--   se.enrolled_year,
--   se.tc_no AS transfer_cirti_no,
--   TO_TIMESTAMP(''1899-12-30'', ''YYYY-MM-DD'') + (se.tc_date::int - 1) * INTERVAL ''1 day'' AS date_of_issue,
--   se.scholarship_id,
--   se.scholarship_password,
--   se.previous_school_name AS lst_school_name,
--   NULL AS lst_school_addrs,
--   se.attended_class AS lst_attended_class,
--   se.school_affiliated AS lst_scl_aff_to,
--   se.last_session AS lst_session,
--   se.dropout AS is_dropout,
--   se.dropout_date,
--   se.dropout_reason,
--   se.student_type,
--   se.type AS student_addmission_type,
--   ''Active'' AS session_status,
--   ''Initial insert'' AS session_status_comment,
--   1 AS updated_by,
--   CURRENT_TIMESTAMP AS updated_date,
--   CURRENT_TIMESTAMP AS create_date,
--   CURRENT_TIMESTAMP AS validity_start_date,
--   ''9999-12-31 00:00:00.000''::timestamp AS validity_end_date
--FROM
--   ' || schema_name || ' .student_excel se
--LEFT JOIN
--   ' || schema_name || ' .mst_class mc ON mc.class_name = se.student_class
--LEFT JOIN
--   ' || schema_name || ' .mst_section ms ON ms.section_name = se.student_section
--LEFT JOIN
--   ' || schema_name || ' .session s ON s.academic_session = se.session
--INNER JOIN
--   ' || schema_name || ' .student_personal_details spd ON spd.uu_id = se.uu_id
--   ';
----    EXECUTE ddl_statement;
--
----    RETURN 'Insert into parent and student details completed successfully';
--EXECUTE studenta_insert_statement ;
--RETURN 'Insert into parent and student details completed successfully';
--END;
--$$ LANGUAGE plpgsql;
--
--SELECT medhapro.insert_student_details('medhapro');

/*DDL for insert_student_details*/
CREATE OR REPLACE FUNCTION medhapro.insert_student_details(schema_name TEXT)
RETURNS TEXT AS $$
DECLARE
   ddl_statement TEXT;
   parent_insert_statement TEXT ;
studentp_insert_statement TEXT ;
studenta_insert_statement TEXT ;
new_seq_value INT;
BEGIN

   parent_insert_statement := '
       INSERT INTO ' || schema_name || '.parent_details (
           uuid, school_id, first_name, last_name, dob, phone_number, emergency_phone_number,
           whatsapp_no, email_address, gender, parent_type, qualification, aadhar_number,
           company_name, designation, company_address, company_phone, address, city, state,
           zipcode, updated_by, updated_date, create_date, deleted
       )
       SELECT
           se.uu_id,
           1 AS school_id,
           se.student_mother_name AS first_name,
           NULL AS last_name,
           se.dob AS dob,

           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_mobile
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
               ELSE se.guardian_mobile
           END AS phone_number,

           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.alternate_number
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
               ELSE se.guardian_mobile
           END AS emergency_phone_number,

           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.whatsapp
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
               ELSE se.guardian_mobile
           END AS whatsapp_no,

           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_email
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_email
               ELSE se.guardian_email
           END AS email_address,
           se.gender AS gender,
           ''Mother'' AS parent_type,

           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_occupation
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_occupation
               ELSE se.guardian_occupation
           END AS qualification,

           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_aadhar_no
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_aadhar_no
               ELSE se.guardian_aadhar_no
           END AS aadhar_number,
           NULL AS company_name,
           NULL AS designation,
           NULL AS company_address,
           NULL AS company_phone,

           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_residential_address
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_residential_address
               ELSE se.guardian_residential_address
           END AS address,
           se.city AS city,
           se.state AS state,
           CAST(se.pincode AS INT) AS zipcode,
           1 AS updated_by,
           CURRENT_TIMESTAMP AS updated_date,
           CURRENT_TIMESTAMP AS create_date,
           FALSE AS deleted
       FROM
           ' || schema_name || ' .student_excel se
       WHERE
           se.student_mother_name IS NOT NULL AND se.student_mother_name != ''''
       UNION
       SELECT
           se.uu_id,
           1 AS school_id,
           se.student_father_name AS first_name,
           se.student_last_name AS last_name,
           se.dob AS dob,
           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_mobile
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
               ELSE se.guardian_mobile
           END AS phone_number,
           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.alternate_number
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
               ELSE se.guardian_mobile
           END AS emergency_phone_number,
           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.whatsapp
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
               ELSE se.mother_mobile
           END AS whatsapp_no,
           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_email
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_email
               ELSE se.guardian_email
           END AS email_address,
           se.gender AS gender,
           ''Father'' AS parent_type,
           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_occupation
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_occupation
               ELSE se.guardian_occupation
           END AS qualification,
           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_aadhar_no
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_aadhar_no
               ELSE se.guardian_aadhar_no
           END AS aadhar_number,
           NULL AS company_name,
           NULL AS designation,
           NULL AS company_address,
           NULL AS company_phone,
           CASE
               WHEN se.student_father_name IS NOT NULL THEN se.father_residential_address
               WHEN se.student_mother_name IS NOT NULL THEN se.mother_residential_address
               ELSE se.guardian_residential_address
           END AS address,
           se.city AS city,
           se.state AS state,
           CAST(se.pincode AS INT) AS zipcode,
           1 AS updated_by,
           CURRENT_TIMESTAMP AS updated_date,
           CURRENT_TIMESTAMP AS create_date,
           FALSE AS deleted
       FROM
           ' || schema_name || ' .student_excel se
       WHERE
           se.student_father_name IS NOT NULL AND se.student_father_name != ''''
UNION
SELECT
           se.uu_id ,
           1 AS school_id,
           se.student_father_name  AS first_name,
           se.student_last_name AS last_name,
           se.dob AS dob,
       CASE
          WHEN se.student_father_name IS NOT NULL THEN se.father_mobile
          WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
          ELSE se.guardian_mobile
       END AS phone_number,
       CASE
          WHEN se.student_father_name IS NOT NULL THEN se.alternate_number
          WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
          ELSE se.guardian_mobile
       END AS emergency_phone_number,
       CASE
          WHEN se.student_father_name IS NOT NULL THEN se.whatsapp
          WHEN se.student_mother_name IS NOT NULL THEN se.mother_mobile
          ELSE se.guardian_mobile
       END AS whatsapp_no,
       CASE
          WHEN se.student_father_name IS NOT NULL THEN se.father_email
          WHEN se.student_mother_name IS NOT NULL THEN se.mother_email
          ELSE se.guardian_email
       END AS email_address,
       se.gender AS gender,
       ''Gaurdian'' as  parent_type,
       CASE
          WHEN se.student_father_name IS NOT NULL THEN se.father_occupation
          WHEN se.student_mother_name IS NOT NULL THEN se.mother_occupation
          ELSE se.guardian_occupation
          END AS qualification,
       CASE
          WHEN se.student_father_name IS NOT NULL THEN se.father_aadhar_no
          WHEN se.student_mother_name IS NOT NULL THEN se.mother_aadhar_no
          ELSE se.guardian_aadhar_no
          END AS aadhar_number,
          NULL AS company_name,
          NULL AS designation,
          NULL AS company_address,
          NULL AS company_phone,
       CASE
          WHEN se.student_father_name IS NOT NULL THEN se.father_residential_address
          WHEN se.student_mother_name IS NOT NULL THEN se.mother_residential_address
          ELSE se.guardian_residential_address
       END AS address,
       se.city AS city,
       se.state AS state,
       CAST(se.pincode AS INT) AS zipcode,
       1 AS updated_by,
       CURRENT_TIMESTAMP AS updated_date,
       CURRENT_TIMESTAMP AS create_date,
       FALSE AS deleted
   FROM
       ' || schema_name || ' .student_excel se
   WHERE
     se.guardian_name IS NOT NULL AND se.guardian_name != ''''
   ';
   EXECUTE parent_insert_statement;

   studentp_insert_statement := '
       INSERT INTO ' || schema_name || '.student_personal_details (
           student_id,school_id,uu_id,parent_id,father_name,father_occupation,mother_name,
           mother_occupation,first_name,last_name,blood_group,gender,height,weight,aadhar_number,phone_number,emergency_phone_number,
           whatsapp_no,email_address,dob,dob_cirtificate_no,income_app_no,caste_app_no,domicile_app_no,govt_student_id_on_portal,
           govt_family_id_on_portal,bank_name,branch_name,ifsc_code,account_no,pan_no,religion,nationality,category,caste,current_address,
           current_city,current_state,current_zipcode,permanent_address,permanent_city,permanent_state,permanent_zipcode,student_country,
           current_status,current_status_comment,updated_by,updated_date,create_date,validity_start_date,validity_end_date
       )
       SELECT
   ROW_NUMBER() OVER () +
   (SELECT COALESCE(MAX(current_value), 0)
    FROM ' || schema_name || '.master_sequence_controller
    WHERE school_id = 1) AS student_id,
   1 AS school_id,
   se.uu_id,
   ARRAY_AGG(pd.parent_id) AS parent_id,
   se.student_father_name AS father_name,
   se.father_occupation,
   se.student_mother_name AS mother_name,
   se.mother_occupation,
   se.student_first_name AS first_name,
   se.student_last_name AS last_name,
   se.blood_group,
   se.gender,
   se.height,
   se.weight,
   se.aadhar_no AS aadhar_number,
   se.mobile_number AS phone_number,
    se.mobile_number AS emergency_phone_number,
   se.whatsapp as whatsapp_no ,
   se.email  AS email_address,
   se.dob,
   se.dob_application_no as dob_cirtificate_no ,
   se.income_application_no as income_app_no,
   se.caste_application_no as caste_app_no ,
   se.domicile_application_no as domicile_app_no ,
   se.govt_student_id as govt_student_id_on_portal,
   se.govt_family_id  as govt_family_id_on_portal,
   se.bank_name,
   se.branch_name,
   se.bank_ifsc as ifsc_code,
   se.bank_account_no as account_no,
   se.pan_no,
   se.religion,
   se.nationality,
   se.category,
   se.caste,
   se.address AS current_address,
   se.city AS current_city,
   se.state AS current_state,
   CAST(se.pincode AS INT) AS current_zipcode,
   se.address AS permanent_address,
   se.city AS permanent_city,
   se.state AS permanent_state,
   CAST(se.pincode AS INT) AS permanent_zipcode,
   se.country AS student_country,
   ''active'' AS current_status,
   '''' AS current_status_comment,
   1 AS updated_by,
   CURRENT_TIMESTAMP AS updated_date,
   CURRENT_TIMESTAMP AS create_date,
   CURRENT_TIMESTAMP AS validity_start_date,
   ''9999-12-31 00:00:00.000''::timestamp AS validity_end_date
FROM
   ' || schema_name || ' .student_excel se
INNER JOIN
   ' || schema_name || ' .parent_details pd
on
   se.uu_id = pd.uuid
   where pd.first_name is not null
   group by  2 ,3 ,5 ,6 ,7 ,8 ,9 ,10 ,11 ,12 ,13 ,14 ,15 ,16 ,17 ,18 ,19 ,20 ,21 ,22 ,23 ,24 ,25 ,26 ,27 ,28 ,29 ,30 ,31 ,32 ,33 ,34 ,35 ,36 ,37 ,38 ,39 ,40 ,41 ,42 ,43 ,44 ,45 ,46 ,47 ,48 ,49
   ';
   EXECUTE studentp_insert_statement;

 -- Update master_sequence_controller with the latest student_id in both columns
 EXECUTE 'UPDATE ' || schema_name || '.master_sequence_controller
          SET current_value = new_max,
              seq_code = new_max
          FROM (SELECT COALESCE(MAX(student_id), 0) as new_max
                FROM ' || schema_name || '.student_personal_details
                WHERE school_id = 1) as max_val
          WHERE school_id = 1 AND seq_code = 0';

studenta_insert_statement := '
       INSERT INTO ' || schema_name || '.student_academic_details (
           student_id, school_id, uu_id, apaar_id, pen_no, admission_no, admission_date, registration_number, roll_number, session_id, student_class_id,
           student_section_id, stream, education_medium, referred_by, is_rte_student, rte_application_no, enrolled_session, enrolled_class,
           enrolled_year, transfer_cirti_no, date_of_issue, scholarship_id, scholarship_password, lst_school_name,   lst_school_addrs,
           lst_attended_class, lst_scl_aff_to, lst_session, is_dropout,  dropout_date, dropout_reason, student_type, student_addmission_type, session_status,
           session_status_comment, updated_by, updated_date, create_date, validity_start_date, validity_end_date )
       SELECT
   spd.student_id,
   1 AS school_id,
   se.uu_id,
   se.apaar_id,
   se.pen_no,
   se.admission_no,
   se.admission_date,
   se.registration_no,
   se.roll_no,
   s.session_id,
   mc.class_id AS student_class_id,
   ms.section_id AS student_section_id,
   se.stream,
   se.medium AS education_medium,
   se.reference AS referred_by,
   se.isRteStudent AS is_rte_student,
   se.rte_application_no,
   se.enrolled_session,
   se.enrolled_class,
   se.enrolled_year,
   se.tc_no AS transfer_cirti_no,
   TO_TIMESTAMP(''1899-12-30'', ''YYYY-MM-DD'') + (se.tc_date::int - 1) * INTERVAL ''1 day'' AS date_of_issue,
   se.scholarship_id,
   se.scholarship_password,
   se.previous_school_name AS lst_school_name,
   NULL AS lst_school_addrs,
   se.attended_class AS lst_attended_class,
   se.school_affiliated AS lst_scl_aff_to,
   se.last_session AS lst_session,
   se.dropout AS is_dropout,
   se.dropout_date,
   se.dropout_reason,
   se.student_type,
   se.type AS student_addmission_type,
   ''Active'' AS session_status,
   ''Initial insert'' AS session_status_comment,
   1 AS updated_by,
   CURRENT_TIMESTAMP AS updated_date,
   CURRENT_TIMESTAMP AS create_date,
   CURRENT_TIMESTAMP AS validity_start_date,
   ''9999-12-31 00:00:00.000''::timestamp AS validity_end_date
FROM
   ' || schema_name || ' .student_excel se
LEFT JOIN
   ' || schema_name || ' .mst_class mc ON mc.class_name = se.student_class
LEFT JOIN
   ' || schema_name || ' .mst_section ms ON ms.section_name = se.student_section
LEFT JOIN
   ' || schema_name || ' .session s ON s.academic_session = se.session
INNER JOIN
   ' || schema_name || ' .student_personal_details spd ON spd.uu_id = se.uu_id
   ';
--    EXECUTE ddl_statement;

--    RETURN 'Insert into parent and student details completed successfully';
EXECUTE studenta_insert_statement ;
RETURN 'Insert into parent and student details completed successfully';
END;
$$ LANGUAGE plpgsql;

SELECT medhapro.insert_student_details('medhapro');