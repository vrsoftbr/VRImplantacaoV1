package vrimplantacao2.gui.planilha;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrimplantacao2.controller.ConversaoPlanilhaController;

/**
 *
 * @author guilhermegomes
 */
public class ConversaoPlanilhaGUI extends VRInternalFrame {

    /**
     * Creates new form ConversaoPlanilhaGUI
     * @param i_mdiFrame
     * @throws java.lang.Exception
     */
    public ConversaoPlanilhaGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        setTitle("Conversor de Planilha");
        centralizarForm();
        this.setMaximum(false);
    }
    
    private void converter() throws Exception {
        ConversaoPlanilhaController controller = new ConversaoPlanilhaController(
                txtProdutoFile.getArquivo(),
                txtTabela.getText()
        );
        
        controller.converter();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNomeTabela = new javax.swing.JLabel();
        txtTabela = new javax.swing.JTextField();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        txtDelimitadorProd = new javax.swing.JTextField();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtProdutoFile = new vrframework.bean.fileChooser.VRFileChooser();
        btnExecutar = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(lblNomeTabela, org.openide.util.NbBundle.getMessage(ConversaoPlanilhaGUI.class, "ConversaoPlanilhaGUI.lblNomeTabela.text")); // NOI18N

        txtTabela.setText(org.openide.util.NbBundle.getMessage(ConversaoPlanilhaGUI.class, "ConversaoPlanilhaGUI.txtTabela.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel8, org.openide.util.NbBundle.getMessage(ConversaoPlanilhaGUI.class, "ConversaoPlanilhaGUI.vRLabel8.text")); // NOI18N

        txtDelimitadorProd.setText(org.openide.util.NbBundle.getMessage(ConversaoPlanilhaGUI.class, "ConversaoPlanilhaGUI.txtDelimitadorProd.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, org.openide.util.NbBundle.getMessage(ConversaoPlanilhaGUI.class, "ConversaoPlanilhaGUI.vRLabel1.text")); // NOI18N

        btnExecutar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/ok.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnExecutar, org.openide.util.NbBundle.getMessage(ConversaoPlanilhaGUI.class, "ConversaoPlanilhaGUI.btnExecutar.text")); // NOI18N
        btnExecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecutarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNomeTabela)
                            .addComponent(txtTabela, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(vRLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDelimitadorProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProdutoFile, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnExecutar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNomeTabela)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDelimitadorProd, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProdutoFile, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExecutar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecutarActionPerformed
        try {
            converter();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnExecutarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExecutar;
    private javax.swing.JLabel lblNomeTabela;
    private javax.swing.JTextField txtDelimitadorProd;
    private vrframework.bean.fileChooser.VRFileChooser txtProdutoFile;
    private javax.swing.JTextField txtTabela;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel8;
    // End of variables declaration//GEN-END:variables

    private static ConversaoPlanilhaGUI instance;
    
    public static void Exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new ConversaoPlanilhaGUI(i_mdiFrame);
            }

            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }
    
}
