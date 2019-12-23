package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Importacao
 */
public class DirectorDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Director";
    }
    
    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select DFcod_empresa id, DFnome_fantasia fantasia from TBempresa")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
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
                    "	d.DFid_departamento_item merc1,\n" +
                    "	d.DFdescricao descmerc1,\n" +
                    "	s.DFid_departamento_item merc2,\n" +
                    "	s.DFdescricao descmerc2,\n" +
                    "	g.DFid_departamento_item merc3,\n" +
                    "	g.DFdescricao descmerc3\n" +
                    "from \n" +
                    "	TBdepartamento_item d\n" +
                    "join\n" +
                    "	TBdepartamento_item s on d.DFid_departamento_item = s.DFid_departamento_item_pai\n" +
                    "join\n" +
                    "	TBdepartamento_item g on s.DFid_departamento_item = g.DFid_departamento_item_pai\n" +
                    "order by\n" +
                    "	2, 4, 6")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
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
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct(f.DFcod_produto_origem) codigo, \n" +
                    "	p.DFdescricao descricao\n" +
                    "from \n" +
                    "	TBproduto_similar f \n" +
                    "inner join TBitem_estoque p on p.DFcod_item_estoque = f.DFcod_produto_origem \n" +
                    "order by\n" +
                    "	2")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
