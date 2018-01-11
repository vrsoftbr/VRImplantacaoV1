package vrimplantacao.dao.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao2.parametro.Parametros;

public class HostMundoDAO {

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }

    private Map<Integer, ProdutoVO> carregarProdutos(String i_arquivo) throws Exception {
        Map<Integer, ProdutoVO> vResult = new HashMap<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        boolean eBalanca;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha <= 6) {
                        continue;
                    } else if ("".equals(sheet.getCell(0, i).getContents().trim())) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellEan = sheet.getCell(1, i);
                    Cell cellDescricao = sheet.getCell(2, i);
                    Cell cellCusto = sheet.getCell(5, i);
                    Cell cellTipoEmbalagem = sheet.getCell(7, i);
                    Cell cellMargem = sheet.getCell(9, i);
                    Cell cellBalanca = sheet.getCell(18, i);
                    Cell cellPesoBruto = sheet.getCell(19, i);
                    Cell cellPesoLiquido = sheet.getCell(20, i);

                    //Instancia o produto
                    ProdutoVO oProduto = new ProdutoVO();
                    //Prepara as variáveis
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(Integer.parseInt(cellCodigo.getContents().trim()));
                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oProduto.setCodigoBarras(Long.parseLong(Utils.formataNumero(cellEan.getContents().trim())));
                    oProduto.setDescricaoCompleta((cellDescricao.getContents() == null ? "" : cellDescricao.getContents().trim()));
                    oProduto.setDescricaoReduzida(oProduto.getDescricaoCompleta());
                    oProduto.setDescricaoGondola(oProduto.getDescricaoGondola());
                    oProduto.setMercadologico1(14);
                    oProduto.setMercadologico2(1);
                    oProduto.setMercadologico3(1);
                    oProduto.setMargem(Double.parseDouble(cellMargem.getContents().trim().replace(",", ".")));
                    oProduto.setSugestaoPedido(true);
                    oProduto.setAceitaMultiplicacaoPdv(true);
                    oProduto.setSazonal(false);
                    oProduto.setFabricacaoPropria(false);
                    oProduto.setConsignado(false);
                    oProduto.setDdv(0);
                    oProduto.setPermiteTroca(true);
                    oProduto.setVendaControlada(false);
                    oProduto.setVendaPdv(true);
                    oProduto.setConferido(true);
                    oProduto.setPermiteQuebra(true);
                    oProduto.setPesoBruto(Double.parseDouble(cellPesoBruto.getContents().replace(",", ".")));
                    oProduto.setPesoLiquido(Double.parseDouble(cellPesoLiquido.getContents().replace(",", ".")));

                    long codigoProduto;
                    codigoProduto = (long) oProduto.getCodigoBarras();

                    if ((cellBalanca.getContents() != null)
                            && (!cellBalanca.getContents().trim().isEmpty())) {
                        eBalanca = "SIM".equals(cellBalanca.getContents().trim());
                    } else {
                        eBalanca = false;
                    }

                    oProduto.eBalanca = eBalanca;

                    if (eBalanca) {
                        oAutomacao.setCodigoBarras(codigoProduto);
                        oCodigoAnterior.setE_balanca(eBalanca);
                        oCodigoAnterior.setCodigobalanca((int) codigoProduto);
                        oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(
                                cellTipoEmbalagem.getContents().trim() == null ? "UN" : cellTipoEmbalagem.getContents().trim()));
                        if (oProduto.getIdTipoEmbalagem() == 0) {
                            oProduto.setPesavel(true);
                        } else {
                            oProduto.setPesavel(false);
                        }
                        oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                    } else {
                        oProduto.setPesavel(false);
                        oCodigoAnterior.setE_balanca(eBalanca);
                        oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(
                                cellTipoEmbalagem.getContents().trim() == null ? "UN" : cellTipoEmbalagem.getContents().trim()));
                        oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());
                        oCodigoAnterior.setCodigobalanca(0);
                        if (codigoProduto > 999999) {
                            oAutomacao.setCodigoBarras(codigoProduto);
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                    }
                    oAutomacao.setQtdEmbalagem(1);

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(8);
                    oAliquota.setIdAliquotaCredito(8);
                    oAliquota.setIdAliquotaDebitoForaEstado(8);
                    oAliquota.setIdAliquotaCreditoForaEstado(8);
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(8);
                    oAliquota.setIdAliquotaConsumidor(8);

                    vResult.put((int) oProduto.getId(), oProduto);
                }
            }
            return vResult;
        } catch (IndexOutOfBoundsException | NumberFormatException ex) {
            throw ex;
        }
    }

    public void importarProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutos(i_arquivo);
            List<LojaVO> vLoja = new LojaDAO().carregar();
            ProgressBar.setMaximum(vProduto.size());

            for (Integer keyId : vProduto.keySet()) {
                ProdutoVO oProduto = vProduto.get(keyId);
                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            produto.usarMercadoligicoProduto = false;
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, idLojaVR, vLoja);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoManterBalanca(String i_arquivo, int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos manter código balanca.....");
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo : carregarProdutos(i_arquivo).values()) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        List<LojaVO> vLoja = new LojaDAO().carregar();
        ProgressBar.setMaximum(aux.size());
        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod : aux.values()) {
            if (prod.eBalanca) {
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        produto.usarMercadoligicoProduto = false;

        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        produto.salvar(balanca, idLojaVR, vLoja);

        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLojaVR, vLoja);
    }

    private List<ProdutoVO> carregarCustoProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha <= 6) {
                        continue;
                    } else if ("".equals(sheet.getCell(0, i).getContents().trim())) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellCusto = sheet.getCell(5, i);

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(Integer.parseInt(cellCodigo.getContents().trim()));
                    oComplemento.setCustoComImposto(Double.parseDouble(cellCusto.getContents().trim().replace(",", ".")));
                    oComplemento.setCustoSemImposto(oComplemento.getCustoComImposto());
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCustoProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Produto Loja " + idLojaVR + "...");
            vResult = carregarCustoProduto(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarCustoProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoVendaProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
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
                    Cell cellPrecoVenda = sheet.getCell(1, i);

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(Integer.parseInt(Utils.formataNumero(cellCodigo.getContents().trim())));
                    oComplemento.setPrecoVenda(Double.parseDouble(cellPrecoVenda.getContents().trim().replace(",", ".")));
                    oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarPrecoVendaProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preco Venda Produto Loja " + idLojaVR + "...");
            vResult = carregarPrecoVendaProduto(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarPrecoProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha <= 6) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().trim().isEmpty()) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellPrecoVenda = sheet.getCell(3, i);

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setId(Integer.parseInt(Utils.formataNumero(cellCodigo.getContents().trim())));
                    oComplemento.setEstoque(Double.parseDouble(cellPrecoVenda.getContents().trim().replace(".", "").replace(",", ".")));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarEstoqueProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Produto Loja " + idLojaVR + "...");
            vResult = carregarEstoqueProduto(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarEstoqueProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarImpostoProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha, ncm1, ncm2, ncm3, idCest, cst, cstPis, cstCofins;
        double icms;
        String ncmAtual;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha <= 6) {
                        continue;
                    } else if ("".equals(sheet.getCell(0, i).getContents().trim())) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellIcms = sheet.getCell(6, i);
                    Cell cellNcm = sheet.getCell(7, i);
                    Cell cellCest = sheet.getCell(8, i);
                    Cell cellCst = sheet.getCell(10, i);
                    Cell cellPis = sheet.getCell(15, i);
                    Cell cellCofins = sheet.getCell(17, i);

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                    if ((cellNcm.getContents() != null)
                            && (!cellNcm.getContents().trim().isEmpty())
                            && (cellNcm.getContents().trim().length() > 5)) {
                        ncmAtual = Utils.formataNumero(cellNcm.getContents().trim());
                        if ((ncmAtual != null)
                                && (ncmAtual.length() > 5)) {

                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                        } else {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }

                    if ((cellCest.getContents() != null)
                            && (!cellCest.getContents().trim().isEmpty())) {
                        idCest = new CestDAO().validar(Integer.parseInt(Utils.formataNumero(cellCest.getContents().trim())));
                    } else {
                        idCest = -1;
                    }

                    if ((cellCst.getContents() != null)
                            && (!cellCst.getContents().trim().isEmpty())) {
                        cst = Integer.parseInt(cellCst.getContents().substring(
                                0, cellCst.getContents().indexOf("-")).trim());
                    } else {
                        cst = 90;
                    }

                    if ((cellIcms.getContents() != null)
                            && (!cellIcms.getContents().trim().isEmpty())) {
                        icms = Double.parseDouble(Utils.formataNumero(cellIcms.getContents().trim()));
                    } else {
                        icms = 0;
                    }

                    if ((cellPis.getContents() != null)
                            && (!cellPis.getContents().trim().isEmpty())) {
                        cstPis = Integer.parseInt(cellPis.getContents().substring(
                                0, cellPis.getContents().indexOf("-")).trim());
                    } else {
                        cstPis = 1;
                    }

                    if ((cellCofins.getContents() != null)
                            && (!cellCofins.getContents().trim().isEmpty())) {
                        cstCofins = Integer.parseInt(cellCofins.getContents().substring(
                                0, cellCofins.getContents().indexOf("-")).trim());
                    } else {
                        cstCofins = 1;
                    }

                    oProduto.setId(Integer.parseInt(cellCodigo.getContents().trim()));
                    oProduto.setNcm1(ncm1);
                    oProduto.setNcm2(ncm2);
                    oProduto.setNcm3(ncm3);
                    oProduto.setIdCest(idCest);
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito2(cstPis));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito2(cstCofins));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cst, icms, 0, false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cst, icms, 0, false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cst, icms, 0, false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cst, icms, 0, false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cst, icms, 0, false));
                    oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), cst, icms, 0, true));

                    oProduto.vAliquota.add(oAliquota);
                    vResult.add(oProduto);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarImpostoProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Imposto Produto...");
            vResult = carregarImpostoProduto(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarNcm(vResult, idLojaVR);
                new ProdutoDAO().alterarPisCofinsProduto(vResult);
                new ProdutoDAO().alterarICMSProduto(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private Map<Long, ProdutoVO> carregarCodigoBarras(String i_arquivo) throws Exception {
        Map<Long, ProdutoVO> vResult = new HashMap<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        long codigoBarras;
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha <= 4) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().trim().isEmpty()) {
                        continue;
                    }
                    
                    Cell cellEan = sheet.getCell(0, i);
                    Cell cellCodigo = sheet.getCell(1, i);
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    
                    if ((cellEan.getContents() != null) &&
                            (!cellEan.getContents().trim().isEmpty())) {                    
                        codigoBarras = Long.parseLong(Utils.formataNumero(cellEan.getContents().trim()));
                        
                        if (codigoBarras > 999999) {
                            oProduto.setId(Integer.parseInt(cellCodigo.getContents().trim()));
                            oAutomacao.setCodigoBarras(codigoBarras);
                            oProduto.vAutomacao.add(oAutomacao);
                            vResult.put(codigoBarras, oProduto);
                        }
                    }
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarCodigoBarras(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarras(i_arquivo);
            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {
                ProdutoVO oProduto = vCodigoBarra.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            produto.alterarBarraAnterio = true;
            produto.addCodigoBarras(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ClientePreferencialVO> carregarClientePreferencial(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        long cnpj;
        String dataNascimento;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha <= 6) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().trim().isEmpty()) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellCnpj = sheet.getCell(1, i);
                    Cell cellNome = sheet.getCell(2, i);
                    Cell cellTelefone = sheet.getCell(3, i);
                    Cell cellUf = sheet.getCell(4, i);
                    Cell cellCidade = sheet.getCell(5, i);
                    Cell cellNascimento = sheet.getCell(6, i);
                    Cell cellEmail = sheet.getCell(7, i);
                    Cell cellEndereco = sheet.getCell(8, i);

                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    vo.setCodigoanterior(Long.parseLong(Utils.formataNumero(cellCodigo.getContents().trim())));
                    vo.setId(Integer.parseInt(cellCodigo.getContents().trim()));
                    vo.setNome((cellNome.getContents() == null ? "" : cellNome.getContents().trim()));
                    vo.setEndereco((cellEndereco.getContents() == null ? "" : cellEndereco.getContents().trim()));

                    if ((cellCnpj.getContents() != null)
                            && (!cellCnpj.getContents().trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(cellCnpj.getContents().trim()));
                    } else {
                        cnpj = -1;
                    }

                    if (cnpj > 99999999) {
                        vo.setCnpj(cnpj);
                    } else {
                        vo.setCnpj(-1);
                    }

                    vo.setInscricaoestadual("ISENTO");

                    if ((cellTelefone.getContents() != null)
                            && (!cellTelefone.getContents().trim().isEmpty())) {
                        vo.setTelefone(Utils.formataNumero(cellTelefone.getContents().trim()));
                    } else {
                        vo.setTelefone("000000000");
                    }

                    if ((cellEmail.getContents() != null)
                            && (!cellEmail.getContents().trim().isEmpty())
                            && (cellEmail.getContents().contains("@"))) {
                        vo.setEmail(Utils.acertarTexto(cellEmail.getContents().trim().toLowerCase()));
                    } else {
                        vo.setEmail("");
                    }

                    if ((cellNascimento.getContents() != null)
                            && (!cellNascimento.getContents().trim().isEmpty())) {
                        dataNascimento = cellNascimento.getContents().substring(6, 10);
                        dataNascimento = dataNascimento + "/" + cellNascimento.getContents().substring(3, 5);
                        dataNascimento = dataNascimento + "/" + cellNascimento.getContents().substring(0, 2);
                    } else {
                        vo.setDatanascimento("");
                    }

                    vo.setCep(Parametros.get().getCepPadrao());

                    if ((cellCidade.getContents() != null)
                            && (!cellCidade.getContents().trim().isEmpty())) {
                        if ((cellUf.getContents() != null)
                                && (!cellUf.getContents().trim().isEmpty())) {

                            vo.setId_municipio((Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(cellCidade.getContents().trim()),
                                    cellUf.getContents().trim()) == 0
                                            ? Global.idMunicipio
                                            : Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(cellCidade.getContents().trim()),
                                                    cellUf.getContents().trim())));

                        } else {
                            vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                        }
                    } else {
                        vo.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                    }

                    if ((cellUf.getContents() != null)
                            && (!cellUf.getContents().trim().isEmpty())) {
                        vo.setId_estado(Utils.retornarEstadoDescricao(cellUf.getContents().trim()));
                    } else {
                        vo.setId_estado(Parametros.get().getUfPadraoV2().getId());
                    }

                    vResult.add(vo);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarClientePreferencial(String i_arquivo, int idLojaVR) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            vResult = carregarClientePreferencial(i_arquivo);
            if (!vResult.isEmpty()) {
                ClientePreferencialDAO dao = new ClientePreferencialDAO();
                new PlanoDAO().salvar(idLojaVR);
                dao.manterID = true;
                dao.salvar(vResult, idLojaVR, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
