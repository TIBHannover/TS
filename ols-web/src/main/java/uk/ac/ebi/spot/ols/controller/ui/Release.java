package uk.ac.ebi.spot.ols.controller.ui;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Release implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8358413628674282891L;
	String name;
	String htmlUrl;
	String createdAt;
	Set<String> downloadUrls;
	
	public Release(String name, String htmlUrl, String createdAt) {
		super();
		this.name = name;
		this.htmlUrl = htmlUrl;
		this.createdAt = createdAt;
		this.downloadUrls = new HashSet<String>();
	}
	
	public Release(String name, String htmlUrl, String createdAt, Set<String> downloadUrls) {
		super();
		this.name = name;
		this.htmlUrl = htmlUrl;
		this.createdAt = createdAt;
		this.downloadUrls = downloadUrls;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHtmlUrl() {
		return htmlUrl;
	}
	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	public Set<String> getDownloadUrls() {
		return downloadUrls;
	}
	public void setDownloadUrls(Set<String> downloadUrls) {
		this.downloadUrls = downloadUrls;
	}
	
}
