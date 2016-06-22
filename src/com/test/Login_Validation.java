package com.test;

import java.util.ArrayList;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.main.HECustomLibraries;
import com.main.Login;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class Login_Validation
{
	Login l = new Login();
	HECustomLibraries hcl = new HECustomLibraries();
	WebDriver driver;
	Properties obj = l.getObj();
	ExtentTest logger;
	Properties data = l.getData();
	boolean escape = false;
	String notification = "";

	@Parameters({ "browser", "TestingType" })
	@BeforeSuite
	public void InitialSetup(String browser, String TestingType) throws Exception
	{
		l.beforeTest(browser);
		Login.report = new ExtentReports(System.getProperty("user.dir")
				+ "\\Reports\\" + TestingType);
		driver = l.getDriver();
		l.closeOtherTabs();
	}

	public String Commom_steps(ExtentTest logger, String uname, String pwd) throws Throwable
	{
		if (!escape)
		{
			l.gmailLogin(logger);
			driver.switchTo().frame(Login.getApp_frame());
			escape = true;
		}
		l.appLogin(uname, pwd, logger);
		String notification = hcl.getNotification(driver);
		System.out.println(notification);
		WebElement username = driver.findElement(By.cssSelector("div#cega-body div.nsLoginBox input[ng-model='nsloginctrl.username']"));
		Assert.assertTrue(username.isDisplayed(), "Login page should be displayed");
		logger.log(LogStatus.PASS, "Login page is displayed");
		return notification;
	}

	@Test(priority = 0)
	public void userNameIsEmpty() throws Throwable
	{
		logger = Login.report.startTest("Verifing user should be on Login page when username field is blank");
		notification = Commom_steps(logger, "", "");
		if (notification.equals("Username field is empty, Please enter a value."))
		{
			logger.log(LogStatus.PASS, "[" + notification + "] displayed");
		}
	}

	@Test(priority = 1)
	public void passwordIsEmpty() throws Throwable
	{
		// Thread.sleep(2000);
		logger = Login.report.startTest("Empty password");
		notification = Commom_steps(logger, data.getProperty("Emailid"), "");
		if (notification.equals("Password field is empty, Please enter a value."))
		{
			logger.log(LogStatus.PASS, "[" + notification + "] displayed");
		}
	}

	@Test(priority = 2)
	public void inValidCreds() throws Throwable
	{
		Thread.sleep(2000);
		logger = Login.report.startTest("invalid creds");
		notification = Commom_steps(logger, "wronguser", "wrongpwd");
		if (notification.equals("Invalid credentials. Unable to login to NetSuite, please try again."))
		{
			logger.log(LogStatus.PASS, "[" + notification + "] displayed");
		}
	}

	@Test(priority = 3)
	public void additionalAuthentication() throws Throwable
	{
		// Thread.sleep(2000);
		logger = Login.report.startTest("Additional Authentication");
		notification = Commom_steps(logger, data.getProperty("Emailid"), data.getProperty("pwd"));
		if (notification.equals("Additional Authentication required. Please login to NetSuite in a separate tab."))
		{
			logger.log(LogStatus.PASS, "[" + notification + "] displayed");
		}

	}

	@Test(priority = 4)
	public void sessionOutNotification() throws Throwable
	{
		logger = Login.report.startTest("Verifying session timeout notification when user is logged out from New tab");
		l.NsLogin(data.getProperty("Emailid"), data.getProperty("pwd"), logger);
		l.gmailLogin(logger);
		Thread.sleep(9000);
		logger.log(LogStatus.INFO, "Opening Ns home page in new tab");
		hcl.openNewTab(driver);
		ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(allTabs.get(1));
		logger.log(LogStatus.INFO, "Logging out from Netsuite");
		// driver.navigate().to("https://system.na1.netsuite.com/app/center/card.nl?sc=-29&t=dEgUxlGSk&loginSucceeded=T&whence=");
		l.NsLogout();
		hcl.closeCurrentTab_MoveToFirstTab(driver);
		driver.switchTo().window(Login.gmailWindow);
		driver.navigate().to("https://mail.google.com/mail/u/0/#inbox/1554932e0941f621");
		// driver.findElement(By.cssSelector("div[title='Save To NetSuite']")).click();
		String notification = hcl.getNotification(driver);
		System.out.println(notification);

		if (notification.equals("NetSuite session timed out, please login again."))
		{
			logger.log(LogStatus.PASS, "[" + notification + "] displayed");
		}
		WebElement username = driver.findElement(By.cssSelector("div#cega-body div.nsLoginBox input[ng-model='nsloginctrl.username']"));
		Assert.assertTrue(username.isDisplayed(), "Login page should be displayed");
		logger.log(LogStatus.PASS, "Login page is displayed");

	}

	@Test(priority = 5)
	public void changeNsAccountFromApp() throws Throwable
	{
		logger = Login.report.startTest("Changing NS Account from App");
		l.NsLogin(data.getProperty("DiffNSEmailId"), data.getProperty("DiffNsPwd"), logger);
		l.NsLogout();
		l.NsLogin(data.getProperty("Emailid"), data.getProperty("pwd"), logger);
		l.gmailLogin(logger);

		driver.switchTo().frame(Login.getApp_frame());
		logger.log(LogStatus.INFO, "switch to App IFrame");
		Thread.sleep(3000);
		logger.log(LogStatus.INFO, "Clicking on Setting tab");
		hcl.click(driver, "Settings");

		Thread.sleep(3000);
		hcl.click(driver, "Change NS Account Link");
		logger.log(LogStatus.INFO, "click on change NS Account link");
		Thread.sleep(3000);
		l.appLogin(data.getProperty("DiffNSEmailId"), data.getProperty("DiffNsPwd"), logger);
		Thread.sleep(3000);
		logger.log(LogStatus.INFO, "logged into NS account with username: "
				+ data.getProperty("DiffNSEmailId"));
		hcl.click(driver, "Settings");
		logger.log(LogStatus.INFO, "click on Setting Tab");
		String getuser = hcl.get(driver, "Account username");
		System.out.println(getuser);
		logger.log(LogStatus.INFO, "get username");
		String expuser = "Username:\n" + data.getProperty("DiffNSEmailId");
		System.out.println(expuser);
		Assert.assertEquals(getuser, expuser);
		logger.log(LogStatus.PASS, "asserting username");
		l.NsLogout();

	}

	@Test(priority = 6)
	public void Logged_Out_LicenseNetsuiteAccount() throws Throwable
	{
		Thread.sleep(4000);
		logger = Login.report.startTest("Logout From License NS Account");
		l.NsLogin(data.getProperty("Emailid"), data.getProperty("pwd"), logger);
		l.gmailLogin(logger);
		Thread.sleep(2000);
		driver.switchTo().frame(Login.getApp_frame());
		logger.log(LogStatus.INFO, "Opening new tab");
		hcl.openNewTab(driver);
		logger.log(LogStatus.INFO, "Navigating to Newly opened tab");
		hcl.navigateToNextTab(driver);
		l.changeNsRole(driver, "New Celigo QA for Productivity Scrum [Leading] (TSTDRV1143610)", logger);
		logger.log(LogStatus.INFO, "Switching back to gmail window");
		Thread.sleep(3000);

		ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(allTabs.get(1));
		driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "w");
		driver.switchTo().window(allTabs.get(0));
		driver.switchTo().frame(Login.getApp_frame());
		String pop = driver.findElement(By.cssSelector("div.modal-content p")).getText();
		System.out.println(pop);
		logger.log(LogStatus.INFO, "Asserting Pop-up Text");
		if("You have been logged out of your licensed NetSuite account.".equals(pop)){
			System.out.println("popup displayed"+ pop);
		
		logger.log(LogStatus.PASS, "pop is displayed with message [" + pop
				+ "]");
		}
		
		
	}

	@AfterMethod
	public void tearDown(ITestResult result)
	{
		if (result.getStatus() == ITestResult.FAILURE)
		{
			logger.log(LogStatus.FAIL, result.getName() + "function is fail");
		}
		Login.report.endTest(logger);
		Login.report.flush();
	}

}
