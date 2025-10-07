-- liquibase formatted sql
-- changeset document:update_db_core_document-2.2.1-2.2.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Init  table core_dashboard
--
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('DOCUMENT', 3, 1);
