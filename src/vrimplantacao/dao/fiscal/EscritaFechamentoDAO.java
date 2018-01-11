package vrimplantacao.dao.fiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class EscritaFechamentoDAO {

    public boolean isEncerramentoFiscal(String i_data) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        String data = "01/" + i_data;

        sql = new StringBuilder();
        sql.append("SELECT id FROM escritasaldo WHERE data = '" + Util.formatDataBanco(data) + "'");

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return true;
        } else {
            return false;
        }
    }
    
    public void verificar(String i_data) throws Exception {
        ResultSet rst = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        if (isEncerramentoFiscal(i_data.substring(3))) {
            throw new VRException("O fiscal do mês " + i_data.substring(3) + " já foi encerrado e não pode ser alterado!");
        }
        
        rst = stm.executeQuery("SELECT id FROM escritafechamento WHERE data = '" + Util.formatDataBanco(i_data) + "'");

        if (rst.next()) {
            throw new VRException("Este dia foi encerrado e não pode ser alterado!");
        }
    }
    
}
