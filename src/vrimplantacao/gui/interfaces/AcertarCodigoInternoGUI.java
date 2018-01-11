package vrimplantacao.gui.interfaces;

import java.util.List;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.CodigoAnteriorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.interfaces.AcertarCodigoInternoDAO;
import vrimplantacao.vo.loja.LojaVO;

public class AcertarCodigoInternoGUI extends VRInternalFrame {

    public AcertarCodigoInternoGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        carregarLojaDestino();

        centralizarForm();
        this.setMaximum(false);
    }

    private void carregarLojaDestino() throws Exception {
        List<LojaVO> vLojaDestino = new LojaDAO().carregar();
        for (LojaVO oLoja : vLojaDestino) {
            cmbLojaDestino.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
    }

    @Override
    public void importar() throws Exception {
        Util.validarCampoTela(this.getCampoObrigatorio());
        
        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    
                    if (!flcArquivoProduto.getArquivo().isEmpty()) {
                        new AcertarCodigoInternoDAO().importarProdutosFreitas(flcArquivoProduto.getArquivo());
                    }
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação Produtos Freitas realizada com sucesso!", getTitle());

                } catch (Exception ex) {
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };

        thread.start();
    }
    
    private void acertarCodigoProduto0() {
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    
                    new AcertarCodigoInternoDAO().importarAcertarProduto0();
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Acerto id_produto2 realizada com sucesso!", getTitle());
                    
                    
                } catch(Exception ex) {
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };
        
        thread.start();
    }

    private void acertarCodigoBarra() {
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    
                    new CodigoAnteriorDAO().acertarCodigoBarras();

                    ProgressBar.dispose();

                    Util.exibirMensagem("Acerto codigobarras <= 6 realizada com sucesso!", getTitle());
                    
                } catch(Exception ex) {
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };
        
        thread.start();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRRadioButton1 = new vrframework.bean.radioButton.VRRadioButton();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        flcArquivoProduto = new vrframework.bean.fileChooser.VRFileChooser();
        vRRadioButton2 = new vrframework.bean.radioButton.VRRadioButton();
        cmbLojaDestino = new vrframework.bean.comboBox.VRComboBox();
        vRToolBar1 = new vrframework.bean.toolBar.VRToolBar();
        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRButton1 = new vrframework.bean.button.VRButton();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        btnAlterarCodigoProduto = new vrframework.bean.button.VRButton();
        btnAcertarCodigoBarra = new vrframework.bean.button.VRButton();

        vRRadioButton1.setText("vRRadioButton1");

        setTitle("Alteração ID Produtos - Padrão");

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem"));
        vRPanel1.setPreferredSize(new java.awt.Dimension(350, 350));

        vRLabel1.setText("Arquivo");

        flcArquivoProduto.setObrigatorio(true);

        vRRadioButton2.setText("Produtos - Freitas");

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(flcArquivoProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(flcArquivoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(vRRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        vRToolBar1.setRollover(true);

        vRToolBarPadrao3.setRollover(true);
        vRToolBarPadrao3.setVisibleImportar(true);
        vRToolBar1.add(vRToolBarPadrao3);

        vRButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/ignorar.png"))); // NOI18N
        vRButton1.setToolTipText("Informações");
        vRButton1.setFocusable(false);
        vRButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        vRButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        vRButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vRButton1ActionPerformed(evt);
            }
        });
        vRToolBar1.add(vRButton1);

        btnMigrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnMigrar.setText("1 - Importar Produtos Arquivo");
        btnMigrar.setFocusable(false);
        btnMigrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnMigrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMigrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMigrarActionPerformed(evt);
            }
        });

        btnAlterarCodigoProduto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnAlterarCodigoProduto.setText("2 - Alterar Codigo Produto 0");
        btnAlterarCodigoProduto.setFocusable(false);
        btnAlterarCodigoProduto.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAlterarCodigoProduto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAlterarCodigoProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarCodigoProdutoActionPerformed(evt);
            }
        });

        btnAcertarCodigoBarra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnAcertarCodigoBarra.setText("3 - Acertar Código Barras");
        btnAcertarCodigoBarra.setFocusable(false);
        btnAcertarCodigoBarra.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAcertarCodigoBarra.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAcertarCodigoBarra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcertarCodigoBarraActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAlterarCodigoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAcertarCodigoBarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAcertarCodigoBarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterarCodigoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                    .addComponent(vRToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        try {
            this.setWaitCursor();
            importar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void vRButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vRButton1ActionPerformed
        try {
            PlanilhaPadraoInformacaoGUI form = new PlanilhaPadraoInformacaoGUI(mdiFrame);
            form.setVisible(true);

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_vRButton1ActionPerformed

    private void btnAlterarCodigoProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarCodigoProdutoActionPerformed
        // TODO add your handling code here:
        acertarCodigoProduto0();
    }//GEN-LAST:event_btnAlterarCodigoProdutoActionPerformed

    private void btnAcertarCodigoBarraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcertarCodigoBarraActionPerformed
        // TODO add your handling code here:
        acertarCodigoBarra();
    }//GEN-LAST:event_btnAcertarCodigoBarraActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnAcertarCodigoBarra;
    private vrframework.bean.button.VRButton btnAlterarCodigoProduto;
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.comboBox.VRComboBox cmbLojaDestino;
    private vrframework.bean.fileChooser.VRFileChooser flcArquivoProduto;
    private vrframework.bean.button.VRButton vRButton1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.radioButton.VRRadioButton vRRadioButton1;
    private vrframework.bean.radioButton.VRRadioButton vRRadioButton2;
    private vrframework.bean.toolBar.VRToolBar vRToolBar1;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
