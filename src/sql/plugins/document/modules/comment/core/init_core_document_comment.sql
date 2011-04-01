--
-- Dumping data for table core_admin_role
--
INSERT INTO core_admin_role (role_key,role_description) VALUES ('comment','Modération des commentaires de documents');


--
-- Dumping data for table core_admin_role_resource
--
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (106,'comment','DOCUMENT_COMMENT','*','COMMENT');


--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('comment',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('comment',2);

--
-- Init  table core_dashboard
--
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('DOCUMENT_COMMENT', 3, 1);
