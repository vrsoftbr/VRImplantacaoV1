package vrimplantacao2_5.gui.copias;

import java.util.ArrayList;
import java.util.List;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.copias.GeradorDeBackupDAO;

/**
 *
 * @author Wesley
 */
public class GeradorDeBackupGUI extends VRInternalFrame {

    GeradorDeBackupDAO gerador = new GeradorDeBackupDAO();

    private GeradorDeBackupGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);

        initComponents();

        centralizarForm();
        this.setMaximum(true);
    }

    private static GeradorDeBackupGUI instance;

    public static void Exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new GeradorDeBackupGUI(i_mdiFrame);
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

        pnlDados1 = new vrframework.bean.panel.VRPanel();
        produto = new javax.swing.JCheckBox();
        produtoComplemento = new javax.swing.JCheckBox();
        produtoAutomacao = new javax.swing.JCheckBox();
        clientePreferencial = new javax.swing.JCheckBox();
        cliienteEventual = new javax.swing.JCheckBox();
        fornecedor = new javax.swing.JCheckBox();
        produtoFornecedor = new javax.swing.JCheckBox();
        familiaProduto = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        pnlDados2 = new vrframework.bean.panel.VRPanel();
        fazerBackup = new vrframework.bean.button.VRButton();
        excluirBackup = new vrframework.bean.button.VRButton();

        setResizable(true);
        setTitle("Gerar Backup de Tabelas");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                onClose(evt);
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(produto, "Produto");

        org.openide.awt.Mnemonics.setLocalizedText(produtoComplemento, "Produto Complemento");

        org.openide.awt.Mnemonics.setLocalizedText(produtoAutomacao, "Produto Automação");
        produtoAutomacao.setVerifyInputWhenFocusTarget(false);

        org.openide.awt.Mnemonics.setLocalizedText(clientePreferencial, "Cliente Preferencial");
        clientePreferencial.setToolTipText("");

        org.openide.awt.Mnemonics.setLocalizedText(cliienteEventual, "Cliente Eventtual");

        org.openide.awt.Mnemonics.setLocalizedText(fornecedor, "Fornecedor");
        fornecedor.setToolTipText("");

        org.openide.awt.Mnemonics.setLocalizedText(produtoFornecedor, "Produto Fornecedor");
        produtoFornecedor.setToolTipText("");

        org.openide.awt.Mnemonics.setLocalizedText(familiaProduto, "Família Produto");
        familiaProduto.setToolTipText("");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Selecione as tabelas que deseja fazer backup ou excluir o backup");

        javax.swing.GroupLayout pnlDados1Layout = new javax.swing.GroupLayout(pnlDados1);
        pnlDados1.setLayout(pnlDados1Layout);
        pnlDados1Layout.setHorizontalGroup(
            pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(familiaProduto)
                    .addGroup(pnlDados1Layout.createSequentialGroup()
                        .addGroup(pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clientePreferencial)
                            .addComponent(produto)
                            .addComponent(fornecedor))
                        .addGap(35, 35, 35)
                        .addGroup(pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(produtoFornecedor)
                            .addGroup(pnlDados1Layout.createSequentialGroup()
                                .addComponent(produtoComplemento)
                                .addGap(35, 35, 35)
                                .addComponent(produtoAutomacao))
                            .addComponent(cliienteEventual))))
                .addGap(35, 35, 35))
        );
        pnlDados1Layout.setVerticalGroup(
            pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(produto)
                    .addComponent(produtoComplemento)
                    .addComponent(produtoAutomacao))
                .addGap(18, 18, 18)
                .addGroup(pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientePreferencial)
                    .addComponent(cliienteEventual))
                .addGap(18, 18, 18)
                .addGroup(pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fornecedor)
                    .addComponent(produtoFornecedor))
                .addGap(18, 18, 18)
                .addComponent(familiaProduto)
                .addGap(35, 35, 35))
        );

        produtoAutomacao.getAccessibleContext().setAccessibleDescription("");
        cliienteEventual.getAccessibleContext().setAccessibleDescription("");

        pnlDados2.setPreferredSize(new java.awt.Dimension(289, 50));

        fazerBackup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(fazerBackup, "Fazer Backup");
        fazerBackup.setFocusable(false);
        fazerBackup.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        fazerBackup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fazerBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fazerBackupActionPerformed(evt);
            }
        });

        excluirBackup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/limpar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(excluirBackup, "Excluir Backup");
        excluirBackup.setFocusable(false);
        excluirBackup.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        excluirBackup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        excluirBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excluirBackupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDados2Layout = new javax.swing.GroupLayout(pnlDados2);
        pnlDados2.setLayout(pnlDados2Layout);
        pnlDados2Layout.setHorizontalGroup(
            pnlDados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDados2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(excluirBackup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(fazerBackup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlDados2Layout.setVerticalGroup(
            pnlDados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlDados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fazerBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(excluirBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlDados1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDados2, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDados1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDados2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Copia Entre Lojas");
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void fazerBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fazerBackupActionPerformed
        try {
            this.setWaitCursor();

            gerarBackup();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_fazerBackupActionPerformed

    private void excluirBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excluirBackupActionPerformed

        try {
            this.setWaitCursor();

            dropBackup();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_excluirBackupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox clientePreferencial;
    private javax.swing.JCheckBox cliienteEventual;
    private vrframework.bean.button.VRButton excluirBackup;
    private javax.swing.JCheckBox familiaProduto;
    private vrframework.bean.button.VRButton fazerBackup;
    private javax.swing.JCheckBox fornecedor;
    private javax.swing.JLabel jLabel1;
    private vrframework.bean.panel.VRPanel pnlDados1;
    private vrframework.bean.panel.VRPanel pnlDados2;
    private javax.swing.JCheckBox produto;
    private javax.swing.JCheckBox produtoAutomacao;
    private javax.swing.JCheckBox produtoComplemento;
    private javax.swing.JCheckBox produtoFornecedor;
    // End of variables declaration//GEN-END:variables

    private void gerarBackup() throws Exception {

        List<String> listaDeOpcoes = this.getOpcoes();

        if (!listaDeOpcoes.isEmpty()) {

            Util.exibirMensagemConfirmar("Certeza que deseja realizar o backup das tabelas selecionadas", "Confirmação de backup");
            gerador.gerarBackup(listaDeOpcoes);
        } else {
            Util.exibirMensagem("Favor selecionar ao menos uma opção.", "Informativo");
        }
    }

    private void dropBackup() throws Exception {

        List<String> listaDeOpcoes = this.getOpcoes();

        if (!listaDeOpcoes.isEmpty()) {

            Util.exibirMensagemConfirmar("Certeza que deseja deletar o backup das tabelas seleciodas", "Confirmação de excluit backup");
            gerador.dropBackup(listaDeOpcoes);
        } else {
            Util.exibirMensagem("Favor selecionar ao menos uma opção.", "Informativo");
        }
    }

    private List<String> getOpcoes() {

        List<String> listaDeOpcoes = new ArrayList<>();

        if (produto.isSelected()) {
            listaDeOpcoes.add("produto");
        }

        if (produtoComplemento.isSelected()) {
            listaDeOpcoes.add("produtocomplemento");
        }

        if (produtoAutomacao.isSelected()) {
            listaDeOpcoes.add("produtoautomacao");
        }

        if (clientePreferencial.isSelected()) {
            listaDeOpcoes.add("clientepreferencial");
        }

        if (cliienteEventual.isSelected()) {
            listaDeOpcoes.add("clienteeventual");
        }

        if (fornecedor.isSelected()) {
            listaDeOpcoes.add("fornecedor");
        }

        if (produtoFornecedor.isSelected()) {
            listaDeOpcoes.add("produtofornecedor");
        }

        if (familiaProduto.isSelected()) {
            listaDeOpcoes.add("familiaproduto");
        }

        return listaDeOpcoes;
    }
}
