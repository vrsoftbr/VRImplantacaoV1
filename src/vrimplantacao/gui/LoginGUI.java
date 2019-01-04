package vrimplantacao.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import vrframework.bean.dialog.VRDialog;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Conexao;
import vrframework.classe.Properties;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.PropertiesDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.UsuarioDAO;
import vrimplantacao.vo.loja.FornecedorVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.loja.UsuarioVO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.parametro.Versao;

public class LoginGUI extends VRDialog {

    private UsuarioVO oUsuario = null;
    private VRMdiFrame mdiFrame = null;
    private List<EmpresaVO> vEmpresa = null;

    public LoginGUI() throws Exception {
        initComponents();

        centralizarForm();

        cboLoja.setTabela("loja");
        cboLoja.carregar();
        cboLoja.setId(Global.idLoja);

        carregarEmpresa();
        
        Versao.carregar();
        lblVersao.setText("Versão do banco " + Versao.getVersao());

        this.setModal(true);
    }

    public void autenticar() throws Exception {
        if (cboLoja.getId() == -1) {
            cboLoja.requestFocus();
            throw new VRException("Informe a loja!");
        }

        if (Parametros.lite != null && !"".equals(Parametros.lite)) {
            if (txtUsuario.getText().equals("LITE") && txtSenha.getText().equals("VRLITE")) {
                oUsuario = new UsuarioVO();
                oUsuario.setNome("VR LITE");
            } else {
                throw new VRException("Usuário e/ou senha inválido(s)");
            }
        } else {
            oUsuario = new UsuarioDAO().autenticar(txtUsuario.getText(), txtSenha.getText(), cboLoja.getId());
        }

        new PropertiesDAO().carregarConfiguracao(new Properties(Util.getRoot() + "vr/implantacao/vrimplantacao.properties"));

        LojaVO oLoja = new LojaDAO().carregar(cboLoja.getId());

        FornecedorVO oFornecedor = new FornecedorDAO().carregar(oLoja.idFornecedor);

        Global.idUsuario = oUsuario.id;
        Global.usuario = oUsuario.nome;
        Global.idFornecedor = oLoja.idFornecedor;
        Global.fornecedor = oFornecedor.nomeFantasia;
        Global.idEstado = oFornecedor.idEstado;
        Global.idMunicipio = oFornecedor.idMunicipio;
        Global.idLoja = cboLoja.getId();
        Global.loja = oLoja.descricao;

        MenuGUI form = new MenuGUI(this);

        form.atualizarRodape();
        form.setVisible(true);
        form.checkParametros();

        if (mdiFrame != null) {
            mdiFrame.dispose();
        }

        mdiFrame = form;

        Properties oProperties = new Properties(Util.getRoot() + "vr/implantacao/vrimplantacao.properties");

        if (chkLembrar.isSelected() && !oProperties.getString("system.usuario").equals(txtUsuario.getText())) {
            oProperties.setPropertie("system.usuario", txtUsuario.getText());

        } else if (!chkLembrar.isSelected() && !oProperties.getString("system.usuario").equals("")) {
            oProperties.setPropertie("system.usuario", "");
        }

        this.dispose();

        form.requestFocus();
        form.verificarLite();
    }

    public void setUsuario(String i_usuario) throws Exception {
        txtUsuario.setText(i_usuario);
        chkLembrar.setSelected(true);
    }

    public void setSenha(String i_senha) throws Exception {
        txtSenha.setText(i_senha);
    }

