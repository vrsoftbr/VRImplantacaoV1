package vrimplantacao2.gui.component.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;

/**
 *
 * @author Leandro
 */
public class ChecksProdutoPanelGUI extends javax.swing.JTabbedPane {

    private Importador importador;
    private Set<OpcaoProduto> opt = OpcaoProduto.getPadrao();

    public void setImportador(Importador importador) {
        this.importador = importador;
    }

    public void setOpcoesDisponiveis(InterfaceDAO dao) {
        this.opt = dao.getOpcoesDisponiveisProdutos();
        tabImportacao.removeAll();
        tabParametros.removeAll();
                
        chkManterBalanca.setVisible(opt.contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA));
                
        if (
                opt.contains(OpcaoProduto.MERCADOLOGICO_PRODUTO) ||
                opt.contains(OpcaoProduto.MERCADOLOGICO) ||
                opt.contains(OpcaoProduto.FAMILIA) ||
                opt.contains(OpcaoProduto.FAMILIA_PRODUTO)
        ) {
            chkMercadologico.setVisible(opt.contains(OpcaoProduto.MERCADOLOGICO_PRODUTO));
            chkProdMercadologico.setVisible(opt.contains(OpcaoProduto.MERCADOLOGICO));
            if (chkMercadologico.isVisible()) {
                chkMercadologicoPorNivel.setVisible(opt.contains(OpcaoProduto.MERCADOLOGICO_POR_NIVEL));
                chkMercadologicoPorNivelReplicar.setVisible(opt.contains(OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR));
                if (
                        opt.contains(OpcaoProduto.MERCADOLOGICO_POR_NIVEL) ||
                        opt.contains(OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR)
                ) {
                    tabParametros.add(pnlOptMercadologico);
                }
            }
            
            
            chkFamilia.setVisible(opt.contains(OpcaoProduto.FAMILIA));
            chkFamiliaProduto.setVisible(opt.contains(OpcaoProduto.FAMILIA_PRODUTO));
            tabImportacao.add(pnlImpMercadologico);            
        }
        
        if (
                opt.contains(OpcaoProduto.PRODUTOS) ||
                opt.contains(OpcaoProduto.EAN) ||
                opt.contains(OpcaoProduto.EAN_EM_BRANCO)
        ) {
            chkProdutos.setVisible(opt.contains(OpcaoProduto.PRODUTOS));
            if (opt.contains(OpcaoProduto.PRODUTOS)) {
                tabParametros.add(pnlOptProduto);
            }
            chkEAN.setVisible(opt.contains(OpcaoProduto.EAN));
            chkEANemBranco.setVisible(opt.contains(OpcaoProduto.EAN_EM_BRANCO));
            tabImportacao.add(pnlImpProduto);            
        }
        
        if (
                opt.contains(OpcaoProduto.PRECO) ||
                opt.contains(OpcaoProduto.CUSTO) ||
                opt.contains(OpcaoProduto.CUSTO_COM_IMPOSTO) ||
                opt.contains(OpcaoProduto.CUSTO_SEM_IMPOSTO) ||
                opt.contains(OpcaoProduto.ESTOQUE) ||
                opt.contains(OpcaoProduto.ATIVO) ||
                opt.contains(OpcaoProduto.DESCONTINUADO) ||
                opt.contains(OpcaoProduto.ATACADO) ||
                opt.contains(OpcaoProduto.OFERTA) ||
                opt.contains(OpcaoProduto.MARGEM)
        ) {
            chkPreco.setVisible(opt.contains(OpcaoProduto.PRECO));
            chkCusto.setVisible(opt.contains(OpcaoProduto.CUSTO));
            chkCustoComImposto.setVisible(opt.contains(OpcaoProduto.CUSTO_COM_IMPOSTO));
            chkCustoSemImposto.setVisible(opt.contains(OpcaoProduto.CUSTO_SEM_IMPOSTO));
            chkEstoque.setVisible(opt.contains(OpcaoProduto.ESTOQUE));
            chkSituacaoCadastro.setVisible(opt.contains(OpcaoProduto.ATIVO));
            chkDescontinuado.setVisible(opt.contains(OpcaoProduto.DESCONTINUADO));
            chkAtacado.setVisible(opt.contains(OpcaoProduto.ATACADO));
            chkOferta.setVisible(opt.contains(OpcaoProduto.OFERTA));
            tabImportacao.add(pnlImpCompl);
        }
        
