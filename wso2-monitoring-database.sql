DROP DATABASE IF EXISTS `wso2-monitoring-database`;
CREATE DATABASE `wso2-monitoring-database`;

USE `wso2-monitoring-database`;
-- gender *
-- designation *
-- user_account *
-- traffic_type *
-- log_types *
-- logs

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
    `designation`        varchar(30) NOT NULL,
    `date_created`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `date_modified` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`designation`),

    KEY `index_designations_date_created` (`date_created`),
    KEY `index_designations_date_modified` (`date_modified`)
);

CREATE TABLE `user_accounts`
(
  `user_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_status` enum('ACTIVE','LOCKED','INACTIVE','SUSPENDED') NOT NULL,
  `user_status_description` varchar(200) DEFAULT NULL,
  `user_status_date` datetime DEFAULT NULL,
  `account_access_mode` enum('API','INTERACTIVE','HYBRID') NOT NULL,
  `username` varchar(200) NOT NULL,
  `first_name` varchar(200) NOT NULL,
  `last_name` varchar(200) NOT NULL,
  `mobile_number` bigint unsigned NOT NULL,
  `email_address` varchar(200) NOT NULL,
  `user_pwd_status` enum('ACTIVE','RESET','EXPIRED') NOT NULL,
  `user_pwd` varchar(64) NOT NULL,
  `user_pwd_status_date` datetime NOT NULL,
  `login_attempts` int unsigned NOT NULL DEFAULT '0',
  `max_login_attempts` int unsigned NOT NULL DEFAULT '5',
  `allowed_access_sources_status` enum('ACTIVE','INACTIVE') NOT NULL,
  `allowed_access_sources_match_type` enum('STRING','REGEX') NOT NULL,
  `max_allowed_access_sources` int NOT NULL DEFAULT '5',
  `allowed_access_sources` varchar(500) DEFAULT NULL,
  `restricted_access_sources_status` enum('ACTIVE','INACTIVE') NOT NULL,
  `restricted_access_sources_match_type` enum('STRING','REGEX') NOT NULL,
  `max_restricted_access_sources` int NOT NULL DEFAULT '5',
  `restricted_access_sources` varchar(200) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `date_modified` datetime NOT NULL,
  `tracking_id` varchar(64) DEFAULT NULL,
  `tracking_source_ip` varchar(100) DEFAULT NULL,
  `tracking_url` varchar(200) DEFAULT NULL,
  `tracking_time` datetime DEFAULT NULL,
  `tracking_referrer` varchar(200) DEFAULT NULL,
  `gender`        varchar(30) NOT NULL,
  `designation`        varchar(30) NOT NULL,


  PRIMARY KEY (`user_id`),

  UNIQUE KEY `index_user_accounts_email_address` (`email_address`),
  UNIQUE KEY `uindex_user_accounts_username` (`username`),

  KEY `index_user_accounts_user_status` (`user_status`),
  KEY `index_user_accounts_account_member_id` (`member_id`),
  KEY `index_user_accounts_user_names` (`full_name`),
  KEY `index_user_accounts_mobile_number` (`mobile_number`),
  KEY `index_user_accounts_user_pwd_status` (`user_pwd_status`),
  KEY `index_user_accounts_user_pwd_status_date` (`user_pwd_status_date`),
  KEY `index_user_accounts_reset_pwd_request_count` (`reset_pwd_request_count`),
  KEY `index_user_accounts_allowed_access_sources_status` (`allowed_access_sources_status`),
  KEY `index_user_accounts_allowed_access_sources_match_type` (`allowed_access_sources_match_type`),
  KEY `index_user_accounts_restricted_access_sources_status` (`restricted_access_sources_status`),
  KEY `index_user_accounts_restricted_access_sources_match_type` (`restricted_access_sources_match_type`),
  KEY `index_user_accounts_date_created` (`date_created`),
  KEY `index_user_accounts_date_modified` (`date_modified`),

  CONSTRAINT `fk_genders_user_accounts_gender` FOREIGN KEY (`gender`) REFERENCES `genders` (`gender`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_designations_user_accounts_designation` FOREIGN KEY (`designation`) REFERENCES `designations` (`designation`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `log_types`
(
    `log_type`        varchar(30) NOT NULL,
    `date_created`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `date_modified` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`log_type`),

    KEY `index_log_types_date_created` (`date_created`),
    KEY `index_log_types_date_modified` (`date_modified`)
);

CREATE TABLE `traffic_types`
(
    `traffic_type`        varchar(30) NOT NULL,
    `date_created`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `date_modified` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`traffic_type`),

    KEY `index_traffic_types_date_created` (`date_created`),
    KEY `index_traffic_types_date_modified` (`date_modified`)
);