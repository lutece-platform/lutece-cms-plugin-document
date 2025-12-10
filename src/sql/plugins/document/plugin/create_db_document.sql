-- liquibase formatted sql
-- changeset document:create_db_document.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Structure for table document
--
DROP TABLE IF EXISTS document;
CREATE TABLE document (
	id_document int AUTO_INCREMENT,
	code_document_type varchar(30) default NULL,
	date_creation timestamp default CURRENT_TIMESTAMP NOT NULL,
	date_modification timestamp default '1980-01-01 00:00:00' NOT NULL,
	title varchar(255) default NULL,
	id_space int default NULL,
	id_state int default NULL,
	xml_working_content long varchar,
	xml_validated_content long varchar,
	document_summary long varchar,
	document_comment long varchar,
	date_validity_begin timestamp NULL,
	date_validity_end timestamp NULL,
	xml_metadata long varchar,
	id_creator int default NULL,
	id_mailinglist int default 0 NOT NULL,
	id_page_template_document int default 0 NOT NULL,
	skip_portlet    SMALLINT DEFAULT 0 NOT NULL,
	skip_categories SMALLINT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_document)
);

--
-- Structure for table document_attr_type
--
DROP TABLE IF EXISTS document_attr_type;
CREATE TABLE document_attr_type (
	code_attr_type varchar(30) default '' NOT NULL,
	name_key varchar(100) default NULL,
	description_key varchar(255) default NULL,
	manager_class varchar(255) default NULL,
	PRIMARY KEY (code_attr_type)
);

--
-- Structure for table document_attr_type_parameter
--
DROP TABLE IF EXISTS document_attr_type_parameter;
CREATE TABLE document_attr_type_parameter (
	code_attr_type varchar(50) default '' NOT NULL,
	parameter_name varchar(255) default NULL,
	parameter_label_key varchar(255) default NULL,
	parameter_index int default 0 NOT NULL,
	parameter_description_key varchar(255) default NULL,
	parameter_default_value long varchar NOT NULL,
	PRIMARY KEY (code_attr_type,parameter_index)
);

--
-- Table structure for table document_category
--
DROP TABLE IF EXISTS document_category;
CREATE TABLE document_category (
	id_category int AUTO_INCREMENT,
	document_category_name varchar(100) NOT NULL,
	description varchar(255) default NULL,
	icon_content long varbinary default NULL,
	icon_mime_type varchar(100) default NULL,
	workgroup_key varchar(50) default NULL,
	PRIMARY KEY (id_category)	
);

--
-- Table structure for table document_category_link
--
DROP TABLE IF EXISTS document_category_link;
CREATE TABLE document_category_link (
	id_document int NOT NULL,
	id_category int NOT NULL,
	PRIMARY KEY (id_document,id_category)
);

--
-- Table structure for table document_category_list_portlet
--
DROP TABLE IF EXISTS document_category_list_portlet;
CREATE TABLE document_category_list_portlet (
	id_portlet int NOT NULL,
	id_category int NOT NULL,
	PRIMARY KEY (id_portlet,id_category)
);

--
-- Structure for table document_content
--
DROP TABLE IF EXISTS document_content;
CREATE TABLE document_content (
	id_document int default 0 NOT NULL,
	id_document_attr int default 0 NOT NULL,
	validated smallint default 0 NOT NULL,
	text_value long varchar,
	mime_type varchar(255) default NULL,
	binary_value long varbinary,
	PRIMARY KEY (id_document,id_document_attr, validated)
);

--
-- Table structure for table document_history
--
DROP TABLE IF EXISTS document_history;
CREATE TABLE document_history (
	id_document int default 0 NOT NULL,
	event_date timestamp default CURRENT_TIMESTAMP NOT NULL,
	event_user varchar(100) default '' NOT NULL,
	event_message_key varchar(255) default '' NOT NULL,
	document_state_key varchar(100) default NULL,
	document_space varchar(255) default NULL
);
CREATE INDEX index_history_id_document ON document_history (id_document);
CREATE INDEX index_history_event_user ON document_history (event_user);

