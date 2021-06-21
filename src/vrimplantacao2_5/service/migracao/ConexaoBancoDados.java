package vrimplantacao2_5.service.migracao;

import java.sql.Connection;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2_5.vo.enums.EBancoDados;
import static vrimplantacao2_5.vo.enums.EBancoDados.MYSQL;

/**
 *
 * @author Desenvolvimento
 */
public class ConexaoBancoDados {
    
    public static Connection getConexao(EBancoDados eBancoDados) throws Exception {

        Connection conexao = null;

        switch (eBancoDados) {
            case FIREBIRD:
                conexao = (Connection) new ConexaoFirebird();
                break;
            case MYSQL:
                conexao = (Connection) new ConexaoMySQL();
                break;
            default:
                return null;
        }

        return conexao;
    }

}
