/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialContatoVO;
/**
 *
 * @author lucasrafael
 */
public class ClientePreferencialContatoDAO {

    public void salvar(List<ClientePreferencialContatoVO> v_clientePreferencialContato) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        
        try {
            
            Conexao.begin();
            
            ProgressBar.setStatus("Importando Dados...Contato Cliente Preferencial...");
            ProgressBar.setMaximum(v_clientePreferencialContato.size());

            stm = Conexao.createStatement();
            
            for (ClientePreferencialContatoVO i_clientePreferencialContato : v_clientePreferencialContato) {
                
                sql = new StringBuilder();
                sql.append("select c.id from clientepreferencial c ");
                sql.append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ");
                sql.append("where ant.codigoanterior = " + i_clientePreferencialContato.getIdClientePreferncialAnterior()+" ");
                
                rst = stm.executeQuery(sql.toString());
                
                if (rst.next()) {
                
                    i_clientePreferencialContato.setIdClientePreferencial(rst.getInt("id"));
                    
                    sql = new StringBuilder();
                    sql.append("INSERT INTO clientepreferencialcontato( ");
                    sql.append("id_clientepreferencial, nome, telefone, id_tipocontato, celular) ");
                    sql.append("VALUES (");
                    sql.append(i_clientePreferencialContato.getIdClientePreferencial() + ", ");
                    sql.append("'" + i_clientePreferencialContato.getNome() + "', ");
                    sql.append("'" + i_clientePreferencialContato.getTelefone() + "', ");
                    sql.append(i_clientePreferencialContato.getIdTipoContato() + ", ");
                    sql.append("'" + i_clientePreferencialContato.getCelular() + "'");
                    sql.append(");");

                    stm.execute(sql.toString());
                
                }
                
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}