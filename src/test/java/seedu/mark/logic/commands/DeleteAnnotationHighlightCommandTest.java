package seedu.mark.logic.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static seedu.mark.testutil.Assert.assertThrows;
import static seedu.mark.testutil.TypicalIndexes.INDEX_FIRST_BOOKMARK;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import seedu.mark.commons.core.index.Index;
import seedu.mark.commons.exceptions.IllegalValueException;
import seedu.mark.logic.commands.exceptions.CommandException;
import seedu.mark.model.ModelStub;
import seedu.mark.model.annotation.Annotation;
import seedu.mark.model.annotation.AnnotationNote;
import seedu.mark.model.annotation.Highlight;
import seedu.mark.model.annotation.OfflineDocument;
import seedu.mark.model.annotation.Paragraph;
import seedu.mark.model.annotation.ParagraphContent;
import seedu.mark.model.annotation.ParagraphIdentifier;
import seedu.mark.model.annotation.PhantomParagraph;
import seedu.mark.model.annotation.TrueParagraph;
import seedu.mark.model.bookmark.Bookmark;
import seedu.mark.model.bookmark.CachedCopy;
import seedu.mark.model.bookmark.util.BookmarkBuilder;
import seedu.mark.storage.StorageStub;

class DeleteAnnotationHighlightCommandTest {

    @Test
    public void execute_invalidPid_throwsCommandException() {
        Bookmark validBookmark = new BookmarkBuilder().withUrl("http://anyurl")
                .withCachedCopy(new CachedCopyStub(new TrueParagraph(Index.fromOneBased(1),
                        new ParagraphContent("lupus")))).build();
        ModelStubAcceptingBookmarkAdded modelStub = new ModelStubAcceptingBookmarkAdded(validBookmark);

        assertThrows(CommandException.class,
                DeleteAnnotationCommand.COMMAND_WORD + ": condition hit for test case", () ->
                        new DeleteAnnotationHighlightCommand(INDEX_FIRST_BOOKMARK,
                                ParagraphIdentifier.makeExistId(Index.fromOneBased(10)))
                                .execute(modelStub, new StorageStub()));
    }

    @Test
    public void execute_noAnnotationToRemove_throwsCommandException() {
        Bookmark validBookmark = new BookmarkBuilder().withUrl("http://anyurl")
                .withCachedCopy(new CachedCopyStub(new TrueParagraph(Index.fromOneBased(1),
                        new ParagraphContent("lupus")))).build();
        ModelStubAcceptingBookmarkAdded modelStub = new ModelStubAcceptingBookmarkAdded(validBookmark);

        assertThrows(CommandException.class,
                DeleteAnnotationCommand.MESSAGE_NOTHING_TO_DELETE, () ->
                        new DeleteAnnotationHighlightCommand(INDEX_FIRST_BOOKMARK,
                                ParagraphIdentifier.makeExistId(Index.fromOneBased(1)))
                                .execute(modelStub, new StorageStub()));
    }

    @Test
    public void execute_removePhantomHighlight_throwsCommandException() {
        Bookmark validBookmark = new BookmarkBuilder().withUrl("http://anyurl")
                .withCachedCopy(new CachedCopyStub(new PhantomParagraph(Index.fromOneBased(1),
                        new Annotation(Highlight.ORANGE, AnnotationNote.SAMPLE_NOTE)))).build();
        ModelStubAcceptingBookmarkAdded modelStub = new ModelStubAcceptingBookmarkAdded(validBookmark);

        assertThrows(CommandException.class,
                DeleteAnnotationHighlightCommand.MESSAGE_PHANTOM, () ->
                        new DeleteAnnotationHighlightCommand(INDEX_FIRST_BOOKMARK,
                                ParagraphIdentifier.makeStrayId(Index.fromOneBased(1)))
                                .execute(modelStub, new StorageStub()));
    }

    @Test
    public void execute_removeTrueHasAnnotation_success() {
        Paragraph p = new TrueParagraph(Index.fromOneBased(1), new ParagraphContent("content"));
        p.addAnnotation(new Annotation(Highlight.ORANGE, AnnotationNote.SAMPLE_NOTE));

        Bookmark validBookmark = new BookmarkBuilder().withUrl("http://anyurl")
                .withCachedCopy(new CachedCopyStub(p)).build();
        ModelStubAcceptingBookmarkAdded modelStub = new ModelStubAcceptingBookmarkAdded(validBookmark);

        assertDoesNotThrow(() -> new DeleteAnnotationHighlightCommand(INDEX_FIRST_BOOKMARK,
                ParagraphIdentifier.makeExistId(Index.fromOneBased(1)))
                .execute(modelStub, new StorageStub()));
    }


    private class ModelStubAcceptingBookmarkAdded extends ModelStub {
        private Bookmark setBookmark = null;

        ModelStubAcceptingBookmarkAdded(Bookmark bookmark) {
            setBookmark = bookmark;
        }

        @Override
        public void setBookmark(Bookmark target, Bookmark editedBookmark) {
            setBookmark = editedBookmark;
        }

        @Override
        public void saveMark(String message) {
            // phantom save just for testing
        }

        @Override
        public void updateDocument(OfflineDocument doc) {
            // phantom update for testing
        }

        @Override
        public ObservableList<Bookmark> getFilteredBookmarkList() {
            return FXCollections.observableList(List.of(setBookmark));
        }

        @Override
        public void updateCurrentDisplayedCache(Bookmark bookmarkToDisplayCache) {
            // phantom update just for testing
        }

        @Override
        public void setOfflineDocNameCurrentlyShowing(String name) {
            // valid set doc title
        }
    }

    private class CachedCopyStub extends CachedCopy {
        private OfflineDocumentStub doc;

        CachedCopyStub(Paragraph p) {
            super("");
            doc = new OfflineDocumentStub(p);
        }

        @Override
        public OfflineDocument getAnnotations() {
            return doc;
        }
    }

    private class OfflineDocumentStub extends OfflineDocument {
        private Paragraph p;

        OfflineDocumentStub(Paragraph p) {
            super("");
            this.p = p;
        }

        @Override
        public OfflineDocument copy() {
            return this;
        }

        @Override
        public void addAnnotation(ParagraphIdentifier pid, Annotation an) {
            //phantom add annotation for AddAnnotationCommand to call
        }

        @Override
        public Paragraph getParagraph(ParagraphIdentifier pid) throws IllegalValueException {
            if (pid.equals(ParagraphIdentifier.makeExistId(Index.fromOneBased(10)))) {
                throw new IllegalValueException("condition hit for test case");
            }
            return this.p;
        }

        @Override
        public void removePhantom(ParagraphIdentifier pid) {
            //phantom for test
        }

        @Override
        public void loadAnnotations(HashMap annotations) {
            assert false : "this method should not be called.";
        }

        @Override
        public void updateStrayIndex() {
            assert false : "this method should not be called.";
        }

        @Override
        public List<Paragraph> getCollection() {
            assert false : "this method should not be called.";
            return null;
        }

        @Override
        public boolean hasParagraph(ParagraphIdentifier pid) {
            assert false : "this method should not be called.";
            return false;
        }

        @Override
        public int getNumStrayNotes() {
            assert false : "this method should not be called.";
            return 0;
        }

        @Override
        public void addPhantom(Annotation an) {
            //
        }
    }

}
