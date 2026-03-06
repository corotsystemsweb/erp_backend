/* Script for school_registration */
CREATE TABLE school_registration (
   user_id serial PRIMARY KEY,
   first_name varchar(255) NOT NULL,
   last_name varchar(255) NOT NULL,
   phone_number varchar(255) NOT NULL,
   email varchar(255) NOT NULL UNIQUE,
   password varchar(255) NOT NULL,
   school_code varchar(255) NOT null,
   role_id INT[] DEFAULT NULL,
   created_date DATE DEFAULT CURRENT_DATE,
   is_active BOOLEAN
 );

/*script for roll*/
CREATE TABLE role (
    id serial PRIMARY KEY,
    role varchar(50) NOT NULL
);
/*script for refresh_token*/
 CREATE TABLE refresh_token (
    token_id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL UNIQUE,
    expiry_time TIMESTAMP   NOT NULL,
    CONSTRAINT fk_user_email FOREIGN KEY (email) REFERENCES school_registration(email) ON DELETE CASCADE
);

--/*script for student_personal_details*/
--CREATE TABLE student_personal_details (
--    id SERIAL PRIMARY KEY,
--    student_id int not null,
--    school_id int not null,
--    first_name VARCHAR(255) DEFAULT NULL,
--    last_name VARCHAR(255) DEFAULT NULL,
--    blood_group VARCHAR(255) DEFAULT NULL,
--    gender VARCHAR(255) DEFAULT NULL,
--    aadhar_number VARCHAR(255) DEFAULT NULL,
--    phone_number VARCHAR(255) DEFAULT NULL,
--    emergency_phone_number VARCHAR(255) DEFAULT NULL,
--    email_address VARCHAR(255) DEFAULT NULL,
--    father_name VARCHAR(255) DEFAULT NULL,
--    father_occupation VARCHAR(255) DEFAULT NULL,
--    mother_name VARCHAR(255) DEFAULT NULL,
--    mother_occupation VARCHAR(255) DEFAULT NULL,
--    dob TIMESTAMP DEFAULT NULL,
--    religion VARCHAR(255) DEFAULT NULL,
--    nationality VARCHAR(255) DEFAULT NULL,
--    current_address VARCHAR(255) DEFAULT NULL,
--    current_city VARCHAR(255) DEFAULT NULL,
--    current_state VARCHAR(255) DEFAULT NULL,
--    current_zipcode int default null,
--    permanent_address VARCHAR(255) DEFAULT NULL,
--    permanent_city VARCHAR(255) DEFAULT NULL,
--    permanent_state VARCHAR(255) DEFAULT NULL,
--    permanent_zipcode int default null,
--    student_country VARCHAR(255) DEFAULT NULL,
--    current_status VARCHAR(255) DEFAULT NULL,
--    current_status_comment VARCHAR(255) DEFAULT NULL,
--    updated_by int DEFAULT NULL,
--    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    validity_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    validity_end_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    student_photo VARCHAR(255) DEFAULT NULL,
--    deleted BOOLEAN DEFAULT NULL
--    );
--
--/*script for student_academic_details*/
--CREATE TABLE student_academic_details (
--    id SERIAL PRIMARY KEY,
--    student_id int not null,
--    school_id INT NOT NULL,
--    registration_number VARCHAR(255) DEFAULT NULL,
--    roll_number VARCHAR(255) DEFAULT NULL,
--    session_id int not null,
--    student_class_id int not null,
--    student_section_id int not null,
--    session_status VARCHAR(255) DEFAULT NULL,
--    session_status_comment VARCHAR(255) DEFAULT NULL,
--    updated_by int DEFAULT NULL,
--    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    validity_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    validity_end_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--    );

/*script for student_personal_details*/
CREATE TABLE student_personal_details (
    id SERIAL PRIMARY KEY,
    student_id int not null,
    school_id int not null,
    uu_id VARCHAR(255) DEFAULT NULL,
    parent_id INT[] DEFAULT NULL,
    father_name VARCHAR(255) DEFAULT NULL,
    father_occupation VARCHAR(255) DEFAULT NULL,
    mother_name VARCHAR(255) DEFAULT NULL,
    mother_occupation VARCHAR(255) DEFAULT NULL,
    first_name VARCHAR(255) DEFAULT NULL,
    last_name VARCHAR(255) DEFAULT NULL,
    blood_group VARCHAR(255) DEFAULT NULL,
    gender VARCHAR(255) DEFAULT NULL,
    height VARCHAR(255) DEFAULT NULL,
    weight VARCHAR(255) DEFAULT NULL,
    aadhar_number VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL,
    emergency_phone_number VARCHAR(255) DEFAULT NULL,
    whatsapp_no VARCHAR(255) DEFAULT NULL,
    email_address VARCHAR(255) DEFAULT NULL,
    dob TIMESTAMP DEFAULT NULL,
    dob_cirtificate_no VARCHAR(255) DEFAULT NULL,
    income_app_no VARCHAR(255) DEFAULT NULL,
    caste_app_no VARCHAR(255) DEFAULT NULL,
    domicile_app_no VARCHAR(255) DEFAULT NULL,
    govt_student_id_on_portal VARCHAR(255) DEFAULT NULL,
    govt_family_id_on_portal VARCHAR(255) DEFAULT NULL,
    bank_name VARCHAR(255) DEFAULT NULL,
    branch_name VARCHAR(255) DEFAULT NULL,
    ifsc_code VARCHAR(255) DEFAULT NULL,
    account_no VARCHAR(255) DEFAULT NULL,
    pan_no VARCHAR(255) DEFAULT NULL,
    religion VARCHAR(255) DEFAULT NULL,
    nationality VARCHAR(255) DEFAULT NULL,
    category VARCHAR(255) DEFAULT NULL,
    caste VARCHAR(255) DEFAULT NULL,
    current_address VARCHAR(255) DEFAULT NULL,
    current_city VARCHAR(255) DEFAULT NULL,
    current_state VARCHAR(255) DEFAULT NULL,
    current_zipcode int default null,
    permanent_address VARCHAR(255) DEFAULT NULL,
    permanent_city VARCHAR(255) DEFAULT NULL,
    permanent_state VARCHAR(255) DEFAULT NULL,
    permanent_zipcode int default null,
    student_country VARCHAR(255) DEFAULT NULL,
    current_status VARCHAR(255) DEFAULT NULL,
    current_status_comment VARCHAR(255) DEFAULT NULL,
    updated_by int DEFAULT NULL,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    validity_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    validity_end_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    student_photo VARCHAR(255) DEFAULT NULL,
    deleted BOOLEAN DEFAULT NULL,
    CONSTRAINT uk_student_personal_student_id UNIQUE (student_id)
    );

/*script for student_academic_details*/
CREATE TABLE student_academic_details (
    id SERIAL PRIMARY KEY,
    student_id int not null,
    school_id INT NOT NULL,
    uu_id VARCHAR(255) DEFAULT NULL,
    apaar_id VARCHAR(255) DEFAULT NULL,
    pen_no VARCHAR(255) DEFAULT NULL,
    admission_no VARCHAR(255) DEFAULT NULL,
    admission_date TIMESTAMP DEFAULT NULL,
    registration_number VARCHAR(255) DEFAULT NULL,
    roll_number VARCHAR(255) DEFAULT NULL,
    session_id int not null,
    student_class_id int not null,
    student_section_id int not null,
    stream VARCHAR(255) DEFAULT NULL,
    education_medium VARCHAR(255) DEFAULT NULL,
    referred_by VARCHAR(255) DEFAULT NULL,
    is_rte_student BOOLEAN,
    rte_application_no VARCHAR(255) DEFAULT NULL,
    enrolled_session VARCHAR(255) DEFAULT NULL,
    enrolled_class VARCHAR(255) DEFAULT NULL,
    enrolled_year VARCHAR(255) DEFAULT NULL,
    transfer_cirti_no VARCHAR(255) DEFAULT NULL,
    date_of_issue TIMESTAMP DEFAULT NULL,
    scholarship_id VARCHAR(255) DEFAULT NULL,
    scholarship_password VARCHAR(255) DEFAULT NULL,
    lst_school_name VARCHAR(255) DEFAULT NULL,
    lst_school_addrs VARCHAR(255) DEFAULT NULL,
    lst_attended_class VARCHAR(255) DEFAULT NULL,
    lst_scl_aff_to VARCHAR(255) DEFAULT NULL,
    lst_session VARCHAR(255) DEFAULT NULL,
    is_dropout BOOLEAN,
    dropout_date TIMESTAMP DEFAULT NULL,
    dropout_reason VARCHAR(255) DEFAULT NULL,
    student_addmission_type VARCHAR(255) DEFAULT NULL,
    session_status VARCHAR(255) DEFAULT NULL,
    session_status_comment VARCHAR(255) DEFAULT NULL,
    previous_qualifications JSONB DEFAULT '[]',
    updated_by int DEFAULT NULL,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    validity_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    validity_end_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    student_type VARCHAR(255) DEFAULT NULL,
    CONSTRAINT uk_student_session UNIQUE (student_id, session_id)
    );

/*script for accountant*/
CREATE TABLE accountant (
    id SERIAL PRIMARY KEY,
    firstName VARCHAR(255) DEFAULT NULL,
    LastName VARCHAR(255) DEFAULT NULL,
    email VARCHAR(255) DEFAULT NULL,
    phoneNumber varchar(12) DEFAULT NULL
);

/*transport_allocation*/
CREATE TABLE transport_allocation (
    id SERIAL PRIMARY KEY,
    busStartPoint VARCHAR(255) DEFAULT NULL,
    busStopPoint VARCHAR(255) DEFAULT NULL,
    busDriverName VARCHAR(255) DEFAULT NULL,
    admissionNo VARCHAR(255) DEFAULT NULL,
    firstName VARCHAR(255) DEFAULT NULL,
    lastName VARCHAR(255) DEFAULT NULL,
    studentBoardPoint VARCHAR(255) DEFAULT NULL,
    studentGetOffPoint VARCHAR(255) DEFAULT NULL,
    guardianContactNumber VARCHAR(255) DEFAULT NULL
    )

/*script for class_attendance*/
CREATE TABLE class_attendance(
    ca_id serial PRIMARY KEY,
    class_id int NOT NULL,
    section_id int NOT NULL,
    subject_id int NOT NULL,
    teacher_id int DEFAULT NULL,
    present_date DATE DEFAULT NULL,
    attendance_mark_time TIMESTAMP DEFAULT NULL,
    session_id int not null
);

/*script for absent_details*/
CREATE TABLE absent_details (
    ad_id SERIAL PRIMARY KEY,
    ca_id INT NOT NULL,
    student_id INT NOT NULL,
    absent VARCHAR(255) DEFAULT NULL,
    attendance_date DATE NOT NULL
);

/*script for session*/
CREATE TABLE session (
    session_id SERIAL PRIMARY KEY,
    school_id int not null,
    academic_session VARCHAR(255) NOT NULL,
    start_date DATE,
    end_date DATE
);

/*script for school_details*/
CREATE TABLE school_details (
    school_id serial NOT null check (school_id =1 ),
    school_code VARCHAR(255) DEFAULT NULL,
    school_affiliation_no VARCHAR(255) DEFAULT NULL,
    school_name VARCHAR(255) DEFAULT NULL,
    school_building VARCHAR(255) DEFAULT NULL,
    school_address VARCHAR(255) DEFAULT NULL,
    email_address VARCHAR(255) DEFAULT NULL,
    school_city VARCHAR(255) DEFAULT NULL,
    school_state VARCHAR(255) DEFAULT NULL,
    school_country VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL,
    bank_details VARCHAR(255) DEFAULT NULL,
    branch_name VARCHAR(255) DEFAULT NULL,
    account_number VARCHAR(255) DEFAULT NULL,
    ifsc_code VARCHAR(255) DEFAULT NULL,
    alternate_phone_number VARCHAR(255) DEFAULT NULL,
    school_zipcode VARCHAR(255) DEFAULT NULL,
    school_photo VARCHAR(255) DEFAULT NULL
);
/*script for mst_class*/
CREATE TABLE mst_class (
    class_id SERIAL PRIMARY KEY,
    school_id int not null,
    class_name VARCHAR(255) DEFAULT NULL,
    CONSTRAINT unique_class UNIQUE (school_id, class_name)
);
/*script for mst_section*/
CREATE TABLE mst_section (
    section_id SERIAL PRIMARY KEY,
    school_id int not null,
    section_name VARCHAR(255) DEFAULT NULL,
    CONSTRAINT unique_section UNIQUE (school_id, section_name)
);
/*script for class_and_section*/
CREATE TABLE class_and_section (
    class_section_id SERIAL PRIMARY KEY,
    school_id int not null,
    class_id INT NOT NULL,
    section_id INT NOT NULL,
    CONSTRAINT unique_class_section UNIQUE (school_id, class_id, section_id)
);


/* script for staff_designation */
CREATE TABLE staff_designation(
   sd_id SERIAL PRIMARY KEY,
   school_id int not null,
   designation VARCHAR(255),
   description VARCHAR(255),
   status VARCHAR(255),
   department_id int not null
);

/* script for staff_department */
CREATE TABLE staff_department(
   stdp_id SERIAL PRIMARY KEY,
   school_id int not null,
   department VARCHAR(255),
   description VARCHAR(255),
   status VARCHAR(255)
);

