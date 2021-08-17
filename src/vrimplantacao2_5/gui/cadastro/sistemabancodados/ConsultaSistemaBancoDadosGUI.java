package vrimplantacao2_5.gui.cadastro.sistemabancodados;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vr.view.components.textfield.TextCase;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao2_5.controller.cadastro.sistemabancodados.SistemaBancoDadosController;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaBancoDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaSistemaBancoDadosGUI extends VRInternalFrame {
    
    private static ConsultaSistemaBancoDadosGUI consultaSistemaBancoDadosGUI = null;
    public SistemaBancoDadosController controller = null;
    private SistemaBancoDadosVO sistemaBancoDadosVO;
    private CadastroSistemaBancoDadosGUI cadastroSistemaBancoDadosGUI = null;

    /**
     * Creates new form ConsultaConfiguracaoBaseDadosGUI
     * @param main
     * @throws java.lang.Exception
     */
    public ConsultaSistemaBancoDadosGUI(VRMdiFrame main) throws Exception {
        super(main);
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() throws Exception {
        centralizarForm();
        setTitle("Consulta Sistemas x Banco Dados");
        
        txtFiltroSistema.setTextCase(TextCase.UPPERCASE);
        
        controller = new SistemaBancoDadosController(this);
        configurarColuna();
        sistemaBancoDadosVO = new SistemaBancoDadosVO();
        controller.consultar(sistemaBancoDadosVO);
        getBancoDados();
    }
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();

        column.add(new VRColumnTable("Sistema", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Banco Dados", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Schema", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Porta", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Usuário", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Senha", true, SwingConstants.LEFT, false, null));

        tblConsultaSistemaBancoDados.configurarColuna(column, this, "Consulta", "");
    }
    
    @Override
    public void consultar() throws Exception {
        Object[][] dados = new Object[controller.getSistemaBancoDados().size()][6];
        
        int i = 0;
        for (SistemaBancoDadosVO vo : controller.getSistemaBancoDados()) {
            dados[i][0] = vo.getNomeSistema();
            dados[i][1] = vo.getNomeBancoDados();
            dados[i][2] = vo.getNomeSchema();
            dados[i][3] = vo.getPorta();
            dados[i][4] = vo.getUsuario();
            dados[i][5] = vo.getSenha();
            
            i++;
        }

        tblConsultaSistemaBancoDados.setRowHeight(20);
        tblConsultaSistemaBancoDados.setModel(dados);
    }

    @Override
    public void editar() {
        sistemaBancoDadosVO = new SistemaBancoDadosVO();
        
        if(tblConsultaSistemaBancoDados.getLinhaSelecionada() == -1) {
            return;
        }
        
        sistemaBancoDadosVO = controller.getSistemaBancoDados().get(tblConsultaSistemaBancoDados.getLinhaSelecionada());
        
        exibirCadastroSistemaBancoDados(mdiFrame);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblConsultaSistemaBancoDados = new vrframework.bean.tableEx.VRTableEx();
        btnInserirConexao = new vrframework.bean.button.VRButton();
        txtFiltroSistema = new vr.view.components.textfield.VRTextField();
        btnInserirConexao1 = new vrframework.bean.button.VRButton();
        cboFiltroBancoDados = new vrframework.bean.comboBox.VRComboBox();
        vRLabel1 = new vr.view.components.label.VRLabel();
        vRLabel2 = new vr.view.components.label.VRLabel();

        tblConsultaSistemaBancoDados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblConsultaSistemaBancoDadosMouseClicked(evt);
            }
        });

        btnInserirConexao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/adicionar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnInserirConexao, "Cadastrar Sistema");
        btnInserirConexao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirConexaoActionPerformed(evt);
            }
        });

        btnInserirConexao1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/consultar_20.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnInserirConexao1, "Pesquisar");
        btnInserirConexao1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirConexao1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, "Sistema");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, "Banco de Dados");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblConsultaSistemaBancoDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFiltroSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboFiltroBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnInserirConexao1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnInserirConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFiltroSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInserirConexao1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboFiltroBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInserirConexao, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tblConsultaSistemaBancoDados, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblConsultaSistemaBancoDadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblConsultaSistemaBancoDadosMouseClicked
        if (evt.getClickCount() > 1) {
            try {
                editar();
            } catch (Exception ex) {
                ex.printStackTrace();
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_tblConsultaSistemaBancoDadosMouseClicked

    private void btnInserirConexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirConexaoActionPerformed
        CadastroSistemaBancoDadosGUI.consultaSistemaBancoDadosGUI = this;
        CadastroSistemaBancoDadosGUI.exibir(mdiFrame);
    }//GEN-LAST:event_btnInserirConexaoActionPerformed

    private void btnInserirConexao1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirConexao1ActionPerformed
        try {
            
            sistemaBancoDadosVO = new SistemaBancoDadosVO();
            
            sistemaBancoDadosVO.setNomeSistema(txtFiltroSistema.getText().trim());
            sistemaBancoDadosVO.setIdBancoDados(cboFiltroBancoDados.getId());
            
            controller.consultar(sistemaBancoDadosVO);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnInserirConexao1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnInserirConexao;
    private vrframework.bean.button.VRButton btnInserirConexao1;
    private vrframework.bean.comboBox.VRComboBox cboFiltroBancoDados;
    private vrframework.bean.tableEx.VRTableEx tblConsultaSistemaBancoDados;
    private vr.view.components.textfield.VRTextField txtFiltroSistema;
    private vr.view.components.label.VRLabel vRLabel1;
    private vr.view.components.label.VRLabel vRLabel2;
    // End of variables declaration//GEN-END:variables

    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (consultaSistemaBancoDadosGUI == null || consultaSistemaBancoDadosGUI.isClosed()) {
                consultaSistemaBancoDadosGUI = new ConsultaSistemaBancoDadosGUI(menuGUI);
            }

            consultaSistemaBancoDadosGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Banco de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
    private void exibirCadastroSistemaBancoDados(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (cadastroSistemaBancoDadosGUI == null || cadastroSistemaBancoDadosGUI.isClosed()) {
                cadastroSistemaBancoDadosGUI = new CadastroSistemaBancoDadosGUI(menuGUI);
            }
            
            CadastroSistemaBancoDadosGUI.consultaSistemaBancoDadosGUI = this;
            cadastroSistemaBancoDadosGUI.editar(this.sistemaBancoDadosVO);
            cadastroSistemaBancoDadosGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Cadastro Sistemas x Banco de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
    private void getBancoDados() throws Exception {
        cboFiltroBancoDados.setModel(new DefaultComboBoxModel());

        List<BancoDadosVO> bancoDados = controller.getBancoDados();

        if (bancoDados == null) {
            return;
        }

        cboFiltroBancoDados.addItem(new ItemComboVO(0, "SELECIONE O BANCO DE DADOS"));
        for (BancoDadosVO vo : bancoDados) {
            cboFiltroBancoDados.addItem(new ItemComboVO(vo.getId(), vo.getNome()));
        }
    }
    
}
