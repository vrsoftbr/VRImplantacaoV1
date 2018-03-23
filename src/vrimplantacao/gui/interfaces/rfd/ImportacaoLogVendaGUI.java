package vrimplantacao.gui.interfaces.rfd;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao.dao.interfaces.LogVendaDAO;
import vrimplantacao.vo.interfaces.DivergenciaVO;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.interfaces.LogVendaDAO.LojaV2;
import vrimplantacao.vo.loja.LojaVO;

public class ImportacaoLogVendaGUI extends VRInternalFrame {

    private List<LojaVO> vLojaDestino = new ArrayList<>();
    LogVendaDAO dao = new LogVendaDAO();

    public ImportacaoLogVendaGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        centralizarForm();

        chkExibeDivergenciaProduto.setSelected(true);
        flcArquivo.setMultiplosSelecionados(true);

        vLojaDestino = new LojaDAO().carregar();

        carregarLojaDestino();
    }

    public void carregarLojaDestino() throws Exception {
        cmbLoja.removeAllItems();
        for (LojaVO oLoja : vLojaDestino) {
            cmbLoja.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }

    }

    @Override
    public void importar() throws Exception {
        Util.validarCampoTela(this.getCampoObrigatorio());
        Thread thread = new Thread() {
            int idLojaDestino = cmbLoja.getId();

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    String[] arquivos = flcArquivo.getArquivo().split(";");
                    ArrayList<DivergenciaVO> vDivergencia = new ArrayList();

                    for (String arq : arquivos) {
                        vDivergencia = dao.importar(arq, chkExibeDivergenciaProduto.isSelected(),
                                chkVerificarCodAnterior.isSelected(), chkVerificarCodigoBarras.isSelected(),
                                idLojaDestino);
                    }

                    ProgressBar.dispose();

                    if (!vDivergencia.isEmpty()) {
                        Util.exibirMensagem("Divergências encontradas. Verifique antes de prosseguir!", getTitle());

                        ExportacaoDivergenciaGUI form = new ExportacaoDivergenciaGUI(mdiFrame);
                        form.setDivergencia(vDivergencia);
                        form.exibeDivergencia();
                        form.setVisible(true);

                    } else {
                        Util.exibirMensagem("Arquivo(s) importado(s) com sucesso!", getTitle());
                    }

                } catch (Exception ex) {
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };

        thread.start();
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRPanel1 = new vrframework.bean.panel.VRPanel();
        Arquivo = new vrframework.bean.label.VRLabel();
        flcArquivo = new vrframework.bean.fileChooser.VRFileChooser();
        chkExibeDivergenciaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkVerificarCodAnterior = new vrframework.bean.checkBox.VRCheckBox();
        chkVerificarCodigoBarras = new vrframework.bean.checkBox.VRCheckBox();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        cmbLoja = new vrframework.bean.comboBox.VRComboBox();
        chkMapearProdutoEAN = new vrframework.bean.checkBox.VRCheckBox();
        btnMapDivergencias = new vrframework.bean.button.VRButton();
        vRToolBarPadrao1 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnImportar = new vrframework.bean.button.VRButton();

        setTitle("Importação de Log Venda");

        Arquivo.setText("Arquivo");

        flcArquivo.setObrigatorio(true);

        chkExibeDivergenciaProduto.setText("Exibe divergência produto não cadastrado");

        chkVerificarCodAnterior.setText("Verificar Código Anterior");

        chkVerificarCodigoBarras.setText("Verificar Código Barras");
        chkVerificarCodigoBarras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVerificarCodigoBarrasActionPerformed(evt);
            }
        });

        vRLabel1.setText("Loja");

        chkMapearProdutoEAN.setText("Mapear produto não cadastrado (EAN)");
        chkMapearProdutoEAN.setEnabled(false);
        chkMapearProdutoEAN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMapearProdutoEANActionPerformed(evt);
            }
        });

        btnMapDivergencias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnMapDivergencias.setText("Divergências");
        btnMapDivergencias.setEnabled(false);
        btnMapDivergencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapDivergenciasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(chkMapearProdutoEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMapDivergencias, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(flcArquivo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkVerificarCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkVerificarCodAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Arquivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkExibeDivergenciaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Arquivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(flcArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkExibeDivergenciaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkVerificarCodAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkVerificarCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMapearProdutoEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMapDivergencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        vRToolBarPadrao1.setRollover(true);
        vRToolBarPadrao1.setVisibleImportar(true);

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnImportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnImportar.setText("Importar");
        btnImportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnImportar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnImportar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRToolBarPadrao1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBarPadrao1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        } catch (Exception e) {
            Util.exibirMensagemErro(e, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnSairActionPerformed
    private void btnImportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarActionPerformed
        try {
            this.setWaitCursor();
            importar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
}//GEN-LAST:event_btnImportarActionPerformed

    private void chkVerificarCodigoBarrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVerificarCodigoBarrasActionPerformed
        chkMapearProdutoEAN.setEnabled(chkVerificarCodigoBarras.isSelected());
        chkMapearProdutoEAN.setSelected(chkVerificarCodigoBarras.isSelected());
        btnMapDivergencias.setEnabled(chkVerificarCodigoBarras.isSelected());
    }//GEN-LAST:event_chkVerificarCodigoBarrasActionPerformed

    private void btnMapDivergenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapDivergenciasActionPerformed
        ItensNaoExistenteGUI.exibir(this.mdiFrame);
    }//GEN-LAST:event_btnMapDivergenciasActionPerformed

    private void chkMapearProdutoEANActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMapearProdutoEANActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkMapearProdutoEANActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.label.VRLabel Arquivo;
    private vrframework.bean.button.VRButton btnImportar;
    private vrframework.bean.button.VRButton btnMapDivergencias;
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.checkBox.VRCheckBox chkExibeDivergenciaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkMapearProdutoEAN;
    private vrframework.bean.checkBox.VRCheckBox chkVerificarCodAnterior;
    private vrframework.bean.checkBox.VRCheckBox chkVerificarCodigoBarras;
    private vrframework.bean.comboBox.VRComboBox cmbLoja;
    private vrframework.bean.fileChooser.VRFileChooser flcArquivo;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao1;
    // End of variables declaration//GEN-END:variables
}
