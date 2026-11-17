package co.edu.javeriana.as.personapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

class PersonaControllerV1Test {
	private PersonaInputAdapterRest adapter;
	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		adapter = org.mockito.Mockito.mock(PersonaInputAdapterRest.class);
		PersonaControllerV1 controller = new PersonaControllerV1();
		ReflectionTestUtils.setField(controller, "personaInputAdapterRest", adapter);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void exposesAllSixPersonOperations() throws Exception {
		PersonaResponse response = new PersonaResponse("12", "Ana", "Perez", "20", "F", "MongoDB", "OK");
		when(adapter.historial("MONGO")).thenReturn(Collections.singletonList(response));
		when(adapter.contarPersonas("MONGO")).thenReturn(1);
		when(adapter.buscarPersona("MONGO", 12)).thenReturn(response);
		when(adapter.crearPersona(any(PersonaRequest.class))).thenReturn(response);
		when(adapter.editarPersona(eq("MONGO"), eq(12), any(PersonaRequest.class))).thenReturn(response);
		when(adapter.eliminarPersona("MONGO", 12)).thenReturn(true);

		mvc.perform(get("/api/v1/persona/MONGO")).andExpect(status().isOk());
		mvc.perform(get("/api/v1/persona/MONGO/count")).andExpect(status().isOk());
		mvc.perform(get("/api/v1/persona/MONGO/12")).andExpect(status().isOk());
		mvc.perform(post("/api/v1/persona").contentType("application/json")
				.content("{\"dni\":\"12\",\"firstName\":\"Ana\",\"lastName\":\"Perez\",\"age\":\"20\",\"sex\":\"F\",\"database\":\"MONGO\"}"))
				.andExpect(status().isCreated());
		mvc.perform(put("/api/v1/persona/MONGO/12").contentType("application/json")
				.content("{\"firstName\":\"Ana\",\"lastName\":\"Perez\",\"age\":\"20\",\"sex\":\"F\"}"))
				.andExpect(status().isOk());
		mvc.perform(delete("/api/v1/persona/MONGO/12")).andExpect(status().isOk());

		verify(adapter).historial("MONGO");
		verify(adapter).contarPersonas("MONGO");
		verify(adapter).buscarPersona("MONGO", 12);
		verify(adapter).eliminarPersona("MONGO", 12);
	}

	@Test
	void exposesPhonesAndStudiesOfAPerson() throws Exception {
		when(adapter.telefonosPersona("MARIA", 12)).thenReturn(Collections.singletonList(
				new TelefonoResponse("3001234567", "Claro", 12, "MariaDB", "OK")));
		when(adapter.estudiosPersona("MARIA", 12)).thenReturn(Collections.singletonList(
				new EstudioResponse(12, 1, null, "Javeriana", "MariaDB", "OK")));

		mvc.perform(get("/api/v1/persona/MARIA/12/telefonos")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].number").value("3001234567"));
		mvc.perform(get("/api/v1/persona/MARIA/12/estudios")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].universityName").value("Javeriana"));
	}
}
