alter session set container = XEPDB1;

-- SEQUENCES

CREATE SEQUENCE PHASER_OWNER.JOB_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

CREATE SEQUENCE PHASER_OWNER.RESULT_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
	NOCACHE
	ORDER;

-- TABLES

CREATE TABLE PHASER_OWNER.JOB
(
    JOB_ID               INTEGER NOT NULL ,
    START_DATE           DATE NOT NULL ,
    MAX_PHASE_ERROR      FLOAT NOT NULL ,
    CONTINUOUS_YN        CHAR(1) NOT NULL  CONSTRAINT  JOB_CK1 CHECK (CONTINUOUS_YN IN ('Y', 'N')),
    CORRECT_INCREMENTALLY_YN CHAR(1) NOT NULL  CONSTRAINT  JOB_CK2 CHECK (CORRECT_INCREMENTALLY_YN IN ('Y', 'N')),
    END_DATE             DATE NULL ,
    MAX_MOMENTUM_ERROR   FLOAT NOT NULL ,
    KICK_SAMPLES         INTEGER NOT NULL  CONSTRAINT  JOB_CK4 CHECK (KICK_SAMPLES >= 1),
    CONSTRAINT  JOB_PK PRIMARY KEY (JOB_ID)
);

CREATE TABLE PHASER_OWNER.RESULT
(
    RESULT_ID            INTEGER NOT NULL ,
    JOB_ID               INTEGER NOT NULL ,
    START_DATE           DATE NOT NULL ,
    END_DATE             DATE NOT NULL ,
    CAVITY               VARCHAR2(24 CHAR) NOT NULL ,
    PHASE_ERROR          FLOAT NULL ,
    OUTCOME              VARCHAR2(24 CHAR) NOT NULL  CONSTRAINT  RESULT_CK1 CHECK (OUTCOME IN ('MEASURED', 'BYPASSED', 'SKIPPED', 'ERROR', 'CORRECTED', 'DEFERRED')),
    PHASE                FLOAT NULL ,
    CORRECTION_DATE      DATE NULL ,
    CORRECTION_ERROR_REASON VARCHAR2(64 CHAR) NULL ,
    CONSTRAINT  RESULT_PK PRIMARY KEY (RESULT_ID),
    CONSTRAINT RESULT_FK1 FOREIGN KEY (JOB_ID) REFERENCES PHASER_OWNER.JOB (JOB_ID) ON DELETE CASCADE
);

-- PERMISSIONS

grant select on phaser_owner.job to phaser_reader;
grant select on phaser_owner.result to phaser_reader;

grant select on phaser_owner.job to phaser_writer;
grant select on phaser_owner.result to phaser_writer;
grant update on phaser_owner.job to phaser_writer;
grant update on phaser_owner.result to phaser_writer;
grant insert on phaser_owner.job to phaser_writer;
grant insert on phaser_owner.result to phaser_writer;
grant select on phaser_owner.job_id to phaser_writer;
grant select on phaser_owner.result_id to phaser_writer;

-- SYNONYMS
create synonym phaser_writer.job for phaser_owner.job;
create synonym phaser_writer.result for phaser_owner.result;
create synonym phaser_writer.job_id for phaser_owner.job_id;
create synonym phaser_writer.result_id for phaser_owner.result_id;