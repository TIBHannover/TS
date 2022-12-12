package uk.ac.ebi.spot.ols.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.spot.ols.service.OntologyFormatEnum;

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
	
	public void filterDownloadUrls(RepoFilterEnum filter, String keyword) {
		if (filter == RepoFilterEnum.ALL_FILES)
			return;
		if (filter == RepoFilterEnum.ALL_ONTOLOGIES) {
			Set<String> tempDownloadUrls = new HashSet<String>();
			for (String downloadUrl : this.getDownloadUrls()) {
				for (OntologyFormatEnum format : OntologyFormatEnum.values()) {
					if (downloadUrl.endsWith("."+format.toString()))
						tempDownloadUrls.add(downloadUrl);
				}
			}
			this.setDownloadUrls(tempDownloadUrls);
			
		}
		
		if (filter == RepoFilterEnum.MAPPING_FILES) {
			Set<String> tempDownloadUrls = new HashSet<String>();
			for (String downloadUrl : this.getDownloadUrls()) {
				if (downloadUrl.split("/")[downloadUrl.split("/").length -1].toLowerCase().replaceAll("[^a-zA-Z0-9]", "").contains(keyword.toLowerCase().replaceAll("[^a-zA-Z0-9]", "")))
					tempDownloadUrls.add(downloadUrl);
			}
			this.setDownloadUrls(tempDownloadUrls);
		}
		
		if (filter == RepoFilterEnum.MAPPING_ONTOLOGIES) {
			Set<String> tempDownloadUrls = new HashSet<String>();
			for (String downloadUrl : this.getDownloadUrls()) {
				for (OntologyFormatEnum format : OntologyFormatEnum.values()) {
				    if (downloadUrl.split("/")[downloadUrl.split("/").length -1].toLowerCase().replaceAll("[^a-zA-Z0-9]", "").contains(keyword.toLowerCase().replaceAll("[^a-zA-Z0-9]", "")))
					    if (downloadUrl.endsWith("."+format.toString()))
					        tempDownloadUrls.add(downloadUrl);
				}
			}
			this.setDownloadUrls(tempDownloadUrls);
		}
		
	}
	
}
