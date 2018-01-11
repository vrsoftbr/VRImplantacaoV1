package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;

public class ContaContabilFinanceiroDAO {

    public int getIdContaContabilFiscal(int i_idContaContabilFinanceiro) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id_contacontabilfiscal FROM contacontabilfinanceiro WHERE id = " + i_idContaContabilFinanceiro);

        if (!rst.next() || rst.getObject("id_contacontabilfiscal") == null) {
            return -1;
        } else {
            return rst.getInt("id_contacontabilfiscal");
        }
    }
}
