package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.mapper.EstudiosMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.EstudiosRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.ProfesionRepositoryMaria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("studyOutputAdapterMaria")
@Transactional
public class StudyOutputAdapterMaria implements StudyOutputPort {
	@Autowired private EstudiosRepositoryMaria repository;
	@Autowired private PersonaRepositoryMaria personRepository;
	@Autowired private ProfesionRepositoryMaria professionRepository;
	@Autowired private EstudiosMapperMaria mapper;

	@Override
	public Study save(Study study) {
		if (study.getPerson() == null || !personRepository.existsById(study.getPerson().getIdentification())
				|| study.getProfession() == null
				|| !professionRepository.existsById(study.getProfession().getIdentification())) {
			throw new IllegalArgumentException("Study requires an existing person and profession");
		}
		EstudiosEntity mapped = mapper.fromDomainToAdapter(study);
		mapped.setPersona(personRepository.getReferenceById(study.getPerson().getIdentification()));
		mapped.setProfesion(professionRepository.getReferenceById(study.getProfession().getIdentification()));
		return mapper.fromAdapterToDomain(repository.save(mapped));
	}

	@Override
	public Boolean delete(Integer personIdentification, Integer professionIdentification) {
		repository.deleteById(new EstudiosEntityPK(professionIdentification, personIdentification));
		return !repository.existsById(new EstudiosEntityPK(professionIdentification, personIdentification));
	}

	@Override
	public List<Study> find() {
		return repository.findAll().stream().map(mapper::fromAdapterToDomain).collect(Collectors.toList());
	}

	@Override
	public Study findById(Integer personIdentification, Integer professionIdentification) {
		return repository.findById(new EstudiosEntityPK(professionIdentification, personIdentification))
				.map(mapper::fromAdapterToDomain).orElse(null);
	}

	@Override
	public Integer count() {
		return Math.toIntExact(repository.count());
	}
}
