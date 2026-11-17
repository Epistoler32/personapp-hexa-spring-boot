package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.CrudRestMapper;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@Adapter
public class CrudInputAdapterRest {
	@Autowired private RestUseCaseFactory useCases;
	@Autowired private CrudRestMapper mapper;
	@Autowired private PersonaMapperRest personaMapper;

	public List<ProfesionResponse> profesiones(String database) {
		String label = useCases.databaseLabel(database);
		return useCases.profession(database).findAll().stream()
				.map(profession -> mapper.toResponse(profession, label)).collect(Collectors.toList());
	}

	public ProfesionResponse buscarProfesion(String database, Integer id) throws NoExistException {
		return mapper.toResponse(useCases.profession(database).findOne(id), useCases.databaseLabel(database));
	}

	public List<EstudioResponse> estudiosProfesion(String database, Integer id) throws NoExistException {
		String label = useCases.databaseLabel(database);
		return useCases.profession(database).getStudies(id).stream()
				.map(study -> mapper.toResponse(study, label)).collect(Collectors.toList());
	}

	public Integer contarProfesiones(String database) {
		return useCases.profession(database).count();
	}

	public ProfesionResponse crearProfesion(ProfesionRequest request) {
		require(request);
		String database = request.getDatabase();
		Profession profession = useCases.profession(database).create(mapper.toDomain(request));
		return mapper.toResponse(profession, useCases.databaseLabel(database));
	}

	public ProfesionResponse editarProfesion(String database, Integer id, ProfesionRequest request)
			throws NoExistException {
		require(request);
		request.setId(id);
		return mapper.toResponse(useCases.profession(database).edit(id, mapper.toDomain(request)),
				useCases.databaseLabel(database));
	}

	public Boolean eliminarProfesion(String database, Integer id) throws NoExistException {
		return useCases.profession(database).drop(id);
	}

	public List<TelefonoResponse> telefonos(String database) {
		String label = useCases.databaseLabel(database);
		return useCases.phone(database).findAll().stream()
				.map(phone -> mapper.toResponse(phone, label)).collect(Collectors.toList());
	}

	public TelefonoResponse buscarTelefono(String database, String number) throws NoExistException {
		return mapper.toResponse(useCases.phone(database).findOne(number), useCases.databaseLabel(database));
	}

	public PersonaResponse duenioTelefono(String database, String number) throws NoExistException {
		return personaMapper.fromDomainToAdapterRest(useCases.phone(database).getOwner(number),
				useCases.databaseLabel(database));
	}

	public Integer contarTelefonos(String database) {
		return useCases.phone(database).count();
	}

	public TelefonoResponse crearTelefono(TelefonoRequest request) {
		require(request);
		String database = request.getDatabase();
		Phone phone = useCases.phone(database).create(mapper.toDomain(request));
		return mapper.toResponse(phone, useCases.databaseLabel(database));
	}

	public TelefonoResponse editarTelefono(String database, String number, TelefonoRequest request)
			throws NoExistException {
		require(request);
		request.setNumber(number);
		return mapper.toResponse(useCases.phone(database).edit(number, mapper.toDomain(request)),
				useCases.databaseLabel(database));
	}

	public Boolean eliminarTelefono(String database, String number) throws NoExistException {
		return useCases.phone(database).drop(number);
	}

	public List<EstudioResponse> estudios(String database) {
		String label = useCases.databaseLabel(database);
		return useCases.study(database).findAll().stream()
				.map(study -> mapper.toResponse(study, label)).collect(Collectors.toList());
	}

	public EstudioResponse buscarEstudio(String database, Integer personId, Integer professionId)
			throws NoExistException {
		return mapper.toResponse(useCases.study(database).findOne(personId, professionId),
				useCases.databaseLabel(database));
	}

	public Integer contarEstudios(String database) {
		return useCases.study(database).count();
	}

	public EstudioResponse crearEstudio(EstudioRequest request) {
		require(request);
		String database = request.getDatabase();
		Study study = useCases.study(database).create(mapper.toDomain(request));
		return mapper.toResponse(study, useCases.databaseLabel(database));
	}

	public EstudioResponse editarEstudio(String database, Integer personId, Integer professionId,
			EstudioRequest request) throws NoExistException {
		require(request);
		request.setPersonIdentification(personId);
		request.setProfessionIdentification(professionId);
		return mapper.toResponse(useCases.study(database).edit(personId, professionId, mapper.toDomain(request)),
				useCases.databaseLabel(database));
	}

	public Boolean eliminarEstudio(String database, Integer personId, Integer professionId) throws NoExistException {
		return useCases.study(database).drop(personId, professionId);
	}

	private void require(Object request) {
		if (request == null) {
			throw new IllegalArgumentException("Request is required");
		}
	}
}
