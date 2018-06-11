/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.fiscal.inventario;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioAnteriorVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVO;

/**
 *
 * @author lucasrafael
 */
public class InventarioAnteriorDAO {

    public static void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(""
                    + "select table_schema||'.'||table_name tabela "
                    + "from information_schema.tables "
                    + "where table_schema = 'implantacao' "
                    + "and table_name = 'codant_inventario'"
            )) {
                if (rst.next()) {
                    return;
                }
            }
            stm.execute(
                    "create table implantacao.codant_inventario ("
                    + "sistema varchar,\n"
                    + "idloja varchar, \n"
                    + "id varchar, \n"
                    + "idatual integer, \n"
                    + "codigoanteior varchar, \n"
                    + "codigoatual varchar, \n"
                    + "data date, \n"
                    + "datageracao date, \n"
                    + "descricao varchar, \n"
                    + "precovenda numeric, \n"
                    + "quantidade numeric, \n"
                    + "custocomimposto numeric, \n"
                    + "custosemimposto nuemric, \n"
                    + "idaliquotacredito integer, \n"
                    + "idaliquotadebito integer, \n"
                    + "pis numeric, \n"
                    + "cofins numeric, \n"
                    + "customediocomImposto numeric, \n"
                    + "customediosemImposto numeric\n"
                    + ")"
            );
        }
    }

    public InventarioAnteriorDAO() throws Exception {
        createTable();
    }

    public Map<String, InventarioAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, InventarioAnteriorVO> result = new LinkedHashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "sistema,\n"
                    + "idloja, \n"
                    + "id, \n"
                    + "idatual, \n"
                    + "codigoanteior, \n"
                    + "codigoatual, \n"
                    + "data, \n"
                    + "datageracao, \n"
                    + "descricao, \n"
                    + "precovenda, \n"
                    + "quantidade, \n"
                    + "custocomimposto, \n"
                    + "custosemimposto, \n"
                    + "idaliquotacredito, \n"
                    + "idaliquotadebito, \n"
                    + "pis, \n"
                    + "cofins, \n"
                    + "customediocomimposto, \n"
                    + "customediosemimposto \n"
                    + "from codant_inventario "
                    + "where sistema = " + SQLUtils.stringSQL(sistema) + "\n"
                    + "and idloja = " + SQLUtils.stringSQL(loja)
            )) {
                while (rst.next()) {
                    InventarioAnteriorVO ant = new InventarioAnteriorVO();
                    ant.setSistema(rst.getString("sistema"));
                    ant.setIdLoja(rst.getString("idloja"));
                    ant.setId(rst.getString("id"));

                    if (rst.getString("idatual") != null) {
                        InventarioVO vo = new InventarioVO();
                        vo.setId(rst.getInt("idatual"));
                        ant.setIdAtual(vo);
                    }

                    ant.setCodigoAnteior(rst.getString("codigoanteior"));
                    ant.setCodigoAtual(rst.getString("codigoatual"));
                    ant.setData(rst.getDate("data"));
                    ant.setDatageracao(rst.getDate("datageracao"));
                    ant.setDescricao(rst.getString("descricao"));
                    ant.setPrecoVenda(rst.getDouble("precovenda"));
                    ant.setQuantidade(rst.getDouble("quantidade"));
                    ant.setCustoComImposto(rst.getDouble("custocomimposto"));
                    ant.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    ant.setIdAliquotaCredito(rst.getString("idaliquotacredito"));
                    ant.setIdAliquotadebito(rst.getString("idaliquotadebito"));
                    ant.setPis(rst.getDouble("pis"));
                    ant.setCofins(rst.getDouble("cofins"));
                    ant.setCustoMedioComImposto(rst.getDouble("customediocomimposto"));
                    ant.setCustoMedioSemImposto(rst.getDouble("customediosemimposto"));
                    result.put(ant.getId(), ant);
                }
            }
        }
        return result;
    }

    public void gravar(InventarioAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_inventario");
            sql.put("sistema", anterior.getSistema());
            sql.put("idLoja", anterior.getIdLoja());
            sql.put("id", anterior.getId());
            if (anterior.getIdAtual() != null) {
                sql.put("idAtual", anterior.getIdAtual().getId());
            }
            sql.put("codigoAnterior", anterior.getCodigoAnteior());
            sql.put("codigoAtual", anterior.getCodigoAtual());
            sql.put("descricao", anterior.getDescricao());
            sql.put("custocomimposto", anterior.getCustoComImposto());
            sql.put("custosemimposto", anterior.getCustoSemImposto());
            sql.put("precovenda", anterior.getPrecoVenda());
            sql.put("quantidade", anterior.getQuantidade());
            sql.put("customediocomimposto", anterior.getCustoMedioComImposto());
            sql.put("customediosemimposto", anterior.getCustoMedioSemImposto());
            sql.put("idaliquotacredito", anterior.getIdAliquotaCredito());
            sql.put("idaliquotadebito", anterior.getIdAliquotadebito());
            sql.put("pis", anterior.getPis());
            sql.put("cofins", anterior.getCofins());
            stm.execute(sql.getInsert());
        }
    }
}
