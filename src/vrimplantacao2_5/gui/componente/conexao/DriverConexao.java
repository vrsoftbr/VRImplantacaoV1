package vrimplantacao2_5.gui.componente.conexao;

/**
 *
 * @author guilhermegomes
 */
public interface DriverConexao {
    void abrirConexao(String i_ip, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception;
    void abrirConexao(String conString, String i_usuario, String i_senha) throws Exception;
    void close() throws Exception;
}
