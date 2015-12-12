package controller;

public class FilterCommand {
	
	public static String filter(String command) {
		int start = 0, end = 0;

		for (int i = 0; i < command.length() - 1; i++) {
			if (command.charAt(i) == '\n') {
				start = i + 1;
				for (int j = i + 1; j < command.length() - 1; j++) {
					if (command.charAt(j) == '\n') {
						end = j - 1;
						break;
					}
				}

				if (end != 0){
					break;
				}
			}
		}
		return command.substring(start, end);
	}
	
}
