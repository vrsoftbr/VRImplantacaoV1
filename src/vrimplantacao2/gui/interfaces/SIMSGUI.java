package vrimplantacao2.gui.interfaces;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao2.dao.interfaces.SIMSDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.Importador;

public class SIMSGUI extends VRInternalFrame {

    private final SIMSDAO dao = new SIMSDAO();
    private static final String SISTEMA = "SIMS";
    private static SIMSGUI instance;

    private String vLojaCliente = "-1";
    private int vLojaVR = -1;

    public SIMSGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        txtCBMER.setArquivo("C:\\vr\\UNIAO\\CBMER.gsa");
        txtMER.setArquivo("C:\\vr\\UNIAO\\MER.gsa");
        txtNIVE1.setArquivo("C:\\vr\\UNIAO\\NIVE1.gsa");
        txtNIVE2.setArquivo("C:\\vr\\UNIAO\\NIVE2.gsa");
        txtNIVE3.setArquivo("C:\\vr\\UNIAO\\NIVE3.gsa");
        txtNIVE4.setArquivo("C:\\vr\\UNIAO\\NIVE4.gsa");
        txtCLIENTE.setArquivo("C:\\vr\\UNIAO\\CLIENTE.gsa");
        txtFORNECEDOR.setArquivo("C:\\vr\\UNIAO\\FORNECEDOR.gsa");

        carregarLojaDestino();

