package com.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
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

public class Attach_mail_and_Verify_In_NS
{

	Login l = new Login();
	HECustomLibraries hcl = new HECustomLibraries();
	WebDriver driver;
	ExtentTest logger;
	Properties obj = l.getObj();
	Properties data = l.getData();
	// boolean escape = false;
	List<String> fileList;


	public void attach_and_Verify(boolean inline, boolean attachments, String of, int how_many_records, boolean include_attachments_checkbox) throws Throwable
	{

		l.NsLogin(data.getProperty("Emailid"), data.getProperty("pwd"), logger);
		driver = l.getDriver();
		l.gmailLogin(logger);
		fileList = new ArrayList<String>();
		fileList.add("E:\\alfiles\\2.png");
		fileList.add("E:\\alfiles\\3.html");
		fileList.add("E:\\alfiles\\4.pdf");
		String files = hcl.ExtractFilesName(fileList);
		// Creating subject and Attached files list
		Date date = new Date();
		long timeMilli = date.getTime();
		ArrayList<String> attachment_list = new ArrayList<String>(Arrays.asList(files.split(" ")));
		String subject = "Testing Mail" + timeMilli;

		logger.log(LogStatus.INFO, "Composing Normal mail");
		String compose_url = hcl.compose(driver, "raghu@celigo.in", subject, inline, attachments, fileList, false, logger);
		driver.navigate().to(compose_url);
		// Thread.sleep(6000);
		ArrayList<String> attechedlist = hcl.attach(driver, of, how_many_records, include_attachments_checkbox, fileList.size(), logger);
		hcl.startVerification(driver, attechedlist, subject, attachment_list, include_attachments_checkbox, logger);

	}

	@Test(priority = 0)
	public void attach_To_Single_RecordFrom_SenderList_With_Attachments() throws Throwable
	{
		logger = Login.report.startTest("Attaching mail from Sender list to single record with attachments", "Compose a mail with attachments and attach to single record from Sender List from Sender List with attachments");
		// inline, attachments, of, how_many_records,
		// include_attachments_checkbox
		attach_and_Verify(true, true, "sender", 1, true);
	}

	@Test(priority = 1)
	public void attach_To_Single_RecordFrom_SenderList_Without_Attachments() throws Throwable
	{
		logger = Login.report.startTest("Attaching mail from Sender list to single record without attachments", "Compose a mail with attachments and attach to single record from Sender List without attachments");
		// inline, attachments, of, how_many_records,
		// include_attachments_checkbox
		attach_and_Verify(true, true, "sender", 1, false);
	}

	@Test(priority = 2)
	public void attach_To_Multiple_RecordsFrom_SenderList_With_Attachments() throws Throwable
	{
		logger = Login.report.startTest("Attaching mail from Sender list to Multiple record with attachments", "Compose a mail with attachments and attach to Multiple record from Sender List with attachments");
		// inline, attachments, of, how_many_records,
		// include_attachments_checkbox
		attach_and_Verify(true, true, "sender", 3, true);
	}

	@Test(priority = 3)
	public void attach_To_Multiple_RecordsFrom_SenderList_Without_Attachments() throws Throwable
	{
		logger = Login.report.startTest("Attaching mail from Sender list to Multiple record without attachments", "Compose a mail with attachments and attach to Multiple record from Sender List without attachments");
		// inline, attachments, of, how_many_records,
		// include_attachments_checkbox
		attach_and_Verify(true, true, "sender", 3, false);
	}

	@Test(priority = 4)
	public void attach_To_Single_RecordFrom_ContextualList_With_Attachments() throws Throwable
	{
		logger = Login.report.startTest("Attaching mail from Contextual list to single record with attachments", "Compose a mail with attachments and attach to single record from Sender List from Contextual list with attachments");
		// inline, attachments, of, how_many_records,
		// include_attachments_checkbox
		attach_and_Verify(true, true, "contextual", 1, true);
	}

	@Test(priority = 5)
	public void attach_To_Single_RecordFrom_ContextualList_Without_Attachments() throws Throwable
	{
		logger = Login.report.startTest("Attaching mail from Contextual list to single record without attachments", "Compose a mail with attachments and attach to single record from Contextual list without attachments");
		// inline, attachments, of, how_many_records,
		// include_attachments_checkbox
		attach_and_Verify(true, true, "contextual", 1, false);
	}

	@Test(priority = 6)
	public void attach_To_Multiple_RecordsFrom_ContextualList_With_Attachments() throws Throwable
	{
		logger = Login.report.startTest("Attaching mail from Contextual list to Multiple record with attachments", "Compose a mail with attachments and attach to Multiple record from Contextual list with attachments");
		// inline, attachments, of, how_many_records,
		// include_attachments_checkbox
		attach_and_Verify(true, true, "contextual", 3, true);
	}

	@Test(priority = 7)
	public void attach_To_Multiple_RecordsFrom_ContextualList_Without_Attachments() throws Throwable
	{
		logger = Login.report.startTest("Attaching mail from Contextual list to Multiple record without attachments", "Compose a mail with attachments and attach to Multiple record from Contextual list without attachments");
		// inline, attachments, of, how_many_records,
		// include_attachments_checkbox
		attach_and_Verify(true, true, "contextual", 3, false);
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
