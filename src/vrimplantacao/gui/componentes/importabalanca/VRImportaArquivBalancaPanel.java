/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.gui.componentes.importabalanca;

import java.awt.Dimension;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.panel.VRPanel;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.dao.cadastro.FilizolaSalvarArquivos;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.nutricional.CondicaoToledo;
import vrimplantacao2.dao.cadastro.nutricional.ToledoService;

/**
 *
 * @author Leandro
 */
public class VRImportaArquivBalancaPanel extends VRPanel {

    private String sistema;
    private String loja;
    private CondicaoToledo condicaoToledo;
    private ToledoService servico = new ToledoService();
    private File[] filesMemo;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
        if (this.sistema != null && this.loja != null) {
            tabsOpcoes.remove(tabNutricional);
            tabsOpcoes.addTab("Nutricionais", tabNutricional);
        } else {
            tabsOpcoes.remove(tabNutricional);
        }
    }

    public String getLoja() {
        if (loja.equals("")) {
            loja = "1";
        }
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
        if (this.sistema != null && this.loja != null) {
            tabsOpcoes.remove(tabNutricional);
            tabsOpcoes.addTab("Nutricionais", tabNutricional);
        } else {
            tabsOpcoes.remove(tabNutricional);
        }
    }

    /**
     * Creates new form ArquivoDeBalanca
     */
    public VRImportaArquivBalancaPanel() {
        initComponents();

        rdbCodigoInterno.setSelected(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdbGroup1 = new javax.swing.ButtonGroup();
        rdbNutriconal = new javax.swing.ButtonGroup();
        tabsOpcoes = new javax.swing.JTabbedPane();
        tabPesaveis = new javax.swing.JPanel();
        rdbCadTxt = new vrframework.bean.radioButton.VRRadioButton();
        rdbTxtItens = new vrframework.bean.radioButton.VRRadioButton();
        rdbItensMgv = new vrframework.bean.radioButton.VRRadioButton();
        rdbPlanilha = new vrframework.bean.radioButton.VRRadioButton();
        btnImportarBalanca = new vrframework.bean.button.VRButton();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        btnAbrir = new vrframework.bean.button.VRButton();
        txtArquivoBalanca = new vrframework.bean.textField.VRTextField();
        tabNutricional = new javax.swing.JPanel();
        rdbFilizolaRdc360 = new javax.swing.JRadioButton();
        btnImportarNutricional = new vrframework.bean.button.VRButton();
        rdbToledo = new javax.swing.JRadioButton();
        rdbToledoProduto = new javax.swing.JRadioButton();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        rdbCodigoInterno = new vrframework.bean.radioButton.VRRadioButton();
        rdbCodigoBarras = new vrframework.bean.radioButton.VRRadioButton();
        chkIgnorarUltimoDigito = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        btnAbrirNutri = new vrframework.bean.button.VRButton();
        txtNutricional = new vrframework.bean.textField.VRTextField();

        setBorder(null);

        rdbGroup1.add(rdbCadTxt);
        rdbCadTxt.setText("CADTXT");

        rdbGroup1.add(rdbTxtItens);
        rdbTxtItens.setText("TXTITENS");

        rdbGroup1.add(rdbItensMgv);
        rdbItensMgv.setText("ITENSMGV");

        rdbGroup1.add(rdbPlanilha);
        rdbPlanilha.setSelected(true);
        rdbPlanilha.setText("Planilha");

        btnImportarBalanca.setText("Importar");
        btnImportarBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarBalancaActionPerformed(evt);
            }
        });

        vRPanel1.setBorder(null);
        vRPanel1.setPreferredSize(new java.awt.Dimension(285, 21));

        btnAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/bean/fileChooser/abrir.png"))); // NOI18N
        btnAbrir.setToolTipText("Abrir (F9)");
        btnAbrir.setFocusable(false);
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });

        txtArquivoBalanca.setCaixaAlta(false);
        txtArquivoBalanca.setMascara("");

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addComponent(txtArquivoBalanca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAbrir, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtArquivoBalanca, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAbrir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabPesaveisLayout = new javax.swing.GroupLayout(tabPesaveis);
        tabPesaveis.setLayout(tabPesaveisLayout);
        tabPesaveisLayout.setHorizontalGroup(
            tabPesaveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPesaveisLayout.createSequentialGroup()
                .addGroup(tabPesaveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabPesaveisLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rdbCadTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbTxtItens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbItensMgv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbPlanilha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 92, Short.MAX_VALUE))
                    .addGroup(tabPesaveisLayout.createSequentialGroup()
                        .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnImportarBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        tabPesaveisLayout.setVerticalGroup(
            tabPesaveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPesaveisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabPesaveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbCadTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbTxtItens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbItensMgv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbPlanilha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabPesaveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnImportarBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(113, Short.MAX_VALUE))
        );

        tabsOpcoes.addTab("Pesáveis", tabPesaveis);

        rdbNutriconal.add(rdbFilizolaRdc360);
        rdbFilizolaRdc360.setSelected(true);
        rdbFilizolaRdc360.setText("Filizola (RDC-360)");

        btnImportarNutricional.setText("Importar");
        btnImportarNutricional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarNutricionalActionPerformed(evt);
            }
        });

        rdbNutriconal.add(rdbToledo);
        rdbToledo.setText("Toledo (ITENSMGV)");
        rdbToledo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbToledoActionPerformed(evt);
            }
        });

        rdbNutriconal.add(rdbToledoProduto);
        rdbToledoProduto.setText("Toledo x Produto (INFNUTRI)");

        vRLabel1.setText("Importar Nutricional por:");

        rdbCodigoInterno.setText("Código Interno");
        rdbCodigoInterno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCodigoInternoActionPerformed(evt);
            }
        });

        rdbCodigoBarras.setText("Codigo de Barras");
        rdbCodigoBarras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCodigoBarrasActionPerformed(evt);
            }
        });

        chkIgnorarUltimoDigito.setText("Ignorar Último Dígito");

        vRPanel2.setBorder(null);
        vRPanel2.setPreferredSize(new java.awt.Dimension(285, 21));

        btnAbrirNutri.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/bean/fileChooser/abrir.png"))); // NOI18N
        btnAbrirNutri.setToolTipText("Abrir (F9)");
        btnAbrirNutri.setFocusable(false);
        btnAbrirNutri.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirNutriActionPerformed(evt);
            }
        });

        txtNutricional.setCaixaAlta(false);
        txtNutricional.setMascara("");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel2Layout.createSequentialGroup()
                .addComponent(txtNutricional, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAbrirNutri, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtNutricional, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAbrirNutri, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabNutricionalLayout = new javax.swing.GroupLayout(tabNutricional);
        tabNutricional.setLayout(tabNutricionalLayout);
        tabNutricionalLayout.setHorizontalGroup(
            tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabNutricionalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabNutricionalLayout.createSequentialGroup()
                        .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnImportarNutricional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabNutricionalLayout.createSequentialGroup()
                        .addGroup(tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdbToledoProduto)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabNutricionalLayout.createSequentialGroup()
                                .addComponent(rdbCodigoInterno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdbCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkIgnorarUltimoDigito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(rdbFilizolaRdc360)
                            .addComponent(rdbToledo))
                        .addGap(0, 59, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabNutricionalLayout.setVerticalGroup(
            tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabNutricionalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbCodigoInterno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIgnorarUltimoDigito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbFilizolaRdc360)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbToledo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbToledoProduto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnImportarNutricional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        /*

        tabsOpcoes.addTab("Nutricionais", tabNutricional);
        */

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabsOpcoes)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabsOpcoes)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnImportarBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarBalancaActionPerformed
        VRInternalFrame parent = null;
        if (getParent() instanceof VRInternalFrame) {
            parent = (VRInternalFrame) getParent();
        }
        try {
            if (parent != null) {
                parent.setWaitCursor();
            }
            importarProdutosBalanca();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, parent != null ? parent.getTitle() : "");

        } finally {
            if (parent != null) {
                parent.setDefaultCursor();
            }
        }
    }//GEN-LAST:event_btnImportarBalancaActionPerformed

    private void btnImportarNutricionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarNutricionalActionPerformed
        Thread thread;
        thread = new Thread() {

            int opcao;

            @Override
            public void run() {

                VRInternalFrame parent = null;
                if (getParent() instanceof VRInternalFrame) {
                    parent = (VRInternalFrame) getParent();
                }

                try {

                    ProgressBar.show();
                    ProgressBar.setCancel(false);

                    if (!txtNutricional.getText().isEmpty()) {
                        servico.setSistema(getSistema());
                        servico.setLoja(getLoja());
                        FilizolaSalvarArquivos operacoesSalvar = new FilizolaSalvarArquivos();

                        if (rdbFilizolaRdc360.isSelected()) {
                            //operacoesSalvar.salvarArquivoRdc360(txtNutricional.getArquivo(), sistema, loja);
                            operacoesSalvar.salvarArquivo(txtNutricional.getText(), sistema, loja);
                        }
                        if (rdbToledo.isSelected()) {
                            servico.direcionaImportacao(txtNutricional.getText(), condicaoToledo.INTENSMGV);
                            //NutricionalToledoDAO.importarNutricionalToledoProduto(txtNutricional.getArquivo());
                        }
                        if (rdbToledoProduto.isSelected()) {
                            servico.setIgnorarUltimoDigito(chkIgnorarUltimoDigito.isSelected());
                            servico.setOpcaoCodigo(rdbCodigoInterno.isSelected() ? 2 : 1);
                            servico.direcionaImportacao(txtNutricional.getText(), condicaoToledo.INFNUTRI);
                            //NutricionalToledoDAO.importarNutricionalToledo(txtNutricional.getArquivo(), rdbCodigoInterno.isSelected() ? 1 : 2, chkIgnorarUltimoDigito.isSelected());
                        }
                    }

                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação de balança realizada com sucesso!", parent != null ? parent.getTitle() : null);

                } catch (Exception ex) {

                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, parent != null ? parent.getTitle() : null);
                }
            }

        };

        thread.start();
    }//GEN-LAST:event_btnImportarNutricionalActionPerformed

    private void rdbToledoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbToledoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbToledoActionPerformed

    private void rdbCodigoInternoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCodigoInternoActionPerformed
        // TODO add your handling code here:

        if (rdbCodigoInterno.isSelected()) {
            rdbCodigoInterno.setSelected(true);
            rdbCodigoBarras.setSelected(false);
        }
    }//GEN-LAST:event_rdbCodigoInternoActionPerformed

    private void rdbCodigoBarrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCodigoBarrasActionPerformed
        // TODO add your handling code here:
        if (rdbCodigoBarras.isSelected()) {
            rdbCodigoInterno.setSelected(false);
            rdbCodigoBarras.setSelected(true);
        }
    }//GEN-LAST:event_rdbCodigoBarrasActionPerformed

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        try {
            JFileChooser jFileChooserBalanca = new JFileChooser();
            jFileChooserBalanca.setDialogTitle("Carregar arquivo de Balança");
            jFileChooserBalanca.setPreferredSize(new Dimension(528, 326));
            jFileChooserBalanca.setMultiSelectionEnabled(true);

            int resultado = jFileChooserBalanca.showOpenDialog(this);
            if (resultado == JFileChooser.CANCEL_OPTION) {
                jFileChooserBalanca.setVisible(false);
                txtArquivoBalanca = null;
            } else {
                txtArquivoBalanca.setText(jFileChooserBalanca.getSelectedFile().getPath());
                jFileChooserBalanca.setVisible(false);
            }

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Atenção");
        }
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnAbrirNutriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirNutriActionPerformed
        try {
            JFileChooser jFileChooserNutri = new JFileChooser();
            jFileChooserNutri.setDialogTitle("Carregar arquivo Nutricional");
            jFileChooserNutri.setPreferredSize(new Dimension(528, 326));
            jFileChooserNutri.setMultiSelectionEnabled(true);
            int resultado = jFileChooserNutri.showOpenDialog(this);
            if (resultado == JFileChooser.CANCEL_OPTION) {
                jFileChooserNutri.setVisible(false);
                txtNutricional = null;
            } else {
                txtNutricional.setText(jFileChooserNutri.getSelectedFile().getPath());
                jFileChooserNutri.setVisible(false);
            }

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Atenção");
        }
    }//GEN-LAST:event_btnAbrirNutriActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnAbrir;
    private vrframework.bean.button.VRButton btnAbrirNutri;
    private vrframework.bean.button.VRButton btnImportarBalanca;
    private vrframework.bean.button.VRButton btnImportarNutricional;
    private vrframework.bean.checkBox.VRCheckBox chkIgnorarUltimoDigito;
    private vrframework.bean.radioButton.VRRadioButton rdbCadTxt;
    private vrframework.bean.radioButton.VRRadioButton rdbCodigoBarras;
    private vrframework.bean.radioButton.VRRadioButton rdbCodigoInterno;
    private javax.swing.JRadioButton rdbFilizolaRdc360;
    private javax.swing.ButtonGroup rdbGroup1;
    private vrframework.bean.radioButton.VRRadioButton rdbItensMgv;
    private javax.swing.ButtonGroup rdbNutriconal;
    private vrframework.bean.radioButton.VRRadioButton rdbPlanilha;
    private javax.swing.JRadioButton rdbToledo;
    private javax.swing.JRadioButton rdbToledoProduto;
    private vrframework.bean.radioButton.VRRadioButton rdbTxtItens;
    private javax.swing.JPanel tabNutricional;
    private javax.swing.JPanel tabPesaveis;
    private javax.swing.JTabbedPane tabsOpcoes;
    private vrframework.bean.textField.VRTextField txtArquivoBalanca;
    private vrframework.bean.textField.VRTextField txtNutricional;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    // End of variables declaration//GEN-END:variables

    private void importarProdutosBalanca() {

        Thread thread;
        thread = new Thread() {

            int opcao;

            @Override
            public void run() {

                VRInternalFrame parent = null;
                if (getParent() instanceof VRInternalFrame) {
                    parent = (VRInternalFrame) getParent();
                }

                try {

                    ProgressBar.show();
                    ProgressBar.setCancel(false);

                    if (!txtArquivoBalanca.getText().isEmpty()) {

                        if (rdbCadTxt.isSelected()) {
                            opcao = 1;
                        } else if (rdbTxtItens.isSelected()) {
                            opcao = 2;
                        } else if (rdbItensMgv.isSelected()) {
                            opcao = 3;
                        } else if (rdbPlanilha.isSelected()) {
                            opcao = 4;
                        }

                        ProdutoBalancaDAO.importarProdutoBalanca(txtArquivoBalanca.getText(), opcao);
                    }

                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação de balança realizada com sucesso!", parent != null ? parent.getTitle() : null);

                } catch (Exception ex) {

                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, parent != null ? parent.getTitle() : null);
                }
            }

        };

        thread.start();
    }
}
