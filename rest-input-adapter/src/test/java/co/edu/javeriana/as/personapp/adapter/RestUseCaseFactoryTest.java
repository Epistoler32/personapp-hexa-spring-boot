package co.edu.javeriana.as.personapp.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;

class RestUseCaseFactoryTest {
	@Test
	void routesCallsOnlyToTheSelectedDatabase() {
		PersonOutputPort personMaria = mock(PersonOutputPort.class);
		PersonOutputPort personMongo = mock(PersonOutputPort.class);
		RestUseCaseFactory factory = new RestUseCaseFactory(personMaria, personMongo,
				mock(ProfessionOutputPort.class), mock(ProfessionOutputPort.class),
				mock(PhoneOutputPort.class), mock(PhoneOutputPort.class),
				mock(StudyOutputPort.class), mock(StudyOutputPort.class));

		factory.person("MONGO").findAll();

		verify(personMongo).find();
		verifyNoInteractions(personMaria);
		assertThrows(IllegalArgumentException.class, () -> factory.person("OTHER"));
	}
}
