package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class GestorDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Gestor";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.RECEITA
        ));
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct "
                    + "    icm.tributacao as cst,\n"
                    + "    icm.icms as aliquota,\n"
                    + "    icm.icms_reducao as reducao\n"
                    + " SM_CD_ES_PRODUTO_EF icm \n"
                    + "where icm.empresa = " + getLojaOrigem() + "\n"
                    + "and icm.uf = 'MS'"
            )) {
                while (rst.next()) {
                    String id = rst.getString("cst") + "-" + rst.getString("aliquota") + rst.getString("reducao");
                    String descricao = id;
                    result.add(new MapaTributoIMP(id,
                            descricao,
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")));
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
                    "select\n"
                    + "    m1.cod as merc1,\n"
                    + "    m1.dsc as descricao_merc1,\n"
                    + "    m2.cod as merc2,\n"
                    + "    m2.dsc as descricao_merc2\n"
                    + "from sm_cd_es_departamento m1\n"
                    + "join sm_cd_es_grupo m2 on m2.dep = m1.cod"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descricao_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descricao_merc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
                    "select\n"
                    + "    p.cod as id,\n"
                    + "    p.pd_balanca as balanca,\n"
                    + "    p.da_validade as validade,\n"
                    + "    coalesce(b.barras, p.cod) as codigobarras,\n"
                    + "    b.pdv_quantidade as qtdembalagem,\n"
                    + "    p.dsc as descricaocompleta,\n"
                    + "    p.rdz as descricaoreduzida,\n"
                    + "    p.pd_departamento as mercadologico1,\n"
                    + "    p.pd_grupo as mercadologico2,\n"
                    + "    '1' as mercadologico3,\n"
                    + "    p.pd_data,\n"
                    + "    p.data_c, \n"
                    + "    p.pd_unidade as tipoembalagem,\n"
                    + "    p.pd_cest as cest,\n"
                    + "    p.pd_ncm as ncm,\n"
                    + "    pc.pis_credito_tributacao as pisconfinsentrada,\n"
                    + "    pc.cofins_tributacao as piscofinssaida,\n"
                    + "    pc.pis_nat_receita_tab,\n"
                    + "    pc.pis_nat_receita_it,\n"
                    + "    pc.pis_nat_receita_it_var,\n"
                    + "    pr.custo_s_imp as custosemimposto,\n"
                    + "    pr.custo_c_imp as custocomimposto,\n"
                    + "    pr.preco as precovenda,\n"
                    + "    pr.margem_atual as margem,\n"
                    + "    pr.margem_minima as margemminima,\n"
                    + "    pr.margem_maxima as margemmaxima,\n"
                    + "    pr.estoque_minimo as estoqueminimo,\n"
                    + "    pr.estoque_maximo as estoquemaximo,\n"
                    + "    pr.estoque_fiscal as estque ,\n"
                    + "    icm.tributacao as csticms,\n"
                    + "    icm.icms as aliqicms,\n"
                    + "    icm.icms_reducao as redicms\n"
                    + "from SM_CD_ES_PRODUTO p\n"
                    + "left join SM_CD_ES_PRODUTO_BAR b on b.cod = p.cod\n"
                    + "left join SM_CD_ES_PRODUTO_DNM pr on pr.cod = p.cod\n"
                    + "    and pr.empresa = " + getLojaOrigem() + "\n"
                    + "left join SM_CD_ES_PRODUTO_EF_F pc on pc.cod = p.cod\n"
                    + "    and pc.empresa = " + getLojaOrigem() + "\n"
                    + "left join SM_CD_ES_PRODUTO_EF icm on icm.cod = p.cod\n"
                    + "    and icm.empresa = " + getLojaOrigem() + "\n"
                    + "    and icm.uf = 'MS'"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDataCadastro(rst.getDate("data_c"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setMargemMinima(rst.getDouble("margemminima"));
                    imp.setMargemMaxima(rst.getDouble("margemmaxima"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("pisconfinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("pis_nat_receita_tab"));
                    
                    String idIcms = rst.getString("csticms") + "-" + rst.getString("aliqicms") + rst.getString("redicms");
                    
                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);
                    
                    result.add(imp);                    
                }
            }
        }
        return result;
    }
}
