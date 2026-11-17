package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import org.springframework.stereotype.Controller;

@Controller
public class MenuPrincipal {

	private final CrudMenu crudMenu;

	public MenuPrincipal(CrudMenu crudMenu) {
		this.crudMenu = crudMenu;
	}

	public void inicio() {
		try (Scanner keyboard = new Scanner(System.in)) {
			crudMenu.start(keyboard);
		}
	}
}
