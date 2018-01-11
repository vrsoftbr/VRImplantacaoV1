package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.vo.cadastro.AssociadoItemVO;
import vrimplantacao.vo.cadastro.AssociadoVO;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

public class AssociadoDAO {

    public long verificar(int i_idProduto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id FROM associado WHERE id_produto = " + i_idProduto);

        long id = 0;

        if (rst.next()) {
            id = rst.getLong("id");
        }

        return id;
    }

    public AssociadoVO carregar(long i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT * FROM associado WHERE id = " + i_id);

        if (!rst.next()) {
            throw new VRException("Associado não encontrado!");
        }

        AssociadoVO oAssociado = new AssociadoVO();
        oAssociado.id = rst.getLong("id");
        oAssociado.idProduto = rst.getInt("id_produto");
        oAssociado.qtdEmbalagem = rst.getInt("qtdembalagem");

        sql = new StringBuilder();
        sql.append("SELECT item.*, produto.descricaocompleta AS produto");
        sql.append(" FROM associadoitem AS item");
        sql.append(" INNER JOIN produto ON produto.id = item.id_produto");
        sql.append(" WHERE item.id_associado = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            AssociadoItemVO oProduto = new AssociadoItemVO();
            oProduto.idProduto = rst.getInt("id_produto");
            oProduto.produto = rst.getString("produto");
            oProduto.qtdEmbalagem = rst.getInt("qtdembalagem");
            oProduto.percentualPreco = rst.getDouble("percentualpreco");
            oProduto.aplicaCusto = rst.getBoolean("aplicacusto");
            oProduto.aplicaEstoque = rst.getBoolean("aplicaestoque");
            oProduto.aplicaPreco = rst.getBoolean("aplicapreco");
            oProduto.percentualCustoEstoque = rst.getDouble("percentualcustoestoque");

            oAssociado.vProduto.add(oProduto);
        }

        stm.close();

        return oAssociado;
    }
}
