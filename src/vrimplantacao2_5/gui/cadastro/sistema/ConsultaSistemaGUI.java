package vrimplantacao2_5.gui.cadastro.sistema;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vr.view.components.textfield.TextCase;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrimplantacao2_5.controller.cadastro.sistema.SistemaController;
import vrimplantacao2_5.vo.cadastro.SistemaVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaSistemaGUI extends VRInternalFrame {
    
    private static ConsultaSistemaGUI consultaSistemaGUI = null;
    public SistemaController controller = null;
    private SistemaVO sistemaVO;
    private CadastroSistemaGUI cadastroSistema = null;

    /**
     * Creates new form ConsultaConfiguracaoBaseDadosGUI
     * @param main
     * @throws java.lang.Exception
     */
    public ConsultaSistemaGUI(VRMdiFrame main) throws Exception {
        super(main);
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() throws Exception {
        centralizarForm();
        setTitle("Consulta Sistemas");
        
        txtFiltro.setTextCase(TextCase.UPPERCASE);
        
        controller = new SistemaController(this);
        configurarColuna();
        controller.consultar("");
    }
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();

        column.add(new VRColumnTable("Nome", true, SwingConstants.LEFT, false, null));        

        tblConsulta.configurarColuna(column, this, "Consulta", "");
    }
    
    @Override
    public void consultar() throws Exception {
        Object[][] dados = new Object[controller.getSistema().size()][1];
        
        int i = 0;
        for (SistemaVO vo : controller.getSistema()) {
            dados[i][0] = vo.getNome();
            
            i++;
        }

        tblConsulta.setRowHeight(20);
        tblConsulta.setModel(dados);
    }

    @Override
    public void editar() {
        sistemaVO = new SistemaVO();
        
        if(tblConsulta.getLinhaSelecionada() == -1) {
            return;
        }
        
        sistemaVO = controller.getSistema().get(tblConsulta.getLinhaSelecionada());
        
        exibirCadastroSistema(mdiFrame);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblConsulta = new vrframework.bean.tableEx.VRTableEx();
        btnInserir = new vrframework.bean.button.VRButton();
        txtFiltro = new vr.view.components.textfield.VRTextField();
        btnPesquisar = new vrframework.bean.button.VRButton();
        vRLabel1 = new vr.view.components.label.VRLabel();

        tblConsulta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblConsultaMouseClicked(evt);
            }
        });

        btnInserir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/adicionar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnInserir, "Cadastrar Sistema");
        btnInserir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirActionPerformed(evt);
            }
        });

        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/consultar_20.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnPesquisar, "Pesquisar");
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, "Nome");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnInserir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInserir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblConsultaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblConsultaMouseClicked
        if (evt.getClickCount() > 1) {
            try {
                editar();
            } catch (Exception ex) {
                ex.printStackTrace();
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_tblConsultaMouseClicked

    private void btnInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirActionPerformed
        CadastroSistemaGUI.consultaSistemaGUI = this;
        CadastroSistemaGUI.exibir(mdiFrame);
    }//GEN-LAST:event_btnInserirActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        try {
            // TODO add your handling code here:
            controller.consultar(txtFiltro.getText().trim());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnPesquisarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnInserir;
    private vrframework.bean.button.VRButton btnPesquisar;
    private vrframework.bean.tableEx.VRTableEx tblConsulta;
    private vr.view.components.textfield.VRTextField txtFiltro;
    private vr.view.components.label.VRLabel vRLabel1;
    // End of variables declaration//GEN-END:variables

    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (consultaSistemaGUI == null || consultaSistemaGUI.isClosed()) {
                consultaSistemaGUI = new ConsultaSistemaGUI(menuGUI);
            }

            consultaSistemaGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Banco de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
    private void exibirCadastroSistema(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (cadastroSistema == null || cadastroSistema.isClosed()) {
                cadastroSistema = new CadastroSistemaGUI(menuGUI);
            }
            
            CadastroSistemaGUI.consultaSistemaGUI = this;
            cadastroSistema.editar(this.sistemaVO);
            cadastroSistema.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Configuração de Base de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
}
