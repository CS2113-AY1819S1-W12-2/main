//@@author leegengyu

package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import seedu.address.logic.commands.ImportCommand;

/**
 * Test scope: similar to {@code FreeCommandParserTest}.
 * @see FreeCommandParserTest
 */
public class ImportCommandParserTest {

    private ImportCommandParser parser = new ImportCommandParser();

    @Test
    public void parse_validArgs() {
        //the import string does not matter, just to test if the parser is able to pass the string to the command
        String importString = "abc";
        assertParseSuccess(parser, " " + importString, new ImportCommand(importString));
    }

    @Test
    public void parse_invalidArgs() {
        // test if it throws parse exception when user enter empty string
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
    }


}
