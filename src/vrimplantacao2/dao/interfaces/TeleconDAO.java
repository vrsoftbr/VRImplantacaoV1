package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author guilhermegomes
 */
public class TeleconDAO extends InterfaceDAO implements MapaTributoProvider {
    
    public String complemento = "";
    
    @Override
    public String getSistema() {
        return "Telecon" + complemento;
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codigo,\n" +
                    "	descricao,\n" +
                    "	aliquota\n" +
                    "from\n" +
                    "	Aliquotas")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"), 
                            rs.getString("descricao"), 0, 
                            rs.getDouble("aliquota"), 0));
                }
            }
        }
        
        return result;
    }
    
    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                      "select \n"
                    + "	codigo,\n"
                    + "	nome\n"
                    + "from \n"
                    + "	lojas")) {
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct\n" +
                    "	substring(g1.codigo, 0, 3) merc1,\n" +
                    "	(select nome from grupos where len(codigo) = 2 and substring(codigo, 0, 3) = substring(g1.codigo, 0, 3)) descmerc1, \n" +
                    "	substring(g2.codigo, 4, 6) merc2,\n" +
                    "	g2.nome descmerc2\n" +
                    "from \n" +
                    "	produtos p\n" +
                    "join grupos g1 on substring(p.grupo, 0, 3) = substring(g1.codigo, 0, 3)\n" +
                    "join grupos g2 on substring(g2.codigo, 0, 3) = substring(g1.codigo, 0, 3) and\n" +
                    "	 substring(g1.codigo, 4, 6) = substring(g2.codigo, 4, 6)\n" +
                    "order by 1, 3")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "    CODIGO,\n" +
                    "    NOME\n" +
                    "FROM \n" +
                    "   PRODUTOS_ASSOCIADOS\n" +
                    "order by\n" +
                    "   2")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricao(rs.getString("nome"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
