package vrimplantacao2_5.gui.copias;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.dao.copias.ZerarEstoquesDao;

/**
 *
 * @author Wesley
 */
public class ZerarEstoqueGUI extends VRInternalFrame {

    ZerarEstoquesDao zerarEstoquesDao = new ZerarEstoquesDao();

    private ZerarEstoqueGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);

        initComponents();

        carregarLojas();
        centralizarForm();
        this.setMaximum(true);
    }

    private static ZerarEstoqueGUI instance;

    public static void Exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new ZerarEstoqueGUI(i_mdiFrame);
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

        pnlDados = new vrframework.bean.panel.VRPanel();
        jLabel1 = new javax.swing.JLabel();
        clb1 = new javax.swing.JComboBox();
        pnlDados1 = new vrframework.bean.panel.VRPanel();
        estoque = new javax.swing.JCheckBox();
        estoqueMaximo = new javax.swing.JCheckBox();
        estoqueMinimo = new javax.swing.JCheckBox();
        pnlDados2 = new vrframework.bean.panel.VRPanel();
        btnCopiar = new vrframework.bean.button.VRButton();

        setResizable(true);
        setTitle("Zerar Estoques");
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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Selecione a loja que deseja zerar o estoque.");

        clb1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clb1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDadosLayout = new javax.swing.GroupLayout(pnlDados);
        pnlDados.setLayout(pnlDadosLayout);
        pnlDadosLayout.setHorizontalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(36, 36, 36)
                .addComponent(clb1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );
        pnlDadosLayout.setVerticalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clb1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43))
        );

        org.openide.awt.Mnemonics.setLocalizedText(estoque, "Estoque");
        estoque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estoqueActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(estoqueMaximo, "Estoque Máximo");

        org.openide.awt.Mnemonics.setLocalizedText(estoqueMinimo, "Estoque Mínimo");

        javax.swing.GroupLayout pnlDados1Layout = new javax.swing.GroupLayout(pnlDados1);
        pnlDados1.setLayout(pnlDados1Layout);
        pnlDados1Layout.setHorizontalGroup(
            pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(estoque)
                .addGap(68, 68, 68)
                .addComponent(estoqueMaximo)
                .addGap(51, 51, 51)
                .addComponent(estoqueMinimo)
                .addGap(30, 30, 30))
        );
        pnlDados1Layout.setVerticalGroup(
            pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estoque)
                    .addComponent(estoqueMaximo)
                    .addComponent(estoqueMinimo))
                .addContainerGap(67, Short.MAX_VALUE))
        );

        btnCopiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnCopiar, "Atualizar");
        btnCopiar.setFocusable(false);
        btnCopiar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCopiar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCopiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDados2Layout = new javax.swing.GroupLayout(pnlDados2);
        pnlDados2.setLayout(pnlDados2Layout);
        pnlDados2Layout.setHorizontalGroup(
            pnlDados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDados2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCopiar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlDados2Layout.setVerticalGroup(
            pnlDados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCopiar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDados1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDados2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(pnlDados1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(pnlDados2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Zerar Estoques");
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void btnCopiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopiarActionPerformed
        try {
            this.setWaitCursor();

            copiarInfo();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnCopiarActionPerformed

    private void clb1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clb1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clb1ActionPerformed

    private void estoqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estoqueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_estoqueActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnCopiar;
    private javax.swing.JComboBox clb1;
    private javax.swing.JCheckBox estoque;
    private javax.swing.JCheckBox estoqueMaximo;
    private javax.swing.JCheckBox estoqueMinimo;
    private javax.swing.JLabel jLabel1;
    private vrframework.bean.panel.VRPanel pnlDados;
    private vrframework.bean.panel.VRPanel pnlDados1;
    private vrframework.bean.panel.VRPanel pnlDados2;
    // End of variables declaration//GEN-END:variables

    public void carregarLojas() throws Exception {
        clb1.setModel(new DefaultComboBoxModel());
        for (LojaVO oLoja : new LojaDAO().carregar()) {
            clb1.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
    }

    private void copiarInfo() throws Exception {
        List<String> listaDeOpcoes = new ArrayList<>();

        ItemComboVO lojaDestino = (ItemComboVO) clb1.getSelectedItem();

        if (estoque.isSelected()) {
            listaDeOpcoes.add(" estoque = 0");
        }

        if (estoqueMaximo.isSelected()) {
            listaDeOpcoes.add(" estoquemaximo = 0");
        }

        if (estoqueMinimo.isSelected()) {
            listaDeOpcoes.add(" estoqueminimo = 0");
        }

        if (!listaDeOpcoes.isEmpty()) {
            Util.exibirMensagemConfirmar("Tem certeza que deseja zerar os estoques selecionados da Loja " + lojaDestino.id + ". Após a confirmação, nao será possivel reverter a ação.", "Confirmação zera de estoque");
                zerarEstoquesDao.zerarEstoquePorLoja(lojaDestino.id, listaDeOpcoes);
        } else {
            Util.exibirMensagem("Favor selecionar ao menos uma opção.", "Informativo");
        }
    }
}
