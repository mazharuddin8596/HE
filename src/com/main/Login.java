package com.main;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class Login
{

	private Properties obj;
	private Properties data;
	public static WebDriver driver;
	public static ExtentReports report;

	public static String gmailWindow;
	public static WebElement NsFrame;
	public static WebElement app_frame;

	public static WebElement getNsFrame()
	{
		return NsFrame;
	}

	public static void setNsFrame(WebElement nsFrame)
	{
		NsFrame = nsFrame;
	}

	public static WebElement getApp_frame()
	{
		return app_frame;
	}

	public static void setApp_frame(WebElement app_frame)
	{
		Login.app_frame = app_frame;
	}

	public Properties getObj()
	{
		return obj;
	}

	public Properties getData()
	{
		return data;
	}

	public static void setGmailWindow(String gmailWindow)
	{
		Login.gmailWindow = gmailWindow;
	}

	public static String getGmailWindow()
	{
		return gmailWindow;
	}

	public WebDriver getDriver()
	{
		return driver;
	}

	public void setDriver(WebDriver driver)
	{
		Login.driver = driver;
	}

	public Login() {
		try
		{
			LoadPropertyFiles();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void beforeTest(String browser)
	{
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")
				+ "//Lib//chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("load-extension="
				+ obj.getProperty("load-extension"));
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);

		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

		driver = new ChromeDriver(capabilities);
		setDriver(driver);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	}

	public void NsLogin(String uname, String pwd, ExtentTest logger) throws Exception
	{
		driver.navigate().to("https://system.na1.netsuite.com/app/center/card.nl?sc=-29&whence=");
		if (!(driver.getTitle().contains("Home") && driver.getTitle().contains("TSTDRV1069573")))
		{
			Thread.sleep(1000);
			logger.log(LogStatus.INFO, "Opening Netsuite URL");
			driver.navigate().to(obj.getProperty("Ns_Url"));
			Thread.sleep(2000);

			logger.log(LogStatus.INFO, "Inserting Netsuite Username : " + uname);
			driver.findElement(By.name(obj.getProperty("email"))).sendKeys(uname);

			logger.log(LogStatus.INFO, "Inserting Netsuite Password : " + pwd);
			driver.findElement(By.name(obj.getProperty("pwd"))).sendKeys(pwd);

			logger.log(LogStatus.INFO, "Clicking on Submit button");
			// driver.findElement(By.name(obj.getProperty("submit"))).click();

			driver.findElement(By.cssSelector("input[value='Login']")).click();

			Thread.sleep(1000);
			String title = driver.getTitle();
			if (title.equals("Authentication Required"))
			{
				logger.log(LogStatus.INFO, "Providing Additional Authentication");
				String question = driver.findElement(By.xpath(obj.getProperty("question"))).getText();

				// logger.log(LogStatus.INFO, "Security Question :" + question);
				String[] dataRows = question.split(" ");
				String string = dataRows[dataRows.length - 1];
				String str = (String) string.substring(0, string.length() - 1);
				System.out.println(str);

				// logger.log(LogStatus.INFO, "Security Answer :" + str);
				driver.findElement(By.name(obj.getProperty("answer"))).sendKeys(str);

				// logger.log(LogStatus.INFO, "Clicking Submit button");
				driver.findElement(By.name(obj.getProperty("submitter"))).click();
			}
		}
		else
		{
			logger.log(LogStatus.INFO, "Netsuite Session available, Skipped login ");
		}

	}

	public void gmailLogin(ExtentTest logger) throws IOException
	{
		try
		{
			driver.navigate().to("https://mail.google.com/mail/u/0");
			if (!(driver.getCurrentUrl().contains("inbox")))
			{
				logger.log(LogStatus.INFO, "Gmail URL");
				driver.navigate().to(obj.getProperty("GmailURL"));
				setGmailWindow(driver.getWindowHandle());
				System.out.println(Login.getGmailWindow());

				logger.log(LogStatus.INFO, "Gmail Username : "
						+ data.getProperty("Gmail_user"));
				Locator(driver.findElement(By.name(obj.getProperty("Gmail_user")))).sendKeys(data.getProperty("Gmail_user"));

				logger.log(LogStatus.INFO, "Next button");
				Locator(driver.findElement(By.id(obj.getProperty("Gmail_NextButton")))).click();

				logger.log(LogStatus.INFO, "Gmail Password : "
						+ data.getProperty("Gmail_Password"));
				Locator(driver.findElement(By.id(obj.getProperty("Gmail_Password")))).sendKeys(data.getProperty("Gmail_Password"));

				logger.log(LogStatus.INFO, "Sign in button");
				Locator(driver.findElement(By.id(obj.getProperty("SignInButton")))).click();
				System.out.println("login success");
			}

			driver.switchTo().window(Login.getGmailWindow());
			System.out.println(gmailWindow);
			setframes();
			appLoaded(driver);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

	}

	public void LoadPropertyFiles() throws IOException
	{
		obj = new Properties();
		// Create Object of FileInputStream Class. Pass file path.
		FileInputStream objfile = new FileInputStream(System.getProperty("user.dir")
				+ "\\src\\com\\main\\objects.properties");
		// Pass object reference objfile to load method of Properties object.
		obj.load(objfile);
		data = new Properties();
		// Create Object of FileInputStream Class. Pass file path.
		FileInputStream testdatafile = new FileInputStream(System.getProperty("user.dir")
				+ "\\src\\com\\main\\testdata.properties");
		// Pass object reference objfile to load method of Properties object.
		data.load(testdatafile);

	}

	public void appLogin(String un, String password, ExtentTest logger) throws Throwable
	{

		System.out.println("app login method");
		Thread.sleep(2000);
		boolean click = false;

		Actions action = new Actions(driver);
		WebElement username = driver.findElement(By.cssSelector("div#cega-body div.nsLoginBox input[ng-model='nsloginctrl.username']"));
		username.clear();
		logger.log(LogStatus.INFO, "Inserted Username : " + un);
		username.sendKeys(un);

		WebElement pwd = driver.findElement(By.cssSelector("div#cega-body div.nsLoginBox input[ng-model='nsloginctrl.password']"));
		pwd.clear();
		logger.log(LogStatus.INFO, "Inserted password : " + password);
		pwd.sendKeys(password);

		WebElement signin = driver.findElement(By.cssSelector("a#signin"));
		logger.log(LogStatus.INFO, "Clicking App SignIn button");

		try
		{
			action.moveToElement(signin).click().perform();
			System.out.println("sigin button pressed");
			click = true;
		}
		catch (Exception e)
		{
			System.out.println("pressed using id");
		}
		if (!click)
		{
			driver.findElement(By.id("signin")).click();
		}

	}

	public void appLoaded(WebDriver driver)
	{
		driver.switchTo().frame(app_frame);
		int Time = 0;
		do
		{
			Time += 1;
			try
			{
				WebElement username = driver.findElement(By.cssSelector("div#cega-body div.nsLoginBox input[ng-model='nsloginctrl.username']"));
				if (username.isDisplayed())
					break;
			}
			catch (Exception e)
			{
				System.out.println("Login page is not displayed");
			}
			try
			{
				WebElement home = driver.findElement(By.cssSelector("li[title='Home']"));
				if (home.isDisplayed())
				{
					System.out.println("Home page is displayed");
					break;
				}
			}
			catch (Exception e)
			{
				System.out.println("home page is not displayed");
			}
			System.out.println("In while loop " + Time);
		} while (Time < 50);
		driver.switchTo().window(Login.getGmailWindow());
		System.out.println("App loaded ");
		
	}

	public void NsLogout() throws InterruptedException
	{
		driver.get("https://system.na1.netsuite.com/pages/nllogoutnoback.jsp");
		System.out.println("Netsuite sigOut");

	}

	public void gmail_Logout() throws InterruptedException
	{
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElement(By.xpath("// a [starts-with(@title, 'Google Account:')]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@id='gb_71']")).click();
	}

	public void changeNsRole(WebDriver driver, String Account, ExtentTest logger)
	{
		driver.navigate().to("https://system.na1.netsuite.com/app/center/card.nl?sc=-29&t=dA7K232gp&loginSucceeded=T&whence=");

		logger.log(LogStatus.INFO, "Changing Role");
		driver.findElement(By.cssSelector("div#spn_cRR_d1")).click();
		List<WebElement> roles = driver.findElements(By.cssSelector("table#div__bodytab tr"));
		System.out.println(roles.size());
		int td2 = 0;
		String selrole = Account;
		for (int i = 1; i < roles.size(); i++)
		{
			List<WebElement> tds = (List<WebElement>) roles.get(i).findElements(By.tagName("td"));

			if (td2 > 0)
			{
				String account = tds.get(2).getText();

				if (account.equals(selrole))
				{
					logger.log(LogStatus.INFO, "switching to role : " + selrole);
					System.out.println("Account : " + account);
					System.out.println(tds.get(1).getText());
					tds.get(1).findElement(By.tagName("a")).click();
					break;
				}
			}
			td2++;

		}
	}

	public void closeOtherTabs() throws InterruptedException
	{
		// Extracting all Open Windows
		Thread.sleep(3000);
		ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
		// Selecting
		System.out.println(allTabs.size());
		driver.switchTo().window(allTabs.get(1));
		System.out.println(driver.getTitle());
		// closing Extra windows
		System.out.println("closing windows");
		driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "w");
		driver.switchTo().window(allTabs.get(2));
		System.out.println("closing window 1");
		driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "w");
		System.out.println("closing window 2");
		// Switching to open Window
		driver.switchTo().window(allTabs.get(0));
		driver.manage().window().maximize();
		gmailWindow = driver.getWindowHandle();

	}

	public void setframes() throws Exception
	{
		Thread.sleep(5000);
		// System.out.println("gmail window driver " + driver); Gmail window
		setApp_frame(switchFrame("class", obj.getProperty("App")));
		// System.out.println("App window driver " + Login.getApp_frame());//
		// App Frame
		setNsFrame(switchFrame("class", "ns-iframe"));
		// System.out.println("Ns frame driver " + Login.getNsFrame());// Ns
		// Frame
	}

	public WebElement switchFrame(String att, String attValue) throws Exception
	{

		System.out.println("changing frame");
		// Searching app frame
		List<WebElement> allframes = driver.findElements(By.tagName("iframe"));
		for (WebElement frames : allframes)
		{
			if ((frames.getAttribute(att)).equals(attValue))
			{
				System.out.println("frame selected " + attValue);

				return frames;
			}
		}
		return null;
	}

	public WebElement Locator(final WebElement selector)
	{
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(150, TimeUnit.SECONDS).pollingEvery(4, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);

		WebElement selectorObj = wait.until(new Function<WebDriver, WebElement>()
		{
			public WebElement apply(WebDriver driver)
			{
				return selector;
			}
		});
		return selectorObj;
	}

	public void analyzeLog() throws FileNotFoundException, IOException, Exception
	{
		System.out.println("logging console.... ");
		Date date = new Date();
		long timeMilli = date.getTime();
		// driver = switchFrame(obj.getProperty("App"), "class");
		driver.switchTo().frame(app_frame);
		Thread.sleep(2000);
		String TestFile = "log_" + timeMilli + ".txt";
		FileWriter FW = new FileWriter(System.getProperty("user.dir")
				+ "\\Logs\\" + TestFile);
		BufferedWriter BW = new BufferedWriter(FW);
		Logger BROWSER_LOG = LoggerFactory.getLogger("Browser");
		// LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		for (LogEntry logEntry : driver.manage().logs().get("browser").getAll())
		{
			BROWSER_LOG.debug("" + logEntry);
			System.out.println(logEntry);

			// for (LogEntry entry : logEntries) {
			// BW.write(entry.getLevel() + " " + entry.getMessage());
			// System.out.println(entry.toString());
			// System.out.println("===== "+entry.getLevel()+" =====");
			BW.newLine();// To write next string on new line
		}
		BW.close();
	}
}