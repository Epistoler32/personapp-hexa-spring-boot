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
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/profesion")
public class ProfesionControllerV1 {
	@Autowired private CrudInputAdapterRest adapter;

	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "List professions from the selected database")
	public List<ProfesionResponse> all(@PathVariable("database") String database) { return adapter.profesiones(database); }

	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Count professions")
	public Integer count(@PathVariable("database") String database) { return adapter.contarProfesiones(database); }

	@GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Find a profession by id")
	public ProfesionResponse one(@PathVariable("database") String database, @PathVariable("id") Integer id) throws NoExistException {
		return adapter.buscarProfesion(database, id);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create a profession")
	public ProfesionResponse create(@RequestBody ProfesionRequest request) { return adapter.crearProfesion(request); }

	@PutMapping(path = "/{database}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update a profession")
	public ProfesionResponse update(@PathVariable("database") String database, @PathVariable("id") Integer id,
			@RequestBody ProfesionRequest request) throws NoExistException {
		return adapter.editarProfesion(database, id, request);
	}

	@DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete a profession")
	public Boolean delete(@PathVariable("database") String database, @PathVariable("id") Integer id) throws NoExistException {
		return adapter.eliminarProfesion(database, id);
	}
}
