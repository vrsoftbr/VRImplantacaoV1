/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.LogEstoqueDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.vo.vrimplantacao.LogEstoqueVO;

/**
 *
 * @author lucasrafael
 */
public class PlanilhaLogEstoqueDAO {
    
    public void verificarSemLogEstoqueCodigoAtual(String i_arquivo, int i_idLoja) throws Exception {
        List<LogEstoqueVO> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        int linha;
        long codigoProduto;
        double estoque;
        String strQtd = "";
        Sheet[] sheets = arquivo.getSheets();
        File f = new File("C:\\vr\\Implantacao\\arquivos_importacao\\140317\\estoque_diferente_lojaSemLogEstoqueCodigoAtual"+i_idLoja+".txt");
        //File f = new File("C:\\svn\\estoque_diferente_loja"+i_idLoja+".txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        
        try {
            
            stm = Conexao.createStatement();
            int contador = 1;
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    //ignora o cabeçalho
                    if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    } else if (linha == 1) {
                        continue;
                    }
                    
                    Cell cellIdProduto = sheet.getCell(0, i);
                    Cell cellEstoque = sheet.getCell(1, i);
                    
                    if (linha == 108) {
                        System.out.print("aqui");
                    }
                    
                    codigoProduto = Long.parseLong(cellIdProduto.getContents().trim());
                    
                    strQtd = "";
                    
                    if (cellEstoque.getContents().trim().length() == 9) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 1) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else if (cellEstoque.getContents().trim().length() == 10) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 2) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else if (cellEstoque.getContents().trim().length() == 11) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 3) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else {
                        estoque = Double.parseDouble(cellEstoque.getContents().trim());
                    }
                    
                    sql = new StringBuilder();
                    sql.append("select p.id from produto p "
                            + "inner join implantacao.codigoanterior ant on ant.codigoatual = p.id "
                            + "where ant.codigoanterior = " + codigoProduto);
                    rst = stm.executeQuery(sql.toString());
                    
                    if (rst.next()) {
                        bw.write(rst.getInt("id") + ";" + estoque + ";");
                        bw.newLine();
                    }
                    
                    ProgressBar.setStatus("Carregando dados..."+contador);
                    contador++;
                    //LogEstoqueVO vo = new LogEstoqueVO();
                    //vo.setId_loja(i_idLoja);
                    //vo.setId_produto(Integer.parseInt(cellIdProduto.getContents()));
                    //vo.setQuantidade(Double.parseDouble(cellQuantidade.getContents()));
                    //vResult.add(vo);
                }
            }
            bw.flush();
            bw.close();
            stm.close();
            //return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void verificarSemLogEstoque(String i_arquivo, int i_idLoja) throws Exception {
        List<LogEstoqueVO> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        int linha;
        long codigoProduto;
        double estoque;
        String strQtd = "";
        Sheet[] sheets = arquivo.getSheets();
        File f = new File("C:\\vr\\Implantacao\\arquivos_importacao\\140317\\estoque_diferente_lojaSemLogEstoque"+i_idLoja+".txt");
        //File f = new File("C:\\svn\\estoque_diferente_loja"+i_idLoja+".txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        
        try {
            
            stm = Conexao.createStatement();
            int contador = 1;
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    //ignora o cabeçalho
                    if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    } else if (linha == 1) {
                        continue;
                    }
                    
                    Cell cellIdProduto = sheet.getCell(1, i);
                    Cell cellEstoque = sheet.getCell(2, i);
                    
                    if (linha == 108) {
                        System.out.print("aqui");
                    }
                    
                    codigoProduto = Long.parseLong(cellIdProduto.getContents().trim());
                    
                    strQtd = "";
                    
                    if (cellEstoque.getContents().trim().length() == 9) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 1) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else if (cellEstoque.getContents().trim().length() == 10) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 2) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else if (cellEstoque.getContents().trim().length() == 11) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 3) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else {
                        estoque = Double.parseDouble(cellEstoque.getContents().trim());
                    }
                    
                    sql = new StringBuilder();
                    sql.append("select id from logestoque ");
                    sql.append("where id_produto = " + new ProdutoDAO().getIdProdutoCodigoAnterior(codigoProduto)+" ");
                    sql.append("and id_loja = " + i_idLoja);
                    rst = stm.executeQuery(sql.toString());
                    
                    if (!rst.next()) {
                        bw.write(codigoProduto + ";" + estoque + ";");
                        bw.newLine();
                    }
                    
                    ProgressBar.setStatus("Carregando dados..."+contador);
                    contador++;
                    //LogEstoqueVO vo = new LogEstoqueVO();
                    //vo.setId_loja(i_idLoja);
                    //vo.setId_produto(Integer.parseInt(cellIdProduto.getContents()));
                    //vo.setQuantidade(Double.parseDouble(cellQuantidade.getContents()));
                    //vResult.add(vo);
                }
            }
            bw.flush();
            bw.close();
            stm.close();
            //return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void verificarLogEstoque(String i_arquivo, int i_idLoja) throws Exception {
        List<LogEstoqueVO> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        int linha;
        long codigoProduto;
        double estoque;
        String strQtd = "";
        Sheet[] sheets = arquivo.getSheets();
        File f = new File("C:\\vr\\Implantacao\\arquivos_importacao\\140317\\estoque_diferente_loja"+i_idLoja+".txt");
        //File f = new File("C:\\svn\\estoque_diferente_loja"+i_idLoja+".txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        
        try {
            
            stm = Conexao.createStatement();
            int contador = 1;
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    //ignora o cabeçalho
                    if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    } else if (linha == 1) {
                        continue;
                    }
                    
                    Cell cellIdProduto = sheet.getCell(1, i);
                    Cell cellEstoque = sheet.getCell(2, i);
                    
                    if (linha == 108) {
                        System.out.print("aqui");
                    }
                    
                    codigoProduto = Long.parseLong(cellIdProduto.getContents().trim());
                    
                    strQtd = "";
                    
                    if (cellEstoque.getContents().trim().length() == 9) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 1) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else if (cellEstoque.getContents().trim().length() == 10) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 2) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else if (cellEstoque.getContents().trim().length() == 11) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 3) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else {
                        estoque = Double.parseDouble(cellEstoque.getContents().trim());
                    }
                    
                    
                    sql = new StringBuilder();
                    sql.append("select min(id) id from logestoque ");
                    sql.append("where id_produto = " + new ProdutoDAO().getIdProdutoCodigoAnterior(codigoProduto)+" ");
                    sql.append("and id_usuario = 0 ");
                    sql.append("and datamovimento >= '2017-03-14' ");
                    sql.append("and id_loja = " + i_idLoja);
                    rst = stm.executeQuery(sql.toString());
                    
                    
                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("select estoqueanterior, id_produto from logestoque where id = "+rst.getInt("id") + " and id_loja = " + i_idLoja);
                        rst2 = stm.executeQuery(sql.toString());
                        
                        if (rst2.next()) {
                            
                            if (rst2.getDouble("estoqueanterior") != estoque) {
                                bw.write(rst2.getInt("id_produto") + ";" + estoque + ";");
                                bw.newLine();
                            }
                        }
                    }
                    
                    ProgressBar.setStatus("Carregando dados..."+contador);
                    contador++;
                    //LogEstoqueVO vo = new LogEstoqueVO();
                    //vo.setId_loja(i_idLoja);
                    //vo.setId_produto(Integer.parseInt(cellIdProduto.getContents()));
                    //vo.setQuantidade(Double.parseDouble(cellQuantidade.getContents()));
                    //vResult.add(vo);
                }
            }
            bw.flush();
            bw.close();
            stm.close();
            //return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<LogEstoqueVO> carregarLogEstoque(String i_arquivo, int i_idLoja) throws Exception {
        List<LogEstoqueVO> vlogEstoque = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        int linha = 0, idProduto = 0;
        double estoque = 0;
        String strQtd = "";
        Sheet[] sheets = arquivo.getSheets();
        
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    //ignora o cabeçalho
                    if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    }
                    
                    strQtd = "";
                    Cell cellIdProduto  = sheet.getCell(0, i);
                    Cell cellEstoque    = sheet.getCell(1, i);
                    idProduto   = Integer.parseInt(cellIdProduto.getContents().trim());

                    if (cellEstoque.getContents().trim().length() == 9) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 1) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else if (cellEstoque.getContents().trim().length() == 10) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 2) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else if (cellEstoque.getContents().trim().length() == 11) {
                        for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                            if (j == 3) {
                                strQtd = strQtd + "";
                            } else {
                                strQtd = strQtd + cellEstoque.getContents().charAt(j);
                            }
                        }
                        estoque = Double.parseDouble(strQtd);
                    } else {
                        estoque = Double.parseDouble(cellEstoque.getContents().trim());
                    }

                    LogEstoqueVO oLogEstoque = new LogEstoqueVO();
                    oLogEstoque.setId_produto(idProduto);
                    oLogEstoque.setEstoqueanterior(estoque);
                    oLogEstoque.setId_loja(i_idLoja);
                    vlogEstoque.add(oLogEstoque);
                }
            }
           
            return vlogEstoque;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarLogEstoque(String i_arquivo, int i_idLoja) throws Exception {
        List<LogEstoqueVO> v_logEstoque = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...LogEstoque...");
            v_logEstoque = carregarLogEstoque(i_arquivo, i_idLoja);
            
            if (!v_logEstoque.isEmpty()) {
                LogEstoqueDAO logEstoqueDAO = new LogEstoqueDAO();
                logEstoqueDAO.salvar(v_logEstoque);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
}