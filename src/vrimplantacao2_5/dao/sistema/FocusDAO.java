package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author importacao
 */
public class FocusDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "FOCUS";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct\n" +
                    "	tribut id,\n" +
                    "	case when tribut = '01' then '25%'\n" +
                    "	when tribut = '02' then '18%'\n" +
                    "	when tribut = '03' then '12%'\n" +
                    "	when tribut = '04' then '07%'\n" +
                    "	when tribut = '05' then 'ISENTO'\n" +
                    "	when tribut = '06' then 'SUBSTITUIDO'\n" +
                    "	when tribut = '07' then 'NAO ICIDENCIA'\n" +
                    "	when tribut = '08' then '11%'\n" +
                    "	when tribut = '09' then '4,5%'\n" +
                    "	when tribut = '10' then '3,2%'\n" +
                    "	when tribut = '11' then '9,4%'\n" +
                    "	when tribut = '12' then '13,3%'\n" +
                    "	when tribut = '13' then '11,2%'\n" +
                    "	when tribut = '14' then '4,7%'\n" +
                    "	when tribut = '15' then '4,14'\n" +
                    "	when tribut = '16' then '5,5%'\n" +
                    "	else 'ISENTO' end descricao\n" +
                    "from \n" +
                    "	estoque")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"),
                            rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.id,\n" +
                    "	p.codigo ean,\n" +
                    "	p.CODIGOINTERNO,\n" +
                    "	p.DESCRICAO descricaoreduzida,\n" +
                    "	p.DESCRICAO_INT descricaocompleta,\n" +
                    "	p.un unidade,\n" +
                    "	p.QTDEMB qtdembalagem,\n" +
                    "	p.valdias validade,\n" +
                    "	p.datacad cadastro,\n" +
                    "	p.margem,\n" +
                    "	p.precovenda,\n" +
                    "	p.precomedio,\n" +
                    "	p.PRECOVENDA3 custo,\n" +
                    "	p.PRECOATACADO,\n" +
                    "	p.QTDE estoque,\n" +
                    "	p.MIN estoqueminimo,\n" +
                    "	p.CLFISCAL NCM,\n" +
                    "	p.cest,\n" +
                    "	p.icms,\n" +
                    "	p.CST,\n" +
                    "	p.ecst,\n" +
                    "	p.eicms,\n" +
                    "	p.tribut idicms,\n" +
                    "	p.eiva,\n" +
                    "	p.CSTCOFINS,\n" +
                    "	p.natrec naturezareceita,\n" +
                    "	p.SITUACAO,\n" +
                    "	p.PESOBRUTO,\n" +
                    "	p.PESOLIQUIDO \n" +
                    "from \n" +
                    "	estoque p")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    
                    if (imp.getDescricaoCompleta() == null & imp.getDescricaoCompleta().isEmpty()) {
                        imp.setDescricaoCompleta(imp.getDescricaoReduzida());
                    }
                    
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    
                    /*imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));*/
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setSituacaoCadastro(rs.getInt("situacao") == 0 ? 1 : 0);
                    
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));
                    imp.setPiscofinsCstCredito(rs.getString("cstpisent"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
