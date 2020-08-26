package com.upwork.test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.upwork.lib.TestBase;
import com.upwork.pages.LandingPage;

public class upworkTest extends TestBase {

	public Logger logger;

	@BeforeClass
	public void setUp() throws IOException {
		logger = Logger.getLogger("upworkTest");
		System.out.println("upworkTest Started");
		PropertyConfigurator.configure("Log4j.properties");
		Reporter.log(
				"<h1><Center><Font face=\"arial\" color=\"Orange\">Log Summary</font></Center><h1><table border=\"1\" bgcolor=\"lightgray\">");
	}

	@Test
	public void testFindFreelancer() throws Exception {
		driver = appLibrary.getDriverInstance();
		appLibrary.launchApp();
		LandingPage lp = new LandingPage();
		lp.searchFreeLancers(driver, "QA");
	}

}
