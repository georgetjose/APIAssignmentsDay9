package tests.rest;

import java.util.List;
import java.util.Random;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.rest.RESTAssuredBase;

public class ClientOperations extends RESTAssuredBase
{
	public String clientId;

	@BeforeTest
	public void setValues() 
	{
		testCaseName = "Client Operation Test Cases";
		testDescription = "Create Verify Update Verify and Delete Client";
		nodes = "OKTA - Client Operations";
		authors = "George";
		category = "API";
		dataFileName = "ClientBody";
		dataFileType = "JSON";
	}
	
	@Test(priority=1)
	public void createClient() 
	{
		System.out.println("\n\n*******************************************************************");
		System.out.println("Client Creation\n\n");
		Random rand = new Random(); 
		  
        // Generate random integers in range 0 to 999 
        int randomnumber = rand.nextInt(1000);
		String createClientBody ="{\r\n" + 
				"    \"client_name\": \"Web client "+randomnumber+"\",\r\n" + 
				"    \"redirect_uris\": [\r\n" + 
				"      \"https://httpbin.org/get\"\r\n" + 
				"    ],\r\n" + 
				"    \"response_types\": [\r\n" + 
				"      \"code\",\r\n" + 
				"      \"token\",\r\n" + 
				"      \"id_token\"\r\n" + 
				"    ],\r\n" + 
				"    \"grant_types\": [\r\n" + 
				"      \"refresh_token\",\r\n" + 
				"      \"authorization_code\",\r\n" + 
				"      \"implicit\"\r\n" + 
				"    ],\r\n" + 
				"    \"token_endpoint_auth_method\": \"client_secret_basic\",\r\n" + 
				"    \"application_type\": \"web\"\r\n" + 
				"  }";
		
		Response createClient = postWithHeaderAndJsonBody(map,createClientBody,"clients");
		
		clientId=createClient.jsonPath().get("client_id");
		System.out.println("---->Client ID: "+clientId);
	}
	
	@Test(priority=2)
	public void listClientsAndVerify() 
	{
		System.out.println("\n\n*******************************************************************");
		System.out.println("Client List and its Verification\n\n");
		int count = 0;
		Response listClientsResponse = getWithHeader(map, "clients");
		System.out.println("--->"+listClientsResponse.getStatusCode());
		JsonPath listClientsJsonResponse = listClientsResponse.jsonPath();
		List<String> allItems = listClientsJsonResponse.getList("$");
		for(int i=0;i<allItems.size();i++)
		{
			String eachClientId = listClientsJsonResponse.getString("["+i+"].\"client_id\"");
			if(eachClientId.equals(clientId))
				count++;
		}
		if(count==0)
			System.out.println("---->The newly created client is not available in the list.");
		else
			System.out.println("---->The newly created client is available in the list.");
	}
	
	@Test(priority=3)
	public void updateClientAndVerify() 
	{
		System.out.println("\n\n*******************************************************************");
		System.out.println("Update Client and its Verification\n\n");
		String updateClientBody ="{\r\n" + 
				"	\"client_id\": \""+clientId+"\",\r\n" + 
				"	\"client_name\": \"Updated Web client\",\r\n" + 
				"	\"redirect_uris\": [\r\n" + 
				"		\"https://httpbin.org/get\"\r\n" + 
				"	],\r\n" + 
				"	\"response_types\": [\r\n" + 
				"		\"code\",\r\n" + 
				"		\"token\",\r\n" + 
				"		\"id_token\"\r\n" + 
				"	],\r\n" + 
				"	\"grant_types\": [\r\n" + 
				"		\"refresh_token\",\r\n" + 
				"		\"authorization_code\",\r\n" + 
				"		\"implicit\"\r\n" + 
				"	],\r\n" + 
				"	\"token_endpoint_auth_method\": \"client_secret_basic\",\r\n" + 
				"	\"application_type\": \"web\"\r\n" + 
				"}";
		Response updateClientResponse = putWithHeaderAndBodyParam(map, updateClientBody, "clients/"+clientId);
		if(updateClientResponse.getStatusCode()==200)
			System.out.println("The newly created client is updated successfully.");
		else
			System.out.println("The newly created client is not updated");
	
		Response getClientResponse = getWithHeader(map, "clients/"+clientId);
		String clientName=getClientResponse.jsonPath().get("client_name");
		if(clientName.equals("Updated Web client"))
			System.out.println("---->The client name Updated is verified");
		else
			System.out.println("---->The client name is not Updated and verification failed");
	}
	
	@Test(priority=4)
	public void deleteClient() throws Exception 
	{
		System.out.println("\\n\\n*******************************************************************");
		System.out.println("Client Deletion\n\n");
		Response deleteClientResponse = deleteWithHeaderAndPathParamWithoutRequestBody(map,"clients/"+clientId);
		if(deleteClientResponse.getStatusCode()==204)
			System.out.println("---->The newly created client is deleted successfully.");
		else
			System.out.println("---->The newly created client is not deleted");
	}
	
}
