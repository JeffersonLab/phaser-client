package org.jlab.phaser.swing.chooser;

/**
 * A "chooser" widget template for choosing a generic element of type E.
 *
 * <p>A "chooser" is a concept borrowed from Swing classes like JColorChooser and JFileChooser.
 *
 * @author ryans
 * @param <E> Generic element type
 */
public abstract class Chooser<E> {
  /** CANCEL */
  public static final int CANCEL_OPTION = 0;

  /** APPROVE */
  public static final int APPROVE_OPTION = 1;

  private final ChooserDialog<E> dialog;

  /**
   * Create a new Chooser widget with the corresponding chooser dialog.
   *
   * @param dialog The ChooserDialog
   */
  public Chooser(ChooserDialog<E> dialog) {
    this.dialog = dialog;
  }

  /**
   * Show the chooser dialog and wait for user approval or cancel.
   *
   * @return Either Chooser.APPROVE_OPTION or Chooser.CANCEL_OPTION
   */
  public int showChooserDialog() {
    dialog.centerDialogOverParent();

    // This method will block as dialog is modal
    dialog.setVisible(true);

    int retval = CANCEL_OPTION;

    if (dialog.isOkPressed()) {
      retval = APPROVE_OPTION;
    }

    return retval;
  }

  /**
   * Return the selected element.
   *
   * @return The selected element or null if none
   */
  public E getSelected() {
    E selected = null;

    if (dialog != null) {
      selected = dialog.getSelected();
    }

    return selected;
  }

  /**
   * Set the initially selected element.
   *
   * @param selected The initially selected element
   */
  public void setInitiallySelected(E selected) {
    if (dialog != null) {
      dialog.setInitiallySelected(selected);
    }
  }
}
