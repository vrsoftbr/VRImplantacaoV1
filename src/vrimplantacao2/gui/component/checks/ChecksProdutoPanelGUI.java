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
import vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButtonProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;

/**
 *
 * @author Leandro
 */
public class ChecksProdutoPanelGUI extends javax.swing.JTabbedPane {

    public Importador importador;
    private Set<OpcaoProduto> opt = OpcaoProduto.getPadrao();
    private List<OpcaoProduto> parametrosExtras = new ArrayList<>();
    private ImportAction importadorMercadologico = new ImportAction(this) {

        @Override
        public void importarMercadologico() throws Exception {
            List<OpcaoProduto> opt = new ArrayList<>();

            if (gui.chkMercadologicoPorNivelReplicar.isSelected()) {
                opt.add(OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR);
            }

            if (gui.chkMercadologicoNaoExcluir.isSelected()) {
                opt.add(OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR);
            }

            if (gui.chkMercadologico.isSelected()) {
                if (gui.opt.contains(OpcaoProduto.MERCADOLOGICO_POR_NIVEL)) {
                    gui.importador.importarMercadologicoPorNiveis(opt.toArray(new OpcaoProduto[]{}));
                } else {
                    gui.importador.importarMercadologico(opt.toArray(new OpcaoProduto[]{}));
                }
            }
        }
        
    };
    private boolean utilizarVersao2 = false;

    public void setParametrosExtras(List<OpcaoProduto> parametrosExtras) {
        this.parametrosExtras = parametrosExtras;
    }

    public List<OpcaoProduto> getParametrosExtras() {
        return parametrosExtras;
    }

    public void setImportadorMercadologico(ImportAction importadorMercadologico) {
        this.importadorMercadologico = importadorMercadologico;
    }

    public void setImportador(Importador importador) {
        this.importador = importador;
    }

