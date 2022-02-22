package uk.ac.ebi.spot.ols.entities;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.ElementCollection;

@UniqueField(message = "The id or preferredPrefix exists in previous records")
@Entity
@ApiModel
public class UserOntology {
    
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
    @ApiModelProperty(value = "PURL of the Ontology Suggestion", name = "PURL", dataType = "String", example = "http://purl.obolibrary.org/obo/iao.owl")
    private String PURL;
    
    private String URI;
    
    private String licenseURL;
    
    private String licenseLogo;
    
    private String licenseLabel;
    
    private String title;
    @Length(max = 2500)
    private String description;

    @ElementCollection
    @Column(length=3000)
    private List<String> creator;
    
    private String homePage;
    
    private String tracker;
    
    private String mailingList;
    
    @NotNull(message = "Preferred Prefix is mandatory")
    @Size(min=1, max=30,message = "Enter a string with max 30 characters")
    @Column(unique=true)
    @ApiModelProperty(value = "Preferred Prefiy of the Ontology Suggestion", name = "preferredPrefix", dataType = "String", example = "iao")
    private String preferredPrefix;
    
    private String baseURI;
    
    private ReasonerEnum reasoner;
    
    private String labelProperty;
    @ElementCollection
    private List<String> definitionProperty;
    @ElementCollection
    private List<String> synonymProperty;
    @ElementCollection
    private List<String> hierarchicalProperty;
    @ElementCollection
    private List<String> hiddenProperty;
    
    @NotNull(message = "oboSlims is mandatory")
    private boolean oboSlims;
    @ElementCollection
    private List<String> preferredRootTerm;
    
    private String logo;
    
    @NotNull(message = "isFoundary is mandatory")
    private boolean foundary;
    
    private ApprovalEnum approval;
    
    private String addedBy;
    

    public UserOntology() {}

	public UserOntology(long id, @NotNull(message = "Name is mandatory") String name,
			@NotNull(message = "PURL is mandatory") String pURL, String uRI, String licenseURL, String licenseLogo,
			String licenseLabel, String title, String description, List<String> creator, String homePage, String tracker,
			String mailingList, @NotNull(message = "Preferred Prefix is mandatory") String preferredPrefix,
			String baseURI, ReasonerEnum reasoner, String labelProperty, List<String> definitionProperty, List<String> synonymProperty,
			List<String> hierarchicalProperty, List<String> hiddenProperty,
			@NotNull(message = "oboSlims is mandatory") boolean oboSlims, List<String> preferredRootTerm, String logo,
			@NotNull(message = "isFoundary is mandatory") boolean foundary, ApprovalEnum approval, String addedBy) {
		super();
		this.id = id;
		this.name = name;
		PURL = pURL;
		URI = uRI;
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

	public String getPURL() {
		return PURL;
	}

	public void setPURL(String pURL) {
		PURL = pURL;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
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
		return "UserOntology [id=" + id + ", name=" + name + ", PURL=" + PURL + ", URI=" + URI + ", licenseURL="
				+ licenseURL + ", licenseLogo=" + licenseLogo + ", licenseLabel=" + licenseLabel + ", title=" + title
				+ ", description=" + description + ", creator=" + creator + ", homePage=" + homePage + ", tracker="
				+ tracker + ", mailingList=" + mailingList + ", preferredPrefix=" + preferredPrefix + ", baseURI="
				+ baseURI + ", reasoner=" + reasoner + ", labelProperty=" + labelProperty + ", definitionProperty="
				+ definitionProperty + ", synonymProperty=" + synonymProperty + ", hierarchicalProperty="
				+ hierarchicalProperty + ", hiddenProperty=" + hiddenProperty + ", oboSlims=" + oboSlims
				+ ", preferredRootTerm=" + preferredRootTerm + ", logo=" + logo + ", foundary=" + foundary + ", approval=" + approval + ", addedBy=" + addedBy +"]";
	}
}