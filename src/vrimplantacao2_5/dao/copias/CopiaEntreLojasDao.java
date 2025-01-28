package vrimplantacao2_5.dao.copias;

import java.sql.PreparedStatement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

/**
 *
 * @author Wesley
 */
public class CopiaEntreLojasDao {

    public void copiaInfoProdutos(Integer lojaOrigem, Integer LojaDestino, List<String> listaDeOpcoes) throws Exception {

        StringBuilder sql = new StringBuilder("UPDATE produtocomplemento a SET ");
        sql.append(String.join(", ", listaDeOpcoes));
    sql.append(" FROM (SELECT id_produto, precovenda, precodiaseguinte, custocomimposto, custosemimposto, margem, margemminima, margemmaxima, id_situacaocadastro FROM produtocomplemento WHERE id_loja = ?) b ");
        sql.append("WHERE a.id_produto = b.id_produto AND a.id_loja = ?");

        try (PreparedStatement pst = Conexao.prepareStatement(sql.toString())) {

            pst.setInt(1, lojaOrigem);
            pst.setInt(2, LojaDestino);

            int numeroAtualizado = pst.executeUpdate();

            Util.exibirMensagem("Atualizado um total de " + numeroAtualizado + " produtos na loja " + LojaDestino, "Número de atualizações");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao tentar copiar as informações de um loja para outra.", e);
        }
    }
}
