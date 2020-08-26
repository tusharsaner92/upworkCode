package com.upwork.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.upwork.lib.AppLibrary;

public class SearchHotelPage {

	public static String cityNameField = "xpath://input[@id='querytext']";
	public static String cityList = "xpath://span[@class='ssg-title']";
	public static String checkInDate = "xpath://button[@class='dealform-button js-dealform-button-calendar dealform-button--checkin']";
	public static String checkOutDate = "xpath://button[@class='dealform-button js-dealform-button-calendar dealform-button--checkout']";
	public static String searchButton = "xpath://button[@class='btn btn--primary btn--regular search-button js-search-button']";

	public static String monthHeadin = "xpath://th[@id='cal-heading-month']";
	public static String preMonth = "xpath://span[@class='icon-ic cal-btn-ic icon-rtl icon-flip']";
	public static String nextMonth = "xpath://span[@class='icon-ic cal-btn-ic icon-rtl']";

	public static String todayDate = "xpath:(//tbody//time[contains(@class,'cal-is-selectable')]//span//span[text()='number'])[1]";
	public static String weekdays = "xpath://time[@class='cal-day cal-is-selectable']//span[@class='cal-day-price']";
	public static String weekends = "xpath://time[@class='cal-day cal-is-weekend cal-is-selectable']//span[@class='cal-day-price']";

	public static String selectStartDate = "xpath://time//span[contains(text(),'31')]";
	public static String selectEndDate = "xpath://time//span[contains(text(),'14')]";

	public static String addGuestButton = "xpath://button[@class='dealform-button dealform-button--guests js-dealform-button-guests']";
	public static String adultCount = "xpath://input[@id='adults-input']";
	public static String ChildrenCount = "xpath://input[@id='children-input']";
	public static String roomCount = "xpath://input[@id='rooms-input']";
	public static String applyButton = "xpath://button[@class='btn btn--primary btn--small btn--apply-config']";
	public static String resetButton = "xpath://button[@class='btn btn--small refinement-row__btn btn--tertiary']";
	public static String slider = "xpath://div[@class='fl-slider__range']";
	public static String sliderPoint = "xpath://div[@class='fl-slider__handle']";

	public static String hotelName = "xpath://section[@id='js_item_list_section']//span[contains(@class,'item-link')]";
	public static String ratings = "xpath://section[@id='js_item_list_section']//div[@class='quick-info']";
	public static String startPrice = "xpath://section[@id='js_item_list_section']//strong[contains(@class,'accommodation-list__price')]";
	public static String lowestPrice = "xpath://section[@id='js_item_list_section']//article[contains(@class,'accommodation-list__cheapest')]//span[contains(@class,'accommodation-list__price')]";
	public static String hotelImage = "xpath:(//section[@id='js_item_list_section']//img[contains(@class,'lazy-image')])[1]";

	public static String nextPageButton = "xpath://span[@class='icon-ic icon-rtl pagination__icon icon-contain']";

	public static String verifyHotelName = "xpath:(//section[@id='js_item_list_section']//span[contains(@class,'item-link')])[1]";
	public static String verifyRatings = "xpath:(//section[@id='js_item_list_section']//div[@class='quick-info'])[1]";
	public static String verifyStartPrice = "xpath:(//section[@id='js_item_list_section']//strong[contains(@class,'accommodation-list__price')])[1]";
	public static String verifyLowestPrice = "xpath:(//section[@id='js_item_list_section']//article[contains(@class,'accommodation-list__cheapest')]//span[contains(@class,'accommodation-list__price')])[1]";

	private WebDriver driver;

	public SearchHotelPage(AppLibrary appLibrary) {
		this.driver = appLibrary.getCurrentDriverInstance();
	}

	public void selectCity(String cityname) {

		AppLibrary.enterText(driver, cityNameField, cityname);
		List<WebElement> machedCities = AppLibrary.findElements(driver, cityList);

		AppLibrary.sleep(2000);

		for (WebElement a : machedCities) {

//			System.out.println(a.getText());

			if (a.getText().equals(cityname)) {

				AppLibrary.sleep(2000);
				a.click();
				break;

			} else {

				System.out.println("Searched cities not matched");

			}

		}

	}

	public void selectDate(String StartYear, String StartMonth, String StartDate) throws Exception {

		Boolean flag = false;

		do {

			String CalenderYear = AppLibrary.findElement(driver, monthHeadin).getText();

			if (!CalenderYear.contains(StartYear)) {

				AppLibrary.clickElement(driver, nextMonth);

				flag = true;

			}

			else
				flag = false;

		} while (flag);

		if (AppLibrary.isElementPresent(driver, preMonth)) {

			AppLibrary.clickElement(driver, preMonth);

		} else {

			flag = false;

			do {

				String CalenderMonth = AppLibrary.findElement(driver, monthHeadin).getText();

				if (CalenderMonth.contains(StartMonth))

					break;

				else {
					AppLibrary.clickElement(driver, nextMonth);

					flag = true;
				}
			} while (flag);

			AppLibrary.clickByJavascript(driver, todayDate.replace("number", StartDate));
		}

	}

