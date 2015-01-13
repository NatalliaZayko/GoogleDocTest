package com.epam.pageobject.pages;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.epam.utils.DateUtils;
import com.epam.utils.HashMapSkin;
import com.epam.utils.WebDriverWaitUtils;

public class ClassMarketTestsPage extends AbstractPage {

	private static String PAGE_URL = "http://www.classmarker.com/a/tests/";
	private final static String PARENT_COURSE_XPATH = "//.[@class='test-name name'][text()='%s']";
	private final static String BUTTON_RESULT_OF_COURSE = "//div[ancestor::li/div/p[text()='%s']]/a[text()='Results']";
	private final static String DATE = "//div[@class='col-span-2'][ancestor::li//a[text()='%s']]";
	private final static String RESULT = "//div[@class='col-span-2 graph'][ancestor::li//a[text()='%s']][ancestor::li//div[@class='col-span-2'][text()[contains(.,\"%s\")]]]/span[@class='value']";

	private HashMapSkin hashMapSkin = new HashMapSkin();

	private final static String LINK_NEXT = "//a[contains(text(),'Next')]";
	@FindBy(xpath = LINK_NEXT)
	private WebElement linkNext;

	public ClassMarketTestsPage() {
		PageFactory.initElements(webDriver, this);

	}

	public void openResults(String courseRootName, String courseChildName) {

		WebElement parentCourse = webDriver.findElement(By.xpath(String.format(
				PARENT_COURSE_XPATH, courseRootName)));
		parentCourse.click();
		WebElement childCourse = webDriver.findElement(By.xpath(String.format(
				BUTTON_RESULT_OF_COURSE, courseChildName)));
		childCourse.click();

	}

	public HashMapSkin searchResults(List<String> names,
			Map<String, Calendar> datesFromDoc) {
		Calendar startDate = null;
		Calendar finishDate = null;

		for (Entry<String, Calendar> entry : datesFromDoc.entrySet()) {
			if (entry.getKey().equals("startDate")) {
				startDate = entry.getValue();

			}

			if (entry.getKey().equals("finishDate")) {
				finishDate = entry.getValue();
			}

		}

		for (String name : names) {
			List<WebElement> dates = webDriver.findElements(By.xpath(String
					.format(DATE, name)));
			if (!dates.isEmpty()) {
				for (WebElement date : dates) {
					String date_of_passing = date.getText();
					Calendar calendar = DateUtils.parserDate(date_of_passing);

					if (DateUtils.isDateInTheRange(startDate, finishDate,
							calendar)) {
						String dateForXpath = DateUtils
								.getDateForXpath(calendar);
						List<WebElement> results = webDriver.findElements(By
								.xpath(String
										.format(RESULT, name, dateForXpath)));
						for (WebElement result : results) {
							System.out.println(name + "-" + result.getText());
							hashMapSkin.add(name, result.getText());

						}

					}

				}
			}

		}

		if (WebDriverWaitUtils.isElementPresent(LINK_NEXT, 5)) {

			linkNext.click();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			searchResults(names, datesFromDoc);

		}

		return hashMapSkin;

	}

	@Override
	public void openPage() {
		webDriver.navigate().to(PAGE_URL);
	}

}