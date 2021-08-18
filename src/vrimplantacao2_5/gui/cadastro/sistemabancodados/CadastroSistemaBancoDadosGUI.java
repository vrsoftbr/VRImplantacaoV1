package vrimplantacao2_5.gui.cadastro.sistemabancodados;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.controller.cadastro.sistemabancodados.SistemaBancoDadosController;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaBancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;

public class CadastroSistemaBancoDadosGUI extends VRInternalFrame {

    private LojaVO oLoja = null;
    public VRMdiFrame parentFrame = null;
    public static CadastroSistemaBancoDadosGUI cadastroSistemaBancoDadosGUI = null;
    public static ConsultaSistemaBancoDadosGUI consultaSistemaBancoDadosGUI = null;
    private SistemaBancoDadosController sistemaBancoDadosController = null;
    private SistemaBancoDadosVO sistemaBancoDadosVO = null;
    private int idSistemaBancoDados = 0;

    public CadastroSistemaBancoDadosGUI(VRMdiFrame menuGUI) throws Exception {
        super(menuGUI);
        initComponents();
        
        centralizarForm();
        setConfigurarCampos();
        
        this.parentFrame = menuGUI;
        sistemaBancoDadosController = new SistemaBancoDadosController();
        consultaSistemaBancoDadosGUI = new ConsultaSistemaBancoDadosGUI(menuGUI);
        
        getBancoDados();
        getSistemas();
    }
    
    @Override
    public void incluir() throws Exception {
    }

    private void setConfigurarCampos() {
        txtPorta.setObrigatorio(false);
        txtPorta.setCaixaAlta(false);
        txtNomeSchema.setObrigatorio(false);
        txtNomeSchema.setCaixaAlta(false);
        txtUsuario.setObrigatorio(false);
        txtUsuario.setCaixaAlta(false);
        txtSenha.setObrigatorio(false);
        txtSenha.setCaixaAlta(false);        
    }
    
