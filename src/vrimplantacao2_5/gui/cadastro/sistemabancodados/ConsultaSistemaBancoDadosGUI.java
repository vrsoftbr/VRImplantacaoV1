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

        tblConsulta.configurarColuna(column, this, "Consulta", "");
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

        tblConsulta.setRowHeight(20);
        tblConsulta.setModel(dados);
    }

    @Override
    public void editar() {
        sistemaBancoDadosVO = new SistemaBancoDadosVO();
        
        if(tblConsulta.getLinhaSelecionada() == -1) {
            return;
        }
        
        sistemaBancoDadosVO = controller.getSistemaBancoDados().get(tblConsulta.getLinhaSelecionada());
        
        exibirCadastroSistemaBancoDados(mdiFrame);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblConsulta = new vrframework.bean.tableEx.VRTableEx();
        btnInserir = new vrframework.bean.button.VRButton();
        txtFiltroSistema = new vr.view.components.textfield.VRTextField();
        btnPesquisar = new vrframework.bean.button.VRButton();
        cboFiltroBancoDados = new vrframework.bean.comboBox.VRComboBox();
        vRLabel1 = new vr.view.components.label.VRLabel();
        vRLabel2 = new vr.view.components.label.VRLabel();

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

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, "Sistema");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, "Banco de Dados");

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
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFiltroSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboFiltroBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnInserir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboFiltroBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInserir, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
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
        CadastroSistemaBancoDadosGUI.consultaSistemaBancoDadosGUI = this;
        CadastroSistemaBancoDadosGUI.exibir(mdiFrame);
    }//GEN-LAST:event_btnInserirActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        try {
            
            sistemaBancoDadosVO = new SistemaBancoDadosVO();
            
            sistemaBancoDadosVO.setNomeSistema(txtFiltroSistema.getText().trim());
            sistemaBancoDadosVO.setIdBancoDados(cboFiltroBancoDados.getId());
            
            controller.consultar(sistemaBancoDadosVO);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnPesquisarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnInserir;
    private vrframework.bean.button.VRButton btnPesquisar;
    private vrframework.bean.comboBox.VRComboBox cboFiltroBancoDados;
    private vrframework.bean.tableEx.VRTableEx tblConsulta;
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
