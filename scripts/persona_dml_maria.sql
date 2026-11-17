INSERT INTO 
	`persona_db`.`persona`(`cc`,`nombre`,`apellido`,`genero`,`edad`) 
VALUES
	(123456789,'Pepe','Perez','M',30),
	(987654321,'Pepito','Perez','M',null),
	(321654987,'Pepa','Juarez','F',30),
	(147258369,'Pepita','Juarez','F',10),
	(963852741,'Fede','Perez','M',18);

INSERT INTO `persona_db`.`profesion` (`id`, `nom`, `des`)
VALUES
	(1, 'Ingenieria de sistemas', 'Desarrollo de software'),
	(2, 'Medicina', 'Atencion de salud'),
	(3, 'Docencia', 'Ensenanza'),
	(4, 'Administracion', 'Gestion de organizaciones'),
	(5, 'Contaduria', 'Gestion financiera y contable');

INSERT INTO `persona_db`.`telefono` (`num`, `oper`, `duenio`)
VALUES
	('3000000001', 'Claro', 123456789),
	('3000000002', 'Movistar', 987654321),
	('3000000003', 'Tigo', 321654987),
	('3000000004', 'Claro', 147258369),
	('3000000005', 'Movistar', 963852741);

INSERT INTO `persona_db`.`estudios` (`id_prof`, `cc_per`, `fecha`, `univer`)
VALUES
	(1, 123456789, '2018-06-15', 'Universidad Central'),
	(2, 987654321, '2019-12-10', 'Universidad Nacional'),
	(3, 321654987, '2020-06-20', 'Universidad Pedagogica'),
	(4, 123456789, '2021-06-18', 'Universidad Central'),
	(5, 987654321, '2022-12-09', 'Universidad Nacional');