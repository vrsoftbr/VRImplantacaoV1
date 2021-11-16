package vrimplantacao2_5.gui.cadastro.unidade;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.controller.cadastro.unidade.UnidadeController;
import vrimplantacao2_5.controller.utils.EstadoController;
import vrimplantacao2_5.controller.utils.MunicipioController;
import vrimplantacao2_5.vo.cadastro.UnidadeVO;
import vrimplantacao2_5.vo.utils.EstadoVO;
import vrimplantacao2_5.vo.utils.MunicipioVO;

public class CadastroUnidadeGUI extends VRInternalFrame {

    private LojaVO oLoja = null;
    public VRMdiFrame parentFrame = null;
    public static CadastroUnidadeGUI cadastroUnidadeGUI = null;
    public static ConsultaUnidadeGUI consultaUnidadeGUI = null;
    private UnidadeController unidadeController = null;
    private EstadoController estadoController = null;
    private MunicipioController municipioController = null;
    private UnidadeVO unidadeVO = null;

    public CadastroUnidadeGUI(VRMdiFrame menuGUI) throws Exception {
        super(menuGUI);
        initComponents();

        centralizarForm();
        setConfiguracao();

        this.parentFrame = menuGUI;
        unidadeController = new UnidadeController();
        estadoController = new EstadoController();
        municipioController = new MunicipioController();
        
        getEstados();        
    }

    @Override
    public void incluir() throws Exception {
    }

    public void setConfiguracao() {
        txtCodigo.setEditable(false);
        txtCodigo.setEnabled(false);
    }    
    
    @Override
    public void salvar() throws Exception {
        UnidadeVO vo = new UnidadeVO();
        
        vo.setId(unidadeController.getProximoId());
        vo.setNome(txtNomeUnidade.getText().trim());
        vo.setIdMunicipio(cboMunicipio.getId());
        vo.setIdEstado(cboUF.getId());

        if (txtCodigo.getText().trim().isEmpty()) {
            unidadeController.inserir(vo);
        } else {
            vo.setId(Integer.parseInt(txtCodigo.getText()));
            unidadeController.alterar(vo);
        }
        
        if (vo.getId() != 0) {
            txtCodigo.setText(String.valueOf(vo.getId()));
            consultaUnidadeGUI.controller.consultar(null);

            try {
                Util.exibirMensagem("Unidade salva com sucesso!", getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }
    }

    public void editar(UnidadeVO unidadeVO) throws Exception {
        this.unidadeVO = unidadeVO;
        
        txtCodigo.setText(String.valueOf(unidadeVO.getId()));
        txtNomeUnidade.setText(unidadeVO.getNome());
        cboUF.setId(unidadeVO.getIdEstado());
        cboMunicipio.setId(unidadeVO.getIdMunicipio());        
    }

    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();

            if (cadastroUnidadeGUI == null || cadastroUnidadeGUI.isClosed()) {
                cadastroUnidadeGUI = new CadastroUnidadeGUI(menuGUI);
            }

            cadastroUnidadeGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Cadastro Unidades VR");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }

    private void getEstados() throws Exception {
        cboUF.setModel(new DefaultComboBoxModel());

        List<EstadoVO> estados = estadoController.getEstados();

        if (estados == null) {
            return;
        }

        for (EstadoVO vo : estados) {
            cboUF.addItem(new ItemComboVO(vo.getId(), vo.getDescricao()));
        }
    }

    private void getMunicipios() throws Exception {
        cboMunicipio.setModel(new DefaultComboBoxModel());

        List<MunicipioVO> municipios = municipioController.getMunicipios(cboUF.getId());

        if (municipios == null) {
            return;
        }

        municipios.forEach((vo) -> {
            cboMunicipio.addItem(new ItemComboVO(vo.getId(), vo.getDescricao()));
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRToolBar1 = new vrframework.bean.toolBar.VRToolBar();
        btnTbIncluir = new vrframework.bean.button.VRButton();
        btnTbSalvar = new vrframework.bean.button.VRButton();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        txtNomeUnidade = new vrframework.bean.textField.VRTextField();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel1 = new vr.view.components.label.VRLabel();
        vRLabel2 = new vr.view.components.label.VRLabel();
        cboUF = new vrframework.bean.comboBox.VRComboBox();
        cboMunicipio = new vrframework.bean.comboBox.VRComboBox();
        txtCodigo = new vrframework.bean.textField.VRTextField();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnSalvar = new vrframework.bean.button.VRButton();

        setTitle("Cadastro Unidade");

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

        txtNomeUnidade.setColumns(25);
        txtNomeUnidade.setName(""); // NOI18N
        txtNomeUnidade.setObrigatorio(true);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel7, "Unidade");
        vRLabel7.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, "Estado");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, "Município");

        cboUF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUFActionPerformed(evt);
            }
        });

        cboMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMunicipioActionPerformed(evt);
            }
        });

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

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel3, "Código");
        vRLabel3.setName(""); // NOI18N

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtNomeUnidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboUF, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNomeUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(vRPanel1Layout.createSequentialGroup()
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(26, 26, 26))
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void cboUFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUFActionPerformed
        try {        
            getMunicipios();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_cboUFActionPerformed

    private void cboMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMunicipioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMunicipioActionPerformed

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
    private vrframework.bean.comboBox.VRComboBox cboMunicipio;
    private vrframework.bean.comboBox.VRComboBox cboUF;
    private vrframework.bean.textField.VRTextField txtCodigo;
    private vrframework.bean.textField.VRTextField txtNomeUnidade;
    private vr.view.components.label.VRLabel vRLabel1;
    private vr.view.components.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.toolBar.VRToolBar vRToolBar1;
    // End of variables declaration//GEN-END:variables
}
