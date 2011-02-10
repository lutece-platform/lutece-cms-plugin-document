--
-- Structure for table document_comment
--
DROP TABLE IF EXISTS document_comment;
CREATE TABLE document_comment (
	id_comment int default 0 NOT NULL,
	id_document int default 0 NOT NULL,
	date_comment timestamp default CURRENT_TIMESTAMP NOT NULL,
	document_comment_name varchar(255) NOT NULL,
	email varchar(255) NOT NULL,
	ip_address varchar(100) NOT NULL,
	document_comment_comment long varchar NOT NULL,
	status int default 0 NOT NULL,
	PRIMARY KEY (id_comment)
);
