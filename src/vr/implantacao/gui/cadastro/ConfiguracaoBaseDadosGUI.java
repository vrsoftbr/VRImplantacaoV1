package vr.implantacao.gui.cadastro;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vr.implantacao.controller.cadastro.ConfiguracaoBaseDadosController;
import vr.implantacao.vo.cadastro.BancoDadosVO;
import vr.implantacao.vo.cadastro.SistemaVO;
import vr.implantacao.vo.enums.EBancoDados;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosGUI extends VRInternalFrame {

    private static ConfiguracaoBaseDadosGUI configuracaoBaseDados = null;
    private ConfiguracaoBaseDadosController controller = null;
    
    /**
     * Creates new form ConfiguracaoPrincipalGUI
     */
    public ConfiguracaoBaseDadosGUI(VRMdiFrame menuGUI) throws Exception {
        super(menuGUI);
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() throws Exception {
        centralizarForm();
        this.setMaximum(false);
        setTitle("Configuração de Base de Dados");
        
        controller = new ConfiguracaoBaseDadosController();
        
        getSistema();
        configurarColuna();
    }
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();

        column.add(new VRColumnTable("Loja Anterior", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Desc. Anterior", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Loja VR", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Desc. VR", true, SwingConstants.LEFT, false, null));

        tblLoja.configurarColuna(column, this, "Loja", "");
    }
    
    private void getSistema() {
        cboSistema.setModel(new DefaultComboBoxModel());
        
        List<SistemaVO> sistemas = controller.getSistema();
        
        if (sistemas == null) {
            return;
        }
        
        for (SistemaVO vo : sistemas) {
            ItemComboVO it = new ItemComboVO();
            
            it.id = vo.getId();
            it.descricao = vo.getNome();
            
            cboSistema.addItem(it);
        }
    }
    
    private void getBancoDadosPorSistema(int idSistema) {
        cboBD.setModel(new DefaultComboBoxModel());
        
        List<BancoDadosVO> bancosPorSistema = controller.getBancoDadosPorSistema(idSistema);
        
        if (bancosPorSistema == null) {
            return;
        }
        
        for (BancoDadosVO bdVO : bancosPorSistema) {
            ItemComboVO it = new ItemComboVO();
            
            it.id = bdVO.getId();
            it.descricao = bdVO.getNome();
            
            cboBD.addItem(it);
        }
    }
    
    private void exibiPainelConexao(int idBancoDados) {
        tabConexao.removeAll();
        
        JPanel painelDeConexaoDinamico = controller.exibiPainelConexao(idBancoDados);
        
        if (painelDeConexaoDinamico == null) {
            try {
                Util.exibirMensagem("Nenhum painel configurado para o banco de dados " + 
                                                EBancoDados.getById(idBancoDados) + "!",
                        "Configuração de Base de Dados");
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            tabConexao.add(painelDeConexaoDinamico);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNomeCon = new vrframework.bean.label.VRLabel();
        txtNomeConexao = new vrframework.bean.textField.VRTextField();
        lblSistema = new vrframework.bean.label.VRLabel();
        cboSistema = new vrframework.bean.comboBox.VRComboBox();
        lblBD = new vrframework.bean.label.VRLabel();
        cboBD = new vrframework.bean.comboBox.VRComboBox();
        pnlLoja = new vrframework.bean.panel.VRPanel();
        btnMapear = new vrframework.bean.button.VRButton();
        tblLoja = new vrframework.bean.tableEx.VRTableEx();
        btnSalvar = new vrframework.bean.button.VRButton();
        tabConexao = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRButton1 = new vrframework.bean.button.VRButton();

        org.openide.awt.Mnemonics.setLocalizedText(lblNomeCon, "Nome da Conexão");

        org.openide.awt.Mnemonics.setLocalizedText(lblSistema, "Sistema");

        cboSistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSistemaActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblBD, "Banco de Dados");

        cboBD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBDActionPerformed(evt);
            }
        });

        pnlLoja.setBorder(javax.swing.BorderFactory.createTitledBorder("Loja"));

        btnMapear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/configurar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnMapear, "Mapear Loja");

        javax.swing.GroupLayout pnlLojaLayout = new javax.swing.GroupLayout(pnlLoja);
        pnlLoja.setLayout(pnlLojaLayout);
        pnlLojaLayout.setHorizontalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLojaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlLojaLayout.createSequentialGroup()
                        .addComponent(btnMapear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlLojaLayout.setVerticalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLojaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMapear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblLoja, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSalvar, "Salvar");

        tabConexao.setBorder(javax.swing.BorderFactory.createTitledBorder("Painel de Conexão"));

        vRButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/ignorar.png"))); // NOI18N
        vRButton1.setToolTipText("Dica!");
        vRButton1.setBorderPainted(false);
        vRButton1.setContentAreaFilled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cboSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboBD, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblNomeCon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtNomeConexao, javax.swing.GroupLayout.PREFERRED_SIZE, 526, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(vRButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabConexao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblNomeCon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNomeConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(vRButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabConexao, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboSistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSistemaActionPerformed
        getBancoDadosPorSistema(cboSistema.getId());
    }//GEN-LAST:event_cboSistemaActionPerformed

    private void cboBDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBDActionPerformed
        exibiPainelConexao(cboBD.getId());
    }//GEN-LAST:event_cboBDActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMapear;
    private vrframework.bean.button.VRButton btnSalvar;
    private vrframework.bean.comboBox.VRComboBox cboBD;
    private vrframework.bean.comboBox.VRComboBox cboSistema;
    private vrframework.bean.label.VRLabel lblBD;
    private vrframework.bean.label.VRLabel lblNomeCon;
    private vrframework.bean.label.VRLabel lblSistema;
    private vrframework.bean.panel.VRPanel pnlLoja;
    private vrframework.bean.tabbedPane.VRTabbedPane tabConexao;
    private vrframework.bean.tableEx.VRTableEx tblLoja;
    private vrframework.bean.textField.VRTextField txtNomeConexao;
    private vrframework.bean.button.VRButton vRButton1;
    // End of variables declaration//GEN-END:variables
    
    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (configuracaoBaseDados == null || configuracaoBaseDados.isClosed()) {
                configuracaoBaseDados = new ConfiguracaoBaseDadosGUI(menuGUI);
            }

            configuracaoBaseDados.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Mapeamento de Loja");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
}
