package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class SircomDAO extends InterfaceDAO implements MapaTributoProvider {

    //private Date vendasDataInicio = null;
    //private Date vendasDataTermino = null;
    private static final Logger LOG = Logger.getLogger(SircomDAO.class.getName());

    @Override
    public String getSistema() {
        return "SIRCOM";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.OFERTA
                }
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    cod_cf id,\n" +
                    "    descricao,\n" +
                    "    per_aliq aliquota,\n" +
                    "    substring(sit_trib_nf from 2 for 3) cst,\n" +
                    "    per_red reducao\n" +
                    "from\n" +
                    "    cat_fiscal\n" +
                    "where\n" +
                    "    cod_cf in (select cast(cod_cat_fiscal as integer) from produtos)\n" +
                    "order by\n" +
                    "    2"
            )) {
                while (rs.next()) {
                    // D - Aliquota de Débito
                    result.add(new MapaTributoIMP("D" + rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
            
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    cod_cf id,\n" +
                    "    descricao,\n" +
                    "    per_aliq aliquota,\n" +
                    "    substring(sit_trib_nf from 2 for 3) cst,\n" +
                    "    per_red reducao\n" +
                    "from\n" +
                    "    cat_fiscal\n" +
                    "where\n" +
                    "    cod_cf in (select cast(cod_cat_fis_entrada as integer) from produtos)\n" +
                    "order by\n" +
                    "    2")) {
                while (rs.next()) {
                    // C - Aliquota de Crédito
                    result.add(new MapaTributoIMP("C" + rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "     cod_empr id,\n"
                    + "     razao_social razao\n"
                    + "from empresas"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {

            //<editor-fold defaultstate="collapsed" desc="MERCADOLOGICO 1">
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    substring(cod_clas from 1 for 2) as merc1,\n"
                    + "    descricao\n"
                    + "from classificacao\n"
                    + "where substring(cod_clas from 3 for 3) = '000'\n"
                    + "and substring(cod_clas from 6 for 3) = '000'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();

                    imp.setId(rs.getString("merc1"));
                    imp.setDescricao(rs.getString("descricao"));

                    merc.put(imp.getId(), imp);
                }
            }
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="MERCADOLOGICO 2">
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    substring(cod_clas from 1 for 2) as merc1,\n"
                    + "    substring(cod_clas from 3 for 3) as merc2,\n"
                    + "    descricao\n"
                    + "from classificacao\n"
                    + "where substring(cod_clas from 3 for 3) != '000'\n"
                    + "and substring(cod_clas from 6 for 3) = '000'\n"
                    + "order by 1, 2"
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP merc2 = merc.get(rs.getString("merc1"));
                    if (merc2 != null) {
                        merc2.addFilho(
                                rs.getString("merc2"),
                                rs.getString("descricao")
                        );
                    }
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="MERCADOLOGICO 3">
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    substring(cod_clas from 1 for 2) as merc1,\n"
                    + "    substring(cod_clas from 3 for 3) as merc2,\n"
                    + "    substring(cod_clas from 6 for 3) as merc3,\n"
                    + "    descricao\n"
                    + "from classificacao\n"
                    + "where substring(cod_clas from 3 for 3) != '000'\n"
                    + "and substring(cod_clas from 6 for 3) != '000'\n"
                    + "order by 1, 2, 3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("descricao")
                            );
                        }
                    }
                }
            }
            //</editor-fold>            
        }

        return new ArrayList<>(merc.values());
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "    p.cod_plu as id,\n"
                    + "    p.descricao\n"
                    + "from produtos p\n"
                    + "where p.cod_plu in (select cod_plu_msmprc from produtos)\n"
                    + "order by p.cod_plu"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    c.cod_plu id,\n"
                    + "    c.cod_ean ean\n"
                    + "from\n"
                    + "    cod_ean_aux c\n"
                    + "join produtos p on c.cod_plu = p.cod_plu\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "     p.cod_plu importid,\n" +
                    "     p.data_inc datacadastro,\n" +
                    "     p.cod_ean ean,\n" +
                    "     emb_cpra_und tipoembalagem,\n" +
                    "     p.emb_cpra_tipo tipoembalagemcompra,\n" +
                    "     p.emb_cpra_dim qtdembalagemcompra,\n" +
                    "     p.granel ebalanca,\n" +
                    "     p.granel_val validade,\n" +
                    "     cast(p.cod_cat_fiscal as integer) idicms_debito,\n" +
                    "     p.cod_cat_fis_entrada idicms_credito,\n" +
                    "     p.icms_s,\n" +
                    "     p.icms_e,\n" +
                    "     p.per_iva,\n" +
                    "     p.descricao descricaocompleta,\n" +
                    "     p.descr_reduzida descricaoreduzida,\n" +
                    "     p.descricao descricaogondola,\n" +
                    "     p.cod_plu_msmprc as idfamiliaproduto,\n" +
                    "     substring(p.cod_classif from 1 for 2) as merc1,\n" +
                    "     substring(p.cod_classif from 3 for 3) as merc2,\n" +
                    "     substring(p.cod_classif from 6 for 3) as merc3,\n" +
                    "     cl.mar_custo as margem,\n" +
                    "     e.estoque_max estoquemaximo,\n" +
                    "     e.estoque_min estoqueminimo,\n" +
                    "     e.estoque estoque,\n" +
                    "     e.custo_final custosemimposto,\n" +
                    "     e.custo_final custocomimposto,\n" +
                    "     e.preco_venda precovenda,\n" +
                    "     case when upper(p.inativo) = 'S' then 0 else 1 end situacaocadastro,\n" +
                    "     p.codigo_ncm ncm,\n" +
                    "     p.codigo_cest cest,\n" +
                    "     p.piscofins_mod as cstpiscofins\n" +
                    " from \n" +
                    "     produtos p\n" +
                    " left join \n" +
                    "     estoque e\n" +
                    "     on p.cod_plu = e.cod_plu\n" +
                    " left join classificacao cl \n" +
                    "     on cl.cod_clas = p.cod_classif\n" +         
                    " where loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("importid"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.setTipoEmbalagemCotacao(rs.getString("tipoembalagemcompra"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcompra"));
                    imp.seteBalanca("S".equals(rs.getString("ebalanca")));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setIdFamiliaProduto(rs.getString("idfamiliaproduto"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    //imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("cstpiscofins"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpiscofins"));

                    // Icms Debito
                    imp.setIcmsDebitoId("D" + rs.getString("idicms_debito"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    
                    // Icms Credito
                    imp.setIcmsCreditoId("C" + rs.getString("idicms_credito"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());
                    

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.CUSTO_SEM_IMPOSTO) {
            try (Statement stm2 = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rs2 = stm2.executeQuery(
                        "WITH\n"
                        + "alteracao AS(\n"
                        + "select\n"
                        + "    pc.cod_plu,\n"
                        + "    MAX(data) as alteracao\n"
                        + "   FROM itens_nota_forn as pc\n"
                        + "  where pc.loja in(" + getLojaOrigem() + ") group by pc.cod_plu\n"
                        + ")\n"
                        + "select\n"
                        + "    pc.cod_plu,\n"
                        + "    pc.custo_unitario,\n"
                        + "    pc.data,\n"
                        + "    pc.qtde_emb,\n"
                        + "    (pc.custo_unitario / pc.qtde_emb) as custosemimposto\n"
                        + "from itens_nota_forn as pc\n"
                        + "inner join alteracao as alt on alt.cod_plu = pc.cod_plu AND alt.alteracao = pc.data\n"
                        + "where  pc.loja in (" + getLojaOrigem() + ")"
                )) {
                    while (rs2.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rs2.getString("cod_plu"));
                        imp.setCustoSemImposto(rs2.getDouble("custosemimposto"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        
        if (opt == OpcaoProduto.EXCECAO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "     p.cod_plu importid,\n"
                        + "     p.codigo_ncm ncm,\n"
                        + "     p.per_iva,\n"
                        + "     c1.sit_trib_nf as cst_debito,\n"
                        + "     c1.per_aliq as aliquota_debito,\n"
                        + "     c1.per_red as reducao_debito,\n"
                        + "     c2.sit_trib_nf as cst_credito,\n"
                        + "     c2.per_aliq as aliquota_credito,\n"
                        + "     c2.per_red as reducao_credito\n"
                        + "from produtos p\n"
                        + "join cat_fiscal c1 on c1.cod_cf = cast(p.cod_cat_fiscal as integer)\n"
                        + "join cat_fiscal c2 on c2.cod_cf = cod_cat_fis_entrada\n"
                        + "where p.per_iva > 0\n"
                        + "and p.codigo_ncm is not null\n"
                        + "and p.codigo_ncm != ''\n"
                        + "order by 1"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("importid"));
                        imp.setPautaFiscalId(imp.getImportId());
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }
    
    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "     p.cod_plu importid,\n"
                    + "     p.codigo_ncm ncm,\n"
                    + "     p.per_iva,\n"
                    + "     c1.sit_trib_nf as cst_debito,\n"
                    + "     c1.per_aliq as aliquota_debito,\n"
                    + "     c1.per_red as reducao_debito,\n"
                    + "     c2.sit_trib_nf as cst_credito,\n"
                    + "     c2.per_aliq as aliquota_credito,\n"
                    + "     c2.per_red as reducao_credito\n"
                    + "from produtos p\n"
                    + "join cat_fiscal c1 on c1.cod_cf = cast(p.cod_cat_fiscal as integer)\n"
                    + "join cat_fiscal c2 on c2.cod_cf = cod_cat_fis_entrada\n"
                    + "where p.per_iva > 0\n"
                    + "and p.codigo_ncm is not null\n"
                    + "and p.codigo_ncm != ''\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rst.getString("importid"));
                    imp.setTipoIva(TipoIva.PERCENTUAL);
                    imp.setIva(rst.getDouble("per_iva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rst.getString("ncm"));
                    
                    // DÉBITO
                    if ((rst.getDouble("aliquota_debito") > 0) && (rst.getDouble("reducao_debito") == 0)) {
                        
                        imp.setAliquotaDebito(0, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(0, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        
                    } else if ((rst.getDouble("aliquota_debito") > 0) && (rst.getDouble("reducao_debito") > 0)) {
                        
                        imp.setAliquotaDebito(20, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(20, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        
                    } else {
                        
                        imp.setAliquotaDebito(rst.getInt("cst_debito"), rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(rst.getInt("cst_debito"), rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                    }
                    
                    // CRÉDITO
                    if ((rst.getDouble("aliquota_credito") > 0) && (rst.getDouble("reducao_credito") == 0)) {
                        
                        imp.setAliquotaCredito(0, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(0, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        
                    } else if ((rst.getDouble("aliquota_credito") > 0) && (rst.getDouble("reducao_credito") > 0)) {
                        
                        imp.setAliquotaCredito(20, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(20, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        
                    } else {
                        
                        imp.setAliquotaCredito(rst.getInt("cst_credito"), rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(rst.getInt("cst_credito"), rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
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
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    f.cod_forn importId,\n"
                    + "    f.nome_razao razao,\n"
                    + "    f.fantasia,\n"
                    + "    f.cnpj_cpf,\n"
                    + "    f.ie_rg,\n"
                    + "    f.home_page,\n"
                    + "    case f.bloqueado when 'S' then 1 else 0 end bloqueado,\n"
                    + "    f.endereco,\n"
                    + "    f.end_numero numero,\n"
                    + "    f.bairro,\n"
                    + "    f.cod_munic_ibge ibge_municipio,\n"
                    + "    f.cidade municipio,\n"
                    + "    f.estado uf,\n"
                    + "    f.cep,\n"
                    + "    f.fone1 tel_principal,\n"
                    + "    f.vr_min_pedido valor_minimo_pedido,\n"
                    + "    f.data_inc datacadastro,\n"
                    + "    f.observ observacao,\n"
                    + "    f.prazo_entrega prazoEntrega,\n"
                    + "    f.contato_ger,\n"
                    + "    f.fone_gerencia,\n"
                    + "    f.nome_vendedor,\n"
                    + "    f.fone_vendedor,\n"
                    + "    f.email_nfe,\n"
                    + "    c.contato as nome_contato,\n"
                    + "    c.setor as setor_contato,\n"
                    + "    c.telefone as telefone_contato,\n"
                    + "    c.fonefax as fax_contato,\n"
                    + "    c.fonecel as celular_contato,\n"
                    + "    c.email as email_contato,\n"
                    + "    f.fabricante\n"        
                    + "from fornecedores f\n"
                    + "left join forn_contatos c on\n"
                    + "    c.cod_forn = f.cod_forn"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("importId"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setAtivo(true);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setValor_minimo_pedido(rs.getDouble("valor_minimo_pedido"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                     
                    imp.setTipoFornecedor("S".equals(rs.getString("fabricante").trim()) 
                            ? TipoFornecedor.INDUSTRIA
                            : TipoFornecedor.DISTRIBUIDOR);
                    
                    if (rs.getString("observacao") != null && !"".equals(rs.getString("observacao"))) {
                        imp.setObservacao(rs.getString("observacao"));
                    }
                    imp.setPrazoEntrega(rs.getInt("prazo_entrega"));

                    if (rs.getString("tel_principal") != null
                            && !"0".equals(rs.getString("tel_principal").trim())
                            && !"".equals(rs.getString("tel_principal").trim())) {

                        if (rs.getString("tel_principal").trim().startsWith("0")) {
                            imp.setTel_principal(rs.getString("tel_principal").substring(1));
                        } else {
                            imp.setTel_principal(rs.getString("tel_principal"));
                        }

                    }
                    
                    imp.addContato(
                            rs.getString("nome_contato") + " " + rs.getString("setor_contato"), 
                            rs.getString("telefone_contato"), 
                            rs.getString("celular_contato"), 
                            TipoContato.COMERCIAL, 
                            rs.getString("email_contato") != null ? rs.getString("email_contato").toLowerCase() : rs.getString("email_contato")
                    );
                    
                    if ((rs.getString("fax_contato") != null) &&
                            (!rs.getString("fax_contato").trim().isEmpty())) {
                        
                        imp.addTelefone(
                                rs.getString("nome_contato") + " " + rs.getString("setor_contato"), 
                                rs.getString("fax_contato")
                        );
                    }
                    
                    if (rs.getString("fone_gerencia") != null
                            && !"0".equals(rs.getString("fone_gerencia"))
                            && !"".equals(rs.getString("fone_gerencia"))) {
                        imp.addContato((rs.getString("contato_ger")),
                                (rs.getString("fone_gerencia")),
                                null,
                                TipoContato.COMERCIAL,
                                null);
                    }

                    if (rs.getString("home_page") != null
                            && !"".equals(rs.getString("home_page"))) {
                        imp.addContato(
                                (rs.getString("home_page")),
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                null);
                    }
                    if (rs.getString("email_nfe") != null
                            && !"".equals(rs.getString("email_nfe"))) {
                        imp.addContato("EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rs.getString("email_nfe"));
                    }
                    if (rs.getString("nome_vendedor") != null
                            && !"".equals(rs.getString("nome_vendedor"))) {
                        imp.addContato(
                                (rs.getString("nome_vendedor")),
                                (rs.getString("fone_vendedor")),
                                null,
                                TipoContato.FINANCEIRO,
                                null);
                    }

                    /*if (imp.getCnpj_cpf() == null) {
                     imp.setCnpj_cpf(rs.getString("cpf"));
                     }*/
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    f.cod_plu id_produto,\n"
                    + "    f.cod_forn id_fornecedor,\n"
                    + "    f.cod_prod_forn codexterno,\n"
                    + "    p.emb_cpra_dim qtdEmbalagem,\n"
                    + "    f.data_alt dataalteracao\n"
                    + "from cod_ref_for f\n"
                    + "    left join produtos p\n"
                    + "    on p.cod_plu = f.cod_plu\n"
                    + "order by 1,2"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdEmbalagem"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    id,\n"
                    + "    numero,\n"
                    + "    ativo,\n"
                    + "    nome,\n"
                    + "    endereco,\n"
                    + "    complemento,\n"
                    + "    bairro,\n"
                    + "    cidade,\n"
                    + "    uf,\n"
                    + "    cep,\n"
                    + "    tipo_pessoa,\n"
                    + "    cpf,\n"
                    + "    cnpj,\n"
                    + "    insc_estadual,\n"
                    + "    rg,\n"
                    + "    ddd,\n"
                    + "    telefone,\n"
                    + "    fax, \n"
                    + "    celular,\n"
                    + "    site,\n"
                    + "    data_cadastro,\n"
                    + "    data_aniversario,\n"
                    + "    limite_credito, \n"
                    + "    email,\n"
                    + "    s_codigo_municipio,\n"
                    + "    end_num,\n"
                    + "    comentarios\n"
                    + "from \n"
                    + "    clientes\n"
                    + "order by\n"
                    + "    id "
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("end_num"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setCnpj(rs.getString("cnpj"));
                    if (imp.getCnpj() == null && "".equals(imp.getCnpj())) {
                        imp.setCnpj(rs.getString("cpf"));
                    }
                    imp.setInscricaoestadual(rs.getString("insc_estadual"));
                    if (imp.getInscricaoestadual() == null && "".equals(imp.getInscricaoestadual())) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    }
                    imp.setTelefone(rs.getString("ddd") + rs.getString("telefone"));
                    imp.setCelular(rs.getString("ddd") + rs.getString("celular"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setDataNascimento(rs.getDate("data_aniversario"));
                    imp.setValorLimite(rs.getDouble("limite_credito"));
                    imp.setEmail(rs.getString("email"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    prm.cod_plu as id_produto,\n"
                    + "    prm.data_inicio,\n"
                    + "    prm.data_fim,\n"
                    + "    prm.preco_prom as preco_oferta,\n"
                    + "    pre.preco_venda\n"
                    + "from produtos p\n"
                    + "join promocoes prm on prm.cod_plu = p.cod_plu\n"
                    + "join estoque pre on pre.cod_plu = p.cod_plu\n"
                    + "where data_fim > current_date\n"
                    + "and prm.loja = " + getLojaOrigem() + "\n"
                    + "and pre.loja = " + getLojaOrigem() + "\n"
                    + "order by data_inicio"
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setDataInicio(rs.getDate("data_inicio"));
                    imp.setDataFim(rs.getDate("data_fim"));
                    imp.setPrecoOferta(rs.getDouble("preco_oferta"));
                    imp.setPrecoNormal(rs.getDouble("preco_venda"));
                    imp.setTipoOferta(TipoOfertaVO.CAPA);

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
