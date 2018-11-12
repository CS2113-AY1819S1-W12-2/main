//@@author leegengyu

package seedu.address.logic.commands;

import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalModuleCodes.getTypicalNotesDownloaded;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.enrolledmodule.EnrolledModule;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.TimeSlots;
import seedu.address.model.tag.Tag;
import seedu.address.ui.testutil.EventsCollectorRule;

/**
 * Contains integration tests (interaction with the Model) for {@code ExportCommand}.
 */
public class ImportCommandTest {
    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    private Model model;
    private Model expectedModel;
    private CommandHistory commandHistory;

    // valid person string
    private String theString = "";

    private String nonDuplicate = "rO0ABXNyACFzZWVkdS5hZGRyZXNzLm1vZGVsLnBlcnNvbi5QZXJzb27Kp3XsSBScIQIAB"
        + "0wAB2FkZHJlc3N0ACRMc2VlZHUvYWRkcmVzcy9tb2RlbC9wZXJzb24vQWRkcmVzcztMAAVlbWFpbHQAIkxzZWVkdS9h"
        + "ZGRyZXNzL21vZGVsL3BlcnNvbi9FbWFpbDtMAA9lbnJvbGxlZE1vZHVsZXN0AA9MamF2YS91dGlsL01hcDtMAARuYW1"
        + "ldAAhTHNlZWR1L2FkZHJlc3MvbW9kZWwvcGVyc29uL05hbWU7TAAFcGhvbmV0ACJMc2VlZHUvYWRkcmVzcy9tb2RlbC"
        + "9wZXJzb24vUGhvbmU7TAAEdGFnc3QAD0xqYXZhL3V0aWwvU2V0O0wACXRpbWVzbG90c3EAfgADeHBzcgAic2VlZHUuY"
        + "WRkcmVzcy5tb2RlbC5wZXJzb24uQWRkcmVzc+68QhHGjRwVAgABTAAFdmFsdWV0ABJMamF2YS9sYW5nL1N0cmluZzt4"
        + "cHQAGzMxMSwgQ2xlbWVudGkgQXZlIDIsICMwMi0yNXNyACBzZWVkdS5hZGRyZXNzLm1vZGVsLnBlcnNvbi5FbWFpbDw"
        + "rPd7n2s8jAgABTAAFdmFsdWVxAH4ACXhwdAARam9obmRAZXhhbXBsZS5jb21zcgARamF2YS51dGlsLlRyZWVNYXAMw"
        + "fY+LSVq5gMAAUwACmNvbXBhcmF0b3J0ABZMamF2YS91dGlsL0NvbXBhcmF0b3I7eHBwdwQAAAACdAAGQ1MyMTAxc3I"
        + "AMXNlZWR1LmFkZHJlc3MubW9kZWwuZW5yb2xsZWRtb2R1bGUuRW5yb2xsZWRNb2R1bGX5iWPuIhxbmwIAAkwAEmVucm"
        + "9sbGVkTW9kdWxlTmFtZXEAfgAJTAAQbm90ZXNTdG9yYWdlUGF0aHEAfgAJeHBxAH4AEnQAC2hvbWUvQ1MyMTAxdAAHQ"
        + "1MyMTEzVHNxAH4AE3EAfgAWdAAMaG9tZS9DUzIxMTNUeHNyAB9zZWVkdS5hZGRyZXNzLm1vZGVsLnBlcnNvbi5OYW1lo"
        + "7cOEQAgTZMCAAFMAAhmdWxsTmFtZXEAfgAJeHB0AAhKb2huIERvZXNyACBzZWVkdS5hZGRyZXNzLm1vZGVsLnBlcnNvb"
        + "i5QaG9uZU1oyTjSxPD8AgABTAAFdmFsdWVxAH4ACXhwdAAIOTg3NjU0MzJzcgARamF2YS51dGlsLkhhc2hTZXS6RIWVl"
        + "ri3NAMAAHhwdwwAAAAQP0AAAAAAAAJzcgAbc2VlZHUuYWRkcmVzcy5tb2RlbC50YWcuVGFnDqZZdHVM1NACAAFMAAd0YW"
        + "dOYW1lcQB+AAl4cHQACW93ZXNNb25leXNxAH4AIXQAB2ZyaWVuZHN4c3IAEWphdmEudXRpbC5IYXNoTWFwBQfawcMWYN"
        + "EDAAJGAApsb2FkRmFjdG9ySQAJdGhyZXNob2xkeHA/QAAAAAAABncIAAAACAAAAAV0AAN0aHVzcgATamF2YS51dGlsLkFy"
        + "cmF5TGlzdHiB0h2Zx2GdAwABSQAEc2l6ZXhwAAAADHcEAAAADHNyACRzZWVkdS5hZGRyZXNzLm1vZGVsLnBlcnNvbi5Ua"
        + "W1lU2xvdHNgQiduaNr7LQIAAUwACHRpbWVzbG90cQB+AAl4cHQABGZyZWVzcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfg"
        + "ArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+"
        + "AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAteHQAA2ZyaXNxAH4AKQAAAAx3BAAAAAxzcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3"
        + "EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQ"
        + "B+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC14dAADdHVlc3EAfgApAAAADHcEAAAADHNxAH4AK3EAfgAtc"
        + "3EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArc"
        + "QB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC1zcQB+ACtxAH4ALXh0AANtb25zcQB+ACkAAAAMdwQAAAAM"
        + "c3EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgA"
        + "rcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAteHQAA3dlZHNxAH4"
        + "AKQAAAAx3BAAAAAxzcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgA"
        + "rcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC1zcQB+ACtxAH4ALXNxAH4AK3EAfgAtc3EAfgArcQB+AC"
        + "14eA==";

