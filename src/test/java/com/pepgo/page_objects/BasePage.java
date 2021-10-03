package com.pepgo.page_objects;

import com.pepgo.DriverBase;
import java.net.MalformedURLException;
import org.openqa.selenium.remote.RemoteWebDriver;

public abstract class BasePage {

    protected RemoteWebDriver driver;

    public BasePage() {
        try {
            driver = DriverBase.getDriver();
        } catch (MalformedURLException ignored) {
            // This will be thrown when the test starts if it cannot connect to a RemoteWebDriver Instance
        }
    }
}