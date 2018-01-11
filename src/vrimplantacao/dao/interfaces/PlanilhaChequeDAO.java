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
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;

public class PlanilhaChequeDAO {

    public void migrarReceberCheque(String i_arquivo, int i_idLojaDestino, boolean Devolvido) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(i_arquivo, i_idLojaDestino, Devolvido);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vReceberCheque.size());

            ReceberChequeDAO receberChequeDAO = new ReceberChequeDAO();
            receberChequeDAO.salvar(vReceberCheque, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarReceberChequeDevolvidoAlegria(String i_arquivo, int i_idLojaDestino, boolean Devolvido) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ReceberChequeVO> vReceberCheque = carregarReceberChequeDevolvidoAlegria(i_arquivo, i_idLojaDestino);

            ProgressBar.setMaximum(vReceberCheque.size());

            ReceberChequeDAO receberChequeDAO = new ReceberChequeDAO();
            receberChequeDAO.alterarCadastroCliente = true;
            receberChequeDAO.salvar(vReceberCheque, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarReceberChequeGodoy(String i_arquivo, int i_idLojaDestino, boolean Devolvido) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ReceberChequeVO> vReceberCheque = carregarReceberChequeGodoy(i_arquivo, i_idLojaDestino);

            ProgressBar.setMaximum(vReceberCheque.size());

            ReceberChequeDAO receberChequeDAO = new ReceberChequeDAO();
            receberChequeDAO.salvar(vReceberCheque, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }

    private List<ReceberChequeVO> carregarReceberCheque(String i_arquivo, int id_loja, boolean Devolvido) throws Exception {
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        try {
            int linha = 0;
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Integer Alinea = 0;
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1252");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {
                stm = Conexao.createStatement();

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        }

                        Cell cellEmissao = sheet.getCell(0, i);
                        Cell cellVencimento = sheet.getCell(1, i);
                        Cell cellDocumento = sheet.getCell(2, i);
                        Cell cellNome = sheet.getCell(3, i);
                        Cell cellBanco = sheet.getCell(4, i);
                        Cell cellAgencia = sheet.getCell(5, i);
                        Cell cellConta = sheet.getCell(6, i);
                        Cell cellNroCheque = sheet.getCell(7, i);
                        Cell cellValor = sheet.getCell(8, i);
                        if (Devolvido) {
                            Alinea = 11;
                        } else {
                            Alinea = 0;
                        }
                        ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                        oReceberCheque.id_loja = id_loja;
                        oReceberCheque.id_tipoalinea = Alinea;
                        oReceberCheque.data = cellEmissao.getContents();
                        oReceberCheque.datadeposito = cellVencimento.getContents();
                        oReceberCheque.cpf = Long.parseLong(cellDocumento.getContents());
                        oReceberCheque.numerocheque = Integer.parseInt(cellNroCheque.getContents());;
                        sql = new StringBuilder();
                        sql.append("select id from banco where id = " + cellBanco.getContents().trim());
                        rst = stm.executeQuery(sql.toString());
                        if (rst.next()) {
                            oReceberCheque.id_banco = Integer.parseInt(cellBanco.getContents());
                        } else {
                            oReceberCheque.id_banco = 999;
                        }
                        oReceberCheque.agencia = cellAgencia.getContents();
                        oReceberCheque.conta = cellConta.getContents();
                        oReceberCheque.valor = Double.parseDouble(cellValor.getContents().toString().replace(",", "")) / 100;
                        oReceberCheque.observacao = "IMPORTADO VR";
                        oReceberCheque.nome = cellNome.getContents();
                        oReceberCheque.valorinicial = oReceberCheque.valor;

                        vReceberCheque.add(oReceberCheque);
                    }
                }

                return vReceberCheque;

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

    private List<ReceberChequeVO> carregarReceberChequeDevolvidoAlegria(String i_arquivo, int id_loja) throws Exception {
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        try {
            int linha = 0;
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            int Alinea = 0, IdBanco = 804;
            String nomeCliente, observacao, dataEmissao = "", dataVencimento = "";
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {
                stm = Conexao.createStatement();

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if (sheet.getCell(0, i).getContents().contains("Empresa:")) {
                            continue;
                        } else if (sheet.getCell(0, i).getContents().contains("Número")) {
                            continue;
                        } else if (sheet.getCell(0, i).getContents().contains("LJ-01 - SUPERMERCADO ALEGRIA LTDA.")) {
                            continue;
                        }

                        Cell cellNumeroCheque = sheet.getCell(0, i);
                        Cell cellDataEmissao = sheet.getCell(3, i);
                        Cell cellDataVencimento = sheet.getCell(4, i);
                        Cell cellDataConcilicao = sheet.getCell(5, i); // data conciliação
                        Cell cellDevolucao = sheet.getCell(6, i);
                        Cell cellCodAlinea = sheet.getCell(7, i);
                        Cell cellValor = sheet.getCell(8, i);
                        Cell cellHistorico = sheet.getCell(11, i);
                        Cell cellCodCliente = sheet.getCell(14, i);
                        Cell cellNomeCliente = sheet.getCell(15, i);
                        Cell cellBanco = sheet.getCell(16, i);

                        dataEmissao = cellDataEmissao.getContents().substring(0, 10);

                        dataEmissao = dataEmissao.substring(6, 10) + "/"
                                + dataEmissao.substring(3, 5) + "/"
                                + dataEmissao.substring(0, 2);

                        dataVencimento = cellDataVencimento.getContents().substring(0, 10);
                        dataVencimento = dataVencimento.substring(6, 10) + "/"
                                + dataVencimento.substring(3, 5) + "/"
                                + dataVencimento.substring(0, 2);

                        nomeCliente = Utils.acertarTexto(cellNomeCliente.getContents().trim());
                        observacao = "Data CONCILIACAO: " + cellDataConcilicao.getContents() + ", "
                                + "DEVOLUCAO: " + Utils.acertarTexto(cellDevolucao.getContents()) + ", "
                                + "HISTORICO: " + Utils.acertarTexto(cellHistorico.getContents());

                        Alinea = Integer.parseInt(cellCodAlinea.getContents().trim());

                        if (!cellBanco.getContents().trim().isEmpty()) {
                            IdBanco = Integer.parseInt(cellBanco.getContents().trim().substring(0, 3));
                        } else {
                            IdBanco = 804;
                        }

                        ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                        oReceberCheque.id_loja = id_loja;
                        oReceberCheque.id_tipoalinea = Alinea;
                        oReceberCheque.data = dataEmissao;
                        oReceberCheque.datadeposito = dataVencimento;

                        sql = new StringBuilder();
                        sql.append("select c.id, c.cnpj, c.nome, c.telefone, c.inscricaoestadual from clientepreferencial c ");
                        sql.append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ");
                        sql.append("where ant.codigoanterior = " + cellCodCliente.getContents().trim());
                        rst = stm.executeQuery(sql.toString());
                        if (rst.next()) {
                            oReceberCheque.idCliente = rst.getInt("id");
                            oReceberCheque.nome = rst.getString("nome");
                            oReceberCheque.cpf = rst.getLong("cnpj");
                            oReceberCheque.telefone = rst.getString("telefone");
                            oReceberCheque.rg = rst.getString("inscricaoestadual");
                        } else {
                            if (nomeCliente.length() > 40) {
                                nomeCliente = nomeCliente.substring(0, 40);
                            }

                            oReceberCheque.nome = nomeCliente;
                            oReceberCheque.cpf = Long.parseLong("0");
                        }

                        oReceberCheque.numerocheque = Integer.parseInt(cellNumeroCheque.getContents());

                        sql = new StringBuilder();
                        sql.append("select id from banco where id = " + IdBanco);
                        rst = stm.executeQuery(sql.toString());
                        if (rst.next()) {
                            oReceberCheque.id_banco = rst.getInt("id");
                        } else {
                            oReceberCheque.id_banco = 804;
                        }
                        oReceberCheque.agencia = "";
                        oReceberCheque.conta = "";

                        oReceberCheque.valor = Double.parseDouble(
                                cellValor.getContents().toString().replace(".", "").replace(",", "."));

                        oReceberCheque.observacao = "IMPORTADO VR => " + observacao;

                        oReceberCheque.valorinicial = oReceberCheque.valor;

                        vReceberCheque.add(oReceberCheque);
                    }
                }

                return vReceberCheque;

            } catch (Exception ex) {
                //if (linha > 0) {
                //    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                //} else {
                throw ex;
                //}
            }

        } catch (Exception e) {
            throw e;
        }
    }

    private List<ReceberChequeVO> carregarReceberChequeGodoy(String i_arquivo, int id_loja) throws Exception {
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        try {
            int linha = 0;
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            int Alinea = 0, IdBanco = 804, ecf, numeroCheque, numeroCupom;
            long cnpjCpf;
            double valor, valorPago, valorTotal;
            String nomeCliente, observacao, dataEmissao = "",
                    dataVencimento = "", dataDevolucao = "", agencia;
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1252");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {
                stm = Conexao.createStatement();

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        }

                        dataDevolucao = "";

                        Cell cellDataEmissao = sheet.getCell(3, i);
                        Cell cellCnpjCpf = sheet.getCell(4, i);
                        Cell cellNomeCliente = sheet.getCell(5, i);
                        Cell cellNumeroCaixa = sheet.getCell(6, i);
                        Cell cellNumeroCupom = sheet.getCell(7, i);
                        Cell cellNumeroCheque = sheet.getCell(8, i);
                        Cell cellAgencia = sheet.getCell(9, i);
                        Cell cellBanco = sheet.getCell(10, i);
                        Cell cellValor = sheet.getCell(11, i);
                        Cell cellDataVencimento = sheet.getCell(12, i);
                        Cell cellValorPago = sheet.getCell(13, i);
                        Cell cellDevolvido = sheet.getCell(18, i);
                        Cell cellDataDevolucao = sheet.getCell(19, i);
                        Cell cellObservacao = sheet.getCell(20, i);

                        if ((cellCnpjCpf.getContents() != null)
                                && (!cellCnpjCpf.getContents().trim().isEmpty())) {
                            cnpjCpf = Long.parseLong(Utils.formataNumero(cellCnpjCpf.getContents().trim()));
                        } else {
                            cnpjCpf = 0;
                        }

                        dataEmissao = cellDataEmissao.getContents().substring(0, 10);
                        dataEmissao = dataEmissao.substring(6, 10) + "/"
                                + dataEmissao.substring(3, 5) + "/"
                                + dataEmissao.substring(0, 2);

                        dataVencimento = cellDataVencimento.getContents().substring(0, 10);
                        dataVencimento = dataVencimento.substring(6, 10) + "/"
                                + dataVencimento.substring(3, 5) + "/"
                                + dataVencimento.substring(0, 2);

                        nomeCliente = Utils.acertarTexto(cellNomeCliente.getContents().trim());

                        if ((cellNumeroCaixa.getContents() != null)
                                && (!cellNumeroCaixa.getContents().trim().isEmpty())) {
                            ecf = Integer.parseInt(cellNumeroCaixa.getContents().trim());
                        } else {
                            ecf = 0;
                        }

                        if ((cellNumeroCheque.getContents() != null)
                                && (!cellNumeroCheque.getContents().trim().isEmpty())) {
                            numeroCheque = Integer.parseInt(Utils.formataNumero(cellNumeroCheque.getContents().trim()));
                        } else {
                            numeroCheque = 0;
                        }

                        if ((cellNumeroCupom.getContents() != null)
                                && (!cellNumeroCupom.getContents().trim().isEmpty())) {
                            numeroCupom = Integer.parseInt(Utils.formataNumero(cellNumeroCupom.getContents().trim()));
                        } else {
                            numeroCupom = 0;
                        }

                        if ((cellAgencia.getContents() != null)
                                && (!cellAgencia.getContents().trim().isEmpty())) {
                            agencia = Utils.acertarTexto(cellAgencia.getContents().replace("'", "").trim());
                        } else {
                            agencia = "";
                        }

                        if ((cellValor.getContents() != null)
                                && (!cellValor.getContents().trim().isEmpty())) {
                            valor = Double.parseDouble(cellValor.getContents().trim().replace(".", "").replace(",", "."));
                        } else {
                            valor = 0;
                        }

                        if ((cellValorPago.getContents() != null)
                                && (!cellValorPago.getContents().trim().isEmpty())) {
                            valorPago = Double.parseDouble(cellValorPago.getContents().trim().replace(".", "").replace(",", "."));
                        } else {
                            valorPago = 0;
                        }

                        if (!cellBanco.getContents().trim().isEmpty()) {
                            IdBanco = Integer.parseInt(cellBanco.getContents().trim());
                        } else {
                            IdBanco = 804;
                        }

                        if ("N".equals(cellDevolvido.getContents().trim())) {
                            Alinea = 0;
                        } else {
                            Alinea = 11;

                            if ((cellDataDevolucao.getContents() != null)
                                    && (!cellDataDevolucao.getContents().trim().isEmpty())) {
                                dataDevolucao = cellDataDevolucao.getContents().substring(0, 10);
                                dataDevolucao = dataDevolucao.substring(6, 10) + "/"
                                        + dataDevolucao.substring(3, 5) + "/"
                                        + dataDevolucao.substring(0, 2);
                            } else {
                                dataDevolucao = "";
                            }
                        }

                        if ((cellObservacao.getContents() != null)
                                && (!cellObservacao.getContents().trim().isEmpty())) {
                            observacao = Utils.acertarTexto(cellObservacao.getContents().replace("'", "").trim());
                        } else {
                            observacao = "IMPORTADO VR";
                        }

                        valorTotal = valor - valorPago;

                        if (nomeCliente.length() > 40) {
                            nomeCliente = nomeCliente.substring(0, 40);
                        }

                        ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                        oReceberCheque.id_loja = id_loja;
                        oReceberCheque.id_tipoalinea = Alinea;
                        oReceberCheque.data = dataEmissao;
                        oReceberCheque.datadeposito = dataVencimento;

                        if (cnpjCpf != 0) {
                            sql = new StringBuilder();
                            sql.append("select c.id, c.inscricaoestadual, c.telefone from clientepreferencial c ");
                            sql.append("where c.cnpj = " + cnpjCpf);
                            rst = stm.executeQuery(sql.toString());
                            if (rst.next()) {

                                oReceberCheque.telefone = rst.getString("telefone");
                                oReceberCheque.rg = rst.getString("inscricaoestadual");

                            }
                        } else {
                            sql = new StringBuilder();
                            sql.append("select c.id, c.cnpj, c.inscricaoestadual, c.telefone from clientepreferencial c ");
                            sql.append("where c.nome like '%" + nomeCliente + "%'");
                            rst = stm.executeQuery(sql.toString());
                            if (rst.next()) {

                                oReceberCheque.telefone = rst.getString("telefone");
                                oReceberCheque.rg = rst.getString("inscricaoestadual");
                                cnpjCpf = rst.getLong("cnpj");

                            }
                        }

                        oReceberCheque.cpf = cnpjCpf;
                        oReceberCheque.numerocheque = numeroCheque;

                        sql = new StringBuilder();
                        sql.append("select id from banco where id = " + IdBanco);
                        rst = stm.executeQuery(sql.toString());
                        if (rst.next()) {
                            oReceberCheque.id_banco = rst.getInt("id");
                        } else {
                            oReceberCheque.id_banco = 804;
                        }
                        oReceberCheque.agencia = agencia;
                        oReceberCheque.conta = "";

                        oReceberCheque.valor = valorTotal;

                        oReceberCheque.observacao = "IMPORTADO VR => " + observacao;

                        oReceberCheque.valorinicial = oReceberCheque.valor;
                        oReceberCheque.ecf = ecf;
                        oReceberCheque.numerocupom = numeroCupom;
                        oReceberCheque.datadevolucao = dataDevolucao;
                        oReceberCheque.nome = nomeCliente;

                        vReceberCheque.add(oReceberCheque);
                    }
                }

                return vReceberCheque;

            } catch (Exception ex) {
                //if (linha > 0) {
                //    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                //} else {
                throw ex;
                //}
            }

        } catch (Exception e) {
            throw e;
        }
    }

    public List<ReceberChequeVO> carregarChequeParana(String i_arquivo, int idLojaVR, boolean devolvido) throws Exception {
        List<ReceberChequeVO> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        String dataEmissao, dataDeposito;
        int linha;
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            
            stm = Conexao.createStatement();
            
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    //ignora o cabeçalho
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellNumeroCheque = sheet.getCell(0, i);
                    Cell cellCpfCliente = sheet.getCell(1, i);
                    Cell cellValorCheque = sheet.getCell(2, i);
                    Cell cellDataEmissao = sheet.getCell(3, i);
                    Cell cellCodBanco = sheet.getCell(4, i);
                    Cell cellAgencia = sheet.getCell(5, i);
                    Cell cellConta = sheet.getCell(6, i);
                    Cell cellDataDeposito = sheet.getCell(7, i);

                    dataEmissao = cellDataEmissao.getContents().substring(0, 10);
                    dataEmissao = dataEmissao.substring(6, 10) + "/"
                            + dataEmissao.substring(3, 5) + "/"
                            + dataEmissao.substring(0, 2);

                    dataDeposito = cellDataDeposito.getContents().substring(0, 10);
                    dataDeposito = dataDeposito.substring(6, 10) + "/"
                            + dataDeposito.substring(3, 5) + "/"
                            + dataDeposito.substring(0, 2);

                    ReceberChequeVO oReceberCheque = new ReceberChequeVO();
                    oReceberCheque.setNumerocheque(Integer.parseInt(cellNumeroCheque.getContents().trim()));
                    oReceberCheque.setValor(Double.parseDouble(cellValorCheque.getContents().replace(",", ".").trim()));
                    oReceberCheque.setId_banco(Integer.parseInt(cellCodBanco.getContents().trim()));
                    oReceberCheque.setAgencia(Utils.acertarTexto(cellAgencia.getContents().trim()));
                    oReceberCheque.setConta(Utils.acertarTexto(cellConta.getContents().trim()));
                    oReceberCheque.setCpf(Long.parseLong(cellCpfCliente.getContents().trim()));
                    oReceberCheque.setData(dataEmissao);
                    oReceberCheque.setDatadeposito(dataDeposito);

                    if (devolvido) {
                        oReceberCheque.setId_tipoalinea(11);
                        oReceberCheque.setDatadevolucao(dataDeposito);
                    } else {
                        oReceberCheque.setId_tipoalinea(0);
                    }

                    sql = new StringBuilder();
                    sql.append("select nome, telefone, inscricaoestadual from clientepreferencial "
                            + "where cnpj = " + oReceberCheque.getCpf());
                    rst = stm.executeQuery(sql.toString());
                    if (rst.next()) {
                        oReceberCheque.setNome(rst.getString("nome"));
                        oReceberCheque.setRg(rst.getString("inscricaoestadual"));
                        oReceberCheque.setTelefone(rst.getString("telefone"));
                    }
                    
                    sql = new StringBuilder();
                    sql.append("select id from banco "
                            + "where id = " + oReceberCheque.getId_banco());
                    rst = stm.executeQuery(sql.toString());
                    if (!rst.next()) {
                        oReceberCheque.setId_banco(804);
                    }
                    
                    result.add(oReceberCheque);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarChequeParana(String i_arquivo, int idLojaVR, boolean devolvido) throws Exception {
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber cheque "+(devolvido ? "devolvido" : ""));
            vReceberCheque = carregarChequeParana(i_arquivo, idLojaVR, devolvido);
            new ReceberChequeDAO().salvarComCondicao2(vReceberCheque, idLojaVR);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
