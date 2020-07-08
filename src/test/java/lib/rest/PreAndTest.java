package lib.rest;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lib.utils.DataInputProvider;
import lib.utils.HTMLReporter;

public class PreAndTest extends HTMLReporter{
	
	public String dataFileName, dataFileType, currentUrl, accessToken;	
	public HashMap<String, String> map = new HashMap<>();
	
	
	@BeforeSuite
	public void beforeSuite() {
		startReport();
	}
	
	@BeforeTest
	public void beforeTest() {
		
	}
	
	
	@BeforeClass
	public void beforeClass() throws InterruptedException, FileNotFoundException, IOException {
		startTestCase(testCaseName, testDescription);
		
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File("./src/test/resources/config.properties")));
		
		RestAssured.baseURI = "https://"+prop.getProperty("server")+"/"+prop.getProperty("resources")+"/";
		
		Response response = RestAssured
							.given()
							.log().all()
							.contentType(ContentType.URLENC)
							.redirects().follow(false)
							.queryParam("nonce", "George")
							.queryParam("response_type", "token")
							.queryParam("state", "george123")
							.queryParam("client_id", "0oajnj4d2XYmr7xoG4x6")
							.queryParam("scope", "okta.apps.manage")
							.queryParam("scope", "okta.clients.manage")
							.queryParam("scope", "okta.clients.read")
							.queryParam("scope", "okta.apps.read")
							.queryParam("scope", "okta.apps.manage")													
							.queryParam("redirect_uri", "https://www.getpostman.com/oauth2/callback")
							.queryParam("grant_type", "Implicit")
							.get("authorize");
		
			System.out.println("--->"+response.getStatusCode());
			System.out.println("--->"+response.header("location"));
			String referer = response.header("location");
			System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
			ChromeDriver driver = new ChromeDriver();
			driver.get(referer);
			driver.findElementById("okta-signin-username").sendKeys("georgeb4pc@gmail.com");
			driver.findElementById("okta-signin-password").sendKeys("Test@123");
			driver.findElementById("okta-signin-submit").click();
			Thread.sleep(10000);
			currentUrl = driver.getCurrentUrl();
			accessToken = currentUrl.substring(currentUrl.indexOf("access_token=")+13,currentUrl.indexOf("&token_type"));
			System.out.println("--->"+accessToken);
			driver.close();
			map.put("Authorization", "Bearer "+accessToken);
			
			if(testCaseName.equals("Client Operation Test Cases"))
				RestAssured.baseURI = "https://"+prop.getProperty("server")+"/"+prop.getProperty("resources")+"/";
			else
				RestAssured.baseURI = "https://"+prop.getProperty("server")+"/"+prop.getProperty("appresources")+"/";
	}
	
	
	@BeforeMethod
	public void beforeMethod() {
		//for reports		
		svcTest = startTestModule(nodes);
		svcTest.assignAuthor(authors);
		svcTest.assignCategory(category);
		
	}

	@AfterMethod
	public void afterMethod() {
		
	}
	
	@AfterClass
	public void afterClass() {
		
	}
	
	@AfterTest
	public void afterTest() {
		
	}

	@AfterSuite
	public void afterSuite() {
		endResult();
	}

	@DataProvider(name="fetchData")
	public  Object[][] getData(){
		if(dataFileType.equalsIgnoreCase("Excel"))
			return DataInputProvider.getSheet(dataFileName);	
		else if(dataFileType.equalsIgnoreCase("JSON")){
			Object[][] data = new Object[1][1];
			data[0][0] = new File("./data/"+dataFileName+"."+dataFileType);
			System.out.println(data[0][0]);
			return data;
		}else {
			return null;
		}
			
	}

	@Override
	public long takeSnap() {
		return 0;
	}	

	
	
}
