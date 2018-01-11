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
import vrimplantacao.vo.vrimplantacao.ClienteEventualContatoVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialContatoVO;
/**
 *
 * @author lucasrafael
 */
public class ClienteEventualContatoDAO {

    public void salvar(List<ClienteEventualContatoVO> v_clienteEventualContato) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        
        try {
            
            Conexao.begin();
            
            ProgressBar.setStatus("Importando Dados...Contato Cliente Eventual...");
            ProgressBar.setMaximum(v_clienteEventualContato.size());

            stm = Conexao.createStatement();
            
            for (ClienteEventualContatoVO i_clienteEventualContato : v_clienteEventualContato) {
                
                sql = new StringBuilder();
                sql.append("select c.id from clienteeventual c ");
                sql.append("where c.id = " + i_clienteEventualContato.getIdClienteEventualAnterior()+" ");
                
                rst = stm.executeQuery(sql.toString());
                
                if (rst.next()) {
                
                    i_clienteEventualContato.setIdClienteEventual(rst.getInt("id"));
                    
                    sql = new StringBuilder();
                    sql.append("INSERT INTO clienteeventualcontato( ");
                    sql.append("id_clienteeventual, nome, telefone, id_tipocontato, celular, email) ");
                    sql.append("VALUES (");
                    sql.append(i_clienteEventualContato.getIdClienteEventual()+ ", ");
                    sql.append("'" + i_clienteEventualContato.getNome() + "', ");
                    sql.append("'" + i_clienteEventualContato.getTelefone() + "', ");
                    sql.append(i_clienteEventualContato.getIdTipoContato() + ", ");
                    sql.append("'" + i_clienteEventualContato.getCelular() + "', ");
                    sql.append("'" + i_clienteEventualContato.getEmail()+ "'");
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