package vrimplantacao2_5.gui.copias;

import javax.swing.DefaultComboBoxModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.dao.copias.CopiaEntreLojasDao;

/**
 *
 * @author Wesley
 */
public class CopiarPromocoesGUI extends VRInternalFrame {

    CopiaEntreLojasDao copiaEntreLojas = new CopiaEntreLojasDao();

    private CopiarPromocoesGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);

        initComponents();

        carregarLojas();
        centralizarForm();
        this.setMaximum(true);
    }

    private static CopiarPromocoesGUI instance;

    public static void Exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new CopiarPromocoesGUI(i_mdiFrame);
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
        clb2 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        pnlDados1 = new vrframework.bean.panel.VRPanel();
        promocao = new javax.swing.JCheckBox();
        pnlDados2 = new vrframework.bean.panel.VRPanel();
        btnCopiar = new vrframework.bean.button.VRButton();

        setResizable(true);
        setTitle("Copiar Promoções e Ofertas");
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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Loja Origem");

        clb1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clb1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Loja Destino");

        javax.swing.GroupLayout pnlDadosLayout = new javax.swing.GroupLayout(pnlDados);
        pnlDados.setLayout(pnlDadosLayout);
        pnlDadosLayout.setHorizontalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDadosLayout.createSequentialGroup()
                        .addComponent(clb1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(194, 194, 194))
                    .addGroup(pnlDadosLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(clb2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(80, 80, 80))
        );
        pnlDadosLayout.setVerticalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clb1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clb2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        org.openide.awt.Mnemonics.setLocalizedText(promocao, "Promoções");

        javax.swing.GroupLayout pnlDados1Layout = new javax.swing.GroupLayout(pnlDados1);
        pnlDados1.setLayout(pnlDados1Layout);
        pnlDados1Layout.setHorizontalGroup(
            pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(promocao)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDados1Layout.setVerticalGroup(
            pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(promocao)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        btnCopiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnCopiar, "Copiar");
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
                .addGap(20, 20, 20)
                .addComponent(pnlDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlDados1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDados2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Copia Entre Lojas");

        setBounds(0, 0, 570, 280);
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void btnCopiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopiarActionPerformed
        try {
            this.setWaitCursor();

            copiarPromocoes();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnCopiarActionPerformed

    private void clb1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clb1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clb1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnCopiar;
    private javax.swing.JComboBox clb1;
    private javax.swing.JComboBox clb2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private vrframework.bean.panel.VRPanel pnlDados;
    private vrframework.bean.panel.VRPanel pnlDados1;
    private vrframework.bean.panel.VRPanel pnlDados2;
    private javax.swing.JCheckBox promocao;
    // End of variables declaration//GEN-END:variables

    public void carregarLojas() throws Exception {
        clb1.setModel(new DefaultComboBoxModel());
        for (LojaVO oLoja : new LojaDAO().carregar()) {
            clb1.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
            clb2.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
    }

    private void copiarPromocoes() throws Exception {

        ItemComboVO lojaOrigem = (ItemComboVO) clb1.getSelectedItem();
        ItemComboVO lojaDestino = (ItemComboVO) clb2.getSelectedItem();

        if (lojaOrigem.id != lojaDestino.id) {
            Util.exibirMensagemConfirmar("Certeza que deseja copiar as promoções da Loja " + lojaOrigem.id + " para a Loja " + lojaDestino.id, "Confirmação de copia");

            if (promocao.isSelected()) {
                copiaEntreLojas.copiaPromocoes(lojaOrigem.id, lojaDestino.id);
            } else {
                Util.exibirMensagem("Favor selecionar ao menos uma opção.", "Informativo");
            }
        } else {
            Util.exibirMensagem("A loja DESTINO deve ser diferente da loja ORIGEM.", "Informativo");
        }
    }
}
