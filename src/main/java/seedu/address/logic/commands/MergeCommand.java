package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
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
import seedu.address.commons.core.Messages;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.person.*;
import seedu.address.model.enrolledModule.EnrolledModule;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.IsNotSelfOrMergedPredicate;
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

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Merges the timetables of selected people"
            + "by the index number used in the last person listing.\n "
            + "Parameters: INDEX (must be positive integer )"
            + PREFIX_MERGE + "[INDEX] " + PREFIX_NAME + "[GROUP NAME]"
            + "for all timetables you want to merge.\n"
            + "Example: " + COMMAND_WORD + PREFIX_MERGE + "1 " + PREFIX_MERGE + "2 " + PREFIX_NAME + "GES PROJECT";

    public static final String MESSAGE_MERGE_TIMETABLE_SUCCESS = "Timetables Merged";
    public static final String MESSAGE_NOT_MERGED = "At least two people to merge must be provided";
    public static final String MESSAGE_DUPLICATE_GROUP = "Group name already used, please choose another one";

    private final List<String> indices;
    private final Name name;

    public MergeCommand(List<String> indices, String name) {
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


        for (String index : indices) {
            if (Integer.parseInt(index) > lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }

        int i = 0;
        for (String it : indices) {
            int index;
            try {
                index = Integer.parseInt(it) - 1;
            } catch (NumberFormatException nfe) {
                throw new CommandException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        MergeCommand.MESSAGE_USAGE), nfe);
            }
            personsToMerge[i] = mainList.get(index);
            i++;
        }
        personsToMerge[i]= selfList.get(0);
        i++;
        for (int j = 0; j < i - 1; j++) {
            personsToMerge[j + 1] = mergeTimetables(personsToMerge[j], personsToMerge[j + 1], j);
        }
        if (model.hasPerson(personsToMerge[i-1])) {
            throw new CommandException(MESSAGE_DUPLICATE_GROUP);
        }
        model.addPerson(personsToMerge[i - 1]);
        model.commitAddressBook();
        return new CommandResult(MESSAGE_MERGE_TIMETABLE_SUCCESS);

    }


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

    List<TimeSlots> compareTimeSlots(TimeSlots[] day1, TimeSlots[] day2){
        List<TimeSlots> finalDay = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
                if(day1[i].toString().equalsIgnoreCase("free")
                        ||day1[i].toString().equalsIgnoreCase("0")){
                    day1[i] = new TimeSlots("0");
                }
                else{
                    try
                    {
                        Integer.parseInt(day1[i].toString());
                    }
                    catch (NumberFormatException e)
                    {
                        day1[i] = new TimeSlots("1");
                    }
                }

            if(day2[i].toString().equalsIgnoreCase("free")){
                day2[i] = new TimeSlots("0");
            }
            else{
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



