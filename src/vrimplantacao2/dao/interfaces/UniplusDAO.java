package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class UniplusDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;
    
    @Override
    public String getSistema() {
       return "Uniplus";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codigo,\n" +
                    "	nome,\n" +
                    "	cnpj\n" +
                    "from \n" +
                    "	filial")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("nome")));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id as merc1,\n" +
                    "	nome as desmerc1,\n" +
                    "	id as merc2,\n" +
                    "	nome as descmerc2,\n" +
                    "	id as merc3,\n" +
                    "	nome as descmerc3 \n" +
                    "from \n" +
                    "	hierarquia \n" +
                    "order by\n" +
                    "	id")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.codigo,\n" +
                    "	p.ean,\n" +
                    "	p.inativo,\n" +
                    "	p.nome as descricaocompleta,\n" +
                    "	p.nomeecf as descricaoreduzida,\n" +
                    "	p.nome as descricaogondola,\n" +
                    "	p.datacadastro,\n" +
                    "	p.unidademedida as unidade,\n" +
                    "	1 qtdembalagem,\n" +
                    "	p.custoindireto custooperacional,\n" +
                    "	p.precocusto,\n" +
                    "	p.lucrobruto as margembruta,\n" +
                    "	p.percentuallucroajustado as margem,\n" +
                    "	p.percentualmarkup,\n" +
                    "	p.preco as precovenda,\n" +
                    "	p.quantidademinima,\n" +
                    "	p.quantidademaxima,\n" +
                    "	e.quantidade,\n" +
                    "	p.tributacao,\n" +
                    "	p.situacaotributaria as cst,\n" +
                    "	p.cstpis,\n" +
                    "	p.cstcofins,\n" +
                    "	p.cstpisentrada,\n" +
                    "	p.icmsentrada as icmscredito,\n" +
                    "	p.icmssaida as icmsdebito,\n" +
                    "	p.aliquotaicmsinterna,\n" +
                    "	p.pesavel,\n" +
                    "	p.ncm,\n" +
                    "	p.idcest,\n" +
                    "	cest.codigo as cest,\n" +
                    "	p.cstpisentrada,\n" +
                    "	p.cstcofinsentrada,\n" +
                    "	p.idfamilia,\n" +
                    "	p.idhierarquia as merc1,\n" +
                    "	p.idhierarquia as merc2,\n" +
                    "	p.idhierarquia as merc3\n" +
                    "from\n" +
                    "	produto p\n" +
                    "left join\n" +
                    "	saldoestoque e on e.idproduto = p.id and e.codigoproduto = p.codigo\n" +
                    "left join\n" +
                    "	cest on cest.id = p.idcest\n" +
                    "order by\n" +
                    "	p.codigo::integer")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setEan(rs.getString("ean"));
                    imp.setSituacaoCadastro(rs.getInt("inativo") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzica"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setCustoComImposto(rs.getDouble("precocusto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rs.getDouble("quantidademinima"));
                    imp.setEstoqueMaximo(rs.getDouble("quantidademaxima"));
                    imp.setEstoque(rs.getDouble("quantidade"));
                    imp.setIcmsCst(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("aliquotaicmsinterna"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icmscredito"));
                    imp.setIcmsAliqSaidaForaEstado(rs.getDouble("aliquotaicmsinterna"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rs.getDouble("aliquotaicmsinterna"));
                    imp.setPiscofinsCstCredito(rs.getString("cstpisentrada"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpisentrada"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
