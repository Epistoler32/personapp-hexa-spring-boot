package co.edu.javeriana.as.personapp.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;

class RestMapperTest {
	private final PersonaMapperRest personMapper = new PersonaMapperRest();
	private final CrudRestMapper crudMapper = new CrudRestMapper();

	@Test
	void mapsOnlyFemaleAndMaleFromRest() {
		assertEquals(Gender.FEMALE, personMapper.fromAdapterToDomain(
				new PersonaRequest("1", "Ana", "Perez", "20", "F", "MARIA")).getGender());
		assertEquals(Gender.MALE, personMapper.fromAdapterToDomain(
				new PersonaRequest("2", "Juan", "Perez", "21", "m", "MONGO")).getGender());
		assertThrows(IllegalArgumentException.class, () -> personMapper.fromAdapterToDomain(
				new PersonaRequest("3", "Alex", "Perez", "22", "X", "MARIA")));
	}

	@Test
	void mapsRelatedEntityIdentifiersWithoutRecursing() {
		assertEquals(12, crudMapper.toDomain(new TelefonoRequest("555", "Telco", 12, "MARIA"))
				.getOwner().getIdentification());
		assertEquals(14, crudMapper.toDomain(new EstudioRequest(14, 8, null, "Uni", "MONGO"))
				.getPerson().getIdentification());
		assertEquals(8, crudMapper.toDomain(new EstudioRequest(14, 8, null, "Uni", "MONGO"))
				.getProfession().getIdentification());
		assertEquals("Medicina", crudMapper.toDomain(new ProfesionRequest(8, "Medicina", "", "MONGO")).getName());
	}
}
