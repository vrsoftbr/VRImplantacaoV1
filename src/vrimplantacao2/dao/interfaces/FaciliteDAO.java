package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class FaciliteDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "FACILITE";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    DFIS_CODIGO id,\n" +
                    "    DFIS_DESCRICAO descricao,\n" +
                    "    DFIS_ALIQUOTA aliquota\n" +
                    "from\n" +
                    "    deptofis\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    result.add(
                            new MapaTributoIMP(
                                    rst.getString("id"), 
                                    rst.getString("descricao")
                            )
                    );
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    E.EMP_CODIGO,\n" +
                    "    E.EMP_DESCRICAO\n" +
                    "from\n" +
                    "    EMPRESA E\n" +
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("emp_codigo"), rst.getString("emp_descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    g.gru_codigo merc1,\n" +
                    "    g.gru_descricao merc1_descricao,\n" +
                    "    coalesce(s.sgru_codigo, '01') merc2,\n" +
                    "    coalesce(nullif(trim(s.sgru_descricao),''), g.gru_descricao) merc2_descricao\n" +
                    "from\n" +
                    "    grupo g\n" +
                    "    left join sgrupo s on\n" +
                    "        g.gru_codigo = s.sgru_grupo\n" +
                    "order by\n" +
                    "    merc1, merc2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_descricao"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_descricao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    PROFA_ID,\n" +
                    "    PROFA_DESCRICAO\n" +
                    "from\n" +
                    "    produtofamilia\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("PROFA_ID"));
                    imp.setDescricao(rst.getString("PROFA_DESCRICAO"));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.pro_codigo id,\n" +
                    "    p.pro_datacadastro datacadastro,\n" +
                    "    ean.ean,\n" +
                    "    coalesce(nullif(p.pro_qtdeembal, 0), 1) qtdembalagemcotacao,\n" +
                    "    coalesce(ean.unidade, p.pro_unidade, 'UN') unidade,\n" +
                    "    case when upper(p.pro_pesavel) = 'TRUE' then 1 else 0 end ebalanca,\n" +
                    "    p.pro_descricao descricaocompleta,\n" +
                    "    p.pro_nomereduzido descricaoreduzida,\n" +
                    "    p.pro_grupo merc1,\n" +
                    "    coalesce(p.pro_subgrupo, '01') merc2,\n" +
                    "    p.pro_idprodutofamilia id_familia,\n" +
                    "    p.pro_peso peso,\n" +
                    "    p.pro_estminimo estoqueminimo,\n" +
                    "    p.pro_estmaximo estoquemaximo,\n" +
                    "    coalesce(p.pro_estoqueatual, 0) estoque,\n" +
                    "    p.pro_margemv margem,\n" +
                    "    p.pro_valorcompra custo,\n" +
                    "    p.pro_valorvista preco,\n" +
                    "    case when p.pro_datafinal is null then 1 else 0 end descontinuado,\n" +
                    "    ncm.cfis_numero ncm,\n" +
                    "    ncm.cfis_idtabgenericaicest cest,\n" +
                    "    p.pro_deptofis icms_id,\n" +
                    "    substring(p.pro_stpis from 1 for 2) pis_s,\n" +
                    "    substring(p.pro_stpisentrada from 1 for 2) pis_e,\n" +
                    "    p.pro_natreceitaisentapis pis_natreceita,\n" +
                    "    case upper(p.pro_promocao) when 'TRUE' then 1 else 0 end promo,\n" +
                    "    p.pro_datapromocao promo_dtinicio,\n" +
                    "    p.pro_datafinal promo_dtfinal,\n" +
                    "    p.pro_valorpromocao promo_valor,\n" +
                    "    p.pro_vendaatac atacado_valor,\n" +
                    "    p.pro_fabricante fabricante\n" +
                    "from\n" +
                    "    produto p\n" +
                    "    left join clasfis ncm on\n" +
                    "        p.pro_clasfiscal = ncm.cfis_codigo\n" +
                    "    left join (\n" +
                    "        select distinct\n" +
                    "            a.procb_produto id_produto,\n" +
                    "            a.procb_codigobarras ean,\n" +
                    "            a.procb_unidade unidade\n" +
                    "        from\n" +
                    "            PRODUTOCODBARRAS a\n" +
                    "        where\n" +
                    "            a.procb_empresa = '" + getLojaOrigem() + "'  and\n" +
                    "            a.procb_codigobarras similar to '[0-9]*' and\n" +
                    "            not nullif(trim(a.procb_codigobarras),'') is null\n" +
                    "        union\n" +
                    "        select distinct\n" +
                    "            p.pro_codigo id_produto,\n" +
                    "            p.pro_codigobarra ean,\n" +
                    "            p.pro_unidade unidade\n" +
                    "        from\n" +
                    "            produto p\n" +
                    "        where\n" +
                    "            p.pro_empresa = '" + getLojaOrigem() + "' and\n" +
                    "            p.pro_codigobarra similar to '[0-9]*' and\n" +
                    "            not nullif(trim(p.pro_codigobarra),'') is null\n" +
                    "    ) ean on\n" +
                    "        p.pro_codigo = ean.id_produto\n" +
                    "order by\n" +
                    "    id;"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsCreditoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoId(rst.getString("icms_id"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_s"));
                    imp.setPiscofinsCstCredito(rst.getString("pis_e"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("pis_natreceita"));
                    imp.setAtacadoPreco(rst.getDouble("atacado_valor"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}