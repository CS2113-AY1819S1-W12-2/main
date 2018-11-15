//@@author E0201942
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TimetableContainsModulePredicate;

/**
 * Parses input arguments and creates a new FilterCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns an EditCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }
        String[] keywords = trimmedArgs.split("\\s+");
        String[] days = {"mon", "tue", "wed", "thu", "fri"};
        for (int i = 0; i < keywords.length; i++) {
            for (String day : days) {
                if (keywords[i].equalsIgnoreCase(day)) {
                    if (i + 1 >= keywords.length) {
                        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            FilterCommand.MESSAGE_USAGE));
                    }
                    String time = keywords[i + 1];
                    int timeIndex = changeTimeToIndex(time);
                    if (timeIndex == 13) {
                        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            FilterCommand.MESSAGE_USAGE));
                    }
                    keywords[i + 1] = Integer.toString(timeIndex);
                }
            }
        }

        return new FilterCommand(new TimetableContainsModulePredicate(Arrays.asList(keywords)));
    }
    /**
     * Changes an inputted time into the correct index in the list of time slots in a day
     */
    private int changeTimeToIndex(String time) {
        int index = 13;
        if (time.equalsIgnoreCase("8am")) {
            index = 0;
        } else if (time.equalsIgnoreCase("9am")) {
            index = 1;
        } else if (time.equalsIgnoreCase("10am")) {
            index = 2;
        } else if (time.equalsIgnoreCase("11am")) {
            index = 3;
        } else if (time.equalsIgnoreCase("12pm")) {
            index = 4;
        } else if (time.equalsIgnoreCase("1pm")) {
            index = 5;
        } else if (time.equalsIgnoreCase("2pm")) {
            index = 6;
        } else if (time.equalsIgnoreCase("3pm")) {
            index = 7;
        } else if (time.equalsIgnoreCase("4pm")) {
            index = 8;
        } else if (time.equalsIgnoreCase("5pm")) {
            index = 9;
        } else if (time.equalsIgnoreCase("6pm")) {
            index = 10;
        } else if (time.equalsIgnoreCase("7pm")) {
            index = 11;
        }
        return index;
    }
}
