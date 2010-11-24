package edu.ncsa.gridshib.gridshibca;
/*
GridShibCAClientView.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2010 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/


import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.jdesktop.application.ResourceMap;

/**
 * The application's main frame.
 */
public class GridShibCAClientView
        extends FrameView
{

    JFrame mainFrame;
    DebugFrame debugFrame = null;
    CredentialInfoFrame credInfo = null;
    ResourceMap resourceMap;

    public GridShibCAClientView(SingleFrameApplication app)
    {
        super(app);

        initComponents();

        resourceMap = org.jdesktop.application.Application.getInstance(edu.ncsa.gridshib.gridshibca.GridShibCAClientApp.class).getContext().getResourceMap(GridShibCAClientView.class);

        mainFrame = getFrame();

        /* Don't allow resizing */
        mainFrame.setResizable(false);
        
        /* Center our GUI*/
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);

        debugFrame = new DebugFrame();
        credInfo = new CredentialInfoFrame();

        showCredInfoMenuItem.setEnabled(false);
        exitButton.setEnabled(false);

        locationLabel.setVisible(false);
        locationField.setVisible(false);
        locationField.setEditable(false);
        PKCSlocationLabel.setVisible(false);
        PKCSlocationField.setVisible(false);
        PKCSlocationField.setEditable(false);
    }

    @Action
    public void showAboutBox()
    {
        if (aboutBox == null)
        {
            aboutBox = new GridShibCAClientAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        GridShibCAClientApp.getApplication().show(aboutBox);
    }

    @Action
    public void showDebugFrame()
    {
        if (debugFrame != null)
        {
            debugFrame.setLocationRelativeTo(mainFrame);
            debugFrame.display();
        }
    }

    @Action
    public void showCredentialInfo()
    {
        if (credInfo != null)
        {
            credInfo.setLocationRelativeTo(mainFrame);
            credInfo.display();
        }
    }

    public void setStatus(String message)
    {
        statusLabel.setText(message);
        debugMessage(message);
    }

    public void debugMessage(String message)
    {
        if (debugFrame != null)
        {
            debugFrame.debugMessage(message);
        }
    }

    public void error(String message)
    {
        error(message, null);
    }

    public void error(String message, Exception ex)
    {
        if (ex != null)
        {
            debugFrame.logException(ex);
        }
        ErrorDialog errorDialog = new ErrorDialog(mainFrame, /* modal */ false);
        errorDialog.setDebugFrame(debugFrame);
        errorDialog.display(message);
        errorDialog.display(ex.getMessage());
    }

    public void displayCredentalInfo(Credential cred)
    {
        credInfo.setCredential(cred);
        showCredInfoMenuItem.setEnabled(true);
    }

    public void displaySuccess()
    {
        doneLabel.setIcon(resourceMap.getIcon("doneLabel.icon"));
        doneLabel.setText(resourceMap.getString("doneLabel.successText"));

    }

    public void enableExitButton()
    {
        exitButton.setEnabled(true);
    }

    public char [] getPassphrase()
    {
        PassphraseDialog pDialog =
                new PassphraseDialog(mainFrame, /* modal */ true);
        pDialog.setLocationRelativeTo(mainFrame);
        return pDialog.getPassphrase();
    }

    public void showCredentialLocation(String loc)
    {
        locationField.setText(loc);
        locationLabel.setVisible(true);
        locationField.setVisible(true);
    }

    public void showPKCS12Location(String loc)
    {
        PKCSlocationField.setText(loc);
        PKCSlocationLabel.setVisible(true);
        PKCSlocationField.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        exitButton = new javax.swing.JButton();
        doneLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        statusTitleLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        locationField = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        PKCSlocationLabel = new javax.swing.JLabel();
        PKCSlocationField = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        showCredInfoMenuItem = new javax.swing.JMenuItem();
        showDebugMenuItem = new javax.swing.JMenuItem();
        showHelpMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();

        mainPanel.setMaximumSize(new java.awt.Dimension(400, 350));
        mainPanel.setMinimumSize(new java.awt.Dimension(400, 350));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(400, 350));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edu.ncsa.gridshib.gridshibca.GridShibCAClientApp.class).getContext().getActionMap(GridShibCAClientView.class, this);
        exitButton.setAction(actionMap.get("quit")); // NOI18N
        exitButton.setName("exitButton"); // NOI18N

        doneLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edu.ncsa.gridshib.gridshibca.GridShibCAClientApp.class).getContext().getResourceMap(GridShibCAClientView.class);
        doneLabel.setIcon(resourceMap.getIcon("doneLabel.icon")); // NOI18N
        doneLabel.setText(resourceMap.getString("doneLabel.text")); // NOI18N
        doneLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        doneLabel.setDisabledIcon(resourceMap.getIcon("doneLabel.disabledIcon")); // NOI18N
        doneLabel.setEnabled(false);
        doneLabel.setName("doneLabel"); // NOI18N
        doneLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        statusTitleLabel.setFont(resourceMap.getFont("statusTitleLabel.font")); // NOI18N
        statusTitleLabel.setText(resourceMap.getString("statusTitleLabel.text")); // NOI18N
        statusTitleLabel.setName("statusTitleLabel"); // NOI18N

        statusLabel.setText(resourceMap.getString("statusLabel.text")); // NOI18N
        statusLabel.setName("statusLabel"); // NOI18N

        locationField.setText(resourceMap.getString("locationField.text")); // NOI18N
        locationField.setName("locationField"); // NOI18N

        locationLabel.setText(resourceMap.getString("locationLabel.text")); // NOI18N
        locationLabel.setName("locationLabel"); // NOI18N

        PKCSlocationLabel.setText(resourceMap.getString("PKCSlocationLabel.text")); // NOI18N
        PKCSlocationLabel.setName("PKCSlocationLabel"); // NOI18N

        PKCSlocationField.setName("PKCSlocationField"); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(exitButton)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
                .addContainerGap())
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(PKCSlocationLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap(48, Short.MAX_VALUE))
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(locationLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap(48, Short.MAX_VALUE))
            .add(mainPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(statusTitleLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addContainerGap(48, Short.MAX_VALUE))
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(PKCSlocationField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addContainerGap())
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(doneLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addContainerGap())
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(locationField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusTitleLabel)
                    .add(statusLabel))
                .add(18, 18, 18)
                .add(doneLabel)
                .add(18, 18, 18)
                .add(locationLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(locationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(PKCSlocationLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(PKCSlocationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(exitButton)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        showCredInfoMenuItem.setAction(actionMap.get("showCredentialInfo")); // NOI18N
        showCredInfoMenuItem.setText(resourceMap.getString("showCredInfoMenuItem.text")); // NOI18N
        showCredInfoMenuItem.setName("showCredInfoMenuItem"); // NOI18N
        fileMenu.add(showCredInfoMenuItem);

        showDebugMenuItem.setAction(actionMap.get("showDebugFrame")); // NOI18N
        showDebugMenuItem.setText(resourceMap.getString("showDebugMenuItem.text")); // NOI18N
        showDebugMenuItem.setName("showDebugMenuItem"); // NOI18N
        fileMenu.add(showDebugMenuItem);

        showHelpMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        showHelpMenuItem.setText(resourceMap.getString("showHelpMenuItem.text")); // NOI18N
        showHelpMenuItem.setName("showHelpMenuItem"); // NOI18N
        fileMenu.add(showHelpMenuItem);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField PKCSlocationField;
    private javax.swing.JLabel PKCSlocationLabel;
    public javax.swing.JLabel doneLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField locationField;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem showCredInfoMenuItem;
    private javax.swing.JMenuItem showDebugMenuItem;
    private javax.swing.JMenuItem showHelpMenuItem;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel statusTitleLabel;
    // End of variables declaration//GEN-END:variables
    private JDialog aboutBox;
}
