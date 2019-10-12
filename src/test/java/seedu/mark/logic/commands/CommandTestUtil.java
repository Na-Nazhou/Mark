package seedu.mark.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_FOLDER;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_PARENT_FOLDER;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_URL;
import static seedu.mark.testutil.Assert.assertThrows;
import static seedu.mark.testutil.TypicalIndexes.INDEX_FIRST_BOOKMARK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.mark.commons.core.index.Index;
import seedu.mark.logic.commands.commandresult.CommandResult;
import seedu.mark.logic.commands.exceptions.CommandException;
import seedu.mark.model.Mark;
import seedu.mark.model.Model;
import seedu.mark.model.bookmark.Bookmark;
import seedu.mark.model.predicates.NameContainsKeywordsPredicate;
import seedu.mark.testutil.EditBookmarkDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_URL_AMY = "https://amy-example.com";
    public static final String VALID_URL_BOB = "https://bob-example.com";
    public static final String VALID_REMARK_AMY = "Block 312, Amy Street 1";
    public static final String VALID_REMARK_BOB = "Block 123, Bobby Street 3";
    public static final String VALID_TAG_HUSBAND = "husband";
    public static final String VALID_TAG_FRIEND = "friend";
    public static final String VALID_FOLDER_CONTACTS = "contacts";
    public static final String VALID_FOLDER_CS2103T = "CS2103T";
    public static final String VALID_FOLDER_CS2101 = "CS2101";

    public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
    public static final String URL_DESC_AMY = " " + PREFIX_URL + VALID_URL_AMY;
    public static final String URL_DESC_BOB = " " + PREFIX_URL + VALID_URL_BOB;
    public static final String REMARK_DESC_AMY = " " + PREFIX_REMARK + VALID_REMARK_AMY;
    public static final String REMARK_DESC_BOB = " " + PREFIX_REMARK + VALID_REMARK_BOB;
    public static final String PARENT_FOLDER_DESC_CS2103T = " " + PREFIX_PARENT_FOLDER + VALID_FOLDER_CS2103T;
    public static final String FOLDER_DESC_CS2103T = " " + PREFIX_FOLDER + VALID_FOLDER_CS2103T;
    public static final String FOLDER_DESC_CS2101 = " " + PREFIX_FOLDER + VALID_FOLDER_CS2101;
    public static final String TAG_DESC_FRIEND = " " + PREFIX_TAG + VALID_TAG_FRIEND;
    public static final String TAG_DESC_HUSBAND = " " + PREFIX_TAG + VALID_TAG_HUSBAND;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME + "James&"; // '&' not allowed in names
    public static final String INVALID_URL_DESC = " " + PREFIX_URL + "bob??yahoo"; // double '?'
    public static final String INVALID_REMARK_DESC = " " + PREFIX_REMARK + "t/ means tag"; // '/' not allowed in remarks
    public static final String INVALID_TAG_DESC = " " + PREFIX_TAG + "hubby*"; // '*' not allowed in tags
    public static final String INVALID_FOLDER_DESC = " " + PREFIX_FOLDER + "fold#er"; // # not allowed in folders
    public static final String INVALID_PARENT_FOLDER_DESC = " " + PREFIX_PARENT_FOLDER + "fold^er";

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditBookmarkDescriptor DESC_AMY;
    public static final EditCommand.EditBookmarkDescriptor DESC_BOB;

    static {
        DESC_AMY = new EditBookmarkDescriptorBuilder().withName(VALID_NAME_AMY)
                .withUrl(VALID_URL_AMY).withRemark(VALID_REMARK_AMY)
                .withTags(VALID_TAG_FRIEND).build();
        DESC_BOB = new EditBookmarkDescriptorBuilder().withName(VALID_NAME_BOB)
                .withUrl(VALID_URL_BOB).withRemark(VALID_REMARK_BOB)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the mark model, filtered bookmark list and selected bookmark in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        Mark expectedMark = new Mark(actualModel.getMark());
        List<Bookmark> expectedFilteredList = new ArrayList<>(actualModel.getFilteredBookmarkList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedMark, actualModel.getMark());
        assertEquals(expectedFilteredList, actualModel.getFilteredBookmarkList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the bookmark at the given {@code targetIndex} in the
     * {@code model}'s mark.
     */
    public static void showBookmarkAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredBookmarkList().size());

        Bookmark bookmark = model.getFilteredBookmarkList().get(targetIndex.getZeroBased());
        final String[] splitName = bookmark.getName().fullName.split("\\s+");
        model.updateFilteredBookmarkList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredBookmarkList().size());
    }

    /**
     * Deletes the bookmark at the given {@code targetIndex} in the {@code model}'s mark.
     */
    public static void deleteBookmarkAtIndex(Model model, Index targetIndex) {
        int initialSize = model.getFilteredBookmarkList().size();
        assertTrue(targetIndex.getZeroBased() < initialSize);

        Bookmark bookmarkToDelete = model.getFilteredBookmarkList().get(targetIndex.getZeroBased());
        model.deleteBookmark(bookmarkToDelete);
        model.saveMark();

        assertEquals(initialSize - 1, model.getFilteredBookmarkList().size());
    }

    /**
     * Deletes the first bookmark in {@code model}'s mark.
     */
    public static void deleteFirstBookmark(Model model) {
        deleteBookmarkAtIndex(model, INDEX_FIRST_BOOKMARK);
    }

}
