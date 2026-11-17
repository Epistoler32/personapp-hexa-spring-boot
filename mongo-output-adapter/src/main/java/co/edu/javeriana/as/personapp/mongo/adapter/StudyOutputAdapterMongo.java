package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;

@Adapter("studyOutputAdapterMongo")
public class StudyOutputAdapterMongo implements StudyOutputPort {
	@Autowired private EstudiosRepositoryMongo repository;
	@Autowired private PersonaRepositoryMongo personRepository;
	@Autowired private ProfesionRepositoryMongo professionRepository;
	@Autowired private EstudiosMapperMongo mapper;

	@Override
	public Study save(Study study) {
		if (study.getPerson() == null || study.getPerson().getIdentification() == null
				|| study.getProfession() == null || study.getProfession().getIdentification() == null) {
			throw new IllegalArgumentException("Study requires an existing person and profession");
		}
		PersonaDocument person = personRepository.findById(study.getPerson().getIdentification())
				.orElseThrow(() -> new IllegalArgumentException("Study requires an existing person and profession"));
		ProfesionDocument profession = professionRepository.findById(study.getProfession().getIdentification())
				.orElseThrow(() -> new IllegalArgumentException("Study requires an existing person and profession"));
		EstudiosDocument document = mapper.fromDomainToAdapter(study);
		document.setPrimaryPersona(person);
		document.setPrimaryProfesion(profession);
		return mapper.fromAdapterToDomain(repository.save(document));
	}

	@Override
	public Boolean delete(Integer personIdentification, Integer professionIdentification) {
		String id = studyId(personIdentification, professionIdentification);
		repository.deleteById(id);
		return !repository.existsById(id);
	}

	@Override
	public List<Study> find() {
		return repository.findAll().stream().map(mapper::fromAdapterToDomain).collect(Collectors.toList());
	}

	@Override
	public Study findById(Integer personIdentification, Integer professionIdentification) {
		return repository.findById(studyId(personIdentification, professionIdentification))
				.map(mapper::fromAdapterToDomain).orElse(null);
	}

	@Override
	public Integer count() {
		return Math.toIntExact(repository.count());
	}

	private String studyId(Integer personIdentification, Integer professionIdentification) {
		return personIdentification + "-" + professionIdentification;
	}
}
