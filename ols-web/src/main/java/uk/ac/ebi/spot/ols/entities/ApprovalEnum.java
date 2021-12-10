package uk.ac.ebi.spot.ols.entities;

public enum ApprovalEnum {
	
	ONREVIEW("onreview"),
	APPROVED("approved"),
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

    public String getPropertyName() {
        return propertyName;
    }

}
