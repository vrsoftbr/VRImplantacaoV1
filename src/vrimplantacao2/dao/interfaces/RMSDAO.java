package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.PagarOutrasDespesasDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVencimentoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemAnteriorDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemDAO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.venda.VendaHistoricoIMP;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.notafiscal.TipoNota;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoDestinatario;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;
import vrimplantacao2.vo.importacao.NotaFiscalItemIMP;
import vrimplantacao2.vo.importacao.NotaOperacao;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/*
 *
 * @author Leandro, Guilherme
 * Para localizar tabelas do RMS, utilizar o sistema RMS Log Viewer, 
 * o mesmo gera os scripts e nomes da tabela, caso necessário.
 * Site com manual do sistema: https://tdn.totvs.com/display/public/LRMS/Manual+de+Extrato+de+Itens
*/
public class RMSDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(RMSDAO.class.getName());
    public static String tabela_venda = "";
    public static int digito;
    
    private boolean utilizarViewMixFiscal = true;
    
    private int versaoDaVenda;
    
    private boolean somenteAtivos = false;

    public void setSomenteAtivos(boolean somenteAtivos) {
        this.somenteAtivos = somenteAtivos;
    }

    public void setUtilizarViewMixFiscal(boolean utilizarViewMixFiscal) {
        this.utilizarViewMixFiscal = utilizarViewMixFiscal;
    }

    public void setVersaoDaVenda(int versaoDaVenda) {
        this.versaoDaVenda = versaoDaVenda;
    }
    
    @Override
    public String getSistema() {
        return "RMS";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    tab_acesso,\n" +
                    "    tab_conteudo\n" +
                    "from\n" +
                    "    AA2CTABE\n" +
                    "where\n" +
                    "    tab_codigo = 3\n" +
                    "order by\n" +
                    "    tab_acesso"
            )) {
                while (rst.next()) {
                    if (Utils.stringToInt(rst.getString("tab_acesso")) > 0) {
                        String id = String.valueOf(Utils.stringToInt(rst.getString("tab_acesso")));
                        result.add(
                                MapaTributoIMP.make(id, rst.getString("tab_conteudo"))
                        );
                    }
                }
            }
        }
        
        return result;
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select \n" +
                "    loj_codigo id,\n" +
                "    loj_digito dig\n" +
                "from\n" +
                "    AA2CLOJA\n" +
                "order by\n" +
                "    loj_codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id") + rst.getString("dig"), "LOJA " + rst.getString("id")));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.git_cod_item||p.git_digito id,\n" +
                    "	p.git_descricao descricao\n" +
                    "from\n" +
                    "	AA3CITEM p\n" +
                    "where\n" +
                    "	p.git_cod_item||p.git_digito in (	  \n" +
                    "		select distinct\n" +
                    "			it_pai\n" +
                    "		from\n" +
                    "			AA1CHELI\n" +
                    "		where\n" +
                    "			it_pai > 0\n" +
                    "	)"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        
        try (Statement st = ConexaoOracle.createStatement()) {
            
            //<editor-fold defaultstate="collapsed" desc="MERCADOLOGICO 1">
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                            "    f_numerico(substr(tab_acesso,1,3)) tab_acesso,\n" +
                            "    rtrim(tab_conteudo) tab_conteudo\n" +
                            "from\n" +
                            "    AA2CTABE\n" +
                            "where\n" +
                            "    tab_codigo = 16 and \n" +
                            "    f_numerico(substr(tab_acesso,1,3)) > 0\n" +
                            "order by\n" +
                            "    tab_acesso"
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    
                    imp.setId(rs.getString("tab_acesso"));
                    imp.setDescricao(rs.getString("tab_conteudo"));
                    
                    merc.put(imp.getId(), imp);
                }
            }
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="MERCADOLOGICO 2">
            try (ResultSet rs = st.executeQuery(
                    "select \n" +
                            "    NCC_DEPARTAMENTO as merc1,\n" +
                            "    NCC_SECAO as merc2,\n" +
                            "    NCC_DESCRICAO as merc2_desc\n" +
                            "from\n" +
                            "    AA3CNVCC merc2        \n" +
                            "where\n" +
                            "    NCC_SECAO > 0\n" +
                            "    and NCC_GRUPO = 0\n" +
                            "    and NCC_SUBGRUPO = 0\n" +
                            "order by\n" +
                            "    merc2.ncc_departamento,\n" +
                            "    merc2.ncc_secao"
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP merc2 = merc.get(rs.getString("merc1"));
                    if (merc2 != null) {
                        merc2.addFilho(
                                rs.getString("merc2"),
                                rs.getString("merc2_desc")
                        );
                    }
                }
            }
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="MERCADOLOGICO 3">
            try (ResultSet rst = st.executeQuery(
                    "select \n" +
                    "    NCC_DEPARTAMENTO as merc1,\n" +
                    "    NCC_SECAO as merc2,\n" +
                    "	NCC_GRUPO as merc3,\n" +
                    "    NCC_DESCRICAO as merc3_desc\n" +
                    "from\n" +
                    "    AA3CNVCC        \n" +
                    "where\n" +
                    "    NCC_SECAO > 0\n" +
                    "    and NCC_GRUPO > 0\n" +
                    "    and NCC_SUBGRUPO = 0\n" +
                    "order by\n" +
                    "    ncc_departamento,\n" +
                    "    ncc_secao,\n" +
                    "    ncc_grupo"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("merc3_desc")
                            );
                        }
                    }
                }
            }
            //</editor-fold>
            
            try (ResultSet rst = st.executeQuery(
                    "select \n" +
                    "    NCC_DEPARTAMENTO as merc1,\n" +
                    "    NCC_SECAO as merc2,\n" +
                    "    NCC_GRUPO as merc3,\n" +
                    "    NCC_SUBGRUPO as merc4,\n" +
                    "    NCC_DESCRICAO as merc4_desc\n" +
                    "from\n" +
                    "    AA3CNVCC        \n" +
                    "where\n" +
                    "    NCC_SECAO > 0\n" +
                    "    and NCC_GRUPO > 0\n" +
                    "    and NCC_SUBGRUPO > 0\n" +
                    "order by\n" +
                    "    ncc_departamento,\n" +
                    "    ncc_secao,\n" +
                    "    ncc_grupo,\n" +
                    "    ncc_subgrupo" 
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            MercadologicoNivelIMP merc3 = merc2.getNiveis().get(rst.getString("merc3"));
                            if (merc3 != null) {
                                merc3.addFilho(
                                    rst.getString("merc4"),
                                    rst.getString("merc4_desc")
                                );
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "      distinct\n" +
                    "      ncm || icms_aliq_e || icms_rbc_e || icms_aliq_s || icms_rbc_s || iva id, \n" +
                    "      ncm,\n" +
                    "      tipo_iva,\n" +
                    "      iva,\n" +
                    "      icms_aliq_e,\n" +
                    "      icms_cst_e,\n" +
                    "      icms_rbc_e,\n" +
                    "      icms_aliq_s,\n" +
                    "      icms_cst_s,\n" +
                    "      icms_rbc_s\n" +
                    "from \n" +
                    "      vw_fis_mxf_produtos \n" +
                    "where \n" +
                    "      iva != 0\n" +
                    "order by\n" +
                    "      ncm, iva")) {
                while(rs.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    
                    imp.setId(rs.getString("id"));
                    
                    imp.setNcm(rs.getString("ncm"));
                    imp.setUf("SP");
                    if ("P".equals(rs.getString("tipo_iva"))) {
                        imp.setTipoIva(TipoIva.PERCENTUAL);
                        imp.setIva(rs.getDouble("iva"));
                    } else {
                        imp.setTipoIva(TipoIva.VALOR);
                        imp.setIva(rs.getDouble("iva"));
                    }
                    
                    if (rs.getInt("icms_cst_s") == 60) {
                        imp.setAliquotaDebito(0, rs.getDouble("icms_aliq_s"), 0);
                    } else {
                        imp.setAliquotaDebito(rs.getInt("icms_cst_s"), rs.getDouble("icms_aliq_s"), rs.getDouble("icms_rbc_s"));
                    }
                    
                    if (rs.getInt("icms_cst_e") == 60) {
                        imp.setAliquotaCredito(0, rs.getDouble("icms_aliq_e"), 0);
                    } else {
                        imp.setAliquotaCredito(rs.getInt("icms_cst_e"), rs.getDouble("icms_aliq_e"), rs.getDouble("icms_rbc_e"));
                    }
                    
                    if (rs.getInt("icms_cst_s") == 60) {
                        imp.setAliquotaDebitoForaEstado(0, rs.getDouble("icms_aliq_s"), 0);
                    } else {
                        imp.setAliquotaDebitoForaEstado(rs.getInt("icms_cst_s"), rs.getDouble("icms_aliq_s"), rs.getDouble("icms_rbc_s"));
                    }
                    
                    if (rs.getInt("icms_cst_e") == 60) {
                        imp.setAliquotaCreditoForaEstado(0, rs.getDouble("icms_aliq_e"), 0);
                    } else {
                        imp.setAliquotaCreditoForaEstado(rs.getInt("icms_cst_e"), rs.getDouble("icms_aliq_e"), rs.getDouble("icms_rbc_e"));
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.FABRICANTE
        ));
    }

    @Override
    public List<CompradorIMP> getCompradores() throws Exception {
        List<CompradorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    to_number(tab_acesso) id_comprador,\n" +
                    "    tab_conteudo nome\n" +
                    "from\n" +
                    "    AA2CTABE\n" +
                    "where\n" +
                    "    tab_codigo = 1 and \n" +
                    "    length(trim(tab_acesso)) = 3\n" +
                    "order by\n" +
                    "    tab_acesso"
            )) {
                while (rst.next()) {
                    result.add(new CompradorIMP(rst.getString("id_comprador"), rst.getString("nome")));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.git_cod_item id,\n" +
                    "	p.git_cod_item||p.git_digito codigosped,\n" +
                    "	p.GIT_DAT_ENT_LIN datacadastro,\n" +
                    "	p.GIT_EMB_FOR qtdembalagemcotacao,\n" +
                    "	ean.EAN_COD_EAN ean,\n" +
                    "	ean.EAN_EMB_VENDA qtdEmbalagem,\n" +
                    "	ean.EAN_TPO_EMB_VENDA tipoEmbalagem,\n" +
                    "	nullif(trim(p.GIT_TPO_EMB_FOR),'') tipoEmbalagemCotacao,\n" +
                    "	case when bal.balcol_codigo is null then 0 else 1 end e_balanca,\n" +
                    "	coalesce(bal.BALCOL_VALIDADE, p.GIT_PRZ_VALIDADE, 0) validade,\n" +
                    "	p.GIT_DESCRICAO descricaocompleta,\n" +
                    "	p.GIT_DESC_REDUZ descricaoreduzida,\n" +
                    "	p.GIT_DESCRICAO descricaogondola,\n" +
                    "	p.git_depto merc1,\n" +
                    "	p.GIT_SECAO merc2,\n" +
                    "	p.GIT_GRUPO merc3,\n" +
                    "	p.GIT_SUBGRUPO merc4,\n" +
                    "	p.GIT_CATEGORIA merc5,\n" +
                    "	p.GIT_COMPRADOR id_comprador,\n" +
                    "	F.TIP_CODIGO||F.TIP_DIGITO id_fabricante,\n" +
                    "	coalesce(cast(nullif(familia.it_pai, 0) as varchar(20)),p.git_cod_item||p.git_digito) id_familia,\n" +
                    "	case when p.GIT_DAT_SAI_LIN = 0 then 1 else 0 end id_situacaocadastral,\n" +
                    "	coalesce(nullif(det.DET_PESO_VND, 0), p.GIT_PESO) pesoliquido,\n" +
                    "	coalesce(nullif(det.DET_PESO_TRF, 0), p.GIT_PESO) pesobruto,\n" +
                    "	0 estoqueminimo,\n" +
                    "	0 estoquemaximo,    \n" +
                    "	est.GET_ESTOQUE estoque,\n" +
                    "	p.GIT_MRG_LUCRO_1 margem,\n" +
                    "	p.git_envia_pdv,\n" +
                    "	p.git_dat_sai_lin saidadelinha,\n" +
                    "	case when coalesce(preco.preco, 0) != 0 \n" +
                    "	then preco.preco\n" +
                    "	else coalesce(est.get_preco_venda,0) end precovenda,\n" +
                    "   coalesce(p.GIT_CUS_ULT_ENT_BRU, est.GET_CUS_ULT_ENT) custocomimposto,\n" +
                    "	p.git_cus_rep custosemimposto,\n" +
                    "	det.DET_CLASS_FIS ncm,\n" +
                    "	det.DET_NCM_EXCECAO excecao,\n" +
                    "	det.DET_CEST cest,\n" +
                    "	trib.piscofins_debito,\n" +
                    "	det.DET_NAT_REC nat_rec,\n" +
                    "	p.git_nat_fiscal icms_id,\n" +
                    "	trib.icms_cst,\n" +
                    "	trib.icms_aliq,\n" +
                    "	trib.icms_red,\n" +
                    "	coalesce(\n" +
                    "	    atac.precoatac,\n" +
                    "	    (case when coalesce(preco.preco, 0) != 0 \n" +
                    "	    then preco.preco\n" +
                    "	    else coalesce(est.get_preco_venda,0) end)\n" +
                    "	) precoatac\n" +
                    (utilizarViewMixFiscal ? "  ,vwfis.Icms_Aliq_E,\n" +
                    "  vwfis.icms_cst_e,\n" +
                    "  vwfis.ICMS_RBC_E,\n" +
                    "  vwfis.icms_aliq_s,\n" +
                    "  vwfis.ICMS_RBC_S,\n" +
                    "  vwfis.icms_cst_s,\n" +
                    "  vwfis.Pis_Cst_E,\n" +
                    "  vwfis.Pis_Cst_S,\n" +
                    "  vwfis.Cofins_Cst_E,\n" +
                    "  vwfis.cofins_cst_s,\n" +
                    "  vwfis.iva,\n" +
                    "  vwfis.tipo_iva,\n" +
                    "  replace(det.DET_CLASS_FIS || vwfis.Icms_Aliq_E || vwfis.ICMS_RBC_E || vwfis.icms_aliq_s || vwfis.ICMS_RBC_S || vwfis.iva, '.', ',') idpautafiscal\n" : "") +
                    "from\n" +
                    "	AA3CCEAN ean\n" +
                    "join AA3CITEM p on\n" +
                    "	    ean.EAN_COD_PRO_ALT = p.GIT_COD_ITEM || p.git_digito\n" +
                    "left join AA1CHELI familia on\n" +
                    "       p.git_cod_item = familia.it_codigo and\n" +
                    "       p.git_digito = familia.it_digito\n" +
                    "left join AA2CLOJA loja on\n" +
                    "	    loja.loj_codigo || loja.loj_digito = " + SQLUtils.stringSQL(getLojaOrigem()) + "\n" +
                    "left join AG1PBACO bal on\n" +
                    "	    bal.BALCOL_CODIGO = p.GIT_COD_ITEM and\n" +
                    "	    bal.BALCOL_DIGITO = p.GIT_DIGITO and\n" +
                    "	    bal.BALCOL_FILIAL = loja.loj_codigo || loja.loj_digito\n" +
                    "left join AA1DITEM det on\n" +
                    "	    p.GIT_COD_ITEM = det.DET_COD_ITEM\n" +
                    "join AA2CESTQ est on\n" +
                    "	    est.GET_COD_PRODUTO = p.GIT_COD_ITEM || p.GIT_DIGITO and\n" +
                    "	    est.GET_COD_LOCAL = loja.LOJ_CODIGO || loja.LOJ_DIGITO\n" +
                    "left join (\n" +
                    "	    select\n" +
                    "	    PDV_FILIAL filial,\n" +
                    "	    PDV_ITEM id,\n" +
                    "	    PDV_ITEM_DIGITO digito,\n" +
                    "	    max(PDV_CUSTO) custo,\n" +
                    "	    max(cast((PDV_PRECO_NORMAL / PDV_EMB_VENDA_UN) as numeric(10,2))) preco,\n" +
                    "	    max(case when PDV_EXCLUIR = 'S' then 0 else 1 end) id_situacaocadastral\n" +
                    "	    from\n" +
                    "	    AG1PDVPD\n" +
                    "	    group by\n" +
                    "	    PDV_FILIAL,\n" +
                    "	    PDV_ITEM,\n" +
                    "	    PDV_ITEM_DIGITO\n" +
                    "	    ) preco on\n" +
                    "	    preco.filial = loja.LOJ_CODIGO and\n" +
                    "	    preco.id = p.GIT_COD_ITEM and\n" +
                    "	    preco.digito = p.GIT_DIGITO\n" +
                    "left join (\n" +
                    "	    select distinct\n" +
                    "	    pdv.PDV_FILIAL filial,\n" +
                    "	    pdv.PDV_ITEM id,\n" +
                    "	    pdv.PDV_ITEM_DIGITO digito,\n" +
                    "	    pdv.PDV_SIT_TRIBUT icms_cst,\n" +
                    "	    pdv.PDV_TRIBUT icms_aliq,\n" +
                    "	    pdv.PDV_REDUCAO icms_red,\n" +
                    "	    pdv.PDV_CST_PIS piscofins_debito\n" +
                    "	    from\n" +
                    "	    AG1PDVPD pdv\n" +
                    "	    where pdv.PDV_EXCLUIR != 'S'\n" +
                    "	    ) trib on\n" +
                    "	    trib.filial = loja.LOJ_CODIGO and\n" +
                    "	    trib.id = p.GIT_COD_ITEM and\n" +
                    "	    trib.digito = p.GIT_DIGITO\n" +
                    "left join (\n" +
                    "	    select distinct\n" +
                    "	    pdv.PDV_FILIAL filial,\n" +
                    "	    pdv.PDV_CODIGO_EAN13 ean,\n" +
                    "	    pdv.PDV_PRECO_NORMAL / pdv.PDV_EMB_VENDA_UN precoatac,\n" +
                    "	    pdv.PDV_TPO_EMB_VENDA tipoembalagem,\n" +
                    "	    pdv.PDV_EMB_VENDA qtd\n" +
                    "	    from\n" +
                    "	    AG1PDVPD pdv\n" +
                    "	    ) atac on\n" +
                    "	    atac.filial = loja.LOJ_CODIGO and\n" +
                    "	    atac.ean = ean.EAN_COD_EAN\n" +
                    "left join AA2CTIPO f on\n" +
                    "       p.git_cod_for = f.TIP_CODIGO\n" +
                    (utilizarViewMixFiscal ? "left join\n" +
                    "       vw_fis_mxf_produtos vwfis on vwfis.codigo_produto = p.git_cod_item || p.git_digito\n" : "") +
                    (somenteAtivos ? "where p.GIT_DAT_SAI_LIN = 0\n" : " ") +
                    //(somenteAtivos ? "where p.GIT_DAT_SAI_LIN = 0\n" : "where p.GIT_DAT_SAI_LIN = 1\n") +                            
                    "order by \n" +
                    "	  p.git_cod_item"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
                int dataatual = Utils.stringToInt(format2.format(new java.util.Date()));
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setCodigoSped(rst.getString("codigosped"));
                    imp.setDataCadastro(format.parse(String.format("%06d", Utils.stringToInt(rst.getString("datacadastro")))));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoEmbalagemCotacao"));
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1("0".equals(rst.getString("merc1")) ? "" : rst.getString("merc1"));
                    imp.setCodMercadologico2("0".equals(rst.getString("merc2")) ? "" : rst.getString("merc2"));
                    imp.setCodMercadologico3("0".equals(rst.getString("merc3")) ? "" : rst.getString("merc3"));
                    imp.setCodMercadologico4("0".equals(rst.getString("merc4")) ? "" : rst.getString("merc4"));
                    imp.setFornecedorFabricante(rst.getString("id_fabricante"));
                    imp.setIdComprador(rst.getString("id_comprador"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(Utils.stringToInt(rst.getString("id_situacaocadastral"))));
                    imp.setPesoBruto(rst.getDouble("pesoliquido"));
                    imp.setPesoLiquido(rst.getDouble("pesobruto"));
                    if (rst.getInt("saidadelinha") > 0) {
                        int dataForaDeLinha = Utils.stringToInt(format2.format(format.parse(rst.getString("saidadelinha"))));
                        imp.setDescontinuado(dataForaDeLinha < dataatual);
                    }                    
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    
                    imp.setVendaPdv("S".equals(rst.getString("git_envia_pdv")));
                    
                    if (utilizarViewMixFiscal) {
                        imp.setPiscofinsCstDebito(rst.getInt("pis_cst_s"));
                        imp.setPiscofinsNaturezaReceita(rst.getInt("nat_rec"));
                        
                        imp.setIcmsCstSaida(rst.getInt("icms_cst_s"));
                        imp.setIcmsCstEntrada(rst.getInt("icms_cst_e"));
                        
                        imp.setIcmsAliqEntrada(rst.getDouble("icms_aliq_e"));
                        imp.setIcmsAliqSaida(rst.getDouble("icms_aliq_s"));
                        imp.setIcmsReducaoEntrada(rst.getDouble("icms_rbc_e"));
                        imp.setIcmsReducaoSaida(rst.getDouble("icms_rbc_s"));
                        imp.setAtacadoPreco(rst.getDouble("precoatac"));

                        imp.setPautaFiscalId(rst.getString("idpautafiscal"));
                    } else {
                        imp.setPiscofinsCstDebito(rst.getInt("piscofins_debito"));
                        imp.setPiscofinsNaturezaReceita(rst.getInt("nat_rec"));
                        
                        imp.setIcmsDebitoId(rst.getString("icms_id"));
                        imp.setIcmsDebitoForaEstadoId(rst.getString("icms_id"));
                        imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms_id"));
                        imp.setIcmsCreditoId(rst.getString("icms_id"));
                        imp.setIcmsCreditoForaEstadoId(rst.getString("icms_id"));                        
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	F.TIP_CODIGO||F.TIP_DIGITO id,\n" +
                    "	F.TIP_RAZAO_SOCIAL razao,\n" +
                    "	F.TIP_NOME_FANTASIA fantasia,\n" +
                    "	F.TIP_CGC_CPF cnpj,\n" +
                    "	F.TIP_INSC_EST_IDENT inscricaoestadual,\n" +
                    "	F.TIP_INSC_MUN insc_municipal,\n" +
                    "	F.TIP_ENDERECO endereco,\n" +
                    "	F.TIP_BAIRRO bairro,\n" +
                    "	F.TIP_CIDADE cidade,\n" +
                    "	F.TIP_ESTADO uf,\n" +
                    "	F.TIP_CEP cep,\n" +
                    "	F.TIP_DATA_CAD datacadastro,\n" +
                    "	coalesce(cast(F.TIP_FONE_DDD as varchar(20)), '') ||	cast(F.TIP_FONE_NUM as varchar(20)) fone1,\n" +
                    "	case when not F.TIP_TELEX_NUM is null then coalesce(cast(F.TIP_TELEX_DDD as varchar(20)), '') || cast(F.TIP_TELEX_NUM as varchar(20)) else null end fone2,\n" +
                    "	case when not F.TIP_FAX_NUM is null then coalesce(cast(F.TIP_FAX_DDD as varchar(20)), '') || cast(F.TIP_FAX_NUM as varchar(20)) else null end fax,\n" +
                    "    F2.FOR_COND_1 condicaopag,\n" +
                    "    f2.FOR_PRZ_ENTREGA entrega,\n" +
                    "    f2.FOR_FREQ_VISITA visita,\n" +
                    "    f3.FOR_PED_MIN_EMB qtd_pedido_minimo,\n" +
                    "    f3.FOR_PED_MIN_VLR valor_pedido_minimo\n" +
                    "FROM\n" +
                    "	AA2CTIPO F\n" +
                    "    left join AA2CFORN f2 on f.TIP_CODIGO = f2.FOR_CODIGO and F.TIP_DIGITO = f2.FOR_DIG_FOR \n" +
                    "    left join AA1FORDT f3 on f.TIP_CODIGO = f3.FOR_CODIGO\n" +
                    "WHERE\n" +
                    "	F.TIP_LOJ_CLI in ('F', 'L')\n" +
                    "order by\n" +
                    "    id"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rst.getString("insc_municipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    //imp.setDatacadastro(format.parse(rst.getString("datacadastro")));
                    String datacadastro;
                    java.sql.Date data = null;
                    if ((rst.getString("datacadastro") != null) &&
                            (!rst.getString("datacadastro").trim().isEmpty())) {
                        if (rst.getString("datacadastro").trim().length() == 5) {
                            datacadastro = rst.getString("datacadastro").trim();
                            datacadastro = rst.getString("datacadastro").substring(0, 1) + "/"
                                    + rst.getString("datacadastro").substring(1, 3) + "/"
                                    + rst.getString("datacadastro").substring(3, 5);
                            datacadastro = datacadastro.trim();
                            data = new java.sql.Date(format.parse(datacadastro).getTime());
                            imp.setDatacadastro(data);
                        } else if (rst.getString("datacadastro").trim().length() == 6) {
                            datacadastro = rst.getString("datacadastro").trim();
                            datacadastro = rst.getString("datacadastro").substring(0, 2) + "/"
                                    + rst.getString("datacadastro").substring(2, 4) + "/"
                                    + rst.getString("datacadastro").substring(4, 6);
                            datacadastro = datacadastro.trim();
                            data = new java.sql.Date(format.parse(datacadastro).getTime());
                            imp.setDatacadastro(data);
                        }
                    } else {
                        datacadastro = "";
                        data = null;
                        imp.setDatacadastro(data);
                    }
                    imp.setTel_principal(rst.getString("fone1"));                    
                    if (Utils.stringToLong(rst.getString("fone2")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("FONE 2");
                        cont.setTelefone(Utils.stringLong(rst.getString("fone2")));
                        imp.getContatos().put(cont, "1");
                    }
                    if (Utils.stringToLong(rst.getString("fax")) > 0) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("FAX");
                        cont.setTelefone(Utils.stringLong(rst.getString("fax")));
                    }
                    imp.setQtd_minima_pedido(rst.getInt("qtd_pedido_minimo"));
                    imp.setValor_minimo_pedido(rst.getDouble("valor_pedido_minimo"));
                    imp.setPrazoEntrega(rst.getInt("entrega"));
                    imp.setPrazoVisita(rst.getInt("visita"));
                    imp.setPrazoSeguranca(0);
                    imp.setCondicaoPagamento(rst.getInt("condicaopag"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();   
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "	FORITE_COD_FORN||FORITE_DIG_FORN AS FORNECEDOR,\n" +
                    "	GIT_COD_ITEM PRODUTO,\n" +
                    "	FORITE_REFERENCIA AS REFERENCIA,\n" +
                    "	forn.forite_uf_fator_conv AS FATOR_COVERSAO  \n" +
                    "FROM \n" +
                    "	AA1FORIT FORN\n" +
                    "	join AA3CITEM PROD on\n" +
                    "		PROD.GIT_COD_ITEM = FORN.FORITE_COD_ITEM\n" +
                    "where\n" +
                    "    not coalesce(nullif(trim(BOTH ' ' from FORITE_REFERENCIA),''),'()AA') = '()AA'\n" +
                    "union\n" +
                    "SELECT\n" +
                    "    p.git_cod_for||f.tip_digito AS FORNECEDOR,\n" +
                    "	p.GIT_COD_ITEM PRODUTO,\n" +
                    "    p.git_referencia  AS REFERENCIA,\n" +
                    "	p.git_emb_for fator_conversao\n" +
                    "FROM \n" +
                    "	AA3CITEM p\n" +
                    "	join AA2CTIPO f on\n" +
                    "        p.git_cod_for = f.tip_codigo\n" +
                    "where\n" +
                    "    not coalesce(nullif(trim(BOTH ' ' from p.git_referencia),''),'()AA') = '()AA'"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setQtdEmbalagem(rst.getInt("FATOR_COVERSAO"));
                    imp.setCodigoExterno(rst.getString("referencia"));
                    result.add(imp);
                }
            }
        }        
        
        return result;
    }    

    @Override
    public List<ClienteIMP> getClientesEventuais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(                        
                    "SELECT\n" +
                    "	F.TIP_CODIGO||F.TIP_DIGITO id,\n" +
                    "	F.TIP_RAZAO_SOCIAL razao,\n" +
                    "	F.TIP_NOME_FANTASIA fantasia,\n" +
                    "	F.TIP_CGC_CPF cnpj,\n" +
                    "	F.TIP_INSC_EST_IDENT inscricaoestadual,\n" +
                    "	F.TIP_INSC_MUN insc_municipal,\n" +
                    "	F.TIP_ENDERECO endereco,\n" +
                    "	F.TIP_BAIRRO bairro,\n" +
                    "	F.TIP_CIDADE cidade,\n" +
                    "	F.TIP_ESTADO uf,\n" +
                    "	F.TIP_CEP cep,\n" +
                    "	F.TIP_DATA_CAD datacadastro,\n" +
                    "	coalesce(cast(F.TIP_FONE_DDD as varchar(20)), '') ||	cast(F.TIP_FONE_NUM as varchar(20)) fone1,\n" +
                    "	case when not F.TIP_TELEX_NUM is null then coalesce(cast(F.TIP_TELEX_DDD as varchar(20)), '') || cast(F.TIP_TELEX_NUM as varchar(20)) else null end fone2,\n" +
                    "	case when not F.TIP_FAX_NUM is null then coalesce(cast(F.TIP_FAX_DDD as varchar(20)), '') || cast(F.TIP_FAX_NUM as varchar(20)) else null end fax,\n" +
                    "    F2.FOR_COND_1 prazoPagamento,\n" +
                    "    f2.FOR_PRZ_ENTREGA entrega,\n" +
                    "    f2.FOR_FREQ_VISITA visita,\n" +
                    "    f3.FOR_PED_MIN_EMB qtd_pedido_minimo,\n" +
                    "    f3.FOR_PED_MIN_VLR valor_pedido_minimo\n" +
                    "FROM\n" +
                    "	AA2CTIPO F\n" +
                    "    left join AA2CFORN f2 on f.TIP_CODIGO = f2.FOR_CODIGO and F.TIP_DIGITO = f2.FOR_DIG_FOR \n" +
                    "    left join AA1FORDT f3 on f.TIP_CODIGO = f3.FOR_CODIGO\n" +
                    "WHERE\n" +
                    "	F.TIP_LOJ_CLI = 'C'\n" +
                    "order by\n" +
                    "    id"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rst.getString("insc_municipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(format.parse(rst.getString("datacadastro")));
                    imp.setTelefone(rst.getString("fone1"));                    
                    if (Utils.stringToLong(rst.getString("fone2")) > 0) {
                        imp.addContato("1", "FONE 2", Utils.stringLong(rst.getString("fone2")), "", "");
                    }
                    if (Utils.stringToLong(rst.getString("fax")) > 0) {
                        imp.addContato("2", "FAX", Utils.stringLong(rst.getString("fax")), "", "");
                    }
                    imp.setPrazoPagamento(rst.getInt("prazoPagamento"));   
                    

                    result.add(imp);
                }                
            }
        }
        
        return result;                        
    }

    
    
    /*@Override
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (Statement stm2 = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(                        
                        "select \n" +
                        "    cli.cli_codigo||cli.cli_digito id,\n" +
                        "    cli.cli_codigo idSemDigito,\n" +
                        "    cli.CLI_CPF_CNPJ cnpj,\n" +
                        "    cli.CLI_RG_INSC_EST inscricaoestadual,\n" +
                        "    cli.CLI_ORG_EMIS orgaoemissor,\n" +
                        "    cli.CLI_NOME razao,\n" +
                        "    cli.CLI_NOME fantasia,\n" +
                        "    1 ativo,\n" +
                        "    case when cli.CLI_STATUS = 0 then 0 else 1 end bloqueado,\n" +
                        "    ender.END_RUA endereco,\n" +
                        "    ender.END_NRO numero,\n" +
                        "    ender.END_COMPL complemento,\n" +
                        "    ender.END_BAIRRO bairro,\n" +
                        "    ender.END_CID cidade,\n" +
                        "    ender.END_UF uf,\n" +
                        "    ender.END_CEP cep,\n" +
                        "    cli.CLI_ESTADO_CIVIL estadoCivil,\n" +
                        "    cli.CLI_DTA_NASC dataNascimento,\n" +
                        "    cli.CLI_DTA_CAD dataCadastro,\n" +
                        "    coalesce(cli.CLI_SEXO, 'M') sexo,\n" +
                        "    cli.CLI_NOME_EMPRESA empresa,\n" +
                        "    cli.CLI_DTA_ADMIS dataAdmissao,\n" +
                        "    cli.CLI_CARGO cargo,\n" +
                        "    cli.CLI_SALARIO salario,\n" +
                        "    coalesce(lim_ch.LIM_LIMITE,0) limite_cheque,\n" +
                        "    coalesce(lim_rt.LIM_LIMITE,0) limite_rotativo,\n" +
                        "    coalesce(lim_cv.LIM_LIMITE,0) limite_convenio,\n" +
                        "    cli.CLI_NOME_PAI nomepai,\n" +
                        "    cli.CLI_NOME_MAE nomemae\n" +
                        "from \n" +
                        "    CAD_CLIENTE cli\n" +
                        "    left join END_CLIENTE ender on\n" +
                        "        cli.cli_codigo = ender.cli_codigo\n" +
                        "        and ender.end_tpo_end = 1\n" +
                        "    left join AC1QLIMI lim_ch on\n" +
                        "        cli.cli_codigo = lim_ch.lim_codigo\n" +
                        "        and cli.cli_digito = lim_ch.LIM_DIGITO\n" +
                        "        and lim_ch.LIM_MODALIDADE = 1\n" +
                        "    left join AC1QLIMI lim_rt on\n" +
                        "        cli.cli_codigo = lim_rt.lim_codigo\n" +
                        "        and cli.cli_digito = lim_rt.LIM_DIGITO\n" +
                        "        and lim_rt.LIM_MODALIDADE = 2\n" +
                        "    left join AC1QLIMI lim_cv on\n" +
                        "        cli.cli_codigo = lim_cv.lim_codigo\n" +
                        "        and cli.cli_digito = lim_cv.LIM_DIGITO\n" +
                        "        and lim_cv.LIM_MODALIDADE = 3\n" +
                        "order by \n" +
                        "    cli.cli_codigo"
                )) {
                    SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                    while (rst.next()) {
                        ClienteIMP imp = new ClienteIMP();
                        imp.setId(rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpj"));
                        imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                        imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                        imp.setRazao(rst.getString("razao"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setAtivo(true);
                        imp.setBloqueado(rst.getBoolean("bloqueado"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setUf(rst.getString("uf"));
                        imp.setCep(rst.getString("cep"));
                        switch(rst.getInt("estadocivil")) {
                            case 1: imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO); break;
                            case 2: imp.setEstadoCivil(TipoEstadoCivil.CASADO); break;
                            case 3: imp.setEstadoCivil(TipoEstadoCivil.VIUVO); break;
                            case 4: imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO); break;
                            default: imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO); break;
                        }
                        imp.setDataNascimento(rst.getDate("datanascimento"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        switch (rst.getString("sexo")) {
                            case "F": imp.setSexo(TipoSexo.FEMININO); break;
                            default: imp.setSexo(TipoSexo.MASCULINO); break;
                        }
                        imp.setEmpresa(rst.getString("empresa"));
                        imp.setDataAdmissao(rst.getDate("dataadmissao"));
                        imp.setCargo(rst.getString("cargo"));
                        imp.setSalario(rst.getDouble("salario"));
                        imp.setValorLimite(rst.getDouble("limite_rotativo"));
                        imp.setNomePai(rst.getString("nomepai"));
                        imp.setNomeMae(rst.getString("nomemae"));

                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n" +
                                "    CLI_CODIGO idCliente,\n" +
                                "    CONT_COD_SEQ seq,\n" +
                                "    CONT_TIPO tipo,\n" +
                                "    cast(CONT_DDD||CONT_NUMERO as numeric) telefone,\n" +
                                "    CONT_RAMAL ramal,\n" +
                                "    CONT_OBS obs\n" +
                                "from \n" +
                                "    CONTATO_CLIENTE\n" +
                                "where \n" +
                                "    CLI_CODIGO = " + rst.getString("idSemDigito") + "\n" +
                                "order by\n" +
                                "    CLI_CODIGO, CONT_COD_SEQ"
                        )) {
                            while (rst2.next()) {
                                if (imp.getTelefone() == null) {
                                    imp.setTelefone(rst2.getString("telefone"));
                                } else {
                                    ClienteContatoIMP cont = new ClienteContatoIMP();
                                    if (rst2.getInt("tipo") == 1) {
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("RESIDENCIA");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    } else if (rst2.getInt("tipo") == 2) {                                        
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("COMERCIAL");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    } else if (rst2.getInt("tipo") == 3) {                                        
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("CELULAR");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    } else if (rst2.getInt("tipo") == 4) {                                        
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("FAX");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    } else if (rst2.getInt("tipo") == 5) {                                        
                                        cont.setId(rst2.getString("seq"));
                                        cont.setNome("RECADOS");
                                        cont.setCliente(imp);
                                        cont.setTelefone(rst2.getString("telefone"));
                                    }
                                    imp.getContatos().add(cont);
                                }
                            }
                        }                    

                        result.add(imp);
                    }
                }
            }
        }
        
        return result;
    }*/
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	     TIP.tip_cgc_cpf Cgc_cpf, \n" +
                    "        TIP.tip_codigo Codigo , \n" +
                    "	     TIP.tip_razao_social Razao_social, \n" +
                    "        TIP.tip_nome_fantasia Nome_fantasia,  \n" +
                    "	     TIP.tip_endereco Endereco, \n" +
                    "        TIP.tip_bairro Bairro, \n" +
                    "        TIP.tip_cidade Cidade,  \n" +
                    "	     TIP.tip_estado Estado, \n" +
                    "        TIP.tip_cep Cep, \n" +
                    "        TIP.tip_natureza Natureza,  \n" +
                    "	     TIP.tip_data_cad Data_cad, \n" +
                    "        TIP.tip_fax_ddd Fax_ddd, \n" +
                    "        TIP.tip_fax_num Fax_num,  \n" +
                    "        TIP.tip_fone_ddd Fone_ddd, \n" +
                    "        TIP.tip_fone_num Fone_num, \n" +
                    "        TIP.tip_fis_jur Fis_jur,  \n" +
                    "	     TIP.tip_insc_est_ident Insc_est_ident, \n" +
                    "        TIP.tip_regiao  Regiao,  \n" +
                    "	     TIP.tip_divisao Divisao, \n" +
                    "        TIP.tip_distrito Distrito, \n" +
                    "        CLI.cli_contato Contato_principal,  \n" +
                    "	     CLI.cli_cod_vend Vendedor,  \n" +
                    "	     CLI.cli_situacao Status, \n" +
                    "        CLI.cli_limite_cred,  \n" +
                    "	     Round(CLI.cli_limite_cred * (SELECT To_number(Substr(tab_conteudo, 1, 15) ) / 1000000  \n" +
                    "				FROM   aa2ctabe WHERE  tab_codigo = (SELECT emp_ind_limite  \n" +
                    "				FROM   aa2cempr  \n" +
                    "				WHERE emp_codigo = TIP.tip_empresa) AND tab_acesso = Rpad( \n" +
                    "				To_char(SYSDATE, 'YYMMDD'), 10, ' ')), 2) Limite_cred,  \n" +
                    "	     Nvl(por_banco, 0) banco,  \n" +
                    "	     Decode(cli.cli_situacao, 'A', 'ATIVO',\n" +
                    "	     Decode(cli.cli_situacao, 'I', 'INATIVO',\n" +
                    "	     Decode(cli.cli_situacao, 'C', 'CANCELADO',\n" +
                    "	     Decode(cli.cli_situacao, 'S', 'SUSPENSO',\n" +
                    "	     'ATIVO'))))\n" +
                    "	     SIGLA_STATUS,\n" +
                    "	     Nvl(dtip_cod_municipio, 0) COD_MUNI\n" +
                    "FROM   \n" +
                    "      aa2cclir CLI, \n" +
                    "      aa2ctipo TIP, \n" +
                    "      final_cliente FIN, \n" +
                    "      aa1rport, \n" +
                    "      aa1dtipo \n" +
                    "WHERE  \n" +
                    "		  TIP.tip_cgc_cpf >= 0 \n" +
                    "       AND 	TIP.tip_codigo >= 0 \n" +
                    "       AND 	TIP.tip_digito >= 0 \n" +
                    "       AND 	TIP.tip_codigo = CLI.cli_codigo(+) \n" +
                    "       AND 	TIP.tip_digito = CLI.cli_digito(+) \n" +
                    "       AND 	FIN.cli_codigo(+) = CLI.cli_codigo \n" +
                    "       AND 	por_portador (+) = cli.cli_port \n" +
                    "       AND 	dtip_codigo (+) = tip_codigo\n" +
                    "       and  tip.tip_loj_cli in ('C','R')")) {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("Codigo"));
                    imp.setRazao(rs.getString("Razao_social"));
                    imp.setCnpj(rs.getString("Cgc_cpf"));
                    imp.setFantasia(rs.getString("Nome_fantasia"));
                    imp.setEndereco(rs.getString("Endereco"));
                    imp.setBairro(rs.getString("Bairro"));
                    imp.setMunicipio(rs.getString("Cidade"));
                    imp.setMunicipioIBGE(rs.getInt("COD_MUNI"));
                    imp.setUf(rs.getString("Estado"));
                    imp.setCep(rs.getString("Cep"));
                    imp.setDataCadastro(format.parse(rs.getString("Data_cad")));
                    if (rs.getString("Fax_ddd") != null &&
                            !"0".equals(rs.getString("Fax_ddd"))) {
                        imp.addContato("Fax", "Fax", rs.getString("Fax_ddd") + rs.getString("Fax_num"), null, null);
                    }
                    if (rs.getString("Fone_ddd") != null &&
                            !"0".equals(rs.getString("Fone_ddd"))) {
                        imp.setTelefone(rs.getString("Fone_ddd") + rs.getString("Fone_num"));
                    }
                    imp.setInscricaoestadual(rs.getString("Insc_est_ident"));
                    if(rs.getString("Contato_principal") != null) {
                        imp.addContato("Contato Principal", rs.getString("Contato_principal"), null, null, null);
                    }
                    if(rs.getString("Status") != null && "".equals(rs.getString("Status"))) {
                        imp.setAtivo("A".equals(rs.getString("Status").trim()) ? true : false);
                    } else {
                        imp.setAtivo(false);
                    }
                    
                    imp.setValorLimite(rs.getDouble("Limite_cred"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public void importarPagamentoRotativo() throws Exception {        
        Conexao.begin();
        try {
            Map<String, Double> pagamentos = new HashMap<>();

            ProgressBar.setStatus("Importando pagamentos...");
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    lan.lanc_codigo,\n" +
                        "    sum(lan.lanc_valor) valor\n" +
                        "from\n" +
                        "    ac1clanc lan\n" +
                        "    join CAD_CLIENTE c on\n" +
                        "        lan.lanc_codigo = c.cli_codigo||c.cli_digito\n" +
                        "where\n" +
                        "    lan.lanc_tipo = 2\n" +
                        "    and lan.lanc_modalidade in (2, 3)\n" +
                        "    and c.cli_convenio = 0\n" +
                        "group by\n" +
                        "    lan.lanc_codigo\n" +
                        "order by\n" +
                        "    lanc_codigo"
                )) {
                    while (rst.next()) {
                        double valor = rst.getDouble("valor");
                        if (valor < 0) {
                            valor *= -1;
                        }
                        pagamentos.put(rst.getString("lanc_codigo"), MathUtils.trunc(valor, 2));
                    }
                }
            }

            System.out.println("Pagamentos: " + pagamentos.size() + " (209015) = " + pagamentos.get("209015"));
            
            CreditoRotativoDAO rotDao = new CreditoRotativoDAO();
            CreditoRotativoItemDAO dao = new CreditoRotativoItemDAO();
            CreditoRotativoItemAnteriorDAO antDao = new CreditoRotativoItemAnteriorDAO();
            MultiMap<String, CreditoRotativoItemAnteriorVO> baixasAnteriores = antDao.getBaixasAnteriores(null, null);

            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "	 ant.sistema,\n" +
                        "    ant.loja,\n" +
                        "    ant.id_cliente,\n" +
                        "    ant.id,\n" +
                        "    ant.codigoatual,\n" +
                        "    r.id_loja,\n" +
                        "    r.valor,\n" +
                        "    r.datavencimento\n" +
                        "from \n" +
                        "	implantacao.codant_recebercreditorotativo ant\n" +
                        "    join recebercreditorotativo r on\n" +
                        "    	ant.codigoatual = r.id\n" +
                        "order by\n" +
                        "	ant.id_cliente, r.datavencimento"
                )) {
                    int cont1 = 0, cont2 = 0;
                    while (rst.next()) {
                        
                        String sistema = rst.getString("sistema");
                        String loja = rst.getString("loja");
                        String idCliente = rst.getString("id_cliente");
                        String idRotativo = rst.getString("id");
                        int codigoAtual = rst.getInt("codigoatual");
                        int id_loja = rst.getInt("id_loja");
                        double valor = rst.getDouble("valor");
                        Date vencimento = rst.getDate("datavencimento");
                        
                        if ( !baixasAnteriores.containsKey(sistema, loja, idRotativo, idRotativo) ) {
                            if ( pagamentos.containsKey(idCliente) ) {                                
                                double valorPagoTotal = pagamentos.get(idCliente);
                                if (valorPagoTotal > 0) {
                                    double valorParc;
                                    if (valorPagoTotal >= valor) {
                                        valorPagoTotal -= valor;
                                        valorParc = valor;
                                    } else {
                                        valorParc = valorPagoTotal;
                                        valorPagoTotal = 0;
                                    }

                                    CreditoRotativoItemVO pag = new CreditoRotativoItemVO();
                                    pag.setId_receberCreditoRotativo(codigoAtual);
                                    pag.setValor(valorParc);
                                    pag.setValorTotal(valorParc);
                                    pag.setDatabaixa(vencimento);
                                    pag.setDataPagamento(vencimento);
                                    pag.setObservacao("IMPORTADO VR");
                                    pag.setId_loja(id_loja);

                                    dao.gravarRotativoItem(pag);

                                    CreditoRotativoItemAnteriorVO ant = new CreditoRotativoItemAnteriorVO();
                                    ant.setSistema(sistema);
                                    ant.setLoja(loja);
                                    ant.setIdCreditoRotativo(idRotativo);
                                    ant.setId(idRotativo);
                                    ant.setCodigoAtual(pag.getId());
                                    ant.setDataPagamento(vencimento);
                                    ant.setValor(pag.getValor());

                                    antDao.gravarRotativoItemAnterior(ant);
                                    
                                    rotDao.verificarBaixado(codigoAtual);

                                    pagamentos.put(idCliente, valorPagoTotal);
                                    baixasAnteriores.put(ant, 
                                            ant.getSistema(),
                                            ant.getLoja(),
                                            ant.getIdCreditoRotativo(),
                                            ant.getId()
                                    );
                                }
                            }
                        } 
                        cont1++;
                        cont2++;
                        
                        if (cont1 == 1000) {
                            cont1 = 0;
                            ProgressBar.setStatus("Importando pagamentos..." + cont2);
                        }
                    }
                }
            }
            
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    lan.lanc_codigo||'-'||lan.lanc_data||'-'||lan.lanc_seq id,\n" +
                    "    lan.lanc_codigo,\n" +
                    "    lan.lanc_data,\n" +
                    "    lan.lanc_seq,\n" +
                    "    lan.lanc_loja id_loja,\n" +
                    "    lan.lanc_data emissao,\n" +
                    "    case lan.lanc_cupom when 0 then lan.lanc_documento else lan.lanc_cupom end numerocupom,\n" +
                    "    lan.lanc_caixa ecf,\n" +
                    "    lan.lanc_valor valor,\n" +
                    "    lan.lanc_historico historico,\n" +
                    "    lan.lanc_usuario usuario,\n" +
                    "    lan.lanc_codigo idcliente,\n" +
                    "    lan.lanc_vencimento vencimento,\n" +
                    "    decode(lan.lanc_parcela, 0, 1, lan.lanc_parcela) parcela,\n" +
                    "    lan.lanc_vlr_juros juros,\n" +
                    "    lan.lanc_vlr_multa multa,\n" +
                    "    c.cli_convenio\n" +
                    "from\n" +
                    "    ac1clanc lan\n" +
                    "    join CAD_CLIENTE c on\n" +
                    "        lan.lanc_codigo = c.cli_codigo||c.cli_digito\n" +
                    "where\n" +
                    "    lan.lanc_tipo = 1\n" +
                    "    and lan.lanc_modalidade in (2, 3)\n" +
                    "    and c.cli_convenio = 0\n" +
                    "    and lan.lanc_loja = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n" +
                    "order by\n" +
                    "    lanc_codigo,\n" +
                    "    lanc_data,\n" +
                    "    lanc_seq"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(format.parse(rst.getString("emissao")));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(
                            (rst.getString("usuario") != null ? " USUARIO: " + rst.getString("usuario") : "") +
                            (rst.getString("historico") != null ? " HISTORICO: " + rst.getString("historico") : "")
                    );
                    imp.setIdCliente(rst.getString("idcliente"));
                    try {
                        imp.setDataVencimento(format.parse(rst.getString("vencimento")));
                    } catch (ParseException e) {
                        imp.setDataVencimento(imp.getDataEmissao());
                        System.out.println("**ERRO DE PARSING - vencimento: " + rst.getString("id") + " valor: " + rst.getString("valor"));                        
                    }
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setMulta(rst.getDouble("multa"));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "    ch.CHE_CODIGO||'-'||ch.CHE_BANCO||'-'||ch.CHE_AGENCIA||'-'||ch.CHE_CONTA_CORRENTE||'-'||ch.CHE_CHEQUE id,\n" +
                    "    ch.CHE_LOJA id_loja,\n" +
                    "    cli.CLI_CPF_CNPJ cpf,\n" +
                    "    ch.CHE_CHEQUE numeroCheque,\n" +
                    "    ch.CHE_BANCO banco,\n" +
                    "    ch.CHE_AGENCIA agencia,\n" +
                    "    ch.CHE_CONTA_CORRENTE conta,\n" +
                    "    ch.CHE_EMISSAO data,\n" +
                    "    ch.CHE_VALOR valor,\n" +
                    "    cli.CLI_RG_INSC_EST rg,\n" +
                    "    (select cast(CONT_DDD||CONT_NUMERO as numeric) from CONTATO_CLIENTE where CLI_CODIGO = cli.CLI_CODIGO and ROWNUM = 1) telefone,\n" +
                    "    cli.CLI_NOME nome,\n" +
                    "    ch.CHE_STATUS status,\n" +
                    "    ch.CHE_CMC7 cmc7,\n" +
                    "    ch.CHE_ALINEA alinea,\n" +
                    "    ch.CHE_VLR_JUROS juros,\n" +
                    "    ch.CHE_DTA_STATUS dataHoraAlteracao\n" +
                    "FROM \n" +
                    "    AC1CCHEQ ch\n" +
                    "    left join CAD_CLIENTE cli on\n" +
                    "        ch.che_codigo = cli.CLI_CODIGO||cli.CLI_DIGITO\n" +
                    "where \n" +
                    "    ch.CHE_STATUS = 1\n" +
                    "    and ch.CHE_LOJA = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1)
            )) {
                System.out.println(getLojaOrigem().substring(0, getLojaOrigem().length() - 1));
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroCheque(rst.getString("numeroCheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setDate(format.parse(rst.getString("data")));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setCmc7(rst.getString("cmc7"));
                    imp.setAlinea(rst.getInt("alinea"));
                    imp.setValorJuros(rst.getDouble("juros"));
                    imp.setDataHoraAlteracao(new Timestamp(format.parse(rst.getString("dataHoraAlteracao")).getTime()));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "    emp.CONV_CODIGO id,\n" +
                    "    emp.CONV_DESCRICAO razao,\n" +
                    "    cli.TIP_CGC_CPF cnpj,\n" +
                    "    cli.TIP_INSC_EST_IDENT inscricaoestadual,\n" +
                    "    cli.TIP_ENDERECO endereco,\n" +
                    "    cli.TIP_BAIRRO bairro,\n" +
                    "    cli.TIP_CIDADE cidade,\n" +
                    "    cli.TIP_ESTADO uf,\n" +
                    "    cli.TIP_CEP cep,\n" +
                    "    cast(cli.TIP_FONE_DDD||cli.TIP_FONE_NUM as numeric) fone1,\n" +
                    "    cicl.cicl_dta_inicio dataInicio,\n" +
                    "    cicl.cicl_dta_fim dataTermino,\n" +
                    "    emp.CONV_DESCONTO desconto,\n" +
                    "    emp.CONV_DIA_COBRANCA diapagamento,\n" +
                    "    emp.CONV_DIA_CORTE diainiciorenovacao,\n" +
                    "    emp.CONV_BLOQUEAR bloquear\n" +
                    "FROM \n" +
                    "    AC1CCONV emp\n" +
                    "    left join AA2CTIPO cli on\n" +
                    "        emp.conv_emp_codigo = cli.TIP_CODIGO and\n" +
                    "        emp.conv_emp_digito = cli.TIP_DIGITO\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            *\n" +
                    "        from\n" +
                    "            AC2CVCIC c\n" +
                    "        where\n" +
                    "            c.cicl_codigo = \n" +
                    "            (select max(cicl_codigo) from AC2CVCIC where conv_codigo = c.CONV_CODIGO)\n" +
                    "    ) cicl on\n" +
                    "        emp.conv_codigo = cicl.conv_codigo\n" +
                    "order by \n" +
                    "    emp.CONV_CODIGO"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero("0");
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTelefone(rst.getString("fone1"));
                    imp.setDataInicio(format.parse(rst.getString("datainicio")));
                    imp.setDataTermino(format.parse(rst.getString("datatermino")));
                    imp.setDesconto(rst.getDouble("desconto"));
                    imp.setDiaPagamento(rst.getInt("diapagamento"));
                    imp.setDiaInicioRenovacao(rst.getInt("diainiciorenovacao"));
                    imp.setBloqueado(rst.getBoolean("bloquear"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "    cli.cli_codigo||cli.cli_digito id,\n" +
                    "    cli.CLI_CPF_CNPJ cnpj,\n" +
                    "    cli.CLI_NOME razao,\n" +
                    "    cli.CLI_FIL_CAD loja,\n" +
                    "    cli.cli_convenio idEmpresa,\n" +
                    "    case when cli.CLI_STATUS = 0 then 0 else 1 end bloqueado,\n" +
                    "    coalesce(lim_cv.LIM_LIMITE,0) limite_convenio,\n" +
                    "    coalesce(conv.CONV_DESCONTO,0) desconto\n" +
                    "from \n" +
                    "    CAD_CLIENTE cli\n" +
                    "    left join END_CLIENTE ender on\n" +
                    "        cli.cli_codigo = ender.cli_codigo\n" +
                    "        and ender.end_tpo_end = 1\n" +
                    "    left join AC1QLIMI lim_cv on\n" +
                    "        cli.cli_codigo = lim_cv.lim_codigo\n" +
                    "        and cli.cli_digito = lim_cv.LIM_DIGITO\n" +
                    "        and lim_cv.LIM_MODALIDADE = 3\n" +
                    "    left join AC1CCONV conv on\n" +
                    "        conv.CONV_CODIGO = cli.CLI_convenio\n" +
                    "where\n" +
                    "    cli.cli_convenio > 0\n" +
                    "order by \n" +
                    "    cli.cli_codigo"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setNome(rst.getString("razao"));
                    imp.setIdEmpresa(rst.getString("idEmpresa"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setConvenioLimite(rst.getDouble("limite_convenio"));
                    imp.setConvenioDesconto(rst.getDouble("desconto"));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    lan.lanc_codigo||'-'||lan.lanc_data||'-'||lan.lanc_seq id,\n" +
                    "    lan.lanc_loja id_loja,\n" +
                    "    lan.lanc_codigo idcliente,\n" +
                    "    lan.lanc_caixa ecf,\n" +
                    "    case lan.lanc_cupom when 0 then lan.lanc_documento else lan.lanc_cupom end numerocupom,\n" +
                    "    lan.lanc_data dataHora,\n" +
                    "    lan.lanc_valor valor,\n" +
                    "    lan.lanc_historico historico,\n" +
                    "    emp.CONV_DIA_CORTE,\n" +
                    "    cicl.cicl_dta_inicio dataInicio,\n" +
                    "    cicl.cicl_dta_fim dataTermino\n" +
                    "from\n" +
                    "    ac1clanc lan\n" +
                    "    join CAD_CLIENTE c on\n" +
                    "        lan.lanc_codigo = c.cli_codigo||c.cli_digito\n" +
                    "    join AC1CCONV emp on\n" +
                    "        c.cli_convenio = emp.conv_codigo\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            *\n" +
                    "        from\n" +
                    "            AC2CVCIC c\n" +
                    "        where\n" +
                    "            c.cicl_codigo = \n" +
                    "            (select max(cicl_codigo) from AC2CVCIC where conv_codigo = c.CONV_CODIGO)\n" +
                    "    ) cicl on\n" +
                    "        emp.conv_codigo = cicl.conv_codigo\n" +
                    "where\n" +
                    "    lan.lanc_tipo = 1\n" +
                    "    and lan.lanc_modalidade = 3\n" +
                    "    and c.cli_convenio > 0\n" +
                    "    and lan.lanc_data >= cicl.cicl_dta_inicio\n" +
                    "    and cicl_dta_fim >= cast('1' || (extract(year from current_date) - 2000) || \n" +
                    "    (lpad(extract(month from current_date), 2, '0')) ||\n" +
                    "    (lpad(extract(day from current_date), 2, '0')) as numeric)\n" +
                    "    and lan.lanc_loja = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n" +
                    "order by\n" +
                    "    lan.lanc_codigo,\n" +
                    "    lan.lanc_data,\n" +
                    "    lan.lanc_seq"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("idCliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataHora(new Timestamp(format.parse(rst.getString("datahora")).getTime()));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("historico"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(java.util.Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            String sql = "select\n" +
                    "    p.pdv_filial id_loja,\n" +
                    "    p.pdv_item id_produto,\n" +
                    "    p.pdv_oferta_ini datainicio,\n" +
                    "    p.pdv_oferta_fim datafim,\n" +
                    "    p.pdv_oferta_preco preco_oferta,\n" +
                    "    p.pdv_preco_normal preco_normal\n" +
                    "from\n" +
                    "    AG1PDVPD p\n" +
                    "where\n" +
                    "    p.pdv_oferta_fim > 0 and\n" +
                    "    p.pdv_oferta_fim >= 1" + format.format(new Date()) + " and\n" +
                    "    p.pdv_filial = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n" +
                    "order by\n" +
                    "    1, 2";
            System.out.println(sql);
            try (ResultSet rst = stm.executeQuery(
                    /*"select\n" +
                    "    i.pof_loja id_loja,\n" +
                    "    i.pof_cod_item id_produto,\n" +
                    "    o.ofta_ini_vig datainicio,\n" +
                    "    o.ofta_fim_vig datafim,\n" +
                    "    i.POF_PRECO_OFTA precooferta\n" +
                    "from \n" +
                    "    AA1PROFT i\n" +
                    "    join AA1COFTA o on\n" +
                    "        i.POF_COD_OFERTA = o.OFTA_COD_OFERTA\n" +
                    "where\n" +
                    "    o.OFTA_FIM_VIG >= (1||substr(extract(year from current_date),3,4) || \n" +
                    "        lpad(extract(month from current_date), 2, '0') ||\n" +
                    "        lpad(extract(day from current_date), 2, '0'))\n" +
                    "    and i.POF_LOJA = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1)*/
                    sql
            )) {
                
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setDataInicio(format.parse(String.format("%06d", Utils.stringToInt(rst.getString("datainicio").substring(1)))));
                    imp.setDataFim(format.parse(String.format("%06d", Utils.stringToInt(rst.getString("datafim").substring(1)))));
                    imp.setPrecoOferta(rst.getDouble("preco_oferta"));
                    imp.setPrecoNormal(rst.getDouble("preco_normal"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);
                    
                    result.add(imp);
                }
            }/*
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  git_cod_item id_produto,\n" +
                    "  p.git_cod_item||p.git_digito cod_sped,\n" +
                    "  p.git_ini_oft_1 datainiciooferta,\n" +
                    "  p.git_fim_oft_1 datafimoferta,\n" +
                    "  p.git_prc_oft_1 precooferta\n" +
                    "from \n" +
                    "  AA3CITEM p \n" +
                    "where\n" +
                    "  p.git_fim_oft_1 > 0 and\n" +
                    "  p.git_prc_oft_1 > 0" //Provavelmente são as ofertas da loja 1
            )) {
                long dataAtual = Utils.stringToLong(format2.format(new java.util.Date()));
                while (rst.next()) {
                    
                    java.util.Date parse = format.parse(rst.getString("datafimoferta"));
                    long dataTerm = Utils.stringToLong(format2.format(parse));
                    
                    if (dataTerm >= dataAtual) {
                        OfertaIMP imp = new OfertaIMP();
                    
                        imp.setIdProduto(rst.getString("id_produto"));
                        imp.setDataInicio(format.parse(String.format("%06d", Utils.stringToInt(rst.getString("datainiciooferta")))));
                        imp.setDataFim(format.parse(String.format("%06d", Utils.stringToInt(rst.getString("datafimoferta")))));
                        imp.setPrecoOferta(rst.getDouble("precooferta"));
                        imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                        imp.setTipoOferta(TipoOfertaVO.CAPA);

                        result.add(imp);
                    }
                }
            }*/
        }
        
        return result;
    }

    public void importarContasAPagar(int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando as Contas a Pagar...");
        List<PagarOutrasDespesasVO> vPagarOutrasDespesas = getContasAPagar(idLojaVR);
        PagarOutrasDespesasDAO pagarOutrasDespesasDAO = new PagarOutrasDespesasDAO();
        pagarOutrasDespesasDAO.salvar(vPagarOutrasDespesas);
    }

    private List<PagarOutrasDespesasVO> getContasAPagar(int idLojaVR) throws Exception {
        List<PagarOutrasDespesasVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cp.cpd_cgc_cpf cnpj,\n" +
                    "    cp.cpd_forpri id_fornecedor,\n" +
                    "    cp.cpd_forne,\n" +
                    "    cp.cpd_ntfis numerodocumento,\n" +
                    "    210 tipoentrada,\n" +
                    "    cp.cpd_emissao dataemissao,\n" +
                    "    cp.cpd_dt_emi,\n" +
                    "    cp.cpd_recep dataentrada,\n" +
                    "    cp.cpd_dt_inclusao,\n" +
                    "    cp.cpd_vrnota valor,\n" +
                    "    cp.cpd_loja id_loja,\n" +
                    "    cp.cpd_dta_baixa,\n" +
                    "    cp.cpd_ven_org,\n" +
                    "    cp.cpd_serie,\n" +
                    "    cp.cpd_ntfis,\n" +
                    "    cp.cpd_vlr_pago_cheque,\n" +
                    "    cp.cpd_vlr_pago_dinhei,\n" +
                    "    cp.cpm_banco,\n" +
                    "    cp.cpd_agencia_emp,\n" +
                    "    cp.cpd_conta_emp,\n" +
                    "    cp.cpm_ncheq,\n" +
                    "    cp.cpm_venc datavencimento\n" +
                    "from\n" +
                    "    ag1pagcp cp\n" +
                    "where cp.cpd_loja = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n" +
                    "   and cp.cpd_emissao >= 170601\n" +
                    "order by\n" +
                    "    cp.cpd_emissao"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                while (rst.next()) {
                    PagarOutrasDespesasVO vo = new PagarOutrasDespesasVO();
                    
                    vo.setId_loja(idLojaVR);
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setNumerodocumento(rst.getInt("numerodocumento"));
                    vo.setId_tipoentrada(rst.getInt("tipoentrada"));
                    vo.setDataemissao(new java.sql.Date(format.parse(rst.getString("dataemissao")).getTime()));
                    vo.setDataentrada(new java.sql.Date(format.parse(rst.getString("dataentrada")).getTime()));
                    vo.setValor(rst.getDouble("valor"));
                    PagarOutrasDespesasVencimentoVO dup = new PagarOutrasDespesasVencimentoVO();
                    dup.setDatavencimento(new java.sql.Date(format.parse(rst.getString("datavencimento")).getTime()));
                    dup.setValor(vo.getValor());
                    vo.getvPagarOutrasDespesasVencimento().add(dup);
                    vo.setObservacao("IMPORTADO VR");
                    
                    result.add(vo);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cp.cpd_cgc_cpf cnpj,\n" +
                    "    F.TIP_CODIGO||F.TIP_DIGITO id_fornecedor,\n" +
                    "    cp.cpd_forne,\n" +
                    "    cp.cpd_ntfis numerodocumento,\n" +
                    "    cp.cpd_emissao dataemissao,\n" +
                    "    cp.cpd_dt_emi,\n" +
                    "    cp.cpd_recep dataentrada,\n" +
                    "    cp.cpd_dt_inclusao,\n" +
                    "    cp.cpd_vrnota valor,\n" +
                    "    cp.cpd_loja id_loja,\n" +
                    "    cp.cpd_dta_baixa,\n" +
                    "    cp.cpd_ven_org,\n" +
                    "    cp.cpd_serie,\n" +
                    "    cp.cpd_ntfis,\n" +
                    "    cp.cpd_vlr_pago_cheque,\n" +
                    "    cp.cpd_vlr_pago_dinhei,    \n" +
                    "    case when cp.cpd_vlr_pago_cheque + cp.cpd_vlr_pago_dinhei >= cp.cpd_vrnota then 'PAGO' else 'ABERTO' end situacao,\n" +
                    "    cp.cpm_banco,\n" +
                    "    cp.cpd_agencia_emp,\n" +
                    "    cp.cpd_conta_emp,\n" +
                    "    cp.cpm_ncheq,\n" +
                    "    cp.cpm_venc datavencimento,\n" +
                    "    cp.cpd_duplicata duplicata\n" +
                    "from\n" +
                    "    ag1pagcp cp\n" +
                    "    join AA2CTIPO F on\n" +
                    "         cp.cpd_forne = f.TIP_CODIGO\n" +
                    "where cp.cpd_loja = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + " and CPD_DTA_BAIXA = 0\n" +
                    "order by\n" +
                    "    cp.cpd_emissao"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                SimpleDateFormat format2 = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ContaPagarIMP vo = new ContaPagarIMP();
                    
                    vo.setId(String.format(
                            "%s-%s-%s-%s-%s-%s",
                            rst.getString("id_fornecedor"),
                            rst.getString("numerodocumento"),
                            rst.getString("dataemissao"),
                            rst.getString("valor"),
                            rst.getString("datavencimento"),
                            rst.getString("duplicata")
                    ));
                    vo.setIdFornecedor(rst.getString("id_fornecedor"));
                    vo.setDataHoraAlteracao(new Timestamp(format.parse(rst.getString("dataemissao")).getTime()));
                    vo.setDataEmissao(format.parse(rst.getString("dataemissao")));
                    vo.setDataEntrada(format.parse(rst.getString("dataentrada")));
                    vo.setNumeroDocumento(rst.getString("numerodocumento"));
                    vo.setObservacao("DUPLICATA " + rst.getString("duplicata"));
                    vo.setValor(rst.getDouble("valor"));
                    vo.addVencimento(format2.parse(rst.getString("datavencimento")), vo.getValor()).setObservacao("DUPLICATA " + rst.getString("duplicata"));
                    
                    result.add(vo);
                }
            }
        }
        
        return result;
    }
    
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem().substring(0, getLojaOrigem().length() - 1), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem().substring(0, getLojaOrigem().length() - 1), dataInicioVenda, dataTerminoVenda);
    }
    
    private class VendaIterator implements Iterator<VendaIMP> {

        final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoOracle.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                if (next == null) {                    
                    if (rst.next()) {
                        
                        if (versaoDaVenda != 3) {
                            next = new VendaIMP();
                            String chave = rst.getString("chave");

                            if (chave != null && !"".equals(chave)) {
                                chave = chave.substring(36, 40);
                            } else {
                                chave = "";
                            }

                            String id = chave + "-" + rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
                            if (!uk.add(id)) {
                                LOG.warning("Venda " + id + " já existe na listagem " + rst.getString("chave") + "-" + rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("data"));
                            }
                            next.setId(id);
                            next.setNumeroCupom(Utils.stringToInt(rst.getString("coo")));
                            next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                            next.setData(format.parse(rst.getString("data")));
                            next.setIdClientePreferencial(rst.getString("id_cliente"));
                            String horaInicio = timestampDate.format(format.parse(rst.getString("data"))) + " " + rst.getString("horainicio");
                            String horaTermino = timestampDate.format(format.parse(rst.getString("data"))) + " " + rst.getString("horafim");
                            next.setHoraInicio(timestamp.parse(horaInicio));
                            next.setHoraTermino(timestamp.parse(horaTermino));
                            next.setCancelado("C".equals(rst.getString("situacao").trim()));
                            next.setSubTotalImpressora(rst.getDouble("vltotal"));
                            next.setCpf(rst.getString("cnpj"));
                            next.setValorDesconto(rst.getDouble("vldesconto"));
                            next.setNumeroSerie(rst.getString("seriecf"));
                            next.setModeloImpressora(rst.getString("modeloecf"));
                            next.setNomeCliente(rst.getString("razaosocial"));
                            String endereco
                                    = Utils.acertarTexto(rst.getString("endereco")) + ","
                                    + Utils.acertarTexto(rst.getString("bairro")) + ","
                                    + Utils.acertarTexto(rst.getString("municipio")) + "-"
                                    + Utils.acertarTexto(rst.getString("uf")) + ","
                                    + Utils.acertarTexto(rst.getString("cep"));
                            next.setEnderecoCliente(endereco);
                            next.setChaveCfe(rst.getString("chave"));

                        } else {
                            
                            next = new VendaIMP();
                            String chave = rst.getString("chave");

                            if (chave != null && !"".equals(chave)) {
                                chave = chave.substring(36, 40);
                            } else {
                                chave = "";
                            }

                            String id = rst.getString("id");
                            if (!uk.add(id)) {
                                LOG.warning("Venda " + id + " já existe na listagem " + rst.getString("chave") + "-" + rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("data"));
                            }
                            next.setId(id);
                            next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                            next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                            next.setData(format.parse(rst.getString("datavenda")));
                            next.setIdClientePreferencial(rst.getString("idcliente"));
                            String horaInicio = timestampDate.format(format.parse(rst.getString("datavenda"))) + " 00:00:00";
                            String horaTermino = timestampDate.format(format.parse(rst.getString("datavenda"))) + " 00:00:00";
                            next.setHoraInicio(timestamp.parse(horaInicio));
                            next.setHoraTermino(timestamp.parse(horaTermino));
                            next.setCancelado(false);
                            next.setSubTotalImpressora(rst.getDouble("valortotal"));
                            next.setCpf(rst.getString("cnpj"));
                            next.setValorDesconto(rst.getDouble("desconto"));
                            next.setNumeroSerie(rst.getString("serieecf"));
                            next.setModeloImpressora(rst.getString("modeloecf"));
                            next.setNomeCliente(rst.getString("razaosocial"));
                            String endereco
                                    = Utils.acertarTexto(rst.getString("endereco")) + ","
                                    + Utils.acertarTexto(rst.getString("bairro")) + ","
                                    + Utils.acertarTexto(rst.getString("municipio")) + "-"
                                    + Utils.acertarTexto(rst.getString("uf")) + ","
                                    + Utils.acertarTexto(rst.getString("cep"));
                            next.setEnderecoCliente(endereco);
                            next.setChaveCfe(rst.getString("chave"));
                            
                        }
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
            if (versaoDaVenda == 1) {
                sql = "select distinct\n" +
                    "	vda.mag60i_loja_a id_loja,\n" +
                    "	vda.mag60i_aaammdd data,\n" +
                    "	vda.mag60i_caixa caixa,\n" +
                    "	vda.mag60i_caixa ecf,\n" +
                    "	vda.mag60i_cupom coo,\n" +
                    "	'00:00:0000' horainicio,\n" +
                    "	'00:00:0000' horafim,\n" +
                    "	sum(vda.mag60i_vlr_base) vltotal,\n" +
                    "	0 vldesconto,\n" +
                    "	min(vda.mag60i_situacao) situacao,\n" +
                    "	'' modeloecf,\n" +
                    "	vda.mag60i_seri_ecf seriecf,\n" +
                    "	'' chave,\n" +
                    "	'' id_cliente,\n" +
                    "	'' cnpj,\n" +
                    "	'' razaosocial,\n" +
                    "	'' endereco,\n" +
                    "	'' bairro,\n" +
                    "	'' municipio,\n" +
                    "	'' uf,\n" +
                    "	'' cep,\n" +
                    "	'' chave	\n" +
                    "from\n" +
                    "	ag2vr60i vda\n" +
                    "where\n" +
                    "	vda.mag60i_loja_a = " + idLojaCliente + " and\n" +
                    "	vda.mag60i_aaammdd >= " + format.format(dataInicio) + " and\n" +
                    "	vda.mag60i_aaammdd <= " + format.format(dataTermino) + "\n" +
                    "group by\n" +
                    "	vda.mag60i_loja_a,\n" +
                    "	vda.mag60i_aaammdd,\n" +
                    "	vda.mag60i_caixa,\n" +
                    "	vda.mag60i_seri_ecf,\n" +
                    "	vda.mag60i_cupom";
            } if (versaoDaVenda == 2) {
                    sql = "select distinct\n" +
                    "       vda.r60i_fil idloja,\n" +
                    "       vda.r60i_dta data,\n" +
                    "       vda.r60i_cxa caixa,\n" +
                    "       case when vda.r60i_ecf_nro = 8 then 25\n" +
                    "       when vda.r60i_ecf_nro = 9 then 26\n" +
                    "       else vda.r60i_ecf_nro end as ecf,\n" +
                    "       vda.r60i_cup coo,\n" +
                    "       '00:00:0000' horainicio,\n" +
                    "       '00:00:0000' horafim,\n" +
                    "       sum(vda.r60i_vlr_ctb) vltotal,\n" +
                    "       sum(vda.r60i_vlr_dsc) vldesconto,\n" +
                    "       min(vda.r60i_sit) situacao,\n" +
                    "       vda.r60i_ecf_mdl modeloecf,\n" +
                    "       vda.r60i_ecf_ser seriecf,\n" +
                    "       vda.r60i_chv_cel ,\n" +
                    "       tip.tip_codigo as id_cliente,\n" +
                    "       tip.tip_cgc_cpf cnpj,\n" +
                    "       tip.tip_razao_social razaosocial,\n" +
                    "       tip.tip_endereco endereco,\n" +
                    "       tip.tip_bairro bairro,\n" +
                    "       tip.tip_cidade municipio,\n" +
                    "       tip.tip_estado uf,\n" +
                    "       tip.tip_cep cep,\n" +
                    "       vda.r60i_chv_cel chave\n" +
                    "from \n" +
                      " AA1FR60I_" + tabela_venda + " vda\n" +
                    "left join\n" +
                    "       aa2ctipo tip on vda.r60i_cgc_cpf = tip.tip_cgc_cpf\n" +
                    "left join\n" +
                    "       aa2cclir cli on tip.tip_codigo = cli.cli_codigo\n" +
                    "where\n" +
                    "       vda.r60i_fil = " + idLojaCliente + "\n" +
                    "group by\n" +
                    "       vda.r60i_fil,\n" +
                    "       vda.r60i_dta,\n" +
                    "       vda.r60i_cxa,\n" +
                    "       vda.r60i_ecf_nro,\n" +
                    "       vda.r60i_cup,\n" +
                    "       vda.r60i_ecf_mdl,\n" +
                    "       vda.r60i_ecf_ser,\n" +
                    "       vda.r60i_chv_cel,\n" +
                    "       tip.tip_codigo,\n" +
                    "       tip.tip_cgc_cpf,\n" +
                    "       tip.tip_razao_social,\n" +
                    "       tip.tip_endereco,\n" +
                    "       tip.tip_bairro,\n" +
                    "       tip.tip_cidade,\n" +
                    "       tip.tip_estado,\n" +
                    "       tip.tip_cep,\n" +
                    "       vda.r60i_chv_cel";
            } else if (versaoDaVenda == 3) {
                sql = "select\n"
                        + "    v.NFCC_ID as id,\n"
                        + "    v.NFCC_LOJ as idloja,\n"
                        + "    v.NFCC_DTA as datavenda,\n"
                        + "    v.NFCC_CUP as numerocupom,\n"
                        + "    v.NFCC_CPF as cpf,\n"
                        + "    v.NFCC_CLI as idcliente,\n"
                        + "    v.NFCC_CXA as caixa,\n"
                        + "    v.NFCC_OPE as operador,\n"
                        + "    v.NFCC_HRS_EMI as horavenda,\n"
                        + "    v.NFCC_CHV_NFC as chave,\n"
                        + "    v.NFCC_PDV_NRO as ecf,\n"
                        + "    v.NFCC_PDV_SER as serieecf,\n"
                        + "    v.NFCC_EST as estado,\n"
                        + "    v.NFCC_CTB_VAL as valortotal,\n"
                        + "    v.NFCC_ACR_VAL as acrescimo,\n"
                        + "    v.NFCC_DSC_VAL as desconto,\n"
                        + "    v.NFCC_PDV_MDL as modeloecf,"
                        + "    tip.TIP_CODIGO as id_cliente,\n"
                        + "    tip.TIP_CGC_CPF as cnpj,\n"
                        + "    tip.TIP_RAZAO_SOCIAL as razaosocial,\n"
                        + "    tip.TIP_ENDERECO as endereco,\n"
                        + "    tip.TIP_BAIRRO as bairro,\n"
                        + "    tip.TIP_CIDADE as municipio,\n"
                        + "    tip.TIP_ESTADO as uf,\n"
                        + "    tip.TIP_CEP as cep \n"
                        + "from AG3VNFCC_" + tabela_venda + " v\n"
                        + "left join aa2ctipo tip on tip.TIP_CODIGO = v.NFCC_CLI\n"
                        + "where v.NFCC_CFO in (5102, 5405)\n"
                        + "and v.NFCC_EST = 'PE'\n"
                        + "and v.NFCC_LOJ = " + idLojaCliente + "\n"
                        + "order by v.NFCC_ID";
            }
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
            System.out.println("Loja Digito: " + digito + "; Tabela: " + tabela_venda);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
    
    private class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoOracle.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;
        
        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        
                        if (versaoDaVenda != 3) {
                            next = new VendaItemIMP();

                            String chave = rst.getString("chave");

                            if (chave != null && !"".equals(chave)) {
                                chave = chave.substring(36, 40);
                            } else {
                                chave = "";
                            }

                            String id = chave + "-" + rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
                            String id_vendaitem = rst.getString("vltotal") + "-" + rst.getInt("sequencia") + "-" + rst.getString("R60i_Sec") + "-" + rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("data");

                            next.setId(id_vendaitem);
                            next.setVenda(id);
                            next.setProduto(rst.getString("id_produto"));
                            next.setDescricaoReduzida(rst.getString("descricao"));
                            next.setPrecoVenda(rst.getDouble("vlunitario"));
                            next.setQuantidade(rst.getDouble("qtd"));
                            next.setTotalBruto(rst.getDouble("vltotal"));
                            next.setValorDesconto(rst.getDouble("vldesconto"));
                            next.setCancelado("C".equals(rst.getString("situacao").trim()));
                            next.setCodigoBarras(rst.getString("ean"));
                            next.setUnidadeMedida(rst.getString("embalagem"));

                            String trib = rst.getString("tributacao");

                            obterAliquota(next, trib);

                        } else {
                            next = new VendaItemIMP();

                            String id = rst.getString("idvenda") + "-" + rst.getString("idproduto") + "-" + rst.getString("sequencia");
                            String idvenda = rst.getString("idvenda");

                            next.setId(id);
                            next.setVenda(idvenda);
                            next.setProduto(rst.getString("idproduto"));
                            next.setSequencia(rst.getInt("sequencia"));
                            next.setDescricaoReduzida(rst.getString("descricaoproduto"));
                            next.setPrecoVenda(rst.getDouble("precovenda"));
                            next.setQuantidade(rst.getDouble("qtd"));
                            next.setTotalBruto(rst.getDouble("valortotal"));
                            next.setValorDesconto(rst.getDouble("desconto"));
                            next.setValorAcrescimo(rst.getDouble("acrescimo"));
                            next.setCancelado(false);
                            next.setCodigoBarras(rst.getString("codigobarras"));
                            next.setUnidadeMedida(rst.getString("tipoembalagem"));

                            String trib = rst.getString("icmstrib").trim();

                            obterAliquota(next, trib);
                        }
                        
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             0450       ALIQUOTA 4.5%
             0700	ALIQUOTA 07%
             1100       ALIQUOTA 11%
             1200	ALIQUOTA 12%
             1800	ALIQUOTA 18%
             2500	ALIQUOTA 25%
             I          ISENTO
             F          SUBST TRIBUTARIA
             */
            int cst;
            double aliq;
            switch (icms) {
                case "0450":
                    cst = 0;
                    aliq = 4.5;
                    break;
                case "1200":
                    cst = 0;
                    aliq = 12;
                    break;
                case "1800":
                    cst = 0;
                    aliq = 18;
                    break;
                case "2500":
                    cst = 0;
                    aliq = 25;
                    break;
                case "1100":
                    cst = 0;
                    aliq = 11;
                    break;
                case "2700":
                    cst = 0;
                    aliq = 27;
                    break;
                case "1700":
                    cst = 0;
                    aliq = 17;
                    break;
                case "0700":
                    cst = 0;
                    aliq = 7;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            if (versaoDaVenda == 2) {
                this.sql
                        = "select\n" +
                        "       itm.r60i_cup coo,\n" +
                        "       itm.r60i_dta data,\n" +
                        "       itm.r60i_cxa caixa,\n" +
                        "       case when itm.r60i_ecf_nro = 8 then 25 \n" +
                        "       when itm.r60i_ecf_nro = 9 then 26 \n" +
                        "       else itm.r60i_ecf_nro end as ecf, \n" +
                        "       itm.R60i_Seq sequencia,\n" +
                        "       itm.R60i_Sec, \n" +
                        "       itm.r60i_ean ean,\n" +
                        "       p.git_descricao descricao,\n" +
                        "       itm.r60i_ite id_produto,\n" +
                        "       itm.r60i_emb_tpo embalagem,\n" +
                        "       itm.r60i_sit situacao,\n" +
                        "       itm.r60i_qtd qtd,\n" +
                        "       itm.r60i_vlr_uni vlunitario,\n" +
                        "       itm.r60i_vlr_ctb vltotal,\n" +
                        "       itm.r60i_vlr_dsc vldesconto,\n" +
                        "       itm.r60i_icm_trb tributacao,\n" +
                        "       itm.r60i_icm_alq aliqicms,\n" +
                        "       itm.r60i_chv_cel chave\n" +
                        "from\n" +
                        "       AA1FR60I_" + tabela_venda + " itm\n" +
                        "join\n" +
                        "       AA3CITEM p on itm.r60i_ite = p.git_cod_item\n" +
                        "order by\n" +
                        "       itm.r60i_cup, itm.r60i_seq";
            } else if (versaoDaVenda == 3) {
                this.sql
                        = "select \n"
                        + "    i.NFCI_ID as idvenda,\n"
                        + "    i.NFCI_SEQ as sequencia,\n"
                        + "    i.NFCI_ITE as idproduto,\n"
                        + "    i.NFCI_EAN as codigobarras,\n"
                        + "    i.NFCI_DCR as descricaoproduto,\n"
                        + "    i.NFCI_TPO_EMB as tipoembalagem,\n"
                        + "    i.NFCI_QTD_UNI as qtd,\n"
                        + "    i.NFCI_VLR_UNI as precovenda,\n"
                        + "    i.NFCI_CTB_VAL as valortotal,\n"
                        + "    i.NFCI_ICM_TRB as icmstrib,\n"
                        + "    i.NFCI_ICM_CST as icmscst,\n"
                        + "    i.NFCI_ICM_ALQ as icmsaliquota,\n"
                        + "    i.NFCI_ICM_RED as icmsreducao,\n"
                        + "    i.NFCI_DSC_VAL as desconto,\n"
                        + "    i.NFCI_ACR_VAL as acrescimo\n"
                        + "from AG3VNFCI_" + tabela_venda + " i\n"
                        + "where NFCI_ID in (select NFCC_ID from AG3VNFCC_" + tabela_venda + " where NFCC_CFO in (5102, 5405)\n"
                        + "and NFCC_EST = 'PE'\n"
                        + "and NFCC_LOJ = " + idLojaCliente + ")";
            }
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private class ProdutoComplementoParcial {
        int id;
        String impId;
        double pisDeb = 0;
        double pisCred = 0;
        double pisRed = 0;
        double icmsDebCst = 0;
        double icmsDebAliq = 0;
        double icmsDebRed = 0;
        double icmsCredCst = 0;
        double icmsCredAliq = 0;
        double icmsCredRed = 0;
    }
    
    @Override
    public List<VendaHistoricoIMP> getHistoricoVenda() throws Exception {
        List<VendaHistoricoIMP> result = new ArrayList<>();
        
        Map<String, String> eans = new HashMap<>();        
        Map<String, ProdutoComplementoParcial> aliquotas = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	ant.impid,\n" +
                    "	min(ean.codigobarras) codigobarras\n" +
                    "from\n" +
                    "	produtoautomacao ean\n" +
                    "	join implantacao.codant_produto ant on\n" +
                    "		ant.codigoatual = ean.id_produto and\n" +
                    "		ant.impsistema = 'RMS'\n" +
                    "group by\n" +
                    "	ant.impid"
            )) {
                while (rst.next()) {
                    eans.put(rst.getString("impid"), rst.getString("codigobarras"));
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.impid,\n" +
                    "	ant.codigoatual,\n" +
                    "	pisdeb.valorpis pisdeb,\n" +
                    "	piscred.valorpis piscred,\n" +
                    "	piscred.reduzidocredito pisred,\n" +
                    "	aliqdeb.situacaotributaria icmsdeb_cst,\n" +
                    "	aliqdeb.porcentagem icmsdeb_aliq,\n" +
                    "	aliqdeb.reduzido icmsdeb_red,\n" +
                    "	aliqcred.situacaotributaria icmscred_cst,\n" +
                    "	aliqcred.porcentagem icmscred_aliq,\n" +
                    "	aliqcred.reduzido icmscred_red\n" +
                    "from\n" +
                    "	produtoaliquota al\n" +
                    "	join (\n" +
                    "			select \n" +
                    "				loja.id, \n" +
                    "				f.id_estado\n" +
                    "			from\n" +
                    "				loja\n" +
                    "				join fornecedor f on\n" +
                    "					loja.id_fornecedor = f.id\n" +
                    "	) loja on\n" +
                    "		loja.id = 1 and\n" +
                    "		loja.id_estado = al.id_estado\n" +
                    "	join produto p on\n" +
                    "		al.id_produto = p.id\n" +
                    "	join implantacao.codant_produto ant on\n" +
                    "		ant.impsistema = 'RMS' and\n" +
                    "		ant.imploja = '" + getLojaOrigem() + "' and\n" +
                    "		ant.codigoatual = al.id_produto\n" +
                    "	join tipopiscofins pisdeb on\n" +
                    "		p.id_tipopiscofins = pisdeb.id\n" +
                    "	join tipopiscofins piscred on\n" +
                    "		p.id_tipopiscofinscredito = piscred.id\n" +
                    "	join aliquota aliqdeb on\n" +
                    "		al.id_aliquotadebito = aliqdeb.id\n" +
                    "	join aliquota aliqcred on\n" +
                    "		al.id_aliquotacredito = aliqcred.id\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    ProdutoComplementoParcial pcp = new ProdutoComplementoParcial();
                    pcp.id = rst.getInt("codigoatual");
                    pcp.impId = rst.getString("impid");
                    pcp.pisDeb = rst.getDouble("pisdeb");
                    pcp.pisCred = rst.getDouble("piscred");
                    pcp.pisRed = rst.getDouble("pisred");
                    pcp.icmsDebCst = rst.getDouble("icmsdeb_cst");
                    pcp.icmsDebAliq = rst.getDouble("icmsdeb_aliq");
                    pcp.icmsDebRed = rst.getDouble("icmsdeb_red");
                    pcp.icmsCredCst = rst.getDouble("icmscred_cst");
                    pcp.icmsCredAliq = rst.getDouble("icmscred_aliq");
                    pcp.icmsCredRed = rst.getDouble("icmscred_red");
                    aliquotas.put(pcp.impId, pcp);
                }
            }
        }
            
        try (Statement stm = ConexaoOracle.createStatement()) {
            SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	I.ESITC_CODIGO id_produto, \n" +
                    "	I.ESITC_DIGITO digito,\n" +
                    "	I.ESCHC_DATA data,\n" +
                    "	I.ENTSAIC_PRC_UN precovenda,  \n" +
                    "	I.ENTSAIC_PRC_EMB precovenda2,\n" +
                    "	I.ENTSAIC_QUANTI_UN quantidade,\n" +
                    "	I.ENTSAIC_CUS_UN custo\n" +
                    "FROM\n" +
                    "	AG1IENSA I,\n" +
                    "	AA1CFISC FIS,\n" +
                    "	AA2CTIPO,\n" +
                    "	AA1CTCON \n" +
                    "WHERE\n" +
                    "	ROWNUM >= 0 \n" +
                    "	AND I.ESCHC_DATA >= " + format.format(dataInicioVenda) + " \n" +
                    "	AND I.ESCHC_DATA <= " + format.format(dataTerminoVenda) + " \n" +
                    "	AND FIS.FIS_OPER in (231,238)\n" + //TUDO QUE NÃO FOR VENDA PDV
                    "	AND NVL(I.ENTSAIC_SITUACAO, ' ') <> 'E' \n" +
                    "	AND NVL(I.ENTSAIC_SITUACAO, ' ') <> '9' \n" +
                    "	AND I.ESCHC_AGENDA IN (\n" +
                    "		SELECT \n" +
                    "			FIG_AGD AGD  \n" +
                    "		FROM\n" +
                    "			AA2CFIGS \n" +
                    "		WHERE\n" +
                    "			FIG_DTA = I.ESCHC_DATA \n" +
                    "			AND FIG_SER = I.ESCHC_SER_NOTA \n" +
                    "			AND FIG_NTA = I.ESCHC_NRO_NOTA \n" +
                    "			AND FIG_ORG = I.ESCHLJC_CODIGO \n" +
                    "			AND FIG_AGE = I.ESCHC_AGENDA \n" +
                    "		UNION \n" +
                    "		SELECT\n" +
                    "			REF_DEP_OPER AGD \n" +
                    "		FROM\n" +
                    "			AA1CRFIS \n" +
                    "		WHERE\n" +
                    "			REF_DEP_DTA_AGENDA = I.ESCHC_DATA \n" +
                    "			AND REF_DEP_SERIE = I.ESCHC_SER_NOTA \n" +
                    "			AND REF_DEP_NRO_NOTA = I.ESCHC_NRO_NOTA \n" +
                    "			AND REF_DEP_LOJ_ORG = I.ESCHLJC_CODIGO \n" +
                    "			AND REF_DEP_OPER = I.ESCHC_AGENDA \n" +
                    "		UNION \n" +
                    "		SELECT\n" +
                    "			I.ESCHC_AGENDA AGD \n" +
                    "		FROM DUAL\n" +
                    "	)   \n" +
                    "	AND FIS.FIS_LOJ_ORG = I.ESCHLJC_CODIGO \n" +
                    "	AND FIS.FIS_DIG_ORG = I.ESCHLJC_DIGITO \n" +
                    "	AND FIS.FIS_NRO_NOTA = I.ESCHC_NRO_NOTA \n" +
                    "	AND FIS.FIS_DTA_AGENDA = I.ESCHC_DATA \n" +
                    "	AND DECODE(FIS.FIS_ENT_SAI,'E',FIS_LOJ_DST,FIS_LOJ_ORG) = 1 \n" +
                    "	AND FIS.FIS_SITUACAO <> '9' \n" +
                    "	AND TIP_CODIGO = DECODE(TBC_INTG_3,'E',I.ESCHLJC_CODIGO,I.ESCLC_CODIGO) \n" +
                    "	AND TIP_DIGITO = DECODE(TBC_INTG_3,'E',I.ESCHLJC_DIGITO,I.ESCLC_DIGITO) \n" +
                    "	AND TBC_AGENDA = I.ESCHC_AGENDA  \n" +
                    "	AND TBC_CODIGO = 0 \n" +
                    "	AND TBC_INTG_13 <> '4' \n" +
                    "	AND TBC_INTG_11 IN('V') \n" +
                    "	AND TBC_INTG_3 IN('S') \n" +
                    " ORDER BY\n" +
                    "	ESCHC_DATA DESC,\n" +
                    "	ESCHC_AGENDA DESC"
            )) {
                while (rst.next()) {
                    VendaHistoricoIMP imp = new VendaHistoricoIMP();
                    imp.setIdProduto(rst.getString("id_produto"));
                    
                    String ean = eans.get(imp.getIdProduto());
                    if (ean == null) {ean = imp.getIdProduto();}                    
                    imp.setEan(ean);
                    
                    ProdutoComplementoParcial pcp = aliquotas.get(imp.getIdProduto());                    
                    
                    imp.setData(format.parse(rst.getString("data")));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecoVenda(rst.getDouble("precovenda"));
                    imp.setQuantidade(rst.getDouble("quantidade"));
                    imp.setValorTotal(imp.getPrecoVenda() * imp.getQuantidade());
                    imp.setIcmsCredito(MathUtils.round((imp.getValorTotal() * ((100 - pcp.icmsCredRed) / 100)) * pcp.icmsCredAliq / 100, 2));
                    imp.setIcmsDebito(MathUtils.round((imp.getValorTotal() * ((100 - pcp.icmsDebRed) / 100)) * pcp.icmsDebAliq / 100, 2));
                    imp.setPisCofinsCredito(MathUtils.round((imp.getValorTotal() * ((100 - pcp.pisRed) / 100)) * pcp.pisCred / 100, 2));
                    imp.setPisCofinsDebito(MathUtils.round(imp.getValorTotal() * pcp.pisDeb / 100, 2));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("1yyMMdd");
    private Date dataInicioNFEntrada;

    public void setDataInicioNFEntrada(Date dataInicioNota) {
        this.dataInicioNFEntrada = dataInicioNota;
    }
    
    @Override
    public List<NotaFiscalIMP> getNotasFiscais() throws Exception {
        List<NotaFiscalIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "    NF.ID_FORNECEDOR,\n" +
                    "    NF.TIP_RAZAO_SOCIAL,\n" +
                    "    NF.FIS_OPER,\n" +
                    "    NF.NOTAFISCAL,\n" +
                    "    SUM(NF.CUSTOTOTAL) CUSTOTOTALNOTA,\n" +
                    "    NF.FIS_DTA_AGENDA,\n" +
                    "    NF.FIS_SITUACAO,\n" +
                    "    NF.DATA,\n" +
                    "    NF.ESCHC_AGENDA,\n" +
                    "    NF.ESCLC_CODIGO,\n" +
                    "    NF.DIGITO_LOJA,\n" +
                    "    NF.ESCHC_NRO_NOTA,\n" +
                    "    NF.ESCHC_SER_NOTA,\n" +
                    "    NF.FIS_SERIE_MR,\n" +
                    "    NF.VENCIMENTO\n" +
                    "FROM\n" +
                    "    (SELECT\n" +
                    "        TIP_CODIGO || TIP_DIGITO ID_FORNECEDOR,\n" +
                    "        RTRIM(DECODE(TIP_LOJ_CLI,\n" +
                    "                    'L',\n" +
                    "                    TIP_NOME_FANTASIA,\n" +
                    "                    DECODE(TIP_LOJ_CLI,\n" +
                    "                          'D',\n" +
                    "                          TIP_NOME_FANTASIA,\n" +
                    "                          TIP_RAZAO_SOCIAL))) AS TIP_RAZAO_SOCIAL,\n" +
                    "        FIS_OPER,\n" +
                    "        FIS_NRO_NOTA NOTAFISCAL,\n" +
                    "        FIS_DTA_AGENDA,\n" +
                    "        FIS_SITUACAO,\n" +
                    "        ESCHC_DATA DATA,\n" +
                    "        ((ENTSAIC_QUANTI_UN / ENTSAIC_BASE_EMB) * ENTSAIC_PRC_EMB) CUSTOTOTAL,\n" +
                    "        ESCHC_AGENDA,\n" +
                    "        ESCLC_CODIGO,\n" +
                    "        ESCLC_DIGITO DIGITO_LOJA,\n" +
                    "        ESCHC_NRO_NOTA,\n" +
                    "        ESCHC_SER_NOTA,\n" +
                    "        FIS_SERIE_MR,\n" +
                    "        (SELECT\n" +
                    "            MIN(FIV_DTA_VENCTO)\n" +
                    "        FROM\n" +
                    "            AA3LVENC\n" +
                    "        WHERE\n" +
                    "            FIV_LOJ_ORG    = FIS_LOJ_ORG AND\n" +
                    "            FIV_DIG_ORG    = FIS_DIG_ORG AND\n" +
                    "            FIV_NRO_NOTA   = FIS_NRO_NOTA AND\n" +
                    "            FIV_SERIE      = FIS_SERIE AND\n" +
                    "            FIV_DTA_AGENDA = FIS_DTA_AGENDA AND\n" +
                    "            FIV_OPER = FIS_OPER) AS VENCIMENTO\n" +
                    "    FROM\n" +
                    "        AG1IENSA I,\n" +
                    "        AA1CFISC FIS,\n" +
                    "        AA2CTIPO,\n" +
                    "        AA1CTCON\n" +
                    "    WHERE\n" +
                    "        ROWNUM >= 0 AND\n" +
                    "        I.ESCHC_DATA >= 1190101 AND\n" +
                    "        I.ESITC_DIGITO = 0 AND\n" +
                    "        NVL(I.ENTSAIC_SITUACAO, ' ') <> 'E' AND\n" +
                    "        NVL(I.ENTSAIC_SITUACAO, ' ') <> '9' AND\n" +
                    "        I.ESCHC_AGENDA\n" +
                    "            IN (SELECT\n" +
                    "                    FIG_AGD AGD\n" +
                    "                FROM\n" +
                    "                    AA2CFIGS\n" +
                    "                WHERE\n" +
                    "                    FIG_DTA = I.ESCHC_DATA\n" +
                    "                    AND FIG_SER = I.ESCHC_SER_NOTA\n" +
                    "                    AND FIG_NTA = I.ESCHC_NRO_NOTA\n" +
                    "                    AND FIG_ORG = I.ESCHLJC_CODIGO\n" +
                    "                    AND FIG_AGE = I.ESCHC_AGENDA\n" +
                    "                UNION\n" +
                    "                SELECT\n" +
                    "                    REF_DEP_OPER AGD\n" +
                    "                FROM\n" +
                    "                    AA1CRFIS\n" +
                    "                WHERE\n" +
                    "                    REF_DEP_DTA_AGENDA = I.ESCHC_DATA\n" +
                    "                  AND REF_DEP_SERIE = I.ESCHC_SER_NOTA\n" +
                    "                  AND REF_DEP_NRO_NOTA = I.ESCHC_NRO_NOTA\n" +
                    "                  AND REF_DEP_LOJ_ORG = I.ESCHLJC_CODIGO\n" +
                    "                  AND REF_DEP_OPER = I.ESCHC_AGENDA\n" +
                    "                UNION\n" +
                    "                  SELECT\n" +
                    "                    I.ESCHC_AGENDA AGD\n" +
                    "                  FROM\n" +
                    "                    DUAL) AND\n" +
                    "        FIS.FIS_LOJ_ORG = I.ESCHLJC_CODIGO AND\n" +
                    "        FIS.FIS_DIG_ORG = I.ESCHLJC_DIGITO AND\n" +
                    "        FIS.FIS_NRO_NOTA = I.ESCHC_NRO_NOTA AND\n" +
                    "        FIS.FIS_DTA_AGENDA = I.ESCHC_DATA AND\n" +
                    "        DECODE(FIS.FIS_ENT_SAI,'E',FIS_LOJ_DST,FIS_LOJ_ORG) || ESCLC_DIGITO = " + getLojaOrigem() + " AND\n" +
                    "        FIS.FIS_SITUACAO <> '9' AND\n" +
                    "        TIP_CODIGO = DECODE(TBC_INTG_3,'E',I.ESCHLJC_CODIGO,I.ESCLC_CODIGO) AND\n" +
                    "        TIP_DIGITO = DECODE(TBC_INTG_3,'E',I.ESCHLJC_DIGITO,I.ESCLC_DIGITO) AND\n" +
                    "        TBC_AGENDA = I.ESCHC_AGENDA AND\n" +
                    "        TBC_CODIGO = 0 AND\n" +
                    "        TBC_INTG_13 <> '4' AND\n" +
                    "        TBC_INTG_11 IN ('C','T','D','R','O') AND\n" +
                    "        TBC_INTG_3 IN ('E')\n" +
                    "    ORDER BY\n" +
                    "        ESCHC_DATA DESC,\n" +
                    "        ESCHC_AGENDA DESC) NF\n" +
                    "GROUP BY\n" +
                    "    NF.ID_FORNECEDOR,\n" +
                    "    NF.TIP_RAZAO_SOCIAL,\n" +
                    "    NF.FIS_OPER,\n" +
                    "    NF.NOTAFISCAL,\n" +
                    "    NF.FIS_DTA_AGENDA,\n" +
                    "    NF.FIS_SITUACAO,\n" +
                    "    NF.DATA,\n" +
                    "    NF.ESCHC_AGENDA,\n" +
                    "    NF.ESCLC_CODIGO,\n" +
                    "    NF.DIGITO_LOJA,\n" +
                    "    NF.ESCHC_NRO_NOTA,\n" +
                    "    NF.ESCHC_SER_NOTA,\n" +
                    "    NF.FIS_SERIE_MR,\n" +
                    "    NF.VENCIMENTO\n" +
                    "ORDER BY\n" +
                    "    NF.FIS_DTA_AGENDA"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while(rs.next()) {
                    NotaFiscalIMP imp = new NotaFiscalIMP();
                    imp.setIdDestinatario(rs.getString("id_fornecedor"));
                    imp.setTipoDestinatario(TipoDestinatario.FORNECEDOR);
                    imp.setNumeroNota(rs.getInt("notafiscal"));
                    imp.setValorTotal(rs.getDouble("custototalnota"));
                    imp.setDataEmissao(format.parse(rs.getString("data")));
                    imp.setDataEntradaSaida(format.parse(rs.getString("data")));
                    imp.setDataHoraAlteracao(format.parse(rs.getString("data")));
                    imp.setSerie(Utils.formataNumero(rs.getString("eschc_ser_nota")));
                    imp.setOperacao(NotaOperacao.ENTRADA);
                    imp.setTipoNota(TipoNota.NORMAL);
                    formataIDNotaFiscal(imp);
                    
                    getNotasItem(String.valueOf(imp.getNumeroNota()), imp.getIdDestinatario(), imp);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    private void getNotasItem(String numeroNota, String idFornecedor, NotaFiscalIMP imp) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "    * \n" +
                    "FROM \n" +
                    "    (SELECT \n" +
                    "	     TIP_CODIGO || TIP_DIGITO ID_FORNECEDOR,\n" +
                    "        RTRIM(DECODE(TIP_LOJ_CLI, \n" +
                    "                    'L', \n" +
                    "                    TIP_NOME_FANTASIA,\n" +
                    "                    DECODE(TIP_LOJ_CLI,\n" +
                    "                          'D',\n" +
                    "                          TIP_NOME_FANTASIA,\n" +
                    "                          TIP_RAZAO_SOCIAL))) AS TIP_RAZAO_SOCIAL, \n" +
                    "        FIS_OPER, \n" +
                    "        FIS_NRO_NOTA NOTAFISCAL,\n" +
                    "        ESITC_CODIGO ID_PRODUTO,\n" +
                    "        P.GIT_DESCRICAO DESCRICAO,\n" +        
                    "        ENTSAIC_PRC_UN CUSTOUNITARIO,\n" +
                    "        ROUND(ENTSAIC_QUANTI_UN / ENTSAIC_BASE_EMB, 2) QUANTIDADE,\n" +
                    "        (ENTSAIC_PRC_EMB * ROUND(ENTSAIC_QUANTI_UN / ENTSAIC_BASE_EMB, 2)) CUSTOTOTAL,\n" +        
                    "        ENTSAIC_BASE_EMB QTDEMBALAGEM,\n" +        
                    "        ENTSAIC_PRC_EMB CUSTOEMBALAGEM,\n" +
                    "        ENTSAIC_TPO_EMB EMBALAGEM,\n" +
                    "        FIS_DTA_AGENDA, \n" +
                    "        FIS_SITUACAO,  \n" +
                    "        ESCHC_DATA DATA,\n" +
                    "        ENTSAIC_VLR_ICM,\n" +
                    "        I.ENTSAIC_PERC_ICMS ICMS,\n" +
                    "        I.ENTSAIC_PERC_IPI,\n" +
                    "        ESCHC_AGENDA, \n" +
                    "        ESCLC_CODIGO, \n" +
                    "        ESCLC_DIGITO DIGITO_LOJA, \n" +
                    "        ESCHC_NRO_NOTA, \n" +
                    "        ESCHC_SER_NOTA, \n" +
                    "        FIS_SERIE_MR, \n" +
                    "        (SELECT \n" +
                    "            MIN(FIV_DTA_VENCTO) \n" +
                    "        FROM \n" +
                    "            AA3LVENC \n" +
                    "        WHERE \n" +
                    "            FIV_LOJ_ORG    = FIS_LOJ_ORG AND  \n" +
                    "            FIV_DIG_ORG    = FIS_DIG_ORG AND  \n" +
                    "            FIV_NRO_NOTA   = FIS_NRO_NOTA AND   \n" +
                    "            FIV_SERIE      = FIS_SERIE AND   \n" +
                    "            FIV_DTA_AGENDA = FIS_DTA_AGENDA AND   \n" +
                    "            FIV_OPER       = FIS_OPER) AS VENCIMENTO \n" +
                    "    FROM  \n" +
                    "        AG1IENSA I,\n" +
                    "        AA1CFISC FIS, \n" +
                    "        AA2CTIPO, \n" +
                    "        AA1CTCON, \n" +
                    "        AA3CITEM P \n" +        
                    "    WHERE \n" +
                    "        ROWNUM >= 0 AND \n" +
                    "        I.ESCHC_DATA >= 1190101 AND\n" +
                    "        FIS_NRO_NOTA = " + numeroNota + " AND\n" +
                    "        TIP_CODIGO || TIP_DIGITO = " + idFornecedor + " AND\n" +        
                    "        I.ESITC_DIGITO = 0 AND \n" +
                    "        NVL(I.ENTSAIC_SITUACAO, ' ') <> 'E' AND \n" +
                    "        NVL(I.ENTSAIC_SITUACAO, ' ') <> '9' AND \n" +
                    "        I.ESCHC_AGENDA \n" +
                    "            IN (SELECT \n" +
                    "                    FIG_AGD AGD \n" +
                    "                FROM \n" +
                    "                    AA2CFIGS \n" +
                    "                WHERE \n" +
                    "                    FIG_DTA = I.ESCHC_DATA \n" +
                    "                    AND FIG_SER = I.ESCHC_SER_NOTA \n" +
                    "                    AND FIG_NTA = I.ESCHC_NRO_NOTA \n" +
                    "                    AND FIG_ORG = I.ESCHLJC_CODIGO \n" +
                    "                    AND FIG_AGE = I.ESCHC_AGENDA \n" +
                    "                UNION \n" +
                    "                SELECT \n" +
                    "                    REF_DEP_OPER AGD \n" +
                    "                FROM \n" +
                    "                    AA1CRFIS \n" +
                    "                WHERE \n" +
                    "                    REF_DEP_DTA_AGENDA = I.ESCHC_DATA \n" +
                    "                  AND REF_DEP_SERIE = I.ESCHC_SER_NOTA \n" +
                    "                  AND REF_DEP_NRO_NOTA = I.ESCHC_NRO_NOTA \n" +
                    "                  AND REF_DEP_LOJ_ORG = I.ESCHLJC_CODIGO \n" +
                    "                  AND REF_DEP_OPER = I.ESCHC_AGENDA \n" +
                    "                UNION \n" +
                    "                  SELECT \n" +
                    "                    I.ESCHC_AGENDA AGD \n" +
                    "                  FROM \n" +
                    "                    DUAL) AND   \n" +
                    "        FIS.FIS_LOJ_ORG = I.ESCHLJC_CODIGO AND \n" +
                    "        FIS.FIS_DIG_ORG = I.ESCHLJC_DIGITO AND \n" +
                    "        FIS.FIS_NRO_NOTA = I.ESCHC_NRO_NOTA AND \n" +
                    "        FIS.FIS_DTA_AGENDA = I.ESCHC_DATA AND \n" +
                    "        DECODE(FIS.FIS_ENT_SAI,'E',FIS_LOJ_DST,FIS_LOJ_ORG) || ESCLC_DIGITO = " + getLojaOrigem() + " AND \n" +
                    "        FIS.FIS_SITUACAO <> '9' AND \n" +
                    "        TIP_CODIGO = DECODE(TBC_INTG_3,'E',I.ESCHLJC_CODIGO,I.ESCLC_CODIGO) AND \n" +
                    "        TIP_DIGITO = DECODE(TBC_INTG_3,'E',I.ESCHLJC_DIGITO,I.ESCLC_DIGITO) AND \n" +
                    "        TBC_AGENDA = I.ESCHC_AGENDA AND \n" +
                    "        I.ESITC_CODIGO = P.GIT_COD_ITEM AND\n" +        
                    "        TBC_CODIGO = 0 AND\n" +
                    "        TBC_INTG_13 <> '4' AND \n" +
                    "        TBC_INTG_11 IN ('C','T','D','R','O') AND \n" +
                    "        TBC_INTG_3 IN ('E') \n" +
                    "    ORDER BY \n" +
                    "        ESCHC_DATA DESC, \n" +
                    "        ESCHC_AGENDA DESC)"
            )) {
                while (rst.next()) {
                    NotaFiscalItemIMP item = imp.addItem();
                    item.setId(
                            rst.getString("ID_FORNECEDOR"), "-",
                            rst.getString("NOTAFISCAL"), "-",
                            rst.getString("ID_PRODUTO"), "-",
                            rst.getString("CUSTOUNITARIO"), "-",
                            rst.getString("QUANTIDADE"), "-",
                            rst.getString("CUSTOEMBALAGEM")
                    );
                    item.setIdProduto(rst.getString("ID_PRODUTO"));
                    item.setDescricao(rst.getString("DESCRICAO"));
                    item.setUnidade(rst.getString("EMBALAGEM"));
                    item.setQuantidadeEmbalagem(rst.getInt("QTDEMBALAGEM"));
                    item.setQuantidade(rst.getDouble("QUANTIDADE"));
                    item.setValorTotalProduto(rst.getDouble("CUSTOTOTAL"));
                    item.setIcmsAliquota(rst.getDouble("ICMS"));
                    item.setCfop("1.102");
                    if(item.getIcmsAliquota() > 0) {
                        item.setIcmsCst(0);
                    }
                    item.setIcmsValor(rst.getDouble("ENTSAIC_VLR_ICM"));
                }
            }
        }
    }
    
    private void formataIDNotaFiscal(NotaFiscalIMP imp) {
        String formatada = "";
        formatada = String.format("%s-%s-%s-%s", 
                imp.getIdDestinatario(), 
                imp.getNumeroNota(),
                imp.getDataEmissao(),
                imp.getValorTotal());
        imp.setId(formatada);
    }
}
