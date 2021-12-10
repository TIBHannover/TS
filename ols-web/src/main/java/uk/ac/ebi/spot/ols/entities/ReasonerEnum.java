package uk.ac.ebi.spot.ols.entities;

public enum ReasonerEnum {
	
	NONE("none"),
	OWL2("hermit"),
	EL("ELK");
    
	
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

    public String getPropertyName() {
        return propertyName;
    }

}
