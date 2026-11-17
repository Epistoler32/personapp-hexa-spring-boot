package co.edu.javeriana.as.personapp.mongo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

class RelationMappingTest {
	@Test
	void mapsCyclicMongoRelationsWithoutRecursiveTraversal() {
		EstudiosMapperMongo studyMapper = new EstudiosMapperMongo();
		PersonaMapperMongo personMapper = new PersonaMapperMongo();
		ProfesionMapperMongo professionMapper = new ProfesionMapperMongo();
		TelefonoMapperMongo phoneMapper = new TelefonoMapperMongo();
		ReflectionTestUtils.setField(personMapper, "estudiosMapperMongo", studyMapper);
		ReflectionTestUtils.setField(personMapper, "telefonoMapperMongo", phoneMapper);
		ReflectionTestUtils.setField(studyMapper, "personaMapperMongo", personMapper);
		ReflectionTestUtils.setField(studyMapper, "profesionMapperMongo", professionMapper);
		ReflectionTestUtils.setField(professionMapper, "estudiosMapperMongo", studyMapper);
		ReflectionTestUtils.setField(phoneMapper, "personaMapperMongo", personMapper);

		PersonaDocument personDocument = new PersonaDocument();
		personDocument.setId(1);
		personDocument.setNombre("Ana");
		personDocument.setApellido("Perez");
		personDocument.setGenero("F");
		personDocument.setEdad(25);
		ProfesionDocument professionDocument = new ProfesionDocument();
		professionDocument.setId(7);
		professionDocument.setNom("Engineer");
		EstudiosDocument studyDocument = new EstudiosDocument();
		studyDocument.setPrimaryPersona(personDocument);
		studyDocument.setPrimaryProfesion(professionDocument);
		TelefonoDocument phoneDocument = new TelefonoDocument();
		phoneDocument.setId("555");
		phoneDocument.setOper("Telco");
		phoneDocument.setPrimaryDuenio(personDocument);
		personDocument.setEstudios(Arrays.asList(studyDocument));
		personDocument.setTelefonos(Arrays.asList(phoneDocument));

		Person person = personMapper.fromAdapterToDomain(personDocument);

		assertEquals(1, person.getStudies().get(0).getPerson().getIdentification());
		assertEquals(7, person.getStudies().get(0).getProfession().getIdentification());
		assertEquals(1, person.getPhoneNumbers().get(0).getOwner().getIdentification());
	}
}
