package vrimplantacao2_5.mural.gui;

import javax.swing.JOptionPane;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrimplantacao2_5.mural.service.MemorandoService;
import vrimplantacao2_5.mural.vo.MemorandoVO;

public class MemorandoGUI extends VRInternalFrame {

    private static MemorandoGUI memorando = null;
    private static String numeroLembrete = "0";
    private MemorandoVO memoVO;
    private MemorandoService memorandoService;

    public static String getNumeroLembrete() {
        return numeroLembrete;
    }

    public static void setNumeroLembrete(String numeroLembrete) {
        MemorandoGUI.numeroLembrete = numeroLembrete;
    }

    public MemorandoGUI(VRMdiFrame menuGUI, MemorandoService memorandoService) throws Exception {
        super(menuGUI);
        initComponents();
        lbEsquerda.setText("<html>&#8592;</html>");
        lbDireita.setText("<html>&#8594;</html>");
        txtAreaMemo.setLineWrap(true);
        txtCentro.setText(numeroLembrete);
        setLocation(10, 10);
        this.memorandoService = memorandoService;
        carregarLembretes();
    }

    public static void exibir(VRMdiFrame menuGUI, MemorandoService memorandoService) {
        try {
            menuGUI.setWaitCursor();

            if (memorando == null || memorando.isClosed()) {
                memorando = new MemorandoGUI(menuGUI, memorandoService);
            }

            memorando.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Banco de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaMemo = new javax.swing.JTextArea();
        btnGravar = new javax.swing.JButton();
        btnDeletar = new javax.swing.JButton();
        lbEsquerda = new javax.swing.JLabel();
        lbDireita = new javax.swing.JLabel();
        txtCentro = new javax.swing.JTextField();
        btnlimpar = new javax.swing.JButton();

        setTitle("Memorando");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Lembrete:");

        txtAreaMemo.setBackground(new java.awt.Color(255, 204, 102));
        txtAreaMemo.setColumns(20);
        txtAreaMemo.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtAreaMemo.setForeground(new java.awt.Color(0, 0, 0));
        txtAreaMemo.setRows(5);
        txtAreaMemo.setText("Anote detalhes do projeto aqui, depois clique em Gravar.\nCuidado para não salvar dados sensíveis.");
        jScrollPane1.setViewportView(txtAreaMemo);

        org.openide.awt.Mnemonics.setLocalizedText(btnGravar, "Gravar");
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnDeletar, "Deletar do BD");
        btnDeletar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletarActionPerformed(evt);
            }
        });

        lbEsquerda.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(lbEsquerda, "<-");
        lbEsquerda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbEsquerdaMouseClicked(evt);
            }
        });

        lbDireita.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(lbDireita, "->");
        lbDireita.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbDireitaMouseClicked(evt);
            }
        });

        txtCentro.setEditable(false);
        txtCentro.setText("00");

        org.openide.awt.Mnemonics.setLocalizedText(btnlimpar, "Limpar lembrete");
        btnlimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnlimparActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnlimpar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnDeletar)
                        .addGap(1, 1, 1)
                        .addComponent(lbEsquerda, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCentro, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbDireita)
                        .addGap(55, 55, 55)
                        .addComponent(btnGravar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnlimpar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGravar)
                    .addComponent(btnDeletar)
                    .addComponent(lbEsquerda)
                    .addComponent(lbDireita)
                    .addComponent(txtCentro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeletarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletarActionPerformed
        int decicao = JOptionPane.showConfirmDialog(null, "Tem Certezade que deseja deletar os lembretes do banco?", "Apagar Lembretes", JOptionPane.YES_NO_OPTION);
        if (decicao == 0) {
        memorandoService.deletarLembretes();
        carregarLembretes();
        }
    }//GEN-LAST:event_btnDeletarActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        memorandoService.incluir(txtAreaMemo.getText());
        carregarLembretes();
    }//GEN-LAST:event_btnGravarActionPerformed

    private void lbDireitaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbDireitaMouseClicked
        int numeroLembrete = Integer.parseInt(txtCentro.getText());
        numeroLembrete++;
        if (numeroLembrete != 0) {
            memoVO = memorandoService.carregarLembretePorId(numeroLembrete);
        }
        if (memoVO == null || numeroLembrete == 0) {
            JOptionPane.showMessageDialog(null, "Não há mais lembretes cadastrados");
        } else {
            carregarLembretePorId(memoVO);
        }
    }//GEN-LAST:event_lbDireitaMouseClicked

    private void lbEsquerdaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbEsquerdaMouseClicked
        int numeroLembrete = Integer.parseInt(txtCentro.getText());
        numeroLembrete--;
        if (numeroLembrete != 0) {
            memoVO = memorandoService.carregarLembretePorId(numeroLembrete);
        }
        if (memoVO == null || numeroLembrete == 0) {
            JOptionPane.showMessageDialog(null, "Não há mais lembretes cadastrados");
        } else {
            carregarLembretePorId(memoVO);
        }
    }//GEN-LAST:event_lbEsquerdaMouseClicked

    private void btnlimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnlimparActionPerformed
        txtAreaMemo.setText("");
        txtCentro.setText("X");
    }//GEN-LAST:event_btnlimparActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeletar;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnlimpar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbDireita;
    private javax.swing.JLabel lbEsquerda;
    private javax.swing.JTextArea txtAreaMemo;
    private javax.swing.JTextField txtCentro;
    // End of variables declaration//GEN-END:variables

    private void carregarLembretes() {
        memoVO = memorandoService.carregarUltimoLembrete();
        txtAreaMemo.setText(memoVO.getId() == 0 ? memoVO.getLembrete() + "\n\n" + memoVO.getData()
                : memoVO.getLembrete() + "\n\nCadastrado em: " + memoVO.getData());
        txtCentro.setText(memoVO.getId() == 0 ? "0" : String.valueOf(memoVO.getId()));
    }

    private void carregarLembretePorId(MemorandoVO memoVO) {
        txtAreaMemo.setText(memoVO.getLembrete() + "\n\n" + memoVO.getData());
        txtCentro.setText(String.valueOf(memoVO.getId()));
    }
}
