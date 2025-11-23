DELETE FROM users;

INSERT INTO users (id, email, password_hash, nombre, apellido, rol, activo, fecha_registro) VALUES
(1, 'soporte@gmail.com', '$2a$10$3yA.gR/q3G5i.yX.eS4gpeZk1A3yRzN5X.Vf.cW.cW.cW.cW.cW.c', 'Soporte', '', 'SUPPORT', true, NOW()),
(2, 'admin@gmail.com',   '$2a$10$N0tF6gC.e2bV.gR/q3G5i.yX.eS4gpeZk1A3yRzN5X.Vf.cW.cW.c', 'Admin',   '', 'ADMIN',   true, NOW()),
(3, 'luc@gmail.com',     '$2a$10$gR/q3G5i.yX.eS4gpeZk1A3yRzN5X.Vf.cW.cW.cW.cW.cW.cW.c', 'Lucas',   '', 'USER',    true, NOW());
