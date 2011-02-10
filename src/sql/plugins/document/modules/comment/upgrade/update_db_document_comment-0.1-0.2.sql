--
-- Table document_comment
--
ALTER TABLE document_comment CHANGE COLUMN name document_comment_name varchar(255) NOT NULL;
ALTER TABLE document_comment CHANGE COLUMN comment document_comment_comment long varchar NOT NULL;