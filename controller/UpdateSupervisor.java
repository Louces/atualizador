package controller;

import java.util.ArrayList;

import supervisor.Supervisor4Legacy;
import view.Principal;

public class UpdateSupervisor {

	@SuppressWarnings("unused")
	private int nColetores;
	private String typeColetor;
	private ArrayList<Object> SPVL;
	
	public void update() {
		Principal.configBtn(5, false);
		config();

		if (typeColetor.contains("8886")) {
			ArrayList<Supervisor4Legacy> spvlLegacy = new ArrayList<>();

			for (Object array : SPVL) {
				spvlLegacy.add((Supervisor4Legacy) array);
			}

			for (Supervisor4Legacy spvl : spvlLegacy) {
				spvl.Update();
			}
		Principal.configBtn(1, true);
		}
	}
	
	public void config() {
		nColetores = DiscoveryTypeColetor.getColetoresValidos();
		typeColetor = DiscoveryTypeColetor.getTypeColetor();
		SPVL = DiscoveryNetwork.getSupervisores();
	}
}
