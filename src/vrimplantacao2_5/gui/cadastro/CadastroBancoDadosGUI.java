package vrimplantacao2_5.gui.cadastro;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.controller.cadastro.bancodados.BancoDadosController;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

public class CadastroBancoDadosGUI extends VRInternalFrame {

    private LojaVO oLoja = null;
    public VRMdiFrame parentFrame = null;
    public static CadastroBancoDadosGUI cadastroBancoDadosGUI = null;
    public static ConsultaBancoDadosGUI consultaBancoDadosGUI = null;
    private BancoDadosController bancoDadosController = null;
    private BancoDadosVO bancoDadosVO = null;

    public CadastroBancoDadosGUI(VRMdiFrame menuGUI) throws Exception {
        super(menuGUI);
        initComponents();
        
        centralizarForm();
        
        this.parentFrame = menuGUI;
        bancoDadosController = new BancoDadosController();
        txtCodigo.setEditable(false);
        txtCodigo.setEnabled(false);
    }
    
    @Override
    public void incluir() throws Exception {
        txtCodigo.setText("");
        txtNomeBancoDados.setText("");
    }

    @Override
    public void salvar() throws Exception {
        if (txtNomeBancoDados.getText().trim().isEmpty()) {
            try {
                Util.exibirMensagem("Campo Nome obrigatório!", getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }

            return;
        }

        BancoDadosVO vo = new BancoDadosVO();
        vo.setNome(txtNomeBancoDados.getText().trim());

        if (txtCodigo.getText().trim().isEmpty()) {
            bancoDadosController.salvar(vo);
        } else {            
            vo.setId(Integer.parseInt(txtCodigo.getText()));            
            bancoDadosController.alterar(vo);
        }

        if (vo.getId() != 0) {
            txtCodigo.setText(String.valueOf(vo.getId()));

            consultaBancoDadosGUI.controller.consultar();

            try {
                Util.exibirMensagem("Banco de Dados salvo com sucesso!", getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }
    }

    public void editar(BancoDadosVO bancoDadosVO) throws Exception {
        this.bancoDadosVO = bancoDadosVO;
        
        txtCodigo.setText(String.valueOf(bancoDadosVO.getId()));
        txtNomeBancoDados.setText(bancoDadosVO.getNome());
    }
    
    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();

            if (cadastroBancoDadosGUI == null || cadastroBancoDadosGUI.isClosed()) {
                cadastroBancoDadosGUI = new CadastroBancoDadosGUI(menuGUI);
            }

            cadastroBancoDadosGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Configuração de Base de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRToolBar1 = new vrframework.bean.toolBar.VRToolBar();
        btnTbIncluir = new vrframework.bean.button.VRButton();
        btnTbSalvar = new vrframework.bean.button.VRButton();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtNomeBancoDados = new vrframework.bean.textField.VRTextField();
        txtCodigo = new vrframework.bean.textField.VRTextField();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnSalvar = new vrframework.bean.button.VRButton();

        setTitle("Cadastro Banco de Dados");

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

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, "Código");
        vRLabel2.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel3, "Nome");
        vRLabel3.setName(""); // NOI18N

        txtNomeBancoDados.setColumns(25);
        txtNomeBancoDados.setName(""); // NOI18N
        txtNomeBancoDados.setObrigatorio(true);

        txtCodigo.setColumns(6);
        txtCodigo.setMascara("Numero");
        txtCodigo.setName(""); // NOI18N
        txtCodigo.setObrigatorio(true);
        txtCodigo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCodigoFocusLost(evt);
            }
        });
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 330, Short.MAX_VALUE))
                    .addComponent(txtNomeBancoDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNomeBancoDados, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private void txtCodigoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCodigoFocusLost
        try {
            this.setWaitCursor();
            txtCodigo.setText(Util.formatNumber(txtCodigo.getText(), txtCodigo.getColumns()));
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_txtCodigoFocusLost

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.button.VRButton btnSalvar;
    private vrframework.bean.button.VRButton btnTbIncluir;
    private vrframework.bean.button.VRButton btnTbSalvar;
    private vrframework.bean.textField.VRTextField txtCodigo;
    private vrframework.bean.textField.VRTextField txtNomeBancoDados;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.toolBar.VRToolBar vRToolBar1;
    // End of variables declaration//GEN-END:variables
}
