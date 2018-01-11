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
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.vo.vrimplantacao.AgendaTelefoneVO;

/**
 *
 * @author lucasrafael
 */
public class AgendaTelefoneDAO {
    
    public void salvar(List<AgendaTelefoneVO> v_agendaTelefone, int idLojaCliente) throws Exception  {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        boolean achou;
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            
            ProgressBar.setStatus("Importando dados...Agenda Fornecedor/Cliente Preferencial...");
            ProgressBar.setMaximum(v_agendaTelefone.size());
            
            for (AgendaTelefoneVO i_agendaTelefone : v_agendaTelefone) {
                
                achou = false;
                
                if (i_agendaTelefone.getId_tipotelefone() == 4) {
                    sql = new StringBuilder();
                    sql.append("select c.nome from clientepreferencial c ");
                    sql.append("inner join implantacao.codigoanteriorcli ant ");
                    sql.append("on ant.codigoatual = c.id ");
                    sql.append("where ant.codigoanterior = " + i_agendaTelefone.getIdCliente());
                    
                    rst = stm.executeQuery(sql.toString());
                    
                    if (rst.next()) {
                        achou = true;
                        i_agendaTelefone.setNome(rst.getString("nome"));
                    }
                    
                } else if (i_agendaTelefone.getId_tipotelefone() == 5) {
                    sql = new StringBuilder();
                    sql.append("select f.razaosocial from fornecedor f ");
                    sql.append("inner join implantacao.codigoanteriorforn ant ");
                    sql.append("on ant.codigoatual = f.id ");
                    sql.append("where ant.codigoanterior = " + i_agendaTelefone.getIdFornecedor());
                    
                    rst = stm.executeQuery(sql.toString());
                    
                    if (rst.next()) {
                        achou = true;
                        i_agendaTelefone.setNome(rst.getString("razaosocial"));
                    }
                    
                }
                
                if (achou) {
                    i_agendaTelefone.setId(new CodigoInternoDAO().get("agendatelefone"));

                    sql = new StringBuilder();
                    sql.append("INSERT INTO agendatelefone( ");
                    sql.append("id, id_loja, nome, empresa, telefone, ");
                    sql.append("id_tipotelefone, id_usuario, email) ");
                    sql.append("VALUES ( ");
                    sql.append(i_agendaTelefone.getId() + ", ");
                    sql.append(i_agendaTelefone.getId_loja() + ", ");
                    sql.append("'" + i_agendaTelefone.getNome() + "', ");
                    sql.append("'" + i_agendaTelefone.getEmpresa() + "', ");
                    sql.append("'" + i_agendaTelefone.getTelefone() + "', ");
                    sql.append(i_agendaTelefone.getId_tipotelefone() + ", ");
                    sql.append(i_agendaTelefone.getId_usuario() + ", ");
                    sql.append("'" + i_agendaTelefone.getEmail() +"' ");
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