/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NcmVO;

/**
 *
 * @author lucasrafael
 */
public class NcmDAO {

    public NcmVO validar(String ncm) throws Exception {
        
        ncm = Utils.formataNumero(ncm);
        
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        Map<String, Boolean> vNCMValido = null;
        NcmVO oNcm = null;

        String ncm1Parts = "";
        String ncm2Parts = "";
        String ncm3Parts = "";

        try {
            stm = Conexao.createStatement();
            Conexao.begin();

            if (ncm.trim().length() >= 6) {
                if (!"00000000".equals(ncm.trim())
                        && (!"11111111".equals(ncm.trim()))
                        && (!"22222222".equals(ncm.trim()))
                        && (!"33333333".equals(ncm.trim()))
                        && (!"44444444".equals(ncm.trim()))
                        && (!"55555555".equals(ncm.trim()))
                        && (!"66666666".equals(ncm.trim()))
                        && (!"77777777".equals(ncm.trim()))
                        && (!"88888888".equals(ncm.trim()))
                        && (!"99999999".equals(ncm.trim()))) {

                    if (ncm.length() >= 8) {
                        ncm1Parts = ncm.substring(0, 4);
                        ncm2Parts = ncm.substring(4, 6);
                        ncm3Parts = ncm.substring(6, 8);
                    } else if (ncm.length() == 7) {
                        ncm1Parts = ncm.substring(0, 3);
                        ncm2Parts = ncm.substring(3, 5);
                        ncm3Parts = ncm.substring(5, 7);
                    } else if (ncm.length() == 6) {
                        ncm1Parts = ncm.substring(0, 2);
                        ncm2Parts = ncm.substring(2, 4);
                        ncm3Parts = ncm.substring(4, 6);
                    } else {
                        ncm1Parts = "-1";
                        ncm2Parts = "-1";
                        ncm3Parts = "-1";
                    }
                } else {
                    ncm1Parts = "-1";
                    ncm2Parts = "-1";
                    ncm3Parts = "-1";
                }
            } else {
                ncm1Parts = "-1";
                ncm2Parts = "-1";
                ncm3Parts = "-1";
            }                

            /*if (!"-1".equals(ncm1Parts) &&
                    (!"-1".equals(ncm2Parts)) &&
                    (!"-1".equals(ncm3Parts))) {
                sql = new StringBuilder();
                sql.append("select ncm1, ncm2, ncm3 ");
                sql.append("from ncm ");
                sql.append("where ncm1 = " + ncm1Parts + " ");
                sql.append("and ncm2 = " + ncm2Parts + " ");
                sql.append("and ncm3 = " + ncm3Parts);
                rst = stm.executeQuery(sql.toString());
                if (rst.next()) {
                   oNcm = new NcmVO();
                   oNcm.ncm1 = rst.getInt("ncm1");
                   oNcm.ncm2 = rst.getInt("ncm2");
                   oNcm.ncm3 = rst.getInt("ncm3");
                } else {
                   oNcm = new NcmVO();
                   oNcm.ncm1 = 402;
                   oNcm.ncm2 = 99;
                   oNcm.ncm3 = 0;                
                }
            } else {
                oNcm = new NcmVO();
                oNcm.ncm1 = 402;
                oNcm.ncm2 = 99;
                oNcm.ncm3 = 0;
            }*/
            
            StringBuilder camposBuscaSelect = new StringBuilder(); 

            if (!ncm1Parts.equals("-1")) {
                camposBuscaSelect.append("n1.ncm1Cadastrado, ");
            }

            if (!ncm2Parts.equals("-1")) {
                camposBuscaSelect.append("n2.ncm2Cadastrado, ");
            }

            if (!ncm3Parts.equals("-1")) {
                camposBuscaSelect.append("n3.ncm3Cadastrado");
            }
            
            if (!ncm1Parts.equals("-1")) {
                
                sql = new StringBuilder();
                
                sql.append("SELECT " + camposBuscaSelect.toString() + "  FROM ");
                
                if (!ncm1Parts.equals("-1")) {
                    sql.append(" (SELECT (CASE WHEN count(1) > 0 THEN true ELSE false END) AS ncm1Cadastrado FROM ncm WHERE ncm1=" + ncm1Parts + " AND nivel=1) as n1,");
                }
                if (!ncm2Parts.equals("-1")) {
                    sql.append(" (SELECT (CASE WHEN count(1) > 0 THEN true ELSE false END) AS ncm2Cadastrado FROM ncm WHERE ncm1=" + ncm1Parts + " AND ncm2=" + ncm2Parts + " AND nivel=2) AS n2,");
                }
                if (!ncm3Parts.equals("-1")) {
                    sql.append(" (SELECT (CASE WHEN count(1) > 0 THEN true ELSE false END) AS ncm3Cadastrado FROM ncm WHERE ncm1=" + ncm1Parts + " AND ncm2=" + ncm2Parts + " AND ncm3=" + ncm3Parts + " AND nivel=3) AS n3 ");
                }
                
                try {
                    rst = stm.executeQuery(sql.toString());
                } catch (Exception ex) {
                    ncm1Parts = "-1";
                }

                if (rst.next()) {

                    vNCMValido = new HashMap<>();

                    if (!ncm1Parts.equals("-1")) {
                        vNCMValido.put("ncm1", rst.getBoolean("ncm1Cadastrado"));
                    }
                    if (!ncm1Parts.equals("-1")) {
                        vNCMValido.put("ncm2", rst.getBoolean("ncm2Cadastrado"));
                    }
                    if (!ncm1Parts.equals("-1")) {
                        vNCMValido.put("ncm3", rst.getBoolean("ncm3Cadastrado"));
                    }
                }
            }
            if (vNCMValido != null) { 
                if (((vNCMValido.get("ncm1"))
                        &&(vNCMValido.get("ncm2"))
                        &&(vNCMValido.get("ncm3")))
                        ||(vNCMValido.get("ncm3"))) {
                   oNcm = new NcmVO();
                   oNcm.ncm1 = Integer.parseInt(ncm1Parts);
                   oNcm.ncm2 = Integer.parseInt(ncm2Parts);
                   oNcm.ncm3 = Integer.parseInt(ncm3Parts);
                }else{
                   // Caso o vNCMValido estiver Invalido preencher com o Padrão
                   oNcm = new NcmVO();
                   oNcm.ncm1 = 402;
                   oNcm.ncm2 = 99;
                   oNcm.ncm3 = 0;                
                }                
            }else{
               // Caso o vNCMValido estiver Invalido preencher com o Padrão
               oNcm = new NcmVO();
               oNcm.ncm1 = 402;
               oNcm.ncm2 = 99;
               oNcm.ncm3 = 0;                
             }

            Conexao.commit();
            return oNcm;

        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        } finally {
            Conexao.destruir(null, stm, rst);
        }
    }

