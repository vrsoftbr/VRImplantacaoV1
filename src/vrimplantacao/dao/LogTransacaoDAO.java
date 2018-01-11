package vrimplantacao.dao;

import java.sql.Statement;
import vrimplantacao.classe.Global;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoTransacao;
import vrframework.classe.Conexao;
import vrframework.classe.Util;

public class LogTransacaoDAO {

    public void gerar(Formulario i_formulario, TipoTransacao i_tipoTransacao, long i_referencia, String i_observacao) throws Exception {
        gerar(i_formulario, i_tipoTransacao, i_referencia, i_observacao, 0);
    }
    
    public void gerar(Formulario i_formulario, TipoTransacao i_tipoTransacao, long i_referencia, String i_observacao, long i_idReferencia) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("INSERT INTO logtransacao (id_loja, id_formulario, referencia, id_referencia, id_tipotransacao, observacao, datahora, datamovimento,");
        sql.append(" id_usuario, ipterminal, versao) VALUES (");
        sql.append(Global.idLoja + ", ");
        sql.append(i_formulario.getId() + ", ");
        sql.append(i_referencia + ", ");
        sql.append(i_idReferencia + ", ");
        sql.append(i_tipoTransacao.getId() + ", ");
        sql.append("'" + i_observacao + "', ");
        sql.append("'" + Util.formatDataHoraBanco(Util.getDataHoraAtual()) + "', ");
        sql.append("'" + Util.formatDataBanco(new DataProcessamentoDAO().get()) + "', ");
        sql.append(Global.idUsuario + ", ");
        sql.append("'" + Util.getIp() + "',");
        sql.append("'" + Global.VERSAO + "')");

        stm.execute(sql.toString());

        stm.close();
    }
}
