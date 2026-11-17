package co.edu.javeriana.as.personapp.terminal.menu;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.adapter.CrudInputAdapterCli;

class CrudMenuTest {
	@Test
	void runsTheCountOperationAgainstTheChosenDatabase() {
		PersonOutputPort personMaria = mock(PersonOutputPort.class);
		PersonOutputPort personMongo = mock(PersonOutputPort.class);
		when(personMaria.count()).thenReturn(3);
		CrudInputAdapterCli adapter = new CrudInputAdapterCli(personMaria, personMongo,
				mock(ProfessionOutputPort.class), mock(ProfessionOutputPort.class),
				mock(PhoneOutputPort.class), mock(PhoneOutputPort.class),
				mock(StudyOutputPort.class), mock(StudyOutputPort.class));
		CrudMenu menu = new CrudMenu(adapter);

		menu.start(new Scanner("1\n1\n3\n0\n0\n"));

		verify(personMaria).count();
		verifyNoInteractions(personMongo);
	}

	@Test
	void showsTheOwnerOfAPhoneFromTheRelationsOption() {
		PhoneOutputPort phoneMongo = mock(PhoneOutputPort.class);
		Person owner = new Person(1, "Ana", "Perez", Gender.FEMALE);
		when(phoneMongo.findById("555")).thenReturn(new Phone("555", "Telco", owner));
		CrudInputAdapterCli adapter = new CrudInputAdapterCli(mock(PersonOutputPort.class), mock(PersonOutputPort.class),
				mock(ProfessionOutputPort.class), mock(ProfessionOutputPort.class),
				mock(PhoneOutputPort.class), phoneMongo,
				mock(StudyOutputPort.class), mock(StudyOutputPort.class));
		PrintStream original = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
			new CrudMenu(adapter).start(new Scanner("3\n2\n7\n555\n0\n0\n"));
		} finally {
			System.setOut(original);
		}

		assertTrue(output.toString(StandardCharsets.UTF_8).contains("Duenio: CC 1 | Ana Perez"));
	}
}
