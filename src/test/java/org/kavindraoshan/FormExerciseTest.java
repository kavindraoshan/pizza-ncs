package org.kavindraoshan;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Random;

public class FormExerciseTest {

    @Test
    public void Login() {

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();
        driver.get("https://d1zgi04j6ht6lv.cloudfront.net/#/");

        // Click the LOGIN/SIGNUP icon
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@aria-label='login or signup']")));
        safeClick(driver, loginButton);

        // Click "Sign Up" link
        WebElement loginSignUpButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Sign Up']")));
        safeClick(driver, loginSignUpButton);

        // Trigger validation by clicking Sign Up without input
        WebElement signUpButtonOnForm = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Sign Up']")));
        safeClick(driver, signUpButtonOnForm);

        Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#username-err"))).getText(), "Username is required");
        Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#password-err"))).getText(), "Password is required");
        Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#confirm-err"))).getText(), "Please confirm your password");

        // Enter invalid inputs
        WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#input-91")));
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#input-94")));
        WebElement confirmPasswordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#input-97")));

        usernameField.sendKeys("abc");
        passwordField.sendKeys("abc");
        confirmPasswordField.sendKeys("ab");
        safeClick(driver, signUpButtonOnForm);

        Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#username-err"))).getText(), "Username must be minimum of 6 characters");
        Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#password-err"))).getText(), "Password must be minimum of 8 characters");
        Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#confirm-err"))).getText(), "Your passwords do not match");

        // Check existing username
        clearAndSendKeys(usernameField, "donaldtrump");
        Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#username-err"))).getText(), "Username already exists");

        // Register with unique username
        int randomNum = new Random().nextInt(1000);
        String uniqueUsername = "robinhood" + randomNum;

        clearAndSendKeys(usernameField, uniqueUsername);
        clearAndSendKeys(passwordField, "letmein2019");
        clearAndSendKeys(confirmPasswordField, "letmein2019");

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#username-err")));
        safeClick(driver, signUpButtonOnForm);

        // Verify success popup
        WebElement snackPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='v-snack__wrapper v-sheet theme--dark']")));
        String[] lines = snackPopup.getText().split("\n");
        String message = lines.length > 1 ? lines[1] : lines[0];
        Assert.assertEquals(message, "Thanks " + uniqueUsername + ", you can now login.");

        //Quit WebDriver
        driver.quit();
    }

    // Safely click with fallback if Overlay is there
    private void safeClick(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    // Clear and type input
    private void clearAndSendKeys(WebElement element, String value) {
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
        element.sendKeys(value);
    }

}
