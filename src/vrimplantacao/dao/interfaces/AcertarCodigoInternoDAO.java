/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.io.File;
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
import vrframework.classe.VRException;
import vrimplantacao.dao.cadastro.CodigoAnteriorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

public class AcertarCodigoInternoDAO {

    public void importarAcertarProduto0() throws Exception {
        try {
            ProgressBar.setStatus("Carregando Produtos com id_produto2 = 0...");
            List<ProdutoVO> vProduto = carregarProdutoTrocarId();
            
            CodigoAnteriorDAO codigoAnterior = new CodigoAnteriorDAO();
            codigoAnterior.alterarIdProduto(vProduto);
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarProdutosFreitas(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para importação...");
            List<CodigoAnteriorVO> vCodigoAnterior = carregarProdutosFreitas(i_arquivo);
            
            CodigoAnteriorDAO codigoAnterior = new CodigoAnteriorDAO();
            codigoAnterior.salvarCodigoFreitas(vCodigoAnterior);
        } catch(Exception ex) {
            throw ex;
        }
    }
     
    public List<CodigoAnteriorVO> carregarProdutosFreitas(String i_arquivo) throws Exception {
        List<CodigoAnteriorVO> vProdutoCodigoAnterior = new ArrayList<>();
        long codigoBarras = 0;
        int linha = 0, codigoRms = 0;
        String descricao, strCodigoRms = "";
        Utils util = new Utils();
        
        try {
            
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            
            Sheet[] sheets = arquivo.getSheets();
            
            try {
                
                for (int sh = 0; sh < sheets.length; sh++) {
                    
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;
                    
                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha ++;
                        
                        if (linha == 1) {
                            continue;
                        }
                        
                        Cell cellCodigoItem = sheet.getCell(1, i);
                        Cell cellDigito = sheet.getCell(2, i);
                        Cell cellCodigoBarra = sheet.getCell(3, i);
                        Cell cellDescricao = sheet.getCell(4, i);
                        
                        strCodigoRms = cellCodigoItem.getContents().trim() + cellDigito.getContents().trim();
                        codigoRms = Integer.parseInt(strCodigoRms);
                                                
                        codigoBarras = Long.parseLong(cellCodigoBarra.getContents().substring(0, 
                                cellCodigoBarra.getContents().trim().length() -3));
                        
                        descricao = util.acertarTexto(cellDescricao.getContents().trim().replace("'", ""));
                        
                        System.out.println(linha+" = "+codigoBarras+" "+descricao+";");
                        
                        if (descricao.length() > 60) {
                            descricao = descricao.substring(0, 60);
                        }
                        
                        CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                        
                        oAnterior.codigo_rms = codigoRms;
                        oAnterior.codigobarras_rms = codigoBarras;
                        oAnterior.descricao_rms = descricao;
                        
                        
                        vProdutoCodigoAnterior.add(oAnterior);
                    }
                }
                
                return vProdutoCodigoAnterior;
                
            } catch(Exception ex) {
                
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }                
            }
            
        } catch(Exception e) {
            throw e;
        }
    }
    
    private List<ProdutoVO> carregarProdutoTrocarId() throws Exception {
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto, idTipoEmbalagem;
        boolean pesavel;
        
        try {
            
            Conexao.begin();
        
            stm = Conexao.createStatement();
            
            sql = new StringBuilder();
            sql.append("select id, id_produto2, id_tipoembalagem, pesavel from produto ");
            sql.append("where id_produto2 = 0 ");
            sql.append("or id_produto2 is null ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
            
                idProduto = rst.getInt("id");
                idTipoEmbalagem = rst.getInt("id_tipoembalagem");
                pesavel = rst.getBoolean("pesavel");
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.pesavel = pesavel;
                
                vProdutoOrigem.add(oProduto);
                
            }
            
            Conexao.commit();
            return vProdutoOrigem;
        } catch(Exception ex) {
            throw ex;
        }
        
    }    
}