/*script for holiday*/
CREATE TABLE holiday(
    id serial primary key,
    school_id int,
    holiday_title VARCHAR(255) NOT NULL,
    holiday_date Date NOT NULL,
    holiday_description VARCHAR(255) NOT NULL,
    session_id int not null
);


/*script for add_bank_details*/
CREATE TABLE add_bank_details (
    bd_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    staff_id INT not null,
    staff_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    bank_name VARCHAR(255),
    account_number VARCHAR(255) not null,
    ifsc_code VARCHAR(255),
    permanent_account_number VARCHAR(255),
    branch_name VARCHAR(255),
    bank_address VARCHAR(255),
    updated_by INT DEFAULT NULL,
    update_date_time TIMESTAMP NOT NULL,
    deleted BOOLEAN DEFAULT FALSE
);

/*script for mst_subject*/
CREATE TABLE mst_subject (
    subject_id SERIAL PRIMARY KEY,
    school_id int not null,
    subject_name VARCHAR(255) DEFAULT NULL,
    CONSTRAINT unique_subject UNIQUE(school_id, subject_name)
);
/* script for master_sequence_controller*/
CREATE TABLE master_sequence_controller(
     id serial PRIMARY KEY,
     school_id INT DEFAULT 1,
     seq_code INT NOT NULL,
     current_value INT NOT NULL
);
/* table for staff*/
 CREATE TABLE staff
 (
     staff_id SERIAL PRIMARY KEY,
     school_id INT NOT NULL,
	 session_id int not null,
     first_name VARCHAR(255) default null,
     last_name VARCHAR(255) default null,
     registration_number VARCHAR(255) default null,
     joining_date TIMESTAMP not null,
     department_id int not null,
     designation_id int not null,
	 employment_type VARCHAR(255) NOT NULL,
     father_name  VARCHAR(255) default null,
     blood_group  VARCHAR(255) default null,
     gender  VARCHAR(255) default null,
     aadhar_number  VARCHAR(255) default null,
     highest_qualification VARCHAR(255) default null,
     pf_account_no VARCHAR(255) DEFAULT NULL,
	 experience VARCHAR(255) DEFAULT NULL,
     experience_details TEXT DEFAULT NULL,
     phone_number VARCHAR(255) default null,
     email_address VARCHAR(255) default null,
     dob TIMESTAMP DEFAULT NULL,
     religion VARCHAR(255) default null,
     emergency_phone_number VARCHAR(255) default null,
     emergency_contact_name VARCHAR(255) default null,
     current_address VARCHAR(255) default null,
     current_zipcode int default null,
     current_city VARCHAR(255) default null,
     current_state VARCHAR(255) default null,
     permanent_address VARCHAR(255) default null,
     permanent_zipcode int default null,
     permanent_city VARCHAR(255) default null,
     permanent_state VARCHAR(255) default null ,
     staff_country VARCHAR(255) default null,
     current_status VARCHAR(255) DEFAULT NULL,
     current_status_comment VARCHAR(255) DEFAULT NULL,
     staff_photo VARCHAR(255) default null,
     deleted BOOLEAN DEFAULT NULL
 );

/* table for mst_frequency */
CREATE TABLE mst_frequency (
    frequency_id SERIAL PRIMARY KEY,
    school_id int not null,
    frequency_type VARCHAR(255) DEFAULT NULL,
    deleted boolean default null
);
/* table for school_fees */
CREATE TABLE school_fees (
fee_id SERIAL PRIMARY KEY ,
school_id int not null,
fee_type VARCHAR(255) DEFAULT NULL,
frequency_id int not null,
deleted boolean default null
);

