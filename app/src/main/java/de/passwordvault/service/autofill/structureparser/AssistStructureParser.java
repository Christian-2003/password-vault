package de.passwordvault.service.autofill.structureparser;

import android.app.assist.AssistStructure;
import android.view.View;


/**
 * Class models a parser which can parse an {@linkplain AssistStructure} and generate a
 * {@link ParsedStructure} as a result.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class AssistStructureParser {

    /**
     * Attribute stores the parsed assist structure.
     */
    private ParsedStructure result;


    /**
     * Constructor instantiates a new assist structure parser.
     */
    public AssistStructureParser() {
        result = null;
    }


    /**
     * Method parses the passed assist structure and returns a parsed structure as result. The
     * parsed structure contains the app package name as well as IDs for the remote views in which
     * to insert username and password (if found).
     *
     * @param structure             Assist structure to be parsed.
     * @return                      Parsed structure.
     * @throws NullPointerException The passed assist structure is {@code null}.
     */
    public ParsedStructure parse(AssistStructure structure) throws NullPointerException {
        if (structure == null) {
            throw new NullPointerException();
        }
        result = new ParsedStructure(structure.getActivityComponent().getPackageName());

        int nodeCount = structure.getWindowNodeCount();
        for (int i = 0; i < nodeCount; i++) {
            AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
            AssistStructure.ViewNode viewNode = windowNode.getRootViewNode();
            parseNode(viewNode);
        }

        return result;
    }


    /**
     * Method parses the passed view node and it's child nodes recursively.
     *
     * @param viewNode  View node to be parsed.
     */
    private void parseNode(AssistStructure.ViewNode viewNode) {
        if (viewNode == null) {
            return;
        }

        if (viewNode.getAutofillHints() != null && viewNode.getAutofillHints().length > 0) {
            String[] autofillHints = viewNode.getAutofillHints();
            for (String autofillHint : autofillHints) {
                if (autofillHint.contains(View.AUTOFILL_HINT_PASSWORD)) {
                    result.setPasswordId(viewNode.getAutofillId());
                    result.setPasswordHint(View.AUTOFILL_HINT_PASSWORD);
                    result.setPasswordText(viewNode.getText() != null ? viewNode.getText().toString() : null);
                    break;
                }
                else if (autofillHint.contains(View.AUTOFILL_HINT_EMAIL_ADDRESS)) {
                    result.setUsernameId(viewNode.getAutofillId());
                    result.setUsernameHint(View.AUTOFILL_HINT_EMAIL_ADDRESS);
                    result.setUsernameText(viewNode.getText() != null ? viewNode.getText().toString() : null);
                    break;
                }
                else if (autofillHint.contains(View.AUTOFILL_HINT_USERNAME)) {
                    result.setUsernameId(viewNode.getAutofillId());
                    result.setUsernameHint(View.AUTOFILL_HINT_USERNAME);
                    result.setUsernameText(viewNode.getText() != null ? viewNode.getText().toString() : null);
                    break;
                }
            }
        }

        int childNodeCount = viewNode.getChildCount();
        for (int i = 0; i < childNodeCount; i++) {
            AssistStructure.ViewNode childNode = viewNode.getChildAt(i);
            parseNode(childNode);
        }
    }

}
