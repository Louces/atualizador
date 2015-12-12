package controller;

import view.Principal;

public class Console {

	public static void print(String msn) {
		Principal.getTextAreaConsole().append(" > " + msn + "\n");
		Principal.getTextAreaConsole().setCaretPosition(
				Principal.getTextAreaConsole().getText().length());
	}

}
