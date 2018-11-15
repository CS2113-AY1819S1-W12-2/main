//@@author E0201942
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.MergeCommand.MESSAGE_INDEX_NEEDS_TO_BE_NUMBER;
import static seedu.address.logic.commands.MergeCommand.MESSAGE_NO_GROUP_NAME;
import static seedu.address.logic.commands.MergeCommand.MESSAGE_USAGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MERGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.MergeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new FilterCommand object
 */
public class MergeCommandParser implements Parser<MergeCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public MergeCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_MERGE, PREFIX_NAME);

        List<String> indices = argMultimap.getAllValues(PREFIX_MERGE);
        List<Integer> numIndices = new ArrayList<>();
        List<String> name = argMultimap.getAllValues(PREFIX_NAME);
        int numIndex;

        if (indices.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MergeCommand.MESSAGE_USAGE));
        }
        if (name.isEmpty()) {
            throw new ParseException(MESSAGE_NO_GROUP_NAME + " " + MESSAGE_USAGE);
        }
        if (name.get(0).isEmpty()) {
            throw new ParseException(MESSAGE_NO_GROUP_NAME + " " + MESSAGE_USAGE);
        }

        for (String index : indices) {
            try {
                numIndex = Integer.parseInt(index);
                numIndices.add(numIndex - 1);
            } catch (NumberFormatException nfe) {
                throw new ParseException(MESSAGE_INDEX_NEEDS_TO_BE_NUMBER + MESSAGE_USAGE);
            }
        }

        return new MergeCommand(numIndices, name.get(0));
    }
}
