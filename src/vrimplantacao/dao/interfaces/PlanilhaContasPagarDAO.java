package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.PagarOutrasDespesasDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVencimentoVO;

public class PlanilhaContasPagarDAO {

    public void migrarContasPagar(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");
            List<PagarOutrasDespesasVO> vPagarOutrasDespesas = carregarContasPagar(i_arquivo, i_idLojaDestino);
            ProgressBar.setMaximum(vPagarOutrasDespesas.size());
            PagarOutrasDespesasDAO pagarOutrasDespesasDAO = new PagarOutrasDespesasDAO();
            pagarOutrasDespesasDAO.salvar2(vPagarOutrasDespesas);
        } catch (Exception e) {
            throw e;
        }
    }

    private List<PagarOutrasDespesasVO> carregarContasPagar(String i_arquivo, int i_idLojaDestino) throws Exception {
        List<PagarOutrasDespesasVO> vPagarOutrasDespesas = new ArrayList<>();
        String dataemissao, datavencimento;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dataVencimento;
        try {
            int linha = 0;
            String nome = "";
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();
            try {
                int contador = 1;
                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            nome = sheet.getCell(0, i).getContents();
                            continue;
                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if (nome.equals(sheet.getCell(0, i).getContents())) { //ignora linha em branco
                            continue;
                        }

                        Cell cellCnpj = sheet.getCell(0, i);
                        Cell cellCodFornecedor = sheet.getCell(1, i);
                        Cell cellNota = sheet.getCell(3, i);
                        Cell cellValor = sheet.getCell(7, i);
                        Cell cellVencimento = sheet.getCell(5, i);
                        Cell cellEmissao = sheet.getCell(4, i);
                        Cell cellValorTotal = sheet.getCell(6, i);
                        Cell cellParcela = sheet.getCell(8, i);
                        Cell cellDataBaixa = sheet.getCell(9, i);

                        PagarOutrasDespesasVO oPagarOutrasDespesas = new PagarOutrasDespesasVO();
                        dataemissao = cellEmissao.getContents().substring(6, 10);
                        dataemissao = dataemissao + "/" + cellEmissao.getContents().substring(3, 5);
                        dataemissao = dataemissao + "/" + cellEmissao.getContents().substring(0, 2);
                        
                        //dataVencimento = new java.sql.Date (dateFormat.parse(cellVencimento.getContents().substring(0, 10)).getTime());
                        
                        datavencimento = cellVencimento.getContents().substring(6, 10);
                        datavencimento = datavencimento + "/" + cellVencimento.getContents().substring(3, 5);
                        datavencimento = datavencimento + "/" + cellVencimento.getContents().substring(0, 2);
                        //datavencimento = cellVencimento.getContents().substring(0, 10);
                        
                        //dataVencimento = new java.sql.Date(dateFormat.parse(datavencimento).getTime());
                        
                        //datavencimento = cellVencimento.getContents().substring(0, 10);

                        //if ((cellDataBaixa.getContents() == null)
                        //        || (cellDataBaixa.getContents().trim().isEmpty())
                        //        || ("NULL".equals(cellDataBaixa.getContents().trim()))) {
                        int idFornecedor = new FornecedorDAO().getId(Long.parseLong(Utils.formataNumero((cellCnpj.getContents().trim()))));

                        if (idFornecedor != -1) {

                            oPagarOutrasDespesas.id_fornecedor = idFornecedor;

                            if (cellNota.getContents().length() > 9) {
                                oPagarOutrasDespesas.numerodocumento = Integer.parseInt(cellNota.getContents().substring(0, 9));
                                oPagarOutrasDespesas.observacao = oPagarOutrasDespesas.observacao + "NUMERO DOCUMENTO: " + cellNota.getContents();
                            } else {
                                oPagarOutrasDespesas.numerodocumento = Integer.parseInt(cellNota.getContents());
                            }

                            if ((cellDataBaixa.getContents() != null)
                                    && (!cellDataBaixa.getContents().trim().isEmpty())) {
                                oPagarOutrasDespesas.id_situacaopagaroutrasdespesas = 1;
                                oPagarOutrasDespesas.observacao = Utils.acertarTexto("IMPORTACAO VR => " + "DATA BAIXA: " + cellDataBaixa.getContents()
                                        + " - PARCELA: " + cellParcela.getContents() + " - VALOR TOTAL: " + cellValorTotal.getContents() + ".");
                            } else {
                                oPagarOutrasDespesas.id_situacaopagaroutrasdespesas = 0;
                                oPagarOutrasDespesas.observacao = Utils.acertarTexto("IMPORTACAO VR => " + " PARCELA: " + cellParcela.getContents() + " - "
                                        + "VALOR TOTAL: " + cellValorTotal.getContents());
                            }

                            //oPagarOutrasDespesas.id_situacaopagaroutrasdespesas = 0;
                            //oPagarOutrasDespesas.observacao = Utils.acertarTexto("IMPORTACAO VR => " + " PARCELA: " + cellParcela.getContents() + " - "
                            //        + "VALOR TOTAL: " + cellValorTotal.getContents());
                            oPagarOutrasDespesas.valor = Double.parseDouble(cellValorTotal.getContents().replace(".", "").replace(",", "."));
                            oPagarOutrasDespesas.dataemissao = dataemissao;
                            oPagarOutrasDespesas.dataentrada = dataemissao;
                            oPagarOutrasDespesas.id_loja = i_idLojaDestino;
                            PagarOutrasDespesasVencimentoVO oPagarOutrasDespesasVencimento = new PagarOutrasDespesasVencimentoVO();
                            oPagarOutrasDespesasVencimento.datavencimento = datavencimento;
                            oPagarOutrasDespesasVencimento.valor = Double.parseDouble(cellValor.getContents().replace(".", "").replace(",", "."));
                            oPagarOutrasDespesas.vPagarOutrasDespesasVencimento.add(oPagarOutrasDespesasVencimento);
                            vPagarOutrasDespesas.add(oPagarOutrasDespesas);
                        }
                        
                        ProgressBar.setStatus("Carregando dados para comparação..."+contador+"...");
                        contador++;
                        //}
                    }
                }

                return vPagarOutrasDespesas;

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
}