    public NcmVO validar2(String ncm) throws Exception {
        
        ncm = Utils.formataNumero(ncm);
        
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        Map<String, Boolean> vNCMValido = null;
        NcmVO oNcm = null;

        String ncm1Parts = "";
        String ncm2Parts = "";
        String ncm3Parts = "";

        try {
            stm = Conexao.createStatement();
            Conexao.begin();

            if (ncm.trim().length() >= 6) {
                if (!"00000000".equals(ncm.trim())
                        && (!"11111111".equals(ncm.trim()))
                        && (!"22222222".equals(ncm.trim()))
                        && (!"33333333".equals(ncm.trim()))
                        && (!"44444444".equals(ncm.trim()))
                        && (!"55555555".equals(ncm.trim()))
                        && (!"66666666".equals(ncm.trim()))
                        && (!"77777777".equals(ncm.trim()))
                        && (!"88888888".equals(ncm.trim()))
                        && (!"99999999".equals(ncm.trim()))) {

                    if (ncm.length() >= 8) {
                        ncm1Parts = ncm.substring(0, 4);
                        ncm2Parts = ncm.substring(4, 6);
                        ncm3Parts = ncm.substring(6, 8);
                    } else if (ncm.length() == 7) {
                        ncm1Parts = ncm.substring(0, 3);
                        ncm2Parts = ncm.substring(3, 5);
                        ncm3Parts = ncm.substring(5, 7);
                    } else if (ncm.length() == 6) {
                        ncm1Parts = ncm.substring(0, 2);
                        ncm2Parts = ncm.substring(2, 4);
                        ncm3Parts = ncm.substring(4, 6);
                    } else {
                        ncm1Parts = "-1";
                        ncm2Parts = "-1";
                        ncm3Parts = "-1";
                    }
                } else {
                    ncm1Parts = "-1";
                    ncm2Parts = "-1";
                    ncm3Parts = "-1";
                }
            } else {
                ncm1Parts = "-1";
                ncm2Parts = "-1";
                ncm3Parts = "-1";
            }                

            StringBuilder camposBuscaSelect = new StringBuilder();

            if (!ncm1Parts.equals("-1")) {
                camposBuscaSelect.append("n1.ncm1Cadastrado, ");
            }

            if (!ncm2Parts.equals("-1")) {
                camposBuscaSelect.append("n2.ncm2Cadastrado, ");
            }

            if (!ncm3Parts.equals("-1")) {
                camposBuscaSelect.append("n3.ncm3Cadastrado");
            }
            
            if (!ncm1Parts.equals("-1")) {
                
                sql = new StringBuilder();
                
                sql.append("SELECT " + camposBuscaSelect.toString() + "  FROM ");
                
                if (!ncm1Parts.equals("-1")) {
                    sql.append(" (SELECT (CASE WHEN count(1) > 0 THEN true ELSE false END) AS ncm1Cadastrado FROM ncm WHERE ncm1=" + ncm1Parts + " AND nivel=1) as n1,");
                }
                if (!ncm2Parts.equals("-1")) {
                    sql.append(" (SELECT (CASE WHEN count(1) > 0 THEN true ELSE false END) AS ncm2Cadastrado FROM ncm WHERE ncm1=" + ncm1Parts + " AND ncm2=" + ncm2Parts + " AND nivel=2) AS n2,");
                }
                if (!ncm3Parts.equals("-1")) {
                    sql.append(" (SELECT (CASE WHEN count(1) > 0 THEN true ELSE false END) AS ncm3Cadastrado FROM ncm WHERE ncm1=" + ncm1Parts + " AND ncm2=" + ncm2Parts + " AND ncm3=" + ncm3Parts + " AND nivel=3) AS n3 ");
                }
                
                try {
                    rst = stm.executeQuery(sql.toString());
                } catch (Exception ex) {
                    ncm1Parts = "-1";
                }

                if (rst.next()) {

                    vNCMValido = new HashMap<>();

                    if (!ncm1Parts.equals("-1")) {
                        vNCMValido.put("ncm1", rst.getBoolean("ncm1Cadastrado"));
                    }
                    if (!ncm1Parts.equals("-1")) {
                        vNCMValido.put("ncm2", rst.getBoolean("ncm2Cadastrado"));
                    }
                    if (!ncm1Parts.equals("-1")) {
                        vNCMValido.put("ncm3", rst.getBoolean("ncm3Cadastrado"));
                    }
                }
            }
            if (vNCMValido != null) { 

                if (!vNCMValido.get("ncm1")) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO ncm (ncm1, ncm2, ncm3, nivel, descricao) VALUES (");
                    sql.append(Integer.parseInt(ncm1Parts) + ",");
                    sql.append("null,");
                    sql.append("null,");
                    sql.append("1,");
                    sql.append("'IMPORTADO VR');");
                    stm.execute(sql.toString());
                }
                
                if (!vNCMValido.get("ncm2")) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO ncm (ncm1, ncm2, ncm3, nivel, descricao) VALUES (");
                    sql.append(Integer.parseInt(ncm1Parts) + ",");
                    sql.append(Integer.parseInt(ncm2Parts) + ",");
                    sql.append("null,");
                    sql.append("2,");
                    sql.append("'IMPORTADO VR');");
                    stm.execute(sql.toString());
                }

                if (!vNCMValido.get("ncm3")) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO ncm (ncm1, ncm2, ncm3, nivel, descricao) VALUES (");
                    sql.append(Integer.parseInt(ncm1Parts) + ",");
                    sql.append(Integer.parseInt(ncm2Parts) + ",");
                    sql.append(Integer.parseInt(ncm3Parts) + ",");
                    sql.append("3,");
                    sql.append("'IMPORTADO VR');");
                    stm.execute(sql.toString());
                }

                oNcm = new NcmVO();

                oNcm.ncm1 = Integer.parseInt(ncm1Parts);
                oNcm.ncm2 = Integer.parseInt(ncm2Parts);
                oNcm.ncm3 = Integer.parseInt(ncm3Parts);
                oNcm.descricao = "IMPORTADO VR";
                
            }else{
               // Caso o vNCMValido estiver Invalido preencher com o Padrão
               oNcm = new NcmVO();
               oNcm.ncm1 = 402;
               oNcm.ncm2 = 99;
               oNcm.ncm3 = 0;                
             }

            Conexao.commit();
            return oNcm;

        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        } finally {
            Conexao.destruir(null, stm, rst);
        }
    }
    
