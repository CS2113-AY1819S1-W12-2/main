//@@author leegengyu

package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Import a person from a string
 */

public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": import a contact into NSync. "
        + "Parameters: YOUR_ENCODED_STRING\n"
        + "Example: " + COMMAND_WORD + " " + "[PASTE_YOUR_ENCODED_STRING_HERE]";

    public static final String MESSAGE_SUCCESS = "Import Successful!";
    public static final String MESSAGE_SUCCESS_OVERWRITE = "Import Successful! "
        + "An existing contact has been found in NSync and has been overwritten.";
    public static final String MESSAGE_FAILED = "Import Failed!";

    private final String personString;

    public ImportCommand(String input) {
        requireNonNull(input);
        this.personString = input.trim();
    }

    /**
     * Reads the input Base64 String and serialize a person object and add it into NSync
     * overwrites user data if person already exists
     */
    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {

        requireNonNull(model);
        Person p = getSerializedPerson(personString);

        String outputToUser = MESSAGE_SUCCESS;

        if (model.hasPerson(p)) {
            model.deletePerson(p);
            outputToUser = MESSAGE_SUCCESS_OVERWRITE;
        }

        model.addPerson(p);
        model.commitAddressBook();
        return new CommandResult(outputToUser);

    }

    /**
     * Serialize a Person object from the given Base64 String
     *
     * @throws CommandException if the given Base64 string is bad and is unable to serialize an object
     */
    private Person getSerializedPerson(String s) throws CommandException {

        try {
            byte[] data = Base64.getDecoder().decode(s);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Person p = (Person) ois.readObject();
            return p;
        } catch (Exception e) {
            throw new CommandException(MESSAGE_FAILED);
        }

    }

    @Override
    public boolean equals(Object obj) {
        return this.personString.equalsIgnoreCase(((ImportCommand) obj).personString);
    }

}
