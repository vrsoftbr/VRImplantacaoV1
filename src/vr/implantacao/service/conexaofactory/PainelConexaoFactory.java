package vr.implantacao.service.conexaofactory;

import javax.swing.JPanel;
import vr.implantacao.vo.enums.EBancoDados;
import vrimplantacao2.gui.component.conexao.firebird.ConexaoFirebirdPanel;
import vrimplantacao2.gui.component.conexao.mysql.ConexaoMySQLPanel;
import vrimplantacao2.gui.component.conexao.oracle.ConexaoOraclePanel;
import vrimplantacao2.gui.component.conexao.postgresql.ConexaoPostgreSQLPanel;
import vrimplantacao2.gui.component.conexao.sqlserver.ConexaoSqlServerPanel;

/**
 *
 * @author guilhermegomes
 */
public abstract class PainelConexaoFactory {
    
    public static JPanel getPanelConexao(int idBancoDados) {
        
        JPanel panelConexao = null;
        EBancoDados eBD = EBancoDados.getById(idBancoDados);
        
        switch(eBD) {
            case FIREBIRD: 
                panelConexao = new ConexaoFirebirdPanel();
            break;
            
            case MYSQL: 
                panelConexao = new ConexaoMySQLPanel();
            break;
            
            case ORACLE: 
                panelConexao = new ConexaoOraclePanel();
            break;
            
            case POSTGRESQL: 
                panelConexao = new ConexaoPostgreSQLPanel();
            break;
            
            case SQLSERVER: 
                panelConexao = new ConexaoSqlServerPanel();
            break;
            
            default: return null;
        }
        
        return panelConexao;
    }
}
