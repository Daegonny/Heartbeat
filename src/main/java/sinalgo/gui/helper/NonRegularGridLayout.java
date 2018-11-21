/*
BSD 3-Clause License

Copyright (c) 2007-2013, Distributed Computing Group (DCG)
                         ETH Zurich
                         Switzerland
                         dcg.ethz.ch
              2017-2018, André Brait

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package sinalgo.gui.helper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * Grid Layout which allows components of differrent sizes.
 */
public class NonRegularGridLayout extends GridLayout {

    private static final long serialVersionUID = 211832307940253926L;

    /**
     * This creates an instance of the NonRegularGridLayout with 1 row and no colums
     * without gaps.
     */
    public NonRegularGridLayout() {
        this(1, 0, 0, 0);
    }

    /**
     * This creates an instance of the NonRegularGridLayout with given number of
     * rows and colums.
     *
     * @param rows The number of rows to prepare the grid for.
     * @param cols The number of colums to prepare the grid for.
     */
    public NonRegularGridLayout(int rows, int cols) {
        this(rows, cols, 0, 0);
    }

    /**
     * This creates an instance of the NonRegularGridLayout with given number of
     * rows, colums, and given gaps between the rows and the colums respectively.
     *
     * @param rows The number of rows to prepare the Grid for.
     * @param cols The number of colums to prepare the Grid for.
     * @param hgap The horizontal gap.
     * @param vgap The vertical gap.
     */
    public NonRegularGridLayout(int rows, int cols, int hgap, int vgap) {
        super(rows, cols, hgap, vgap);
    }

    /**
     * Determines whether all components of the grid should be left-aligned and the
     * remaining space given to the right-most cells.
     *
     * @param alignToLeft Whether to align left or not
     */
    @Getter(AccessLevel.PRIVATE)
    @Setter
    private boolean alignToLeft;

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        // System.err.println("preferredLayoutSize");
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = this.getRows();
            int ncols = this.getColumns();
            if (nrows > 0) {
                ncols = (ncomponents + nrows - 1) / nrows;
            } else {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            int[] w = new int[ncols];
            int[] h = new int[nrows];
            this.calculateSize(parent, ncomponents, ncols, w, h);
            int nw = 0;
            for (int j = 0; j < ncols; j++) {
                nw += w[j];
            }
            int nh = 0;
            for (int i = 0; i < nrows; i++) {
                nh += h[i];
            }
            return new Dimension(insets.left + insets.right + nw + (ncols - 1) * this.getHgap(),
                    insets.top + insets.bottom + nh + (nrows - 1) * this.getVgap());
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        // System.err.println("minimumLayoutSize");
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = this.getRows();
            int ncols = this.getColumns();
            if (nrows > 0) {
                ncols = (ncomponents + nrows - 1) / nrows;
            } else {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            int[] w = new int[ncols];
            int[] h = new int[nrows];
            for (int i = 0; i < ncomponents; i++) {
                int r = i / ncols;
                int c = i % ncols;
                Component comp = parent.getComponent(i);
                Dimension d = comp.getMinimumSize();
                if (w[c] < d.width) {
                    w[c] = d.width;
                }
                if (h[r] < d.height) {
                    h[r] = d.height;
                }
            }
            int nw = 0;
            for (int j = 0; j < ncols; j++) {
                nw += w[j];
            }
            int nh = 0;
            for (int i = 0; i < nrows; i++) {
                nh += h[i];
            }
            return new Dimension(insets.left + insets.right + nw + (ncols - 1) * this.getHgap(),
                    insets.top + insets.bottom + nh + (nrows - 1) * this.getVgap());
        }
    }

    @Override
    public void layoutContainer(Container parent) {
        // System.err.println("layoutContainer");
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = this.getRows();
            int ncols = this.getColumns();
            if (ncomponents == 0) {
                return;
            }
            if (nrows > 0) {
                ncols = (ncomponents + nrows - 1) / nrows;
            } else {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            int hgap = this.getHgap();
            int vgap = this.getVgap();
            // scaling factors
            Dimension pd = this.preferredLayoutSize(parent);

            if (this.isAlignToLeft()) {
                int[] w = new int[ncols]; // maximal width
                int[] h = new int[nrows]; // maximal height
                this.calculateSize(parent, ncomponents, ncols, w, h);
                int totW = 0;
                for (int i : w) {
                    totW += i + hgap;
                }
                totW -= hgap;
                if (totW < parent.getWidth()) {
                    w[ncols - 1] += parent.getWidth() - totW;
                }

                this.calculateBounds(parent, insets, ncomponents, nrows, ncols, hgap, vgap, w, h);
            } else {
                double sw = (1.0 * parent.getWidth()) / pd.width;
                double sh = (1.0 * parent.getHeight()) / pd.height;
                // scale
                int[] w = new int[ncols]; // maximal width
                int[] h = new int[nrows]; // maximal height
                for (int i = 0; i < ncomponents; i++) {
                    int r = i / ncols;
                    int c = i % ncols;
                    Component comp = parent.getComponent(i);
                    Dimension d = comp.getPreferredSize();
                    d.width = (int) (sw * d.width);
                    d.height = (int) (sh * d.height);
                    if (w[c] < d.width) {
                        w[c] = d.width;
                    }
                    if (h[r] < d.height) {
                        h[r] = d.height;
                    }
                }
                this.calculateBounds(parent, insets, ncomponents, nrows, ncols, hgap, vgap, w, h);
            }
        }
    }

    private void calculateBounds(Container parent, Insets insets, int ncomponents, int nrows, int ncols, int hgap, int vgap, int[] w, int[] h) {
        for (int c = 0, x = insets.left; c < ncols; c++) {
            for (int r = 0, y = insets.top; r < nrows; r++) {
                int i = r * ncols + c;
                if (i < ncomponents) {
                    parent.getComponent(i).setBounds(x, y, w[c], h[r]);
                }
                y += h[r] + vgap;
            }
            x += w[c] + hgap;
        }
    }

    private void calculateSize(Container parent, int ncomponents, int ncols, int[] w, int[] h) {
        for (int i = 0; i < ncomponents; i++) {
            int r = i / ncols;
            int c = i % ncols;
            Component comp = parent.getComponent(i);
            Dimension d = comp.getPreferredSize();
            if (w[c] < d.width) {
                w[c] = d.width;
            }
            if (h[r] < d.height) {
                h[r] = d.height;
            }
        }
    }
}
