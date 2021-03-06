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

package sinalgo.gui.dialogs;

import sinalgo.configuration.Configuration;
import sinalgo.gui.GuiHelper;
import sinalgo.io.versionTest.VersionTester;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class AboutDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1267479965949214938L;

    /**
     * The constructor for the GlobalSettingsDialog class.
     *
     * @param parent The parentGUI Frame to attach the dialog to.
     */
    public AboutDialog(JFrame parent) {
        super(parent, "About Sinalgo", true);
        GuiHelper.setWindowIcon(this);

        this.setLayout(new BorderLayout());
        // this.setResizable(false);
        this.setPreferredSize(new Dimension(600, 300));

        JEditorPane html = new JEditorPane();
        html.setContentType("text/html");
        html.setText("" + "<html>" + "<head>" + "<title>Sinalgo</title>" + "<style type='text/css'>" + "<!--"
                + "body { font-family: Verdana, Arial, Helvetica, sans-serif; }" + "h1 { color:#000077; } "
                + ".red { color: rgb(160, 0, 0);} " + "-->" + "</style>" + "</head>" + "<body>"
                + "<center><h1><span class='red'>Si</span>mulator for <span class='red'>N</span>etwork <span class='red'>Algo</span>rithms</h1></center>"
                + "" + "<center>Version " + Configuration.VERSION_STRING
                + "</center><center><small><a href='TestVersion'>Test for newer version</a></small></center>"
                + "<p>Visit <a href='" + Configuration.SINALGO_REPO + "'>" + Configuration.SINALGO_REPO + "</a> to obtain the latest version, report bugs or problems, "
                + "and visit <a href='" + Configuration.SINALGO_WEB_PAGE + "'>" + Configuration.SINALGO_WEB_PAGE + "</a> for a documentation of Sinalgo"
                + "<p>"
                + "Sinalgo is brought to you by the Distributed Computing Group of ETH Zurich <a href='https://dcg.ethz.ch'>https://dcg.ethz.ch</a>"
                + "<p>" + "</body>" + "</html>");
        html.setEditable(false);
        html.setBackground(this.getBackground());
        this.add(html);

        html.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (e.getDescription().equals("TestVersion")) {
                    VersionTester.testVersion(false, true);
                    return;
                }
                Desktop dt = Desktop.getDesktop();
                try {
                    dt.browse(new URL(e.getDescription()).toURI());
                } catch (IOException | URISyntaxException ex) {
                    Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
                    cp.setContents(new StringSelection(e.getDescription()), null);
                    JOptionPane.showMessageDialog(this, "The link was copied to the clipboard!");
                }
            }
        });

        // The close button
        JPanel buttons = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        buttons.add(closeButton);

        // Detect ESCAPE button
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addKeyEventPostProcessor(e -> {
            if (!e.isConsumed() && e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                AboutDialog.this.setVisible(false);
            }
            return false;
        });

        this.add(BorderLayout.SOUTH, buttons);
        this.getRootPane().setDefaultButton(closeButton);

        this.pack();
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        this.setVisible(false);
    }
}