        if (
                opt.contains(OpcaoProduto.PIS_COFINS) ||
                opt.contains(OpcaoProduto.NATUREZA_RECEITA) ||
                opt.contains(OpcaoProduto.ICMS) ||
                opt.contains(OpcaoProduto.NCM) ||
                opt.contains(OpcaoProduto.CEST) ||
                opt.contains(OpcaoProduto.PAUTA_FISCAL_PRODUTO) ||
                opt.contains(OpcaoProduto.PAUTA_FISCAL)
        ) {
            chkPisCofins.setVisible(opt.contains(OpcaoProduto.PIS_COFINS));
            chkNatReceita.setVisible(opt.contains(OpcaoProduto.NATUREZA_RECEITA));
            chkICMS.setVisible(opt.contains(OpcaoProduto.ICMS));
            btnMapaTribut.setVisible(chkICMS.isVisible());            
            chkNcm.setVisible(opt.contains(OpcaoProduto.NCM));
            chkCest.setVisible(opt.contains(OpcaoProduto.CEST));
            chkPautaFiscal.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL_PRODUTO));
            chkPautaFiscalProduto.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL));
            tabImportacao.add(pnlImpTributacao);
            if (opt.contains(OpcaoProduto.PAUTA_FISCAL)) {
                tabParametros.add(pnlOptPautaFiscal);
            }
        }
        
        if (
                opt.contains(OpcaoProduto.DESC_COMPLETA) ||
                opt.contains(OpcaoProduto.DESC_REDUZIDA) ||
                opt.contains(OpcaoProduto.DESC_GONDOLA) ||
                opt.contains(OpcaoProduto.TIPO_EMBALAGEM_PRODUTO) ||
                opt.contains(OpcaoProduto.VALIDADE) ||
                opt.contains(OpcaoProduto.TIPO_EMBALAGEM_EAN) ||
                opt.contains(OpcaoProduto.QTD_EMBALAGEM_COTACAO) ||
                opt.contains(OpcaoProduto.QTD_EMBALAGEM_EAN) ||
                opt.contains(OpcaoProduto.SUGESTAO_PEDIDO) ||
                opt.contains(OpcaoProduto.SUGESTAO_COTACAO) ||
                opt.contains(OpcaoProduto.VENDA_PDV)
        ) {
            chkDescCompleta.setVisible(opt.contains(OpcaoProduto.DESC_COMPLETA));
            chkDescReduzida.setVisible(opt.contains(OpcaoProduto.DESC_REDUZIDA));
            chkDescGondola.setVisible(opt.contains(OpcaoProduto.DESC_GONDOLA));
            chkTipoEmbalagemProd.setVisible(opt.contains(OpcaoProduto.TIPO_EMBALAGEM_PRODUTO));
            chkValidade.setVisible(opt.contains(OpcaoProduto.VALIDADE));
            chkTipoEmbalagemEAN.setVisible(opt.contains(OpcaoProduto.TIPO_EMBALAGEM_EAN));
            chkQtdEmbalagemProd.setVisible(opt.contains(OpcaoProduto.QTD_EMBALAGEM_COTACAO));
            chkQtdEmbalagemEAN.setVisible(opt.contains(OpcaoProduto.QTD_EMBALAGEM_EAN));
            chkSugestaoPedido.setVisible(opt.contains(OpcaoProduto.SUGESTAO_PEDIDO));
            chkSugestaoCotacao.setVisible(opt.contains(OpcaoProduto.SUGESTAO_COTACAO));
            chkVendaPdv.setVisible(opt.contains(OpcaoProduto.VENDA_PDV));
            tabImportacao.add(pnlImpInfoAdic);
        }
        
        if (
                opt.contains(OpcaoProduto.ASSOCIADO) ||
                opt.contains(OpcaoProduto.COMPRADOR) ||
                opt.contains(OpcaoProduto.COMPRADOR_PRODUTO) ||
                opt.contains(OpcaoProduto.RECEITA_BALANCA) ||
                opt.contains(OpcaoProduto.NUTRICIONAL)
        ) {
            chkAssociado.setVisible(opt.contains(OpcaoProduto.ASSOCIADO));
            if (chkAssociado.isVisible()) {
                chkAssociadoSomenteAtivos.setVisible(chkAssociado.isVisible());
                chkInverterAssociado.setVisible(chkAssociado.isVisible());
                tabParametros.add(pnlOptAssociado);
            }
            chkComprador.setVisible(opt.contains(OpcaoProduto.COMPRADOR));
            chkCompradorProduto.setVisible(opt.contains(OpcaoProduto.COMPRADOR_PRODUTO));
            chkReceitaToledo.setVisible(opt.contains(OpcaoProduto.RECEITA_BALANCA));
            chkReceitaFilizola.setVisible(opt.contains(OpcaoProduto.RECEITA_BALANCA));
            chkNutricionalToledo.setVisible(opt.contains(OpcaoProduto.NUTRICIONAL));
            chkNutricionalFilizola.setVisible(opt.contains(OpcaoProduto.NUTRICIONAL));
            tabImportacao.add(pnlImpOutrosDados);
        }
        
        tabImportacao.revalidate();
        
    }

    public Set<OpcaoProduto> getOpcoesDisponiveis() {
        return opt;
    }
    
    /**
     * Creates new form ChecksProdutoPanelGUI
     */
    public ChecksProdutoPanelGUI() {
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

        btgPautaFiscal = new javax.swing.ButtonGroup();
        scrollParametros = new javax.swing.JScrollPane();
        tabParametros = new vrframework.bean.panel.VRPanel();
        pnlOptMercadologico = new vrframework.bean.panel.VRPanel();
        jLabel3 = new javax.swing.JLabel();
        chkMercadologicoPorNivel = new vrframework.bean.checkBox.VRCheckBox();
        chkMercadologicoPorNivelReplicar = new vrframework.bean.checkBox.VRCheckBox();
        pnlOptProduto = new vrframework.bean.panel.VRPanel();
        chkManterBalanca = new vrframework.bean.checkBox.VRCheckBox();
        btnMapaTribut = new vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton();
        jLabel1 = new javax.swing.JLabel();
        pnlOptAssociado = new vrframework.bean.panel.VRPanel();
        jLabel2 = new javax.swing.JLabel();
        chkInverterAssociado = new vrframework.bean.checkBox.VRCheckBox();
        chkAssociadoSomenteAtivos = new vrframework.bean.checkBox.VRCheckBox();
        pnlOptPautaFiscal = new vrframework.bean.panel.VRPanel();
        jLabel10 = new javax.swing.JLabel();
        rdbPautaIdPauta = new vrframework.bean.radioButton.VRRadioButton();
        rdbPautaIdProduto = new vrframework.bean.radioButton.VRRadioButton();
        rdbPautaEan = new vrframework.bean.radioButton.VRRadioButton();
        chkPautaUsarEansMenores = new vrframework.bean.checkBox.VRCheckBox();
        scrollImportação = new javax.swing.JScrollPane();
        tabImportacao = new vrframework.bean.panel.VRPanel();
        pnlImpMercadologico = new vrframework.bean.panel.VRPanel();
        jLabel5 = new javax.swing.JLabel();
        chkMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkProdMercadologico = new vrframework.bean.checkBox.VRCheckBox();
        chkFamiliaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkFamilia = new vrframework.bean.checkBox.VRCheckBox();
        pnlImpProduto = new vrframework.bean.panel.VRPanel();
        jLabel6 = new javax.swing.JLabel();
        chkProdutos = new vrframework.bean.checkBox.VRCheckBox();
        chkEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkEANemBranco = new vrframework.bean.checkBox.VRCheckBox();
        pnlImpCompl = new vrframework.bean.panel.VRPanel();
        jLabel7 = new javax.swing.JLabel();
        chkPreco = new vrframework.bean.checkBox.VRCheckBox();
        chkCusto = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoSemImposto = new vrframework.bean.checkBox.VRCheckBox();
        chkCustoComImposto = new vrframework.bean.checkBox.VRCheckBox();
        chkEstoque = new vrframework.bean.checkBox.VRCheckBox();
        chkSituacaoCadastro = new vrframework.bean.checkBox.VRCheckBox();
        chkDescontinuado = new vrframework.bean.checkBox.VRCheckBox();
        chkAtacado = new vrframework.bean.checkBox.VRCheckBox();
        chkOferta = new vrframework.bean.checkBox.VRCheckBox();
        chkMargem = new vrframework.bean.checkBox.VRCheckBox();
        pnlImpTributacao = new vrframework.bean.panel.VRPanel();
        jLabel4 = new javax.swing.JLabel();
        chkPisCofins = new vrframework.bean.checkBox.VRCheckBox();
        chkNatReceita = new vrframework.bean.checkBox.VRCheckBox();
        chkICMS = new vrframework.bean.checkBox.VRCheckBox();
        chkNcm = new vrframework.bean.checkBox.VRCheckBox();
        chkCest = new vrframework.bean.checkBox.VRCheckBox();
        chkPautaFiscal = new vrframework.bean.checkBox.VRCheckBox();
        chkPautaFiscalProduto = new vrframework.bean.checkBox.VRCheckBox();
        pnlImpInfoAdic = new vrframework.bean.panel.VRPanel();
        jLabel8 = new javax.swing.JLabel();
        chkDescCompleta = new vrframework.bean.checkBox.VRCheckBox();
        chkDescReduzida = new vrframework.bean.checkBox.VRCheckBox();
        chkDescGondola = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemProd = new vrframework.bean.checkBox.VRCheckBox();
        chkValidade = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbalagemProd = new vrframework.bean.checkBox.VRCheckBox();
        chkQtdEmbalagemEAN = new vrframework.bean.checkBox.VRCheckBox();
        chkSugestaoPedido = new vrframework.bean.checkBox.VRCheckBox();
        chkSugestaoCotacao = new vrframework.bean.checkBox.VRCheckBox();
        chkVendaPdv = new vrframework.bean.checkBox.VRCheckBox();
        pnlImpOutrosDados = new vrframework.bean.panel.VRPanel();
        jLabel9 = new javax.swing.JLabel();
        chkAssociado = new vrframework.bean.checkBox.VRCheckBox();
        chkComprador = new vrframework.bean.checkBox.VRCheckBox();
        chkCompradorProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkReceitaToledo = new vrframework.bean.checkBox.VRCheckBox();
        chkReceitaFilizola = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalToledo = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalFilizola = new vrframework.bean.checkBox.VRCheckBox();

        scrollParametros.setBorder(null);

        tabParametros.setLayout(new org.jdesktop.swingx.VerticalLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "MERCADOLÓGICO");

        org.openide.awt.Mnemonics.setLocalizedText(chkMercadologicoPorNivel, "Mercadologico Por Nível");
        chkMercadologicoPorNivel.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkMercadologicoPorNivelReplicar, "Replicar Subníveis");
        chkMercadologicoPorNivelReplicar.setEnabled(true);

        javax.swing.GroupLayout pnlOptMercadologicoLayout = new javax.swing.GroupLayout(pnlOptMercadologico);
        pnlOptMercadologico.setLayout(pnlOptMercadologicoLayout);
        pnlOptMercadologicoLayout.setHorizontalGroup(
            pnlOptMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptMercadologicoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(chkMercadologicoPorNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMercadologicoPorNivelReplicar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(493, Short.MAX_VALUE))
        );
        pnlOptMercadologicoLayout.setVerticalGroup(
            pnlOptMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptMercadologicoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlOptMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOptMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkMercadologicoPorNivelReplicar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkMercadologicoPorNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabParametros.add(pnlOptMercadologico);

        org.openide.awt.Mnemonics.setLocalizedText(chkManterBalanca, "Manter código PLU dos produtos de balança");
        chkManterBalanca.setEnabled(true);

        btnMapaTribut.setEnabled(false);
        btnMapaTribut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaTributActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "PRODUTOS");
        jLabel1.setPreferredSize(new java.awt.Dimension(132, 14));

        javax.swing.GroupLayout pnlOptProdutoLayout = new javax.swing.GroupLayout(pnlOptProduto);
        pnlOptProduto.setLayout(pnlOptProdutoLayout);
        pnlOptProdutoLayout.setHorizontalGroup(
            pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMapaTribut, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 508, Short.MAX_VALUE))
        );
        pnlOptProdutoLayout.setVerticalGroup(
            pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                        .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMapaTribut, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 11, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        tabParametros.add(pnlOptProduto);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "ASSOCIADOS");
        jLabel2.setPreferredSize(new java.awt.Dimension(132, 14));

        org.openide.awt.Mnemonics.setLocalizedText(chkInverterAssociado, "Inverter");
        chkInverterAssociado.setToolTipText("Selecione essa opção para que ao importar o associado, seja gerado também o associado para preço e custo.");

        org.openide.awt.Mnemonics.setLocalizedText(chkAssociadoSomenteAtivos, "Somente produtos ativos");
        chkAssociadoSomenteAtivos.setToolTipText("Selecione essa opção para que ao importar o associado, seja gerado também o associado para preço e custo.");

        javax.swing.GroupLayout pnlOptAssociadoLayout = new javax.swing.GroupLayout(pnlOptAssociado);
        pnlOptAssociado.setLayout(pnlOptAssociadoLayout);
        pnlOptAssociadoLayout.setHorizontalGroup(
            pnlOptAssociadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptAssociadoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(chkInverterAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAssociadoSomenteAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(533, Short.MAX_VALUE))
        );
        pnlOptAssociadoLayout.setVerticalGroup(
            pnlOptAssociadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptAssociadoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlOptAssociadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkInverterAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAssociadoSomenteAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlOptAssociadoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chkAssociadoSomenteAtivos, chkInverterAssociado, jLabel2});

        tabParametros.add(pnlOptAssociado);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, "PAUTA FISCAL");
        jLabel10.setPreferredSize(new java.awt.Dimension(132, 14));

        btgPautaFiscal.add(rdbPautaIdPauta);
        rdbPautaIdPauta.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(rdbPautaIdPauta, "Usar ID da Pauta");
        rdbPautaIdPauta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbPautaEanActionPerformed(evt);
            }
        });

        btgPautaFiscal.add(rdbPautaIdProduto);
        org.openide.awt.Mnemonics.setLocalizedText(rdbPautaIdProduto, "Usar ID de Produto");
        rdbPautaIdProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbPautaEanActionPerformed(evt);
            }
        });

        btgPautaFiscal.add(rdbPautaEan);
        org.openide.awt.Mnemonics.setLocalizedText(rdbPautaEan, "Usar EAN");
        rdbPautaEan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbPautaEanActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkPautaUsarEansMenores, "Usar EANs menores");
        chkPautaUsarEansMenores.setToolTipText("Considera EANs menores ou iguais a 999999 na busca por EAN.");
        chkPautaUsarEansMenores.setEnabled(false);

        javax.swing.GroupLayout pnlOptPautaFiscalLayout = new javax.swing.GroupLayout(pnlOptPautaFiscal);
        pnlOptPautaFiscal.setLayout(pnlOptPautaFiscalLayout);
        pnlOptPautaFiscalLayout.setHorizontalGroup(
            pnlOptPautaFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptPautaFiscalLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbPautaIdPauta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbPautaIdProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbPautaEan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPautaUsarEansMenores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(324, Short.MAX_VALUE))
        );
        pnlOptPautaFiscalLayout.setVerticalGroup(
            pnlOptPautaFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptPautaFiscalLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlOptPautaFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbPautaIdPauta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbPautaIdProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbPautaEan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPautaUsarEansMenores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabParametros.add(pnlOptPautaFiscal);

        scrollParametros.setViewportView(tabParametros);

        addTab("Parâmetros", scrollParametros);

        scrollImportação.setBorder(null);

        tabImportacao.setLayout(new org.jdesktop.swingx.VerticalLayout());

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "MERCADOLOGICO E FAMÍLIA");

        org.openide.awt.Mnemonics.setLocalizedText(chkMercadologico, "Mercadologico");
        chkMercadologico.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkProdMercadologico, "Mercadológico X Produto");

        org.openide.awt.Mnemonics.setLocalizedText(chkFamiliaProduto, "Familia Produto");
        chkFamiliaProduto.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkFamilia, "Família X Produto");
        chkFamilia.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        javax.swing.GroupLayout pnlImpMercadologicoLayout = new javax.swing.GroupLayout(pnlImpMercadologico);
        pnlImpMercadologico.setLayout(pnlImpMercadologicoLayout);
        pnlImpMercadologicoLayout.setHorizontalGroup(
            pnlImpMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpMercadologicoLayout.createSequentialGroup()
                .addGroup(pnlImpMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImpMercadologicoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel5))
                    .addGroup(pnlImpMercadologicoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkProdMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(115, Short.MAX_VALUE))
        );
        pnlImpMercadologicoLayout.setVerticalGroup(
            pnlImpMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpMercadologicoLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFamiliaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkProdMercadologico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        tabImportacao.add(pnlImpMercadologico);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, "PRODUTOS");

        org.openide.awt.Mnemonics.setLocalizedText(chkProdutos, "Produtos");
        chkProdutos.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkEAN, "EAN");

        org.openide.awt.Mnemonics.setLocalizedText(chkEANemBranco, "EAN em branco");

        javax.swing.GroupLayout pnlImpProdutoLayout = new javax.swing.GroupLayout(pnlImpProduto);
        pnlImpProduto.setLayout(pnlImpProdutoLayout);
        pnlImpProdutoLayout.setHorizontalGroup(
            pnlImpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpProdutoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlImpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(pnlImpProdutoLayout.createSequentialGroup()
                        .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkEANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(345, Short.MAX_VALUE))
        );
        pnlImpProdutoLayout.setVerticalGroup(
            pnlImpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpProdutoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkProdutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEANemBranco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabImportacao.add(pnlImpProduto);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, "COMPLEMENTO (POR LOJA)");

        org.openide.awt.Mnemonics.setLocalizedText(chkPreco, "Preço");

        org.openide.awt.Mnemonics.setLocalizedText(chkCusto, "Custo");

        org.openide.awt.Mnemonics.setLocalizedText(chkCustoSemImposto, "Custo Sem Imposto");

        org.openide.awt.Mnemonics.setLocalizedText(chkCustoComImposto, "Custo Com Imposto");

        org.openide.awt.Mnemonics.setLocalizedText(chkEstoque, "Estoque");

        org.openide.awt.Mnemonics.setLocalizedText(chkSituacaoCadastro, "Sit. Cadastro");

        org.openide.awt.Mnemonics.setLocalizedText(chkDescontinuado, "Descontinuado");

        org.openide.awt.Mnemonics.setLocalizedText(chkAtacado, "Atacado");

        org.openide.awt.Mnemonics.setLocalizedText(chkOferta, "Oferta");

        org.openide.awt.Mnemonics.setLocalizedText(chkMargem, "Margem");

        javax.swing.GroupLayout pnlImpComplLayout = new javax.swing.GroupLayout(pnlImpCompl);
        pnlImpCompl.setLayout(pnlImpComplLayout);
        pnlImpComplLayout.setHorizontalGroup(
            pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpComplLayout.createSequentialGroup()
                .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImpComplLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addGroup(pnlImpComplLayout.createSequentialGroup()
                                .addComponent(chkDescontinuado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnlImpComplLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCustoSemImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCustoComImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        pnlImpComplLayout.setVerticalGroup(
            pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpComplLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCustoSemImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCustoComImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkDescontinuado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabImportacao.add(pnlImpCompl);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "TRIBUTAÇÃO");

        org.openide.awt.Mnemonics.setLocalizedText(chkPisCofins, "PIS/COFINS");

        org.openide.awt.Mnemonics.setLocalizedText(chkNatReceita, "Nat. Receita");

        org.openide.awt.Mnemonics.setLocalizedText(chkICMS, "ICMS");

        org.openide.awt.Mnemonics.setLocalizedText(chkNcm, "NCM");

        org.openide.awt.Mnemonics.setLocalizedText(chkCest, "CEST");

        org.openide.awt.Mnemonics.setLocalizedText(chkPautaFiscal, "Pauta Fiscal");

        org.openide.awt.Mnemonics.setLocalizedText(chkPautaFiscalProduto, "Pauta Fiscal X Produto");

        javax.swing.GroupLayout pnlImpTributacaoLayout = new javax.swing.GroupLayout(pnlImpTributacao);
        pnlImpTributacao.setLayout(pnlImpTributacaoLayout);
        pnlImpTributacaoLayout.setHorizontalGroup(
            pnlImpTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpTributacaoLayout.createSequentialGroup()
                .addGroup(pnlImpTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImpTributacaoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel4))
                    .addGroup(pnlImpTributacaoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlImpTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chkPautaFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlImpTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlImpTributacaoLayout.createSequentialGroup()
                                .addComponent(chkNatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkNcm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkCest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkPautaFiscalProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6))
        );
        pnlImpTributacaoLayout.setVerticalGroup(
            pnlImpTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpTributacaoLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNcm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPautaFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPautaFiscalProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        tabImportacao.add(pnlImpTributacao);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, "INFORMAÇÕES ADICIONAIS (ATUALIZAÇÃO)");

        org.openide.awt.Mnemonics.setLocalizedText(chkDescCompleta, "Descrição Completa");

        org.openide.awt.Mnemonics.setLocalizedText(chkDescReduzida, "Descrição Reduzida");

        org.openide.awt.Mnemonics.setLocalizedText(chkDescGondola, "Descrição Gondola");

        org.openide.awt.Mnemonics.setLocalizedText(chkTipoEmbalagemProd, "Tipo Emb. Prod");

        org.openide.awt.Mnemonics.setLocalizedText(chkValidade, "Validade");

        org.openide.awt.Mnemonics.setLocalizedText(chkTipoEmbalagemEAN, "Tipo Emb. EAN");

        org.openide.awt.Mnemonics.setLocalizedText(chkQtdEmbalagemProd, "Qtd. Emb. Produto");

        org.openide.awt.Mnemonics.setLocalizedText(chkQtdEmbalagemEAN, "Qtd. Emb. EAN");

        org.openide.awt.Mnemonics.setLocalizedText(chkSugestaoPedido, "Sugestão Pedido");

        org.openide.awt.Mnemonics.setLocalizedText(chkSugestaoCotacao, "Sugestão Cotação");

        org.openide.awt.Mnemonics.setLocalizedText(chkVendaPdv, "Venda (PDV)");

        javax.swing.GroupLayout pnlImpInfoAdicLayout = new javax.swing.GroupLayout(pnlImpInfoAdic);
        pnlImpInfoAdic.setLayout(pnlImpInfoAdicLayout);
        pnlImpInfoAdicLayout.setHorizontalGroup(
            pnlImpInfoAdicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpInfoAdicLayout.createSequentialGroup()
                .addGroup(pnlImpInfoAdicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImpInfoAdicLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel8))
                    .addGroup(pnlImpInfoAdicLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlImpInfoAdicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlImpInfoAdicLayout.createSequentialGroup()
                                .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkQtdEmbalagemProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkSugestaoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkSugestaoCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlImpInfoAdicLayout.createSequentialGroup()
                                .addComponent(chkDescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkDescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkDescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkTipoEmbalagemProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnlImpInfoAdicLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkVendaPdv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlImpInfoAdicLayout.setVerticalGroup(
            pnlImpInfoAdicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlImpInfoAdicLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel8)
                .addGap(0, 0, 0)
                .addGroup(pnlImpInfoAdicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkDescCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDescReduzida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDescGondola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTipoEmbalagemProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTipoEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpInfoAdicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkQtdEmbalagemEAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSugestaoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkQtdEmbalagemProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSugestaoCotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkVendaPdv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        tabImportacao.add(pnlImpInfoAdic);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, "OUTROS DADOS");

        org.openide.awt.Mnemonics.setLocalizedText(chkAssociado, "Associado");

        org.openide.awt.Mnemonics.setLocalizedText(chkComprador, "Comprador");

        org.openide.awt.Mnemonics.setLocalizedText(chkCompradorProduto, "Comprador X Produto");

        org.openide.awt.Mnemonics.setLocalizedText(chkReceitaToledo, "Receita (Toledo)");

        org.openide.awt.Mnemonics.setLocalizedText(chkReceitaFilizola, "Receita (Filizola)");

        org.openide.awt.Mnemonics.setLocalizedText(chkNutricionalToledo, "Nutricional (Toledo)");

        org.openide.awt.Mnemonics.setLocalizedText(chkNutricionalFilizola, "Nutricional (Filizola)");

        javax.swing.GroupLayout pnlImpOutrosDadosLayout = new javax.swing.GroupLayout(pnlImpOutrosDados);
        pnlImpOutrosDados.setLayout(pnlImpOutrosDadosLayout);
        pnlImpOutrosDadosLayout.setHorizontalGroup(
            pnlImpOutrosDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpOutrosDadosLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlImpOutrosDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(pnlImpOutrosDadosLayout.createSequentialGroup()
                        .addComponent(chkNutricionalToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkNutricionalFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlImpOutrosDadosLayout.createSequentialGroup()
                        .addComponent(chkAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkComprador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCompradorProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkReceitaToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkReceitaFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        pnlImpOutrosDadosLayout.setVerticalGroup(
            pnlImpOutrosDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpOutrosDadosLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpOutrosDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkComprador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCompradorProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkReceitaToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkReceitaFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpOutrosDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkNutricionalToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNutricionalFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        tabImportacao.add(pnlImpOutrosDados);

        scrollImportação.setViewportView(tabImportacao);

        addTab("Importação de Produtos", scrollImportação);
    }// </editor-fold>//GEN-END:initComponents

    private void btnMapaTributActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaTributActionPerformed

    }//GEN-LAST:event_btnMapaTributActionPerformed

    private void rdbPautaEanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbPautaEanActionPerformed
        chkPautaUsarEansMenores.setEnabled(rdbPautaEan.isSelected());
    }//GEN-LAST:event_rdbPautaEanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.ButtonGroup btgPautaFiscal;
    public vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton btnMapaTribut;
    public vrframework.bean.checkBox.VRCheckBox chkAssociado;
    public vrframework.bean.checkBox.VRCheckBox chkAssociadoSomenteAtivos;
    public vrframework.bean.checkBox.VRCheckBox chkAtacado;
    public vrframework.bean.checkBox.VRCheckBox chkCest;
    public vrframework.bean.checkBox.VRCheckBox chkComprador;
    public vrframework.bean.checkBox.VRCheckBox chkCompradorProduto;
    public vrframework.bean.checkBox.VRCheckBox chkCusto;
    public vrframework.bean.checkBox.VRCheckBox chkCustoComImposto;
    public vrframework.bean.checkBox.VRCheckBox chkCustoSemImposto;
    public vrframework.bean.checkBox.VRCheckBox chkDescCompleta;
    public vrframework.bean.checkBox.VRCheckBox chkDescGondola;
    public vrframework.bean.checkBox.VRCheckBox chkDescReduzida;
    public vrframework.bean.checkBox.VRCheckBox chkDescontinuado;
    public vrframework.bean.checkBox.VRCheckBox chkEAN;
    public vrframework.bean.checkBox.VRCheckBox chkEANemBranco;
    public vrframework.bean.checkBox.VRCheckBox chkEstoque;
    public vrframework.bean.checkBox.VRCheckBox chkFamilia;
    public vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    public vrframework.bean.checkBox.VRCheckBox chkICMS;
    public vrframework.bean.checkBox.VRCheckBox chkInverterAssociado;
    public vrframework.bean.checkBox.VRCheckBox chkManterBalanca;
    public vrframework.bean.checkBox.VRCheckBox chkMargem;
    public vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    public vrframework.bean.checkBox.VRCheckBox chkMercadologicoPorNivel;
    public vrframework.bean.checkBox.VRCheckBox chkMercadologicoPorNivelReplicar;
    public vrframework.bean.checkBox.VRCheckBox chkNatReceita;
    public vrframework.bean.checkBox.VRCheckBox chkNcm;
    public vrframework.bean.checkBox.VRCheckBox chkNutricionalFilizola;
    public vrframework.bean.checkBox.VRCheckBox chkNutricionalToledo;
    public vrframework.bean.checkBox.VRCheckBox chkOferta;
    public vrframework.bean.checkBox.VRCheckBox chkPautaFiscal;
    public vrframework.bean.checkBox.VRCheckBox chkPautaFiscalProduto;
    public vrframework.bean.checkBox.VRCheckBox chkPautaUsarEansMenores;
    public vrframework.bean.checkBox.VRCheckBox chkPisCofins;
    public vrframework.bean.checkBox.VRCheckBox chkPreco;
    public vrframework.bean.checkBox.VRCheckBox chkProdMercadologico;
    public vrframework.bean.checkBox.VRCheckBox chkProdutos;
    public vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemEAN;
    public vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemProd;
    public vrframework.bean.checkBox.VRCheckBox chkReceitaFilizola;
    public vrframework.bean.checkBox.VRCheckBox chkReceitaToledo;
    public vrframework.bean.checkBox.VRCheckBox chkSituacaoCadastro;
    public vrframework.bean.checkBox.VRCheckBox chkSugestaoCotacao;
    public vrframework.bean.checkBox.VRCheckBox chkSugestaoPedido;
    public vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemEAN;
    public vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemProd;
    public vrframework.bean.checkBox.VRCheckBox chkValidade;
    public vrframework.bean.checkBox.VRCheckBox chkVendaPdv;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel10;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel4;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public javax.swing.JLabel jLabel8;
    public javax.swing.JLabel jLabel9;
    public vrframework.bean.panel.VRPanel pnlImpCompl;
    public vrframework.bean.panel.VRPanel pnlImpInfoAdic;
    public vrframework.bean.panel.VRPanel pnlImpMercadologico;
    public vrframework.bean.panel.VRPanel pnlImpOutrosDados;
    public vrframework.bean.panel.VRPanel pnlImpProduto;
    public vrframework.bean.panel.VRPanel pnlImpTributacao;
    public vrframework.bean.panel.VRPanel pnlOptAssociado;
    public vrframework.bean.panel.VRPanel pnlOptMercadologico;
    public vrframework.bean.panel.VRPanel pnlOptPautaFiscal;
    public vrframework.bean.panel.VRPanel pnlOptProduto;
    public vrframework.bean.radioButton.VRRadioButton rdbPautaEan;
    public vrframework.bean.radioButton.VRRadioButton rdbPautaIdPauta;
    public vrframework.bean.radioButton.VRRadioButton rdbPautaIdProduto;
    public javax.swing.JScrollPane scrollImportação;
    public javax.swing.JScrollPane scrollParametros;
    public vrframework.bean.panel.VRPanel tabImportacao;
    public vrframework.bean.panel.VRPanel tabParametros;
    // End of variables declaration//GEN-END:variables

    public void executarImportacao() throws Exception {
        new ProdutoPanelImportador().importar();
    }
    public void executarImportacao(ProdutoPanelImportador produtoPanelImportador) throws Exception {
        if (produtoPanelImportador != null) {
            produtoPanelImportador.importar();
        }
    }
    
    private String[] concat(String[] params, String novo) {
        params = Arrays.copyOf(params, params.length + 1);
        params[params.length - 1] = novo;
        return params;
    }

    public void gravarParametros(Parametros parametros, String... params) {        
        
        parametros.put(chkMercadologicoPorNivel.isSelected(), concat(params, "MERCADOLOGICO_POR_NIVEL"));
        parametros.put(chkMercadologicoPorNivelReplicar.isSelected(), concat(params, "MERCADOLOGICO_POR_NIVEL_REPLICAR" ));
        parametros.put(chkManterBalanca.isSelected(), concat(params, "MANTER_PLU_BALANCA" ));
        parametros.put(chkInverterAssociado.isSelected(), concat(params, "INVERTER_ASSOCIADO" ));
        parametros.put(chkAssociadoSomenteAtivos.isSelected(), concat(params, "SOMENTES_ASSOCIADOS_DE_PRODUTOS_ATIVOS" ));
        if (rdbPautaIdPauta.isSelected()) {
            parametros.put(1, concat(params, "PAUTA_OPCAO" ));
        } else if (rdbPautaIdProduto.isSelected()) {
            parametros.put(2, concat(params, "PAUTA_OPCAO" ));
        } else if (rdbPautaEan.isSelected()) {
            parametros.put(3, concat(params, "PAUTA_OPCAO" ));
        }
        parametros.put(chkPautaUsarEansMenores.isSelected(), concat(params, "PAUTA_USAR_EANS_MENORES" ));
    }

    public void carregarParametros(Parametros parametros, String... params) {
        
        chkMercadologicoPorNivel.setSelected(parametros.getBool(concat(params, "MERCADOLOGICO_POR_NIVEL" )));
        chkMercadologicoPorNivelReplicar.setSelected(parametros.getBool(concat(params, "MERCADOLOGICO_POR_NIVEL_REPLICAR" )));
        chkManterBalanca.setSelected(parametros.getBool(concat(params, "MANTER_PLU_BALANCA" )));
        chkInverterAssociado.setSelected(parametros.getBool(concat(params, "INVERTER_ASSOCIADO" )));
        chkAssociadoSomenteAtivos.setSelected(parametros.getBool(concat(params, "SOMENTES_ASSOCIADOS_DE_PRODUTOS_ATIVOS" )));
        
        switch (parametros.getInt(concat(params, "PAUTA_OPCAO" ))) {
            case 2: rdbPautaIdProduto.setSelected(true); break;
            case 3: rdbPautaEan.setSelected(true); break;
            default: rdbPautaIdPauta.setSelected(true); break;
        }
        rdbPautaEanActionPerformed(null);
        chkPautaUsarEansMenores.setSelected(parametros.getBool(concat(params, "PAUTA_USAR_EANS_MENORES" )));
        
    }
    
    public class ProdutoPanelImportador {
        public void importar() throws Exception {
            
            if (chkFamiliaProduto.isSelected()) {
                importador.importarFamiliaProduto();
            }

            if (chkMercadologico.isSelected()) {
                importador.importarMercadologico();
            }

            if (chkMercadologicoPorNivel.isSelected()) {
                importador.importarMercadologicoPorNiveis(chkMercadologicoPorNivelReplicar.isSelected());
            }

            if (chkProdutos.isSelected()) {
                importador.importarProduto(chkManterBalanca.isSelected());
            }
            if (chkPautaFiscal.isSelected()) {
                
                List<OpcaoFiscal> opcoes = new ArrayList<>();
                
                if (rdbPautaIdProduto.isSelected()) {
                    opcoes.add(OpcaoFiscal.USAR_IDPRODUTO);
                }
                if (rdbPautaEan.isSelected()) {
                    opcoes.add(OpcaoFiscal.USAR_EAN);
                }
                if (chkPautaUsarEansMenores.isSelected()) {
                    opcoes.add(OpcaoFiscal.UTILIZAR_EANS_MENORES);
                }
                opcoes.add(OpcaoFiscal.NOVOS);
                
                if (!opcoes.isEmpty()) {
                    importador.importarPautaFiscal(opcoes.toArray(new OpcaoFiscal[]{}));
                }
            }
            if (chkComprador.isSelected()) {
                importador.importarComprador();
            }             

            {
                List<OpcaoProduto> opcoes = new ArrayList<>();
                if (chkCusto.isSelected()) {
                    opcoes.add(OpcaoProduto.CUSTO);
                }
                if (chkCustoComImposto.isSelected()) {
                    opcoes.add(OpcaoProduto.CUSTO_COM_IMPOSTO);
                }
                if (chkCustoSemImposto.isSelected()) {
                    opcoes.add(OpcaoProduto.CUSTO_SEM_IMPOSTO);
                }
                if (chkPreco.isSelected()) {
                    opcoes.add(OpcaoProduto.PRECO);
                }
                if (chkEstoque.isSelected()) {
                    opcoes.add(OpcaoProduto.ESTOQUE);
                }
                if (chkPisCofins.isSelected()) {
                    opcoes.add(OpcaoProduto.PIS_COFINS);
                }
                if (chkNatReceita.isSelected()) {
                    opcoes.add(OpcaoProduto.NATUREZA_RECEITA);
                }
                if (chkICMS.isSelected()) {
                    opcoes.add(OpcaoProduto.ICMS);
                }
                if (chkSituacaoCadastro.isSelected()) {
                    opcoes.add(OpcaoProduto.ATIVO);
                }
                if (chkDescCompleta.isSelected()) {
                    opcoes.add(OpcaoProduto.DESC_COMPLETA);
                }
                if (chkDescReduzida.isSelected()) {
                    opcoes.add(OpcaoProduto.DESC_REDUZIDA);
                }
                if (chkDescGondola.isSelected()) {
                    opcoes.add(OpcaoProduto.DESC_GONDOLA);
                }
                if (chkProdMercadologico.isSelected()) {
                    opcoes.add(OpcaoProduto.MERCADOLOGICO);
                }
                if (chkValidade.isSelected()) {
                    opcoes.add(OpcaoProduto.VALIDADE);
                }
                if (chkMargem.isSelected()) {
                    opcoes.add(OpcaoProduto.MARGEM);
                }
                if (chkFamilia.isSelected()) {
                    opcoes.add(OpcaoProduto.FAMILIA);
                }
                if (chkTipoEmbalagemProd.isSelected()) {
                    opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_PRODUTO);
                }
                if (chkTipoEmbalagemEAN.isSelected()) {
                    opcoes.add(OpcaoProduto.TIPO_EMBALAGEM_EAN);
                }
                if (chkQtdEmbalagemEAN.isSelected()) {
                    opcoes.add(OpcaoProduto.QTD_EMBALAGEM_EAN);
                }
                if (chkPautaFiscalProduto.isSelected()) {
                    opcoes.add(OpcaoProduto.EXCECAO);
                }
                if (chkDescontinuado.isSelected()) {
                    opcoes.add(OpcaoProduto.DESCONTINUADO);
                }
                if (chkVendaPdv.isSelected()) {
                    opcoes.add(OpcaoProduto.VENDA_PDV);
                }
                if (chkSugestaoCotacao.isSelected()) {
                    opcoes.add(OpcaoProduto.SUGESTAO_COTACAO);
                }
                if (chkCompradorProduto.isSelected()) {
                    opcoes.add(OpcaoProduto.COMPRADOR_PRODUTO);
                }
                if (chkAtacado.isSelected()) {
                    opcoes.add(OpcaoProduto.ATACADO);
                }
                if (chkNcm.isSelected()) {
                    opcoes.add(OpcaoProduto.NCM);
                }
                if (chkCest.isSelected()) {
                    opcoes.add(OpcaoProduto.CEST);
                }
                if (chkQtdEmbalagemProd.isSelected()) {
                    opcoes.add(OpcaoProduto.QTD_EMBALAGEM_COTACAO);
                }
                if (!opcoes.isEmpty()) {
                    importador.atualizarProdutos(opcoes);
                }
            }
            if (chkEAN.isSelected()) {
                importador.importarEAN();
            }
            if (chkEANemBranco.isSelected()) {
                importador.importarEANemBranco();
            }

            {
                List<OpcaoNutricional> opcoes = new ArrayList<>();
                if (chkNutricionalFilizola.isSelected()) {
                    opcoes.add(OpcaoNutricional.FILIZOLA);
                }
                if (chkNutricionalToledo.isSelected()) {
                    opcoes.add(OpcaoNutricional.TOLEDO);
                }
                if (!opcoes.isEmpty()) {
                    importador.importarNutricional(opcoes.toArray(new OpcaoNutricional[] {}));
                }                            
            }

            {
                List<OpcaoReceitaBalanca> opcoes = new ArrayList<>();
                if (chkReceitaFilizola.isSelected()) {
                    opcoes.add(OpcaoReceitaBalanca.FILIZOLA);
                }
                if (chkReceitaToledo.isSelected()) {
                    opcoes.add(OpcaoReceitaBalanca.TOLEDO);
                }
                if (!opcoes.isEmpty()) {
                    importador.importarReceitaBalanca(opcoes.toArray(new OpcaoReceitaBalanca[] {}));
                }                            
            }

            if (chkOferta.isSelected()) {
                importador.importarOfertas(new Date());
            }
        }
    }
   
}