    @Override
    public void salvar() throws Exception {
        SistemaBancoDadosVO vo = new SistemaBancoDadosVO();
        vo.setIdSistema(cboSistema.getId());
        vo.setIdBancoDados(cboBancoDados.getId());
        vo.setPorta(Integer.parseInt(txtPorta.getText().trim()));
        vo.setNomeSchema(txtNomeSchema.getText().trim());
        vo.setUsuario(txtUsuario.getText().trim());
        vo.setSenha(txtSenha.getText().trim());
        
        if (idSistemaBancoDados <= 0) {
            sistemaBancoDadosController.inserir(vo);
        } else {
            vo.setId(idSistemaBancoDados);
            sistemaBancoDadosController.alterar(vo);
        }

        if (vo.getId() != 0) {
            idSistemaBancoDados = vo.getId();
            consultaSistemaBancoDadosGUI.controller.consultar(null);

            try {
                Util.exibirMensagem("Sistema x Banco de Dados salvo com sucesso!", getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }
    }

    public void editar(SistemaBancoDadosVO sistemaBancoDadosVO) throws Exception {
        this.sistemaBancoDadosVO = sistemaBancoDadosVO;

        idSistemaBancoDados = sistemaBancoDadosVO.getId();
        cboSistema.setId(sistemaBancoDadosVO.getIdSistema());
        cboBancoDados.setId(sistemaBancoDadosVO.getIdBancoDados());
        txtPorta.setText(String.valueOf(sistemaBancoDadosVO.getPorta()));
        txtNomeSchema.setText(sistemaBancoDadosVO.getNomeSchema());
        txtUsuario.setText(sistemaBancoDadosVO.getUsuario());
        txtSenha.setText(sistemaBancoDadosVO.getSenha());
    }
    
    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();

            if (cadastroSistemaBancoDadosGUI == null || cadastroSistemaBancoDadosGUI.isClosed()) {
                cadastroSistemaBancoDadosGUI = new CadastroSistemaBancoDadosGUI(menuGUI);
            }

            cadastroSistemaBancoDadosGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Cadastro Sistemas x Banco de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
    private void getSistemas() throws Exception {
        cboSistema.setModel(new DefaultComboBoxModel());
        
        List<SistemaVO> sistemas = consultaSistemaBancoDadosGUI.controller.getSistemas();
        
        if (sistemas == null) {
            return ;
        }
        
        for (SistemaVO vo : sistemas) {
            cboSistema.addItem(new ItemComboVO(vo.getId(), vo.getNome()));
        }
    }
    
    private void getBancoDados() throws Exception {
        cboBancoDados.setModel(new DefaultComboBoxModel());

        List<BancoDadosVO> bancoDados = consultaSistemaBancoDadosGUI.controller.getBancoDados();

        if (bancoDados == null) {
            return;
        }

        for (BancoDadosVO vo : bancoDados) {
            cboBancoDados.addItem(new ItemComboVO(vo.getId(), vo.getNome()));
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRToolBar1 = new vrframework.bean.toolBar.VRToolBar();
        btnTbIncluir = new vrframework.bean.button.VRButton();
        btnTbSalvar = new vrframework.bean.button.VRButton();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtSenha = new vrframework.bean.textField.VRTextField();
        txtPorta = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtUsuario = new vrframework.bean.textField.VRTextField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        cboSistema = new vrframework.bean.comboBox.VRComboBox();
        vRLabel2 = new vr.view.components.label.VRLabel();
        cboBancoDados = new vrframework.bean.comboBox.VRComboBox();
        vRLabel6 = new vr.view.components.label.VRLabel();
        txtNomeSchema = new vrframework.bean.textField.VRTextField();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnSalvar = new vrframework.bean.button.VRButton();

        setTitle("Cadastro Sistemas x Banco de Dados");

        vRToolBar1.setRollover(true);

        btnTbIncluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/incluir.png"))); // NOI18N
        btnTbIncluir.setFocusable(false);
        btnTbIncluir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTbIncluir.setName(""); // NOI18N
        btnTbIncluir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTbIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTbIncluirActionPerformed(evt);
            }
        });
        vRToolBar1.add(btnTbIncluir);

        btnTbSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        btnTbSalvar.setFocusable(false);
        btnTbSalvar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTbSalvar.setName(""); // NOI18N
        btnTbSalvar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTbSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTbSalvarActionPerformed(evt);
            }
        });
        vRToolBar1.add(btnTbSalvar);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel3, "Senha");
        vRLabel3.setName(""); // NOI18N

        txtSenha.setColumns(25);
        txtSenha.setName(""); // NOI18N
        txtSenha.setObrigatorio(false);

        txtPorta.setColumns(25);
        txtPorta.setName(""); // NOI18N
        txtPorta.setObrigatorio(false);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel4, "Porta");
        vRLabel4.setName(""); // NOI18N

        txtUsuario.setColumns(25);
        txtUsuario.setName(""); // NOI18N
        txtUsuario.setObrigatorio(false);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel5, "Usuário");
        vRLabel5.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, "Sistema");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel6, "Banco de Dados");

        txtNomeSchema.setColumns(25);
        txtNomeSchema.setName(""); // NOI18N
        txtNomeSchema.setObrigatorio(false);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel7, "Schema");
        vRLabel7.setName(""); // NOI18N

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtPorta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNomeSchema, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNomeSchema, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSair, "Sair");
        btnSair.setName(""); // NOI18N
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSalvar, "Salvar");
        btnSalvar.setName(""); // NOI18N
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSalvar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSair, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(vRToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        try {
            this.setWaitCursor();
            salvar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnSalvarActionPerformed
    private void btnTbIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTbIncluirActionPerformed
        try {
            this.setWaitCursor();
            incluir();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnTbIncluirActionPerformed
    private void btnTbSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTbSalvarActionPerformed
        try {
            this.setWaitCursor();
            salvar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnTbSalvarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.button.VRButton btnSalvar;
    private vrframework.bean.button.VRButton btnTbIncluir;
    private vrframework.bean.button.VRButton btnTbSalvar;
    private vrframework.bean.comboBox.VRComboBox cboBancoDados;
    private vrframework.bean.comboBox.VRComboBox cboSistema;
    private vrframework.bean.textField.VRTextField txtNomeSchema;
    private vrframework.bean.textField.VRTextField txtPorta;
    private vrframework.bean.textField.VRTextField txtSenha;
    private vrframework.bean.textField.VRTextField txtUsuario;
    private vr.view.components.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vr.view.components.label.VRLabel vRLabel6;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.toolBar.VRToolBar vRToolBar1;
    // End of variables declaration//GEN-END:variables
}
