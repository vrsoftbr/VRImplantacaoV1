package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class EptusDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Eptus";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codemp,\n" +
                    "	descricao\n" +
                    "from empresa")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codemp"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        int idLoja = Utils.stringToInt(getLojaOrigem());
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	distinct\n" +
                    "	cast(codigo as char) codigo,\n" +
                    "	descricao,\n" +
                    "	duff_cod_trib cst,\n" +
                    "	duff_aliq_icms icms,\n" +
                    "	duff_red_baseicm icms_reducao\n" +
                    "from \n" +
                    "	tributacao \n" +
                    "where \n" +
                    "	codemp = " + idLoja + " and \n" +
                    "	codigo in (select distinct cod_tributacao from prodserv_valor where codemp = " + idLoja + ")\n" +
                    "union all\n" +
                    "select \n" +
                    "	distinct\n" +
                    "	cast(concat('C', codigo) as char) as codigo,\n" +
                    "	descricao,\n" +
                    "	duff_cod_trib cst,\n" +
                    "	duff_aliq_icms icms,\n" +
                    "	duff_red_baseicm icms_reducao\n" +
                    "from \n" +
                    "	tributacao \n" +
                    "where \n" +
                    "	codemp = " + idLoja + " and \n" +
                    "	codigo in (select distinct cod_tribcompra from prodserv_valor where codemp = " + idLoja + ")"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("codigo"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("icms_reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.record_no,\n" +
                    "	p.codigo,\n" +
                    "	p.descricao,\n" +
                    "	p.dt_cadastro,\n" +
                    "	p.Sit_Desativado situacaocadastro,\n" +
                    "	p.desc_reduzida,\n" +
                    "	pa.cod_barras ean,\n" +
                    "	p.envia_balanca balanca,\n" +
                    "	p.dias_validade validade,\n" +
                    "	pc.qtd_atual estoque,\n" +
                    "	pc.qtd_minima,\n" +
                    "	pc.qtd_maxima,\n" +
                    "	pc.custo_compra custosemimposto,\n" +
                    "	pc.custo_medio,\n" +
                    "	pc.custo_anterior,	\n" +
                    "	pc.custo_varejo custocomimposto,\n" +
                    "	pc.mlucro_varejo margem,\n" +
                    "	pc.gondola_venda precovenda,\n" +
                    "	pa.embalagem_venda qtdembalagem,\n" +
                    "	p.embalagem_compra qtdembalagemcompra,\n" +
                    "	pc.gondola_atacado precoatacado,\n" +
                    "	p.embalagem_venda qtdembalagemvenda,\n" +
                    "	p.embalagem_atacado qtdembalagematacado,\n" +
                    "	p.peso_bruto,\n" +
                    "	p.peso_liquido,\n" +
                    "	p.unidade,\n" +
                    "	p.cod_grupo merc1,\n" +
                    "	p.cod_subgrupo merc2,\n" +
                    "	p.cod_ncmercosul ncm,\n" +
                    "	p.cod_depto,\n" +
                    "	pc.cod_tributacao id_icms_debito,\n" +
                    "	pc.cod_tribvendadev id_icmsdev_debito,\n" +
                    "	pc.cod_tribcompradev id_icmsdev_credito,\n" +
                    "	concat('C', pc.cod_tribcompra) id_icms_credito,\n" +
                    "	t.duff_cst_cofins cofins_debito,\n" +
                    "	substr(t.dUFf_CodCont_Pis, 5, 3) natureza_receita\n" +
                    "from\n" +
                    "	prodserv_dados p\n" +
                    "left join prodserv_codbar pa on p.codigo = pa.cod_produto and\n" +
                    "	p.codemp = pa.codemp\n" +
                    "left join prodserv_valor pc on p.codigo = pc.cod_produto and \n" +
                    "	p.codemp = pc.codemp\n" +
                    "left join tributacao t on pc.cod_tributacao = t.codigo\n" +
                    "where \n" +
                    "	p.codemp = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rs.getDate("dt_cadastro"));
                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro") == 0 ? 1 : 0);
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getInt("balanca") == 1);
                    imp.setValidade(rs.getInt("validade"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("qtd_minima"));
                    imp.setEstoqueMaximo(rs.getDouble("qtd_maxima"));
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoMedioComImposto(rs.getDouble("custo_medio"));
                    imp.setCustoMedioSemImposto(imp.getCustoMedioComImposto());
                    imp.setCustoAnteriorComImposto(rs.getDouble("custo_anterior"));
                    imp.setCustoAnteriorSemImposto(imp.getCustoAnteriorComImposto());
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("natureza_receita"));
                    imp.setIcmsDebitoId(rs.getString("id_icms_debito"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(rs.getString("id_icms_credito"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());
                    imp.setPiscofinsCstDebito(rs.getString("cofins_debito"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
