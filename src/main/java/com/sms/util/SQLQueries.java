package com.sms.util;

public class SQLQueries {
    public static final String CREATE_SCHEMA = "CREATE SCHEMA IF NOT EXISTS %s";
    public static final String CREATE_SCHOOL_REGISTRATION =
            "CREATE TABLE IF NOT EXISTS %s.school_registration (" +
                    "user_id serial NOT NULL," +
                    "first_name varchar(255) NOT NULL," +
                    "last_name varchar(255) NOT NULL," +
                    "email varchar(255) NOT NULL," +
                    "password varchar(255) NOT NULL," +
                    "school_code varchar(255) NOT NULL);";

    public static final String CREATE_STUDENT_PERSONAL_DETAILS =
                    "CREATE TABLE IF NOT EXISTS %s.student_personal_details (" +
                    "id SERIAL PRIMARY KEY," +
                    "student_id int not null," +
                    "school_id int not null," +
                    "first_name VARCHAR(255) DEFAULT NULL," +
                    "last_name VARCHAR(255) DEFAULT NULL," +
                    "blood_group VARCHAR(255) DEFAULT NULL," +
                    "gender VARCHAR(255) DEFAULT NULL," +
                    "aadhar_number VARCHAR(255) DEFAULT NULL," +
                    "phone_number VARCHAR(255) DEFAULT NULL," +
                    "emergency_phone_number VARCHAR(255) DEFAULT NULL," +
                    "email_address VARCHAR(255) DEFAULT NULL," +
                    "father_name VARCHAR(255) DEFAULT NULL," +
                    "father_occupation VARCHAR(255) DEFAULT NULL," +
                    "mother_name VARCHAR(255) DEFAULT NULL," +
                    "mother_occupation VARCHAR(255) DEFAULT NULL," +
                    "dob TIMESTAMP DEFAULT NULL," +
                    "religion VARCHAR(255) DEFAULT NULL," +
                    "nationality VARCHAR(255) DEFAULT NULL," +
                    "current_address VARCHAR(255) DEFAULT NULL," +
                    "current_city VARCHAR(255) DEFAULT NULL," +
                    "current_state VARCHAR(255) DEFAULT NULL," +
                    "current_zipcode int default null," +
                    "permanent_address VARCHAR(255) DEFAULT NULL," +
                    "permanent_city VARCHAR(255) DEFAULT NULL," +
                    "permanent_state VARCHAR(255) DEFAULT NULL," +
                    "permanent_zipcode int default null," +
                    "student_country VARCHAR(255) DEFAULT NULL," +
                    "current_status VARCHAR(255) DEFAULT NULL," +
                    "current_status_comment VARCHAR(255) DEFAULT NULL," +
                    "updated_by int DEFAULT NULL," +
                    "updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "validity_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "validity_end_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "student_photo VARCHAR(255) DEFAULT NULL," +
                    "deleted BOOLEAN DEFAULT NULL);";

    public static final String CREATE_STUDENT_ACADEMIC_DETAILS =
                    "CREATE TABLE IF NOT EXISTS %s.student_academic_details (" +
                    "id SERIAL PRIMARY KEY," +
                    "student_id int not null," +
                    "school_id INT NOT NULL," +
                    "registration_number VARCHAR(255) DEFAULT NULL," +
                    "roll_number VARCHAR(255) DEFAULT NULL," +
                    "session_id int not null," +
                    "student_class_id int not null," +
                    "student_section_id int not null," +
                    "session_status VARCHAR(255) DEFAULT NULL," +
                    "session_status_comment VARCHAR(255) DEFAULT NULL," +
                    "updated_by int DEFAULT NULL," +
                    "updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "validity_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "validity_end_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";


   /* public static final String CREATE_TRANSPORT_ALLOCATION =
                    "CREATE TABLE IF NOT EXISTS %s.transport_allocation (" +
                    "id SERIAL PRIMARY KEY," +
                    "busStartPoint VARCHAR(255) DEFAULT NULL," +
                    "busStopPoint VARCHAR(255) DEFAULT NULL," +
                    "busDriverName VARCHAR(255) DEFAULT NULL," +
                    "admissionNo VARCHAR(255) DEFAULT NULL," +
                    "firstName VARCHAR(255) DEFAULT NULL," +
                    "lastName VARCHAR(255) DEFAULT NULL," +
                    "studentBoardPoint VARCHAR(255) DEFAULT NULL," +
                    "studentGetOffPoint VARCHAR(255) DEFAULT NULL," +
                    "guardianContactNumber VARCHAR(255) DEFAULT NULL);";*/

