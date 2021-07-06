package vrimplantacao2_5.gui.componente.conexao.configuracao;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import vrframework.bean.panel.VRPanel;
import vrframework.remote.ItemComboVO;
import vrimplantacao2_5.controller.selecaoloja.SelecaoLojaController;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDados extends VRPanel {

    private SelecaoLojaController controller = null;
    private List<ConfiguracaoBancoLojaVO> lojas = null;
    
    /**
     * Creates new form ConfiguracaoBaseDados
     */
    public ConfiguracaoBaseDados() throws Exception {
        initComponents();
    }
    
    private void setConfiguracao() throws Exception {
        this.controller = new SelecaoLojaController();
        getNomeConexao();
    }
    
    private void getNomeConexao() throws Exception {
        cboConexao.setModel(new DefaultComboBoxModel());
        
        List<ConfiguracaoBaseDadosVO> conexoes = controller.consultar();
        
        for (ConfiguracaoBaseDadosVO configuracaoVO : conexoes) {
            cboConexao.addItem(new ItemComboVO(configuracaoVO.getId(), configuracaoVO.getDescricao()));
        }
    }
    
    private void getLojaMapeada() {
        cboOrigem.setModel(new DefaultComboBoxModel());
        
        lojas = controller.getLojaMapeada(cboConexao.getId());
        
        for (ConfiguracaoBancoLojaVO configuracaoLojaVO : lojas) {
            cboOrigem.addItem(new ItemComboVO(configuracaoLojaVO.getIdLojaOrigem(), 
                                              configuracaoLojaVO.getIdLojaOrigem() + " - " + 
                                              (configuracaoLojaVO.isLojaMatriz() ? "MATRIZ" : "FILIAL")));
        }
    }
    
    private void getLojaVR() {
        ConfiguracaoBancoLojaVO configuracaoVO = lojas.get(cboOrigem.getSelectedIndex());
        
        txtLojaVR.setText(configuracaoVO.getIdLojaVR() + " - " + configuracaoVO.getDescricaoVR());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblConexao = new vrframework.bean.label.VRLabel();
        cboConexao = new vrframework.bean.comboBox.VRComboBox();
        lblLojaOrigem = new vrframework.bean.label.VRLabel();
        cboOrigem = new vrframework.bean.comboBox.VRComboBox();
        lblLojaVR = new vrframework.bean.label.VRLabel();
        txtLojaVR = new vrframework.bean.textField.VRTextField();
        btnConectar = new javax.swing.JToggleButton();
        txtComplemento = new vrframework.bean.textField.VRTextField();
        lblComplemento = new vrframework.bean.label.VRLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Componente de Conexão"));

        org.openide.awt.Mnemonics.setLocalizedText(lblConexao, "Conexão Cadastrada");

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

        btnConectar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnConectar, "Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblComplemento, "Complemento");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboConexao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnConectar)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboOrigem, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboConexao, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(txtComplemento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConectar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboOrigemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOrigemActionPerformed
        getLojaVR();
    }//GEN-LAST:event_cboOrigemActionPerformed

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        /*try {
            if (conexao != null) {
                conexao.close();
            }

            validarDadosAcesso();
            btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao conectar");
            btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png")));
        }*/
    }//GEN-LAST:event_btnConectarActionPerformed

    private void cboConexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboConexaoActionPerformed
        getLojaMapeada();
    }//GEN-LAST:event_cboConexaoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectar;
    private vrframework.bean.comboBox.VRComboBox cboConexao;
    private vrframework.bean.comboBox.VRComboBox cboOrigem;
    private vrframework.bean.label.VRLabel lblComplemento;
    private vrframework.bean.label.VRLabel lblConexao;
    private vrframework.bean.label.VRLabel lblLojaOrigem;
    private vrframework.bean.label.VRLabel lblLojaVR;
    private vrframework.bean.textField.VRTextField txtComplemento;
    private vrframework.bean.textField.VRTextField txtLojaVR;
    // End of variables declaration//GEN-END:variables
}
