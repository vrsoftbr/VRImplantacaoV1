package vrimplantacao.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

public class EstadoDAO {

    public int getId(String i_sigla) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT * FROM estado WHERE sigla = '" + i_sigla + "'");

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Estado " + i_sigla + " não encontrado!");
        }

        int id = rst.getInt("id");

        stm.close();

        return id;
    }
}
