-- liquibase formatted sql
-- changeset document:update_db_document-6.0.0-6.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- Cache of the downloaded files disabled by default: each entry holds a whole file, every read copied it, and with
-- the default cache size of the core it could fill the heap. The browser cache and a caching proxy serve the files.
DELETE FROM core_datastore WHERE entity_key = 'core.cache.status.DocumentResourceServletCache.enabled';
INSERT INTO core_datastore (entity_key, entity_value) VALUES ('core.cache.status.DocumentResourceServletCache.enabled', '0');
