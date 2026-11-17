package co.edu.javeriana.as.personapp.mongo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import lombok.NonNull;

@Mapper
public class PersonaMapperMongo {

	@Autowired
	private EstudiosMapperMongo estudiosMapperMongo;

	@Autowired
	private TelefonoMapperMongo telefonoMapperMongo;

	public PersonaDocument fromDomainToAdapter(Person person) {
		PersonaDocument personaDocument = new PersonaDocument();
		personaDocument.setId(person.getIdentification());
		personaDocument.setNombre(person.getFirstName());
		personaDocument.setApellido(person.getLastName());
		personaDocument.setGenero(validateGenero(person.getGender()));
		personaDocument.setEdad(validateEdad(person.getAge()));
		return personaDocument;
	}

	private String validateGenero(@NonNull Gender gender) {
		return gender == Gender.FEMALE ? "F" : gender == Gender.MALE ? "M"
				: invalidGender();
	}

	private String invalidGender() {
		throw new IllegalArgumentException("Gender must be FEMALE or MALE");
	}

	private Integer validateEdad(Integer age) {
		if (age == null || age < 0) {
			throw new IllegalArgumentException("Age is required and cannot be negative");
		}
		return age;
	}

	private List<EstudiosDocument> validateEstudios(List<Study> studies) {
		return studies != null && !studies.isEmpty() ? studies.stream()
				.map(study -> estudiosMapperMongo.fromDomainToAdapter(study)).collect(Collectors.toList())
				: new ArrayList<EstudiosDocument>();
	}

	private List<TelefonoDocument> validateTelefonos(List<Phone> phoneNumbers) {
		return phoneNumbers != null && !phoneNumbers.isEmpty() ? phoneNumbers.stream()
				.map(phone -> telefonoMapperMongo.fromDomainToAdapter(phone)).collect(Collectors.toList())
				: new ArrayList<TelefonoDocument>();
	}

	public Person fromAdapterToDomain(PersonaDocument personaDocument) {
		Person person = new Person();
		person.setIdentification(personaDocument.getId());
		person.setFirstName(personaDocument.getNombre());
		person.setLastName(personaDocument.getApellido());
		person.setGender(validateGender(personaDocument.getGenero()));
		person.setAge(validateAge(personaDocument.getEdad()));
		person.setStudies(validateStudies(personaDocument.getEstudios()));
		person.setPhoneNumbers(validatePhones(personaDocument.getTelefonos()));
		return person;
	}

	public Person fromAdapterToDomainReference(PersonaDocument personaDocument) {
		Person person = new Person();
		person.setIdentification(personaDocument.getId());
		person.setFirstName(personaDocument.getNombre());
		person.setLastName(personaDocument.getApellido());
		person.setGender(validateGender(personaDocument.getGenero()));
		person.setAge(validateAge(personaDocument.getEdad()));
		return person;
	}

	private @NonNull Gender validateGender(String genero) {
		if ("F".equals(genero)) {
			return Gender.FEMALE;
		}
		if ("M".equals(genero)) {
			return Gender.MALE;
		}
		throw new IllegalArgumentException("Gender must be F or M");
	}

	private Integer validateAge(Integer edad) {
		if (edad == null || edad < 0) {
			throw new IllegalArgumentException("Stored age must be present and non-negative");
		}
		return edad;
	}

	private List<Study> validateStudies(List<EstudiosDocument> estudiosDocuments) {
		return estudiosDocuments != null && !estudiosDocuments.isEmpty() ? estudiosDocuments.stream()
				.map(estudio -> estudiosMapperMongo.fromAdapterToDomain(estudio)).collect(Collectors.toList())
				: new ArrayList<Study>();
	}

	private List<Phone> validatePhones(List<TelefonoDocument> telefonosDocuments) {
		return telefonosDocuments != null && !telefonosDocuments.isEmpty() ? telefonosDocuments.stream()
				.map(telefono -> telefonoMapperMongo.fromAdapterToDomain(telefono)).collect(Collectors.toList())
				: new ArrayList<Phone>();
	}
}