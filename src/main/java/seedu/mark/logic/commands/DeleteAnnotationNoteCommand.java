package seedu.mark.logic.commands;

import seedu.mark.commons.core.index.Index;
import seedu.mark.logic.commands.exceptions.CommandException;
import seedu.mark.logic.commands.results.CommandResult;
import seedu.mark.logic.commands.results.OfflineCommandResult;
import seedu.mark.model.Model;
import seedu.mark.model.annotation.AnnotationNote;
import seedu.mark.model.annotation.OfflineDocument;
import seedu.mark.model.annotation.Paragraph;
import seedu.mark.model.annotation.ParagraphIdentifier;
import seedu.mark.storage.Storage;

public class DeleteAnnotationNoteCommand extends DeleteAnnotationCommand {

    public static final String MESSAGE_SUCCESS =
            "Note successfully removed from paragraph %1$s:\nNote \"%2$s\"";

    public DeleteAnnotationNoteCommand(Index index, ParagraphIdentifier pid) {
        super(index, pid, true, false);
    }


    @Override
    public CommandResult execute(Model model, Storage storage) throws CommandException {
        OfflineDocument doc = getRequiredDoc(model);
        Paragraph p = doc.getParagraph(getPid());

        if (!p.hasAnnotation() || !p.hasNote()) {
            throw new CommandException(DeleteAnnotationCommand.MESSAGE_NOTHING_TO_DELETE);
        }

        AnnotationNote note = p.removeNote();
        if (!p.isTrueParagraph()) {
            doc.removePhantom(getPid());
        }

        model.updateDocument(doc);

        model.saveMark(String.format(MESSAGE_SUCCESS, getPid(), note));
        return new OfflineCommandResult(String.format(MESSAGE_SUCCESS, getPid(), note));
    }
}
