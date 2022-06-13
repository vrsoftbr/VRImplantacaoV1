package vrimplantacao2.dao.cadastro.desmembramento;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
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
                    + "	i.percentualestoque\n"
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
                    item.setPercentualEstoque(rst.getInt("percentualpreco"));

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
            //sql.put("percentualestoque", vo.getPercentualEstoque());
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                while (rst.next()) {
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
            
            stm.executeQuery(sql.getInsert());

        }
    }
   
}
