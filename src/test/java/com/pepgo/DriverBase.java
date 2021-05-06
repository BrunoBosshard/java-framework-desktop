package com.pepgo;

import com.pepgo.config.DriverFactory;
import com.pepgo.listeners.ScreenshotListener;
import com.pepgo.listeners.TestListener;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Listeners({ScreenshotListener.class, TestListener.class})
public class DriverBase {

    private static List<DriverFactory> webDriverThreadPool = Collections.synchronizedList(new ArrayList<DriverFactory>());
    private static ThreadLocal<DriverFactory> driverThread;

    public static void setFilesExecutable(File dir) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursive call
                    setFilesExecutable(file);
                } else {
                    if (!file.canExecute()) {
                        file.setExecutable(true);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Unable to set file executable: " + ex);
        }
    }

    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        // Set all Linux driver files executable
        File currentDirLinux = new File("src/test/resources/selenium_standalone_binaries/linux");
        setFilesExecutable(currentDirLinux);
        // Set all OSX (macOS) driver files executable
        File currentDirOSX = new File("src/test/resources/selenium_standalone_binaries/osx");
        setFilesExecutable(currentDirOSX);

        driverThread = new ThreadLocal<DriverFactory>() {
            @Override
            protected DriverFactory initialValue() {
                DriverFactory webDriverThread = new DriverFactory();
                webDriverThreadPool.add(webDriverThread);
                return webDriverThread;
            }
        };
    }

    public static RemoteWebDriver getDriver() throws MalformedURLException {
        return driverThread.get().getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public static void clearCookies() {
        try {
            getDriver().manage().deleteAllCookies();
        } catch (Exception ex) {
            System.err.println("Unable to delete cookies: " + ex);
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void closeDriverObjects() {
        for (DriverFactory webDriverThread : webDriverThreadPool) {
            webDriverThread.quitDriver();
        }
    }
}