    public NcmVO getPadrao() {
        NcmVO oNcm = new NcmVO();

        oNcm.ncm1 = 402;
        oNcm.ncm2 = 99;
        oNcm.ncm3 = 0;    

        return oNcm;
    }
    
    public List<NcmVO> carregar() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<NcmVO> vNcm = new ArrayList<>();
        
        try {
            stm = Conexao.createStatement();
            
            sql = new StringBuilder();
            sql.append("select ncm1, ncm2, ncm3 ");
            sql.append("from ncm ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                NcmVO oNcm = new NcmVO();
                oNcm.ncm1 = rst.getInt("ncm1");
                oNcm.ncm2 = rst.getInt("ncm2");
                oNcm.ncm3 = rst.getInt("ncm3");
                vNcm.add(oNcm);                
            }
          
            stm.close();
            return vNcm;
        } catch(Exception ex) {
            throw ex;
        } 
    }
    
    public void salvar(List<NcmVO> v_ncm) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        //File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\ImportarNCM\\insert_ncm.txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            
            ProgressBar.setStatus("Importando dados...Tabela de NCM...");
            ProgressBar.setMaximum(v_ncm.size());
            
            for (NcmVO i_ncm : v_ncm) {
                sql = new StringBuilder();
                
                if (i_ncm.nivel == 1) {                    
                    sql.append("select * from ncm where ncm1 = " + i_ncm.ncm1 + " and nivel = 1;");
                } else if (i_ncm.nivel == 2) {
                    sql.append("select * from ncm where ncm1 = " + i_ncm.ncm1 + " and ncm2 = " + i_ncm.ncm2 + " and nivel = 2;");
                } else if (i_ncm.nivel == 3) {
                    sql.append("select * from ncm where ncm1 = " + i_ncm.ncm1 + " and ncm2 = " + i_ncm.ncm2 + " and ncm3 = " + i_ncm.ncm3 + " and nivel = 3;");
                }
                
                rst = stm.executeQuery(sql.toString());
                
                if (!rst.next()) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO ncm (");
                    sql.append("ncm1, ncm2, ncm3, descricao, nivel) ");
                    sql.append("VALUES (");
                    sql.append(i_ncm.ncm1 + ", ");
                    sql.append((i_ncm.ncm2 == -1 ? null : i_ncm.ncm2) + ", ");
                    sql.append((i_ncm.ncm3 == -1 ? null : i_ncm.ncm3) + ", ");
                    sql.append("'" + i_ncm.descricao + "', ");
                    sql.append(i_ncm.nivel);
                    sql.append(");");
                    
                    //bw.write(sql.toString());
                    //bw.newLine();
                    stm.execute(sql.toString());
                }
                
                ProgressBar.next();
            }
            
            //bw.flush();
            //bw.close();
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void update(List<NcmVO> v_ncm) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            
            ProgressBar.setStatus("Importando dados...Tabela de NCM...");
            ProgressBar.setMaximum(v_ncm.size());
            
            for (NcmVO i_ncm : v_ncm) {
                sql = new StringBuilder();
                
                /*if (i_ncm.nivel == 1) {                    
                    sql.append("select * from ncm where ncm1 = " + i_ncm.ncm1 + " and nivel = 1;");
                } else if (i_ncm.nivel == 2) {
                    sql.append("select * from ncm where ncm1 = " + i_ncm.ncm1 + " and ncm2 = " + i_ncm.ncm2 + " and nivel = 2;");
                } else if (i_ncm.nivel == 3) {
                    sql.append("select * from ncm where ncm1 = " + i_ncm.ncm1 + " and ncm2 = " + i_ncm.ncm2 + " and ncm3 = " + i_ncm.ncm3 + " and nivel = 3;");
                }
                
                rst = stm.executeQuery(sql.toString());
                
                if (!rst.next()) {*/
                    sql = new StringBuilder();
                    sql.append("update ncm set descricao = '"+i_ncm.descricao+"' ");
                    sql.append("where descricao = '"+ i_ncm.strNcm + "' ");
                    sql.append("and nivel = 3;");
                    stm.execute(sql.toString());
                //}
                
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            throw ex;
        }
    }
    
}