package vrimplantacao2_5.service.migracao;

import java.sql.Connection;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.classe.ConexaoDB2;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoInformix;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao2_5.vo.enums.EBancoDados;

/**
 *
 * @author Desenvolvimento
 */
public abstract class ConexaoBancoDadosFactory {
    
    public static Connection getConexao(EBancoDados eBancoDados) throws Exception {

        Connection conexao = null;

        switch (eBancoDados) {
            case ACCESS:
                conexao = ConexaoAccess.getConexao();
                break;
            case ADS:
                conexao = null;
                break;
            case CACHE:
                conexao = null;
                break;
            case DBF:
                conexao = ConexaoDBF.getConexao();
                break;
            case DB2:
                conexao = ConexaoDB2.getConexao();
                break;
            case FIREBIRD:
                conexao = ConexaoFirebird.getConexao();
                break;
            case INFORMIX:
                conexao = ConexaoInformix.getConexao();
                break;
            case MYSQL:
                conexao = ConexaoMySQL.getConexao();
                break;
            case ORACLE:
                conexao = ConexaoOracle.getConexao();
                break;
            case PARADOX:
                conexao = null;
                break;
            case POSTGRESQL:
                conexao = ConexaoPostgres.getConexao();
                break;
            case SQLITE:
                conexao = null;
                break;
            case SQLSERVER:
                conexao = ConexaoSqlServer.getConexao();
                break;
            default:
                return null;
        }
        
        return conexao;
    }

}
