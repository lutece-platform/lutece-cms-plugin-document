-- liquibase formatted sql
-- changeset document:update_db_document-2.0.12-2.0.13.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO document_attribute_type (code_attribute_type,name_key,description_key,manager_class) VALUES 
('internallink','document.attributeType.internallink.name','document.attributeType.internallink.description','fr.paris.lutece.plugins.document.service.attributes.InternalLinkManager');
