package vrimplantacao2_5.gui.cadastro.bancodados;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrimplantacao2_5.controller.cadastro.bancodados.BancoDadosController;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaBancoDadosGUI extends VRInternalFrame {
    
    private static ConsultaBancoDadosGUI consultaBancoDadosGUI = null;
    public BancoDadosController controller = null;
    private BancoDadosVO bancoDadosVO;
    private CadastroBancoDadosGUI cadastroBancoDados = null;

    /**
     * Creates new form ConsultaConfiguracaoBaseDadosGUI
     * @param main
     * @throws java.lang.Exception
     */
    public ConsultaBancoDadosGUI(VRMdiFrame main) throws Exception {
        super(main);
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() throws Exception {
        centralizarForm();
        setTitle("Consulta Banco de Dados");
        
        controller = new BancoDadosController(this);
        configurarColuna();
        controller.consultar("");
    }
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();

        column.add(new VRColumnTable("Nome", true, SwingConstants.LEFT, false, null));        

        tblConsultaBancoDados.configurarColuna(column, this, "Consulta", "");
    }
    
    @Override
    public void consultar() throws Exception {
        Object[][] dados = new Object[controller.getBancoDados().size()][1];
        
        int i = 0;
        for (BancoDadosVO vo : controller.getBancoDados()) {
            dados[i][0] = vo.getNome();
            
            i++;
        }

        tblConsultaBancoDados.setRowHeight(20);
        tblConsultaBancoDados.setModel(dados);
    }

    @Override
    public void editar() {
        bancoDadosVO = new BancoDadosVO();
        
        if(tblConsultaBancoDados.getLinhaSelecionada() == -1) {
            return;
        }
        
        bancoDadosVO = controller.getBancoDados().get(tblConsultaBancoDados.getLinhaSelecionada());
        
        exibirCadastroBancoDados(mdiFrame);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblConsultaBancoDados = new vrframework.bean.tableEx.VRTableEx();
        btnInserirConexao = new vrframework.bean.button.VRButton();
        txtFiltro = new vr.view.components.textfield.VRTextField();
        btnInserirConexao1 = new vrframework.bean.button.VRButton();

        tblConsultaBancoDados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblConsultaBancoDadosMouseClicked(evt);
            }
        });

        btnInserirConexao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/adicionar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnInserirConexao, "Cadastrar Banco de Dados");
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
                    .addComponent(tblConsultaBancoDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnInserirConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInserirConexao1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 30, Short.MAX_VALUE)))
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
                .addComponent(tblConsultaBancoDados, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblConsultaBancoDadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblConsultaBancoDadosMouseClicked
        if (evt.getClickCount() > 1) {
            try {
                editar();
            } catch (Exception ex) {
                ex.printStackTrace();
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_tblConsultaBancoDadosMouseClicked

    private void btnInserirConexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirConexaoActionPerformed
        CadastroBancoDadosGUI.consultaBancoDadosGUI = this;
        CadastroBancoDadosGUI.exibir(mdiFrame);
    }//GEN-LAST:event_btnInserirConexaoActionPerformed

    private void btnInserirConexao1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirConexao1ActionPerformed
        try {
            // TODO add your handling code here:
            controller.consultar(txtFiltro.getText().trim());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnInserirConexao1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnInserirConexao;
    private vrframework.bean.button.VRButton btnInserirConexao1;
    private vrframework.bean.tableEx.VRTableEx tblConsultaBancoDados;
    private vr.view.components.textfield.VRTextField txtFiltro;
    // End of variables declaration//GEN-END:variables

    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (consultaBancoDadosGUI == null || consultaBancoDadosGUI.isClosed()) {
                consultaBancoDadosGUI = new ConsultaBancoDadosGUI(menuGUI);
            }

            consultaBancoDadosGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Banco de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
    private void exibirCadastroBancoDados(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (cadastroBancoDados == null || cadastroBancoDados.isClosed()) {
                cadastroBancoDados = new CadastroBancoDadosGUI(menuGUI);
            }
            
            CadastroBancoDadosGUI.consultaBancoDadosGUI = this;
            cadastroBancoDados.editar(this.bancoDadosVO);
            cadastroBancoDados.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Configuração de Base de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
}
