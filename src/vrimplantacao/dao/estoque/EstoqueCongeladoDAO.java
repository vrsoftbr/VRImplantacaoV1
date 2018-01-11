package vrimplantacao.dao.estoque;

import java.sql.Statement;
import vrimplantacao.vo.estoque.EstoqueCongeladoVO;
import vrframework.classe.Conexao;

public class EstoqueCongeladoDAO {

    public void salvar(EstoqueCongeladoVO i_congelado) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("INSERT INTO estoquecongelado (id_produto, id_loja, id_tipoentradasaida, id_tipomovimentacao,");
            sql.append(" quantidade, baixareceita, baixaassociado, baixaperda, observacao) VALUES (");
            sql.append(i_congelado.idProduto + ", ");
            sql.append(i_congelado.idLoja + ", ");
            sql.append(i_congelado.idTipoEntradaSaida + ", ");
            sql.append(i_congelado.idTipoMovimentacao + ", ");
            sql.append(i_congelado.quantidade + ", ");
            sql.append(i_congelado.baixaReceita + ", ");
            sql.append(i_congelado.baixaAssociado + ", ");
            sql.append(i_congelado.baixaPerda + ", ");
            sql.append("'" + i_congelado.observacao + "')");

            stm.execute(sql.toString());

            Conexao.commit();
            stm.close();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
