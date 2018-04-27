/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrimplantacao.utils.Utils;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SiaCriareByFileDAO extends InterfaceDAO implements MapaTributoProvider {

    public String v_pahtFileXls;

    @Override
    public String getSistema() {
        return "SiaCriareByFile";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//aliquota.xls"), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            linha = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                linha++;
                if (linha == 1) {
                    continue;
                }

                Cell cellId = sheet.getCell(0, i);
                Cell cellDescricao = sheet.getCell(1, i);
                Cell cellCst = sheet.getCell(4, i);
                result.add(new MapaTributoIMP(cellId.getContents(), cellCst.getContents() + " - " + cellDescricao.getContents()));
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//familia.xls"), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            linha = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                linha++;
                if (linha == 1) {
                    continue;
                }

                Cell cellCodigo = sheet.getCell(0, i);
                Cell cellDescricao = sheet.getCell(2, i);

                FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(cellCodigo.getContents());
                imp.setDescricao(cellDescricao.getContents());
                result.add(imp);
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//grupo.xls"), settings);
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

                    Cell cellId = sheet.getCell(0, i);
                    Cell cellDescricao = sheet.getCell(1, i);

                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(cellId.getContents());
                    imp.setMerc1Descricao(cellDescricao.getContents());
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(cellDescricao.getContents());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(cellDescricao.getContents());
                    result.add(imp);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//produto.xls"), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        String strPreco, strCusto;
        java.sql.Date dataCadastro;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        try {

            //Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellId = sheet.getCell(0, i);
                    Cell cellIdGrupo = sheet.getCell(1, i);
                    Cell cellDescricao = sheet.getCell(2, i);
                    Cell cellReduzido = sheet.getCell(3, i);
                    Cell cellPreco = sheet.getCell(4, i);
                    Cell cellBalanca = sheet.getCell(5, i);
                    Cell cellUnidade = sheet.getCell(8, i);
                    Cell cellValidade = sheet.getCell(11, i);
                    Cell cellIcms = sheet.getCell(17, i);
                    Cell cellIdFamilia = sheet.getCell(24, i);
                    Cell cellCodBarras = sheet.getCell(25, i);
                    Cell cellCusto = sheet.getCell(39, i);
                    Cell cellNcm = sheet.getCell(42, i);
                    Cell cellAtivo = sheet.getCell(48, i);
                    Cell cellPesoLiquido = sheet.getCell(59, i);
                    Cell cellPesoBruto = sheet.getCell(60, i);
                    Cell cellMargem = sheet.getCell(67, i);
                    Cell cellData = sheet.getCell(102, i);
                    Cell cellCstPisCredito = sheet.getCell(103, i);
                    Cell cellCstPisDebito = sheet.getCell(121, i);
                    Cell cellCest = sheet.getCell(196, i);

                    dataCadastro = new java.sql.Date(fmt.parse(cellData.getContents()).getTime());

                    strPreco = "";
                    strCusto = "";

                    if (cellPreco.getContents().length() == 9) {
                        for (int j = 0; j < cellPreco.getContents().length(); j++) {
                            if (j == 1) {
                                strPreco = strPreco + "";
                            } else {
                                strPreco = strPreco + cellPreco.getContents().charAt(j);
                            }
                        }
                    } else if (cellPreco.getContents().length() == 10) {
                        for (int j = 0; j < cellPreco.getContents().length(); j++) {
                            if (j == 2) {
                                strPreco = strPreco + "";
                            } else {
                                strPreco = strPreco + cellPreco.getContents().charAt(j);
                            }
                        }
                    } else if (cellPreco.getContents().length() == 11) {
                        for (int j = 0; j < cellPreco.getContents().length(); j++) {
                            if (j == 3) {
                                strPreco = strPreco + "";
                            } else {
                                strPreco = strPreco + cellPreco.getContents().charAt(j);
                            }
                        }
                    } else {
                        strPreco = cellPreco.getContents();
                    }

                    if (cellCusto.getContents().length() == 9) {
                        for (int j = 0; j < cellCusto.getContents().length(); j++) {
                            if (j == 1) {
                                strCusto = strCusto + "";
                            } else {
                                strCusto = strCusto + cellCusto.getContents().charAt(j);
                            }
                        }
                    } else if (cellCusto.getContents().length() == 10) {
                        for (int j = 0; j < cellCusto.getContents().length(); j++) {
                            if (j == 2) {
                                strCusto = strCusto + "";
                            } else {
                                strCusto = strCusto + cellCusto.getContents().charAt(j);
                            }
                        }
                    } else if (cellCusto.getContents().length() == 11) {
                        for (int j = 0; j < cellCusto.getContents().length(); j++) {
                            if (j == 3) {
                                strCusto = strCusto + "";
                            } else {
                                strCusto = strCusto + cellCusto.getContents().charAt(j);
                            }
                        }
                    } else {
                        strCusto = cellCusto.getContents();
                    }

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellId.getContents());
                    imp.setEan(cellCodBarras.getContents());
                    imp.seteBalanca("S".equals(cellBalanca.getContents()));
                    imp.setValidade(Utils.stringToInt(cellValidade.getContents()));
                    imp.setSituacaoCadastro("S".equals(cellAtivo.getContents()) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDataCadastro(dataCadastro);
                    imp.setDescricaoCompleta(cellDescricao.getContents());
                    imp.setDescricaoReduzida(cellReduzido.getContents());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(cellUnidade.getContents());
                    imp.setIdFamiliaProduto(cellIdFamilia.getContents());
                    imp.setCodMercadologico1(cellIdGrupo.getContents());
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setPrecovenda(Double.parseDouble(strPreco));
                    imp.setCustoComImposto(Double.parseDouble(strCusto));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(Double.parseDouble(cellMargem.getContents()));
                    imp.setPesoBruto(Double.parseDouble(cellPesoBruto.getContents()));
                    imp.setPesoLiquido(Double.parseDouble(cellPesoLiquido.getContents()));
                    imp.setNcm(cellNcm.getContents());
                    imp.setCest(cellCest.getContents());
                    imp.setPiscofinsCstDebito(cellCstPisDebito.getContents());
                    imp.setPiscofinsCstCredito(cellCstPisCredito.getContents());
                    imp.setIcmsDebitoId(cellIcms.getContents());
                    imp.setIcmsCreditoId(cellIcms.getContents());

                    /*if (imp.getEan().trim().length() < 7) {
                     ProdutoBalancaVO produtoBalanca;
                     long codigoProduto;
                     codigoProduto = Long.parseLong(imp.getEan().trim());
                     if (codigoProduto <= Integer.MAX_VALUE) {
                     produtoBalanca = produtosBalanca.get((int) codigoProduto);
                     } else {
                     produtoBalanca = null;
                     }
                     if (produtoBalanca != null) {
                     imp.seteBalanca(true);
                     imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : Utils.stringToInt(cellValidade.getContents()));
                     } else {
                     imp.setValidade(0);
                     imp.seteBalanca(false);
                     }
                     } else {
                     imp.setValidade(Utils.stringToInt(cellValidade.getContents()));
                     imp.seteBalanca(false);
                     }*/
                    result.add(imp);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        java.sql.Date dataCadastro;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//cliente.xls"), settings);
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

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellFantasia = sheet.getCell(1, i);
                    Cell cellEndereco = sheet.getCell(2, i);
                    Cell cellBairro = sheet.getCell(3, i);
                    Cell cellMunicipio = sheet.getCell(4, i);
                    Cell cellUf = sheet.getCell(5, i);
                    Cell cellCep = sheet.getCell(6, i);
                    Cell cellTelefone = sheet.getCell(7, i);
                    Cell cellFax = sheet.getCell(8, i);
                    Cell cellinscMunicipal = sheet.getCell(11, i);
                    Cell cellObservacao = sheet.getCell(14, i);
                    Cell cellCnpjCpf = sheet.getCell(15, i);
                    Cell cellIeRg = sheet.getCell(16, i);
                    Cell cellEmail = sheet.getCell(19, i);
                    Cell cellReferencia = sheet.getCell(22, i);
                    Cell cellAtivo = sheet.getCell(40, i);
                    Cell cellMotivoDesativo = sheet.getCell(41, i);
                    Cell cellTipo = sheet.getCell(42, i);
                    Cell cellRazao = sheet.getCell(43, i);
                    Cell cellDataCadastro = sheet.getCell(48, i);
                    Cell cellMunicipioIBGE = sheet.getCell(57, i);
                    Cell cellComplemento = sheet.getCell(59, i);
                    Cell cellNumero = sheet.getCell(60, i);
                    Cell cellBairroCob = sheet.getCell(64, i);
                    Cell cellMunicipioCob = sheet.getCell(65, i);
                    Cell cellUfCob = sheet.getCell(66, i);
                    Cell cellCepCob = sheet.getCell(67, i);
                    Cell cellFoneCob = sheet.getCell(68, i);
                    Cell cellFaxCob = sheet.getCell(69, i);
                    Cell cellEndCod = sheet.getCell(70, i);
                    Cell cellCompCob = sheet.getCell(71, i);
                    Cell cellNumCob = sheet.getCell(72, i);
                    Cell cellBairroEnt = sheet.getCell(73, i);
                    Cell cellMunicipioEnt = sheet.getCell(74, i);
                    Cell cellUfEnt = sheet.getCell(75, i);
                    Cell cellCepEnt = sheet.getCell(76, i);
                    Cell cellFoneEnt = sheet.getCell(77, i);
                    Cell cellFaxEnt = sheet.getCell(78, i);
                    Cell cellEndEnt = sheet.getCell(79, i);
                    Cell cellCompEnt = sheet.getCell(80, i);
                    Cell cellNumEnt = sheet.getCell(81, i);
                    Cell cellPontoRef = sheet.getCell(82, i);
                    Cell cellIdEmpresa = sheet.getCell(90, i);
                    Cell cellSite = sheet.getCell(110, i);

                    if ("F".equals(cellTipo.getContents().trim())) {

                        FornecedorIMP imp = new FornecedorIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(cellCodigo.getContents());
                        imp.setAtivo("S".equals(cellAtivo.getContents()));
                        imp.setRazao(cellRazao.getContents());
                        imp.setFantasia(cellFantasia.getContents());
                        imp.setCnpj_cpf(cellCnpjCpf.getContents());
                        imp.setInsc_municipal(cellinscMunicipal.getContents());
                        imp.setIe_rg(cellIeRg.getContents());
                        imp.setEndereco(cellEndereco.getContents());
                        imp.setNumero(cellNumero.getContents());
                        imp.setComplemento(cellComplemento.getContents());
                        imp.setBairro(cellBairro.getContents());
                        imp.setMunicipio(cellMunicipio.getContents());
                        imp.setCob_ibge_municipio(Utils.stringToInt(cellMunicipioIBGE.getContents()));
                        imp.setUf(cellUf.getContents());
                        imp.setCep(cellCep.getContents());
                        imp.setTel_principal(cellTelefone.getContents());
                        imp.setObservacao(cellObservacao.getContents());
                        imp.setCob_endereco(cellEndCod.getContents());
                        imp.setCob_numero(cellNumCob.getContents());
                        imp.setCob_complemento(cellCompCob.getContents());
                        imp.setCob_bairro(cellBairroCob.getContents());
                        imp.setCob_municipio(cellMunicipioCob.getContents());
                        imp.setCob_uf(cellUfCob.getContents());
                        imp.setCob_cep(cellCepCob.getContents());

                        if (!imp.isAtivo()) {
                            if ((cellMotivoDesativo.getContents() != null)
                                    && (!cellMotivoDesativo.getContents().trim().isEmpty())) {
                                imp.setObservacao(imp.getObservacao() + "; MOTIVO DESATIVACAO - " + cellMotivoDesativo.getContents());
                            }
                        }

                        if ((cellReferencia.getContents() != null)
                                && (!cellReferencia.getContents().trim().isEmpty())) {
                            imp.setObservacao(imp.getObservacao() + "; REFERENCIA - " + cellReferencia.getContents());
                        }

                        if ((cellPontoRef.getContents() != null)
                                && (!cellPontoRef.getContents().trim().isEmpty())) {
                            imp.setObservacao(imp.getObservacao() + "; PONTO REFERENCIA - " + cellPontoRef.getContents());
                        }

                        if ((cellFax.getContents() != null)
                                && (!cellFax.getContents().trim().isEmpty())) {
                            imp.addContato(
                                    "1",
                                    "FAX",
                                    Utils.formataNumero(cellFax.getContents()),
                                    null,
                                    TipoContato.COMERCIAL,
                                    null
                            );
                        }

                        if ((cellEmail.getContents() != null)
                                && (!cellEmail.getContents().trim().isEmpty())) {
                            imp.addContato(
                                    "2",
                                    "EMAIL",
                                    null,
                                    null,
                                    TipoContato.COMERCIAL,
                                    cellEmail.getContents().toLowerCase()
                            );
                        }

                        if ((cellSite.getContents() != null)
                                && (!cellSite.getContents().trim().isEmpty())) {
                            imp.addContato(
                                    "3",
                                    "SITE",
                                    null,
                                    null,
                                    TipoContato.COMERCIAL,
                                    cellSite.getContents().toLowerCase()
                            );
                        }

                        if ((cellFoneCob.getContents() != null)
                                && (!cellFoneCob.getContents().trim().isEmpty())) {
                            imp.addContato(
                                    "4",
                                    "FONE COBRANCA",
                                    Utils.formataNumero(cellFoneCob.getContents()),
                                    null,
                                    TipoContato.COMERCIAL,
                                    null
                            );
                        }

                        if ((cellFaxCob.getContents() != null)
                                && (!cellFaxCob.getContents().trim().isEmpty())) {
                            imp.addContato(
                                    "5",
                                    "FAX COBRANCA",
                                    Utils.formataNumero(cellFaxCob.getContents()),
                                    null,
                                    TipoContato.COMERCIAL,
                                    null
                            );
                        }
                        result.add(imp);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        java.sql.Date dataCadastro, dataNascimento;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//cliente.xls"), settings);
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

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellFantasia = sheet.getCell(1, i);
                    Cell cellEndereco = sheet.getCell(2, i);
                    Cell cellBairro = sheet.getCell(3, i);
                    Cell cellMunicipio = sheet.getCell(4, i);
                    Cell cellUf = sheet.getCell(5, i);
                    Cell cellCep = sheet.getCell(6, i);
                    Cell cellTelefone = sheet.getCell(7, i);
                    Cell cellFax = sheet.getCell(8, i);
                    Cell cellNomePai = sheet.getCell(9, i);
                    Cell cellNomeMae = sheet.getCell(10, i);
                    Cell cellinscMunicipal = sheet.getCell(11, i);
                    Cell cellTrabalho = sheet.getCell(12, i);
                    Cell cellNascimento = sheet.getCell(13, i);
                    Cell cellObservacao = sheet.getCell(14, i);
                    Cell cellCnpjCpf = sheet.getCell(15, i);
                    Cell cellIeRg = sheet.getCell(16, i);
                    Cell cellCargo = sheet.getCell(18, i);
                    Cell cellEmail = sheet.getCell(19, i);
                    Cell cellConjuge = sheet.getCell(21, i);
                    Cell cellReferencia = sheet.getCell(22, i);
                    Cell cellSalario = sheet.getCell(23, i);
                    Cell cellAtivo = sheet.getCell(40, i);
                    Cell cellMotivoDesativo = sheet.getCell(41, i);
                    Cell cellTipo = sheet.getCell(42, i);
                    Cell cellRazao = sheet.getCell(43, i);
                    Cell cellDataCadastro = sheet.getCell(48, i);
                    Cell cellValorLimite = sheet.getCell(54, i);
                    Cell cellMunicipioIBGE = sheet.getCell(57, i);
                    Cell cellComplemento = sheet.getCell(59, i);
                    Cell cellNumero = sheet.getCell(60, i);
                    Cell cellBairroCob = sheet.getCell(64, i);
                    Cell cellMunicipioCob = sheet.getCell(65, i);
                    Cell cellUfCob = sheet.getCell(66, i);
                    Cell cellCepCob = sheet.getCell(67, i);
                    Cell cellFoneCob = sheet.getCell(68, i);
                    Cell cellFaxCob = sheet.getCell(69, i);
                    Cell cellEndCod = sheet.getCell(70, i);
                    Cell cellCompCob = sheet.getCell(71, i);
                    Cell cellNumCob = sheet.getCell(72, i);
                    Cell cellBairroEnt = sheet.getCell(73, i);
                    Cell cellMunicipioEnt = sheet.getCell(74, i);
                    Cell cellUfEnt = sheet.getCell(75, i);
                    Cell cellCepEnt = sheet.getCell(76, i);
                    Cell cellFoneEnt = sheet.getCell(77, i);
                    Cell cellFaxEnt = sheet.getCell(78, i);
                    Cell cellEndEnt = sheet.getCell(79, i);
                    Cell cellCompEnt = sheet.getCell(80, i);
                    Cell cellNumEnt = sheet.getCell(81, i);
                    Cell cellPontoRef = sheet.getCell(82, i);
                    Cell cellCliEspecial = sheet.getCell(83, i);
                    Cell cellCrediario = sheet.getCell(85, i);
                    Cell cellIdEmpresa = sheet.getCell(90, i);
                    Cell cellSite = sheet.getCell(110, i);

                    if ("C".equals(cellTipo.getContents())) {

                        if ((cellDataCadastro.getContents() != null)
                                && (!cellDataCadastro.getContents().trim().isEmpty())) {
                            dataCadastro = new java.sql.Date(fmt.parse(cellDataCadastro.getContents()).getTime());
                        } else {
                            dataCadastro = new Date(new java.util.Date().getTime());
                        }

                        if ((cellNascimento.getContents() != null)
                                && (!cellNascimento.getContents().trim().isEmpty())) {
                            dataNascimento = new java.sql.Date(fmt.parse(cellNascimento.getContents()).getTime());
                        } else {
                            dataNascimento = null;
                        }

                        ClienteIMP imp = new ClienteIMP();
                        imp.setId(cellCodigo.getContents());
                        imp.setAtivo("S".equals(cellAtivo.getContents()));
                        imp.setRazao(cellRazao.getContents());
                        imp.setFantasia(cellFantasia.getContents());
                        imp.setCnpj(cellCnpjCpf.getContents());
                        imp.setInscricaoestadual(cellIeRg.getContents());
                        imp.setInscricaoMunicipal(cellinscMunicipal.getContents());
                        imp.setEndereco(cellEndereco.getContents());
                        imp.setNumero(cellNumero.getContents());
                        imp.setComplemento(cellComplemento.getContents());
                        imp.setBairro(cellBairro.getContents());
                        imp.setMunicipio(cellMunicipio.getContents());
                        imp.setMunicipioIBGE(Utils.stringToInt(cellMunicipioIBGE.getContents()));
                        imp.setUf(cellUf.getContents());
                        imp.setCep(cellCep.getContents());
                        imp.setTelefone(cellTelefone.getContents());
                        imp.setEmail(cellEmail.getContents());
                        imp.setFax(cellFax.getContents());
                        imp.setNomePai(cellNomePai.getContents());
                        imp.setNomeMae(cellNomeMae.getContents());
                        imp.setNomeConjuge(cellConjuge.getContents());
                        imp.setEmpresa(cellTrabalho.getContents());
                        imp.setCargo(cellCargo.getContents());
                        imp.setSalario(Double.parseDouble(cellSalario.getContents()));
                        imp.setValorLimite(Double.parseDouble(cellValorLimite.getContents()));
                        imp.setDataCadastro(dataCadastro);
                        imp.setDataNascimento(dataNascimento);
                        imp.setObservacao(cellObservacao.getContents());
                        imp.setCobrancaEndereco(cellEndCod.getContents());
                        imp.setCobrancaNumero(cellNumCob.getContents());
                        imp.setCobrancaComplemento(cellCompCob.getContents());
                        imp.setCobrancaBairro(cellBairroCob.getContents());
                        imp.setCobrancaMunicipio(cellMunicipioCob.getContents());
                        imp.setCobrancaUf(cellUfCob.getContents());
                        imp.setCobrancaCep(cellCepCob.getContents());
                        imp.setCobrancaTelefone(cellFoneCob.getContents());

                        if (!imp.isAtivo()) {
                            if ((cellMotivoDesativo.getContents() != null)
                                    && (!cellMotivoDesativo.getContents().trim().isEmpty())) {
                                imp.setObservacao2("MOTIVO DESATIVACAO - " + cellMotivoDesativo.getContents());
                            }
                        }

                        if ((cellCliEspecial.getContents() != null)
                                && (!cellCliEspecial.getContents().trim().isEmpty())) {

                            if ("S".equals(cellCliEspecial.getContents().trim())) {
                                imp.setObservacao2(imp.getObservacao2() + "; CLIENTE ESPECIAL");
                            }
                        }

                        if ((cellCrediario.getContents() != null)
                                && (!cellCrediario.getContents().trim().isEmpty())) {

                            if ("S".equals(cellCrediario.getContents().trim())) {
                                imp.setObservacao2(imp.getObservacao2() + "; CLIENTE CREDIARIO");
                            }
                        }

                        if ((cellPontoRef.getContents() != null)
                                && (!cellPontoRef.getContents().trim().isEmpty())) {
                            imp.setObservacao2(imp.getObservacao2() + "; PONTO REFERENCIA - " + cellPontoRef.getContents());
                        }

                        if ((cellReferencia.getContents() != null)
                                && (!cellReferencia.getContents().trim().isEmpty())) {
                            imp.setObservacao2(imp.getObservacao2() + "; REFERENCIA - " + cellReferencia.getContents());
                        }

                        if ((cellFaxCob.getContents() != null)
                                && (!cellFaxCob.getContents().trim().isEmpty())) {
                            imp.addContato(
                                    "1",
                                    "FAX COBRANCA",
                                    Utils.formataNumero(cellFaxCob.getContents().trim()),
                                    null,
                                    null
                            );
                        }

                        if ((cellSite.getContents() != null)
                                && (!cellSite.getContents().trim().isEmpty())) {
                            imp.addContato(
                                    "2",
                                    "SITE",
                                    null,
                                    null,
                                    cellSite.getContents().toLowerCase()
                            );
                        }
                        result.add(imp);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        java.sql.Date dataEmissao, dataVencimento;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//cReceber.xls"), settings);
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

                    Cell cellIdVenda = sheet.getCell(17, i);
                    Cell cellCodCliente = sheet.getCell(2, i);
                    Cell cellEmissao = sheet.getCell(5, i);
                    Cell cellVencimento = sheet.getCell(6, i);
                    Cell cellValor = sheet.getCell(7, i);
                    Cell cellHistorico = sheet.getCell(8, i);
                    Cell cellJuros = sheet.getCell(15, i);
                    Cell cellDesconto = sheet.getCell(16, i);
                    Cell cellCaixa = sheet.getCell(23, i);
                    Cell cellCupom = sheet.getCell(24, i);

                    if ((cellEmissao.getContents() != null)
                            && (!cellEmissao.getContents().trim().isEmpty())) {
                        dataEmissao = new java.sql.Date(fmt.parse(cellEmissao.getContents()).getTime());
                    } else {
                        dataEmissao = new Date(new java.util.Date().getTime());
                    }

                    if ((cellVencimento.getContents() != null)
                            && (!cellVencimento.getContents().trim().isEmpty())) {
                        dataVencimento = new java.sql.Date(fmt.parse(cellVencimento.getContents()).getTime());
                    } else {
                        dataVencimento = new Date(new java.util.Date().getTime());
                    }

                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(cellIdVenda.getContents());
                    imp.setIdCliente(cellCodCliente.getContents());
                    imp.setDataEmissao(dataEmissao);
                    imp.setDataVencimento(dataVencimento);
                    imp.setValor(Double.parseDouble(cellValor.getContents()));
                    imp.setJuros(Double.parseDouble(cellJuros.getContents()));
                    imp.setNumeroCupom(cellCupom.getContents());
                    imp.setEcf(cellCaixa.getContents());
                    imp.setObservacao(cellHistorico.getContents());
                    result.add(imp);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        java.sql.Date dataEmissao, dataDeposito;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//cheque.xls"), settings);
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

                    Cell cellIdCheque = sheet.getCell(0, i);
                    Cell cellNCheque = sheet.getCell(1, i);
                    Cell cellBanco = sheet.getCell(4, i);
                    Cell cellValor = sheet.getCell(5, i);
                    Cell cellVencto = sheet.getCell(6, i);
                    Cell cellDevolvido = sheet.getCell(7, i);
                    Cell cellCaixa = sheet.getCell(8, i);
                    Cell cellCupom = sheet.getCell(9, i);
                    Cell cellEmissao = sheet.getCell(10, i);
                    Cell cellObservacao = sheet.getCell(12, i);
                    Cell cellCmc7 = sheet.getCell(17, i);
                    Cell cellIdCliente = sheet.getCell(21, i);

                    if ((cellEmissao.getContents() != null)
                            && (!cellEmissao.getContents().trim().isEmpty())) {
                        dataEmissao = new java.sql.Date(fmt.parse(cellEmissao.getContents()).getTime());
                    } else {
                        dataEmissao = new Date(new java.util.Date().getTime());
                    }

                    if ((cellVencto.getContents() != null)
                            && (!cellVencto.getContents().trim().isEmpty())) {
                        dataDeposito = new java.sql.Date(fmt.parse(cellVencto.getContents()).getTime());
                    } else {
                        dataDeposito = new Date(new java.util.Date().getTime());
                    }

                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(cellIdCheque.getContents());
                    imp.setDate(dataEmissao);
                    imp.setDataDeposito(dataDeposito);
                    imp.setNumeroCheque(cellNCheque.getContents());
                    imp.setValor(Double.parseDouble(cellValor.getContents()));
                    imp.setEcf(cellCaixa.getContents());
                    imp.setNumeroCupom(cellCupom.getContents());
                    imp.setObservacao(cellObservacao.getContents());
                    imp.setCmc7(cellCmc7.getContents());
                    imp.setAlinea("S".equals(cellDevolvido.getContents()) ? 0 : 11);
                    imp.setBanco(Utils.stringToInt(cellBanco.getContents()));
                    result.add(imp);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<OfertaIMP> getOfertas(java.util.Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        java.sql.Date dataFimOferta, dataInicioOferta;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//produto.xls"), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        Calendar c = Calendar.getInstance();

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdProduto = sheet.getCell(0, i);
                    Cell cellPrecoOferta = sheet.getCell(13, i);
                    Cell cellFimOferta = sheet.getCell(57, i);
                    Cell cellInicioOferta = sheet.getCell(71, i);

                    if ((cellInicioOferta.getContents() != null)
                            && (!cellInicioOferta.getContents().trim().isEmpty())
                            && (!cellInicioOferta.getContents().contains("-"))
                            && (cellFimOferta.getContents() != null)
                            && (!cellFimOferta.getContents().trim().isEmpty())
                            && (!cellFimOferta.getContents().contains("-"))) {

                        if ((cellFimOferta.getContents() != null)
                                && (!cellFimOferta.getContents().trim().isEmpty())) {
                            dataFimOferta = new java.sql.Date(fmt.parse(cellFimOferta.getContents()).getTime());
                        } else {
                            dataFimOferta = new Date(new java.util.Date().getTime());
                        }

                        dataInicioOferta = new Date(new java.util.Date().getTime());

                        if (dataFimOferta.after(dataInicioOferta)) {
                            OfertaIMP imp = new OfertaIMP();
                            imp.setTipoOferta(TipoOfertaVO.CAPA);
                            imp.setIdProduto(cellIdProduto.getContents());
                            imp.setPrecoOferta(Double.parseDouble(cellPrecoOferta.getContents()));
                            imp.setDataInicio(dataInicioOferta);
                            imp.setDataFim(dataFimOferta);
                            result.add(imp);
                        }
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
