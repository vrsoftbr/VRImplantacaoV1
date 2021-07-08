package vrimplantacao2_5.service.cadastro.configuracao;

import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao2_5.gui.componente.conexao.DriverConexao;
import vrimplantacao2_5.vo.enums.EBancoDados;

/**
 *
 * @author guilhermegomes
 */
public abstract class ConexaoComponenteFactory {
    
    public static DriverConexao getConexao(EBancoDados eBD) {
        DriverConexao driverConn;
        
        switch(eBD) {
            case FIREBIRD:
                driverConn = new ConexaoFirebird();
            break;
            case POSTGRESQL:
                driverConn = new ConexaoPostgres();
            break; 
            default: driverConn = null;
        }
        
        return driverConn;
    }
}
