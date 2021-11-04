package uk.ac.ebi.spot.ols.controller.api;

public enum Schema {
	
    dfg("dfg"),
    collection("collection"),
    subject("subject"),
    bk("bk"),
    gbv("gbv");

    private String name;

    Schema(String name){
       this.name = name;
    }

    public String getName() {
       return name;
    }

}
