package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterRest {
	@Autowired private RestUseCaseFactory useCases;
	@Autowired private PersonaMapperRest mapper;

	public List<PersonaResponse> historial(String database) {
		log.info("Listing persons from {}", database);
		String label = useCases.databaseLabel(database);
		return useCases.person(database).findAll().stream()
				.map(person -> mapper.fromDomainToAdapterRest(person, label))
				.collect(Collectors.toList());
	}

	public PersonaResponse buscarPersona(String database, Integer identification) throws NoExistException {
		Person person = useCases.person(database).findOne(identification);
		return mapper.fromDomainToAdapterRest(person, useCases.databaseLabel(database));
	}

	public Integer contarPersonas(String database) {
		return useCases.person(database).count();
	}

	public PersonaResponse crearPersona(PersonaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Request is required");
		}
		String database = request.getDatabase();
		Person person = useCases.person(database).create(mapper.fromAdapterToDomain(request));
		return mapper.fromDomainToAdapterRest(person, useCases.databaseLabel(database));
	}

	public PersonaResponse editarPersona(String database, Integer identification, PersonaRequest request)
			throws NoExistException {
		if (request == null) {
			throw new IllegalArgumentException("Request is required");
		}
		request.setDni(String.valueOf(identification));
		Person person = mapper.fromAdapterToDomain(request);
		Person persisted = useCases.person(database).edit(identification, person);
		return mapper.fromDomainToAdapterRest(persisted, useCases.databaseLabel(database));
	}

	public Boolean eliminarPersona(String database, Integer identification) throws NoExistException {
		return useCases.person(database).drop(identification);
	}
}
