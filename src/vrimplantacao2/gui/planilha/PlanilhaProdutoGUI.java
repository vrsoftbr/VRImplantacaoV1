package vrimplantacao2.gui.planilha;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoDAO;
import vrimplantacao2.dao.relatorio.RelatorioProdutoDAO;
import vrimplantacao2.vo.relatorio.ProdutoRelatorioVO;

/**
 *
 * @author Importacao
 */
public class PlanilhaProdutoGUI extends VRInternalFrame {

    private static PlanilhaProdutoGUI planilhaProdutoGUI;

    public PlanilhaProdutoGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        setConf();
    }

    private void configuraColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();
        column.add(new VRColumnTable("ID", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Desc. Completa", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Desc. Reduzida", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Eans", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Balança", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Vl. Venda", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Custo C/Imposto", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Custo S/Imposto", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Cód. Merc1", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Mercadológico", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Cód. Merc2", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Mercadológico2", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Cód. Merc3", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Mercadológico3", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Cód. Merc4", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Mercadológico4", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Cód. Merc5", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Mercadológico5", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Cód. Familia", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Familia", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Validade", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Estoque Max.", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Estoque Min.", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Estoque", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Ativo", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Descontinuado", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("NCM", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Cest", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Pis Cofins Deb.", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Pis Cofins Cre.", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Natureza Receita", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("ICMS Debito", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("ICMS Credito", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("ICMS Consumidor", true, SwingConstants.LEFT, false, null));
        tblProduto.configurarColuna(column, this, "Produto", "");
    }

    private void setConf() throws Exception {
        centralizarForm();
        configuraColuna();
        this.setMaximum(true);
        this.setTitle("Planilha de Produto para Validação");
        carregaLoja();
    }

    public void carregaLoja() throws Exception {
        cmbLoja.setModel(new DefaultComboBoxModel());
        for (LojaVO oLoja : new LojaDAO().carregar()) {
            cmbLoja.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
    }

    private void carregaProduto() throws Exception {
        int idLojaVR = ((ItemComboVO) cmbLoja.getSelectedItem()).id;
        List<ProdutoRelatorioVO> produto = new RelatorioProdutoDAO().getPlanilhaProduto(idLojaVR);
        
        Object[][] dados = new Object[produto.size()][34];
        int i = 0;
        for (ProdutoRelatorioVO vo : produto) {
            dados[i][0] = vo.getId();
            dados[i][1] = vo.getDescricaoCompleta();
            dados[i][2] = vo.getDescricaoReduzida();
            dados[i][3] = vo.getEan().toUpperCase();
            dados[i][4] = vo.getBalanca();
            dados[i][5] = vo.getPrecoVenda();
            dados[i][6] = vo.getCustoComImposto();
            dados[i][7] = vo.getCustoSemImposto();
            dados[i][8] = vo.getCodMerc1();
            dados[i][9] = vo.getMerc1();
            dados[i][10] = vo.getCodMerc2();
            dados[i][11] = vo.getMerc2();
            dados[i][12] = vo.getCodMerc3();
            dados[i][13] = vo.getMerc3();
            dados[i][14] = vo.getCodMerc4();
            dados[i][15] = vo.getMerc4();
            dados[i][16] = vo.getCodMerc5();
            dados[i][17] = vo.getMerc5();
            dados[i][18] = vo.getIdFamiliaProduto();
            dados[i][19] = vo.getFamiliaProduto();
            dados[i][20] = vo.getValidade();
            dados[i][21] = vo.getEstoqueMax();
            dados[i][22] = vo.getEstoqueMin();
            dados[i][23] = vo.getEstoque();
            dados[i][24] = vo.getAtivo();
            dados[i][25] = vo.getDescontinuado();
            dados[i][26] = vo.getNcm();
            dados[i][27] = vo.getCest();
            dados[i][28] = vo.getPisCofinsDebito();
            dados[i][29] = vo.getPisCofinsCredito();
            dados[i][30] = vo.getPisCofinsNaturezaReceita();
            dados[i][31] = vo.getIcmsAliquotaDebito();
            dados[i][32] = vo.getIcmsAliquotaCredito();
            dados[i][33] = vo.getIcmsAliquotaConsumidor();
            
            i++;
        }
        tblProduto.setRowHeight(20);
        tblProduto.setModel(dados);
    }
    
    private void dropTable() throws Exception {
        new RelatorioProdutoDAO().dropTable();
        Util.exibirMensagem("Tabela excluída", getTitle());
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (planilhaProdutoGUI == null || planilhaProdutoGUI.isClosed()) {
                planilhaProdutoGUI = new PlanilhaProdutoGUI(i_mdiFrame);
            }
            planilhaProdutoGUI.setVisible(true);
            planilhaProdutoGUI.moveToFront();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblProduto = new vrframework.bean.tableEx.VRTableEx();
        pnlBotoes = new vrframework.bean.panel.VRPanel();
        btnRel = new vrframework.bean.button.VRButton();
        btnSair = new vrframework.bean.button.VRButton();
        btnDel = new vrframework.bean.button.VRButton();
        pnlDados = new vrframework.bean.panel.VRPanel();
        cmbLoja = new javax.swing.JComboBox();
        lblLoja = new vrframework.bean.label.VRLabel();

        btnRel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/arquivo.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnRel, "Relatório");
        btnRel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRelActionPerformed(evt);
            }
        });

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSair, "Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/apagar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnDel, "Deletar Tabela");
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBotoesLayout = new javax.swing.GroupLayout(pnlBotoes);
        pnlBotoes.setLayout(pnlBotoesLayout);
        pnlBotoesLayout.setHorizontalGroup(
            pnlBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotoesLayout.createSequentialGroup()
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 301, Short.MAX_VALUE)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlBotoesLayout.setVerticalGroup(
            pnlBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotoesLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnlBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        org.openide.awt.Mnemonics.setLocalizedText(lblLoja, "Loja");

        javax.swing.GroupLayout pnlDadosLayout = new javax.swing.GroupLayout(pnlDados);
        pnlDados.setLayout(pnlDadosLayout);
        pnlDadosLayout.setHorizontalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDadosLayout.setVerticalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBotoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tblProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tblProduto, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRelActionPerformed
        try {
            this.setWaitCursor();
            carregaProduto();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnRelActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelActionPerformed
        try {
            this.setWaitCursor();
            dropTable();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnDelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnDel;
    private vrframework.bean.button.VRButton btnRel;
    private vrframework.bean.button.VRButton btnSair;
    private javax.swing.JComboBox cmbLoja;
    private vrframework.bean.label.VRLabel lblLoja;
    private vrframework.bean.panel.VRPanel pnlBotoes;
    private vrframework.bean.panel.VRPanel pnlDados;
    private vrframework.bean.tableEx.VRTableEx tblProduto;
    // End of variables declaration//GEN-END:variables
}
