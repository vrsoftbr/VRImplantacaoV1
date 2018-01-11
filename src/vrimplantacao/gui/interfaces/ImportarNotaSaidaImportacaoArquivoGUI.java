package vrimplantacao.gui.interfaces;

import vrimplantacao.gui.interfaces.rfd.ExportacaoDivergenciaGUI;
import java.util.List;
import vrimplantacao.dao.fiscal.CfopDAO;
import vrimplantacao.dao.notafiscal.NotaSaidaDAO;
import vrimplantacao.dao.notafiscal.ImportarNotaSaidaImportacaoDAO;
import vrimplantacao.dao.notafiscal.TipoSaidaDAO;
import vrimplantacao.vo.notafiscal.NotaSaidaVO;
import vrimplantacao.vo.notafiscal.TipoSaidaVO;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.notafiscal.ImportarNotaSaidaImportacaoDAO.LojaV2;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;

public class ImportarNotaSaidaImportacaoArquivoGUI extends VRInternalFrame {

    private NotaSaidaVO oImportacao = null;
    private TipoSaidaVO oTipoSaida = null;
    ImportarNotaSaidaImportacaoDAO dao = new ImportarNotaSaidaImportacaoDAO();

    public ImportarNotaSaidaImportacaoArquivoGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        carregarLojaV2();
        
        centralizarForm();
    }

    private void carregarNotaSaida() throws Exception {
        String cfop = dao.carregarCFOP(flcArquivo.getArquivo());
        txtNatOperacao.setText(dao.carregarNatOperacao(flcArquivo.getArquivo()));

        cboTipoNotaSaida.removeAllItems();

        List<TipoSaidaVO> vTipoSaida = new CfopDAO().carregarTipoSaida(cfop);

        for (TipoSaidaVO oTipo : vTipoSaida) {
            cboTipoNotaSaida.addItem(new ItemComboVO(oTipo.id, oTipo.descricao));
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
                    ProgressBar.setStatus("Importando Nota Fiscal...");
                    
                    if (cboLojaV2.getItemCount() > 0) {
                        dao.impLoja = (LojaV2) cboLojaV2.getSelectedItem();
                    }
                    oImportacao = dao.carregar(flcArquivo.getArquivo(), cboTipoNotaSaida.getId(), 
                            chkVerificarCodigoAnterior.isSelected());

                    oTipoSaida = new TipoSaidaDAO().carregar(oImportacao.idTipoSaida);

                    if ((oTipoSaida.destinatarioCliente || oTipoSaida.geraReceber) && oImportacao.idClienteEventualDestinatario == -1) {
                        throw new VRException("O destinatário da nota fiscal deve ser um cliente eventual!");

                    } else if (!oTipoSaida.destinatarioCliente && oImportacao.idFornecedorDestinatario == -1) {
                        throw new VRException("O destinatário da nota fiscal deve ser um fornecedor!");
                    }

                    ProgressBar.dispose();

                    if (!oImportacao.vDivergencia.isEmpty()) {
                        ExportacaoDivergenciaGUI form = new ExportacaoDivergenciaGUI(mdiFrame);
                        form.setDivergencia(oImportacao.vDivergencia);
                        form.exibeDivergencia();
                        form.setVisible(true);

                        Util.exibirMensagem("Divergências encontradas. Verifique antes de prosseguir!", getTitle());

                    } else {
                        new NotaSaidaDAO().salvar(oImportacao);

                        Util.exibirMensagem("Nota Fiscal importada com sucesso!", getTitle());
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

        tlbToolBar = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        flcArquivo = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel34 = new vrframework.bean.label.VRLabel();
        cboTipoNotaSaida = new vrframework.bean.comboBox.VRComboBox();
        chkVerificarCodigoAnterior = new vrframework.bean.checkBox.VRCheckBox();
        pnlLojaV2 = new vrframework.bean.panel.VRPanel();
        vRLabel35 = new vrframework.bean.label.VRLabel();
        cboLojaV2 = new javax.swing.JComboBox();
        vRLabel36 = new vrframework.bean.label.VRLabel();
        txtNatOperacao = new vrframework.bean.textField.VRTextField();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnImportar = new vrframework.bean.button.VRButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Importação de Nota Saída");

        tlbToolBar.setRollover(true);
        tlbToolBar.setVisibleImportar(true);

        vRLabel1.setText("Arquivo");

        flcArquivo.setObrigatorio(true);
        flcArquivo.addEventoFileChooserListener(new vrframework.bean.fileChooser.VREventoFileChooserListener() {
            public void aposSelecao(vrframework.bean.fileChooser.VREventoFileChooser evt) {
                flcArquivoAposSelecao(evt);
            }
        });

        vRLabel34.setText("Tipo Saída");

        cboTipoNotaSaida.setObrigatorio(true);

        chkVerificarCodigoAnterior.setText("Verificar Código Anterior");

        pnlLojaV2.setBorder(null);
        pnlLojaV2.setEnabled(false);
        pnlLojaV2.setOpaque(false);

        vRLabel35.setText("Lojas encontradas (Importação V2)");

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

        javax.swing.GroupLayout pnlLojaV2Layout = new javax.swing.GroupLayout(pnlLojaV2);
        pnlLojaV2.setLayout(pnlLojaV2Layout);
        pnlLojaV2Layout.setHorizontalGroup(
            pnlLojaV2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLojaV2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLojaV2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlLojaV2Layout.createSequentialGroup()
                        .addComponent(vRLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cboLojaV2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlLojaV2Layout.setVerticalGroup(
            pnlLojaV2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLojaV2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLojaV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vRLabel36.setText("Natureza da operação");

        txtNatOperacao.setEnabled(false);

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlLojaV2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(flcArquivo, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                    .addComponent(cboTipoNotaSaida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkVerificarCodigoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtNatOperacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(flcArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNatOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboTipoNotaSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkVerificarCodigoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLojaV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
            Util.exibirMensagemErro(ex, getTitle());

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
    private void flcArquivoAposSelecao(vrframework.bean.fileChooser.VREventoFileChooser evt) {//GEN-FIRST:event_flcArquivoAposSelecao
        try {
            this.setWaitCursor();
            carregarNotaSaida();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_flcArquivoAposSelecao

    private void cboLojaV2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLojaV2ActionPerformed
        // TODO add your handling code here:        
    }//GEN-LAST:event_cboLojaV2ActionPerformed

    private void cboLojaV2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboLojaV2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLojaV2ItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnImportar;
    private vrframework.bean.button.VRButton btnSair;
    private javax.swing.JComboBox cboLojaV2;
    private vrframework.bean.comboBox.VRComboBox cboTipoNotaSaida;
    private vrframework.bean.checkBox.VRCheckBox chkVerificarCodigoAnterior;
    private vrframework.bean.fileChooser.VRFileChooser flcArquivo;
    private vrframework.bean.panel.VRPanel pnlLojaV2;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao tlbToolBar;
    private vrframework.bean.textField.VRTextField txtNatOperacao;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel34;
    private vrframework.bean.label.VRLabel vRLabel35;
    private vrframework.bean.label.VRLabel vRLabel36;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    // End of variables declaration//GEN-END:variables

    private void carregarLojaV2() throws Exception {
        ImportarNotaSaidaImportacaoDAO dao = new ImportarNotaSaidaImportacaoDAO();
        if (dao.isImportacaoV2()) {
            pnlLojaV2.setEnabled(true);
            cboLojaV2.removeAllItems();
            for (LojaV2 oTipo :dao.carregarLojaV2()) {
                cboLojaV2.addItem(oTipo);
            }
        }
    }

}
