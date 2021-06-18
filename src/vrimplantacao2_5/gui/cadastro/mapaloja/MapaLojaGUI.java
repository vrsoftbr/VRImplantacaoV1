package vrimplantacao2_5.gui.cadastro.mapaloja;

import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.dialog.VRDialog;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.gui.cadastro.LojaConsultaGUI;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.controller.mapaloja.MapaLojaController;
import vrimplantacao2_5.gui.cadastro.configuracao.ConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.service.mapaloja.MapaLojaService;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoVO;

/**
 *
 * @author guilhermegomes
 */
public class MapaLojaGUI extends VRDialog {

    private MapaLojaService mapaLojaService = null;
    public LojaConsultaGUI lojaConsultaGUI = null;
    public ConfiguracaoBaseDadosGUI configuracaoBaseDadosGUI = null;
    private ConfiguracaoBancoVO configuracaoBancoVO = null;
    private ConfiguracaoBancoLojaVO configuracaoBancoLojaVO = null;
    private MapaLojaController mapaLojaController = null;
    
    /**
     * Creates new form MapaLojaGUI
     * @param mapaController
     */
    public MapaLojaGUI() {
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() {
        centralizarForm();
        setResizable(false);
        setModal(true);
        setTitle("Mapa de Loja");
        
        mapaLojaService = new MapaLojaService();
        configuracaoBancoLojaVO = new ConfiguracaoBancoLojaVO();
        
        carregarLojaVR();
        carregarLojaOrigem();
    }
    
    private void carregarLojaOrigem() {
        cboLojaOrigem.setModel(new DefaultComboBoxModel());
        
        cboLojaOrigem.addItem(new ItemComboVO("1", "SYSPDV - LOJA 01"));
        cboLojaOrigem.addItem(new ItemComboVO("2", "SYSPDV - LOJA 02"));
    }
    
    private void carregarLojaVR() {
        cboLojaVR.setModel(new DefaultComboBoxModel());
        
        for (LojaVO oLoja : mapaLojaService.getLojaVR()) {
            cboLojaVR.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
    }
    
    public void setConfiguracaoConexao(ConfiguracaoBancoVO configuracaoBancoVO) {
        this.configuracaoBancoVO = configuracaoBancoVO;
    }
    
    public void setMapaLojaController(MapaLojaController mapaLojaController) {
        this.mapaLojaController = mapaLojaController;
    }
    
    @Override
    public void salvar() {
         configuracaoBancoLojaVO.setIdLojaOrigem(((ItemComboVO) cboLojaOrigem.getSelectedItem()).idString);
         configuracaoBancoLojaVO.setIdLojaVR(cboLojaVR.getId());        
         configuracaoBancoVO.setConfiguracaoBancoLoja(configuracaoBancoLojaVO);
         
         mapaLojaController.salvar(configuracaoBancoVO);
         
         if (configuracaoBancoLojaVO.getId() != 0) {
             try {
                mapaLojaController.consultaLojaMapeada(configuracaoBancoVO.getId());
                
                Util.exibirMensagem("Loja Mapeada com sucesso!", getTitle());

                this.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                Exceptions.printStackTrace(ex);
            }
         }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chkMatriz = new vrframework.bean.checkBox.VRCheckBox();
        lblLojaOrigem = new vrframework.bean.label.VRLabel();
        cboLojaOrigem = new vrframework.bean.comboBox.VRComboBox();
        lblLojaVR = new vrframework.bean.label.VRLabel();
        chkEncerrada = new vrframework.bean.checkBox.VRCheckBox();
        btnParametro = new vrframework.bean.button.VRButton();
        btnSalvar = new vrframework.bean.button.VRButton();
        btnInserirLoja = new vrframework.bean.button.VRButton();
        cboLojaVR = new vrframework.bean.comboBox.VRComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(chkMatriz, org.openide.util.NbBundle.getMessage(MapaLojaGUI.class, "MapaLojaGUI.chkMatriz.text")); // NOI18N
        chkMatriz.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaOrigem, org.openide.util.NbBundle.getMessage(MapaLojaGUI.class, "MapaLojaGUI.lblLojaOrigem.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaVR, org.openide.util.NbBundle.getMessage(MapaLojaGUI.class, "MapaLojaGUI.lblLojaVR.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkEncerrada, org.openide.util.NbBundle.getMessage(MapaLojaGUI.class, "MapaLojaGUI.chkEncerrada.text")); // NOI18N
        chkEncerrada.setEnabled(false);

        btnParametro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/parametrizar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnParametro, org.openide.util.NbBundle.getMessage(MapaLojaGUI.class, "MapaLojaGUI.btnParametro.text")); // NOI18N

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSalvar, org.openide.util.NbBundle.getMessage(MapaLojaGUI.class, "MapaLojaGUI.btnSalvar.text")); // NOI18N
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnInserirLoja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vr/view/img/add-black-18x18.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnInserirLoja, org.openide.util.NbBundle.getMessage(MapaLojaGUI.class, "MapaLojaGUI.btnInserirLoja.text")); // NOI18N
        btnInserirLoja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirLojaActionPerformed(evt);
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
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkMatriz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkEncerrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 66, Short.MAX_VALUE)
                                .addComponent(btnParametro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cboLojaOrigem, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInserirLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMatriz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEncerrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnInserirLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnParametro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInserirLojaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirLojaActionPerformed
        exibirCadastroLoja();
    }//GEN-LAST:event_btnInserirLojaActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        salvar();
    }//GEN-LAST:event_btnSalvarActionPerformed

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
            java.util.logging.Logger.getLogger(MapaLojaGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MapaLojaGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MapaLojaGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MapaLojaGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MapaLojaGUI dialog = new MapaLojaGUI();
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
    private vrframework.bean.button.VRButton btnInserirLoja;
    private vrframework.bean.button.VRButton btnParametro;
    private vrframework.bean.button.VRButton btnSalvar;
    private vrframework.bean.comboBox.VRComboBox cboLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cboLojaVR;
    private vrframework.bean.checkBox.VRCheckBox chkEncerrada;
    private vrframework.bean.checkBox.VRCheckBox chkMatriz;
    private vrframework.bean.label.VRLabel lblLojaOrigem;
    private vrframework.bean.label.VRLabel lblLojaVR;
    // End of variables declaration//GEN-END:variables
    
    private void exibirCadastroLoja() {
        try {
            this.setWaitCursor();
            if (lojaConsultaGUI == null || lojaConsultaGUI.isClosed()) {
                lojaConsultaGUI = new LojaConsultaGUI(configuracaoBaseDadosGUI.parentFrame);
            }

            lojaConsultaGUI.setVisible(true);
            this.setVisible(false);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }
}
