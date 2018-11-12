package seedu.address.logic.commands;

//@@author BearPerson1

import static junit.framework.TestCase.assertTrue;

import java.io.File;

import org.junit.Test;

public class ShowNotesCommandTest {
    /**
     * test if properly detects file location
     */

    @Test
    public void execute_showNotes_properlyFindNotesFolderSuccess() {
        String intendedFileLocation = System.getProperty("user.dir") + ShowNotesCommand.getNotesPathExtension();
        File notesFolder = new File(intendedFileLocation);
        assertTrue(notesFolder.exists());
    }

}
