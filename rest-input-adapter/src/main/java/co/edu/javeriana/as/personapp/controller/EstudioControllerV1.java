package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.CrudInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/estudio")
public class EstudioControllerV1 {
	@Autowired private CrudInputAdapterRest adapter;

	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "List studies from the selected database")
	public List<EstudioResponse> all(@PathVariable("database") String database) { return adapter.estudios(database); }

	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Count studies")
	public Integer count(@PathVariable("database") String database) { return adapter.contarEstudios(database); }

	@GetMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Find a study by person and profession identifiers")
	public EstudioResponse one(@PathVariable("database") String database, @PathVariable("personId") Integer personId,
			@PathVariable("professionId") Integer professionId) throws NoExistException {
		return adapter.buscarEstudio(database, personId, professionId);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create a study")
	public EstudioResponse create(@RequestBody EstudioRequest request) { return adapter.crearEstudio(request); }

	@PutMapping(path = "/{database}/{personId}/{professionId}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update a study")
	public EstudioResponse update(@PathVariable("database") String database, @PathVariable("personId") Integer personId,
			@PathVariable("professionId") Integer professionId, @RequestBody EstudioRequest request) throws NoExistException {
		return adapter.editarEstudio(database, personId, professionId, request);
	}

	@DeleteMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete a study")
	public Boolean delete(@PathVariable("database") String database, @PathVariable("personId") Integer personId,
			@PathVariable("professionId") Integer professionId) throws NoExistException {
		return adapter.eliminarEstudio(database, personId, professionId);
	}
}
