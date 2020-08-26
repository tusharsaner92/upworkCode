package com.upwork.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class AppLibrary {

	public static final long GLOBALTIMEOUT = 40;
	private WebDriver driver; // Driver instance
	private WebDriver mailDriver; // Default Driver instance
	private Configuration config;
	public String baseUrl;
	public String mailUrl;
	public String siteName;
	public String browser;
	public String device;
	public boolean isExecutionOnMobile;
	public String currentTestName;
//	private AppLibrary appLibrary;

	public String getCurrentTestName() {
		return currentTestName;
	}

	public void setCurrentTestName(String currentTestName) {
		this.currentTestName = currentTestName;
	}

	private String currentSessionID;

	// This is used for parameterized tests
	public AppLibrary(String testName) {
		this.currentTestName = testName;
	}

	public AppLibrary() {
		// do nothign
	}

	/**
	 * Creates an Webdriver Instance
	 *
	 * @param browser -Type of the browser to be used for test execution like "IE",
	 *                firefox, Chrome etc
	 * @throws Exception
	 */
	public WebDriver getDriverInstance() throws Exception {
		DesiredCapabilities caps = new DesiredCapabilities();

		String browser = (System.getProperty("browser") != null
				&& !(System.getProperty("browser").equals("${browser}"))) ? (System.getProperty("browser"))
						: (getConfiguration().getBrowserName());
		this.browser = browser;

		baseUrl = (System.getProperty("instanceUrl") != null
				&& !(System.getProperty("instanceUrl").equals("${instanceUrl}"))) ? System.getProperty("instanceUrl")
						: getConfiguration().getURL();

//		mailUrl = (System.getProperty("mailUrl") != null && !(System.getProperty("mailUrl").equals("${mailUrl}")))
//				? System.getProperty("mailUrl")
//				: getConfiguration().getmailURL();

		boolean isBrowserStackExecution = (System.getProperty("isBrowserstackExecution") != null
				&& !(System.getProperty("isBrowserstackExecution").equals("${isBrowserstackExecution}")))
						? (System.getProperty("isBrowserstackExecution").equals("true"))
						: getConfiguration().isBrowserStackExecution();
		System.out.println("Is Browser Stack execution: " + System.getProperty("isBrowserstackExecution") + " : "
				+ isBrowserStackExecution);

		boolean isRemoteExecution = (System.getProperty("isRemoteExecution") != null
				&& !(System.getProperty("isRemoteExecution").equals("${isRemoteExecution}")))
						? (System.getProperty("isRemoteExecution").equals("true"))
						: getConfiguration().isRemoteExecution();
		System.out
				.println("Is Remote execution: " + System.getProperty("isRemoteExecution") + " : " + isRemoteExecution);

		if (isBrowserStackExecution) {
			String browserVersion, os, osVersion, platform, device, browserStackUserName = "", browserStackAuthKey = "",
					isEmulator = "false";

			if (System.getProperty("isJenkinsJob") != null && System.getProperty("isJenkinsJob").equals("true")) {
				// isBrowserStackExecution=((System.getProperty("isBrowserstackExecution")!=null)&&(System.getProperty("isBrowserstackExecution").equals("true")));
				Reporter.log("starting from is jenkins job", true);
				baseUrl = System.getProperty("instanceUrl");
				Reporter.log(baseUrl + "", true);

				browserVersion = System.getProperty("browserVersion");
				Reporter.log(browserVersion + "", true);

				os = System.getProperty("os");
				;
				Reporter.log(os + "", true);

				osVersion = System.getProperty("osVersion");
				Reporter.log(osVersion + "", true);

				platform = System.getProperty("platform");
				Reporter.log(platform + "", true);

				device = System.getProperty("device");
				Reporter.log(device + "", true);

				browser = System.getProperty("browser");
				Reporter.log(browser + "", true);

				browserStackUserName = System.getProperty("broswerStackUserName").trim();
				Reporter.log(browserStackUserName + "", true);

				browserStackAuthKey = System.getProperty("broswerStackAuthKey").trim();
				Reporter.log(browserStackAuthKey + "", true);

				isEmulator = System.getProperty("isEmulator").trim();
				Reporter.log(isEmulator + "", true);

				Reporter.log("stopping from is jenkins job", true);
			} else {

				browserVersion = (System.getProperty("browserVersion") != null
						&& !(System.getProperty("browserVersion").equals("${browserVersion}")))
								? (System.getProperty("browserVersion"))
								: getConfiguration().getBrowserStackBrowserVersion();
				os = getConfiguration().getBrowserStackOS();
				osVersion = getConfiguration().getBrowserStackOSVersion();
				platform = getConfiguration().getBrowserStackPlatform();
				device = getConfiguration().getBrowserStackDevice();
				isEmulator = getConfiguration().getBrowserStackIsEmulator();
			}

			if (browser.equalsIgnoreCase("IE")) {
				caps.setCapability("browser", "IE");
			} else if (browser.equalsIgnoreCase("GCH") || browser.equalsIgnoreCase("chrome")) {
				caps.setCapability("browser", "Chrome");
			} else if (browser.equalsIgnoreCase("safari")) {
				caps.setCapability("browser", "Safari");
			} else if (browser.equalsIgnoreCase("android") || browser.equalsIgnoreCase("iphone")
					|| browser.equalsIgnoreCase("ipad")) {
				caps.setCapability("browserName", browser);
				caps.setCapability("platform", platform);
				caps.setCapability("device", device);
				if (isEmulator.equals("true")) {
					caps.setCapability("emulator", isEmulator);
				}
				caps.setCapability("autoAcceptAlerts", "true");
			} else {
				caps.setCapability("browser", "Firefox");
			}
			if (!(browser.equalsIgnoreCase("android"))) {
				if (browserVersion != null && !browserVersion.equals("") && !browserVersion.equals("latest")) {
					caps.setCapability("browser_version", browserVersion);
				}
			}
			if (osVersion != null && !(browser.equalsIgnoreCase("android"))) {
				caps.setCapability("os", os);
				if (os.toLowerCase().startsWith("win")) {
					caps.setCapability("os", "Windows");
				} else if (os.toLowerCase().startsWith("mac-") || os.toLowerCase().startsWith("os x-")) {
					caps.setCapability("os", "OS X");
				}

				if (os.equalsIgnoreCase("win7")) {
					osVersion = "7";
				} else if (os.equalsIgnoreCase("win8")) {
					osVersion = "8";
				} else if (os.equalsIgnoreCase("win8.1") || os.equalsIgnoreCase("win8_1")) {
					osVersion = "8.1";
				} else if (os.toLowerCase().startsWith("mac-") || os.toLowerCase().startsWith("os x-")) {
					osVersion = os.split("-")[1];
				}
				System.out.println("OS Version:" + osVersion);
				caps.setCapability("os_version", osVersion);
			}
			caps.setCapability("resolution", "1920x1080");
			caps.setCapability("browserstack.debug", "true");

			System.out.println("AppLibrary Build: " + System.getProperty("Build"));
			System.out.println("AppLibrary Project: " + System.getProperty("Suite"));
			System.out.println("AppLibrary Name: " + currentTestName);
			caps.setCapability("build", System.getProperty("Build"));
			caps.setCapability("project", System.getProperty("Suite"));
			caps.setCapability("name", currentTestName);

			try {
				driver = new RemoteWebDriver(new URL("http://"
						+ (browserStackUserName.equals("") ? getConfiguration().getBrowserStackUserName()
								: browserStackUserName)
						+ ":" + (browserStackAuthKey.equals("") ? getConfiguration().getBrowserStackAuthKey()
								: browserStackAuthKey)
						+ "@hub.browserstack.com/wd/hub"), caps);
				((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			} catch (Exception e) {
				Reporter.log("Issue creating new driver instance due to following error: " + e.getMessage() + "\n"
						+ e.getStackTrace(), true);
				throw e;
			}

			currentSessionID = ((RemoteWebDriver) driver).getSessionId().toString();

		} else if (isRemoteExecution) {
			System.out.println("Remote execution set up");
			String remoteGridUrl = (System.getProperty("remoteGridUrl") != null
					&& !(System.getProperty("remoteGridUrl").equals("${remoteGridUrl}")))
							? (System.getProperty("remoteGridUrl"))
							: getConfiguration().getRemoteGridUrl();
			String os = (System.getProperty("os") != null && !(System.getProperty("os").equals("${os}")))
					? (System.getProperty("os"))
					: getConfiguration().getOS();
			String deviceName = (System.getProperty("deviceName") != null
					&& !(System.getProperty("deviceName").equals("${deviceName}"))) ? (System.getProperty("deviceName"))
							: getConfiguration().getDeviceName();
			String deviceVersion = (System.getProperty("deviceVersion") != null
					&& !(System.getProperty("deviceVersion").equals("${deviceVersion}")))
							? (System.getProperty("deviceVersion"))
							: getConfiguration().getDeviceVersion();
			String version = (System.getProperty("browserVersion") != null
					&& !(System.getProperty("browserVersion").equals("${browserVersion}")))
							? (System.getProperty("browserVersion"))
							: getConfiguration().getBrowserVersion();
			browser = (browser.equalsIgnoreCase("ie") || browser.equalsIgnoreCase("internet explorer")
					|| browser.equalsIgnoreCase("iexplore")) ? "internet explorer" : browser;
			caps.setBrowserName(browser.toLowerCase());

			if (os.equalsIgnoreCase("win7") || os.equalsIgnoreCase("vista")) {
				caps.setPlatform(Platform.VISTA);
			} else if (os.equalsIgnoreCase("win8")) {
				caps.setPlatform(Platform.WIN8);
			} else if (os.equalsIgnoreCase("win8.1") || os.equalsIgnoreCase("win8_1")) {
				caps.setPlatform(Platform.WIN8_1);
			} else if (os.equalsIgnoreCase("mac")) {
				caps.setPlatform(Platform.MAC);
			} else if (os.equalsIgnoreCase("android")) {
				caps.setCapability("platformName", "ANDROID");
				caps.setBrowserName(StringUtils.capitalize(browser.toLowerCase()));
				caps.setCapability("deviceName", deviceName);
				caps.setCapability("platformVersion", deviceVersion);
			} else if (os.equalsIgnoreCase("ios")) {
				caps.setCapability("platformName", "iOS");
				caps.setCapability("deviceName", deviceName);
				caps.setCapability("platformVersion", deviceVersion);
				if (browser.equalsIgnoreCase("safari")) {
					caps.setBrowserName("Safari");
				} else {
					caps.setCapability("app", "safari");
					caps.setBrowserName("iPhone");
				}
			} else {
				caps.setPlatform(Platform.ANY);
			}

			if (version != null && !(version.equalsIgnoreCase("") && !(version.equalsIgnoreCase("null")))) {
				// caps.setVersion(version);
			}
			System.out.println(caps.asMap());
			driver = new RemoteWebDriver(new URL(remoteGridUrl), caps);
			((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			System.out.println("Session ID: " + ((RemoteWebDriver) driver).getSessionId());
		} else {
			if (browser.equalsIgnoreCase("IE")) {
				String driverPath = getConfiguration().getIEDriverPath();
				if ((driverPath == null) || (driverPath.trim().length() == 0)) {
					driverPath = "IEDriverServer.exe";
				}
				System.setProperty("webdriver.ie.driver", driverPath);
				DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
				capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
						false);
				capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
				capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
				capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
				capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
				driver = new InternetExplorerDriver(capabilities);

			} else if (browser.equalsIgnoreCase("GCH") || browser.equalsIgnoreCase("chrome")) {
				String driverPath = getConfiguration().getChromeDriverPath();
				if ((driverPath == null) || (driverPath.trim().length() == 0)) {
					driverPath = "chromedriver.exe";
				}
				System.setProperty("webdriver.chrome.driver", driverPath);
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--test-type");
				options.addArguments("chrome.switches", "--disable-extensions");
				options.addArguments("start-maximized");
				driver = new ChromeDriver();

			} else if (browser.equalsIgnoreCase("safari")) {
				driver = new SafariDriver();
			}

			else {

				System.setProperty("webdriver.gecko.driver", "default");
				driver = new FirefoxDriver();
			}

		}

		driver.manage().timeouts().implicitlyWait(GLOBALTIMEOUT, TimeUnit.SECONDS);
		// isExecutionOnMobile = this.browser.equalsIgnoreCase("iPhone") ||
		// this.browser.equalsIgnoreCase("android");
		// if (!isExecutionOnMobile) {
		driver.manage().window().maximize();
		// }
		return driver;

	}

	public WebDriver launchDefaultDriverInstance() throws Exception {
		DesiredCapabilities caps = new DesiredCapabilities();
		System.out.println("AppLibrary Build: " + System.getProperty("Build"));
		System.out.println("AppLibrary Project: " + System.getProperty("Suite"));
		System.out.println("AppLibrary Name: " + currentTestName);
		caps.setCapability("build", System.getProperty("Build"));
		caps.setCapability("project", System.getProperty("Suite"));
		caps.setCapability("name", currentTestName);

		String browser = "chrome";
		// this.browser = browser;

		boolean isBrowserStackExecution = (System.getProperty("isBrowserstackExecution") != null
				&& !(System.getProperty("isBrowserstackExecution").equals("${isBrowserstackExecution}")))
						? (System.getProperty("isBrowserstackExecution").equals("true"))
						: getConfiguration().isBrowserStackExecution();
		System.out.println("Is Browser Stack execution: " + System.getProperty("isBrowserstackExecution") + " : "
				+ isBrowserStackExecution);
		boolean isRemoteExecution = getConfiguration().isRemoteExecution();
		if (isBrowserStackExecution) {
			String browserVersion, os, osVersion, browserStackUserName = "", browserStackAuthKey = "";
			// isEmulator = "false", platform, device;
			os = "Windows";
			Reporter.log(os + "", true);

			osVersion = "7";
			Reporter.log(osVersion + "", true);
			if (System.getProperty("isJenkinsJob") != null && System.getProperty("isJenkinsJob").equals("true")) {
				// isBrowserStackExecution=((System.getProperty("isBrowserstackExecution")!=null)&&(System.getProperty("isBrowserstackExecution").equals("true")));
				Reporter.log("starting from is jenkins job", true);
				baseUrl = System.getProperty("instanceUrl");
				Reporter.log(baseUrl + "", true);

				browserVersion = System.getProperty("browserVersion");
				Reporter.log(browserVersion + "", true);

				os = "Windows";
				Reporter.log(os + "", true);

				osVersion = "7";
				Reporter.log(osVersion + "", true);

				browserStackUserName = System.getProperty("broswerStackUserName").trim();
				Reporter.log(browserStackUserName + "", true);

				browserStackAuthKey = System.getProperty("broswerStackAuthKey").trim();
				Reporter.log(browserStackAuthKey + "", true);

				Reporter.log("stopping from is jenkins job", true);
			} else {

				browserVersion = (System.getProperty("browserVersion") != null
						&& !(System.getProperty("browserVersion").equals("${browserVersion}")))
								? (System.getProperty("browserVersion"))
								: getConfiguration().getBrowserStackBrowserVersion();

			}

			if (!(browser.equalsIgnoreCase("android"))) {
				// caps.setCapability("browser_version", browserVersion);
			}
			if (osVersion != null && !(browser.equalsIgnoreCase("android"))) {

				caps.setCapability("os", os);
				caps.setCapability("os_version", osVersion);
			}
			caps.setCapability("resolution", "1920x1080");
			caps.setCapability("browserstack.debug", "true");

			try {
				mailDriver = new RemoteWebDriver(new URL("http://"
						+ (browserStackUserName.equals("") ? getConfiguration().getBrowserStackUserName()
								: browserStackUserName)
						+ ":" + (browserStackAuthKey.equals("") ? getConfiguration().getBrowserStackAuthKey()
								: browserStackAuthKey)
						+ "@hub.browserstack.com/wd/hub"), caps);

				((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());

			} catch (Exception e) {
				Reporter.log("Issue creating new driver instance due to following error: " + e.getMessage() + "\n"
						+ e.getStackTrace(), true);
				throw e;
			}
			currentSessionID = ((RemoteWebDriver) driver).getSessionId().toString();
		} else if (isRemoteExecution) {

		} else {
			String driverPath = getConfiguration().getChromeDriverPath();
			if ((driverPath == null) || (driverPath.trim().length() == 0)) {
				driverPath = "chromedriver.exe";
			}
			System.setProperty("webdriver.chrome.driver", driverPath);
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--test-type");
			options.addArguments("chrome.switches", "--disable-extensions");
			options.addArguments("start-maximized");
			mailDriver = new ChromeDriver(options);
		}

		mailDriver.manage().timeouts().implicitlyWait(GLOBALTIMEOUT, TimeUnit.SECONDS);
		// driver.manage().window().maximize();
		return mailDriver;

	}

	/**
	 * Launch the Application with the specified url
	 *
	 * @param url -URL of the product to be launched
	 */
	public void launchApp() {

		// Delete cookies and Launch the Application
		driver.manage().deleteAllCookies();
		driver.get(getBaseUrl());

		// Maximize the browser
		if (!(this.browser.equalsIgnoreCase("iPhone") || this.browser.equalsIgnoreCase("android"))) {
			System.out.println("Maximized Browser");
			driver.manage().window().maximize();
		}

	}

	public String getBaseUrl() {
		String baseUrl = getConfiguration().getURL();
		return baseUrl;
	}

	/**
	 * Returns the array of InputData by parsing an excel at the first sheet
	 *
	 * @throws BiffException
	 * @throws IOException
	 */
	public static String[][] readExcel(String excelFilePath) throws BiffException, IOException {
		String[][] data = readExcel(excelFilePath, 0);
		return data;
	}

	/**
	 * Returns the array of InputData by parsing an excel at a given sheetNo(index
	 * starts from 0)
	 *
	 * @throws BiffException
	 * @throws IOException
	 */
	public static String[][] readExcel(String excelFilePath, int sheetNo) throws BiffException, IOException {
		File file = new File(excelFilePath);
		String inputData[][] = null;
		Workbook w;

		try {
			w = Workbook.getWorkbook(file);

			// Get the first sheet
			Sheet sheet = w.getSheet(sheetNo);

			int colcount = sheet.getColumns();

			int rowcount = sheet.getRows();
			int countYes = 0;

			for (int i = 0; i < rowcount; i++) {
				if (sheet.getCell(colcount - 1, i).getContents().equalsIgnoreCase("Yes")) {
					countYes = countYes + 1;

				}
			}
			inputData = new String[countYes][colcount];
			int k = 0;
			for (int i = 0; i < rowcount; i++) {
				if (sheet.getCell(colcount - 1, i).getContents().equalsIgnoreCase("Yes")) {

					for (int j = 0; j < colcount; j++) {
						Cell cell = sheet.getCell(j, i);
						inputData[k][j] = cell.getContents().trim();

					}
					k = k + 1;
				}

			}

		} catch (BiffException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return inputData;
	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive. The difference
	 * between min and max can be at most
	 */
	public static int randInt() {
		int min = 1;
		int max = 999;
		Random rand = new Random();
		int randomNum = (rand.nextInt((max - min) + 1) + rand.nextInt((max - min) + 1)) / 2;
		return randomNum;
	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive. The difference
	 * between min and max can be at most
	 */
	public static int randIntDigits(int min, int max) {
		Random rand = new Random();
		int randomNum = (rand.nextInt((max - min) + 1) + rand.nextInt((max - min) + 1)) / 2;
		return randomNum;
	}

	/**
	 * Get Driver Instance
	 */
	public WebDriver getCurrentDriverInstance() {
		return driver;
	}

	/**
	 * Closes the Browser
	 */
	public void closeBrowser() {
		if (driver != null)
//			driver.close();
		driver.quit();
	}

	/**
	 * Tests if there are any broken links on currentPage
	 *
	 * @throws InterruptedException
	 */
	public void testBrokenLinks() throws InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) (driver);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		List<WebElement> allLinks = driver.findElements(By.xpath("//a"));

		int totalLinks = 0, failedLinks = 0;

		for (WebElement link : allLinks) {
			String src = link.getAttribute("href");
			// System.out.println(src);

			if (null != src && src.trim().startsWith("javascript")) {
				continue;
			}
			if (null != src && (!(src.startsWith("http://") || src.startsWith("https://")))) {
				src = baseUrl + src;

			}
			// System.out.println("Source:"+src);
			if (null != src) {
				totalLinks++;
				Thread.sleep(200);
				// String linkName=link.getText();
				String linkName = ((String) jse.executeScript("var e=arguments[0];return e.innerText;", link))
						.replaceAll("\n", "").trim();
				if (linkName.trim().equals(""))
					Thread.sleep(200);
				{
					linkName = ((String) jse.executeScript("var e=arguments[0];return e.innerHTML;", link))
							.replaceAll("\n", "").trim();
				}
				URL url;
				HttpURLConnection conn;
				int response = 0;
				try {
					url = new URL(src);
					conn = (HttpURLConnection) url.openConnection();
					response = conn.getResponseCode();
					if (response != HttpURLConnection.HTTP_OK) {
						System.out.println("Link: " + linkName + "is broken");
						System.out.println("Broken URL: " + src);
						System.out.println("Resonse is: " + response);
						failedLinks++;
					} else {
						System.out.println("Link: " + linkName + "is Working");
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					System.out.println("Link: " + linkName + "is broken");
					failedLinks++;
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Link: " + linkName + "is broken");
					failedLinks++;
				}

			}
		}

		System.out.println("Total Links Verified:" + totalLinks + "\n Failed Links Count :" + failedLinks);

	}

	/**
	 * Tests if there are any broken links on currentPage
	 */
	public void testBrokenLinks(String fileName) {
		JavascriptExecutor jse = (JavascriptExecutor) (driver);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		List<WebElement> allLinks = driver.findElements(By.xpath("//a"));

		int totalLinks = 0, failedLinks = 0;

		for (WebElement link : allLinks) {
			String src = link.getAttribute("href");
			// System.out.println(src);
			if (null != src && src.trim().startsWith("javascript")) {
				continue;
			}
			if (null != src && (!(src.startsWith("http://") || src.startsWith("https://")))) {
				src = baseUrl + src;
			}
			// System.out.println("Source:"+src);
			if (null != src) {
				totalLinks++;
				// String linkName=link.getText();
				String linkName = ((String) jse.executeScript("var e=arguments[0];return e.innerText;", link))
						.replaceAll("\n", "").trim();
				if (linkName.trim().equals("")) {
					linkName = ((String) jse.executeScript("var e=arguments[0];return e.innerHTML;", link))
							.replaceAll("\n", "").trim();
				}
				URL url;
				HttpURLConnection conn;
				int response = 0;
				try {
					url = new URL(src);
					conn = (HttpURLConnection) url.openConnection();
					response = conn.getResponseCode();
					if (response != HttpURLConnection.HTTP_OK) {
						System.out.println("Link: " + linkName + "is broken");
						System.out.println("Broken URL: " + src);
						System.out.println("Resonse is: " + response);
						failedLinks++;
					} else {
						System.out.println("Link: " + linkName + "is Working");
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					System.out.println("Link: " + linkName + "is broken");
					failedLinks++;
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Link: " + linkName + "is broken");
					failedLinks++;
				}

			}
		}

		System.out.println("Total Links Verified:" + totalLinks + "\n Failed Links Count :" + failedLinks);

		writeToExcel(fileName);
	}

	public void writeToExcel(String fileName) {
		try {
			File file = new File("BrokenLinks");
			if (!file.exists()) {
				if (file.mkdir()) {
					System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			}

			File exlFile = new File("BrokenLinks\\" + fileName + ".xls");
			WritableWorkbook writableWorkbook = Workbook.createWorkbook(exlFile);

			WritableSheet writableSheet = writableWorkbook.createSheet("Sheet1", 0);

			// Create Cells with contents of different data types.
			// Also specify the Cell coordinates in the constructor
			Label label = new Label(0, 0, "Label (String)");
			DateTime date = new DateTime(1, 0, new Date());
			jxl.write.Boolean bool = new jxl.write.Boolean(2, 0, true);
			jxl.write.Number num = new jxl.write.Number(3, 0, 9.99);

			// Add the created Cells to the sheet
			writableSheet.addCell(label);
			writableSheet.addCell(date);
			writableSheet.addCell(bool);
			writableSheet.addCell(num);

			// Write and close the workbook
			writableWorkbook.write();
			writableWorkbook.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	}

	public static WebElement findElement(WebDriver driver, String locatorString) {

		String string = locatorString;
		String[] parts = string.split(":");
		String type = parts[0]; // 004
		String object = parts[1];
		if (parts.length > 2) {
			for (int i = 2; i < parts.length; i++) {
				object = object + ":" + parts[i];
			}
		}
		Reporter.log("Finding element with logic: " + locatorString, true);
		System.out.println("Finding element with logic: " + locatorString);
		WebElement element = null;
		if (type.equals("id")) {
			element = driver.findElement(By.id(object));
		} else if (type.equals("name")) {
			element = driver.findElement(By.name(object));
		} else if (type.equals("class")) {
			element = driver.findElement(By.className(object));
		} else if (type.equals("link")) {
			;
			element = driver.findElement(By.linkText(object));
		} else if (type.equals("partiallink")) {
			;
			element = driver.findElement(By.partialLinkText(object));
		} else if (type.equals("css")) {
			element = driver.findElement(By.cssSelector(object));
		} else if (type.equals("xpath")) {
			element = driver.findElement(By.xpath(object));
		} else {
			throw new RuntimeException("Please provide correct element locating strategy");
		}

		return element;
	}

	public static By getLocatorType(String locatorString) {
		String string = locatorString;
		String[] parts = string.split(":");
		String type = parts[0]; // 004
		String object = parts[1];
		if (parts.length > 2) {
			for (int i = 2; i < parts.length; i++) {
				object = object + ":" + parts[i];
			}
		}

		if (type.equals("id")) {
			return By.id(object);
		} else if (type.equals("name")) {
			return By.name(object);
		} else if (type.equals("class")) {
			return By.className(object);
		} else if (type.equals("link")) {
			return By.linkText(object);
		} else if (type.equals("partiallink")) {
			return By.partialLinkText(object);
		} else if (type.equals("css")) {
			return By.cssSelector(object);
		} else if (type.equals("xpath")) {
			return By.xpath(object);
		} else {
			throw new RuntimeException("Please provide correct element locating strategy");
		}
	}

	public static WebElement findElement(WebDriver driver, String locatorString, boolean isOptional) {
		try {
			System.out.println(locatorString);
			return findElement(driver, locatorString);
		} catch (NoSuchElementException nse) {
			if (isOptional) {
				return null;
			} else {
				throw new RuntimeException("Element " + locatorString + " not found");
			}
		}
	}

	/**
	 * Click a dom element via JavaScript executed by the JavaScript engine that's
	 * built in to Selenium.
	 * <p/>
	 * This is useful if you get an error like
	 * "org.openqa.selenium.ElementNotVisibleException" when trying to use
	 * WebElement.click() in a test.
	 * <p/>
	 * Reference: "selenium webdriver:
	 * org.openqa.selenium.ElementNotVisibleException: Element is not currently
	 * visible and so may not be interacted with - Stack Overflow" http
	 * ://stackoverflow.com/questions/12082946/selenium-webdriver-org-openqa-
	 * selenium-elementnotvisibleexception-element-is-n
	 * <p/>
	 *
	 * @param driver        The WebDriver instance being used in the test
	 * @param objectLocator The objectLocator string of the element (like
	 *                      "id:<domId>")
	 * @return the dom element that was found
	 * @throws Exception If an error occurs executing this code (a timeout, etc)
	 */
	public static WebElement clickByJavascript(WebDriver driver, String objectLocator) throws Exception {
		String[] parts = objectLocator.split(":");
		if (parts.length < 2) {
			throw new RuntimeException("No type is specified in object locator: " + objectLocator);
		}
		String type = parts[0];
		String object = parts[1];

		WebElement element;
		try {
			if (type.equals("id")) {
				element = driver.findElement(By.id(object));
			} else if (type.equals("name")) {
				element = driver.findElement(By.name(object));
			} else if (type.equals("class")) {
				element = driver.findElement(By.className(object));
			} else if (type.equals("link")) {
				;
				element = driver.findElement(By.linkText(object));
			} else if (type.equals("partiallink")) {
				;
				element = driver.findElement(By.partialLinkText(object));
			} else if (type.equals("css")) {
				element = driver.findElement(By.cssSelector(object));
			} else if (type.equals("xpath")) {
				element = driver.findElement(By.xpath(object));
			} else {
				throw new RuntimeException("Please provide correct element locating strategy");
			}

			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		} catch (Exception e) {
			System.out.println(objectLocator + ": exception occurred: " + e.getClass().toString());
			throw e;
		}
		return element;
	}

	public Configuration getConfiguration() {
		if (config == null) {
			config = new Configuration();
		}
		return config;
	}

	public static void selectElement(WebElement element, String option) {
		Select select = new Select(element);
		select.selectByVisibleText(option);
		sleep(2000);
	}

	public static void selectByPartOfVisibleText(WebElement element, String value) {
		boolean flag = true;
		List<WebElement> optionElements = element.findElements(By.tagName("option"));
		Select select = new Select(element);
		for (WebElement optionElement : optionElements) {
			if (optionElement.getText().contains(value)) {
				String optionValue = optionElement.getAttribute("value");
				select.selectByValue(optionValue);
				flag = false;
				break;
			}
		}

		if (flag) {
			Assert.assertTrue(false, "Option " + value + " was not found in the select");
		}
	}

	public static void selectByPartOfVisibleTextAtEnd(WebElement element, String value) {
		boolean flag = true;
		List<WebElement> optionElements = element.findElements(By.tagName("option"));
		Select select = new Select(element);
		for (WebElement optionElement : optionElements) {
			if (optionElement.getText().endsWith(value)) {
				String optionIndex = optionElement.getAttribute("index");
				select.selectByIndex(Integer.parseInt(optionIndex));
				flag = false;
				break;
			}
		}

		if (flag) {
			Assert.assertTrue(false, "Option " + value + " was not found in the select");
		}
	}

	public static boolean verifyElement(WebDriver driver, String locatorString) {
		return verifyElement(driver, locatorString, false);
	}

	public static boolean verifyElement(WebDriver driver, String locatorString, boolean checkVisibility) {
		boolean isDisplayed = true;
		try {
			if (checkVisibility) {
				isDisplayed = (findElement(driver, locatorString).isDisplayed());
			} else {
				findElement(driver, locatorString);
			}
		} catch (NoSuchElementException nsee) {
			Assert.assertTrue(false, "Element not found using locator: " + locatorString);
		}
		return isDisplayed;
	}

	public static boolean verifyElement(WebDriver driver, String locatorString, long timeOutInSeconds) {
		try {
			driver.manage().timeouts().implicitlyWait(timeOutInSeconds, TimeUnit.SECONDS);
			findElement(driver, locatorString);
		} catch (NoSuchElementException nsee) {
			driver.manage().timeouts().implicitlyWait(GLOBALTIMEOUT, TimeUnit.SECONDS);
			return false;
		}
		driver.manage().timeouts().implicitlyWait(GLOBALTIMEOUT, TimeUnit.SECONDS);
		return true;
	}

	public static void sleep(long milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static List<WebElement> findElements(WebDriver driver, String locatorString) {

		String string = locatorString;
		String[] parts = string.split(":");
		String type = parts[0]; // 004
		String object = parts[1];

		Reporter.log("Finding element with logic: " + locatorString, true);
		List<WebElement> element = null;
		if (type.equals("id")) {
			element = driver.findElements(By.id(object));
		} else if (type.equals("name")) {
			element = driver.findElements(By.name(object));
		} else if (type.equals("class")) {
			element = driver.findElements(By.className(object));
		} else if (type.equals("link")) {
			;
			element = driver.findElements(By.linkText(object));
		} else if (type.equals("partiallink")) {
			;
			element = driver.findElements(By.partialLinkText(object));
		} else if (type.equals("css")) {
			element = driver.findElements(By.cssSelector(object));
		} else if (type.equals("xpath")) {
			element = driver.findElements(By.xpath(object));
		} else {
			throw new RuntimeException("Please provide correct element locating strategy");
		}
		return element;
	}

	public static By getBy(WebDriver driver, String locatorString) {

		String string = locatorString;
		String[] parts = string.split(":");
		String type = parts[0];
		String object = parts[1];

		if (type.equals("id")) {
			return By.id(object);
		} else if (type.equals("name")) {
			return By.name(object);
		} else if (type.equals("class")) {
			return By.className(object);
		} else if (type.equals("link")) {
			return By.linkText(object);
		} else if (type.equals("partiallink")) {
			return By.partialLinkText(object);
		} else if (type.equals("css")) {
			return By.cssSelector(object);
		} else if (type.equals("xpath")) {
			return By.xpath(object);
		} else {
			throw new RuntimeException("Please provide correct element locating strategy");
		}
	}

	public void switchToWindow(int windowNo) {
		Set<String> set = driver.getWindowHandles();
		String windowHandle = null;
		Reporter.log("Current no. of windows are: " + set.size(), true);
		if (windowNo <= set.size()) {
			ArrayList<String> windows = new ArrayList<String>(set);
			windowHandle = windows.get(windowNo - 1);
		}

		if (windowHandle != null) {
			driver.switchTo().window(windowHandle);
		} else {
			throw new RuntimeException("Specified window not available");
		}
	}

	public void mouseOver(WebElement elmt) {
		driver.switchTo().defaultContent();
		String script = "var elem=arguments[0];" + "var evt = elem.ownerDocument.createEvent('MouseEvents');"
				+ "evt.initMouseEvent('mouseover',true,true,elem.ownerDocument.defaultView,0,0,0,0,0,false,false,false,false,0,null);"
				+ "elem.dispatchEvent(evt);";
		Reporter.log(this.getClass().getName() + "::mouseOver::" + script, true);
		((JavascriptExecutor) driver).executeScript(script, elmt);

	}

	public String getCurrentSessionID() {
		return currentSessionID;
	}

	public static void waitForElementClickable(WebDriver driver, WebElement element) {
		new WebDriverWait(driver, 20).until(ExpectedConditions.elementToBeClickable(element));
	}

	public void waitForElementVisible(WebElement element) {
		new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOf(element));
	}

	public static void selectCheckBox(WebDriver driver, String locator) {
		if (!AppLibrary.findElement(driver, locator).isSelected()) {
			AppLibrary.clickElement(driver, locator);
		}
	}

	public static void deselectCheckBox(WebDriver driver, String locator) {
		if (AppLibrary.findElement(driver, locator).isSelected()) {
			AppLibrary.clickElement(driver, locator);
		}
	}

	public static void enterText(WebDriver driver, String locator, String text) {

		int i = 0;

		do {
			try {

				if (text.equalsIgnoreCase("tab")) {
					driver.findElement(getBy(driver, locator)).click();
					driver.findElement(getBy(driver, locator)).sendKeys(Keys.TAB);
				} else if (!text.equalsIgnoreCase("")) {
					driver.findElement(getBy(driver, locator)).click();
					driver.findElement(getBy(driver, locator)).clear();
					driver.findElement(getBy(driver, locator)).sendKeys(text);
				}

				break;

			} catch (Exception e) {
				AppLibrary.sleep(1000);
				i++;
				continue;
			}

		} while (i < 5);
	}

	public static void enterTextForValidation(WebDriver driver, String locator, String text) {
		if (text.equalsIgnoreCase("Tab")) {
			driver.findElement(getBy(driver, locator)).sendKeys(Keys.TAB);
		}

		else if (!text.equalsIgnoreCase("")) {
			driver.findElement(getBy(driver, locator)).sendKeys(text);
		}

	}

	public static void clickElement(WebDriver driver, String locator) {

		int i = 0;
		do {
			try {
				driver.findElement(getBy(driver, locator)).click();
				break;
			} catch (Exception e) {
				AppLibrary.sleep(1000);
				i++;
				continue;
			}

		} while (i < 5);

		if (i >= 5) {
			throw new ElementNotFoundException(locator, "ElementVal", "Not Found");
		}
	}

	public static boolean verifyCheckBox(WebDriver driver, String locator) {
		return AppLibrary.findElement(driver, locator).isSelected();
	}

	public void uploadImage(WebDriver driver, String locator, String imageName, String verificationLocator,
			String Attribute) {

		String finalPath = "";
		File fp = new File(imageName);
		String imagePath = fp.getAbsolutePath();
		finalPath = imagePath.replace(imageName, "Resources" + File.separator + imageName);
		System.out.println(finalPath);

		AppLibrary.findElement(driver, locator).sendKeys(finalPath);
		AppLibrary.sleep(5000);

		verifyUploadedImagePath(verificationLocator, imageName, Attribute);
	}

	public boolean verifyUploadedImagePath(String locator, String imageName, String attribute) {

		String[] temp = imageName.split("\\.");
		int counter;
		for (counter = 5; counter > 0; counter--) {
			AppLibrary.sleep(5000);
			try {
				if (driver.findElement(By.xpath(locator.replace("xpath:", ""))).getAttribute(attribute)
						.contains(temp[0])) {
					return true;
				}
			} catch (Exception e) {
				continue;
			}
		}

		Assert.assertTrue(false, "Failed to upload image " + imageName + " in " + locator);
		return false;
	}

	public static String getFDate() {
		return getDate().replaceAll("/", "_").replaceAll(":", "_").replaceAll(" ", "_");
	}

	public static String getDate() {

		Date date = new Date();

		// Calendar c = Calendar.getInstance();
		// c.setTime(date);
		// c.add(Calendar.DATE, value);
		// date = c.getTime();
		DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
		System.out.println(dateFormat.format(date));

		return dateFormat.format(date);
	}

	public static void waitUntilElementDisplayed(WebDriver driver, String locatorString) {

		WebDriverWait wait = new WebDriverWait(driver, 40);
		String string = locatorString;
		String[] parts = string.split(":");
		String type = parts[0];
		String object = parts[1];

		Reporter.log("Finding element with logic: " + locatorString, true);
		if (type.equals("id")) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(object)));
		} else if (type.equals("name")) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(object)));
		} else if (type.equals("class")) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(object)));
		} else if (type.equals("link")) {
			;
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(object)));
		} else if (type.equals("partiallink")) {
			;
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(object)));
		} else if (type.equals("css")) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(object)));
		} else if (type.equals("xpath")) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(object)));
		} else {
			throw new RuntimeException("Please provide correct element locating strategy");
		}
	}

	public static void verifyAbsentWithTimeOut(WebDriver driver, String locatorString, long timeOutInSeconds) {

		String string = locatorString;
		String[] parts = string.split(":");
		String type = parts[0]; // 004
		String object = parts[1];

		driver.manage().timeouts().implicitlyWait(timeOutInSeconds, TimeUnit.SECONDS);
		WebElement element = null;
		try {
			Reporter.log("Finding element with logic: " + locatorString, true);
			if (type.equals("id")) {
				element = driver.findElement(By.id(object));
			} else if (type.equals("name")) {
				element = driver.findElement(By.name(object));
			} else if (type.equals("class")) {
				element = driver.findElement(By.className(object));
			} else if (type.equals("link")) {
				element = driver.findElement(By.linkText(object));
			} else if (type.equals("partiallink")) {
				element = driver.findElement(By.partialLinkText(object));
			} else if (type.equals("css")) {
				element = driver.findElement(By.cssSelector(object));
			} else if (type.equals("xpath")) {
				element = driver.findElement(By.xpath(object));
			}

			org.testng.Assert.assertTrue(false,
					"Expected element to be absent, but it was found on the page. Text = " + element.getText());

		} catch (Exception e) {
			// It's good if not found
		} finally {
			driver.manage().timeouts().implicitlyWait(AppLibrary.GLOBALTIMEOUT, TimeUnit.SECONDS);
		}

	}

	public static void verifyAbsent(WebDriver driver, String locatorString) {

		String string = locatorString;
		String[] parts = string.split(":");
		String type = parts[0]; // 004
		String object = parts[1];

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		WebElement element = null;
		try {
			Reporter.log("Finding element with logic: " + locatorString, true);
			if (type.equals("id")) {
				element = driver.findElement(By.id(object));
			} else if (type.equals("name")) {
				element = driver.findElement(By.name(object));
			} else if (type.equals("class")) {
				element = driver.findElement(By.className(object));
			} else if (type.equals("link")) {
				element = driver.findElement(By.linkText(object));
			} else if (type.equals("partiallink")) {
				element = driver.findElement(By.partialLinkText(object));
			} else if (type.equals("css")) {
				element = driver.findElement(By.cssSelector(object));
			} else if (type.equals("xpath")) {
				element = driver.findElement(By.xpath(object));
			}

			org.testng.Assert.assertTrue(false,
					"Expected element to be absent, but it was found on the page. Text = " + element.getText());

		} catch (Exception e) {
			// It's good if not found
		} finally {
			driver.manage().timeouts().implicitlyWait(AppLibrary.GLOBALTIMEOUT, TimeUnit.SECONDS);
		}

	}

	public static void waitForNavigation(WebDriver driver, String url) {

		int counter = 10;
		for (; counter > 0; counter--) {
			if (driver.getCurrentUrl().contains(url)) {
				break;
			} else {
				AppLibrary.sleep(10000);
			}
		}
	}

	public static void syncAndClick(WebDriver driver, String locator) {
		try {
			AppLibrary.waitForElementClickable(driver, AppLibrary.findElement(driver, locator, true));
			AppLibrary.findElement(driver, locator, true).click();
		} catch (Exception e) {
			AppLibrary.sleep(5000);
			AppLibrary.findElement(driver, locator, true).click();
		}
	}

	public static void switchToWindow(WebDriver driver, int windowNo) {
		Set<String> set = driver.getWindowHandles();
		String windowHandle = null;
		Reporter.log("Current no. of windows are: " + set.size(), true);
		if (windowNo <= set.size()) {
			ArrayList<String> windows = new ArrayList<String>(set);
			windowHandle = windows.get(windowNo - 1);
		}

		if (windowHandle != null) {
			driver.switchTo().window(windowHandle);
		} else {
			throw new RuntimeException("Specified window not available");
		}
	}

	public String getFileData(String defaultEditorVal) {
		byte[] data = null;
		String str = "";
		FileInputStream fis = null;

		File file = new File("resources" + File.separator + defaultEditorVal);

		try {
			fis = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fis.read(data);
			str = new String(data, "UTF-8");
			return str;

		} catch (FileNotFoundException e) {
			Assert.assertTrue(false, "File not found " + defaultEditorVal);
		} catch (IOException e) {
			Assert.assertTrue(false, "IO exception when reading file " + defaultEditorVal);
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				Assert.assertTrue(false, "IO exception when closing file " + defaultEditorVal);
			}
		}

		return str;

	}

	public void ClickElement(WebElement element) {
		try {
			AppLibrary.sleep(1000);
			element.click();
		} catch (Exception e) {
			AppLibrary.sleep(3000);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].scrollIntoView()", element);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		}
	}

	public static void selectDropdown(WebDriver driver, String parent, String child) {

		AppLibrary.clickElement(driver, parent);
		AppLibrary.sleep(2000);
		AppLibrary.clickElement(driver, child);
	}

	public static void verification(WebDriver driver, String locator, String expectedValue) {

		String actualValue = AppLibrary.findElement(driver, locator).getText();
		Assert.assertEquals(actualValue.replace(",", ""), expectedValue,
				"values didnot match for " + locator + "Expected =" + expectedValue + "  Actual =" + actualValue);
	}

	public static void verificationWithAttribute(WebDriver driver, String locator, String expectedValue) {

		String actualValue = AppLibrary.findElement(driver, locator).getAttribute("value");

		Assert.assertEquals(actualValue.replace(",", ""), expectedValue,
				"values didnot match for " + locator + "Expected =" + expectedValue + "  Actual =" + actualValue);
	}

	public static void verificationforcustomizePage(WebDriver driver, String locator, String expectedValue,
			BufferedWriter bw, FileWriter fw, String id, String identifier) {

		String actualValue = AppLibrary.findElement(driver, locator).getText().replace(",", "");

		if (actualValue.equals(expectedValue)) {
			Assert.assertEquals(actualValue.replace(",", ""), expectedValue, "values didnot match for " + identifier
					+ "Expected =" + expectedValue + "  Actual =" + actualValue);
		} else {

			String message = identifier + "\t\tExpected =" + expectedValue + "\t\tActual =" + actualValue;

			try {
				bw.write(message + "\t\t");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public String generateRandomString(int length) {
		return RandomStringUtils.randomAlphabetic(length);
	}

	public String generateRandomNumber(int length) {
		return RandomStringUtils.randomNumeric(length);
	}

	public String generateRandomAlphaNumeric(int length) {
		return RandomStringUtils.randomAlphanumeric(length);
	}

	public String generateStringWithAllowedSplChars(int length, String allowdSplChrs) {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + // alphabets
				"1234567890" + // numbers
				allowdSplChrs;
		return RandomStringUtils.random(length, allowedChars);
	}

	public String generateEmail(int length) {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + // alphabets
				"1234567890" + // numbers
				"_-."; // special characters
		String email = "";
		String temp = RandomStringUtils.random(length, allowedChars);
		email = temp.substring(0, temp.length() - 9) + "@test.org";
		return email;
	}

	public String generateUrl(int length) {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + // alphabets
				"1234567890" + // numbers
				"_-."; // special characters
		String url = "";
		String temp = RandomStringUtils.random(length, allowedChars);
		url = temp.substring(0, 3) + "." + temp.substring(4, temp.length() - 4) + "."
				+ temp.substring(temp.length() - 3);
		return url;
	}

	public static void mouseHover(WebDriver driver, String locator) {

		WebElement element = AppLibrary.findElement(driver, locator);
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();

	}

	public static void mouseRightClick(WebDriver driver, String locator) {

		Actions actions = new Actions(driver);
		WebElement elementLocator = AppLibrary.findElement(driver, locator);
///	actions.contextClick(elementLocator).perform();
		actions.contextClick(elementLocator).sendKeys(Keys.ARROW_UP).sendKeys(Keys.ENTER).build().perform();
	}

	public static boolean isElementPresent(WebDriver driver, String locator) {
		try {

			AppLibrary.findElement(driver, locator);
			return true;
		} catch (org.openqa.selenium.NoSuchElementException e) {
			return false;
		}
	}

	public static void openNewTabByJavaScript(WebDriver driver) {

		((JavascriptExecutor) driver).executeScript("window.open();");

	}

	public void getScreenshot(String name) throws IOException {
		driver = getCurrentDriverInstance();
		String path = "screenshots/" + name;
		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(src, new File(path));
		System.out.println("screenshot at :" + path);
		Reporter.log("screenshot for " + name + " available at :" + path, true);
	}

	public void checkAlertsForScreenshot(ITestResult result, String testName) {
		System.out.println("im doing nothign");
		String screenshotName = result.getName() + "_" + browser + "_" + AppLibrary.randInt() + ".png";

		if (result.getStatus() == ITestResult.FAILURE) {
			try {

				getScreenshot(screenshotName);
				Reporter.log("Failed at URL: " + getCurrentDriverInstance().getCurrentUrl(), true);
				int paramsLength = result.getParameters().length;
				Reporter.log("Screenshot Name : " + screenshotName, true);
				Reporter.log("ScreenShot for " + testName + " "
						+ ((paramsLength > 0) ? " with parameter " + result.getParameters()[1] : "") + " saved as "
						+ screenshotName + ".png", true);
				driver.navigate().refresh();

			} catch (Exception e) {
				Reporter.log("Failed fetching URL and screenshot due to error:" + e.getMessage(), true);
				e.printStackTrace();
			}

			if (getCurrentSessionID() != null) {
				Reporter.log("Session id for " + testName + " is " + getCurrentSessionID(), true);
				Reporter.log("Session details for " + testName
						+ " can be found at https://www.browserstack.com/automate/sessions/" + getCurrentSessionID()
						+ ".json", true);
			}
		}

	}

	public static void selectDropDown(WebDriver driver, String locator, String locator2, String Option) {

		AppLibrary.findElement(driver, locator).click();
		AppLibrary.findElement(driver, locator).sendKeys(Option);

		AppLibrary.sleep(3000);

		List<WebElement> all = AppLibrary.findElements(driver, locator2);
		for (WebElement element : all) {
			if (element.getText().contains(Option)) {
				element.click();
				break;
			}
		}

	}

	public static WebElement clearTextByJavascript(WebDriver driver, String objectLocator) throws Exception {
		String[] parts = objectLocator.split(":");
		if (parts.length < 2) {
			throw new RuntimeException("No type is specified in object locator: " + objectLocator);
		}
		String type = parts[0];
		String object = parts[1];

		WebElement element;
		try {
			if (type.equals("id")) {
				element = driver.findElement(By.id(object));
			} else if (type.equals("name")) {
				element = driver.findElement(By.name(object));
			} else if (type.equals("class")) {
				element = driver.findElement(By.className(object));
			} else if (type.equals("link")) {
				;
				element = driver.findElement(By.linkText(object));
			} else if (type.equals("partiallink")) {
				;
				element = driver.findElement(By.partialLinkText(object));
			} else if (type.equals("css")) {
				element = driver.findElement(By.cssSelector(object));
			} else if (type.equals("xpath")) {
				element = driver.findElement(By.xpath(object));
			} else {
				throw new RuntimeException("Please provide correct element locating strategy");
			}

			((JavascriptExecutor) driver).executeScript("arguments[0].value ='';", element);
		} catch (Exception e) {
			System.out.println(objectLocator + ": exception occurred: " + e.getClass().toString());
			throw e;
		}
		return element;
	}

	public static WebElement setAttributeByJavascript(WebDriver driver, String objectLocator, String value)
			throws Exception {
		String[] parts = objectLocator.split(":");
		if (parts.length < 2) {
			throw new RuntimeException("No type is specified in object locator: " + objectLocator);
		}
		String type = parts[0];
		String object = parts[1];

		WebElement element;
		try {
			if (type.equals("id")) {
				element = driver.findElement(By.id(object));
			} else if (type.equals("name")) {
				element = driver.findElement(By.name(object));
			} else if (type.equals("class")) {
				element = driver.findElement(By.className(object));
			} else if (type.equals("link")) {
				;
				element = driver.findElement(By.linkText(object));
			} else if (type.equals("partiallink")) {
				;
				element = driver.findElement(By.partialLinkText(object));
			} else if (type.equals("css")) {
				element = driver.findElement(By.cssSelector(object));
			} else if (type.equals("xpath")) {
				element = driver.findElement(By.xpath(object));
			} else {
				throw new RuntimeException("Please provide correct element locating strategy");
			}

			((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
					element, "value", value);
		} catch (Exception e) {
			System.out.println(objectLocator + ": exception occurred: " + e.getClass().toString());
			throw e;
		}
		return element;
	}

	public static boolean isAttribtuePresent(WebElement element, String attribute) {
		Boolean result = false;
		try {
			String value = element.getAttribute(attribute);
			if (value != null) {
				result = true;
			}
		} catch (Exception e) {
		}

		return result;
	}

	public void verifyListGridRow(String locator, String uniqueColumnValue, String tableRow) {

		String rowData = AppLibrary.findElement(driver, locator + "//tr[td[text()='" + uniqueColumnValue + "']]")
				.getText();
		System.out.println("Actual  : " + rowData);
		System.out.println("Expected: " + tableRow);
		Assert.assertTrue(rowData.contains(tableRow), "Expected: " + tableRow + "  Actual: " + rowData);
	}

	public void clickListGridEdit(String uniqueColumnValue) {
		AppLibrary.sleep(5000);
		AppLibrary.findElement(driver, "//tr[td[text()='" + uniqueColumnValue + "']]//a[contains(text(),'Edit')]")
				.click();
		AppLibrary.sleep(5000);
	}

}
