-- MySQL dump 10.13  Distrib 8.0.29, for Linux (x86_64)
--
-- Host: 192.168.2.11    Database: esb_monitoring_db
-- ------------------------------------------------------
-- Server version	8.0.25-15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE esb_monitoring_db;
USE esb_monitoring_db;

--
-- Table structure for table `access_tokens`
--

DROP TABLE IF EXISTS `access_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access_tokens` (
  `access_token_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `access_token` varchar(150) NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `auth_ip_address` varchar(100) DEFAULT NULL,
  `scope` varchar(500) NOT NULL,
  `time_to_live` int NOT NULL,
  `time_unit` enum('SECONDS','MINUTES','HOURS','DAYS') NOT NULL DEFAULT 'SECONDS',
  `date_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`access_token_id`),
  UNIQUE KEY `admin_access_tokens_access_token_uindex` (`access_token`),
  KEY `admin_access_tokens_date_created_index` (`date_created`),
  KEY `admin_access_tokens_time_to_live_index` (`time_to_live`),
  KEY `admin_access_tokens_user_account_id_index` (`user_id`),
  KEY `admin_access_tokens_access_token` (`access_token`),
  CONSTRAINT `fk_access_tokens_user_accounts_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_accounts` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_tokens`
--

LOCK TABLES `access_tokens` WRITE;
/*!40000 ALTER TABLE `access_tokens` DISABLE KEYS */;
INSERT INTO `access_tokens` VALUES (21,'Vd8VvLjk1PNRrZYLgvNNUm8hZWuqIzh0eZh5IpSayxCK3wPLVNytjxF9fQ5bPcTRunT6OpmA7hXnuipQ',1,'0:0:0:0:0:0:0:1','GLOBAL',3600,'SECONDS','2022-05-26 12:16:30');
/*!40000 ALTER TABLE `access_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_user_groups`
--

DROP TABLE IF EXISTS `app_user_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_user_groups` (
  `group_id` int NOT NULL AUTO_INCREMENT,
  `group_name` varchar(100) NOT NULL,
  `group_description` varchar(200) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `date_modified` datetime NOT NULL,
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `uindex_admin_group_type_group_name` (`group_name`),
  KEY `index_admin_app_user_groups_date_created` (`date_created`),
  KEY `index_admin_app_user_groups_date_modified` (`date_modified`),
  KEY `index_admin_app_user_groups_group_name` (`group_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_user_groups`
--

LOCK TABLES `app_user_groups` WRITE;
/*!40000 ALTER TABLE `app_user_groups` DISABLE KEYS */;
INSERT INTO `app_user_groups` VALUES (1,'ATM Bridge Admin','ATM Bridge Admin','2022-05-25 17:01:10','2022-05-25 17:01:10'),(2,'Members Portal Admin','Members Portal Admin','2022-05-25 17:01:10','2022-05-25 17:01:10');
/*!40000 ALTER TABLE `app_user_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_user_rights`
--

DROP TABLE IF EXISTS `app_user_rights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_user_rights` (
  `user_rights_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `app_code` int NOT NULL,
  `user_rights` varchar(1000) NOT NULL DEFAULT '<RIGHTS />',
  `group_id` int NOT NULL,
  `date_created` datetime NOT NULL,
  `date_modified` datetime NOT NULL,
  PRIMARY KEY (`user_rights_id`),
  UNIQUE KEY `uindex_admin_aur_app_code_approval_level_group_id` (`app_code`,`group_id`),
  KEY `index_admin_app_user_rights_app_code` (`app_code`),
  KEY `index_admin_app_user_rights_date_created` (`date_created`),
  KEY `index_admin_app_user_rights_date_modified` (`date_modified`),
  KEY `index_admin_app_user_rights_group_id` (`group_id`),
  CONSTRAINT `fkey_admin_app_user_rights_applications_app_code` FOREIGN KEY (`app_code`) REFERENCES `applications` (`app_code`) ON UPDATE CASCADE,
  CONSTRAINT `fkey_admin_app_user_rights_group_id` FOREIGN KEY (`group_id`) REFERENCES `app_user_groups` (`group_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_user_rights`
--

LOCK TABLES `app_user_rights` WRITE;
/*!40000 ALTER TABLE `app_user_rights` DISABLE KEYS */;
INSERT INTO `app_user_rights` VALUES (1,101,'<RIGHTS>\n    <PERMISSIONS INPUT=\"NO\" APPROVE=\"NO\" VIEW=\"YES\" EDIT=\"NO\" DELETE=\"NO\" EXECUTE=\"NO\">\n        <APPROVAL_LEVELS />\n    </PERMISSIONS>\n</RIGHTS>',1,'2022-05-25 17:02:33','2022-05-25 17:02:33'),(2,201,'<RIGHTS>\n    <PERMISSIONS INPUT=\"NO\" APPROVE=\"NO\" VIEW=\"YES\" EDIT=\"NO\" DELETE=\"NO\" EXECUTE=\"NO\">\n        <APPROVAL_LEVELS />\n    </PERMISSIONS>\n</RIGHTS>',2,'2022-05-25 17:02:33','2022-05-25 17:02:33');
/*!40000 ALTER TABLE `app_user_rights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application_workflow_logs`
--

DROP TABLE IF EXISTS `application_workflow_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `application_workflow_logs` (
  `log_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `approval_level` int NOT NULL,
  `approval_status` enum('APPROVED','REVIEW','REJECTED') NOT NULL,
  `record_id` bigint unsigned NOT NULL,
  `user_comments` varchar(500) DEFAULT NULL,
  `user_id` bigint unsigned NOT NULL,
  `log_date` datetime NOT NULL,
  PRIMARY KEY (`log_id`),
  KEY `index_admin_workflow_log_approval_level` (`approval_level`),
  KEY `index_admin_workflow_log_approval_status` (`approval_status`),
  KEY `index_admin_workflow_log_date` (`log_date`),
  KEY `index_admin_workflow_log_record_id` (`record_id`),
  KEY `index_admin_workflow_log_user_id` (`user_id`),
  CONSTRAINT `fkey_admin_workflow_log_record_id` FOREIGN KEY (`record_id`) REFERENCES `application_workflows` (`record_id`) ON UPDATE CASCADE,
  CONSTRAINT `fkey_admin_workflow_log_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_accounts` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_workflow_logs`
--

LOCK TABLES `application_workflow_logs` WRITE;
/*!40000 ALTER TABLE `application_workflow_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `application_workflow_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application_workflows`
--

DROP TABLE IF EXISTS `application_workflows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `application_workflows` (
  `record_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `app_code` int NOT NULL,
  `approval_level` int NOT NULL,
  `approval_status` enum('PENDING','APPROVED','REVIEW','REJECTED') NOT NULL,
  `record_content` longtext NOT NULL,
  `workflow_status` enum('DRAFT','IN_PROGRESS','COMPLETED','DISCARDED') NOT NULL,
  `workflow_title` varchar(250) NOT NULL,
  `date_created` datetime NOT NULL,
  `date_modified` datetime NOT NULL,
  PRIMARY KEY (`record_id`),
  KEY `index_admin_application_approval_level` (`approval_level`),
  KEY `index_admin_application_approval_status` (`approval_status`),
  KEY `index_admin_application_workflow_app_code` (`app_code`),
  KEY `index_admin_application_workflow_date_created` (`date_created`),
  KEY `index_admin_application_workflow_date_modified` (`date_modified`),
  KEY `index_admin_application_workflow_status` (`workflow_status`),
  KEY `index_admin_application_workflow_title` (`workflow_title`),
  KEY `fk_user_admin_accounts_application_workflows_user_id` (`user_id`),
  CONSTRAINT `fk_admin_user_accounts_application_workflows_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_accounts` (`user_id`) ON UPDATE CASCADE,
  CONSTRAINT `fkey_admin_application_workflow_app_code` FOREIGN KEY (`app_code`) REFERENCES `applications` (`app_code`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_workflows`
--

LOCK TABLES `application_workflows` WRITE;
/*!40000 ALTER TABLE `application_workflows` DISABLE KEYS */;
/*!40000 ALTER TABLE `application_workflows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `applications` (
  `app_code` int NOT NULL,
  `app_name` varchar(100) NOT NULL,
  `app_type` varchar(50) NOT NULL,
  `app_description` varchar(200) NOT NULL,
  `approval_levels` int NOT NULL,
  `date_created` datetime NOT NULL,
  `date_modified` datetime NOT NULL,
  PRIMARY KEY (`app_code`),
  UNIQUE KEY `applications_app_code_app_type_uindex` (`app_code`,`app_type`),
  KEY `index_applications_app_name` (`app_name`),
  KEY `index_applications_date_created` (`date_created`),
  KEY `index_applications_date_modified` (`date_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `applications`
--

LOCK TABLES `applications` WRITE;
/*!40000 ALTER TABLE `applications` DISABLE KEYS */;
INSERT INTO `applications` VALUES (101,'ATM Bridge Logs','ATM_BRIDGE','ATM Bridge Logs',0,'2022-05-25 17:00:25','2022-05-25 17:00:25'),(201,'Members Portal Logs','MEMBERS_PORTAL','Members Portal Logs',0,'2022-05-25 17:00:25','2022-05-25 17:00:25');
/*!40000 ALTER TABLE `applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `designations`
--

DROP TABLE IF EXISTS `designations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `designations` (
  `designation` varchar(30) NOT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`designation`),
  KEY `index_designations_date_created` (`date_created`),
  KEY `index_designations_date_modified` (`date_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `designations`
--

LOCK TABLES `designations` WRITE;
/*!40000 ALTER TABLE `designations` DISABLE KEYS */;
INSERT INTO `designations` VALUES ('ICT Manager','2022-05-25 14:06:42','2022-05-25 14:06:42');
/*!40000 ALTER TABLE `designations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genders`
--

DROP TABLE IF EXISTS `genders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genders` (
  `gender` varchar(30) NOT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`gender`),
  KEY `index_genders_date_created` (`date_created`),
  KEY `index_genders_date_modified` (`date_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genders`
--

LOCK TABLES `genders` WRITE;
/*!40000 ALTER TABLE `genders` DISABLE KEYS */;
INSERT INTO `genders` VALUES ('FEMALE','2022-05-25 14:06:30','2022-05-25 14:06:30'),('MALE','2022-05-25 14:06:30','2022-05-25 14:06:30'),('OTHER','2022-05-25 14:06:30','2022-05-25 14:06:30');
/*!40000 ALTER TABLE `genders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request_logs`
--

DROP TABLE IF EXISTS `request_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request_logs` (
  `request_id` bigint NOT NULL AUTO_INCREMENT,
  `app_code` int NOT NULL,
  `app_name` varchar(100) NOT NULL,
  `request_reference` varchar(150) NOT NULL,
  `request_method` varchar(20) DEFAULT NULL,
  `request_resource` varchar(500) DEFAULT NULL,
  `request_parameters` text,
  `request_headers` text,
  `request_body` longtext,
  `request_origin_ip` varchar(50) NOT NULL,
  `request_status` enum('PENDING','SUCCESS','ERROR') NOT NULL,
  `response_headers` text,
  `response_body` longtext,
  `error_code` varchar(50) DEFAULT NULL,
  `error_message` longtext,
  `error_stacktrace` longtext,
  `date_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`request_id`),
  UNIQUE KEY `uindex_request_logs_app_code_request_reference` (`app_code`,`request_reference`),
  KEY `index_request_logs_app_name` (`app_name`),
  KEY `index_request_logs_request_method` (`request_method`),
  KEY `index_request_logs_request_resource` (`request_resource`),
  KEY `index_request_logs_request_id` (`request_id`),
  KEY `index_request_logs_request_origin_ip` (`request_origin_ip`),
  KEY `index_request_logs_request_status` (`request_status`),
  KEY `index_request_logs_error_code` (`error_code`),
  KEY `index_request_logs_date_created` (`date_created`),
  KEY `index_request_logs_date_modified` (`date_modified`),
  CONSTRAINT `fk_applications_request_logs_app_code` FOREIGN KEY (`app_code`) REFERENCES `applications` (`app_code`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_logs`
--

LOCK TABLES `request_logs` WRITE;
/*!40000 ALTER TABLE `request_logs` DISABLE KEYS */;
INSERT INTO `request_logs` VALUES (1,101,'ATM BRIDGE','f57c8495-dcd2-11ec-a62f-00505628d96b','POST','/resource/uri','appCode=101&filter=NONE','Authorization: Bearer Token','Create & Set up Database for Gibbon\n       - CREATE DATABASE mcec_portal_db;\n       - CREATE USER \'mcec_portal\'@\'192.168.140.142\' IDENTIFIED BY \'MC3C#P02t4l@22!\';\n       - GRANT ALL ON mcec_portal_db.* TO \'mcec_portal\'@\'192.168.140.142\';\n       - FLUSH PRIVILEGES;','192.168.2.128','PENDING',NULL,NULL,NULL,NULL,NULL,'2022-05-26 12:06:44','2022-05-26 12:09:54'),(3,201,'MEMBERS PORTAL','f57c8495-dcd2-11ec-a62f-00505628d96b-2','POST','/resource/uri','appCode=101&filter=NONE','Authorization: Bearer Token','Create & Set up Database for Gibbon\n       - CREATE DATABASE mcec_portal_db;\n       - CREATE USER \'mcec_portal\'@\'192.168.140.142\' IDENTIFIED BY \'MC3C#P02t4l@22!\';\n       - GRANT ALL ON mcec_portal_db.* TO \'mcec_portal\'@\'192.168.140.142\';\n       - FLUSH PRIVILEGES;','192.168.2.128','ERROR','Authorization: Bearer Token','<VirtualHost *:80>\n    #ServerName example.com\n    #ServerAlias www.example.com\n    DocumentRoot /var/www/applications/gibbon-edu-core-v23.0.02\n\n    <Directory /var/www/applications/gibbon-edu-core-v23.0.02>\n	Options -Indexes +FollowSymLinks +MultiViews\n	AllowOverride All\n	Require all granted\n    </Directory>\n\n    <FilesMatch \\.php$>\n	SetHandler \"proxy:unix:/var/run/php/php8.0-fpm.sock|fcgi://localhost\"\n    </FilesMatch>\n\n    ErrorLog ${APACHE_LOG_DIR}/gibbon-error.log\n    CustomLog ${APACHE_LOG_DIR}/gibbon-access.log combined\n\n</VirtualHost>','E1005','Error Processing Request','sudo vi /etc/php/8.0/fpm/php.ini\n   		- memory_limit = 512M\n   		- date.timezone = Africa/Nairobi\n   		- max_file_uploads = 100\n   		- magic_quotes_gpc = Off\n   		- register_globals = Off\n   		- short_open_tag = On\n   		- allow_url_fopen = On\n   		- max_input_vars = 10000\n       - sudo systemctl restart php8.0-fpm','2022-05-26 12:09:17','2022-05-26 12:09:17'),(4,101,'ATM BRIDGE','f57c8495-dcd2-11ec-a62f-00505628d96b-2','POST','/resource/uri','appCode=101&filter=NONE','Authorization: Bearer Token','Create & Set up Database for Gibbon\n       - CREATE DATABASE mcec_portal_db;\n       - CREATE USER \'mcec_portal\'@\'192.168.140.142\' IDENTIFIED BY \'MC3C#P02t4l@22!\';\n       - GRANT ALL ON mcec_portal_db.* TO \'mcec_portal\'@\'192.168.140.142\';\n       - FLUSH PRIVILEGES;','192.168.2.128','ERROR','Authorization: Bearer Token','<VirtualHost *:80>\n    #ServerName example.com\n    #ServerAlias www.example.com\n    DocumentRoot /var/www/applications/gibbon-edu-core-v23.0.02\n\n    <Directory /var/www/applications/gibbon-edu-core-v23.0.02>\n	Options -Indexes +FollowSymLinks +MultiViews\n	AllowOverride All\n	Require all granted\n    </Directory>\n\n    <FilesMatch \\.php$>\n	SetHandler \"proxy:unix:/var/run/php/php8.0-fpm.sock|fcgi://localhost\"\n    </FilesMatch>\n\n    ErrorLog ${APACHE_LOG_DIR}/gibbon-error.log\n    CustomLog ${APACHE_LOG_DIR}/gibbon-access.log combined\n\n</VirtualHost>','E1005','Error Processing Request','sudo vi /etc/php/8.0/fpm/php.ini\n   		- memory_limit = 512M\n   		- date.timezone = Africa/Nairobi\n   		- max_file_uploads = 100\n   		- magic_quotes_gpc = Off\n   		- register_globals = Off\n   		- short_open_tag = On\n   		- allow_url_fopen = On\n   		- max_input_vars = 10000\n       - sudo systemctl restart php8.0-fpm','2022-05-26 12:09:54','2022-05-26 12:09:54'),(5,101,'ATM BRIDGE','f57c8495-dcd2-11ec-a62f-00505628d96b-3','POST','/resource/uri','appCode=101&filter=NONE','Authorization: Bearer Token','Create & Set up Database for Gibbon\n       - CREATE DATABASE mcec_portal_db;\n       - CREATE USER \'mcec_portal\'@\'192.168.140.142\' IDENTIFIED BY \'MC3C#P02t4l@22!\';\n       - GRANT ALL ON mcec_portal_db.* TO \'mcec_portal\'@\'192.168.140.142\';\n       - FLUSH PRIVILEGES;','192.168.2.128','SUCCESS','Authorization: Bearer Token','<VirtualHost *:80>\n    #ServerName example.com\n    #ServerAlias www.example.com\n    DocumentRoot /var/www/applications/gibbon-edu-core-v23.0.02\n\n    <Directory /var/www/applications/gibbon-edu-core-v23.0.02>\n	Options -Indexes +FollowSymLinks +MultiViews\n	AllowOverride All\n	Require all granted\n    </Directory>\n\n    <FilesMatch \\.php$>\n	SetHandler \"proxy:unix:/var/run/php/php8.0-fpm.sock|fcgi://localhost\"\n    </FilesMatch>\n\n    ErrorLog ${APACHE_LOG_DIR}/gibbon-error.log\n    CustomLog ${APACHE_LOG_DIR}/gibbon-access.log combined\n\n</VirtualHost>',NULL,NULL,NULL,'2022-05-26 12:10:57','2022-05-26 12:12:43');
/*!40000 ALTER TABLE `request_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_settings`
--

DROP TABLE IF EXISTS `system_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_settings` (
  `system_settings_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `system_settings_type` varchar(50) NOT NULL,
  `system_settings_name` varchar(50) NOT NULL,
  `system_settings_xml` longtext,
  `created_by_user_id` bigint unsigned NOT NULL,
  `updated_by_user_id` bigint unsigned NOT NULL,
  `date_created` datetime NOT NULL,
  `date_modified` datetime NOT NULL,
  PRIMARY KEY (`system_settings_id`),
  UNIQUE KEY `uindex_system_settings_scope_identifier_type_name` (`system_settings_type`,`system_settings_name`),
  KEY `index_system_settings_type` (`system_settings_type`),
  KEY `index_system_settings_name` (`system_settings_name`),
  KEY `index_system_settings_created_by_user_id` (`created_by_user_id`),
  KEY `index_system_settings_date_created` (`date_created`),
  KEY `index_system_settings_date_modified` (`date_modified`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_settings`
--

LOCK TABLES `system_settings` WRITE;
/*!40000 ALTER TABLE `system_settings` DISABLE KEYS */;
INSERT INTO `system_settings` VALUES (1,'PASSWORD_POLICY','Password Policy','<PASSWORD_POLICY>\n    <REGEX><![CDATA[^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*;#?&^()-+={}\\[\\]\"\'\\/]{8,}$]]></REGEX>\n    <DESCRIPTION>Your password should be a minimum of 8 characters long, contain at least 1 upper case letter, 1 lower case letter and 1 number</DESCRIPTION>\n    <OTHER_DETAILS />\n</PASSWORD_POLICY>',1,1,'2022-05-25 17:43:43','2022-05-25 17:43:45');
/*!40000 ALTER TABLE `system_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_accounts`
--

DROP TABLE IF EXISTS `user_accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_accounts` (
  `user_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_status` enum('ACTIVE','LOCKED','INACTIVE','SUSPENDED') NOT NULL,
  `user_status_description` varchar(200) DEFAULT NULL,
  `user_status_date` datetime DEFAULT NULL,
  `account_access_mode` enum('API','INTERACTIVE','HYBRID') NOT NULL,
  `username` varchar(200) NOT NULL,
  `first_name` varchar(200) NOT NULL,
  `last_name` varchar(200) NOT NULL,
  `gender` varchar(30) NOT NULL,
  `designation` varchar(30) NOT NULL,
  `mobile_number` bigint unsigned NOT NULL,
  `email_address` varchar(200) NOT NULL,
  `user_pwd_status` enum('ACTIVE','RESET','EXPIRED') NOT NULL,
  `user_pwd` varchar(64) NOT NULL,
  `user_pwd_status_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
  `tracking_id` varchar(64) DEFAULT NULL,
  `tracking_source_ip` varchar(100) DEFAULT NULL,
  `tracking_url` varchar(200) DEFAULT NULL,
  `tracking_time` datetime DEFAULT NULL,
  `tracking_referrer` varchar(200) DEFAULT NULL,
  `date_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uindex_user_accounts_email_address` (`email_address`),
  UNIQUE KEY `uindex_user_accounts_username` (`username`),
  KEY `index_user_accounts_user_status` (`user_status`),
  KEY `index_user_accounts_user_status_description` (`user_status_description`),
  KEY `index_user_accounts_user_status_date` (`user_status_date`),
  KEY `index_user_accounts_account_access_mode` (`account_access_mode`),
  KEY `index_user_accounts_username` (`username`),
  KEY `index_user_accounts_first_name` (`first_name`),
  KEY `index_user_accounts_last_name` (`last_name`),
  KEY `index_user_accounts_gender` (`gender`),
  KEY `index_user_accounts_designation` (`designation`),
  KEY `index_user_accounts_mobile_number` (`mobile_number`),
  KEY `index_user_accounts_email_address` (`email_address`),
  KEY `index_user_accounts_user_pwd_status` (`user_pwd_status`),
  KEY `index_user_accounts_user_pwd` (`user_pwd`),
  KEY `index_user_accounts_user_pwd_status_date` (`user_pwd_status_date`),
  KEY `index_user_accounts_login_attempts` (`login_attempts`),
  KEY `index_user_accounts_max_login_attempts` (`max_login_attempts`),
  KEY `index_user_accounts_allowed_access_sources_status` (`allowed_access_sources_status`),
  KEY `index_user_accounts_allowed_access_sources_match_type` (`allowed_access_sources_match_type`),
  KEY `index_user_accounts_max_allowed_access_sources` (`max_allowed_access_sources`),
  KEY `index_user_accounts_allowed_access_sources` (`allowed_access_sources`),
  KEY `index_user_accounts_restricted_access_sources_status` (`restricted_access_sources_status`),
  KEY `index_user_accounts_restricted_access_sources_match_type` (`restricted_access_sources_match_type`),
  KEY `index_user_accounts_max_restricted_access_sources` (`max_restricted_access_sources`),
  KEY `index_user_accounts_restricted_access_sources` (`restricted_access_sources`),
  KEY `index_user_accounts_tracking_id` (`tracking_id`),
  KEY `index_user_accounts_tracking_source_ip` (`tracking_source_ip`),
  KEY `index_user_accounts_tracking_url` (`tracking_url`),
  KEY `index_user_accounts_tracking_time` (`tracking_time`),
  KEY `index_user_accounts_tracking_referrer` (`tracking_referrer`),
  KEY `index_user_accounts_date_created` (`date_created`),
  KEY `index_user_accounts_date_modified` (`date_modified`),
  CONSTRAINT `fk_designations_user_accounts_designation` FOREIGN KEY (`designation`) REFERENCES `designations` (`designation`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_genders_user_accounts_gender` FOREIGN KEY (`gender`) REFERENCES `genders` (`gender`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_accounts`
--

LOCK TABLES `user_accounts` WRITE;
/*!40000 ALTER TABLE `user_accounts` DISABLE KEYS */;
INSERT INTO `user_accounts` VALUES (1,'ACTIVE','ACTIVE','2022-05-25 17:03:05','INTERACTIVE','johnkarani@skyworld.co.ke','John','Karani','MALE','ICT Manager',254721303295,'johnkarani@skyworld.co.ke','ACTIVE','37117c2634965b85a96ee09ba319bbcad708f24228e83d8caee9ac276c7beb44','2022-05-26 12:16:36',0,5,'INACTIVE','STRING',5,'192.168.2.128,0:0:0:0:0:0:0:1','INACTIVE','STRING',5,'192.168.2.128,0:0:0:0:0:0:0:0:1','oge0vnstp9issbnhlpwgljhdpbluzzog','0:0:0:0:0:0:0:1','/','2022-05-26 12:16:36','/','2022-05-25 17:06:47','2022-05-26 12:16:36');
/*!40000 ALTER TABLE `user_accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_app_user_groups`
--

DROP TABLE IF EXISTS `user_app_user_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_app_user_groups` (
  `user_app_user_groups_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `group_id` int NOT NULL,
  `date_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_app_user_groups_id`),
  UNIQUE KEY `unidex_admin_user_app_user_groups_user_id_group_id` (`user_id`,`group_id`),
  KEY `index_admin_user_app_user_groups_user_id` (`user_id`),
  KEY `index_admin_user_app_user_groups_group_id` (`group_id`),
  KEY `index_admin_user_app_user_groups_date_created` (`date_created`),
  KEY `index_admin_user_app_user_groups_date_modified` (`date_modified`),
  CONSTRAINT `fkey_admin_user_app_user_groups_group_id` FOREIGN KEY (`group_id`) REFERENCES `app_user_groups` (`group_id`) ON UPDATE CASCADE,
  CONSTRAINT `fkey_admin_user_app_user_groups_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_accounts` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_app_user_groups`
--

LOCK TABLES `user_app_user_groups` WRITE;
/*!40000 ALTER TABLE `user_app_user_groups` DISABLE KEYS */;
INSERT INTO `user_app_user_groups` VALUES (1,1,1,'2022-05-25 17:07:05','2022-05-25 17:07:05'),(2,1,2,'2022-05-25 17:07:05','2022-05-25 17:07:05');
/*!40000 ALTER TABLE `user_app_user_groups` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-26 12:19:59
