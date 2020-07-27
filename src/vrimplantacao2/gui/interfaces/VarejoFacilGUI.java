package vrimplantacao2.gui.interfaces;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.planilhas.VarejoFacilDAO;
import vrimplantacao2.parametro.Parametros;

public class VarejoFacilGUI extends VRInternalFrame {

    private static final String SISTEMA = "VarejoFacil";
    private static final String SERVIDOR_SQL = "Planilha";
    private static VarejoFacilGUI instance;

    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        txtLoja.setText(params.getWithNull("1", SISTEMA, "COMPLEMENTO_SISTEMA"));
        txtPlanilhaCreditoRotativo.setArquivo(params.get(SISTEMA, "PLAN_FAMILIA_PRODUTO"));
        chkInativacao.setSelected(params.getBool(SISTEMA, "INATIVACAO"));
        vLojaCliente = params.get(SISTEMA, "LOJA_CLIENTE");
        vLojaVR = params.getInt(SISTEMA, "LOJA_VR");
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        params.put(txtLoja.getText(), SISTEMA, "COMPLEMENTO_SISTEMA");
        params.put(txtPlanilhaCreditoRotativo.getArquivo(), SISTEMA, "PLAN_FAMILIA_PRODUTO");
        params.put(chkInativacao.isSelected(), SISTEMA, "INATIVACAO");
        ItemComboVO vr = (ItemComboVO) cmbLojaVR.getSelectedItem();
        if (vr != null) {
            params.put(vr.id, SISTEMA, "LOJA_VR");
            vLojaVR = vr.id;
        }
        params.salvar();
    }

    private VarejoFacilDAO dao = new VarejoFacilDAO();

    private VarejoFacilGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;
                
        carregarParametros();
        
        dao.setLojaOrigem(txtLoja.getText());
        
        pnlBalanca.setSistema(dao.getSistema());
        pnlBalanca.setLoja(dao.getLojaOrigem());

        centralizarForm();
        this.setMaximum(false);

        carregarLojaVR();
    }

    public void validarDadosAcesso() throws Exception {
        gravarParametros();
    }

    public void carregarLojaVR() throws Exception {
        cmbLojaVR.setModel(new DefaultComboBoxModel());
        int cont = 0;
        int index = 0;
        for (LojaVO oLoja : new LojaDAO().carregar()) {
            cmbLojaVR.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
            if (oLoja.id == vLojaVR) {
                index = cont;
            }
            cont++;
        }
        cmbLojaVR.setSelectedIndex(index);
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new VarejoFacilGUI(i_mdiFrame);
            }

            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    public void importarTabelas() throws Exception {
        Thread thread = new Thread() {
            int idLojaVR;
            StringBuilder erros = new StringBuilder();

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = ((ItemComboVO) cmbLojaVR.getSelectedItem()).id;

                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(txtLoja.getText());
                    importador.setLojaVR(idLojaVR);
                    
                    if (tabsImportacao.getSelectedIndex() == 1) {
                        if (chkCreditoRotativo.isSelected()) {
                            if ((txtPlanilhaCreditoRotativo.getArquivo()).equals("")) {
                                erros.append("Crédito Rotativo - Informe o arquivo do Crédito Rotativo").append("\n");
                            }
                        }
                        
                        if (chkFornTipoEmpresa.isSelected()) {
                            if ((txtPlanilhaFornecedor.getArquivo()).equals("")) {
                                erros.append("Fornecedor - Informe o arquivo do Cadastro de Fornecedor").append("\n");
                            }
                        }
                        if (chkFornTipoFornecedor.isSelected()) {
                            if ((txtPlanilhaFornecedor.getArquivo()).equals("")) {
                                erros.append("Fornecedor - Informe o arquivo do Cadastro de Fornecedor").append("\n");
                            }
                        }
                        
                        if (erros.toString().equals("")) {
                            if (chkCreditoRotativo.isSelected()) {
                                dao.setPlanilhaCreditoRotativo(txtPlanilhaCreditoRotativo.getArquivo());
                                importador.importarCreditoRotativo();
                            }
                            
                            {
                                List<OpcaoFornecedor> opcoes = new ArrayList<>();
                                if (chkFornTipoEmpresa.isSelected()) {
                                    opcoes.add(OpcaoFornecedor.TIPO_EMPRESA);
                                }
                                if (chkFornTipoFornecedor.isSelected()) {
                                    opcoes.add(OpcaoFornecedor.TIPO_FORNECEDOR);
                                }
                                if (!opcoes.isEmpty()) {
                                    dao.setPlanilhaFornecedor(txtPlanilhaFornecedor.getArquivo());
                                    importador.atualizarFornecedor(opcoes.toArray(new OpcaoFornecedor[]{}));
                                }
                            }
                
                        } else {
                            Util.exibirMensagem("Atenção", erros.toString());
                        }                                                
                    } else if (tabsImportacao.getSelectedIndex() == 2) {
                        if (chkUnifFornecedores.isSelected()) {
                            if ((txtPlanilhaFornecedor.getArquivo()).equals("")) {
                                erros.append("Fornecedor - Informe o arquivo do Cadastro de Fornecedor").append("\n");
                            }
                        }
                        
                        if (erros.toString().equals("")) {
                            dao.setPlanilhaFornecedor(txtPlanilhaFornecedor.getArquivo());
                            importador.unificarFornecedor();
                        } else {
                            Util.exibirMensagem("Atenção", erros.toString());
                        }                                                
                    }
                    
                    gravarParametros();

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação " + SISTEMA + " realizada com sucesso!", getTitle());
                } catch (Exception ex) {
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

        vRLabel25 = new vrframework.bean.label.VRLabel();
        txtLoja = new javax.swing.JTextField();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        jLabel1 = new javax.swing.JLabel();
        cmbLojaVR = new vrframework.bean.comboBox.VRComboBox();
        tabsImportacao = new vrframework.bean.tabbedPane.VRTabbedPane();
        scroll = new javax.swing.JScrollPane();
        tabPlanilhasProduto = new vrframework.bean.panel.VRPanel();
        vRLabel24 = new vrframework.bean.label.VRLabel();
        txtPlanilhaCreditoRotativo = new vrframework.bean.fileChooser.VRFileChooser();
        chkInativacao = new vrframework.bean.checkBox.VRCheckBox();
        btnMigrar1 = new vrframework.bean.button.VRButton();
        pnlBalanca = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        txtPlanilhaFornecedor = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel26 = new vrframework.bean.label.VRLabel();
        tabFornecedor = new vrframework.bean.panel.VRPanel();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        chkFornTipoEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkFornTipoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        tabUnificacao = new vrframework.bean.panel.VRPanel();
        chkUnifFornecedores = new vrframework.bean.checkBox.VRCheckBox();

        setTitle("Importação Varejo Facil");
        setToolTipText("");
        setMinimumSize(new java.awt.Dimension(683, 494));

        vRLabel25.setText("Loja de Origem");

        txtLoja.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLoja.setText("1");
        txtLoja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLojaKeyReleased(evt);
            }
        });

        btnMigrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnMigrar.setText("Migrar");
        btnMigrar.setFocusable(false);
        btnMigrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnMigrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMigrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMigrarActionPerformed(evt);
            }
        });

        jLabel1.setText("Loja:");

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbLojaVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(cmbLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vRLabel24.setText("Informe a localização da planilha de Crédito Rotativo");

        chkInativacao.setText("Inativação");

        btnMigrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnMigrar1.setText("Gravar parâmetros");
        btnMigrar1.setFocusable(false);
        btnMigrar1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnMigrar1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMigrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMigrar1ActionPerformed(evt);
            }
        });

        pnlBalanca.setSistema("");

        vRLabel26.setText("Informe a localização da planilha de Fornecedor");

        javax.swing.GroupLayout tabPlanilhasProdutoLayout = new javax.swing.GroupLayout(tabPlanilhasProduto);
        tabPlanilhasProduto.setLayout(tabPlanilhasProdutoLayout);
        tabPlanilhasProdutoLayout.setHorizontalGroup(
            tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPlanilhasProdutoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabPlanilhasProdutoLayout.createSequentialGroup()
                        .addComponent(chkInativacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMigrar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlBalanca, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                    .addComponent(txtPlanilhaFornecedor, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                    .addComponent(txtPlanilhaCreditoRotativo, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                    .addGroup(tabPlanilhasProdutoLayout.createSequentialGroup()
                        .addGroup(tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabPlanilhasProdutoLayout.setVerticalGroup(
            tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPlanilhasProdutoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanilhaFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanilhaCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(293, 293, 293)
                .addComponent(pnlBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(tabPlanilhasProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMigrar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkInativacao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        scroll.setViewportView(tabPlanilhasProduto);

        tabsImportacao.addTab("Planilhas", scroll);

        vRPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Clientes"));

        chkCreditoRotativo.setText("Crédito Rotativo");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        vRPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Fornecedores"));

        chkFornTipoEmpresa.setText("Tipo Empresa");

        chkFornTipoFornecedor.setText("Tipo Fornecedor");

        javax.swing.GroupLayout vRPanel4Layout = new javax.swing.GroupLayout(vRPanel4);
        vRPanel4.setLayout(vRPanel4Layout);
        vRPanel4Layout.setHorizontalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkFornTipoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFornTipoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(582, Short.MAX_VALUE))
        );
        vRPanel4Layout.setVerticalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFornTipoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFornTipoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabFornecedorLayout = new javax.swing.GroupLayout(tabFornecedor);
        tabFornecedor.setLayout(tabFornecedorLayout);
        tabFornecedorLayout.setHorizontalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabFornecedorLayout.setVerticalGroup(
            tabFornecedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFornecedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(262, Short.MAX_VALUE))
        );

        tabsImportacao.addTab("Clientes/Fornecedores", tabFornecedor);

        chkUnifFornecedores.setText("Fornecedores (Somente com CPF/CNPJ)");

        javax.swing.GroupLayout tabUnificacaoLayout = new javax.swing.GroupLayout(tabUnificacao);
        tabUnificacao.setLayout(tabUnificacaoLayout);
        tabUnificacaoLayout.setHorizontalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(589, Short.MAX_VALUE))
        );
        tabUnificacaoLayout.setVerticalGroup(
            tabUnificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUnifFornecedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(343, Short.MAX_VALUE))
        );

        tabsImportacao.addTab("Unificação", tabUnificacao);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabsImportacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsImportacao, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMigrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrarActionPerformed
        try {
            this.setWaitCursor();
            importarTabelas();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnMigrarActionPerformed

    private void btnMigrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMigrar1ActionPerformed
        try {
            gravarParametros();
            Util.exibirMensagem("Parâmetros gravados com sucesso!", "Mensagem");
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnMigrar1ActionPerformed

    private void txtLojaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLojaKeyReleased
        dao.setLojaOrigem(txtLoja.getText());
    }//GEN-LAST:event_txtLojaKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.button.VRButton btnMigrar1;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkFornTipoEmpresa;
    private vrframework.bean.checkBox.VRCheckBox chkFornTipoFornecedor;
    private vrframework.bean.checkBox.VRCheckBox chkInativacao;
    private vrframework.bean.checkBox.VRCheckBox chkUnifFornecedores;
    private vrframework.bean.comboBox.VRComboBox cmbLojaVR;
    private javax.swing.JLabel jLabel1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel pnlBalanca;
    private javax.swing.JScrollPane scroll;
    private vrframework.bean.panel.VRPanel tabFornecedor;
    private vrframework.bean.panel.VRPanel tabPlanilhasProduto;
    private vrframework.bean.panel.VRPanel tabUnificacao;
    private vrframework.bean.tabbedPane.VRTabbedPane tabsImportacao;
    private javax.swing.JTextField txtLoja;
    private vrframework.bean.fileChooser.VRFileChooser txtPlanilhaCreditoRotativo;
    private vrframework.bean.fileChooser.VRFileChooser txtPlanilhaFornecedor;
    private vrframework.bean.label.VRLabel vRLabel24;
    private vrframework.bean.label.VRLabel vRLabel25;
    private vrframework.bean.label.VRLabel vRLabel26;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel4;
    // End of variables declaration//GEN-END:variables

}
