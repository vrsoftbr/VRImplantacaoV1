package vrimplantacao.dao.administrativo;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

public class VendaPdvDAO {

    public int getId(int i_numeroCumpom, String i_data, int i_ecf) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append(" SELECT id");
        sql.append(" FROM pdv.venda");
        sql.append(" WHERE numerocupom = " + i_numeroCumpom);
        //sql.append(" AND data = '" + Util.formatDataBanco(i_data) + "'");
        sql.append(" AND ecf = " + i_ecf);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }
    
    public int getId2(int i_numeroCumpom, String i_data, int i_ecf) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append(" SELECT id");
        sql.append(" FROM pdv.venda");
        sql.append(" WHERE numerocupom = " + i_numeroCumpom);
        sql.append(" AND data = '" + i_data + "'");
        sql.append(" AND ecf = " + i_ecf);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }
    
}
