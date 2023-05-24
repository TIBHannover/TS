package uk.ac.ebi.spot.ols.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.spot.ols.service.OntologyFormatEnum;

public class Issue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8358413379674282891L;
	String title;
	String description;
	boolean open;
	String creator;
	List<String> assignees;
	String htmlUrl;
	String createdAt;
	
	public Issue(String title, String description, boolean open, String creator, List<String> assignees, String htmlUrl,
			String createdAt) {
		super();
		this.title = title;
		this.description = description;
		this.open = open;
		this.creator = creator;
		this.assignees = assignees;
		this.htmlUrl = htmlUrl;
		this.createdAt = createdAt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public List<String> getAssignees() {
		return assignees;
	}

	public void setAssignees(List<String> assignees) {
		this.assignees = assignees;
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
	
	
	
}
