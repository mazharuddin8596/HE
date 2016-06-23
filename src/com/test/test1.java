package com.test;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.main.HECustomLibraries;
import com.main.Login;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class test1
{

	Login l = new Login();
	HECustomLibraries hcl = new HECustomLibraries();
	WebDriver driver;
	Properties obj = l.getObj();
	Properties data = l.getData();
	ExtentTest logger;

	public void run() throws Throwable
	{
		// ArrayList<String> attechedlist=new ArrayList<String>();
		logger = Login.report.startTest("Attaching mail to Multiple record with attachments");
		l.closeOtherTabs();
		//l.NsLogin(data.getProperty("Emailid"), data.getProperty("pwd"), logger);
		driver = l.getDriver();
		l.gmailLogin(logger);
		List<String> fileList = new ArrayList<String>();
		//hcl.startVerification(driver, attechedlist, subject, attachment_list, include_attachments_checkbox, logger);
		hcl.compose(driver, "maz@celigo.in","","", "something", false, false, fileList, false, logger);
		driver.get("https://mail.google.com/mail/u/0/#inbox/1557269e96d22acc");
		// driver.switchTo().frame(Login.NsFrame);
		System.out.println(Login.app_frame);
		driver.switchTo().frame(Login.app_frame);
		System.out.println(Login.app_frame);
		hcl.click(driver, "Home");
		hcl.click(driver, "searchBox");
		driver.switchTo().activeElement().sendKeys("mazharuddin");

	}

	public void DownloadImage(WebElement Image, String loc) throws IOException
	{
		// WebElement Image=element;
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		int width = Image.getSize().getWidth();
		int height = Image.getSize().getHeight();
		BufferedImage img = ImageIO.read(screen);
		BufferedImage dest = img.getSubimage(Image.getLocation().getX(), Image.getLocation().getY(), width, height);
		ImageIO.write(dest, "png", screen);
		File file = new File(loc);
		FileUtils.copyFile(screen, file);
	}

	public void downloadFile_into_path(String path, WebElement image) throws Exception
	{
		Actions action = new Actions(driver);
		Robot robot = new Robot();
		action.contextClick(image).build().perform();
		robot.delay(2000);
		path = System.getProperty("user.dir") + "\\images\\Nsimage.png";
		robot.keyPress(KeyEvent.VK_V);
		StringSelection ss = new StringSelection(path);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		robot.keyPress(KeyEvent.VK_CONTROL); // press Ctrl
		robot.keyPress(KeyEvent.VK_V); // and press V
		robot.keyRelease(KeyEvent.VK_V); // release Ctrl
		robot.keyRelease(KeyEvent.VK_CONTROL); // release V
		robot.delay(2000);

		robot.keyPress(KeyEvent.VK_ENTER); // press Enter
		robot.keyRelease(KeyEvent.VK_ENTER);
	}

	@Parameters({ "browser" })
	@BeforeSuite
	public void browsercheck(String browser)
	{

		l.beforeTest(browser);
		Login.report = new ExtentReports("F:\\Screenshots\\Report.html");

	}

	@Test
	public void main() throws Throwable
	{
		run();
		// l.analyzeLog();
	}
}
