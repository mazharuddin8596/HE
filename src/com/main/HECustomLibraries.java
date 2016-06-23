package com.main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Mazhar_2
 */
public class HECustomLibraries
{
	Login l = new Login();
	Properties obj = l.getObj();
	Properties data = l.getData();
	String attachedMailSubject = "";
	GmailSubTabData gmail_subtab_data = new GmailSubTabData();

	public void waitForInvisibility(WebElement webElement, int maxSeconds)
	{
		Long startTime = System.currentTimeMillis();
		try
		{
			while (System.currentTimeMillis() - startTime < maxSeconds * 1000
					&& webElement.isDisplayed())
			{}
		}
		catch (StaleElementReferenceException e)
		{
			return;
		}
	}

	/**
	 * Description: get notification from Gmail window, switch to Gmail frame
	 * captures notification and switch back to old frame
	 * 
	 * @param driver
	 * @return Notification String
	 * @throws InterruptedException
	 * @throws Throwable
	 */

	public String getNotification(WebDriver driver) throws InterruptedException
	{
		int Time = 0;
		String notification = "";
		do
		{
			Time += 1;
			driver.switchTo().window(Login.getGmailWindow());

			try
			{
				WebElement notify = driver.findElement(By.cssSelector(obj.getProperty("notification")));
				if (notify.isDisplayed())
				{
					notification = notify.getText();
					if (notification.equals(""))
					{
						notify = driver.findElement(By.cssSelector(obj.getProperty("notification")));
						System.out.println("trying to get notification again");
					}
					Thread.sleep(2000);
					break;
				}
			}
			catch (Exception e)
			{
				System.out.println("not visible");
				Thread.sleep(20);
			}

		} while (Time < 50);
		driver.switchTo().frame(Login.getApp_frame());
		return notification;
	}

	/****
	 * Description: Attach a open mail to first 2 records in sender
	 * 
	 * @param driver
	 * @return Attached list
	 * @throws Throwable
	 */
	public ArrayList<String> attach(WebDriver driver, String of, int norecords, boolean IncludeAttachments, int how_many_attachments, ExtentTest logger) throws Throwable
	{
		Thread.sleep(6000);
		System.out.println(Login.getApp_frame());
		driver.switchTo().frame(Login.getApp_frame());
		String notification = "";

		click(driver, "Home");
		System.out.println("waitng for sender/contextual");
		//Click on specific tab to get sender or contextual
		if (of.equals("sender"))
		{
			click(driver, "sender");
		}
		else if (of.equals("contextual"))
		{
			click(driver, "contextual");
		}
		else
		{
			System.out.println("Search operation");
		}
		
		List<WebElement> senderList = (List<WebElement>) driver.findElements(By.cssSelector(obj.getProperty("resultList")));

		System.out.println(senderList.size());
		ArrayList<String> attachList = new ArrayList<String>();
		if (norecords <= senderList.size())
		{
			for (int i = 0; i < norecords; i++)
			{
				senderList.get(i).findElement(By.cssSelector(obj.getProperty("check"))).click();
				String slist = senderList.get(i).getText();
				System.out.println("----------\n selected record name and type :\n "
						+ slist + "\n----------");
				attachList.add(slist);
			}
			logger.log(LogStatus.INFO, "Selected" + norecords + " records");

			if (IncludeAttachments)
			{
				click(driver, "IncludeAttachment_check");
				logger.log(LogStatus.INFO, "Checked Include Attachments");
			}
			else
			{
				click(driver, "IncludeAttachment_Uncheck");
				logger.log(LogStatus.INFO, "Un-Checked Include Attachments");
			}
			driver.findElement(By.cssSelector(obj.getProperty("save_email"))).click();
			logger.log(LogStatus.INFO, "Clicked on Save Email button");
			notification = getNotification(driver);

			System.out.println(notification);
			if (notification.equals(""))
				logger.log(LogStatus.INFO, "fail to get notification");
			if (notification.equals("Preparing to attach the email to "
					+ norecords + " record(s)..."))
			{
				logger.log(LogStatus.PASS, "[" + notification + "] displayed");
			}

		}
		logger.log(LogStatus.INFO, "Waiting for Loading to disappear");
		// waiting for loading icon to be disappear
		WebElement Loading = driver.findElement(By.cssSelector(obj.getProperty("loading")));
		waitForInvisibility(Loading, 6000);

		notification = getNotification(driver);
		System.out.println(notification);
		if (!IncludeAttachments)
		{
			if (notification.equals("Successfully attached the email to "
					+ norecords + " record(s)."))
			{
				logger.log(LogStatus.PASS, "[" + notification + "] displayed");
			}
			// Assert.assertEquals(notification,"Successfully attached the email to "+norecords+" record(s).");
		}
		else
		{
			String with_attachment_notification = "Successfully attached the email to "
					+ norecords
					+ " record(s).\n"
					+ how_many_attachments
					+ " files were successfully attached to record(s).";
			if (notification.equals(with_attachment_notification))
				logger.log(LogStatus.PASS, "[" + notification + "] displayed");
			else
				logger.log(LogStatus.INFO, "fail to get notification");
		}
		return attachList;
	}

