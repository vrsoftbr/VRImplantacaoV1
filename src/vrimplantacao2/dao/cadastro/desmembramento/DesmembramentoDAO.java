package vrimplantacao2.dao.cadastro.desmembramento;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoItemVO;

public class DesmembramentoDAO {

    public Map<Integer, DesmembramentoVO> getDesmembramentosExistentes() throws Exception {
        Map<Integer, DesmembramentoVO> result = new HashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	id_produto\n"
                    + "from\n"
                    + "	desmembramento\n"
                    + "order by\n"
                    + "	id_produto"
            )) {
                while (rst.next()) {
                    DesmembramentoVO vo = new DesmembramentoVO();
                    vo.setId(rst.getInt("id"));
                    vo.setIdProduto(rst.getInt("id_produto"));
                    result.put(vo.getIdProduto(), vo);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	i.id,\n"
                    + "	d.id_produto id_produto_pai,\n"
                    + "	i.id_produto,\n"
                    + "	i.percentualestoque,\n"
                    + " 0 percentualperda,\n"
                    + " 0 percentualdesossa,\n"
                    + " 0 percentualcusto\n"
                    + "from\n"
                    + "	desmembramento d\n"
                    + "	join desmembramentoitem i on i.id_desmembramento = d.id\n"
                    + "order by\n"
                    + "	id_produto"
            )) {
                while (rst.next()) {
                    DesmembramentoVO pai = result.get(rst.getInt("id_produto_pai"));
                    DesmembramentoItemVO item = new DesmembramentoItemVO();

                    item.setId(rst.getInt("id"));
                    item.setIdDesmembramento(pai.getId());
                    item.setIdProduto(rst.getInt("id_produto"));
                    item.setPercentualEstoque(rst.getInt("percentualestoque"));

                    pai.getItens().put(item.getIdProduto(), item);
                }
            }
        }

        return result;
    }

    public void gravar(DesmembramentoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("desmembramento");
            sql.put("id_produto", vo.getIdProduto());
            sql.put("id_situacaocadastro", 1);
            sql.getReturning().add("id");
            
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                if (rst.next()) {
                    vo.setId(rst.getInt("id"));
                }
            }
        }
    }

    public void gravar(DesmembramentoItemVO vItem) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("desmembramentoitem");
            sql.put("id_desmembramento", vItem.getIdDesmembramento());
            sql.put("id_produto", vItem.getIdProduto());
            sql.put("percentualestoque", vItem.getPercentualEstoque());
            sql.put("percentualperda", 0.0d);
            sql.put("percentualdesossa", vItem.getPercentualDesossa());
            sql.put("percentualcusto", vItem.getPercentualCusto());

            stm.execute(sql.getInsert());
        }
    }

    public Set<Integer> getProdutosAtivos(int lojaVR) throws Exception {
        Set<Integer> result = new HashSet<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_produto from produtocomplemento where id_loja = " + lojaVR + " and id_situacaocadastro = 1"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id_produto"));
                }
            }
        }

        return result;
    }
}
