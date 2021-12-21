package uk.ac.ebi.spot.ols.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReasonerEnum {
	
	NONE("NONE"),
	OWL2("OWL2"),
	EL("EL");
    
	
    private final String propertyName;

    ReasonerEnum(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public static String[] getNames() {
    	String[] commands = new String[ReasonerEnum.values().length];
    	for (int i = 0;i<ReasonerEnum.values().length;i++) {
    		commands[i] = ReasonerEnum.values()[i].getPropertyName();
    	}
    	return commands;
    }
    
    @JsonValue
    public String getPropertyName() {
        return propertyName;
    }

}
