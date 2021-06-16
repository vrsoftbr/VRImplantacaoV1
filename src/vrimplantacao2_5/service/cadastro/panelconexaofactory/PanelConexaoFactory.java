package vrimplantacao2_5.service.cadastro.panelconexaofactory;

import javax.swing.JPanel;
import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.bancodados.BancoDadosDAO;
import vrimplantacao2_5.service.cadastro.ConfiguracaoPanel;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.gui.componente.conexao.firebird.ConexaoFirebirdPanel;
import vrimplantacao2_5.gui.componente.conexao.mysql.ConexaoMySQLPanel;
import vrimplantacao2_5.gui.componente.conexao.oracle.ConexaoOraclePanel;
import vrimplantacao2_5.gui.componente.conexao.postgresql.ConexaoPostgreSQLPanel;
import vrimplantacao2_5.gui.componente.conexao.sqlserver.ConexaoSqlServerPanel;

/**
 *
 * @author guilhermegomes
 */
public abstract class PanelConexaoFactory {

    public static ConfiguracaoPanel getPanelConexao(int idSistema, int idBancoDados) {

        BancoDadosVO bdVO = null;

        try {
            bdVO = new BancoDadosDAO().getInformacaoBancoDados(idSistema, idBancoDados);
        } catch (Exception ex) {
            if (bdVO == null) {
                bdVO = new BancoDadosVO();
            }

            Exceptions.printStackTrace(ex);
        }

        ConfiguracaoPanel panelConexao = null;
        EBancoDados eBD = EBancoDados.getById(idBancoDados);

        switch (eBD) {
            case FIREBIRD:
                panelConexao = new ConexaoFirebirdPanel(bdVO.getSchema(), bdVO.getPorta(),
                        bdVO.getUsuario(), bdVO.getSenha());
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
                break;

            case SQLSERVER:
                panelConexao = new ConexaoSqlServerPanel(bdVO.getSchema(), bdVO.getPorta(),
                        bdVO.getUsuario(), bdVO.getSenha());
                break;

            default:
                return null;
        }

        return panelConexao;
    }
}
