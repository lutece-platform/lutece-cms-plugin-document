-- liquibase formatted sql
-- changeset document:update_db_document-2.0.17-2.0.18.sql
-- preconditions onFail:MARK_RAN onError:WARN
﻿--
-- Table document
--
ALTER TABLE document CHANGE COLUMN summary document_summary long varchar;
ALTER TABLE document CHANGE COLUMN comment document_comment long varchar;

--
-- Table document_attribute_type
--
RENAME TABLE document_attribute_type TO document_attr_type;
ALTER TABLE document_attr_type CHANGE COLUMN code_attribute_type code_attr_type varchar(30) default '' NOT NULL;

--
-- Table document_attribute_type_parameter
--
RENAME TABLE document_attribute_type_parameter TO document_attr_type_parameter;
ALTER TABLE document_attr_type_parameter CHANGE COLUMN code_attribute_type code_attr_type varchar(50) default '' NOT NULL;

--
-- Table document_content
--
ALTER TABLE document_content CHANGE COLUMN id_document_attribute id_document_attr int default 0 NOT NULL;

--
-- Table document_rule_attributes
--
RENAME TABLE document_rule_attributes TO document_rule_attr;
ALTER TABLE document_rule_attr CHANGE COLUMN attribute_name attr_name varchar(255) default '' NOT NULL;
ALTER TABLE document_rule_attr CHANGE COLUMN attribute_value attr_value varchar(255) default NULL;

--
-- Table document_space
--
ALTER TABLE document_space CHANGE COLUMN view document_space_view varchar(20) default NULL;
ALTER TABLE document_space CHANGE COLUMN name document_space_name varchar(100) default NULL;

--
-- Table document_type
--
ALTER TABLE document_type CHANGE COLUMN thumbnail_attribute_id thumbnail_attr_id int default NULL;
ALTER TABLE document_type CHANGE COLUMN name document_type_name varchar(100) default NULL;

--
-- Table document_type_attributes
--
RENAME TABLE document_type_attributes TO document_type_attr;
ALTER TABLE document_type_attr CHANGE COLUMN id_document_attribute id_document_attr int default 0 NOT NULL;
ALTER TABLE document_type_attr CHANGE COLUMN code_attribute_type code_attr_type varchar(50) default NULL;
ALTER TABLE document_type_attr CHANGE COLUMN attribute_order attr_order int default NULL;
ALTER TABLE document_type_attr CHANGE COLUMN name document_type_attr_name varchar(100) default NULL;

--
-- Table document_type_attributes_parameters
--
RENAME TABLE document_type_attributes_parameters TO document_type_attr_parameters;
ALTER TABLE document_type_attr_parameters CHANGE COLUMN id_document_attribute id_document_attr int default 0 NOT NULL;

--
-- Table document_type_attributes_verify_by
--
RENAME TABLE document_type_attributes_verify_by TO document_type_attr_verify_by;
ALTER TABLE document_type_attr_verify_by CHANGE COLUMN id_document_attribute id_document_attr int default 0 NOT NULL;

--
-- Table document_category
--
ALTER TABLE document_category CHANGE COLUMN name document_category_name varchar(100) NOT NULL;