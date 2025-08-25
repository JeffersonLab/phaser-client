/*
 * Copyright (c) 1997, 2008, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.jlab.phaser.swing.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.SwingConstants;

/**
 * This class is a configurable LayoutManager for a panel of buttons.
 *
 * <p>This class is essentially a copy and paste of
 * javax.swing.plaf.basic.BasicOptionPaneUI.ButtonAreaLayout, but with the private constructor made
 * public so button sizing could be managed.
 */
public class GenericButtonAreaLayout implements LayoutManager {

  private final int topMargin = 17;

  /** syncAllWidths */
  protected boolean syncAllWidths;

  /** Amount of padding */
  protected int padding;

  /** If true, children are lumped together in parent. */
  protected boolean centersChildren;

  /** Orientation */
  private int orientation;

  /** reverseButtons */
  private boolean reverseButtons;

  /**
   * Indicates whether centersChildren should be used vs the orientation. This is done for backward
   * compatability for subclassers.
   */
  private boolean useOrientation;

  /**
   * Create a new GenericButtonAreaLayout.
   *
   * @param syncAllWidths True to syncAllWidths
   * @param padding Amount of padding
   */
  public GenericButtonAreaLayout(boolean syncAllWidths, int padding) {
    this.syncAllWidths = syncAllWidths;
    this.padding = padding;
    centersChildren = true;
    useOrientation = false;
  }

  /**
   * Create a new GenericButtonAreaLayout.
   *
   * @param syncAllWidths True to syncAllWidths
   * @param padding Amount of padding
   * @param orientation Orientation
   * @param reverseButtons True to reverse buttons
   */
  public GenericButtonAreaLayout(
      boolean syncAllWidths, int padding, int orientation, boolean reverseButtons) {
    this(syncAllWidths, padding);
    useOrientation = true;
    this.orientation = orientation;
    this.reverseButtons = reverseButtons;
  }

  public void setSyncAllWidths(boolean newValue) {
    syncAllWidths = newValue;
  }

  public boolean getSyncAllWidths() {
    return syncAllWidths;
  }

  public void setPadding(int newPadding) {
    this.padding = newPadding;
  }

  /**
   * Get padding.
   *
   * @return The padding
   */
  public int getPadding() {
    return padding;
  }

  public void setCentersChildren(boolean newValue) {
    centersChildren = newValue;
    useOrientation = false;
  }

  /**
   * Get centersChildren.
   *
   * @return centersChildren
   */
  public boolean getCentersChildren() {
    return centersChildren;
  }

  private int getOrientation(Container container) {
    if (!useOrientation) {
      return SwingConstants.CENTER;
    }
    if (container.getComponentOrientation().isLeftToRight()) {
      return orientation;
    }
    switch (orientation) {
      case SwingConstants.LEFT:
        return SwingConstants.RIGHT;
      case SwingConstants.RIGHT:
        return SwingConstants.LEFT;
      case SwingConstants.CENTER:
        return SwingConstants.CENTER;
    }
    return SwingConstants.LEFT;
  }

  @Override
  public void addLayoutComponent(String string, Component comp) {}

  @Override
  public void layoutContainer(Container container) {
    Component[] children = container.getComponents();

    if (children != null && children.length > 0) {
      int numChildren = children.length;
      Insets insets = container.getInsets();
      int maxWidth = 0;
      int maxHeight = 0;
      int totalButtonWidth = 0;
      int x = 0;
      int xOffset = 0;
      boolean ltr = container.getComponentOrientation().isLeftToRight();
      boolean reverse = (ltr) ? reverseButtons : !reverseButtons;

      for (int counter = 0; counter < numChildren; counter++) {
        Dimension pref = children[counter].getPreferredSize();
        maxWidth = Math.max(maxWidth, pref.width);
        maxHeight = Math.max(maxHeight, pref.height);
        totalButtonWidth += pref.width;
      }
      if (getSyncAllWidths()) {
        totalButtonWidth = maxWidth * numChildren;
      }
      totalButtonWidth += (numChildren - 1) * padding;

      switch (getOrientation(container)) {
        case SwingConstants.LEFT:
          x = insets.left;
          break;
        case SwingConstants.RIGHT:
          x = container.getWidth() - insets.right - totalButtonWidth;
          break;
        case SwingConstants.CENTER:
          if (getCentersChildren() || numChildren < 2) {
            x = (container.getWidth() - totalButtonWidth) / 2;
          } else {
            x = insets.left;
            if (getSyncAllWidths()) {
              xOffset =
                  (container.getWidth() - insets.left - insets.right - totalButtonWidth)
                          / (numChildren - 1)
                      + maxWidth;
            } else {
              xOffset =
                  (container.getWidth() - insets.left - insets.right - totalButtonWidth)
                      / (numChildren - 1);
            }
          }
          break;
      }

      for (int counter = 0; counter < numChildren; counter++) {
        int index = (reverse) ? numChildren - counter - 1 : counter;
        Dimension pref = children[index].getPreferredSize();

        if (getSyncAllWidths()) {
          children[index].setBounds(x, insets.top + topMargin, maxWidth, maxHeight);
        } else {
          children[index].setBounds(x, insets.top + topMargin, pref.width, pref.height);
        }
        if (xOffset != 0) {
          x += xOffset;
        } else {
          x += children[index].getWidth() + padding;
        }
      }
    }
  }

  @Override
  public Dimension minimumLayoutSize(Container c) {
    if (c != null) {
      Component[] children = c.getComponents();

      if (children != null && children.length > 0) {
        Dimension aSize;
        int numChildren = children.length;
        int height = 0;
        Insets cInsets = c.getInsets();
        int extraHeight = cInsets.top + cInsets.bottom;
        int extraWidth = cInsets.left + cInsets.right;

        if (syncAllWidths) {
          int maxWidth = 0;

          for (int counter = 0; counter < numChildren; counter++) {
            aSize = children[counter].getPreferredSize();
            height = Math.max(height, aSize.height);
            maxWidth = Math.max(maxWidth, aSize.width);
          }
          return new Dimension(
              extraWidth + (maxWidth * numChildren) + (numChildren - 1) * padding,
              extraHeight + height + topMargin);
        } else {
          int totalWidth = 0;

          for (int counter = 0; counter < numChildren; counter++) {
            aSize = children[counter].getPreferredSize();
            height = Math.max(height, aSize.height);
            totalWidth += aSize.width;
          }
          totalWidth += ((numChildren - 1) * padding);
          return new Dimension(extraWidth + totalWidth, extraHeight + height + topMargin);
        }
      }
    }
    return new Dimension(0, 0);
  }

  @Override
  public Dimension preferredLayoutSize(Container c) {
    return minimumLayoutSize(c);
  }

  @Override
  public void removeLayoutComponent(Component c) {}
}
