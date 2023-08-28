package org.jlab.phaser.swing.document;

import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A text document model for holding a number representation of year.
 * 
 * @author ryans
 */
public class YearDocument extends PlainDocument {

    private final Pattern pattern = Pattern.compile("\\d{0,4}");

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str != null && !str.isEmpty()) {
            String wouldbe = this.getText(0, this.getLength());
            StringBuilder builder = new StringBuilder(wouldbe);
            builder.insert(offs, str);
            if (pattern.matcher(builder.toString()).matches()) {
                super.insertString(offs, str, a);
            }
        }
    }
}
