package co.edu.javeriana.as.personapp.mariadb.adapter;

import java.util.List;

import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.mapper.PersonaMapperMaria;
import co.edu.javeriana.as.personapp.mariadb.repository.PersonaRepositoryMaria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("personOutputAdapterMaria")
@Transactional
public class PersonOutputAdapterMaria implements PersonOutputPort {

	@Autowired
	private PersonaRepositoryMaria personaRepositoryMaria;

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	@Override
	public Person save(Person person) {
		log.debug("Into save on Adapter MariaDB");
		PersonaEntity mappedPersona = personaMapperMaria.fromDomainToAdapter(person);
		PersonaEntity persistedPersona = personaRepositoryMaria.findById(person.getIdentification()).orElseGet(PersonaEntity::new);
		persistedPersona.setCc(mappedPersona.getCc());
		persistedPersona.setNombre(mappedPersona.getNombre());
		persistedPersona.setApellido(mappedPersona.getApellido());
		persistedPersona.setGenero(mappedPersona.getGenero());
		persistedPersona.setEdad(mappedPersona.getEdad());
		persistedPersona = personaRepositoryMaria.save(persistedPersona);
		return personaMapperMaria.fromAdapterToDomain(persistedPersona);
	}

	@Override
	public Boolean delete(Integer identification) {
		log.debug("Into delete on Adapter MariaDB");
		personaRepositoryMaria.deleteById(identification);
		return personaRepositoryMaria.findById(identification).isEmpty();
	}

	@Override
	public List<Person> find() {
		log.debug("Into find on Adapter MariaDB");
		return personaRepositoryMaria.findAll().stream().map(personaMapperMaria::fromAdapterToDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Person findById(Integer identification) {
		log.debug("Into findById on Adapter MariaDB");
		return personaRepositoryMaria.findById(identification)
				.map(personaMapperMaria::fromAdapterToDomain)
				.orElse(null);
	}

	@Override
	public Integer count() {
		return Math.toIntExact(personaRepositoryMaria.count());
	}

}
