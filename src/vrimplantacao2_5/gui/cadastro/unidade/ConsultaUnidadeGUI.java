package vrimplantacao2_5.gui.cadastro.unidade;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vr.view.components.textfield.TextCase;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrimplantacao2_5.controller.cadastro.unidade.UnidadeController;
import vrimplantacao2_5.vo.cadastro.UnidadeVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaUnidadeGUI extends VRInternalFrame {
    
    private static ConsultaUnidadeGUI consultaUnidadeGUI = null;
    public UnidadeController controller = null;
    private UnidadeVO unidadeVO;
    private CadastroUnidadeGUI cadastroUnidadeGUI = null;

    /**
     * Creates new form ConsultaConfiguracaoBaseDadosGUI
     * @param main
     * @throws java.lang.Exception
     */
    public ConsultaUnidadeGUI(VRMdiFrame main) throws Exception {
        super(main);
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() throws Exception {
        centralizarForm();
        setTitle("Consulta Unidades VR");
        
        txtFiltroUnidade.setTextCase(TextCase.UPPERCASE);
        
        controller = new UnidadeController(this);
        configurarColuna();
        unidadeVO = new UnidadeVO();
        controller.consultar(unidadeVO);
    }
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();

        column.add(new VRColumnTable("Unidade", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Município", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Estado", true, SwingConstants.LEFT, false, null));

        tblConsultaUnidade.configurarColuna(column, this, "Consulta", "");
    }
    
    @Override
    public void consultar() throws Exception {
        Object[][] dados = new Object[controller.getUnidade().size()][3];
        
        int i = 0;
        for (UnidadeVO vo : controller.getUnidade()) {
            dados[i][0] = vo.getNome();
            dados[i][1] = vo.getDescricaoMunicipio();
            dados[i][2] = vo.getDescricaoEstado();
            
            i++;
        }

        tblConsultaUnidade.setRowHeight(20);
        tblConsultaUnidade.setModel(dados);
    }

    @Override
    public void editar() {
        unidadeVO = new UnidadeVO();
        
        if(tblConsultaUnidade.getLinhaSelecionada() == -1) {
            return;
        }
        
        unidadeVO = controller.getUnidade().get(tblConsultaUnidade.getLinhaSelecionada());
        
        exibirCadastroUnidade(mdiFrame);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblConsultaUnidade = new vrframework.bean.tableEx.VRTableEx();
        btnInserirUnidade = new vrframework.bean.button.VRButton();
        txtFiltroUnidade = new vr.view.components.textfield.VRTextField();
        btnPesquisar = new vrframework.bean.button.VRButton();
        vRLabel1 = new vr.view.components.label.VRLabel();

        tblConsultaUnidade.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblConsultaUnidadeMouseClicked(evt);
            }
        });

        btnInserirUnidade.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/adicionar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnInserirUnidade, "Cadastrar Unidade");
        btnInserirUnidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirUnidadeActionPerformed(evt);
            }
        });

        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/consultar_20.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnPesquisar, "Pesquisar");
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, "Unidade");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblConsultaUnidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFiltroUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInserirUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFiltroUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInserirUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tblConsultaUnidade, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblConsultaUnidadeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblConsultaUnidadeMouseClicked
        if (evt.getClickCount() > 1) {
            try {
                editar();
            } catch (Exception ex) {
                ex.printStackTrace();
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_tblConsultaUnidadeMouseClicked

    private void btnInserirUnidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirUnidadeActionPerformed
        CadastroUnidadeGUI.consultaUnidadeGUI = this;
        CadastroUnidadeGUI.exibir(mdiFrame);
    }//GEN-LAST:event_btnInserirUnidadeActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        try {
            
            unidadeVO = new UnidadeVO();
            
            unidadeVO.setNome(txtFiltroUnidade.getText().trim());
            
            controller.consultar(unidadeVO);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnPesquisarActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnInserirUnidade;
    private vrframework.bean.button.VRButton btnPesquisar;
    private vrframework.bean.tableEx.VRTableEx tblConsultaUnidade;
    private vr.view.components.textfield.VRTextField txtFiltroUnidade;
    private vr.view.components.label.VRLabel vRLabel1;
    // End of variables declaration//GEN-END:variables

    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (consultaUnidadeGUI == null || consultaUnidadeGUI.isClosed()) {
                consultaUnidadeGUI = new ConsultaUnidadeGUI(menuGUI);
            }

            consultaUnidadeGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Unidades");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
    private void exibirCadastroUnidade(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (cadastroUnidadeGUI == null || cadastroUnidadeGUI.isClosed()) {
                cadastroUnidadeGUI = new CadastroUnidadeGUI(menuGUI);
            }
            
            CadastroUnidadeGUI.consultaUnidadeGUI = this;
            cadastroUnidadeGUI.editar(this.unidadeVO);
            cadastroUnidadeGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Cadastro Unidades VR");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
}
