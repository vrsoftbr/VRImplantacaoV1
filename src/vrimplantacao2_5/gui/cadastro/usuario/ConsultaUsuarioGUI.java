package vrimplantacao2_5.gui.cadastro.usuario;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vr.view.components.textfield.TextCase;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrimplantacao2_5.controller.cadastro.usuario.UsuarioController;
import vrimplantacao2_5.vo.cadastro.UsuarioVO;

/**
 *
 * @author Desenvolvimento
 */
public class ConsultaUsuarioGUI extends VRInternalFrame {
    
    private static ConsultaUsuarioGUI consultaUsuarioGUI = null;
    public UsuarioController controller = null;
    private UsuarioVO usuarioVO;
    private CadastroUsuarioGUI cadastroUsuarioGUI = null;

    /**
     * Creates new form ConsultaConfiguracaoBaseDadosGUI
     * @param main
     * @throws java.lang.Exception
     */
    public ConsultaUsuarioGUI(VRMdiFrame main) throws Exception {
        super(main);
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() throws Exception {
        centralizarForm();
        setTitle("Consulta Usuários VRImplantacao");
        
        txtFiltroUsuario.setTextCase(TextCase.UPPERCASE);
        
        controller = new UsuarioController(this);
        configurarColuna();
        usuarioVO = new UsuarioVO();
        controller.consultar(usuarioVO);
    }
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();

        column.add(new VRColumnTable("Unidade", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Município", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Estado", true, SwingConstants.LEFT, false, null));

        tblConsulta.configurarColuna(column, this, "Consulta", "");
    }
    
    @Override
    public void consultar() throws Exception {
        Object[][] dados = new Object[controller.getUsuarios().size()][3];
        
        int i = 0;
        for (UsuarioVO vo : controller.getUsuarios()) {
            dados[i][0] = vo.getNome();
            dados[i][1] = vo.getLogin();
            dados[i][2] = vo.getDescricaoUnidade();
            
            i++;
        }

        tblConsulta.setRowHeight(20);
        tblConsulta.setModel(dados);
    }

    @Override
    public void editar() {
        usuarioVO = new UsuarioVO();
        
        if(tblConsulta.getLinhaSelecionada() == -1) {
            return;
        }
        
        usuarioVO = controller.getUsuarios().get(tblConsulta.getLinhaSelecionada());
        
        exibirCadastroUsuario(mdiFrame);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblConsulta = new vrframework.bean.tableEx.VRTableEx();
        btnInserir = new vrframework.bean.button.VRButton();
        txtFiltroUsuario = new vr.view.components.textfield.VRTextField();
        btnPesquisar = new vrframework.bean.button.VRButton();
        vRLabel1 = new vr.view.components.label.VRLabel();

        tblConsulta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblConsultaMouseClicked(evt);
            }
        });

        btnInserir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/adicionar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnInserir, "Cadastrar Usuário");
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

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, "Usuario");

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
                            .addComponent(txtFiltroUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInserir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFiltroUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInserir, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
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
        CadastroUsuarioGUI.consultaUsuarioGUI = this;
        CadastroUsuarioGUI.exibir(mdiFrame);
    }//GEN-LAST:event_btnInserirActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        try {
            
            usuarioVO = new UsuarioVO();
            
            usuarioVO.setNome(txtFiltroUsuario.getText().trim());
            
            controller.consultar(usuarioVO);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnPesquisarActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnInserir;
    private vrframework.bean.button.VRButton btnPesquisar;
    private vrframework.bean.tableEx.VRTableEx tblConsulta;
    private vr.view.components.textfield.VRTextField txtFiltroUsuario;
    private vr.view.components.label.VRLabel vRLabel1;
    // End of variables declaration//GEN-END:variables

    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (consultaUsuarioGUI == null || consultaUsuarioGUI.isClosed()) {
                consultaUsuarioGUI = new ConsultaUsuarioGUI(menuGUI);
            }

            consultaUsuarioGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Unidades");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
    private void exibirCadastroUsuario(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();     
            
            if (cadastroUsuarioGUI == null || cadastroUsuarioGUI.isClosed()) {
                cadastroUsuarioGUI = new CadastroUsuarioGUI(menuGUI);
            }
            
            CadastroUsuarioGUI.consultaUsuarioGUI = this;
            cadastroUsuarioGUI.editar(this.usuarioVO);
            cadastroUsuarioGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Cadastro Unidades VR");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
}
