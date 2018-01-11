package vrimplantacao.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;

public class PostgresDAO {

    public boolean tabelaExiste(String i_tabela) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT DISTINCT pg_namespace.nspname AS esquema, pg_class.relname AS tabela");
        sql.append(" FROM pg_index, pg_attribute ");
        sql.append(" JOIN pg_class ON (pg_attribute.attrelid = pg_class.oid AND pg_class.relkind = 'r')");
        sql.append(" JOIN pg_namespace ON (pg_namespace.oid = pg_class.relnamespace)");
        sql.append(" WHERE pg_class.relkind = 'r'");

        if (i_tabela.contains(".")) {
            sql.append("AND pg_namespace.nspname || '.' ||   pg_class.relname = '" + i_tabela.trim() + "'");
        } else {
            sql.append("AND pg_namespace.nspname || '.' ||   pg_class.relname = 'public." + i_tabela.trim() + "'");
        }

        rst = stm.executeQuery(sql.toString());

        return rst.next();
    }
}
