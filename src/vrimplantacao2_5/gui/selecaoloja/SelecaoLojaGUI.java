package vrimplantacao2_5.gui.selecaoloja;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.dialog.VRDialog;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao2_5.controller.selecaoloja.SelecaoLojaController;
import vrimplantacao2_5.gui.cadastro.configuracao.ConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author guilhermegomes
 */
public class SelecaoLojaGUI extends VRDialog {

    public VRMdiFrame parentFrame;
    private SelecaoLojaController controller = null;
    private List<ConfiguracaoBancoLojaVO> lojas = null;
    private List<ConfiguracaoBaseDadosVO> conexoes = null;
    public ConfiguracaoBaseDadosGUI baseDadosGUI = null;
    public static int idConexao = 0;
    public static int lojaOrigem = 0;
    
    /**
     * Creates new form MigracaoGUI
     * @throws java.lang.Exception
     */
    public SelecaoLojaGUI() throws Exception {
        initComponents();
        
        setConfiguracao();
        
    }
    
    public SelecaoLojaGUI(ConfiguracaoBaseDadosGUI baseDadosGUI) throws Exception {
        this.baseDadosGUI = baseDadosGUI;
        
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() throws Exception {
        centralizarForm();
        setResizable(false);
        setModal(true);
        setTitle("Seleção de Loja");
        
        this.controller = new SelecaoLojaController();
        getNomeConexao();
    }
    
    private void getNomeConexao() throws Exception {
        cboConexao.setModel(new DefaultComboBoxModel());
        
        conexoes = controller.consultar();
        
        for (ConfiguracaoBaseDadosVO configuracaoVO : conexoes) {
            cboConexao.addItem(new ItemComboVO(configuracaoVO.getId(), configuracaoVO.getDescricao()));
        }
    }
    
    private void getLojaMapeada() {
        cboOrigem.setModel(new DefaultComboBoxModel());
        
        lojas = controller.getLojaMapeada(cboConexao.getId());
        
        for (ConfiguracaoBancoLojaVO configuracaoLojaVO : lojas) {
            cboOrigem.addItem(new ItemComboVO(configuracaoLojaVO.getIdLojaOrigem(),
                    configuracaoLojaVO.getIdLojaOrigem() + " - " + configuracaoLojaVO.getDescricaoLojaOrigem() + " - "
                    + (configuracaoLojaVO.isLojaMatriz() ? "LOJA MIX PRINCIPAL" : "LOJA NORMAL")));
        }
    }
    
    private void getLojaVR() {
        ConfiguracaoBancoLojaVO configuracaoVO = lojas.get(cboOrigem.getSelectedIndex());
        
        txtLojaVR.setText(configuracaoVO.getIdLojaVR() + " - " + configuracaoVO.getDescricaoVR());
        lojaOrigem = Integer.parseInt(configuracaoVO.getIdLojaOrigem().trim());
        if (configuracaoVO != null) {
            btnProximo.setEnabled(true);
        }
    }
    
    private void construirInternalFrame() throws Exception {
        ConfiguracaoBaseDadosVO configuracaoVO = conexoes.get(cboConexao.getSelectedIndex());
        ESistema eSistema = ESistema.getById(configuracaoVO.getSistema().getId());
        
        idConexao = configuracaoVO.getId();
        VRInternalFrame internalFrame = controller.construirInternalFrame(
                                                        eSistema, 
                                                        parentFrame);
        
        if (internalFrame == null) {
            throw new VRException("Nenhuma tela encontrada para o sistema informado!");
        }
        
        this.setVisible(false);
        internalFrame.setVisible(true);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNomeConexao = new vrframework.bean.label.VRLabel();
        cboConexao = new vrframework.bean.comboBox.VRComboBox();
        lblLojaOrigem = new vrframework.bean.label.VRLabel();
        cboOrigem = new vrframework.bean.comboBox.VRComboBox();
        lblLojaVR = new vrframework.bean.label.VRLabel();
        txtLojaVR = new vrframework.bean.textField.VRTextField();
        btnProximo = new vrframework.bean.button.VRButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(lblNomeConexao, "Nome da Conexão");

        cboConexao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboConexaoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaOrigem, "Loja Origem");

        cboOrigem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboOrigemActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaVR, "Loja VR");

        txtLojaVR.setEnabled(false);

        btnProximo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/proximo.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnProximo, "Pŕoximo");
        btnProximo.setEnabled(false);
        btnProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProximoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboConexao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboOrigem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNomeConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 619, Short.MAX_VALUE))
                    .addComponent(txtLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnProximo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNomeConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboConexao, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProximo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProximoActionPerformed
        try {
            construirInternalFrame();
        } catch (Exception ex) {
            ex.printStackTrace();
            Util.exibirMensagemErro(ex, "Seleção de Loja");
        }
    }//GEN-LAST:event_btnProximoActionPerformed

    private void cboConexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboConexaoActionPerformed
        getLojaMapeada();
    }//GEN-LAST:event_cboConexaoActionPerformed

    private void cboOrigemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOrigemActionPerformed
        getLojaVR();
    }//GEN-LAST:event_cboOrigemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SelecaoLojaGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SelecaoLojaGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SelecaoLojaGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SelecaoLojaGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SelecaoLojaGUI dialog = null;
                try {
                    dialog = new SelecaoLojaGUI();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnProximo;
    private vrframework.bean.comboBox.VRComboBox cboConexao;
    private vrframework.bean.comboBox.VRComboBox cboOrigem;
    private vrframework.bean.label.VRLabel lblLojaOrigem;
    private vrframework.bean.label.VRLabel lblLojaVR;
    private vrframework.bean.label.VRLabel lblNomeConexao;
    private vrframework.bean.textField.VRTextField txtLojaVR;
    // End of variables declaration//GEN-END:variables

}
