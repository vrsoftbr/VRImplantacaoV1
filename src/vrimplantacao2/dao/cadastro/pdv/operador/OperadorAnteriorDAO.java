/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.operador;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.pdv.operador.OperadorAnteriorVO;

/**
 *
 * @author lucasrafael
 */
public class OperadorAnteriorDAO {

    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.executeQuery(
                    "do $$\n"
                    + "declare\n"
                    + "begin\n"
                    + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_operador') then\n"
                    + "		create table implantacao.codant_operador(\n"
                    + "			sistema varchar not null,\n"
                    + "			loja varchar not null,\n"
                    + "			matricula varchar not null,\n"
                    + "			matriculaatual integer,\n"
                    + "			nome varchar,\n"
                    + "			senha varchar,\n"
                    + "			id_tiponiveloperador varchar,\n"
                    + "			id_situacaocadastro varchar,\n"
                    + "			forcargravacao boolean not null default false,\n"
                    + "			primary key (sistema, loja, matricula)\n"
                    + "		);\n"
                    + "	end if;\n"
                    + "end;\n"
                    + "$$;"
            );
        }
    }

    public void salvar(OperadorAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_operador");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("matricula", anterior.getMatricula());
            if (anterior.getMatriculaatual() != null) {
                sql.put("matriculaatual", anterior.getMatriculaatual().getId());
            }
            sql.put("nome", anterior.getNome());
            sql.put("senha", anterior.getSenha());
            sql.put("id_tiponiveloperador", anterior.getId_tiponiveloperador());
            sql.put("id_situacaocadastro", anterior.getId_situacaocadastro());
            sql.put("forcagravacao", anterior.isForcargravacao());
            stm.executeQuery(sql.getInsert());
        }
    }

    public MultiMap<String, OperadorAnteriorVO> getAnterior(String sistema, String loja) throws Exception {
        MultiMap<String, OperadorAnteriorVO> result = new MultiMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "ant.sistema,"
                    + "ant.loja,"
                    + "ant.matricula, "
                    + "ant.matriculaatual, "
                    + "o.nome, "
                    + "o.senha, "
                    + "o.codigo, "
                    + "o.id_tiponiveloperador, "
                    + "from implantacao.codant_operador ant "
                    + "inner join pdv.operador o on o.matricula = ant.matriculaatual "
                    + "where "
                    + "	ant.sistema = " + Utils.quoteSQL(sistema) + "\n"
                    + "	and ant.loja = " + Utils.quoteSQL(loja) + "\n"
                    + "order by\n"
                    + "	ant.id"
            )) {
                while (rst.next()) {
                    OperadorAnteriorVO vo = new OperadorAnteriorVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setMatricula(rst.getString("matricula"));
                    vo.setNome(rst.getString("nome"));
                    vo.setSenha(rst.getString("senha"));
                    vo.setId_tiponiveloperador(rst.getString("id_tiponiveloperador"));
                    result.put(vo, vo.getSistema(), vo.getLoja(), vo.getMatricula());
                }
            }
        }
        return result;
    }
}
