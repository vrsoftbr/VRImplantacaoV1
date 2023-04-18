/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoADS;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.classe.ConexaoDB2;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.classe.ConexaoInformix;
import vrimplantacao.classe.ConexaoParadox;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Desenvolvimento
 */
public class ProviderGenericoDAO extends InterfaceDAO {

    private String lojaCliente;
    private Statement statement;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "GENERICO";
    }

    public void setStatement(String banco) throws SQLException {
        System.out.println("O DAO Generico está usando o banco: " + banco);
        switch (banco) {
            case "ACCESS": {
                statement = ConexaoAccess.getConexao().createStatement();
                break;
            }
            case "ADS": {
                statement = ConexaoADS.getConexao().createStatement();
                break;
            }
            case "CACHE": {
                statement = null;
                break;
            }
            case "DBF": {
                statement = ConexaoDBF.getConexao().createStatement();
                break;
            }
            case "FIREBIRD": {
                statement = ConexaoFirebird.getConexao().createStatement();
                break;
            }
            case "DB2": {
                statement = ConexaoDB2.getConexao().createStatement();
                break;
            }
            case "INFORMIX": {
                statement = ConexaoInformix.getConexao().createStatement();
                break;
            }
            case "MYSQL": {
                statement = ConexaoMySQL.getConexao().createStatement();
                break;
            }
            case "ORACLE": {
                statement = ConexaoOracle.getConexao().createStatement();
                break;
            }
            case "PARADOX": {
                statement = ConexaoParadox.getConexao().createStatement();
                break;
            }
            case "POSTGRESQL": {
                statement = ConexaoPostgres.getConexao().createStatement();
                break;
            }
            case "SQLITE": {
                statement = null;//ConexaoSQLite.get().createStatement();
                break;
            }
            case "SQLSERVER": {
                statement = ConexaoSqlServer.getConexao().createStatement();
                break;
            }
            default: {
                statement = null;
                System.out.println("Erro na classe GenericoDAO, problemas com a conexão");
                break;
            }
        }
    }

    public Statement getStatement() {
        return statement;
    }

    public String getBancoDados() throws Exception {
        String result = null;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	bd.nome banco\n"
                    + "from\n"
                    + "	implantacao2_5.sistemabancodadosscripts script\n"
                    + "left join implantacao2_5.bancodados bd on\n"
                    + "	script.id_bancodados = bd.id\n"
                    + "where\n"
                    + "	script.id_sistema = 252"
            )) {
                while (rst.next()) {
                    result = rst.getString("banco");
                }
            }
        }
        return result;
    }

}
