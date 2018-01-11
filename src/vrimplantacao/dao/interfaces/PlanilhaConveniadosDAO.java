/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.ConveniadoDAO;
import vrimplantacao.dao.cadastro.ConveniadoTransacaoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ConveniadoServicoVO;
import vrimplantacao.vo.vrimplantacao.ConveniadoTransacaoVO;
import vrimplantacao.vo.vrimplantacao.ConveniadoVO;

/**
 *
 * @author lucasrafael
 */
public class PlanilhaConveniadosDAO {
    
    public void importarConveniados(String i_arquivo, int i_idLojaDestino, int i_idEmpresa) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ConveniadoVO> vConveniados = carregarConveniados(i_arquivo);

            ProgressBar.setMaximum(vConveniados.size());

            ConveniadoDAO conveniadoDAO = new ConveniadoDAO();
            conveniadoDAO.salvar(vConveniados, i_idLojaDestino, i_idEmpresa);
            
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void importarConveniadoTransacao(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ConveniadoTransacaoVO> vConveniadoTransacao = carregarConveniadoTransacao(i_arquivo);

            ProgressBar.setMaximum(vConveniadoTransacao.size());

            ConveniadoTransacaoDAO conveniadoTransacaoDAO = new ConveniadoTransacaoDAO();
            conveniadoTransacaoDAO.salvar(vConveniadoTransacao, i_idLojaDestino);
            
        } catch (Exception e) {
            throw e;
        }
    }    
    private List<ConveniadoTransacaoVO> carregarConveniadoTransacao(String i_arquivo) throws Exception {
        List<ConveniadoTransacaoVO> vConveniadoTransacao = new ArrayList<>();

        try {
            int linha = 0;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            
            Sheet[] sheets = arquivo.getSheets();
            
            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        //} else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                        //    continue;
                        }
                        
                        Cell cellConveniado = sheet.getCell(0,i);
                        Cell cellValor = sheet.getCell(1,i);
                        
                        ConveniadoTransacaoVO oTransacao = new ConveniadoTransacaoVO();
                        oTransacao.id_conveniado = Integer.parseInt(cellConveniado.getContents().substring(7, 
                                cellConveniado.getContents().indexOf("-")).trim());
                        oTransacao.valor = Double.parseDouble(cellValor.getContents().replace(".", "").replace(",", "."));
                        oTransacao.observacao = "IMPORTADO VR";
                        
                        vConveniadoTransacao.add(oTransacao);
                            
                    }
                }

                return vConveniadoTransacao;

            } catch (Exception ex) {
                throw ex;
                /*if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }*/
            }

        } catch (Exception e) {
            throw e;
        }
    }
    
    private List<ConveniadoVO> carregarConveniados(String i_arquivo) throws Exception {
        List<ConveniadoVO> vConveniados = new ArrayList<>();
        Utils util = new Utils();
        String nome;
        int idTipoInscricao;
        long cnpj;
        
        try {
            int linha = 0;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            
            Sheet[] sheets = arquivo.getSheets();
            
            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } //else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                        //    continue;
                        //}
                        
                        Cell cellNome = sheet.getCell(0, i);
                        Cell cellCnpj = sheet.getCell(1, i);
                        Cell cellValorLimite = sheet.getCell(2, i);

                        
                            if ((cellNome.getContents() != null)
                                    && (!cellNome.getContents().trim().isEmpty())) {
                                nome = Utils.acertarTexto(cellNome.getContents().trim().replace("'", ""));
                            } else {
                                nome = "";
                            }

                            cnpj = Long.parseLong(cellCnpj.getContents());

                            idTipoInscricao = 1;
                        
                            if (nome.length() > 40) {
                                nome = nome.substring(0, 40);
                            }

                            
                            ConveniadoVO oConveniado = new ConveniadoVO();
                            oConveniado.id = 0;
                            oConveniado.nome = nome;
                            oConveniado.id_tipoinscricao = idTipoInscricao;
                            oConveniado.cnpj = cnpj;
                            oConveniado.observacao = "IMPORTACAO VR";
                            
                            ConveniadoServicoVO oConveniadoServico = new ConveniadoServicoVO();
                            oConveniadoServico.valor = Double.parseDouble(cellValorLimite.getContents().replace(",", ".").trim());
                            oConveniadoServico.id_tiposervicoconvenio = 1;
                            oConveniado.vConveniadoServico.add(oConveniadoServico);
                            
                            vConveniados.add(oConveniado);
                            

                    }
                }

                return vConveniados;

            } catch (Exception ex) {
                throw ex;
                /*if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }*/
            }

        } catch (Exception e) {
            throw e;
        }
    }
}
