package vr.implantacao.service.cadastro.panelconexaofactory;

import javax.swing.JPanel;
import org.openide.util.Exceptions;
import vr.implantacao.dao.bancodados.BancoDadosDAO;
import vr.implantacao.service.cadastro.panelobserver.PanelObserver;
import vr.implantacao.vo.cadastro.BancoDadosVO;
import vr.implantacao.vo.enums.EBancoDados;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrimplantacao2.gui.component.conexao.firebird.ConexaoFirebirdPanel;
import vrimplantacao2.gui.component.conexao.mysql.ConexaoMySQLPanel;
import vrimplantacao2.gui.component.conexao.oracle.ConexaoOraclePanel;
import vrimplantacao2.gui.component.conexao.postgresql.ConexaoPostgreSQLPanel;
import vrimplantacao2.gui.component.conexao.sqlserver.ConexaoSqlServerPanel;

/**
 *
 * @author guilhermegomes
 */
public abstract class PanelConexaoFactory {
    
    public static JPanel getPanelConexao(PanelObserver conexaoBD, int idSistema, int idBancoDados) {
        
        BancoDadosVO bdVO = null;
        
        try {
            bdVO = new BancoDadosDAO().getInformacaoBancoDados(idSistema, idBancoDados);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        JPanel panelConexao = null;
        EBancoDados eBD = EBancoDados.getById(idBancoDados);
        
        switch(eBD) {
            case FIREBIRD: 
                panelConexao = new ConexaoFirebirdPanel(bdVO.getSchema(), bdVO.getPorta(), 
                                                        bdVO.getUsuario(), bdVO.getSenha());
                
                ((ConexaoFirebirdPanel) panelConexao).registrarObservador(conexaoBD);
            break;
            
            case MYSQL: 
                panelConexao = new ConexaoMySQLPanel(bdVO.getSchema(), bdVO.getPorta(), 
                                                     bdVO.getUsuario(), bdVO.getSenha());
            break;
            
            case ORACLE: 
                panelConexao = new ConexaoOraclePanel(bdVO.getSchema(), bdVO.getPorta(), 
                                                      bdVO.getUsuario(), bdVO.getSenha());
            break;
            
            case POSTGRESQL: 
                panelConexao = new ConexaoPostgreSQLPanel(bdVO.getSchema(), bdVO.getPorta(), 
                                                          bdVO.getUsuario(), bdVO.getSenha());
                
                ((ConexaoPostgreSQLPanel) panelConexao).registrarObservador(conexaoBD);
            break;
            
            case SQLSERVER: 
                panelConexao = new ConexaoSqlServerPanel(bdVO.getSchema(), bdVO.getPorta(), 
                                                        bdVO.getUsuario(), bdVO.getSenha());
            break;
            
            default: return null;
        }
        
        return panelConexao;
    }
}
