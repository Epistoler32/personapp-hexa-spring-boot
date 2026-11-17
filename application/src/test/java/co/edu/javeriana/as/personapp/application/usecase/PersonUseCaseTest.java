package co.edu.javeriana.as.personapp.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;

class PersonUseCaseTest {
	@Test
	void performsCrudAgainstTheSelectedOutputPort() throws Exception {
		InMemoryPersonOutput output = new InMemoryPersonOutput();
		PersonUseCase useCase = new PersonUseCase(output);
		Person person = person(1, "Ana");

		useCase.create(person);
		assertEquals(1, useCase.count());
		assertEquals(person, useCase.findOne(1));

		Person update = person(99, "Bea");
		assertEquals(1, useCase.edit(1, update).getIdentification());
		assertEquals("Bea", useCase.findOne(1).getFirstName());
		assertEquals(1, useCase.findAll().size());

		assertEquals(Boolean.TRUE, useCase.drop(1));
		assertEquals(0, useCase.count());
	}

	@Test
	void acceptsMissingAgeButRejectsNegativeAge() {
		PersonUseCase useCase = new PersonUseCase(new InMemoryPersonOutput());
		assertNull(useCase.create(person(1, "Ana", null)).getAge());
		assertThrows(IllegalArgumentException.class, () -> useCase.create(person(2, "Ana", -1)));
	}

	@Test
	void reportsMissingPersonForUpdateAndDelete() {
		PersonUseCase useCase = new PersonUseCase(new InMemoryPersonOutput());
		assertThrows(NoExistException.class, () -> useCase.edit(5, person(5, "Ana")));
		assertThrows(NoExistException.class, () -> useCase.drop(5));
	}

	private Person person(Integer id, String firstName) {
		return person(id, firstName, 25);
	}

	private Person person(Integer id, String firstName, Integer age) {
		Person person = new Person(id, firstName, "Perez", Gender.FEMALE);
		person.setAge(age);
		return person;
	}

	private static class InMemoryPersonOutput implements PersonOutputPort {
		private final List<Person> people = new ArrayList<>();

		@Override
		public Person save(Person person) {
			people.removeIf(existing -> existing.getIdentification().equals(person.getIdentification()));
			people.add(person);
			return person;
		}

		@Override
		public Boolean delete(Integer identification) {
			return people.removeIf(person -> person.getIdentification().equals(identification));
		}

		@Override
		public List<Person> find() {
			return new ArrayList<>(people);
		}

		@Override
		public Person findById(Integer identification) {
			return people.stream().filter(person -> person.getIdentification().equals(identification))
					.findFirst().orElse(null);
		}

		@Override
		public Integer count() {
			return people.size();
		}
	}
}