        centralizarForm();
        this.setMaximum(false);

    }

    public void validarDadosAcessoSqlServer() throws Exception {
        if (txtMER.getArquivo().isEmpty()) {
            throw new VRException("Favor informar a localização do arquivo MER!");
        }
        if (txtCBMER.getArquivo().isEmpty()) {
            throw new VRException("Favor informar a localização do arquivo CBMER!");
        }

        carregarLojaDestino();
    }

    private void carregarLojaDestino() throws Exception {
        cmbLojaDestino.removeAllItems();
        cmbLojaDestino.setModel(new DefaultComboBoxModel());
        for (LojaVO oLoja : new LojaDAO().carregar()) {
            cmbLojaDestino.addItem(new ItemComboVO(oLoja.id, oLoja.descricao));
        }
        cmbLojaDestino.setEnabled(true);
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new SIMSGUI(i_mdiFrame);
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
            String idLojaCliente = "1";

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    idLojaVR = ((ItemComboVO) cmbLojaDestino.getSelectedItem()).id;
                    dao.setASSOCIACAOFile(txtASSOCIACAO.getArquivo());
                    dao.setCBMERFile(txtCBMER.getArquivo());
                    dao.setMERFile(txtMER.getArquivo());
                    dao.setNIVE1File(txtNIVE1.getArquivo());
                    dao.setNIVE2File(txtNIVE2.getArquivo());
                    dao.setNIVE3File(txtNIVE3.getArquivo());
                    dao.setNIVE4File(txtNIVE4.getArquivo());
                    dao.setMERELEFile(txtMERELO.getArquivo());
                    dao.setCLIENTEFile(txtCLIENTE.getArquivo());
                    dao.setFORNECEDORFile(txtFORNECEDOR.getArquivo());
                    dao.setCRVOUCH1File(txtRotativo.getArquivo());

                    Importador importador = new Importador(dao);
                    importador.setLojaOrigem(idLojaCliente);
                    importador.setLojaVR(idLojaVR);

                    if (chkFamiliaProduto.isSelected()) {
                        importador.importarFamiliaProduto();
                    }
                    
                    if (cbxMercadologico.isSelected()) {
                        importador.importarMercadologico();
                    }

                    if (cbxProduto.isSelected()) {
                        importador.importarProduto(true);
                    }

                    if (cbxEan.isSelected()) {
                        importador.importarEAN();
                    }

                    if (cbxEanEmBranco.isSelected()) {
                        importador.importarEANemBranco();
                    }

                    {
                        List<OpcaoProduto> opt = new ArrayList<>();
                        if (cbxPreco.isSelected()) {
                            opt.add(OpcaoProduto.PRECO);
                        }
                        if (cbxCusto.isSelected()) {
                            opt.add(OpcaoProduto.CUSTO);
                        }
                        if (cbxEstoque.isSelected()) {
                            opt.add(OpcaoProduto.ESTOQUE);
                        }
                        if (chkPiscofins.isSelected()) {
                            opt.add(OpcaoProduto.PIS_COFINS);
                        }
                        if (chkIcms.isSelected()) {
                            opt.add(OpcaoProduto.ICMS);
                        }
                        if (chkCest.isSelected()) {
                            opt.add(OpcaoProduto.CEST);
                        }
                        if (chkProdFamilia.isSelected()) {
                            opt.add(OpcaoProduto.FAMILIA);
                        }
                        if (!opt.isEmpty()) {
                            importador.atualizarProdutos(opt);
                        }
                    }

                    if (cbxFornecedor.isSelected()) {
                        importador.importarFornecedor();
                    }

                    if (cbxClientePreferencial.isSelected()) {
                        importador.importarClientePreferencial(OpcaoCliente.DADOS);
                    }
                    
                    if (chkCreditoRotativo.isSelected()) {
                        importador.importarCreditoRotativo();
                    }

                    ProgressBar.dispose();
                    Util.exibirMensagem("Importação SimSoft realizada com sucesso!", getTitle());
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

        vRConsultaMercadologico1 = new vrframework.bean.consultaMercadologico.VRConsultaMercadologico();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        cmbLojaDestino = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();
        tab = new vrframework.bean.tabbedPane.VRTabbedPane();
        pnlPagina1 = new vrframework.bean.panel.VRPanel();
        vRPanel0 = new vrframework.bean.panel.VRPanel();
        cbxMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        cbxProduto = new vrframework.bean.checkBox.VRCheckBox();
        cbxEan = new vrframework.bean.checkBox.VRCheckBox();
        cbxEanEmBranco = new vrframework.bean.checkBox.VRCheckBox();
        cbxPreco = new vrframework.bean.checkBox.VRCheckBox();
        cbxCusto = new vrframework.bean.checkBox.VRCheckBox();
        cbxEstoque = new vrframework.bean.checkBox.VRCheckBox();
        chkPiscofins = new vrframework.bean.checkBox.VRCheckBox();
        chkIcms = new vrframework.bean.checkBox.VRCheckBox();
        chkCest = new vrframework.bean.checkBox.VRCheckBox();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkProdFamilia = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        txtMER = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        txtCBMER = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel9 = new vrframework.bean.label.VRLabel();
        vRLabel10 = new vrframework.bean.label.VRLabel();
        vRLabel11 = new vrframework.bean.label.VRLabel();
        vRLabel12 = new vrframework.bean.label.VRLabel();
        txtNIVE1 = new vrframework.bean.fileChooser.VRFileChooser();
        txtNIVE2 = new vrframework.bean.fileChooser.VRFileChooser();
        txtNIVE3 = new vrframework.bean.fileChooser.VRFileChooser();
        txtNIVE4 = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtASSOCIACAO = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        txtMERELO = new vrframework.bean.fileChooser.VRFileChooser();
        pnlPagina2 = new vrframework.bean.panel.VRPanel();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        txtCLIENTE = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        vRLabel13 = new vrframework.bean.label.VRLabel();
        txtFORNECEDOR = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel14 = new vrframework.bean.label.VRLabel();
        txtRotativo = new vrframework.bean.fileChooser.VRFileChooser();
        vRPanel4 = new vrframework.bean.panel.VRPanel();
        cbxClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel5 = new vrframework.bean.panel.VRPanel();
        cbxFornecedor = new vrframework.bean.checkBox.VRCheckBox();

        setTitle("SIMS");
        setToolTipText("");

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

        cmbLojaDestino.setEnabled(false);
        cmbLojaDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaDestinoActionPerformed(evt);
            }
        });

        jLabel1.setText("Loja:");

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbLojaDestino, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))
                    .addGroup(vRPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vRPanel0.setBorder(javax.swing.BorderFactory.createTitledBorder("Produtos"));

        cbxMercadologico.setText("Mercadológico");
        cbxMercadologico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxMercadologicoActionPerformed(evt);
            }
        });

        cbxProduto.setText("Produto");

        cbxEan.setText("EAN");

        cbxEanEmBranco.setText("EAN em branco");

        cbxPreco.setText("Preço");
        cbxPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxPrecoActionPerformed(evt);
            }
        });

        cbxCusto.setText("Custo");
        cbxCusto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxCustoActionPerformed(evt);
            }
        });

        cbxEstoque.setText("Estoque");
        cbxEstoque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxEstoqueActionPerformed(evt);
            }
        });

        chkPiscofins.setText("Piscofins");

        chkIcms.setText("Icms");

        chkCest.setText("Cest");

        chkFamiliaProduto.setText("Familia Produto");

        chkProdFamilia.setText("Prod. Familia");

        javax.swing.GroupLayout vRPanel0Layout = new javax.swing.GroupLayout(vRPanel0);
        vRPanel0.setLayout(vRPanel0Layout);
        vRPanel0Layout.setHorizontalGroup(
            vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel0Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxEan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxEanEmBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPiscofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        vRPanel0Layout.setVerticalGroup(
            vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel0Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxEanEmBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel0Layout.createSequentialGroup()
                        .addComponent(cbxMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(cbxProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxEan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel0Layout.createSequentialGroup()
                        .addGroup(vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkCest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbxPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkPiscofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbxCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbxEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkProdFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - Caché"));
        vRPanel1.setPreferredSize(new java.awt.Dimension(350, 350));

        vRLabel6.setText("Arquivo MER");

        vRLabel8.setText("Arquivo CBMER");

        vRLabel9.setText("Arquivo NIVE1");

        vRLabel10.setText("Arquivo NIVE2");

        vRLabel11.setText("Arquivo NIVE3");

        vRLabel12.setText("Arquivo NIVE4");

        vRLabel1.setText("Arquivo ASSOCIACAO");

        vRLabel2.setText("Arquivo MERELO");

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtASSOCIACAO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMER, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                    .addComponent(txtCBMER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNIVE1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNIVE2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNIVE3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNIVE4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMERELO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtASSOCIACAO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCBMER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNIVE1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNIVE2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNIVE3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNIVE4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMERELO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlPagina1Layout = new javax.swing.GroupLayout(pnlPagina1);
        pnlPagina1.setLayout(pnlPagina1Layout);
        pnlPagina1Layout.setHorizontalGroup(
            pnlPagina1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPagina1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPagina1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 483, Short.MAX_VALUE)
                    .addComponent(vRPanel0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPagina1Layout.setVerticalGroup(
            pnlPagina1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPagina1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tab.addTab("Produto", pnlPagina1);

        vRPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem - Caché"));
        vRPanel2.setPreferredSize(new java.awt.Dimension(350, 350));

        vRLabel7.setText("Arquivo CLIENTE");

        vRLabel13.setText("Arquivo FORNECEDOR");

        vRLabel14.setText("Arquivo ROTATIVO");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCLIENTE, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(vRLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFORNECEDOR, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(vRLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCLIENTE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFORNECEDOR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        vRPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente Preferêncial"));

        cbxClientePreferencial.setText("Clientes");

        chkCreditoRotativo.setText("Crédito Rotativo");

        javax.swing.GroupLayout vRPanel4Layout = new javax.swing.GroupLayout(vRPanel4);
        vRPanel4.setLayout(vRPanel4Layout);
        vRPanel4Layout.setHorizontalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel4Layout.setVerticalGroup(
            vRPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel4Layout.createSequentialGroup()
                .addComponent(cbxClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        vRPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Fornecedor"));

        cbxFornecedor.setText("Fornecedor");

        javax.swing.GroupLayout vRPanel5Layout = new javax.swing.GroupLayout(vRPanel5);
        vRPanel5.setLayout(vRPanel5Layout);
        vRPanel5Layout.setHorizontalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbxFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel5Layout.setVerticalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbxFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout pnlPagina2Layout = new javax.swing.GroupLayout(pnlPagina2);
        pnlPagina2.setLayout(pnlPagina2Layout);
        pnlPagina2Layout.setHorizontalGroup(
            pnlPagina2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPagina2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPagina2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 483, Short.MAX_VALUE)
                    .addComponent(vRPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPagina2Layout.setVerticalGroup(
            pnlPagina2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPagina2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(175, Short.MAX_VALUE))
        );

        tab.addTab("Fornecedor/Cliente", pnlPagina2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tab, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void cmbLojaDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaDestinoActionPerformed

    }//GEN-LAST:event_cmbLojaDestinoActionPerformed

    private void cbxPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxPrecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxPrecoActionPerformed

    private void cbxCustoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxCustoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxCustoActionPerformed

    private void cbxEstoqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxEstoqueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxEstoqueActionPerformed

    private void cbxMercadologicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxMercadologicoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxMercadologicoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox cbxClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox cbxCusto;
    private vrframework.bean.checkBox.VRCheckBox cbxEan;
    private vrframework.bean.checkBox.VRCheckBox cbxEanEmBranco;
    private vrframework.bean.checkBox.VRCheckBox cbxEstoque;
    private vrframework.bean.checkBox.VRCheckBox cbxFornecedor;
    private vrframework.bean.checkBox.VRCheckBox cbxMercadologico;
    private vrframework.bean.checkBox.VRCheckBox cbxPreco;
    private vrframework.bean.checkBox.VRCheckBox cbxProduto;
    private vrframework.bean.checkBox.VRCheckBox chkCest;
    private vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    private vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    private vrframework.bean.checkBox.VRCheckBox chkIcms;
    private vrframework.bean.checkBox.VRCheckBox chkPiscofins;
    private vrframework.bean.checkBox.VRCheckBox chkProdFamilia;
    private javax.swing.JComboBox cmbLojaDestino;
    private javax.swing.JLabel jLabel1;
    private vrframework.bean.panel.VRPanel pnlPagina1;
    private vrframework.bean.panel.VRPanel pnlPagina2;
    private vrframework.bean.tabbedPane.VRTabbedPane tab;
    private vrframework.bean.fileChooser.VRFileChooser txtASSOCIACAO;
    private vrframework.bean.fileChooser.VRFileChooser txtCBMER;
    private vrframework.bean.fileChooser.VRFileChooser txtCLIENTE;
    private vrframework.bean.fileChooser.VRFileChooser txtFORNECEDOR;
    private vrframework.bean.fileChooser.VRFileChooser txtMER;
    private vrframework.bean.fileChooser.VRFileChooser txtMERELO;
    private vrframework.bean.fileChooser.VRFileChooser txtNIVE1;
    private vrframework.bean.fileChooser.VRFileChooser txtNIVE2;
    private vrframework.bean.fileChooser.VRFileChooser txtNIVE3;
    private vrframework.bean.fileChooser.VRFileChooser txtNIVE4;
    private vrframework.bean.fileChooser.VRFileChooser txtRotativo;
    private vrframework.bean.consultaMercadologico.VRConsultaMercadologico vRConsultaMercadologico1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel10;
    private vrframework.bean.label.VRLabel vRLabel11;
    private vrframework.bean.label.VRLabel vRLabel12;
    private vrframework.bean.label.VRLabel vRLabel13;
    private vrframework.bean.label.VRLabel vRLabel14;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel6;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.label.VRLabel vRLabel8;
    private vrframework.bean.label.VRLabel vRLabel9;
    private vrframework.bean.panel.VRPanel vRPanel0;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel4;
    private vrframework.bean.panel.VRPanel vRPanel5;
    // End of variables declaration//GEN-END:variables

}
