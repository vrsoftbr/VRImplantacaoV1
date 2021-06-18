package vrimplantacao2_5.gui.cadastro.configuracao;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrimplantacao2_5.controller.cadastro.configuracao.ConsultaConfiguracaoBancoDadosController;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaConfiguracaoBaseDadosGUI extends VRInternalFrame {
    
    private static ConsultaConfiguracaoBaseDadosGUI consultaConfiguracaoBaseDadosGUI = null;
    private ConsultaConfiguracaoBancoDadosController controller = null;

    /**
     * Creates new form ConsultaConfiguracaoBaseDadosGUI
     * @param main
     * @throws java.lang.Exception
     */
    public ConsultaConfiguracaoBaseDadosGUI(VRMdiFrame main) throws Exception {
        super(main);
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() throws Exception {
        centralizarForm();
        setTitle("Consulta Configuração Base de Dados");
        
        controller = new ConsultaConfiguracaoBancoDadosController();
        configurarColuna();
        consultar();
    }
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();

        column.add(new VRColumnTable("Descrição", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Sistema", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Banco", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Host", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Porta", true, SwingConstants.LEFT, false, null));

        tblConsultaConexao.configurarColuna(column, this, "Consulta", "");
    }
    
    @Override
    public void consultar() throws Exception {
        List<ConfiguracaoBancoVO> conexoes = controller.consultar();
        
        Object[][] dados = new Object[conexoes.size()][5];
        
        int i = 0;
        for (ConfiguracaoBancoVO cx : conexoes) {
            dados[i][0] = cx.getDescricao();
            dados[i][1] = cx.getSistema().getNome();
            dados[i][2] = cx.getBancoDados().getNome();
            dados[i][3] = cx.getHost();
            dados[i][4] = cx.getPorta();
            
            i++;
        }

        tblConsultaConexao.setRowHeight(20);
        tblConsultaConexao.setModel(dados);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblConsultaConexao = new vrframework.bean.tableEx.VRTableEx();
        btnInserirConexao = new vrframework.bean.button.VRButton();

        btnInserirConexao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/adicionar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnInserirConexao, "Cadastrar Conexão");
        btnInserirConexao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirConexaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblConsultaConexao, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnInserirConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnInserirConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblConsultaConexao, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInserirConexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirConexaoActionPerformed
        ConfiguracaoBaseDadosGUI.exibir(mdiFrame);
    }//GEN-LAST:event_btnInserirConexaoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnInserirConexao;
    private vrframework.bean.tableEx.VRTableEx tblConsultaConexao;
    // End of variables declaration//GEN-END:variables

    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (consultaConfiguracaoBaseDadosGUI == null || consultaConfiguracaoBaseDadosGUI.isClosed()) {
                consultaConfiguracaoBaseDadosGUI = new ConsultaConfiguracaoBaseDadosGUI(menuGUI);
            }

            consultaConfiguracaoBaseDadosGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Configuração de Base de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
}
