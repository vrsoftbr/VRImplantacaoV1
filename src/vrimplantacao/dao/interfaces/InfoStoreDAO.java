package vrimplantacao.dao.interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.parametro.Parametros;

public class InfoStoreDAO {

    private int cstIcms;
    private double porcentagemIcms;
    private double reduzidoIcms;

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarIcmsInfoStore(String i_arquivo) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        int linha;
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando ICMS...InfoStore...");

            sql = new StringBuilder();
            sql.append("create table implantacao.icmsInfoStore(\n"
                    + "  codigo integer,\n"
                    + "  descricao character varying(50),\n"
                    + "  porcentagem numeric(11,2),\n"
                    + "  reduzido numeric(13,4),\n"
                    + "  cst integer\n"
                    + ")");
            stm.execute(sql.toString());

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        ProgressBar.setMaximum(sheet.getRows());
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(1, i);
                    Cell cellDescricao = sheet.getCell(4, i);
                    Cell cellPorcentagem = sheet.getCell(7, i);
                    Cell cellReduzido = sheet.getCell(8, i);
                    Cell cellCst = sheet.getCell(9, i);

                    sql = new StringBuilder();
                    sql.append("insert into implantacao.icmsInfoStore ("
                            + "codigo, descricao, porcentagem, reduzido, cst) "
                            + "values ("
                            + Integer.parseInt(cellCodigo.getContents().trim()) + ", "
                            + "'" + Utils.acertarTexto(cellDescricao.getContents().trim()) + "', "
                            + Double.parseDouble(cellPorcentagem.getContents().trim()) + ", "
                            + Double.parseDouble(cellReduzido.getContents().trim().replace(",", ".")) + ", "
                            + Integer.parseInt(cellCst.getContents().trim())
                            + ");");
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

