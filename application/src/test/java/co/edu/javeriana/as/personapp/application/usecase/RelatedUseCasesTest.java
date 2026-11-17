package co.edu.javeriana.as.personapp.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;

class RelatedUseCasesTest {
	@Test
	void professionUseCaseExposesAllSixOperations() throws Exception {
		InMemoryProfessionOutput output = new InMemoryProfessionOutput();
		Profession profession = new Profession(7, "Engineer", "", Collections.emptyList());
		ProfessionUseCase useCase = new ProfessionUseCase(output);

		assertEquals(profession, useCase.create(profession));
		assertEquals(1, useCase.findAll().size());
		assertEquals(profession, useCase.findOne(7));
		assertEquals(1, useCase.count());
		assertEquals(profession, useCase.edit(7, profession));
		assertEquals(true, useCase.drop(7));
	}

	@Test
	void phoneUseCaseExposesAllSixOperations() throws Exception {
		InMemoryPhoneOutput output = new InMemoryPhoneOutput();
		Person owner = new Person();
		owner.setIdentification(1);
		Phone phone = new Phone("555", "Telco", owner);
		PhoneUseCase useCase = new PhoneUseCase(output);

		assertEquals(phone, useCase.create(phone));
		assertEquals(1, useCase.findAll().size());
		assertEquals(phone, useCase.findOne("555"));
		assertEquals(1, useCase.count());
		assertEquals(phone, useCase.edit("555", phone));
		assertEquals(true, useCase.drop("555"));
	}

	@Test
	void studyUseCaseExposesAllSixOperationsAndUsesCompositeIdentifier() throws Exception {
		InMemoryStudyOutput output = new InMemoryStudyOutput();
		Person person = new Person();
		person.setIdentification(1);
		Profession profession = new Profession();
		profession.setIdentification(7);
		Study study = new Study(person, profession, null, "Uni");
		StudyUseCase useCase = new StudyUseCase(output);

		assertEquals(study, useCase.create(study));
		assertEquals(1, useCase.findAll().size());
		assertEquals(study, useCase.findOne(1, 7));
		assertEquals(1, useCase.count());
		assertEquals(study, useCase.edit(1, 7, study));
		assertEquals(true, useCase.drop(1, 7));
	}

	@Test
	void exposesStudiesOfAProfessionAndOwnerOfAPhone() throws Exception {
		Person owner = new Person(1, "Ana", "Perez", Gender.FEMALE);
		Profession profession = new Profession(7, "Engineer", null, null);
		profession.setStudies(Arrays.asList(new Study(owner, profession, null, "Uni")));
		InMemoryProfessionOutput professions = new InMemoryProfessionOutput();
		professions.save(profession);
		InMemoryPhoneOutput phones = new InMemoryPhoneOutput();
		phones.save(new Phone("555", "Telco", owner));

		assertEquals(1, new ProfessionUseCase(professions).getStudies(7).size());
		assertEquals("Ana", new PhoneUseCase(phones).getOwner("555").getFirstName());
		assertThrows(NoExistException.class, () -> new ProfessionUseCase(professions).getStudies(8));
		assertThrows(NoExistException.class, () -> new PhoneUseCase(phones).getOwner("999"));
	}

	@Test
	void relatedUseCasesRejectUpdatesForMissingRecords() {
		ProfessionUseCase profession = new ProfessionUseCase(new InMemoryProfessionOutput());
		PhoneUseCase phone = new PhoneUseCase(new InMemoryPhoneOutput());
		StudyUseCase study = new StudyUseCase(new InMemoryStudyOutput());
		assertThrows(NoExistException.class, () -> profession.edit(1, new Profession()));
		assertThrows(NoExistException.class, () -> phone.edit("555", new Phone()));
		assertThrows(NoExistException.class, () -> study.edit(1, 7, new Study()));
	}

	private static class InMemoryProfessionOutput implements ProfessionOutputPort {
		private Profession value;
		public Profession save(Profession profession) { value = profession; return value; }
		public Boolean delete(Integer id) { if (findById(id) == null) return false; value = null; return true; }
		public List<Profession> find() { return value == null ? Collections.emptyList() : Arrays.asList(value); }
		public Profession findById(Integer id) { return value != null && id.equals(value.getIdentification()) ? value : null; }
		public Integer count() { return value == null ? 0 : 1; }
	}

	private static class InMemoryPhoneOutput implements PhoneOutputPort {
		private Phone value;
		public Phone save(Phone phone) { value = phone; return value; }
		public Boolean delete(String id) { if (findById(id) == null) return false; value = null; return true; }
		public List<Phone> find() { return value == null ? Collections.emptyList() : Arrays.asList(value); }
		public Phone findById(String id) { return value != null && id.equals(value.getNumber()) ? value : null; }
		public Integer count() { return value == null ? 0 : 1; }
	}

	private static class InMemoryStudyOutput implements StudyOutputPort {
		private Study value;
		public Study save(Study study) { value = study; return value; }
		public Boolean delete(Integer personId, Integer professionId) {
			if (findById(personId, professionId) == null) return false;
			value = null;
			return true;
		}
		public List<Study> find() { return value == null ? Collections.emptyList() : Arrays.asList(value); }
		public Study findById(Integer personId, Integer professionId) {
			return value != null && personId.equals(value.getPerson().getIdentification())
					&& professionId.equals(value.getProfession().getIdentification()) ? value : null;
		}
		public Integer count() { return value == null ? 0 : 1; }
	}
}
