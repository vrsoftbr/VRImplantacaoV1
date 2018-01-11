package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import vrimplantacao.dao.sistema.MunicipioDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.MunicipioVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao2.parametro.Parametros;

public class SoftLineDAO {

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

        } catch (SQLException | NumberFormatException ex) {

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
    
    public List<MercadologicoVO> carregarMercadologico(String i_arquivo, int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        int linha;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    Cell cellMercadologico2 = sheet.getCell(2, i);
                    Cell cellMercadologico1 = sheet.getCell(3, i);
                    Cell cellMercadologico3 = sheet.getCell(4, i);

                    if ((cellMercadologico1.getContents() != null) && (!cellMercadologico1.getContents().trim().isEmpty())
                            && (cellMercadologico2.getContents() != null) && (!cellMercadologico2.getContents().trim().isEmpty()) &&
                            (cellMercadologico3.getContents() != null) && (!cellMercadologico3.getContents().trim().isEmpty())) {

                        if ((Integer.parseInt(cellMercadologico1.getContents().trim()) > 0) &&
                                (Integer.parseInt(cellMercadologico2.getContents().trim()) > 0) && 
                                (Integer.parseInt(cellMercadologico1.getContents().trim()) <= 999) &&
                                (Integer.parseInt(cellMercadologico3.getContents().trim()) > 0)) {
                            
                            MercadologicoVO oMercadologico = new MercadologicoVO();
                            oMercadologico.setDescricao("ACERTAR DESCRICAO");
                            oMercadologico.setNivel(nivel);

                            if (nivel == 1) {
                                oMercadologico.setMercadologico1(Integer.parseInt(cellMercadologico1.getContents().trim()));
                            } else if (nivel == 2) {
                                oMercadologico.setMercadologico1(Integer.parseInt(cellMercadologico1.getContents().trim()));
                                oMercadologico.setMercadologico2(Integer.parseInt(cellMercadologico2.getContents().trim()));
                            } else if (nivel == 3) {
                                oMercadologico.setMercadologico1(Integer.parseInt(cellMercadologico1.getContents().trim()));
                                oMercadologico.setMercadologico2(Integer.parseInt(cellMercadologico2.getContents().trim()));
                                oMercadologico.setMercadologico3(Integer.parseInt(cellMercadologico3.getContents().trim()));
                            }
                            result.add(oMercadologico);
                        }
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarMercadologico(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        ProgressBar.setStatus("Carregando dados...Mercadologico...");
        try {
            vMercadologico = carregarMercadologico(i_arquivo, 1);
            new MercadologicoDAO().salvar2(vMercadologico, false);
            
            vMercadologico = carregarMercadologico(i_arquivo, 2);
            new MercadologicoDAO().salvar2(vMercadologico, false);
            
            vMercadologico = carregarMercadologico(i_arquivo, 3);
            new MercadologicoDAO().salvar2(vMercadologico, false);
            
            //new MercadologicoDAO().salvarMax();
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<MercadologicoVO> carregarDescricaoMercadologico(String i_arquivo, int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        int linha;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha ++;
                    
                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellDescricao = sheet.getCell(1, i);
                    
                    MercadologicoVO oMercadologico = new MercadologicoVO();
                    oMercadologico.setDescricao((cellDescricao.getContents() == null ? "" : cellDescricao.getContents().trim()));
                    
                    if (nivel == 1) {
                        oMercadologico.setMercadologico1(Integer.parseInt(cellCodigo.getContents().trim()));
                    } else if (nivel == 2) {
                        oMercadologico.setMercadologico2(Integer.parseInt(cellCodigo.getContents().trim()));
                    } else if (nivel == 3) {
                        oMercadologico.setMercadologico3(Integer.parseInt(cellCodigo.getContents().trim()));
                    }
                    result.add(oMercadologico);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarDescricaoMercadologico(String i_arquivo, int nivel) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        try {            
            if (nivel == 1) {
                ProgressBar.setStatus("Carregando dados...descricao mercadologico1...");
            } else if (nivel == 2) {
                ProgressBar.setStatus("Carregando dados...descricao mercadologico2...");
            } else if (nivel == 3) {
                ProgressBar.setStatus("Carregando dados...descricao mercadologico3...");
            }
            
            vMercadologico = carregarDescricaoMercadologico(i_arquivo, nivel);
            new MercadologicoDAO().updateDescricao(vMercadologico, nivel);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoVO> carregarNcmProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int linha;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha ++;
                    
                    Cell cellCodProduto     = sheet.getCell(0, i);
                    Cell cellNcm            = sheet.getCell(15, i);
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oProduto.setId(Integer.parseInt(cellCodProduto.getContents().trim()));
                    
                    if ((cellNcm.getContents() != null) && (!cellNcm.getContents().trim().isEmpty())
                            && (cellNcm.getContents().trim().length() > 5)) {

                        NcmVO oNcm = new NcmDAO().validar(cellNcm.getContents().trim());
                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }
                    
                    oAnterior.setCodigoanterior(Double.parseDouble(cellCodProduto.getContents().trim()));
                    oAnterior.setNcm((cellNcm.getContents() == null ? "" : cellNcm.getContents().trim()));
                    
                    oProduto.vCodigoAnterior.add(oAnterior);                    
                    result.add(oProduto);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarNcmProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Ncm Produtos...");
            vProduto = carregarNcmProduto(i_arquivo);
            new ProdutoDAO().alterarNcm(vProduto, 1);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarMercadologicoProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int linha;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha ++;
                    
                    Cell cellCodProduto     = sheet.getCell(0, i);
                    Cell cellMercadologico2 = sheet.getCell(2, i);
                    Cell cellMercadologico1 = sheet.getCell(3, i);
                    Cell cellMercadologico3 = sheet.getCell(4, i);
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.setId(Integer.parseInt(cellCodProduto.getContents().trim()));
                    
                    if ((cellMercadologico1.getContents() != null) && (!cellMercadologico1.getContents().trim().isEmpty())) {
                        oProduto.setMercadologico1(Integer.parseInt(cellMercadologico1.getContents().trim()));
                    } else {
                        oProduto.setMercadologico1(0);
                    }
                    
                    if ((cellMercadologico2.getContents() != null) && (!cellMercadologico2.getContents().trim().isEmpty())) {
                        oProduto.setMercadologico2(Integer.parseInt(cellMercadologico2.getContents().trim()));
                    } else {
                        oProduto.setMercadologico2(0);
                    }
                    
                    if ((cellMercadologico3.getContents() != null) && (!cellMercadologico3.getContents().trim().isEmpty())) {
                        oProduto.setMercadologico3(Integer.parseInt(cellMercadologico3.getContents().trim()));
                    } else {
                        oProduto.setMercadologico3(0);
                    }                    
                    result.add(oProduto);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarMercadologicoProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico Produtos...");
            vProduto = carregarMercadologicoProduto(i_arquivo);
            new ProdutoDAO().alterarMercadologicoProdutoSemCodigoAnterior(vProduto);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int linha;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    Cell cellCodProduto     = sheet.getCell(0, i);
                    Cell cellDescricao      = sheet.getCell(1, i);
                    Cell cellTipo           = sheet.getCell(2, i);
                    Cell cellDepartamento   = sheet.getCell(3, i);
                    Cell cellGrupo          = sheet.getCell(4, i);
                    Cell cellTipoEmbalagem  = sheet.getCell(7, i);
                    Cell cellNcm            = sheet.getCell(15, i);

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setId(Integer.parseInt(cellCodProduto.getContents().trim()));
                    oProduto.setDescricaoCompleta(cellDescricao.getContents().trim());
                    oProduto.setDescricaoReduzida(oProduto.getDescricaoCompleta());
                    oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());
                    
                    if ((cellDepartamento.getContents() != null) && (!cellDepartamento.getContents().trim().isEmpty())) {
                        oProduto.setMercadologico1(Integer.parseInt(cellDepartamento.getContents().trim()));
                    } else {
                        oProduto.setMercadologico1(0);
                    }
                    
                    if ((cellGrupo.getContents() != null) && (!cellGrupo.getContents().trim().isEmpty())) {
                        oProduto.setMercadologico2(Integer.parseInt(cellGrupo.getContents().trim()));
                    } else {
                        oProduto.setMercadologico2(0);
                    }
                    
                    oProduto.setMercadologico3(1);

                    if ((cellNcm.getContents() != null) && (!cellNcm.getContents().trim().isEmpty())
                            && (cellNcm.getContents().trim().length() > 5)) {

                        NcmVO oNcm = new NcmDAO().validar(cellNcm.getContents().trim());
                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    oProduto.setIdFamiliaProduto(-1);
                    oProduto.setMargem(0);
                    oProduto.setQtdEmbalagem(1);
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);

                    long codigoProduto;
                    codigoProduto = Long.parseLong(cellCodProduto.getContents().trim().substring(0, 
                            cellCodProduto.getContents().trim().length() -1));

                    /**
                     * Aparentemente o sistema utiliza o próprio id para
                     * produtos de balança.
                     */
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getId());
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                        oCodigoAnterior.setE_balanca(true);

                        if ("P".equals(produtoBalanca.getPesavel())) {
                            oAutomacao.setIdTipoEmbalagem(4);
                            oProduto.setPesavel(false);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setPesavel(true);
                        }

                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                        oCodigoAnterior.setE_balanca(true);
                    } else {
                        oProduto.setValidade(0);
                        oProduto.setPesavel(false);
                        oCodigoAnterior.setE_balanca(false);
                        oAutomacao.setCodigoBarras(-2);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(cellTipoEmbalagem.getContents().trim()));
                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }
                    
                    oAutomacao.setQtdEmbalagem(1);
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());

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
                    oProduto.setIdTipoPisCofinsDebito(1);
                    oProduto.setIdTipoPisCofinsCredito(13);
                    oProduto.setTipoNaturezaReceita(999);

                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(1);

                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.setIdEstado(uf.getId());
                    oAliquota.setIdAliquotaDebito(8);
                    oAliquota.setIdAliquotaCredito(8);
                    oAliquota.setIdAliquotaDebitoForaEstado(8);
                    oAliquota.setIdAliquotaCreditoForaEstado(8);
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(8);

                    oCodigoAnterior.setCodigoanterior(oProduto.getId());
                    oCodigoAnterior.setNcm(cellNcm.getContents());
                    result.add(oProduto);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProduto(String i_arquivo, int idLojaVR) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();
        ProgressBar.setStatus("Carregando dados...Produtos...");
        List<ProdutoVO> vProdutos = carregarProduto(i_arquivo, idLojaVR);
        List<LojaVO> vLoja = new LojaDAO().carregar();
        produto.implantacaoExterna = true;
        produto.salvar(vProdutos, idLojaVR, vLoja);
    }

    public List<ProdutoVO> carregarProdutoImposto(String i_arquivo) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int linha;
        String perRedInt, perRedDec, perReducao;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    Cell cellCodProduto = sheet.getCell(0, i);
                    Cell cellStIcms = sheet.getCell(2, i);
                    Cell cellValorIcms = sheet.getCell(4, i);
                    Cell cellPercRedIcms = sheet.getCell(6, i);
                    Cell cellCstPis = sheet.getCell(11, i);
                    Cell cellCstCofins = sheet.getCell(14, i);

                    perReducao = "0";
                    perRedInt = "";
                    perRedDec = "";

                    if (cellPercRedIcms.getContents().trim().length() == 4) {
                        perRedInt = cellPercRedIcms.getContents().trim().substring(0, 2);
                        perRedDec = cellPercRedIcms.getContents().trim().substring(
                                cellPercRedIcms.getContents().trim().length() - 2);
                        perReducao = perRedInt + "." + perRedDec;
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                    oProduto.setId(Integer.parseInt(cellCodProduto.getContents().trim()));

                    if ((cellCstPis.getContents() != null) && (!cellCstPis.getContents().trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(Integer.parseInt(cellCstPis.getContents().trim())));
                        oAnterior.setPiscofinsdebito(Integer.parseInt(cellCstPis.getContents().trim()));
                    } else {
                        oProduto.setIdTipoPisCofinsDebito(1);
                        oAnterior.setPiscofinsdebito(-1);
                    }

                    if ((cellCstCofins.getContents() != null) && (!cellCstCofins.getContents().trim().isEmpty())) {
                        oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(Integer.parseInt(cellCstCofins.getContents().trim())));
                        oAnterior.setPiscofinscredito(Integer.parseInt(cellCstCofins.getContents().trim()));
                    } else {
                        oProduto.setIdTipoPisCofinsCredito(13);
                        oAnterior.setPiscofinscredito(-1);
                    }

                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));

                    if ((cellStIcms.getContents() != null) && (!cellStIcms.getContents().trim().isEmpty())) {
                        oAliquota.setIdAliquotaDebito(Utils.getAliquotaIcms(Integer.parseInt(cellStIcms.getContents().trim()),
                                Double.parseDouble(cellValorIcms.getContents().trim()), Double.parseDouble(perReducao)));
                        oAliquota.setIdAliquotaCredito(Utils.getAliquotaIcms(Integer.parseInt(cellStIcms.getContents().trim()),
                                Double.parseDouble(cellValorIcms.getContents().trim()), Double.parseDouble(perReducao)));
                        oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaIcms(Integer.parseInt(cellStIcms.getContents().trim()),
                                Double.parseDouble(cellValorIcms.getContents().trim()), Double.parseDouble(perReducao)));
                        oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaIcms(Integer.parseInt(cellStIcms.getContents().trim()),
                                Double.parseDouble(cellValorIcms.getContents().trim()), Double.parseDouble(perReducao)));
                        oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaIcms(Integer.parseInt(cellStIcms.getContents().trim()),
                                Double.parseDouble(cellValorIcms.getContents().trim()), Double.parseDouble(perReducao)));
                    } else {
                        oAliquota.setIdAliquotaDebito(8);
                        oAliquota.setIdAliquotaCredito(8);
                        oAliquota.setIdAliquotaDebitoForaEstado(8);
                        oAliquota.setIdAliquotaCreditoForaEstado(8);
                        oAliquota.setIdAliquotaDebitoForaEstadoNF(8);
                    }

                    oProduto.vAliquota.add(oAliquota);

                    if ((cellStIcms.getContents() != null) && (!cellStIcms.getContents().trim().isEmpty())
                            && (Integer.parseInt(cellStIcms.getContents().trim()) == 0)) {

                        oAnterior.setRef_icmsdebito(cellValorIcms.getContents().trim());
                    } else {
                        oAnterior.setRef_icmsdebito(cellStIcms.getContents().trim());
                    }

                    oProduto.vCodigoAnterior.add(oAnterior);
                    result.add(oProduto);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoImposto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos - Impostos...");
            vProduto = carregarProdutoImposto(i_arquivo);
            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoVO> carregarCodigoBarras(String i_arquivo) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        long codigoBarras;
        int linha;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    Cell cellCodProduto = sheet.getCell(0, i);
                    Cell cellCodigoBarras = sheet.getCell(3, i);

                    if ((cellCodigoBarras.getContents() != null)
                            && (!cellCodigoBarras.getContents().trim().isEmpty())
                            && (cellCodProduto.getContents() != null)
                            && (!cellCodProduto.getContents().trim().isEmpty())) {

                        if (Long.parseLong(Utils.formataNumero(cellCodigoBarras.getContents().trim())) >= 1000000) {
                            codigoBarras = Long.parseLong(Utils.formataNumero(cellCodigoBarras.getContents().trim()));
                        } else {
                            codigoBarras = -2;
                        }

                        ProdutoVO oProduto = new ProdutoVO();
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oProduto.setId(Integer.parseInt(cellCodProduto.getContents().trim()));
                        oAutomacao.setCodigoBarras(codigoBarras);
                        oProduto.vAutomacao.add(oAutomacao);
                        result.add(oProduto);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCodigoBarra(String i_arquivo) throws Exception {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Código de Barras...");
            vProduto = carregarCodigoBarras(i_arquivo);
            produtoDAO.alterarBarraAnterio = true;
            produtoDAO.addCodigoBarras(vProduto);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoVO> carregarCustoPrecoMargemProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int linha;
        String margemInt, margemDec, precoInt, precoDec, custoInt, custoDec,
                margem, precoVenda, custo;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    Cell cellCodProduto = sheet.getCell(0, i);
                    Cell cellMargemInt = sheet.getCell(11, i);
                    Cell cellMargemDec = sheet.getCell(12, i);
                    Cell cellPrecoInt = sheet.getCell(8, i);
                    Cell cellPrecoDec = sheet.getCell(9, i);
                    Cell cellCustoInt = sheet.getCell(21, i);
                    Cell cellCustoDec = sheet.getCell(22, i);

                    if ((cellCodProduto.getContents() != null) && (!cellCodProduto.getContents().trim().isEmpty())) {
                        margemInt = "0";
                        margemDec = "0";
                        margem = "0";
                        precoInt = "0";
                        precoDec = "0";
                        precoVenda = "0";
                        custoInt = "0";
                        custoDec = "0";
                        custo = "0";

                        if ((cellMargemInt.getContents() != null) && (!cellMargemInt.getContents().trim().isEmpty())) {
                            margemInt = cellMargemInt.getContents().trim();
                        }
                        if ((cellMargemDec.getContents() != null) && (!cellMargemDec.getContents().trim().isEmpty())) {
                            margemDec = cellMargemDec.getContents().trim();
                        }
                        if ((cellCustoInt.getContents() != null) && (!cellCustoInt.getContents().trim().isEmpty())) {
                            custoInt = cellCustoInt.getContents().trim();
                        }
                        if ((cellCustoDec.getContents() != null) && (!cellCustoDec.getContents().trim().isEmpty())) {
                            custoDec = cellCustoDec.getContents().trim();
                        }
                        if ((cellPrecoInt.getContents() != null) && (!cellPrecoInt.getContents().trim().isEmpty())) {
                            precoInt = cellPrecoInt.getContents().trim();
                        }
                        if ((cellPrecoDec.getContents() != null) && (!cellPrecoDec.getContents().trim().isEmpty())) {
                            precoDec = cellPrecoDec.getContents().trim();
                        }

                        margem = margemInt + "." + margemDec;
                        custo = custoInt + "." + custoDec;
                        precoVenda = precoInt + "." + precoDec;

                        ProdutoVO oProduto = new ProdutoVO();
                        ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                        oProduto.setId(Integer.parseInt(Utils.formataNumero(cellCodProduto.getContents().trim())));
                        oProduto.setMargem(Double.parseDouble(margem));
                        oComplemento.setIdLoja(idLojaVR);
                        oComplemento.setPrecoVenda(Double.parseDouble(precoVenda));
                        oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                        oComplemento.setCustoComImposto(Double.parseDouble(custo));
                        oComplemento.setCustoSemImposto(oComplemento.getCustoComImposto());
                        oProduto.vComplemento.add(oComplemento);
                        result.add(oProduto);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCustoPrecoMargemProduto(String i_arquivo, int idLojaVR, int Tipo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Preço, Custo, Margem...");
            vProduto = carregarCustoPrecoMargemProduto(i_arquivo, idLojaVR);
            new ProdutoDAO().alterarPrecoCustoTributacaoEstoque(vProduto, idLojaVR, Tipo);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<FornecedorVO> carregarFornecedor(String i_arquivo) throws Exception {
        List<FornecedorVO> result = new ArrayList<>();
        int linha, id_estado, id_municipio;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    Cell cellIdFornecedor = sheet.getCell(0, i);
                    Cell cellRazaoSocial = sheet.getCell(1, i);
                    Cell cellNomeFantasia = sheet.getCell(2, i);
                    Cell cellEndereco = sheet.getCell(3, i);
                    Cell cellBairro = sheet.getCell(4, i);
                    Cell cellCodCidade = sheet.getCell(5, i);
                    Cell cellCep = sheet.getCell(6, i);
                    Cell cellTelefone = sheet.getCell(7, i);
                    Cell cellTelefone2 = sheet.getCell(8, i);
                    Cell cellIdTipoInscricao = sheet.getCell(9, i);
                    Cell cellCnpjCpf = sheet.getCell(10, i);
                    Cell cellInscricaoEstadual = sheet.getCell(11, i);
                    Cell cellEmail = sheet.getCell(13, i);
                    Cell cellDataCadastro = sheet.getCell(15, i);

                    FornecedorVO oFornecedor = new FornecedorVO();
                    oFornecedor.setId(Integer.parseInt(cellIdFornecedor.getContents().trim()));
                    oFornecedor.setCodigoanterior(oFornecedor.getId());
                    oFornecedor.setRazaosocial((cellRazaoSocial.getContents() == null ? "" : cellRazaoSocial.getContents().trim()));
                    oFornecedor.setNomefantasia((cellNomeFantasia.getContents() == null ? "" : cellNomeFantasia.getContents().trim()));
                    oFornecedor.setEndereco((cellEndereco.getContents() == null ? "" : cellEndereco.getContents().trim()));
                    oFornecedor.setBairro((cellBairro.getContents() == null ? "" : cellBairro.getContents().trim()));

                    if ((cellCep.getContents() != null) && (cellCep.getContents().trim().isEmpty())) {
                        oFornecedor.setCep(Long.parseLong(Utils.formataNumero(cellCep.getContents().trim())));
                    } else {
                        oFornecedor.setCep(Parametros.get().getCepPadrao());
                    }

                    id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                    id_municipio = Parametros.get().getMunicipioPadrao().getId(); // CIDADE DO CLIENTE
                    oFornecedor.setId_estado(id_estado);
                    oFornecedor.setId_municipio(id_municipio);

                    oFornecedor.setTelefone((cellTelefone.getContents() == null ? "" : cellTelefone.getContents().trim()));
                    oFornecedor.setTelefone2((cellTelefone.getContents() == null ? "" : cellTelefone2.getContents().trim()));
                    oFornecedor.setEmail((cellEmail.getContents() == null ? "" : cellEmail.getContents().trim()));
                    oFornecedor.setId_tipoinscricao(("1".equals(cellIdTipoInscricao.getContents().trim()) ? 0 : 1));

                    if ((cellCnpjCpf.getContents() != null) && (!cellCnpjCpf.getContents().trim().isEmpty())) {
                        if (Long.parseLong(cellCnpjCpf.getContents()) < 9) {
                            oFornecedor.setCnpj(-1);
                        } else {
                            oFornecedor.setCnpj(Long.parseLong(Utils.formataNumero(cellCnpjCpf.getContents().trim())));
                        }
                    } else {
                        oFornecedor.setCnpj(-1);
                    }

                    oFornecedor.setInscricaoestadual((cellInscricaoEstadual.getContents() == null ? "" : cellInscricaoEstadual.getContents().trim()));

                    if ((cellCodCidade.getContents() != null) && (!cellCodCidade.getContents().trim().isEmpty())) {
                        oFornecedor.setCodigocidade_sistemaanterior(Integer.parseInt(cellCodCidade.getContents().trim()));
                    } else {
                        oFornecedor.setCodigocidade_sistemaanterior(0);
                    }
                    result.add(oFornecedor);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarFornecedor(String arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedor(arquivo);
            FornecedorDAO fornecedor = new FornecedorDAO();
            fornecedor.salvarCodigoCidadeSistemaAnterior = true;
            fornecedor.salvarCnpj(vFornecedor);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ProdutoFornecedorVO> carregarProdutoFornecedor(String i_arquivo) throws Exception {
        List<ProdutoFornecedorVO> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        java.sql.Date dataAlteracao;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    dataAlteracao = new Date(new java.util.Date().getTime());
                    Cell cellCodFornecedor = sheet.getCell(0, i);
                    Cell cellCodProduto = sheet.getCell(1, i);
                    Cell cellCodigoExterno = sheet.getCell(3, i);

                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                    oProdutoFornecedor.setId_produto(Integer.parseInt(cellCodProduto.getContents().trim()));
                    oProdutoFornecedor.setId_fornecedor(Integer.parseInt(cellCodFornecedor.getContents().trim()));
                    oProdutoFornecedor.setCodigoexterno(cellCodigoExterno.getContents().trim());
                    oProdutoFornecedor.setDataalteracao(dataAlteracao);
                    result.add(oProdutoFornecedor);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoFornecedor(String i_arquivo) throws Exception {
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            vProdutoFornecedor = carregarProdutoFornecedor(i_arquivo);
            new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ClientePreferencialVO> carregarClientePreferencial(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        int linha, id_estado, id_municipio;
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    Cell cellIdCliente = sheet.getCell(0, i);
                    Cell cellRazaoSocial = sheet.getCell(1, i);
                    Cell cellNomeFantasia = sheet.getCell(2, i);
                    Cell cellEndereco = sheet.getCell(3, i);
                    Cell cellBairro = sheet.getCell(4, i);
                    Cell cellCodCidade = sheet.getCell(5, i);
                    Cell cellCep = sheet.getCell(6, i);
                    Cell cellTelefone = sheet.getCell(11, i);
                    Cell cellTelefone2 = sheet.getCell(12, i);
                    Cell cellCelular = sheet.getCell(13, i);
                    Cell cellIdTipoInscricao = sheet.getCell(14, i);
                    Cell cellCnpjCpf = sheet.getCell(15, i);
                    Cell cellInscricaoEstadual = sheet.getCell(16, i);
                    Cell cellNomePai = sheet.getCell(18, i);
                    Cell cellNomeMae = sheet.getCell(19, i);
                    Cell cellNomeEmpresa = sheet.getCell(20, i);
                    Cell cellEndEmpresa = sheet.getCell(21, i);
                    Cell cellTelelefoneEmpresa = sheet.getCell(22, i);
                    Cell cellCargo = sheet.getCell(24, i);

                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                    oClientePreferencial.setId(Integer.parseInt(cellIdCliente.getContents().trim()));
                    oClientePreferencial.setCodigoanterior(oClientePreferencial.getId());
                    oClientePreferencial.setNome((cellRazaoSocial.getContents() == null ? "" : cellRazaoSocial.getContents().trim()));
                    oClientePreferencial.setEndereco((cellEndereco.getContents() == null ? "" : cellEndereco.getContents().trim()));
                    oClientePreferencial.setBairro((cellBairro.getContents() == null ? "" : cellBairro.getContents().trim()));

                    if ((cellCep.getContents() != null) && (!cellCep.getContents().trim().isEmpty())) {
                        oClientePreferencial.setCep(Long.parseLong(Utils.formataNumero(cellCep.getContents().trim())));
                    } else {
                        oClientePreferencial.setCep(Parametros.get().getCepPadrao());
                    }

                    id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                    id_municipio = Parametros.get().getMunicipioPadrao().getId(); // CIDADE DO CLIENTE;                   
                    oClientePreferencial.id_estado = id_estado;
                    oClientePreferencial.id_municipio = id_municipio;

                    oClientePreferencial.setTelefone((cellTelefone.getContents() == null ? "" : cellTelefone.getContents().trim()));
                    oClientePreferencial.setTelefone2((cellTelefone2.getContents() == null ? "" : cellTelefone2.getContents().trim()));
                    oClientePreferencial.setCelular((cellCelular.getContents() == null ? "" : cellCelular.getContents().trim()));

                    if ("1".equals(cellIdTipoInscricao.getContents())) {
                        oClientePreferencial.setId_tipoinscricao(0);
                    } else {
                        oClientePreferencial.setId_tipoinscricao(1);
                    }

                    if ((cellCnpjCpf.getContents() != null) && (!cellCnpjCpf.getContents().trim().isEmpty())) {

                        if ("0".equals(cellCnpjCpf.getContents().trim())) {
                            oClientePreferencial.setCnpj(-1);
                        } else {
                            oClientePreferencial.setCnpj(Long.parseLong(Utils.formataNumero(cellCnpjCpf.getContents().trim())));
                        }
                    } else {
                        oClientePreferencial.setCnpj(-1);
                    }

                    oClientePreferencial.setInscricaoestadual((cellInscricaoEstadual.getContents() == null ? "" : cellInscricaoEstadual.getContents().trim()));
                    oClientePreferencial.setNomepai((cellNomePai.getContents() == null ? "" : cellNomePai.getContents().trim()));
                    oClientePreferencial.setNomemae((cellNomeMae.getContents() == null ? "" : cellNomeMae.getContents().trim()));
                    oClientePreferencial.setEmpresa((cellNomeEmpresa.getContents() == null ? "" : cellNomeEmpresa.getContents().trim()));
                    oClientePreferencial.setEnderecoempresa((cellEndEmpresa.getContents() == null ? "" : cellEndEmpresa.getContents().trim()));
                    oClientePreferencial.setCargo((cellCargo.getContents() == null ? "" : cellCargo.getContents().trim()));
                    oClientePreferencial.setTelefoneempresa((cellTelelefoneEmpresa.getContents() == null ? "" : Utils.formataNumero(cellTelelefoneEmpresa.getContents().trim())));

                    if ("SEM NOME VR".equals(oClientePreferencial.getNome())) {
                        if ((cellNomeFantasia.getContents() != null) && (!cellNomeFantasia.getContents().trim().isEmpty())) {
                            oClientePreferencial.setNome(cellNomeFantasia.getContents().trim());
                        }
                    }

                    if ((cellCodCidade.getContents() != null) && (!cellCodCidade.getContents().trim().isEmpty())) {
                        oClientePreferencial.setCodigocidade_sistemaanterior(Integer.parseInt(cellCodCidade.getContents().trim()));
                    } else {
                        oClientePreferencial.setCodigocidade_sistemaanterior(0);
                    }

                    result.add(oClientePreferencial);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarClientePreferencial(String arquivo, int id_loja) throws Exception {
        try {
            ClientePreferencialDAO clientePreferencial = new ClientePreferencialDAO();
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClientePreferencial(arquivo);
            new PlanoDAO().salvar(id_loja);
            clientePreferencial.salvarCodigoCidadeSistemaAnterior = true;
            clientePreferencial.salvar(vClientePreferencial, id_loja, 1);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<MunicipioVO> carregarCidade(String i_arquivo) throws Exception {
        List<MunicipioVO> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        int linha;
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    Cell cellCodCidade = sheet.getCell(0, i);
                    Cell cellDescricao = sheet.getCell(1, i);
                    Cell cellEstado = sheet.getCell(2, i);
                    Cell cellCodigoIbge = sheet.getCell(3, i);

                    MunicipioVO oMunicipio = new MunicipioVO();
                    oMunicipio.setId(Integer.parseInt(cellCodCidade.getContents().trim()));
                    oMunicipio.setCodigoIbge(Integer.parseInt(cellCodigoIbge.getContents().trim()));
                    oMunicipio.setDescricao(Utils.acertarTexto(cellDescricao.getContents().trim()));
                    oMunicipio.setDescricaoEstado(Utils.acertarTexto(cellEstado.getContents().trim()));
                    oMunicipio.setIdEstado(Integer.parseInt(cellCodigoIbge.getContents().trim().substring(0, 2)));
                    result.add(oMunicipio);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCidade(String i_arquivo) throws Exception {
        List<MunicipioVO> vMunicipio = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cidade/Estados...");
            vMunicipio = carregarCidade(i_arquivo);
            new MunicipioDAO().updateCidadeEstadoPessoas(vMunicipio);
        } catch (Exception ex) {
            throw ex;
        }
    }
}