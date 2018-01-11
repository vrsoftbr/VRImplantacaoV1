package vrimplantacao.dao.estoque;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.vo.administrativo.AcertoEstoqueVO;
import vrimplantacao.vo.administrativo.TipoEntradaSaida;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

public class LogEstoqueDAO {

    public void gravarMovimentacao(AcertoEstoqueVO i_acertoEstoque) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        double custoSemImposto = 0;
        double custoComImposto = 0;
        double custoMedioSemImposto = 0;
        double custoMedioComImposto = 0;
        double estoqueAtual = 0;

        sql = new StringBuilder();
        sql.append("SELECT custosemimposto, customediocomimposto, customediosemimposto, custocomimposto, estoque");
        sql.append(" FROM produtocomplemento");
        sql.append(" WHERE id_produto = " + i_acertoEstoque.idProduto);
        sql.append(" AND id_loja = " + i_acertoEstoque.idLoja);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            custoSemImposto = rst.getDouble("custosemimposto");
            custoComImposto = rst.getDouble("custocomimposto");
            custoMedioSemImposto = rst.getDouble("customediosemimposto");
            custoMedioComImposto = rst.getDouble("customediocomimposto");
            estoqueAtual = rst.getDouble("estoque");
        }

        double estoqueAnterior = 0;

        if (i_acertoEstoque.idTipoEntradaSaida == TipoEntradaSaida.ENTRADA.getId()) {
            estoqueAnterior = estoqueAtual - i_acertoEstoque.quantidade;

        } else if (i_acertoEstoque.idTipoEntradaSaida == TipoEntradaSaida.SAIDA.getId()) {
            estoqueAnterior = estoqueAtual + i_acertoEstoque.quantidade;
        }

        sql = new StringBuilder();
        sql.append("INSERT INTO logestoque (id_loja, id_produto, quantidade, id_tipomovimentacao, datahora, id_usuario, estoqueanterior,");
        sql.append(" observacao, estoqueatual, id_tipoentradasaida, custosemimposto, custocomimposto, customediosemimposto, customediocomimposto, datamovimento) VALUES (");
        sql.append(i_acertoEstoque.idLoja + ", ");
        sql.append(i_acertoEstoque.idProduto + ", ");
        sql.append(i_acertoEstoque.quantidade + ", ");
        sql.append(i_acertoEstoque.idTipoMovimentacao + ", ");
        sql.append("'" + Util.formatDataHoraBanco(Util.getDataHoraAtual()) + "', ");
        sql.append(Global.idUsuario + ", ");
        sql.append(estoqueAnterior + ", ");
        sql.append("'" + i_acertoEstoque.observacao + "', ");
        sql.append(estoqueAtual + ", ");
        sql.append(i_acertoEstoque.idTipoEntradaSaida + ", ");
        sql.append(custoSemImposto + ", ");
        sql.append(custoComImposto + ", ");
        sql.append(custoMedioSemImposto + ", ");
        sql.append(custoMedioComImposto + ", ");
        sql.append("'" + Util.formatDataBanco(new DataProcessamentoDAO().get()) + "')");

        stm.execute(sql.toString());

        stm.close();
    }
}