--
-- Table structure for table document_list_portlet
--
DROP TABLE IF EXISTS document_list_portlet;
CREATE TABLE document_list_portlet (
	id_portlet int NOT NULL,
	code_document_type varchar(30) default NULL,
	PRIMARY KEY (id_portlet)
);

--
-- Table structure for table document_portlet
--
DROP TABLE IF EXISTS document_portlet;
CREATE TABLE document_portlet (
	id_portlet int NOT NULL,
	code_document_type varchar(30) default NULL,
	PRIMARY KEY (id_portlet)
);

--
-- Table structure for table document_category_portlet
--
DROP TABLE IF EXISTS document_category_portlet;
CREATE TABLE document_category_portlet (
	id_portlet int NOT NULL,
	id_category int NOT NULL,
	PRIMARY KEY (id_portlet,id_category)
);

--
-- Table structure for table document_auto_publication
--
DROP TABLE IF EXISTS document_auto_publication;
CREATE TABLE document_auto_publication (
	id_portlet int default 0,
	id_space int default 0,
	PRIMARY KEY (id_portlet,id_space)
);

--
-- Table structure for table document_page_template
--
DROP TABLE IF EXISTS document_page_template;
CREATE TABLE document_page_template (
	id_page_template_document int AUTO_INCREMENT,
	page_template_path varchar(255) default NULL,
	picture_path varchar(255) default NULL,
	description varchar(255) default NULL,
	PRIMARY KEY (id_page_template_document)
);
  
--
-- Table structure for table document_published
--
DROP TABLE IF EXISTS document_published;
CREATE TABLE document_published (
	id_portlet int NOT NULL,
	id_document int NOT NULL,
	document_order int default NULL,
	status smallint default 1 NOT NULL,
	date_publishing timestamp default CURRENT_TIMESTAMP NOT NULL,
	PRIMARY KEY (id_portlet,id_document)
);

--
-- Table structure for table document_rule
--
DROP TABLE IF EXISTS document_rule;
CREATE TABLE document_rule (
	id_rule int AUTO_INCREMENT,
	rule_type varchar(50) NOT NULL,
	PRIMARY KEY (id_rule)
);

--
-- Table structure for table document_rule_attr
--
DROP TABLE IF EXISTS document_rule_attr;
CREATE TABLE document_rule_attr (
	id_rule int default 0 NOT NULL,
	attr_name varchar(255) default '' NOT NULL,
	attr_value varchar(255) default NULL,
	PRIMARY KEY (id_rule,attr_name)
);

--
-- Table structure for table document_space
--
DROP TABLE IF EXISTS document_space;
CREATE TABLE document_space (
	id_space int AUTO_INCREMENT,
	id_parent int default NULL,
	document_space_name varchar(100) default NULL,
	description varchar(255) default NULL,
	document_space_view varchar(20) default NULL,
	id_space_icon int default NULL,
	space_order int default NULL,
	document_creation_allowed int default NULL,
	workgroup_key varchar(50) default NULL,
	PRIMARY KEY (id_space)
);

--
-- Table structure for table document_space_action
--
DROP TABLE IF EXISTS document_space_action;
CREATE TABLE document_space_action (
	id_action int default 0 NOT NULL,
	name_key varchar(100) default NULL,
	description_key varchar(100) default NULL,
	action_url varchar(255) default NULL,
	icon_url varchar(255) default NULL,
	action_permission varchar(255) default NULL,
	PRIMARY KEY (id_action)
);

--
-- Table structure for table document_space_document_type
--
DROP TABLE IF EXISTS document_space_document_type;
CREATE TABLE document_space_document_type (
	id_space int default 0 NOT NULL,
	code_document_type varchar(30) default '' NOT NULL,
	PRIMARY KEY (id_space,code_document_type)
);

