package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class AtenasDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Atenas";
    }
    
    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    codigo,\n" +
                    "    fantasia\n" +
                    "from\n" +
                    "    c999999")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    codigo,\n" +
                    "    grupo\n" +
                    "from\n" +
                    "    c000017")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("codigo"));
                    imp.setMerc1Descricao(rs.getString("grupo"));
                    imp.setMerc2ID(rs.getString("codigo"));
                    imp.setMerc2Descricao(rs.getString("grupo"));
                    imp.setMerc3ID(rs.getString("codigo"));
                    imp.setMerc3Descricao(rs.getString("grupo"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    codigo,\n" +
                    "    codbarra,\n" +
                    "    usa_balanca,\n" +
                    "    validade,\n" +
                    "    situacao,\n" +
                    "    produto,\n" +
                    "    unidade,\n" +
                    "    data_cadastro,\n" +
                    "    codgrupo merc1,\n" +
                    "    precocusto,\n" +
                    "    precovenda,\n" +
                    "    e.estoque_atual estoque,\n" +
                    "    estoqueminimo,\n" +
                    "    classificacao_fiscal ncm,\n" +
                    "    cst,\n" +
                    "    situacao_tributaria,\n" +
                    "    csosn,\n" +
                    "    aliquota,\n" +
                    "    cest\n" +
                    "from\n" +
                    "    c000025 p\n" +
                    "left join c000100 e on p.codigo = e.codproduto\n" +
                    "order by\n" +
                    "    p.codigo")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setEan(rs.getString("codbarra"));
                    imp.seteBalanca(rs.getInt("usa_balanca") == 1);
                    imp.setValidade(rs.getInt("validade"));
                    imp.setSituacaoCadastro(1);
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc1"));
                    imp.setCodMercadologico3(rs.getString("merc1"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("precocusto"));
                    imp.setCustoSemImposto(rs.getDouble("precocusto"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setNcm(rs.getString("ncm"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

}
