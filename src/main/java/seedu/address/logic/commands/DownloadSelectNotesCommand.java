package seedu.address.logic.commands;
//@@author BearPerson1

import java.io.IOException;
import java.util.ArrayList;
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
 * DownloadSelectCommand has 2 functions depending on the existance of PREFIX_SELECT_FILE. If PREFIX_SELECT_FILE
 * exists it displays all available notes for that module, else, it will follow the index supplied after
 * PREFIX_SELECT_FILE to download the files and store it in the "notes folder"
 * <p>
 * DownloadSelectCommand extends on DownloadAbstract that extends on Commands
 */

public class DownloadSelectNotesCommand extends DownloadAbstract {

    public static final String COMMAND_WORD = "downloadSelectNotes";

    public static final String MESSAGE_USAGE = "To display all available notes:" + NEWLINE_SEPARATOR + COMMAND_WORD
            + " user/(IVLE username) pass/(IVLE password) mod/(moduleCode)" + NEWLINE_SEPARATOR
            + "Example: user/e0123456 pass/******** mod/CS2113" + NEWLINE_SEPARATOR
            + "To select and download the notes(by file index):" + NEWLINE_SEPARATOR + COMMAND_WORD
            + " user/(IVLE username) pass/(IVLE password) mod/(moduleCode) file/0,1,2...n(file index)"
            + "Example: user/e0123456 pass/******** mod/CS2113 file/0,1,2" + NEWLINE_SEPARATOR;



    private static final String WORKBIN_CSS_SELECTOR_ID = "a[href^=\"/workbin\"]";
    private static final String TREEVIEW_CLASS_ID = "TreeView";
    private static final String FILE_DOWNLOAD_LINK_ATTRIBUTE_ID = "href";
    private static final String FILE_INDEX_SEPARATOR = ",";

    private String fileIndexInput;
    private ArrayList<Integer> fileSelect = new ArrayList<>();
    private String availableDownloadFiles;

    /**
     * secondary constructor to handle execution if user enters values with the PREFIX_SELECT_FILE prefix.
     */

    public DownloadSelectNotesCommand(String username, String password, String moduleCode, String fileIndexInput) {
        super(username, password, moduleCode);
        this.fileIndexInput = fileIndexInput;
    }

    public DownloadSelectNotesCommand(String username, String password, String moduleCode) {
        super(username, password, moduleCode);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        fileSelect = new ArrayList<>();
        try {
            for (String id : fileIndexInput.split(FILE_INDEX_SEPARATOR)) {
                fileSelect.add(Integer.parseInt(id));
            }
        } catch (NumberFormatException nfe) {
            throw new CommandException(Messages.MESSAGE_FILE_INDEX_ERROR + NEWLINE_SEPARATOR + MESSAGE_USAGE);
        }
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
        if (fileSelect == null) {
            availableDownloadFiles = getFileNames(driver);
            driver.close();
            return new CommandResult(Messages.MESSAGE_DOWNLOAD_SELECT_SUCCESS + moduleCode
                    + NEWLINE_SEPARATOR + availableDownloadFiles);
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
        try {
            downloadFiles(driver);
        } catch (IndexOutOfBoundsException iobe) {
            driver.close();
            throw new CommandException(Messages.MESSAGE_FILE_DOES_NOT_EXIST_ERROR + NEWLINE_SEPARATOR + MESSAGE_USAGE);
        }
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
     * If user did not enter specified file name after the PREFIX_SELECT_FILES, program will search all available
     * files of the selected module and parse it into a string.
     *
     * @param driver is the current existing WebDriver session
     * @return A string parsed with all the available files from that module.
     */

    private String getFileNames(WebDriver driver) {
        WebElement treeview = driver.findElement(By.className(TREEVIEW_CLASS_ID));
        List<WebElement> fileResult = treeview.findElements(By.cssSelector(WORKBIN_CSS_SELECTOR_ID));
        String result = new String();
        for (int i = 0; i < fileResult.size(); i++) {
            result += (i + ": " + fileResult.get(i).getText() + NEWLINE_SEPARATOR);
            //below statements are for debug. todo: remove when publishing
            //System.out.println(fileResult.get(i).getText()); // filename
            //System.out.println(fileResult.get(i).getAttribute("href")); // link
        }
        return result;
    }

    /**
     * Download files, download all the selected files mentioned after the PREFIX_SELECTED_FILES to the specified
     * download location
     *
     * @param driver is the current existing WebDriver session
     */

    protected void downloadFiles(WebDriver driver) {
        WebElement treeview = driver.findElement(By.className(TREEVIEW_CLASS_ID));
        List<WebElement> fileResult = treeview.findElements(By.cssSelector(WORKBIN_CSS_SELECTOR_ID));
        for (int fileId : fileSelect) {
            driver.get(fileResult.get(fileId).getAttribute(FILE_DOWNLOAD_LINK_ATTRIBUTE_ID));
        }
    }
}
