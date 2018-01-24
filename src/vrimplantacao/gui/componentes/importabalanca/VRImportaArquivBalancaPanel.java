/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.gui.componentes.importabalanca;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.panel.VRPanel;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.dao.cadastro.NutricionalFilizolaDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;

/**
 *
 * @author Leandro
 */
public class VRImportaArquivBalancaPanel extends VRPanel {

    private String sistema;
    private String loja;

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
        txtArquivoBalanca = new vrframework.bean.fileChooser.VRFileChooser();
        btnImportarBalanca = new vrframework.bean.button.VRButton();
        tabNutricional = new javax.swing.JPanel();
        rdbFilizolaRdc360 = new javax.swing.JRadioButton();
        txtNutricional = new vrframework.bean.fileChooser.VRFileChooser();
        btnImportarNutricional = new vrframework.bean.button.VRButton();

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

        javax.swing.GroupLayout tabPesaveisLayout = new javax.swing.GroupLayout(tabPesaveis);
        tabPesaveis.setLayout(tabPesaveisLayout);
        tabPesaveisLayout.setHorizontalGroup(
            tabPesaveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPesaveisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabPesaveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabPesaveisLayout.createSequentialGroup()
                        .addComponent(txtArquivoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnImportarBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabPesaveisLayout.createSequentialGroup()
                        .addComponent(rdbCadTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbTxtItens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbItensMgv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbPlanilha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addGroup(tabPesaveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImportarBalanca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArquivoBalanca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabsOpcoes.addTab("Pesáveis", tabPesaveis);

        rdbFilizolaRdc360.setSelected(true);
        rdbFilizolaRdc360.setText("Filizola (RDC-360)");

        btnImportarNutricional.setText("Importar");
        btnImportarNutricional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarNutricionalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabNutricionalLayout = new javax.swing.GroupLayout(tabNutricional);
        tabNutricional.setLayout(tabNutricionalLayout);
        tabNutricionalLayout.setHorizontalGroup(
            tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabNutricionalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabNutricionalLayout.createSequentialGroup()
                        .addComponent(txtNutricional, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnImportarNutricional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabNutricionalLayout.createSequentialGroup()
                        .addComponent(rdbFilizolaRdc360)
                        .addGap(0, 169, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabNutricionalLayout.setVerticalGroup(
            tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabNutricionalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdbFilizolaRdc360)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabNutricionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImportarNutricional, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNutricional, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        /*

        tabsOpcoes.addTab("Nutricionais", tabNutricional);
        */

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsOpcoes)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsOpcoes)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnImportarBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarBalancaActionPerformed
        VRInternalFrame parent = null;
        if (getParent() instanceof VRInternalFrame) {
            parent = (VRInternalFrame)getParent();
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
                    parent = (VRInternalFrame)getParent();
                }
                
                try {
                    
                    ProgressBar.show();
                    ProgressBar.setCancel(false);
                    
                    if (!txtNutricional.getArquivo().isEmpty()) {                        
                        if (rdbFilizolaRdc360.isSelected()) {
                            NutricionalFilizolaDAO.importarArquivoRdc360(sistema, loja, txtNutricional.getArquivo());
                        }
                    }                   
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação de balança realizada com sucesso!", parent != null ? parent.getTitle() : null);
                    
                } catch(Exception ex) {
                    
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, parent != null ? parent.getTitle() : null);
                }
            }
            
            
        };
                
        thread.start();
    }//GEN-LAST:event_btnImportarNutricionalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnImportarBalanca;
    private vrframework.bean.button.VRButton btnImportarNutricional;
    private vrframework.bean.radioButton.VRRadioButton rdbCadTxt;
    private javax.swing.JRadioButton rdbFilizolaRdc360;
    private javax.swing.ButtonGroup rdbGroup1;
    private vrframework.bean.radioButton.VRRadioButton rdbItensMgv;
    private javax.swing.ButtonGroup rdbNutriconal;
    private vrframework.bean.radioButton.VRRadioButton rdbPlanilha;
    private vrframework.bean.radioButton.VRRadioButton rdbTxtItens;
    private javax.swing.JPanel tabNutricional;
    private javax.swing.JPanel tabPesaveis;
    private javax.swing.JTabbedPane tabsOpcoes;
    private vrframework.bean.fileChooser.VRFileChooser txtArquivoBalanca;
    private vrframework.bean.fileChooser.VRFileChooser txtNutricional;
    // End of variables declaration//GEN-END:variables

    private void importarProdutosBalanca() {
        
        Thread thread;
        thread = new Thread() {
            
            int opcao;
            
            @Override
            public void run() {
                
                VRInternalFrame parent = null;
                if (getParent() instanceof VRInternalFrame) {
                    parent = (VRInternalFrame)getParent();
                }
                
                try {
                    
                    ProgressBar.show();
                    ProgressBar.setCancel(false);
                    
                    if (!txtArquivoBalanca.getArquivo().isEmpty()) {
                        
                        if (rdbCadTxt.isSelected()) {
                            opcao = 1;
                        } else if (rdbTxtItens.isSelected()) {
                            opcao = 2;
                        } else if (rdbItensMgv.isSelected()) {
                            opcao = 3;
                        } else if (rdbPlanilha.isSelected()) {
                            opcao = 4;
                        }
                        
                        ProdutoBalancaDAO.importarProdutoBalanca(txtArquivoBalanca.getArquivo(), opcao);
                    }
                    
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Importação de balança realizada com sucesso!", parent != null ? parent.getTitle() : null);
                    
                } catch(Exception ex) {
                    
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, parent != null ? parent.getTitle() : null);
                }
            }
            
            
        };
                
        thread.start();
    }
}
