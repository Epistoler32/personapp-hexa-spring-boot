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
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/telefono")
public class TelefonoControllerV1 {
	@Autowired private CrudInputAdapterRest adapter;

	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "List phones from the selected database")
	public List<TelefonoResponse> all(@PathVariable("database") String database) { return adapter.telefonos(database); }

	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Count phones")
	public Integer count(@PathVariable("database") String database) { return adapter.contarTelefonos(database); }

	@GetMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Find a phone by number")
	public TelefonoResponse one(@PathVariable("database") String database, @PathVariable("number") String number) throws NoExistException {
		return adapter.buscarTelefono(database, number);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create a phone")
	public TelefonoResponse create(@RequestBody TelefonoRequest request) { return adapter.crearTelefono(request); }

	@PutMapping(path = "/{database}/{number}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update a phone")
	public TelefonoResponse update(@PathVariable("database") String database, @PathVariable("number") String number,
			@RequestBody TelefonoRequest request) throws NoExistException {
		return adapter.editarTelefono(database, number, request);
	}

	@DeleteMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete a phone")
	public Boolean delete(@PathVariable("database") String database, @PathVariable("number") String number) throws NoExistException {
		return adapter.eliminarTelefono(database, number);
	}
}
