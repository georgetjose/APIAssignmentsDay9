package tests.rest;

import java.util.List;
import java.util.Random;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.rest.RESTAssuredBase;

public class AppOperations extends RESTAssuredBase
{
	public String appId;
	
	@BeforeTest
	public void setValues() 
	{
		testCaseName = "App Operation Test Cases";
		testDescription = "Create App, Update the logo, Verify the app in the List Apps";
		nodes = "OKTA - App Operations";
		authors = "George";
		category = "API";
		dataFileName = "AppBody";
		dataFileType = "JSON";
	}
	
	@Test(priority=1,enabled=false)
	public void createApp() 
	{
		System.out.println("\n\n*******************************************************************");
		System.out.println("App Creation\n\n");
		Random rand = new Random(); 
		  
        // Generate random integers in range 0 to 999 
        int randomnumber = rand.nextInt(1000);
        String createAppBody = "{\r\n" + 
        		"    \"name\": \"oidc_client\",\r\n" + 
        		"    \"label\": \"Sample App"+randomnumber+"\",\r\n" + 
        		"    \"signOnMode\": \"OPENID_CONNECT\",\r\n" + 
        		"    \"credentials\": {\r\n" + 
        		"      \"oauthClient\": {\r\n" + 
        		"        \"token_endpoint_auth_method\": \"client_secret_post\"\r\n" + 
        		"      }\r\n" + 
        		"    },\r\n" + 
        		"    \"settings\": {\r\n" + 
        		"      \"oauthClient\": {\r\n" + 
        		"        \"client_uri\": \"http://dev-340230.okta.com\",\r\n" + 
        		"        \"logo_uri\": \"http://developer.okta.com/assets/images/logo-new.png\",\r\n" + 
        		"        \"redirect_uris\": [\r\n" + 
        		"          \"https://example.com/oauth2/callback\",\r\n" + 
        		"          \"myapp://callback\"\r\n" + 
        		"        ],\r\n" + 
        		"        \"response_types\": [\r\n" + 
        		"          \"token\",\r\n" + 
        		"          \"id_token\",\r\n" + 
        		"          \"code\"\r\n" + 
        		"        ],\r\n" + 
        		"        \"grant_types\": [\r\n" + 
        		"          \"implicit\",\r\n" + 
        		"          \"authorization_code\"\r\n" + 
        		"        ],\r\n" + 
        		"        \"application_type\": \"native\"\r\n" + 
        		"      }\r\n" + 
        		"    }\r\n" + 
        		"}";
        
        Response createAppResponse = postWithHeaderAndJsonBody(map,createAppBody,"apps");
        System.out.println(createAppResponse.getStatusCode());
        appId=createAppResponse.jsonPath().get("id");
		System.out.println("---->App ID: "+appId);
	}
	
	@Test(priority=2,enabled=false)
	public void listAppsAndVerify() 
	{
		System.out.println("\n\n*******************************************************************");
		System.out.println("App List and its Verification\n\n");
		int count = 0;
		Response listAppsResponse = getWithHeader(map, "apps");
		System.out.println("--->"+listAppsResponse.getStatusCode());
		JsonPath listAppsJsonResponse = listAppsResponse.jsonPath();
		List<String> allItems = listAppsJsonResponse.getList("$");
		for(int i=0;i<allItems.size();i++)
		{
			String eachAppId = listAppsJsonResponse.getString("["+i+"].\"id\"");
			if(eachAppId.equals(appId))
				count++;
		}
		if(count==0)
			System.out.println("---->The newly created app is not available in the list.");
		else
			System.out.println("---->The newly created app is available in the list.");
	}
}
