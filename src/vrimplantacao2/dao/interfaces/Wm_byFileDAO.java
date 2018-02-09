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
import vrimplantacao2.dao.cadastro.mercadologico.MercadologicoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
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
        }
        return null;
    }
}
