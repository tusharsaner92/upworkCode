package com.upwork.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.upwork.lib.AppLibrary;
import com.upwork.lib.TestBase;
import com.upwork.pages.SearchHotelPage;

public class SearchHotel extends TestBase {

	@Test
	public void testFindHotel() throws Exception {
		driver = appLibrary.getDriverInstance();
		appLibrary.launchApp();
		SearchHotelPage shp = new SearchHotelPage(appLibrary);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,1000)");

		shp.selectCity("Pune");
		shp.selectDate("2020", "September", "16");
		shp.selectDate("2020", "September", "17");
//		shp.numberOfGuest("1", "0", "1");

		AppLibrary.sleep(2000);
		AppLibrary.clickElement(driver, SearchHotelPage.searchButton);
		shp.priceRange(15000);

		AppLibrary.sleep(5000);
		int p = 0;
		Boolean flag = false;

		do {

			p = shp.excelData(p);

			if (AppLibrary.isElementPresent(driver, SearchHotelPage.nextPageButton)) {

				AppLibrary.clickElement(driver, SearchHotelPage.nextPageButton);
				AppLibrary.sleep(5000);
				flag = true;
			} else
				flag = false;

		} while (flag);

		AppLibrary.sleep(3000);
		
		shp.selectCity("Holiday Inn Express Pune Hinjewadi");

		shp.findDetails("Holiday Inn Express Pune Hinjewadi");

		WebElement hotelImage = AppLibrary.findElement(driver, SearchHotelPage.hotelImage);
		String logoSRC = hotelImage.getAttribute("src");
		URL imageURL = new URL(logoSRC);
		BufferedImage saveImage = ImageIO.read(imageURL);
		ImageIO.write(saveImage, "png", new File("Holiday Inn Express Pune Hinjewadi.png"));

	}

}
