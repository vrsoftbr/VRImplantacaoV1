package vrimplantacao.dao.estoque;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.classe.Global;
import vrimplantacao.vo.administrativo.TipoEntradaSaida;
import vrimplantacao.vo.estoque.AcertoTrocaVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

public class LogTrocaDAO {

    public void gravarMovimentacao(AcertoTrocaVO i_acertoTroca) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        double estoqueAtual = 0;

        sql = new StringBuilder();
        sql.append("SELECT troca");
        sql.append(" FROM produtocomplemento");
        sql.append(" WHERE id_produto = " + i_acertoTroca.idProduto);
        sql.append(" AND id_loja = " + i_acertoTroca.idLoja);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            estoqueAtual = rst.getDouble("troca");
        }

        double estoqueAnterior = 0;

        if (i_acertoTroca.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId()) {
            estoqueAnterior = estoqueAtual - i_acertoTroca.quantidade;

        } else if (i_acertoTroca.idTipoEntradaSaida == TipoEntradaSaida.SAIDA.getId()) {
            estoqueAnterior = estoqueAtual + i_acertoTroca.quantidade;
        }

        sql = new StringBuilder();
        sql.append("INSERT INTO logtroca (id_loja, id_produto, quantidade, datahora, id_usuario, estoqueanterior, estoqueatual,");
        sql.append(" id_tipoentradasaida, datamovimento) VALUES (");
        sql.append(i_acertoTroca.idLoja + ", ");
        sql.append(i_acertoTroca.idProduto + ", ");
        sql.append(i_acertoTroca.quantidade + ", ");
        sql.append("'" + Util.formatDataBanco(i_acertoTroca.data) + "', ");
        sql.append(Global.idUsuario + ", ");
        sql.append(estoqueAnterior + ", ");
        sql.append(estoqueAtual + ", ");
        sql.append(i_acertoTroca.idTipoEntradaSaida + ", ");
        sql.append("'" + Util.formatDataBanco(i_acertoTroca.data) + "')");

        stm.execute(sql.toString());

        stm.close();
    }
}
