package com.test;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.main.HECustomLibraries;
import com.main.Login;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

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
		ArrayList<String> attechedlist=new ArrayList<String>();
		logger = Login.report.startTest("Attaching mail to Multiple record with attachments");
		l.closeOtherTabs();
		l.NsLogin(data.getProperty("Emailid"),data.getProperty("pwd"), logger);
		driver = l.getDriver();
		//
		/*l.gmailLogin(logger);
		ArrayList<String> fileList = new ArrayList<String>(); 
		//driver.get("https://www.facebook.com/");
		hcl.compose(driver, "mazharuddin.md@celigo.in", "Subject", true, false, fileList, true, logger);
		hcl.attach(driver, "sender", 1, true, fileList.size(), logger);
		hcl.startVerification(driver, fileList, "subject", attechedlist, true, logger);
		//hcl.startVerification(driver, attechedlist, subject, attachment_list, include_attachments_checkbox, logger);
*/		
		driver.navigate().to("https://system.na1.netsuite.com/app/crm/common/crmmessage.nl?id=10163&whence=");
		WebElement image=driver.findElement(By.cssSelector("form[name='messages_form'] img"));
		String path=System.getProperty("user.dir")+ "\\images\\Nsimage.png";
		//downloadFile_into_path(path,image);
		DownloadImage(image,path);
		//image.sendKeys("v");
		
		processImage();
		
		
		
		
		
		
		
		
	}
	
	
	static void processImage() {
		 
		String file1 = System.getProperty("user.dir")+ "\\images\\Nsimage.png";
		String file2 = System.getProperty("user.dir")+ "\\images\\image.png";
		 
		Image image1 = Toolkit.getDefaultToolkit().getImage(file1);
		Image image2 = Toolkit.getDefaultToolkit().getImage(file2);
		 
		try {
		 
		PixelGrabber grab1 =new PixelGrabber(image1, 0, 0, -1, -1, false);
		PixelGrabber grab2 =new PixelGrabber(image2, 0, 0, -1, -1, false);
		 
		int[] data1 = null;
		 
		if (grab1.grabPixels()) {
		int width = grab1.getWidth();
		int height = grab1.getHeight();
		data1 = new int[width * height];
		data1 = (int[]) grab1.getPixels();
		}
		 
		int[] data2 = null;
		 
		if (grab2.grabPixels()) {
		int width = grab2.getWidth();
		int height = grab2.getHeight();
		data2 = new int[width * height];
		data2 = (int[]) grab2.getPixels();
		}
		 
		System.out.println("Pixels equal: " + java.util.Arrays.equals(data1, data2));
		 
		} catch (InterruptedException e1) {
		e1.printStackTrace();
		}
		}
	
	
	public void DownloadImage(WebElement Image,String loc) throws IOException{
		//WebElement Image=element;
		File screen=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        int width=Image.getSize().getWidth();
        int height=Image.getSize().getHeight();
        BufferedImage img=ImageIO.read(screen);
        BufferedImage dest=img.getSubimage(Image.getLocation().getX(), Image.getLocation().getY(), width, height);
        ImageIO.write(dest, "png", screen);
        File file=new File(loc);
        FileUtils.copyFile(screen,file);
	}
	
	
	
	
	public void downloadFile_into_path(String path,WebElement image) throws Exception{
		Actions action= new Actions(driver);
		Robot robot = new Robot();
		action.contextClick(image).build().perform();
		robot.delay(2000);
		path=System.getProperty("user.dir")+ "\\images\\Nsimage.png";
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