    private void carregarEmpresa() throws Exception {
        Properties oProperties = new Properties(Util.getRoot() + "vr/vr.properties");

        vEmpresa = new ArrayList();
        cboEmpresa.removeAllItems();

        int i = 1;

        while (true) {
            String empresa = i == 1 ? "" : String.valueOf(i);

            if (oProperties.getString("database" + empresa + ".ip").isEmpty()) {
                break;
            }
            EmpresaVO oEmpresa = new EmpresaVO();
            oEmpresa.ipBanco = oProperties.getString("database" + empresa + ".ip");
            oEmpresa.ipSecBanco = oProperties.getString("database" + empresa + ".ipsec");
            oEmpresa.portaBanco = oProperties.getInt("database" + empresa + ".porta");
            oEmpresa.nomeBanco = oProperties.getString("database" + empresa + ".nome");
            oEmpresa.usuarioBanco = oProperties.getString("database" + empresa + ".usuario").isEmpty() ? "postgres" : oProperties.getString("database" + empresa + ".usuario");
            oEmpresa.senhaBanco = oProperties.getString("database" + empresa + ".senha").isEmpty() ? "postgres" : oProperties.getString("database" + empresa + ".senha");
            oEmpresa.alias = oProperties.getString("database" + empresa + ".alias").isEmpty() ? ("EMPRESA " + String.valueOf(i)) : oProperties.getString("database" + empresa + ".alias");

            vEmpresa.add(oEmpresa);
            cboEmpresa.addItem(new ItemComboVO(i, oEmpresa.alias));

            i++;
        }

        if (vEmpresa.size() <= 1) {
            cboEmpresa.setVisible(false);
            return;
        }

        lblLoja.setText("Empresa / Loja");
        cboEmpresa.setWide(true);

        cboEmpresa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conectarEmpresa();
                    Versao.carregar();
                    lblVersao.setText("Versão do banco " + Versao.getVersao());
                } catch (Exception ex) {
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        });
    }

    private void conectarEmpresa() throws Exception {
        if (cboEmpresa.getId() == -1) {
            return;
        }

        EmpresaVO oEmpresa = vEmpresa.get(cboEmpresa.getSelectedIndex());

        Conexao.abrirConexao(oEmpresa.ipBanco, oEmpresa.ipSecBanco, oEmpresa.portaBanco, oEmpresa.nomeBanco, oEmpresa.usuarioBanco, oEmpresa.senhaBanco);

        cboLoja.carregar();
        cboLoja.setId(Global.idLoja);

        Global.idUsuario = -1;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtUsuario = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        lblLoja = new vrframework.bean.label.VRLabel();
        txtSenha = new vrframework.bean.passwordField.VRPasswordField();
        cboLoja = new vrframework.bean.comboBox.VRComboBox();
        chkLembrar = new vrframework.bean.checkBox.VRCheckBox();
        cboEmpresa = new vrframework.bean.comboBox.VRComboBox();
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

        vRLabel2.setText("Senha");

        lblLoja.setText("Loja");

        txtSenha.setColumns(11);

        cboLoja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboLojaFocusLost(evt);
            }
        });

        chkLembrar.setText("Lembrar usuário");
        chkLembrar.setAlignmentY(0.0F);
        chkLembrar.setContentAreaFilled(false);
        chkLembrar.setFocusable(false);
        chkLembrar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkLembrar.setMargin(new java.awt.Insets(2, 0, 2, 0));
        chkLembrar.setPreferredSize(new java.awt.Dimension(65, 14));

        cboEmpresa.setFocusable(false);

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addGap(206, 206, 206)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(chkLembrar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLoja, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(cboEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(31, 31, 31))
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
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        lblPrograma.setForeground(new java.awt.Color(51, 51, 51));
        lblPrograma.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPrograma.setText("VR Implantação");
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

    private void cboLojaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboLojaFocusLost
    }//GEN-LAST:event_cboLojaFocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnAutenticar;
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.comboBox.VRComboBox cboEmpresa;
    private vrframework.bean.comboBox.VRComboBox cboLoja;
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

class EmpresaVO {

    public String ipBanco = "";
    public String ipSecBanco = "";
    public int portaBanco = 0;
    public String nomeBanco = "";
    public String usuarioBanco = "";
    public String senhaBanco = "";
    public String alias = "";
}
