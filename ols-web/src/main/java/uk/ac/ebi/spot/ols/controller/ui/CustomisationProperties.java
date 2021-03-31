
package uk.ac.ebi.spot.ols.controller.ui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;

@ConfigurationProperties(prefix = "ols.customisation")
@Configuration("customisationProperties")
public class CustomisationProperties {

    @Value("${ols.customisation.debrand:false}")
    private boolean debrand;
    
    @Value("${ols.customisation.ebiInfo:false}")
    private boolean ebiInfo;
    
    @Value("${ols.customisation.logo:/img/TIB_Logo_en.png}")
    private String logo;

    @Value("${ols.customisation.title:Terminology Service}")
    private String title;

    @Value("${ols.customisation.short-title:TS}")
    private String shortTitle;

    @Value("${ols.customisation.org:TIB}")
    private String org;
    
    @Value("${ols.customisation.web: https://www.tib.eu/}")
    private String web;
    
    @Value("${ols.customisation.twitter: https://twitter.com/tibhannover?lang=en}")
    private String twitter;
    
    @Value("${ols.customisation.backgroundImage:/img/background_trial.jpg}")
    private String backgroundImage;
    
    @Value("${ols.customisation.backgroundColor:0080FF}")
    private String backgroundColor;
    
    @Value("${ols.customisation.issuesPage: https://github.com/TIBHannover/OLS/issues}")
    private String issuesPage;

    @Value("${ols.customisation.supportMail: information@tib.eu}")
    private String supportMail;

    public void setCustomisationModelAttributes(Model model) {
        model.addAttribute("debrand", debrand);
        model.addAttribute("ebiInfo", ebiInfo);
        model.addAttribute("logo", logo);
        model.addAttribute("title", title);
        model.addAttribute("shortTitle", shortTitle);
        model.addAttribute("org", org);
        model.addAttribute("web", web);
        model.addAttribute("twitter", twitter);
        model.addAttribute("backgroundImage", backgroundImage);
        model.addAttribute("backgroundColor", backgroundColor);
        model.addAttribute("issuesPage", issuesPage);
        model.addAttribute("supportMail", supportMail);
    }

    public boolean getDebrand() {
        return debrand;
    }
}
