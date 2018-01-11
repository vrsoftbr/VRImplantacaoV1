package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;

public class BancoDAO {

    public int getIdContaContabilFiscal(int i_idBanco, String i_agencia, String i_conta) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id_contacontabilfiscal FROM bancoconta");
        sql.append(" WHERE id_banco = " + i_idBanco + " AND agencia = '" + i_agencia + "' AND conta = '" + i_conta + "'");

        rst = stm.executeQuery(sql.toString());

        if (!rst.next() || rst.getObject("id_contacontabilfiscal") == null) {
            return -1;
        } else {
            return rst.getInt("id_contacontabilfiscal");
        }
    }
    
    public int getId(int i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        stm = Conexao.createStatement();
        
        rst = stm.executeQuery("select id from banco where id = " + i_codigo);
        
        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return 804;
        }
    }
}
