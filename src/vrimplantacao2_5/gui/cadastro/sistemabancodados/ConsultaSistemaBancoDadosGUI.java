package vrimplantacao2_5.gui.cadastro.sistemabancodados;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vr.view.components.textfield.TextCase;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrimplantacao2_5.controller.cadastro.sistemabancodados.SistemaBancoDadosController;
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
        
        txtFiltro.setTextCase(TextCase.UPPERCASE);
        
        controller = new SistemaBancoDadosController(this);
        configurarColuna();
        sistemaBancoDadosVO = new SistemaBancoDadosVO();
        controller.consultar(sistemaBancoDadosVO);
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
        
        exibirCadastroSistema(mdiFrame);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblConsultaSistemaBancoDados = new vrframework.bean.tableEx.VRTableEx();
        btnInserirConexao = new vrframework.bean.button.VRButton();
        txtFiltro = new vr.view.components.textfield.VRTextField();
        btnInserirConexao1 = new vrframework.bean.button.VRButton();

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblConsultaSistemaBancoDados, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnInserirConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInserirConexao1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInserirConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInserirConexao1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblConsultaSistemaBancoDados, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
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
            // TODO add your handling code here:
            controller.consultar(sistemaBancoDadosVO);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnInserirConexao1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnInserirConexao;
    private vrframework.bean.button.VRButton btnInserirConexao1;
    private vrframework.bean.tableEx.VRTableEx tblConsultaSistemaBancoDados;
    private vr.view.components.textfield.VRTextField txtFiltro;
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
    
    private void exibirCadastroSistema(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (cadastroSistemaBancoDadosGUI == null || cadastroSistemaBancoDadosGUI.isClosed()) {
                cadastroSistemaBancoDadosGUI = new CadastroSistemaBancoDadosGUI(menuGUI);
            }
            
            CadastroSistemaBancoDadosGUI.consultaSistemaBancoDadosGUI = this;
            cadastroSistemaBancoDadosGUI.editar(this.sistemaBancoDadosVO);
            cadastroSistemaBancoDadosGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Configuração de Base de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
}
