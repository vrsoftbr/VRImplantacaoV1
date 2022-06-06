package vrimplantacao2.dao.cadastro.desmembramento;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoAnteriorVO;

public class DesmembramentoDAO {

    public void gravar(DesmembramentoVO desmem) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("desmembramento");
            sql.setSchema("public");
            sql.put("id", desmem.getId());
            sql.put("id_produto", desmem.getIdProduto());
            sql.put("id_situacaocadastro", desmem.getSituacaoCadastro());

            try {
                stm.execute(sql.getInsert());
            } catch (Exception a) {
                try {
                    stm.execute(sql.getUpdate());
                } catch (Exception e) {
                    System.out.println(sql.getInsert());
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }

    public void gravarDesmembramentoItens(DesmembramentoAnteriorVO itens) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("desmembramentoitem");
            sql.setSchema("public");
            sql.put("id", itens.getImpId());  // a definir ainda
            sql.put("id_desmembramento", itens.getCodigoAtual());
            sql.put("id_produto", itens.getProdutoPai());
            sql.put("produtofilho", itens.getProdutoFilho());
            sql.put("percentual", itens.getPercentual());

            try {
                stm.execute(sql.getInsert());
            } catch (Exception a) {
                try {
                    stm.execute(sql.getUpdate());
                } catch (Exception e) {
                    System.out.println(sql.getInsert());
                    e.printStackTrace();
                    throw e;
                }
            }

        }
    }

    public int getId() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id "
                    + "from desmembramento"
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                } else {
                    return -1;
                }
            }
        }
    }

    List<DesmembramentoIMP> getDesmembramentoItens() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*public void salvarDesmembramentoItens(DesmembramentoAnteriorVO anterior) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/
}
