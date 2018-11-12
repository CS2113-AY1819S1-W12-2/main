//@@author leegengyu

package seedu.address.logic.parser;

import static org.junit.Assert.assertNotEquals;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FREE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.ArrayList;

import org.junit.Test;

import seedu.address.logic.commands.FreeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Test scope: similar to {@code DeleteCommandParserTest}.
 * @see DeleteCommandParserTest
 */
public class FreeCommandParserTest {

    private FreeCommandParser parser = new FreeCommandParser();

    @Test
    public void parse_validArgs() {
        ArrayList<String> listIndex = new ArrayList<>();
        listIndex.add("1");
        assertParseSuccess(parser, " " + PREFIX_FREE + "1", new FreeCommand(listIndex));
    }

    @Test
    public void parse_validArgs_differentIndex() {
        ArrayList<String> listIndex = new ArrayList<>();
        listIndex.add("2");
        FreeCommand freeCommandOne = null;
        FreeCommand freeCommandTwo = new FreeCommand(listIndex);

        try {
            freeCommandOne = parser.parse(" " + PREFIX_FREE + "1");
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid userInput.", e);
        }
        assertNotEquals(freeCommandTwo, freeCommandOne);
    }

    @Test
    public void parse_invalidArgs() {
        assertParseFailure(parser, " 1", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidArgs_wrongPrefix() {
        assertParseFailure(parser, " " + PREFIX_TAG + "1",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));
    }

}
