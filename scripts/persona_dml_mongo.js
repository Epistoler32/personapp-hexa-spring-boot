db = db.getSiblingDB("persona_db");

db.persona.insertMany([
	{
		"_id": NumberInt(123456789),
		"nombre": "Pepe",
		"apellido": "Perez",
		"genero": "M",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(987654321),
		"nombre": "Pepito",
		"apellido": "Perez",
		"genero": "M",
		"edad": NumberInt(30),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(321654987),
		"nombre": "Pepa",
		"apellido": "Juarez",
		"genero": "F",
		"edad": NumberInt(30),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(147258369),
		"nombre": "Pepita",
		"apellido": "Juarez",
		"genero": "F",
		"edad": NumberInt(10),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	},
	{
		"_id": NumberInt(963852741),
		"nombre": "Fede",
		"apellido": "Perez",
		"genero": "M",
		"edad": NumberInt(18),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument"
	}
], { ordered: false })

db.profesion.insertMany([
	{
		"_id": NumberInt(1),
		"nom": "Ingenieria de sistemas",
		"des": "Desarrollo de software",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument"
	},
	{
		"_id": NumberInt(2),
		"nom": "Medicina",
		"des": "Atencion de salud",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument"
	},
	{
		"_id": NumberInt(3),
		"nom": "Docencia",
		"des": "Ensenanza",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument"
	},
	{
		"_id": NumberInt(4),
		"nom": "Administracion",
		"des": "Gestion de organizaciones",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument"
	},
	{
		"_id": NumberInt(5),
		"nom": "Contaduria",
		"des": "Gestion financiera y contable",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument"
	}
])

db.telefono.insertMany([
	{
		"_id": "3000000001",
		"oper": "Claro",
		"primaryDuenio": NumberInt(123456789),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument"
	},
	{
		"_id": "3000000002",
		"oper": "Movistar",
		"primaryDuenio": NumberInt(987654321),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument"
	},
	{
		"_id": "3000000003",
		"oper": "Tigo",
		"primaryDuenio": NumberInt(321654987),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument"
	},
	{
		"_id": "3000000004",
		"oper": "Claro",
		"primaryDuenio": NumberInt(147258369),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument"
	},
	{
		"_id": "3000000005",
		"oper": "Movistar",
		"primaryDuenio": NumberInt(963852741),
		"_class": "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument"
	}
])

db.estudios.insertMany([
	{
		"_id": "123456789-1",
		"primaryPersona": NumberInt(123456789),
		"primaryProfesion": NumberInt(1),
		"fecha": ISODate("2018-06-15T05:00:00Z"),
		"univer": "Universidad Central",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument"
	},
	{
		"_id": "987654321-2",
		"primaryPersona": NumberInt(987654321),
		"primaryProfesion": NumberInt(2),
		"fecha": ISODate("2019-12-10T05:00:00Z"),
		"univer": "Universidad Nacional",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument"
	},
	{
		"_id": "321654987-3",
		"primaryPersona": NumberInt(321654987),
		"primaryProfesion": NumberInt(3),
		"fecha": ISODate("2020-06-20T05:00:00Z"),
		"univer": "Universidad Pedagogica",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument"
	},
	{
		"_id": "123456789-4",
		"primaryPersona": NumberInt(123456789),
		"primaryProfesion": NumberInt(4),
		"fecha": ISODate("2021-06-18T05:00:00Z"),
		"univer": "Universidad Central",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument"
	},
	{
		"_id": "987654321-5",
		"primaryPersona": NumberInt(987654321),
		"primaryProfesion": NumberInt(5),
		"fecha": ISODate("2022-12-09T05:00:00Z"),
		"univer": "Universidad Nacional",
		"_class": "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument"
	}
])
