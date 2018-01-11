package vrimplantacao.gui.interfaces;

import java.util.List;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.interfaces.PlanilhaPadraoDAO;
import vrimplantacao.vo.loja.LojaVO;

public class PlanilhaPadraoGUI extends VRInternalFrame {

    public PlanilhaPadraoGUI(VRMdiFrame i_mdiFrame) throws Exception {
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
        //Util.validarCampoTela(this.getCampoObrigatorio());
        
        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(false);

                    if (rdbPadrao.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoLoja(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    } else if (rdbCarnauba.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoLojaCarnauba(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    } else if (rdbCarnaubaPisCofins.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoPisCofins(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    } else if (rdbFreitasProdutosReceita.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoReceitaFreitas(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    } else if (rdbCarnaubaEstoque.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoEstoqueCarnauba(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    } else if (rdbPaivaProdutos.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoLojaPaiva(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    } else if (rdbPaivaPrecoProdutoos.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoLojaPrecoProdutosPaiva(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    } else if (rdbPaivaIcms.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoICMSPaiva(flcArquivoProduto.getArquivo());
                    } else if (rdbSaoFranciscoIcms.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoICMSSaoFrancisco(flcArquivoProduto.getArquivo());
                    } else if (rdbCarnaubaPrecoCusto.isSelected()) {
                        new PlanilhaPadraoDAO().migrarPrecoMargemCustoCarnauba(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    } else if (rdbProdutosICMSSysPdv.isSelected()) {
                        new PlanilhaPadraoDAO().migrarProdutoICMSSysPdv(flcArquivoProduto.getArquivo());
                    } else if (rdbOferta.isSelected()) {
                        new PlanilhaPadraoDAO().importarOfertas(cmbLojaDestino.getId(), flcArquivoProduto.getArquivo());
                    } else if (rdbProdutoFornecedor.isSelected()) {
                        new PlanilhaPadraoDAO().importarProdutoForencedor(flcArquivoProduto.getArquivo());
                    } else if (rdbEstoque.isSelected()) {
                        new PlanilhaPadraoDAO().importarEstoqueProduto(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    } else if (rdbSituacaoCadastroMiranda.isSelected()) {
                        new PlanilhaPadraoDAO().gerarScriptIdSituacaoCadastro(flcArquivoProduto.getArquivo());
                    }
                    
                    if (tabs.getSelectedIndex() == 1) {
                        if (chkChampMercadologico.isSelected()) {
                            new PlanilhaPadraoDAO().migrarMercadologicoChamp(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId()); 
                        }
                        if (chkChampProdutos.isSelected()) {
                            new PlanilhaPadraoDAO().migrarProdutoLojaChamp(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId()); 
                        }
                        if (chkChampPrecoCusto.isSelected()) {
                            new PlanilhaPadraoDAO().atualizarPrecoCusto(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                        }
                        if (chkChampEAN.isSelected()) {
                            new PlanilhaPadraoDAO().migrarEANChamp(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId()); 
                        }
                        if (chkChampClientePreferencial.isSelected()) {
                            new PlanilhaPadraoDAO().migrarClientePreferencial(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                        }
                    }


                    if (chkDataProduto.isSelected()) {
                        new PlanilhaPadraoDAO().migrarDataProdutoCarnauba(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    }
                    
                    if (chkProdutoSemCodBarra.isSelected()) {
                        new PlanilhaPadraoDAO().importarCodigoBarraEmBranco();
                    }
                    
                    if (rdbCodigoBarras.isSelected()) {
                        new PlanilhaPadraoDAO().importarCodigoBarras(flcArquivoProduto.getArquivo());
                    }
                    
                    if (rdbEstoqueCentralEconomia.isSelected()) {
                        new PlanilhaPadraoDAO().atualizarEstoqueCentralEconomia(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    }

                    if (rdbEstoqueCentralEconomiaCodBarras.isSelected()) {
                        new PlanilhaPadraoDAO().atualizarEstoqueCentralEconomiaCodBarras(flcArquivoProduto.getArquivo(), cmbLojaDestino.getId());
                    }
                    
                    ProgressBar.dispose();

                    Util.exibirMensagem("Migração planilha padrão realizada com sucesso!", getTitle());

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

        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        flcArquivoProduto = new vrframework.bean.fileChooser.VRFileChooser();
        tabs = new vrframework.bean.tabbedPane.VRTabbedPane();
        tab1 = new vrframework.bean.panel.VRPanel();
        rdbCarnaubaPisCofins = new vrframework.bean.radioButton.VRRadioButton();
        rdbCarnauba = new vrframework.bean.radioButton.VRRadioButton();
        rdbPadrao = new vrframework.bean.radioButton.VRRadioButton();
        rdbCarnaubaEstoque = new vrframework.bean.radioButton.VRRadioButton();
        rdbCarnaubaPrecoCusto = new vrframework.bean.radioButton.VRRadioButton();
        rdbProdutosICMSSysPdv = new vrframework.bean.radioButton.VRRadioButton();
        chkDataProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkProdutoSemCodBarra = new vrframework.bean.checkBox.VRCheckBox();
        rdbSaoFranciscoIcms = new vrframework.bean.radioButton.VRRadioButton();
        rdbPaivaIcms = new vrframework.bean.radioButton.VRRadioButton();
        rdbPaivaPrecoProdutoos = new vrframework.bean.radioButton.VRRadioButton();
        rdbPaivaProdutos = new vrframework.bean.radioButton.VRRadioButton();
        rdbFreitasProdutosReceita = new vrframework.bean.radioButton.VRRadioButton();
        rdbEstoqueCentralEconomia = new vrframework.bean.radioButton.VRRadioButton();
        rdbEstoqueCentralEconomia1 = new vrframework.bean.radioButton.VRRadioButton();
        rdbCodigoBarras = new vrframework.bean.radioButton.VRRadioButton();
        rdbEstoqueCentralEconomiaCodBarras = new vrframework.bean.radioButton.VRRadioButton();
        rdbOferta = new vrframework.bean.radioButton.VRRadioButton();
        rdbProdutoFornecedor = new vrframework.bean.radioButton.VRRadioButton();
        rdbEstoque = new vrframework.bean.radioButton.VRRadioButton();
        rdbSituacaoCadastroMiranda = new vrframework.bean.radioButton.VRRadioButton();
        tabChamp = new vrframework.bean.panel.VRPanel();
        chkChampProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkChampEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkChampMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkChampClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkChampPrecoCusto = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        cmbLojaDestino = new vrframework.bean.comboBox.VRComboBox();
        vRLabel8 = new vrframework.bean.label.VRLabel();
        vRToolBar1 = new vrframework.bean.toolBar.VRToolBar();
        vRToolBarPadrao3 = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRButton1 = new vrframework.bean.button.VRButton();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnMigrar = new vrframework.bean.button.VRButton();
        vRImportaArquivBalancaPanel1 = new vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel();

        setTitle("Migração Planilha Padrão");

        vRPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Origem"));
        vRPanel1.setPreferredSize(new java.awt.Dimension(350, 350));

        vRLabel1.setText("Planilha Produto");

        flcArquivoProduto.setObrigatorio(true);

        rdbCarnaubaPisCofins.setText("Produtos Piscofins - Carnaúba");
        rdbCarnaubaPisCofins.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCarnaubaPisCofinsActionPerformed(evt);
            }
        });

        rdbCarnauba.setText("Produtos - Carnaúba");
        rdbCarnauba.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCarnaubaActionPerformed(evt);
            }
        });

        rdbPadrao.setText("Padrão");
        rdbPadrao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbPadraoActionPerformed(evt);
            }
        });

        rdbCarnaubaEstoque.setText("Produtos Estoque - Carnaúba");
        rdbCarnaubaEstoque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCarnaubaEstoqueActionPerformed(evt);
            }
        });

        rdbCarnaubaPrecoCusto.setText("Produtos Custo / Preço - Carnaúba");
        rdbCarnaubaPrecoCusto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCarnaubaPrecoCustoActionPerformed(evt);
            }
        });

        rdbProdutosICMSSysPdv.setText("Produtos ICMS - SysPdv");
        rdbProdutosICMSSysPdv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbProdutosICMSSysPdvActionPerformed(evt);
            }
        });

        chkDataProduto.setText("Acertar Data Cadastro Produto");

        chkProdutoSemCodBarra.setText("Produtos sem código de barras");

        rdbSaoFranciscoIcms.setText("Produtos ICMS - São Francisco");
        rdbSaoFranciscoIcms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSaoFranciscoIcmsActionPerformed(evt);
            }
        });

        rdbPaivaIcms.setText("Produtos ICMS - Paiva");
        rdbPaivaIcms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbPaivaIcmsActionPerformed(evt);
            }
        });

        rdbPaivaPrecoProdutoos.setText("Produtos Preço - Paiva");
        rdbPaivaPrecoProdutoos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbPaivaPrecoProdutoosActionPerformed(evt);
            }
        });

        rdbPaivaProdutos.setText("Produtos - Paiva");
        rdbPaivaProdutos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbPaivaProdutosActionPerformed(evt);
            }
        });

        rdbFreitasProdutosReceita.setText("Produtos Receita - Freitas");
        rdbFreitasProdutosReceita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbFreitasProdutosReceitaActionPerformed(evt);
            }
        });

        rdbEstoqueCentralEconomia.setText("Produtos Estoque - Central Economia");
        rdbEstoqueCentralEconomia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbEstoqueCentralEconomiaActionPerformed(evt);
            }
        });

        rdbEstoqueCentralEconomia1.setText("Produtos Estoque - Central Economia");
        rdbEstoqueCentralEconomia1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbEstoqueCentralEconomia1ActionPerformed(evt);
            }
        });

        rdbCodigoBarras.setText("Produtos Codigo Barras");
        rdbCodigoBarras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbCodigoBarrasActionPerformed(evt);
            }
        });

        rdbEstoqueCentralEconomiaCodBarras.setText("Produtos Estoque/Cod.Barras - Central Economia");
        rdbEstoqueCentralEconomiaCodBarras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbEstoqueCentralEconomiaCodBarrasActionPerformed(evt);
            }
        });

        rdbOferta.setText("Oferta");

        rdbProdutoFornecedor.setText("Produto Forncedor");

        rdbEstoque.setText("Estoque");

        rdbSituacaoCadastroMiranda.setText("Situacao Cadastro - Miranda");

        javax.swing.GroupLayout tab1Layout = new javax.swing.GroupLayout(tab1);
        tab1.setLayout(tab1Layout);
        tab1Layout.setHorizontalGroup(
            tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab1Layout.createSequentialGroup()
                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdbPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tab1Layout.createSequentialGroup()
                                .addComponent(rdbCarnauba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(115, 115, 115)
                                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rdbPaivaProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rdbFreitasProdutosReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rdbEstoqueCentralEconomia1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(tab1Layout.createSequentialGroup()
                        .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tab1Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rdbCarnaubaEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rdbCarnaubaPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rdbCarnaubaPrecoCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rdbProdutosICMSSysPdv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rdbSituacaoCadastroMiranda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(tab1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkProdutoSemCodBarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkDataProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(47, 47, 47)
                        .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdbEstoqueCentralEconomia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbPaivaPrecoProdutoos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbPaivaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbSaoFranciscoIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbEstoqueCentralEconomiaCodBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(58, 63, Short.MAX_VALUE))
        );
        tab1Layout.setVerticalGroup(
            tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab1Layout.createSequentialGroup()
                        .addComponent(rdbPadrao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdbCarnauba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbPaivaProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(rdbEstoqueCentralEconomia1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbFreitasProdutosReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbCarnaubaPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbPaivaPrecoProdutoos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbCarnaubaEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbPaivaIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbSaoFranciscoIcms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbCarnaubaPrecoCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbProdutosICMSSysPdv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbEstoqueCentralEconomia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(tab1Layout.createSequentialGroup()
                        .addGroup(tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdbEstoqueCentralEconomiaCodBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbSituacaoCadastroMiranda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addComponent(rdbCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdbOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdbProdutoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdbEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tab1Layout.createSequentialGroup()
                        .addComponent(chkDataProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(chkProdutoSemCodBarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        tabs.addTab("Opções", tab1);

        chkChampProdutos.setText("Produtos");

        chkChampEAN.setText("EAN");

        chkChampMercadologico.setText("Mercadológico");

        chkChampClientePreferencial.setText("Cliente Preferêncial");

        chkChampPrecoCusto.setText("Preço e Custo");

        javax.swing.GroupLayout tabChampLayout = new javax.swing.GroupLayout(tabChamp);
        tabChamp.setLayout(tabChampLayout);
        tabChampLayout.setHorizontalGroup(
            tabChampLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabChampLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabChampLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkChampProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabChampLayout.createSequentialGroup()
                        .addComponent(chkChampEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkChampPrecoCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkChampMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkChampClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(428, Short.MAX_VALUE))
        );
        tabChampLayout.setVerticalGroup(
            tabChampLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabChampLayout.createSequentialGroup()
                .addGroup(tabChampLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabChampLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkChampMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkChampProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkChampEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkChampClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabChampLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(chkChampPrecoCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(175, Short.MAX_VALUE))
        );

        tabs.addTab("Supermercado Champ", tabChamp);

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(flcArquivoProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(flcArquivoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        vRPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados Destino"));
        vRPanel2.setPreferredSize(new java.awt.Dimension(350, 350));

        cmbLojaDestino.setObrigatorio(true);

        vRLabel8.setText("Loja");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLojaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        btnMigrar.setText("Migrar");
        btnMigrar.setFocusable(false);
        btnMigrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnMigrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMigrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMigrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnMigrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                    .addComponent(vRToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(105, 105, 105)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(vRToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRImportaArquivBalancaPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void rdbEstoqueCentralEconomia1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbEstoqueCentralEconomia1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbEstoqueCentralEconomia1ActionPerformed

    private void rdbEstoqueCentralEconomiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbEstoqueCentralEconomiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbEstoqueCentralEconomiaActionPerformed

    private void rdbFreitasProdutosReceitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbFreitasProdutosReceitaActionPerformed
        if (rdbFreitasProdutosReceita.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnauba.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
            rdbCarnaubaEstoque.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbPaivaIcms.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }
    }//GEN-LAST:event_rdbFreitasProdutosReceitaActionPerformed

    private void rdbPaivaProdutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbPaivaProdutosActionPerformed
        if (rdbPaivaProdutos.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnauba.setSelected(false);
            rdbCarnaubaEstoque.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbPaivaIcms.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }
    }//GEN-LAST:event_rdbPaivaProdutosActionPerformed

    private void rdbPaivaPrecoProdutoosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbPaivaPrecoProdutoosActionPerformed
        if (rdbPaivaIcms.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnauba.setSelected(false);
            rdbCarnaubaEstoque.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }
    }//GEN-LAST:event_rdbPaivaPrecoProdutoosActionPerformed

    private void rdbPaivaIcmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbPaivaIcmsActionPerformed
        // TODO add your handling code here:
        if (rdbPaivaIcms.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnauba.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }
    }//GEN-LAST:event_rdbPaivaIcmsActionPerformed

    private void rdbSaoFranciscoIcmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSaoFranciscoIcmsActionPerformed
        // TODO add your handling code here:
        if (rdbSaoFranciscoIcms.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnauba.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbPaivaIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }

    }//GEN-LAST:event_rdbSaoFranciscoIcmsActionPerformed

    private void rdbProdutosICMSSysPdvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbProdutosICMSSysPdvActionPerformed
        // TODO add your handling code here:
        if (rdbProdutosICMSSysPdv.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnauba.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbPaivaIcms.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
        }
    }//GEN-LAST:event_rdbProdutosICMSSysPdvActionPerformed

    private void rdbCarnaubaPrecoCustoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCarnaubaPrecoCustoActionPerformed
        // TODO add your handling code here:
        if (rdbCarnaubaPrecoCusto.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnauba.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbPaivaIcms.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }
    }//GEN-LAST:event_rdbCarnaubaPrecoCustoActionPerformed

    private void rdbCarnaubaEstoqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCarnaubaEstoqueActionPerformed
        if (rdbFreitasProdutosReceita.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnauba.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbPaivaIcms.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }
    }//GEN-LAST:event_rdbCarnaubaEstoqueActionPerformed

    private void rdbPadraoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbPadraoActionPerformed
        if (rdbPadrao.isSelected()) {
            rdbCarnauba.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbCarnaubaEstoque.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbPaivaIcms.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }
    }//GEN-LAST:event_rdbPadraoActionPerformed

    private void rdbCarnaubaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCarnaubaActionPerformed
        // TODO add your handling code here:
        if (rdbCarnauba.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnaubaPisCofins.setSelected(false);
            rdbCarnaubaEstoque.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbPaivaIcms.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }
    }//GEN-LAST:event_rdbCarnaubaActionPerformed

    private void rdbCarnaubaPisCofinsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCarnaubaPisCofinsActionPerformed
        if (rdbCarnaubaPisCofins.isSelected()) {
            rdbPadrao.setSelected(false);
            rdbCarnauba.setSelected(false);
            rdbCarnaubaEstoque.setSelected(false);
            rdbCarnaubaPrecoCusto.setSelected(false);
            rdbFreitasProdutosReceita.setSelected(false);
            rdbPaivaProdutos.setSelected(false);
            rdbPaivaPrecoProdutoos.setSelected(false);
            rdbPaivaIcms.setSelected(false);
            rdbSaoFranciscoIcms.setSelected(false);
            rdbProdutosICMSSysPdv.setSelected(false);
        }
    }//GEN-LAST:event_rdbCarnaubaPisCofinsActionPerformed

    private void rdbCodigoBarrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbCodigoBarrasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbCodigoBarrasActionPerformed

    private void rdbEstoqueCentralEconomiaCodBarrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbEstoqueCentralEconomiaCodBarrasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbEstoqueCentralEconomiaCodBarrasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnMigrar;
    private vrframework.bean.checkBox.VRCheckBox chkChampClientePreferencial;
    private vrframework.bean.checkBox.VRCheckBox chkChampEAN;
    private vrframework.bean.checkBox.VRCheckBox chkChampMercadologico;
    private vrframework.bean.checkBox.VRCheckBox chkChampPrecoCusto;
    private vrframework.bean.checkBox.VRCheckBox chkChampProdutos;
    private vrframework.bean.checkBox.VRCheckBox chkDataProduto;
    private vrframework.bean.checkBox.VRCheckBox chkProdutoSemCodBarra;
    private vrframework.bean.comboBox.VRComboBox cmbLojaDestino;
    private vrframework.bean.fileChooser.VRFileChooser flcArquivoProduto;
    private vrframework.bean.radioButton.VRRadioButton rdbCarnauba;
    private vrframework.bean.radioButton.VRRadioButton rdbCarnaubaEstoque;
    private vrframework.bean.radioButton.VRRadioButton rdbCarnaubaPisCofins;
    private vrframework.bean.radioButton.VRRadioButton rdbCarnaubaPrecoCusto;
    private vrframework.bean.radioButton.VRRadioButton rdbCodigoBarras;
    private vrframework.bean.radioButton.VRRadioButton rdbEstoque;
    private vrframework.bean.radioButton.VRRadioButton rdbEstoqueCentralEconomia;
    private vrframework.bean.radioButton.VRRadioButton rdbEstoqueCentralEconomia1;
    private vrframework.bean.radioButton.VRRadioButton rdbEstoqueCentralEconomiaCodBarras;
    private vrframework.bean.radioButton.VRRadioButton rdbFreitasProdutosReceita;
    private vrframework.bean.radioButton.VRRadioButton rdbOferta;
    private vrframework.bean.radioButton.VRRadioButton rdbPadrao;
    private vrframework.bean.radioButton.VRRadioButton rdbPaivaIcms;
    private vrframework.bean.radioButton.VRRadioButton rdbPaivaPrecoProdutoos;
    private vrframework.bean.radioButton.VRRadioButton rdbPaivaProdutos;
    private vrframework.bean.radioButton.VRRadioButton rdbProdutoFornecedor;
    private vrframework.bean.radioButton.VRRadioButton rdbProdutosICMSSysPdv;
    private vrframework.bean.radioButton.VRRadioButton rdbSaoFranciscoIcms;
    private vrframework.bean.radioButton.VRRadioButton rdbSituacaoCadastroMiranda;
    private vrframework.bean.panel.VRPanel tab1;
    private vrframework.bean.panel.VRPanel tabChamp;
    private vrframework.bean.tabbedPane.VRTabbedPane tabs;
    private vrframework.bean.button.VRButton vRButton1;
    private vrimplantacao.gui.componentes.importabalanca.VRImportaArquivBalancaPanel vRImportaArquivBalancaPanel1;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel8;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.toolBar.VRToolBar vRToolBar1;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao vRToolBarPadrao3;
    // End of variables declaration//GEN-END:variables

}
