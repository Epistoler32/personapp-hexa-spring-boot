package co.edu.javeriana.as.personapp.mongo.mapper;


import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import lombok.NonNull;

@Mapper
public class EstudiosMapperMongo {

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	@Autowired
	private ProfesionMapperMongo profesionMapperMongo;

	public EstudiosDocument fromDomainToAdapter(Study study) {
		EstudiosDocument estudio = new EstudiosDocument();
		estudio.setId(validateId(study.getPerson().getIdentification(), study.getProfession().getIdentification()));
		estudio.setPrimaryPersona(validatePrimaryPersona(study.getPerson()));
		estudio.setPrimaryProfesion(validatePrimaryProfesion(study.getProfession()));
		estudio.setFecha(study.getGraduationDate());
		estudio.setUniver(study.getUniversityName());
		return estudio;
	}

	private String validateId(@NonNull Integer identificationPerson, @NonNull Integer identificationProfession) {
		return identificationPerson + "-" + identificationProfession;
	}

	private PersonaDocument validatePrimaryPersona(@NonNull Person person) {
		PersonaDocument reference = new PersonaDocument();
		reference.setId(person.getIdentification());
		return reference;
	}

	private ProfesionDocument validatePrimaryProfesion(@NonNull Profession profession) {
		ProfesionDocument reference = new ProfesionDocument();
		reference.setId(profession.getIdentification());
		return reference;
	}

	public Study fromAdapterToDomain(EstudiosDocument estudiosDocument) {
		Study study = new Study();
		study.setPerson(personaMapperMongo.fromAdapterToDomainReference(estudiosDocument.getPrimaryPersona()));
		study.setProfession(profesionMapperMongo.fromAdapterToDomainReference(estudiosDocument.getPrimaryProfesion()));
		study.setGraduationDate(estudiosDocument.getFecha());
		study.setUniversityName(estudiosDocument.getUniver());
		return study;
	}
}