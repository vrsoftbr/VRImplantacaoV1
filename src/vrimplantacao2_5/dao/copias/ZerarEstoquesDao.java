package vrimplantacao2_5.dao.copias;

import java.sql.PreparedStatement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

/**
 *
 * @author Wesley
 */
public class ZerarEstoquesDao {

    public void zerarEstoquePorLoja(Integer lojaDestino, List<String> listaDeOpcoes) throws Exception {

        StringBuilder sql = new StringBuilder("UPDATE produtocomplemento SET ");
        sql.append(String.join(", ", listaDeOpcoes));
        sql.append(" WHERE id_loja = ?");

        try (PreparedStatement pst = Conexao.prepareStatement(sql.toString())) {

            pst.setInt(1, lojaDestino);

            int numeroAtualizado = pst.executeUpdate();

            Util.exibirMensagem("Atualizado um total de " + numeroAtualizado + " produtos na loja " + lojaDestino, "Número de atualizações");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao tentar zerar o estoque da loja.", e);
        }
    }
}
