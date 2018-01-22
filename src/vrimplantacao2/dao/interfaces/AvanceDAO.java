package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class AvanceDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Avance";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id, fantasia FROM adm_empresas_estab ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("fantasia")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT codigo, nome FROM depto ORDER BY 1"
            )) {
                while (rst.next()) {
                    
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("nome"));
                    
                    result.add(imp);
                    
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id, descricao FROM familia ORDER BY id"
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	p.codigo id,\n" +
                    "	p.cadastro datacadastro,\n" +
                    "	p.embalagem qtdcotacao,\n" +
                    "	CASE WHEN p.codbalanca != 0 THEN p.codbalanca ELSE ean.codbarra END ean,\n" +
                    "	CASE WHEN p.codbalanca != 0 THEN 1 ELSE ean.qtd_embalagem END qtdembalagem,\n" +
                    "	p.unidade,\n" +
                    "	CASE WHEN p.codbalanca != 0 THEN 1 ELSE 0 END ebalanca,\n" +
                    "	p.validade,\n" +
                    "	p.descricao descricaocompleta,\n" +
                    "	p.descecf descricaoreduzida,\n" +
                    "	p.depart mercadologico1,\n" +
                    "	p.id_familia,\n" +
                    "	p.peso_bruto,\n" +
                    "	p.peso_liquido,\n" +
                    "	est.lojaestmin estoqueminimo,\n" +
                    "	est.lojaestmax estoquemaximo,\n" +
                    "	est.lojaest estoque,\n" +
                    "	p.dentrouf margem,\n" +
                    "	p.custo custosemimposto,\n" +
                    "	p.custofinal custocomimposto,\n" +
                    "	p.atualvenda precovenda,\n" +
                    "	p.inativo situacaocadastro,\n" +
                    "	ncm.ncm,\n" +
                    "	p.cest,\n" +
                    "	p.cst_pis_ent piscofins_entrada,\n" +
                    "	p.cst_pis_sai piscofins_saida,\n" +
                    "	p.cod_nat_receita piscofins_nat_receita,\n" +
                    "	p.cst icms_cst,\n" +
                    "	p.aliquota\n" +
                    "FROM\n" +
                    "	cadmer p\n" +
                    "	LEFT JOIN codbarra ean ON p.codigo = ean.codigo\n" +
                    "	LEFT JOIN cadmer_estoque est ON p.codigo = est.codigo AND est.id_loja = " + getLojaOrigem() + "\n" +
                    "	LEFT JOIN ncm ON ncm.id = p.id_ncm\n" +
                    "WHERE\n" +
                    "	NOT ean.codbarra IS NULL\n" +
                    "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdcotacao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(Utils.stringToInt(rst.getString("piscofins_entrada")));
                    imp.setPiscofinsCstDebito(Utils.stringToInt(rst.getString("piscofins_saida")));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofins_nat_receita")));
                    imp.setIcmsDebitoId(rst.getString("aliquota"));
                    imp.setIcmsCreditoId(rst.getString("aliquota"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id, CONCAT(descricao,'  |cst:' ,cst) descricao FROM aliquota ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }
    
    
}
