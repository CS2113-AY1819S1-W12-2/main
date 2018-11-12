package seedu.address.logic.commands;

//@@author BearPerson1

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.CORRECT_FILE_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.CORRECT_MODULE_CODE;
import static seedu.address.logic.commands.CommandTestUtil.INCORRECT_FILE_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.INCORRECT_MODULE_CODE;
import static seedu.address.logic.commands.CommandTestUtil.INCORRECT_PASSWORD;
import static seedu.address.logic.commands.CommandTestUtil.INCORRECT_USERNAME;
import static seedu.address.logic.commands.CommandTestUtil.VERY_INCORRECT_FILE_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.io.File;

import seedu.address.commons.core.Messages;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class DownloadSelectNotesCommandTest {

    private Model model = new ModelManager();
    private CommandHistory commandHistory = new CommandHistory();

    /**
     * checks if incorrect username and password fails correctly
     */

    public void execute_downloadSelectNotesCommand_wrongUserNameAndPass() {
        DownloadSelectNotesCommand command = new DownloadSelectNotesCommand(INCORRECT_USERNAME, INCORRECT_PASSWORD,
                CORRECT_MODULE_CODE);
        assertCommandFailure(command, model, commandHistory, Messages.MESSAGE_USERNAME_PASSWORD_ERROR
                + DownloadSelectNotesCommand.NEWLINE_SEPARATOR + DownloadSelectNotesCommand.MESSAGE_USAGE);
    }

    /**
     * checks if incorrect module code will fail correctly
     */

    public void execute_downloadSelectNotesCommand_invalidModuleCodeFailure() {
        DownloadSelectNotesCommand command = new DownloadSelectNotesCommand(INCORRECT_USERNAME,
                INCORRECT_PASSWORD, INCORRECT_MODULE_CODE);
        assertCommandFailure(command, model, commandHistory, Messages.MESSAGE_USERNAME_PASSWORD_ERROR
                + DownloadSelectNotesCommand.NEWLINE_SEPARATOR + DownloadSelectNotesCommand.MESSAGE_USAGE);
    }

    /**
     * checks if extremely long, un-parsable file Index will fail correctly
     */

    public void execute_downloadSelectNotesCommand_veryInvalidFileIndex() {
        DownloadSelectNotesCommand command = new DownloadSelectNotesCommand(INCORRECT_USERNAME,
                INCORRECT_PASSWORD, CORRECT_MODULE_CODE, VERY_INCORRECT_FILE_INDEX);
        assertCommandFailure(command, model, commandHistory, Messages.MESSAGE_FILE_INDEX_ERROR
                + DownloadSelectNotesCommand.NEWLINE_SEPARATOR + DownloadSelectNotesCommand.MESSAGE_USAGE);
    }

    /**
     * checks if un-findable file Index will fail correctly
     */

    public void execute_downloadSelectNotesCommand_invalidFileName() {
        DownloadSelectNotesCommand command = new DownloadSelectNotesCommand(INCORRECT_USERNAME,
                INCORRECT_PASSWORD, INCORRECT_MODULE_CODE, INCORRECT_FILE_INDEX);
        assertCommandFailure(command, model, commandHistory, Messages.MESSAGE_USERNAME_PASSWORD_ERROR
                + DownloadSelectNotesCommand.NEWLINE_SEPARATOR + DownloadSelectNotesCommand.MESSAGE_USAGE);
    }

    /**
     * checks if notes file is correctly created after execution of downloadSelectNotesCommand.
     */


    public void execute_notesFilesCreated() {
        DownloadSelectNotesCommand command = new DownloadSelectNotesCommand(INCORRECT_USERNAME,
                INCORRECT_PASSWORD, INCORRECT_MODULE_CODE, CORRECT_FILE_INDEX);
        String intendedFileLocation = System.getProperty("user.dir") + DownloadAllNotesCommand.DOWNLOAD_FILE_PATH;
        File notesFile = new File(intendedFileLocation);
        assertCommandFailure(command, model, commandHistory, Messages.MESSAGE_USERNAME_PASSWORD_ERROR
                + DownloadSelectNotesCommand.NEWLINE_SEPARATOR + DownloadSelectNotesCommand.MESSAGE_USAGE);
        assertTrue(notesFile.exists());
    }

    /**
     * check if windows chrome driver is properly extracted
     */

    public void execute_windowsDriverExtracted() {
        DownloadSelectNotesCommand command = new DownloadSelectNotesCommand(INCORRECT_USERNAME,
                INCORRECT_PASSWORD, INCORRECT_MODULE_CODE);
        String intendedFileLocation = System.getProperty("user.dir")
                + "/" + DownloadAllNotesCommand.WINDOWS_CHROME_DRIVER_DIRECTORY;
        File windowsDriverDir = new File(intendedFileLocation);
        intendedFileLocation += "/" + DownloadAllNotesCommand.WINDOWS_CHROME_DRIVER_NAME;
        File windowsChromeDriver = new File(intendedFileLocation);
        assertCommandFailure(command, model, commandHistory, Messages.MESSAGE_USERNAME_PASSWORD_ERROR
                + DownloadSelectNotesCommand.NEWLINE_SEPARATOR + DownloadSelectNotesCommand.MESSAGE_USAGE);
        try {
            assertTrue(windowsDriverDir.exists());
        } catch (NullPointerException npe) {
            throw new AssertionError("MacDirectory was not created");
        }
        try {
            assertTrue(windowsChromeDriver.exists());
        } catch (NullPointerException npe) {
            throw new AssertionError("MacDirectory was not created");
        }
    }

    /**
     * check if mac chrome driver is properly extracted.
     */


    public void execute_macDriverExtracted() {
        DownloadSelectNotesCommand command = new DownloadSelectNotesCommand(INCORRECT_USERNAME,
                INCORRECT_PASSWORD, INCORRECT_MODULE_CODE);
        String intendedFileLocation = System.getProperty("user.dir")
                + "/" + DownloadAllNotesCommand.MAC_CHROME_DRIVER_DIRECTORY;
        File macDriverDir = new File(intendedFileLocation);
        intendedFileLocation += "/" + DownloadAllNotesCommand.MAC_CHROME_DRIVER_NAME;
        File macChromeDriver = new File(intendedFileLocation);
        assertCommandFailure(command, model, commandHistory, Messages.MESSAGE_USERNAME_PASSWORD_ERROR
                + DownloadSelectNotesCommand.NEWLINE_SEPARATOR + DownloadSelectNotesCommand.MESSAGE_USAGE);
        try {
            assertTrue(macDriverDir.exists());
        } catch (NullPointerException npe) {
            throw new AssertionError("MacDirectory was not created");
        }
        try {
            assertTrue(macChromeDriver.exists());
        } catch (NullPointerException npe) {
            throw new AssertionError("MacDirectory was not created");
        }
    }

    /**
     * check if notes download is clear of files of the "crdownload" file type.
     */
    public void execute_checkNotesFolderClearForDownload() {
        DownloadSelectNotesCommand command = new DownloadSelectNotesCommand(INCORRECT_USERNAME,
                INCORRECT_PASSWORD, INCORRECT_MODULE_CODE, CORRECT_FILE_INDEX);
        String intendedFileLocation = System.getProperty("user.dir")
                + DownloadAllNotesCommand.DOWNLOAD_FILE_PATH;
        assertCommandFailure(command, model, commandHistory, Messages.MESSAGE_USERNAME_PASSWORD_ERROR
                + DownloadSelectNotesCommand.NEWLINE_SEPARATOR + DownloadSelectNotesCommand.MESSAGE_USAGE);
        File notesFile = new File(intendedFileLocation);
        String[] filesInNotesFile = notesFile.list();
        try {
            for (String files : filesInNotesFile) {
                assertFalse(files.contains(DownloadAllNotesCommand.DOWNLOAD_FILE_ONGOING_EXTENSION));
            }
        } catch (Exception e) {
            throw new AssertionError("A crdownload file exist");
        }
    }

}

