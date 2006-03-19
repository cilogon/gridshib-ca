package edu.ncsa.gridshib.gridshibca;
// $Id$

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLDecoder;
import org.globus.util.ConfigUtil;
import org.globus.util.Util;

public class CredentialRetriever implements ActionListener {
	final JLabel label = new JLabel("");
	final JButton button = new JButton("OK");
	final JFrame frame = new JFrame("GridShib CA Credential Retriever");
	final JTextArea textArea = new JTextArea(20,40);

	public static void main(String[] args) {
		CredentialRetriever app = new CredentialRetriever();
		app.doit(args);
	}

	public void doit(String[] args) {
		displayGUI();
		Version version = new Version();
		displayMessage("GridShib CA Credential Retriever version " + version.getVersion() +
					   " (" + version.getBuildDate() + ")");

		try {
			URL credURL = new URL(args[0]);
			String token = args[1];
	
			setupTrustedCAs();
			
			// URL must be https
			String protocol = credURL.getProtocol();
			if (!protocol.equals("https"))
			{
				throw new Exception("Credential URL is not secure (is '" + protocol + "' rather than 'https'). Service is misconfigured.");
			}

			displayMessage("Connecting to " + credURL.toString());
			
			URLConnection conn = credURL.openConnection();
	        conn.setDoOutput(true);
			OutputStreamWriter postWriter = new OutputStreamWriter(conn.getOutputStream());
			String postData = URLEncoder.encode("token", "UTF-8") +
				"=" + URLEncoder.encode(token, "UTF-8");
			
			postWriter.write(postData);
			postWriter.flush();

			BufferedReader credStream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	
			String targetFile = getDefaultProxyLocation();
			displayMessage("Writing credential to " + targetFile);

			File outFile = new File(targetFile);

			outFile.delete();
			outFile.createNewFile();
			// Argh. small time window here where file could be opened()
			// Need to find way of setting UMASK.
			Util.setFilePermissions(targetFile, 0600);

			FileWriter out = new FileWriter(outFile);

			String line;
			// Check first line for error string
			line = credStream.readLine();
			if (line != null)
			{
				if (line.startsWith("ERROR:"))
				{
					throw new Exception("Got error from GridShib CA server." + line);
				}
				out.write(line + "\n");
				while ((line = credStream.readLine()) != null) {
					out.write(line + "\n");
				}
			}
			else
			{
				throw new Exception("Got no data from server trying to read Credential.");
			}
			out.close();
			credStream.close();
			displayMessage("Credential written to " + targetFile);
			displayMessage("Success.");
		} catch (java.net.MalformedURLException e) {
			error("Malformed URL: " + args[0]);
		} catch (java.io.IOException e) {
			error("IO Error: " + e.getMessage());
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			error("Missing argument");
		} catch (Exception e) {
			error(e.getMessage());
		}

		enableButton();
	}

	private String getDefaultProxyLocation() throws Exception {
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
				throw new Exception("Could not get uid: " + e.getMessage());
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

		JPanel pane = new JPanel();

		textArea.setEditable(false);
	    JScrollPane scrollPane =
			new JScrollPane(textArea,
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pane.add(scrollPane);

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
		//label.setText(s);
		//frame.pack();
		textArea.append(s + "\n");
	}

	public void error(String s) {
		displayMessage("Fatal Error: " + s);
	}

	public void warning(String s) {
		displayMessage("Warning:" + s);
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
		System.exit(0);
	}

	public void setupTrustedCAs()
	{
		try
		{
			ClassLoader cl = this.getClass().getClassLoader();
			URL url = cl.getResource("resources/trustStore");
			if (url == null)
			{
				warning("Could not find trusted CAs");
				return;
 			}
			String path = url.toString();
			// If file is in a jar, we need to write it into a temporary
			// file, as we need a real file for the trustStore
			if (path.startsWith("jar:"))
			{
				displayMessage("Copying trusted CA certificates from jar.");
				File tempFile = File.createTempFile("CredentialRetriever",
													".trustStore");
				InputStream in = cl.getResourceAsStream("resources/trustStore");
				FileOutputStream out = new FileOutputStream(tempFile);
				copyStream(in, out);
				in.close();
				out.close();
				path = tempFile.getAbsolutePath();
				tempFile.deleteOnExit();
			}
			// Remove "file:" prefix from path if it exists as the security
			// runtime can't deal with it.
			if (path.startsWith("file:"))
			{
				path = path.substring(5);
			}
			displayMessage("Setting trusted CAs to " + path);
			System.setProperty("javax.net.ssl.trustStore", path);
		} catch (Exception e ) {
			warning("Failed to initiate trusted CAs: " + e.getMessage());
		}
	}

	public void copyStream(InputStream in, OutputStream out)
		throws IOException
	{
		byte[] buffer = new byte[1024];	// Arbitrary size
		int read;

		while((read = in.read(buffer)) != -1)
		{
			out.write(buffer, 0, read);
		}
	}
}
