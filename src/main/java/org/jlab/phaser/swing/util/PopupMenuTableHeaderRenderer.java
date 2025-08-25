package org.jlab.phaser.swing.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * A custom JTable colum header renderer to show a menu button with a pop-up dialog for showing such
 * things as "Select All" and "Select None" menu items.
 *
 * @author ryans
 */
public class PopupMenuTableHeaderRenderer extends JPanel implements TableCellRenderer {

  private int column = -1;
  private JTable table = null;
  private final MenuButton button;
  private final JPopupMenu menu;

  /**
   * Private Constructor since we are using a factory method to obtain instance.
   *
   * @param menu the JPopupMenu
   */
  private PopupMenuTableHeaderRenderer(JPopupMenu menu) {
    super(new BorderLayout());

    ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/south.png"));

    button = MenuButton.create(icon, menu);
    this.menu = menu;
  }

  /** Initialize the widget (called from factory method, not constructor). */
  private void init() {
    button.setPreferredSize(new Dimension(25, 25));
    JPanel wrapper = new JPanel();
    wrapper.setLayout(new FlowLayout());
    wrapper.add(button);
    setLayout(new BorderLayout());
    wrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(wrapper, BorderLayout.WEST);

    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
  }

  /**
   * Factory method instead of constructor is used to obtain instance since we would otherwise be
   * calling overridable method in the constructor.
   *
   * @param menu The JPopupMenu
   * @return The PopupMenuTableHeaderRenderer
   */
  public static PopupMenuTableHeaderRenderer create(JPopupMenu menu) {
    PopupMenuTableHeaderRenderer renderer = new PopupMenuTableHeaderRenderer(menu);
    renderer.init();

    return renderer;
  }

  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, final int r, final int c) {
    if (table != null && this.table != table) {
      this.table = table;
      final JTableHeader header = table.getTableHeader();
      if (header != null) {
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());

        header.addMouseListener(
            new MouseAdapter() {

              @Override
              public void mouseClicked(MouseEvent e) {
                int col = header.getTable().columnAtPoint(e.getPoint());
                if (col != column || col == -1) {
                  return;
                }

                int index = header.getColumnModel().getColumnIndexAtX(e.getPoint().x);
                if (index == -1) {
                  return;
                }

                setBounds(header.getHeaderRect(index));
                header.add(PopupMenuTableHeaderRenderer.this);
                validate();

                button.doClick();

                header.remove(PopupMenuTableHeaderRenderer.this);

                header.repaint();
              }
            });

        menu.addPopupMenuListener(
            new PopupMenuListener() {
              @Override
              public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

              @Override
              public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                button.setSelected(false);
                header.repaint();
              }

              @Override
              public void popupMenuCanceled(PopupMenuEvent e) {}
            });
      }
    }
    column = c;
    return this;
  }
}

/**
 * A custom menu button, which is just a JToggleButton that shows a JPopupMenu when you click the
 * button.
 *
 * @author ryans
 */
class MenuButton extends JToggleButton {

  /**
   * Private Constructor since we are using a factory method to obtain instance.
   *
   * @param icon The icon to use
   */
  private MenuButton(ImageIcon icon) {
    super(icon);
  }

  /**
   * Factory method instead of constructor is used to obtain instance since we would otherwise be
   * calling overridable method in the constructor.
   *
   * @param icon The icon to use
   * @param popup The JPopupMenu
   * @return The MenuButton
   */
  public static MenuButton create(ImageIcon icon, final JPopupMenu popup) {
    final MenuButton button = new MenuButton(icon);

    button.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ev) {
            if (button.isSelected()) {
              popup.show(button, 0, button.getBounds().height);
            } else {
              popup.setVisible(false);
            }
          }
        });

    return button;
  }
}