    @Before
    public void setUp() throws CommandException {
        theString = "rO0ABXNyACFzZWVkdS5hZGRyZXNzLm1vZGVsLnBlcnNvbi5QZXJzb27Kp3XsSBScIQIAB0wAB2FkZHJlc3N0ACRM"
            + "c2VlZHUvYWRkcmVzcy9tb2RlbC9wZXJzb24vQWRkcmVzcztMAAVlbWFpbHQAIkxzZWVkdS9hZGRyZXNzL21vZGVsL3Bl"
            + "cnNvbi9FbWFpbDtMAA9lbnJvbGxlZE1vZHVsZXN0AA9MamF2YS91dGlsL01hcDtMAARuYW1ldAAhTHNlZWR1L2FkZHJlc"
            + "3MvbW9kZWwvcGVyc29uL05hbWU7TAAFcGhvbmV0ACJMc2VlZHUvYWRkcmVzcy9tb2RlbC9wZXJzb24vUGhvbmU7TAAEdG"
            + "Fnc3QAD0xqYXZhL3V0aWwvU2V0O0wACXRpbWVzbG90c3EAfgADeHBzcgAic2VlZHUuYWRkcmVzcy5tb2RlbC5wZXJzb2"
            + "4uQWRkcmVzc+68QhHGjRwVAgABTAAFdmFsdWV0ABJMamF2YS9sYW5nL1N0cmluZzt4cHQAHzEyMywgSnVyb25nIFdlc3Q"
            + "gQXZlIDYsICMwOC0xMTFzcgAgc2VlZHUuYWRkcmVzcy5tb2RlbC5wZXJzb24uRW1haWw8Kz3e59rPIwIAAUwABXZhbHVl"
            + "cQB+AAl4cHQAEWFsaWNlQGV4YW1wbGUuY29tc3IAEWphdmEudXRpbC5UcmVlTWFwDMH2Pi0lauYDAAFMAApjb21wYXJhdG"
            + "9ydAAWTGphdmEvdXRpbC9Db21wYXJhdG9yO3hwcHcEAAAAAHhzcgAfc2VlZHUuYWRkcmVzcy5tb2RlbC5wZXJzb24uTmFt"
            + "ZaO3DhEAIE2TAgABTAAIZnVsbE5hbWVxAH4ACXhwdAANQWxpY2UgUGF1bGluZXNyACBzZWVkdS5hZGRyZXNzLm1vZGVsLn"
            + "BlcnNvbi5QaG9uZU1oyTjSxPD8AgABTAAFdmFsdWVxAH4ACXhwdAAIOTQzNTEyNTNzcgARamF2YS51dGlsLkhhc2hTZXS6"
            + "RIWVlri3NAMAAHhwdwwAAAAQP0AAAAAAAAB4c3IAEWphdmEudXRpbC5IYXNoTWFwBQfawcMWYNEDAAJGAApsb2FkRmFjdG"
            + "9ySQAJdGhyZXNob2xkeHA/QAAAAAAABncIAAAACAAAAAV0AAN0aHVzcgATamF2YS51dGlsLkFycmF5TGlzdHiB0h2Zx2GdAw"
            + "ABSQAEc2l6ZXhwAAAADHcEAAAADHNyACRzZWVkdS5hZGRyZXNzLm1vZGVsLnBlcnNvbi5UaW1lU2xvdHNgQiduaNr7LQIAAU"
            + "wACHRpbWVzbG90cQB+AAl4cHQABGZyZWVzcQB+AB9xAH4AIXNxAH4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxA"
            + "H4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAf"
            + "gAheHQAA2ZyaXNxAH4AHQAAAAx3BAAAAAxzcQB+AB9xAH4AIXNxAH4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxA"
            + "H4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAfg"
            + "Ahc3EAfgAfcQB+ACF4dAADdHVlc3EAfgAdAAAADHcEAAAADHNxAH4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH"
            + "4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAf"
            + "gAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXh0AANtb25zcQB+AB0AAAAMdwQAAAAMc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxA"
            + "H4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAf"
            + "gAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAfgAheHQAA3dlZHNxAH4AHQAAAAx3BAAAAAxzcQB+AB9xAH4AIXNxAH"
            + "4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAfgAhc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAfgA"
            + "hc3EAfgAfcQB+ACFzcQB+AB9xAH4AIXNxAH4AH3EAfgAhc3EAfgAfcQB+ACF4eA==";


        model = new ModelManager(getTypicalAddressBook(), getTypicalNotesDownloaded(), new UserPrefs());
        expectedModel = new ModelManager(getTypicalAddressBook(), getTypicalNotesDownloaded(), new UserPrefs());
        commandHistory = new CommandHistory();
    }

