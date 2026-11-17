package co.edu.javeriana.as.personapp.terminal.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@EnabledIfEnvironmentVariable(named = "PERSONAPP_ISOLATED_INTEGRATION_TEST", matches = "true")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class IsolatedCliIntegrationTest {
	private static InputStream originalInput;

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private CrudMenu menu;

	@BeforeAll
	static void provideExitOption() {
		originalInput = System.in;
		System.setIn(new ByteArrayInputStream("0\n".getBytes(StandardCharsets.UTF_8)));
	}

	@AfterAll
	static void restoreInput() {
		System.setIn(originalInput);
	}

	@Test
	void startsAndAcceptsExit() {
		assertTrue(context.isActive());
	}

	@Test
	void countsAllEntitiesInBothDatabases() {
		StringBuilder input = new StringBuilder();
		for (int entity = 1; entity <= 4; entity++) {
			for (int database = 1; database <= 2; database++) {
				input.append(entity).append('\n').append(database).append("\n3\n0\n");
			}
		}
		input.append("0\n");
		PrintStream originalOutput = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8);
				Scanner scanner = new Scanner(input.toString())) {
			System.setOut(captured);
			menu.start(scanner);
		} finally {
			System.setOut(originalOutput);
		}
		String text = output.toString(StandardCharsets.UTF_8);
		assertFalse(text.contains("La operacion fallo"), text);
		for (String label : new String[] { "Personas", "Profesiones", "Telefonos", "Estudios" }) {
			assertEquals(2, Pattern.compile(label + ": \\d+").matcher(text).results().count(), label);
		}
	}
}
