package edu.ncsa.gridshib.gridshibca;
/*
ErrorDialog.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/

import java.awt.Dimension;
import java.awt.Toolkit;
import org.jdesktop.application.Action;

/**
 *
 * @author vwelch
 */
public class ErrorDialog
        extends javax.swing.JDialog
{

    private DebugFrame debugFrame;

    /** Creates new form ErrorDialog */
    public ErrorDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        // XXX Make this a resource
        setTitle("GridShib-CA Error");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        closeButton = new javax.swing.JButton();
        openDebugButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        errorText = new javax.swing.JTextArea();
        titleLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edu.ncsa.gridshib.gridshibca.GridShibCAClientApp.class).getContext().getActionMap(ErrorDialog.class, this);
        closeButton.setAction(actionMap.get("quit")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edu.ncsa.gridshib.gridshibca.GridShibCAClientApp.class).getContext().getResourceMap(ErrorDialog.class);
        closeButton.setText(resourceMap.getString("closeButton.text")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N

        openDebugButton.setAction(actionMap.get("openDebugFrame")); // NOI18N
        openDebugButton.setText(resourceMap.getString("openDebugButton.text")); // NOI18N
        openDebugButton.setName("openDebugButton"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        errorText.setColumns(20);
        errorText.setRows(5);
        errorText.setName("errorText"); // NOI18N
        jScrollPane1.setViewportView(errorText);

        titleLabel.setText(resourceMap.getString("titleLabel.text")); // NOI18N
        titleLabel.setName("titleLabel"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(openDebugButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 164, Short.MAX_VALUE)
                .add(closeButton)
                .addContainerGap(20, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .add(titleLabel)
                    .add(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(titleLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 23, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(closeButton)
                    .add(openDebugButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {

            public void run()
            {
                ErrorDialog dialog = new ErrorDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {

                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public void setMessage(String message)
    {
        errorText.setText(message);
    }

    public void setDebugFrame(DebugFrame frame)
    {
        debugFrame = frame;
        openDebugButton.setEnabled(true);
    }

    public void display(String message)
    {
        center();
        setMessage(message);
        setVisible(true);
        requestFocus(true);
    }

    private void center()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    @Action
    public void openDebugFrame()
    {
        if (debugFrame != null)
        {
            debugFrame.display();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JTextArea errorText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton openDebugButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
