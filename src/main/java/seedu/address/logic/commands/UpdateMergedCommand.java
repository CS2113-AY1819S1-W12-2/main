package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.IsMergedPredicate;
import seedu.address.model.person.IsNotSelfOrMergedPredicate;
import seedu.address.model.person.IsSelfPredicate;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.TimeSlots;
import seedu.address.model.tag.Tag;

/**
 * Updates all the groups you have with the lastest timetables from the contacts in the group.
 */
public class UpdateMergedCommand extends Command {
    public static final String COMMAND_WORD = "update";

    public static final String MESSAGE_UPDATE_SUCCESS = "Groups updated";

    public static final String MESSAGE_UPDATE_SUCCESS_WITH_REMOVED_PERSONS = "Groups updated. \nContacts who were in"
            + " " + "groups were detected to have been deleted.\nList of deleted contacts and affected groups: \n";

    public static final String MESSAGE_UPDATE_SUCCESS_WITH_REMOVED_GROUPS = "Some groups have been deleted because "
            + "all members except yourself have been removed from your contacts: ";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        List<Person> filteredPersonList = model.getFilteredPersonList();
        List<Person> mergedList = ((ObservableList<Person>) filteredPersonList).filtered(new IsMergedPredicate());
        List<Person> mainList = ((ObservableList<Person>) filteredPersonList).filtered
                (new IsNotSelfOrMergedPredicate());
        Map<String, ArrayList<String>> removedPersons = new HashMap<>();
        List<String> removedGroups = new ArrayList<>();

        for (int l = 0; l < mergedList.size(); l++) {
            boolean personCheck = false;
            Person merged = mergedList.get(l);
            Address people = merged.getAddress();
            Name groupName = merged.getName();

            String groupNameString = groupName.toString();
            String peopleString = people.toString();
            peopleString = peopleString.trim();

            String[] persons = peopleString.split("\\s*(=>|,|\\s)\\s");
            Person[] personsToMerge = new Person[persons.length + 1];

            int i = 0;
            for (String name : persons) {
                String[] splitName = name.split("\\s+");
                if (!name.equalsIgnoreCase("self")) {
                    List<String> getPerson = new ArrayList<>(Arrays.asList(splitName));
                    List<Person> singlePersonList = ((FilteredList<Person>) mainList).filtered
                            (new NameContainsKeywordsPredicate(getPerson));
                    if (singlePersonList.size() < 1) {
                        if (removedPersons.get(name) == null) {
                            removedPersons.put(name, new ArrayList<>());
                        }
                        removedPersons.get(name).add(groupNameString);
                        continue;
                    }
                    Person person = singlePersonList.get(0);
                    personsToMerge[i] = person;
                    personCheck = true;
                    i++;
                }
            }

            if (!personCheck) {
                model.deletePerson(merged);
                removedGroups.add(groupNameString);
            }

            List<Person> selfList = ((ObservableList<Person>) filteredPersonList).filtered(new IsSelfPredicate());
            personsToMerge[i] = selfList.get(0);
            i++;
            for (int j = 0; j < i - 1; j++) {
                personsToMerge[j + 1] = mergeTimetables(personsToMerge[j], personsToMerge[j + 1], j, groupName);
            }
            model.updatePerson(personsToMerge[i - 1], personsToMerge[i - 1]);
        }
        model.commitAddressBook();
        if (!removedPersons.isEmpty()) {
            String outputPersons = createCorrectOutput(removedPersons);
            if (!removedGroups.isEmpty()) {
                String outputGroups = createCorrectOutputGroups(removedGroups);
                return new CommandResult(MESSAGE_UPDATE_SUCCESS_WITH_REMOVED_PERSONS + outputPersons
                        + MESSAGE_UPDATE_SUCCESS_WITH_REMOVED_GROUPS + outputGroups);
            }

            return new CommandResult(MESSAGE_UPDATE_SUCCESS_WITH_REMOVED_PERSONS + outputPersons);
        } else {
            return new CommandResult(MESSAGE_UPDATE_SUCCESS);
        }

    }

    /**
     * Merges 2 people into a single person with a merged timetable
     */
    private Person mergeTimetables(Person person1, Person person2, int index, Name name) {
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
        Map<String, seedu.address.model.enrolledmodule.EnrolledModule> enrolledClassMap = new TreeMap<>();


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

    /**
     * Takes a map of the contacts who have been deleted and the groups affected by their deletion and returns the
     * correct output
     */
    String createCorrectOutput(Map<String, ArrayList<String>> removedPersons) {
        String output = "";
        Iterator<Map.Entry<String, ArrayList<String>>> it = removedPersons.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<String>> removedName = it.next();
            output = output + removedName.getKey() + ":" + " ";
            ArrayList<String> removedModules = removedName.getValue();
            for (String affectedGroup : removedModules) {
                if (affectedGroup.equalsIgnoreCase(removedModules.get(removedModules.size() - 1))) {
                    output = output + affectedGroup + "\n";
                } else {
                    output = output + affectedGroup + ", ";
                }
            }

        }
        return output;
    }

    /**
     * Takes a list of groups that have been deleted and returns the correct output
     */

    String createCorrectOutputGroups(List<String> removedGroups) {
        String output = "";
        for (String it : removedGroups) {
            if (!it.equalsIgnoreCase(removedGroups.get(removedGroups.size() - 1))) {
                output = output + it + ", ";
            } else {
                output = output + it;
            }
        }
        return output;
    }
}
