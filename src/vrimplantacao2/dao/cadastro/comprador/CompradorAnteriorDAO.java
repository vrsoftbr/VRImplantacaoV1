/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.comprador;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.comprador.CompradorAnteriorVO;

/**
 *
 * @author Leandro
 */
public class CompradorAnteriorDAO {
    
    public static void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {      
            try (ResultSet rst = stm.executeQuery(
                    "select table_schema||'.'||table_name tabela from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_comprador'"
            )) {
                if (rst.next()) {
                    return;
                }
            }
            stm.execute(
                    "create table implantacao.codant_comprador (\n" +
                    "	sistema varchar not null,\n" +
                    "	loja varchar not null,\n" +
                    "	id varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	descricao varchar,\n" +
                    "	primary key (sistema, loja, id)\n" +
                    ")"
            );
        }
    }

    public CompradorAnteriorDAO() throws Exception {
        CompradorAnteriorDAO.createTable();
    }

    public Map<String, CompradorAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, CompradorAnteriorVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id,\n" +
                    "	codigoatual,\n" +
                    "	descricao\n" +
                    "from\n" +
                    "	implantacao.codant_comprador\n" +
                    "where\n" +
                    "	sistema = '" + sistema + "' and\n" +
                    "	loja = '" + loja + "'"
            )) {
                if (rst.next()) {
                    CompradorAnteriorVO vo = new CompradorAnteriorVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setId(rst.getString("id"));
                    vo.setCodigoAtual(rst.getInt("codigoatual"));
                    vo.setDescricao(rst.getString("descricao"));
                    result.put(vo.getId(), vo);
                }
            }
        }
        
        return result;
    }

    public void gravar(CompradorAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("implantacao");
            sql.setTableName("codant_comprador");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            sql.put("codigoatual", anterior.getCodigoAtual());
            sql.put("descricao", anterior.getDescricao());
            
            stm.execute(sql.getInsert());
        }
    }

    public Map<String, Integer> getCompradoresImportador(String sistema, String loja) throws Exception {
        Map<String, Integer> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.id,\n" +
                    "	ant.codigoatual\n" +
                    "from\n" +
                    "	implantacao.codant_comprador ant\n" +
                    "	join comprador cp on\n" +
                    "		ant.codigoatual = cp.id\n" +
                    "where\n" +
                    "	sistema = '" + sistema + "' and\n" +
                    "	loja = '" + loja + "'"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id"), rst.getInt("codigoatual"));
                }
            }
        }
        
        return result;
    }
    
}
