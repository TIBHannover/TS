package uk.ac.ebi.spot.ols.entities;

import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@UniqueField(message = "The id or preferredPrefix exists in previous records")
@Document(collection = "user_ontology")
@ApiModel
public class UserOntology {
	
    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @NotNull(message = "Name is mandatory.")
    @Size(min=1, max=30,message = "Enter a string with max 30 characters.")
    @Column(unique=true)
    @ApiModelProperty(value = "Name/Ontology ID of the Suggestion", name = "name", dataType = "String", example = "iao")
    private String name;
    
    @ValidURL(message = "Enter a valid URL.")
    @NotNull(message = "PURL is mandatory.")
    @Size(min=1, message = "PURL is mandatory.")
    @ApiModelProperty(value = "PURL of the Ontology Suggestion", name = "permanenturl", dataType = "String", example = "http://purl.obolibrary.org/obo/iao.owl")
    private String permanenturl;
    
    private String URI;
    
    private String licenseURL;
    
    private String licenseLogo;
    
    private String licenseLabel;
    
    private String title;
    
    private String description;

    private List<String> creator;
    
    private String homePage;
    
    private String tracker;
    
    private String mailingList;
    
    @NotNull(message = "Preferred Prefix is mandatory")
    @Size(min=1, max=30,message = "Enter a string with max 30 characters")
    @Column(unique=true)
    @ApiModelProperty(value = "Preferred Prefix of the Ontology Suggestion", name = "preferredPrefix", dataType = "String", example = "iao")
    private String preferredPrefix;
    
    private String baseURI;
    
    private ReasonerEnum reasoner;
    
    private String labelProperty;
    
    private List<String> definitionProperty;
    
    private List<String> synonymProperty;
    
    private List<String> hierarchicalProperty;
    
    private List<String> hiddenProperty;
    
    @NotNull(message = "oboSlims is mandatory")
    private boolean oboSlims;
    
    private List<String> preferredRootTerm;
    
    private String logo;
    
    @NotNull(message = "isFoundary is mandatory")
    private boolean foundary;
    
    private ApprovalEnum approval;
    
    private String addedBy;
    

    public UserOntology() {}

