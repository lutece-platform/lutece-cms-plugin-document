DROP TABLE IF EXISTS document;
CREATE TABLE document (
	id_document int default 0 NOT NULL,
	code_document_type varchar(30) default NULL,
	date_creation timestamp default CURRENT_TIMESTAMP NOT NULL,
	date_modification timestamp default '0000-00-00 00:00:00' NOT NULL,
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
	PRIMARY KEY (id_document)
);