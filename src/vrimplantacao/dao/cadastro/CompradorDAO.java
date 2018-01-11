/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.vo.vrimplantacao.CompradorVO;

/**
 *
 * @author lucasrafael
 */
public class CompradorDAO {

    public CompradorVO carregar() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        CompradorVO oComprador = null;

        try {
            stm = Conexao.createStatement();
            Conexao.begin();

            sql = new StringBuilder();

            sql.append("SELECT * FROM comprador WHERE UPPER(nome)=UPPER('Migração Loja VR')");

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                oComprador = new CompradorVO();
                oComprador.nome = rst.getString("nome");
                oComprador.id = rst.getInt("id");

            }

            if (oComprador == null) {

                sql = new StringBuilder();

                sql.append("SELECT id from (SELECT id FROM generate_series(1, (SELECT COALESCE(MAX(id), 0) + 1 FROM  comprador)) AS s(id) EXCEPT SELECT id FROM comprador) AS codigointerno ORDER BY id LIMIT 1");
                rst = stm.executeQuery(sql.toString());
                rst.next();

                int idComprador = rst.getInt("id");

                sql = new StringBuilder();

                sql.append("INSERT INTO comprador (id,nome,id_situacaocadastro)");
                sql.append(" VALUES(");
                sql.append(" " + idComprador + ",'Migração Loja VR', 1)");

                stm.execute(sql.toString());

                oComprador = new CompradorVO();

                oComprador.nome = "Migração Loja VR";
                oComprador.id = idComprador;
            }

            Conexao.commit();

            return oComprador;

        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        } finally {
            Conexao.destruir(null, stm, rst);
        }
    }
}