	public void startVerification(WebDriver driver, ArrayList<String> attechedlist, String subject, List<String> attachment_list, boolean include_attachments_checkbox, ExtentTest logger) throws Throwable
	{
		verifyInHistory(driver, attechedlist, logger);
		for (String title : attechedlist)
		{
			logger.log(LogStatus.INFO, "Verifying mail for record : [" + title
					+ "]");
			openRecordAndVerifyInNS(driver, title, subject, attachment_list, include_attachments_checkbox, logger);

		}
	}

	public void openRecord(WebDriver driver, WebElement hist_list, ExtentTest logger) throws Throwable
	{
		click(driver, "History");
		logger.log(LogStatus.INFO, "Clicked on History");
		hist_list.click();
		logger.log(LogStatus.INFO, "Clicked on Ns record in history tab");
		Thread.sleep(1000);
		logger.log(LogStatus.INFO, "Waiting for Loading to disappear");
		// waiting for loading icon to be disappear
		try
		{
			WebElement Loading = driver.findElement(By.cssSelector(obj.getProperty("loading")));
			waitForInvisibility(Loading, 4000);
		}
		catch (Exception e)
		{
			System.out.println("Skipped Loading as it got completed early");
		}
		String RecordLink = driver.findElement(By.cssSelector(obj.getProperty("RecDetails_NS"))).getAttribute("href");
		System.out.println(RecordLink + "\n");
		driver.findElement(By.cssSelector(obj.getProperty("RecDetails_NS"))).click();
		logger.log(LogStatus.INFO, "Clicked on NS icon");

		System.out.println("Ns Record Opened");
		Thread.sleep(3000);

	}

	public void openRecordAndVerifyInNS(WebDriver driver, String title, String subject, List<String> attachments, boolean include_attachments_checkbox, ExtentTest logger) throws Throwable
	{
		click(driver, "History");
		List<WebElement> historyList = driver.findElements(By.cssSelector(obj.getProperty("history_list")));

		for (WebElement elem : historyList)
		{
			if (title.contains(elem.getText()))
			{
				click(driver, "History");
				Thread.sleep(1000);
				openRecord(driver, elem, logger);
				GmailSubTabData gmail_subtab_data = verifyMailInGmailSubTab(driver, subject, logger);
				logger.log(LogStatus.INFO, "Asserting data in Gmail SubTab");
				// Asserting data
				Assert.assertEquals(subject, gmail_subtab_data.getSubject());
				Assert.assertEquals("incoming", gmail_subtab_data.getDirection());
				System.out.println(gmail_subtab_data.getAttachments());
				if (include_attachments_checkbox)
				{
					verifyAttachments(attachments);
				}
				else
				{
					logger.log(LogStatus.INFO, "Skipping Attachment verification as Include attachment was unchecked");
				}
				verifyInCommunicationSubtab(driver, gmail_subtab_data.getViewmsgInNS(), logger);
				logger.log(LogStatus.PASS, "Verified mail is Succesfully attached to ["
						+ title + "] in Ns");
				break;
			}
		}

	}

