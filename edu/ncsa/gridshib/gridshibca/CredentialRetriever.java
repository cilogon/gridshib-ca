package edu.ncsa.gridshib.gridshibca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import org.globus.util.ConfigUtil;
import org.globus.util.Util;

public class CredentialRetriever implements ActionListener {
	final JLabel label = new JLabel("");
	final JButton button = new JButton("OK");
	final JFrame frame = new JFrame("Grid Proxy Retriever");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CredentialRetriever app = new CredentialRetriever();
		app.doit(args);
	}

	public void doit(String[] args) {
		displayGUI();

		try {
			URL proxyURL = new URL(args[0]);
			InputStream proxyStream = proxyURL.openStream();

			String targetFile = getDefaultProxyLocation();
			displayMessage("Writing proxy to " + targetFile);

			File outFile = new File(targetFile);

			outFile.delete();
			outFile.createNewFile();
			// Argh. small time window here where file could be opened()
			// Need to find way of setting UMASK.
			Util.setFilePermissions(targetFile, 0600);

			FileWriter out = new FileWriter(outFile);

			int c;
			while ((c = proxyStream.read()) != -1) {
				out.write(c);
			}
			out.close();
			proxyStream.close();
			displayMessage("Proxy written to " + targetFile);
		} catch (java.net.MalformedURLException e) {
			error("Malformed URL: " + args[0]);
		} catch (java.io.IOException e) {
			error("IO Error: " + e.getMessage());
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			error("Missing URL argument");
		}

		enableButton();
	}

	private String getDefaultProxyLocation() {
		String os = System.getProperty("os.name");
		String sep = System.getProperty("file.separator");
		String path = "";

		if ((os != null) && (os.startsWith("Windows"))) {
			String username = System.getProperty("user.name");
			String tmpDir = System.getProperty("java.io.tmpdir");
			path = tmpDir + sep + "x509up_u_" + username;
		} else {
			try {
				// Assume Unix
				String uid = ConfigUtil.getUID();
				path = "/tmp/x509up_u" + uid;
			} catch (Exception e) {
				error("Could not get uid: " + e.getMessage());
			}
		}

		return path;
	}

	private void displayGUI() {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 200);

		JPanel pane = new JPanel(new GridLayout(0, 1));
		label.setText("Grid Proxy Retriever running...");
		pane.add(label);

		pane.add(button);
		button.addActionListener(this);

		pane.setBorder(BorderFactory.createEmptyBorder(30, // top
				30, // left
				10, // bottom
				30) // right
				);

		frame.getContentPane().add(pane, BorderLayout.CENTER);

		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		// Calculate the frame location
		int x = (screenSize.width - frame.getWidth()) / 2;
		int y = (screenSize.height - frame.getHeight()) / 2;

		// Set the new frame location
		frame.setLocation(x, y);

		disableButton();

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public void displayMessage(String s) {
		label.setText(s);
		frame.pack();
	}

	public void error(String s) {
		JOptionPane.showMessageDialog(frame, s, "Proxy Retriever Error",
				JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

	public void enableButton() {
		button.setEnabled(true);
	}

	public void disableButton() {
		button.setEnabled(false);
	}

	// Button presses
public void actionPerformed(ActionEvent arg0)
	{
		// TODO Auto-generated method stub
		System.exit(0);
	}
}
