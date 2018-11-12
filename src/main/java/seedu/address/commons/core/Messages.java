package seedu.address.commons.core;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "There is no such person with the index that "
        + "you have entered. Please try again.\n";
    public static final String MESSAGE_INVALID_INDEX_PARAMETER = "You have entered an invalid index parameter."
        + "Please try again.\n";
    public static final String MESSAGE_INVALID_PRIVACY_PARAMETER = "Please enter either 'public' or 'private'"
        + "after 'export'.\n";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_PERSONS_AND_GROUPS_LISTED_OVERVIEW = "%1$d persons/groups listed!";
    public static final String MESSAGE_MODULE_NOT_FOUND = "MODULE CODE NOT FOUND";
    public static final String MESSAGE_UNABLE_REACH_IVLE = "UNABLE TO LOGIN TO IVLE AT THIS TIME";
    public static final String MESSAGE_DOWNLOAD_SUCCESS = "\r\nDownloaded file at ";
    public static final String MESSAGE_CHROME_DRIVER_NOT_FOUND =
        "chromeDrivers are not found, please check if you have installed the application correctly";
    public static final String MESSAGE_EXTRACTION_JAR_FAIL =
        "Extracting chromeDrivers or setting up Notes folder has failed";
    public static final String MESSAGE_DOWNLOAD_SELECT_SUCCESS = "Here are your the files available for: ";
    public static final String MESSAGE_USERNAME_PASSWORD_ERROR = "You have entered a wrong username or Password.";
    public static final String MESSAGE_DYNAMIC_WAITING_INTERRUPTED = "Waiting for the files have been interrupted";
    public static final String MESSAGE_DOWNLOAD_SELECT_NO_FILES_SELECTED =
        "Please select a file after the \"file/\" tag. Ie: file/(0,1,2...n))";
    public static final String MESSAGE_FILE_DOES_NOT_EXIST_ERROR = "A file you selected does not exist,"
        + "download Incomplete";
    public static final String MESSAGE_DOWNLOAD_DISABLED = "The download function has temporarily been disabled "
        + "in accordance with\r\nNUS Information Technology Acceptable use policy for IT resources Ver4.2,"
        + " Clause 4.6\r\nUsers are still able to use downloadSelectNotes to view available files to be downloaded.";
    public static final String NEWLINE_SEPARATOR = "\r\n";

    public static final String MESSAGE_FILE_LOCATION_ERROR = "The folder \"notes\" is not found!" + NEWLINE_SEPARATOR
            + "please download some notes to continue using showNotes";
    public static final String MESSAGE_FILE_INDEX_ERROR = "You have entered an invalid file index, please try again!";
}
