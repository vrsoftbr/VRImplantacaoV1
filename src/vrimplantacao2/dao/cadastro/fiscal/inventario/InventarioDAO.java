/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.fiscal.inventario;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.ProdutoInventario;

/**
 *
 * @author lucasrafael
 */
public class InventarioDAO {

    public void salvar(InventarioVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("public");
            sql.setTableName("inventario");
            sql.put("id_loja", vo.getIdLoja());
            sql.put("id_produto", vo.getIdProduto());
            sql.put("data", vo.getData());
            sql.put("datageracao", vo.getDatageracao());
            sql.put("descricao", vo.getDescricao());
            sql.put("precovenda", vo.getPrecoVenda());
            sql.put("quantidade", vo.getQuantidade());
            sql.put("custocomimposto", vo.getCustoComImposto());
            sql.put("custosemimposto", vo.getCustoSemImposto());
            sql.put("id_aliquotacredito", vo.getIdAliquotaCredito());
            sql.put("id_aliquotadebito", vo.getIdAliquotadebito());
            sql.put("pis", vo.getPis());
            sql.put("cofins", vo.getCofins());
            sql.put("customediocomimposto", vo.getCustoMedioComImposto());
            sql.put("customediosemimposto", vo.getCustoMedioSemImposto());
            sql.getReturning().add("id");
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                rst.next();
                vo.setId(rst.getInt("id"));
            }
        }
    }

    public void atualizar(InventarioVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("public");
            sql.setTableName("inventario");
            sql.put("quantidade", "quantidade + " + vo.getQuantidade());
            sql.setWhere("id_loja = " + vo.getIdLoja() + " "
                    + "and id_produto = " + vo.getIdProduto() + " "
                    + "and data = '" + vo.getData() + "'");
            sql.getReturning().add("id");
            try (ResultSet rst = stm.executeQuery(
                    sql.getUpdate()
            )) {
                rst.next();
                vo.setId(rst.getInt("id"));
            }
        }
    }

    public Map<String, ProdutoInventario> getProdutosInventario(String sistema, String lojaOrigem) throws Exception {
        Map<String, ProdutoInventario> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.impid,\n" +
                    "	ant.codigoatual,\n" +
                    "	p.descricaocompleta,\n" +
                    "	pc.precovenda,\n" +
                    "	pc.custocomimposto,\n" +
                    "	pc.custosemimposto,\n" +
                    "	pc.customediocomimposto,\n" +
                    "	pc.customediosemimposto,\n" +
                    "	pa.id_aliquotacredito,\n" +
                    "	pa.id_aliquotadebito\n" +
                    "from\n" +
                    "	implantacao.codant_produto ant\n" +
                    "	join loja l on\n" +
                    "		l.id = 1\n" +
                    "	join fornecedor f on\n" +
                    "		l.id_fornecedor = f.id\n" +
                    "	join produto p on\n" +
                    "		ant.codigoatual = p.id\n" +
                    "	join produtocomplemento pc on\n" +
                    "		pc.id_produto = p.id and\n" +
                    "		pc.id_loja = l.id\n" +
                    "	join produtoaliquota pa on\n" +
                    "		pa.id_produto = p.id and\n" +
                    "		pa.id_estado = f.id_estado\n" +
                    "where\n" +
                    "	ant.impsistema = '" + sistema + "' and\n" +
                    "	ant.imploja = '" + lojaOrigem + "'\n" +
                    "order by\n" +
                    "	impid"
            )) {
                while (rst.next()) {
                    ProdutoInventario pi = new ProdutoInventario();
                    
                    pi.setIdProduto(rst.getInt("codigoatual"));
                    pi.setDescricao(rst.getString("descricaocompleta"));
                    pi.setPrecoVenda(rst.getDouble("precovenda"));
                    pi.setCustoComImposto(rst.getDouble("custocomimposto"));
                    pi.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    pi.setCustoMedioComImposto(rst.getDouble("customediocomimposto"));
                    pi.setCustoMedioSemImposto(rst.getDouble("customediosemimposto"));
                    pi.setIdAliquotaCredito(rst.getInt("id_aliquotacredito"));
                    pi.setIdAliquotaDebito(rst.getInt("id_aliquotadebito"));
                    
                    result.put(rst.getString("impid"), pi);
                }
            }
        }
        
        return result;
    }

}
