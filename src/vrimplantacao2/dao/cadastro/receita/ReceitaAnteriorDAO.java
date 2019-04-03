/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.receita;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.receita.ReceitaAnteriorVO;

/**
 *
 * @author lucasrafael
 */
public class ReceitaAnteriorDAO {

    public static void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_receita (\n"
                    + "  sistema varchar,\n"
                    + "  loja varchar,\n"
                    + "  id varchar,\n"
                    + "  idproduto varchar,\n"
                    + "  descricao varchar,\n"
                    + "  fichatecnica text,\n"
                    + "  qtdembalagemreceita int,\n"
                    + "  qtdembalagemproduto int,\n"
                    + "  fatorembalagem numeric (11,2),\n"
                    + "  baixaestoque boolean,\n"
                    + "  embalagem boolean default false,\n"
                    + "  rendimento numeric(11,3),\n"
                    + "  codigoatual integer ,\n"
                    + "  primary key (sistema, loja, id)\n"
                    + ")"
            );
        }
    }
    
    public ReceitaAnteriorDAO() throws Exception {
        createTable();
    }
    
    public Map<String, ReceitaAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, ReceitaAnteriorVO> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  sistema,\n"
                    + "  loja,\n"
                    + "  id,\n"
                    + "  idproduto,\n"
                    + "  descricao,\n"
                    + "  fichatecnica,\n"
                    + "  qtdembalagemreceita,\n"
                    + "  qtdembalagemproduto,\n"
                    + "  fatorembalagem,\n"
                    + "  baixaestoque,\n"
                    + "  embalagem,\n"
                    + "  rendimento\n"
                    + "from implantacao.codant_receita\n"
                    + "where sistema = '" + sistema + "' \n"
                    + "  and loja = '" + loja + "'\n"
                    + "order by 1,2,3 "
            )) {
                while (rst.next()) {
                    ReceitaAnteriorVO ant = new ReceitaAnteriorVO();                    
                    ant.setImportsistema(rst.getString("sistema"));
                    ant.setImportloja(rst.getString("loja"));
                    ant.setImportid(rst.getString("id"));
                    ant.setDescricao(rst.getString("descricao"));                    
                    result.put(ant.getImportid(), ant);
                }
            }
        }
        
        return result;
    }
    
    public void gravar(ReceitaAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_receita");
            sql.put("sistema", vo.getImportsistema());
            sql.put("loja", vo.getImportloja());
            sql.put("id", vo.getImportid());
            sql.put("descricao", vo.getDescricao());
            sql.put("fichatecnica", vo.getFichatecnica());
            sql.put("qtdembalagemreceita", vo.getQtdembalagemreceita());
            sql.put("qtdembalagemproduto", vo.getQtdembalagemproduto());
            sql.put("rendimento", vo.getRendimento());
            sql.put("codigoatual", vo.getCodigoAtual());
            stm.execute(sql.getInsert());            
        }
    }
}