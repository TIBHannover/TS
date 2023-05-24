package uk.ac.ebi.spot.ols.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import uk.ac.ebi.spot.ols.entities.Issue;
import uk.ac.ebi.spot.ols.entities.IssueFilterEnum;
import uk.ac.ebi.spot.ols.entities.Release;
import uk.ac.ebi.spot.ols.entities.RepoFilterEnum;

@Service
public class RepoMetadataService {

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

    public static String removePrefix(String s, String prefix)
    {
        if (s != null && prefix != null && s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }

	 public static GHContent traverse (GHContent ghc, String keyword, GHTag tag, GHRepository repo, GHRelease ghr, Set<String> downloadUrls) {

		 if (ghc.isFile()) {
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

	@Cacheable(value = "releases", key="#repoUrl.concat('-').concat('kohsuke').concat('-').concat(#filter).concat('-').concat(#keyword)")
	public List<Release> releasesKohsuke(String repoUrl, String externalToken, RepoFilterEnum filter, String keyword) {
	    String userName = "";
	    String personalAccessToken = "";

	    if(externalToken != null)
			if (externalToken.length() >= 1)
				personalAccessToken = externalToken;
		if(externalToken == null || externalToken.length() < 1) {
	        try {
	        	// reads from src/main/resource
				InputStream is = new ClassPathResource("/github.com.token.txt").getInputStream();
				try {
				    String contents = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);
				    System.out.println(contents);
				    personalAccessToken = contents.split("\n")[0];
				    userName = contents.split("\n")[1];
				} catch (IOException e) {
				    e.printStackTrace();
				} finally {
				    if (is != null) {
				        is.close();
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
					releasesWithRawUrls.add(addRelease(ghr.getName(), ghr.getHtmlUrl().toString(), ghr.getCreatedAt().toString(),downloadUrls,  filter, keyword));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return releasesWithRawUrls;
	}

	@Cacheable(value = "releases", key="#repoUrl.concat('-').concat('github').concat('-').concat('rest').concat('-').concat(#filter).concat('-').concat(#keyword)")
	public List<Release> releasesGithubREST(String repoUrl, String externalToken, RepoFilterEnum filter, String keyword){
	    StringBuilder token = new StringBuilder();

		if(externalToken != null)
			if (externalToken.length() >= 1)
				token.append(externalToken);
		if(externalToken == null || externalToken.length() < 1) {

	        try {
	        	// reads from src/main/resource
				InputStream is = new ClassPathResource("/github.com.token.txt").getInputStream();
				try {
					String contents = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);
				    token.append(contents.split("\n")[0]);
				} catch (IOException e) {
				    e.printStackTrace();
				} finally {
				    if (is != null) {
				        is.close();
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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


            String responseBody = runCallGithub(sb.toString(),token.toString());

            if (responseBody.equals(""))
            	return releases;

            JSONArray items = new JSONArray(responseBody);

            for (int i = 0; i< items.length();i++) {
                final JSONObject item = items.getJSONObject(i);
                Set<String> downloadUrls = new HashSet<String>();
                StringBuilder sbShaUrl = new StringBuilder();
                sbShaUrl.append("https://api.github.com/repos/"+institution+"/"+user+"/git/ref/tags/");
                sbShaUrl.append(item.getString("html_url").split("/")[item.getString("html_url").split("/").length - 1]);
                String responseBodySha = runCallGithub(sbShaUrl.toString(),token.toString());
                if (responseBodySha.equals("")) {
                	releases.add(addRelease(item.getString("name"), item.getString("html_url"), item.getString("created_at"),downloadUrls, filter, keyword));
                	continue;
                }

                JSONObject shaObject = new JSONObject(responseBodySha);
                String sha  =shaObject.getJSONObject("object").getString("sha");
                String responseBodyFileList = runCallGithub("https://api.github.com/repos/"+institution+"/"+user+"/git/trees/"+sha+"?recursive=1",token.toString());

                if (!responseBodyFileList.equals("")) {
                	JSONObject fileListObject = new JSONObject(responseBodyFileList);
                    JSONArray tree = fileListObject.getJSONArray("tree");

                    for (int j = 0;j < tree.length() ; j++) {
                    	final JSONObject node = tree.getJSONObject(j);
                    	downloadUrls.add("https://raw.githubusercontent.com/"+institution+"/"+user+"/"+sha+"/"+node.getString("path"));
                    }
                }

                releases.add(addRelease(item.getString("name"), item.getString("html_url"), item.getString("created_at"),downloadUrls, filter, keyword));
            }


        return releases;
	}

	@Cacheable(value = "releases", key="#repoUrl.concat('-').concat('gitlab').concat('-').concat('rest').concat('-').concat(#filter).concat('-').concat(#keyword)")
	public List<Release> releasesGitlabREST(String repoUrl, String externalToken, RepoFilterEnum filter, String keyword){

		List<Release> releases = new ArrayList<Release>();

        String[] parsedRepoUrl = repoUrl.split("/");
        StringBuilder sb = new StringBuilder();

        String gitlabInstance = "gitlab.com";
        for (int i = 0;i<parsedRepoUrl.length;i++) {
        	if (i ==2) {
        		gitlabInstance = parsedRepoUrl[i];
        		sb.append("https://"+gitlabInstance+"/api/v4/projects/");
        	}

        	if (i > 2) {
        		sb.append(parsedRepoUrl[i]);
        	}
        	if (i > 2 && i <parsedRepoUrl.length - 1)
        	sb.append("%2F");

        }

	    StringBuilder token = new StringBuilder();
	    if(externalToken != null)
			if (externalToken.length() >= 1)
				token.append(externalToken);
		if(externalToken == null || externalToken.length() < 1) {
	        try {
	        	// reads from src/main/resource
				InputStream is = new ClassPathResource("/"+gitlabInstance+".token.txt").getInputStream();
				try {
				    String contents = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);
				    token.append(contents.split("\n")[0]);
				} catch (IOException e) {
				    e.printStackTrace();
				} finally {
				    if (is != null) {
				        is.close();
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

            String responseBody = runCallGitlab(sb.toString(),token.toString());
            if(responseBody.equals(""))
            	return releases;
            JSONObject project = new JSONObject(responseBody);
            Long id = project.getLong("id");
            String releasesUrl = "https://"+gitlabInstance+"/api/v4/projects/"+id+"/releases";
            String responseBodyReleases = runCallGitlab(releasesUrl,token.toString());
            if(responseBodyReleases.equals(""))
            	return releases;
            JSONArray items = new JSONArray(responseBodyReleases);

            for (int i = 0; i< items.length();i++) {
                final JSONObject item = items.getJSONObject(i);

                String commitId = item.getJSONObject("commit").getString("id");

                String treeUrl = "https://"+gitlabInstance+"/api/v4/projects/"+id+"/repository/tree?ref="+commitId;
                String responseBodyTree = runCallGitlab(treeUrl,token.toString());
                if(responseBodyTree.equals(""))
                	responseBodyTree = "[]";
                JSONArray treeFiles = new JSONArray(responseBodyTree);
                Set<String> downloadUrls = new HashSet<String>();
                for (int j = 0;j < treeFiles.length() ; j++) {
                	final JSONObject node = treeFiles.getJSONObject(j);
                	downloadUrls.add(repoUrl+"/-/raw/"+commitId+"/"+node.getString("path"));

                }

                releases.add(addRelease(item.getString("name"), item.getJSONObject("_links").getString("self"), item.getString("created_at"),downloadUrls, filter, keyword));
            }

        return releases;
	}

	@Cacheable(value = "issues", key="#repoUrl.concat('-').concat(#filter).concat('-').concat('github').concat('-').concat('rest').concat('-').concat('issues')")
	public List<Issue> issuesGithubREST(String repoUrl, String externalToken, IssueFilterEnum filter){
	    StringBuilder token = new StringBuilder();


		if(externalToken != null)
			if (externalToken.length() >= 1)
				token.append(externalToken);
		if(externalToken == null || externalToken.length() < 1) {

	        try {
	        	// reads from src/main/resource
				InputStream is = new ClassPathResource("/github.com.token.txt").getInputStream();
				try {
					String contents = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);
				    token.append(contents.split("\n")[0]);
				} catch (IOException e) {
				    e.printStackTrace();
				} finally {
				    if (is != null) {
				        is.close();
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<Issue> issues = new ArrayList<Issue>();

        String[] parsedRepoUrl = repoUrl.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<parsedRepoUrl.length;i++) {
        	if(i == 2)
        		sb.append("api.");
        	if (i == 3) {
        		sb.append("repos").append("/");
        	}

        	sb.append(parsedRepoUrl[i]).append("/");
        }
        sb.append("issues");

		if (filter == IssueFilterEnum.OPEN)
			sb.append("?state=open");
		else if (filter == IssueFilterEnum.ALL)
			sb.append("?state=all");
		else if (filter == IssueFilterEnum.CLOSED)
			sb.append("?state=closed");

            String responseBody = runCallGithub(sb.toString(),token.toString());

            if (responseBody.equals(""))
            	return issues;

            JSONArray items = new JSONArray(responseBody);

            for (int i = 0; i< items.length();i++) {
                final JSONObject item = items.getJSONObject(i);

                String state = item.getString("state");
                boolean open = false;
                if(state.equals("open"))
                	open = true;
                List<String> assignees = new ArrayList<String>();

                JSONArray assigneesJSON = item.getJSONArray("assignees");

                for (int j = 0; j< assigneesJSON.length();j++ ) {
                	final JSONObject assigneeJSON = assigneesJSON.getJSONObject(j);
                	assignees.add(assigneeJSON.getString("login"));
                }

                String creator = item.getJSONObject("user").getString("login");
				String body = "";
				if(item.get("body") != null)
					body = item.optString("body");

                issues.add(addIssue(item.getString("title"), body, open, creator, assignees, item.getString("html_url"), item.getString("created_at")));
            }


        return issues;
	}


	@Cacheable(value = "issues", key="#repoUrl.concat('-').concat(#filter).concat('-').concat('gitlab').concat('-').concat('rest').concat('-').concat('issues')")
	public List<Issue> issuesGitlabREST(String repoUrl, String externalToken, IssueFilterEnum filter){

		List<Issue> issues = new ArrayList<Issue>();

        String[] parsedRepoUrl = repoUrl.split("/");
        StringBuilder sb = new StringBuilder();

        String gitlabInstance = "gitlab.com";
        for (int i = 0;i<parsedRepoUrl.length;i++) {
        	if (i ==2) {
        		gitlabInstance = parsedRepoUrl[i];
        		sb.append("https://"+gitlabInstance+"/api/v4/projects/");
        	}

        	if (i > 2) {
        		sb.append(parsedRepoUrl[i]);
        	}
        	if (i > 2 && i <parsedRepoUrl.length - 1)
        	sb.append("%2F");

        }

	    StringBuilder token = new StringBuilder();
	    if(externalToken != null)
			if (externalToken.length() >= 1)
				token.append(externalToken);
		if(externalToken == null || externalToken.length() < 1) {
	        try {
	        	// reads from src/main/resource
				InputStream is = new ClassPathResource("/"+gitlabInstance+".token.txt").getInputStream();
				try {
				    String contents = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);
				    token.append(contents.split("\n")[0]);
				} catch (IOException e) {
				    e.printStackTrace();
				} finally {
				    if (is != null) {
				        is.close();
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

            String responseBody = runCallGitlab(sb.toString(),token.toString());
            if(responseBody.equals(""))
            	return issues;
            JSONObject project = new JSONObject(responseBody);
            Long id = project.getLong("id");
            String issuesUrl = "https://"+gitlabInstance+"/api/v4/projects/"+id+"/issues";
            StringBuilder issueUrlBuilder = new StringBuilder(issuesUrl);
			if (filter == IssueFilterEnum.OPEN)
				issueUrlBuilder.append("?state=opened");
			else if (filter == IssueFilterEnum.ALL)
				issueUrlBuilder.append("?state=all");
			else if (filter == IssueFilterEnum.CLOSED)
				issueUrlBuilder.append("?state=closed");

            String responseBodyIssues = runCallGitlab(issueUrlBuilder.toString(),token.toString());
            if(responseBodyIssues.equals(""))
            	return issues;
            JSONArray items = new JSONArray(responseBodyIssues);

            for (int i = 0; i< items.length();i++) {
                final JSONObject item = items.getJSONObject(i);

                String state = item.getString("state");
                boolean open = false;
                if(state.equals("opened"))
                	open = true;
                List<String> assignees = new ArrayList<String>();

                JSONArray assigneesJSON = item.getJSONArray("assignees");

                for (int j = 0; j< assigneesJSON.length();j++ ) {
                	final JSONObject assigneeJSON = assigneesJSON.getJSONObject(j);
                	assignees.add(assigneeJSON.getString("username"));
                }

                String creator = item.getJSONObject("author").getString("name");
                String description = "";
				if(item.get("description") != null)
                    description = item.optString("description");
                issues.add(addIssue(item.getString("title"), description, open, creator, assignees, item.getString("web_url"), item.getString("created_at")));
            }

        return issues;
	}


    public String runCallGithub(String callUrl, String token) {
      CloseableHttpClient httpclient = HttpClients.createDefault();
      HttpGet httpget = new HttpGet(callUrl);
//      httpget.addHeader("Authorization", "Basic "+token.toString());
      httpget.addHeader("Authorization", "Bearer "+token.toString());
      try {
		String responseBody = httpclient.execute(httpget, responseHandler);
		return responseBody;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

      return "";
    }

    public String runCallGitlab(String callUrl, String token) {
        StringBuilder responseBody = new StringBuilder();
        try {
			URL url = new URL(callUrl);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestProperty("PRIVATE-TOKEN", token);
		      try (BufferedReader reader = new BufferedReader(
	                  new InputStreamReader(http.getInputStream()))) {
	          for (String line; (line = reader.readLine()) != null; ) {
	              responseBody.append(line);
	          }
	      }
			http.disconnect();
			return responseBody.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "";
    }

	public Release addRelease(String name, String htmlUrl, String createdAt, Set<String> downloadUrls, RepoFilterEnum filter, String keyword) {
		Release release = new Release(name, htmlUrl, createdAt,downloadUrls);
		release.filterDownloadUrls(filter, keyword);
		return release;
	}

	public Issue addIssue(String title, String description, boolean state, String creator, List<String> assignees, String htmlUrl, String createdAt) {
		Issue issue = new Issue(title, description, state, creator, assignees, htmlUrl, createdAt);
		return issue;
	}

}
