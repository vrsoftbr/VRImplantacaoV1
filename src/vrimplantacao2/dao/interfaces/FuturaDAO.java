package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author guilhermegomes
 */
public class FuturaDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Futura";
    }
    
    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	id,\n" +
                    "	FANTASIA,\n" +
                    "	CNPJ_CPF \n" +
                    "FROM\n" +
                    "	CADASTRO c\n" +
                    "WHERE \n" +
                    "	FK_EMPRESA IS null")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(
                                        rs.getString("id"), 
                                        rs.getString("fantasia")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	gr.id idgrupo,\n" +
                    "	gr.DESCRICAO descgrupo,\n" +
                    "	sb.ID idsubgrupo,\n" +
                    "	sb.DESCRICAO descsubgrupo\n" +
                    "FROM\n" +
                    "	PRODUTO_GRUPO gr\n" +
                    "LEFT JOIN PRODUTO_SUBGRUPO sb \n" +
                    "		ON gr.ID = sb.FK_PRODUTO_GRUPO")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("idgrupo"));
                    imp.setMerc1Descricao(rs.getString("descgrupo"));
                    imp.setMerc2ID(rs.getString("idsubgrupo"));
                    imp.setMerc2Descricao(rs.getString("descsubgrupo"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
}
