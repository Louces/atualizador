package controller;

import view.Principal;

public class TableInfo {

	public static void refresh(String SN, int coluna, String gravar) {

		int rows = Principal.getTabela().getRowCount();

		for (int i = 0; i < rows; i++)
			if (Principal.getTabela().getValueAt(i, 1).equals(SN))
				Principal.getTabela().setValueAt(gravar, i, coluna);

	}

}
