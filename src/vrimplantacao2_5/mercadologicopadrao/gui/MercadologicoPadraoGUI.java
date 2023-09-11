package vrimplantacao2_5.mercadologicopadrao.gui;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrimplantacao2_5.mercadologicopadrao.service.MercadologicoPadraoService;

public class MercadologicoPadraoGUI extends VRInternalFrame {
    
    private MercadologicoPadraoService repository = new MercadologicoPadraoService();
    
    public MercadologicoPadraoGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();       
        this.title = "Mercadológico Padrão ";
        setMaximizable(false);
        setResizable(false);
        centralizarForm();
        carregaIntrucoes();
        this.setMaximum(false);
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            new MercadologicoPadraoGUI(i_mdiFrame).setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir Mercadológico Padrão");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        passo1 = new javax.swing.JLabel();
        intrucaoPasso1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        passo2 = new javax.swing.JLabel();
        intrucaoPasso2 = new javax.swing.JLabel();
        btnDeleteMercAnterior = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        passo3 = new javax.swing.JLabel();
        intrucaoPasso3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        passo4 = new javax.swing.JLabel();
        intrucaoPasso4 = new javax.swing.JLabel();
        btnFinalizar = new javax.swing.JButton();
        imagem = new javax.swing.JLabel();
        btnMercTemporario = new javax.swing.JButton();

        setTitle("Mercadológico Padrão");
        setToolTipText("");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        passo1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        passo1.setText("Passo 1:");

        intrucaoPasso1.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        intrucaoPasso1.setText("Instrucao 1");

        passo2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        passo2.setText("Passo 2:");

        intrucaoPasso2.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        intrucaoPasso2.setText("Delete o mercadológico atual da base clicando no botão ao lado:");

        btnDeleteMercAnterior.setText("Deletar Mercadológico Atual");
        btnDeleteMercAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteMercAnteriorActionPerformed(evt);
            }
        });

        passo3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        passo3.setText("Passo 3:");

        intrucaoPasso3.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        intrucaoPasso3.setText("Clique no botão ao lado para gerar o Mercadológico Padrão VR:");

        jButton1.setText("Gerar Mercadológico Padrão");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        passo4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        passo4.setText("Passo 4:");

        intrucaoPasso4.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        intrucaoPasso4.setText("Instrucao 1");

        btnFinalizar.setText("Finalizar");
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });

        imagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrimplantacao2_5/mercadologicopadrao/gui/vrsoftware-pqn.png"))); // NOI18N

        btnMercTemporario.setText("Mercadológico Temporário");
        btnMercTemporario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMercTemporarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(intrucaoPasso2, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDeleteMercAnterior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(intrucaoPasso3, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(intrucaoPasso4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passo1)
                            .addComponent(passo2)
                            .addComponent(passo3)
                            .addComponent(passo4)
                            .addComponent(btnFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(intrucaoPasso1, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMercTemporario, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(imagem)
                .addGap(71, 71, 71))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(passo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intrucaoPasso1)
                    .addComponent(btnMercTemporario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passo2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intrucaoPasso2)
                    .addComponent(btnDeleteMercAnterior))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passo3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intrucaoPasso3)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passo4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(intrucaoPasso4)
                .addGap(18, 18, 18)
                .addComponent(btnFinalizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imagem)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        
    }//GEN-LAST:event_formComponentHidden

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formInternalFrameClosing

    private void btnMercTemporarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMercTemporarioActionPerformed
        repository.executaPasso1();
    }//GEN-LAST:event_btnMercTemporarioActionPerformed

    private void btnDeleteMercAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteMercAnteriorActionPerformed
        repository.executaPasso2();
    }//GEN-LAST:event_btnDeleteMercAnteriorActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        repository.executaPasso3();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        repository.executaPasso4();
    }//GEN-LAST:event_btnFinalizarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteMercAnterior;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JButton btnMercTemporario;
    private javax.swing.JLabel imagem;
    private javax.swing.JLabel intrucaoPasso1;
    private javax.swing.JLabel intrucaoPasso2;
    private javax.swing.JLabel intrucaoPasso3;
    private javax.swing.JLabel intrucaoPasso4;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel passo1;
    private javax.swing.JLabel passo2;
    private javax.swing.JLabel passo3;
    private javax.swing.JLabel passo4;
    // End of variables declaration//GEN-END:variables

    private void carregaIntrucoes() {
        intrucaoPasso1.setBackground(null);
        intrucaoPasso1.setText("<html>"
                + "<body>"
                + "<p align = \"justify\">"
                + "     Clique no botão ao lado para dar inicio ao processo.<br>"
                + "     O sistema irá criar um mercadológico temporário (\"TEMP\") e vai inserir todos os produtos nesse mercadológico."
                + "</p>"
                + "</body"
                + "</html>");
        
        intrucaoPasso4.setBackground(null);
        intrucaoPasso4.setText("<html>"
                + "<body>"
                + "<p align = \"justify\">"
                + "     Clique no botão abaixo para finalizar o processo.<br>"
                + "     Nessa etapa o sistema irá lanção todos os produtos para o mercadológico \"A ACERTAR\", "
                + "corrigirá a sequencia de id's da tabela mercadológico e deleterá o mercadológico temporário."
                + "</p>"
                + "</body"
                + "</html>");
    }

}