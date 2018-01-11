/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

/**
 *
 * @author lucasrafael
 */
public class IcmsDAO {

    public int carregar() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        int idAliquotaICMS = 8;

        try {
            stm = Conexao.createStatement();

            sql = new StringBuilder();

            sql.append("SELECT id FROM aliquota WHERE UPPER(descricao)= UPPER('ISENTO')");
            //sql.append("SELECT id FROM aliquota ");
            
            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                idAliquotaICMS = rst.getInt("id");
            }

            if (idAliquotaICMS == 0) {
                throw new VRException("Aliquota Isento não cadastrada!");
            }

            return idAliquotaICMS;

        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        } finally {
            Conexao.destruir(null, stm, rst);
        }

    }

    public int carregarOutras() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        int idAliquotaICMS = 0;

        try {
            stm = Conexao.createStatement();

            sql = new StringBuilder();

            sql.append("SELECT id FROM aliquota WHERE UPPER(descricao)= UPPER('OUTRAS')");

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                idAliquotaICMS = rst.getInt("id");
            }

            if (idAliquotaICMS == 0) {
                throw new VRException("Aliquota Outras não cadastrada!");
            }

            return idAliquotaICMS;

        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        } finally {
            Conexao.destruir(null, stm, rst);
        }
    }
    
    public int carregarIcmsCeara(String codigo) {
        int idAliquotaICMS = 8;
        
        if ("F00".equals(codigo)) {
            idAliquotaICMS = 7;
        } else if ("F01".equals(codigo)) {
            idAliquotaICMS = 7;
        } else if ("F90".equals(codigo)) {
            idAliquotaICMS = 7;
        } else if ("I00".equals(codigo)) {
            idAliquotaICMS = 6;
        } else if ("T17".equals(codigo)) {
            idAliquotaICMS = 31;
        } else if ("T27".equals(codigo)) {
            idAliquotaICMS = 32;
        } else {
            idAliquotaICMS = 8;
        }
        
        return idAliquotaICMS;
    }
}
