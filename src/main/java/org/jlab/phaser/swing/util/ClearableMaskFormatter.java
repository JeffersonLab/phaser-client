package org.jlab.phaser.swing.util;

import java.text.ParseException;
import javax.swing.text.MaskFormatter;

/**
 * A MaskFormatter that allows users to clear it.
 *
 * Inspired by http://www.javalobby.org/java/forums/t48652.html
 *
 * @author ryans
 */
public class ClearableMaskFormatter extends MaskFormatter {

    private boolean allowBlankField = true;
    private String blankRepresentation;

    /**
     * Create a new ClearableMaskFormatter.
     */
    public ClearableMaskFormatter() {
        super();
    }

    /**
     * Create a new ClearableMaskFormatter with the specified mask format.
     *
     * @param mask The mask format
     * @throws ParseException If unable to parse the mask format
     */
    public ClearableMaskFormatter(String mask) throws ParseException {
        super(mask);
    }

    /**
     * Set whether or not to allow a blank fields.
     *
     * @param allowBlankField true to allow a blank field, false otherwise
     */
    public void setAllowBlankField(boolean allowBlankField) {
        this.allowBlankField = allowBlankField;
    }

    /**
     * Return whether a blank field is allowed.
     *
     * @return true if a blank field is allowed, false otherwise
     */
    public boolean isAllowBlankField() {
        return allowBlankField;
    }

    @Override
    public void setMask(String mask) throws ParseException {
        super.setMask(mask);
        updateBlankRepresentation();
    }

    @Override
    public void setPlaceholderCharacter(char placeholder) {
        super.setPlaceholderCharacter(placeholder);
        updateBlankRepresentation();
    }

    @Override
    public Object stringToValue(String value) throws ParseException {
        Object result;
        if (isAllowBlankField() && blankRepresentation != null && blankRepresentation.equals(value)) {
            result = null;
        } else {
            result = super.stringToValue(value);
        }
        return result;
    }

    /**
     * Update the blank field representation.
     */
    private void updateBlankRepresentation() {
        try {
            blankRepresentation = valueToString(null);
        } catch (ParseException e) {
            blankRepresentation = null;
        }
    }
}
