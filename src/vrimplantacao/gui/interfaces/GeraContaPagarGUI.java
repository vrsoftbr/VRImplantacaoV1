package vrimplantacao.gui.interfaces;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.PagarOutrasDespesasDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.vo.cadastro.financeiro.ContaPagarVO;

/**
 *
 * @author Importacao
 */
public class GeraContaPagarGUI extends VRInternalFrame {

    private static GeraContaPagarGUI instance;
    private List<ContaPagarVO> vContas;

    /**
     * Creates new form GeraContaPagarGUI
     *
     * @param i_mdiFrame
     * @throws java.lang.Exception
     */
    public GeraContaPagarGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        setTitle("Gera Conta a Pagar");
        centralizarForm();
        carregarLoja();
        configurarColuna();
    }

    private void carregarLoja() throws Exception {
        List<LojaVO> lojas = new LojaDAO().carregar();
        cmbLoja.setModel(new DefaultComboBoxModel());
        for (LojaVO oLoja : lojas) {
            cmbLoja.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
        cmbLoja.setEnabled(true);
    }

    private void configurarColuna() throws Exception {
        List<VRColumnTable> vColunaCP = new ArrayList();
        vColunaCP.add(new VRColumnTable("Selecionado", 40, false, SwingConstants.CENTER, true, null));
        vColunaCP.add(new VRColumnTable("Número Doc.", 70, true, SwingConstants.LEFT, false, null));
        vColunaCP.add(new VRColumnTable("Fornecedor", 280, true, SwingConstants.LEFT, false, null));
        vColunaCP.add(new VRColumnTable("Razão Social", 70, true, SwingConstants.LEFT, false, null));
        vColunaCP.add(new VRColumnTable("Data Entrada", 70, true, SwingConstants.LEFT, false, null));
        vColunaCP.add(new VRColumnTable("Data Emissão", 70, true, SwingConstants.LEFT, false, null));
        vColunaCP.add(new VRColumnTable("Data Vencimento", 70, true, SwingConstants.LEFT, false, null));
        vColunaCP.add(new VRColumnTable("Tipo Entrada", 80, true, SwingConstants.LEFT, false, null));
        vColunaCP.add(new VRColumnTable("Valor", 80, true, SwingConstants.RIGHT, false, null));
        vColunaCP.add(new VRColumnTable("Observação", 200, true, SwingConstants.LEFT, false, null));

        tblContaPagar.configurarColuna(vColunaCP, this, "tblContaPagar", "exibirConsulta");
    }

    private void carregarConta() throws Exception {
        int idLoja = ((ItemComboVO) cmbLoja.getSelectedItem()).id;
        vContas = new PagarOutrasDespesasDAO().getOutrasDespesas(idLoja, Utils.stringToInt(txtFornecedor.getText()), txtRazao.getText());

        Object[][] dados = new Object[vContas.size()][10];
        int i = 0;
        for (ContaPagarVO vo : vContas) {
            dados[i][0] = false;
            dados[i][1] = vo.getNumeroDocumento();
            dados[i][2] = vo.getFornecedor().getId();
            dados[i][3] = vo.getFornecedor().getRazaoSocial();
            dados[i][4] = vo.getDataEntrada();
            dados[i][5] = vo.getDataEmissao();
            dados[i][6] = vo.getVencimento();
            dados[i][7] = vo.getTipoEntrada().getDescricao();
            dados[i][8] = Util.formatDecimal2(vo.getValor());
            dados[i][9] = vo.getObservacao();
            i++;
        }

        tblContaPagar.setRowHeight(20);
        tblContaPagar.setModel(dados);
    }

    private void selecionarTodos() throws Exception {
        selecionarTodos(chkTodos.isSelected());
    }

    public void selecionarTodos(boolean i_selecionado) throws Exception {
        chkTodos.setSelected(i_selecionado);

        for (int i = 0; i < tblContaPagar.getRowCount(); i++) {
            tblContaPagar.setValueAt(i_selecionado, i, tblContaPagar.getOrdem(0));
        }
    }

    private void gerarContaPagar() throws Exception {
        List<ContaPagarVO> vContaPagarSelecionado = new ArrayList();
        for (int i = 0; i < tblContaPagar.getRowCount(); i++) {
            if (((Boolean) tblContaPagar.getValueAt(i, tblContaPagar.getOrdem(0))).booleanValue() == true) {
                vContaPagarSelecionado.add(vContas.get(tblContaPagar.convertRowIndexToModel(i)));
            }
        }

        if (vContaPagarSelecionado.isEmpty()) {
            Util.exibirMensagem("Nenhum registro selecionado", this.getTitle());
            return;
        }

        int idLoja = ((ItemComboVO) cmbLoja.getSelectedItem()).id;
        new PagarOutrasDespesasDAO().finalizar(vContaPagarSelecionado, idLoja);
        Util.exibirMensagem("Conta a Pagar gerado com sucesso!", this.getTitle());
        carregarConta();
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new GeraContaPagarGUI(i_mdiFrame);
            }
            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblContaPagar = new vrframework.bean.tableEx.VRTableEx();
        pnlBotoes = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnConsultar = new vrframework.bean.button.VRButton();
        pnlOpcoes = new vrframework.bean.panel.VRPanel();
        lblLoja = new vrframework.bean.label.VRLabel();
        cmbLoja = new javax.swing.JComboBox();
        lblRazao = new vrframework.bean.label.VRLabel();
        lblFornecedor = new vrframework.bean.label.VRLabel();
        txtFornecedor = new javax.swing.JTextField();
        txtRazao = new vrframework.bean.textField.VRTextField();
        chkTodos = new vrframework.bean.checkBox.VRCheckBox();
        tbBotoes = new javax.swing.JToolBar();
        btnFinalizar = new javax.swing.JButton();

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSair, org.openide.util.NbBundle.getMessage(GeraContaPagarGUI.class, "GeraContaPagarGUI.btnSair.text")); // NOI18N
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnConsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/consultar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnConsultar, org.openide.util.NbBundle.getMessage(GeraContaPagarGUI.class, "GeraContaPagarGUI.btnConsultar.text")); // NOI18N
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBotoesLayout = new javax.swing.GroupLayout(pnlBotoes);
        pnlBotoes.setLayout(pnlBotoesLayout);
        pnlBotoesLayout.setHorizontalGroup(
            pnlBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotoesLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlBotoesLayout.setVerticalGroup(
            pnlBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotoesLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnlBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        org.openide.awt.Mnemonics.setLocalizedText(lblLoja, org.openide.util.NbBundle.getMessage(GeraContaPagarGUI.class, "GeraContaPagarGUI.lblLoja.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblRazao, org.openide.util.NbBundle.getMessage(GeraContaPagarGUI.class, "GeraContaPagarGUI.lblRazao.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblFornecedor, org.openide.util.NbBundle.getMessage(GeraContaPagarGUI.class, "GeraContaPagarGUI.lblFornecedor.text")); // NOI18N

        txtFornecedor.setText(org.openide.util.NbBundle.getMessage(GeraContaPagarGUI.class, "GeraContaPagarGUI.txtFornecedor.text")); // NOI18N
        txtFornecedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFornecedorKeyPressed(evt);
            }
        });

        txtRazao.setText(org.openide.util.NbBundle.getMessage(GeraContaPagarGUI.class, "GeraContaPagarGUI.txtRazao.text")); // NOI18N
        txtRazao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRazaoKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout pnlOpcoesLayout = new javax.swing.GroupLayout(pnlOpcoes);
        pnlOpcoes.setLayout(pnlOpcoesLayout);
        pnlOpcoesLayout.setHorizontalGroup(
            pnlOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOpcoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRazao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRazao, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlOpcoesLayout.setVerticalGroup(
            pnlOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOpcoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRazao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(pnlOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRazao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(chkTodos, org.openide.util.NbBundle.getMessage(GeraContaPagarGUI.class, "GeraContaPagarGUI.chkTodos.text")); // NOI18N
        chkTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTodosActionPerformed(evt);
            }
        });

        tbBotoes.setRollover(true);

        btnFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/finalizar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnFinalizar, org.openide.util.NbBundle.getMessage(GeraContaPagarGUI.class, "GeraContaPagarGUI.btnFinalizar.text")); // NOI18N
        btnFinalizar.setFocusable(false);
        btnFinalizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFinalizar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });
        tbBotoes.add(btnFinalizar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBotoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlOpcoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tblContaPagar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkTodos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tbBotoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOpcoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(chkTodos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tblContaPagar, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            this.setWaitCursor();
            carregarConta();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void chkTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTodosActionPerformed
        try {
            this.setWaitCursor();
            selecionarTodos();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_chkTodosActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        try {
            this.setWaitCursor();
            gerarContaPagar();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void txtFornecedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFornecedorKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                this.setWaitCursor();
                carregarConta();
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            } finally {
                this.setDefaultCursor();
            }
        }
    }//GEN-LAST:event_txtFornecedorKeyPressed

    private void txtRazaoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRazaoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                this.setWaitCursor();
                carregarConta();
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            } finally {
                this.setDefaultCursor();
            }
        }
    }//GEN-LAST:event_txtRazaoKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnConsultar;
    private javax.swing.JButton btnFinalizar;
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.checkBox.VRCheckBox chkTodos;
    private javax.swing.JComboBox cmbLoja;
    private vrframework.bean.label.VRLabel lblFornecedor;
    private vrframework.bean.label.VRLabel lblLoja;
    private vrframework.bean.label.VRLabel lblRazao;
    private vrframework.bean.panel.VRPanel pnlBotoes;
    private vrframework.bean.panel.VRPanel pnlOpcoes;
    private javax.swing.JToolBar tbBotoes;
    private vrframework.bean.tableEx.VRTableEx tblContaPagar;
    private javax.swing.JTextField txtFornecedor;
    private vrframework.bean.textField.VRTextField txtRazao;
    // End of variables declaration//GEN-END:variables
}
