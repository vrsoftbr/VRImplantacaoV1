package vrimplantacao2_5.gui.login;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import vr.core.collection.Properties;
import vr.core.parametro.versao.Versao;
import vr.implantacao.main.App;
import vrframework.bean.dialog.VRDialog;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.DadosConexaoPostgreSQL;
import vrimplantacao.gui.MenuGUI;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.classe.Global;
import vrimplantacao2_5.controller.atualizador.AtualizadorController;
import vrimplantacao2_5.controller.cadastro.unidade.UnidadeController;
import vrimplantacao2_5.controller.cadastro.usuario.UsuarioController;
import vrimplantacao2_5.vo.cadastro.UnidadeVO;
import vrimplantacao2_5.vo.cadastro.UsuarioVO;

public class LoginGUI extends VRDialog {

    private UsuarioVO oUsuario = null;
    private VRMdiFrame mdiFrame = null;
    private List<DadosConexaoPostgreSQL> vEmpresa = null;
    private AtualizadorController atualizadorController = null;
        
    /* Classes da versão 2.5 */
    UnidadeController unidadeController = new UnidadeController();
    UsuarioController usuarioController = new UsuarioController();
    

    public LoginGUI() throws Exception {
        initComponents();

        centralizarForm();
        
        criarEstrutura2_5();

        getUnidades();

        lblVersao.setText("Versão do banco " + Versao.createFromConnectionInterface(Conexao.getConexao()).getVersao());

        this.setModal(true);
    }

    private void criarEstrutura2_5() throws Exception {
        atualizadorController = new AtualizadorController();
        atualizadorController.criarEstrutura2_5();
    }
    
    private void getUnidades() throws Exception {
        cboUnidade.setModel(new DefaultComboBoxModel());
        
        List<UnidadeVO> unidadesVO = unidadeController.getUnidades();
        
        if (unidadesVO == null) {
            return;
        }
        
        unidadesVO.forEach((vo) -> {
            cboUnidade.addItem(new ItemComboVO(vo.getId(), vo.getNome()));
        });
    }
    
    public void autenticar() throws Exception {
        if (cboUnidade.getId() == -1) {
            cboUnidade.requestFocus();
            throw new VRException("Informe a Unidade!");
        }

        UsuarioVO vo = new UsuarioVO();
        vo.setLogin(txtUsuario.getText());
        vo.setSenha(txtSenha.getText());
        vo.setIdUnidade(cboUnidade.getId());
        
        List<UsuarioVO> usuarioVO = usuarioController.autenticar(vo);
        
        usuarioVO.stream().map((usuario) -> {
            Global.setNomeUsuario(usuario.getNome());
            return usuario;
        }).map((usuario) -> {
            Global.setIdUnidade(usuario.getIdUnidade());
            return usuario;
        }).forEachOrdered((usuario) -> {
            Global.setNomeUnidade(usuario.getDescricaoUnidade());
        });

        MenuGUI form = new MenuGUI(this);
        
        form.atualizarRodape();
        form.setVisible(true);
        form.checkParametros();

        if (mdiFrame != null) {
            mdiFrame.dispose();
        }

        mdiFrame = form;

        Properties oProperties = App.properties();

        if (chkLembrar.isSelected()) {
            oProperties.set("system.usuario", txtUsuario.getText());
        }

        this.dispose();

        form.requestFocus();
    }

    public void setUsuario(String i_usuario) throws Exception {
        txtUsuario.setText(i_usuario);
        chkLembrar.setSelected(true);
    }

    public void setSenha(String i_senha) throws Exception {
        txtSenha.setText(i_senha);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtUsuario = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        lblLoja = new vrframework.bean.label.VRLabel();
        txtSenha = new vrframework.bean.passwordField.VRPasswordField();
        chkLembrar = new vrframework.bean.checkBox.VRCheckBox();
        cboUnidade = new vrframework.bean.comboBox.VRComboBox();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnAutenticar = new vrframework.bean.button.VRButton();
        btnSair = new vrframework.bean.button.VRButton();
        lblPrograma = new vrframework.bean.label.VRLabel();
        lblVersao = new vrframework.bean.label.VRLabel();
        vRLabel5 = new vrframework.bean.label.VRLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("VR Loja");
        setIconImage(null);
        setUndecorated(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        vRPanel1.setBorder(null);
        vRPanel1.setOpaque(false);

        vRLabel1.setText("Usuário");

        txtUsuario.setColumns(12);
        txtUsuario.setObrigatorio(true);

        vRLabel2.setText("Senha");

        lblLoja.setText("Unidade");

        txtSenha.setColumns(11);
        txtSenha.setObrigatorio(true);

        chkLembrar.setText("Lembrar usuário");
        chkLembrar.setAlignmentY(0.0F);
        chkLembrar.setContentAreaFilled(false);
        chkLembrar.setFocusable(false);
        chkLembrar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkLembrar.setMargin(new java.awt.Insets(2, 0, 2, 0));
        chkLembrar.setPreferredSize(new java.awt.Dimension(65, 14));

        cboUnidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUnidadeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addGap(206, 206, 206)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(chkLembrar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLoja, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(cboUnidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(34, 34, 34))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(chkLembrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        getContentPane().add(vRPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 25, 370, 190));

        vRPanel3.setBorder(null);
        vRPanel3.setOpaque(false);

        btnAutenticar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/autenticar.png"))); // NOI18N
        btnAutenticar.setText("Autenticar");
        btnAutenticar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutenticarActionPerformed(evt);
            }
        });

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(174, Short.MAX_VALUE)
                .addComponent(btnAutenticar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAutenticar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(vRPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 370, 40));

        lblPrograma.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPrograma.setText("VR Implantação 2.5");
        lblPrograma.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        getContentPane().add(lblPrograma, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 180, 30));

        lblVersao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblVersao.setText("X.X.X");
        getContentPane().add(lblVersao, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 130, -1));

        vRLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        vRLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/login.png"))); // NOI18N
        vRLabel5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                vRLabel5KeyPressed(evt);
            }
        });
        getContentPane().add(vRLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 390, 260));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAutenticarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutenticarActionPerformed
        try {
            this.setWaitCursor();
            autenticar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnAutenticarActionPerformed
    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        try {
            this.setWaitCursor();
            sair();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnSairActionPerformed
    private void vRLabel5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_vRLabel5KeyPressed
    }//GEN-LAST:event_vRLabel5KeyPressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
    }//GEN-LAST:event_formKeyPressed

    private void cboUnidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUnidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboUnidadeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnAutenticar;
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.comboBox.VRComboBox cboUnidade;
    private vrframework.bean.checkBox.VRCheckBox chkLembrar;
    private vrframework.bean.label.VRLabel lblLoja;
    private vrframework.bean.label.VRLabel lblPrograma;
    private vrframework.bean.label.VRLabel lblVersao;
    private vrframework.bean.passwordField.VRPasswordField txtSenha;
    private vrframework.bean.textField.VRTextField txtUsuario;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel3;
    // End of variables declaration//GEN-END:variables
}
