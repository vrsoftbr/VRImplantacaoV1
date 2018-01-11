package vrimplantacao.gui.interfaces.nfce;

import java.util.ArrayList;
import vrimplantacao.dao.ParametroDAO;
import vrimplantacao.dao.notafiscal.NotaSaidaNfceDAO;
import vrimplantacao.vo.interfaces.DivergenciaVO;
import vrimplantacao.vo.interfaces.TipoDivergencia;
import vrimplantacao.vo.venda.VendaVO;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
/*import vrframework.classe.CampoTela;
import vrframework.classe.Mensagem;*/
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.gui.interfaces.rfd.ExportacaoDivergenciaGUI;
import vrimplantacao.dao.notafiscal.NotaSaidaNfceDAO.LojaV2;

public class NotaSaidaNfceImportacaoArquivoGUI extends VRInternalFrame {
    
    NotaSaidaNfceDAO dao = new NotaSaidaNfceDAO();

    public NotaSaidaNfceImportacaoArquivoGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        flcArquivo.setMultiplosSelecionados(true);
        carregarLojaV2();

        centralizarForm();
        habilitarChkBaixaEstoque();
    }

    private void carregarLojaV2() throws Exception {
        NotaSaidaNfceDAO dao = new NotaSaidaNfceDAO();
        if (dao.isImportacaoV2()) {
            cboLojaV2.removeAllItems();
            for (LojaV2 oTipo :dao.carregarLojaV2()) {
                cboLojaV2.addItem(oTipo);
            }
        }
    }
    
    @Override
    public void importar() throws Exception {
        //CampoTela.validar(this.getCampoObrigatorio());
        Util.validarCampoTela(this.getCampoObrigatorio());

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setStatus("Importando NFC-e...");
                    if (cboLojaV2.getItemCount() > 0) {
                        dao.impLoja = (LojaV2) cboLojaV2.getSelectedItem();
                    }

                    String[] arquivos = flcArquivo.getArquivo().split(";");
                    ArrayList<DivergenciaVO> vDivergenciaGeral = new ArrayList();

                    for (String arq : arquivos) {

                        VendaVO oVenda = null;

                        try {
                            oVenda = dao.importar(arq, chkVerificarCodigoAnterior.isSelected());

                        } catch (Exception ex) {
                            vDivergenciaGeral.add(new DivergenciaVO("Não foi possível importar o arquivo " + arq + "!", TipoDivergencia.ERRO.getId()));
                            continue;
                        }

                        long idVendaNFCe = new NotaSaidaNfceDAO().getIdVendaNFCe(oVenda);

                        if (idVendaNFCe <= 0) {
                            oVenda.baixarEstoque = chkBaixaEstoque.isSelected();
                            new NotaSaidaNfceDAO().salvarVenda(oVenda);

                        } else {
                            new NotaSaidaNfceDAO().atualizarVendaNFCe(idVendaNFCe, oVenda);

                        }
                    }

                    ProgressBar.dispose();

                    if (!vDivergenciaGeral.isEmpty()) {
                        ExportacaoDivergenciaGUI form = new ExportacaoDivergenciaGUI(mdiFrame);
                        form.setDivergencia(vDivergenciaGeral);
                        form.exibeDivergencia();
                        form.setVisible(true);
                        if (arquivos.length != vDivergenciaGeral.size()) {
                            Util.exibirMensagem("Arquivo(s) importado(s) com divergência(s)!", getTitle());
                        } else {
                            Util.exibirMensagem("Arquivo(s) com divergência(s)!", getTitle());
                        }

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

    private void habilitarChkBaixaEstoque() throws Exception {
        chkBaixaEstoque.setEnabled(new ParametroDAO().get(104).getBoolean());
        chkBaixaEstoque.setSelected(false);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tlbToolBar = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        flcArquivo = new vrframework.bean.fileChooser.VRFileChooser();
        chkBaixaEstoque = new vrframework.bean.checkBox.VRCheckBox();
        chkVerificarCodigoAnterior = new vrframework.bean.checkBox.VRCheckBox();
        cboLojaV2 = new javax.swing.JComboBox();
        vRLabel35 = new vrframework.bean.label.VRLabel();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnImportar = new vrframework.bean.button.VRButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Importação de NFC-e");

        tlbToolBar.setRollover(true);
        tlbToolBar.setVisibleImportar(true);

        vRLabel1.setText("Arquivo");

        flcArquivo.setObrigatorio(true);

        chkBaixaEstoque.setSelected(true);
        chkBaixaEstoque.setText("Baixar estoque loja");

        chkVerificarCodigoAnterior.setText("Verificar Código Anterior");

        cboLojaV2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboLojaV2ItemStateChanged(evt);
            }
        });
        cboLojaV2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLojaV2ActionPerformed(evt);
            }
        });

        vRLabel35.setText("Lojas encontradas (Importação V2)");

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboLojaV2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkVerificarCodigoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkBaixaEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(flcArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(flcArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkBaixaEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkVerificarCodigoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vRLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLojaV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
            .addComponent(btnSair, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnImportar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tlbToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tlbToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

        } catch (Exception ex) {
            //Mensagem.exibirErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnSairActionPerformed
    private void btnImportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarActionPerformed
        try {
            this.setWaitCursor();
            importar();

        } catch (Exception ex) {
            //Mensagem.exibirErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnImportarActionPerformed

    private void cboLojaV2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboLojaV2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLojaV2ItemStateChanged

    private void cboLojaV2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLojaV2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLojaV2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnImportar;
    private vrframework.bean.button.VRButton btnSair;
    private javax.swing.JComboBox cboLojaV2;
    private vrframework.bean.checkBox.VRCheckBox chkBaixaEstoque;
    private vrframework.bean.checkBox.VRCheckBox chkVerificarCodigoAnterior;
    private vrframework.bean.fileChooser.VRFileChooser flcArquivo;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao tlbToolBar;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel35;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    // End of variables declaration//GEN-END:variables
    
}
