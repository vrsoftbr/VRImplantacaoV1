package vrimplantacao2_5.gui.cadastro.mapaloja;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.dialog.VRDialog;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.gui.cadastro.LojaConsultaGUI;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2_5.controller.cadastro.configuracao.MapaLojaController;
import vrimplantacao2_5.controller.migracao.MigracaoSistemasController;
import vrimplantacao2_5.gui.cadastro.configuracao.ConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class MapaLojaGUI extends VRDialog {

    public LojaConsultaGUI lojaConsultaGUI = null;
    public ConfiguracaoBaseDadosGUI configuracaoBaseDadosGUI = null;
    private ConfiguracaoBaseDadosVO configuracaoBancoVO = null;
    private ConfiguracaoBancoLojaVO configuracaoBancoLojaVO = null;
    private MapaLojaController mapaLojaController = null;
    private MigracaoSistemasController migracaoSistemasController;
    
    /**
     * Creates new form MapaLojaGUI
     * @throws java.lang.Exception
     */
    public MapaLojaGUI() throws Exception {
        initComponents();
    }
    
    public void setConfiguracao() throws Exception {
        centralizarForm();
        setResizable(false);
        setModal(true);
        setTitle("Mapa de Loja");
        
        configuracaoBancoLojaVO = new ConfiguracaoBancoLojaVO();
        migracaoSistemasController = new MigracaoSistemasController();
        
        carregarLojaVR();
        carregarLojaOrigem();
    }
    
    private void carregarLojaOrigem() throws Exception {
        cboLojaOrigem.setModel(new DefaultComboBoxModel());

        List<Estabelecimento> lojas = migracaoSistemasController.
                                        getLojasOrigem(MigracaoSistemasController.getIdSistema(), 
                                                            MigracaoSistemasController.getIdBancoDados());
        
        for (Estabelecimento loja : lojas) {
            cboLojaOrigem.addItem(new ItemComboVO(loja.cnpj, loja.razao));
        }
    }
    
    private void carregarLojaVR() {
        cboLojaVR.setModel(new DefaultComboBoxModel());
        
        for (LojaVO oLoja : mapaLojaController.getLojaVR()) {
            cboLojaVR.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
    }
    
    public void setConfiguracaoConexao(ConfiguracaoBaseDadosVO configuracaoBancoVO) {
        this.configuracaoBancoVO = configuracaoBancoVO;
    }
    
    public void setMapaLojaController(MapaLojaController mapaLojaController) {
        this.mapaLojaController = mapaLojaController;
    }
    
    @Override
    public void salvar() throws Exception {
         configuracaoBancoLojaVO.setIdLojaOrigem(((ItemComboVO) cboLojaOrigem.getSelectedItem()).idString);
         configuracaoBancoLojaVO.setIdLojaVR(cboLojaVR.getId());
         configuracaoBancoLojaVO.setLojaMatriz(chkMatriz.isSelected());
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
        btnParametro = new vrframework.bean.button.VRButton();
        btnSalvar = new vrframework.bean.button.VRButton();
        btnInserirLoja = new vrframework.bean.button.VRButton();
        cboLojaVR = new vrframework.bean.comboBox.VRComboBox();
        btnDica = new vrframework.bean.button.VRButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(chkMatriz, "Mix de Produto Principal");

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaOrigem, "Loja Origem");

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaVR, "Loja VR");

        btnParametro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/parametrizar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnParametro, "Parâmetro Loja");

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSalvar, "Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnInserirLoja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vr/view/img/add-black-18x18.png"))); // NOI18N
        btnInserirLoja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirLojaActionPerformed(evt);
            }
        });

        btnDica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/ignorar.png"))); // NOI18N
        btnDica.setToolTipText("Dica!");
        btnDica.setBorderPainted(false);
        btnDica.setContentAreaFilled(false);
        btnDica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDicaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addComponent(btnInserirLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkMatriz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDica, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkMatriz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDica, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        try {
            salvar();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnDicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDicaActionPerformed
        try {
            Util.exibirMensagem("Será mantido o código dos produtos\n"
                    + "da loja mapeada com este checkbox selecionado!", getTitle());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnDicaActionPerformed

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
                MapaLojaGUI dialog = null;
                try {
                    dialog = new MapaLojaGUI();
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
    private vrframework.bean.button.VRButton btnDica;
    private vrframework.bean.button.VRButton btnInserirLoja;
    private vrframework.bean.button.VRButton btnParametro;
    private vrframework.bean.button.VRButton btnSalvar;
    private vrframework.bean.comboBox.VRComboBox cboLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cboLojaVR;
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
