package co.edu.javeriana.as.personapp.mariadb.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

class RelationMappingTest {
	@Test
	void mapsGraduationDatesReturnedByMariaDb() {
		EstudiosMapperMaria mapper = new EstudiosMapperMaria();
		ReflectionTestUtils.setField(mapper, "personaMapperMaria", new PersonaMapperMaria());
		ReflectionTestUtils.setField(mapper, "profesionMapperMaria", new ProfesionMapperMaria());
		EstudiosEntity entity = new EstudiosEntity(new EstudiosEntityPK(7, 1));
		PersonaEntity person = new PersonaEntity(1, "Ana", "Perez", 'F');
		person.setEdad(25);
		entity.setPersona(person);
		entity.setProfesion(new ProfesionEntity(7, "Engineer"));
		LocalDate expected = LocalDate.of(2020, 2, 29);

		entity.setFecha(java.sql.Date.valueOf(expected));
		assertEquals(expected, mapper.fromAdapterToDomain(entity).getGraduationDate());
		entity.setFecha(Date.from(expected.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		assertEquals(expected, mapper.fromAdapterToDomain(entity).getGraduationDate());
		entity.setFecha(mapper.fromDomainToAdapter(mapper.fromAdapterToDomain(entity)).getFecha());
		assertEquals(expected, mapper.fromAdapterToDomain(entity).getGraduationDate());
		entity.setFecha(null);
		assertNull(mapper.fromAdapterToDomain(entity).getGraduationDate());
	}

	@Test
	void mapsCyclicMariaRelationsWithoutRecursiveTraversal() {
		EstudiosMapperMaria studyMapper = new EstudiosMapperMaria();
		PersonaMapperMaria personMapper = new PersonaMapperMaria();
		ProfesionMapperMaria professionMapper = new ProfesionMapperMaria();
		TelefonoMapperMaria phoneMapper = new TelefonoMapperMaria();
		ReflectionTestUtils.setField(personMapper, "estudiosMapperMaria", studyMapper);
		ReflectionTestUtils.setField(personMapper, "telefonoMapperMaria", phoneMapper);
		ReflectionTestUtils.setField(studyMapper, "personaMapperMaria", personMapper);
		ReflectionTestUtils.setField(studyMapper, "profesionMapperMaria", professionMapper);
		ReflectionTestUtils.setField(professionMapper, "estudiosMapperMaria", studyMapper);
		ReflectionTestUtils.setField(phoneMapper, "personaMapperMaria", personMapper);

		PersonaEntity personEntity = new PersonaEntity(1, "Ana", "Perez", 'F');
		personEntity.setEdad(25);
		ProfesionEntity professionEntity = new ProfesionEntity(7, "Engineer");
		EstudiosEntity studyEntity = new EstudiosEntity(new EstudiosEntityPK(7, 1));
		studyEntity.setPersona(personEntity);
		studyEntity.setProfesion(professionEntity);
		TelefonoEntity phoneEntity = new TelefonoEntity("555", "Telco");
		phoneEntity.setDuenio(personEntity);
		personEntity.setEstudios(Arrays.asList(studyEntity));
		personEntity.setTelefonos(Arrays.asList(phoneEntity));

		Person person = personMapper.fromAdapterToDomain(personEntity);

		assertEquals(1, person.getStudies().get(0).getPerson().getIdentification());
		assertEquals(7, person.getStudies().get(0).getProfession().getIdentification());
		assertEquals(1, person.getPhoneNumbers().get(0).getOwner().getIdentification());
	}
}
