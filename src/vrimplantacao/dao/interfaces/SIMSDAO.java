package vrimplantacao.dao.interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.file.ArquivoLeitura;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Leandro
 */
public class SIMSDAO extends AbstractIntefaceDao {
    private String MERFile;
    private String CBMERFile;
    private String NIVE1File;
    private String NIVE2File;
    private String NIVE3File;
    private String NIVE4File;
    private String CLIENTEFile;
    private String FORNECEDORFile;
    private InterfaceDAO aux = new InterfaceDAO() {

        @Override
        public String getSistema() {
            return "SIMS";
        }

        @Override
        public String getLojaOrigem() {
            return "1";
        }
        
        

        @Override
        public List<MercadologicoIMP> getMercadologicos() throws Exception {
            List<MercadologicoIMP> result = new ArrayList<>();
            
            MultiMap<String, MercadologicoIMP> temp = new MultiMap<>(5);
            
            for (List<String> nivel: carregarNIVE(1)) {
                MercadologicoIMP imp = new MercadologicoIMP();
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setMerc1ID(nivel.get(0));
                imp.setMerc1Descricao(nivel.get(4));
                result.add(imp);
                temp.put(imp, imp.getMerc1ID(),"0","0","0","0");
            }
            for (List<String> nivel: carregarNIVE(2)) {
                MercadologicoIMP get = temp.get(nivel.get(0),"0","0","0","0");
                if (get != null) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(get.getMerc1ID());                
                    imp.setMerc1Descricao(get.getMerc1Descricao());
                    imp.setMerc2ID(nivel.get(1));
                    imp.setMerc2Descricao(nivel.get(4));
                    result.add(imp);
                    temp.put(imp, imp.getMerc1ID(),imp.getMerc2ID(),"0","0","0");
                }
            }
            for (List<String> nivel: carregarNIVE(3)) {
                MercadologicoIMP get = temp.get(nivel.get(0),nivel.get(1),"0","0","0");
                if (get != null) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(get.getMerc1ID());
                    imp.setMerc1Descricao(get.getMerc1Descricao());
                    imp.setMerc2ID(get.getMerc2ID());
                    imp.setMerc2Descricao(get.getMerc2Descricao());
                    imp.setMerc3ID(nivel.get(2));
                    imp.setMerc3Descricao(nivel.get(4));
                    result.add(imp);
                    temp.put(imp, imp.getMerc1ID(),imp.getMerc2ID(),imp.getMerc3ID(),"0","0");
                }
            }
            for (List<String> nivel: carregarNIVE(4)) {
                MercadologicoIMP get = temp.get(nivel.get(0),nivel.get(1),nivel.get(2),"0","0");
                if (get != null) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(get.getMerc1ID());
                    imp.setMerc1Descricao(get.getMerc1Descricao());
                    imp.setMerc2ID(get.getMerc2ID());
                    imp.setMerc2Descricao(get.getMerc2Descricao());
                    imp.setMerc3ID(get.getMerc3ID());
                    imp.setMerc3Descricao(get.getMerc3Descricao());
                    imp.setMerc4ID(nivel.get(3));
                    imp.setMerc4Descricao(nivel.get(4));
                    result.add(imp);
                    temp.put(imp, imp.getMerc1ID(),imp.getMerc2ID(),imp.getMerc3ID(),imp.getMerc4ID(),"0");                    
                }
            }
            
            return result;
        }

        
        
    };

    public void setMERFile(String MERFile) {
        this.MERFile = MERFile;
    }

    public void setCBMERFile(String CBMERFile) {
        this.CBMERFile = CBMERFile;
    }

    public void setNIVE1File(String NIVE1File) {
        this.NIVE1File = NIVE1File;
    }

    public void setNIVE2File(String NIVE2File) {
        this.NIVE2File = NIVE2File;
    }

    public void setNIVE3File(String NIVE3File) {
        this.NIVE3File = NIVE3File;
    }

    public void setNIVE4File(String NIVE4File) {
        this.NIVE4File = NIVE4File;
    }

    public void setCLIENTEFile(String CLIENTEFile) {
        this.CLIENTEFile = CLIENTEFile;
    }

    public void setFORNECEDORFile(String FORNECEDORFile) {
        this.FORNECEDORFile = FORNECEDORFile;
    }

    
    @Override
    public void importarMercadologico() throws Exception {
        ProgressBar.setStatus("Carregando dados do mercadológico...");
        List<MercadologicoIMP> mercadologicos = aux.getMercadologicos();
        vrimplantacao2.dao.cadastro.MercadologicoDAO dao = new vrimplantacao2.dao.cadastro.MercadologicoDAO();
        dao.apagarMercadologico();
        dao.salvar(mercadologicos);
    }

    private List<List<String>> carregarNIVE(int level) throws Exception {
        List<List<String>> result = new ArrayList<>();
        
        File f;
        switch (level) {
            case 1:  f = new File(this.NIVE1File); break;
            case 2:  f = new File(this.NIVE2File); break;
            case 3:  f = new File(this.NIVE3File); break;
            default:  f = new File(this.NIVE4File); break;
        }             
        if (f.exists() && !f.isDirectory()) {
            try (FileReader fr = new FileReader(f)) {
                try (BufferedReader br = new BufferedReader(fr)) {
                    //Salta as duas primeiras linhas que é o cabeçalho.
                    br.readLine();
                    br.readLine();
                    String[] recordId = null;

                    for (String linha = br.readLine(); linha != null; linha = br.readLine()) {
                        if (linha.startsWith("^")) {
                            String ids = linha.trim().substring(7, linha.trim().length() - 1);
                            recordId = ids.split(",");
                        } else {
                            List<String> nivel = new ArrayList<>(Arrays.asList(linha.split("\\^")));

                            nivel.set(0, Utils.acertarTexto(nivel.get(0)));

                            String nivel1Id = recordId[0];
                            String nivel2Id = "0";
                            String nivel3Id = "0";
                            String nivel4Id = "0";


                            if (level > 1) {
                                nivel2Id = recordId[1];
                            }
                            if (level > 2) {
                                nivel3Id = recordId[2];
                            }
                            if (level > 3) {
                                nivel4Id = recordId[3];
                            }

                            nivel.add(0, nivel1Id);
                            nivel.add(1, nivel2Id);
                            nivel.add(2, nivel3Id);
                            nivel.add(3, nivel4Id);

                            if (nivel.size() < 6) {
                                int cont = 6 - nivel.size();
                                while (cont > 0) {
                                    nivel.add("");
                                    cont--;
                                }
                            }
                            result.add(nivel);
                        }            
                    }            
                }        
            }
        }
        
        return result;
    }
    
    private List<List<String>> carregarCLIENTE() throws Exception {
        List<List<String>> result = new ArrayList<>();
        
        File f;
        f = new File(this.CLIENTEFile);
        try (FileReader fr = new FileReader(f)) {
            try (BufferedReader br = new BufferedReader(fr)) {
                //Salta as duas primeiras linhas que é o cabeçalho.
                br.readLine();
                br.readLine();
                String recordId = null;
                
                for (String linha = br.readLine(); linha != null; linha = br.readLine()) {
                    if (linha.startsWith("^")) {
                        recordId = getId(linha)[0];
                    } else {
                        List<String> cliente = new ArrayList<>(Arrays.asList(linha.split("\\^")));
                        cliente.add(0, recordId);
                        
                        if (cliente.size() < 55) {
                            int cont = 55 - cliente.size();
                            while (cont > 0) {
                                cliente.add("");
                                cont--;
                            }
                        }
                        result.add(cliente);
                    }            
                }            
            }        
        }
        
        return result;
    }
    
    private List<List<String>> carregarFORNECEDOR() throws IOException {
        List<List<String>> result = new ArrayList<>();
        
        try (ArquivoLeitura arquivo = new ArquivoLeitura(this.FORNECEDORFile)) {        
            //Salta as duas primeiras linhas que é o cabeçalho.
            arquivo.remove(0);
            arquivo.remove(0);
            String recordId = null;

            for (String linha: arquivo) {
                if (linha.startsWith("^")) {
                    recordId = getId(linha)[0];
                } else {
                    List<String> fornecedor = new ArrayList<>(Arrays.asList(linha.split("\\^")));
                    fornecedor.add(0, recordId);

                    if (fornecedor.size() < 55) {
                        int cont = 55 - fornecedor.size();
                        while (cont > 0) {
                            fornecedor.add("");
                            cont--;
                        }
                    }
                    result.add(fornecedor);
                }            
            }    
        }
        
        return result;
    }
    
    private String[] getId(String linha) {
        linha = linha.trim();
        linha = linha.replaceAll("\\^.*\\(", "");
        linha = linha.replaceAll("\\)", "");
        return linha.split(",");
    }
    
    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        return carregarProduto(idLojaVR, false);
    }

    private List<ProdutoVO> carregarProduto(int idLojaVR, boolean comEAN) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        int cont = 1;
        vrimplantacao2.dao.cadastro.MercadologicoDAO mercDAO = new vrimplantacao2.dao.cadastro.MercadologicoDAO();
        //Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
        for (List<String> prod: carregarMER()) {
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
            
            Long id = Long.parseLong(prod.get(0));
            String descricao = prod.get(1);
            String dataCadastro = null;
            if (prod.size() - 1 > 69) {
                dataCadastro = prod.get(70);
                if (dataCadastro.length() < 8) {
                    dataCadastro = null;
                }
            }
            String ncm = Utils.formataNumero(prod.get(30));
            int idTipoEmbalagem = Utils.converteTipoEmbalagem(prod.get(3));
            double peso = Utils.truncar2(Utils.stringToDouble(prod.get(10)), 2);
            double preco = Utils.truncar2(converterValor("".equals(prod.get(105)) ? prod.get(12) : prod.get(105)), 2);
            double custoComImposto = Utils.truncar2(converterValor(prod.get(11)), 2);
            double custoSemImposto = custoComImposto;//Utils.truncar2(converterValor(prod.get(26)), 2);
            double estoque = prod.get(13) == null || prod.get(13).trim().isEmpty() ? 0 : Double.parseDouble(prod.get(13));
            String piscofins = prod.get(65);
            int aliqPdv = prod.get(7) != null || !prod.get(7).trim().isEmpty() ? Utils.stringToInt(prod.get(7)) : -2;
            String merc1 = prod.get(4);
            String merc2 = prod.get(68);
            String merc3 = prod.get(69);
            String merc4 = prod.get(71);
            boolean eBalanca = false;
            String descricaoReduzida = descricao;
            int idTipoEmbalagemEAN = idTipoEmbalagem;
            int validade = 0;
            long ean = comEAN ? Utils.stringToLong(prod.get(101), -2) : -2;
            int idSituacaoCadastral = "".equals(prod.get(100)) ? 0 : 1;
            int qtdembalagem = Utils.stringToInt(prod.get(104));
            qtdembalagem = qtdembalagem < 1 ? 1 : qtdembalagem;
            
            if (prod.get(100) != null && !prod.get(100).equals("")) {
                descricaoReduzida = prod.get(102);
                idTipoEmbalagemEAN = Utils.converteTipoEmbalagem(prod.get(103));
                validade = Utils.stringToInt(prod.get(106));
                ean = Utils.stringToLong(prod.get(101), -2);
                eBalanca = (ean > 0 || ean <= 999999) && (Utils.stringToBool(prod.get(107)));
            }            
            
            //oProduto.setId(id);
            oProduto.setIdDouble(id);
            oProduto.setDescricaoCompleta(descricao);
            oProduto.setDescricaoReduzida(descricaoReduzida);
            oProduto.setDescricaoGondola(descricao);
            oProduto.setIdSituacaoCadastro(1);
            if (dataCadastro != null && !dataCadastro.isEmpty()) {
                oProduto.setDataCadastro(Util.formatDataBanco(dataCadastro));
            } else {
                oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
            }

            oProduto.setIdSituacaoCadastro(idSituacaoCadastral);
            vrimplantacao2.vo.cadastro.MercadologicoVO mercadologico = mercDAO.getMercadologico(aux.getSistema(), aux.getLojaOrigem(), merc1, merc2, merc3, merc4, "");
            if (mercadologico != null) {
            oProduto.setMercadologico1(mercadologico.getMercadologico1());
            oProduto.setMercadologico2(mercadologico.getMercadologico2());
            oProduto.setMercadologico3(mercadologico.getMercadologico3());
            oProduto.setMercadologico4(mercadologico.getMercadologico4());
            oProduto.setMercadologico5(0);
            }

            if ((ncm != null)
                    && (!ncm.isEmpty())
                    && (ncm.trim().length() > 5)) {
                NcmVO oNcm = new NcmDAO().validar(ncm.trim());

                oProduto.setNcm1(oNcm.ncm1);
                oProduto.setNcm2(oNcm.ncm2);
                oProduto.setNcm3(oNcm.ncm3);
            }

            oProduto.setIdFamiliaProduto(-1);
            oProduto.setMargem(0);
            oProduto.setQtdEmbalagem(1);              
            oProduto.setIdComprador(1);
            oProduto.setIdFornecedorFabricante(1);
               
            if (eBalanca) {               
                oAutomacao.setCodigoBarras(ean);
                
                oProduto.setValidade(validade);
                
                if (idTipoEmbalagem == 4) {
                    oAutomacao.setIdTipoEmbalagem(4);
                    oProduto.setPesavel(false);
                } else {
                    oAutomacao.setIdTipoEmbalagem(0);
                    oProduto.setPesavel(true);
                }
                oCodigoAnterior.setCodigobalanca((int) ean);
                
                oProduto.eBalanca = true;
                oCodigoAnterior.setE_balanca(true);
            } else {                                                
                oProduto.setValidade(validade);
                oProduto.setPesavel(false); 
                oProduto.eBalanca = false;

                oAutomacao.setCodigoBarras(ean);
                if (ean > 0) {
                    oAutomacao.setIdTipoEmbalagem(idTipoEmbalagemEAN);
                } else {
                    oAutomacao.setIdTipoEmbalagem(idTipoEmbalagem);
                }

                oCodigoAnterior.setCodigobalanca(0);
                oCodigoAnterior.setE_balanca(false);
            }
            
            oAutomacao.setQtdEmbalagem(qtdembalagem);
            
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
            oProduto.setPesoBruto(peso);
            oProduto.setPesoLiquido(peso);

            oProduto.setIdTipoPisCofinsDebito(getPisCofinsSaida(piscofins));
            oProduto.setIdTipoPisCofinsCredito(getPisCofinsEntrada(piscofins));
            oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, ""));
            
            oComplemento.setPrecoVenda(preco);
            oComplemento.setPrecoDiaSeguinte(preco);
            oComplemento.setCustoComImposto(custoComImposto);
            oComplemento.setCustoSemImposto(custoSemImposto);
            oComplemento.setIdLoja(idLojaVR);
            oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
            oComplemento.setEstoque(estoque);
            oComplemento.setEstoqueMinimo(0);
            oComplemento.setEstoqueMaximo(0);
            
            
            setAliquota(aliqPdv, oAliquota);
            
            
            oCodigoAnterior.setCodigoanterior(oProduto.getId());
            oCodigoAnterior.setMargem(oProduto.getMargem());
            oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
            oCodigoAnterior.setBarras(ean);
            oCodigoAnterior.setReferencia((int) oProduto.getId());
            oCodigoAnterior.setNcm(ncm);
            oCodigoAnterior.setId_loja(idLojaVR);
            oCodigoAnterior.setPiscofinsdebito(Utils.stringToInt(piscofins));
            oCodigoAnterior.setPiscofinscredito(Utils.stringToInt(piscofins));
            oCodigoAnterior.setNaturezareceita(-1);
            oCodigoAnterior.setRef_icmsdebito(prod.get(7));

            //Encerramento produto
            if (oProduto.getMargem() == 0) {
                oProduto.recalcularMargem();
            }
            
            vProduto.add(oProduto);                           
        }
        
        return vProduto;
    }
    
    @Override
    public void importarProdutoMantendoCodigoDeBalanca(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos.....");
      
        /*Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: carregarListaDeProdutos(idLojaVR, idLojaCliente)) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }*/

        List<LojaVO> vLoja = new LojaDAO().carregar();

        List<ProdutoVO> lista = carregarListaDeProdutos(idLojaVR, idLojaCliente);
        ProgressBar.setMaximum(lista.size());
        
        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod: lista) {
            if (prod.eBalanca) {
                //prod.setId(prod.getvCodigoAnterior().get(0).getCodigobalanca());
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        produto.naoVerificarCodigoAnterior = true;
        
        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        produto.usarMercadoligicoProduto = true;
        produto.salvar(balanca, idLojaVR, vLoja);
        
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: normais) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }
        
        produto.usarMercadoligicoProduto = true;
        produto.usarCodigoBalancaComoID = false;
        produto.naoVerificarCodigoAnterior = false;
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(aux.size());
        produto.salvar(new ArrayList(aux.values()), idLojaVR, vLoja);
    }
    
    @Override
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> result = new LinkedHashMap<>();
        
        for (List<String> prod: carregarMER()) {

            ProdutoVO oProduto = new ProdutoVO();                                      
            ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
            oProduto.getvAutomacao().add(oAutomacao);
            
   
            double id = Long.parseLong(prod.get(0));
            int idTipoEmbalagem = Utils.converteTipoEmbalagem(prod.get(3));
            int idTipoEmbalagemEAN = idTipoEmbalagem;
            int qtdembalagem = Utils.stringToInt(prod.get(104));
            qtdembalagem = qtdembalagem < 1 ? 1 : qtdembalagem;
            long ean = -2;
            
            if (prod.get(100) != null && !prod.get(100).equals("")) {
                idTipoEmbalagemEAN = Utils.converteTipoEmbalagem(prod.get(103));
                ean = Utils.stringToLong(prod.get(101), -2);
            }

            oProduto.setIdDouble(id);  
            oAutomacao.setCodigoBarras(ean);
            oAutomacao.setIdTipoEmbalagem(idTipoEmbalagemEAN);
            oAutomacao.setQtdEmbalagem(qtdembalagem);

            if ((String.valueOf(ean).length() >= 7) &&
                (String.valueOf(ean).length() <= 14)) {                                             
                result.put(oAutomacao.getCodigoBarras(), oProduto);
            }                    
        }   
                
        return result;
    }

    @Override
    public void importarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Preço...");

        List<LojaVO> vLoja = new LojaDAO().carregar();

        List<ProdutoVO> lista = carregarListaDeProdutos(idLojaVR, idLojaCliente);
        ProgressBar.setMaximum(lista.size());
        
        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod: lista) {
            if (prod.eBalanca) {
                //prod.setId(prod.getvCodigoAnterior().get(0).getCodigobalanca());
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        produto.naoVerificarCodigoAnterior = true;
        
        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        alterarPrecoPorEAN(balanca, idLojaVR);
        
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: normais) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }
        
        produto.usarCodigoBalancaComoID = false;
        produto.naoVerificarCodigoAnterior = false;
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(aux.size());
        produto.alterarPrecoProdutoRapido(new ArrayList(aux.values()), idLojaVR);
    }

    @Override
    public void importarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Custo...");

        List<LojaVO> vLoja = new LojaDAO().carregar();

        List<ProdutoVO> lista = carregarListaDeProdutos(idLojaVR, idLojaCliente);
        ProgressBar.setMaximum(lista.size());
        
        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod: lista) {
            if (prod.eBalanca) {
                //prod.setId(prod.getvCodigoAnterior().get(0).getCodigobalanca());
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        produto.naoVerificarCodigoAnterior = true;
        
        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        alterarCustoPorEAN(balanca, idLojaVR);
        
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: normais) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }
        
        produto.usarCodigoBalancaComoID = false;
        produto.naoVerificarCodigoAnterior = false;
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(aux.size());
        produto.alterarCustoProdutoRapido(new ArrayList(aux.values()), idLojaVR);
    }

    @Override
    public void importarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");

        List<LojaVO> vLoja = new LojaDAO().carregar();

        List<ProdutoVO> lista = carregarListaDeProdutos(idLojaVR, idLojaCliente);
        ProgressBar.setMaximum(lista.size());
        
        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod: lista) {
            if (prod.eBalanca) {
                //prod.setId(prod.getvCodigoAnterior().get(0).getCodigobalanca());
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        produto.naoVerificarCodigoAnterior = true;
        
        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        alterarEstoquePorEAN(balanca, idLojaVR);
        
        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: normais) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }
        
        produto.usarCodigoBalancaComoID = false;
        produto.naoVerificarCodigoAnterior = false;
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(aux.size());
        produto.alterarEstoqueProdutoRapido(new ArrayList(aux.values()), idLojaVR);
    }
    
    
    
    
    public void alterarCustoPorEAN(Collection<ProdutoVO> values, int idLojaVR) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        double IdProduto;
        int x = 0, cont = 0;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            Map<Long, ProdutoVO> v_produto = new LinkedHashMap<>();
            for (ProdutoVO produto : values) {
                long codigobarras;

                if (produto.getvAutomacao() != null && produto.getvAutomacao().size() == 1) {
                    codigobarras = produto.getvAutomacao().get(0).getCodigoBarras();
                } else {
                    codigobarras = produto.getCodigoBarras();
                }
                if (codigobarras > 0) {
                    v_produto.put(codigobarras, produto);
                }
            }

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Custo...");
            sql = new StringBuilder();

            //Zera os custos
            //stm.execute("update produtocomplemento set custosemimposto = 0, custocomimposto = 0 where id_loja = " + id_loja + " and dataultimaentrada is null");
            //stm.execute("update implantacao.codigoanterior set custosemimposto = 0, custocomimposto = 0");
            Map<Long, Long> idPorEan = new ProdutoDAO().carregarCodigoBarras();
            for (ProdutoVO i_produto : v_produto.values()) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    long codigobarras;

                    if (i_produto.getvAutomacao() != null && i_produto.getvAutomacao().size() == 1) {
                        codigobarras = i_produto.getvAutomacao().get(0).getCodigoBarras();
                    } else {
                        codigobarras = i_produto.getCodigoBarras();
                    }

                    Long idAtualProduto = idPorEan.get(codigobarras);

                    if (idAtualProduto != null && idAtualProduto != 0) {
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("where id_produto = " + idAtualProduto + " ");
                        sql.append("and id_loja = " + idLojaVR + " ");
                        sql.append("and dataultimaentrada is null; ");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("WHERE codigoatual = " + idAtualProduto + " ");
                        sql.append("AND id_loja = " + idLojaVR + ";");
                        x++;
                    }
                }
                cont++;
                if (x == 1000 || cont >= v_produto.size()) {
                    stm.execute(sql.toString());
                    sql = new StringBuilder();
                    x = 0;
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
    
    public void alterarPrecoPorEAN(List<ProdutoVO> produtos, int idLojaVR) throws Exception {
        int x = 0, cont = 0;
        try {

            Conexao.begin();

            Map<Long, ProdutoVO> v_produto = new LinkedHashMap<>();
            for (ProdutoVO produto : produtos) {
                long codigobarras;

                if (produto.getvAutomacao() != null && produto.getvAutomacao().size() == 1) {
                    codigobarras = produto.getvAutomacao().get(0).getCodigoBarras();
                } else {
                    codigobarras = produto.getCodigoBarras();
                }
                if (codigobarras > 0) {
                    v_produto.put(codigobarras, produto);
                }
            }

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço...");
            StringBuilder sql = new StringBuilder();

            //Zera os custos
            //stm.execute("update produtocomplemento set custosemimposto = 0, custocomimposto = 0 where id_loja = " + id_loja + " and dataultimaentrada is null");
            //stm.execute("update implantacao.codigoanterior set custosemimposto = 0, custocomimposto = 0");
            try (Statement stm = Conexao.createStatement()) {
                Map<Long, Long> idPorEan = new ProdutoDAO().carregarCodigoBarras();
                for (ProdutoVO i_produto : v_produto.values()) {
                    for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                        long codigobarras;

                        if (i_produto.getvAutomacao() != null && i_produto.getvAutomacao().size() == 1) {
                            codigobarras = i_produto.getvAutomacao().get(0).getCodigoBarras();
                        } else {
                            codigobarras = i_produto.getCodigoBarras();
                        }

                        Long idAtualProduto = idPorEan.get(codigobarras);

                        if (idAtualProduto != null && idAtualProduto != 0) {
                            try (Statement st = Conexao.createStatement()) {
                                try (ResultSet rs = st.executeQuery(
                                        "select * from oferta where id_produto = " + idAtualProduto + " and id_loja = " + idLojaVR
                                )) {
                                    if (!rs.next()) {
                                        sql.append("UPDATE produtocomplemento SET ");
                                        sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                                        sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + " ");
                                        sql.append("where id_produto = " + idAtualProduto + " ");
                                        sql.append("and id_loja = " + idLojaVR + "; ");

                                        sql.append(" UPDATE implantacao.codigoanterior SET ");
                                        sql.append(" precovenda = " + oComplemento.precoVenda + " ");
                                        sql.append(" WHERE codigoatual = " + idAtualProduto + " ");
                                        sql.append(" AND id_loja = " + idLojaVR + "; ");
                                        x++;
                                    }
                                }
                            }
                        }
                    }
                    cont++;
                    if (x == 1000 || cont >= v_produto.size()) {
                        stm.execute(sql.toString());
                        sql = new StringBuilder();
                        x = 0;
                    }
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void alterarEstoquePorEAN(List<ProdutoVO> produtos, int idLojaVR) throws Exception {
        int x = 0, cont = 0;
        try {

            Conexao.begin();

            Map<Long, ProdutoVO> v_produto = new LinkedHashMap<>();
            for (ProdutoVO produto : produtos) {
                long codigobarras;

                if (produto.getvAutomacao() != null && produto.getvAutomacao().size() == 1) {
                    codigobarras = produto.getvAutomacao().get(0).getCodigoBarras();
                } else {
                    codigobarras = produto.getCodigoBarras();
                }
                if (codigobarras > 0) {
                    v_produto.put(codigobarras, produto);
                }
            }

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Estoque...");
            StringBuilder sql = new StringBuilder();

            //Zera os custos
            //stm.execute("update produtocomplemento set custosemimposto = 0, custocomimposto = 0 where id_loja = " + id_loja + " and dataultimaentrada is null");
            //stm.execute("update implantacao.codigoanterior set custosemimposto = 0, custocomimposto = 0");
            try (Statement stm = Conexao.createStatement()) {
                Map<Long, Long> idPorEan = new ProdutoDAO().carregarCodigoBarras();
                for (ProdutoVO i_produto : v_produto.values()) {
                    for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                        long codigobarras;

                        if (i_produto.getvAutomacao() != null && i_produto.getvAutomacao().size() == 1) {
                            codigobarras = i_produto.getvAutomacao().get(0).getCodigoBarras();
                        } else {
                            codigobarras = i_produto.getCodigoBarras();
                        }

                        Long idAtualProduto = idPorEan.get(codigobarras);

                        if (idAtualProduto != null && idAtualProduto != 0) {
                            sql.append("UPDATE produtocomplemento SET ");
                            sql.append("estoque = " + oComplemento.estoque + " ");
                            sql.append("where id_produto = " + idAtualProduto + " ");
                            sql.append("and id_loja = " + idLojaVR + "; ");

                            sql.append("UPDATE implantacao.codigoanterior SET ");
                            sql.append("estoque = " + oComplemento.estoque + " ");
                            sql.append("WHERE codigoatual = " + idAtualProduto + " ");
                            sql.append("AND id_loja = " + idLojaVR + ";");
                            x++;
                        }
                    }
                    cont++;
                    if (x == 1000 || cont >= v_produto.size()) {
                        stm.execute(sql.toString());
                        sql = new StringBuilder();
                        x = 0;
                    }
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        for (List<String> rst: carregarCLIENTE()) {                    
            ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

            oClientePreferencial.setId(Utils.stringToInt(rst.get(0)));
            oClientePreferencial.setCodigoanterior(Utils.stringToInt(rst.get(0)));
            oClientePreferencial.setNome(rst.get(1));
            oClientePreferencial.setEndereco((rst.get(19) + " " + rst.get(20)).trim());
            oClientePreferencial.setNumero("0");
            oClientePreferencial.setComplemento("N".equals(rst.get(21)) ? "" : rst.get(21));
            oClientePreferencial.setBairro(rst.get(22));
            oClientePreferencial.setId_estado(Utils.getEstadoPelaSigla(rst.get(24)));
            oClientePreferencial.setId_municipio("".equals(rst.get(23)) ? Global.idMunicipio : Utils.retornarMunicipioIBGEDescricao(rst.get(23), rst.get(24)));
            oClientePreferencial.setCep(Utils.stringToLong(rst.get(25)));
            oClientePreferencial.setTelefone(rst.get(4));
            oClientePreferencial.setInscricaoestadual(rst.get(3));
            oClientePreferencial.setCnpj(rst.get(2));
            oClientePreferencial.setSexo(1);
            oClientePreferencial.setDataresidencia("1990/01/01");
            Date dataCadastro;
            try {
                dataCadastro = new Date(new SimpleDateFormat("dd/MM/yyyy").parse(rst.get(14)).getTime());                
            } catch (ParseException e) {
                dataCadastro = new Date(new java.util.Date().getTime());
            }
            oClientePreferencial.setDatacadastro(dataCadastro);
            String email = Utils.acertarTexto(rst.get(48));
            oClientePreferencial.setEmail(email.length() > 4 ? email : "");
            oClientePreferencial.setValorlimite(Utils.stringToDouble(rst.get(16)));
            oClientePreferencial.setFax(rst.get(12));            
            oClientePreferencial.setBloqueado(Utils.acertarTexto(rst.get(13)).startsWith("5"));
            oClientePreferencial.setId_situacaocadastro(1);
            oClientePreferencial.setTelefone2(rst.get(8));
            oClientePreferencial.setObservacao("IMPORTADO VR");
            Date dataNasc;
            try {
                dataNasc = new Date(new SimpleDateFormat("dd/MM/yyyy").parse(rst.get(5)).getTime());
            } catch (ParseException e) {
                dataNasc = new Date(new java.util.Date().getTime());
            }            
            oClientePreferencial.setDatanascimento(dataNasc);
            oClientePreferencial.setNomepai("");
            oClientePreferencial.setNomemae("");
            oClientePreferencial.setEmpresa("");
            oClientePreferencial.setTelefoneempresa("");
            oClientePreferencial.setCargo(rst.get(10));
            oClientePreferencial.setEnderecoempresa("");
            oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);            
            oClientePreferencial.setSalario(Utils.stringToDouble(rst.get(15)));
            String strCivil = Utils.acertarTexto(rst.get(6));
            if (strCivil.contains("CASADO")) {
                oClientePreferencial.setId_tipoestadocivil(2);
            } else if (strCivil.contains("SOLTEI")) {
                oClientePreferencial.setId_tipoestadocivil(1);
            } else if (strCivil.contains("AMAZ")) {
                oClientePreferencial.setId_tipoestadocivil(4);
            } else {
                oClientePreferencial.setId_tipoestadocivil(0);
            }
            oClientePreferencial.setCelular(rst.get(9));
            oClientePreferencial.setNomeconjuge("");
            oClientePreferencial.setOrgaoemissor("");                  

            vClientePreferencial.add(oClientePreferencial);
        }                

        return vClientePreferencial;
    }

    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        for (List<String> rst: carregarFORNECEDOR()) {
            FornecedorVO oFornecedor = new FornecedorVO();

            Date datacadastro;
            try {
                datacadastro = new Date(new SimpleDateFormat("dd/MM/yyyy").parse(rst.get(26)).getTime());                
            } catch (ParseException e) {
                datacadastro = new Date(new java.util.Date().getTime());
            }

            oFornecedor.setId(Utils.stringToInt(rst.get(0)));
            oFornecedor.setDatacadastro(datacadastro);
            oFornecedor.setCodigoanterior(Utils.stringToInt(rst.get(0)));
            oFornecedor.setRazaosocial(rst.get(1));
            oFornecedor.setNomefantasia(rst.get(2));
            oFornecedor.setEndereco((rst.get(6) + " " + rst.get(7)).trim());
            oFornecedor.setComplemento(rst.get(8));
            oFornecedor.setBairro(rst.get(9));
            oFornecedor.setId_estado(Utils.getEstadoPelaSigla(rst.get(11)));
            oFornecedor.setId_municipio("".equals(rst.get(10)) ? Global.idMunicipio : Utils.retornarMunicipioIBGEDescricao(rst.get(10), rst.get(11)));
            oFornecedor.setCep(Utils.stringToLong(rst.get(12), 0));
            oFornecedor.setTelefone(rst.get(13));
            oFornecedor.setTelefone(rst.get(14));
            oFornecedor.setTelefone3(rst.get(24));
            oFornecedor.setInscricaoestadual(rst.get(4));
            oFornecedor.setCnpj(Utils.stringToLong(rst.get(3), 0));
            oFornecedor.setId_tipoinscricao(String.valueOf(oFornecedor.getCnpj()).length() > 11 ? 0 : 1);
            oFornecedor.setNumero("0");
            oFornecedor.setComplemento(rst.get(8));
            oFornecedor.setObservacao("IMPORTADO VR" + (!"".equals(rst.get(25)) ? " - TIPOEMPRESA " + rst.get(15) : ""));
            oFornecedor.setFax("");
            oFornecedor.setEmail(rst.get(25).contains("@") ? rst.get(25) : "");
            oFornecedor.setId_situacaocadastro(1);

            result.add(oFornecedor);
        }
        
        return result;
    }
    
    
    
    
    
    private List<List<String>> carregarMER() throws Exception {
        List<List<String>> result = new ArrayList<>();
        
        File f = new File(this.MERFile);
        try (FileReader fr = new FileReader(f)) {
            try (BufferedReader br = new BufferedReader(fr)) {
                //Salta as duas primeiras linhas que é o cabeçalho.
                br.readLine();
                br.readLine();
                Long recordId = null;
                
                Map<Long, Map<Long, List<String>>> cb = carregarCBMER();
                for (String linha = br.readLine(); linha != null; linha = br.readLine()) {
                    if (linha.startsWith("^MER")) {
                        recordId = Long.parseLong(getId(linha)[0]);
                    } else {                        
                        Map<Long, List<String>> get = cb.get(recordId);
                        if (get != null) {                            
                            for (List<String> ean: get.values()) {
                                List<String> produto = new ArrayList<>(Arrays.asList(linha.split("\\^")));
                                produto.add(0, String.valueOf(recordId));
                                if (produto.size() < 100) {
                                    int cont = 100 - produto.size();
                                    while (cont > 0) {
                                        produto.add("");
                                        cont--;
                                    }
                                }
                                produto.addAll(ean);
                                long barra = Utils.stringToLong(produto.get(101), -2);
                                if (/*Utils.stringToBool(produto.get(107)) && */barra > 0 && barra < 999999) {
                                    produto.set(107, "S");//, linha)get(107) 
                                } else {
                                    produto.set(107, "N");
                                }
                                result.add(produto);
                            }
                        } else {
                            List<String> produto = new ArrayList<>(Arrays.asList(linha.split("\\^")));
                            produto.add(0, String.valueOf(recordId));
                            if (produto.size() < 100) {
                                int cont = 100 - produto.size();
                                while (cont > 0) {
                                    produto.add("");
                                    cont--;
                                }
                            }
                            int cont = 15;
                            while (cont > 0) {
                                produto.add("");
                                cont--;
                            }
                            result.add(produto);
                        }
                    }            
                }            
            }        
        }
        
        return result;
    }
    
    private Map<Long, Map<Long, List<String>>> carregarCBMER() throws Exception {
        Map<Long, Map<Long, List<String>>> result = new LinkedHashMap<>();
        String erro = "";
        File f = new File(this.CBMERFile);
        try (FileReader fr = new FileReader(f)) {
            try (BufferedReader br = new BufferedReader(fr)) {
                //Salta as duas primeiras linhas que é o cabeçalho.
                br.readLine();
                br.readLine();
                long recordId = -1;
                long ean = -2;
                for (String linha = br.readLine(); linha != null; linha = br.readLine()) {
                    if (linha.startsWith("^CBMER")) {
                        erro = linha;
                        String[] ids = getId(linha);
                        recordId = Long.parseLong(ids[1]);
                        ean = Long.parseLong(ids[0]);
                    } else {
                        List<String> produto = new ArrayList<>(Arrays.asList(linha.split("\\^")));
                        produto.add(0, String.valueOf(recordId));
                        produto.add(1, String.valueOf(ean));
                        if (produto.size() < 20) {
                            int cont = 20 - produto.size();
                            while (cont > 0) {
                                produto.add("");
                                cont--;
                            }
                        }
                        Map<Long, List<String>> eans = result.get(recordId);
                        if (eans == null) {
                            eans = new HashMap<>();
                            eans.put(Long.parseLong(produto.get(1)), produto);
                            result.put(recordId, eans);
                        } else {
                            eans.put(Long.parseLong(produto.get(1)), produto);
                            result.put(recordId, eans);
                        }
                    }            
                }            
            } catch (Exception e) {
                System.out.println(erro);
                throw e;
            }       
        }
        
        return result;
    }

    private int getPisCofinsSaida(String get) {
        switch(get.trim()) {
            case "0": return 0;
            case "1": return 7;
            case "2": return 3;
            case "3": return 2;
            case "4": return 8;
            default: return 9;
        }
    }

    private int getPisCofinsEntrada(String get) {
        switch(get.trim()) {
            case "0": return 12;
            case "1": return 19;
            case "2": return 15;
            case "3": return 14;
            case "4": return 20;
            default: return 21;
        }
    }

    private double converterValor(String valor) {
        if (valor == null) {
            return 0;
        } else {
            valor = Utils.formataNumero(valor);
            valor = "000" + valor;                       
            if (valor.length() >= 3) {
                String inteiro = valor.substring(0, valor.length() - 2);
                String decimal = valor.substring(valor.length() - 2, valor.length());
                return Double.parseDouble(inteiro + "." + decimal);
            } else {
                return Double.parseDouble(valor);
            }
        }
        
    }

    private void setAliquota(int aliqPDV, ProdutoAliquotaVO oAliquota) {
        oAliquota.setIdEstado(Utils.getEstadoPelaSigla("SP"));
        int cst;
        double aliq = 0;
        switch (aliqPDV) {
            case 2: cst = 51; break;
            case 4: cst = 60; break;
            case 5: cst = 40; break;
            case 0: cst = 0; aliq = 18; break;
            case 7: cst = 0; aliq = 7; break;
            case 8: cst = 0; aliq = 25; break;
            case 9: cst = 0; aliq = 12; break;
            default: cst = 8; break;
        }
        oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS("SP", cst, aliq, 0));
        oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS("SP", cst, aliq, 0));
        oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS("SP", cst, aliq, 0));
        oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS("SP", cst, aliq, 0));
        oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS("SP", cst, aliq, 0));
    }

    public void corrigePreco(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Preço...");

        //List<ProdutoVO> lista = carregarProdutoPrecoMenor(idLojaVR, true);
        List<ProdutoVO> lista = carregarProduto(idLojaVR, true);
        ProgressBar.setMaximum(lista.size());
        
        List<ProdutoVO> balanca = new ArrayList<>();
        List<ProdutoVO> normais = new ArrayList<>();
        for (ProdutoVO prod: lista) {
            if (prod.eBalanca) {
                //prod.setId(prod.getvCodigoAnterior().get(0).getCodigobalanca());
                balanca.add(prod);
            } else {
                normais.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.usarCodigoBalancaComoID = true;
        produto.naoVerificarCodigoAnterior = true;
        
        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        ProgressBar.setMaximum(balanca.size());
        alterarPrecoPorEAN(balanca, idLojaVR);
        
        Map<Long, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: normais) {      
            aux.put(vo.getvAutomacao().get(0).getCodigoBarras(), vo);
        }
        
        produto.usarCodigoBalancaComoID = false;
        produto.naoVerificarCodigoAnterior = false;
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(aux.size());
        produto.alterarPrecoPorEAN(new ArrayList(aux.values()), idLojaVR);
    }

    public void corrigeQtdEmbalagem(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Qtd...");

        List<ProdutoVO> lista = new ArrayList<>(carregarEanProduto(idLojaVR, idLojaCliente).values());
        ProgressBar.setMaximum(lista.size());
        
        List<ProdutoVO> qtdEmbalagem = new ArrayList<>();
        for (ProdutoVO prod: lista) {
            if ((!prod.eBalanca) && prod.getvAutomacao().get(0).getQtdEmbalagem() > 1) {
                qtdEmbalagem.add(prod);
            }
        }

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        
        Map<Long, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo: qtdEmbalagem) {      
            aux.put(vo.getvAutomacao().get(0).getCodigoBarras(), vo);
        }
        
        produto.usarCodigoBalancaComoID = false;
        produto.naoVerificarCodigoAnterior = false;
        ProgressBar.setStatus("Carregando dados...Produtos normais.....");
        ProgressBar.setMaximum(aux.size());
        produto.alterarQtdEmbalagem(new ArrayList(aux.values()));
    }
    
    public void corrigeAnterior(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Anterior...");
        
        List<ProdutoVO> lista = carregarListaDeProdutos(idLojaVR, idLojaCliente);
  
        Conexao.begin();
        try {
            try (Statement stm = Conexao.createStatement()) {
                Map<Long, Integer> eans = new LinkedHashMap<>();
                try (ResultSet rst = stm.executeQuery(
                        "select codigobarras, id_produto from produtoautomacao"
                )) {
                    while (rst.next()) {
                        eans.put(rst.getLong("codigobarras"), rst.getInt("id_produto"));
                    }
                }
                
                ProgressBar.setStatus("Gravando dados...Produtos...Anterior...");
                ProgressBar.setMaximum(lista.size());
                for (ProdutoVO vo: lista) {
                    SQLBuilder sql = new SQLBuilder();
                    sql.setSchema("implantacao");
                    sql.setTableName("codigoanterior");
                    long ean = vo.getvAutomacao().get(0).getCodigoBarras();
                    if (ean > 999999) {
                        if (eans.containsKey(ean)) {
                            sql.put("codigoanterior", (long) vo.getIdDouble());
                            sql.setWhere("codigoatual = " + eans.get(ean));
                        } else {
                            ProgressBar.next();
                            continue;
                        }
                    } else {                    
                        if (vo.eBalanca) {
                            sql.put("codigoanterior", (long) vo.getIdDouble());
                            sql.setWhere("codigoatual = " + vo.getvAutomacao().get(0).getCodigoBarras());
                        } else {
                            ProgressBar.next();
                            continue;
                            /*sql.put("codigoanterior", (long) vo.getIdDouble());
                            sql.setWhere("codigoatual = " + ((long) vo.getIdDouble()));*/
                        }
                    }
                    stm.execute(sql.getUpdate());
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
  
}
