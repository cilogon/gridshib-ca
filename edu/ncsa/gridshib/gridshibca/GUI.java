package edu.ncsa.gridshib.gridshibca;
// $Id$

/*
Copyright 2006 The Board of Trustees of the University of Illinois.
All rights reserved.

Developed by:

  The GridShib Project
  National Center for Supercomputing Applications
  University of Illinois
  http://gridshib.globus.org/

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal with the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

  Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimers.

  Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimers in the
  documentation and/or other materials provided with the distribution.

  Neither the names of the National Center for Supercomputing
  Applications, the University of Illinois, nor the names of its
  contributors may be used to endorse or promote products derived from
  this Software without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI implements ActionListener {
	JButton button;
	JFrame frame;
	JTextArea textArea;
    int width=40;

	public GUI()
    {
        button = new JButton("OK");
        frame = new JFrame("GridShib CA Credential Retriever");
        textArea = new JTextArea(20,width);

		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 200);

		Container pane = frame.getContentPane();

		textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

	    JScrollPane scrollPane =
			new JScrollPane(textArea);
		pane.add(scrollPane, BorderLayout.CENTER);

		pane.add(button, BorderLayout.PAGE_END);
		button.addActionListener(this);

        JTextArea header = new JTextArea(2,width);
        header.setEditable(false);
		Version version = new Version();
        header.append("GridShib CA Credential Retriever version " +
                      version.getVersion() + "\n");
        header.append(version.getCopyright());
		pane.add(header, BorderLayout.PAGE_START);

        /* 
		pane.setBorder(BorderFactory.createEmptyBorder(30, // top
				30, // left
				10, // bottom
				30) // right
				);
        */

		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		// Calculate the frame location
		int x = (screenSize.width - frame.getWidth()) / 2;
		int y = (screenSize.height - frame.getHeight()) / 2;

		// Set the new frame location
		frame.setLocation(x, y);

		disableButton();
    }

    public void display()
    {
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public void displayMessage(String s)
    {
		textArea.append(s + "\n");
	}

	public void error(String s)
    {
		displayMessage("Fatal Error: " + s);
	}

	public void warning(String s)
    {
		displayMessage("Warning:" + s);
	}

	public void enableButton()
    {
		button.setEnabled(true);
	}

	public void disableButton()
    {
		button.setEnabled(false);
	}

	// Button presses
	public void actionPerformed(ActionEvent arg0)
	{
		System.exit(0);
	}

    public void waitForOK()
    {
        enableButton();
        // XXX need to actually wait now...
    }
}
