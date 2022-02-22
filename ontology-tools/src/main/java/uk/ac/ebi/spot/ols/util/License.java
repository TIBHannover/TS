package uk.ac.ebi.spot.ols.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class License {
	
	@ApiModelProperty(value = "URL of the license", name = "url", dataType = "String", example = "http://creativecommons.org/licenses/by/4.0/")
	String url;
	@ApiModelProperty(value = "Logo of the license", name = "logo", dataType = "String", example = "http://mirrors.creativecommons.org/presskit/buttons/80x15/png/by.png")
	String logo;
	@ApiModelProperty(value = "Label of the license", name = "label", dataType = "String", example = "CC-BY")
	String label;
	
	public License() {}
	
	public License(String url, String logo, String label) {
		super();
		this.url = url;
		this.logo = logo;
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "License [url=" + url + ", logo=" + logo + ", label=" + label + "]";
	}

	
	
}
