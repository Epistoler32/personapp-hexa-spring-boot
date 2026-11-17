package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {

	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				String.valueOf(person.getIdentification()),
				person.getFirstName(),
				person.getLastName(),
				person.getAge() == null ? null : String.valueOf(person.getAge()),
				person.getGender() == Gender.FEMALE ? "F" : "M",
				database,
				"OK");
	}

	public Person fromAdapterToDomain(PersonaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request is required");
		}
		if (request.getDni() == null || request.getFirstName() == null || request.getFirstName().trim().isEmpty()
				|| request.getLastName() == null || request.getLastName().trim().isEmpty()) {
			throw new IllegalArgumentException("DNI, first name, last name and sex are required");
		}
		Gender gender;
		if ("F".equalsIgnoreCase(request.getSex())) {
			gender = Gender.FEMALE;
		} else if ("M".equalsIgnoreCase(request.getSex())) {
			gender = Gender.MALE;
		} else {
			throw new IllegalArgumentException("Sex must be F or M");
		}
		Person person = new Person(parseNumber(request.getDni(), "DNI"), request.getFirstName(),
				request.getLastName(), gender);
		if (request.getAge() != null && !request.getAge().trim().isEmpty()) {
			int age = parseNumber(request.getAge(), "Age");
			if (age < 0) {
				throw new IllegalArgumentException("Age cannot be negative");
			}
			person.setAge(age);
		}
		return person;
	}

	private int parseNumber(String value, String field) {
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(field + " must be a valid number");
		}
	}
}
