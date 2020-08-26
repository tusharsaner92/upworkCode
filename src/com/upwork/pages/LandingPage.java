package com.upwork.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.upwork.lib.AppLibrary;

public class LandingPage {

	public AppLibrary appLibrary;

//	public static String invitationButton = "xpath://li[contains(text(),'Invitations')]";
//	public static String invitationLabel = "xpath://div[contains(@class,'ant-card-head-title ')][contains(text(),'Invitations')]";
//	public static String inviteMemberButton = "xpath://button[span[contains(text(),'Invite Member')]]";
//	public static String pendingInvitationButton = "xpath://button[span[contains(text(),'Pending invitations')]]";
//	public static String firstNameLabel = "xpath://div[text()='First Name']";
//	public static String lastNameLabel = "xpath://div[text()='Last name']";
//	public static String emailLabel = "xpath://nz-card[div[div[div[text()='Invitations']]]]//div[text()='Email']";
//	public static String roleLabel = "xpath://div[text()='Role']";

	public static String searchField = "xpath://div[@class='navbar-collapse d-none d-lg-flex sticky-sublocation']//input[@class='form-control']";
	public static String searchIcon = "xpath://div[@class='navbar-collapse d-none d-lg-flex sticky-sublocation']//span[@class='glyphicon air-icon-search m-sm-left m-0-right']";
	public static String title = "xpath://div[@class='ellipsis freelancer-tile-title_compact d-none d-md-block']";
	public static String Description = "xpath://p[@data-qa='tile_description']";
	public static String skills = "xpath://div[@class=' skills-section']";
	public static String name = "xpath://a[@class='freelancer-tile-name']";
	public static String profileName = "xpath://section[@class='up-card-section py-30']//h1[@itemprop='name']";
	public static String profileTitle = "xpath://h2[@class='mb-0 font-weight-black']";
	public static String profileDesc = "xpath://div[@class='up-line-clamp']//span[@class='text-pre-line break']";
	public static String profileSkill = "xpath://a[@class='up-skill-badge']";
	public static String firstPageRandomName = "xpath://a[@class='freelancer-tile-name']";

//	public LandingPage verifyData() {
//
//		return new LandingPage(appLibrary);
//
//	}

	public void searchFreeLancers(WebDriver driver, String keyword) throws Exception {

		AppLibrary.enterText(driver, searchField, keyword);
		AppLibrary.clickByJavascript(driver, searchIcon);

		List<WebElement> titlesList = AppLibrary.findElements(driver, title);
		List<WebElement> descriptionsList = AppLibrary.findElements(driver, Description);
		List<WebElement> skillsList = AppLibrary.findElements(driver, skills);
		List<WebElement> freelancerNamesList = AppLibrary.findElements(driver, name);

		List<String> titleAll = new ArrayList<>();
		List<String> descriptionAll = new ArrayList<>();
		List<String> skillAll = new ArrayList<>();
		List<String> freelancerNameAll = new ArrayList<>();

		// Storing title, description and skills
		for (int i = 0; i < titlesList.size(); i++) {

			titleAll.add(titlesList.get(i).getText());
			descriptionAll.add(descriptionsList.get(i).getText());
			skillAll.add(skillsList.get(i).getText());
			freelancerNameAll.add(freelancerNamesList.get(i).getText());

		}

		findKeyword(driver, titleAll, descriptionAll, skillAll, freelancerNameAll, keyword);
		clickAnyName(driver);
		checkWithFreelancerData(driver, titleAll, descriptionAll, skillAll, freelancerNameAll, keyword);
	}

	public void clickAnyName(WebDriver driver) {

		List<WebElement> anyNamesList = driver.findElements(By.xpath(firstPageRandomName));
		Random rn = new Random();

		int num = 0;
		for (int i = 0; i < anyNamesList.size(); i++) {
			num = rn.nextInt(10) + 1;
		}
		anyNamesList.get(num).click();

	}

	public void findKeyword(WebDriver driver, List<String> titleAll, List<String> descriptionAll, List<String> skillAll,
			List<String> freelancerNameAll, String keyword) {

//		System.out.println("List of freelancer's in which keyword is found :");

		for (int i = 0; i < titleAll.size(); i++) {

			String tit = titleAll.get(i);
			String des = descriptionAll.get(i);
			String skl = skillAll.get(i);
			String Name = freelancerNameAll.get(i);
			System.out.println("\n" + Name);

			if (tit.contains(keyword)) {
				System.out.println("Title");
			}

			if (des.contains(keyword)) {
				System.out.println("Description");
			}

			if (skl.contains(keyword)) {
				System.out.println("Skill");
			}

		}

	}

	public void checkWithFreelancerData(WebDriver driver, List<String> titleAll, List<String> descriptionAll,
			List<String> skillAll, List<String> freelancerNameAll, String keyword) {

		String freelancerName = AppLibrary.findElement(driver, profileName).getText();
		String freelancerTitle = AppLibrary.findElement(driver, profileTitle).getText();
		String freelancerDescription = AppLibrary.findElement(driver, profileDesc).getText();
		String freelancerDescriptionNew = freelancerDescription.replace("\n", "").replaceAll("\\s+", "");

		List<WebElement> randomClickList = AppLibrary.findElements(driver, profileSkill);
		ArrayList<String> profileSkillAll = new ArrayList<String>();

		for (WebElement a : randomClickList) {

			profileSkillAll.add(a.getText());

		}

		String randomSkills = String.join(" ", profileSkillAll);

		for (int i = 0; i < freelancerNameAll.size(); i++) {

			if (freelancerNameAll.get(i).equals(freelancerName)) {

				System.out.println("Random Name " + freelancerName + " found at " + i + " position");

				if (titleAll.get(i).equals(freelancerTitle)) {

					System.out.println("Title : " + freelancerTitle);
				}

				String descText = descriptionAll.get(i).replaceAll("\\s+", "").replace("...", "");
				String skills = skillAll.get(i).replaceAll("\\s+", "").replace("more", "").replaceAll("[0-9]", "");
				String rate = randomSkills.replaceAll("\\s+", "");

				if (freelancerDescriptionNew.contains(descText)) {
					System.out.println("Description : " + freelancerDescription);
				}

				if (rate.contains(skills)) {
					System.out.println("Skills : " + profileSkillAll);
				}
			}

		}

		System.out.println("Finding keyword in Attributes of Random Record");

		if (freelancerTitle.contains(keyword)) {
			System.out.println("Keyword found in Title: " + freelancerTitle);
		}

		if (freelancerDescription.contains(keyword)) {
			System.out.println("Keyword found in Description: " + freelancerDescription);
		}
		for (int i = 0; i < profileSkillAll.size(); i++) {

			if (profileSkillAll.get(i).contains(keyword)) {
				System.out.println("Keyword found in Skills: " + profileSkillAll.get(i));
			}

		}
	}

}
