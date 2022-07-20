package uk.ac.ebi.spot.ols.controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GitHubMetadata {
	
    public static String removePrefix(String s, String prefix)
    {
        if (s != null && prefix != null && s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }

	public Map<String, String> releaseUrls(String repoUrl) {
        Map<String, String> releaseUrls = new HashMap<String,String>();
		try {
			GitHub github = new GitHubBuilder().build();
			GHRepository repo = null;
			if(repoUrl.startsWith("https://github.com/"))
				repo = github.getRepository(removePrefix(repoUrl,"https://github.com/"));
			else if (repoUrl.startsWith("http://github.com/"))
				repo = github.getRepository(removePrefix(repoUrl,"http://github.com/"));
			if(repo != null)	
			for(GHRelease ghr : repo.getReleases()) {
				releaseUrls.put(ghr.getName(), ghr.getHtmlUrl().toString());					
			} 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return releaseUrls;
	}
	
	public List<Release> releaseUrls2(String repoUrl){
		List<Release> releaseUrls = new ArrayList<Release>();
		
        String[] parsedRepoUrl = repoUrl.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<parsedRepoUrl.length;i++) {  	
        	if(i == 2)
        		sb.append("api.");
        	if (i == 3)
        		sb.append("repos").append("/");
        	sb.append(parsedRepoUrl[i]).append("/");
        }   
        sb.append("releases");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(sb.toString());
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };         
            
            String responseBody = httpclient.execute(httpget, responseHandler);
            
            JSONArray items = new JSONArray(responseBody);
            
            for (int i = 0; i< items.length();i++) {
                final JSONObject item = items.getJSONObject(i);
                releaseUrls.add(new Release(item.getString("name"), item.getString("html_url"), item.getString("created_at")));	
            }
            httpclient.close();        
            } catch(IOException ioe){
        	ioe.printStackTrace();
        }
        return releaseUrls;
	}
	
	private class Release {
		String name;
		String htmlUrl;
		String createdAt;
		public Release(String name, String htmlUrl, String createdAt) {
			super();
			this.name = name;
			this.htmlUrl = htmlUrl;
			this.createdAt = createdAt;
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
		
	}
}
