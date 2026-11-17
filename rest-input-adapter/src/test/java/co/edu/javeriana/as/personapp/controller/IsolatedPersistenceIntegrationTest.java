package co.edu.javeriana.as.personapp.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@EnabledIfEnvironmentVariable(named = "PERSONAPP_ISOLATED_INTEGRATION_TEST", matches = "true")
@SpringBootTest
class IsolatedPersistenceIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		String mariaUrl = System.getenv("SPRING_DATASOURCE_URL");
		String mongoHost = System.getenv("SPRING_MONGODB_HOST");
		String mongoPort = System.getenv("SPRING_MONGODB_PORT");
		assertTrue(mariaUrl != null && mariaUrl.startsWith("jdbc:mariadb://127.0.0.1:")
				&& !mariaUrl.startsWith("jdbc:mariadb://127.0.0.1:3307/"),
				"Configure an isolated MariaDB on a non-default local port");
		assertTrue("127.0.0.1".equals(mongoHost) && mongoPort != null && !"27017".equals(mongoPort),
				"Configure an isolated MongoDB on a non-default local port");
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void exposesOpenApiAndReadsBothDatabases() throws Exception {
		mvc.perform(get("/api-docs")).andExpect(status().isOk());
		for (String database : new String[] { "MARIA", "MONGO" }) {
			for (String resource : new String[] { "persona", "profesion", "telefono", "estudio" }) {
				mvc.perform(get("/api/v1/" + resource + "/" + database))
						.andExpect(status().isOk());
			}
		}
	}

	@Test
	void preservesCrudAndValidationOnBothDatabases() throws Exception {
		for (String database : new String[] { "MARIA", "MONGO" }) {
			int id = ThreadLocalRandom.current().nextInt(100_000_000, 900_000_000);
			String phone = "8" + id;
			String[] resources = { "persona", "profesion", "telefono", "estudio" };
			String[] paths = {
				"/api/v1/persona/" + database + "/" + id,
				"/api/v1/profesion/" + database + "/" + id,
				"/api/v1/telefono/" + database + "/" + phone,
				"/api/v1/estudio/" + database + "/" + id + "/" + id
			};
			String person = "{\"dni\":\"" + id + "\",\"firstName\":\"MigrationTest\","
					+ "\"lastName\":\"Temporary\",\"age\":\"31\",\"sex\":\"F\",\"database\":\"" + database + "\"}";
			String profession = "{\"id\":" + id + ",\"name\":\"MigrationTest\","
					+ "\"description\":\"temporary\",\"database\":\"" + database + "\"}";
			String telephone = "{\"number\":\"" + phone + "\",\"company\":\"MigrationTest\","
					+ "\"ownerIdentification\":" + id + ",\"database\":\"" + database + "\"}";
			String study = "{\"personIdentification\":" + id + ",\"professionIdentification\":" + id
					+ ",\"graduationDate\":\"2020-02-29\",\"universityName\":\"MigrationTest\","
					+ "\"database\":\"" + database + "\"}";
			String[] bodies = { person, profession, telephone, study };
			int created = 0;
			for (String path : paths) {
				mvc.perform(get(path)).andExpect(status().isNotFound());
			}
			try {
				for (int i = 0; i < resources.length; i++) {
					mvc.perform(post("/api/v1/" + resources[i]).contentType(MediaType.APPLICATION_JSON)
							.content(bodies[i])).andExpect(status().isCreated());
					created++;
					mvc.perform(get(paths[i])).andExpect(status().isOk());
				}
				String[] updatedBodies = {
					person.replace("\"31\"", "\"32\""),
					profession.replace("temporary", "updated"),
					telephone.replace("MigrationTest", "MigrationUpdated"),
					study.replace("2020-02-29", "2021-12-30")
				};
				String[] fields = { "age", "description", "company", "graduationDate" };
				String[] values = { "32", "updated", "MigrationUpdated", "2021-12-30" };
				mvc.perform(get(paths[3])).andExpect(jsonPath("$.graduationDate").value("2020-02-29"));
				for (int i = 0; i < paths.length; i++) {
					mvc.perform(put(paths[i]).contentType(MediaType.APPLICATION_JSON)
							.content(updatedBodies[i])).andExpect(status().isOk());
					mvc.perform(get(paths[i])).andExpect(jsonPath("$." + fields[i]).value(values[i]));
				}
				mvc.perform(post("/api/v1/persona").contentType(MediaType.APPLICATION_JSON)
						.content(person.replace("\"31\"", "\"-1\"").replace("\"" + id + "\"", "\"" + (id + 1) + "\"")))
						.andExpect(status().isBadRequest());
			} finally {
				for (int i = created - 1; i >= 0; i--) {
					mvc.perform(delete(paths[i])).andExpect(status().isOk());
					mvc.perform(get(paths[i])).andExpect(status().isNotFound());
				}
			}
		}
	}
}
