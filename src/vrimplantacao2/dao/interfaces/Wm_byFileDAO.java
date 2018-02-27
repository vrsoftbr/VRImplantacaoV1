/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.Date;
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
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
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
    public String v_arquivoXlsCompl;
    public String v_arquivoXlsForn;
    public String v_arquivoXlsFornCompl;
    public String v_arquivoXlsProdForn;
    public String v_arquivoXlsCliente;
    public String v_arquivoXlsCliCompl;
    public String v_arquivoXlsCreditoRotativo;

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
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        List<String> mercs = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        String mercadologico1 = "";

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

                if ("1".equals(cellNivel.getContents().trim())) {

                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(cellCodigo.getContents());
                    imp.setDescricao(cellDescricao.getContents());

                    merc.put(imp.getId(), imp);
                }
                if ("2".equals(cellNivel.getContents().trim())) {

                    mercs.add(cellCodigo.getContents() + ";" + cellPai.getContents());

                    MercadologicoNivelIMP merc1 = merc.get(cellPai.getContents());
                    if (merc1 != null) {
                        merc1.addFilho(
                                cellCodigo.getContents(),
                                cellDescricao.getContents()
                        );
                    }
                }
                if ("3".equals(cellNivel.getContents().trim())) {
                    for (int j = 0; j < mercs.size(); j++) {
                        String[] mercs1 = mercs.get(j).split(";");
                        String merca1 = "", merca2 = "";

                        for (int k = 0; k < mercs1.length; k++) {

                            switch (k) {
                                case 0:
                                    merca2 = mercs1[k];
                                    break;
                                case 1:
                                    merca1 = mercs1[k];
                                    break;
                            }

                            if (merca2.equals(cellPai.getContents())) {
                                mercadologico1 = merca1;
                                MercadologicoNivelIMP merc1 = merc.get(mercadologico1);
                                if (merc1 != null) {
                                    MercadologicoNivelIMP merc2 = merc1.getNiveis().get(cellPai.getContents());
                                    if (merc2 != null) {
                                        merc2.addFilho(
                                                cellCodigo.getContents(),
                                                cellDescricao.getContents()
                                        );
                                    }
                                }
                            }
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
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
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
                imp.setEan(getCodigoBarrasBalanca(cellCodigo.getContents()));
                imp.setDescricaoCompleta(cellDescricaoCompleta.getContents());
                imp.setDescricaoReduzida(cellDescricaoReduzida.getContents());
                imp.setDescricaoGondola(imp.getDescricaoCompleta());
                imp.setIdFamiliaProduto(cellIdFamiliaProduto.getContents());
                imp.setValidade(Integer.parseInt("".equals(cellValidade.getContents()) ? "0" : cellValidade.getContents()));
                imp.setSituacaoCadastro(cellSituacaoCadastro.getContents().contains("N") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                imp.setNcm(cellNcm.getContents());
                imp.setTipoEmbalagem(cellTipoEmbalagem.getContents());
                imp.setDataCadastro(dataCadastro);

                ProdutoBalancaVO produtoBalanca;
                long codigoProduto;
                codigoProduto = Long.parseLong(imp.getEan());
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
        if (opcao == OpcaoProduto.CUSTO) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCompl), settings);
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

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellCustoProduto = sheet.getCell(1, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setCustoComImposto(Double.parseDouble(cellCustoProduto.getContents().replace(".", "").replace(",", ".")));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.PRECO) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCompl), settings);
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

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellPrecoProduto = sheet.getCell(2, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setPrecovenda(Double.parseDouble(cellPrecoProduto.getContents().replace(".", "").replace(",", ".")));
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.MARGEM) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCompl), settings);
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

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellMargemProduto = sheet.getCell(3, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setMargem(Double.parseDouble(cellMargemProduto.getContents().replace(".", "").replace(",", ".")));
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCompl), settings);
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

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellEstoqueProduto = sheet.getCell(4, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigoProduto.getContents());
                    imp.setEstoque(Double.parseDouble(cellEstoqueProduto.getContents().replace(".", "").replace(",", ".")));
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.NATUREZA_RECEITA) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCompl), settings);
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
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCompl), settings);
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
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCompl), settings);
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
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCompl), settings);
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
        } else if (opcao == OpcaoProduto.MERCADOLOGICO) {
            //25
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCompl), settings);
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

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellMercadologico = sheet.getCell(25, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(getProdutoFamilia(cellCodigoProduto.getContents()));

                    String merc = getMercadologicoAnterior(cellMercadologico.getContents());
                    String[] cods = merc.split(";");

                    for (int j = 0; j < cods.length; j++) {
                        switch (j) {
                            case 0:
                                imp.setCodMercadologico1(cods[j]);
                                break;
                            case 1:
                                imp.setCodMercadologico2(cods[j]);
                                break;
                            case 2:
                                imp.setCodMercadologico3(cods[j]);
                                break;
                        }
                    }
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
                Cell cellTipo = sheet.getCell(2, i);

                if ((cellEan.getContents() != null)
                        && (!cellEan.getContents().trim().isEmpty())
                        && (cellTipo.getContents().contains("E"))) {

                    if (Long.parseLong(Utils.formataNumero(cellEan.getContents())) > 999999) {

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(cellIdProduto.getContents());
                        imp.setEan(Utils.formataNumero(cellEan.getContents()));
                        result.add(imp);
                    }
                }

                if ((cellEan.getContents() != null)
                        && (!cellEan.getContents().trim().isEmpty())
                        && (cellTipo.getContents().contains("F"))) {

                    if (Long.parseLong(Utils.formataNumero(cellEan.getContents())) > 999999999) {

                        if (("789".equals(cellEan.getContents().trim().substring(0, 3)))
                                || ("1789".equals(cellEan.getContents().trim().substring(0, 4)))
                                || ("2789".equals(cellEan.getContents().trim().substring(0, 4)))
                                || ("3789".equals(cellEan.getContents().trim().substring(0, 4)))
                                || ("4789".equals(cellEan.getContents().trim().substring(0, 4)))
                                || ("5789".equals(cellEan.getContents().trim().substring(0, 4)))
                                || ("6789".equals(cellEan.getContents().trim().substring(0, 4)))
                                || ("7789".equals(cellEan.getContents().trim().substring(0, 4)))
                                || ("8789".equals(cellEan.getContents().trim().substring(0, 4)))
                                || ("9789".equals(cellEan.getContents().trim().substring(0, 4)))) {

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
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsForn), settings);
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

        if (opcao == OpcaoFornecedor.CNPJ_CPF) {
            List<FornecedorIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsFornCompl), settings);
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
                    Cell cellCnpj = sheet.getCell(81, i);

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setCnpj_cpf(cellCnpj.getContents());
                    result.add(imp);
                    System.out.println("CodForn: " + imp.getImportId() + " Cnpj: " + imp.getCnpj_cpf());
                }
            }
            return result;
        } else if (opcao == OpcaoFornecedor.INSCRICAO_ESTADUAL) {
            List<FornecedorIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsFornCompl), settings);
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
        } else if (opcao == OpcaoFornecedor.MUNICIPIO) {
            List<FornecedorIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsFornCompl), settings);
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
                    Cell CellMunicipio = sheet.getCell(86, i);
                    Cell cellUf = sheet.getCell(87, i);

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setMunicipio(CellMunicipio.getContents());
                    imp.setUf(cellUf.getContents());
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
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsProdForn), settings);
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
                Cell cellTipo = sheet.getCell(2, i);
                Cell cellQtdEmbalagem = sheet.getCell(3, i);
                Cell cellIdFornecedor = sheet.getCell(4, i);

                if ((cellIdFornecedor.getContents() != null)
                        && (!cellIdFornecedor.getContents().trim().isEmpty())
                        && (cellTipo.getContents().contains("F"))) {

                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(cellIdFornecedor.getContents().trim());
                    imp.setIdProduto(cellIdProduto.getContents().trim());
                    imp.setCodigoExterno(cellCodigoExterno.getContents());
                    imp.setQtdEmbalagem(Double.parseDouble(cellQtdEmbalagem.getContents().replace(".", "").replace(",", ".")));
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
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCliente), settings);
        Sheet[] sheets = arquivo.getSheets();
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
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

                if ((cellDataCadastro.getContents() != null)
                        && (!cellDataCadastro.getContents().trim().isEmpty())) {
                    dataCadastro = new java.sql.Date(fmt.parse(cellDataCadastro.getContents().replace("-", "/")).getTime());
                } else {
                    dataCadastro = new Date(new java.util.Date().getTime());
                }

                if ((cellDataNascimento.getContents() != null)
                        && (!cellDataNascimento.getContents().trim().isEmpty())) {
                    dataNascimento = new java.sql.Date(fmt.parse(cellDataNascimento.getContents().replace("-", "/")).getTime());
                } else {
                    dataNascimento = null;
                }

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

                if ((cellSalario.getContents() != null)
                        && (!cellSalario.getContents().trim().isEmpty())) {
                    imp.setSalario(Double.parseDouble(cellSalario.getContents().replace(".", "").replace(",", ".")));
                } else {
                    imp.setSalario(0);
                }

                if ((cellLimite.getContents() != null)
                        && (!cellLimite.getContents().trim().isEmpty())) {
                    imp.setValorLimite(Double.parseDouble(cellLimite.getContents().replace(".", "").replace(",", ".")));
                } else {
                    imp.setValorLimite(0);
                }
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
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCliCompl), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        if (opcao == OpcaoCliente.ENDERECO_COMPLETO) {

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

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsCreditoRotativo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        java.sql.Date dataEmissao, dataVencimento;

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            linha = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                linha++;
                if (linha == 1) {
                    continue;
                }

                Cell cellId = sheet.getCell(0, i);
                Cell cellDataEmissao = sheet.getCell(2, i);
                Cell cellDataVencimento = sheet.getCell(18, i);
                Cell cellValor = sheet.getCell(4, i);
                Cell cellIdCliente = sheet.getCell(3, i);
                Cell cellNumeroCupom = sheet.getCell(9, i);
                Cell cellPdv = sheet.getCell(8, i);
                Cell cellSituacao = sheet.getCell(20, i);

                dataEmissao = new java.sql.Date(fmt.parse(cellDataEmissao.getContents().replace("-", "/")).getTime());
                dataVencimento = new java.sql.Date(fmt.parse(cellDataVencimento.getContents().replace("-", "/")).getTime());

                if ("A".equals(cellSituacao.getContents().trim())) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(cellId.getContents());
                    imp.setIdCliente(cellIdCliente.getContents());
                    imp.setNumeroCupom(cellNumeroCupom.getContents());
                    imp.setEcf(cellPdv.getContents());
                    imp.setValor(Double.parseDouble(cellValor.getContents().replace(".", "").replace(",", ".")));
                    imp.setDataEmissao(dataEmissao);
                    imp.setDataVencimento(dataVencimento);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public void gravarCodigoBarrasxBalanca(String i_arquivo) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        Statement stm = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("create table implantacao.codbarras_balanca ( "
                    + "codigo_produto character varying(20), "
                    + "codigo_barras character varying(20) ) ");
            stm.execute(sql.toString());

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
                    Cell cellTipo = sheet.getCell(2, i);

                    if (cellTipo.getContents().contains("E")) {
                        sql = new StringBuilder();
                        sql.append("insert into implantacao.codbarras_balanca ("
                                + "codigo_produto, codigo_barras ) "
                                + "values ("
                                + "'" + cellIdProduto.getContents().trim() + "', "
                                + "'" + Utils.formataNumero(cellEan.getContents().trim()) + "')");
                        stm.execute(sql.toString());
                    }
                    System.out.println(i);
                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void gravarCodProdCodFam(String i_arquivo) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        Statement stm = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("create table implantacao.produto_familia ( "
                    + "codigo_produto character varying(20), "
                    + "codigo_familia character varying(20) ) ");
            stm.execute(sql.toString());

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {

                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdProduto = sheet.getCell(0, i);
                    Cell cellIdFamilia = sheet.getCell(1, i);

                    sql = new StringBuilder();
                    sql.append("insert into implantacao.produto_familia ("
                            + "codigo_produto, codigo_familia ) "
                            + "values ("
                            + "'" + cellIdProduto.getContents().trim() + "', "
                            + "'" + cellIdFamilia.getContents().trim() + "')");
                    stm.execute(sql.toString());
                    System.out.println(i);
                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private String getCodigoBarrasBalanca(String i_codigo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct codigo_barras "
                    + "from implantacao.codbarras_balanca "
                    + "where codigo_produto = '" + i_codigo + "'"
            )) {
                if (rst.next()) {
                    return rst.getString("codigo_barras");
                } else {
                    return "0";
                }
            }
        }
    }

    private String getProdutoFamilia(String i_codigo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "codigo_produto "
                    + "from implantacao.produto_familia "
                    + "where codigo_familia = '" + i_codigo + "'"
            )) {
                if (rst.next()) {
                    return rst.getString("codigo_produto");
                } else {
                    return "0";
                }
            }
        }
    }

    private String getMercadologicoAnterior(String i_codigo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "ant_merc1, "
                    + "ant_merc2, "
                    + "ant_merc3 "
                    + "from implantacao.codant_mercadologico "
                    + "where imp_sistema = '" + getSistema() + "' "
                    + "and imp_loja = '" + getLojaOrigem() + "' "
                    + "and ant_merc3 = '" + i_codigo + "'"
            )) {
                if (rst.next()) {
                    return rst.getString("ant_merc1") + ";" + rst.getString("ant_merc2") + ";" + rst.getString("ant_merc3");
                } else {
                    return "";
                }
            }
        }
    }
}
