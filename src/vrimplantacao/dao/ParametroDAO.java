package vrimplantacao.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.classe.Global;
import vrimplantacao.vo.loja.ParametroVO;

public class ParametroDAO {

    public ParametroVO get(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT p.id, p.descricao, COALESCE(pv.valor, '') AS valor");
        sql.append(" FROM parametro AS p");
        sql.append(" LEFT JOIN parametrovalor AS pv ON pv.id_parametro = p.id AND pv.id_loja = " + Global.idLoja);
        sql.append(" WHERE p.id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        ParametroVO oParametro = new ParametroVO();

        if (rst.next()) {
            oParametro.id = rst.getInt("id");
            oParametro.descricao = rst.getString("descricao");
            oParametro.valor = rst.getString("valor");
        }

        stm.close();

        return oParametro;
    }
    
    public ParametroVO get(int i_id, int i_idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT p.id, p.descricao, COALESCE(pv.valor, '') AS valor");
        sql.append(" FROM parametro AS p");
        sql.append(" LEFT JOIN parametrovalor AS pv ON pv.id_parametro = p.id AND pv.id_loja = " + i_idLoja);
        sql.append(" WHERE p.id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        ParametroVO oParametro = new ParametroVO();

        if (rst.next()) {
            oParametro.id = rst.getInt("id");
            oParametro.descricao = rst.getString("descricao");
            oParametro.valor = rst.getString("valor");
        }

        stm.close();

        return oParametro;
    }
    
}
