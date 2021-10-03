package com.pepgo.page_objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class GoogleHomePage extends BasePage {

    private WebElement searchBar = driver.findElement(By.name("q"));
    private WebElement googleSearch =  driver.findElement(By.name("btnK"));
    private WebElement imFeelingLucky = driver.findElement(By.name("btnI"));
    private WebElement imagesLink = driver.findElement(By.linkText("Images"));

    public GoogleHomePage enterSearchTerm(String searchTerm) {
        searchBar.clear();
        searchBar.sendKeys(searchTerm);

        return this;
    }

    public GoogleHomePage submitSearch() {
        googleSearch.submit();

        return this;
    }

    public GoogleHomePage searchImages() {
        imagesLink.click();

        return this;
    }

    public GoogleHomePage feelingLucky()
    {
        imFeelingLucky.click();

        return this;
    }
}