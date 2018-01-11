package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.AtacadoVendedorClienteVO;

public class AtacadoVendedorClienteDAO {

    public void salvar(List<AtacadoVendedorClienteVO> v_vendedorCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            ProgressBar.setMaximum(v_vendedorCliente.size());
            ProgressBar.setStatus("Importando dados...atacado.vendedorcliente...");

            for (AtacadoVendedorClienteVO i_vendedorCliente : v_vendedorCliente) {
                sql = new StringBuilder();
                sql.append("select id from clienteeventual ");
                sql.append("where id = " + i_vendedorCliente.getId_clienteeventual());
                rst = stm.executeQuery(sql.toString());
                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("select id from atacado.vendedorcliente ");
                    sql.append("where id_clienteeventual = " + rst.getInt("id"));
                    rst2 = stm2.executeQuery(sql.toString());

                    if (!rst2.next()) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO atacado.vendedorcliente(\n"
                                + "            id_vendedor, id_clienteeventual) "
                                + " VALUES ("
                                + i_vendedorCliente.getId_vendedor() + ", "
                                + i_vendedorCliente.getId_clienteeventual() + ");");
                        stm.execute(sql.toString());
                    }
                }
                ProgressBar.next();
            }
            Conexao.commit();
            stm.close();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