--
-- Table structure for table document_space_icon
--
DROP TABLE IF EXISTS document_space_icon;
CREATE TABLE document_space_icon (
	id_space_icon int default 0 NOT NULL,
	icon_url varchar(255) default NULL,
	PRIMARY KEY (id_space_icon)
);

--
-- Table structure for table document_type
--
DROP TABLE IF EXISTS document_type;
CREATE TABLE document_type (
	code_document_type varchar(30) default '' NOT NULL,
	document_type_name varchar(100) default NULL,
	description varchar(255) default NULL,
	thumbnail_attr_id int default NULL,
	default_thumbnail_url varchar(255) default NULL,
	admin_xsl long varbinary,
	content_service_xsl long varbinary,
	metadata_handler varchar(100) default NULL,
	PRIMARY KEY (code_document_type)
);

--
-- Table structure for table document_type_attr
--
DROP TABLE IF EXISTS document_type_attr;
CREATE TABLE document_type_attr (
	id_document_attr int AUTO_INCREMENT,
	code_document_type varchar(30) default NULL,
	code_attr_type varchar(50) default NULL,
	code varchar(50) default NULL,
	document_type_attr_name varchar(100) default NULL,
	description varchar(255) default NULL,
	attr_order int default NULL,
	required int default NULL,
	searchable int default NULL,
	PRIMARY KEY (id_document_attr)
);

--
-- Table structure for table document_type_attr_parameters
--
DROP TABLE IF EXISTS document_type_attr_parameters;
CREATE TABLE document_type_attr_parameters (
	id_document_attr int default 0 NOT NULL,
	parameter_name varchar(255) default '' NOT NULL,
	id_list_parameter int default 0 NOT NULL,
	parameter_value long varchar,
	PRIMARY KEY (id_document_attr,parameter_name,id_list_parameter)
);

--
-- Table structure for table document_view
--

DROP TABLE IF EXISTS document_view;
CREATE TABLE document_view (
	code_view varchar(20) default '' NOT NULL,
	name_key varchar(100) default NULL,
	PRIMARY KEY (code_view)
);

--
-- Table structure for table document_workflow_action
--

DROP TABLE IF EXISTS document_workflow_action;
CREATE TABLE document_workflow_action (
	id_action int default 0 NOT NULL,
	name_key varchar(100) default NULL,
	description_key varchar(100) default NULL,
	action_url varchar(255) default NULL,
	icon_url varchar(255) default NULL,
	action_permission varchar(255) default NULL,
	id_finish_state int default NULL,
	PRIMARY KEY (id_action)
);

--
-- Table structure for table document_workflow_state
--
DROP TABLE IF EXISTS document_workflow_state;
CREATE TABLE document_workflow_state (
	id_state int default 0 NOT NULL,
	name_key varchar(100) default NULL,
	description_key varchar(255) default NULL,
	state_order int default NULL,
	PRIMARY KEY (id_state)
);

--
-- Table structure for table document_workflow_transition
--
DROP TABLE IF EXISTS document_workflow_transition;
CREATE TABLE document_workflow_transition (
	id_state int default 0 NOT NULL,
	id_action int default 0 NOT NULL,
	PRIMARY KEY (id_state,id_action)
);

--
-- Table structure for table document_type_attr_verify_by
--
DROP TABLE IF EXISTS document_type_attr_verify_by;
CREATE TABLE document_type_attr_verify_by (
	id_document_attr int default 0 NOT NULL,
	id_expression int default 0 NOT NULL,
	PRIMARY KEY (id_document_attr,id_expression)
);

--
-- Table structure for table document_indexer_action
--
DROP TABLE IF EXISTS document_indexer_action;
CREATE TABLE document_indexer_action(
  id_action int AUTO_INCREMENT,
  id_record int default 0 NOT NULL,
  id_task int default 0 NOT NULL,
  PRIMARY KEY (id_action)  
);
