package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@Mapper
public class CrudRestMapper {
	public Profession toDomain(ProfesionRequest request) {
		if (request.getId() == null || request.getName() == null || request.getName().trim().isEmpty()) {
			throw new IllegalArgumentException("Profession id and name are required");
		}
		if (request.getName().length() > 90) {
			throw new IllegalArgumentException("Profession name must be at most 90 characters");
		}
		return new Profession(request.getId(), request.getName(), request.getDescription(), null);
	}

	public ProfesionResponse toResponse(Profession profession, String database) {
		return new ProfesionResponse(profession.getIdentification(), profession.getName(), profession.getDescription(),
				database, "OK");
	}

	public Phone toDomain(TelefonoRequest request) {
		if (request.getNumber() == null || request.getCompany() == null || request.getOwnerIdentification() == null) {
			throw new IllegalArgumentException("Phone number, company, and owner identification are required");
		}
		if (request.getNumber().trim().isEmpty() || request.getNumber().length() > 15
				|| request.getCompany().trim().isEmpty() || request.getCompany().length() > 45) {
			throw new IllegalArgumentException("Phone number must be at most 15 characters and company at most 45");
		}
		Person owner = new Person();
		owner.setIdentification(request.getOwnerIdentification());
		return new Phone(request.getNumber(), request.getCompany(), owner);
	}

	public TelefonoResponse toResponse(Phone phone, String database) {
		return new TelefonoResponse(phone.getNumber(), phone.getCompany(),
				phone.getOwner() == null ? null : phone.getOwner().getIdentification(), database, "OK");
	}

	public Study toDomain(EstudioRequest request) {
		if (request.getPersonIdentification() == null || request.getProfessionIdentification() == null) {
			throw new IllegalArgumentException("Study person and profession identifications are required");
		}
		if (request.getUniversityName() != null && request.getUniversityName().length() > 50) {
			throw new IllegalArgumentException("University name must be at most 50 characters");
		}
		Person person = new Person();
		person.setIdentification(request.getPersonIdentification());
		Profession profession = new Profession();
		profession.setIdentification(request.getProfessionIdentification());
		return new Study(person, profession, request.getGraduationDate(), request.getUniversityName());
	}

	public EstudioResponse toResponse(Study study, String database) {
		return new EstudioResponse(study.getPerson().getIdentification(), study.getProfession().getIdentification(),
				study.getGraduationDate(), study.getUniversityName(), database, "OK");
	}
}
