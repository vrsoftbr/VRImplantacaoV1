/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.fiscal.inventario;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVOIMP;

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
            sql.put("id", vo.getId());
            sql.put("id_produto", vo.getIdProduto());
            sql.put("data", vo.getData());
            sql.put("datageracao", vo.getData());
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
            stm.execute(sql.getInsert());
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
            stm.execute(sql.getUpdate());
        }
    }

    public MultiMap<String, InventarioVOIMP> getInventario(int idLojaVR, int idProduto, Date data) throws Exception {
        MultiMap<String, InventarioVOIMP> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "id_loja, "
                    + "id_produto, "
                    + "data, "
                    + "datageracao, "
                    + "descricao, "
                    + "precovenda, \n"
                    + "quantidade, "
                    + "custocomimposto, "
                    + "custosemimposto, "
                    + "id_aliquotacredito, \n"
                    + "id_aliquotadebito, "
                    + "pis, "
                    + "cofins, "
                    + "customediocomimposto, "
                    + "customediosemimposto\n"
                    + "FROM inventario\n"
                    + "WHERE id_loja = " + idLojaVR + "\n"
                    + "AND id_produto = " + idProduto + "\n"
                    + "AND data = '" + data + "'"
            )) {
                while (rst.next()) {
                    InventarioVOIMP vo = new InventarioVOIMP();
                    vo.setIdLoja(rst.getInt("id_loja"));
                    vo.setIdProduto(rst.getInt("id_produto"));
                    vo.setData(rst.getDate("data"));
                    vo.setDatageracao(rst.getDate("datageracao"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setPrecoVenda(rst.getDouble("precovenda"));
                    vo.setQuantidade(rst.getDouble("quantidade"));
                    vo.setCustoComImposto(rst.getDouble("custocomimposto"));
                    vo.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    vo.setIdAliquotaCredito(rst.getInt("id_aliquotacredito"));
                    vo.setIdAliquotadebito(rst.getInt("id_aliquotadebito"));
                    vo.setPis(rst.getInt("pis"));
                    vo.setCofins(rst.getInt("cofins"));
                    vo.setCustoMedioComImposto(rst.getDouble("customediocomimposto"));
                    vo.setCustoMedioSemImposto(rst.getDouble("customediosemimposto"));
                    result.put(vo, String.valueOf(vo.getIdLoja()), String.valueOf(vo.getIdProduto()), String.valueOf(vo.getData()));
                }
            }
        }
        return result;
    }
}
