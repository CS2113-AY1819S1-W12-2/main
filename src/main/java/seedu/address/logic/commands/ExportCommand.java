//@@author leegengyu

package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;

import seedu.address.commons.core.Messages;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.IsNotSelfOrMergedPredicate;
import seedu.address.model.person.IsSelfPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.TimeSlots;


/**
 * Export a person into string
 */

public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Export a person's full details "
        + "into a string, which is used for import (into another NSync). "
        + "Parameters: PUBLIC/PRIVATE, SELF/INDEX\n"
        + "Example: " + COMMAND_WORD + " public 1";

    private final String index;
    private final String privacy;

    public ExportCommand(String privacy, String index) {
        requireNonNull(index);
        this.index = index;
        this.privacy = privacy;
    }

    /**
     * Calls getSerializedString to get a Base64 string for the person selected through the index.
     * This generated string is copied to the user's clipboard for convenience (easy pasting).
     *
     * @throws CommandException if the index given is invalid
     */

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {

        requireNonNull(model);
        List<Person> filteredPersonList = model.getFilteredPersonList();
        Person myPerson;

        if (index.equalsIgnoreCase("self")) {
            filteredPersonList = ((ObservableList<Person>) filteredPersonList).filtered(new IsSelfPredicate());
            myPerson = filteredPersonList.get(0);
        } else {
            int num;
            try {
                num = Integer.parseInt(index) - 1;
            } catch (NumberFormatException nfe) {
                throw new CommandException(String.format("You have entered an invalid number for the INDEX parameter. "
                    + "Please enter a valid index.\n" + MESSAGE_USAGE));
            }
            filteredPersonList = ((ObservableList<Person>) filteredPersonList).filtered
                (new IsNotSelfOrMergedPredicate());

            if (num >= filteredPersonList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            myPerson = filteredPersonList.get(num);
        }

        Map<String, List<TimeSlots>> timeSlotsNew;
        Person newPerson = myPerson;
        if (!privacy.equalsIgnoreCase("public")) {
            timeSlotsNew = changeToBusy(myPerson);

            newPerson = new Person(myPerson.getName(), myPerson.getPhone(), myPerson.getEmail(),
                myPerson.getAddress(), myPerson.getTags(), myPerson.getEnrolledModules(),
                timeSlotsNew);
        }

        String serializedString = getSerializedString(newPerson);

        StringSelection ss = new StringSelection(serializedString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(ss, null);

        String outputToUser = serializedString + "\n";
        outputToUser += "The generated string has been copied onto your clip-board.";

        return new CommandResult(outputToUser);

    }

    /**
     * Generates the Base64 String for the Person object.
     */
    private String getSerializedString(Serializable o) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            oos.close();

            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            return "Error generating string. Please try again.";
        }

    }

    /**
     * Changes all time slots that have activities to a busy slot.
     */
    private Map changeToBusy(Person source) {

        Map<String, List<TimeSlots>> timeSlots = source.getTimeSlots();
        Map<String, List<TimeSlots>> timeSlotsNew = new HashMap<>();
        List<TimeSlots> timeNew = new ArrayList<>();
        String[] days = {"mon", "tue", "wed", "thu", "fri"};
        for (String day : days) {
            List<TimeSlots> daySlots = timeSlots.get(day);
            timeSlotsNew.put(day, timeNew);
            for (int i = 0; i < 12; i++) {
                TimeSlots activity = daySlots.get(i);

                if (!activity.toString().equalsIgnoreCase("free")) {
                    activity = new TimeSlots("busy");
                    timeNew.add(activity);
                } else {
                    timeNew.add(new TimeSlots(activity.toString()));
                }
            }
            timeNew = new ArrayList<>();
        }
        return timeSlotsNew;
    }

    @Override
    public boolean equals(Object other) {

        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ExportCommand)) {
            return false;
        }

        // state check
        ExportCommand e = (ExportCommand) other;

        return this.privacy.equals(e.privacy)
            && this.index.equals(e.index);


    }
}
