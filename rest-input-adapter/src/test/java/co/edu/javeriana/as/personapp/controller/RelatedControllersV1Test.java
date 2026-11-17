package co.edu.javeriana.as.personapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import co.edu.javeriana.as.personapp.adapter.CrudInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

class RelatedControllersV1Test {
	private CrudInputAdapterRest adapter;
	private MockMvc professionMvc;
	private MockMvc phoneMvc;
	private MockMvc studyMvc;

	@BeforeEach
	void setUp() {
		adapter = mock(CrudInputAdapterRest.class);
		ProfesionControllerV1 professionController = new ProfesionControllerV1();
		TelefonoControllerV1 phoneController = new TelefonoControllerV1();
		EstudioControllerV1 studyController = new EstudioControllerV1();
		ReflectionTestUtils.setField(professionController, "adapter", adapter);
		ReflectionTestUtils.setField(phoneController, "adapter", adapter);
		ReflectionTestUtils.setField(studyController, "adapter", adapter);
		professionMvc = MockMvcBuilders.standaloneSetup(professionController).build();
		phoneMvc = MockMvcBuilders.standaloneSetup(phoneController).build();
		studyMvc = MockMvcBuilders.standaloneSetup(studyController).build();
	}

	@Test
	void professionControllerMapsSixRoutes() throws Exception {
		ProfesionResponse response = new ProfesionResponse(7, "Engineer", "", "MariaDB", "OK");
		when(adapter.buscarProfesion("MARIA", 7)).thenReturn(response);
		when(adapter.contarProfesiones("MARIA")).thenReturn(1);
		when(adapter.crearProfesion(any(ProfesionRequest.class))).thenReturn(response);
		when(adapter.editarProfesion(eq("MARIA"), eq(7), any(ProfesionRequest.class))).thenReturn(response);
		when(adapter.eliminarProfesion("MARIA", 7)).thenReturn(true);

		professionMvc.perform(get("/api/v1/profesion/MARIA")).andExpect(status().isOk());
		professionMvc.perform(get("/api/v1/profesion/MARIA/count")).andExpect(status().isOk());
		professionMvc.perform(get("/api/v1/profesion/MARIA/7")).andExpect(status().isOk());
		professionMvc.perform(post("/api/v1/profesion").contentType("application/json")
				.content("{\"id\":7,\"name\":\"Engineer\",\"database\":\"MARIA\"}")).andExpect(status().isOk());
		professionMvc.perform(put("/api/v1/profesion/MARIA/7").contentType("application/json")
				.content("{\"name\":\"Engineer\"}")).andExpect(status().isOk());
		professionMvc.perform(delete("/api/v1/profesion/MARIA/7")).andExpect(status().isOk());
	}

	@Test
	void phoneControllerMapsSixRoutes() throws Exception {
		TelefonoResponse response = new TelefonoResponse("555", "Telco", 1, "MongoDB", "OK");
		when(adapter.buscarTelefono("MONGO", "555")).thenReturn(response);
		when(adapter.contarTelefonos("MONGO")).thenReturn(1);
		when(adapter.crearTelefono(any(TelefonoRequest.class))).thenReturn(response);
		when(adapter.editarTelefono(eq("MONGO"), eq("555"), any(TelefonoRequest.class))).thenReturn(response);
		when(adapter.eliminarTelefono("MONGO", "555")).thenReturn(true);

		phoneMvc.perform(get("/api/v1/telefono/MONGO")).andExpect(status().isOk());
		phoneMvc.perform(get("/api/v1/telefono/MONGO/count")).andExpect(status().isOk());
		phoneMvc.perform(get("/api/v1/telefono/MONGO/555")).andExpect(status().isOk());
		phoneMvc.perform(post("/api/v1/telefono").contentType("application/json")
				.content("{\"number\":\"555\",\"company\":\"Telco\",\"ownerIdentification\":1,\"database\":\"MONGO\"}"))
				.andExpect(status().isOk());
		phoneMvc.perform(put("/api/v1/telefono/MONGO/555").contentType("application/json")
				.content("{\"company\":\"Telco\",\"ownerIdentification\":1}")).andExpect(status().isOk());
		phoneMvc.perform(delete("/api/v1/telefono/MONGO/555")).andExpect(status().isOk());
	}

	@Test
	void studyControllerUsesBothPartsOfItsCompositeId() throws Exception {
		EstudioResponse response = new EstudioResponse(1, 7, null, "Uni", "MariaDB", "OK");
		when(adapter.buscarEstudio("MARIA", 1, 7)).thenReturn(response);
		when(adapter.contarEstudios("MARIA")).thenReturn(1);
		when(adapter.crearEstudio(any(EstudioRequest.class))).thenReturn(response);
		when(adapter.editarEstudio(eq("MARIA"), eq(1), eq(7), any(EstudioRequest.class))).thenReturn(response);
		when(adapter.eliminarEstudio("MARIA", 1, 7)).thenReturn(true);

		studyMvc.perform(get("/api/v1/estudio/MARIA")).andExpect(status().isOk());
		studyMvc.perform(get("/api/v1/estudio/MARIA/count")).andExpect(status().isOk());
		studyMvc.perform(get("/api/v1/estudio/MARIA/1/7")).andExpect(status().isOk());
		studyMvc.perform(post("/api/v1/estudio").contentType("application/json")
				.content("{\"personIdentification\":1,\"professionIdentification\":7,\"universityName\":\"Uni\",\"database\":\"MARIA\"}"))
				.andExpect(status().isOk());
		studyMvc.perform(put("/api/v1/estudio/MARIA/1/7").contentType("application/json")
				.content("{\"universityName\":\"Uni\"}")).andExpect(status().isOk());
		studyMvc.perform(delete("/api/v1/estudio/MARIA/1/7")).andExpect(status().isOk());
	}
}