/* table for fee_assignment */
CREATE TABLE fee_assignment(
    fa_id serial PRIMARY KEY,
    school_id int NOT NULL,
    session_id int NOT NULL,
    class_id int DEFAULT NULL,
    section_id int DEFAULT NULL,
    student_id int DEFAULT NULL,
    fee_id int NOT NULL,
    dc_id int DEFAULT NULL,
    valid_from date DEFAULT CURRENT_DATE NULL,
    valid_to date NULL,
    updated_by int DEFAULT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* table for fee_assignment_exclusion */
CREATE TABLE fee_assignment_exclusion (
	exclusion_id serial NOT NULL,
	fa_id int NOT NULL,
	student_id int NOT NULL,
	valid_from date DEFAULT CURRENT_DATE NULL,
	valid_to date NULL,
	reason text NULL,
	created_by int NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL
);

/* Sequence for discount_code */

CREATE SEQUENCE discount_code_dc_id_seq
START WITH 0
MINVALUE 0;

/* table for discount_code */
CREATE TABLE discount_code (
    dc_id INTEGER NOT NULL DEFAULT nextval('discount_code_dc_id_seq'),
    dc_description VARCHAR(255) NOT NULL,
    dc_rate INT NOT NULL,
    dc_rate_type VARCHAR(255) NOT NULL,
    additional_info VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (dc_id),
    CONSTRAINT uk_dc_description UNIQUE (dc_description)
);
/* table for fee_due_date */
CREATE TABLE fee_due_date (
    fddt_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    fa_id INT NOT NULL,
    due_date DATE NOT NULL,
    fee_amount INT NOT NULL DEFAULT 0,
    discount_amount INT NOT NULL DEFAULT 0,
    updated_by INT default NULL,
    update_date_time TIMESTAMP NOT null
);
/* table for class_teacher_allocation*/
CREATE TABLE class_teacher_allocation (
    cta_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    class_id INT NOT NULL,
    section_id INT NOT NULL,
    staff_id INT NOT NULL,
    updated_by INT DEFAULT NULL,
    update_date_time TIMESTAMP NOT NULL
);
/* table for fee_deposit */
CREATE TABLE fee_deposit (
    fd_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT not null,
    class_id INT not null,
    section_id INT not null,
    student_id int not null,
    payment_mode int not null,
    total_amount_paid int not null,
    payment_received_by int default null,
    system_date_time TIMESTAMP not null,
    payment_description VARCHAR(255) default null,
    transaction_id VARCHAR(255) default null,
    comment  VARCHAR(255) DEFAULT NULL,
    fdd_status VARCHAR(255) DEFAULT NULL,
    payment_date date NOT NULL
);
/*table for payment_mode*/
CREATE TABLE payment_mode (
    pm_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    payment_type VARCHAR(255) DEFAULT NULL
);
/* table for penalty */
CREATE TABLE penalty (
    penalty_id SERIAL PRIMARY KEY,
    penalty_amount INT DEFAULT NULL,
    penalty_type VARCHAR(255) NOT NULL,
    penalty_percentage VARCHAR(255) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    system_date_time TIMESTAMP NOT NULL
);
/* table for fee_deposit_details */
CREATE TABLE fee_deposit_details (
    fdd_id SERIAL PRIMARY KEY,
    fd_id INT default NULL,
    fa_id INT not NULL,
    fddt_id INT not NULL,
    amount_paid DECIMAL(10, 0) not null,
    dc_id INT not null,
    discount_amount DECIMAL(10, 0) not null,
    penalty_id int DEFAULT NULL,
    penalty_amount DECIMAL(10, 0) DEFAULT NULL,
    balance DECIMAL(10, 0) NOT NULL,
    approved_by int default null,
    payment_received_by int not null,
    system_date_time TIMESTAMP not null,
    status VARCHAR(255) NOT NULL,
    comment  VARCHAR(255) DEFAULT NULL,
    additional_discount numeric(10) DEFAULT 0 NULL,
    	additional_dc_id int4 NULL,
    	additional_discount_reason varchar(255) NULL,
    	CONSTRAINT fee_deposit_details_pkey PRIMARY KEY (fdd_id)
);
CREATE TABLE transport_fee_due (
    tfdue_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    student_transport_id INT NOT NULL,
    due_month DATE NOT NULL,
    fee_amount NUMERIC(10,2) NOT NULL,
    discount_amount NUMERIC(10,2) DEFAULT 0,
    penalty_amount NUMERIC(10,2) DEFAULT 0
);
CREATE TABLE transport_fee_deposit (
    tfd_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    class_id INT not null,
    section_id INT not null,
    student_id INT NOT NULL,
    route_id INT NOT NULL,
    payment_mode INT NOT NULL,
    total_amount_paid NUMERIC(10,2) NOT NULL,
    payment_received_by INT DEFAULT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_id VARCHAR(255) DEFAULT NULL,
    payment_description VARCHAR(255) DEFAULT NULL,
    status VARCHAR(255) DEFAULT NULL
);
CREATE TABLE transport_fee_deposit_details (
    tfdd_id SERIAL PRIMARY KEY,
    tfd_id INT NOT NULL, -- FK to transport_fee_deposit
    student_transport_id INT NOT NULL,
    due_month DATE NOT NULL, -- e.g. 2026-04-01 (month basis)
    fee_amount NUMERIC(10,2) NOT NULL,
    discount_amount NUMERIC(10,2) DEFAULT 0,
    penalty_amount NUMERIC(10,2) DEFAULT 0,
    amount_paid NUMERIC(10,2) NOT NULL,
    balance NUMERIC(10,2) DEFAULT 0,
    status VARCHAR(255) NOT NULL,
	system_date_time TIMESTAMP NOT NULL
);
/* script for class_subject_allocation */
create table class_subject_allocation(
csa_id serial primary key,
school_id int not null,
session_id int not null,
class_id int not null,
section_id int not null,
subject_id int not null,
unique(class_id,section_id,subject_id)
);

/*script for class_subject_teacher-allocation*/
CREATE TABLE class_subject_teacher_allocation (
    csta_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    class_id INT NOT NULL,
    section_id INT NOT NULL,
    subject_id INT NOT NULL,
    teacher_id INT NOT NULL
);

/*script for home_work*/
CREATE TABLE home_work (
    hw_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    class_id INT NOT NULL,
    section_id INT NOT NULL,
    subject_id INT NOT NULL,
    assign_home_work_date DATE DEFAULT NULL,
    description VARCHAR(255) NOT NULL,
    home_work_pdf VARCHAR(512) DEFAULT NULL,
    updated_by INT NOT NULL,
    updated_date DATE DEFAULT NULL
);

/*script for add_vehicle*/
CREATE TABLE add_vehicle (
   vehicle_id SERIAL NOT NULL,
   school_id INT NOT NULL,
   vehicle_number VARCHAR(255) NOT NULL,
   vehicle_type VARCHAR(255) DEFAULT NULL,
   number_of_seat INT DEFAULT NULL,
   refuel_amount DOUBLE PRECISION DEFAULT NULL,
   last_insurance_date DATE DEFAULT NULL,
   renewal_insurance_date DATE DEFAULT NULL,
   last_service_date DATE DEFAULT NULL
);
CREATE table add_driver (
   driver_id SERIAL NOT NULL,
   school_id INT NOT NULL,
   session_id INT NOT null,
   sd_id INT NOT NULL,
   first_name VARCHAR(255) NOT NULL,
   last_name VARCHAR(255) DEFAULT NULL,
   dob DATE NOT NULL,
   contact_number VARCHAR(255) NOT NULL,
   license_number VARCHAR(255) NOT NULL,
   aadhar_number VARCHAR(255) NOT NULL,
   joining_date Date NOT NULL,
   employment_type VARCHAR(255) DEFAULT NULL,
   experience VARCHAR(255) DEFAULT NULL,
   address VARCHAR(255) default null,
   city VARCHAR(255) default null,
   state VARCHAR(255) default null,
   zip_code INT default null,
   country VARCHAR(255) default null,
   current_status VARCHAR(255) default null,
   current_status_comment VARCHAR(255),
   updated_by INT DEFAULT NULL,
   update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/*script for add_route*/
CREATE TABLE add_route (
   route_id SERIAL NOT NULL,
   school_id INT NOT NULL,
   vehicle_id int NOT NULL,
   boarding_point VARCHAR(255) NOT NULL,
   destination VARCHAR(255) NOT NULL,
   max_fee NUMERIC(10,2) NOT NULL,
   stop_name VARCHAR(255),
   stop_fare NUMERIC(10,2),
   start_date DATE NOT NULL,
   end_date DATE NOT NULL,
   hash_value VARCHAR(255) NOT NULL
);
/*script for transport allocation*/
   CREATE TABLE transport_allocation (
   ta_id SERIAL NOT NULL,
   school_id INT NOT NULL,
   driver_id int NOT NULL,
   vehicle_id int NOT NULL,
   route_id int NOT NULL
);
/*script for student_transport_details*/
CREATE TABLE student_transport_details (
    student_transport_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    student_id INT NOT NULL,
    route_id INT NOT NULL,
    fee NUMERIC(10,2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);
/*script for student_registration*/
CREATE TABLE student_registration (
    std_registration_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) DEFAULT NULL,
    dob DATE NOT NULL,
    blood_group VARCHAR(50) DEFAULT NULL,
    religion VARCHAR(100) DEFAULT NULL,

    aadhaar_number VARCHAR(255) DEFAULT NULL,
    student_pen_no VARCHAR(255) DEFAULT NULL,
    contact_no VARCHAR(50) NOT NULL,
    alternate_number VARCHAR(50) DEFAULT NULL,
    email VARCHAR(255) DEFAULT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pin_code VARCHAR(20) NOT NULL,
    father_name VARCHAR(255) NOT NULL,
    mother_name VARCHAR(255) NOT NULL,
    session_id INT NOT NULL,
    qualification VARCHAR(255) NOT NULL,
    enrolled_class VARCHAR(100) NOT NULL,
    last_institute_name VARCHAR(255) DEFAULT NULL,
    transfer_certificate_no VARCHAR(255) DEFAULT NULL,
    reference_name VARCHAR(255) DEFAULT NULL,
    reference_type VARCHAR(255) DEFAULT NULL,
    registration_date DATE NOT NULL,
    enquiry_status VARCHAR(100) DEFAULT 'PENDING',
    comment VARCHAR(500) DEFAULT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE'
);


/*script for exam_meeting*/
CREATE TABLE exam_meeting (
    em_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    meeting_date Date not null,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    updated_by INT NOT NULL,
    update_date_Time TIMESTAMP NOT null,
    deleted boolean default null

);
/* scripts for exam_marks_entry */
create table exam_marks_entry (
eme_id SERIAl not null,
school_id int not null,
class_id int not null,
section_id int not null,
student_id int not null,
session_id int not null,
exam_type VARCHAR(255) not null,
subject_id int not null,
maximum_marks DOUBLE PRECISION not null,
minimum_marks DOUBLE PRECISION not null,
obtain DOUBLE PRECISION not null,
grade VARCHAR(255) not null,
status VARCHAR(255) not null
);

CREATE TABLE exam_schedule (
    es_id serial primary key,
    school_id int not null,
    session_id int not null,
    class_id int not null,
    section_id int not null,
    subject_id int not null,
    start_month varchar(255) not null,
    end_month varchar(255) not null,
    exam_type varchar(255) not null,
    exam_date Date not null,
    exam_day varchar(255) not null,
    exam_timing varchar(255) not null,
    updated_by varchar(255) not null,
    update_date_time Date not null,
    deleted boolean default null
);
/* table for staff_salary */
CREATE TABLE staff_salary (
    ss_id serial PRIMARY KEY,
    school_id int NOT NULL,
    session_id int DEFAULT NULL,
    staff_id int NOT NULL,
    department_id int NOT NULL,
    designation_id int NOT NULL,
    salary_amount VARCHAR(255) NOT null,
    deleted boolean default null
);

/* table for pay_salary */
CREATE TABLE pay_salary (
    ps_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT DEFAULT NULL,
    designation_id INT NOT NULL,
    staff_id INT NOT NULL,
    salary_amount VARCHAR(255) NOT NULL,
    count_leave VARCHAR(255) NOT NULL,
    total_salary VARCHAR(255) NOT NULL,
    pay_salary_month VARCHAR(255) NOT NULL,
    pay_salary_year VARCHAR(255) NOT NULL,
    payment_date DATE NOT NULL,
    updated_by INT NOT NULL,
    update_date_time DATE NOT NULL,
    deleted BOOLEAN DEFAULT NULL
);

/* table for book_category */
CREATE TABLE book_category (
    book_category_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    book_category_name VARCHAR(255) NOT NULL,
    book_description VARCHAR(255) DEFAULT NULL,
    updated_by INT NOT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

/* table for add_new_book */
CREATE TABLE add_new_book (
    book_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    book_name VARCHAR(255) NOT NULL,
    book_author_name VARCHAR(255) not null,
    book_category_id INT NOT NULL,
    isbn VARCHAR(255) NOT NULL,
    price VARCHAR(255) NOT NULL,
    updated_by INT NOT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
    deleted boolean default null
);

/* table for book_stock */
CREATE TABLE book_stock(
    book_stock_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    book_id INT NOT NULL,
    total_book_stock VARCHAR(255) NOT NULL,
    updated_by INT NOT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

/* table for issue_book */
CREATE table issue_book (
    issue_book_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    class_id INT NOT NULL,
    section_id INT DEFAULT NULL,
    student_id INT NOT NULL,
    book_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    updated_by INT NOT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
    deleted boolean default null
);
/*table for add_supplier*/
CREATE TABLE add_supplier (
    supplier_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    supplier_name VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    gstin VARCHAR(255) NOT NULL,
    pan VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    pin_code VARCHAR(255) NOT NULL,
    website VARCHAR(255),
    bank_name VARCHAR(255) NOT NULL,
    branch_name VARCHAR(255) NOT NULL,
    account_number VARCHAR(255) NOT NULL,
    ifsc_code VARCHAR(255) NOT NULL,
    updated_by INT NOT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
    deleted boolean default null
);

 /*table for inventory_category*/
CREATE TABLE inventory_category (
    inventory_category_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    category_details VARCHAR(255) NOT NULL,
    category_description VARCHAR(255),
    updated_by INT NOT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
/*table for add_inventory_items*/
CREATE TABLE add_inventory_items (
    add_inventory_items_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    supplier_id INT NOT NULL,
    inventory_category_id INT NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    description VARCHAR(255) default NULL,
    publish VARCHAR(255) not NULL,
    cost_price DOUBLE PRECISION default NULL,
    sale_price DOUBLE PRECISION default NULL,
    stock INT default null,
    updated_by INT DEFAULT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted boolean default null
);
/*table for fuel_expense*/
CREATE TABLE fuel_expense (
    fuel_expense_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    fuel_date DATE NOT NULL,
    fuel_liter DECIMAL(10, 2) NOT NULL,
    refuel_amount DECIMAL(10, 2) NOT NULL,
    updated_by INT DEFAULT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted boolean default null
    );
/*table for expense_type*/
CREATE TABLE expense_type (
    expense_type_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    expense_title VARCHAR(255) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    updated_by INT DEFAULT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted boolean default null
    );
/*table for add_expense*/
CREATE TABLE add_expense (
    add_expense_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    expense_type_id INT NOT NULL,
    particular VARCHAR(255) NOT NULL,
    expense_date DATE NOT NULL,
    expense_amount DECIMAL(10,2) NOT NULL,
    updated_by INT DEFAULT NULL,
    update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted boolean default null
    );
/*script for student_excel for excel*/
CREATE TABLE student_excel(
    student_id serial PRIMARY KEY,
    uu_id VARCHAR(255) DEFAULT null,
    apaar_id VARCHAR(255) DEFAULT NULL,
    session VARCHAR(255) default null,
    student_first_name VARCHAR(255) NOT NULL,
    student_last_name VARCHAR(255) DEFAULT NULL,
    student_class VARCHAR(255) NOT NULL,
    student_section VARCHAR(255) NOT NULL,
    roll_no INT NOT NULL,
    medium VARCHAR(255) NOT NULL,
    dob DATE NOT NULL,
    religion VARCHAR(255) DEFAULT NULL,
    nationality VARCHAR(255) NOT NULL,
    gender VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(255) NOT NULL,
    email VARCHAR(255) DEFAULT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    pincode VARCHAR(255) NOT NULL,
    type VARCHAR(255) DEFAULT NULL,
    isRteStudent BOOLEAN DEFAULT NULL,
    student_mother_name VARCHAR(255) DEFAULT NULL ,
    student_father_name VARCHAR(255) DEFAULT NULL,
    guardian_name VARCHAR(255) DEFAULT NULL,
    admission_date DATE NOT NULL,
    enrolled_session VARCHAR(255) DEFAULT NULL,
    enrolled_year VARCHAR(255) DEFAULT NULL,
    enrolled_class VARCHAR(255) DEFAULT NULL,
    pen_no VARCHAR(255) DEFAULT NULL,
    student_type VARCHAR(255) DEFAULT NULL,
    admission_no VARCHAR(255) DEFAULT NULL,
    registration_no VARCHAR(255) DEFAULT NULL,
    enrollment_no VARCHAR(255) DEFAULT NULL,
    stream VARCHAR(255) DEFAULT NULL,
    whatsapp VARCHAR(255) DEFAULT NULL,
    alternate_number VARCHAR(255) DEFAULT NULL,
    blood_group VARCHAR(255) DEFAULT NULL,
    aadhar_No VARCHAR(255) DEFAULT NULL,
    caste VARCHAR(255) DEFAULT NULL,
    category VARCHAR(255) DEFAULT NULL,
    rte_application_no VARCHAR(255) DEFAULT NULL,
    attended_school VARCHAR(255) DEFAULT NULL,
    attended_class VARCHAR(255) DEFAULT NULL,
    school_affiliated VARCHAR(255) DEFAULT NULL,
    last_session VARCHAR(255) DEFAULT NULL,
    mother_qualification VARCHAR(255) DEFAULT NULL,
    father_qualification VARCHAR(255) DEFAULT NULL,
    guardian_qualification VARCHAR(255) DEFAULT null,
    mother_occupation VARCHAR(255) DEFAULT NULL,
    father_occupation VARCHAR(255) DEFAULT NULL,
    guardian_occupation VARCHAR(255) DEFAULT NULL,
    mother_residential_address VARCHAR(255) DEFAULT NULL,
    father_residential_address VARCHAR(255) DEFAULT NULL,
    guardian_residential_address VARCHAR(256) DEFAULT NULL,
    mother_income VARCHAR(256) DEFAULT NULL,
    father_income VARCHAR(256) DEFAULT NULL,
    guardian_income VARCHAR(256) DEFAULT NULL,
    mother_email VARCHAR(256) DEFAULT NULL,
    father_email VARCHAR(256) DEFAULT NULL,
    guardian_email VARCHAR(256) DEFAULT NULL,
    mother_mobile VARCHAR(256) DEFAULT NULL,
    father_mobile VARCHAR(256) DEFAULT NULL,
    guardian_mobile VARCHAR(256) DEFAULT NULL,
    tc_no VARCHAR(256) DEFAULT NULL,
    tc_date VARCHAR(256) DEFAULT NULL,
    scholarship_id VARCHAR(256) DEFAULT NULL,
    scholarship_password VARCHAR(256) DEFAULT NULL,
    domicile_application_no VARCHAR(256) DEFAULT NULL,
    income_application_no VARCHAR(256) DEFAULT NULL,
    caste_application_no VARCHAR(256) DEFAULT NULL,
    dob_application_no VARCHAR(256) DEFAULT NULL,
    mother_aadhar_no VARCHAR(256) DEFAULT NULL,
    father_aadhar_no VARCHAR(256) DEFAULT NULL,
    guardian_aadhar_no VARCHAR(256) DEFAULT NULL,
    height VARCHAR(256) DEFAULT NULL,
    weight VARCHAR(256) DEFAULT NULL,
    bank_name VARCHAR(255) DEFAULT NULL,
    branch_name VARCHAR(255) DEFAULT NULL,
    bank_account_no VARCHAR(255) DEFAULT NULL,
    bank_ifsc VARCHAR(255) DEFAULT NULL,
    pan_no VARCHAR(256) DEFAULT NULL,
    reference VARCHAR(256) DEFAULT NULL,
    govt_student_id VARCHAR(255) DEFAULT NULL,
    govt_family_id VARCHAR(255) DEFAULT NULL,
    dropout BOOLEAN DEFAULT NULL,
    dropout_reason VARCHAR(256) DEFAULT NULL,
    dropout_date DATE DEFAULT NULL,
    previous_qualification VARCHAR(100) DEFAULT NULL,
    previous_pass_year VARCHAR(256) DEFAULT NULL,
    previous_roll_no VARCHAR(256) DEFAULT NULL,
    previous_obt_marks VARCHAR(256) DEFAULT NULL,
    previous_percentage VARCHAR(256) DEFAULT NULL,
    previous_subjects VARCHAR(256) DEFAULT NULL,
    previous_school_name VARCHAR(256) DEFAULT NULL
);
/*script for parent_details*/
CREATE TABLE parent_details (
    parent_id SERIAL PRIMARY KEY,
    uuid VARCHAR(255) DEFAULT NULL,
    school_id int not null,
    first_name VARCHAR(255) DEFAULT NULL,
    last_name VARCHAR(255) DEFAULT NULL,
    dob TIMESTAMP DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL,
    emergency_phone_number VARCHAR(255) DEFAULT NULL,
    whatsapp_no VARCHAR(255) DEFAULT NULL,
    email_address VARCHAR(255) DEFAULT NULL,
    gender VARCHAR(255) DEFAULT NULL,
    parent_type VARCHAR(255) NOT NULL,
    qualification VARCHAR(255) DEFAULT NULL,
    aadhar_number VARCHAR(255) NOT NULL,
    company_name VARCHAR(255) DEFAULT NULL,
    designation VARCHAR(255) DEFAULT NULL,
    company_address VARCHAR(255) DEFAULT NULL,
    company_phone VARCHAR(255) DEFAULT NULL,
    address VARCHAR(255) DEFAULT NULL,
    city VARCHAR(255) DEFAULT NULL,
    state VARCHAR(255) DEFAULT NULL,
    zipcode int default null,
    updated_by int DEFAULT NULL,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT NULL
);
/*script for staff_attendance*/
CREATE TABLE staff_attendance (
    sa_id SERIAL PRIMARY KEY,
    staff_id INT NOT NULL,
    staff_type VARCHAR(255) NOT NULL,
    designation_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    attendance_status VARCHAR(255) NOT NULL
);

CREATE TABLE exam_type(
 exam_type_id SERIAL PRIMARY KEY,
 name VARCHAR(50) UNIQUE NOT NULL,         -- Changed from exam_type to name
 session_id int NOT NULL,
 description TEXT,                         -- Changed to TEXT for longer descriptions
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mst_grade (
    grade_id SERIAL PRIMARY KEY,
    grade_name VARCHAR(10) NOT NULL,  -- e.g., 'A+', 'B', 'F'
    min_percentage DECIMAL(5,2) NOT NULL,
    max_percentage DECIMAL(5,2) NOT NULL,
    description VARCHAR(255)
);

-- Updated Attendance Tables
CREATE TABLE attendance_session (
    attendance_session_id SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES mst_session(session_id),
    class_id INT NOT NULL,
    section_id INT NOT NULL,
    subject_id INT,
    teacher_id INT NOT NULL,
    session_date DATE NOT NULL,
    session_type VARCHAR(7) NOT NULL CHECK (session_type IN ('DAILY', 'SUBJECT')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (session_id, class_id, section_id, session_date, subject_id)
);

CREATE TABLE attendance_record (
    record_id SERIAL PRIMARY KEY,
    attendance_session_id INT NOT NULL REFERENCES attendance_session(attendance_session_id),
    student_id INT NOT NULL,
    marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (attendance_session_id, student_id)
)

-- Example grade data
INSERT INTO mst_grade (grade_name, min_percentage, max_percentage) VALUES
('A+', 90, 100),
('A', 80, 89.99),
('B+', 70, 79.99),
('B', 60, 69.99),
('C', 50, 59.99),
('F', 0, 49.99);


CREATE TABLE exams (
    exam_id SERIAL PRIMARY KEY,
    exam_type_id int NOT NULL,
    session_id int NOT NULL ,
    class_id INT NOT NULL,
    section_id INT default null,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL CHECK (end_date >= start_date),
    status VARCHAR(20) CHECK (status IN ('Scheduled', 'Ongoing', 'Completed')) DEFAULT 'Scheduled',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT null
);

CREATE TABLE exam_subjects (
    exam_subject_id SERIAL PRIMARY KEY,
    exam_id INT NOT NULL,
    subject_id INT NOT NULL,

    -- Component Max Marks (NULL allowed for subjects without certain components)
    theory_max_marks INT CHECK (theory_max_marks >= 0),
    practical_max_marks INT CHECK (practical_max_marks >= 0),
    viva_max_marks INT CHECK (viva_max_marks >= 0),
    total_max_marks Int DEFAUlT null,

    -- Total Passing Marks (Must be ≤ sum of all components)
    passing_marks INT NOT NULL ,
    -- Exam Timing
    exam_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL CHECK (end_time > start_time),

    -- Constraints
    CHECK ( -- At least one component must exist
        theory_max_marks IS NOT NULL OR
        practical_max_marks IS NOT NULL OR
        viva_max_marks IS NOT NULL
    ),
    UNIQUE(exam_id, subject_id) -- No duplicate subjects per exam
);

CREATE TABLE exam_results (
    result_id SERIAL PRIMARY KEY,
    student_id INT NOT null,
    exam_subject_id INT NOT NULL,

    -- Component Marks (NULL allowed for ungraded components)
    theory_marks INT DEFAULT null,
    practical_marks INT DEFAULT null,
    viva_marks INT DEFAULT null
    ,
    -- Auto-Calculated Total Marks
    total_marks INT GENERATED ALWAYS AS (
        COALESCE(theory_marks, 0) +
        COALESCE(practical_marks, 0) +
        COALESCE(viva_marks, 0)
    ) STORED,

    -- Unique Constraint
    UNIQUE(student_id, exam_subject_id)
);

/* Script for transfer_certificates */
CREATE TABLE transfer_certificates (
    tc_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    issued_by INT NOT NULL,

    -- Student Details
    admission_no VARCHAR(50) NOT NULL UNIQUE,
    student_name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    dob_in_words VARCHAR(255) NOT NULL,
    proof_of_dob VARCHAR(50) NOT NULL CHECK (proof_of_dob IN ('Birth Certificate', 'Aadhaar Card', 'Passport')),

    -- Parent Details
    father_name VARCHAR(150) NOT NULL,
    mother_name VARCHAR(150) NOT NULL,
    guardian_name VARCHAR(150),  -- Optional

    -- Academic Details
    class_at_leaving VARCHAR(50) NOT NULL,
    section_at_leaving VARCHAR(10),
    last_exam_passed VARCHAR(100),
    last_class_studied VARCHAR(50) NOT NULL,
    total_working_days INT NOT NULL CHECK (total_working_days >= 0),
    total_presence INT NOT NULL CHECK (total_presence >= 0 AND total_presence <= total_working_days),
    subjects_studied TEXT NOT NULL,

    -- Category & Concessions
    category VARCHAR(50) NOT NULL CHECK (category IN ('SC', 'ST', 'OBC', 'General')),
    fee_concession VARCHAR(255),

    -- Extracurricular & Activities
    extracurricular VARCHAR(255),
    school_category VARCHAR(50) NOT NULL CHECK (school_category IN ('Govt', 'Minority', 'Independent')),
    games_activities TEXT,

    -- TC Information
    date_of_first_admission DATE NOT NULL,
    date_of_application DATE NOT NULL,
    date_struck_off DATE NOT NULL,
    date_of_issue DATE NOT NULL DEFAULT CURRENT_DATE,
    reason_for_leaving VARCHAR(255) NOT NULL,
    fee_dues_status VARCHAR(50) CHECK (fee_dues_status IN ('Cleared', 'Pending')) NOT NULL,
    remarks TEXT,

    -- JSON Data (For Extra Information)
    tc_data JSONB
);

/* Script for transfer_certificates_alumni */
CREATE TABLE transfer_certificates_alumni (
    tc_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
	school_code VARCHAR(255) DEFAULT NULL,
	school_category VARCHAR(50) NOT NULL CHECK (school_category IN ('Govt', 'Minority', 'Independent')),
    session_id INT DEFAULT NULL,
	academic_session VARCHAR(255) DEFAULT NULL,
    issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    issued_by INT NOT NULL,

    -- Student Details
	student_id BIGINT DEFAULT NULL,
    admission_no VARCHAR(50) NOT NULL UNIQUE,
    student_name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    dob_in_words VARCHAR(255) NOT NULL,
    proof_of_dob VARCHAR(50) NOT NULL CHECK (proof_of_dob IN ('Birth Certificate', 'Aadhaar Card', 'Passport')),
    pen_no VARCHAR(255) DEFAULT NULL,
    apaar_id VARCHAR(255) DEFAULT NULL,

    -- Parent Details
    father_name VARCHAR(150) NOT NULL,
    mother_name VARCHAR(150) NOT NULL,

    -- Academic Details
	is_student_failed VARCHAR(50) DEFAULT NULL,
	subjects_offered TEXT NOT NULL,
	is_promoted_to_next_class VARCHAR(255) DEFAULT NULL,
	no_of_meetings INT DEFAULT NULL,
	total_present INT DEFAULT NULL,

    class_at_leaving VARCHAR(50) DEFAULT NULL,
    section_at_leaving VARCHAR(10),
	last_annual_exam_result VARCHAR(255) DEFAULT NULL,
	last_studied_class VARCHAR(50) DEFAULT NULL,


    -- Category & Concessions
    category VARCHAR(50) NOT NULL CHECK (category IN ('SC', 'ST', 'OBC', 'General')),
    fee_due_status VARCHAR(50) CHECK (fee_due_status IN ('Cleared', 'Pending')) NOT NULL,

    -- Extracurricular & Activities
    extracurricular VARCHAR(255),
    games_activities TEXT,

    -- TC Information
	general_conduct VARCHAR(255) DEFAULT NULL,
	reason_for_leaving VARCHAR(255) NOT NULL,
	date_of_issue DATE NOT NULL DEFAULT CURRENT_DATE,
	date_struck_off DATE DEFAULT NULL,
    remarks TEXT,
    sr_number VARCHAR(255) DEFAULT NULL,

    -- JSON Data (For Extra Information)
    tc_data JSONB
);

/*student attendance*/
-- Updated Attendance Tables
CREATE TABLE attendance_session (
    attendance_session_id SERIAL PRIMARY KEY,
    session_id INT NOT NULL,
    class_id INT NOT NULL,
    section_id INT NOT NULL,
    subject_id INT,
    teacher_id INT NOT NULL,
    session_date DATE NOT NULL,
    session_type VARCHAR(7) NOT NULL CHECK (session_type IN ('DAILY', 'SUBJECT')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- For DAILY sessions (subject_id must be NULL)
CREATE UNIQUE INDEX unique_daily_attendance
ON attendance_session (session_id, class_id, section_id, session_date)
WHERE session_type = 'DAILY' AND subject_id IS NULL;

-- For SUBJECT sessions (subject_id must NOT NULL)
CREATE UNIQUE INDEX unique_subject_attendance
ON attendance_session (session_id, class_id, section_id, subject_id, session_date)
WHERE session_type = 'SUBJECT' AND subject_id IS NOT NULL;



CREATE TABLE attendance_record (
    record_id SERIAL PRIMARY KEY,
    attendance_session_id INT NOT NULL REFERENCES attendance_session(attendance_session_id),
    student_id INT NOT NULL,
    marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (attendance_session_id, student_id)
);

CREATE TABLE timetable (
    timetable_id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    session_id INT NOT NULL,
    class_id INT NOT NULL,
    section_id INT NOT NULL,
    subject_id INT NOT NULL,
    teacher_id INT NOT NULL,
    day_of_week VARCHAR(10) NOT NULL, -- e.g., Monday, Tuesday, etc.
    period_number INT NOT NULL, -- e.g., 1 for first period, 2 for second period, etc.
    start_time TIME NOT NULL, -- Start time of the period
    end_time TIME NOT NULL, -- End time of the period
    room_number VARCHAR(50) DEFAULT NULL, -- Optional: Room number where the class is held
    CONSTRAINT fk_class_subject_allocation FOREIGN KEY (class_id, section_id, subject_id)
        REFERENCES class_subject_allocation(class_id, section_id, subject_id),
    CONSTRAINT fk_class_subject_teacher_allocation FOREIGN KEY (teacher_id)
        REFERENCES staff(staff_id),
    CONSTRAINT unique_timetable_entry UNIQUE (school_id, session_id, class_id, section_id, day_of_week, period_number)
);



/*DDL for TABLES AND SEQUENCE*/
CREATE OR REPLACE FUNCTION medhapro.exec_ddl(schema_name TEXT)
RETURNS TEXT AS $$
DECLARE
    ddl_statement TEXT;
    sequence_t TEXT ;
BEGIN
    -- Create the schema dynamically if it does not exist
    ddl_statement := 'CREATE SCHEMA IF NOT EXISTS ' || quote_ident(schema_name);
    EXECUTE ddl_statement;

    -- Create the school_registration table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.school_registration (
        user_id serial PRIMARY KEY,
        first_name varchar(255) NOT NULL,
        last_name varchar(255) NOT NULL,
        phone_number varchar(255) NOT NULL,
        email varchar(255) NOT NULL UNIQUE,
        password varchar(255) NOT NULL,
        school_code varchar(255) NOT null,
        role_id INT[] DEFAULT NULL,
        created_date DATE DEFAULT CURRENT_DATE,
        is_active BOOLEAN
    )';
    EXECUTE ddl_statement;

    -- Create the role table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.role (
         id serial PRIMARY KEY,
         role varchar(50) NOT NULL
    )';
    EXECUTE ddl_statement;

    -- Create the refresh_token table
--    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.refresh_token (
--        token_id SERIAL PRIMARY KEY,
--        email VARCHAR(255) NOT NULL,
--        refresh_token VARCHAR(255) NOT NULL UNIQUE,
--        expiry_time TIMESTAMP   NOT NULL,
--        CONSTRAINT fk_user_email FOREIGN KEY (email) REFERENCES school_registration(email) ON DELETE CASCADE
--    )';
--    EXECUTE ddl_statement;
      ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.refresh_token (
          token_id SERIAL PRIMARY KEY,
          email VARCHAR(255) NOT NULL,
          refresh_token VARCHAR(255) NOT NULL UNIQUE,
          expiry_time TIMESTAMP   NOT NULL,
          CONSTRAINT fk_user_email FOREIGN KEY (email) REFERENCES ' || quote_ident(schema_name) || '.school_registration(email) ON DELETE CASCADE
      )';
      EXECUTE ddl_statement;

    -- Create the student_personal_details table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.student_personal_details (
        id SERIAL PRIMARY KEY,
        student_id int not null,
        school_id int not null,
        uu_id VARCHAR(255) DEFAULT NULL,
        parent_id INT[] DEFAULT NULL,
        father_name VARCHAR(255) DEFAULT NULL,
        father_occupation VARCHAR(255) DEFAULT NULL,
        mother_name VARCHAR(255) DEFAULT NULL,
        mother_occupation VARCHAR(255) DEFAULT NULL,
        first_name VARCHAR(255) DEFAULT NULL,
        last_name VARCHAR(255) DEFAULT NULL,
        blood_group VARCHAR(255) DEFAULT NULL,
        gender VARCHAR(255) DEFAULT NULL,
        height VARCHAR(255) DEFAULT NULL,
        weight VARCHAR(255) DEFAULT NULL,
        aadhar_number VARCHAR(255) DEFAULT NULL,
        phone_number VARCHAR(255) DEFAULT NULL,
        emergency_phone_number VARCHAR(255) DEFAULT NULL,
        whatsapp_no VARCHAR(255) DEFAULT NULL,
        email_address VARCHAR(255) DEFAULT NULL,
        dob TIMESTAMP DEFAULT NULL,
        dob_cirtificate_no VARCHAR(255) DEFAULT NULL,
        income_app_no VARCHAR(255) DEFAULT NULL,
        caste_app_no VARCHAR(255) DEFAULT NULL,
        domicile_app_no VARCHAR(255) DEFAULT NULL,
        govt_student_id_on_portal VARCHAR(255) DEFAULT NULL,
        govt_family_id_on_portal VARCHAR(255) DEFAULT NULL,
        bank_name VARCHAR(255) DEFAULT NULL,
        branch_name VARCHAR(255) DEFAULT NULL,
        ifsc_code VARCHAR(255) DEFAULT NULL,
        account_no VARCHAR(255) DEFAULT NULL,
        pan_no VARCHAR(255) DEFAULT NULL,
        religion VARCHAR(255) DEFAULT NULL,
        nationality VARCHAR(255) DEFAULT NULL,
        category VARCHAR(255) DEFAULT NULL,
        caste VARCHAR(255) DEFAULT NULL,
        current_address VARCHAR(255) DEFAULT NULL,
        current_city VARCHAR(255) DEFAULT NULL,
        current_state VARCHAR(255) DEFAULT NULL,
        current_zipcode int default null,
        permanent_address VARCHAR(255) DEFAULT NULL,
        permanent_city VARCHAR(255) DEFAULT NULL,
        permanent_state VARCHAR(255) DEFAULT NULL,
        permanent_zipcode int default null,
        student_country VARCHAR(255) DEFAULT NULL,
        current_status VARCHAR(255) DEFAULT NULL,
        current_status_comment VARCHAR(255) DEFAULT NULL,
        updated_by int DEFAULT NULL,
        updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        validity_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        validity_end_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        student_photo VARCHAR(255) DEFAULT NULL,
        deleted BOOLEAN DEFAULT NULL,
        CONSTRAINT uk_student_personal_student_id UNIQUE (student_id)
    )';
    EXECUTE ddl_statement;

    -- Create the student_academic_details table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.student_academic_details (
       id SERIAL PRIMARY KEY,
       student_id int not null,
       school_id INT NOT NULL,
       uu_id VARCHAR(255) DEFAULT NULL,
       apaar_id VARCHAR(255) DEFAULT NULL,
       pen_no VARCHAR(255) DEFAULT NULL,
       admission_no VARCHAR(255) DEFAULT NULL,
       admission_date TIMESTAMP DEFAULT NULL,
       registration_number VARCHAR(255) DEFAULT NULL,
       roll_number VARCHAR(255) DEFAULT NULL,
       session_id int not null,
       student_class_id int not null,
       student_section_id int not null,
       stream VARCHAR(255) DEFAULT NULL,
       education_medium VARCHAR(255) DEFAULT NULL,
       referred_by VARCHAR(255) DEFAULT NULL,
       is_rte_student BOOLEAN,
       rte_application_no VARCHAR(255) DEFAULT NULL,
       enrolled_session VARCHAR(255) DEFAULT NULL,
       enrolled_class VARCHAR(255) DEFAULT NULL,
       enrolled_year VARCHAR(255) DEFAULT NULL,
       transfer_cirti_no VARCHAR(255) DEFAULT NULL,
       date_of_issue TIMESTAMP DEFAULT NULL,
       scholarship_id VARCHAR(255) DEFAULT NULL,
       scholarship_password VARCHAR(255) DEFAULT NULL,
       lst_school_name VARCHAR(255) DEFAULT NULL,
       lst_school_addrs VARCHAR(255) DEFAULT NULL,
       lst_attended_class VARCHAR(255) DEFAULT NULL,
       lst_scl_aff_to VARCHAR(255) DEFAULT NULL,
       lst_session VARCHAR(255) DEFAULT NULL,
       is_dropout BOOLEAN,
       dropout_date TIMESTAMP DEFAULT NULL,
       dropout_reason VARCHAR(255) DEFAULT NULL,
       student_addmission_type VARCHAR(255) DEFAULT NULL,
       session_status VARCHAR(255) DEFAULT NULL,
       session_status_comment VARCHAR(255) DEFAULT NULL,
       previous_qualifications JSONB DEFAULT ''[]'',
       updated_by int DEFAULT NULL,
       updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       validity_start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       validity_end_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       student_type VARCHAR(255) DEFAULT NULL,
       CONSTRAINT uk_student_session UNIQUE (student_id, session_id)
    )';
    EXECUTE ddl_statement;

    -- Create the accountant table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.accountant (
        id SERIAL PRIMARY KEY,
        firstName VARCHAR(255) DEFAULT NULL,
        LastName VARCHAR(255) DEFAULT NULL,
        email VARCHAR(255) DEFAULT NULL,
        phoneNumber varchar(12) DEFAULT NULL
    )';
    EXECUTE ddl_statement;

    -- Create the class_attendance table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.class_attendance (
        ca_id serial PRIMARY KEY,
        class_id int NOT NULL,
        section_id int NOT NULL,
        subject_id int NOT NULL,
        teacher_id int DEFAULT NULL,
        present_date DATE DEFAULT NULL,
        attendance_mark_time TIMESTAMP DEFAULT NULL
    )';
    EXECUTE ddl_statement;

    -- Create the absent_details table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.absent_details (
        ad_id SERIAL PRIMARY KEY,
        ca_id INT NOT NULL,
        student_id INT NOT NULL,
        absent VARCHAR(255) DEFAULT NULL,
        attendance_date DATE NOT NULL
    )';
    EXECUTE ddl_statement;

    -- Create the session table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.session (
        session_id SERIAL PRIMARY KEY,
        school_id int not null,
        academic_session VARCHAR(255) NOT NULL,
        start_date DATE,
        end_date DATE
    )';
    EXECUTE ddl_statement;

    -- Create the school_details table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.school_details (
        school_id serial NOT null check (school_id =1 ),
        school_code VARCHAR(255) DEFAULT NULL,
        school_affiliation_no VARCHAR(255) DEFAULT NULL,
        school_name VARCHAR(255) DEFAULT NULL,
        school_building VARCHAR(255) DEFAULT NULL,
        school_address VARCHAR(255) DEFAULT NULL,
        email_address VARCHAR(255) DEFAULT NULL,
        school_city VARCHAR(255) DEFAULT NULL,
        school_state VARCHAR(255) DEFAULT NULL,
        school_country VARCHAR(255) DEFAULT NULL,
        phone_number VARCHAR(255) DEFAULT NULL,
        bank_details VARCHAR(255) DEFAULT NULL,
        branch_name VARCHAR(255) DEFAULT NULL,
        account_number VARCHAR(255) DEFAULT NULL,
        ifsc_code VARCHAR(255) DEFAULT NULL,
        alternate_phone_number VARCHAR(255) DEFAULT NULL,
        school_zipcode VARCHAR(255) DEFAULT NULL,
        school_photo VARCHAR(255) DEFAULT NULL
    )';
    EXECUTE ddl_statement;

    -- Create the mst_class table with auto-insertion of classes and hardcoded school_id=1
        ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.mst_class (
            class_id SERIAL PRIMARY KEY,
            school_id int not null DEFAULT 1,
            class_name VARCHAR(255) DEFAULT NULL,
            CONSTRAINT unique_class UNIQUE (school_id, class_name)
        )';
        EXECUTE ddl_statement;

        -- Insert classes automatically with school_id = 1
        BEGIN
            EXECUTE 'INSERT INTO ' || quote_ident(schema_name) || '.mst_class (school_id, class_name) VALUES
                (1, ''Playway''),
                (1, ''Nursery''),
                (1, ''L.K.G.''),
                (1, ''U.K.G.''),
                (1, ''1st''),
                (1, ''2nd''),
                (1, ''3rd''),
                (1, ''4th''),
                (1, ''5th''),
                (1, ''6th''),
                (1, ''7th''),
                (1, ''8th''),
                (1, ''9th''),
                (1, ''10th''),
                (1, ''11th''),
                (1, ''12th'')
            ON CONFLICT (school_id, class_name) DO NOTHING';
        EXCEPTION
            WHEN OTHERS THEN
                -- Handle any errors silently (table might not exist yet or other issues)
                NULL;
        END;

    -- Create the mst_section table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.mst_section (
        section_id SERIAL PRIMARY KEY,
        school_id int not null,
        section_name VARCHAR(255) DEFAULT NULL,
        CONSTRAINT unique_section UNIQUE (school_id, section_name)
    )';
    EXECUTE ddl_statement;
    -- Create the class_and_section table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.class_and_section (
       class_section_id SERIAL PRIMARY KEY,
       school_id int not null,
       class_id INT NOT NULL,
       section_id INT NOT NULL,
       CONSTRAINT unique_class_section UNIQUE (school_id, class_id, section_id)
    )';
    EXECUTE ddl_statement;
    -- Create the staff_designation table
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.staff_designation (
       sd_id SERIAL PRIMARY KEY,
       school_id int not null,
       designation VARCHAR(255),
       description VARCHAR(255),
       status VARCHAR(255),
       department_id int not null
    )';

        EXECUTE ddl_statement;
        -- Create the staff_department table
        ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.staff_department (
           stdp_id SERIAL PRIMARY KEY,
           school_id int not null,
           department VARCHAR(255),
           description VARCHAR(255),
           status VARCHAR(255)
        )';
    EXECUTE ddl_statement;
     -- Create the holiday table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.holiday (
        id serial primary key,
        school_id int,
        holiday_title VARCHAR(255) NOT NULL,
        holiday_date Date NOT NULL,
        holiday_description VARCHAR(255) NOT NULL,
        session_id int not null
     )';
     EXECUTE ddl_statement;
     -- Create the bank_details table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.add_bank_details (
         bd_id SERIAL PRIMARY KEY,
         school_id INT NOT NULL,
         staff_id INT not null,
         staff_name VARCHAR(255) NOT NULL,
         phone_number VARCHAR(255),
         bank_name VARCHAR(255),
         account_number VARCHAR(255) not null,
         ifsc_code VARCHAR(255),
         permanent_account_number VARCHAR(255),
         branch_name VARCHAR(255),
         bank_address VARCHAR(255),
         updated_by INT DEFAULT NULL,
         update_date_time TIMESTAMP NOT NULL,
         deleted BOOLEAN DEFAULT FALSE
     )';
     EXECUTE ddl_statement;
--     -- Create the subject table
--      ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.mst_subject (
--      subject_id SERIAL PRIMARY KEY,
--      school_id INT NOT NULL,
--      subject_name VARCHAR(255) DEFAULT NULL,
--      CONSTRAINT unique_subject_' || quote_ident(schema_name) || '_subject UNIQUE (school_id, subject_name)
--      )';
--      EXECUTE ddl_statement;
-- Create the subject table with auto-insertion of subjects and hardcoded school_id=1
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.mst_subject (
      subject_id SERIAL PRIMARY KEY,
      school_id INT NOT NULL DEFAULT 1,
      subject_name VARCHAR(255) DEFAULT NULL,
      CONSTRAINT unique_subject_' || quote_ident(schema_name) || '_subject UNIQUE (school_id, subject_name)
      )';
      EXECUTE ddl_statement;

      -- Insert subjects automatically with school_id = 1
      BEGIN
          EXECUTE 'INSERT INTO ' || quote_ident(schema_name) || '.mst_subject (school_id, subject_name) VALUES
              (1, ''Hindi''),
              (1, ''English''),
              (1, ''Sanskrit''),
              (1, ''Maths''),
              (1, ''Science''),
              (1, ''Home Science''),
              (1, ''Social Science''),
              (1, ''EVS''),
              (1, ''Physical Education''),
              (1, ''Music''),
              (1, ''Commerce''),
              (1, ''Art''),
              (1, ''Painting''),
              (1, ''Computer''),
              (1, ''Computer Studies''),
              (1, ''Technical Drawing Applications''),
              (1, ''Sports''),
              (1, ''Agriculture''),
              (1, ''Sociology''),
              (1, ''Economics''),
              (1, ''Physics''),
              (1, ''Chemistry''),
              (1, ''Biology''),
              (1, ''Accountancy''),
              (1, ''Business Studies''),
              (1, ''Geography''),
              (1, ''History''),
              (1, ''Political Science''),
              (1, ''Civics''),
              (1, ''Humanities''),
              (1, ''Psychology''),
              (1, ''Legal Studies''),
              (1, ''N.C.C.'')
          ON CONFLICT (school_id, subject_name) DO NOTHING';
      EXCEPTION
          WHEN OTHERS THEN
              -- Handle any errors silently (table might not exist yet or other issues)
              NULL;
      END;
     -- Create the master_sequence_controller table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.master_sequence_controller (
         id serial PRIMARY KEY,
         school_id INT DEFAULT 1,
         seq_code INT NOT NULL,
         current_value INT NOT NULL
     )';
     EXECUTE ddl_statement;
     -- Insert first value in master_sequence_controller
     EXECUTE 'INSERT INTO ' || quote_ident(schema_name) || '.master_sequence_controller (school_id, seq_code, current_value)
                       VALUES (1, 0,0)';
     -- Create the staff table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.staff (
         staff_id SERIAL PRIMARY KEY,
         school_id INT NOT NULL,
         session_id int not null,
         first_name VARCHAR(255) default null,
         last_name VARCHAR(255) default null,
         registration_number VARCHAR(255) default null,
         joining_date TIMESTAMP not null,
         department_id int not null,
         designation_id int not null,
         employment_type VARCHAR(255) NOT NULL,
         father_name  VARCHAR(255) default null,
         blood_group  VARCHAR(255) default null,
         gender  VARCHAR(255) default null,
         aadhar_number  VARCHAR(255) default null,
         highest_qualification VARCHAR(255) default null,
         pf_account_no VARCHAR(255) DEFAULT NULL,
         experience VARCHAR(255) DEFAULT NULL,
         experience_details TEXT DEFAULT NULL,
         phone_number VARCHAR(255) default null,
         email_address VARCHAR(255) default null,
         dob TIMESTAMP DEFAULT NULL,
         religion VARCHAR(255) default null,
         emergency_phone_number VARCHAR(255) default null,
         emergency_contact_name VARCHAR(255) default null,
         current_address VARCHAR(255) default null,
         current_zipcode int default null,
         current_city VARCHAR(255) default null,
         current_state VARCHAR(255) default null,
         permanent_address VARCHAR(255) default null,
         permanent_zipcode int default null,
         permanent_city VARCHAR(255) default null,
         permanent_state VARCHAR(255) default null ,
         staff_country VARCHAR(255) default null,
         current_status VARCHAR(255) DEFAULT NULL,
         current_status_comment VARCHAR(255) DEFAULT NULL,
         staff_photo VARCHAR(255) default null,
         deleted BOOLEAN DEFAULT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the mst_frequency table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.mst_frequency (
         frequency_id SERIAL PRIMARY KEY,
         school_id int not null,
         frequency_type VARCHAR(255) DEFAULT NULL,
         deleted boolean default null
     )';
     EXECUTE ddl_statement;
      -- Create the school_fees table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.school_fees (
        fee_id SERIAL PRIMARY KEY ,
        school_id int not null,
        fee_type VARCHAR(255) DEFAULT NULL,
        frequency_id int not null,
        deleted boolean default null
     )';
     EXECUTE ddl_statement;
     -- Create the fee_assignment table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.fee_assignment (
        fa_id serial PRIMARY KEY,
        school_id int NOT NULL,
        session_id int NOT NULL,
        class_id int DEFAULT NULL,
        section_id int DEFAULT NULL,
        student_id int DEFAULT NULL,
        fee_id int NOT NULL,
        dc_id int DEFAULT NULL,
        valid_from date DEFAULT CURRENT_DATE NULL,
        valid_to date NULL,
        updated_by int DEFAULT NULL,
        update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
     )';
     EXECUTE ddl_statement;
     -- Create the fee_assignment_exclusion table
      ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.fee_assignment_exclusion (
        exclusion_id serial NOT NULL,
        fa_id int NOT NULL,
        student_id int NOT NULL,
        valid_from date DEFAULT CURRENT_DATE NULL,
        valid_to date NULL,
        reason text NULL,
        created_by int NULL,
        created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL
      )';
      EXECUTE ddl_statement;

    -- Create the discount_code_dc_id_seq sequence if it doesn't exist
     sequence_t := 'CREATE SEQUENCE IF NOT EXISTS ' || quote_ident(schema_name) || '.discount_code_dc_id_seq START WITH 0 MINVALUE 0';
     EXECUTE sequence_t;


    -- Create the discount_code table if it doesn't exist
    ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.discount_code (
    dc_id INTEGER NOT NULL DEFAULT nextval(''' || quote_ident(schema_name) || '.discount_code_dc_id_seq''),
    dc_description VARCHAR(255) NOT NULL,
    dc_rate INT NOT NULL,
    dc_rate_type VARCHAR(255) NOT NULL,
    additional_info VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (dc_id),
    CONSTRAINT uk_dc_description UNIQUE (dc_description)
   )';
   EXECUTE ddl_statement;

     -- Create the fee_due_date table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.fee_due_date (
        fddt_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        fa_id INT NOT NULL,
        due_date DATE NOT NULL,
        fee_amount INT NOT NULL DEFAULT 0,
        discount_amount INT NOT NULL DEFAULT 0,
        updated_by INT default NULL,
        update_date_time TIMESTAMP NOT null
     )';
     EXECUTE ddl_statement;
     -- Create the class_teacher_allocation table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.class_teacher_allocation (
        cta_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        class_id INT NOT NULL,
        section_id INT NOT NULL,
        staff_id INT NOT NULL,
        updated_by INT DEFAULT NULL,
        update_date_time TIMESTAMP NOT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the fee_deposit table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.fee_deposit (
        fd_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT not null,
        class_id INT not null,
        section_id INT not null,
        student_id int not null,
        payment_mode int not null,
        total_amount_paid int not null,
        payment_received_by int default null,
        system_date_time TIMESTAMP not null,
        payment_description VARCHAR(255) default null,
        transaction_id VARCHAR(255) default null,
        comment  VARCHAR(255) DEFAULT NULL,
        fdd_status VARCHAR(255) DEFAULT NULL,
        payment_date date NOT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the payment_mode table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.payment_mode (
        pm_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        payment_type VARCHAR(255) DEFAULT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the payment_mode table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.penalty (
         penalty_id SERIAL PRIMARY KEY,
         penalty_amount INT DEFAULT NULL,
         penalty_type VARCHAR(255) NOT NULL,
         penalty_percentage VARCHAR(255) DEFAULT NULL,
         description TEXT DEFAULT NULL,
         system_date_time TIMESTAMP NOT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the fee_deposit_details table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.fee_deposit_details (
        fdd_id SERIAL PRIMARY KEY,
        fd_id INT default NULL,
        fa_id INT not NULL,
        fddt_id INT not NULL,
        amount_paid DECIMAL(10, 0) not null,
        dc_id INT not null,
        discount_amount DECIMAL(10, 0) not null,
        penalty_id int DEFAULT NULL,
        penalty_amount DECIMAL(10, 0) DEFAULT NULL,
        balance DECIMAL(10, 0) NOT NULL,
        approved_by int default null,
        payment_received_by int not null,
        system_date_time TIMESTAMP not null,
        status VARCHAR(255) NOT NULL,
        comment  VARCHAR(255) DEFAULT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the transport_fee_due table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.transport_fee_due (
        tfdue_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        student_transport_id INT NOT NULL,
        due_month DATE NOT NULL,
        fee_amount NUMERIC(10,2) NOT NULL,
        discount_amount NUMERIC(10,2) DEFAULT 0,
        penalty_amount NUMERIC(10,2) DEFAULT 0
      )';
      EXECUTE ddl_statement;
      -- Create the transport_fee_deposit table
       ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.transport_fee_deposit (
          tfd_id SERIAL PRIMARY KEY,
          school_id INT NOT NULL,
          session_id INT NOT NULL,
          class_id INT not null,
          section_id INT not null,
          student_id INT NOT NULL,
          route_id INT NOT NULL,
          payment_mode INT NOT NULL,
          total_amount_paid NUMERIC(10,2) NOT NULL,
          payment_received_by INT DEFAULT NULL,
          payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
          transaction_id VARCHAR(255) DEFAULT NULL,
          payment_description VARCHAR(255) DEFAULT NULL,
          status VARCHAR(255) DEFAULT NULL
        )';
        EXECUTE ddl_statement;
        -- Create the transport_fee_deposit_details table
         ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.transport_fee_deposit_details (
            tfdd_id SERIAL PRIMARY KEY,
            tfd_id INT NOT NULL, -- FK to transport_fee_deposit
            student_transport_id INT NOT NULL,
            due_month DATE NOT NULL, -- e.g. 2026-04-01 (month basis)
            fee_amount NUMERIC(10,2) NOT NULL,
            discount_amount NUMERIC(10,2) DEFAULT 0,
            penalty_amount NUMERIC(10,2) DEFAULT 0,
            amount_paid NUMERIC(10,2) NOT NULL,
            balance NUMERIC(10,2) DEFAULT 0,
            status VARCHAR(255) NOT NULL,
            system_date_time TIMESTAMP NOT NULL
          )';
          EXECUTE ddl_statement;
    -- Create the class_subject_allocation table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.class_subject_allocation (
        csa_id serial primary key,
        school_id int not null,
        session_id int not null,
        class_id int not null,
        section_id int not null,
        subject_id int not null,
        unique(class_id,section_id,subject_id)
     )';
     EXECUTE ddl_statement;
    -- Create the class_subject_teacher_allocation table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.class_subject_teacher_allocation (
        csta_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        class_id INT NOT NULL,
        section_id INT NOT NULL,
        subject_id INT NOT NULL,
        teacher_id INT NOT NULL
     )';
     EXECUTE ddl_statement;
    -- Create the home_work table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.home_work (
        hw_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        class_id INT NOT NULL,
        section_id INT NOT NULL,
        subject_id INT NOT NULL,
        assign_home_work_date DATE DEFAULT NULL,
        description VARCHAR(255) NOT NULL,
        home_work_pdf VARCHAR(512) DEFAULT NULL,
        updated_by INT NOT NULL,
        updated_date DATE DEFAULT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the add_vehicle table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.add_vehicle (
        vehicle_id SERIAL NOT NULL,
        school_id INT NOT NULL,
        vehicle_number VARCHAR(255) NOT NULL,
        vehicle_type VARCHAR(255) DEFAULT NULL,
        number_of_seat INT DEFAULT NULL,
        refuel_amount DOUBLE PRECISION DEFAULT NULL,
        last_insurance_date DATE DEFAULT NULL,
        renewal_insurance_date DATE DEFAULT NULL,
        last_service_date DATE DEFAULT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the add_driver table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.add_driver (
        driver_id SERIAL NOT NULL,
          school_id INT NOT NULL,
          session_id INT NOT null,
          sd_id INT NOT NULL,
          first_name VARCHAR(255) NOT NULL,
          last_name VARCHAR(255) DEFAULT NULL,
          dob DATE NOT NULL,
          contact_number VARCHAR(255) NOT NULL,
          license_number VARCHAR(255) NOT NULL,
          aadhar_number VARCHAR(255) NOT NULL,
          joining_date Date NOT NULL,
          employment_type VARCHAR(255) DEFAULT NULL,
          experience VARCHAR(255) DEFAULT NULL,
          address VARCHAR(255) default null,
          city VARCHAR(255) default null,
          state VARCHAR(255) default null,
          zip_code INT default null,
          country VARCHAR(255) default null,
          current_status VARCHAR(255) default null,
          current_status_comment VARCHAR(255),
          updated_by INT DEFAULT NULL,
          update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)';
     EXECUTE ddl_statement;
    -- Create the add_route table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.add_route (
        route_id SERIAL NOT NULL,
        school_id INT NOT NULL,
        vehicle_id int NOT NULL,
        boarding_point VARCHAR(255) NOT NULL,
        destination VARCHAR(255) NOT NULL,
        max_fee NUMERIC(10,2) NOT NULL,
        stop_name VARCHAR(255),
        stop_fare NUMERIC(10,2),
        start_date DATE NOT NULL,
        end_date DATE NOT NULL,
        hash_value VARCHAR(255) NOT NULL
     )';
     EXECUTE ddl_statement;
    -- Create the transport_allocation table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.transport_allocation (
        ta_id SERIAL NOT NULL,
        school_id INT NOT NULL,
        driver_id int NOT NULL,
        vehicle_id int NOT NULL,
        route_id int NOT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the student_transport_details
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.student_transport_details (
        student_transport_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        student_id INT NOT NULL,
        route_id INT NOT NULL,
        fee NUMERIC(10,2) NOT NULL,
        status VARCHAR(255) NOT NULL,
        start_date DATE NOT NULL,
        end_date DATE NOT NULL
      )';
         EXECUTE ddl_statement;
      -- Create the student_registration  table
        ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.student_registration  (
           std_registration_id SERIAL PRIMARY KEY,
           school_id INT NOT NULL,
           first_name VARCHAR(255) NOT NULL,
           last_name VARCHAR(255) DEFAULT NULL,
           dob DATE NOT NULL,
           blood_group VARCHAR(50) DEFAULT NULL,
           religion VARCHAR(100) DEFAULT NULL,
           aadhaar_number VARCHAR(255) DEFAULT NULL,
           student_pen_no VARCHAR(255) DEFAULT NULL,
           contact_no VARCHAR(50) NOT NULL,
           alternate_number VARCHAR(50) DEFAULT NULL,
           email VARCHAR(255) DEFAULT NULL,
           address VARCHAR(255) NOT NULL,
           city VARCHAR(100) NOT NULL,
           state VARCHAR(100) NOT NULL,
           pin_code VARCHAR(20) NOT NULL,
           father_name VARCHAR(255) NOT NULL,
           mother_name VARCHAR(255) NOT NULL,
           session_id INT NOT NULL,
           qualification VARCHAR(255) NOT NULL,
           enrolled_class VARCHAR(100) NOT NULL,
           last_institute_name VARCHAR(255) DEFAULT NULL,
           transfer_certificate_no VARCHAR(255) DEFAULT NULL,
           reference_name VARCHAR(255) DEFAULT NULL,
           reference_type VARCHAR(255) DEFAULT NULL,
           registration_date DATE NOT NULL,
           enquiry_status VARCHAR(100) DEFAULT ''PENDING'',
           comment VARCHAR(500) DEFAULT NULL,
           status VARCHAR(50) DEFAULT ''ACTIVE''
          )';

         EXECUTE ddl_statement;
     -- Create the exam_meeting table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.exam_meeting (
        em_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        title VARCHAR(255) NOT NULL,
        meeting_date Date not null,
        start_time TIME NOT NULL,
        end_time TIME NOT NULL,
        updated_by INT NOT NULL,
        update_date_Time TIMESTAMP NOT null,
        deleted boolean default null
     )';
     EXECUTE ddl_statement;
     -- Create the exam_marks_entry table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.exam_marks_entry (
        eme_id SERIAl not null,
        school_id int not null,
        class_id int not null,
        section_id int not null,
        student_id int not null,
        session_id int not null,
        exam_type VARCHAR(255) not null,
        subject_id int not null,
        maximum_marks DOUBLE PRECISION not null,
        minimum_marks DOUBLE PRECISION not null,
        obtain DOUBLE PRECISION not null,
        grade VARCHAR(255) not null,
        status VARCHAR(255) not null
     )';
     EXECUTE ddl_statement;
    -- Create the exam_schedule table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.exam_schedule (
        es_id serial primary key,
        school_id int not null,
        session_id int not null,
        class_id int not null,
        section_id int not null,
        subject_id int not null,
        start_month varchar(255) not null,
        end_month varchar(255) not null,
        exam_type varchar(255) not null,
        exam_date Date not null,
        exam_day varchar(255) not null,
        exam_timing varchar(255) not null,
        updated_by varchar(255) not null,
        update_date_time Date not null,
        deleted boolean default null
     )';
     EXECUTE ddl_statement;
     -- Create the staff_salary table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.staff_salary (
        ss_id serial PRIMARY KEY,
        school_id int NOT NULL,
        session_id int DEFAULT NULL,
        staff_id int NOT NULL,
        department_id int NOT NULL,
        designation_id int NOT NULL,
        salary_amount VARCHAR(255) NOT null,
        deleted boolean default null
     )';
     EXECUTE ddl_statement;
    -- Create the pay_salary table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.pay_salary (
        ps_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT DEFAULT NULL,
        designation_id INT NOT NULL,
        staff_id INT NOT NULL,
        salary_amount VARCHAR(255) NOT NULL,
        count_leave VARCHAR(255) NOT NULL,
        total_salary VARCHAR(255) NOT NULL,
        pay_salary_month VARCHAR(255) NOT NULL,
        pay_salary_year VARCHAR(255) NOT NULL,
        payment_date DATE NOT NULL,
        updated_by INT NOT NULL,
        update_date_time DATE NOT NULL,
        deleted BOOLEAN DEFAULT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the book_category table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.book_category (
        book_category_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        book_category_name VARCHAR(255) NOT NULL,
        book_description VARCHAR(255) DEFAULT NULL,
        updated_by INT NOT NULL,
        update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
     )';
     EXECUTE ddl_statement;
    -- Create the add_new_book table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.add_new_book (
        book_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        book_name VARCHAR(255) NOT NULL,
        book_author_name VARCHAR(255) not null,
        book_category_id INT NOT NULL,
        isbn VARCHAR(255) NOT NULL,
        price VARCHAR(255) NOT NULL,
        updated_by INT NOT NULL,
        update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
        deleted boolean default null
     )';
     EXECUTE ddl_statement;
    -- Create the book_stock table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.book_stock (
        book_stock_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        book_id INT NOT NULL,
        total_book_stock VARCHAR(255) NOT NULL,
        updated_by INT NOT NULL,
        update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
     )';
     EXECUTE ddl_statement;
    -- Create the issue_book table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.issue_book (
        issue_book_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        class_id INT NOT NULL,
        section_id INT DEFAULT NULL,
        student_id INT NOT NULL,
        book_id INT NOT NULL,
        issue_date DATE NOT NULL,
        due_date DATE NOT NULL,
        status VARCHAR(255) NOT NULL,
        updated_by INT NOT NULL,
        update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
        deleted boolean default null
     )';
     EXECUTE ddl_statement;
    -- Create the add_supplier table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.add_supplier (
        supplier_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        supplier_name VARCHAR(255) NOT NULL,
        mobile_number VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL,
        address VARCHAR(255) NOT NULL,
        gstin VARCHAR(255) NOT NULL,
        pan VARCHAR(255) NOT NULL,
        city VARCHAR(255) NOT NULL,
        state VARCHAR(255) NOT NULL,
        pin_code VARCHAR(255) NOT NULL,
        website VARCHAR(255),
        bank_name VARCHAR(255) NOT NULL,
        branch_name VARCHAR(255) NOT NULL,
        account_number VARCHAR(255) NOT NULL,
        ifsc_code VARCHAR(255) NOT NULL,
        updated_by INT NOT NULL,
        update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
        deleted boolean default null
     )';
     EXECUTE ddl_statement;
    -- Create the inventory_category table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.inventory_category (
        inventory_category_id SERIAL PRIMARY KEY,
        school_id INT NOT NULL,
        session_id INT NOT NULL,
        category_details VARCHAR(255) NOT NULL,
        category_description VARCHAR(255),
        updated_by INT NOT NULL,
        update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
     )';
     EXECUTE ddl_statement;
    -- Create the add_inventory_items table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.add_inventory_items (
       add_inventory_items_id SERIAL PRIMARY KEY,
       school_id INT NOT NULL,
       session_id INT NOT NULL,
       supplier_id INT NOT NULL,
       inventory_category_id INT NOT NULL,
       item_name VARCHAR(255) NOT NULL,
       description VARCHAR(255) default NULL,
       publish VARCHAR(255) not NULL,
       cost_price DOUBLE PRECISION default NULL,
       sale_price DOUBLE PRECISION default NULL,
       stock INT default null,
       updated_by INT DEFAULT NULL,
       update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       deleted boolean default null
     )';
     EXECUTE ddl_statement;
    -- Create the fuel_expense table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.fuel_expense (
       fuel_expense_id SERIAL PRIMARY KEY,
       school_id INT NOT NULL,
       session_id INT NOT NULL,
       vehicle_id INT NOT NULL,
       fuel_date DATE NOT NULL,
       fuel_liter DECIMAL(10, 2) NOT NULL,
       refuel_amount DECIMAL(10, 2) NOT NULL,
       updated_by INT DEFAULT NULL,
       update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       deleted boolean default null
     )';
     EXECUTE ddl_statement;
    -- Create the expense_type table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.expense_type (
       expense_type_id SERIAL PRIMARY KEY,
       school_id INT NOT NULL,
       session_id INT NOT NULL,
       expense_title VARCHAR(255) NOT NULL,
       description VARCHAR(255) DEFAULT NULL,
       updated_by INT DEFAULT NULL,
       update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       deleted boolean default null
     )';
     EXECUTE ddl_statement;
    -- Create the add_expense table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.add_expense (
       add_expense_id SERIAL PRIMARY KEY,
       school_id INT NOT NULL,
       session_id INT NOT NULL,
       expense_type_id INT NOT NULL,
       particular VARCHAR(255) NOT NULL,
       expense_date DATE NOT NULL,
       expense_amount DECIMAL(10,2) NOT NULL,
       updated_by INT DEFAULT NULL,
       update_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       deleted boolean default null
     )';
     EXECUTE ddl_statement;
    -- Create the student_excel table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.student_excel (
       student_id serial PRIMARY KEY,
       uu_id VARCHAR(255) DEFAULT null,
       apaar_id VARCHAR(255) DEFAULT NULL,
       session VARCHAR(255) default null,
       student_first_name VARCHAR(255) NOT NULL,
       student_last_name VARCHAR(255) DEFAULT NULL,
       student_class VARCHAR(255) NOT NULL,
       student_section VARCHAR(255) NOT NULL,
       roll_no INT NOT NULL,
       medium VARCHAR(255) NOT NULL,
       dob DATE NOT NULL,
       religion VARCHAR(255) DEFAULT NULL,
       nationality VARCHAR(255) NOT NULL,
       gender VARCHAR(255) NOT NULL,
       mobile_number VARCHAR(255) NOT NULL,
       email VARCHAR(255) DEFAULT NULL,
       address VARCHAR(255) NOT NULL,
       city VARCHAR(255) NOT NULL,
       state VARCHAR(255) NOT NULL,
       country VARCHAR(255) NOT NULL,
       pincode VARCHAR(255) NOT NULL,
       type VARCHAR(255) DEFAULT NULL,
       isRteStudent BOOLEAN DEFAULT NULL,
       student_mother_name VARCHAR(255) DEFAULT NULL ,
       student_father_name VARCHAR(255) DEFAULT NULL,
       guardian_name VARCHAR(255) DEFAULT NULL,
       admission_date DATE NOT NULL,
       enrolled_session VARCHAR(255) DEFAULT NULL,
       enrolled_year VARCHAR(255) DEFAULT NULL,
       enrolled_class VARCHAR(255) DEFAULT NULL,
       pen_no VARCHAR(255) DEFAULT NULL,
       student_type VARCHAR(255) DEFAULT NULL,
       admission_no VARCHAR(255) DEFAULT NULL,
       registration_no VARCHAR(255) DEFAULT NULL,
       enrollment_no VARCHAR(255) DEFAULT NULL,
       stream VARCHAR(255) DEFAULT NULL,
       whatsapp VARCHAR(255) DEFAULT NULL,
       alternate_number VARCHAR(255) DEFAULT NULL,
       blood_group VARCHAR(255) DEFAULT NULL,
       aadhar_No VARCHAR(255) DEFAULT NULL,
       caste VARCHAR(255) DEFAULT NULL,
       category VARCHAR(255) DEFAULT NULL,
       rte_application_no VARCHAR(255) DEFAULT NULL,
       attended_school VARCHAR(255) DEFAULT NULL,
       attended_class VARCHAR(255) DEFAULT NULL,
       school_affiliated VARCHAR(255) DEFAULT NULL,
       last_session VARCHAR(255) DEFAULT NULL,
       mother_qualification VARCHAR(255) DEFAULT NULL,
       father_qualification VARCHAR(255) DEFAULT NULL,
       guardian_qualification VARCHAR(255) DEFAULT null,
       mother_occupation VARCHAR(255) DEFAULT NULL,
       father_occupation VARCHAR(255) DEFAULT NULL,
       guardian_occupation VARCHAR(255) DEFAULT NULL,
       mother_residential_address VARCHAR(255) DEFAULT NULL,
       father_residential_address VARCHAR(255) DEFAULT NULL,
       guardian_residential_address VARCHAR(256) DEFAULT NULL,
       mother_income VARCHAR(256) DEFAULT NULL,
       father_income VARCHAR(256) DEFAULT NULL,
       guardian_income VARCHAR(256) DEFAULT NULL,
       mother_email VARCHAR(256) DEFAULT NULL,
       father_email VARCHAR(256) DEFAULT NULL,
       guardian_email VARCHAR(256) DEFAULT NULL,
       mother_mobile VARCHAR(256) DEFAULT NULL,
       father_mobile VARCHAR(256) DEFAULT NULL,
       guardian_mobile VARCHAR(256) DEFAULT NULL,
       tc_no VARCHAR(256) DEFAULT NULL,
       tc_date VARCHAR(256) DEFAULT NULL,
       scholarship_id VARCHAR(256) DEFAULT NULL,
       scholarship_password VARCHAR(256) DEFAULT NULL,
       domicile_application_no VARCHAR(256) DEFAULT NULL,
       income_application_no VARCHAR(256) DEFAULT NULL,
       caste_application_no VARCHAR(256) DEFAULT NULL,
       dob_application_no VARCHAR(256) DEFAULT NULL,
       mother_aadhar_no VARCHAR(256) DEFAULT NULL,
       father_aadhar_no VARCHAR(256) DEFAULT NULL,
       guardian_aadhar_no VARCHAR(256) DEFAULT NULL,
       height VARCHAR(256) DEFAULT NULL,
       weight VARCHAR(256) DEFAULT NULL,
       bank_name VARCHAR(255) DEFAULT NULL,
       branch_name VARCHAR(255) DEFAULT NULL,
       bank_account_no VARCHAR(255) DEFAULT NULL,
       bank_ifsc VARCHAR(255) DEFAULT NULL,
       pan_no VARCHAR(256) DEFAULT NULL,
       reference VARCHAR(256) DEFAULT NULL,
       govt_student_id VARCHAR(255) DEFAULT NULL,
       govt_family_id VARCHAR(255) DEFAULT NULL,
       dropout BOOLEAN DEFAULT NULL,
       dropout_reason VARCHAR(256) DEFAULT NULL,
       dropout_date DATE DEFAULT NULL,
       previous_qualification VARCHAR(100) DEFAULT NULL,
       previous_pass_year VARCHAR(256) DEFAULT NULL,
       previous_roll_no VARCHAR(256) DEFAULT NULL,
       previous_obt_marks VARCHAR(256) DEFAULT NULL,
       previous_percentage VARCHAR(256) DEFAULT NULL,
       previous_subjects VARCHAR(256) DEFAULT NULL,
       previous_school_name VARCHAR(256) DEFAULT NULL
     )';
     EXECUTE ddl_statement;
     -- Create the parent_details table
     ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.parent_details (
     parent_id SERIAL PRIMARY KEY,
     uuid VARCHAR(255) DEFAULT NULL,
     school_id int not null,
     first_name VARCHAR(255) DEFAULT NULL,
     last_name VARCHAR(255) DEFAULT NULL,
     dob TIMESTAMP DEFAULT NULL,
     phone_number VARCHAR(255) DEFAULT NULL,
     emergency_phone_number VARCHAR(255) DEFAULT NULL,
     whatsapp_no VARCHAR(255) DEFAULT NULL,
     email_address VARCHAR(255) DEFAULT NULL,
     gender VARCHAR(255) DEFAULT NULL,
     parent_type VARCHAR(255) NOT NULL,
     qualification VARCHAR(255) DEFAULT NULL,
     aadhar_number VARCHAR(255) NOT NULL,
     company_name VARCHAR(255) DEFAULT NULL,
     designation VARCHAR(255) DEFAULT NULL,
     company_address VARCHAR(255) DEFAULT NULL,
     company_phone VARCHAR(255) DEFAULT NULL,
     address VARCHAR(255) DEFAULT NULL,
     city VARCHAR(255) DEFAULT NULL,
     state VARCHAR(255) DEFAULT NULL,
     zipcode int default null,
     updated_by int DEFAULT NULL,
     updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     deleted BOOLEAN DEFAULT NULL
)';
   EXECUTE ddl_statement;
   -- Create the staff_attendance table
   ddl_statement := 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.staff_attendance (
   sa_id SERIAL PRIMARY KEY,
   staff_id INT NOT NULL,
   staff_type VARCHAR(255) NOT NULL,
   designation_id INT NOT NULL,
   attendance_date DATE NOT NULL,
   attendance_status VARCHAR(255) NOT NULL
   )';
   EXECUTE ddl_statement;

    -- 1. exam_type
       EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.exam_type(
           exam_type_id SERIAL PRIMARY KEY,
           name VARCHAR(50) UNIQUE NOT NULL,
           session_id INT NOT NULL,
           description TEXT,
           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
       )';

       -- 2. mst_grade (ग्रेड डेटा के साथ)
       EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.mst_grade (
           grade_id SERIAL PRIMARY KEY,
           grade_name VARCHAR(10) NOT NULL UNIQUE,
           min_percentage DECIMAL(5,2) NOT NULL,
           max_percentage DECIMAL(5,2) NOT NULL,
           description VARCHAR(255)
       )';

       -- ग्रेड डेटा इन्सर्ट करें
       EXECUTE 'INSERT INTO ' || quote_ident(schema_name) || '.mst_grade (grade_name, min_percentage, max_percentage)
           VALUES
               (''A+'', 90, 100),
               (''A'', 80, 89.99),
               (''B+'', 70, 79.99),
               (''B'', 60, 69.99),
               (''C'', 50, 59.99),
               (''F'', 0, 49.99)
           ON CONFLICT (grade_name) DO NOTHING';

       -- 3. attendance_session (संशोधित)
       EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.attendance_session (
           attendance_session_id SERIAL PRIMARY KEY,
           session_id INT NOT NULL REFERENCES ' || quote_ident(schema_name) || '.session(session_id),
           class_id INT NOT NULL,
           section_id INT NOT NULL,
           subject_id INT,
           teacher_id INT NOT NULL,
           session_date DATE NOT NULL,
           session_type VARCHAR(7) NOT NULL CHECK (session_type IN (''DAILY'', ''SUBJECT'')),
           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
       )';

       -- इंडेक्स जोड़ें
       EXECUTE 'CREATE UNIQUE INDEX IF NOT EXISTS unique_daily_attendance
                ON ' || quote_ident(schema_name) || '.attendance_session (session_id, class_id, section_id, session_date)
                WHERE session_type = ''DAILY'' AND subject_id IS NULL';

       EXECUTE 'CREATE UNIQUE INDEX IF NOT EXISTS unique_subject_attendance
                ON ' || quote_ident(schema_name) || '.attendance_session (session_id, class_id, section_id, subject_id, session_date)
                WHERE session_type = ''SUBJECT'' AND subject_id IS NOT NULL';

       -- 4. attendance_record
       EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.attendance_record (
           record_id SERIAL PRIMARY KEY,
           attendance_session_id INT NOT NULL REFERENCES ' || quote_ident(schema_name) || '.attendance_session(attendance_session_id),
           student_id INT NOT NULL,
           status VARCHAR(7) NOT NULL CHECK (status IN (''PRESENT'', ''ABSENT'')),
           marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
           UNIQUE (attendance_session_id, student_id)
       )';

       -- 5. exams
       EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.exams (
           exam_id SERIAL PRIMARY KEY,
           exam_type_id INT NOT NULL REFERENCES ' || quote_ident(schema_name) || '.exam_type(exam_type_id),
           session_id INT NOT NULL REFERENCES ' || quote_ident(schema_name) || '.session(session_id),
           class_id INT NOT NULL,
           section_id INT DEFAULT NULL,
           start_date DATE NOT NULL,
           end_date DATE NOT NULL CHECK (end_date >= start_date),
           status VARCHAR(20) CHECK (status IN (''Scheduled'', ''Ongoing'', ''Completed'')) DEFAULT ''Scheduled'',
           created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
           deleted BOOLEAN DEFAULT NULL
       )';

       -- 6. exam_subjects
       EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.exam_subjects (
           exam_subject_id SERIAL PRIMARY KEY,
           exam_id INT NOT NULL REFERENCES ' || quote_ident(schema_name) || '.exams(exam_id),
           subject_id INT NOT NULL,
           theory_max_marks INT CHECK (theory_max_marks >= 0),
           practical_max_marks INT CHECK (practical_max_marks >= 0),
           viva_max_marks INT CHECK (viva_max_marks >= 0),
           total_max_marks INT GENERATED ALWAYS AS (
               COALESCE(theory_max_marks, 0) +
               COALESCE(practical_max_marks, 0) +
               COALESCE(viva_max_marks, 0)
           ) STORED,
           passing_marks INT NOT NULL,
           exam_date DATE NOT NULL,
           start_time TIME NOT NULL,
           end_time TIME NOT NULL CHECK (end_time > start_time),
           CHECK (
               theory_max_marks IS NOT NULL OR
               practical_max_marks IS NOT NULL OR
               viva_max_marks IS NOT NULL
           ),
           UNIQUE(exam_id, subject_id)
       )';

       -- 7. exam_results
       EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.exam_results (
           result_id SERIAL PRIMARY KEY,
           student_id INT NOT NULL,
           exam_subject_id INT NOT NULL REFERENCES ' || quote_ident(schema_name) || '.exam_subjects(exam_subject_id),
           theory_marks INT DEFAULT NULL,
           practical_marks INT DEFAULT NULL,
           viva_marks INT DEFAULT NULL,
           total_marks INT GENERATED ALWAYS AS (
               COALESCE(theory_marks, 0) +
               COALESCE(practical_marks, 0) +
               COALESCE(viva_marks, 0)
           ) STORED,
           UNIQUE(student_id, exam_subject_id)
       )';

-- 8. transfer_certificates
      EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.transfer_certificates (
          tc_id SERIAL PRIMARY KEY,
          school_id INT NOT NULL,
          session_id INT NOT NULL REFERENCES ' || quote_ident(schema_name) || '.session(session_id),
          issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          issued_by INT NOT NULL,
          admission_no VARCHAR(50) NOT NULL UNIQUE,
          student_name VARCHAR(100) NOT NULL,
          dob DATE NOT NULL,
          dob_in_words VARCHAR(255) NOT NULL,
          proof_of_dob VARCHAR(50) NOT NULL CHECK (proof_of_dob IN (''Birth Certificate'', ''Aadhaar Card'', ''Passport'')),
          father_name VARCHAR(150) NOT NULL,
          mother_name VARCHAR(150) NOT NULL,
          guardian_name VARCHAR(150),
          class_at_leaving VARCHAR(50) NOT NULL,
          section_at_leaving VARCHAR(10),
          last_exam_passed VARCHAR(100),
          last_class_studied VARCHAR(50) NOT NULL,
          total_working_days INT NOT NULL CHECK (total_working_days >= 0),
          total_presence INT NOT NULL CHECK (total_presence >= 0 AND total_presence <= total_working_days),
          subjects_studied TEXT NOT NULL,
          category VARCHAR(50) NOT NULL CHECK (category IN (''SC'', ''ST'', ''OBC'', ''General'')),
          fee_concession VARCHAR(255),
          extracurricular VARCHAR(255),
          school_category VARCHAR(50) NOT NULL CHECK (school_category IN (''Govt'', ''Minority'', ''Independent'')),
          games_activities TEXT,
          date_of_first_admission DATE NOT NULL,
          date_of_application DATE NOT NULL,
          date_struck_off DATE NOT NULL,
          date_of_issue DATE NOT NULL DEFAULT CURRENT_DATE,
          reason_for_leaving VARCHAR(255) NOT NULL,
          fee_dues_status VARCHAR(50) CHECK (fee_dues_status IN (''Cleared'', ''Pending'')) NOT NULL,
          remarks TEXT,
          tc_data JSONB
      )';

       -- 8. transfer_certificates_alumni
       EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.transfer_certificates_alumni (
             tc_id SERIAL PRIMARY KEY,
             school_id INT NOT NULL,
             school_code VARCHAR(255) DEFAULT NULL,
             school_category VARCHAR(50) NOT NULL CHECK (school_category IN (''Govt'', ''Minority'', ''Independent'')),
             session_id INT DEFAULT NULL,
             academic_session VARCHAR(255) DEFAULT NULL,
             issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             issued_by INT NOT NULL,
             student_id BIGINT DEFAULT NULL,
             admission_no VARCHAR(50) NOT NULL UNIQUE,
             student_name VARCHAR(100) NOT NULL,
             dob DATE NOT NULL,
             dob_in_words VARCHAR(255) NOT NULL,
             proof_of_dob VARCHAR(50) NOT NULL CHECK (proof_of_dob IN (''Birth Certificate'', ''Aadhaar Card'', ''Passport'')),
             pen_no VARCHAR(255) DEFAULT NULL,
             apaar_id VARCHAR(255) DEFAULT NULL,
             father_name VARCHAR(150) NOT NULL,
             mother_name VARCHAR(150) NOT NULL,
             is_student_failed VARCHAR(50) DEFAULT NULL,
             subjects_offered TEXT NOT NULL,
             is_promoted_to_next_class VARCHAR(255) DEFAULT NULL,
             no_of_meetings INT DEFAULT NULL,
             total_present INT DEFAULT NULL,
             class_at_leaving VARCHAR(50) DEFAULT NULL,
             section_at_leaving VARCHAR(10),
             last_annual_exam_result VARCHAR(255) DEFAULT NULL,
             last_studied_class VARCHAR(50) DEFAULT NULL,
             category VARCHAR(50) NOT NULL CHECK (category IN (''SC'', ''ST'', ''OBC'', ''General'')),
             fee_due_status VARCHAR(50) CHECK (fee_due_status IN (''Cleared'', ''Pending'')) NOT NULL,
             extracurricular VARCHAR(255),
             games_activities TEXT,
             general_conduct VARCHAR(255) DEFAULT NULL,
             reason_for_leaving VARCHAR(255) NOT NULL,
             date_of_issue DATE NOT NULL DEFAULT CURRENT_DATE,
             date_struck_off DATE DEFAULT NULL,
             remarks TEXT,
             sr_number VARCHAR(255) DEFAULT NULL,
             tc_data JSONB
       )';

       -- 9. timetable
       EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.timetable (
           timetable_id SERIAL PRIMARY KEY,
           school_id INT NOT NULL,
           session_id INT NOT NULL REFERENCES ' || quote_ident(schema_name) || '.session(session_id),
           class_id INT NOT NULL,
           section_id INT NOT NULL,
           subject_id INT NOT NULL,
           teacher_id INT NOT NULL,
           day_of_week VARCHAR(10) NOT NULL,
           period_number INT NOT NULL,
           start_time TIME NOT NULL,
           end_time TIME NOT NULL,
           room_number VARCHAR(50) DEFAULT NULL,
           CONSTRAINT fk_class_subject_allocation FOREIGN KEY (class_id, section_id, subject_id)
               REFERENCES ' || quote_ident(schema_name) || '.class_subject_allocation(class_id, section_id, subject_id),
           CONSTRAINT unique_timetable_entry UNIQUE (school_id, session_id, class_id, section_id, day_of_week, period_number)
       )';

    -- Return the schema name after execution
    RETURN schema_name;
END;
$$ LANGUAGE plpgsql;

SELECT medhapro.exec_ddl('acv-3223');