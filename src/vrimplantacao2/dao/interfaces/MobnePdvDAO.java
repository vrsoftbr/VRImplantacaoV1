package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author leandro
 */
public class MobnePdvDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "".equals(complemento) ? "Mobne" : "Mobne - " + complemento;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select nroempresa, nomereduzido from tb_empresa order by nroempresa"
            )) {
                while (rs.next()) {
                    result.add(
                            new Estabelecimento(rs.getString("nroempresa"), rs.getString("nomereduzido"))
                    );
                }
            }
        }
        
        return result;
    }
    
}
