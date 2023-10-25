package vrimplantacao.classe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoAccessTeste {

    private static Connection con;

    public static Connection newConnection(String i_database, String i_usuario, String i_senha) throws Exception {
        Connection connection;

        Properties props = new Properties();
        props.put("charSet", "ISO-8859-1");

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            connection = DriverManager.getConnection("jdbc:ucanaccess://" + i_database, props);


            return connection;
        } catch (ClassNotFoundException | SQLException ex) {
            throw ex;
        }
    }

    public void abrirConexao(String caminhoDoBanco) throws SQLException {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            con = DriverManager.getConnection("jdbc:ucanaccess://" + caminhoDoBanco);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Erro ao abrir a conexão com o banco de dados.");
        }
    }

    public Connection getConexao() {
        return con;
    }

    public void fecharConexao() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
