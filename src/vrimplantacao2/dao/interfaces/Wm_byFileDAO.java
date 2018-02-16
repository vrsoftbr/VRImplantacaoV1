/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.File;
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
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.mercadologico.MercadologicoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class Wm_byFileDAO extends InterfaceDAO implements MapaTributoProvider {

    public String v_arquivoXls;

    @Override
    public String getSistema() {
        return "Wm_byFile";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
        Sheet[] sheets = arquivo.getSheets();

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);

            for (int i = 0; i < sheet.getRows(); i++) {

                Cell cellIcms = sheet.getCell(0, i);
                result.add(new MapaTributoIMP(cellIcms.getContents(), cellIcms.getContents()));
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
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
                Cell cellDescricao = sheet.getCell(1, i);

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
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        ArrayList<String> mercadologico1 = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
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
                Cell cellNivel = sheet.getCell(1, i);
                Cell cellDescricao = sheet.getCell(2, i);
                Cell cellPai = sheet.getCell(5, i);

                MercadologicoNivelIMP imp = new MercadologicoNivelIMP();

                if (null != cellNivel.getContents().trim()) {
                    switch (cellNivel.getContents().trim()) {
                        case "1":
                            mercadologico1 = new ArrayList<>();
                            imp.setId(cellCodigo.getContents());
                            imp.setDescricao(cellDescricao.getContents());
                            merc.put(imp.getId(), imp);
                            mercadologico1.add(imp.getId());
                            break;
                        case "2": {
                            MercadologicoNivelIMP merc1 = merc.get(cellPai.getContents());
                            if (merc1 != null) {
                                merc1.addFilho(
                                        cellCodigo.getContents(),
                                        cellDescricao.getContents()
                                );
                            }
                            break;
                        }
                        case "3": {
                            MercadologicoNivelIMP merc1 = merc.get(new MercadologicoAnteriorDAO().getCodMerc1(getSistema(), getLojaOrigem(), cellPai.getContents()));
                            if (merc1 != null) {
                                MercadologicoNivelIMP merc2 = merc1.getNiveis().get(cellPai.getContents());
                                if (merc2 != null) {
                                    merc2.addFilho(
                                            cellCodigo.getContents(),
                                            cellDescricao.getContents()
                                    );
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        java.sql.Date dataCadastro;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            linha = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                linha++;
                if (linha == 1) {
                    continue;
                }

                Cell cellCodigo = sheet.getCell(0, i);
                Cell cellIdFamiliaProduto = sheet.getCell(1, i);
                Cell cellDescricaoCompleta = sheet.getCell(2, i);
                Cell cellDescricaoReduzida = sheet.getCell(3, i);
                Cell cellValidade = sheet.getCell(6, i);
                Cell cellSituacaoCadastro = sheet.getCell(10, i);
                Cell cellNcm = sheet.getCell(12, i);
                Cell cellDataCadastro = sheet.getCell(14, i);
                Cell cellTipoEmbalagem = sheet.getCell(19, i);

                dataCadastro = new java.sql.Date(fmt.parse(cellDataCadastro.getContents().replace("-", "/")).getTime());

                ProdutoIMP imp = new ProdutoIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(cellCodigo.getContents());
                imp.setDescricaoCompleta(cellDescricaoCompleta.getContents());
                imp.setDescricaoReduzida(cellDescricaoReduzida.getContents());
                imp.setDescricaoGondola(imp.getDescricaoCompleta());
                imp.setIdFamiliaProduto(cellIdFamiliaProduto.getContents());
                imp.setValidade(Integer.parseInt(cellValidade.getContents()));
                imp.setSituacaoCadastro(cellSituacaoCadastro.getContents().contains("N") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                imp.setNcm(cellNcm.getContents());
                imp.setTipoEmbalagem(cellTipoEmbalagem.getContents().contains("QUILO") ? "KG" : cellTipoEmbalagem.getContents());
                imp.setDataCadastro(dataCadastro);

                ProdutoBalancaVO produtoBalanca;
                long codigoProduto;
                codigoProduto = Long.parseLong(imp.getImportId());
                if (codigoProduto <= Integer.MAX_VALUE) {
                    produtoBalanca = produtosBalanca.get((int) codigoProduto);
                } else {
                    produtoBalanca = null;
                }
                if (produtoBalanca != null) {
                    imp.seteBalanca(true);
                } else {
                    imp.seteBalanca(false);
                }
                result.add(imp);
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        if (opcao == OpcaoProduto.CUSTO) {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellCustoProduto = sheet.getCell(1, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setCustoComImposto(Double.parseDouble(cellCustoProduto.getContents()));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.PRECO) {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellPrecoProduto = sheet.getCell(2, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setPrecovenda(Double.parseDouble(cellPrecoProduto.getContents()));
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.MARGEM) {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellMargemProduto = sheet.getCell(3, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setMargem(Double.parseDouble(cellMargemProduto.getContents()));
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.ESTOQUE) {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellEstoqueProduto = sheet.getCell(4, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setEstoque(Double.parseDouble(cellEstoqueProduto.getContents()));
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.NATUREZA_RECEITA) {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellNaturezaReceita = sheet.getCell(5, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setPiscofinsNaturezaReceita(cellNaturezaReceita.getContents());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.PIS_COFINS) {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellCstPisCofins = sheet.getCell(48, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setPiscofinsCstDebito(cellCstPisCofins.getContents());
                    imp.setPiscofinsCstCredito(cellCstPisCofins.getContents());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.CEST) {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellCest = sheet.getCell(60, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setCest(cellCest.getContents());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.ICMS) {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellIcms = sheet.getCell(6, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setIcmsDebitoId(cellIcms.getContents());
                    imp.setIcmsCreditoId(cellIcms.getContents());
                    result.add(imp);
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
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

                Cell cellIdProduto = sheet.getCell(0, i);
                Cell cellEan = sheet.getCell(1, i);

                if ((cellEan.getContents() != null)
                        && (!cellEan.getContents().trim().isEmpty())) {

                    if (Long.parseLong(Utils.formataNumero(cellEan.getContents())) > 999999) {

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(cellIdProduto.getContents());
                        imp.setEan(Utils.formataNumero(cellEan.getContents()));
                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
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
                Cell cellProdRural = sheet.getCell(2, i);
                Cell cellAtivo = sheet.getCell(3, i);
                Cell cellObservacao = sheet.getCell(4, i);
                Cell cellPeriodoVisita = sheet.getCell(8, i);
                Cell cellPrazoEntrega = sheet.getCell(9, i);
                Cell cellFantasia = sheet.getCell(38, i);
                Cell cellRazao = sheet.getCell(82, i);
                Cell cellEndereco = sheet.getCell(83, i);
                Cell cellNumero = sheet.getCell(84, i);
                Cell cellBairro = sheet.getCell(85, i);
                Cell CellMunicipio = sheet.getCell(86, i);
                Cell cellUf = sheet.getCell(87, i);
                Cell cellEmail = sheet.getCell(89, i);
                Cell cellTelefone = sheet.getCell(90, i);
                Cell cellFax = sheet.getCell(91, i);

                FornecedorIMP imp = new FornecedorIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(cellCodigo.getContents());
                imp.setRazao(cellRazao.getContents());
                imp.setFantasia(cellFantasia.getContents());
                imp.setEndereco(cellEndereco.getContents());
                imp.setNumero(cellNumero.getContents());
                imp.setBairro(cellBairro.getContents());
                imp.setMunicipio(CellMunicipio.getContents());
                imp.setUf(cellUf.getContents());
                imp.setObservacao(cellObservacao.getContents());
                imp.setTel_principal(cellTelefone.getContents());
                imp.setAtivo("A".equals(cellAtivo.getContents()));
                imp.setPrazoEntrega(Integer.parseInt(cellPrazoEntrega.getContents()));
                imp.setPrazoVisita(Integer.parseInt(cellPeriodoVisita.getContents()));

                if ((cellEmail.getContents() != null)
                        && (!cellEmail.getContents().trim().isEmpty())) {
                    imp.addContato(
                            "1",
                            "EMAIL",
                            null,
                            TipoContato.COMERCIAL,
                            cellEmail.getContents().toLowerCase()
                    );
                }
                if ((cellFax.getContents() != null)
                        && (!cellFax.getContents().trim().isEmpty())) {
                    imp.addContato(
                            "2",
                            "FAX",
                            cellFax.getContents(),
                            TipoContato.COMERCIAL,
                            null
                    );
                }
                result.add(imp);
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores(OpcaoFornecedor opcao) throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        if (opcao == OpcaoFornecedor.CNPJ_CPF) {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellCnpj = sheet.getCell(81, i);

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setCnpj_cpf(cellCnpj.getContents());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoFornecedor.INSCRICAO_ESTADUAL) {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellInscEstadual = sheet.getCell(91, i);

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setIe_rg(cellInscEstadual.getContents());
                    result.add(imp);
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
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

                Cell cellIdProduto = sheet.getCell(0, i);
                Cell cellCodigoExterno = sheet.getCell(1, i);
                Cell cellQtdEmbalagem = sheet.getCell(3, i);
                Cell cellIdFornecedor = sheet.getCell(4, i);

                if ((cellIdFornecedor.getContents() != null)
                        && (!cellIdFornecedor.getContents().trim().isEmpty())) {

                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setIdFornecedor(cellIdFornecedor.getContents());
                    imp.setIdProduto(cellIdProduto.getContents());
                    imp.setCodigoExterno(cellCodigoExterno.getContents());
                    imp.setQtdEmbalagem(Double.parseDouble(cellQtdEmbalagem.getContents()));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
        Sheet[] sheets = arquivo.getSheets();
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        java.sql.Date dataCadastro, dataNascimento;
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
                Cell cellRg = sheet.getCell(5, i);
                Cell cellPai = sheet.getCell(10, i);
                Cell cellMae = sheet.getCell(11, i);
                Cell cellSexo = sheet.getCell(12, i);
                Cell cellEstCivil = sheet.getCell(16, i);
                Cell cellConjuge = sheet.getCell(17, i);
                Cell cellSalario = sheet.getCell(18, i);
                Cell cellObservacao = sheet.getCell(27, i);
                Cell cellLimite = sheet.getCell(39, i);
                Cell cellNome = sheet.getCell(93, i);
                Cell cellComplemento = sheet.getCell(94, i);
                Cell cellDDDCelular = sheet.getCell(95, i);
                Cell cellTelefone = sheet.getCell(96, i);
                Cell cellEmail = sheet.getCell(97, i);
                Cell cellCelular = sheet.getCell(98, i);
                Cell cellUf = sheet.getCell(105, i);
                Cell cellDataNascimento = sheet.getCell(106, i);
                Cell cellCep = sheet.getCell(109, i);
                Cell cellDDD = sheet.getCell(110, i);
                Cell cellDDD2 = sheet.getCell(111, i);
                Cell cellTelefone2 = sheet.getCell(112, i);
                Cell cellDDDFax = sheet.getCell(113, i);
                Cell cellFax = sheet.getCell(114, i);
                Cell cellDataCadastro = sheet.getCell(116, i);
                Cell cellNumero = sheet.getCell(119, i);
                Cell cellCnpj = sheet.getCell(120, i);
                Cell cellDDD3 = sheet.getCell(123, i);
                Cell cellTelefone3 = sheet.getCell(124, i);
                Cell cellDDD4 = sheet.getCell(125, i);
                Cell cellTelefone4 = sheet.getCell(126, i);

                dataCadastro = new java.sql.Date(fmt.parse(cellDataCadastro.getContents().replace("-", "/")).getTime());
                dataNascimento = new java.sql.Date(fmt.parse(cellDataNascimento.getContents().replace("-", "/")).getTime());

                ClienteIMP imp = new ClienteIMP();
                imp.setId(cellCodigo.getContents());
                imp.setRazao(cellNome.getContents());
                imp.setComplemento(cellComplemento.getContents());
                imp.setNumero(cellNumero.getContents());
                imp.setCep(cellCep.getContents());
                imp.setCnpj(cellCnpj.getContents());
                imp.setInscricaoestadual(cellRg.getContents());
                imp.setNomePai(cellPai.getContents());
                imp.setNomeMae(cellMae.getContents());
                imp.setNomeConjuge(cellConjuge.getContents());
                imp.setTelefone(cellDDD.getContents() + cellTelefone.getContents());
                imp.setEmail(cellEmail.getContents().trim() == null ? "" : cellEmail.getContents().toLowerCase());
                imp.setCelular(cellDDDCelular.getContents() + cellCelular.getContents());
                imp.setUf(cellUf.getContents());
                imp.setSalario(Double.parseDouble(cellSalario.getContents()));
                imp.setValorLimite(Double.parseDouble(cellLimite.getContents()));
                imp.setObservacao(cellObservacao.getContents());
                imp.setDataCadastro(dataCadastro);
                imp.setDataNascimento(dataNascimento);

                if ((cellSexo.getContents() != null)
                        && (!cellSexo.getContents().trim().isEmpty())) {
                    if ("Feminino".equals(cellSexo.getContents())) {
                        imp.setSexo(TipoSexo.FEMININO);
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }
                } else {
                    imp.setSexo(TipoSexo.MASCULINO);
                }

                if ((cellEstCivil.getContents() != null)
                        && (!cellEstCivil.getContents().trim().isEmpty())) {

                    if (cellEstCivil.getContents().contains("Casa")) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else if (cellEstCivil.getContents().contains("Solt")) {
                        imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }
                } else {
                    imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                }

                if ((cellTelefone2.getContents() != null)
                        && (!cellTelefone2.getContents().trim().isEmpty())) {
                    imp.addContato(
                            "1",
                            "TELEFONE 2",
                            cellDDD2.getContents() + cellTelefone2.getContents(),
                            null,
                            null
                    );
                }
                if ((cellTelefone3.getContents() != null)
                        && (!cellTelefone3.getContents().trim().isEmpty())) {
                    imp.addContato(
                            "2",
                            "TELEFONE 3",
                            cellDDD3.getContents() + cellTelefone3.getContents(),
                            null,
                            null
                    );
                }
                if ((cellTelefone4.getContents() != null)
                        && (!cellTelefone4.getContents().trim().isEmpty())) {
                    imp.addContato(
                            "3",
                            "TELEFONE 4",
                            cellDDD4.getContents() + cellTelefone4.getContents(),
                            null,
                            null
                    );
                }
                if ((cellFax.getContents() != null)
                        && (!cellFax.getContents().trim().isEmpty())) {
                    imp.addContato(
                            "4",
                            "FAX",
                            cellDDDFax.getContents() + cellFax.getContents(),
                            null,
                            null
                    );
                }
                result.add(imp);
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes(OpcaoCliente opcao) throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        if (opcao == OpcaoCliente.ENDERECO) {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdCliente = sheet.getCell(0, i);
                    Cell cellEndereco = sheet.getCell(94, i);
                    Cell cellNumero = sheet.getCell(95, i);
                    Cell cellBairro = sheet.getCell(96, i);
                    Cell cellMunicipio = sheet.getCell(97, i);
                    Cell cellUf = sheet.getCell(98, i);

                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(cellIdCliente.getContents());
                    imp.setEndereco(cellEndereco.getContents());
                    imp.setNumero(cellNumero.getContents());
                    imp.setBairro(cellBairro.getContents());
                    imp.setMunicipio(cellMunicipio.getContents());
                    imp.setUf(cellUf.getContents());
                    result.add(imp);
                }
            }
            return result;
        }
        return null;
    }
}
