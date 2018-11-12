//@@author leegengyu

package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PRIVACY_PARAMETER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import seedu.address.logic.commands.ExportCommand;

/**
 * Test scope: similar to {@code FreeCommandParserTest}.
 * @see FreeCommandParserTest
 */
public class ExportCommandParserTest {

    private ExportCommandParser parser = new ExportCommandParser();

    @Test
    public void parse_validArgs() {
        String exportString = "public 1";
        String privacy = "public";
        String index = "1";
        assertParseSuccess(parser, " " + exportString, new ExportCommand(privacy, index));
    }

    @Test
    public void parse_invalidArgs() {
        // test if it throws parse exception when user enter empty string
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidArgs_noModifier() {

        // test if it throws parse exception when user enter empty string
        assertParseFailure(parser, "1", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            MESSAGE_INVALID_PRIVACY_PARAMETER + ExportCommand.MESSAGE_USAGE));
    }

}

