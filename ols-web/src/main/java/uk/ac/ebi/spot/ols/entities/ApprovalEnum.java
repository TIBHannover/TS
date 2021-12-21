package uk.ac.ebi.spot.ols.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ApprovalEnum {
	
	ONREVIEW("ONREVIEW"),
	APPROVED("APPROVED"),
	REJECTED("REJECTED");
    
	
    private final String propertyName;

    ApprovalEnum(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public static String[] getNames() {
    	String[] commands = new String[ApprovalEnum.values().length];
    	for (int i = 0;i<ApprovalEnum.values().length;i++) {
    		commands[i] = ApprovalEnum.values()[i].getPropertyName();
    	}
    	return commands;
    }
    
    @JsonValue
    public String getPropertyName() {
        return propertyName;
    }

}
