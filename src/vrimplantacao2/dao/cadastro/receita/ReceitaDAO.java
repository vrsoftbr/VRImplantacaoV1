/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.receita;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.receita.ReceitaVO;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.receita.ReceitaItemVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaLojaVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaProdutoVO;

/**
 *
 * @author lucasrafael
 */
public class ReceitaDAO {

    public IDStack getIdsVagos(int maxId) throws Exception {
        IDStack result = new IDStack();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from\n"
                    + "(SELECT id FROM generate_series(1, " + maxId + ")\n"
                    + "AS s(id) EXCEPT SELECT id FROM comprador) AS receita ORDER BY id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getLong("id"));
                }
            }
        }
        return result;
    }

    public void gravar(ReceitaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("receita");
            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("fichatecnica", vo.getFichatecnica());
            sql.put("id_situacaocadastro", vo.getId_situacaocadastro());
            stm.execute(sql.getInsert());
        }
    }

    public void gravarItem(ReceitaItemVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("receitaitem");
            sql.put("id_receita", vo.getId_receita());
            sql.put("id_produto", vo.getId_produto());
            sql.put("qtdembalagemreceita", vo.getQtdembalagemreceita());
            sql.put("qtdembalagemproduto", vo.getQtdembalagemproduto());            
            sql.put("baixaestoque", vo.isBaixaestoque());
            sql.put("fatorconversao", vo.getFatorconversao());
            sql.put("embalagem", vo.isEmbalagem());
            
            stm.execute(sql.getInsert());
        }
    }

    public void gravarProduto(ReceitaProdutoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("receitaproduto");
            sql.put("id_receita", vo.getId_receita());
            sql.put("id_produto", vo.getId_produto());
            sql.put("rendimento", vo.getRendimento());
            stm.execute(sql.getInsert());
        }
    }

    public void gravarReceitaLoja(ReceitaLojaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("receitaloja");
            sql.put("id_loja", vo.getId_loja());
            sql.put("id_receita", vo.getId_receita());
            stm.execute(sql.getInsert());
        }
    }

    public MultiMap<Integer, Void> getReceitaItem() throws Exception {
        MultiMap<Integer, Void> result = new MultiMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  id_produto,\n"
                    + "  id_receita\n"
                    + "from receitaproduto"
            )) {
                while (rst.next()) {
                    result.put(null, rst.getInt("id_receita"), rst.getInt("id_produto"));
                }
            }
        }

        return result;
    }

    public MultiMap<Integer, Void> getReceitaProduto() throws Exception {
        MultiMap<Integer, Void> result = new MultiMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id_produto "
                    + "from receitaproduto "
            )) {
                while (rst.next()) {
                    result.put(null, rst.getInt("id_produto"), rst.getInt("id_produto"));
                }
            }
        }
        return result;
    }

    public MultiMap<Integer, Void> getReceitaLoja(int idLojaVR) throws Exception {
        MultiMap<Integer, Void> result = new MultiMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id_receita,\n"
                    + "id_loja\n"
                    + "from receitaloja"
            )) {
                while (rst.next()) {
                    result.put(null, rst.getInt("id_receita"), rst.getInt("id_loja"));
                }
            }
        }
        return result;
    }
}