    public void setOpcoesDisponiveis(InterfaceDAO dao) {
        this.opt = dao.getOpcoesDisponiveisProdutos();
        tabImportacao.removeAll();
        tabParametros.removeAll();
                
        chkManterBalanca.setVisible(opt.contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA));
        chkResetarCodigoBalanca.setVisible(opt.contains(OpcaoProduto.IMPORTAR_RESETAR_BALANCA));
        chkNaoTransformarEANemUN.setVisible(opt.contains(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN));
        chkAliquotaCompleta.setVisible(opt.contains(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA));
        chkManterEANsMenores.setVisible(opt.contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS));
                
        if (
                opt.contains(OpcaoProduto.MERCADOLOGICO_PRODUTO) ||
                opt.contains(OpcaoProduto.MERCADOLOGICO) ||
                opt.contains(OpcaoProduto.FAMILIA) ||
                opt.contains(OpcaoProduto.FAMILIA_PRODUTO)
        ) {
            chkMercadologico.setVisible(opt.contains(OpcaoProduto.MERCADOLOGICO) || opt.contains(OpcaoProduto.MERCADOLOGICO_POR_NIVEL));
            chkProdMercadologico.setVisible(opt.contains(OpcaoProduto.MERCADOLOGICO_PRODUTO));
            chkMercadologicoPorNivelReplicar.setVisible(opt.contains(OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR));
            if (chkMercadologico.isVisible()) {
                chkMercadologicoNaoExcluir.setVisible(opt.contains(OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR));                
                if (
                        opt.contains(OpcaoProduto.MERCADOLOGICO_POR_NIVEL) ||
                        opt.contains(OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR) ||
                        opt.contains(OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR)
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
                opt.contains(OpcaoProduto.ESTOQUE_MINIMO) ||
                opt.contains(OpcaoProduto.ESTOQUE_MAXIMO) ||
                opt.contains(OpcaoProduto.ATIVO) ||
                opt.contains(OpcaoProduto.DESCONTINUADO) ||
                opt.contains(OpcaoProduto.ATACADO) ||
                opt.contains(OpcaoProduto.OFERTA) ||
                opt.contains(OpcaoProduto.MARGEM) ||
                opt.contains(OpcaoProduto.TIPO_PRODUTO) ||
                opt.contains(OpcaoProduto.FABRICANTE) ||
                opt.contains(OpcaoProduto.FABRICACAO_PROPRIA) ||
                opt.contains(OpcaoProduto.NORMA_REPOSICAO)
        ) {
            chkPreco.setVisible(opt.contains(OpcaoProduto.PRECO));
            chkCusto.setVisible(opt.contains(OpcaoProduto.CUSTO));
            chkCustoComImposto.setVisible(opt.contains(OpcaoProduto.CUSTO_COM_IMPOSTO));
            chkCustoSemImposto.setVisible(opt.contains(OpcaoProduto.CUSTO_SEM_IMPOSTO));
            chkEstoque.setVisible(opt.contains(OpcaoProduto.ESTOQUE));
            chkEstoqueMinimo.setVisible(opt.contains(OpcaoProduto.ESTOQUE_MINIMO));
            chkEstoqueMaximo.setVisible(opt.contains(OpcaoProduto.ESTOQUE_MAXIMO));
            chkSituacaoCadastro.setVisible(opt.contains(OpcaoProduto.ATIVO));
            chkDescontinuado.setVisible(opt.contains(OpcaoProduto.DESCONTINUADO));
            chkAtacado.setVisible(opt.contains(OpcaoProduto.ATACADO));
            chkVrAtacado.setVisible(opt.contains(OpcaoProduto.VR_ATACADO));
            chkMargemMinima.setVisible(opt.contains(OpcaoProduto.MARGEM_MINIMA));
            chkMargem.setVisible(opt.contains(OpcaoProduto.MARGEM));
            chkOferta.setVisible(opt.contains(OpcaoProduto.OFERTA));
            chkTipoProduto.setVisible(opt.contains(OpcaoProduto.TIPO_PRODUTO));
            chkFabricacaoPropria.setVisible(opt.contains(OpcaoProduto.FABRICACAO_PROPRIA));
            chkFabricante.setVisible(opt.contains(OpcaoProduto.FABRICANTE));
            chkNormaReposicao.setVisible(opt.contains(OpcaoProduto.NORMA_REPOSICAO));
            chkSecao.setVisible(opt.contains(OpcaoProduto.SECAO));
            chkPrateleira.setVisible(opt.contains(OpcaoProduto.PRATELEIRA));
            tabImportacao.add(pnlImpCompl);
        }
        
        if (
                opt.contains(OpcaoProduto.PIS_COFINS) ||
                opt.contains(OpcaoProduto.NATUREZA_RECEITA) ||
                opt.contains(OpcaoProduto.ICMS) ||
                opt.contains(OpcaoProduto.ICMS_SAIDA) ||
                opt.contains(OpcaoProduto.ICMS_SAIDA_FORA_ESTADO) ||
                opt.contains(OpcaoProduto.ICMS_ENTRADA) ||
                opt.contains(OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO) ||
                opt.contains(OpcaoProduto.ICMS_CONSUMIDOR) ||
                opt.contains(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA) ||
                opt.contains(OpcaoProduto.NCM) ||
                opt.contains(OpcaoProduto.CEST) ||
                opt.contains(OpcaoProduto.CODIGO_BENEFICIO) 
        ) {
            chkPisCofins.setVisible(opt.contains(OpcaoProduto.PIS_COFINS));
            chkNatReceita.setVisible(opt.contains(OpcaoProduto.NATUREZA_RECEITA));
            chkICMS.setVisible(opt.contains(OpcaoProduto.ICMS));
            chkIcmsDebito.setVisible(opt.contains(OpcaoProduto.ICMS_SAIDA));
            chkIcmsDebitoForaEstado.setVisible(opt.contains(OpcaoProduto.ICMS_SAIDA_FORA_ESTADO));
            chkIcmsCredito.setVisible(opt.contains(OpcaoProduto.ICMS_ENTRADA));
            chkIcmsCreditoForaEstado.setVisible(opt.contains(OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO));
            chkIcmsConsumidor.setVisible(opt.contains(OpcaoProduto.ICMS_CONSUMIDOR));
            btnMapaTribut.setVisible(chkICMS.isVisible());
            chkNcm.setVisible(opt.contains(OpcaoProduto.NCM));
            chkCest.setVisible(opt.contains(OpcaoProduto.CEST));
            chkCodigoBeneficio.setVisible(opt.contains(OpcaoProduto.CODIGO_BENEFICIO));
            chkCopiarIcmsDebitoNaEntrada.setVisible(opt.contains(OpcaoProduto.IMPORTAR_COPIAR_ICMS_DEBITO_NO_CREDITO));
            tabImportacao.add(pnlImpTributacao);
            if (opt.contains(OpcaoProduto.PAUTA_FISCAL)) {
                tabParametros.add(pnlOptPautaFiscal);
            }
            if (opt.contains(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA)||
                    opt.contains(OpcaoProduto.IMPORTAR_COPIAR_ICMS_DEBITO_NO_CREDITO)) {
                tabParametros.add(pnlOptIcms);
            }
        }
        
        if (
                opt.contains(OpcaoProduto.PAUTA_FISCAL_PRODUTO) ||
                opt.contains(OpcaoProduto.PAUTA_FISCAL)
                ) {
            
            chkPautaFiscal.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL_PRODUTO));
            chkPautaFiscalProduto.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL));
            chkPfIcmsCredito.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL));
            chkPfIcmsCreditoForaEst.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL));
            chkPfIcmsDebito.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL));
            chkPfIcmsDebitoForaEst.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL));
            chkPfIcmsIva.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL));
            chkPfIcmsIvaAjustado.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL));
            chkPfIcmsTipoIva.setVisible(opt.contains(OpcaoProduto.PAUTA_FISCAL));
            tabImportacao.add(pnlImpPautaFiscal);
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
                opt.contains(OpcaoProduto.VENDA_PDV) ||
                opt.contains(OpcaoProduto.PESO_BRUTO) ||
                opt.contains(OpcaoProduto.PESO_LIQUIDO) ||
                opt.contains(OpcaoProduto.VOLUME_TIPO_EMBALAGEM) ||
                opt.contains(OpcaoProduto.VOLUME_QTD) ||
                opt.contains(OpcaoProduto.VENDA_CONTROLADA)
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
            chkPesoBruto.setVisible(opt.contains(OpcaoProduto.PESO_BRUTO));
            chkPesoLiquido.setVisible(opt.contains(OpcaoProduto.PESO_LIQUIDO));
            chkVolumeEmbalagem.setVisible(opt.contains(OpcaoProduto.VOLUME_TIPO_EMBALAGEM));
            chkVolumeQtd.setVisible(opt.contains(OpcaoProduto.VOLUME_QTD));
            chkVendaControlada.setVisible(opt.contains(OpcaoProduto.VENDA_CONTROLADA));
            tabImportacao.add(pnlImpInfoAdic);
        }
        
        if (
                opt.contains(OpcaoProduto.ASSOCIADO) ||
                opt.contains(OpcaoProduto.COMPRADOR) ||
                opt.contains(OpcaoProduto.COMPRADOR_PRODUTO) ||
                opt.contains(OpcaoProduto.RECEITA_BALANCA) ||
                opt.contains(OpcaoProduto.INVENTARIO) ||
                opt.contains(OpcaoProduto.NUTRICIONAL) ||
                opt.contains(OpcaoProduto.RECEITA) ||
                opt.contains(OpcaoProduto.DIVISAO) ||
                opt.contains(OpcaoProduto.DIVISAO_PRODUTO)
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
            chkInventario.setVisible(opt.contains(OpcaoProduto.INVENTARIO));
            chkReceitaProduto.setVisible(opt.contains(OpcaoProduto.RECEITA));
            chkDivisao.setVisible(opt.contains(OpcaoProduto.DIVISAO));
            chkDivisaoProduto.setVisible(opt.contains(OpcaoProduto.DIVISAO_PRODUTO));
            tabImportacao.add(pnlImpOutrosDados);
        }
        
        tabImportacao.revalidate();
        
    }

    public Set<OpcaoProduto> getOpcoesDisponiveis() {
        return opt;
    }
    
    public void setProvider(MapaTributacaoButtonProvider provider) {
        btnMapaTribut.setProvider(provider);
        btnMapaTribut.setEnabled(provider != null);
    }
    
    /**
     * Creates new form ChecksProdutoPanelGUI
     */
    public ChecksProdutoPanelGUI() {
        super();
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
        vRCheckBox3 = new vrframework.bean.checkBox.VRCheckBox();
        scrollParametros = new javax.swing.JScrollPane();
        tabParametros = new vrframework.bean.panel.VRPanel();
        pnlOptMercadologico = new vrframework.bean.panel.VRPanel();
        jLabel3 = new javax.swing.JLabel();
        chkMercadologicoPorNivelReplicar = new vrframework.bean.checkBox.VRCheckBox();
        chkMercadologicoNaoExcluir = new vrframework.bean.checkBox.VRCheckBox();
        pnlOptProduto = new vrframework.bean.panel.VRPanel();
        chkManterBalanca = new vrframework.bean.checkBox.VRCheckBox();
        jLabel1 = new javax.swing.JLabel();
        chkNaoTransformarEANemUN = new vrframework.bean.checkBox.VRCheckBox();
        chkSomarEstoqueAoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkResetarCodigoBalanca = new vrframework.bean.checkBox.VRCheckBox();
        chkManterEANsMenores = new vrframework.bean.checkBox.VRCheckBox();
        btnMapaTribut = new vrimplantacao2.gui.component.mapatributacao.mapatributacaobutton.MapaTributacaoButton();
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
        pnlOptIcms = new vrframework.bean.panel.VRPanel();
        jLabel11 = new javax.swing.JLabel();
        chkAliquotaCompleta = new vrframework.bean.checkBox.VRCheckBox();
        chkCopiarIcmsDebitoNaEntrada = new vrframework.bean.checkBox.VRCheckBox();
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
        chkTipoProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkFabricacaoPropria = new vrframework.bean.checkBox.VRCheckBox();
        chkMargemMinima = new vrframework.bean.checkBox.VRCheckBox();
        chkEstoqueMinimo = new vrframework.bean.checkBox.VRCheckBox();
        chkEstoqueMaximo = new vrframework.bean.checkBox.VRCheckBox();
        chkNormaReposicao = new vrframework.bean.checkBox.VRCheckBox();
        jLabel7 = new javax.swing.JLabel();
        chkVrAtacado = new vrframework.bean.checkBox.VRCheckBox();
        chkSecao = new vrframework.bean.checkBox.VRCheckBox();
        chkPrateleira = new vrframework.bean.checkBox.VRCheckBox();
        pnlImpTributacao = new vrframework.bean.panel.VRPanel();
        jLabel4 = new javax.swing.JLabel();
        chkPisCofins = new vrframework.bean.checkBox.VRCheckBox();
        chkNatReceita = new vrframework.bean.checkBox.VRCheckBox();
        chkICMS = new vrframework.bean.checkBox.VRCheckBox();
        chkNcm = new vrframework.bean.checkBox.VRCheckBox();
        chkCest = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsDebito = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsCredito = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsDebitoForaEstado = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsCreditoForaEstado = new vrframework.bean.checkBox.VRCheckBox();
        chkIcmsConsumidor = new vrframework.bean.checkBox.VRCheckBox();
        chkCodigoBeneficio = new vrframework.bean.checkBox.VRCheckBox();
        pnlImpPautaFiscal = new vrframework.bean.panel.VRPanel();
        jLabel12 = new javax.swing.JLabel();
        chkPautaFiscal = new vrframework.bean.checkBox.VRCheckBox();
        chkPautaFiscalProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkPfIcmsCredito = new vrframework.bean.checkBox.VRCheckBox();
        chkPfIcmsCreditoForaEst = new vrframework.bean.checkBox.VRCheckBox();
        chkPfIcmsDebito = new vrframework.bean.checkBox.VRCheckBox();
        chkPfIcmsDebitoForaEst = new vrframework.bean.checkBox.VRCheckBox();
        chkPfIcmsIva = new vrframework.bean.checkBox.VRCheckBox();
        chkPfIcmsIvaAjustado = new vrframework.bean.checkBox.VRCheckBox();
        chkPfIcmsTipoIva = new vrframework.bean.checkBox.VRCheckBox();
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
        chkFabricante = new vrframework.bean.checkBox.VRCheckBox();
        chkPesoBruto = new vrframework.bean.checkBox.VRCheckBox();
        chkPesoLiquido = new vrframework.bean.checkBox.VRCheckBox();
        chkVolumeEmbalagem = new vrframework.bean.checkBox.VRCheckBox();
        chkVolumeQtd = new vrframework.bean.checkBox.VRCheckBox();
        chkVendaControlada = new vrframework.bean.checkBox.VRCheckBox();
        pnlImpOutrosDados = new vrframework.bean.panel.VRPanel();
        jLabel9 = new javax.swing.JLabel();
        chkAssociado = new vrframework.bean.checkBox.VRCheckBox();
        chkComprador = new vrframework.bean.checkBox.VRCheckBox();
        chkCompradorProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkReceitaToledo = new vrframework.bean.checkBox.VRCheckBox();
        chkReceitaFilizola = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalToledo = new vrframework.bean.checkBox.VRCheckBox();
        chkNutricionalFilizola = new vrframework.bean.checkBox.VRCheckBox();
        chkInventario = new vrframework.bean.checkBox.VRCheckBox();
        chkReceitaProduto = new vrframework.bean.checkBox.VRCheckBox();
        chkDivisao = new vrframework.bean.checkBox.VRCheckBox();
        chkDivisaoProduto = new vrframework.bean.checkBox.VRCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(vRCheckBox3, "vRCheckBox3");

        scrollParametros.setBorder(null);
        scrollParametros.setPreferredSize(new java.awt.Dimension(593, 219));

        tabParametros.setPreferredSize(new java.awt.Dimension(300, 219));
        tabParametros.setLayout(new org.jdesktop.swingx.VerticalLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "MERCADOLÓGICO");

        org.openide.awt.Mnemonics.setLocalizedText(chkMercadologicoPorNivelReplicar, "Replicar Subníveis");
        chkMercadologicoPorNivelReplicar.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkMercadologicoNaoExcluir, "Não Excluir ao importar");
        chkMercadologicoNaoExcluir.setToolTipText("Não executa a rotina de exclusão dos mercadológicos anteriores durante a importação do mercadológico");
        chkMercadologicoNaoExcluir.setEnabled(true);

        javax.swing.GroupLayout pnlOptMercadologicoLayout = new javax.swing.GroupLayout(pnlOptMercadologico);
        pnlOptMercadologico.setLayout(pnlOptMercadologicoLayout);
        pnlOptMercadologicoLayout.setHorizontalGroup(
            pnlOptMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptMercadologicoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMercadologicoPorNivelReplicar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMercadologicoNaoExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(196, Short.MAX_VALUE))
        );
        pnlOptMercadologicoLayout.setVerticalGroup(
            pnlOptMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptMercadologicoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlOptMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOptMercadologicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkMercadologicoPorNivelReplicar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkMercadologicoNaoExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabParametros.add(pnlOptMercadologico);

        org.openide.awt.Mnemonics.setLocalizedText(chkManterBalanca, "Manter código PLU dos produtos de balança");
        chkManterBalanca.setEnabled(true);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "PRODUTOS");
        jLabel1.setPreferredSize(new java.awt.Dimension(132, 14));

        org.openide.awt.Mnemonics.setLocalizedText(chkNaoTransformarEANemUN, "Não transformar produtos com EAN válido em unitário");
        chkNaoTransformarEANemUN.setToolTipText("<html>\nEm alguns sistemas o produto pode ser vendido tanto pelo EAN13 quanto na balança.<br>\nIsso para o VR pode causar problemas, por essa razão o VRImplantação trata esse<br>\nproduto com EAN e o converte em unitário.<br>\n<br>\n<b>Ao marcar esta opção, o sistema ignora o EAN e fixa o que for passado como unidade.</b>\n</html>");
        chkNaoTransformarEANemUN.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkSomarEstoqueAoProduto, "Somar o estoque do produto");
        chkSomarEstoqueAoProduto.setToolTipText("<html>\nAo contrario do procedimento normal de atualização do estoque onde o valor dele é substítuido<br>\npelo importado, ao marcar esta opção o valor importado serásomado ao estoque atual.\n</html>");
        chkSomarEstoqueAoProduto.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkResetarCodigoBalanca, "Resetar Código Balança");

        org.openide.awt.Mnemonics.setLocalizedText(chkManterEANsMenores, "Manter EANs menores");
        chkManterEANsMenores.setToolTipText("<html>\nMantem os EANs menores que <b>7</> dígitos nos produtos unitários.<br>\n<i>Diferentemente da opção \"Manter código PLU dos produtos de balança\", <br>\nessa rotina não muda o código interno dos produtos unitário.</i>\n</html>");
        chkManterEANsMenores.setEnabled(true);

        javax.swing.GroupLayout pnlOptProdutoLayout = new javax.swing.GroupLayout(pnlOptProduto);
        pnlOptProduto.setLayout(pnlOptProdutoLayout);
        pnlOptProdutoLayout.setHorizontalGroup(
            pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                        .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkResetarCodigoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(120, Short.MAX_VALUE))
                    .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                        .addGroup(pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                                .addComponent(chkSomarEstoqueAoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkManterEANsMenores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnMapaTribut, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chkNaoTransformarEANemUN, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        pnlOptProdutoLayout.setVerticalGroup(
            pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5))
                    .addGroup(pnlOptProdutoLayout.createSequentialGroup()
                        .addGroup(pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkManterBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkResetarCodigoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMapaTribut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkNaoTransformarEANemUN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlOptProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkSomarEstoqueAoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkManterEANsMenores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                .addContainerGap(259, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(5, 5, 5))
        );

        tabParametros.add(pnlOptPautaFiscal);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, "ICMS");
        jLabel11.setPreferredSize(new java.awt.Dimension(132, 14));

        org.openide.awt.Mnemonics.setLocalizedText(chkAliquotaCompleta, "Usar conversão de aliquota completa");
        chkAliquotaCompleta.setToolTipText("Selecione essa opção para que ao importar o associado, seja gerado também o associado para preço e custo.");

        org.openide.awt.Mnemonics.setLocalizedText(chkCopiarIcmsDebitoNaEntrada, "Copiar ICMS débito na entrada");
        chkCopiarIcmsDebitoNaEntrada.setToolTipText("Marque está opção quando o cliente utilizar a margem bruta do GetWay para calcular seus preços");
        chkCopiarIcmsDebitoNaEntrada.setEnabled(true);

        javax.swing.GroupLayout pnlOptIcmsLayout = new javax.swing.GroupLayout(pnlOptIcms);
        pnlOptIcms.setLayout(pnlOptIcmsLayout);
        pnlOptIcmsLayout.setHorizontalGroup(
            pnlOptIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptIcmsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlOptIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCopiarIcmsDebitoNaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAliquotaCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(250, Short.MAX_VALUE))
        );
        pnlOptIcmsLayout.setVerticalGroup(
            pnlOptIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptIcmsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlOptIcmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAliquotaCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCopiarIcmsDebitoNaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabParametros.add(pnlOptIcms);

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
                .addGap(5, 5, 5))
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
                .addGap(5, 5, 5))
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

        org.openide.awt.Mnemonics.setLocalizedText(chkTipoProduto, "Tipo Produto");

        org.openide.awt.Mnemonics.setLocalizedText(chkFabricacaoPropria, "Fabricação Própria");

        org.openide.awt.Mnemonics.setLocalizedText(chkMargemMinima, "Margem Mínima");

        org.openide.awt.Mnemonics.setLocalizedText(chkEstoqueMinimo, "Estoque Mínimo");

        org.openide.awt.Mnemonics.setLocalizedText(chkEstoqueMaximo, "Estoque Máximo");

        org.openide.awt.Mnemonics.setLocalizedText(chkNormaReposicao, "Norma Reposição");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, "COMPLEMENTO (POR LOJA)");

        org.openide.awt.Mnemonics.setLocalizedText(chkVrAtacado, "VR Atacado");

        org.openide.awt.Mnemonics.setLocalizedText(chkSecao, "Seção");

        org.openide.awt.Mnemonics.setLocalizedText(chkPrateleira, "Prateleira");

        javax.swing.GroupLayout pnlImpComplLayout = new javax.swing.GroupLayout(pnlImpCompl);
        pnlImpCompl.setLayout(pnlImpComplLayout);
        pnlImpComplLayout.setHorizontalGroup(
            pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpComplLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImpComplLayout.createSequentialGroup()
                        .addComponent(chkPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkCustoSemImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkCustoComImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlImpComplLayout.createSequentialGroup()
                        .addComponent(chkDescontinuado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkVrAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkTipoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkFabricacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlImpComplLayout.createSequentialGroup()
                        .addComponent(chkMargemMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkEstoqueMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkEstoqueMaximo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(chkNormaReposicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkSecao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPrateleira, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7))
                .addGap(5, 5, 5))
        );
        pnlImpComplLayout.setVerticalGroup(
            pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpComplLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCustoSemImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCustoComImposto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkDescontinuado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOferta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkVrAtacado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkTipoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFabricacaoPropria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkMargemMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEstoqueMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEstoqueMaximo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlImpComplLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkNormaReposicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkSecao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkPrateleira, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        org.openide.awt.Mnemonics.setLocalizedText(chkIcmsDebito, "ICMS Débito");

        org.openide.awt.Mnemonics.setLocalizedText(chkIcmsCredito, "ICMS Crédio");

        org.openide.awt.Mnemonics.setLocalizedText(chkIcmsDebitoForaEstado, "ICMS Débito Fora Estado");

        org.openide.awt.Mnemonics.setLocalizedText(chkIcmsCreditoForaEstado, "ICMS Crédito Fora Estado");

        org.openide.awt.Mnemonics.setLocalizedText(chkIcmsConsumidor, "ICMS Consumidor");

        org.openide.awt.Mnemonics.setLocalizedText(chkCodigoBeneficio, "Código de Benefício");

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
                        .addGroup(pnlImpTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlImpTributacaoLayout.createSequentialGroup()
                                .addComponent(chkPisCofins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkNatReceita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkICMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkIcmsDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkIcmsCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkIcmsDebitoForaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlImpTributacaoLayout.createSequentialGroup()
                                .addComponent(chkIcmsCreditoForaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkIcmsConsumidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkNcm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkCest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkCodigoBeneficio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
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
                    .addComponent(chkIcmsDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIcmsCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIcmsDebitoForaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpTributacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkNcm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIcmsCreditoForaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIcmsConsumidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCodigoBeneficio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        tabImportacao.add(pnlImpTributacao);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, "PAUTA FISCAL");

        org.openide.awt.Mnemonics.setLocalizedText(chkPautaFiscal, "Pauta Fiscal");

        org.openide.awt.Mnemonics.setLocalizedText(chkPautaFiscalProduto, "Pauta Fiscal X Produto");

        org.openide.awt.Mnemonics.setLocalizedText(chkPfIcmsCredito, "ICMS Crédito");

        org.openide.awt.Mnemonics.setLocalizedText(chkPfIcmsCreditoForaEst, "ICMS Crédito Fora Est.");

        org.openide.awt.Mnemonics.setLocalizedText(chkPfIcmsDebito, "ICMS Débito");

        org.openide.awt.Mnemonics.setLocalizedText(chkPfIcmsDebitoForaEst, "ICMS Débito Fora Est.");

        org.openide.awt.Mnemonics.setLocalizedText(chkPfIcmsIva, "IVA");

        org.openide.awt.Mnemonics.setLocalizedText(chkPfIcmsIvaAjustado, "IVA Ajustado");

        org.openide.awt.Mnemonics.setLocalizedText(chkPfIcmsTipoIva, "Tipo IVA");

        javax.swing.GroupLayout pnlImpPautaFiscalLayout = new javax.swing.GroupLayout(pnlImpPautaFiscal);
        pnlImpPautaFiscal.setLayout(pnlImpPautaFiscalLayout);
        pnlImpPautaFiscalLayout.setHorizontalGroup(
            pnlImpPautaFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpPautaFiscalLayout.createSequentialGroup()
                .addGroup(pnlImpPautaFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImpPautaFiscalLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel12))
                    .addGroup(pnlImpPautaFiscalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkPautaFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkPautaFiscalProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPfIcmsCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPfIcmsCreditoForaEst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPfIcmsDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlImpPautaFiscalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkPfIcmsDebitoForaEst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPfIcmsIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPfIcmsIvaAjustado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPfIcmsTipoIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlImpPautaFiscalLayout.setVerticalGroup(
            pnlImpPautaFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpPautaFiscalLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpPautaFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPautaFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPautaFiscalProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPfIcmsCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPfIcmsCreditoForaEst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPfIcmsDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImpPautaFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPfIcmsDebitoForaEst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPfIcmsIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPfIcmsIvaAjustado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPfIcmsTipoIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabImportacao.add(pnlImpPautaFiscal);

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

        org.openide.awt.Mnemonics.setLocalizedText(chkFabricante, "Fabricante");

        org.openide.awt.Mnemonics.setLocalizedText(chkPesoBruto, "Peso Bruto");

        org.openide.awt.Mnemonics.setLocalizedText(chkPesoLiquido, "Peso Líquido");

        org.openide.awt.Mnemonics.setLocalizedText(chkVolumeEmbalagem, "Emb. Volume");

        org.openide.awt.Mnemonics.setLocalizedText(chkVolumeQtd, "Qtd. Volume");

        org.openide.awt.Mnemonics.setLocalizedText(chkVendaControlada, "Venda Controlada");
        chkVendaControlada.setToolTipText("Produtos alcoólicos ou de controle especial");

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
                        .addComponent(chkVendaPdv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFabricante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPesoBruto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPesoLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkVolumeEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkVolumeQtd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlImpInfoAdicLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkVendaControlada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
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
                .addGroup(pnlImpInfoAdicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkVendaPdv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFabricante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPesoBruto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlImpInfoAdicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkPesoLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkVolumeEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkVolumeQtd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkVendaControlada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        org.openide.awt.Mnemonics.setLocalizedText(chkInventario, "Inventário");

        org.openide.awt.Mnemonics.setLocalizedText(chkReceitaProduto, "Receita (Produto)");

        org.openide.awt.Mnemonics.setLocalizedText(chkDivisao, "Divisão");

        org.openide.awt.Mnemonics.setLocalizedText(chkDivisaoProduto, "Divisao x Produto");

        javax.swing.GroupLayout pnlImpOutrosDadosLayout = new javax.swing.GroupLayout(pnlImpOutrosDados);
        pnlImpOutrosDados.setLayout(pnlImpOutrosDadosLayout);
        pnlImpOutrosDadosLayout.setHorizontalGroup(
            pnlImpOutrosDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImpOutrosDadosLayout.createSequentialGroup()
                .addGroup(pnlImpOutrosDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImpOutrosDadosLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(pnlImpOutrosDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(pnlImpOutrosDadosLayout.createSequentialGroup()
                                .addComponent(chkNutricionalToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkNutricionalFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkInventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkReceitaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkDivisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlImpOutrosDadosLayout.createSequentialGroup()
                                .addComponent(chkAssociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkComprador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkCompradorProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkReceitaToledo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkReceitaFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnlImpOutrosDadosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkDivisaoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
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
                    .addComponent(chkNutricionalFilizola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkInventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkReceitaProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDivisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDivisaoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
    public vrframework.bean.checkBox.VRCheckBox chkAliquotaCompleta;
    public vrframework.bean.checkBox.VRCheckBox chkAssociado;
    public vrframework.bean.checkBox.VRCheckBox chkAssociadoSomenteAtivos;
    public vrframework.bean.checkBox.VRCheckBox chkAtacado;
    public vrframework.bean.checkBox.VRCheckBox chkCest;
    public vrframework.bean.checkBox.VRCheckBox chkCodigoBeneficio;
    public vrframework.bean.checkBox.VRCheckBox chkComprador;
    public vrframework.bean.checkBox.VRCheckBox chkCompradorProduto;
    public vrframework.bean.checkBox.VRCheckBox chkCopiarIcmsDebitoNaEntrada;
    public vrframework.bean.checkBox.VRCheckBox chkCusto;
    public vrframework.bean.checkBox.VRCheckBox chkCustoComImposto;
    public vrframework.bean.checkBox.VRCheckBox chkCustoSemImposto;
    public vrframework.bean.checkBox.VRCheckBox chkDescCompleta;
    public vrframework.bean.checkBox.VRCheckBox chkDescGondola;
    public vrframework.bean.checkBox.VRCheckBox chkDescReduzida;
    public vrframework.bean.checkBox.VRCheckBox chkDescontinuado;
    public vrframework.bean.checkBox.VRCheckBox chkDivisao;
    public vrframework.bean.checkBox.VRCheckBox chkDivisaoProduto;
    public vrframework.bean.checkBox.VRCheckBox chkEAN;
    public vrframework.bean.checkBox.VRCheckBox chkEANemBranco;
    public vrframework.bean.checkBox.VRCheckBox chkEstoque;
    public vrframework.bean.checkBox.VRCheckBox chkEstoqueMaximo;
    public vrframework.bean.checkBox.VRCheckBox chkEstoqueMinimo;
    public vrframework.bean.checkBox.VRCheckBox chkFabricacaoPropria;
    public vrframework.bean.checkBox.VRCheckBox chkFabricante;
    public vrframework.bean.checkBox.VRCheckBox chkFamilia;
    public vrframework.bean.checkBox.VRCheckBox chkFamiliaProduto;
    public vrframework.bean.checkBox.VRCheckBox chkICMS;
    public vrframework.bean.checkBox.VRCheckBox chkIcmsConsumidor;
    public vrframework.bean.checkBox.VRCheckBox chkIcmsCredito;
    public vrframework.bean.checkBox.VRCheckBox chkIcmsCreditoForaEstado;
    public vrframework.bean.checkBox.VRCheckBox chkIcmsDebito;
    public vrframework.bean.checkBox.VRCheckBox chkIcmsDebitoForaEstado;
    public vrframework.bean.checkBox.VRCheckBox chkInventario;
    public vrframework.bean.checkBox.VRCheckBox chkInverterAssociado;
    public vrframework.bean.checkBox.VRCheckBox chkManterBalanca;
    public vrframework.bean.checkBox.VRCheckBox chkManterEANsMenores;
    public vrframework.bean.checkBox.VRCheckBox chkMargem;
    public vrframework.bean.checkBox.VRCheckBox chkMargemMinima;
    public vrframework.bean.checkBox.VRCheckBox chkMercadologico;
    public vrframework.bean.checkBox.VRCheckBox chkMercadologicoNaoExcluir;
    public vrframework.bean.checkBox.VRCheckBox chkMercadologicoPorNivelReplicar;
    public vrframework.bean.checkBox.VRCheckBox chkNaoTransformarEANemUN;
    public vrframework.bean.checkBox.VRCheckBox chkNatReceita;
    public vrframework.bean.checkBox.VRCheckBox chkNcm;
    public vrframework.bean.checkBox.VRCheckBox chkNormaReposicao;
    public vrframework.bean.checkBox.VRCheckBox chkNutricionalFilizola;
    public vrframework.bean.checkBox.VRCheckBox chkNutricionalToledo;
    public vrframework.bean.checkBox.VRCheckBox chkOferta;
    public vrframework.bean.checkBox.VRCheckBox chkPautaFiscal;
    public vrframework.bean.checkBox.VRCheckBox chkPautaFiscalProduto;
    public vrframework.bean.checkBox.VRCheckBox chkPautaUsarEansMenores;
    public vrframework.bean.checkBox.VRCheckBox chkPesoBruto;
    public vrframework.bean.checkBox.VRCheckBox chkPesoLiquido;
    public vrframework.bean.checkBox.VRCheckBox chkPfIcmsCredito;
    public vrframework.bean.checkBox.VRCheckBox chkPfIcmsCreditoForaEst;
    public vrframework.bean.checkBox.VRCheckBox chkPfIcmsDebito;
    public vrframework.bean.checkBox.VRCheckBox chkPfIcmsDebitoForaEst;
    public vrframework.bean.checkBox.VRCheckBox chkPfIcmsIva;
    public vrframework.bean.checkBox.VRCheckBox chkPfIcmsIvaAjustado;
    public vrframework.bean.checkBox.VRCheckBox chkPfIcmsTipoIva;
    public vrframework.bean.checkBox.VRCheckBox chkPisCofins;
    public vrframework.bean.checkBox.VRCheckBox chkPrateleira;
    public vrframework.bean.checkBox.VRCheckBox chkPreco;
    public vrframework.bean.checkBox.VRCheckBox chkProdMercadologico;
    public vrframework.bean.checkBox.VRCheckBox chkProdutos;
    public vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemEAN;
    public vrframework.bean.checkBox.VRCheckBox chkQtdEmbalagemProd;
    public vrframework.bean.checkBox.VRCheckBox chkReceitaFilizola;
    public vrframework.bean.checkBox.VRCheckBox chkReceitaProduto;
    public vrframework.bean.checkBox.VRCheckBox chkReceitaToledo;
    public vrframework.bean.checkBox.VRCheckBox chkResetarCodigoBalanca;
    public vrframework.bean.checkBox.VRCheckBox chkSecao;
    public vrframework.bean.checkBox.VRCheckBox chkSituacaoCadastro;
    public vrframework.bean.checkBox.VRCheckBox chkSomarEstoqueAoProduto;
    public vrframework.bean.checkBox.VRCheckBox chkSugestaoCotacao;
    public vrframework.bean.checkBox.VRCheckBox chkSugestaoPedido;
    public vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemEAN;
    public vrframework.bean.checkBox.VRCheckBox chkTipoEmbalagemProd;
    public vrframework.bean.checkBox.VRCheckBox chkTipoProduto;
    public vrframework.bean.checkBox.VRCheckBox chkValidade;
    public vrframework.bean.checkBox.VRCheckBox chkVendaControlada;
    public vrframework.bean.checkBox.VRCheckBox chkVendaPdv;
    public vrframework.bean.checkBox.VRCheckBox chkVolumeEmbalagem;
    public vrframework.bean.checkBox.VRCheckBox chkVolumeQtd;
    public vrframework.bean.checkBox.VRCheckBox chkVrAtacado;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel10;
    public javax.swing.JLabel jLabel11;
    public javax.swing.JLabel jLabel12;
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
    public vrframework.bean.panel.VRPanel pnlImpPautaFiscal;
    public vrframework.bean.panel.VRPanel pnlImpProduto;
    public vrframework.bean.panel.VRPanel pnlImpTributacao;
    public vrframework.bean.panel.VRPanel pnlOptAssociado;
    public vrframework.bean.panel.VRPanel pnlOptIcms;
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
    public vrframework.bean.checkBox.VRCheckBox vRCheckBox3;
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
        
        parametros.put(chkMercadologicoPorNivelReplicar.isSelected(), concat(params, "MERCADOLOGICO_POR_NIVEL_REPLICAR" ));
        parametros.put(chkManterBalanca.isSelected(), concat(params, "MANTER_PLU_BALANCA" ));
        parametros.put(chkInverterAssociado.isSelected(), concat(params, "INVERTER_ASSOCIADO" ));
        parametros.put(chkAssociadoSomenteAtivos.isSelected(), concat(params, "SOMENTES_ASSOCIADOS_DE_PRODUTOS_ATIVOS" ));
        parametros.put(chkManterEANsMenores.isSelected(), concat(params, "IMPORTAR_EAN_MENORES_QUE_7_DIGITOS" ));
        
        if (rdbPautaIdPauta.isSelected()) {
            parametros.put(1, concat(params, "PAUTA_OPCAO" ));
        } else if (rdbPautaIdProduto.isSelected()) {
            parametros.put(2, concat(params, "PAUTA_OPCAO" ));
        } else if (rdbPautaEan.isSelected()) {
            parametros.put(3, concat(params, "PAUTA_OPCAO" ));
        }
        parametros.put(chkPautaUsarEansMenores.isSelected(), concat(params, "PAUTA_USAR_EANS_MENORES" ));
        parametros.put(chkCopiarIcmsDebitoNaEntrada.isSelected(), concat(params, "COPIAR_DEBITO_NO_CREDITO" ));
        
    }

    public void carregarParametros(Parametros parametros, String... params) {
        
        chkMercadologicoPorNivelReplicar.setSelected(parametros.getBool(concat(params, "MERCADOLOGICO_POR_NIVEL_REPLICAR" )));
        chkManterBalanca.setSelected(parametros.getBool(concat(params, "MANTER_PLU_BALANCA" )));
        chkInverterAssociado.setSelected(parametros.getBool(concat(params, "INVERTER_ASSOCIADO" )));
        chkAssociadoSomenteAtivos.setSelected(parametros.getBool(concat(params, "SOMENTES_ASSOCIADOS_DE_PRODUTOS_ATIVOS" )));
        chkManterEANsMenores.setSelected(parametros.getBool(concat(params, "IMPORTAR_EAN_MENORES_QUE_7_DIGITOS")));
        
        switch (parametros.getInt(concat(params, "PAUTA_OPCAO" ))) {
            case 2: rdbPautaIdProduto.setSelected(true); break;
            case 3: rdbPautaEan.setSelected(true); break;
            default: rdbPautaIdPauta.setSelected(true); break;
        }
        rdbPautaEanActionPerformed(null);
        chkPautaUsarEansMenores.setSelected(parametros.getBool(concat(params, "PAUTA_USAR_EANS_MENORES" )));
        chkCopiarIcmsDebitoNaEntrada.setSelected(parametros.getBool(concat(params, "COPIAR_DEBITO_NO_CREDITO" )));
        
    }

    /**
     * Aciona o novo produto repository para fazer a importação dos produtos.
     * @param b 
     */
    public void setUtilizarVersao2(boolean utilizarVersao2) {
        this.utilizarVersao2 = utilizarVersao2;
    }
    
    public class ProdutoPanelImportador {
        public void importar() throws Exception {
            
            if (chkFamiliaProduto.isSelected()) {
                importador.importarFamiliaProduto();
            }
            
            importadorMercadologico.importarMercadologico();

            if (chkProdutos.isSelected()) {
                List<OpcaoProduto> opt = new ArrayList<>();
                if (chkNaoTransformarEANemUN.isSelected()) {
                    opt.add(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN);
                }
                if (chkManterBalanca.isSelected()) {
                    opt.add(OpcaoProduto.IMPORTAR_MANTER_BALANCA);
                }
                if (chkResetarCodigoBalanca.isSelected()) {
                    opt.add(OpcaoProduto.IMPORTAR_RESETAR_BALANCA);
                }
                if (chkManterEANsMenores.isSelected()) {
                    opt.add(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
                }            
                if (chkCopiarIcmsDebitoNaEntrada.isSelected()) {
                    opt.add(OpcaoProduto.IMPORTAR_COPIAR_ICMS_DEBITO_NO_CREDITO);
                }
                opt.addAll(getParametrosExtras());
                if (utilizarVersao2) {
                    importador.importarProdutoNovo(opt.toArray(new OpcaoProduto[]{}));
                } else {
                    importador.importarProduto(opt.toArray(new OpcaoProduto[]{}));
                }
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
                
                if (chkPfIcmsCredito.isSelected()) {
                    opcoes.add(OpcaoFiscal.ALIQUOTA_CREDITO);
                }
                if (chkPfIcmsCreditoForaEst.isSelected()) {
                    opcoes.add(OpcaoFiscal.ALIQUOTA_CREDITO_FORA_ESTADO);
                }
                if (chkPfIcmsDebito.isSelected()) {
                    opcoes.add(OpcaoFiscal.ALIQUOTA_DEBITO);
                }
                if (chkPfIcmsDebitoForaEst.isSelected()) {
                    opcoes.add(OpcaoFiscal.ALIQUOTA_DEBITO_FORA_ESTADO);
                }
                if (chkPfIcmsIva.isSelected()) {
                    opcoes.add(OpcaoFiscal.IVA);
                }
                if (chkPfIcmsIvaAjustado.isSelected()) {
                    opcoes.add(OpcaoFiscal.IVA_AJUSTADO);
                }
                if (chkPfIcmsTipoIva.isSelected()) {
                    opcoes.add(OpcaoFiscal.TIPO_IVA);
                }
                
                if (!opcoes.isEmpty()) {
                    importador.importarPautaFiscal(opcoes.toArray(new OpcaoFiscal[]{}));
                }
            }
            if (chkComprador.isSelected()) {
                importador.importarComprador();
            }
            if (chkDivisao.isSelected()) {
                importador.importarDivisoes();
            }

            {
                List<OpcaoProduto> opcoes = new ArrayList<>();
                if (chkSomarEstoqueAoProduto.isSelected()) {
                    opcoes.add(OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE);
                }
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
                if (chkEstoqueMinimo.isSelected()) {
                    opcoes.add(OpcaoProduto.ESTOQUE_MINIMO);
                }
                if (chkEstoqueMaximo.isSelected()) {
                    opcoes.add(OpcaoProduto.ESTOQUE_MAXIMO);
                }
                if (chkEstoque.isSelected()) {
                    opcoes.add(OpcaoProduto.ESTOQUE);
                }
                if (chkSecao.isSelected()) {
                    opcoes.add(OpcaoProduto.SECAO);
                }
                if (chkPrateleira.isSelected()) {
                    opcoes.add(OpcaoProduto.PRATELEIRA);
                }
                if (chkPisCofins.isSelected()) {
                    opcoes.add(OpcaoProduto.PIS_COFINS);
                }
                if (chkNatReceita.isSelected()) {
                    opcoes.add(OpcaoProduto.NATUREZA_RECEITA);
                }
                if (chkAliquotaCompleta.isSelected()) {
                    opcoes.add(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA);
                }
                if (chkICMS.isSelected()) {
                    opcoes.add(OpcaoProduto.ICMS);
                }
                if (chkIcmsDebito.isSelected()) {
                    opcoes.add(OpcaoProduto.ICMS_SAIDA);
                }
                if (chkIcmsCredito.isSelected()) {
                    opcoes.add(OpcaoProduto.ICMS_ENTRADA);
                }
                if (chkIcmsDebitoForaEstado.isSelected()) {
                    opcoes.add(OpcaoProduto.ICMS_SAIDA_FORA_ESTADO);
                }
                if (chkIcmsCreditoForaEstado.isSelected()) {
                    opcoes.add(OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO);
                }
                if (chkIcmsConsumidor.isSelected()) {
                    opcoes.add(OpcaoProduto.ICMS_CONSUMIDOR);
                }
                if (chkCodigoBeneficio.isSelected()) {
                    opcoes.add(OpcaoProduto.CODIGO_BENEFICIO);
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
                if (chkMargemMinima.isSelected()) {
                    opcoes.add(OpcaoProduto.MARGEM_MINIMA);
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
                if (chkVrAtacado.isSelected()) {
                    opcoes.add(OpcaoProduto.VR_ATACADO);
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
                if (chkTipoProduto.isSelected()) {
                    opcoes.add(OpcaoProduto.TIPO_PRODUTO);
                }
                if (chkFabricacaoPropria.isSelected()) {
                    opcoes.add(OpcaoProduto.FABRICACAO_PROPRIA);
                }
                if (chkFabricante.isSelected()) {
                    opcoes.add(OpcaoProduto.FABRICANTE);
                }
                if (chkNaoTransformarEANemUN.isSelected()) {
                    opt.add(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN);
                }
                if (chkPesoBruto.isSelected()) {
                    opcoes.add(OpcaoProduto.PESO_BRUTO);
                }
                if (chkPesoLiquido.isSelected()) {
                    opcoes.add(OpcaoProduto.PESO_LIQUIDO);
                }
                if (chkDivisaoProduto.isSelected()) {
                    opcoes.add(OpcaoProduto.DIVISAO_PRODUTO);
                }
                if (chkVolumeEmbalagem.isSelected()) {
                    opcoes.add(OpcaoProduto.VOLUME_TIPO_EMBALAGEM);
                }
                if (chkVolumeQtd.isSelected()) {
                    opcoes.add(OpcaoProduto.VOLUME_QTD);
                }            
                if (chkCopiarIcmsDebitoNaEntrada.isSelected()) {
                    opcoes.add(OpcaoProduto.IMPORTAR_COPIAR_ICMS_DEBITO_NO_CREDITO);
                }
                if (chkNormaReposicao.isSelected()) {
                    opcoes.add(OpcaoProduto.NORMA_REPOSICAO);
                }
                if (chkVendaControlada.isSelected()) {
                    opcoes.add(OpcaoProduto.VENDA_CONTROLADA);
                }
                opcoes.addAll(getParametrosExtras());
                if (!opcoes.isEmpty()) {
                    if (importador.getInterfaceDAO().getOpcoesDisponiveisProdutos().contains(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA)) {
                        opcoes.add(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA);
                    }
                    if (utilizarVersao2) {
                        importador.importarProdutoNovo(opcoes.toArray(new OpcaoProduto[]{}));
                    } else {
                        importador.atualizarProdutos(opcoes);
                    }
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
            
            if (chkInventario.isSelected()) {
                importador.importarInventario();
            }
            
            if (chkReceitaProduto.isSelected()) {
                importador.importarReceitas();
            }
        }
        

        
    }
    
    /**
     * Utilize esta classe para customizar o acionamento de importações.
     */
    public abstract static class ImportAction {

        protected final ChecksProdutoPanelGUI gui;

        public ImportAction(ChecksProdutoPanelGUI gui) {
            this.gui = gui;
        }

        public abstract void importarMercadologico() throws Exception;

    }
   
}