	public void numberOfGuest(String adult, String children, String rooms) {

		AppLibrary.enterText(driver, adultCount, adult);
		AppLibrary.enterText(driver, ChildrenCount, children);
		AppLibrary.enterText(driver, roomCount, rooms);

	}

	public void priceRange(int a) {

		WebElement set = AppLibrary.findElement(driver, sliderPoint);

		Actions move = new Actions(driver);

		move.moveToElement(set).clickAndHold().moveByOffset(0, a).release().perform();

	}

	public int excelData(int totalHotelsFound) throws IOException {
		
		

//		File file = new File("ExcelOutput/testFile.xlsx");
//
//		if (file.exists()) {
//
//			file.delete();
//
//		}

		File src = new File("/Users/apple/eclipse-workspace/TrivagoAutomationCodeOld/ExcelOutput/testFile.xlsx");
		FileInputStream fis = new FileInputStream(src);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet Sheet = wb.getSheetAt(0);

		List<WebElement> listElement = AppLibrary.findElements(driver, SearchHotelPage.hotelName);
		System.out.println("Total number of hotels found" + listElement.size());
		int totalHotels = listElement.size();

		List<WebElement> hotelName = AppLibrary.findElements(driver, SearchHotelPage.hotelName);
		System.out.println("Total number of hotels found" + hotelName.size());
		List<WebElement> ratings = AppLibrary.findElements(driver, SearchHotelPage.ratings);
		System.out.println("Total number of hotels found" + ratings.size());
		List<WebElement> startPrice = AppLibrary.findElements(driver, SearchHotelPage.startPrice);
		System.out.println("Total number of hotels found" + startPrice.size());
		List<WebElement> lowPrice = AppLibrary.findElements(driver, SearchHotelPage.lowestPrice);
		System.out.println("Total number of hotels found" + lowPrice.size());

		int i, q;

		for (i = 0, q = totalHotelsFound; i < totalHotels; i++, q++) {

//			HotelsName
			Row rw = Sheet.createRow(q);
			Cell cl = rw.createCell(0);
			String elementText = hotelName.get(i).getText();
			System.out.println(elementText);
			cl.setCellValue(elementText);

//			Ratings
			Cell cl1 = rw.createCell(1);
			String elementText1 = ratings.get(i).getText();
			System.out.println(elementText1);
			cl1.setCellValue(elementText1);

//			Rate
			Cell cl2 = rw.createCell(2);
			String elementText2 = startPrice.get(i).getText();
			System.out.println(elementText2);
			cl2.setCellValue(elementText2);

//			LowestRate
			Cell cl3 = rw.createCell(3);
			String elementText3 = lowPrice.get(i).getText();
			System.out.println(elementText3);
			cl3.setCellValue(elementText3);

			FileOutputStream fout = new FileOutputStream(src);
			wb.write(fout);

		}

		return q;
	}

	public void findDetails(String hotename) throws IOException {

		File src = new File("/Users/apple/eclipse-workspace/TrivagoAutomationCodeOld/ExcelOutput/testFile.xlsx");
		FileInputStream fis = new FileInputStream(src);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet Sheet = wb.getSheetAt(0);
		int lastRowNum = Sheet.getLastRowNum();

		for (int i = 0; i < lastRowNum; i++) {

			String name1 = Sheet.getRow(i).getCell(0).getStringCellValue();

			if (name1.equals(hotename)) {

//				String ratingExcelValue = Sheet.getRow(i).getCell(1).getStringCellValue();
//				String ratingVerify = AppLibrary.findElement(driver, verifyRatings).getText();
//				assertEquals(ratingVerify, ratingExcelValue);
//
//				String startPriceExcelValue = Sheet.getRow(i).getCell(2).getStringCellValue();
//				String startPriceVerify = AppLibrary.findElement(driver, verifyStartPrice).getText();
//				assertEquals(startPriceVerify, startPriceExcelValue);
//
//				String lowPriceExcelValue = Sheet.getRow(i).getCell(3).getStringCellValue();
//				String lowPriceVerify = AppLibrary.findElement(driver, verifyLowestPrice).getText();
//				assertEquals(lowPriceVerify, lowPriceExcelValue);

				System.out.println("Data mached with row number:" + i++);

			}

		}

	}

}
