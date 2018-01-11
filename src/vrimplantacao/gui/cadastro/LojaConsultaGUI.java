package vrimplantacao.gui.cadastro;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.gui.FiltroGUI;
import vrimplantacao.vo.OrdenacaoConsultaVO;
import vrimplantacao.vo.loja.LojaFiltroConsultaVO;
import vrimplantacao.vo.loja.LojaVO;

public class LojaConsultaGUI extends VRInternalFrame {

    public List<LojaVO> vLoja = null;    
    private FiltroGUI formFiltro = null;

    public LojaConsultaGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        centralizarForm();
        this.setMaximum(true);

        configurarColuna();

        carregarFiltro();

        consultar();
    }

    private void carregarFiltro() throws Exception {
        formFiltro = new FiltroGUI(mdiFrame);

        List<OrdenacaoConsultaVO> vOrdenacao = new ArrayList();
        vOrdenacao.add(new OrdenacaoConsultaVO("Código", "lj.id"));
        vOrdenacao.add(new OrdenacaoConsultaVO("Descrição", "lj.descricao"));
        vOrdenacao.add(new OrdenacaoConsultaVO("Fornecedor", "lj.id_fornecedor"));
        vOrdenacao.add(new OrdenacaoConsultaVO("Região", "lj.id_regiao"));
        vOrdenacao.add(new OrdenacaoConsultaVO("Nome Servidor", "lj.nomeservidor"));
        vOrdenacao.add(new OrdenacaoConsultaVO("Servidor Central", "lj.servidorcentral"));
        vOrdenacao.add(new OrdenacaoConsultaVO("Gera Concentrador", "lj.geraconcentrador"));
        vOrdenacao.add(new OrdenacaoConsultaVO("Situação", "lj.id_situacaocadastro"));

        formFiltro.setvOrdenacao(vOrdenacao);
        formFiltro.exibirOrdenacao();
    }

    @Override
    public void incluir() throws Exception {
        LojaCadastroGUI form = new LojaCadastroGUI(mdiFrame, this);

        form.incluir();
        form.setVisible(true);
    }

    @Override
    public void consultar() throws Exception {
        LojaFiltroConsultaVO oFiltro = new LojaFiltroConsultaVO();
        oFiltro.id = (!txtCodigo.getText().equals("")) ? txtCodigo.getInt() : -1;
        oFiltro.descricao = txtDescricao.getText();
        oFiltro.ordenacao = formFiltro.getOrdenacao();
        oFiltro.limite = formFiltro.getLimite();

        vLoja = new LojaDAO().consultar(oFiltro);

        exibirConsulta();

        if (vLoja.isEmpty()) {
            Util.exibirMensagem(Util.MSG_REGISTRO_NAO_ENCONTRADO, getTitle(), 0, JOptionPane.WARNING_MESSAGE);
        }
    }

    private void configurarColuna() throws Exception {
        List<VRColumnTable> vColuna = new ArrayList();
        vColuna.add(new VRColumnTable("Código", true, SwingConstants.LEFT, false, null));
        vColuna.add(new VRColumnTable("Descrição", true, SwingConstants.LEFT, false, null));
        vColuna.add(new VRColumnTable("Fornecedor", true, SwingConstants.LEFT, false, null));
        vColuna.add(new VRColumnTable("Região", true, SwingConstants.LEFT, false, null));
        vColuna.add(new VRColumnTable("Nome Servidor", true, SwingConstants.LEFT, false, null));
        vColuna.add(new VRColumnTable("Servidor Central", true, SwingConstants.LEFT, false, null));
        vColuna.add(new VRColumnTable("Gera Concentrador", true, SwingConstants.LEFT, false, null));
        vColuna.add(new VRColumnTable("Situacao", true, SwingConstants.LEFT, false, null));

        tblConsulta.configurarColuna(vColuna, this, "tblConsulta", "exibirConsulta", Global.idUsuario);
    }

    public void exibirConsulta() throws Exception {
        Object[][] dados = new Object[vLoja.size()][tblConsulta.getvColuna().size()];

        int i = 0;

        for (LojaVO oLoja : vLoja) {
            dados[i][tblConsulta.getOrdem(0)] = Util.formatNumber(oLoja.id, 6);
            dados[i][tblConsulta.getOrdem(1)] = oLoja.descricao;
            dados[i][tblConsulta.getOrdem(2)] = Util.formatNumber(oLoja.idFornecedor, 6);
            dados[i][tblConsulta.getOrdem(3)] = oLoja.regiao;
            dados[i][tblConsulta.getOrdem(4)] = oLoja.nomeServidor;
            dados[i][tblConsulta.getOrdem(5)] = oLoja.servidorCentral;
            dados[i][tblConsulta.getOrdem(6)] = oLoja.geraConcentrador;
            dados[i][tblConsulta.getOrdem(7)] = oLoja.situacaoCadastro;

            i++;
        }

        tblConsulta.setModel(dados);

        tblConsulta.requestFocus();
    }

    @Override
    public void editar() throws Exception {
        if (tblConsulta.getLinhaSelecionada() == -1) {
            throw new VRException(Util.MSG_NENHUM_ITEM_SELECIONADO);
        }

        LojaVO oLoja = vLoja.get(tblConsulta.getLinhaSelecionada());

        LojaCadastroGUI form = new LojaCadastroGUI(mdiFrame, this);

        form.carregar(oLoja.id);
        form.setVisible(true);
    }

    @Override
    public void filtro() throws Exception {
        formFiltro.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRPanel2 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnConsultar = new vrframework.bean.button.VRButton();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        txtCodigo = new vrframework.bean.textField.VRTextField();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtDescricao = new vrframework.bean.textField.VRTextField();
        tlbToolBar = new vrframework.bean.toolBar.VRToolBar();
        vRToolBarPadrao1 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        tblConsulta = new vrframework.bean.tableEx.VRTableEx();

        setTitle("Consulta de Loja");

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnConsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/consultar.png"))); // NOI18N
        btnConsultar.setText("Consultar");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        vRLabel2.setText("Código");

        txtCodigo.setColumns(6);
        txtCodigo.setMascara("Numero");
        txtCodigo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCodigoFocusLost(evt);
            }
        });

        vRLabel1.setText("Descrição");

        txtDescricao.setColumns(30);

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        tlbToolBar.setRollover(true);

        vRToolBarPadrao1.setRollover(true);
        vRToolBarPadrao1.setImprimir(false);
        vRToolBarPadrao1.setVisibleConsultar(true);
        vRToolBarPadrao1.setVisibleEditar(true);
        vRToolBarPadrao1.setVisibleFiltro(true);
        vRToolBarPadrao1.setVisibleIncluir(true);
        tlbToolBar.add(vRToolBarPadrao1);

        tblConsulta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblConsultaMouseClicked(evt);
            }
        });
        tblConsulta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblConsultaKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tlbToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tlbToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            this.setWaitCursor();
            consultar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConsultarActionPerformed
    private void tblConsultaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblConsultaMouseClicked
        try {
            this.setWaitCursor();
            if (evt.getClickCount() == 2) {
                editar();
            }

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_tblConsultaMouseClicked
    private void tblConsultaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblConsultaKeyPressed
        try {
            this.setWaitCursor();
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                editar();

            } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                excluir();
            }

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_tblConsultaKeyPressed
    private void txtCodigoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCodigoFocusLost
        try {
            this.setWaitCursor();
            if (!txtCodigo.getText().isEmpty()) {
                txtCodigo.setText(Util.formatNumber(txtCodigo.getText(), txtCodigo.getColumns()));
            }
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_txtCodigoFocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnConsultar;
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.tableEx.VRTableEx tblConsulta;
    private vrframework.bean.toolBar.VRToolBar tlbToolBar;
    private vrframework.bean.textField.VRTextField txtCodigo;
    private vrframework.bean.textField.VRTextField txtDescricao;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao1;
    // End of variables declaration//GEN-END:variables
}