    public static final String CREATE_CLASS_ATTENDANCE =
                    "CREATE TABLE IF NOT EXISTS %s.class_attendance(" +
                    "ca_id serial PRIMARY KEY," +
                    "class_id int NOT NULL," +
                    "section_id int NOT NULL," +
                    "subject_id int NOT NULL," +
                    "teacher_id int DEFAULT NULL," +
                    "present_date DATE DEFAULT NULL," +
                    "attendance_mark_time TIMESTAMP DEFAULT NULL);";

    public static final String CREATE_ABSENT_DETAILS =
                    "CREATE TABLE IF NOT EXISTS %s.absent_details (" +
                    "ad_id SERIAL PRIMARY KEY," +
                    "ca_id INT NOT NULL," +
                    "student_id INT NOT NULL," +
                    "absent VARCHAR(255) DEFAULT NULL," +
                    "attendance_date DATE NOT NULL);";

    public static final String CREATE_SESSION =
                    "CREATE TABLE IF NOT EXISTS %s.session (" +
                    "session_id SERIAL PRIMARY KEY," +
                    "school_id int not null," +
                    "academic_session VARCHAR(255) NOT NULL);";

    public static final String CREATE_SCHOOL_DETAILS =
                    "CREATE TABLE IF NOT EXISTS %s.school_details (" +
                    "school_id SERIAL PRIMARY KEY," +
                    "school_code VARCHAR(255) DEFAULT NULL," +
                    "school_name VARCHAR(255) DEFAULT NULL," +
                    "school_building VARCHAR(255) DEFAULT NULL," +
                    "school_address VARCHAR(255) DEFAULT NULL," +
                    "email_address VARCHAR(255) DEFAULT NULL," +
                    "school_city VARCHAR(255) DEFAULT NULL," +
                    "school_state VARCHAR(255) DEFAULT NULL," +
                    "school_country VARCHAR(255) DEFAULT NULL," +
                    "phone_number VARCHAR(255) DEFAULT NULL," +
                    "bank_details VARCHAR(255) DEFAULT NULL," +
                    "branch_name VARCHAR(255) DEFAULT NULL," +
                    "account_number VARCHAR(255) DEFAULT NULL," +
                    "ifsc_code VARCHAR(255) DEFAULT NULL," +
                    "alternate_phone_number VARCHAR(255) DEFAULT NULL," +
                    "school_zipcode VARCHAR(255) DEFAULT NULL," +
                    "school_photo VARCHAR(255) DEFAULT NULL);";

    public static final String CREATE_MST_CLASS =
                    "CREATE TABLE IF NOT EXISTS %s.mst_class (" +
                    "class_id SERIAL PRIMARY KEY," +
                    "school_id int not null," +
                    "class_name VARCHAR(255) DEFAULT NULL," +
                    "CONSTRAINT unique_class UNIQUE (school_id, class_name));";

    public static final String CREATE_MST_SECTION =
                    "CREATE TABLE IF NOT EXISTS %s.mst_section (" +
                    "section_id SERIAL PRIMARY KEY," +
                    "school_id int not null," +
                    "section_name VARCHAR(255) DEFAULT NULL," +
                    "CONSTRAINT unique_section UNIQUE (school_id, section_name));";

    public static final String CREATE_CLASS_AND_SECTION =
                    "CREATE TABLE IF NOT EXISTS %s.class_and_section (" +
                    "class_section_id SERIAL PRIMARY KEY," +
                    "school_id int not null," +
                    "class_id INT NOT NULL," +
                    "section_id INT NOT NULL," +
                    "CONSTRAINT unique_class_section UNIQUE (school_id, class_id, section_id));";

    public static final String CREATE_HOLIDAY =
                    "CREATE TABLE IF NOT EXISTS %s.holiday(" +
                    "id serial primary key," +
                    "school_id int," +
                    "holiday_title VARCHAR(255) NOT NULL," +
                    "holiday_date Date NOT NULL," +
                    "holiday_description VARCHAR(255) NOT NULL);";

    public static final String CREATE_STAFF_DESIGNATION =
                    "CREATE TABLE IF NOT EXISTS %s.staff_designation(" +
                    "sd_id SERIAL PRIMARY KEY," +
                    "school_id int not null," +
                    "designation VARCHAR(255)," +
                    "description VARCHAR(255));";

