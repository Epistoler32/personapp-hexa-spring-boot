package co.edu.javeriana.as.personapp.terminal.menu;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Controller;

import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.adapter.CrudInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CrudMenu {
	private final CrudInputAdapterCli adapter;

	public CrudMenu(CrudInputAdapterCli adapter) {
		this.adapter = adapter;
	}

	public void start(Scanner input) {
		boolean exit = false;
		while (!exit) {
			System.out.println("\n1 Persona  2 Profesion  3 Telefono  4 Estudio  0 Salir");
			int entity = readInt(input, "Modulo: ");
			if (entity == 0) {
				exit = true;
			} else if (entity >= 1 && entity <= 4) {
				runEntity(input, entity);
			} else {
				System.out.println("Opcion invalida");
			}
		}
	}

	private void runEntity(Scanner input, int entity) {
		String database = chooseDatabase(input);
		if (database == null) {
			return;
		}
		boolean back = false;
		while (!back) {
			System.out.println("\n1 GET todos  2 GET por id  3 GET conteo  4 POST  5 PUT  6 DELETE  0 Volver");
			int operation = readInt(input, "Operacion: ");
			try {
				switch (operation) {
				case 0: back = true; break;
				case 1: list(database, entity); break;
				case 2: find(input, database, entity); break;
				case 3: count(database, entity); break;
				case 4: create(input, database, entity); break;
				case 5: update(input, database, entity); break;
				case 6: delete(input, database, entity); break;
				default: System.out.println("Opcion invalida");
				}
			} catch (NoExistException exception) {
				System.out.println("Error: " + exception.getMessage());
			} catch (RuntimeException exception) {
				log.error("CLI CRUD operation failed", exception);
				System.out.println("La operacion fallo: " + exception.getMessage());
			}
		}
	}

	private void list(String database, int entity) {
		switch (entity) {
		case 1: print(adapter.person(database).findAll()); break;
		case 2: print(adapter.profession(database).findAll()); break;
		case 3: print(adapter.phone(database).findAll()); break;
		default: print(adapter.study(database).findAll());
		}
	}

	private void find(Scanner input, String database, int entity) throws NoExistException {
		switch (entity) {
		case 1: System.out.println(adapter.person(database).findOne(readInt(input, "Identificacion: "))); break;
		case 2: System.out.println(adapter.profession(database).findOne(readInt(input, "Id profesion: "))); break;
		case 3: System.out.println(adapter.phone(database).findOne(readText(input, "Numero: "))); break;
		default: System.out.println(adapter.study(database).findOne(readInt(input, "Id persona: "),
				readInt(input, "Id profesion: ")));
		}
	}

	private void count(String database, int entity) {
		switch (entity) {
		case 1: System.out.println("Personas: " + adapter.person(database).count()); break;
		case 2: System.out.println("Profesiones: " + adapter.profession(database).count()); break;
		case 3: System.out.println("Telefonos: " + adapter.phone(database).count()); break;
		default: System.out.println("Estudios: " + adapter.study(database).count());
		}
	}

	private void create(Scanner input, String database, int entity) {
		switch (entity) {
		case 1: System.out.println(adapter.person(database).create(readPerson(input, null))); break;
		case 2: System.out.println(adapter.profession(database).create(readProfession(input, null))); break;
		case 3: System.out.println(adapter.phone(database).create(readPhone(input, null))); break;
		default: System.out.println(adapter.study(database).create(readStudy(input, null, null)));
		}
	}

	private void update(Scanner input, String database, int entity) throws NoExistException {
		switch (entity) {
		case 1: {
			Integer id = readInt(input, "Identificacion a actualizar: ");
			System.out.println(adapter.person(database).edit(id, readPerson(input, id)));
			break;
		}
		case 2: {
			Integer id = readInt(input, "Id profesion a actualizar: ");
			System.out.println(adapter.profession(database).edit(id, readProfession(input, id)));
			break;
		}
		case 3: {
			String number = readText(input, "Numero a actualizar: ");
			System.out.println(adapter.phone(database).edit(number, readPhone(input, number)));
			break;
		}
		default: {
			Integer personId = readInt(input, "Id persona: ");
			Integer professionId = readInt(input, "Id profesion: ");
			System.out.println(adapter.study(database).edit(personId, professionId,
					readStudy(input, personId, professionId)));
		}
		}
	}

	private void delete(Scanner input, String database, int entity) throws NoExistException {
		boolean deleted;
		switch (entity) {
		case 1: deleted = adapter.person(database).drop(readInt(input, "Identificacion: ")); break;
		case 2: deleted = adapter.profession(database).drop(readInt(input, "Id profesion: ")); break;
		case 3: deleted = adapter.phone(database).drop(readText(input, "Numero: ")); break;
		default: deleted = adapter.study(database).drop(readInt(input, "Id persona: "),
				readInt(input, "Id profesion: "));
		}
		System.out.println(deleted ? "Eliminado" : "No se pudo eliminar");
	}

	private Person readPerson(Scanner input, Integer existingId) {
		Integer id = existingId == null ? readInt(input, "Identificacion: ") : existingId;
		String first = readText(input, "Nombre: ");
		String last = readText(input, "Apellido: ");
		String genderText;
		do {
			genderText = readText(input, "Genero (F/M): ");
			if (!"F".equalsIgnoreCase(genderText) && !"M".equalsIgnoreCase(genderText)) {
				System.out.println("El genero debe ser F o M");
			}
		} while (!"F".equalsIgnoreCase(genderText) && !"M".equalsIgnoreCase(genderText));
		Integer age = readInt(input, "Edad (>=0): ");
		while (age < 0) {
			System.out.println("La edad no puede ser negativa");
			age = readInt(input, "Edad (>=0): ");
		}
		Person person = new Person(id, first, last, "F".equalsIgnoreCase(genderText) ? Gender.FEMALE : Gender.MALE);
		person.setAge(age);
		return person;
	}

	private Profession readProfession(Scanner input, Integer existingId) {
		Integer id = existingId == null ? readInt(input, "Id profesion: ") : existingId;
		return new Profession(id, readText(input, "Nombre: "), readText(input, "Descripcion: "), null);
	}

	private Phone readPhone(Scanner input, String existingNumber) {
		String number = existingNumber == null ? readText(input, "Numero: ") : existingNumber;
		String company = readText(input, "Operador: ");
		Person owner = new Person();
		owner.setIdentification(readInt(input, "Identificacion propietario: "));
		return new Phone(number, company, owner);
	}

	private Study readStudy(Scanner input, Integer personId, Integer professionId) {
		if (personId == null) personId = readInt(input, "Id persona: ");
		if (professionId == null) professionId = readInt(input, "Id profesion: ");
		Person person = new Person();
		person.setIdentification(personId);
		Profession profession = new Profession();
		profession.setIdentification(professionId);
		String dateText = readText(input, "Fecha graduacion (AAAA-MM-DD, vacio si no aplica): ");
		LocalDate date = dateText.isEmpty() ? null : LocalDate.parse(dateText);
		String university = readText(input, "Universidad: ");
		return new Study(person, profession, date, university);
	}

	private String chooseDatabase(Scanner input) {
		System.out.println("1 MariaDB  2 MongoDB  0 Volver");
		switch (readInt(input, "Base de datos: ")) {
		case 1: return "MARIA";
		case 2: return "MONGO";
		default: return null;
		}
	}

	private int readInt(Scanner input, String prompt) {
		while (true) {
			String value = readText(input, prompt);
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException exception) {
				System.out.println("Ingrese un numero entero");
			}
		}
	}

	private String readText(Scanner input, String prompt) {
		System.out.print(prompt);
		return input.nextLine().trim();
	}

	private void print(List<?> values) {
		if (values.isEmpty()) {
			System.out.println("Sin registros");
		} else {
			values.forEach(System.out::println);
		}
	}
}
