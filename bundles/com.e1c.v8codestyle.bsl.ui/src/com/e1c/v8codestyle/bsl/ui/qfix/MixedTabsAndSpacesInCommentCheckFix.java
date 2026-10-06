package com.e1c.v8codestyle.bsl.ui.qfix;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.resource.XtextResource;

import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

/**
 * The fix for {@link MixedTabsAndSpacesInCommentCheck}
 * 
 * @author Artem Samohvalov
 */
@QuickFix(checkId = "mixed-tabs-and-spaces-in-comment", supplierId = "com.e1c.v8codestyle.bsl")
public class MixedTabsAndSpacesInCommentCheckFix
    extends SingleVariantXtextBslModuleFix
{
    private static final int COMMENT_PREFIX_LENGTH = 2;
    private static final int TABULATION_SPACE_COUNT = 4;
    private static final String TABULATION_SPACE = " ".repeat(TABULATION_SPACE_COUNT); //$NON-NLS-1$

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.MixedTabsAndSpacesInCommentCheckFix_Description)
            .details(Messages.MixedTabsAndSpacesInCommentCheckFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        int lineNumber = model.getIssue().getLineNumber() - 1;

        IDocument document = model.getDocument();
        IRegion lineInfo = document.getLineInformation(lineNumber);
        String line = document.get(lineInfo.getOffset(), lineInfo.getLength());

        String newIndent = getNormalizedIndent(line);
        int oldIndentLength = getIndentLength(line);

        return new ReplaceEdit(lineInfo.getOffset() + COMMENT_PREFIX_LENGTH, oldIndentLength, newIndent);
    }

    private String getNormalizedIndent(String line)
    {
        StringBuilder indent = new StringBuilder();

        for (int i = COMMENT_PREFIX_LENGTH; i < line.length(); ++i)
        {
            char c = line.charAt(i);

            if (c == ' ')
            {
                indent.append(' ');
            }
            else if (c == '\t')
            {
                indent.append(TABULATION_SPACE);
            }
            else
            {
                break;
            }
        }

        return indent.toString();
    }

    private int getIndentLength(String line)
    {
        int i = COMMENT_PREFIX_LENGTH;

        while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t'))
        {
            ++i;
        }

        return i - COMMENT_PREFIX_LENGTH;
    }
}
