/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class MultiPdvDAO extends InterfaceDAO {

    public String v_arquivo = "";
    private BufferedReader br = null;

    @Override
    public String getSistema() {
        return "MultiPdv";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivo), settings);
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

                    Cell cellCodMercadologico = sheet.getCell(9, i);
                    Cell cellDescMercadologico = sheet.getCell(10, i);

                    if ((cellCodMercadologico.getContents() != null)
                            && (!cellCodMercadologico.getContents().trim().isEmpty())) {

                        MercadologicoIMP imp = new MercadologicoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setMerc1ID(cellCodMercadologico.getContents().substring(0, 2));
                        imp.setMerc1Descricao(cellDescMercadologico.getContents());
                        imp.setMerc2ID(cellCodMercadologico.getContents().substring(0, 2));
                        imp.setMerc2Descricao(cellDescMercadologico.getContents());
                        imp.setMerc3ID(cellCodMercadologico.getContents().substring(0, 2));
                        imp.setMerc3Descricao(cellDescMercadologico.getContents());
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        try {

            Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellId = sheet.getCell(0, i);
                    Cell cellEan = sheet.getCell(1, i);
                    Cell cellDescricao = sheet.getCell(2, i);
                    Cell cellCusto = sheet.getCell(3, i);
                    Cell cellPreco = sheet.getCell(4, i);
                    Cell cellMargem = sheet.getCell(8, i);
                    Cell cellCodMercadologico = sheet.getCell(9, i);
                    Cell cellUnidade = sheet.getCell(11, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellId.getContents());
                    imp.setEan(cellEan.getContents());
                    imp.setDescricaoCompleta(cellDescricao.getContents());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());

                    if ((cellCodMercadologico.getContents() != null)
                            && (!cellCodMercadologico.getContents().trim().isEmpty())) {

                        imp.setCodMercadologico1(cellCodMercadologico.getContents().substring(0, 2));
                        imp.setCodMercadologico2(cellCodMercadologico.getContents().substring(0, 2));
                        imp.setCodMercadologico3(cellCodMercadologico.getContents().substring(0, 2));
                    }

                    imp.setTipoEmbalagem(cellUnidade.getContents());
                    imp.setQtdEmbalagem(1);
                    imp.setMargem(Double.parseDouble(cellMargem.getContents()) / 100);
                    imp.setPrecovenda(Double.parseDouble(cellPreco.getContents()) / 100);
                    imp.setCustoComImposto(Double.parseDouble(cellCusto.getContents()) / 100);
                    imp.setCustoSemImposto(imp.getCustoComImposto());

                    long codigoProduto;
                    codigoProduto = Long.parseLong(imp.getImportId());
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }

                    result.add(imp);
                }
            }

            return result;
        } catch (Exception ex) {
            throw ex;
        }

    }

    public List<ProdutoIMP> getProdutosOld() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String linha = "";
        int cont = 0;
        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(v_arquivo), "UTF-8"));
            Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
            while ((linha = br.readLine()) != null) {

                cont++;
                if (cont == 1) {
                    continue;
                }

                ProdutoIMP imp = new ProdutoIMP();
                ProdutoBalancaVO produtoBalanca;
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(linha.substring(0, 7));
                imp.setEan(linha.substring(7, 20));
                imp.setTipoEmbalagem(linha.substring(86, 88));
                imp.setQtdEmbalagem(1);
                imp.setDescricaoCompleta(linha.substring(20, 60));
                imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                imp.setDescricaoGondola(imp.getDescricaoCompleta());
                imp.setPrecovenda(Double.parseDouble(linha.substring(60, 70)) / 100);
                imp.setCustoComImposto(Double.parseDouble(linha.substring(142, 147)) / 100);
                imp.setCustoSemImposto(imp.getCustoComImposto());

                long codigoProduto;
                codigoProduto = Long.parseLong(imp.getImportId());
                if (codigoProduto <= Integer.MAX_VALUE) {
                    produtoBalanca = produtosBalanca.get((int) codigoProduto);
                } else {
                    produtoBalanca = null;
                }

                if (produtoBalanca != null) {
                    imp.seteBalanca(true);
                    imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                } else {
                    imp.setValidade(0);
                    imp.seteBalanca(false);
                }

                result.add(imp);

            }
            return result;

        } catch (Exception ex) {
            throw ex;
        }
    }
}
