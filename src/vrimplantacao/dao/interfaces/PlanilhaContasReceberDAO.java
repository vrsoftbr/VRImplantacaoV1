/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

/**
 *
 * @author lucasrafael
 */
public class PlanilhaContasReceberDAO {
    
    public void migrarContasReceber(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarContasReceber(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vReceberCreditoRotativo.size());

            ReceberCreditoRotativoDAO receberCreditoRotativoDAO = new ReceberCreditoRotativoDAO();
            receberCreditoRotativoDAO.salvar(vReceberCreditoRotativo, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarContasReceberCarnauba(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarContasReceberCarnauba(i_arquivo, i_idLojaDestino);

            ProgressBar.setMaximum(vReceberCreditoRotativo.size());

            ReceberCreditoRotativoDAO receberCreditoRotativoDAO = new ReceberCreditoRotativoDAO();
            receberCreditoRotativoDAO.salvarSysPdvComIdCnpjNome(vReceberCreditoRotativo, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void migrarContasReceberParana(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarContasReceberParana(i_arquivo);

            ProgressBar.setMaximum(vReceberCreditoRotativo.size());

            ReceberCreditoRotativoDAO receberCreditoRotativoDAO = new ReceberCreditoRotativoDAO();
            receberCreditoRotativoDAO.salvarSysPdvComIdCnpjNome(vReceberCreditoRotativo, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }    
    
    private List<ReceberCreditoRotativoVO> carregarContasReceber(String i_arquivo) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        Utils util = new Utils();
        String dataemissao, datavencimento;
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
                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if ("".equals(sheet.getCell(1, i).getContents())) { //ignora linha em branco
                            continue;
                        } else if ("Cliente".equals(sheet.getCell(0, i).getContents())) { //ignora linha em branco
                            continue;
                        }
                        
                        Cell cellCodCliente = sheet.getCell(0, i);                        
                        Cell cellEmissao = sheet.getCell(1, i);
                        Cell cellVencimento = sheet.getCell(2, i);                        
                        Cell cellNumeroCupom = sheet.getCell(3, i);
                        Cell cellValor = sheet.getCell(4, i);
                        
                        dataemissao = "20"+cellEmissao.getContents().substring(6, 8);
                        dataemissao = dataemissao +"/" + cellEmissao.getContents().substring(3, 5);
                        dataemissao = dataemissao +"/"+ cellEmissao.getContents().substring(0, 2);
                        
                        datavencimento = "20"+cellVencimento.getContents().substring(6, 8);
                        datavencimento = datavencimento +"/" + cellVencimento.getContents().substring(3, 5);
                        datavencimento = datavencimento +"/"+ cellVencimento.getContents().substring(0, 2);
                        
                        ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                        
                        oReceberCreditoRotativo.id_loja = 1;
                        oReceberCreditoRotativo.dataemissao = dataemissao;
                        oReceberCreditoRotativo.numerocupom = Integer.parseInt(util.formataNumero(cellNumeroCupom.getContents()));
                        oReceberCreditoRotativo.valor = Double.parseDouble(cellValor.getContents().replace(".", "").replace(",", "."));
                        oReceberCreditoRotativo.observacao = "IMPORTACAO VR";
                        oReceberCreditoRotativo.id_clientepreferencial = Integer.parseInt(
                                cellCodCliente.getContents().substring(0, 
                                        cellCodCliente.getContents().indexOf("-")));                        
                        oReceberCreditoRotativo.datavencimento = datavencimento;
                        oReceberCreditoRotativo.valorjuros = 0;
                        oReceberCreditoRotativo.parcela = 1;
                        
                        vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                    }
                }

                return vReceberCreditoRotativo;

            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }
    
    private List<ReceberCreditoRotativoVO> carregarContasReceberParana(String i_arquivo) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        Utils util = new Utils();
        String dataemissao, datavencimento;
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
                       String Coluna = sheet.getCell(0, i).getContents().trim();     
                       //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if ((!(sheet.getCell(0, i).getContents().isEmpty()))&&
                                   ("Total".equals(Coluna.substring(0, 5)))) {
                            continue;
                        }
                        
                        String input = sheet.getCell(4, i).getContents();                        
                        String output = input.substring(0, input.indexOf('-'));                        
                        String cellCodCliente = output.trim();
                        
                        Cell cellEmissao = sheet.getCell(1, i);
                        Cell cellVencimento = sheet.getCell(0, i);                        
                        Cell cellNumeroCupom = sheet.getCell(2, i);
                        Cell cellValor = sheet.getCell(3, i);
                        
                        dataemissao = cellEmissao.getContents();
                        datavencimento = cellVencimento.getContents();
                        
                        ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                        
                        oReceberCreditoRotativo.id_loja = 1;
                        oReceberCreditoRotativo.dataemissao = dataemissao;
                        oReceberCreditoRotativo.numerocupom = Integer.parseInt(cellNumeroCupom.getContents());
                        oReceberCreditoRotativo.valor = Double.parseDouble(cellValor.getContents().replace(".", "").replace(",", "."));
                        oReceberCreditoRotativo.observacao = "IMPORTACAO VR";
                        oReceberCreditoRotativo.id_clientepreferencial = Integer.parseInt(cellCodCliente.trim());
                        oReceberCreditoRotativo.datavencimento = datavencimento;
                        oReceberCreditoRotativo.valorjuros = 0;
                        oReceberCreditoRotativo.parcela = 1;
                        
                        vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                    }
                }

                return vReceberCreditoRotativo;

            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }    
    
    private List<ReceberCreditoRotativoVO> carregarContasReceberCarnauba(String i_arquivo, int idLoja) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        Utils util = new Utils();
        String dataEmissao, dataVencimento, observacao = "", nomeCliente = "", strNumeroCupom;
        int numeroCupom = 0;
        long cpfCnpj = -1;
        try {
            int linha = 0, ecf = 0;

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
                        
                        Cell cellNumeroCupom = sheet.getCell(0, i);
                        Cell cellCodCliente = sheet.getCell(1, i);                        
                        Cell cellEcf = sheet.getCell(2, i);
                        Cell cellDataEmissao = sheet.getCell(3, i);
                        Cell cellDataVencimento = sheet.getCell(4, i);                        
                        Cell cellValor = sheet.getCell(5, i);
                        Cell cellObservacao = sheet.getCell(6, i);
                        Cell cellCpfCliente = sheet.getCell(7, i);
                        Cell cellNomeCliente = sheet.getCell(8, i);
                        
                        dataEmissao = cellDataEmissao.getContents().substring(0, 10);
                        
                        dataEmissao = dataEmissao.substring(6, 10) + "/" + 
                                      dataEmissao.substring(3, 5) + "/" +
                                      dataEmissao.substring(0, 2);
                        
                        dataVencimento = cellDataVencimento.getContents().substring(0, 10);
                        dataVencimento = dataVencimento.substring(6, 10) + "/" + 
                                      dataVencimento.substring(3, 5) + "/" +
                                      dataVencimento.substring(0, 2);

                        
                        if ((cellObservacao.getContents() != null) &&
                                (!cellObservacao.getContents().trim().isEmpty()) &&
                                (!"NULL".equals(cellObservacao.getContents().trim()))) {
                            observacao = "IMPORTACAO VR => " + observacao;
                        } else {
                            observacao = "IMPORTACAO VR";
                        }
                        
                        if ((cellCpfCliente.getContents() != null) &&
                                (!cellCpfCliente.getContents().trim().isEmpty()) &&
                                (!"NULL".equals(cellCpfCliente.getContents().trim()))) {
                            cpfCnpj = Long.parseLong(cellCpfCliente.getContents().trim().substring(0, 
                                    cellCpfCliente.getContents().trim().length() -3));
                        } else {
                            cpfCnpj = -1;
                        }
                        
                        if ((cellNomeCliente.getContents() != null) &&
                                (!cellNomeCliente.getContents().trim().isEmpty()) &&
                                (!"NULL".equals(cellNomeCliente.getContents().trim()))) {
                            nomeCliente = util.acertarTexto(cellNomeCliente.getContents().trim().replace("'", ""));
                        } else {
                            nomeCliente = "";
                        }
                        
                        if ((cellEcf.getContents() != null) &&
                                (!cellEcf.getContents().trim().isEmpty()) &&
                                (!"NULL".equals(cellEcf.getContents().trim()))) {
                            ecf = Integer.parseInt(cellEcf.getContents().trim());
                        } else {
                            ecf = 0;
                        }
                        
                        if ((cellNumeroCupom.getContents() != null) &&
                                (!cellNumeroCupom.getContents().trim().isEmpty()) &&
                                (!"NULL".equals(cellNumeroCupom.getContents().trim()))) {
                            
                            if (cellNumeroCupom.getContents().contains("/")) {
                                numeroCupom = Integer.parseInt(util.formataNumero(cellNumeroCupom.getContents().trim()));
                            } else {
                                
                                if (cellNumeroCupom.getContents().length() <= 9) {
                                    numeroCupom = Integer.parseInt(cellNumeroCupom.getContents().trim());
                                } else {
                                    numeroCupom = 0;
                                    observacao = observacao + "CUPOM: "+cellNumeroCupom.getContents().trim();
                                }
                            }
                        }
                        
                        ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                        
                        oReceberCreditoRotativo.id_loja = idLoja;
                        oReceberCreditoRotativo.dataemissao = dataEmissao;
                        oReceberCreditoRotativo.ecf = ecf;
                        oReceberCreditoRotativo.numerocupom = numeroCupom;
                        oReceberCreditoRotativo.valor = Double.parseDouble(cellValor.getContents().trim().replace(",", "."));
                        oReceberCreditoRotativo.observacao = observacao;
                        oReceberCreditoRotativo.id_clientepreferencial = Integer.parseInt(cellCodCliente.getContents().trim());
                        oReceberCreditoRotativo.datavencimento = dataVencimento;
                        oReceberCreditoRotativo.valorjuros = 0;
                        oReceberCreditoRotativo.parcela = 1;
                        oReceberCreditoRotativo.cnpjCliente = cpfCnpj;
                        oReceberCreditoRotativo.nomeCliente = nomeCliente;
                        
                        vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                    }
                }

                return vReceberCreditoRotativo;

            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }
    
    public void migrarContasReceberRabelo(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarContasReceberRabelo(i_arquivo, i_idLojaDestino);

            ProgressBar.setMaximum(vReceberCreditoRotativo.size());

            ReceberCreditoRotativoDAO receberCreditoRotativoDAO = new ReceberCreditoRotativoDAO();
            receberCreditoRotativoDAO.salvarComCodicao(vReceberCreditoRotativo, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }
    
    private List<ReceberCreditoRotativoVO> carregarContasReceberRabelo(String i_arquivo, int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int linha = 0;

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            linha = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                linha++;
               String Coluna = sheet.getCell(0, i).getContents().trim();     
               //ignora o cabeçalho
                if (linha == 1) {
                    continue;
                } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                    continue;
                }                   

                Cell cellCodCliente = sheet.getCell(1, i);
                Cell cellEmissao = sheet.getCell(3, i);
                Cell cellVencimento = sheet.getCell(4, i);                        
                Cell cellNumeroCupom = sheet.getCell(11, i);
                Cell cellValor = sheet.getCell(7, i);

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                oReceberCreditoRotativo.setId_loja(idLojaVR);
                oReceberCreditoRotativo.setDataemissao(Utils.convertStringToDate("M/d/yy h:mm", cellEmissao.getContents()));
                oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(cellNumeroCupom.getContents()));
                oReceberCreditoRotativo.setValor(Utils.stringToDouble(cellValor.getContents(), 0));
                oReceberCreditoRotativo.setObservacao("IMPORTACAO VR");
                oReceberCreditoRotativo.setId_clientepreferencial(Integer.parseInt(cellCodCliente.getContents().trim()));
                oReceberCreditoRotativo.setDatavencimento(Utils.convertStringToDate("M/d/yy h:mm", cellVencimento.getContents()));
                oReceberCreditoRotativo.setValorjuros(0);
                oReceberCreditoRotativo.setParcela(1);

                vReceberCreditoRotativo.add(oReceberCreditoRotativo);
            }
        }

        return vReceberCreditoRotativo;
    }  
    
    private List<ReceberCreditoRotativoVO> carregarContasReceberMirandaCastilhoSP(String i_arquivo, int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        String dataEmissao, dataVencimento;
        int linha = 0;

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            linha = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                linha++;
                String Coluna = sheet.getCell(0, i).getContents().trim();     
               //ignora o cabeçalho
                if (linha == 1) {
                    continue;
                } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                    continue;
                } else if (sheet.getCell(0, i).getContents().contains("Cliente")) {
                    continue;
                } else if (sheet.getCell(0, i).getContents().contains("Total")) {
                    continue;
                }

                Cell cellCodCliente = sheet.getCell(0, i);
                Cell cellNumeroCupom = sheet.getCell(3, i);
                Cell cellDataEmissao = sheet.getCell(6, i);
                Cell cellDataVencimento = sheet.getCell(8, i);                
                Cell cellValor = sheet.getCell(9, i);

                dataEmissao = cellDataEmissao.getContents().substring(0, 10);

                dataEmissao = dataEmissao.substring(6, 10) + "/"
                        + dataEmissao.substring(3, 5) + "/"
                        + dataEmissao.substring(0, 2);

                dataVencimento = cellDataVencimento.getContents().substring(0, 10);
                dataVencimento = dataVencimento.substring(6, 10) + "/"
                        + dataVencimento.substring(3, 5) + "/"
                        + dataVencimento.substring(0, 2);
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                oReceberCreditoRotativo.setId_loja(idLojaVR);
                oReceberCreditoRotativo.setDataemissao(dataEmissao);
                oReceberCreditoRotativo.setNumerocupom(Utils.stringToInt(cellNumeroCupom.getContents()));
                oReceberCreditoRotativo.setValor(Utils.stringToDouble(cellValor.getContents(), 0));
                oReceberCreditoRotativo.setObservacao("IMPORTACAO VR");
                oReceberCreditoRotativo.setId_clientepreferencial(Integer.parseInt(cellCodCliente.getContents().trim()));
                oReceberCreditoRotativo.setDatavencimento(dataVencimento);
                oReceberCreditoRotativo.setValorjuros(0);
                oReceberCreditoRotativo.setParcela(1);

                vReceberCreditoRotativo.add(oReceberCreditoRotativo);
            }
        }

        return vReceberCreditoRotativo;
    }  

    public void migrarContasReceberMirandaCastilhoSP(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarContasReceberMirandaCastilhoSP(i_arquivo, i_idLojaDestino);

            ProgressBar.setMaximum(vReceberCreditoRotativo.size());

            ReceberCreditoRotativoDAO receberCreditoRotativoDAO = new ReceberCreditoRotativoDAO();
            receberCreditoRotativoDAO.salvarComCodicao(vReceberCreditoRotativo, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }
    
}
