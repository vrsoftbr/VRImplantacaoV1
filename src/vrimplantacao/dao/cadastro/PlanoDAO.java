/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.dao.CodigoInternoDAO;

/**
 *
 * @author lucasrafael
 */
public class PlanoDAO {

    public void salvar(int id_loja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null,
                rst2 = null, rst3 = null;
        int id;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id from plano ");
            rst3 = stm.executeQuery(sql.toString());

            if (!rst3.next()) {
                sql = new StringBuilder();
                sql.append("SELECT id FROM produto ");
                sql.append("WHERE descricaocompleta LIKE '%DIVERSOS%' ");
                sql.append("LIMIT 1;");
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    id = new CodigoInternoDAO().get("plano");
                    sql = new StringBuilder();
                    sql.append("INSERT INTO plano( ");
                    sql.append("id, descricao, qtdedia, segunda, terca, quarta, quinta, sexta, ");
                    sql.append("sabado, domingo, datadeposito, juros, valorminimo, valormaximo, ");
                    sql.append("id_produto, id_tipoplanojuros, qtdediamaximo, datadepositomaximo) ");
                    sql.append("VALUES ( ");
                    sql.append(id + ", '30 DIAS', 30, ");
                    sql.append("true, true, true, true, true, true, true, NULL, ");
                    sql.append("0, 0, 1000, " + rst.getInt("id") + ", ");
                    sql.append("0, 30, null);");
                    stm.execute(sql.toString());

                    sql = new StringBuilder();
                    sql.append("INSERT INTO planoloja( ");
                    sql.append("id_plano, id_loja) ");
                    sql.append("VALUES ( ");
                    sql.append(id + ", " + id_loja + ");");
                    stm.execute(sql.toString());

                } else {

                    sql = new StringBuilder();
                    sql.append("SELECT min(id) as id FROM produto ");
                    rst2 = stm.executeQuery(sql.toString());

                    if (rst2.next()) {
                        id = new CodigoInternoDAO().get("plano");
                        sql = new StringBuilder();
                        sql.append("INSERT INTO plano( ");
                        sql.append("id, descricao, qtdedia, segunda, terca, quarta, quinta, sexta, ");
                        sql.append("sabado, domingo, datadeposito, juros, valorminimo, valormaximo, ");
                        sql.append("id_produto, id_tipoplanojuros, qtdediamaximo, datadepositomaximo) ");
                        sql.append("VALUES ( ");
                        sql.append(id + ", '30 DIAS', 30, ");
                        sql.append("true, true, true, true, true, true, true, NULL, ");
                        sql.append("0, 0, 1000, " + rst2.getInt("id") + ", ");
                        sql.append("0, 30, null);");
                        stm.execute(sql.toString());

                        sql = new StringBuilder();
                        sql.append("INSERT INTO planoloja( ");
                        sql.append("id_plano, id_loja) ");
                        sql.append("VALUES ( ");
                        sql.append(id + ", " + id_loja + ");");
                        stm.execute(sql.toString());
                    }
                }
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}