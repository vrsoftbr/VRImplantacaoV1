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
import vrimplantacao.vo.vrimplantacao.CodigoAnterior2VO;

/**
 *
 * @author lucasrafael
 */
public class CodigoAnterior2DAO {

    public void salvar(List<CodigoAnterior2VO> v_codigoAnterior2) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {

            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(v_codigoAnterior2.size());
            ProgressBar.setStatus("Importando dados Produto...Código Barras");
            
            sql = new StringBuilder();
            sql.append("CREATE TABLE IF NOT EXISTS implantacao.codigoanterior2 ( ");
            sql.append("codigoanterior numeric(14,0), ");
            sql.append("codigoatual numeric(14,0), ");
            sql.append("barras numeric(14,0) ");
            sql.append(");");            
            sql.append("DELETE FROM implantacao.codigoanterior2; ");
            
            stm.execute(sql.toString());
            
            for (CodigoAnterior2VO i_codigoAnterior : v_codigoAnterior2) {
                
                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant ");
                sql.append("on ant.codigoatual = p.id ");
                sql.append("where ant.codigoanterior = "+i_codigoAnterior.codigoAnterior+";");
                
                rst = stm.executeQuery(sql.toString());
                
                if (rst.next()) {
                    
                    sql = new StringBuilder();
                    sql.append("INSERT INTO implantacao.codigoanterior2 (");
                    sql.append("codigoanterior, codigoatual, barras) ");
                    sql.append("VALUES (");
                    sql.append(i_codigoAnterior.codigoAnterior+", ");
                    sql.append(rst.getInt("id")+", ");
                    sql.append(i_codigoAnterior.barras+" ");
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