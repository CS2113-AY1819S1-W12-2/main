package seedu.address.logic.commands;

//@@author BearPerson1
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import seedu.address.commons.core.Messages;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * DownloadAllNotesCommand Will download all the notes from a selected module and store it in the "notes" folder.
 * <p>
 * DownloadAllNotesCommand extends on DownloadAbstract that extends on command.
 */

public class DownloadAllNotesCommand extends DownloadAbstract {

    public static final String COMMAND_WORD = "downloadAllNotes";

    public static final String MESSAGE_USAGE = "To download all your notes from IVLE:" + NEWLINE_SEPARATOR
            + COMMAND_WORD + "user/(IVLE username) pass/(IVLE password) mod/(moduleCode)" + NEWLINE_SEPARATOR
            + "Example: user/e0123456 pass/******** mod/CS2113";

    private static final String CHECKBOX_XPATH_VALUE = "//input[@type='checkbox']";

    private static final String IVLE_DOWNLOAD_PAGE_BUTTON_ID =
            "ctl00_ctl00_ctl00_ContentPlaceHolder1_ContentPlaceHolder1_ContentPlaceHolder1_btnDownloadSel";


    public DownloadAllNotesCommand(String username, String password, String moduleCode) {
        super(username, password, moduleCode);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {

        try {
            extractFilesFromJar();
        } catch (IOException io) {
            throw new CommandException(Messages.MESSAGE_EXTRACTION_JAR_FAIL);
        }

        try {
            initializeChromeDriverPaths();
        } catch (NullPointerException npe) {
            throw new CommandException(Messages.MESSAGE_CHROME_DRIVER_NOT_FOUND);
        }

        WebDriver driver = initializeWebDriver();
        try {
            loginIvle(driver);
        } catch (NoSuchElementException nse) {
            driver.close();
            throw new CommandException(Messages.MESSAGE_UNABLE_REACH_IVLE);
        }
        if (!isLoggedIn(driver)) {
            driver.close();
            throw new CommandException(Messages.MESSAGE_USERNAME_PASSWORD_ERROR + NEWLINE_SEPARATOR + MESSAGE_USAGE);
        }
        if (!isModuleExisting(driver)) {
            driver.close();
            throw new CommandException(Messages.MESSAGE_MODULE_NOT_FOUND + NEWLINE_SEPARATOR + MESSAGE_USAGE);
        }
        /**
         * Updated to disable download operations, if isDownloadDisabled==true.
         * Function will not proceed after this if statement.
         */
        if (isDownloadDisabled) {
            driver.close();
            return new CommandResult(Messages.MESSAGE_DOWNLOAD_DISABLED);
        }
        initializeDownloadFolder();
        downloadFiles(driver);
        try {
            dynamicWaiting();
        } catch (InterruptedException ie) {
            throw new CommandException(Messages.MESSAGE_DYNAMIC_WAITING_INTERRUPTED);
        }
        driver.close();
        model.addNotes(COMMAND_WORD, moduleCode);
        return new CommandResult(moduleCode + Messages.MESSAGE_DOWNLOAD_SUCCESS
                + downloadPath);
    }

    /**
     * downloadFiles selects all the available files to be downloaded and then selects the "download files" button
     *
     * @param driver is the current existing WebDriver session
     */



    protected void downloadFiles(WebDriver driver) {
        List<WebElement> checkBoxList = driver.findElements(By.xpath(CHECKBOX_XPATH_VALUE));
        for (WebElement checkBox : checkBoxList) {
            if (!checkBox.isSelected()) {
                checkBox.click();
            }
        }
        driver.findElement(By.id(IVLE_DOWNLOAD_PAGE_BUTTON_ID)).click();
    }
}
