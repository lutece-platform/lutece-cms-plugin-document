--
-- Table document
--

/* Add skip HTML generation columns */
ALTER TABLE document ADD COLUMN skip_portlet    SMALLINT NOT NULL DEFAULT 0 AFTER id_page_template_document;
ALTER TABLE document ADD COLUMN skip_categories SMALLINT NOT NULL DEFAULT 0 AFTER skip_portlet;
