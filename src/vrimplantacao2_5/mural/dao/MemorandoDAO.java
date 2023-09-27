/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.mural.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.mural.vo.MemorandoVO;

/**
 *
 * @author Michael
 */
public class MemorandoDAO {

    public MemorandoDAO() throws Exception {
        criaTabelaMemorando();
    }

    private void criaTabelaMemorando() throws Exception {
        try {
            Statement stm = Conexao.createStatement();
            stm.execute("create table if not exists implantacao.memorando (\n"
                    + "	id serial primary key,\n"
                    + "	data_alteracao date,\n"
                    + "	lembrete text\n"
                    + ")");
        } catch (Exception ex) {
            System.out.println("Erro em MemorandoPostitDAO: criaTabelaMemorando()\n\n");
            ex.printStackTrace();
            throw ex;
        }
    }

    public int inserirMemorando(String lembrete) throws Exception {
        SimpleDateFormat ajusteData = new SimpleDateFormat("yyyy-MM-dd");
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("memorando");
            sql.put("data_alteracao", ajusteData.format(new Date()));
            sql.put("lembrete", lembrete);
            sql.getReturning().add("id");

            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                }
            }
        }
        return -1;
    }

    public MemorandoVO retornaMensagem(int id) throws Exception {
        MemorandoVO vo = new MemorandoVO();
        if (id != 0) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery("select\n"
                        + "	id,\n"
                        + "	data_alteracao,\n"
                        + "	lembrete\n"
                        + "from\n"
                        + "	implantacao.memorando\n"
                        + "where\n"
                        + "	id = " + id)) {
                    while (rst.next()) {
                        vo.setId(rst.getInt("id"));
                        vo.setData(rst.getString("data_alteracao"));
                        vo.setLembrete(rst.getString("lembrete"));
                    }
                }
            }
        }
        return vo;
    }

    public int retornaUltimoId() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery("select\n"
                    + "	max(id) id\n"
                    + "from\n"
                    + "	implantacao.memorando")) {
                if (rst.next()) {
                    return rst.getInt("id");
                }
            }
        }
        return 0;
    }

    public void deletarLembretes() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("do $$\n"
                    + "begin\n"
                    + "	delete from implantacao.memorando;\n"
                    + " alter sequence implantacao.memorando_id_seq restart with 1;  \n"
                    + "end;\n"
                    + "$$ language plpgsql");
        }
    }
}