    private List<MercadologicoVO> carregarMercadologico1(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        int mercadologico1, linha;
        String descricao;
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellMercadologico1 = sheet.getCell(2, i);
                    Cell cellDescricao = sheet.getCell(3, i);

                    mercadologico1 = Integer.parseInt(cellMercadologico1.getContents().trim());
                    
                    if ((cellDescricao.getContents() != null) &&
                            (!cellDescricao.getContents().trim().isEmpty())) {
                        descricao = cellDescricao.getContents().trim();
                    } else {
                        descricao = "SEM DESCRICAO";
                    }
//                    descricao = (cellDescricao.getContents() == null ? "" : cellDescricao.getContents().trim());

                    MercadologicoVO oMercadologico = new MercadologicoVO();
                    oMercadologico.setMercadologico1(mercadologico1);
                    oMercadologico.setDescricao(descricao);
                    oMercadologico.setNivel(1);
                    vMercadologico.add(oMercadologico);
                }
            }
            return vMercadologico;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarMercadologico1(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico 1...");
            vMercadologico = carregarMercadologico1(i_arquivo);
            if (!vMercadologico.isEmpty()) {
                new MercadologicoDAO().salvar2(vMercadologico, true);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarMercadologico2_3(String i_arquivo, int nivel) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        int mercadologico1, mercadologico2, mercadologico3 = 0, linha;
        String descricao;
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellMercadologico1 = sheet.getCell(1, i);
                    Cell cellMercadologico2 = sheet.getCell(2, i);
                    Cell cellDescricao = sheet.getCell(3, i);

                    if ((cellMercadologico1.getContents() != null) && (!cellMercadologico1.getContents().trim().isEmpty())) {
                        mercadologico1 = Integer.parseInt(cellMercadologico1.getContents());
                    } else {
                        mercadologico1 = 0;
                    }

                    if ((cellMercadologico2.getContents() != null) && (!cellMercadologico2.getContents().trim().isEmpty())) {
                        mercadologico2 = Integer.parseInt(cellMercadologico2.getContents());
                    } else {
                        mercadologico2 = 0;
                    }

                    if ((mercadologico1 > 0) && (mercadologico2 == 0)) {
                        mercadologico2 = 1;
                    }

                    if (nivel == 2) {
                        mercadologico3 = 0;
                    } else if (nivel == 3) {
                        mercadologico3 = 1;
                    }
                    if ((cellDescricao.getContents() != null) &&
                            (!cellDescricao.getContents().trim().isEmpty())) {
                        descricao = cellDescricao.getContents().trim();
                    } else {
                        descricao = "SEM DESCRICAO";
                    }

                    //descricao = (cellDescricao.getContents() == null ? "" : cellDescricao.getContents().trim());

                    if (mercadologico2 > 0) {
                        MercadologicoVO oMercadologico = new MercadologicoVO();
                        oMercadologico.setMercadologico1(mercadologico1);
                        oMercadologico.setMercadologico2(mercadologico2);
                        oMercadologico.setMercadologico3(mercadologico3);
                        oMercadologico.setDescricao(descricao);
                        oMercadologico.setNivel(nivel);
                        vMercadologico.add(oMercadologico);
                    }
                }
            }
            return vMercadologico;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarMercadologico2(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico 2...");
            vMercadologico = carregarMercadologico2_3(i_arquivo, 2);
            if (!vMercadologico.isEmpty()) {
                new MercadologicoDAO().salvar2(vMercadologico, false);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarMercadologico3(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico 3...");
            vMercadologico = carregarMercadologico2_3(i_arquivo, 3);
            if (!vMercadologico.isEmpty()) {
                new MercadologicoDAO().salvar2(vMercadologico, false);
            }

            new MercadologicoDAO().salvarMax();
            new MercadologicoDAO().completarMercadologico();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Integer, ProdutoVO> carregarProdutos(String i_arquivo, int idLoja) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        String dataCadastro;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(2, i);
                    Cell cellServico = sheet.getCell(1, i);
                    Cell cellSitucaoCadastro = sheet.getCell(3, i);
                    Cell cellCodigoBarras = sheet.getCell(10, i);
                    Cell cellEan13 = sheet.getCell(11, i);
                    Cell cellCodigoGrupo = sheet.getCell(14, i);
                    Cell cellCodigoSubGrupo = sheet.getCell(15, i);
                    Cell cellDescricaoCompleta = sheet.getCell(21, i);
                    Cell cellDescricaoReduzida = sheet.getCell(22, i);
                    Cell cellDescricaoGondola = sheet.getCell(23, i);
                    Cell cellClasseIcms = sheet.getCell(26, i);
                    Cell cellNcm = sheet.getCell(29, i);
                    Cell cellCest = sheet.getCell(30, i);
                    Cell cellPisCofins = sheet.getCell(32, i);
                    Cell cellUnidade = sheet.getCell(39, i);
                    Cell cellCusto = sheet.getCell(55, i);
                    Cell cellMargem = sheet.getCell(57, i);
                    Cell cellPrecoVenda = sheet.getCell(58, i);
                    Cell cellDataCadastro = sheet.getCell(69, i);

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(Integer.parseInt(Utils.formataNumero(cellCodigoProduto.getContents().trim())));
                    oProduto.setDescricaoCompleta((cellDescricaoCompleta.getContents() == null ? "" : cellDescricaoCompleta.getContents().trim()));
                    oProduto.setDescricaoReduzida((cellDescricaoReduzida.getContents() == null ? "" : cellDescricaoReduzida.getContents().trim()));
                    oProduto.setDescricaoGondola((cellDescricaoGondola.getContents() == null ? "" : cellDescricaoGondola.getContents().trim()));

                    if ((cellMargem.getContents() != null)
                            && (!cellMargem.getContents().trim().isEmpty())) {
                        oProduto.setMargem(Double.parseDouble(cellMargem.getContents().trim().replace(".", "").replace(",", ".")));
                    } else {
                        oProduto.setMargem(0);
                    }

                    /*if ((cellDataCadastro.getContents() != null)
                            && (!cellDataCadastro.getContents().trim().isEmpty())) {
                        dataCadastro = cellDataCadastro.getContents().substring(6, 10);
                        dataCadastro = dataCadastro + "/" + cellDataCadastro.getContents().substring(3, 5);
                        dataCadastro = dataCadastro + "/" + cellDataCadastro.getContents().substring(0, 2);
                        oProduto.setDataCadastro(dataCadastro);
                    } else {*/
                        oProduto.setDataCadastro("");
                    //}

                    if ((cellCodigoGrupo.getContents() != null)
                            && (!cellCodigoGrupo.getContents().trim().isEmpty())) {
                        oProduto.setMercadologico1(Integer.parseInt(cellCodigoGrupo.getContents().trim()));
                    } else {
                        oProduto.setMercadologico1(0);
                    }

                    if ((cellCodigoSubGrupo.getContents() != null)
                            && (!cellCodigoSubGrupo.getContents().trim().isEmpty())) {
                        oProduto.setMercadologico2(Integer.parseInt(cellCodigoSubGrupo.getContents().trim()));
                    } else {
                        oProduto.setMercadologico2(0);
                    }

                    oProduto.setMercadologico3(1);

                    /**
                     * *o pis cofins não é armazenado no banco **picofins são
                     * opções de radiobuttom
                     *
                     * 1 - Tributado 2 - Aliquota Zero 3 - Substituição 4 -
                     * Monofásico 5 - Suspensão de Contribuição
                     */
                    if ((cellPisCofins.getContents() != null)
                            && (!cellPisCofins.getContents().trim().isEmpty())) {

                        if (null != cellPisCofins.getContents().trim()) {
                            switch (cellPisCofins.getContents().trim()) {
                                case "1":
                                    oProduto.setIdTipoPisCofinsDebito(0);
                                    oProduto.setIdTipoPisCofinsCredito(12);
                                    break;
                                case "2":
                                    oProduto.setIdTipoPisCofinsDebito(7);
                                    oProduto.setIdTipoPisCofinsCredito(19);
                                    break;
                                case "3":
                                    oProduto.setIdTipoPisCofinsDebito(2);
                                    oProduto.setIdTipoPisCofinsCredito(14);
                                    break;
                                case "4":
                                    oProduto.setIdTipoPisCofinsDebito(3);
                                    oProduto.setIdTipoPisCofinsCredito(15);
                                    break;
                                case "5":
                                    oProduto.setIdTipoPisCofinsDebito(4);
                                    oProduto.setIdTipoPisCofinsCredito(10);
                                    break;
                            }
                        }
                    } else {
                        oProduto.setIdTipoPisCofinsDebito(1);
                        oProduto.setIdTipoPisCofinsCredito(13);

                    }
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(
                            oProduto.getIdTipoPisCofins(), ""));

                    if ((cellNcm.getContents() != null)
                            && (!cellNcm.getContents().trim().isEmpty())
                            && (cellNcm.getContents().trim().length() > 5)) {

                        NcmVO oNcm = new NcmDAO().validar(cellNcm.getContents().trim());
                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);

                        oAnterior.setNcm(cellNcm.getContents().trim());

                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    if ((cellCest.getContents() != null)
                            && (!cellCest.getContents().trim().isEmpty())) {

                        if (cellCest.getContents().trim().length() == 5) {
                            oProduto.setCest1(Integer.parseInt(cellCest.getContents().trim().substring(0, 1)));
                            oProduto.setCest2(Integer.parseInt(cellCest.getContents().trim().substring(1, 3)));
                            oProduto.setCest3(Integer.parseInt(cellCest.getContents().trim().substring(3, 5)));
                        } else if (cellCest.getContents().trim().length() == 6) {
                            oProduto.setCest1(Integer.parseInt(cellCest.getContents().trim().substring(0, 1)));
                            oProduto.setCest2(Integer.parseInt(cellCest.getContents().trim().substring(1, 4)));
                            oProduto.setCest3(Integer.parseInt(cellCest.getContents().trim().substring(4, 6)));
                        } else if (cellCest.getContents().trim().length() == 7) {
                            oProduto.setCest1(Integer.parseInt(cellCest.getContents().trim().substring(0, 2)));
                            oProduto.setCest2(Integer.parseInt(cellCest.getContents().trim().substring(2, 5)));
                            oProduto.setCest3(Integer.parseInt(cellCest.getContents().trim().substring(5, 7)));
                        }
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }

                    long codigoProduto;
                    
                    if (cellCodigoBarras.getContents().trim().length() > 14) {                    
                        codigoProduto = Long.parseLong(Utils.formataNumero(cellCodigoBarras.getContents().trim().substring(0, 14)));
                    } else {
                        codigoProduto = Long.parseLong(Utils.formataNumero(cellCodigoBarras.getContents().trim()));
                    }

                    /**
                     * Aparentemente o sistema utiliza o codigo de barras
                     */
                    if ("B".equals(cellServico.getContents().trim())) {
                        ProdutoBalancaVO produtoBalanca;
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }
                        if (produtoBalanca != null) {
                            oAutomacao.setCodigoBarras((int) oProduto.getId());
                            oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                            oProduto.eBalanca = true;

                            if ("P".equals(produtoBalanca.getPesavel())) {
                                oProduto.setPesavel(false);
                            } else {
                                oProduto.setPesavel(true);
                            }

                            oAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                            oAnterior.setE_balanca(true);
                        }
                    } else {
                        oProduto.setValidade(0);
                        oProduto.setPesavel(false);
                        oProduto.eBalanca = false;
                        oAnterior.setE_balanca(false);
                        if ((cellCodigoBarras.getContents() != null)
                                && (!cellCodigoBarras.getContents().trim().isEmpty())) {

                            String strCodigoBarras;
                            long codigoBarras;

                            strCodigoBarras = Utils.formataNumero(cellCodigoBarras.getContents().trim());

                            if (strCodigoBarras.length() < 7) {
                                oAutomacao.setCodigoBarras(-2);
                            } else {
                                if (strCodigoBarras.length() > 14) {
                                    codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                                } else {
                                    codigoBarras = Long.parseLong(strCodigoBarras);
                                }

                                oAutomacao.setCodigoBarras(codigoBarras);
                            }

                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                    }

                    oAutomacao.setIdTipoEmbalagem((Utils.converteTipoEmbalagem(cellUnidade.getContents().trim())));
                    oAutomacao.setIdTipoEmbalagem((oAutomacao.getIdTipoEmbalagem() == -1 ? 0 : oAutomacao.getIdTipoEmbalagem()));                   
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());
                    oAutomacao.setQtdEmbalagem(1);                    
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
                    oProduto.setPesoBruto(0);
                    oProduto.setPesoLiquido(0);

                    oComplemento.setIdLoja(idLoja);
                    oComplemento.setIdSituacaoCadastro(("N".equals(cellSitucaoCadastro.getContents()) ? 1 : 0));

                    if ((cellPrecoVenda.getContents() != null)
                            && (!cellPrecoVenda.getContents().trim().isEmpty())) {
                        
                        if (Double.parseDouble(cellPrecoVenda.getContents().replace(".", "").replace(",", ".")) > Double.MAX_VALUE) {
                            oComplemento.setPrecoVenda(0);
                        } else {
                            oComplemento.setPrecoVenda(Double.parseDouble(cellPrecoVenda.getContents().replace(".", "").replace(",", ".")));
                        }
                    } else {
                        oComplemento.setPrecoVenda(0);
                    }

                    oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.setIdEstado(uf.getId());

                    if ((cellClasseIcms.getContents() != null)
                            && (!cellClasseIcms.getContents().trim().isEmpty())) {
                        sql = new StringBuilder();
                        sql.append("select * from implantacao.icmsInfoStore "
                                + "where codigo = " + Integer.parseInt(Utils.formataNumero(cellClasseIcms.getContents().trim())));
                        rst = stm.executeQuery(sql.toString());

                        if (rst.next()) {
                            oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("porcentagem"), rst.getDouble("reduzido")));
                            oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("porcentagem"), rst.getDouble("reduzido")));
                            oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("porcentagem"), rst.getDouble("reduzido")));
                            oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("porcentagem"), rst.getDouble("reduzido")));
                            oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("porcentagem"), rst.getDouble("reduzido")));
                            oAliquota.setIdAliquotaConsumidor(Utils.getAliquotaICMS(uf.getSigla(), rst.getInt("cst"), rst.getDouble("porcentagem"), rst.getDouble("reduzido"), true));
                        }
                    } else {
                        oAliquota.setIdAliquotaDebito(8);
                        oAliquota.setIdAliquotaCredito(8);
                        oAliquota.setIdAliquotaDebitoForaEstado(8);
                        oAliquota.setIdAliquotaCreditoForaEstado(8);
                        oAliquota.setIdAliquotaDebitoForaEstadoNF(8);
                        oAliquota.setIdAliquotaConsumidor(8);
                    }

                    oAnterior.setCodigoanterior(Double.parseDouble(Utils.formataNumero(cellCodigoProduto.getContents().trim())));
                    oAnterior.setRef_icmsdebito(cellClasseIcms.getContents().trim());

                    if ((cellCodigoBarras.getContents() != null)
                            && (!cellCodigoBarras.getContents().trim().isEmpty())) {

                        String barras;
                        barras = Utils.formataNumero(cellCodigoBarras.getContents().trim());

                        if (barras.length() > 14) {
                            oAnterior.setBarras(Long.parseLong(barras.substring(0, 14)));
                        } else {
                            oAnterior.setBarras(Long.parseLong(barras));
                        }
                    } else {
                        oAnterior.setBarras(-2);
                    }

                    vProduto.put((int) oProduto.getId(), oProduto);
                }
            }
            stm.close();
            Conexao.commit();
            return vProduto;
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void importarProdutoManterBalanca(String i_arquivo, int idLoja) throws Exception {

        ProgressBar.setStatus("Carregando dados...Produtos manter código balanca.....");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo : carregarProdutos(i_arquivo, idLoja).values()) {
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
        produto.usarMercadoligicoProduto = true;

        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        produto.salvar(balanca, idLoja, vLoja);

        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(normais.size());
        produto.salvar(normais, idLoja, vLoja);
    }

    public void importarProduto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutos(i_arquivo, idLoja);

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
            produto.usarMercadoligicoProduto = true;
            produto.salvar(vProdutoNovo, idLoja, vLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarProdutoPreco(String i_arquivo, int idLoja) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        List<ProdutoVO> vProduto = new ArrayList<>();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    
                    Cell cellCodigoProduto = sheet.getCell(2, i);
                    Cell cellPrecoVenda = sheet.getCell(58, i);

                    //Inclui elas nas listas
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(Integer.parseInt(Utils.formataNumero(cellCodigoProduto.getContents().trim())));

                    if ((cellPrecoVenda.getContents() != null)
                            && (!cellPrecoVenda.getContents().trim().isEmpty())) {
                        oComplemento.setPrecoVenda(Double.parseDouble(cellPrecoVenda.getContents().replace(".", "").replace(",", ".")));
                    } else {
                        oComplemento.setPrecoVenda(0);
                    }

                    vProduto.add(oProduto);
                }
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarProdutoComplemento(String i_arquivo, int idLoja, boolean preco,
            boolean custo, boolean estoque) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        List<ProdutoVO> vProduto = new ArrayList<>();
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(1, i);
                    Cell cellCustoSemImposto = sheet.getCell(23, i);
                    Cell cellCustoComImposto = sheet.getCell(23, i);
                    Cell cellEstoque = sheet.getCell(15, i);
                    Cell cellEstoqueMinimo = sheet.getCell(13, i);
                    Cell cellEstoqueMaximo = sheet.getCell(14, i);

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    //Inclui elas nas listas
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.setId(Integer.parseInt(Utils.formataNumero(cellCodigoProduto.getContents().trim())));
                    
                    oComplemento.setIdLoja(idLoja);

                    if (custo) {
                        if ((cellCustoComImposto.getContents() != null)
                                && (!cellCustoComImposto.getContents().trim().isEmpty())) {
                            oComplemento.setCustoComImposto(Double.parseDouble(cellCustoComImposto.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            oComplemento.setCustoComImposto(0);
                        }

                        if ((cellCustoSemImposto.getContents() != null)
                                && (!cellCustoSemImposto.getContents().trim().isEmpty())) {
                            oComplemento.setCustoSemImposto(Double.parseDouble(cellCustoSemImposto.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            oComplemento.setCustoSemImposto(0);
                        }
                        //oComplemento.setCustoSemImposto(oComplemento.getCustoComImposto());
                    }

                    if (estoque) {
                        if ((cellEstoque.getContents() != null)
                                && (!cellEstoque.getContents().trim().isEmpty())) {
                            oComplemento.setEstoque(Double.parseDouble(cellEstoque.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            oComplemento.setEstoque(0);
                        }

                        if ((cellEstoqueMinimo.getContents() != null)
                                && (!cellEstoqueMinimo.getContents().trim().isEmpty())) {
                            oComplemento.setEstoqueMinimo(Double.parseDouble(cellEstoqueMinimo.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            oComplemento.setEstoqueMinimo(0);
                        }

                        if ((cellEstoqueMaximo.getContents() != null)
                                && (!cellEstoqueMaximo.getContents().trim().isEmpty())) {
                            oComplemento.setEstoqueMaximo(Double.parseDouble(cellEstoqueMaximo.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            oComplemento.setEstoqueMaximo(0);
                        }
                    }

                    if (!oProduto.vComplemento.isEmpty()) {
                        vProduto.add(oProduto);
                    }
                }
            }
            stm.close();
            Conexao.commit();
            return vProduto;
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void importarProdutoComplemento(String i_arquivo, int idLoja, boolean preco,
            boolean custo, boolean estoque) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            if (preco) {
                ProgressBar.setStatus("Carregando dados...Preço Loja " + idLoja + "...");
                vProduto = carregarProdutoPreco(i_arquivo, idLoja);
            } else {
                if ((custo) && (estoque)) {
                    ProgressBar.setStatus("Carregando dados...Custo e Estoque Loja " + idLoja + "...");
                } else if (custo) {
                    ProgressBar.setStatus("Carregando dados...Custo Loja " + idLoja + "...");
                } else if (estoque) {
                    ProgressBar.setStatus("Carregando dados...Estoque Loja " + idLoja + "...");
                }
                vProduto = carregarProdutoComplemento(i_arquivo, idLoja, false, custo, estoque);
            }
            if (!vProduto.isEmpty()) {
                if (preco) {
                    new ProdutoDAO().alterarPrecoProduto(vProduto, idLoja);
                }
                if (custo) {
                    new ProdutoDAO().alterarCustoProduto(vProduto, idLoja);
                }
                if (estoque) {
                    new ProdutoDAO().alterarEstoqueProdutoSomando(vProduto, idLoja);
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarProdutoPrecoIntegracao(String i_arquivo, int idLoja) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        List<ProdutoVO> vProduto = new ArrayList<>();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    
                    Cell cellCodigoProduto = sheet.getCell(1, i);
                    Cell cellCodigoBarras = sheet.getCell(2, i);
                    Cell cellPrecoVenda = sheet.getCell(3, i);

                    if ((cellCodigoBarras.getContents() != null)
                            && (!cellCodigoBarras.getContents().trim().isEmpty())) {

                        String strCodigoBarras;
                        long codigoBarras;

                        strCodigoBarras = Utils.formataNumero(cellCodigoBarras.getContents().trim());

                        if (strCodigoBarras.length() < 7) {
                            oProduto.setCodigoBarras(-2);
                        } else {
                            if (strCodigoBarras.length() > 14) {
                                codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            } else {
                                codigoBarras = Long.parseLong(strCodigoBarras);
                            }

                            oProduto.setCodigoBarras(codigoBarras);
                        }
                    } else {
                        oProduto.setCodigoBarras(-2);
                    }                    

                    //Inclui elas nas listas
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(Integer.parseInt(cellCodigoProduto.getContents().trim()));

                    if ((cellPrecoVenda.getContents() != null)
                            && (!cellPrecoVenda.getContents().trim().isEmpty()) &&
                            (!Utils.encontrouLetraCampoNumerico(cellPrecoVenda.getContents().trim()))) {
                        oComplemento.setPrecoVenda(Double.parseDouble(cellPrecoVenda.getContents().replace(".", "").replace(",", ".")));
                    } else {
                        oComplemento.setPrecoVenda(0);
                    }

                    vProduto.add(oProduto);
                }
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarProdutoIntegrarPreco(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preço Loja " + idLoja + "...");
            vProduto = carregarProdutoPrecoIntegracao(i_arquivo, idLoja);
            new ProdutoDAO().alterarPrecoProdutoIntegracao(vProduto, idLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarProdutoCustoIntegracao(String i_arquivo, int idLoja) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        List<ProdutoVO> vProduto = new ArrayList<>();
        String strCodigoBarras;
        long codigoBarras;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    strCodigoBarras = "";
                    codigoBarras = -2;
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    
                    Cell cellCodigoBarras = sheet.getCell(2, i);
                    Cell cellCustoSemImposto = sheet.getCell(3, i);
                    Cell cellCustoComImposto = sheet.getCell(4, i);

                    if ((cellCodigoBarras.getContents() != null)
                            && (!cellCodigoBarras.getContents().trim().isEmpty())) {

                        strCodigoBarras = Utils.formataNumero(cellCodigoBarras.getContents().trim());

                        if (strCodigoBarras.length() < 7) {
                            codigoBarras = -2;
                        } else {
                            if (strCodigoBarras.length() > 14) {
                                codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            } else {
                                codigoBarras = Long.parseLong(strCodigoBarras);
                            }
                        }
                    } else {
                        codigoBarras = -2;
                    }                    

                    //Inclui elas nas listas
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.setCodigoBarras(codigoBarras);

                    if ((cellCustoSemImposto.getContents() != null)
                            && (!cellCustoSemImposto.getContents().trim().isEmpty()) &&
                            (!Utils.encontrouLetraCampoNumerico(cellCustoSemImposto.getContents().trim()))) {
                        oComplemento.setCustoSemImposto(Double.parseDouble(cellCustoSemImposto.getContents().replace(".", "").replace(",", ".")));
                    } else {
                        oComplemento.setCustoSemImposto(0);
                    }

                    if ((cellCustoComImposto.getContents() != null) &&
                            (!cellCustoComImposto.getContents().trim().isEmpty()) &&
                            (!Utils.encontrouLetraCampoNumerico(cellCustoComImposto.getContents().trim()))) {
                        oComplemento.setCustoComImposto(Double.parseDouble(cellCustoComImposto.getContents().replace(".", "").replace(",", ".")));
                    } else {
                        oComplemento.setCustoComImposto(0);
                    }
                    
                    vProduto.add(oProduto);
                }
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarProdutoIntegrarCusto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Loja " + idLoja + "...");
            vProduto = carregarProdutoCustoIntegracao(i_arquivo, idLoja);
            new ProdutoDAO().alterarCustoProdutoIntegracao(vProduto, idLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarProdutoEstoqueIntegracao(String i_arquivo, int idLoja) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        List<ProdutoVO> vProduto = new ArrayList<>();
        String strCodigoBarras;
        long codigoBarras;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    strCodigoBarras = "";
                    codigoBarras = -2;
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    
                    Cell cellCodigoBarras = sheet.getCell(2, i);
                    Cell cellEstoque = sheet.getCell(5, i);
                    Cell cellEstoqueMinimo = sheet.getCell(6, i);
                    Cell cellEstoqueMaximo = sheet.getCell(7, i);

                    if ((cellCodigoBarras.getContents() != null)
                            && (!cellCodigoBarras.getContents().trim().isEmpty())) {

                        strCodigoBarras = Utils.formataNumero(cellCodigoBarras.getContents().trim());

                        if (strCodigoBarras.length() < 7) {
                            codigoBarras = -2;
                        } else {
                            if (strCodigoBarras.length() > 14) {
                                codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            } else {
                                codigoBarras = Long.parseLong(strCodigoBarras);
                            }
                        }
                    } else {
                        codigoBarras = -2;
                    }                    

                    //Inclui elas nas listas
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.setCodigoBarras(codigoBarras);

                    if ((cellEstoque.getContents() != null) &&
                            (!cellEstoque.getContents().trim().isEmpty()) &&
                            (!Utils.encontrouLetraCampoNumerico(cellEstoque.getContents().trim()))) {
                        oComplemento.setEstoque(Double.parseDouble(cellEstoque.getContents().replace(".", "").replace(",", ".")));
                    } else {
                        oComplemento.setEstoque(0);
                    }
                    
                    if ((cellEstoqueMinimo.getContents() != null) &&
                            (!cellEstoqueMinimo.getContents().trim().isEmpty()) &&
                            (!Utils.encontrouLetraCampoNumerico(cellEstoqueMinimo.getContents().trim()))) {
                        oComplemento.setEstoqueMinimo(Double.parseDouble(cellEstoqueMinimo.getContents().replace(".", "").replace(",", ".")));
                    } else {
                        oComplemento.setEstoqueMinimo(0);
                    }
                    
                    if ((cellEstoqueMaximo.getContents() != null) &&
                            (!cellEstoqueMaximo.getContents().trim().isEmpty()) &&
                            (!Utils.encontrouLetraCampoNumerico(cellEstoqueMaximo.getContents().trim()))) {
                        oComplemento.setEstoqueMaximo(Double.parseDouble(cellEstoqueMaximo.getContents().replace(".", "").replace(",", ".")));
                    } else {
                        oComplemento.setEstoqueMaximo(0);
                    }
                    
                    vProduto.add(oProduto);
                }
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarProdutoIntegrarEstoque(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Loja " + idLoja + "...");
            vProduto = carregarProdutoEstoqueIntegracao(i_arquivo, idLoja);
            new ProdutoDAO().alterarSomaEstoqueProdutoIntegracao(vProduto, idLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<FornecedorVO> carregarFornecedor(String i_arquivo) throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha, idSituacaoCadastro, idTipoInscricao;

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(1, i);
                    Cell cellSituacaoCadastro = sheet.getCell(3, i);
                    Cell cellTipoInscricao = sheet.getCell(4, i);
                    Cell cellTipoPessoa = sheet.getCell(5, i);
                    Cell cellRazaoSocial = sheet.getCell(6, i);
                    Cell cellNomeFantasia = sheet.getCell(7, i);
                    Cell cellEndereco = sheet.getCell(8, i);
                    Cell cellNumero = sheet.getCell(9, i);
                    Cell cellBairro = sheet.getCell(10, i);
                    Cell cellCidade = sheet.getCell(11, i);
                    Cell cellCep = sheet.getCell(12, i);
                    Cell cellEstado = sheet.getCell(13, i);
                    Cell cellEmail = sheet.getCell(15, i);
                    Cell cellTelefone = sheet.getCell(17, i);
                    Cell cellTelefoneCom = sheet.getCell(18, i);
                    Cell cellTelefoneCel = sheet.getCell(19, i);
                    Cell cellCnpj = sheet.getCell(20, i);
                    Cell cellCpf = sheet.getCell(21, i);
                    Cell cellInscEstadual = sheet.getCell(22, i);
                    Cell cellObservacao = sheet.getCell(97, i);

                    if ((cellTipoPessoa.getContents() != null)
                            && (!cellTipoPessoa.getContents().trim().isEmpty())
                            && "F".equals(cellTipoPessoa.getContents().trim())) {

                        if ((cellSituacaoCadastro.getContents() != null)
                                && (!cellSituacaoCadastro.getContents().trim().isEmpty())) {
                            if ("False".equals(cellSituacaoCadastro.getContents().trim())) {
                                idSituacaoCadastro = 0;
                            } else {
                                idSituacaoCadastro = 1;
                            }
                        } else {
                            idSituacaoCadastro = 1;
                        }

                        if ((cellTipoInscricao.getContents() != null)
                                && (!cellTipoInscricao.getContents().trim().isEmpty())) {
                            if ("False".equals(cellTipoInscricao.getContents().trim())) {
                                idTipoInscricao = 0;
                            } else {
                                idTipoInscricao = 1;
                            }
                        } else {
                            idTipoInscricao = 0;
                        }

                        FornecedorVO oFornecedor = new FornecedorVO();
                        oFornecedor.setId(Integer.parseInt(cellCodigo.getContents().trim()));
                        oFornecedor.setCodigoanterior(oFornecedor.getId());
                        oFornecedor.setRazaosocial((cellRazaoSocial.getContents() == null ? "" : cellRazaoSocial.getContents().trim()));
                        oFornecedor.setNomefantasia((cellNomeFantasia.getContents() == null ? "" : cellNomeFantasia.getContents().trim()));
                        oFornecedor.setEndereco((cellEndereco.getContents() == null ? "" : cellEndereco.getContents().trim()));
                        oFornecedor.setBairro((cellBairro.getContents() == null ? "" : cellBairro.getContents().trim()));
                        oFornecedor.setId_tipoinscricao(idTipoInscricao);
                        oFornecedor.setId_situacaocadastro(idSituacaoCadastro);

                        if ((cellNumero.getContents() != null)
                                && (!cellNumero.getContents().trim().isEmpty())) {
                            oFornecedor.setNumero(cellNumero.getContents().trim());
                        } else {
                            oFornecedor.setNumero("0");
                        }

                        if ((cellCep.getContents() != null)
                                && (!cellCep.getContents().trim().isEmpty())
                                && (cellCep.getContents().trim().length() >= 8)) {
                            oFornecedor.setCep(Long.parseLong(Utils.formataNumero(cellCep.getContents().trim())));
                        } else {
                            oFornecedor.setCep(Parametros.get().getCepPadrao());
                        }

                        if ((cellCidade.getContents() != null) && (!cellCidade.getContents().trim().isEmpty())
                                && (cellEstado.getContents() != null) && (!cellEstado.getContents().trim().isEmpty())) {

                            oFornecedor.setId_municipio(
                                    Utils.retornarMunicipioIBGEDescricao(
                                            Utils.acertarTexto(cellCidade.getContents().trim()),
                                            Utils.acertarTexto(cellEstado.getContents().trim())) == 0
                                            ? Parametros.get().getMunicipioPadrao2().getId()
                                            : Utils.retornarMunicipioIBGEDescricao(
                                                    Utils.acertarTexto(cellCidade.getContents().trim()),
                                                    Utils.acertarTexto(cellEstado.getContents().trim())));
                        } else {
                            oFornecedor.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                        }

                        if ((cellEstado.getContents() != null)
                                && (!cellEstado.getContents().trim().isEmpty())) {
                            oFornecedor.setId_estado(
                                    (Utils.retornarEstadoDescricao(Utils.acertarTexto(cellEstado.getContents().trim()))
                                    == 0
                                            ? Parametros.get().getUfPadrao().getId()
                                            : Utils.retornarEstadoDescricao(Utils.acertarTexto(cellEstado.getContents().trim()))));
                        } else {
                            oFornecedor.setId_estado(Parametros.get().getUfPadrao().getId());
                        }

                        if ((cellTelefone.getContents() != null)
                                && (!cellTelefone.getContents().trim().isEmpty())) {
                            oFornecedor.setTelefone(cellTelefone.getContents().trim());
                        } else {
                            oFornecedor.setTelefone("");
                        }

                        if ((cellEmail.getContents() != null)
                                && (!cellEmail.getContents().trim().isEmpty())
                                && (cellEmail.getContents().contains("@"))) {
                            oFornecedor.setEmail(cellEmail.getContents().trim());
                        } else {
                            oFornecedor.setEmail("");
                        }

                        if ((cellTelefoneCom.getContents() != null)
                                && (!cellTelefoneCom.getContents().trim().isEmpty())) {
                            oFornecedor.setTelefone2(cellTelefoneCom.getContents().trim());
                        } else {
                            oFornecedor.setTelefone2("");
                        }

                        if ((cellTelefoneCel.getContents() != null)
                                && (!cellTelefoneCel.getContents().trim().isEmpty())) {
                            oFornecedor.setCelular(cellTelefoneCel.getContents().trim());
                        } else {
                            oFornecedor.setCelular("");
                        }

                        if (idTipoInscricao == 0) {
                            if ((cellCnpj.getContents() != null)
                                    && (!cellCnpj.getContents().trim().isEmpty())) {
                                oFornecedor.setCnpj(Long.parseLong(Utils.formataNumero(cellCnpj.getContents().trim())));
                            } else {
                                oFornecedor.setCnpj(-1);
                            }
                        } else {
                            if ((cellCpf.getContents() != null)
                                    && (!cellCpf.getContents().trim().isEmpty())) {
                                oFornecedor.setCnpj(Long.parseLong(Utils.formataNumero(cellCpf.getContents().trim())));
                            } else {
                                oFornecedor.setCnpj(-1);
                            }
                        }

                        if ((cellInscEstadual.getContents() != null)
                                && (!cellInscEstadual.getContents().trim().isEmpty())) {
                            oFornecedor.setInscricaoestadual(cellInscEstadual.getContents());
                        } else {
                            oFornecedor.setInscricaoestadual("ISENTO");
                        }

                        vFornecedor.add(oFornecedor);
                    }
                }
            }
            return vFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarFornecedor(String i_arquivo) throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            vFornecedor = carregarFornecedor(i_arquivo);

            if (!vFornecedor.isEmpty()) {
                new FornecedorDAO().salvar(vFornecedor);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoFornecedorVO> carregarProdutoFornecedor(String i_arquivo) throws Exception {
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int linha;
        String strDataAlteracao;
        java.sql.Date dataAlteracao;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoFornecedor = sheet.getCell(1, i);
                    Cell cellCodigoExterno = sheet.getCell(2, i);
                    Cell cellCodigoProduto = sheet.getCell(3, i);
                    Cell cellCusto = sheet.getCell(9, i);
                    Cell cellDataAlteracao = sheet.getCell(11, i);

                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                    if ((cellCodigoFornecedor.getContents() != null)
                            && (!cellCodigoFornecedor.getContents().trim().isEmpty())) {
                        oProdutoFornecedor.setId_fornecedor(Integer.parseInt(cellCodigoFornecedor.getContents().trim()));
                    } else {
                        oProdutoFornecedor.setId_fornecedor(0);
                    }
                    oProdutoFornecedor.setId_produto(Integer.parseInt(Utils.formataNumero(cellCodigoProduto.getContents().trim())));
                    oProdutoFornecedor.setCodigoexterno((cellCodigoExterno.getContents() == null ? "" : cellCodigoExterno.getContents().trim()));
                    oProdutoFornecedor.setCustotabela(Double.parseDouble(cellCusto.getContents().replace(".", "").replace(",", ".")));

                    if ((cellDataAlteracao.getContents() != null)
                            && (!cellDataAlteracao.getContents().trim().isEmpty())) {
                        strDataAlteracao = cellDataAlteracao.getContents().substring(6, 10);
                        strDataAlteracao = strDataAlteracao + "/" + cellDataAlteracao.getContents().substring(3, 5);
                        strDataAlteracao = strDataAlteracao + "/" + cellDataAlteracao.getContents().substring(0, 2);
                        dataAlteracao = new java.sql.Date(fmt.parse(strDataAlteracao).getTime());
                        oProdutoFornecedor.setDataalteracao(dataAlteracao);
                    } else {
                        oProdutoFornecedor.setDataalteracao(new Date(new java.util.Date().getTime()));
                    }
                    vProdutoFornecedor.add(oProdutoFornecedor);
                }
            }
            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoFornecedor(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor(i_arquivo);

            if (!vProdutoFornecedor.isEmpty()) {
                new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ClientePreferencialVO> carregarClientePreferencial(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        int linha, idSituacaoCadastro, idTipoInscricao;
        boolean status;
        String dataNascimento;
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(1, i);
                    Cell cellSituacaoCadastro = sheet.getCell(3, i);
                    Cell cellTipoInscricao = sheet.getCell(4, i);
                    Cell cellTipoPessoa = sheet.getCell(5, i);
                    Cell cellRazaoSocial = sheet.getCell(6, i);
                    Cell cellNomeFantasia = sheet.getCell(7, i);
                    Cell cellEndereco = sheet.getCell(8, i);
                    Cell cellNumero = sheet.getCell(9, i);
                    Cell cellBairro = sheet.getCell(10, i);
                    Cell cellCidade = sheet.getCell(11, i);
                    Cell cellCep = sheet.getCell(12, i);
                    Cell cellEstado = sheet.getCell(13, i);
                    Cell cellProfissao = sheet.getCell(14, i);
                    Cell cellEmail = sheet.getCell(15, i);
                    Cell cellDataNascimento = sheet.getCell(16, i);
                    Cell cellTelefone = sheet.getCell(17, i);
                    Cell cellTelefoneCom = sheet.getCell(18, i);
                    Cell cellTelefoneCel = sheet.getCell(19, i);
                    Cell cellCnpj = sheet.getCell(20, i);
                    Cell cellCpf = sheet.getCell(21, i);
                    Cell cellInscEstadual = sheet.getCell(22, i);
                    Cell cellStatus = sheet.getCell(28, i);
                    Cell cellLimite = sheet.getCell(52, i);
                    Cell cellEmpresaTrabalho = sheet.getCell(55, i);
                    Cell cellTelefoneTrabalho = sheet.getCell(56, i);
                    Cell cellSalario = sheet.getCell(59, i);
                    Cell cellConjuge = sheet.getCell(81, i);
                    Cell cellCpfConjuge = sheet.getCell(82, i);
                    Cell cellRgConjuge = sheet.getCell(83, i);
                    Cell cellObservacao = sheet.getCell(97, i);
                    Cell cellObservacao2 = sheet.getCell(98, i);

                    if ((cellTipoPessoa.getContents() != null)
                            && (!cellTipoPessoa.getContents().trim().isEmpty())
                            && ("C".equals(cellTipoPessoa.getContents().trim()))) {

                        /*if ((cellSituacaoCadastro.getContents() != null)
                                && (!cellSituacaoCadastro.getContents().trim().isEmpty())) {
                            if ("False".equals(cellSituacaoCadastro.getContents().trim())) {
                                idSituacaoCadastro = 0;
                            } else {
                                idSituacaoCadastro = 1;
                            }
                        } else {
                            idSituacaoCadastro = 1;
                        }*/

                        if ((cellTipoInscricao.getContents() != null)
                                && (!cellTipoInscricao.getContents().trim().isEmpty())) {
                            if ("False".equals(cellTipoInscricao.getContents().trim())) {
                                idTipoInscricao = 0;
                            } else {
                                idTipoInscricao = 1;
                            }
                        } else {
                            idTipoInscricao = 0;
                        }

                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                        oClientePreferencial.setId(Integer.parseInt(cellCodigo.getContents().trim()));
                        oClientePreferencial.setCodigoanterior(oClientePreferencial.getId());
                        oClientePreferencial.setNome((cellRazaoSocial.getContents() == null ? "" : cellRazaoSocial.getContents().trim()));
                        oClientePreferencial.setEndereco((cellEndereco.getContents() == null ? "" : cellEndereco.getContents().trim()));
                        oClientePreferencial.setBairro((cellBairro.getContents() == null ? "" : cellBairro.getContents().trim()));

                        if ((cellCep.getContents() != null)
                                && (!cellCep.getContents().trim().isEmpty())
                                && (cellCep.getContents().trim().length() >= 8)) {
                            oClientePreferencial.setCep(Utils.formataNumero(cellCep.getContents().trim()));
                        } else {
                            oClientePreferencial.setCep(Parametros.get().getCepPadrao());
                        }

                        if ((cellCidade.getContents() != null) && (!cellCidade.getContents().trim().isEmpty())
                                && (cellEstado.getContents() != null) && (!cellEstado.getContents().trim().isEmpty())) {

                            oClientePreferencial.setId_municipio(
                                    Utils.retornarMunicipioIBGEDescricao(
                                            Utils.acertarTexto(cellCidade.getContents().trim()),
                                            Utils.acertarTexto(cellEstado.getContents().trim())) == 0
                                            ? Parametros.get().getMunicipioPadrao2().getId()
                                            : Utils.retornarMunicipioIBGEDescricao(
                                                    Utils.acertarTexto(cellCidade.getContents().trim()),
                                                    Utils.acertarTexto(cellEstado.getContents().trim())));
                        } else {
                            oClientePreferencial.setId_municipio(Parametros.get().getMunicipioPadrao2().getId());
                        }

                        if ((cellEstado.getContents() != null)
                                && (!cellEstado.getContents().trim().isEmpty())) {
                            oClientePreferencial.setId_estado(
                                    (Utils.retornarEstadoDescricao(Utils.acertarTexto(cellEstado.getContents().trim()))
                                    == 0
                                            ? Parametros.get().getUfPadrao().getId()
                                            : Utils.retornarEstadoDescricao(Utils.acertarTexto(cellEstado.getContents().trim()))));
                        } else {
                            oClientePreferencial.setId_estado(Parametros.get().getUfPadrao().getId());
                        }

                        if ((cellStatus.getContents() != null)
                                && (!cellStatus.getContents().trim().isEmpty())) {
                            if ("N".equals(cellStatus.getContents().trim())
                                    || ("B".equals(cellStatus.getContents().trim()))
                                    || ("D".equals(cellStatus.getContents().trim()))) {
                                oClientePreferencial.setBloqueado(true);
                            } else {
                                oClientePreferencial.setBloqueado(false);
                            }
                        } else {
                            oClientePreferencial.setBloqueado(false);
                        }

                        if ((cellStatus.getContents() != null)
                                && (!cellStatus.getContents().trim().isEmpty())
                                && ("D".equals(cellStatus.getContents().trim()))) {
                            oClientePreferencial.setId_situacaocadastro(0);
                        } else {
                            oClientePreferencial.setId_situacaocadastro(1);
                        }

                        oClientePreferencial.setId_tipoinscricao(idTipoInscricao);

                        if ((cellTelefone.getContents() != null)
                                && (!cellTelefone.getContents().trim().isEmpty())) {
                            oClientePreferencial.setTelefone(Utils.formataNumero(cellTelefone.getContents().trim()));
                        } else {
                            oClientePreferencial.setTelefone("000000000");
                        }

                        if ((cellTelefoneCel.getContents() != null)
                                && (!cellTelefoneCel.getContents().trim().isEmpty())) {
                            oClientePreferencial.setCelular(Utils.formataNumero(cellTelefoneCel.getContents().trim()));
                        } else {
                            oClientePreferencial.setCelular("");
                        }

                        if ((cellTelefoneCom.getContents() != null)
                                && (!cellTelefoneCom.getContents().trim().isEmpty())) {
                            oClientePreferencial.setTelefone2(Utils.formataNumero(cellTelefoneCom.getContents().trim()));
                        } else {
                            oClientePreferencial.setTelefone2("");
                        }

                        if ((cellEmail.getContents() != null)
                                && (!cellEmail.getContents().trim().isEmpty())
                                && (cellEmail.getContents().contains("@"))) {
                            oClientePreferencial.setEmail(Utils.acertarTexto(cellEmail.getContents().trim()));
                        } else {
                            oClientePreferencial.setEmail("");
                        }

                        if ((cellNumero.getContents() != null)
                                && (!cellNumero.getContents().trim().isEmpty())) {
                            oClientePreferencial.setNumero(cellNumero.getContents().trim());
                        } else {
                            oClientePreferencial.setNumero("0");
                        }

                        if (idTipoInscricao == 0) {
                            if ((cellCnpj.getContents() != null)
                                    && (!cellCnpj.getContents().trim().isEmpty())) {
                                oClientePreferencial.setCnpj(Long.parseLong(Utils.formataNumero(cellCnpj.getContents().trim())));
                            } else {
                                oClientePreferencial.setCnpj(-1);
                            }
                        } else {
                            if ((cellCpf.getContents() != null)
                                    && (!cellCpf.getContents().trim().isEmpty())) {
                                oClientePreferencial.setCnpj(Long.parseLong(Utils.formataNumero(cellCpf.getContents().trim())));
                            } else {
                                oClientePreferencial.setCnpj(-1);
                            }
                        }

                        if ((cellInscEstadual.getContents() != null)
                                && (!cellInscEstadual.getContents().trim().isEmpty())) {
                            oClientePreferencial.setInscricaoestadual(cellInscEstadual.getContents());
                        } else {
                            oClientePreferencial.setInscricaoestadual("ISENTO");
                        }

                        if ((cellDataNascimento.getContents() != null)
                                && (!cellDataNascimento.getContents().trim().isEmpty())) {
                            oClientePreferencial.setDatanascimento(cellDataNascimento.getContents().trim());
                        } else {
                            oClientePreferencial.setDatanascimento("");
                        }

                        if ((cellLimite.getContents() != null)
                                && (!cellLimite.getContents().trim().isEmpty())) {
                            oClientePreferencial.setValorlimite(Double.parseDouble(cellLimite.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            oClientePreferencial.setValorlimite(0);
                        }

                        oClientePreferencial.setEmpresa((cellEmpresaTrabalho.getContents() == null ? "" : cellEmpresaTrabalho.getContents().trim()));
                        oClientePreferencial.setCargo((cellProfissao.getContents() == null ? "" : cellProfissao.getContents().trim()));

                        if ((cellTelefoneTrabalho.getContents() != null)
                                && (!cellTelefoneTrabalho.getContents().trim().isEmpty())) {
                            oClientePreferencial.setTelefoneempresa(Utils.formataNumero(cellTelefoneTrabalho.getContents().trim()));
                        } else {
                            oClientePreferencial.setTelefoneempresa("");
                        }

                        if ((cellSalario.getContents() != null)
                                && (!cellSalario.getContents().trim().isEmpty())) {
                            oClientePreferencial.setSalario(Double.parseDouble(cellSalario.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            oClientePreferencial.setSalario(0);
                        }

                        oClientePreferencial.setNomeconjuge((cellConjuge.getContents() == null ? "" : cellConjuge.getContents().trim()));

                        if ((cellCpfConjuge.getContents() != null)
                                && (!cellCpfConjuge.getContents().trim().isEmpty())) {
                            oClientePreferencial.setCpfconjuge(Double.parseDouble(Utils.formataNumero(cellCpfConjuge.getContents().trim())));
                        } else {
                            oClientePreferencial.setCpfconjuge(0);
                        }

                        oClientePreferencial.setRgconjuge((cellRgConjuge.getContents() == null ? "" : cellRgConjuge.getContents().trim()));

                        oClientePreferencial.setObservacao((cellObservacao.getContents() == null ? "" : cellObservacao.getContents().trim()));
                        oClientePreferencial.setObservacao2((cellObservacao2.getContents() == null ? "" : cellObservacao2.getContents().trim()));

                        vClientePreferencial.add(oClientePreferencial);
                    }
                }
            }
            return vClientePreferencial;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarClientePreferencial(String i_arquivo, int idLoja) throws Exception {
        List<ClientePreferencialVO> vCliente = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            vCliente = carregarClientePreferencial(i_arquivo);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vCliente, idLoja, idLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto = 0;
        long codigobarras = -1;

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("id"));

                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto <= 9999)) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = Utils.gerarEan13((int) idProduto, true);
                }

                qtdeEmbalagem = 1;

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = (int) idProduto;
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.qtdEmbalagem = qtdeEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);
                vProduto.put(codigobarras, oProduto);
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCodigoBarraEmBranco() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarrasEmBranco();
            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {
                ProdutoVO oProduto = vCodigoBarra.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            produto.addCodigoBarrasEmBranco(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ReceberCreditoRotativoVO> carregarCreditoRotativo(String i_arquivo, int idLoja, String log) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha, idCliente;
        String dataEmissao, dataVencimento, observacao;
        File f = new File(log + "\\clienteNaoEncontrados.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha > 1) {
                        Cell cellDataEmissao = sheet.getCell(7, i);
                        Cell cellHistorico = sheet.getCell(8, i);
                        Cell cellCliente = sheet.getCell(9, i);
                        Cell cellDataVencimento = sheet.getCell(11, i);
                        Cell cellValor = sheet.getCell(12, i);
                        Cell cellDocumento = sheet.getCell(19, i);
                        Cell cellNosNumero = sheet.getCell(29, i);
                        Cell cellContador = sheet.getCell(37, i);

                        ReceberCreditoRotativoVO oRotativo = new ReceberCreditoRotativoVO();

                        dataEmissao = cellDataEmissao.getContents().substring(6, 10);
                        dataEmissao = dataEmissao + "/" + cellDataEmissao.getContents().substring(3, 5);
                        dataEmissao = dataEmissao + "/" + cellDataEmissao.getContents().substring(0, 2);

                        dataVencimento = cellDataVencimento.getContents().substring(6, 10);
                        dataVencimento = dataVencimento + "/" + cellDataVencimento.getContents().substring(3, 5);
                        dataVencimento = dataVencimento + "/" + cellDataVencimento.getContents().substring(0, 2);

                        if ((cellHistorico.getContents() != null)
                                && (!cellHistorico.getContents().trim().isEmpty())) {
                            observacao = "HISTORICO.: " + Utils.acertarTexto(cellHistorico.getContents().trim());
                        } else {
                            observacao = "NAO EXISTE HISTORICO";
                        }

                        if ((cellDocumento.getContents() != null)
                                && (!cellDocumento.getContents().trim().isEmpty())) {
                            observacao = observacao + " ;DOCUMENTO.: " + Utils.acertarTexto(cellDocumento.getContents().trim());
                        } else {
                            observacao = observacao + " ;SEM DOCUMENTO";
                        }

                        if ((cellNosNumero.getContents() != null)
                                && (!cellNosNumero.getContents().trim().isEmpty())) {
                            observacao = observacao + " ;NOS_NUMERO.: " + Utils.acertarTexto(cellNosNumero.getContents().trim());
                        } else {
                            observacao = observacao + " ;SEM NOS_NUMERO";
                        }

                        idCliente = new ClientePreferencialDAO().getIdByCodigoAnterior(
                                Integer.parseInt(cellCliente.getContents().trim()), idLoja);

                        if (idCliente != -1) {
                            oRotativo.setId_clientepreferencial(idCliente);
                            oRotativo.setDataemissao(dataEmissao);
                            oRotativo.setDatavencimento(dataVencimento);
                            oRotativo.setValor(Double.parseDouble(cellValor.getContents().replace(".", "").replace(",", ".")));

                            if ((cellContador.getContents() != null)
                                    && (!cellContador.getContents().trim().isEmpty())) {
                                oRotativo.setNumerocupom(Integer.parseInt(cellContador.getContents().trim()));
                            } else {
                                oRotativo.setNumerocupom(0);
                            }

                            oRotativo.setObservacao(observacao);
                            vReceberCreditoRotativo.add(oRotativo);
                        } else {
                            bw.write("CODIGO CLIENTE NÃO ENCONTRADO.: " + cellCliente.getContents().trim() + " DATA EMISSAO.: " + cellDataEmissao.getContents().trim() + " "
                                    + "DATA VENCIMENTO.: " + cellDataVencimento.getContents().trim() + "VALOR.: " + cellValor.getContents().trim() + " "
                                    + "HISTORICO.: " + cellHistorico.getContents().trim() + " DOCUMENTO.: " + cellDocumento.getContents().trim() + " "
                                    + "NOS_NUMERO.: " + cellNosNumero.getContents().trim() + ";");
                            bw.newLine();
                        }
                    }
                }
            }
            bw.flush();
            bw.close();
            return vReceberCreditoRotativo;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarReceberCreditoRotativo(String i_arquivo, int idLoja, String log) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo Loja " + idLoja + "...");
            vReceberCreditoRotativo = carregarCreditoRotativo(i_arquivo, idLoja, log);
            if (!vReceberCreditoRotativo.isEmpty()) {
                new ReceberCreditoRotativoDAO().salvarComIdCliente(vReceberCreditoRotativo, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ReceberChequeVO> carregarReceberCheque(String i_arquivo, int idLoja) throws Exception {
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha, idCliente;
        String dataEmissao, dataVencimento, observacao;
        Statement stm = null;
        ResultSet rst = null;

        try {
            stm = Conexao.createStatement();
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha > 1) {
                        Cell cellDataEmissao = sheet.getCell(7, i);
                        Cell cellHistorico = sheet.getCell(8, i);
                        Cell cellCliente = sheet.getCell(9, i);
                        Cell cellDataVencimento = sheet.getCell(11, i);
                        Cell cellValor = sheet.getCell(12, i);
                        Cell cellDocumento = sheet.getCell(19, i);
                        Cell cellNosNumero = sheet.getCell(29, i);
                        Cell cellContador = sheet.getCell(37, i);

                        ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                        dataEmissao = cellDataEmissao.getContents().substring(6, 10);
                        dataEmissao = dataEmissao + "/" + cellDataEmissao.getContents().substring(3, 5);
                        dataEmissao = dataEmissao + "/" + cellDataEmissao.getContents().substring(0, 2);

                        dataVencimento = cellDataVencimento.getContents().substring(6, 10);
                        dataVencimento = dataVencimento + "/" + cellDataVencimento.getContents().substring(3, 5);
                        dataVencimento = dataVencimento + "/" + cellDataVencimento.getContents().substring(0, 2);

                        if ((cellHistorico.getContents() != null)
                                && (!cellHistorico.getContents().trim().isEmpty())) {
                            observacao = "HISTORICO.: " + Utils.acertarTexto(cellHistorico.getContents().trim());
                        } else {
                            observacao = "NAO EXISTE HISTORICO";
                        }

                        if ((cellDocumento.getContents() != null)
                                && (!cellDocumento.getContents().trim().isEmpty())) {
                            observacao = observacao + " ;DOCUMENTO.: " + Utils.acertarTexto(cellDocumento.getContents().trim());
                        } else {
                            observacao = observacao + " ;SEM DOCUMENTO";
                        }

                        if ((cellNosNumero.getContents() != null)
                                && (!cellNosNumero.getContents().trim().isEmpty())) {
                            observacao = observacao + " ;NOS_NUMERO.: " + Utils.acertarTexto(cellNosNumero.getContents().trim());
                        } else {
                            observacao = observacao + " ;SEM NOS_NUMERO";
                        }

                        idCliente = new ClientePreferencialDAO().getIdByCodigoAnterior(
                                Integer.parseInt(cellCliente.getContents().trim()), idLoja);

                        if (idCliente != -1) {
                            rst = stm.executeQuery("select nome, cnpj, inscricaoestadual, telefone "
                                    + "from clientepreferencial "
                                    + "where id = " + idCliente);
                            if (rst.next()) {
                                oReceberCheque.setNome(rst.getString("nome"));
                                oReceberCheque.setCpf(rst.getLong("cnpj"));
                                oReceberCheque.setRg(rst.getString("inscricaoestadual"));
                                oReceberCheque.setTelefone(rst.getString("telefone"));
                            }
                        } else {
                            oReceberCheque.setNome("SEM NOME");
                            oReceberCheque.setCpf(Long.parseLong("0"));
                            oReceberCheque.setRg("SEM RG");
                            oReceberCheque.setTelefone("SEM TELEFONE");
                        }

                        oReceberCheque.setId_banco(804);
                        oReceberCheque.setObservacao(observacao);
                        oReceberCheque.setValor(Double.parseDouble(cellValor.getContents().replace(".", "").replace(",", ".")));
                        oReceberCheque.setData(dataEmissao);
                        oReceberCheque.setDatadeposito(dataVencimento);

                        if ((cellContador.getContents() != null)
                                && (!cellContador.getContents().trim().isEmpty())) {
                            oReceberCheque.setNumerocupom(Integer.parseInt(cellContador.getContents().trim()));
                        } else {
                            oReceberCheque.setNumerocupom(0);
                        }

                        oReceberCheque.setId_tipoalinea(0);
                        vReceberCheque.add(oReceberCheque);
                    }
                }
            }
            stm.close();
            return vReceberCheque;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarReceberCheque(String i_arquivo, int idLoja) throws Exception {
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Cheque Loja " + idLoja+"...");
            vReceberCheque = carregarReceberCheque(i_arquivo, idLoja);
            if (!vReceberCheque.isEmpty()) {
                new ReceberChequeDAO().salvar(vReceberCheque, idLoja);
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
        int linha, idProduto;
        long codigoBarras;
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(2, i);
                    Cell cellEan13 = sheet.getCell(11, i);
                    
                    idProduto = Integer.parseInt(Utils.formataNumero(cellCodigoProduto.getContents().trim()));
                    if ((cellEan13.getContents() != null)
                            && (!cellEan13.getContents().trim().isEmpty())) {
                        if (cellEan13.getContents().trim().length() > 14) {
                            codigoBarras = Long.parseLong(Utils.formataNumero(cellEan13.getContents().trim().substring(0, 14)));
                        } else {
                            codigoBarras = Long.parseLong(Utils.formataNumero(cellEan13.getContents().trim()));
                        }
                    } else {
                        codigoBarras = 0;
                    }

                    if (codigoBarras > 999999) {
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = idProduto;
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.codigoBarras = codigoBarras;
                        oProduto.vAutomacao.add(oAutomacao);
                        vResult.put(codigoBarras, oProduto);
                    }
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarCodigoBarraGetWay(String i_arquivo, int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vCodigoBarras = carregarCodigoBarras(i_arquivo);

            ProgressBar.setMaximum(vCodigoBarras.size());
            for (Long keyId : vCodigoBarras.keySet()) {
                ProdutoVO oProduto = vCodigoBarras.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            produto.alterarBarraAnterio = false;
            produto.verificarLoja = true;
            produto.id_loja = id_loja;
            produto.addCodigoBarras(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }
}


/*
select
m1.grupo merc1_cod,
m1.descricao merc_desc,
m2.subgrupo merc2_cod,
m2.descricao merc2_desc,
'1' merc3_cod,
m2.descricao merc3_desc
from slbdgrup m1
inner join slbdsbgp m2 on m2.grupo = m1.grupo
order by m1.grupo, m2.subgrupo;

select 
p.produto id,
p.barra codigobarras,
'1' qtdembalagem,
p.unidade,
case p.servico when 'B' then 'S' else 'N' end balanca,
p.descricao descricaocompleta,
p.desc_redu descricaoreduzida,
p.desc_etiq descricaogondola,
p.grupo cod_mercadologico1,
p.desc_grp mercadologico1,
p.subgrupo cod_mercadologico2,
p.desc_sbg mercadologico2,
'1' cod_mercadologico3,
p.desc_sbg mercadologico3,
'' cod_mercadologico4,
'' mercadologico4,
'' cod_mercadologico5,
'' mercadologico5,
'' id_familiaproduto,
'' familiaproduto,
'0,00' pesobruto,
'0,00' pesoliquido,
p.dt_cada datacadastro,
p.validad validade,
p.lucro margem,
l.estoqma estoquemaximo,
l.estoqmi estoqueminimo,
l.estoqlo estoque,
l.custopmz custocomimposto,
l.custo custosemimposto,
p.preco precovenda,
case p.inativa when 'N' then 'S' else 'N' end ativo,
p.ncm,
p.cest,
p.pis_cofins piscofins_cst_debito,
p.pis_cofins piscofins_cst_credito,
'' piscofins_natureza_receita,
c.cst icms_cst,
c.aliquota icms_aliquota,
c.redbase icms_reduzido
from slbdprod p
left join slbdploj l on l.produto = p.produto
left join slbdclas c on c.classe = p.classe;

select * from slbdprod;
select * from slbdploj;
select * from slbdclas;

*/