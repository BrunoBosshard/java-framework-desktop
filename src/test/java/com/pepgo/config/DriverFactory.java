package com.pepgo.config;

import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;import static com.pepgo.config.DriverType.FIREFOX;

import static com.pepgo.config.DriverType.valueOf;

public class DriverFactory {

    private RemoteWebDriver webDriver;
    private DriverType selectedDriverType;

    private final String operatingSystem = System.getProperty("os.name").toUpperCase();
    private final String systemArchitecture = System.getProperty("os.arch");
    private final boolean useRemoteWebDriver = Boolean.getBoolean("remoteDriver");

    private final static int MAX_RETRY_COUNT = 25;

    public DriverFactory() {
        DriverType driverType = FIREFOX;
        String browser = System.getProperty("browser", driverType.toString()).toUpperCase();
        try {
            driverType = valueOf(browser);
        } catch (IllegalArgumentException ignored) {
            System.err.println("Unknown driver specified, defaulting to '" + driverType + "'...");
        } catch (NullPointerException ignored) {
            System.err.println("No driver specified, defaulting to '" + driverType + "'...");
        }
        selectedDriverType = driverType;
    }

    public RemoteWebDriver getDriver() throws MalformedURLException {
        if (null == webDriver) {
            instantiateWebDriver(selectedDriverType);
        }

        return webDriver;
    }

    public void quitDriver() {
        if (null != webDriver) {
            webDriver.quit();
            webDriver = null;
        }
    }

    private void instantiateWebDriver(DriverType driverType) throws MalformedURLException {
        System.out.println(" ");
        System.out.println("Local Operating System: " + operatingSystem);
        System.out.println("Local Architecture: " + systemArchitecture);
        System.out.println("Selected Browser: " + selectedDriverType);
        System.out.println("Connecting to Selenium Grid: " + useRemoteWebDriver);
        System.out.println(" ");

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

        if (useRemoteWebDriver) {
            URL seleniumGridURL = new URL(System.getProperty("gridURL"));
            String desiredBrowserVersion = System.getProperty("desiredBrowserVersion");
            String desiredPlatform = System.getProperty("desiredPlatform");

            if (null != desiredPlatform && !desiredPlatform.isEmpty()) {
                desiredCapabilities.setPlatform(Platform.valueOf(desiredPlatform.toUpperCase()));
            } else {
                desiredCapabilities.setPlatform(Platform.valueOf("ANY"));
            }

            if (null != desiredBrowserVersion && !desiredBrowserVersion.isEmpty()) {
                desiredCapabilities.setVersion(desiredBrowserVersion);
            }

            desiredCapabilities.setBrowserName(selectedDriverType.toString().toLowerCase());

            int retryCount = 1;
            while(true)
            {
                try
                {
                    webDriver = new RemoteWebDriver(seleniumGridURL, desiredCapabilities);
                    break;
                }
                catch(Exception e1)
                {
                    System.out.println("Connection attempt number " + Integer.toString(retryCount) + " out of max. " + Integer.toString(MAX_RETRY_COUNT) + " attempts failed.");
                    if(retryCount > MAX_RETRY_COUNT)
                    {
                        throw new RuntimeException("Could not connect RemoteWebDriver.", e1);
                    }
                    try {
                        // thread to sleep for 3 seconds
                        Thread.sleep(3000);
                    } catch (Exception e2) {
                        System.out.println(e2);
                    }
                    retryCount++;
                    continue;
                }
            }
        } else {
            webDriver = driverType.getWebDriverObject(desiredCapabilities);
        }
    }
}