	/**
	 * @Description takes list of records to which mail was added and verify in
	 *              App History tab
	 * @param driver
	 * @param title
	 * @return
	 * @return
	 * @throws InterruptedException
	 */

	public ArrayList<WebElement> verifyInHistory(WebDriver driver, ArrayList<String> title, ExtentTest logger) throws InterruptedException
	{
		logger.log(LogStatus.INFO, "Verifying in History tab");
		click(driver, "History");
		logger.log(LogStatus.INFO, "Click on History tab");
		ArrayList<WebElement> history_List = new ArrayList<WebElement>();
		List<WebElement> historyList = driver.findElements(By.cssSelector(obj.getProperty("history_list")));
		// storing text of web element
		ArrayList<String> his_list = new ArrayList<String>();
		// Getting history list
		for (WebElement elem : historyList)
		{
			his_list.add(elem.getText());
		}
		logger.log(LogStatus.INFO, "Verifying History contain attached records");
		Assert.assertTrue(his_list.containsAll(title), "Something is missing");
		logger.log(LogStatus.PASS, "Verifeid History contain records to which mail was attached");
		
		return history_List;
	}

	public GmailSubTabData verifyMailInGmailSubTab(WebDriver driver, String subject, ExtentTest logger) throws InterruptedException
	{
		logger.log(LogStatus.INFO, "Navigate to Next tab");
		navigateToNextTab(driver);
		logger.log(LogStatus.INFO, "Click on Gmail subtab");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector(obj.getProperty("Gmail_subtab"))).click();
		logger.log(LogStatus.INFO, "Switiching to Gmail iframe");
		Thread.sleep(2000);
		List<WebElement> msgiframe = driver.findElements(By.tagName("iframe"));
		for (WebElement elem : msgiframe)
		{

			if ((elem.getAttribute("id")).equals(obj.getProperty("Gmail_iframe_id")))
			{
				System.out.println(elem.getAttribute("id"));
				String check = obj.getProperty("Gmail_iframe_id");
				Thread.sleep(2000);
				retryingFindClick(check, driver);
				System.out.println(driver);
				break;
			}
		}
		Thread.sleep(2000);
		List<WebElement> trs = driver.findElements(By.cssSelector(obj.getProperty("Gmail_attachmail_table")));
		