    public static final String CREATE_BANK_DETAILS =
                    "CREATE TABLE IF NOT EXISTS %s.add_bank_details (" +
                    "bd_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "staff_id INT not null," +
                    "staff_name VARCHAR(255) NOT NULL," +
                    "phone_number VARCHAR(255)," +
                    "bank_name VARCHAR(255)," +
                    "account_number VARCHAR(255) not null," +
                    "ifsc_code VARCHAR(255)," +
                    "permanent_account_number VARCHAR(255)," +
                    "branch_name VARCHAR(255)," +
                    "bank_address VARCHAR(255)," +
                    "updated_by INT DEFAULT NULL," +
                    "update_date_time TIMESTAMP NOT NULL," +
                    "deleted BOOLEAN DEFAULT FALSE);";

    public static final String CREATE_MST_SUBJECT =
                    "CREATE TABLE IF NOT EXISTS %s.mst_subject (" +
                    "subject_id SERIAL PRIMARY KEY," +
                    "school_id int not null," +
                    "subject_name VARCHAR(255) DEFAULT NULL," +
                    "CONSTRAINT unique_subject UNIQUE(school_id, subject_name));";

    public static final String CREATE_MST_SEQUENCE_CONTROLLER =
                    "CREATE TABLE IF NOT EXISTS %s.master_sequence_controller(" +
                    "id serial PRIMARY KEY," +
                    "school_id INT DEFAULT 1," +
                    "seq_code INT NOT NULL," +
                    "current_value INT NOT NULL);";

    public static final String CREATE_STAFF =
                    "CREATE TABLE IF NOT EXISTS %s.staff(" +
                    "staff_id SERIAL PRIMARY KEY," +
                    "first_name VARCHAR(255) default null," +
                    "last_name VARCHAR(255) default null," +
                    "registration_number VARCHAR(255) default null," +
                    "joining_date TIMESTAMP not null," +
                    "designation VARCHAR(255) default null," +
                    "father_name  VARCHAR(255) default null," +
                    "blood_group  VARCHAR(255) default null," +
                    "gender  VARCHAR(255) default null," +
                    "aadhar_number  VARCHAR(255) default null," +
                    "highest_qualification VARCHAR(255) default null," +
                    "phone_number VARCHAR(255) default null," +
                    "email_address VARCHAR(255) default null," +
                    "dob TIMESTAMP DEFAULT NULL," +
                    "religion VARCHAR(255) default null," +
                    "emergency_phone_number VARCHAR(255) default null," +
                    "current_address VARCHAR(255) default null," +
                    "current_zipcode int default null," +
                    "current_city VARCHAR(255) default null," +
                    "current_state VARCHAR(255) default null," +
                    "permanent_address VARCHAR(255) default null," +
                    "permanent_zipcode int default null," +
                    "permanent_city VARCHAR(255) default null," +
                    "permanent_state VARCHAR(255) default null," +
                    "staff_country VARCHAR(255) default null," +
                    "current_status VARCHAR(255) DEFAULT NULL," +
                    "current_status_comment VARCHAR(255) DEFAULT NULL," +
                    "staff_photo VARCHAR(255) default null," +
                    "deleted BOOLEAN DEFAULT NULL);";

    public static final String CREATE_MST_FREQUENCY =
                    "CREATE TABLE IF NOT EXISTS %s.mst_frequency (" +
                    "frequency_id SERIAL PRIMARY KEY," +
                    "school_id int not null," +
                    "frequency_type VARCHAR(255) DEFAULT NULL," +
                    "deleted boolean default null);";

    public static final String CREATE_SCHOOL_FEES =
                    "CREATE TABLE IF NOT EXISTS %s.school_fees (" +
                    "fee_id SERIAL PRIMARY KEY," +
                    "school_id int not null," +
                    "fee_type VARCHAR(255) DEFAULT NULL," +
                    "frequency_id int not null," +
                    "deleted boolean default null);";

    public static final String CREATE_FEE_ASSIGNMENT =
                    "CREATE TABLE IF NOT EXISTS %s.fee_assignment(" +
                    "fa_id serial PRIMARY KEY," +
                    "school_id int NOT NULL," +
                    "session_id int NOT NULL," +
                    "class_id int DEFAULT NULL," +
                    "section_id int NOT NULL," +
                    "student_id int NOT NULL," +
                    "fee_id int NOT NULL," +
                    "dc_id int NOT NULL," +
                    "updated_by int DEFAULT NULL," +
                    "update_date_time TIMESTAMP NOT NULL);";

