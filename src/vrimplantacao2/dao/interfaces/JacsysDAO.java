/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class JacsysDAO extends InterfaceDAO {

    private ConexaoDBF connDBF = new ConexaoDBF();
    public String pathBancoDBF;
    public String i_arquivoProduto = "";
    public String i_arquivoFornecedor = "";
    public String i_arquivoCliente = "";
    public String i_arquivoCreditoRotativo = "";
    public List<ProdutoIMP> v_list;

    @Override
    public String getSistema() {
        return "Jacsys";
    }

    public List<MercadologicoNivelIMP> getMercadologicoPorNivel_old() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        connDBF.abrirConexao(pathBancoDBF);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codgrp merc1, nome merc1_desc "
                    + "from GRUPO "
                    + "where nivel = 1 "
                    + "order by codgrp"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("merc1_desc"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select codgrp merc2, nome merc2_desc "
                    + "from GRUPO "
                    + "where nivel = 2 "
                    + "order by codgrp"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc2").substring(0, 2));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("merc2").substring(2, 4),
                                rst.getString("merc2_desc")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select codgrp merc3, nome merc3_desc "
                    + "from GRUPO "
                    + "where nivel = 3 "
                    + "order by codgrp"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc3").substring(0, 2));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc3").substring(2, 4));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3").substring(4, 6),
                                    rst.getString("merc3_desc")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    public List<MercadologicoIMP> getMercadologicos_old() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        String merc1Descricao = "", merc2Descricao = "";
        connDBF.abrirConexao(pathBancoDBF);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codgrp, nivel, nome "
                    + "from GRUPO "
                    + "order by codgrp"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    if (rst.getString("codgrp").trim().length() == 2) {
                        imp.setMerc1ID(rst.getString("codgrp").trim());
                        imp.setMerc1Descricao(rst.getString("nome"));
                        merc1Descricao = imp.getMerc1Descricao();
                    } else if (rst.getString("codgrp").trim().length() == 4) {
                        imp.setMerc1ID(rst.getString("codgrp").trim().substring(0, 2));
                        imp.setMerc2ID(rst.getString("codgrp").trim().substring(2, 4));
                        imp.setMerc1Descricao(merc1Descricao);
                        imp.setMerc2Descricao(rst.getString("nome").trim());
                        merc2Descricao = imp.getMerc2Descricao();
                    } else if (rst.getString("codgrp").trim().length() == 6) {
                        imp.setMerc1ID(rst.getString("codgrp").trim().substring(0, 2));
                        imp.setMerc2ID(rst.getString("codgrp").trim().substring(2, 4));
                        imp.setMerc3ID(rst.getString("codgrp").trim().substring(4, 6));
                        imp.setMerc1Descricao(merc1Descricao);
                        imp.setMerc2Descricao(merc2Descricao);
                        imp.setMerc3Descricao(rst.getString("nome").trim());
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public List<MercadologicoIMP> getMercadologico() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoProduto), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellGrupo = sheet.getCell(1, i);
                    Cell cellNivel = sheet.getCell(2, i);
                    Cell cellDescricao = sheet.getCell(3, i);

                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    if (null != cellNivel.getContents()) {
                        switch (cellNivel.getContents()) {
                            case "1":
                                imp.setMerc1ID(cellGrupo.getContents());
                                imp.setMerc1Descricao(cellDescricao.getContents().trim());
                                break;
                            case "2":
                                imp.setMerc1ID(cellGrupo.getContents().substring(0, 2));
                                imp.setMerc2ID(cellGrupo.getContents().substring(2, 4));
                                imp.setMerc2Descricao(cellDescricao.getContents().trim());
                                break;
                            case "3":
                                imp.setMerc1ID(cellGrupo.getContents());
                                imp.setMerc2ID(cellGrupo.getContents());
                                imp.setMerc3ID(cellGrupo.getContents());
                                imp.setMerc3Descricao(cellDescricao.getContents().trim());
                                break;
                        }
                    }
                    vResult.add(imp);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoIMP> getProdutos_old() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        //ConexaoDBF.usarOdbc = true;
        connDBF.abrirConexao(pathBancoDBF);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from "
                    + " ITEMSTK "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codstk"));
                    imp.setDescricaoCompleta(rst.getString("nome").trim());
                    imp.setDescricaoReduzida(rst.getString("balanca").trim());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCustoComImposto(rst.getDouble("prccusto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("prcpro"));
                    imp.setEstoqueMinimo(rst.getDouble("estmin"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setPesoBruto(rst.getDouble("pesobru"));
                    imp.seteBalanca(("S".equals(rst.getString("cargabal"))));
                    imp.setEan(Utils.formataNumero(rst.getString("codbarra")));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    //@Override
    public List<ProdutoIMP> getProdutos_old2() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoProduto), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha, cst;
        double aliquota, reducao;
        boolean eBalanca;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }
                    Cell cellCodProduto = sheet.getCell(0, i);
                    Cell cellGrupo = sheet.getCell(1, i);
                    Cell cellCodBarra = sheet.getCell(2, i);
                    Cell cellNome = sheet.getCell(5, i);
                    Cell cellDetalhe = sheet.getCell(6, i);
                    Cell cellBalanca = sheet.getCell(7, i);
                    Cell cellUnidade = sheet.getCell(10, i);
                    Cell cellCargaBalanca = sheet.getCell(14, i);
                    Cell cellCt = sheet.getCell(21, i);
                    Cell cellValidade = sheet.getCell(33, i);
                    Cell cellPeso = sheet.getCell(34, i);
                    Cell cellPesoBruto = sheet.getCell(35, i);
                    Cell cellPreco = sheet.getCell(69, i);
                    Cell cellCusto = sheet.getCell(40, i);
                    Cell cellEstoque = sheet.getCell(48, i);
                    Cell cellEstMinimo = sheet.getCell(50, i);
                    Cell cellEstMaximo = sheet.getCell(51, i);
                    Cell cellQtdEmb = sheet.getCell(54, i);
                    Cell cellMargem = sheet.getCell(75, i);

                    aliquota = 0;
                    reducao = 0;

                    long codigoProduto;
                    codigoProduto = Long.parseLong(Utils.formataNumero(cellCodProduto.getContents().trim()));

                    ProdutoBalancaVO produtoBalanca;
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        eBalanca = true;
                    } else {
                        if ("S".equals(cellCargaBalanca.getContents())) {
                            eBalanca = true;
                        } else {
                            eBalanca = false;
                        }
                    }

                    if (Integer.parseInt(cellCt.getContents().trim()) == 1) {
                        cst = 40;
                    } else if (Integer.parseInt(cellCt.getContents().trim()) == 2) {
                        cst = 41;
                    } else if (Integer.parseInt(cellCt.getContents().trim()) == 3) {
                        cst = 60;
                    } else if (Integer.parseInt(cellCt.getContents().trim()) == 7) {
                        cst = 0;
                        aliquota = 7;
                    } else if (Integer.parseInt(cellCt.getContents().trim()) == 12) {
                        cst = 0;
                        aliquota = 12;
                    } else if (Integer.parseInt(cellCt.getContents().trim()) == 18) {
                        cst = 0;
                        aliquota = 18;
                    } else {
                        cst = 90;
                    }

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    if (eBalanca) {
                        imp.setImportId(cellCodProduto.getContents().trim());
                    } else {
                        imp.setImportId(Utils.formataNumero(cellCodBarra.getContents().trim()));
                    }
                    imp.seteBalanca(eBalanca);
                    imp.setEan(Utils.formataNumero(cellCodBarra.getContents().trim()));
                    imp.setDescricaoCompleta(cellNome.getContents());
                    imp.setDescricaoReduzida(cellBalanca.getContents());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(cellUnidade.getContents().trim());
                    imp.setValidade(Integer.parseInt(Utils.formataNumero(cellValidade.getContents())));
                    imp.setPesoLiquido(Double.parseDouble(cellPeso.getContents().replace(",", ".")));
                    imp.setPesoBruto(Double.parseDouble(cellPesoBruto.getContents().replace(",", ".")));
                    imp.setCustoComImposto(Double.parseDouble(cellCusto.getContents().replace(",", ".")));
                    imp.setMargem(Double.parseDouble(cellMargem.getContents().replace(",", ".")));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(Double.parseDouble(cellPreco.getContents().replace(",", ".")));
                    imp.setEstoqueMinimo(Double.parseDouble(cellEstMinimo.getContents().replace(",", ".")));
                    imp.setEstoqueMaximo(Double.parseDouble(cellEstMaximo.getContents().replace(",", ".")));
                    imp.setEstoque(Double.parseDouble(cellEstoque.getContents().replace(",", ".")));
                    imp.setIcmsCst(cst);
                    imp.setIcmsAliq(aliquota);
                    imp.setIcmsReducao(reducao);
                    vResult.add(imp);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        String observacao;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoFornecedor), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodCli = sheet.getCell(0, i);
                    Cell cellNome = sheet.getCell(8, i);
                    Cell cellApelido = sheet.getCell(9, i);
                    Cell cellEndereco = sheet.getCell(10, i);
                    Cell cellBairro = sheet.getCell(11, i);
                    Cell cellCidade = sheet.getCell(12, i);
                    Cell cellUf = sheet.getCell(14, i);
                    Cell cellCep = sheet.getCell(21, i);
                    Cell cellCep2 = sheet.getCell(22, i);
                    Cell cellTel = sheet.getCell(23, i);
                    Cell cellFax = sheet.getCell(24, i);
                    Cell cellCel = sheet.getCell(25, i);
                    Cell cellTel2 = sheet.getCell(26, i);
                    Cell cellFax2 = sheet.getCell(27, i);
                    Cell cellCel2 = sheet.getCell(28, i);
                    Cell cellTel02 = sheet.getCell(29, i);
                    Cell cellTel03 = sheet.getCell(30, i);
                    Cell cellContato = sheet.getCell(31, i);
                    Cell cellContato2 = sheet.getCell(32, i);
                    Cell cellCnpj = sheet.getCell(43, i);
                    Cell cellIe = sheet.getCell(44, i);
                    Cell cellHomePage = sheet.getCell(45, i);
                    Cell cellEmail = sheet.getCell(46, i);
                    Cell cellDtCad = sheet.getCell(47, i);
                    Cell cellRamo = sheet.getCell(74, i);
                    Cell cellObs1 = sheet.getCell(87, i);
                    Cell cellObs2 = sheet.getCell(88, i);
                    Cell cellObs3 = sheet.getCell(89, i);
                    Cell cellObs4 = sheet.getCell(90, i);
                    Cell cellObs5 = sheet.getCell(91, i);
                    Cell cellObs6 = sheet.getCell(92, i);

                    observacao = "OBS1: " + cellObs1.getContents() + " OBS2: " + cellObs2.getContents() + " "
                            + "OBS3: " + cellObs3.getContents() + " OBS4: " + cellObs4.getContents() + " "
                            + "OBS5: " + cellObs5.getContents() + " OBS6: " + cellObs6.getContents();

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(cellCodCli.getContents().trim());
                    imp.setRazao(cellNome.getContents().trim());
                    imp.setFantasia(cellApelido.getContents().trim());
                    imp.setEndereco(cellEndereco.getContents().trim());
                    imp.setBairro(cellBairro.getContents().trim());
                    imp.setMunicipio(cellCidade.getContents());
                    imp.setUf(cellUf.getContents().trim());
                    imp.setCep(Utils.formataNumero(cellCep.getContents()).trim());
                    imp.setTel_principal(Utils.formataNumero(cellTel.getContents().trim()));
                    imp.setCnpj_cpf(Utils.formataNumero(cellCnpj.getContents().trim()));
                    imp.setIe_rg(Utils.formataNumero(cellIe.getContents().trim()));
                    imp.setObservacao(observacao);

                    if ((cellCel.getContents() != null) && (!cellCel.getContents().trim().isEmpty())) {
                        imp.addContato("1",
                                "CELULAR",
                                "",
                                Utils.formataNumero(cellCel.getContents().trim()),
                                TipoContato.COMERCIAL,
                                "");
                    }
                    if ((cellFax.getContents() != null) && (!cellFax.getContents().trim().isEmpty())) {
                        imp.addContato("2",
                                "FAX",
                                Utils.formataNumero(cellFax.getContents()),
                                "",
                                TipoContato.COMERCIAL,
                                "");
                    }
                    if ((cellTel2.getContents() != null) && (!cellTel2.getContents().trim().isEmpty())) {
                        imp.addContato("3",
                                "TELEFONE 2",
                                Utils.formataNumero(cellTel2.getContents()),
                                "",
                                TipoContato.COMERCIAL,
                                "");
                    }
                    if ((cellCel2.getContents() != null) && (!cellCel2.getContents().trim().isEmpty())) {
                        imp.addContato("4",
                                "CELULAR 2",
                                "",
                                Utils.formataNumero(cellCel2.getContents()),
                                TipoContato.COMERCIAL,
                                "");
                    }
                    if ((cellFax2.getContents() != null) && (!cellFax2.getContents().trim().isEmpty())) {
                        imp.addContato("5",
                                "FAX 2",
                                Utils.formataNumero(cellFax2.getContents()),
                                "",
                                TipoContato.COMERCIAL,
                                "");
                    }
                    if ((cellEmail.getContents() != null) && (!cellEmail.getContents().trim().isEmpty())) {
                        imp.addContato("6",
                                "EMAIL",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                cellEmail.getContents().trim()
                        );
                    }
                    if ((cellHomePage.getContents() != null) && (!cellHomePage.getContents().trim().isEmpty())) {
                        imp.addContato("6",
                                "HOME PAGE",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                cellHomePage.getContents().trim()
                        );
                    }

                    vResult.add(imp);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        String observacao;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoCliente), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodCli = sheet.getCell(0, i);
                    Cell cellNome = sheet.getCell(8, i);
                    Cell cellApelido = sheet.getCell(9, i);
                    Cell cellEndereco = sheet.getCell(10, i);
                    Cell cellBairro = sheet.getCell(11, i);
                    Cell cellCidade = sheet.getCell(12, i);
                    Cell cellUf = sheet.getCell(14, i);
                    Cell cellCep = sheet.getCell(21, i);
                    Cell cellCep2 = sheet.getCell(22, i);
                    Cell cellTel = sheet.getCell(23, i);
                    Cell cellFax = sheet.getCell(24, i);
                    Cell cellCel = sheet.getCell(25, i);
                    Cell cellTel2 = sheet.getCell(26, i);
                    Cell cellFax2 = sheet.getCell(27, i);
                    Cell cellCel2 = sheet.getCell(28, i);
                    Cell cellTel02 = sheet.getCell(29, i);
                    Cell cellTel03 = sheet.getCell(30, i);
                    Cell cellContato = sheet.getCell(31, i);
                    Cell cellContato2 = sheet.getCell(32, i);
                    Cell cellCnpj = sheet.getCell(43, i);
                    Cell cellIe = sheet.getCell(44, i);
                    Cell cellHomePage = sheet.getCell(45, i);
                    Cell cellEmail = sheet.getCell(46, i);
                    Cell cellDtCad = sheet.getCell(47, i);
                    Cell cellRamo = sheet.getCell(74, i);
                    Cell cellObs1 = sheet.getCell(87, i);
                    Cell cellObs2 = sheet.getCell(88, i);
                    Cell cellObs3 = sheet.getCell(89, i);
                    Cell cellObs4 = sheet.getCell(90, i);
                    Cell cellObs5 = sheet.getCell(91, i);
                    Cell cellObs6 = sheet.getCell(92, i);

                    observacao = "OBS1: " + cellObs1.getContents() + " OBS2: " + cellObs2.getContents() + " "
                            + "OBS3: " + cellObs3.getContents() + " OBS4: " + cellObs4.getContents() + " "
                            + "OBS5: " + cellObs5.getContents() + " OBS6: " + cellObs6.getContents();

                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(cellCodCli.getContents().trim());
                    imp.setRazao(cellNome.getContents().trim());
                    imp.setFantasia(cellApelido.getContents().trim());
                    imp.setEndereco(cellEndereco.getContents().trim());
                    imp.setBairro(cellBairro.getContents().trim());
                    imp.setMunicipio(cellCidade.getContents());
                    imp.setUf(cellUf.getContents().trim());
                    imp.setCep(Utils.formataNumero(cellCep.getContents()).trim());
                    imp.setTelefone(Utils.formataNumero(cellTel.getContents().trim()));
                    imp.setCelular(Utils.formataNumero(cellCel.getContents().trim()));
                    imp.setEmail(cellEmail.getContents().trim());
                    imp.setCnpj(Utils.formataNumero(cellCnpj.getContents().trim()));
                    imp.setInscricaoestadual(Utils.formataNumero(cellIe.getContents().trim()));
                    imp.setCargo(cellRamo.getContents().trim());
                    imp.setPermiteCreditoRotativo(true);
                    imp.setPermiteCheque(true);
                    imp.setObservacao(observacao);

                    if ((cellFax.getContents() != null) && (!cellFax.getContents().trim().isEmpty())) {
                        imp.addContato("1",
                                "FAX",
                                Utils.formataNumero(cellFax.getContents()),
                                "",
                                "");
                    }
                    if ((cellTel2.getContents() != null) && (!cellTel2.getContents().trim().isEmpty())) {
                        imp.addContato("2",
                                "TELEFONE 2",
                                Utils.formataNumero(cellTel2.getContents()),
                                "",
                                "");
                    }
                    if ((cellCel2.getContents() != null) && (!cellCel2.getContents().trim().isEmpty())) {
                        imp.addContato("3",
                                "CELULAR 2",
                                "",
                                Utils.formataNumero(cellCel2.getContents()),
                                "");
                    }
                    if ((cellFax2.getContents() != null) && (!cellFax2.getContents().trim().isEmpty())) {
                        imp.addContato("4",
                                "FAX 2",
                                Utils.formataNumero(cellFax2.getContents()),
                                "",
                                "");
                    }
                    if ((cellHomePage.getContents() != null) && (!cellHomePage.getContents().trim().isEmpty())) {
                        imp.addContato("5",
                                "HOME PAGE",
                                "",
                                "",
                                cellHomePage.getContents().trim()
                        );
                    }

                    vResult.add(imp);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoCreditoRotativo), settings);
        Sheet[] sheets = arquivo.getSheets();
        java.sql.Date dataEmissao;
        java.sql.Date dataVencimento;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        int linha;
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha <= 2) {
                        continue;
                    }

                    Cell cellId = sheet.getCell(0, i);
                    Cell cellCodLoja = sheet.getCell(2, i);
                    Cell cellNumDoc = sheet.getCell(4, i);
                    Cell cellNumPed = sheet.getCell(7, i);
                    Cell cellCodCli = sheet.getCell(12, i);
                    Cell cellDataEmissao = sheet.getCell(31, i);
                    Cell cellDataVencimento = sheet.getCell(32, i);
                    Cell cellDataPagto = sheet.getCell(33, i);
                    Cell cellTipo = sheet.getCell(43, i);
                    Cell cellHistorico = sheet.getCell(47, i);
                    Cell cellObs1 = sheet.getCell(48, i);
                    Cell cellValor = sheet.getCell(56, i);
                    Cell cellPago = sheet.getCell(61, i);

                    dataEmissao = new java.sql.Date(fmt.parse(cellDataEmissao.getContents()).getTime());
                    dataVencimento = new java.sql.Date(fmt.parse(cellDataVencimento.getContents()).getTime());

                    if ((Double.parseDouble(cellPago.getContents().replace(",", "."))
                            < (Double.parseDouble(cellValor.getContents().replace(",", "."))))) {

                        CreditoRotativoIMP imp = new CreditoRotativoIMP();
                        imp.setId(cellId.getContents());
                        //imp.setIdCliente(String.valueOf(Integer.parseInt(cellCodCli.getContents().trim())));
                        imp.setIdCliente(cellCodCli.getContents().trim());
                        imp.setNumeroCupom(cellNumPed.getContents().trim());
                        imp.setDataEmissao(dataEmissao);
                        imp.setDataVencimento(dataVencimento);
                        imp.setValor(Double.parseDouble(cellValor.getContents().replace(",", ".")));
                        imp.setObservacao(cellObs1.getContents().trim());
                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void gerarListagemProdutosMudaramCodigo() throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoProduto), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Sanches - Perola\\rel\\updateTipoEmbalagem.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        try {

            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setStatus("Gerando script UPDATE...");

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodProduto = sheet.getCell(0, i);
                    Cell cellCodBarras = sheet.getCell(2, i);
                    Cell cellUnidade = sheet.getCell(10, i);

                    if ("KG".equals(cellUnidade.getContents().trim())) {

                        sql = new StringBuilder();
                        sql.append("select ant.codigoatual, ant.descricao "
                                + "from implantacao.codant_produto ant "
                                + "inner join implantacao.codant_ean ean on ean.importid = ant.impid "
                                + "where ean.ean = '" + Utils.formataNumero(cellCodBarras.getContents().trim()) + "' "
                                + "and ean.ean like '2%'");
                        rst = stm.executeQuery(sql.toString());
                        if (rst.next()) {
                            //if (rst.getInt("codigoatual") != Integer.parseInt(cellCodProduto.getContents().trim())) {
                            //    bw.write(Utils.formataNumero(cellCodProduto.getContents().trim())+";"+rst.getInt("codigoatual")+";"+rst.getString("descricao"));
                            //    bw.newLine();
                            //}
                            bw.write("update produto set id_tipoembalagem = 4, id_tipoembalagemvolume = 4, pesavel = false "
                                    + "where id = " + rst.getInt("codigoatual") + "; -- "+rst.getString("descricao"));                            
                            bw.newLine();
                            bw.write("update produtoautomacao set id_tipoembalagem = 4 "
                                    + "where id_produto = " + rst.getInt("codigoatual")+"; --"+rst.getString("descricao"));
                            bw.newLine();
                        }
                    }
                    ProgressBar.setStatus("Gerando script UPDATE..." + linha);
                }
            }
            bw.flush();
            bw.close();
            stm.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            Conexao.destruir(null, stm, rst);
        }
    }
    
    /* temporario para o sistema Base*/
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoProduto), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha, cst;
        double aliquota, reducao;
        boolean eBalanca;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }
                    Cell cellCodProduto = sheet.getCell(0, i);
                    Cell cellNome = sheet.getCell(1, i);
                    Cell cellEstoque = sheet.getCell(2, i);
                    Cell cellQtdEmbalagem = sheet.getCell(3, i);
                    Cell cellMargem = sheet.getCell(5, i);
                    Cell cellCusto = sheet.getCell(6, i);
                    Cell cellPrecoVenda = sheet.getCell(7, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    if (cellCodProduto.getContents().length() > 14) {
                        imp.setImportId(cellCodProduto.getContents().substring(0, 14));
                    } else {
                        imp.setImportId(cellCodProduto.getContents());
                    }
                    
                    if (cellCodProduto.getContents().length() > 14) {
                        imp.setEan(cellCodProduto.getContents().substring(0, 14));
                    } else {
                        imp.setEan(cellCodProduto.getContents());
                    }
                    imp.setQtdEmbalagem(Integer.parseInt(cellQtdEmbalagem.getContents()));
                    imp.setTipoEmbalagem("UN");
                    imp.setDescricaoCompleta(cellNome.getContents());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setNcm("22021000");
                    imp.setPiscofinsCstDebito(49);
                    imp.setPiscofinsCstCredito(99);
                    imp.setMargem(Double.parseDouble(cellMargem.getContents().replace(",", ".")));
                    imp.setPrecovenda(Double.parseDouble(cellPrecoVenda.getContents().replace(",", ".")));
                    imp.setCustoComImposto(Double.parseDouble(cellCusto.getContents().replace(",", ".")));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(Double.parseDouble(cellEstoque.getContents().replace(",", ".")));
                    imp.setIcmsCst(60);
                    imp.setIcmsAliq(0);
                    imp.setIcmsReducao(0);
                    vResult.add(imp);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void acertarQtdEmbalagem() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            v_list = getProdutos();
            ProgressBar.setStatus("Acetar QtdEmbalagem Produto...");
            ProgressBar.setMaximum(v_list.size());
            for (ProdutoIMP i_list : v_list) {
                sql = new StringBuilder();
                sql.append("select codigoatual "
                        + "from implantacao.codant_produto "
                        + "where imploja = '"+getLojaOrigem()+"' "
                        + "and impsistema = '"+getSistema()+"' "
                        + "and impid = '"+i_list.getImportId()+"'");
                rst = stm.executeQuery(sql.toString());
                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto "
                            + "set "
                            + "qtdembalagem = " +i_list.getQtdEmbalagem() + " "
                            + "where id = " + rst.getInt("codigoatual"));
                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