		for (WebElement record : trs)
		{
			List<WebElement> tds = record.findElements(By.tagName("td"));
			String subjectingamilsubtab = tds.get(3).getText();
			if (subjectingamilsubtab.equals(subject))
			{
				logger.log(LogStatus.INFO, "Storing data in Gmail subtab");
				String msglink = tds.get(0).findElement(By.tagName("a")).getAttribute("href");
				gmail_subtab_data.setViewmsgInNS(tds.get(0).findElement(By.tagName("a")).getAttribute("href"));
				System.out.println(msglink);
				gmail_subtab_data.setAuthor(tds.get(1).getText());
				gmail_subtab_data.setRecipient(tds.get(2).getText());
				gmail_subtab_data.setSubject(tds.get(3).getText());
				String attachments = tds.get(5).getText();
				gmail_subtab_data.setAttachments(new ArrayList<String>(Arrays.asList(attachments.split("\n"))));
				gmail_subtab_data.setDirection(tds.get(6).getText());
				String sub = tds.get(7).findElement(By.tagName("a")).getAttribute("href");
				String substr = sub.substring(sub.lastIndexOf("/"));
				gmail_subtab_data.setView_in_Gmail(substr);
			}

		}
		driver.switchTo().defaultContent();
		return gmail_subtab_data;
	}

	public void verifyInCommunicationSubtab(WebDriver driver, String msg_url, ExtentTest logger)
	{
		String view_link = "";
		System.out.println("Communication subtab");
		WebElement CommunicatioTab = driver.findElement(By.id("s_commtxt"));
		CommunicatioTab.click();
		logger.log(LogStatus.INFO, "Clicked on Communication subtab");
		List<WebElement> trs = driver.findElements(By.cssSelector("table[id='messages__tab'] tbody tr"));
		logger.log(LogStatus.INFO, "Verifying in Communication tab using message id");
		for (WebElement tds : trs)
		{
			List<WebElement> td = tds.findElements(By.tagName("td"));
			view_link = td.get(0).findElement(By.tagName("a")).getAttribute("href");

			if (msg_url.equals(view_link))
			{
				System.out.println(view_link);
				Assert.assertEquals(msg_url, view_link);
				logger.log(LogStatus.PASS, "Verified Mail is attached in communication SubTab");
				break;
			}
		}
		closeCurrentTab_MoveToFirstTab(driver);
	}

	public void click(WebDriver driver, String clickon)
	{
		boolean status;
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		switch (clickon)
		{

		case "Settings":
			driver.findElement(By.cssSelector("li[title='Settings']")).click();
			break;

		case "History":
			driver.findElement(By.cssSelector("li[title='History']")).click();
			break;
		case "Create Record":
			driver.findElement(By.cssSelector("li[title='Create Record']")).click();
			break;
		case "Home":
			driver.findElement(By.cssSelector("li[title='Home']")).click();
			break;

		case "Minimize":
			driver.findElement(By.cssSelector("li[title='Minimize']")).click();
			break;

		case "Change NS Account Link":
			driver.findElement(By.cssSelector("span[ng-click='settingsctrl.changeNSAccount()']")).click();
			break;

		case "Sender":
			driver.findElement(By.cssSelector("a.sender-btn")).click();
			break;

		case "Contextual":
			driver.findElement(By.cssSelector("a.contextual-btn")).click();
			break;
			
		case "SearchBox":
			driver.findElement(By.cssSelector("input[placeholder='Search Records']")).click();
			break;

		case "IncludeAttachment_Uncheck":
			status = (boolean) jse.executeScript("return document.getElementById('includeattachments').checked;");
			System.out.println("status= " + status);
			if (status)
			{
				driver.findElement(By.cssSelector("div[class='include-attachement'] label")).click();

			}
			break;

		case "IncludeAttachment_check":
			status = (boolean) jse.executeScript("return document.getElementById('includeattachments').checked;");
			System.out.println("status= " + status);
			if (!status)
			{
				driver.findElement(By.cssSelector("div[class='include-attachement'] label")).click();
			}
			break;

		}

	}

	public void openNewTab(WebDriver driver) throws InterruptedException
	{
		// Thread.sleep(2000);
		driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "t");
		ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
		if (allTabs.size() < 1)
			driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "t");
	}

	public void closeCurrentTab_MoveToFirstTab(WebDriver driver)
	{
		ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(allTabs.get(1));
		driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "w");
		driver.switchTo().window(allTabs.get(0));
		driver.switchTo().frame(Login.getApp_frame());
	}

	/**
	 * Navigate to 2nd Tab
	 * 
	 * @param driver
	 * @throws InterruptedException
	 */
	public void navigateToNextTab(WebDriver driver) throws InterruptedException
	{
		ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(allTabs.get(1));
		// Thread.sleep(9000);
	}

	public String compose(WebDriver driver, String recipient,String Cc,String Bcc, String subject, Boolean inlineImage, Boolean Attachments, List<String> fileList, boolean celigoSend, ExtentTest logger) throws IOException, AWTException
	{
		String compose_msg_url = "";
		try
		{

			//Thread.sleep(10000);
			logger.log(LogStatus.INFO, "Clicking on Compose button");
			l.Locator(driver.findElement(By.cssSelector(obj.getProperty("compose_button")))).click();
			
			l.Locator(driver.findElement(By.cssSelector(obj.getProperty("to")))).sendKeys(recipient);
			logger.log(LogStatus.INFO, "Entering recipient as" + recipient);

			
			driver.findElement(By.xpath("//span[text()='Cc']")).click();
			driver.switchTo().activeElement().sendKeys(Cc);
			
			
			driver.findElement(By.xpath("//span[text()='Bcc']")).click();
			driver.switchTo().activeElement().sendKeys(Bcc);
			
			//Thread.sleep(2000);
			Keyboard keyboard = ((HasInputDevices) driver).getKeyboard();

			logger.log(LogStatus.INFO, "Entering subject as" + subject);
			driver.findElement(By.cssSelector(obj.getProperty("subject"))).sendKeys(subject);
			keyboard.pressKey(Keys.TAB);

			logger.log(LogStatus.INFO, "Entering text in body");
			driver.findElement(By.cssSelector(obj.getProperty("body"))).sendKeys(data.getProperty("body"));
			//Thread.sleep(2000);

			logger.log(LogStatus.INFO, "Inserting Inline image");
			if (inlineImage)
			{
				logger.log(LogStatus.INFO, "Click on Inset inline image icon");
				driver.findElement(By.cssSelector(obj.getProperty("insert_photo"))).click();
				//Thread.sleep(2000);
				List<WebElement> s1 = driver.findElements(By.tagName("iframe"));
				for (WebElement elem : s1)
				{
					if ((elem.getAttribute("class")).equals(obj.getProperty("Inline_frame")))
					{
						System.out.println("frame elements \n " + elem);
						driver.switchTo().frame(elem);
						break;
					}
				}

				//Thread.sleep(2000);
				logger.log(LogStatus.INFO, "Selecting Inline image");
				driver.findElement(By.cssSelector(obj.getProperty("Inline_image"))).click();
				//Thread.sleep(1000);
				logger.log(LogStatus.INFO, "Click on Insert button");
				driver.findElement(By.cssSelector(obj.getProperty("Inline_insert_button"))).click();
				Thread.sleep(2000);
				System.out.println("image inserted");
				driver.switchTo().defaultContent();

			}
			Thread.sleep(3000);
			if (Attachments)
			{
				logger.log(LogStatus.INFO, "Click on Attachment icon");
				driver.findElement(By.cssSelector(obj.getProperty("Attach_icon"))).click();
				FileUpload(fileList);
				Thread.sleep(5000);

			}
			if (celigoSend)
			{
				logger.log(LogStatus.INFO, "Clicking on Celigo send");
				driver.findElement(By.cssSelector(obj.getProperty("Celigo_send_button"))).click();

				logger.log(LogStatus.INFO, "capturing id from view msg link ");
				String compose_id = l.Locator(driver.findElement(By.cssSelector(obj.getProperty("view_msg_link")))).getAttribute("param");
				compose_msg_url = driver.getCurrentUrl();
				System.out.println("compose_msg_url = " + compose_msg_url);

				String id_url = compose_msg_url.substring(compose_msg_url.lastIndexOf('/') + 1);

				System.out.println("url id " + id_url);

				if (id_url.equals(compose_id))
				{
					System.out.println("Correct mail opened");
					logger.log(LogStatus.INFO, "Correct mail opened");
				}

			}
			else
			{
				logger.log(LogStatus.INFO, "Gmail send button clicked");
				driver.findElement(By.xpath("//div[text()='Send']")).click();
				Thread.sleep(5000);
				String compose_id = driver.findElement(By.cssSelector(obj.getProperty("view_msg_link"))).getAttribute("param");
				logger.log(LogStatus.INFO, "generating composed mail URL");
				compose_msg_url = "https://mail.google.com/mail/u/0/#sent/"
						+ compose_id;

			}

		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		logger.log(LogStatus.PASS, "Successfully composed mail");
		return compose_msg_url;

	}

	public String ExtractFilesName(List<String> fileList)
	{
		// TODO Auto-generated method stub
		String filenames = "";
		for (String fname : fileList)
		{
			fname = fname.substring(fname.lastIndexOf('\\') + 1);
			filenames += fname + " ";
		}

		return filenames;
	}

	public static void FileUpload(List<String> fileList) throws AWTException, InterruptedException
	{
		String path = "";
		Robot robot = new Robot();
		for (String files : fileList)
		{
			path += "\"" + files + "\"";
		}
		StringSelection ss = new StringSelection(path);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		robot.delay(4000);
		robot.keyPress(KeyEvent.VK_CONTROL); // press Ctrl
		robot.keyPress(KeyEvent.VK_V); // and press V
		robot.keyRelease(KeyEvent.VK_V); // release Ctrl
		robot.keyRelease(KeyEvent.VK_CONTROL); // release V
		robot.delay(2000);

		robot.keyPress(KeyEvent.VK_ENTER); // press Enter
		robot.keyRelease(KeyEvent.VK_ENTER); // release Enter
	}

	private void verifyAttachments(List<String> attachments)
	{
		if (attachments.isEmpty())
		{
			return;
		}
		Collections.sort(attachments);
		Collections.sort(gmail_subtab_data.getAttachments());
		System.out.println("Verified attachments : "
				+ attachments.equals(gmail_subtab_data.getAttachments()));
	}

	public String get(WebDriver driver, String name)
	{
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		String text = "";
		WebElement data;
		switch (name)
		{

		case "Account id":
			data = (WebElement) jse.executeScript("return document.getElementsByClassName('entry-field')[0]");
			text = data.getText();
			break;
		case "Account username":
			data = (WebElement) jse.executeScript("return document.getElementsByClassName('entry-field')[1]");
			text = data.getText();
			break;

		}
		return text;
	}

	/*****************************
	 * @Description retry to find frame to avoid
	 *              "StaleElementReferenceException"
	 * @param by
	 *            ,driver
	 * @return boolean value
	 */
	public boolean retryingFindClick(String by, WebDriver driver)
	{
		boolean result = false;
		int attempts = 0;
		while (attempts < 3)
		{
			try
			{
				// driver.findElement(by).click();
				driver.switchTo().frame(by);
				result = true;
				System.out.println("retrying click method");
				break;
			}
			catch (StaleElementReferenceException e)
			{
				System.out.println(" :( StaleElementReferenceException error");
			}
			attempts++;
		}
		return result;
	}

	/**
	 * @Description Perfrom Global search in NS and return result Except files
	 * @param s
	 * @param driver
	 * @return List of results
	 * @throws JSONException
	 */
	public List<String> globalSearch(String s, WebDriver driver) throws JSONException
	{
		driver.switchTo().frame(Login.NsFrame);
		System.out.println("global search");
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		String script = "return JSON.stringify(nlapiSearchGlobal( '" + s
				+ "' ) || []);";
		System.out.println(jse.executeScript(script));
		String search = (String) jse.executeScript(script);
		List<String> Search_data = new ArrayList<String>();
		JSONArray a = new JSONArray(search);
		System.out.println(a.length());
		for (int i = 0; i < a.length(); ++i)
		{
			 System.out.println(a.get(i));
			if (a.getJSONObject(i).getString("type").equals("file"))
			{
				// System.out.println("file type");
			}
			else
			{
				String name = a.getJSONObject(i).getJSONObject("valuesByKey").getJSONObject("name").getString("value");
				// System.out.println(name);
				String type = a.getJSONObject(i).getJSONObject("valuesByKey").getJSONObject("type").getString("value");
				// System.out.println(type);
				String value = name + "\n" + type;
				Search_data.add((value));
			}
		}
		return Search_data;
	}

}
