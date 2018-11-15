//@@author E0201942
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MERGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.collections.ObservableList;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.enrolledmodule.EnrolledModule;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.IsNotSelfOrMergedPredicate;
import seedu.address.model.person.IsSelfPredicate;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.TimeSlots;
import seedu.address.model.tag.Tag;

/**
 * Merges the timetables of multiple people
 */

public class MergeCommand extends Command {

    public static final String COMMAND_WORD = "merge";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Merges the timetables of selected people "
            + "by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be positive integer) + GROUP NAME (must not be empty)"
            + PREFIX_MERGE + "[INDEX] " + PREFIX_NAME + "[GROUP NAME] "
            + "for all timetables you want to merge.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_MERGE + "1 " + PREFIX_MERGE + "2 " + PREFIX_NAME
            + "GES PROJECT";

    public static final String MESSAGE_MERGE_TIMETABLE_SUCCESS = "Timetables Merged";
    public static final String MESSAGE_INVALID_INDEX = "Invalid index. Index selected does not exist.";
    public static final String MESSAGE_UPDATE_GROUP_SUCCESS = "Group has been edited: %1$s";
    public static final String MESSAGE_INDEX_NEEDS_TO_BE_NUMBER = "Invalid index. Index needs to be a positive "
            + "integer\n";
    public static final String MESSAGE_NO_GROUP_NAME = "No group name entered.";

    private final List<Integer> indices;
    private final Name name;

    public MergeCommand(List<Integer> indices, String name) {
        requireNonNull(indices);
        requireNonNull(name);

        this.indices = indices;
        this.name = new Name(name);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        List<Person> mainList = ((ObservableList<Person>) lastShownList).filtered(new IsNotSelfOrMergedPredicate());
        List<Person> selfList = ((ObservableList<Person>) lastShownList).filtered(new IsSelfPredicate());
        Person[] personsToMerge = new Person[lastShownList.size()];

        for (Integer index : indices) {
            if (index > lastShownList.size()) {
                throw new CommandException(MESSAGE_INVALID_INDEX);
            }
        }

        int i = 0;
        for (int it : indices) {
            if (it > mainList.size() - 1 || it < 0) {
                throw new CommandException(String.format(MESSAGE_INVALID_INDEX,
                        MergeCommand.MESSAGE_USAGE));
            }
            personsToMerge[i] = mainList.get(it);
            i++;
        }
        personsToMerge[i] = selfList.get(0);
        i++;
        for (int j = 0; j < i - 1; j++) {
            personsToMerge[j + 1] = mergeTimetables(personsToMerge[j], personsToMerge[j + 1], j);
        }
        if (model.hasPerson(personsToMerge[i - 1])) {
            model.updatePerson(personsToMerge[i - 1], personsToMerge[i - 1]);
            model.commitAddressBook();
            return new CommandResult(String.format(MESSAGE_UPDATE_GROUP_SUCCESS, name));
        }
        model.addPerson(personsToMerge[i - 1]);
        model.commitAddressBook();
        return new CommandResult(MESSAGE_MERGE_TIMETABLE_SUCCESS);

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof MergeCommand // instanceof handles nulls
                && indices.equals(((MergeCommand) other).indices) && name.equals(((MergeCommand) other).name));
    }

    /**
     * Merges 2 people into a single person with a merged timetable
     */
    private Person mergeTimetables(Person person1, Person person2, int index) {
        Name mergedName = name;
        Phone phone = new Phone("99999999");
        Email email = new Email("notimportant@no");
        Address address;
        if (index == 0) {
            address = new Address(person1.getName().toString() + ", " + person2.getName().toString());
        } else {
            address = new Address(person1.getAddress().toString() + ", " + person2.getName().toString());
        }
        Set<Tag> mergedTags = new HashSet<>();
        mergedTags.add(new Tag("merged"));
        Map<String, List<TimeSlots>> mergedSlots = mergeTimeSlots(person1.getTimeSlots(), person2.getTimeSlots());
        Map<String, EnrolledModule> enrolledClassMap = new TreeMap<>();


        return new Person(mergedName, phone, email, address, mergedTags, enrolledClassMap,
                mergedSlots);


    }

    /**
     * Creates a new merged timetable from 2 timetables.
     */
    private Map<String, List<TimeSlots>> mergeTimeSlots(Map<String, List<TimeSlots>> slots1,
                                                        Map<String, List<TimeSlots>> slots2) {
        TimeSlots[] mon1 = slots1.get("mon").toArray(new TimeSlots[0]);
        TimeSlots[] mon2 = slots2.get("mon").toArray(new TimeSlots[0]);
        TimeSlots[] tue1 = slots1.get("tue").toArray(new TimeSlots[0]);
        TimeSlots[] tue2 = slots2.get("tue").toArray(new TimeSlots[0]);
        TimeSlots[] wed1 = slots1.get("wed").toArray(new TimeSlots[0]);
        TimeSlots[] wed2 = slots2.get("wed").toArray(new TimeSlots[0]);
        TimeSlots[] thu1 = slots1.get("thu").toArray(new TimeSlots[0]);
        TimeSlots[] thu2 = slots2.get("thu").toArray(new TimeSlots[0]);
        TimeSlots[] fri1 = slots1.get("fri").toArray(new TimeSlots[0]);
        TimeSlots[] fri2 = slots2.get("fri").toArray(new TimeSlots[0]);
        List<TimeSlots> finalMon;
        List<TimeSlots> finalTue;
        List<TimeSlots> finalWed;
        List<TimeSlots> finalThu;
        List<TimeSlots> finalFri;
        Map<String, List<TimeSlots>> finalSlots = new HashMap<>();

        finalMon = compareTimeSlots(mon1, mon2);
        finalTue = compareTimeSlots(tue1, tue2);
        finalWed = compareTimeSlots(wed1, wed2);
        finalThu = compareTimeSlots(thu1, thu2);
        finalFri = compareTimeSlots(fri1, fri2);


        finalSlots.put("mon", finalMon);
        finalSlots.put("tue", finalTue);
        finalSlots.put("wed", finalWed);
        finalSlots.put("thu", finalThu);
        finalSlots.put("fri", finalFri);
        return finalSlots;
    }

    /**
     * Compares 2 lists of time slots and returns a merged list.
     */
    List<TimeSlots> compareTimeSlots(TimeSlots[] day1, TimeSlots[] day2) {
        List<TimeSlots> finalDay = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            if (day1[i].toString().equalsIgnoreCase("free")
                    || day1[i].toString().equalsIgnoreCase("0")) {
                day1[i] = new TimeSlots("0");
            } else {
                try {
                    Integer.parseInt(day1[i].toString());
                } catch (NumberFormatException e) {
                    day1[i] = new TimeSlots("1");
                }
            }

            if (day2[i].toString().equalsIgnoreCase("free")) {
                day2[i] = new TimeSlots("0");
            } else {
                day2[i] = new TimeSlots("1");
            }
            String day1BusyCount = day1[i].toString();
            String day2BusyCount = day2[i].toString();
            int totalBusyCount = Integer.parseInt(day1BusyCount) + Integer.parseInt(day2BusyCount);
            String newBusyCount = Integer.toString(totalBusyCount);
            finalDay.add(new TimeSlots(newBusyCount));
        }
        return finalDay;
    }

}