    public static final String CREATE_PREVENT_MULTIPLE_ROWS =
                    "CREATE OR REPLACE FUNCTION IF NOT EXISTS %s.prevent_multiple_rows()" +
                    "RETURNS TRIGGER AS $$" +
                    "BEGIN" +
                    "IF EXISTS (SELECT 1 FROM school_details LIMIT 1) THEN" +
                    "RAISE EXCEPTION 'Cannot insert more than 1 row into the school_details table';" +
                    "END IF;" +
                    "RETURN NEW;" +
                    "END;" +
                    "$$" +
                    "LANGUAGE plpgsql;";
    public static final String CREATE_TRIGGER_PREVENT_MULTIPLE_ROWS =
                    "CREATE TRIGGER IF NOT EXISTS %s.trigger_prevent_multiple_rows" +
                    "BEFORE INSERT ON %s.school_details" +
                    "FOR EACH ROW" +
                    "EXECUTE FUNCTION %s.prevent_multiple_rows();";

    public static final String CREATE_DISCOUNT_CODE_DC_ID_SEQ =
                    "CREATE SEQUENCE IF NOT EXISTS %s.discount_code_dc_id_seq " +
                    "START WITH 0 " +
                    "MINVALUE 0; ";

    public static final String CREATE_DISCOUNT_CODE =
                    "CREATE TABLE IF NOT EXISTS %s.discount_code (" +
                    "dc_id INTEGER NOT NULL DEFAULT nextval('discount_code_dc_id_seq')," +
                    "dc_description VARCHAR(255) NOT NULL," +
                    "dc_rate INT NOT NULL," +
                    "dc_rate_type VARCHAR(255) NOT NULL," +
                    "additional_info VARCHAR(255) DEFAULT NULL," +
                    "PRIMARY KEY (dc_id));";

    public static final String CREATE_FEE_DUE_DATE =
                    "CREATE TABLE IF NOT EXISTS %s.fee_due_date (" +
                    "fddt_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "fa_id INT NOT NULL," +
                    "due_date DATE NOT NULL," +
                    "fee_amount INT NOT NULL DEFAULT 0," +
                    "discount_amount numeric NOT NULL DEFAULT 0," +
                    "updated_by INT default NULL," +
                    "update_date_time TIMESTAMP NOT null);" ;

    public static final String CREATE_CLASS_TEACHER_ALLOCATION =
                    "CREATE TABLE IF NOT EXISTS %s.class_teacher_allocation (" +
                    "cta_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT NOT NULL," +
                    "class_id INT NOT NULL," +
                    "section_id INT NOT NULL," +
                    "staff_id INT NOT NULL," +
                    "updated_by INT DEFAULT NULL," +
                    "update_date_time TIMESTAMP NOT NULL);";

    public static final String CREATE_FEE_DEPOSIT =
                    "CREATE TABLE IF NOT EXISTS %s.fee_deposit (" +
                    "fd_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT not null," +
                    "class_id INT not null," +
                    "section_id INT not null," +
                    "student_id int not null," +
                    "payment_mode int not null," +
                    "total_amount_paid int not null," +
                    "payment_received_by int default null," +
                    "system_date_time TIMESTAMP not null," +
                    "payment_description VARCHAR(255) default null," +
                    "transaction_id VARCHAR(255) default null," +
                    "comment  VARCHAR(255) DEFAULT NULL);";

