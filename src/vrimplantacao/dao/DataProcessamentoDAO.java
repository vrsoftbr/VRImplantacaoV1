package vrimplantacao.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class DataProcessamentoDAO {

    public String get() throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT data FROM dataprocessamento WHERE id_loja = 1");

        if (!rst.next()) {
            throw new VRException("Data processamento não configurada!");
        }

        return Util.formatDataGUI(rst.getDate("data"));
    }
}
