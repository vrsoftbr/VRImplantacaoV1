package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.dao.cadastro.ClienteEventuallDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClienteEventualVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.parametro.Parametros;

public class SoftClass_AutoComDAO {
    
    public String Texto;
    private ConexaoDBF connDBF = new ConexaoDBF();

    public List<ProdutoVO> carregarListaProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int cstSaida, cstEntrada;
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO, DESCRICAO, CADASTRO, CUSTO, VENDA_ATU, MARGEM, "
                            + "BARRA, APRESENTAC, VALIDADE, C_FISCAL, SIT_TRIBUT, "
                            + "ALIQ_ICMS, ALIQ_ICMRD "
                            + "FROM ARQ0006"
            )) {
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                
                while (rst.next()) {
                    /*if ((rst.getString("SIT_TRIBUT") != null) &&
                            (!rst.getString("SIT_TRIBUT").trim().isEmpty())) {
                        cstSaida = rst.getInt("SIT_TRIBUT");
                        cstEntrada = rst.getInt("SIT_TRIBUT");
                    } else {
                        cstSaida = 90;
                        cstEntrada = 90;                                
                    }*/
                    
                    
                    if (rst.getDouble("ALIQ_ICMS") != 0) {
                        cstSaida = 0;
                        cstEntrada = 0;                        
                    } else {                    
                        cstSaida = 40;
                        cstEntrada = 40;
                    }
                    
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
                    
                    oProduto.setId(rst.getInt("CODIGO"));
                    oProduto.setDescricaoCompleta(rst.getString("DESCRICAO").trim());
                    oProduto.setDescricaoReduzida(rst.getString("DESCRICAO").trim());
                    oProduto.setDescricaoGondola(rst.getString("DESCRICAO").trim());
                    oProduto.setMercadologico1(14);
                    oProduto.setMercadologico1(1);
                    oProduto.setMercadologico1(1);
                    oProduto.setIdTipoPisCofinsDebito(1);
                    oProduto.setIdTipoPisCofinsCredito(13);
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), ""));
                    
                    if ((rst.getString("C_FISCAL") != null)
                            && (!rst.getString("C_FISCAL").isEmpty())
                            && (rst.getString("C_FISCAL").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(Utils.formataNumero(rst.getString("C_FISCAL").trim()));
                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }
                    
                    oProduto.setIdFamiliaProduto(-1);
                    oProduto.setMargem(rst.getDouble("MARGEM"));
                    oProduto.setQtdEmbalagem(1);
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    
                    long codigoProduto;

                    if ((rst.getString("CODIGO") != null)
                            && (!rst.getString("CODIGO").trim().isEmpty())) {

                        codigoProduto = Long.parseLong(Utils.formataNumero(
                                Utils.formataNumero(rst.getString("CODIGO").trim())));
                    } else {
                        codigoProduto = -2;
                    }
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

                        if ((rst.getString("BARRA") != null)
                                && (!rst.getString("BARRA").trim().isEmpty())) {

                            if (Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim())) >= 1000000) {
                                oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim())));
                            } else {
                                oAutomacao.setCodigoBarras(-2);
                            }
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }

                        if ((rst.getString("APRESENTAC") != null)
                                && (!rst.getString("APRESENTAC").trim().isEmpty())) {

                            oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem((rst.getString("APRESENTAC").trim().length() > 2 ? rst.getString("DESCRICAO").trim().substring(0, 2)
                                    : rst.getString("APRESENTAC").trim())));
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                        }
                        
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
                    oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));

                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setPrecoVenda(rst.getDouble("VENDA_ATU"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("VENDA_ATU"));
                    oComplemento.setCustoComImposto(rst.getDouble("CUSTO"));
                    oComplemento.setCustoSemImposto(rst.getDouble("CUSTO"));
                    
                    EstadoVO uf = Parametros.get().getUfPadrao();
                    oAliquota.idEstado = uf.getId();
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("ALIQ_ICMS"), rst.getDouble("ALIQ_ICMRD")));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("ALIQ_ICMS"), rst.getDouble("ALIQ_ICMRD")));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("ALIQ_ICMS"), rst.getDouble("ALIQ_ICMRD")));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(uf.getSigla(), cstEntrada, rst.getDouble("ALIQ_ICMS"), rst.getDouble("ALIQ_ICMRD")));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(uf.getSigla(), cstSaida, rst.getDouble("ALIQ_ICMS"), rst.getDouble("ALIQ_ICMRD")));
                    
                    oCodigoAnterior.setCodigoanterior(rst.getDouble("CODIGO"));
                    if ((rst.getString("BARRA") != null)
                            && (!rst.getString("BARRA").trim().isEmpty())) {
                        oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("BARRA").trim())));
                    } else {
                        oCodigoAnterior.setBarras(-2);
                    }

                    oCodigoAnterior.setNcm(rst.getString("C_FISCAL") == null ? ""
                            : Utils.formataNumero(rst.getString("C_FISCAL").trim()));
                    oCodigoAnterior.setId_loja(idLojaVR);

                    result.add(oProduto);                    
                }
            }
        }
        return result;
    }
    
    public void importarProduto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();        
        try {
            ProgressBar.setStatus("Carregando dados...Produtos...");            
            vProdutoNovo = carregarListaProduto(i_arquivo, idLoja);            
            ProgressBar.setMaximum(vProdutoNovo.size());            
            List<LojaVO> vLoja = new LojaDAO().carregar();            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.implantacaoExterna = true;
            produtoDAO.salvar(vProdutoNovo, idLoja, vLoja);            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarListaCustoProduto(String i_arquivo, int idLojaVr) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);        
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO, CUSTO FROM ARQ0006"
            )){
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.setId(rst.getInt("CODIGO"));
                    oComplemento.setCustoComImposto(rst.getDouble("CUSTO"));
                    oComplemento.setCustoSemImposto(rst.getDouble("CUSTO"));
                    oComplemento.setIdLoja(idLojaVr);
                    result.add(oProduto);
                }
            }
        }
        return result;
    }

    public void importarCusto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoCusto = new ArrayList<>();
        
        try {            
            ProgressBar.setStatus("Carregando dados...Custo...Produtos...");
            vProdutoCusto = carregarListaCustoProduto(i_arquivo, idLoja);
            ProgressBar.setMaximum(vProdutoCusto.size());            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarCustoProduto(vProdutoCusto, idLoja);            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarListaPrecoProduto(String i_arquivo, int idLojaVr) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO, VENDA_ATU FROM ARQ0006"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.setId(rst.getInt("CODIGO"));
                    oComplemento.setPrecoVenda(rst.getDouble("VENDA_ATU"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("VENDA_ATU"));
                    oComplemento.setIdLoja(idLojaVr);
                    result.add(oProduto);
                }
            }
        }
        return result;
    }

    public void importarPrecoVenda(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoPrecoVenda = new ArrayList<>();        
        try {            
            ProgressBar.setStatus("Carregando dados...Preço Produtos...");
            vProdutoPrecoVenda = carregarListaPrecoProduto(i_arquivo, idLoja);
            ProgressBar.setMaximum(vProdutoPrecoVenda.size());
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarPrecoProduto(vProdutoPrecoVenda, idLoja);
            
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarListaEstoqueProduto(String i_arquivo, int idLojaVr) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO, ESTOQUE, MINIMO FROM ARQ0006"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.setId(rst.getInt("CODIGO"));
                    oComplemento.setEstoque(rst.getDouble("ESTOQUE"));
                    oComplemento.setEstoqueMinimo(rst.getDouble("MINIMO"));
                    oComplemento.setIdLoja(idLojaVr);
                    result.add(oProduto);
                }
            }
        }
        return result;
    }

    public void importarEstoqueProduto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoEstoque = new ArrayList<>();        
        try {            
            ProgressBar.setStatus("Carregando dados...Estoque Produtos...");
            vProdutoEstoque = carregarListaEstoqueProduto(i_arquivo, idLoja);
            ProgressBar.setMaximum(vProdutoEstoque.size());            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarEstoqueProduto(vProdutoEstoque, idLoja);
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Long, ProdutoVO> carregarCodigoBarras(String i_arquivo) throws Exception {
        Map<Long, ProdutoVO> result = new HashMap<>();
        long codigoBarras = -2;
        String strCodigoBarras = "";
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT BARRA FROM ARQ0060"
            )) {
                while (rst.next()) {
                    if ((rst.getString("CODIGO") != null) &&
                            (!rst.getString("CODIGO").trim().isEmpty())) {                        
                        codigoBarras = -2;
                        if ((rst.getString("BARRA") != null)
                                && (!rst.getString("BARRA").trim().isEmpty())) {
                            strCodigoBarras = Utils.formataNumero(rst.getString("BARRA").trim());
                            if (strCodigoBarras.length() < 7) {
                                codigoBarras = -2;
                            } else {
                                if (strCodigoBarras.length() > 14) {
                                    codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                                } else {
                                    codigoBarras = Long.parseLong(strCodigoBarras);
                                }
                            }
                        }
                        
                        if (String.valueOf(codigoBarras).length() >= 7) {
                            ProdutoVO oProduto = new ProdutoVO();
                            oProduto.setId(rst.getInt("CODIGO"));
                            ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                            oAutomacao.setCodigoBarras(codigoBarras);
                            oProduto.vAutomacao.add(oAutomacao);
                            result.put(codigoBarras, oProduto);
                        }                        
                    }
                }
            }
        }
        return result;
    }

    public void importarCodigoBarras(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarras(i_arquivo);
            ProgressBar.setMaximum(vCodigoBarra.size());
            for (Long keyId : vCodigoBarra.keySet()) {
                ProdutoVO oProduto = vCodigoBarra.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            produto.addCodigoBarras(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto = 0;
        long codigobarras = -1;
        Utils util = new Utils();

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
    
    public List<ClientePreferencialVO> carregarListaDeClientePreferencial(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();
        int id_estado, id_municipio;
        connDBF.abrirConexao(i_arquivo);
        try (Statement stm = connDBF.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO, NOME, ENDERECO1, BAIRRO, CIDADE, ESTADO, "
                            + "FONE, FAX, CEP, CPF, RG, OBS1, LIMITE, SALDO, "
                            + "NASCTO, ATIVO, SEXO, ESTCIV, PAI, MAE, EMPRESA, "
                            + "FONE_EMP, CARGO_EMP, CONJUGE, RG_CONJ, CPF_CONJ, "
                            + "EMP_CONJ, CGC, IE, TIPO, RAZAO, RENDA, COMPLEMENT, "
                            + "CELULAR, RG_EMISSAO, RG_ORGAO, CADASTRO, EMAIL "
                            + "FROM ARQ0001"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                    oClientePreferencial.setId(rst.getInt("CODIGO"));
                    oClientePreferencial.setCodigoanterior(rst.getLong("CODIGO"));
                    
                    if ((rst.getString("RAZAO") != null) &&
                            (!rst.getString("RAZAO").trim().isEmpty())) {
                        oClientePreferencial.setNome(rst.getString("RAZAO") == null ? "" : rst.getString("RAZAO").trim());
                    } else {
                        oClientePreferencial.setNome(rst.getString("NOME") == null ? "" : rst.getString("NOME").trim());
                    }
                    oClientePreferencial.setEndereco(rst.getString("ENDERECO1") == null ? "" : rst.getString("ENDERECO1").trim());
                    oClientePreferencial.setBairro(rst.getString("BAIRRO") == null ? "" : rst.getString("BAIRRO").trim());
                    
                    if ((rst.getString("CIDADE") != null) && (rst.getString("ESTADO") != null)) {
                        id_estado = Utils.retornarEstadoDescricao(rst.getString("ESTADO"));
                        if (id_estado == 0) {
                            id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                        }
                        id_municipio = Utils.retornarMunicipioIBGEDescricao(rst.getString("CIDADE"), rst.getString("ESTADO").toString());
                        if (id_municipio == 0) {
                            id_municipio = Parametros.get().getMunicipioPadrao().getId();// CIDADE DO CLIENTE;
                        }
                    } else {
                        id_estado = Parametros.get().getUfPadrao().getId(); // ESTADO ESTADO DO CLIENTE
                        id_municipio = Parametros.get().getMunicipioPadrao().getId(); // CIDADE DO CLIENTE;                   
                    }
                    
                    oClientePreferencial.id_estado = id_estado;
                    oClientePreferencial.id_municipio = id_municipio;
                    
                    if ((rst.getString("CEP") != null) &&
                            (!rst.getString("CEP").trim().isEmpty())) {
                        oClientePreferencial.setCep(Long.parseLong(Utils.formataNumero(rst.getString("CEP").trim())));
                    } else {
                        oClientePreferencial.setCep(Parametros.get().getCepPadrao());
                    }
                    
                    oClientePreferencial.setTelefone(rst.getString("FONE") == null ? "" : Utils.formataNumero(rst.getString("FONE").trim()));
                    oClientePreferencial.setFax(rst.getString("FAX") == null ? "" : Utils.formataNumero(rst.getString("FAX").trim()));
                    oClientePreferencial.setId_tipoinscricao(rst.getString("TIPO").contains("F") ? 1 : 0);
                    
                    if (oClientePreferencial.getId_tipoinscricao() == 1) {
                        if ((rst.getString("CPF") != null) &&
                                (!rst.getString("CPF").trim().isEmpty())) {
                            oClientePreferencial.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CPF").trim())));
                        } else {
                            oClientePreferencial.setCnpj(-1);
                        }
                        
                        if ((rst.getString("RG") != null) &&
                                (!rst.getString("RG").trim().isEmpty())) {
                            oClientePreferencial.setInscricaoestadual(rst.getString("RG").trim());
                        } else {
                            oClientePreferencial.setInscricaoestadual("ISENTO");
                        }
                    } else {
                        if ((rst.getString("CGC") != null) &&
                                (!rst.getString("CGC").trim().isEmpty())) {
                            oClientePreferencial.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CGC").trim())));
                        } else {
                            oClientePreferencial.setCnpj(-1);
                        }
                        
                        if ((rst.getString("IE") != null) &&
                                (!rst.getString("IE").trim().isEmpty())) {
                            oClientePreferencial.setInscricaoestadual(rst.getString("IE").trim());
                        } else {
                            oClientePreferencial.setInscricaoestadual("ISENTO");
                        }
                    }
                    
                    oClientePreferencial.setValorlimite(rst.getDouble("LIMITE"));
                    oClientePreferencial.setNomemae(rst.getString("MAE") == null ? "" : rst.getString("MAE").trim());
                    oClientePreferencial.setNomepai(rst.getString("PAI") == null ? "" : rst.getString("PAI").trim());
                    oClientePreferencial.setNomeconjuge(rst.getString("CONJUGE") == null ? "" : rst.getString("CONJUGE").trim());
                    oClientePreferencial.setObservacao(rst.getString("OBS1") == null ? "" : rst.getString("OBS1").trim());
                    oClientePreferencial.setId_situacaocadastro(rst.getString("ATIVO").contains("S") ? 1 : 0);
                    oClientePreferencial.setNumero("0");
                    result.add(oClientePreferencial);
                }
            }
        }
        return result;
    }
    
    public void importarClientePreferencial(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            vClientePreferencial = carregarListaDeClientePreferencial(i_arquivo);
            if (!vClientePreferencial.isEmpty()) {
                new PlanoDAO().salvar(1);
                new ClientePreferencialDAO().salvar(vClientePreferencial, 1, 1);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    
    
    
    
    
    private List<ProdutoVO> carregarPisCofinsProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        int idTipoPisCofins = 0, 
            idTipoPisCofinsCredito = 0, tipoNaturezaReceita = 0, idProduto, 
            pisCofinsDebitoAnt, pisCofinsCreditoAnt;
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            

            sql = new StringBuilder();
            sql.append("select e.plu, e.piscst, e.cofinscst ");
            sql.append(" from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");
            
            //sql.append(" order by e.plu ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("plu") != null)
                        && (!rst.getString("plu").trim().isEmpty())
                        && (!"0".equals(rst.getString("plu").trim()))) {

                    idProduto = Integer.parseInt(rst.getString("plu"));

                    if ((rst.getString("piscst") != null)
                            && (!rst.getString("piscst").trim().isEmpty())) {
                        idTipoPisCofins = retornaPisCofinsDebito(rst.getString("piscst").trim());
                        tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                        pisCofinsDebitoAnt = Integer.parseInt(rst.getString("piscst").trim());
                    } else {
                        idTipoPisCofins = 1;
                        tipoNaturezaReceita = 999;
                        pisCofinsDebitoAnt = -1;
                    }

                    if ((rst.getString("cofinscst") != null)
                            && (!rst.getString("cofinscst").trim().isEmpty())) {
                        idTipoPisCofinsCredito = retornaPisCofinsCredito(rst.getString("cofinscst").trim());
                        pisCofinsCreditoAnt = Integer.parseInt(rst.getString("cofinscst").trim());
                    } else {
                        idTipoPisCofinsCredito = 13;
                        pisCofinsCreditoAnt = -1;
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                    oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                    oProduto.tipoNaturezaReceita = tipoNaturezaReceita;

                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oAnterior.codigoanterior = idProduto;
                    oAnterior.piscofinsdebito = pisCofinsDebitoAnt;
                    oAnterior.piscofinscredito = pisCofinsCreditoAnt;

                    oProduto.vCodigoAnterior.add(oAnterior);

                    vProduto.add(oProduto);

                }
            }
                

            stm.close();
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarIcmsProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        int idAliquota, idProduto;
        String strSitTrib, valorIcms, strReducao;
        long codigoBarras = 0;
        
        try {
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            sql.append("select e.plu, e.sittribut, e.icms, e.reducao ");
            sql.append(" from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");
            
            //sql.append(" order by e.plu ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("plu") != null)
                        && (!rst.getString("plu").trim().isEmpty())
                        && (!"0".equals(rst.getString("plu").trim()))) {

                    idProduto = Integer.parseInt(rst.getString("plu"));

                    if ((rst.getString("SITTRIBUT") != null)
                            && (!rst.getString("SITTRIBUT").trim().isEmpty())) {

                        if (rst.getString("SITTRIBUT").length() >= 2) {
                            strSitTrib = rst.getString("SITTRIBUT").trim().substring(rst.getString("SITTRIBUT").length() - 2);

                            if ((rst.getString("ICMS") != null)
                                    && (!rst.getString("ICMS").trim().isEmpty())) {
                                valorIcms = rst.getString("ICMS").trim().substring(0,
                                        rst.getString("ICMS").trim().length() - 3);
                            } else {
                                valorIcms = "";
                            }

                            if ((rst.getString("reducao") != null)
                                    && (!rst.getString("reducao").trim().isEmpty())) {
                                strReducao = rst.getString("reducao").trim();
                            } else {
                                strReducao = "";
                            }

                            idAliquota = retornarICMS(Integer.parseInt(strSitTrib), valorIcms, strReducao);

                        } else {
                            idAliquota = 8;
                        }
                    } else {
                        idAliquota = 8;
                    }

                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;

                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    oAliquota.idEstado = 35;
                    oAliquota.idAliquotaDebito = idAliquota;
                    oAliquota.idAliquotaCredito = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                    oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                    oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                    oProduto.vAliquota.add(oAliquota);

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigoBarras;

                    CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                    oAnterior.codigoanterior = idProduto;

                    if ((rst.getString("SITTRIBUT") != null)
                            && (!rst.getString("SITTRIBUT").trim().isEmpty())) {

                        if (!"000".equals(rst.getString("SITTRIBUT").trim())) {
                            oAnterior.ref_icmsdebito = rst.getString("SITTRIBUT").trim();
                        } else {
                            oAnterior.ref_icmsdebito = rst.getString("ICMS").trim();
                        }

                    } else {
                        oAnterior.ref_icmsdebito = "";
                    }

                    oProduto.vCodigoAnterior.add(oAnterior);

                    vProduto.add(oProduto);

                }
            }

            stm.close();
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<FornecedorVO> carregarFornecedor(String i_arquivo) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro, Numero="",Telefone="",
               telefone2, telefone3, email ;
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        long cnpj, cep;
        double pedidoMin;
        boolean ativo = true;

        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            
            sql.append("SELECT codigo, nome, razao, inscest, cgc, rua, casa, edificio, sala, ");
            sql.append("cidade, bairro, cep, estado, inclusao, email, contato, contatcom, telefone1, ");
            sql.append("telefone2, telefone3, obs ");
            sql.append("FROM FORNECE order by codigo ");            
            rst = stm.executeQuery(sql.toString());
            Linha = 0;
            
            try {
                
                while (rst.next()) {
                    
                    FornecedorVO oFornecedor = new FornecedorVO();

                    id = rst.getInt("codigo");

                    Linha++;

                    if ((rst.getString("razao") != null)
                            && (!rst.getString("razao").isEmpty())) {
                        byte[] bytes = rst.getBytes("razao");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "";
                    }

                    if ((rst.getString("nome") != null)
                            && (!rst.getString("nome").isEmpty())) {
                        byte[] bytes = rst.getBytes("nome");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "";
                    }

                    if ((rst.getString("cgc") != null)
                            && (!rst.getString("cgc").isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("cgc").trim()));
                        
                        if (String.valueOf(cnpj).length() > 11) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }
                        
                    } else {
                        cnpj = -1;
                        id_tipoinscricao = 0;
                    }

                    if ((rst.getString("inscest") != null)
                            && (!rst.getString("inscest").isEmpty())) {
                        inscricaoestadual = util.acertarTexto(rst.getString("inscest").replace("'", "").trim());
                    } else {
                        inscricaoestadual = "ISENTO";
                    }

                    id_tipoinscricao = 0;

                    if ((rst.getString("rua") != null)
                            && (!rst.getString("rua").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("rua").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }
                    
                    if (rst.getString("casa") != null) {
                        Numero = rst.getString("casa").trim();
                        if(Numero.length()>6){
                            Numero = Numero.substring(0,6);
                        }
                    } else {
                        Numero = "0";
                    } 

                    if ((rst.getString("bairro") != null)
                            && (!rst.getString("bairro").isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("bairro").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("cep").trim()));
                    } else {
                        cep = Long.parseLong("0");
                    }

                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").isEmpty())) {

                        if ((rst.getString("estado") != null)
                                && (!rst.getString("estado").isEmpty())) {

                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                    util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                            if (id_municipio == 0) {
                                id_municipio = 3538709;
                            }
                        }
                    } else {
                        id_municipio = 3538709;
                    }

                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = 35;
                        }
                    } else {
                        id_estado = 35;
                    }
                    
                    if ((rst.getString("telefone1") != null) &&
                            (!rst.getString("telefone1").trim().isEmpty())) {
                        Telefone = util.formataNumero(rst.getString("telefone1"));
                    } else {
                        Telefone = "0000000000";
                    }
                    
                    if ((rst.getString("telefone2") != null) &&
                            (!rst.getString("telefone2").trim().isEmpty())) {
                        telefone2 = util.formataNumero(rst.getString("telefone2"));
                    } else {
                        telefone2 = "";
                    }

                    if ((rst.getString("telefone3") != null) &&
                            (!rst.getString("telefone3").trim().isEmpty())) {
                        telefone3 = util.formataNumero(rst.getString("telefone3").trim());
                    } else {
                        telefone3 = "";
                    }
                    
                    if ((rst.getString("email") != null) &&
                            (!rst.getString("email").trim().isEmpty()) &&
                            (rst.getString("email").contains("@"))) {
                        email = util.acertarTexto(rst.getString("email").trim().toLowerCase());
                    } else {
                        email = "";
                    }
                    
                    if (rst.getString("OBS") != null) {
                        obs = rst.getString("OBS").trim();
                    } else {
                        obs = "";
                    }

                    if (rst.getString("inclusao") != null) {
                        datacadastro = rst.getString("inclusao");
                    } else {
                        datacadastro = "";
                    }

                    ativo = true;
                    
                    if (razaosocial.length() > 40) {
                        razaosocial = razaosocial.substring(0, 40);
                    }

                    if (nomefantasia.length() > 30) {
                        nomefantasia = nomefantasia.substring(0, 30);
                    }

                    if (endereco.length() > 40) {
                        endereco = endereco.substring(0, 40);
                    }

                    if (bairro.length() > 30) {
                        bairro = bairro.substring(0, 30);
                    }

                    if (String.valueOf(cep).length() > 8) {
                        cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                    }

                    if (String.valueOf(cnpj).length() > 14) {
                        cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                    }

                    if (inscricaoestadual.length() > 20) {
                        inscricaoestadual = inscricaoestadual.substring(0, 20);
                    }
                    
                    if (Telefone.length() > 14) {
                        Telefone = Telefone.substring(0, 14);
                    }
                    
                    if (telefone2.length() > 14) {
                        telefone2 = telefone2.substring(0, 14);
                    }
                    
                    if (telefone3.length() > 14) {
                        telefone3 = telefone3.substring(0, 14);
                    }

                    oFornecedor.codigoanterior = id;
                    oFornecedor.razaosocial = razaosocial;
                    oFornecedor.nomefantasia = nomefantasia;
                    oFornecedor.endereco = endereco;
                    oFornecedor.numero = Numero;
                    oFornecedor.telefone = Telefone;
                    oFornecedor.bairro = bairro;
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                    oFornecedor.observacao = obs;
                    
                    oFornecedor.telefone2 = telefone2;
                    oFornecedor.telefone3 = telefone3;
                    oFornecedor.email = email;

                    vFornecedor.add(oFornecedor);
                }
            } catch (Exception ex) {
                if (Linha > 0) {
                    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

            return vFornecedor;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedorRelatorio(String i_arquivo) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, contProdForn = 0, qtdEmbalagem, cont, linha;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

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
                        
                        Cell cellCodForn = sheet.getCell(1, i);
                        Cell cellCodExtr = sheet.getCell(2, i);
                        Cell cellCodProd = sheet.getCell(3, i);
                        Cell cellQtde = sheet.getCell(4, i);
                        
                        idFornecedor = Integer.parseInt(cellCodForn.getContents().trim());
                        idProduto = Integer.parseInt(cellCodProd.getContents().trim());
                        
                        if ((cellCodExtr.getContents() != null) &&
                                (!cellCodExtr.getContents().trim().isEmpty())) {
                            codigoExterno = util.acertarTexto(cellCodExtr.getContents().trim().replace("'", ""));
                        } else {
                            codigoExterno = "";
                        }
                        
                        qtdEmbalagem = Integer.parseInt(cellQtde.getContents().trim());
                        
                        ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                        oProdutoFornecedor.id_fornecedor = idFornecedor;
                        oProdutoFornecedor.id_produto = idProduto;
                        oProdutoFornecedor.codigoexterno = codigoExterno;
                        oProdutoFornecedor.qtdembalagem = qtdEmbalagem;
                        
                        vProdutoFornecedor.add(oProdutoFornecedor);
                        
                    }
                }
                
            } catch(Exception e) {
                throw e;
            }
            
            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }    

    public List<ProdutoFornecedorVO> carregarProdutoFornecedor(String i_arquivo) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, qtdEmbalagem;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {
            connDBF.abrirConexao(i_arquivo);
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            
            sql.append("SELECT CODFOR, CODINT, PLU, QTDE FROM LIGFAB");
            rst = stm.executeQuery(sql.toString());

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if ((rst.getString("CODFOR") != null) &&
                        (!rst.getString("CODFOR").trim().isEmpty()) &&
                        (rst.getString("CODINT") != null) &&
                        (!rst.getString("CODINT").trim().isEmpty()) &&
                        (rst.getString("PLU") != null) &&
                        (!rst.getString("PLU").trim().isEmpty())) {
                
                    idFornecedor = rst.getInt("CODFOR");
                    idProduto = rst.getInt("PLU");
                    
                    if ((rst.getString("QTDE") != null) &&
                            (!rst.getString("QTDE").trim().isEmpty())) {
                        qtdEmbalagem = (int) rst.getDouble("QTDE");
                    } else {
                        qtdEmbalagem = 1;
                    }

                    if ((rst.getString("CODINT") != null)
                            && (!rst.getString("CODINT").isEmpty())) {
                        codigoExterno = util.acertarTexto(rst.getString("CODINT").replace("'", ""));
                    } else {
                        codigoExterno = "";
                    }

                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                    oProdutoFornecedor.id_fornecedor = idFornecedor;
                    oProdutoFornecedor.id_produto = idProduto;
                    oProdutoFornecedor.qtdembalagem = qtdEmbalagem;
                    oProdutoFornecedor.dataalteracao = dataAlteracao;
                    oProdutoFornecedor.codigoexterno = codigoExterno;

                    vProdutoFornecedor.add(oProdutoFornecedor);
                
                }
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }    

    private List<ClienteEventualVO> carregarClienteEventual(String i_arquivo) throws Exception {
        List<ClienteEventualVO> vClienteEventual = new ArrayList<>();
        Utils util = new Utils();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, idEstado, idMunicipio = 0, idTipoInscricao;
        String nome, endereco, bairro, cidade, estado, telefone1, telefone2, 
               telefone3, email, dataNascimento, inscricaoEstadual, numero,
               complemento = "", dataCadastro, contato;
        long cnpj, cep = 0;
        double valorLimite;
        
        try {
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            sql.append("select codigo, nome, razao, nascimento, inscest, cgc, cic, saldo, ");
            sql.append("rua, casa, edificio, apto, cidade, bairro, cep, estado, email, abertura, ");
            sql.append("contato, telefone1, telefone2, telefone3 ");
            sql.append("from cliente ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idCliente = Integer.parseInt(rst.getString("codigo").trim());
                
                nome = util.acertarTexto(rst.getString("razao").trim().replace("'", ""));
                
                if ((rst.getString("nascimento") != null) &&
                        (!rst.getString("nascimento").trim().isEmpty())) {
                    dataNascimento = rst.getString("nascimento").trim();
                } else {
                    dataNascimento = "";
                }
                
                if ((rst.getString("inscest") != null) &&
                        (!rst.getString("inscest").trim().isEmpty())) {
                    inscricaoEstadual = util.acertarTexto(rst.getString("inscest").trim().replace("'", ""));
                } else {
                    inscricaoEstadual = "ISENTO";
                }
                
                if ((rst.getString("cgc") != null) &&
                        (!rst.getString("cgc").trim().isEmpty())) {
                    
                    cnpj = Long.parseLong(util.formataNumero(rst.getString("cgc").trim()));
                    
                    if (String.valueOf(cnpj).length() > 11) {
                        idTipoInscricao = 0;
                    } else {
                        idTipoInscricao = 1;
                    }
                } else {
                    
                    if ((rst.getString("cic") != null) &&
                            (!rst.getString("cic").trim().isEmpty())) {
                        
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("cic").trim()));
                        
                    if (String.valueOf(cnpj).length() > 11) {
                        idTipoInscricao = 0;
                    } else {
                        idTipoInscricao = 1;
                    }
                        
                    } else {
                        cnpj = idCliente;
                        idTipoInscricao = 1;
                    }
                }
                
                if ((rst.getString("saldo") != null) &&
                        (!rst.getString("saldo").trim().isEmpty())) {
                    valorLimite = Double.parseDouble(rst.getString("saldo").trim());
                } else {
                    valorLimite = 0;
                }
                
                if ((rst.getString("rua") != null) &&
                        (!rst.getString("rua").trim().isEmpty())) {
                    endereco = util.acertarTexto(rst.getString("rua").trim().replace("'", ""));
                } else {
                    endereco = "";
                }
                
                if ((rst.getString("casa") != null) &&
                        (!rst.getString("casa").trim().isEmpty())) {
                    numero = util.acertarTexto(rst.getString("casa").trim().replace("'", ""));
                } else {
                    numero = "0";
                }
                
                if ((rst.getString("bairro") != null) &&
                        (!rst.getString("bairro").trim().isEmpty())) {
                    bairro = util.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                } else {
                    bairro = "";
                }
                
                if ((rst.getString("edificio") != null) &&
                        (!rst.getString("edificio").trim().isEmpty())) {
                    
                    complemento = util.acertarTexto(rst.getString("edificio").trim()).trim().replace("'", "");
                    
                    if ((rst.getString("apto") != null) &&
                            (!rst.getString("apto").trim().isEmpty())) {
                        
                        complemento = util.acertarTexto(rst.getString("edificio").trim()+" "+rst.getString("apto")).trim().replace("'", "");
                    }
                } else {
                    complemento = "";
                }
                
                
                if ((rst.getString("cidade") != null)
                        && (!rst.getString("cidade").isEmpty())) {

                    if ((rst.getString("estado") != null)
                            && (!rst.getString("estado").isEmpty())) {

                        idMunicipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                        if (idMunicipio == 0) {
                            idMunicipio = 3538709;
                        }
                    }
                } else {
                    idMunicipio = 3538709;
                }

                if ((rst.getString("estado") != null)
                        && (!rst.getString("estado").isEmpty())) {
                    idEstado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                    if (idEstado == 0) {
                        idEstado = 35;
                    }
                } else {
                    idEstado = 35;
                }
                
                if ((rst.getString("email") != null)
                        && (!rst.getString("email").trim().isEmpty())
                        && (rst.getString("email").contains("@"))) {
                    email = util.acertarTexto(rst.getString("email").trim().toLowerCase());
                } else {
                    email = "";
                }
                
                if ((rst.getString("abertura") != null) &&
                        (!rst.getString("abertura").trim().isEmpty())) {
                    dataCadastro = rst.getString("abertura").trim();
                } else {
                    dataCadastro = "";
                }
                
                if ((rst.getString("contato") != null) &&
                        (!rst.getString("contato").trim().isEmpty())) {
                    contato = util.acertarTexto(rst.getString("contato").trim().replace("'", ""));                    
                } else {
                    contato = "";
                }
                
                if ((rst.getString("telefone1") != null) &&
                        (!rst.getString("telefone1").trim().isEmpty())) {
                    telefone1 = util.formataNumero(rst.getString("telefone1").trim());
                } else {
                    telefone1 = "0000000000";
                }
                
                if ((rst.getString("telefone2") != null) &&
                        (!rst.getString("telefone2").trim().isEmpty())) {
                    telefone2 = util.formataNumero(rst.getString("telefone2").trim());
                } else {
                    telefone2 = "";
                }
                
                if ((rst.getString("telefone3") != null) &&
                        (!rst.getString("telefone3").trim().isEmpty())) {
                    telefone3 = util.formataNumero(rst.getString("telefone3").trim());
                } else {
                    telefone3 = "";
                }
                
                if ((rst.getString("email") != null) &&
                        (!rst.getString("email").trim().isEmpty()) &&
                        (rst.getString("email").contains("@"))) {
                    email = util.acertarTexto(rst.getString("email").trim().replace("'", "").toLowerCase());
                } else {
                    email = "";
                }
                
                if ((rst.getString("cep") != null) &&
                        (!rst.getString("cep").trim().isEmpty())) {
                    cep = Long.parseLong(util.formataNumero(rst.getString("cep").trim()));
                } else {
                    cep = 0;
                }
                
                if (nome.length() > 60) {
                    nome = nome.substring(0, 60);
                }
                
                if (endereco.length() > 50) {
                    endereco = endereco.substring(0, 50);
                }
                
                if (bairro.length() > 30) {
                    bairro = bairro.substring(0, 30);
                }
                   
                if (telefone1.length() > 14) {
                    telefone1 = telefone1.substring(0, 14);
                }

                if (telefone2.length() > 14) {
                    telefone2 = telefone2.substring(0, 14);
                }
                
                if (telefone3.length() > 14) {
                    telefone3 = telefone3.substring(0, 14);
                }
                
                if (inscricaoEstadual.length() > 20) {
                    inscricaoEstadual = inscricaoEstadual.substring(0, 20);
                }
                
                if (numero.length() > 6) {
                    numero = numero.substring(0, 6);
                }
                
                if (complemento.length() > 30) {
                    complemento = complemento.substring(0, 30);
                }
                
                ClienteEventualVO oClienteEventual = new ClienteEventualVO();
                oClienteEventual.id = idCliente;
                oClienteEventual.nome = nome;
                oClienteEventual.endereco = endereco;
                oClienteEventual.bairro = bairro;
                oClienteEventual.id_estado = idEstado;
                oClienteEventual.telefone = telefone1;
                oClienteEventual.id_tipoinscricao = idTipoInscricao;
                oClienteEventual.inscricaoestadual = inscricaoEstadual;
                oClienteEventual.id_situacaocadastro = 1;
                oClienteEventual.cnpj = cnpj;
                oClienteEventual.fax = "";
                oClienteEventual.datacadastro = dataCadastro;
                oClienteEventual.limitecompra = valorLimite;
                oClienteEventual.id_municipio = idMunicipio;
                oClienteEventual.cep = cep;
                oClienteEventual.numero = numero;
                oClienteEventual.complemento = complemento;
                
                vClienteEventual.add(oClienteEventual);
                
            }
            
            
            return vClienteEventual;
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarDataCadastroProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        String dataCadastro;
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();
            
            sql = new StringBuilder();
            sql.append("select e.plu, e.inclusao ");
            sql.append("from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");
            //sql.append(" order by cast(e.plu as integer) ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("plu").trim());

                if ((rst.getString("inclusao") != null)
                        && (!rst.getString("inclusao").trim().isEmpty())) {
                    dataCadastro = rst.getString("inclusao").trim();
                } else {
                    dataCadastro = "";
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.dataCadastro = dataCadastro;

                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0;
        double estoque = 0;
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select e.plu, e.quantfisc ");
            sql.append("from estoque e ");
            sql.append("where e.plu is not null    ");
            sql.append("      or trim(e.plu) <> '' ");
            
            //sql.append(" order by cast(e.plu as integer) ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("plu").trim());

                if ((rst.getString("quantfisc") != null)
                        && (!rst.getString("quantfisc").trim().isEmpty())) {
                    estoque = Double.parseDouble(rst.getString("quantfisc").trim());
                } else {
                    estoque = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                oComplemento.estoque = estoque;
                oProduto.vComplemento.add(oComplemento);

                vProduto.add(oProduto);
            }

            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(String i_arquivo, int id_loja) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        Utils util = new Utils();
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codigo, vencimento, dlanca, valorreceb, codigocli, terminal  ");
            sql.append("from receber ");
            sql.append("where vlrpago = 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                
                if ((rst.getString("codigocli") != null) &&
                        (!rst.getString("codigocli").trim().isEmpty())) {

                
                    idCliente = Integer.parseInt(rst.getString("codigocli").trim());

                    if ((rst.getString("dlanca") != null)
                            && (!rst.getString("dlanca").trim().isEmpty())) {
                        dataemissao = rst.getString("dlanca").trim();
                    } else {
                        dataemissao = String.valueOf(data);
                    }

                    if ((rst.getString("vencimento") != null)
                            && (!rst.getString("vencimento").trim().isEmpty())) {
                        datavencimento = rst.getString("vencimento").trim();
                    } else {
                        datavencimento = String.valueOf(data);
                    }

                    if ((rst.getString("terminal") != null)
                            && (!rst.getString("terminal").trim().isEmpty())) {
                        ecf = Integer.parseInt(util.formataNumero(rst.getString("terminal").trim()));
                    } else {
                        ecf = 0;
                    }

                    if ((rst.getString("codigo") != null)
                            && (!rst.getString("codigo").trim().isEmpty())) {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("codigo").trim()));
                    } else {
                        numerocupom = 0;
                    }

                    valor = Double.parseDouble(rst.getString("valorreceb").trim());

                    observacao = "IMPORTADO VR";
                    juros = 0;

                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.id_clientepreferencial = idCliente;
                    oReceberCreditoRotativo.id_loja = id_loja;
                    oReceberCreditoRotativo.dataemissao = dataemissao;
                    oReceberCreditoRotativo.numerocupom = numerocupom;
                    oReceberCreditoRotativo.valor = valor;
                    oReceberCreditoRotativo.ecf = ecf;
                    oReceberCreditoRotativo.observacao = observacao;
                    oReceberCreditoRotativo.datavencimento = datavencimento;
                    oReceberCreditoRotativo.valorjuros = juros;

                    vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                
                }
            }

            
            return vReceberCreditoRotativo;
        } catch(Exception ex) {
            throw ex;
        }
    }

    private List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativoBaixado(String i_arquivo, int id_loja) throws Exception {
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente, numerocupom, ecf;
        double valor, juros, valorPago;
        String observacao, dataemissao, datavencimento, dataPagamento;
        Utils util = new Utils();
        java.sql.Date data = new java.sql.Date(new java.util.Date().getTime());
        
        try {
            
            connDBF.abrirConexao(i_arquivo);
            
            stm = connDBF.createStatement();

            sql = new StringBuilder();
            sql.append("select codigo, vencimento, dlanca, valorreceb, vlrpago, ");
            sql.append("codigocli, terminal, pagamento ");
            sql.append("from receber ");
            sql.append("where vlrpago > 0 ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                
                if ((rst.getString("codigocli") != null) &&
                        (!rst.getString("codigocli").trim().isEmpty()) &&
                        (rst.getString("pagamento") != null) &&
                        (!rst.getString("pagamento").trim().isEmpty())) {

                
                    idCliente = Integer.parseInt(rst.getString("codigocli").trim());

                    if ((rst.getString("dlanca") != null)
                            && (!rst.getString("dlanca").trim().isEmpty())) {
                        dataemissao = rst.getString("dlanca").trim();
                    } else {
                        dataemissao = String.valueOf(data);
                    }

                    if ((rst.getString("vencimento") != null)
                            && (!rst.getString("vencimento").trim().isEmpty())) {
                        datavencimento = rst.getString("vencimento").trim();
                    } else {
                        datavencimento = String.valueOf(data);
                    }

                    if ((rst.getString("terminal") != null)
                            && (!rst.getString("terminal").trim().isEmpty())) {
                        ecf = Integer.parseInt(util.formataNumero(rst.getString("terminal").trim()));
                    } else {
                        ecf = 0;
                    }

                    if ((rst.getString("codigo") != null)
                            && (!rst.getString("codigo").trim().isEmpty())) {
                        numerocupom = Integer.parseInt(util.formataNumero(rst.getString("codigo").trim()));
                    } else {
                        numerocupom = 0;
                    }

                    valor = Double.parseDouble(rst.getString("valorreceb").trim());

                    observacao = "IMPORTADO VR => CONTA BAIXADA";
                    juros = 0;
                    
                    dataPagamento = rst.getString("pagamento").trim();
                    valorPago = Double.parseDouble(rst.getString("vlrpago").trim());

                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    oReceberCreditoRotativo.id_clientepreferencial = idCliente;
                    oReceberCreditoRotativo.id_loja = id_loja;
                    oReceberCreditoRotativo.dataemissao = dataemissao;
                    oReceberCreditoRotativo.numerocupom = numerocupom;
                    oReceberCreditoRotativo.valor = valor;
                    oReceberCreditoRotativo.ecf = ecf;
                    oReceberCreditoRotativo.observacao = observacao;
                    oReceberCreditoRotativo.datavencimento = datavencimento;
                    oReceberCreditoRotativo.valorjuros = juros;
                    oReceberCreditoRotativo.dataPagamento = dataPagamento;
                    oReceberCreditoRotativo.valorPago = valorPago;

                    vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                
                }
            }

            
            return vReceberCreditoRotativo;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    //IMPORTAÇÕES
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }    

    public void importarEstoque(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoEstoque = new ArrayList<>();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Preço Venda...Produtos...");
            vProdutoEstoque = carregarEstoqueProduto(i_arquivo);
            ProgressBar.setMaximum(vProdutoEstoque.size());
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarEstoqueProduto(vProdutoEstoque, idLoja);;
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarPisCofins(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Pis Cofins...");
            List<ProdutoVO> vProduto = carregarPisCofinsProduto(i_arquivo);
            
            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarIcms(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Icms...");
            List<ProdutoVO> vProduto = carregarIcmsProduto(i_arquivo);
            
            new ProdutoDAO().alterarICMSProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarFornecedor(String i_arquivo) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedor(i_arquivo);

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
        
    }
    
    public void importarProdutoFornecedor(String i_arquivo) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor(i_arquivo);

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarClienteEventual(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Cliente Eventual...");
            List<ClienteEventualVO> vClienteEventual = carregarClienteEventual(i_arquivo);
            
            new ClienteEventuallDAO().acertarCnpj(vClienteEventual);//salvar(vClienteEventual);
        } catch(Exception ex) {
            throw ex;
        }
    } 

    public void importarDataCadastroProduto(String i_arquivo) throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...data cadastro Produtos...");
            List<ProdutoVO> vProduto = carregarDataCadastroProduto(i_arquivo);
            
            
            new ProdutoDAO().altertarDataCadastroProdutoGdoor(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarReceberCreditoRotativo(String i_arquivo, int idLoja) throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...Receber Crédito Rotativo...");
            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCreditoRotativo(i_arquivo, idLoja);
            
            
            new ReceberCreditoRotativoDAO().salvar(vReceberCreditoRotativo, idLoja);
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarReceberCreditoRotativoBaixado(String i_arquivo, int idLoja) throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...Receber Crédito Rotativo Baixado...");
            List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarReceberCreditoRotativoBaixado(i_arquivo, idLoja);
            
            
            new ReceberCreditoRotativoDAO().salvarContaBaixada(vReceberCreditoRotativo, idLoja);
        } catch(Exception ex) {
            throw ex;
        }
    }
        
    private int retornarICMS(int codSitTrib, String valor, String reducao) {
        int retorno = 8;
        
        if (codSitTrib == 0) {
            
            if ("7".equals(valor)) {
                retorno = 0;
            } else if ("12".equals(valor)) {
                retorno = 1;
            } else if ("18".equals(valor)) {
                retorno = 2;
            } else if ("25".equals(valor)) {
                retorno = 3;
            }
            
        } else if ((codSitTrib == 10) || (codSitTrib == 30) || 
                   (codSitTrib == 60) || (codSitTrib == 70)) {
            retorno = 7;
        } else if (codSitTrib == 20) {


            if (("12".equals(valor)) &&
                    ("41.67").equals(reducao)) {
                retorno = 5;
            } else if (("18".equals(valor)) &&
                    ("33.33".equals(reducao))) {
                retorno = 9;
            } else if (("18".equals(valor)) &&
                    ("61.11".equals(reducao))) {
                retorno =  4;
            } else if (("25".equals(valor)) &&
                    ("52.00".equals(reducao))) {
                retorno = 10;
            }
            
        } else if (codSitTrib == 40) {
            retorno = 6;
        } else if (codSitTrib == 41) {
            retorno = 17;
        } else if (codSitTrib == 50) {
            retorno = 13;
        } else if (codSitTrib == 51) {
            retorno = 16;
        } else if (codSitTrib == 90) {
            retorno = 8;
        } else {
            retorno = 8;
        }
        
        return retorno;
    }
    
    private int retornaPisCofinsDebito(String cst) {
        int retorno = 1;

        if ("01".equals(cst)) {
            retorno = 0;
        } else if ("02".equals(cst)) {
            retorno = 5;
        } else if ("03".equals(cst)) {
            retorno = 6;
        } else if ("04".equals(cst)) {
            retorno = 3;
        } else if ("49".equals(cst)) {
            retorno = 9;
        } else if ("05".equals(cst)) {
            retorno = 2;
        } else if ("06".equals(cst)) {
            retorno = 7;
        } else if ("07".equals(cst)) {
            retorno = 1;
        } else if ("08".equals(cst)) {
            retorno = 8;
        } else {
            retorno = 1;
        }
        
        return retorno;
    }

    private int retornaPisCofinsCredito(String cst) {
        int retorno = 1;

        if ("01".equals(cst)) {
            retorno = 12;
        } else if ("02".equals(cst)) {
            retorno = 17;
        } else if ("03".equals(cst)) {
            retorno = 18;
        } else if ("04".equals(cst)) {
            retorno = 15;
        } else if ("49".equals(cst)) {
            retorno = 21;
        } else if ("05".equals(cst)) {
            retorno = 14;
        } else if ("06".equals(cst)) {
            retorno = 19;
        } else if ("07".equals(cst)) {
            retorno = 13;
        } else if ("08".equals(cst)) {
            retorno = 20;
        } else {
            retorno = 1;
        }
        
        return retorno;
    }
    
}
