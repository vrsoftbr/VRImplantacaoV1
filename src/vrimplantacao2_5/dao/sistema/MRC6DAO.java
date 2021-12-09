/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Wagner
 */
public class MRC6DAO extends InterfaceDAO implements MapaTributoProvider{
    
    @Override
    public String getSistema(){
        return "MRC6";
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception{
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()){
            try(ResultSet rst = stm.executeQuery(
                    ""
            )){
                while(rst.next()){
                    result.add(new MapaTributoIMP(
                            rst.getString("id"), 
                            rst.getString("descricao")
                    ));
                }
            }
        }
        return result;
    }
}
