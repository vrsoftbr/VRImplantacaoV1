package vrimplantacao2.dao.interfaces.hipermax;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class SupermercadoHipermaxDAO extends InterfaceDAO {
    
    @Override
    public String getSistema() {
        return "SupermercadoHipermax";
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        return null;
    }
}
