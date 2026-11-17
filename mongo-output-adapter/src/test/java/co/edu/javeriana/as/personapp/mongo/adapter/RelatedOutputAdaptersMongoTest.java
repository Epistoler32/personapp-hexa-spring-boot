package co.edu.javeriana.as.personapp.mongo.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.mapper.PersonaMapperMongo;
import co.edu.javeriana.as.personapp.mongo.mapper.ProfesionMapperMongo;
import co.edu.javeriana.as.personapp.mongo.mapper.TelefonoMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.PersonaRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.ProfesionRepositoryMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;

class RelatedOutputAdaptersMongoTest {
	private static void setField(Object target, String name, Object value) {
		try {
			java.lang.reflect.Field field = target.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(target, value);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	private PersonaDocument personDocument() {
		PersonaDocument person = new PersonaDocument();
		person.setId(1);
		person.setNombre("Ana");
		person.setApellido("Perez");
		person.setGenero("F");
		person.setEdad(25);
		return person;
	}

	@Test
	void savesPhoneWithResolvedOwner() {
		PersonaRepositoryMongo people = mock(PersonaRepositoryMongo.class);
		TelefonoRepositoryMongo phones = mock(TelefonoRepositoryMongo.class);
		TelefonoMapperMongo mapper = new TelefonoMapperMongo();
		setField(mapper, "personaMapperMongo", new PersonaMapperMongo());
		PhoneOutputAdapterMongo adapter = new PhoneOutputAdapterMongo();
		setField(adapter, "personRepository", people);
		setField(adapter, "repository", phones);
		setField(adapter, "mapper", mapper);
		when(people.findById(1)).thenReturn(Optional.of(personDocument()));
		when(phones.save(any(TelefonoDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
		Person owner = new Person();
		owner.setIdentification(1);

		Phone result = adapter.save(new Phone("555", "Telco", owner));

		assertEquals("Ana", result.getOwner().getFirstName());
		assertEquals(1, result.getOwner().getIdentification());
	}

	@Test
	void savesStudyWithResolvedPersonAndProfession() {
		PersonaRepositoryMongo people = mock(PersonaRepositoryMongo.class);
		ProfesionRepositoryMongo professions = mock(ProfesionRepositoryMongo.class);
		EstudiosRepositoryMongo studies = mock(EstudiosRepositoryMongo.class);
		EstudiosMapperMongo mapper = new EstudiosMapperMongo();
		setField(mapper, "personaMapperMongo", new PersonaMapperMongo());
		setField(mapper, "profesionMapperMongo", new ProfesionMapperMongo());
		StudyOutputAdapterMongo adapter = new StudyOutputAdapterMongo();
		setField(adapter, "personRepository", people);
		setField(adapter, "professionRepository", professions);
		setField(adapter, "repository", studies);
		setField(adapter, "mapper", mapper);
		ProfesionDocument professionDocument = new ProfesionDocument();
		professionDocument.setId(7);
		professionDocument.setNom("Engineer");
		when(people.findById(1)).thenReturn(Optional.of(personDocument()));
		when(professions.findById(7)).thenReturn(Optional.of(professionDocument));
		when(studies.save(any(EstudiosDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
		Person person = new Person();
		person.setIdentification(1);
		Profession profession = new Profession();
		profession.setIdentification(7);

		Study result = adapter.save(new Study(person, profession, LocalDate.of(2020, 2, 29), "University"));

		assertEquals("Ana", result.getPerson().getFirstName());
		assertEquals("Engineer", result.getProfession().getName());
		assertEquals(LocalDate.of(2020, 2, 29), result.getGraduationDate());
	}

	@Test
	void rejectsMissingReferencesBeforeSaving() {
		PersonaRepositoryMongo people = mock(PersonaRepositoryMongo.class);
		TelefonoRepositoryMongo phones = mock(TelefonoRepositoryMongo.class);
		PhoneOutputAdapterMongo phoneAdapter = new PhoneOutputAdapterMongo();
		setField(phoneAdapter, "personRepository", people);
		setField(phoneAdapter, "repository", phones);
		Person person = new Person();
		person.setIdentification(1);
		when(people.findById(1)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> phoneAdapter.save(new Phone("555", "Telco", person)));
		verify(phones, never()).save(any());

		ProfesionRepositoryMongo professions = mock(ProfesionRepositoryMongo.class);
		EstudiosRepositoryMongo studies = mock(EstudiosRepositoryMongo.class);
		StudyOutputAdapterMongo studyAdapter = new StudyOutputAdapterMongo();
		setField(studyAdapter, "personRepository", people);
		setField(studyAdapter, "professionRepository", professions);
		setField(studyAdapter, "repository", studies);
		when(people.findById(1)).thenReturn(Optional.of(personDocument()));
		when(professions.findById(7)).thenReturn(Optional.empty());
		Profession profession = new Profession();
		profession.setIdentification(7);

		assertThrows(IllegalArgumentException.class,
				() -> studyAdapter.save(new Study(person, profession, null, "University")));
		verify(studies, never()).save(any());
	}

	@Test
	void deletesPersonTogetherWithItsPhonesAndStudies() {
		PersonaRepositoryMongo people = mock(PersonaRepositoryMongo.class);
		TelefonoRepositoryMongo phones = mock(TelefonoRepositoryMongo.class);
		EstudiosRepositoryMongo studies = mock(EstudiosRepositoryMongo.class);
		PersonOutputAdapterMongo adapter = new PersonOutputAdapterMongo();
		setField(adapter, "personaRepositoryMongo", people);
		setField(adapter, "telefonoRepositoryMongo", phones);
		setField(adapter, "estudiosRepositoryMongo", studies);
		when(people.findById(1)).thenReturn(Optional.empty());

		assertTrue(adapter.delete(1));
		verify(phones).deleteByDuenio(1);
		verify(studies).deleteByPersona(1);
		verify(people).deleteById(1);
	}
}