    @Test
    public void execute_importStringIsInvalid() {

        // corrupting the string by adding an extra character
        theString += "a";
        ImportCommand ic = new ImportCommand(theString);
        CommandTestUtil.assertCommandFailure(ic, model, commandHistory, ImportCommand.MESSAGE_FAILED);

    }

    @Test
    public void execute_importNotDuplicate() {

        // using add example command
        ImportCommand ic = new ImportCommand(nonDuplicate);
        Set<Tag> tagSet = new HashSet<>();
        tagSet.add(new Tag("friends"));
        tagSet.add(new Tag("owesMoney"));
        Map<String, EnrolledModule> enrolledMap = new TreeMap<>();
        enrolledMap.put("CS2101", new EnrolledModule("CS2101"));
        enrolledMap.put("CS2113T", new EnrolledModule("CS2113T"));

        Person p = new Person(new Name("John Doe"), new Phone("98765432"), new Email("johnd@example.com"),
            new Address("311, Clementi Ave 2, #02-25"),
            tagSet, enrolledMap, TimeSlots.initTimeSlots());
        expectedModel.addPerson(p);
        expectedModel.commitAddressBook();

        CommandTestUtil.assertCommandSuccess(ic, model, commandHistory, ImportCommand.MESSAGE_SUCCESS, expectedModel);

    }

    @Test
    public void execute_importDuplicate() {

        ImportCommand ic = new ImportCommand(theString);
        Person p = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        expectedModel.deletePerson(p);
        expectedModel.addPerson(p);
        expectedModel.commitAddressBook();

        CommandTestUtil.assertCommandSuccess(ic, model, commandHistory,
            ImportCommand.MESSAGE_SUCCESS_OVERWRITE, expectedModel);

    }

}
