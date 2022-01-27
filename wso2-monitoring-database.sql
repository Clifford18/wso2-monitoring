
DROP DATABASE IF EXISTS `wso2_monitoring_database`;
CREATE DATABASE `wso2_monitoring_database`;

USE `wso2_monitoring_database`;

CREATE TABLE `genders`
(
    `gender`        varchar(30) NOT NULL,
    `date_created`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `date_modified` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`gender`),

    KEY `index_genders_date_created` (`date_created`),
    KEY `index_genders_date_modified` (`date_modified`)
);

CREATE TABLE `designations`
(
    `designation`   varchar(30) NOT NULL,
    `date_created`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `date_modified` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`designation`),

    KEY `index_designations_date_created` (`date_created`),
    KEY `index_designations_date_modified` (`date_modified`)
);

CREATE TABLE `user_accounts`
(
    `user_id`                              bigint unsigned                                 NOT NULL AUTO_INCREMENT,
    `user_status`                          enum ('ACTIVE','LOCKED','INACTIVE','SUSPENDED') NOT NULL,
    `user_status_description`              varchar(200)                                             DEFAULT NULL,
    `user_status_date`                     datetime                                                 DEFAULT NULL,
    `account_access_mode`                  enum ('API','INTERACTIVE','HYBRID')             NOT NULL,
    `username`                             varchar(200)                                    NOT NULL,
    `first_name`                           varchar(200)                                    NOT NULL,
    `last_name`                            varchar(200)                                    NOT NULL,
    `mobile_number`                        bigint unsigned                                 NOT NULL,
    `email_address`                        varchar(200)                                    NOT NULL,
    `user_pwd_status`                      enum ('ACTIVE','RESET','EXPIRED')               NOT NULL,
    `user_pwd`                             varchar(64)                                     NOT NULL,
    `user_pwd_status_date`                 datetime                                        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `login_attempts`                       int unsigned                                    NOT NULL DEFAULT '0',
    `max_login_attempts`                   int unsigned                                    NOT NULL DEFAULT '5',
    `allowed_access_sources_status`        enum ('ACTIVE','INACTIVE')                      NOT NULL,
    `allowed_access_sources_match_type`    enum ('STRING','REGEX')                         NOT NULL,
    `max_allowed_access_sources`           int                                             NOT NULL DEFAULT '5',
    `allowed_access_sources`               varchar(500)                                             DEFAULT NULL,
    `restricted_access_sources_status`     enum ('ACTIVE','INACTIVE')                      NOT NULL,
    `restricted_access_sources_match_type` enum ('STRING','REGEX')                         NOT NULL,
    `max_restricted_access_sources`        int                                             NOT NULL DEFAULT '5',
    `restricted_access_sources`            varchar(200)                                             DEFAULT NULL,
    `tracking_id`                          varchar(64)                                              DEFAULT NULL,
    `tracking_source_ip`                   varchar(100)                                             DEFAULT NULL,
    `tracking_url`                         varchar(200)                                             DEFAULT NULL,
    `tracking_time`                        datetime                                                 DEFAULT NULL,
    `tracking_referrer`                    varchar(200)                                             DEFAULT NULL,
    `gender`                               varchar(30)                                     NOT NULL,
    `designation`                          varchar(30)                                     NOT NULL,
    `date_created`                         datetime                                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `date_modified`                        datetime                                        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`user_id`),

    UNIQUE KEY `index_user_accounts_email_address` (`email_address`),
    UNIQUE KEY `uindex_user_accounts_username` (`username`),

    KEY `index_user_accounts_user_status` (`user_status`),
    KEY `index_user_accounts_mobile_number` (`mobile_number`),
    KEY `index_user_accounts_user_pwd_status` (`user_pwd_status`),
    KEY `index_user_accounts_user_pwd_status_date` (`user_pwd_status_date`),
    KEY `index_user_accounts_allowed_access_sources_status` (`allowed_access_sources_status`),
    KEY `index_user_accounts_allowed_access_sources_match_type` (`allowed_access_sources_match_type`),
    KEY `index_user_accounts_restricted_access_sources_status` (`restricted_access_sources_status`),
    KEY `index_user_accounts_restricted_access_sources_match_type` (`restricted_access_sources_match_type`),
    KEY `index_user_accounts_date_created` (`date_created`),
    KEY `index_user_accounts_date_modified` (`date_modified`),

    CONSTRAINT `fk_genders_user_accounts_gender` FOREIGN KEY (`gender`) REFERENCES `genders` (`gender`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_designations_user_accounts_designation` FOREIGN KEY (`designation`) REFERENCES `designations` (`designation`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `request_logs`
(
    `request_id`          bigint       NOT NULL AUTO_INCREMENT,
    `request_reference`   varchar(150) NOT NULL,
    `request_method`      varchar(20)  NOT NULL,
    `request_resource`    varchar(500) NOT NULL,
    `request_parameters`  text,
    `request_headers`     text         NOT NULL,
    `request_body`        longtext,
    `request_origin_ip`   varchar(50)  NOT NULL,
    `response_headers`    text                  DEFAULT NULL,
    `response_body`       longtext,
    `error_code`          varchar(50)           DEFAULT NULL,
    `error_message`       longtext,
    `error_stacktrace`    longtext,
    `date_created`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `date_modified`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`request_id`),
    UNIQUE KEY `uindex_request_logs_request_reference` (`request_reference`),
    KEY `index_request_logs_request_method` (`request_method`),
    KEY `index_request_logs_error_code` (`error_code`),
    KEY `index_request_logs_request_id` (`request_id`),
    KEY `index_request_logs_request_origin_ip` (`request_origin_ip`),
    KEY `index_request_logs_request_resource` (`request_resource`),
    KEY `index_request_logs_date_created` (`date_created`),
    KEY `index_request_logs_date_modified` (`date_modified`)
);

CREATE TABLE `access_tokens`
(
    `access_token_id`   bigint unsigned      NOT NULL AUTO_INCREMENT,
    `user_id`           bigint unsigned NOT NULL,
    `access_token`      varchar(500)  NOT NULL,
    `ip_address`        varchar(20) NOT NULL,
    `time_to_live`      text,
    `time_units`       text         NOT NULL,
    `expiration_time`   longtext,
    `date_created`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `date_modified`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`access_token_id`),

    KEY `index_access_tokens_user_id` (`user_id`),
    KEY `index_access_tokens_access_token` (`access_token`),
    KEY `index_access_tokens_ip_address` (`ip_address`),
    KEY `index_access_tokens_date_created` (`date_created`),
    KEY `index_access_tokens_date_modified` (`date_modified`),

    CONSTRAINT `fk_user_accounts_access_tokens_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_accounts` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO genders (gender)
VALUES ('Male'),
       ('Female');

INSERT INTO designations (designation)
VALUES ('C_E_O'),
       ('IT_Manager'),
       ('IT_Ass_Manager');

INSERT INTO user_accounts (user_status, account_access_mode, username, first_name, last_name, mobile_number,
                           email_address, user_pwd_status, user_pwd, allowed_access_sources_status,
                           allowed_access_sources_match_type, restricted_access_sources_status,
                           restricted_access_sources_match_type, gender, designation)
VALUES ('ACTIVE', 'API', 'username1', 'first_name1', 'last_name1', 254721111222, 'f1l1@gmail.com', 'ACTIVE',
        'user_pwd1', 'ACTIVE',
        'STRING', 'ACTIVE', 'STRING', 'Male', 'IT_Manager'),
       ('ACTIVE', 'API', 'username2', 'first_name2', 'last_name2', 254722111222, 'f2l2@gmail.com', 'ACTIVE',
        'user_pwd2', 'ACTIVE',
        'STRING', 'ACTIVE', 'STRING', 'Female', 'IT_Ass_Manager'),
       ('ACTIVE', 'API', 'username3', 'first_name3', 'last_name3', 254723111222, 'f3l3@gmail.com', 'ACTIVE',
        'user_pwd3', 'ACTIVE',
        'STRING', 'ACTIVE', 'STRING', 'Male', 'IT_Manager');






DROP PROCEDURE IF EXISTS generate_request_logs;
DELIMITER $$
CREATE PROCEDURE generate_request_logs()
BEGIN
    -- GENERATE REQUEST LOGS*
    DECLARE i INT DEFAULT 1;
    WHILE i <= 100000
        DO


            INSERT INTO request_logs (request_reference, request_method, request_resource,
                                      request_parameters, request_headers, request_body, request_origin_ip,
                                      response_body)
            VALUES (concat('request_reference - ', i), 'request_method1', 'request_resource1',
                    concat('request_parameters - ', i), concat('request_headers - ', i), concat('request_body - ', i),
                    concat('request_origin_ip - ', i), concat('response_body - ', i));
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL generate_request_logs();