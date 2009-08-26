/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CredentialInfoFrame.java
 *
 * Created on Jul 17, 2009, 7:55:43 PM
 */
package edu.ncsa.gridshib.gridshibca;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.WindowConstants;
import org.jdesktop.application.Action;

/**
 * Show user information about their credential.
*
* TODO: Add butons for copying DNs to clipboard.
 * TODO: Show user location of credential on local filesystem.
 * 
 * @author vwelch
 */
public class CredentialInfoFrame
        extends javax.swing.JFrame
{

    /** Creates new form CredentialInfoFrame */
    public CredentialInfoFrame()
    {
        initComponents();
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        // XXX Make this a resource
        setTitle("GridShib-CA Credential Information");
        globusIdField.setEditable(false);
        rfcIdField.setEditable(false);
        validUntilField.setEditable(false);
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
        titleLabel = new javax.swing.JLabel();
        globusIdLabel = new javax.swing.JLabel();
        rfcIdLabel = new javax.swing.JLabel();
        globusIdField = new javax.swing.JTextField();
        rfcIdField = new javax.swing.JTextField();
        validUntilLabel = new javax.swing.JLabel();
        validUntilField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edu.ncsa.gridshib.gridshibca.GridShibCAClientApp.class).getContext().getActionMap(CredentialInfoFrame.class, this);
        closeButton.setAction(actionMap.get("close")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edu.ncsa.gridshib.gridshibca.GridShibCAClientApp.class).getContext().getResourceMap(CredentialInfoFrame.class);
        closeButton.setText(resourceMap.getString("doneButton.text")); // NOI18N
        closeButton.setName("doneButton"); // NOI18N

        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(resourceMap.getString("titleLabel.text")); // NOI18N
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titleLabel.setMinimumSize(new java.awt.Dimension(300, 200));
        titleLabel.setName("titleLabel"); // NOI18N

        globusIdLabel.setText(resourceMap.getString("globusIdLabel.text")); // NOI18N
        globusIdLabel.setName("globusIdLabel"); // NOI18N

        rfcIdLabel.setText(resourceMap.getString("rfcIdLabel.text")); // NOI18N
        rfcIdLabel.setName("rfcIdLabel"); // NOI18N

        globusIdField.setText(resourceMap.getString("globusIdField.text")); // NOI18N
        globusIdField.setName("globusIdField"); // NOI18N

        rfcIdField.setText(resourceMap.getString("rfcIdField.text")); // NOI18N
        rfcIdField.setName("rfcIdField"); // NOI18N

        validUntilLabel.setText(resourceMap.getString("validUntilLabel.text")); // NOI18N
        validUntilLabel.setName("validUntilLabel"); // NOI18N

        validUntilField.setText(resourceMap.getString("validUntilField.text")); // NOI18N
        validUntilField.setName("validUntilField"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(globusIdLabel)
                        .addContainerGap(245, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(rfcIdLabel)
                        .addContainerGap(229, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, rfcIdField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                            .add(globusIdField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(validUntilLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(validUntilField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 228, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(closeButton)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 123, Short.MAX_VALUE)
                        .add(titleLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(132, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(titleLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(globusIdLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(globusIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(7, 7, 7)
                .add(rfcIdLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rfcIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(validUntilLabel)
                    .add(validUntilField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(closeButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                new CredentialInfoFrame().setVisible(true);
            }
        });
    }

    public void display()
    {
        center();
        setVisible(true);
    }

    public void setCredential(Credential cred)
    {
        globusIdField.setText(cred.getSubjectGlobusFormat());
        rfcIdField.setText(cred.getSubject());
        validUntilField.setText(cred.getNotAfter().toString());
    }

    @Action
    public void close()
    {
        setVisible(false);
    }

    private void center()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField globusIdField;
    private javax.swing.JLabel globusIdLabel;
    private javax.swing.JTextField rfcIdField;
    private javax.swing.JLabel rfcIdLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField validUntilField;
    private javax.swing.JLabel validUntilLabel;
    // End of variables declaration//GEN-END:variables
}