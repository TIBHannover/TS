package uk.ac.ebi.spot.ols.controller.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.cache.annotation.Cacheable;

public class GitHubMetadata {
	
    public static String removePrefix(String s, String prefix)
    {
        if (s != null && prefix != null && s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }
    
	 public static GHContent traverse (GHContent ghc, String keyword, GHTag tag, GHRepository repo, GHRelease ghr, Set<String> downloadUrls) {
		 
		 if (ghc.isFile()) {
			 if(ghc.getPath().toLowerCase().contains(keyword) && (ghc.getPath().toLowerCase().contains(".owl") || ghc.getPath().toLowerCase().contains(".ttl") || ghc.getPath().toLowerCase().contains(".obo")))
					try {
						downloadUrls.add(repo.getFileContent(ghc.getPath(), tag.getCommit().getSHA1()).getDownloadUrl());  ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		 } else if (ghc.isDirectory()) {
			 try {
				for (GHContent ghc2 : repo.getDirectoryContent(ghc.getPath(),ghr.getTagName())) {
					 traverse(ghc2,keyword, tag, repo, ghr, downloadUrls);
				 }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		 return null;	 
	 }
	 
	 @Cacheable(value = "releases", key="#repoUrl.concat('-').concat('kohsuke').concat('-').concat(#keyword)")
	public List<Release> releasesKohsuke(String repoUrl,String keyword) {
	    String userName = "";
	    String personalAccessToken = "";
        try {
        	String line;
			File file = new File("githubpersonalaccesstoken.txt");
		    BufferedReader br = new BufferedReader(new FileReader(file));
            int count = 0;
		    while ((line = br.readLine()) != null) {
		    	if (count == 0)
		    		userName = line;
		    	else if (count == 1)
		    		personalAccessToken = line;
		    	count++;
		    }
		    br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Release> releasesWithRawUrls = new ArrayList<Release>();
		try {
			GitHub github = new GitHubBuilder().withOAuthToken(personalAccessToken, userName).build();
			GHRepository repo = null;
			if(repoUrl.startsWith("https://github.com/"))
				repo = github.getRepository(removePrefix(repoUrl,"https://github.com/"));
			else if (repoUrl.startsWith("http://github.com/"))
				repo = github.getRepository(removePrefix(repoUrl,"http://github.com/"));
			if(repo != null) {
				List<GHTag> tags = repo.listTags().asList();
				List<GHRelease> releases = repo.getReleases();
				for(GHRelease ghr : releases) {
					Set<String> downloadUrls = new HashSet<String>();
								
					for (GHTag tag : tags) {
						if(tag.getName().equals(ghr.getTagName())) {
							for (GHContent ghc : repo.getDirectoryContent("",ghr.getTagName())) {
								traverse(ghc,keyword, tag, repo, ghr, downloadUrls);
							}
						}
					}		
					releasesWithRawUrls.add(new Release(ghr.getName(), ghr.getHtmlUrl().toString(), ghr.getCreatedAt().toString(),downloadUrls));				
				} 
			}	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return releasesWithRawUrls;
	}
	
	@Cacheable(value = "releases", key="#repoUrl.concat('-').concat('rest').concat('-').concat(#keyword)")
	public List<Release> releasesREST(String repoUrl,String keyword){
	    StringBuilder basicToken = new StringBuilder();
        try {
        	String line;
			File file = new File("basicgithubauthentication.txt");
		    BufferedReader br = new BufferedReader(new FileReader(file));

		    while ((line = br.readLine()) != null)
		        basicToken.append(line);
		    br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Release> releases = new ArrayList<Release>();
		
        String[] parsedRepoUrl = repoUrl.split("/");
        StringBuilder sb = new StringBuilder();
        String institution = "";
        String user = "";
        for (int i = 0;i<parsedRepoUrl.length;i++) {  	
        	if(i == 2)
        		sb.append("api.");
        	if (i == 3) {
        		sb.append("repos").append("/");
        		institution = parsedRepoUrl[i];
        	}
        	if (i ==4)
        		user = parsedRepoUrl[i];
        	sb.append(parsedRepoUrl[i]).append("/");
        }   
        sb.append("releases");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(sb.toString());
            httpget.addHeader("Authorization", "Basic "+basicToken.toString());
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
                
                StringBuilder sbShaUrl = new StringBuilder();
                sbShaUrl.append("https://api.github.com/repos/"+institution+"/"+user+"/git/ref/tags/");
                sbShaUrl.append(item.getString("html_url").split("/")[item.getString("html_url").split("/").length - 1]);
                
                HttpGet httpgetSha = new HttpGet(sbShaUrl.toString());
                httpgetSha.addHeader("Authorization", "Basic "+basicToken.toString());
                
                String responseBodySha = httpclient.execute(httpgetSha, responseHandler);
                
                JSONObject shaObject = new JSONObject(responseBodySha);
                String sha  =shaObject.getJSONObject("object").getString("sha");
                HttpGet httpgetFileList = new HttpGet("https://api.github.com/repos/"+institution+"/"+user+"/git/trees/"+sha+"?recursive=1");
                httpgetFileList.addHeader("Authorization", "Basic "+basicToken.toString());
                
                String responseBodyFileList = httpclient.execute(httpgetFileList, responseHandler);
                
                JSONObject fileListObject = new JSONObject(responseBodyFileList);
                JSONArray tree = fileListObject.getJSONArray("tree");
                Set<String> downloadUrls = new HashSet<String>();
                for (int j = 0;j < tree.length() ; j++) {
                	final JSONObject node = tree.getJSONObject(j);
                	if(node.getString("path").toLowerCase().contains(keyword) && (node.getString("path").toLowerCase().contains(".owl") || node.getString("path").toLowerCase().contains(".ttl") || node.getString("path").toLowerCase().contains(".obo")))
                	    downloadUrls.add("https://raw.githubusercontent.com/"+institution+"/"+user+"/"+sha+"/"+node.getString("path"));
                }
        
                releases.add(new Release(item.getString("name"), item.getString("html_url"), item.getString("created_at"),downloadUrls));	
            }
            httpclient.close();        
            } catch(IOException ioe){
        	ioe.printStackTrace();
        }
        return releases;
	}
	
}