	public UserOntology(long id, @NotNull(message = "Name is mandatory") String name,
			@NotNull(message = "PURL is mandatory") String permanenturl, String URI, String licenseURL, String licenseLogo,
			String licenseLabel, String title, String description, List<String> creator, String homePage, String tracker,
			String mailingList, @NotNull(message = "Preferred Prefix is mandatory") String preferredPrefix,
			String baseURI, ReasonerEnum reasoner, String labelProperty, List<String> definitionProperty, List<String> synonymProperty,
			List<String> hierarchicalProperty, List<String> hiddenProperty,
			@NotNull(message = "oboSlims is mandatory") boolean oboSlims, List<String> preferredRootTerm, String logo,
			@NotNull(message = "isFoundary is mandatory") boolean foundary, ApprovalEnum approval, String addedBy) {
		super();
		this.id = id;
		this.name = name;
		this.permanenturl = permanenturl;
		this.URI = URI;
		this.licenseURL = licenseURL;
		this.licenseLogo = licenseLogo;
		this.licenseLabel = licenseLabel;
		this.title = title;
		this.description = description;
		this.creator = creator;
		this.homePage = homePage;
		this.tracker = tracker;
		this.mailingList = mailingList;
		this.preferredPrefix = preferredPrefix;
		this.baseURI = baseURI;
		this.reasoner = reasoner;
		this.labelProperty = labelProperty;
		this.definitionProperty = definitionProperty;
		this.synonymProperty = synonymProperty;
		this.hierarchicalProperty = hierarchicalProperty;
		this.hiddenProperty = hiddenProperty;
		this.oboSlims = oboSlims;
		this.preferredRootTerm = preferredRootTerm;
		this.logo = logo;
		this.foundary = foundary;
		this.approval = approval;
		this.addedBy = addedBy;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPermanenturl() {
		return permanenturl;
	}

	public void setPermanenturl(String permanenturl) {
		this.permanenturl = permanenturl;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String URI) {
		this.URI = URI;
	}

	public String getLicenseURL() {
		return licenseURL;
	}

	public void setLicenseURL(String licenseURL) {
		this.licenseURL = licenseURL;
	}

	public String getLicenseLogo() {
		return licenseLogo;
	}

	public void setLicenseLogo(String licenseLogo) {
		this.licenseLogo = licenseLogo;
	}

	public String getLicenseLabel() {
		return licenseLabel;
	}

	public void setLicenseLabel(String licenseLabel) {
		this.licenseLabel = licenseLabel;
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

	public List<String> getCreator() {
		return creator;
	}

	public void setCreator(List<String> creator) {
		this.creator = creator;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getTracker() {
		return tracker;
	}

	public void setTracker(String tracker) {
		this.tracker = tracker;
	}

	public String getMailingList() {
		return mailingList;
	}

	public void setMailingList(String mailingList) {
		this.mailingList = mailingList;
	}

	public String getPreferredPrefix() {
		return preferredPrefix;
	}

	public void setPreferredPrefix(String preferredPrefix) {
		this.preferredPrefix = preferredPrefix;
	}

	public String getBaseURI() {
		return baseURI;
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	public ReasonerEnum getReasoner() {
		return reasoner;
	}

	public void setReasoner(ReasonerEnum reasoner) {
		this.reasoner = reasoner;
	}

	public String getLabelProperty() {
		return labelProperty;
	}

	public void setLabelProperty(String labelProperty) {
		this.labelProperty = labelProperty;
	}

	public List<String> getDefinitionProperty() {
		return definitionProperty;
	}

	public void setDefinitionProperty(List<String> definitionProperty) {
		this.definitionProperty = definitionProperty;
	}

	public List<String> getSynonymProperty() {
		return synonymProperty;
	}

	public void setSynonymProperty(List<String> synonymProperty) {
		this.synonymProperty = synonymProperty;
	}

	public List<String> getHierarchicalProperty() {
		return hierarchicalProperty;
	}

	public void setHierarchicalProperty(List<String> hierarchicalProperty) {
		this.hierarchicalProperty = hierarchicalProperty;
	}

	public List<String> getHiddenProperty() {
		return hiddenProperty;
	}

	public void setHiddenProperty(List<String> hiddenProperty) {
		this.hiddenProperty = hiddenProperty;
	}

	public boolean isOboSlims() {
		return oboSlims;
	}

	public void setOboSlims(boolean oboSlims) {
		this.oboSlims = oboSlims;
	}

	public List<String> getPreferredRootTerm() {
		return preferredRootTerm;
	}

	public void setPreferredRootTerm(List<String> preferredRootTerm) {
		this.preferredRootTerm = preferredRootTerm;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public boolean isFoundary() {
		return foundary;
	}

	public void setFoundary(boolean foundary) {
		this.foundary = foundary;
	}

	public ApprovalEnum getApproval() {
		return approval;
	}

	public void setApproval(ApprovalEnum approval) {
		this.approval = approval;
	}

	public String getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}

	@Override
	public String toString() {
		return "UserOntology [id=" + id + ", name=" + name + ", PURL=" + permanenturl + ", URI=" + URI + ", licenseURL="
				+ licenseURL + ", licenseLogo=" + licenseLogo + ", licenseLabel=" + licenseLabel + ", title=" + title
				+ ", description=" + description + ", creator=" + creator + ", homePage=" + homePage + ", tracker="
				+ tracker + ", mailingList=" + mailingList + ", preferredPrefix=" + preferredPrefix + ", baseURI="
				+ baseURI + ", reasoner=" + reasoner + ", labelProperty=" + labelProperty + ", definitionProperty="
				+ definitionProperty + ", synonymProperty=" + synonymProperty + ", hierarchicalProperty="
				+ hierarchicalProperty + ", hiddenProperty=" + hiddenProperty + ", oboSlims=" + oboSlims
				+ ", preferredRootTerm=" + preferredRootTerm + ", logo=" + logo + ", foundary=" + foundary + ", approval=" + approval + ", addedBy=" + addedBy +"]";
	}
}