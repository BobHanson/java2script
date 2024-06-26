/*
 * Some portions of this file have been modified by Robert Hanson hansonr.at.stolaf.edu 2012-2017
 * for use in SwingJS via transpilation into JavaScript using Java2Script.
 *
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
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
package javax.swing;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.io.Serializable;


/**
 * Comparator which attempts to sort Components based on their size and
 * position. Code adapted from original javax.swing.DefaultFocusManager
 * implementation.
 *
 * @author David Mendenhall
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
final class LayoutComparator implements Comparator, Serializable {

    private static final int ROW_TOLERANCE = 10;

    private boolean horizontal = true;
    private boolean leftToRight = true;

    void setComponentOrientation(ComponentOrientation orientation) {
        horizontal = orientation.isHorizontal();
        leftToRight = orientation.isLeftToRight();
    }

    @Override
		public int compare(Object o1, Object o2) {
        Component a = (Component)o1;
        Component b = (Component)o2;

        if (a == b) {
            return 0;
        }

        // Row/Column algorithm only applies to siblings. If 'a' and 'b'
        // aren't siblings, then we need to find their most inferior
        // ancestors which share a parent. Compute the ancestory lists for
        // each Component and then search from the Window down until the
        // hierarchy branches.
        if (a.getParent() != b.getParent()) {
            LinkedList aAncestory, bAncestory;

            for(aAncestory = new LinkedList(); a != null; a = a.getParent()) {
                aAncestory.add(a);
                if (a.isWindowOrJSApplet()) {
                    break;
                }
            }
            if (a == null) {
                // 'a' is not part of a Window hierarchy. Can't cope.
                throw new ClassCastException();
            }

            for(bAncestory = new LinkedList(); b != null; b = b.getParent()) {
                bAncestory.add(b);
                if (b.isWindowOrJSApplet()) {
                    break;
                }
            }
            if (b == null) {
                // 'b' is not part of a Window hierarchy. Can't cope.
                throw new ClassCastException();
            }

            for (ListIterator
                     aIter = aAncestory.listIterator(aAncestory.size()),
                     bIter = bAncestory.listIterator(bAncestory.size()); ;) {
                if (aIter.hasPrevious()) {
                    a = (Component)aIter.previous();
                } else {
                    // a is an ancestor of b
                    return -1;
                }

                if (bIter.hasPrevious()) {
                    b = (Component)bIter.previous();
                } else {
                    // b is an ancestor of a
                    return 1;
                }

                if (a != b) {
                    break;
                }
            }
        }

        int ax = a.getX(), ay = a.getY(), bx = b.getX(), by = b.getY();

        Container ap = a.getParent();
        Container bp = b.getParent();
        // desktop will have no parent
        int zOrder = (ap == null ? 1 : bp == null ? -1 : ap.getComponentZOrder(a) - bp.getComponentZOrder(b));
        if (horizontal) {
            if (leftToRight) {

                // LT - Western Europe (optional for Japanese, Chinese, Korean)

                if (Math.abs(ay - by) < ROW_TOLERANCE) {
                    return (ax < bx) ? -1 : ((ax > bx) ? 1 : zOrder);
                } else {
                    return (ay < by) ? -1 : 1;
                }
            } else { // !leftToRight

                // RT - Middle East (Arabic, Hebrew)

                if (Math.abs(ay - by) < ROW_TOLERANCE) {
                    return (ax > bx) ? -1 : ((ax < bx) ? 1 : zOrder);
                } else {
                    return (ay < by) ? -1 : 1;
                }
            }
        } else { // !horizontal
            if (leftToRight) {

                // TL - Mongolian

                if (Math.abs(ax - bx) < ROW_TOLERANCE) {
                    return (ay < by) ? -1 : ((ay > by) ? 1 : zOrder);
                } else {
                    return (ax < bx) ? -1 : 1;
                }
            } else { // !leftToRight

                // TR - Japanese, Chinese, Korean

                if (Math.abs(ax - bx) < ROW_TOLERANCE) {
                    return (ay < by) ? -1 : ((ay > by) ? 1 : zOrder);
                } else {
                    return (ax > bx) ? -1 : 1;
                }
            }
        }
    }
}
