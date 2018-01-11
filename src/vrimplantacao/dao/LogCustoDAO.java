package vrimplantacao.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.classe.Global;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

public class LogCustoDAO {

    public void gerarTransacao(int i_idProduto, String i_observacao, int i_idLoja) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT custosemimposto AS csi, custocomimposto AS cci, custosemimpostoanterior AS csia, custocomimpostoanterior AS ccia, ");
        sql.append(" customediosemimposto AS cmsi, customediocomimposto AS cmci, customediosemimpostoanterior AS cmsia, customediocomimpostoanterior AS cmcia");
        sql.append(" FROM produtocomplemento");
        sql.append(" WHERE id_produto = " + i_idProduto);
        sql.append(" AND id_loja = " + i_idLoja);

        rst = stm.executeQuery(sql.toString());

        double custoSemImposto = 0;
        double custoComImposto = 0;
        double custoSemImpostoAnterior = 0;
        double custoComImpostoAnterior = 0;
        double custoMedioSemImposto = 0;
        double custoMedioComImposto = 0;
        double custoMedioSemImpostoAnterior = 0;
        double custoMedioComImpostoAnterior = 0;

        if (rst.next()) {
            custoSemImposto = rst.getDouble("csi");
            custoComImposto = rst.getDouble("cci");
            custoSemImpostoAnterior = rst.getDouble("csia");
            custoComImpostoAnterior = rst.getDouble("ccia");
            custoMedioSemImposto = rst.getDouble("cmsi");
            custoMedioComImposto = rst.getDouble("cmci");
            custoMedioSemImpostoAnterior = rst.getDouble("cmsia");
            custoMedioComImpostoAnterior = rst.getDouble("cmcia");
        }

        sql = new StringBuilder();
        sql.append("INSERT INTO logcusto (id_produto, custosemimpostoanterior, customediosemimpostoanterior, custosemimposto, customediosemimposto, custocomimpostoanterior, customediocomimpostoanterior,");
        sql.append(" custocomimposto, customediocomimposto, datahora, datamovimento, id_usuario, id_loja, observacao) VALUES (");
        sql.append(i_idProduto + ", ");
        sql.append(custoSemImpostoAnterior + ", ");
        sql.append(custoMedioSemImpostoAnterior + ", ");
        sql.append(custoSemImposto + ", ");
        sql.append(custoMedioSemImposto + ", ");
        sql.append(custoComImpostoAnterior + ", ");
        sql.append(custoMedioComImpostoAnterior + ", ");
        sql.append(custoComImposto + ", ");
        sql.append(custoMedioComImposto + ", ");
        sql.append("'" + Util.formatDataHoraBanco(Util.getDataHoraAtual()) + "', ");
        sql.append("'" + Util.formatDataBanco(new DataProcessamentoDAO().get()) + "', ");
        sql.append(Global.idUsuario + ", ");
        sql.append(i_idLoja + ", ");
        sql.append("'" + i_observacao + "')");

        stm.execute(sql.toString());

        stm.close();
    }
}
