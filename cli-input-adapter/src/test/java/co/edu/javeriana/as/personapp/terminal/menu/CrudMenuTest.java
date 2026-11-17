package co.edu.javeriana.as.personapp.terminal.menu;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
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
}
