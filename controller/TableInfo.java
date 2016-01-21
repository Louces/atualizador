package controller;

import view.Principal;

public class TableInfo {

	public static void refresh(String SN, int coluna, String gravar) {
		int rows = Principal.getTabela().getRowCount();

		for (int i = 0; i < rows; i++)
			if (Principal.getTabela().getValueAt(i, 1).equals(SN)) {
				Principal.getTabela().setValueAt(gravar, i, coluna);
			}
	}

	public static int getRow(String SN) {
		int rows = Principal.getTabela().getRowCount();

		for (int i = 0; i < rows; i++)
			if (Principal.getTabela().getValueAt(i, 1).equals(SN)) {
				return i;
			}
		return -1;
	}

	public static boolean contains(String string) {
		int rows = Principal.getTabela().getRowCount();

		for (int i = 0; i < rows; i++) {
			String aux = Principal.getTabela().getValueAt(i, 5) + "";
			if (aux.contains(string)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean endUpdate(){
		int rows = Principal.getTabela().getRowCount();
		int flag=0;
		
		for (int i = 0; i < rows; i++) {
			String aux = Principal.getTabela().getValueAt(i, 5) + "";
			if (aux.contains("-")) {
				flag++;
			}
		}
		
		if(flag==rows){
			return true;
		}else{
			return false;
		}
		
	}
}
