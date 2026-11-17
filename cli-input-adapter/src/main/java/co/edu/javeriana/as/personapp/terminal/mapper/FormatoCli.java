package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

public final class FormatoCli {

	private FormatoCli() {
	}

	public static String formatear(Object valor) {
		if (valor instanceof Person) {
			return persona((Person) valor);
		}
		if (valor instanceof Profession) {
			return profesion((Profession) valor);
		}
		if (valor instanceof Phone) {
			return telefono((Phone) valor);
		}
		if (valor instanceof Study) {
			return estudio((Study) valor);
		}
		return String.valueOf(valor);
	}

	private static String persona(Person persona) {
		return "CC " + persona.getIdentification() + " | " + persona.getFirstName() + " " + persona.getLastName()
				+ " | " + (persona.getGender() == Gender.FEMALE ? "F" : "M")
				+ " | edad: " + (persona.getAge() == null ? "-" : persona.getAge());
	}

	private static String profesion(Profession profesion) {
		return "ID " + profesion.getIdentification() + " | " + profesion.getName() + " | "
				+ texto(profesion.getDescription());
	}

	private static String telefono(Phone telefono) {
		return telefono.getNumber() + " | " + telefono.getCompany() + " | duenio: "
				+ (telefono.getOwner() == null ? "-" : telefono.getOwner().getIdentification());
	}

	private static String estudio(Study estudio) {
		return "Persona " + estudio.getPerson().getIdentification() + " | Profesion "
				+ estudio.getProfession().getIdentification() + " | fecha: " + texto(estudio.getGraduationDate())
				+ " | universidad: " + texto(estudio.getUniversityName());
	}

	private static String texto(Object valor) {
		return valor == null || valor.toString().isEmpty() ? "-" : valor.toString();
	}
}
