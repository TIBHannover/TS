package uk.ac.ebi.spot.ols.entities;

import org.springframework.stereotype.Component;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class URLValidator implements ConstraintValidator<ValidURL, String> {

    @Override
    public boolean isValid(String URLName, ConstraintValidatorContext context) {
    	
        if (URLName == null || URLName.isEmpty() || URLName.length() < 1)
        	return true;
    	
        try {
            HttpURLConnection.setFollowRedirects(true);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
               (HttpURLConnection) new URL(URLName).openConnection();
            con.setInstanceFollowRedirects(true);
            con.setRequestMethod("HEAD");
            
            while (con.getHeaderField("Location") != null) {
            	con = (HttpURLConnection) new URL(con.getHeaderField("Location")).openConnection();
            	con.setInstanceFollowRedirects(true);
                con.setRequestMethod("HEAD");
            }                  
            
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK );
          }
          catch (Exception e) {
             System.out.println(e.getMessage());
             return false;
          }
    }
}
