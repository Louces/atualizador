package controller;

import view.Principal;

public class DiscoveryNetwork {

	DiscoveryTypeColetor typeColetor = new DiscoveryTypeColetor();
	
	public void network(){
		
		typeColetor.validate();
		
		if(Principal.lbTypeColetor.getText().contains("8886")){
			
			switch (typeColetor.getColetoresValidos()) {
			
			case 1:
				
				break;
			case 2:
				
				break;
			
			case 3:
				
				break;

			default:
				break;
			}
			
		}else if(Principal.lbTypeColetor.getText().contains("8887")){
			
		}else{
			
		}
		
	}
	
}
