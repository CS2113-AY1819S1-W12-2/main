//@@author leegengyu

package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PRIVACY_PARAMETER;

import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ExportCommand object
 */
public class ExportCommandParser implements Parser<ExportCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ExportCommand
     * and returns an ExportCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public ExportCommand parse(String args) throws ParseException {

        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
        }

        String[] keywords = trimmedArgs.split("\\s+");
        if (!keywords[0].equalsIgnoreCase("private") && !keywords[0].equalsIgnoreCase("public")) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    MESSAGE_INVALID_PRIVACY_PARAMETER + ExportCommand.MESSAGE_USAGE));
        }

        if (keywords.length > 2) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
        }

        return new ExportCommand(keywords[0], keywords[1]);

    }
}
