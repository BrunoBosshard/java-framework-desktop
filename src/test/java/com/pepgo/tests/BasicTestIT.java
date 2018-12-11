package com.pepgo.tests;

import com.pepgo.DriverBase;
import com.pepgo.page_objects.GoogleHomePage;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicTestIT extends DriverBase {

    private RemoteWebDriver driver;

    // Data provided from CSV file (using OpenCSV)
    @DataProvider(name = "airportDataProvider")
    public Iterator<Object[]> provider() throws Exception {
        // .withSkipLines(1) skips the first line (headers) in the CSV file
        CSVReader reader = new CSVReaderBuilder(new FileReader("./src/test/resources/AirportData.csv")).withSkipLines(1).build();
        List<Object[]> myEntries = new ArrayList<Object[]>();
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            myEntries.add(nextLine);
        }
        reader.close();
        return myEntries.iterator();
    }

// Alternative DataProvider (data in the class)
/*
    @DataProvider
    public Object[][] airportDataProvider() {
        return new Object[][]{
                {"ATL","Atlanta"},
                {"LAX","Los Angeles"},
                {"ORD","Chicago"},
                {"DFW","Dallas"},
                {"DEN","Denver"}
        };
    }
*/

    @BeforeMethod(alwaysRun = true)
    public void setup() throws MalformedURLException {
        driver = getDriver();
    }

    @Test(groups={"regression","all"})
    public void googleCheeseExample() {
        driver.get("https://www.google.com");

        GoogleHomePage googleHomePage = new GoogleHomePage();

        System.out.println("Page title is: " + driver.getTitle());

        googleHomePage.enterSearchTerm("Cheese")
                .submitSearch();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until((ExpectedCondition<Boolean>) d -> d.getTitle().toLowerCase().startsWith("cheese"));

        System.out.println("Page title is: " + driver.getTitle());
        assertThat(driver.getTitle()).containsIgnoringCase("cheese");
    }

    @Test(groups={"smoke","all"})
    public void googleImagesExample() {
        driver.get("https://www.google.com");

        GoogleHomePage googleHomePage = new GoogleHomePage();

        System.out.println("Page title is: " + driver.getTitle());

        googleHomePage.searchImages();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until((ExpectedCondition<Boolean>) d -> d.getTitle().toLowerCase().startsWith("google images"));

        System.out.println("Page title is: " + driver.getTitle());
        assertThat(driver.getTitle()).containsIgnoringCase("images");
    }

    @Test(groups={"regression","all"}, dataProvider="airportDataProvider")
    public void googleAirportExample(final String airportCode, final String airportName) {
        driver.get("https://www.google.com");

        GoogleHomePage googleHomePage = new GoogleHomePage();

        System.out.println("Page title is: " + driver.getTitle());

        googleHomePage.enterSearchTerm("iata:" + airportCode)
                .submitSearch();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until((ExpectedCondition<Boolean>) d -> d.getTitle().toLowerCase().startsWith("iata:" + airportCode.toLowerCase()));

        System.out.println("Page title is: " + driver.getTitle());
        assertThat(driver.findElement(By.tagName("body")).getText().toLowerCase().contains(airportName.toLowerCase()));
    }

    @Test(groups={"all"})
    public void googleLuckyExample() {
        driver.get("https://www.google.com");

        GoogleHomePage googleHomePage = new GoogleHomePage();

        System.out.println("Page title is: " + driver.getTitle());

        googleHomePage.feelingLucky();

        // Ensure that the home page (<title> just "Google") is no longer displayed
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until((ExpectedCondition<Boolean>) d -> d.getTitle().toLowerCase().equals("google") == false);

        System.out.println("Page title is: " + driver.getTitle());
        // This test is deliberately failing, as the  page title is no longer just "Google"
        assertThat(driver.getTitle()).isEqualTo("Google");
    }
}