/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVencimentoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

/**
 *
 * @author lucasrafael
 */
public class ArquivoPadraoDAO {
    
    public void migrarPreco(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ProdutoVO> vProduto = carregarPreco(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setStatus("Comparando produtos Arquivo Origem/Loja Destino...");
            ProgressBar.setMaximum(vProduto.size());

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.acertarPrecoCustoForteMix(vProduto);
            
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarCustoLojaoCabelereiro(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ProdutoVO> vProduto = carregarCustoLojaoCabelereiro(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setStatus("Comparando produtos Arquivo Origem/Loja Destino...");
            ProgressBar.setMaximum(vProduto.size());

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarCustoLojaoCabelereiro(vProduto, i_idLojaDestino);
            
        } catch (Exception e) {
            throw e;
        }
    }
    
    
    public void migrarEstoque(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ProdutoVO> vProduto = carregarEstoque(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setStatus("Comparando produtos Arquivo Origem/Loja Destino...");
            ProgressBar.setMaximum(vProduto.size());

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.acertarEstoqueForteMix(vProduto);
            
        } catch (Exception e) {
            throw e;
        }
    }
    
    private List<ProdutoVO> carregarPreco(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        Utils util = new Utils();
        double precovenda, custoComImposto, custoSemImposto;        
        int idProduto, idLoja, i;
        long codigoBarras;
        String linha = "";
        String[] campos;
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(i_arquivo), "UTF-8"));
            
            i = 0;
            
            while ((linha = br.readLine()) != null) {
                
                if (i > 0) {
                
                    campos = linha.split("\t", -1);

                    idProduto = Integer.parseInt(campos[1]);
                    idLoja = 2; /*Integer.parseInt(campos[4]);*/
                    
                    if ((campos[6] != null) &&
                            (!campos[6].trim().isEmpty())) {
                        precovenda = Double.parseDouble(campos[6].replace(",", "."));
                    } else {
                        precovenda = 0;
                    }
                    

                    //custoComImposto = Double.parseDouble(campos[1].replace(",", "."));
                    //custoSemImposto = Double.parseDouble(campos[2].replace(",", "."));
                    
                    if (!"".equals(campos[1])
                            && (campos[1] != null)) {

                        //codigoBarras = Long.parseLong(campos[3]);

                        ProdutoVO oProduto = new ProdutoVO();

                        oProduto.id = idProduto;
                        //oProduto.codigoBarras = codigoBarras;

                        ProdutoComplementoVO oProdutoComplemento = new ProdutoComplementoVO();

                        oProdutoComplemento.precoVenda = precovenda;
                        oProdutoComplemento.precoDiaSeguinte = precovenda;
                        //oProdutoComplemento.custoComImposto = custoComImposto;
                        //oProdutoComplemento.custoSemImposto = custoSemImposto;
                        oProdutoComplemento.idLoja = idLoja;

                        oProduto.vComplemento.add(oProdutoComplemento);

                        vProduto.add(oProduto);
                    }              
                }
                i = i + 1;
            }

            //bw.flush();
            //bw.close();
            
            return vProduto;       

        } catch (Exception e) {
            throw e;
        }
    }

    private List<ProdutoVO> carregarCustoLojaoCabelereiro(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        double custoComImposto, custoSemImposto;        
        int idProduto, i;
        String linha = "";
        String[] campos;
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(i_arquivo), "UTF-8"));
            
            i = 0;
            
            while ((linha = br.readLine()) != null) {
                
                if (i > 0) {
                
                    campos = linha.split("\t", -1);

                    idProduto = Integer.parseInt(campos[0]);
                    
                    if ((campos[1] != null) &&
                            (!campos[1].trim().isEmpty())) {
                        custoComImposto = Double.parseDouble(campos[1].replace(".", "").replace(",", "."));
                        custoSemImposto = Double.parseDouble(campos[1].replace(".", "").replace(",", "."));
                    } else {
                        custoComImposto = 0;
                        custoSemImposto = 0;
                    }
                    

                    ProdutoVO oProduto = new ProdutoVO();

                    oProduto.id = idProduto;

                    ProdutoComplementoVO oProdutoComplemento = new ProdutoComplementoVO();

                    oProdutoComplemento.custoComImposto = custoComImposto;
                    oProdutoComplemento.custoSemImposto = custoSemImposto;

                    oProduto.vComplemento.add(oProdutoComplemento);

                    vProduto.add(oProduto);
                }              
                            
                i = i + 1;
            
            }
            
            return vProduto;       

        } catch (Exception e) {
            throw e;
        }
    }
    
    private List<ProdutoVO> carregarEstoque(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        Utils util = new Utils();
        double estoque, custo, estoqueMin;        
        int idProduto, idLoja, i;
        long codigoBarras;
        String linha = "";
        String[] campos;
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(i_arquivo), "UTF-8"));
            
            i = 0;
            
            while ((linha = br.readLine()) != null) {
                
                if (i > 0) {
                
                    campos = linha.split("\t", -1);

                    idLoja = Integer.parseInt(campos[1]);
                    idProduto = Integer.parseInt(campos[0]);
                    
                    if ((campos[26] != null) &&
                            (!campos[26].trim().isEmpty())) {
                        estoque = Double.parseDouble(campos[26].replace(",", "."));
                    } else {
                        estoque = 0;
                    }
                    
                    if ((campos[10] != null) &&
                            (!campos[10].trim().isEmpty())) {
                        custo = Double.parseDouble(campos[10].replace(",", "."));
                    } else {
                        custo = 0;
                    }
                    
                    //if ((campos[29] != null) &&
                    //        (!campos[29].trim().isEmpty())) {
                    //    estoqueMin = Double.parseDouble(campos[29].replace(",", "."));
                    //} else {
                    //    estoqueMin = 0;
                    //}
                    
                    if (!"".equals(campos[0])
                            && (campos[0] != null)) {

                        //codigoBarras = Long.parseLong(campos[3]);

                        ProdutoVO oProduto = new ProdutoVO();

                        oProduto.id = idProduto;
                        //oProduto.codigoBarras = codigoBarras;

                        ProdutoComplementoVO oProdutoComplemento = new ProdutoComplementoVO();

                        oProdutoComplemento.estoque = estoque;
                        oProdutoComplemento.custoComImposto = custo;
                        oProdutoComplemento.custoSemImposto = custo;
                        //oProdutoComplemento.estoqueMinimo = estoqueMin;
                        oProdutoComplemento.idLoja = idLoja;

                        oProduto.vComplemento.add(oProdutoComplemento);

                        vProduto.add(oProduto);
                    }              
                }
                i = i + 1;
            }

            return vProduto;                

        } catch (Exception e) {
            throw e;
        }
    }
}
