--
-- Table document_content
--
ALTER TABLE document_content ADD validated  smallint default 0 NOT NULL;
ALTER TABLE document_content DROP PRIMARY KEY , ADD PRIMARY KEY (id_document,id_document_attr, validated);