    public static final String CREATE_PAYMENT_MODE =
                    "CREATE TABLE IF NOT EXISTS %s.payment_mode (" +
                    "pm_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "payment_type VARCHAR(255) DEFAULT NULL);";

    public static final String CREATE_FEE_DEPOSIT_DETAILS =
                    "CREATE TABLE IF NOT EXISTS %s.fee_deposit_details (" +
                    "fdd_id SERIAL PRIMARY KEY," +
                    "fd_id INT default NULL," +
                    "fa_id INT not NULL," +
                    "fddt_id INT not NULL," +
                    "amount_paid INT not null," +
                    "discount_code INT not null," +
                    "discount_amount INT not null," +
                    "penality int not null," +
                    "balance int not null," +
                    "approved_by int default null," +
                    "payment_received_by int not null," +
                    "system_date_time TIMESTAMP not null," +
                    "comment  VARCHAR(255) DEFAULT NULL);";

    public static final String CREATE_CLASS_SUBJECT_ALLOCATION =
                    "create table IF NOT EXISTS %s.class_subject_allocation(" +
                    "csa_id serial primary key," +
                    "school_id int not null," +
                    "session_id int not null," +
                    "class_id int not null," +
                    "section_id int not null," +
                    "subject_id int not null," +
                    "unique(class_id,section_id,subject_id));";

    public static final String CREATE_CLASS_SUBJECT_TEACHER_ALLOCATION =
                    "CREATE TABLE IF NOT EXISTS %s.class_subject_teacher_allocation (" +
                    "csta_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT NOT NULL," +
                    "class_id INT NOT NULL," +
                    "section_id INT NOT NULL," +
                    "subject_id INT NOT NULL," +
                    "teacher_id INT NOT NULL);" ;

    public static final String CREATE_HOME_WORK =
                    "CREATE TABLE IF NOT EXISTS %s.home_work (" +
                    "hw_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT NOT NULL," +
                    "class_id INT NOT NULL," +
                    "section_id INT NOT NULL," +
                    "subject_id INT NOT NULL," +
                    "assign_home_work_date DATE DEFAULT NULL," +
                    "description VARCHAR(255) NOT NULL," +
                    "home_work_pdf VARCHAR(512) DEFAULT NULL," +
                    "updated_by INT NOT NULL," +
                    "updated_date DATE DEFAULT NULL);";

    public static final String CREATE_ADD_VEHICLE =
            "CREATE TABLE IF NOT EXISTS %s.add_vehicle (" +
                    "vehicle_id SERIAL NOT NULL," +
                    "school_id INT NOT NULL," +
                    "vehicle_number VARCHAR(255) NOT NULL," +
                    "vehicle_type VARCHAR(255) DEFAULT NULL," +
                    "number_of_seat INT DEFAULT NULL," +
                    "refuel_amount DOUBLE PRECISION DEFAULT NULL," +
                    "last_insurance_date DATE DEFAULT NULL," +
                    "renewal_insurance_date DATE DEFAULT NULL," +
                    "last_service_date DATE DEFAULT NULL);";

    public static final String CREATE_ADD_DRIVER =
                    "CREATE TABLE IF NOT EXISTS %s.add_driver (" +
                    "driver_id SERIAL NOT NULL," +
                    "school_id INT NOT NULL," +
                    "first_name VARCHAR(255) NOT NULL," +
                    "last_name VARCHAR(255) DEFAULT NULL," +
                    "dob DATE NOT NULL," +
                    "contact_number VARCHAR(255) NOT NULL," +
                    "license_number VARCHAR(255) NOT NULL," +
                    "address VARCHAR(255) default null," +
                    "city VARCHAR(255) default null," +
                    "state VARCHAR(255) default null," +
                    "zip_code INT default null," +
                    "country VARCHAR(255) default null);";

    public static final String CREATE_ADD_ROUTE =
                    "CREATE TABLE IF NOT EXISTS %s.add_route (" +
                    "route_id SERIAL NOT NULL," +
                    "school_id INT NOT NULL," +
                    "vehicle_id int NOT NULL," +
                    "boarding_point VARCHAR(255) NOT NULL," +
                    "destination VARCHAR(255) NOT NULL," +
                    "fee double precision  NOT NULL);";

    public static final String CREATE_TRANSPORT_ALLOCATION =
                    "CREATE TABLE IF NOT EXISTS %s.transport_allocation (" +
                    "ta_id SERIAL NOT NULL," +
                    "school_id INT NOT NULL," +
                    "driver_id int NOT NULL," +
                    "vehicle_id int NOT NULL," +
                    "route_id int NOT NULL);";

    public static final String CREATE_EXAM_MEETING =
                    "CREATE TABLE IF NOT EXISTS %s.exam_meeting (" +
                    "em_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT NOT NULL," +
                    "title VARCHAR(255) NOT NULL," +
                    "meeting_date Date not null," +
                    "start_time TIME NOT NULL," +
                    "end_time TIME NOT NULL," +
                    "updated_by INT NOT NULL," +
                    "update_date_Time TIMESTAMP NOT null," +
                    "deleted boolean default null);";

    public static final String CREATE_EXAM_MARKS_ENTRY =
                    "create table IF NOT EXISTS %s.exam_marks_entry (" +
                    "eme_id SERIAl not null," +
                    "school_id int not null," +
                    "class_id int not null," +
                    "section_id int not null," +
                    "student_id int not null," +
                    "session_id int not null," +
                    "exam_type VARCHAR(255) not null," +
                    "subject_id int not null," +
                    "maximum_marks DOUBLE PRECISION not null," +
                    "minimum_marks DOUBLE PRECISION not null," +
                    "obtain DOUBLE PRECISION not null," +
                    "grade VARCHAR(255) not null," +
                    "status VARCHAR(255) not null);";

    public static final String CREATE_EXAM_SCHEDULE =
                    "CREATE TABLE IF NOT EXISTS %s.exam_schedule (" +
                    "es_id serial primary key," +
                    "school_id int not null," +
                    "session_id int not null," +
                    "class_id int not null," +
                    "section_id int not null," +
                    "subject_id int not null," +
                    "start_month varchar(255) not null," +
                    "end_month varchar(255) not null," +
                    "exam_type varchar(255) not null," +
                    "exam_date Date not null," +
                    "exam_day varchar(255) not null," +
                    "exam_timing varchar(255) not null," +
                    "updated_by varchar(255) not null," +
                    "update_date_time Date not null," +
                    "deleted boolean default null);";

    public static final String CREATE_STAFF_SALARY =
                    "CREATE TABLE IF NOT EXISTS %s.staff_salary (" +
                    "ss_id serial PRIMARY KEY," +
                    "school_id int NOT NULL," +
                    "session_id int DEFAULT NULL," +
                    "staff_id int NOT NULL," +
                    "designation_id int NOT NULL," +
                    "salary_amount VARCHAR(255) NOT null," +
                    "deleted boolean default null);";

    public static final String CREATE_PAY_SALARY =
            "CREATE TABLE IF NOT EXISTS %s.pay_salary (" +
                    "ps_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT DEFAULT NULL," +
                    "designation_id INT NOT NULL," +
                    "staff_id INT NOT NULL," +
                    "salary_amount VARCHAR(255) NOT NULL," +
                    "count_leave VARCHAR(255) NOT NULL," +
                    "total_salary VARCHAR(255) NOT NULL," +
                    "pay_salary_month VARCHAR(255) NOT NULL," +
                    "pay_salary_year VARCHAR(255) NOT NULL," +
                    "payment_date DATE NOT NULL," +
                    "updated_by INT NOT NULL," +
                    "update_date_time DATE NOT NULL," +
                    "deleted BOOLEAN DEFAULT NULL);";

    public static final String CREATE_BOOK_CATEGORY =
                    "CREATE TABLE IF NOT EXISTS %s.book_category (" +
                    "book_category_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT NOT NULL," +
                    "book_category_name VARCHAR(255) NOT NULL," +
                    "book_description VARCHAR(255) DEFAULT NULL," +
                    "updated_by INT NOT NULL," +
                    "update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);";

    public static final String CREATE_ADD_NEW_BOOK =
                    "CREATE TABLE IF NOT EXISTS %s.add_new_book (" +
                    "book_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT NOT NULL," +
                    "book_name VARCHAR(255) NOT NULL," +
                    "book_author_name VARCHAR(255) not null," +
                    "book_category_id INT NOT NULL," +
                    "isbn VARCHAR(255) NOT NULL," +
                    "price VARCHAR(255) NOT NULL," +
                    "updated_by INT NOT NULL," +
                    "update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null," +
                    "deleted boolean default null);";

    public static final String CREATE_BOOK_STOCK =
                    "CREATE TABLE IF NOT EXISTS %s.book_stock(" +
                    "book_stock_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT NOT NULL," +
                    "book_id INT NOT NULL," +
                    "total_book_stock VARCHAR(255) NOT NULL," +
                    "updated_by INT NOT NULL," +
                    "update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);";

    public static final String CREATE_ISSUE_BOOK =
                    "CREATE TABLE IF NOT EXISTS %s.issue_book (" +
                    "issue_book_id SERIAL PRIMARY KEY," +
                    "school_id INT NOT NULL," +
                    "session_id INT NOT NULL," +
                    "class_id INT NOT NULL," +
                    "section_id INT DEFAULT NULL," +
                    "student_id INT NOT NULL," +
                    "book_id INT NOT NULL," +
                    "issue_date DATE NOT NULL," +
                    "due_date DATE NOT NULL," +
                    "status VARCHAR(255) NOT NULL," +
                    "updated_by INT NOT NULL," +
                    "update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);";
}
