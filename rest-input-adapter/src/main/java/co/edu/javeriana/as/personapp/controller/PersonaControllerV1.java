package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;

@Slf4j
@RestController
@RequestMapping("/api/v1/persona")
public class PersonaControllerV1 {
	
	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;
	
	@ResponseBody
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "List persons from the selected database")
	public List<PersonaResponse> personas(@PathVariable("database") String database) {
		log.info("Into personas REST API");
		return personaInputAdapterRest.historial(database);
	}

	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Count persons in the selected database")
	public Integer contar(@PathVariable("database") String database) {
		return personaInputAdapterRest.contarPersonas(database);
	}

	@GetMapping(path = "/{database}/{identification}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Find a person by identification")
	public PersonaResponse buscar(@PathVariable("database") String database, @PathVariable("identification") Integer identification)
			throws NoExistException {
		return personaInputAdapterRest.buscarPersona(database, identification);
	}

	@GetMapping(path = "/{database}/{identification}/telefonos", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "List the phones of a person")
	public List<TelefonoResponse> telefonos(@PathVariable("database") String database,
			@PathVariable("identification") Integer identification) throws NoExistException {
		return personaInputAdapterRest.telefonosPersona(database, identification);
	}

	@GetMapping(path = "/{database}/{identification}/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "List the studies of a person")
	public List<EstudioResponse> estudios(@PathVariable("database") String database,
			@PathVariable("identification") Integer identification) throws NoExistException {
		return personaInputAdapterRest.estudiosPersona(database, identification);
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create a person in the selected database")
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request) {
		return personaInputAdapterRest.crearPersona(request);
	}

	@PutMapping(path = "/{database}/{identification}", produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update a person")
	public PersonaResponse editar(@PathVariable("database") String database, @PathVariable("identification") Integer identification,
			@RequestBody PersonaRequest request) throws NoExistException {
		return personaInputAdapterRest.editarPersona(database, identification, request);
	}

	@DeleteMapping(path = "/{database}/{identification}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete a person")
	public Boolean eliminar(@PathVariable("database") String database, @PathVariable("identification") Integer identification)
			throws NoExistException {
		return personaInputAdapterRest.eliminarPersona(database, identification);
	}
}
