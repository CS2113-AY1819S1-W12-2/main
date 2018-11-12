package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalModuleCodes.getTypicalNotesDownloaded;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.IsMergedPredicate;
import seedu.address.model.person.IsNotSelfOrMergedPredicate;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.TimeSlots;
import seedu.address.testutil.MergedBuilder;

public class UpdateMergedCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalNotesDownloaded(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(),
            getTypicalNotesDownloaded(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();
    private String groupName = "test";

    @Before
    public void setUp() {
        List<Person> filteredPersonList = model.getFilteredPersonList();
        List<Person> mainList = ((ObservableList<Person>) filteredPersonList)
                .filtered(new IsNotSelfOrMergedPredicate());
        List<Integer> indices = new ArrayList<>();
        List<Person> personsToMerge = new ArrayList<>();

        indices.add(INDEX_FIRST_PERSON.getZeroBased());
        indices.add(mainList.size() - 1);

        personsToMerge.add(mainList.get(0));
        personsToMerge.add(mainList.get(mainList.size() - 1));

        MergedBuilder mergedBuilder = new MergedBuilder(personsToMerge, groupName);
        Person newGroup = mergedBuilder.getMergedPerson();

        expectedModel.addPerson(newGroup);
        model.addPerson(newGroup);
    }

    @Test
    public void execute_updateAfterTimetableChange_success() {
        List<Person> filteredPersonList = expectedModel.getFilteredPersonList();
        List<Person> mainList = ((ObservableList<Person>) filteredPersonList)
                .filtered(new IsNotSelfOrMergedPredicate());
        List<Person> mergedList = ((ObservableList<Person>) filteredPersonList).filtered(new IsMergedPredicate());
        Person personToChange = mainList.get(INDEX_FIRST_PERSON.getZeroBased());

        Map<String, List<TimeSlots>> timetableToChange = personToChange.getTimeSlots();
        List<TimeSlots> dayToChange = timetableToChange.get("mon");
        dayToChange.set(1, new TimeSlots("test"));
        personToChange = mergedList.get(0);
        timetableToChange = personToChange.getTimeSlots();
        dayToChange = timetableToChange.get("mon");
        TimeSlots changedTimeSlot = dayToChange.get(1);
        String newBusyNum = changedTimeSlot.toString();
        int newBusyNumInt = Integer.parseInt(newBusyNum);

        changedTimeSlot = new TimeSlots(Integer.toString(newBusyNumInt));
        dayToChange.set(1, changedTimeSlot);

        expectedModel.commitAddressBook();
        assertUpdateTimeSlotSuccess();
    }

    @Test
    public void execute_updateAfterDeletionChange_success() {
        List<Person> filteredPersonList = expectedModel.getFilteredPersonList();
        List<Person> mainList = ((ObservableList<Person>) filteredPersonList)
                .filtered(new IsNotSelfOrMergedPredicate());
        List<Person> mergedList = ((ObservableList<Person>) filteredPersonList).filtered(new IsMergedPredicate());
        List<Person> personsToMerge = new ArrayList<>();
        Map<String, List<String>> removedPersons = new HashMap<>();

        Person personToDelete = mainList.get(mainList.size() - 1);
        Person groupToUpdate = mergedList.get(0);

        personsToMerge.add(mainList.get(INDEX_FIRST_PERSON.getZeroBased()));
        new MergedBuilder(personsToMerge, groupName);

        model.deletePerson(personToDelete);
        expectedModel.deletePerson(personToDelete);
        expectedModel.commitAddressBook();

        List<String> affectedModules = new ArrayList<>();
        Name deletedPersonName = personToDelete.getName();
        Name affectedModuleName = groupToUpdate.getName();
        affectedModules.add(affectedModuleName.toString());
        removedPersons.put(deletedPersonName.toString(), affectedModules);
        String output = createCorrectOutput(removedPersons);

        assertUpdateDeletionSuccess(output);
    }

    @Test
    public void execute_updateAfterDeletionAllMembersOfGroup_success() {
        List<Person> filteredPersonList = expectedModel.getFilteredPersonList();
        List<Person> mainList = ((ObservableList<Person>) filteredPersonList)
                .filtered(new IsNotSelfOrMergedPredicate());
        List<Person> mergedList = ((ObservableList<Person>) filteredPersonList).filtered(new IsMergedPredicate());
        List<Person> personsToMerge = new ArrayList<>();
        Map<String, List<String>> removedPersons = new HashMap<>();

        Person personToDelete1 = mainList.get(mainList.size() - 1);
        Person personToDelete2 = mainList.get(INDEX_FIRST_PERSON.getZeroBased());
        Person groupToUpdate = mergedList.get(0);


        model.deletePerson(personToDelete1);
        model.deletePerson(personToDelete2);
        expectedModel.deletePerson(personToDelete1);
        expectedModel.deletePerson(personToDelete2);
        expectedModel.deletePerson(groupToUpdate);
        expectedModel.commitAddressBook();

        List<String> affectedModules1 = new ArrayList<>();
        Name deletedPersonName1 = personToDelete1.getName();
        Name affectedModuleName = groupToUpdate.getName();
        affectedModules1.add(affectedModuleName.toString());
        removedPersons.put(deletedPersonName1.toString(), affectedModules1);
        List<String> affectedModules2 = new ArrayList<>();
        Name deletedPersonName2 = personToDelete2.getName();
        affectedModules2.add(affectedModuleName.toString());
        removedPersons.put(deletedPersonName2.toString(), affectedModules2);

        String output = createCorrectOutput(removedPersons);

        assertUpdateDeletionGroupSuccess(output, affectedModuleName.toString());
    }

    /**
     * Checks that executing update command after the time slot of a contact has changed causes model to be equal to
     * expected model.
     */
    private void assertUpdateTimeSlotSuccess() {
        UpdateMergedCommand updateMergedCommand = new UpdateMergedCommand();
        String expectedMessage = String.format(updateMergedCommand.MESSAGE_UPDATE_SUCCESS);

        assertCommandSuccess(updateMergedCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    /**
     * Checks that executing update command after a contact has been deleted causes model to be equal to
     * expected model.
     */
    private void assertUpdateDeletionSuccess(String affectedGroupsOutput) {
        UpdateMergedCommand updateMergedCommand = new UpdateMergedCommand();
        String expectedMessage = UpdateMergedCommand.MESSAGE_UPDATE_SUCCESS_WITH_REMOVED_PERSONS + affectedGroupsOutput;

        assertCommandSuccess(updateMergedCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    /**
     * Checks that executing update command after a group deletion is caused by a contact deletion causes
     * model to be
     * equal to
     * expected model.
     */
    private void assertUpdateDeletionGroupSuccess(String affectedGroupsOutput, String deletedGroupsOutput) {
        UpdateMergedCommand updateMergedCommand = new UpdateMergedCommand();
        String expectedMessage =
                UpdateMergedCommand.MESSAGE_UPDATE_SUCCESS_WITH_REMOVED_PERSONS + affectedGroupsOutput
                        + UpdateMergedCommand.MESSAGE_UPDATE_SUCCESS_WITH_REMOVED_GROUPS + deletedGroupsOutput;

        assertCommandSuccess(updateMergedCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    /**
     * Takes a map of the contacts who have been deleted and the groups affected by their deletion and returns the
     * correct output
     */
    String createCorrectOutput(Map<String, List<String>> removedPersons) {
        String output = "";
        Iterator<Map.Entry<String, List<String>>> it = removedPersons.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> removedName = it.next();
            output = output + removedName.getKey() + ":" + " ";
            List<String> removedModules = removedName.getValue();
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
}
