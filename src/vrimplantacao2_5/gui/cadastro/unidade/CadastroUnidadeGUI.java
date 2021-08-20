package vrimplantacao2_5.gui.cadastro.unidade;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.controller.cadastro.unidade.UnidadeController;
import vrimplantacao2_5.controller.utils.EstadoController;
import vrimplantacao2_5.controller.utils.MunicipioController;
import static vrimplantacao2_5.gui.cadastro.sistemabancodados.CadastroSistemaBancoDadosGUI.consultaSistemaBancoDadosGUI;
import vrimplantacao2_5.vo.cadastro.SistemaBancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;
import vrimplantacao2_5.vo.cadastro.UnidadeVO;
import vrimplantacao2_5.vo.utils.EstadoVO;

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
        setConfigurarCampos();
        
        this.parentFrame = menuGUI;
        unidadeController = new UnidadeController();
        consultaUnidadeGUI = new ConsultaUnidadeGUI(menuGUI);
        estadoController = new EstadoController();
        municipioController = new MunicipioController();
        
        getEstados();
    }
    
    @Override
    public void incluir() throws Exception {
    }

    private void setConfigurarCampos() {
    }
    
    @Override
    public void salvar() throws Exception {
        SistemaBancoDadosVO vo = new SistemaBancoDadosVO();
        
        /*if (idSistemaBancoDados <= 0) {
            sistemaBancoDadosController.inserir(vo);
        } else {
            vo.setId(idSistemaBancoDados);
            sistemaBancoDadosController.alterar(vo);
        }*/

        if (vo.getId() != 0) {
            //idSistemaBancoDados = vo.getId();
            consultaSistemaBancoDadosGUI.controller.consultar(null);

            try {
                Util.exibirMensagem("Sistema x Banco de Dados salvo com sucesso!", getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }
    }

    public void editar(UnidadeVO unidadeVO) throws Exception {

        /*cboSistema.setId(sistemaBancoDadosVO.getIdSistema());
        cboBancoDados.setId(sistemaBancoDadosVO.getIdBancoDados());
        txtPorta.setText(String.valueOf(sistemaBancoDadosVO.getPorta()));
        txtNomeSchema.setText(sistemaBancoDadosVO.getNomeSchema());
        txtUsuario.setText(sistemaBancoDadosVO.getUsuario());
        txtSenha.setText(sistemaBancoDadosVO.getSenha());*/
    }
    
    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();

            if (cadastroUnidadeGUI == null || cadastroUnidadeGUI.isClosed()) {
                cadastroUnidadeGUI = new CadastroUnidadeGUI(menuGUI);
            }

            cadastroUnidadeGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Cadastro Sistemas x Banco de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
        
    private void getEstados() throws Exception {
        cboUF.setModel(new DefaultComboBoxModel());
        
        List<EstadoVO> estados = estadoController.getEstados();
        
        if (estados == null) {
            return ;
        }
        
        for (EstadoVO vo : estados) {
            cboUF.addItem(new ItemComboVO(vo.getId(), vo.getDescricao()));
        }
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
        cboUF = new vr.view.components.combobox.VRComboBox();
        cboMunicipio = new vr.view.components.combobox.VRComboBox();
        vRLabel1 = new vr.view.components.label.VRLabel();
        vRLabel2 = new vr.view.components.label.VRLabel();
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
        txtNomeUnidade.setObrigatorio(false);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel7, "Unidade");
        vRLabel7.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, "Estado");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, "Município");

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNomeUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboUF, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNomeUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
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
    private vr.view.components.combobox.VRComboBox cboMunicipio;
    private vr.view.components.combobox.VRComboBox cboUF;
    private vrframework.bean.textField.VRTextField txtNomeUnidade;
    private vr.view.components.label.VRLabel vRLabel1;
    private vr.view.components.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.toolBar.VRToolBar vRToolBar1;
    // End of variables declaration//GEN-END:variables
}
