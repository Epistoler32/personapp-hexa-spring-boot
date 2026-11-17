package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {
	
	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}
	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}
	
	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				person.getIdentification()+"", 
				person.getFirstName(), 
				person.getLastName(), 
				person.getAge()+"", 
				person.getGender() == Gender.FEMALE ? "F" : "M",
				database,
				"OK");
	}

	public Person fromAdapterToDomain(PersonaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request is required");
		}
		if (request.getDni() == null || request.getFirstName() == null || request.getFirstName().trim().isEmpty()
				|| request.getLastName() == null || request.getLastName().trim().isEmpty()
				|| request.getAge() == null) {
			throw new IllegalArgumentException("DNI, first name, last name, age, and sex are required");
		}
		Gender gender;
		if ("F".equalsIgnoreCase(request.getSex())) {
			gender = Gender.FEMALE;
		} else if ("M".equalsIgnoreCase(request.getSex())) {
			gender = Gender.MALE;
		} else {
			throw new IllegalArgumentException("Sex must be F or M");
		}
		Person person = new Person(
				Integer.valueOf(request.getDni()),
				request.getFirstName(),
				request.getLastName(),
				gender);
		int age = Integer.parseInt(request.getAge());
		if (age < 0) {
			throw new IllegalArgumentException("Age cannot be negative");
		}
		person.setAge(age);
		return person;
	}
		
}
