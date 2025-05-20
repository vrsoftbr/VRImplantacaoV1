package vrimplantacao2.dao.cadastro.produto2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.AtacadoProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.cadastro.ProdutoAliquotaVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoDescontoVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoLojaVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.oferta.OfertaVO;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.PisCofinsVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.controller.migracao.LogController;
import vrimplantacao2_5.service.migracao.ProdutoService;
import vrimplantacao2_5.vo.enums.EOperacao;

/**
 *
 * @author Leandro
 */
public class ProdutoRepository {

    private static final Logger LOG = Logger.getLogger(ProdutoRepository.class.getName());
    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

    private final ProdutoRepositoryProvider provider;
    private final LogController logController;
    private static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private boolean naoTransformarEANemUN = false;
    private boolean importarMenoresQue7Digitos = false;
    private boolean copiarIcmsDebitoParaCredito = false;
    private boolean manterDescricao = false;
    public boolean importarSomenteLoja = false;
    private boolean forcarUnificacao = false;
    private int opcao = -1;

    private Map<String, Entry<String, Integer>> divisoes;

    public ProdutoRepository(ProdutoRepositoryProvider provider) throws Exception {
        this.provider = provider;
        this.divisoes = provider.getDivisoesAnteriores();
        this.logController = new LogController();
    }

    public String getSistema() {
        return provider.getSistema();
    }

    public String getLoja() {
        return provider.getLoja();
    }

    public int getLojaVR() {
        return provider.getLojaVR();
    }

    public Set<OpcaoProduto> getOpcoes() {
        return provider.getOpcoes();
    }

    public boolean isForcarUnificacao() {
        return this.forcarUnificacao;
    }

    public void setForcarUnificacao(boolean forcarUnificacao) {
        this.forcarUnificacao = forcarUnificacao;
    }

    public void salvar(List<ProdutoIMP> produtos) throws Exception {
        importarMenoresQue7Digitos = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
        copiarIcmsDebitoParaCredito = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_COPIAR_ICMS_DEBITO_NO_CREDITO);
        boolean filtrarProdutosInativos = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_SOMENTE_PRODUTOS_ATIVOS);

        LOG.finest("Abrindo a transação");
        provider.begin();
        try {
            /**
             * Organizando a listagem de dados antes de efetuar a gravação.
             */
            System.gc();
            List<ProdutoIMP> organizados = new Organizador(this).organizarListagem(produtos);
            if (filtrarProdutosInativos) {
                organizados = filtrarProdutosInativos(organizados);
            }
            produtos.clear();
            System.gc();

            setNotify("Produtos - Carregando IDs vagos...", produtos.size());
            ProdutoIDStack idStack = provider.getIDStack();

            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN)) {
                this.naoTransformarEANemUN = true;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String dataImportacao = sdf.format(new Date());

            setNotify("Gravando os produtos...", organizados.size());

            for (ProdutoIMP imp : organizados) {

                StringBuilder rep = new StringBuilder();
                imp.setImportSistema(this.provider.getSistema());
                String obsImportacaoEan = "";

                try {

                    rep
                            .append("00|")
                            .append(imp.getImportId()).append("|")
                            .append(imp.getEan()).append("|")
                            .append(imp.getTipoEmbalagem()).append("|")
                            .append(imp.isBalanca() ? "PESAVEL" : "UNITARIO").append("|")
                            .append(imp.getDescricaoCompleta()).append("|");

                    //<editor-fold defaultstate="collapsed" desc="Preparando variáveis">
                    int id;
                    long ean;
                    String strID;
                    boolean eBalanca;
                    TipoEmbalagem unidade;
                    boolean manterEAN;

                    {
                        SetUpVariaveisTO to = setUpVariaveis(imp);
                        ean = to.ean;
                        strID = to.strID;
                        eBalanca = to.eBalanca;
                        unidade = to.unidade;
                    }
                    //</editor-fold>

                    ProdutoAnteriorVO anterior = provider.anterior().get(
                            provider.getSistema(),
                            provider.getLoja(),
                            imp.getImportId()
                    );

                    int idProdutoExistente = provider.automacao().getIdProdutoPorEAN(ean);

                    //<editor-fold defaultstate="collapsed" desc="Se código anterior é nulo">
                    if (anterior == null) {
                        rep.append("01|Produto não importado anteriormente");

                        if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_PDV_VR)) {
                            try {
                                id = Integer.parseInt(strID);
                                if (id < 1 || id > 999999) {
                                    throw new NumberFormatException("ID fora do intervalo permitido");
                                }
                                if (idStack.isIdCadastrado(id)) {
                                    anterior = converterImpEmAnterior(imp);
                                    ProdutoVO produtoVO = new ProdutoVO();

                                    produtoVO.setId(id);
                                    anterior.setCodigoAtual(produtoVO);
                                    anterior.setDataHora(dataImportacao);

                                    provider.anterior().salvar(anterior);
                                    notificar();
                                    continue;
                                } else {
                                    //Removo o ID da pilha de IDs disponíveis.
                                    idStack.obterID(strID, eBalanca);
                                }
                            } catch (NumberFormatException ex) {
                                LOG.log(Level.WARNING, "Id () do produto () não é válido, produto não importado", ex);
                                notificar();
                                continue;
                            }
                        } else {
                            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_RESETAR_BALANCA)
                                    || provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_RESETAR_NORMAIS)) {
                                strID = resetarIds(strID, eBalanca);
                            } else if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA) && eBalanca) {
                                strID = String.valueOf(ean);
                            } else if ((importarMenoresQue7Digitos || imp.isManterEAN()) && ean >= 1 && ean <= 999999) {
                                strID = String.valueOf(ean);
                            }

                            id = idStack.obterID(strID, eBalanca);
                        }

                        ProdutoVO prod = converterIMP(imp, id, unidade, eBalanca);

                        anterior = converterImpEmAnterior(imp);
                        anterior.setCodigoAtual(prod);
                        anterior.setDataHora(sdf.format(new Date()));
                        anterior.setObsImportacao("PRODUTO NOVO - INSERIDO PELO METODO salvar DA CLASSE " + ProdutoRepository.class.getName().toString());
                        anterior.setIdConexao(this.provider.getIdConexao());

                        ProdutoAliquotaVO aliquota = converterAliquota(imp);
                        aliquota.setProduto(prod);

                        ProdutoComplementoVO complemento = converterComplemento(imp);
                        complemento.setProduto(prod);
                        complemento.setIdAliquotaCredito(aliquota.getAliquotaCredito().getId());

                        if (idProdutoExistente > 0 && ean > 999999) {
                            anterior.setObsImportacao("PRODUTO JA CADASTRADO NO VR MASTER");

                            prod.setId(idProdutoExistente);
                            anterior.setCodigoAtual(prod);

                            provider.anterior().salvar(anterior);

                            if (!provider.eanAnterior().cadastrado(imp.getImportId(), imp.getEan())) {
                                obsImportacaoEan = "EAN JA CADASTRADO NO VR MASTER - CODIGO ATUAL " + prod.getId();
                                ProdutoAnteriorEanVO eanAnterior = converterAnteriorEAN(imp);
                                provider.eanAnterior().salvar(eanAnterior, obsImportacaoEan);
                            }

                            continue;
                        }

                        provider.salvar(prod);
                        provider.anterior().salvar(anterior);
                        provider.complemento().salvar(complemento, false);
                        provider.aliquota().salvar(aliquota);

                        if (versao.igualOuMaiorQue(4, 1)) {
                            provider.salvarProdutoPisCofins(prod);
                        }

                        if (prod.getDescricaoCompleta() != null
                                && !prod.getDescricaoCompleta().trim().isEmpty()
                                && prod.getDescricaoCompleta().length() >= 3
                                && ean > 999999) {

                            if (!(ean > 99999999999999l)) {
                                provider.salvarLojaVirtual(prod, ean);
                            }
                        }

                        if (aliquota.getBeneficio() != 0) {
                            provider.aliquota().salvarAliquotaBeneficio(aliquota);
                        }
                    } //</editor-fold>
                    else if (anterior.getCodigoAtual() != null) {
                        id = anterior.getCodigoAtual().getId();
                        
                        obsImportacaoEan = "EAN JA CADASTRADO NO VR MASTER - CODIGO ATUAL " + idProdutoExistente;
                        
                        rep.append("01|Produto importado anteriormente (").append("codigoatual:").append(id).append("\n");
                    } else {
                        rep.append("01|Produto sem código atual no VR");
                        LOG.finer("Produto importado: " + rep.toString());
                        continue;
                    }

                    if (eBalanca) {
                        ean = id;
                    }

                    if (id > 0 && ean > 0) { //ID e EAN válidos
                        if (!provider.automacao().cadastrado(ean)) {
                            ProdutoAutomacaoVO automacao = converterEAN(imp, ean, unidade);
                            automacao.setProduto(anterior.getCodigoAtual());
                            provider.automacao().salvar(automacao);
                            obsImportacaoEan = "EAN NOVO - INSERIDO PELO METODO salvar DA CLASSE " + ProdutoRepository.class.getName().toString();
                        }
                    }

                    if (!provider.eanAnterior().cadastrado(imp.getImportId(), imp.getEan())) {
                        ProdutoAnteriorEanVO eanAnterior = converterAnteriorEAN(imp);
                        provider.eanAnterior().salvar(eanAnterior, obsImportacaoEan);
                    }

                    notificar();
                    LOG.log(Level.FINER, "Produto importado: {0}", rep.toString());
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Erro ao importar o produto\n" + rep.toString(), ex);
                    throw ex;
                }
            }

            for (LojaVO loja : provider.getLojas()) {
                if (loja.getId() != getLojaVR()) {
                    provider.complemento().copiarProdutoComplemento(getLojaVR(), loja.getId());
                }
            }

            //Executa log de operação
            logController.executar(EOperacao.SALVAR_PRODUTO.getId(),
                    sdf.format(new Date()),
                    provider.getLojaVR());

            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            LOG.log(Level.SEVERE, "Erro ao importar os produtos", e);
            throw e;
        }
    }

    public void salvar2_5(List<ProdutoIMP> produtos) throws Exception {

        System.out.println(new StringBuilder().append("CONEXAO: ")
                .append(this.provider.getIdConexao())
                .append(" IMPLOJA: ")
                .append(this.getLoja())
                .append(" SISTEMA: ")
                .append(getSistema())
                .toString());

        if (this.provider.isImportarPorPlanilha()) {
            forcarUnificacao = provider.getOpcoes().contains(OpcaoProduto.FORCAR_UNIFICACAO);
            if (this.forcarUnificacao) {
                unificar(produtos);
            } else {
                salvar(produtos);
            }
        } else {
            ProdutoService produtoService = new ProdutoService();

            forcarUnificacao = provider.getOpcoes().contains(OpcaoProduto.FORCAR_UNIFICACAO);

            int idConexao = produtoService.existeConexaoMigrada(this.provider.getIdConexao(), getSistema()),
                    registros = produtoService.verificaRegistro();

            String impSistema = produtoService.getImpSistemaInicial().trim();

            if (!produtoService.isLojaMatrizMigracao(this.provider.getIdConexao(), getLoja())
                    && registros == 0) {
                throw new VRException("Favor, selecionar loja matriz para primeira importação!\n"
                        + "Os códigos devem ser mantidos da loja principal!");
            }

            if (this.forcarUnificacao) {
                unificar(produtos);
            } else {
                /**
                 * Se já existe registro na codant e a nova conexão não existe
                 * na codant ou o nome do sistema (sistema ' - ' complemento) da
                 * primeira importação é diferente do sistema da nova
                 * importação, então a rotina define que é uma unificação.
                 */
                if (registros > 0 && idConexao == 0 || (!impSistema.isEmpty() && !impSistema.equals(getSistema()))) {
                    unificar(produtos);
                } else {
                    boolean existeConexao = produtoService.
                            verificaMigracaoMultiloja(getLoja(), getSistema(), this.provider.getIdConexao());

                    String lojaModelo = produtoService.
                            getLojaModelo(this.provider.getIdConexao(), getSistema());

                    if (registros > 0 && existeConexao && !getLoja().equals(lojaModelo)) {

                        provider.setStatus("Produtos - Copiando código anterior produto...");
                        produtoService.copiarCodantProduto(getSistema(), lojaModelo, getLoja());
                    }

                    salvar(produtos);
                }
            }
        }
    }

    public void atualizar(List<ProdutoIMP> produtos, OpcaoProduto... opcoes) throws Exception {
        Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());
        Set<OpcaoProduto> op = new HashSet<>(Arrays.asList(opcoes));
        importarSomenteLoja = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_INDIVIDUAL_LOJA);
        importarMenoresQue7Digitos = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
        copiarIcmsDebitoParaCredito = op.contains(OpcaoProduto.IMPORTAR_COPIAR_ICMS_DEBITO_NO_CREDITO);
        boolean filtrarProdutosInativos = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_SOMENTE_PRODUTOS_ATIVOS);

        LOG.finer("Entrando no método atualizar; produtos(" + produtos.size() + ") opcoes(" + opcoes.length + ")");
        //<editor-fold defaultstate="collapsed" desc="Separa as opções entre 'com lista especial' e 'sem lista especial'">
        Set<OpcaoProduto> optComLista = new LinkedHashSet<>();
        Set<OpcaoProduto> optSimples = new LinkedHashSet<>();
        for (OpcaoProduto opt : opcoes) {
            if (opt == OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN) {
                this.naoTransformarEANemUN = true;
            }
            if (opt.getListaEspecial() != null && !opt.getListaEspecial().isEmpty()) {
                optComLista.add(opt);
            } else {
                optSimples.add(opt);
            }
        }
        //</editor-fold>

        if (versao.igualOuMaiorQue(4, 1, 39)) {
            if (optSimples.contains(OpcaoProduto.PIS_COFINS)) {
                opcao = JOptionPane.showConfirmDialog(null,
                        "Deseja atualizar a tabela produtopiscofins com base na produto?", "Escolha a origem", JOptionPane.YES_NO_OPTION);
            }
        }

        if (!optSimples.isEmpty()) {

            provider.setStatus("Produtos - Organizando produtos");
            LOG.finer("Lista de produtos antes do Garbage Collector: " + produtos.size());
            System.gc();
            List<ProdutoIMP> organizados = new Organizador(this).organizarListagem(produtos);
            if (filtrarProdutosInativos) {
                organizados = filtrarProdutosInativos(organizados);
            }
            MultiMap<Integer, Void> aliquotas = provider.aliquota().getAliquotas();

            java.sql.Date dataHoraImportacao = Utils.getDataAtual();
            String primeiraLojaMigrada = getPrimeiraLojaMigrada();

            produtos.clear();
            System.gc();

            try {
                provider.begin();

                LOG.info("importarSomenteLoja: " + importarSomenteLoja);

                LOG.info("Produtos a serem atualizados: " + organizados.size());

                /* Identificar qual foi a primeira loja que migrou */
                isDataAlteracaoCodAntProduto();

                StringBuilder strOpt = new StringBuilder();
                for (Iterator<OpcaoProduto> iterator = optSimples.iterator(); iterator.hasNext();) {
                    OpcaoProduto next = iterator.next();
                    strOpt.append(next.toString()).append(iterator.hasNext() ? ", " : "");
                }

                if (importarSomenteLoja) {
                    provider.setStatus("Filtrando produtos que foram inclusos por unificação ou mapeamento");
                    List<ProdutoIMP> a = new ArrayList<>();
                    for (ProdutoIMP imp : organizados) {
                        ProdutoAnteriorVO anterior = provider.anterior().getLojaImp(
                                provider.getSistema(),
                                provider.getLoja(),
                                imp.getImportId()
                        );
                        if (anterior != null) {
                            a.add(imp);
                        }
                    }
                    organizados = a;
                }
                System.gc();

                provider.setStatus("Produtos - Gravando alterações - " + strOpt);
                provider.setMaximum(organizados.size());

                if (optSimples.contains(OpcaoProduto.ESTOQUE)) {
                    provider.complemento().criarEstoqueAnteriorTemporario();
                }
                if (optSimples.contains(OpcaoProduto.TROCA)) {
                    provider.complemento().criarEstoqueTrocaAnteriorTemporario();
                }

                if (optSimples.contains(OpcaoProduto.CEST)) {
                    converterCest(organizados);
                }

                for (ProdutoIMP imp : organizados) {

                    ProdutoAnteriorVO anterior = null;

                    if (!importarSomenteLoja) {
                        anterior = provider.anterior().get(
                                provider.getSistema(),
                                provider.getLoja(),
                                imp.getImportId()
                        );
                    } else {
                        anterior = provider.anterior().getLojaImp(
                                provider.getSistema(),
                                provider.getLoja(),
                                imp.getImportId()
                        );
                    }

                    LOG.finer("Chave Prod: " + Arrays.deepToString(imp.getChave()));

                    if (anterior != null && anterior.getCodigoAtual() != null) {

                        LOG.finer("Anterior encontrado: " + anterior.getImportId() + " - " + anterior.getDescricao());

                        //<editor-fold defaultstate="collapsed" desc="Preparando variáveis">
                        int id = anterior.getCodigoAtual().getId();
                        long ean;
                        String strID;
                        boolean eBalanca;
                        TipoEmbalagem unidade;
                        {
                            SetUpVariaveisTO to = setUpVariaveis(imp);
                            ean = to.ean;
                            strID = to.strID;
                            eBalanca = to.eBalanca;
                            unidade = to.unidade;
                        }
                        //</editor-fold>

                        ProdutoVO prod = converterIMP(imp, id, unidade, eBalanca);

                        anterior = converterImpEmAnterior(imp);
                        anterior.setDataHoraAlteracao(dataHoraImportacao);
                        anterior.setCodigoAtual(prod);

                        ProdutoAliquotaVO aliquota = converterAliquota(imp);
                        aliquota.setProduto(prod);

                        ProdutoComplementoVO complemento = converterComplemento(imp);
                        complemento.setProduto(prod);
                        complemento.setIdAliquotaCredito(aliquota.getAliquotaCredito().getId());

                        ProdutoAutomacaoVO automacao = converterEAN(imp, ean, unidade);
                        automacao.setProduto(prod);

                        ProdutoAutomacaoLojaVO precoAtacadoLoja = converterProdutoAutomacaoLoja(imp);
                        ProdutoAutomacaoDescontoVO precoAtacadoDesconto = converterProdutoAutomacaoDesconto(imp);
                        AtacadoProdutoComplementoVO atacadoProdutoComplemento = converterAtacadoProdutoComplemtnto(imp, id);

                        precoAtacadoDesconto.setProduto(prod);

                        provider.atualizar(prod, optSimples);

                        if (versao.igualOuMaiorQue(4, 1, 39) && opcao == JOptionPane.NO_OPTION) {
                            provider.atualizarProdutoPisCofins(prod);
                        }

                        provider.complemento().atualizar(complemento, optSimples);

                        if (optSimples.contains(OpcaoProduto.ATACADO)) {
                            if (id > 0 && ean > 0) { //ID e EAN válidos
                                if (!provider.automacao().cadastrado(ean)) {
                                    automacao.setProduto(anterior.getCodigoAtual());
                                    provider.automacao().salvar(automacao);
                                }
                            }

                            if (!provider.eanAnterior().cadastrado(imp.getImportId(), imp.getEan())) {
                                ProdutoAnteriorEanVO eanAnterior = converterAnteriorEAN(imp);
                                provider.eanAnterior().salvar(eanAnterior, "");
                            }
                        }

                        if (optSimples.contains(OpcaoProduto.VR_ATACADO)) {
                            if (id > 0 && atacadoProdutoComplemento.getPrecoVenda() > 0) {
                                provider.vrAtacado().salvar(atacadoProdutoComplemento, optSimples);
                            }
                        }

                        provider.automacao().atualizar(automacao, optSimples);

                        if (aliquotas.containsKey(prod.getId(), aliquota.getEstado().getId())) {

                            if (optSimples.contains(OpcaoProduto.ICMS_LOJA)) {

                                String loja_sistema = imp.getImportSistema() + ' ' + imp.getImportLoja();
                                boolean isPrimeiraLojaMigrada = false;
                                if (loja_sistema.equals(primeiraLojaMigrada)) {
                                    isPrimeiraLojaMigrada = true;
                                }

                                provider.aliquota().atualizarIcmsLoja(aliquota, optSimples, anterior, isPrimeiraLojaMigrada);
                                provider.anterior().atualizarIcmsLoja(anterior, isPrimeiraLojaMigrada);

                            } else {
                                provider.aliquota().atualizar(aliquota, optSimples);
                                provider.anterior().atualizarIcms(anterior);
                            }
                        } else {
                            provider.aliquota().salvar(aliquota);
                            aliquotas.put(null, prod.getId(), aliquota.getEstado().getId());
                        }

                        if (optSimples.contains(OpcaoProduto.CODIGO_BENEFICIO)) {
                            int produtoAliquotaBeneficio = provider.aliquota().getProdutoAliquotaBeneficio(aliquota.getId()),
                                    beneficio = provider.aliquota().getBeneficio(imp.getBeneficio()),
                                    idProdutoAliquota = provider.aliquota().getProdutoAliquotaByProduto(prod.getId());

                            if (produtoAliquotaBeneficio != 0 && beneficio != 0 && idProdutoAliquota != 0) {
                                aliquota.setBeneficio(beneficio);
                                aliquota.setId(idProdutoAliquota);
                                provider.aliquota().atualizaBeneficio(aliquota);
                            } else if (produtoAliquotaBeneficio == 0 && beneficio != 0 && idProdutoAliquota != 0) {
                                aliquota.setBeneficio(beneficio);
                                aliquota.setId(idProdutoAliquota);
                                provider.aliquota().salvarAliquotaBeneficio(aliquota);
                            } else {
                                System.out.println("BENEFICIO: " + beneficio + " E PRODUTO: " + prod.getId() + " NÃO ENCONTRADO!");
                            }
                        }

                        if (versao.igualOuMenorQue(3, 18, 1)) {
                            if (precoAtacadoLoja.getPrecoVenda() > 0 && precoAtacadoLoja.getPrecoVenda() != complemento.getPrecoVenda()) {
                                provider.atacado().atualizarLoja(precoAtacadoLoja, optSimples);
                            }
                        }

                        if (precoAtacadoDesconto.getDesconto() > 0) {
                            provider.atacado().atualizarDesconto(precoAtacadoDesconto, optSimples);
                        }
                    }
                    provider.next();
                }

                if (versao.igualOuMaiorQue(4, 1, 39) && opcao == JOptionPane.YES_OPTION) {
                    provider.atualizarProdutoPisCofinsPelaProduto();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                if (optSimples.contains(OpcaoProduto.ESTOQUE)) {
                    provider.complemento().gerarLogDeImportacaoDeEstoque();

                    //Executa log de operação
                    logController.executar(EOperacao.ATUALIZAR_ESTOQUE.getId(),
                            sdf.format(new Date()),
                            provider.getLojaVR());
                }

                if (optSimples.contains(OpcaoProduto.TROCA)) {
                    provider.complemento().gerarLogDeTroca();
                }

                if (optSimples.contains(OpcaoProduto.PRECO)) {
                    //Executa log de operação
                    logController.executar(EOperacao.ATUALIZAR_PRECO.getId(),
                            sdf.format(new Date()),
                            provider.getLojaVR());
                }

                if (optSimples.contains(OpcaoProduto.CUSTO)) {
                    //Executa log de operação
                    logController.executar(EOperacao.ATUALIZAR_CUSTO.getId(),
                            sdf.format(new Date()),
                            provider.getLojaVR());
                }

                if (optSimples.contains(OpcaoProduto.ICMS)) {
                    //Executa log de operação
                    logController.executar(EOperacao.ATUALIZAR_ICMS.getId(),
                            sdf.format(new Date()),
                            provider.getLojaVR());
                }

                if (optSimples.contains(OpcaoProduto.PRECO)
                        || optSimples.contains(OpcaoProduto.CUSTO)
                        || optSimples.contains(OpcaoProduto.ESTOQUE)) {
                    logController.executarLogAtualizacao(organizados, getSistema(), getLoja());
                }

                provider.commit();
            } catch (Exception e) {
                provider.rollback();
                throw e;
            }
        }

        //Executa as opções que possuem lista, transformando-as em "Opcoes Simples".
        for (OpcaoProduto opt : optComLista) {
            List<ProdutoIMP> listaEspecial = opt.getListaEspecial();
            opt.setListaEspecial(null);
            if (op.contains(OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE)) {
                atualizar(listaEspecial, opt, OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE);
            } else {
                atualizar(listaEspecial, opt);
            }
        }
    }

    /**
     * Unifica uma listagem de produtos no sistema.
     *
     * @param produtos Listagem de {@link ProdutoIMP} a ser unificada.
     * @throws Exception
     */
    public void unificar(List<ProdutoIMP> produtos) throws Exception {
        importarMenoresQue7Digitos = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
        copiarIcmsDebitoParaCredito = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_COPIAR_ICMS_DEBITO_NO_CREDITO);
        boolean filtrarProdutosInativos = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_SOMENTE_PRODUTOS_ATIVOS);

        provider.begin();
        try {
            System.gc();
            List<ProdutoIMP> organizados = new Organizador(this).organizarListagem(produtos);
            if (filtrarProdutosInativos) {
                organizados = filtrarProdutosInativos(organizados);
            }
            produtos.clear();
            System.gc();

            ProdutoIDStack idStack = provider.getIDStack();
            String dataHoraImportacao = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            boolean unificarProdutoBalanca = provider.getOpcoes().contains(OpcaoProduto.UNIFICAR_PRODUTO_BALANCA);
            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN)) {
                this.naoTransformarEANemUN = true;
            }

            setNotify("Gravando os produtos (unificação)...", organizados.size());
            for (ProdutoIMP imp : organizados) {
                imp.setImportSistema(this.provider.getSistema());
                processarProdutoIMPParaUnificacao(imp, unificarProdutoBalanca, idStack, dataHoraImportacao);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //Executa log de operação
            logController.executar(EOperacao.UNIFICAR_PRODUTO.getId(),
                    sdf.format(new Date()),
                    provider.getLojaVR());

            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    String resetarIds(String strID, boolean eBalanca) {
        if (eBalanca) {
            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_RESETAR_BALANCA)) {
                return "-1";
            }
            return strID;
        }
        if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_RESETAR_NORMAIS)) {
            return "-1";
        }
        return strID;
    }

    private static class AliquotaICMS {

    }

    Map<String, Integer> codant;
    Map<Long, Integer> produtosPorEan;
    MultiMap<String, Integer> codigosAnterioresIdEan;

    public void unificar2(List<ProdutoIMP> produtos) throws Exception {
        importarMenoresQue7Digitos = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
        copiarIcmsDebitoParaCredito = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_COPIAR_ICMS_DEBITO_NO_CREDITO);
        boolean manterSomenteOsProdutosForcarNovo = Parametros.OpcoesExperimentaisDeProduto.isUnificarSomenteProdutosComForcarNovo();
        boolean incluirProdutosNovos = Parametros.OpcoesExperimentaisDeProduto.isIncluirProdutosNaoExistentes();
        boolean filtrarProdutosInativos = provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_SOMENTE_PRODUTOS_ATIVOS);

        verificarAliquotasMapeadas(produtos);
        verificarAliquotasNaoMapeadas(produtos);

        setNotify("Carregando os dados necessários...", 3);
        this.codant = provider.anterior().getAnterioresIncluindoComCodigoAtualNull();
        notificar();
        this.produtosPorEan = provider.automacao().getProdutosByEan();
        notificar();
        this.codigosAnterioresIdEan = provider.anterior().getAnterioresPorIdEan();
        notificar();
        int a = 0;

        provider.begin();
        try {
            setNotify("Carregando ids livres...", 0);
            ProdutoIDStack idStack = provider.getIDStack();
            String dataHoraImportacao = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            boolean unificarProdutoBalanca = provider.getOpcoes().contains(OpcaoProduto.UNIFICAR_PRODUTO_BALANCA);
            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN)) {
                this.naoTransformarEANemUN = true;
            }

            setNotify("Removendo da listagem produtos já importados e vinculados...", 0);
            produtos = new Organizador(this).organizarListagem(produtos);
            if (filtrarProdutosInativos) {
                produtos = filtrarProdutosInativos(produtos);
            }
            produtos = filtrarProdutosEEansJaMapeados(produtos);
            if (manterSomenteOsProdutosForcarNovo) {
                produtos = filtrarSomenteForcarNovo(produtos);
            }
            System.gc();

            vincularProdutoComEanInvalido(produtos, unificarProdutoBalanca, idStack, dataHoraImportacao);

            incluirEansNovosDeProdutosJaImportados(produtos, unificarProdutoBalanca, idStack, dataHoraImportacao);

            vincularProdutosQueSoExistemNoVrPorEan(produtos, unificarProdutoBalanca, idStack, dataHoraImportacao);

            if (incluirProdutosNovos) {
                incluirProdutosComEansNovos(produtos, unificarProdutoBalanca, idStack, dataHoraImportacao);
            }

            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    List<ProdutoIMP> filtrarProdutosInativos(List<ProdutoIMP> produtos) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp : produtos) {
            if (SituacaoCadastro.ATIVO.equals(imp.getSituacaoCadastro())) {
                result.add(imp);
            }
        }
        return result;
    }

    List<ProdutoIMP> filtrarSomenteForcarNovo(List<ProdutoIMP> produtos) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp : produtos) {
            if (!isProdutoVinculadoNaCodAnt(imp)) {
                continue;
            }
            if (isCodigoAnteriorComCodigoAtualPreenchido(imp)) {
                continue;
            }
            if (!isForcarNovo(imp)) {
                continue;
            }
            result.add(imp);
        }
        produtos.removeAll(result);
        return result;
    }

    boolean isCodigoAnteriorComCodigoAtualPreenchido(ProdutoIMP imp) {
        Integer codigoAtual = this.codant.get(imp.getImportId());
        return codigoAtual != null && codigoAtual != 0;
    }

    boolean isForcarNovo(ProdutoIMP imp) throws Exception {
        return provider.anterior().forcarNovo(imp.getImportId());
    }

    private void incluirProdutosComEansNovos(List<ProdutoIMP> produtos, boolean unificarProdutoBalanca, ProdutoIDStack idStack, String dataHoraImportacao) throws Exception {
        setNotify("Gravando os produtos novos...", produtos.size());
        for (ProdutoIMP imp : produtos) {
            processarProdutoIMPParaUnificacao(imp, unificarProdutoBalanca, idStack, dataHoraImportacao);
        }
    }

    private void vincularProdutosQueSoExistemNoVrPorEan(List<ProdutoIMP> produtos, boolean unificarProdutoBalanca, ProdutoIDStack idStack, String dataHoraImportacao) throws Exception {
        List<ProdutoIMP> produtosNaoVinculadosComEansExistentes = filtrarProdutosNaoVinculadosComEansExistentes(produtos);
        setNotify("Vinculando os produtos com EANs que já existem no VR...", produtosNaoVinculadosComEansExistentes.size());
        for (ProdutoIMP imp : produtosNaoVinculadosComEansExistentes) {
            processarProdutoIMPParaUnificacao(imp, unificarProdutoBalanca, idStack, dataHoraImportacao);
        }
        produtosNaoVinculadosComEansExistentes.clear();
        System.gc();
    }

    private void incluirEansNovosDeProdutosJaImportados(List<ProdutoIMP> produtos, boolean unificarProdutoBalanca, ProdutoIDStack idStack, String dataHoraImportacao) throws Exception {
        List<ProdutoIMP> produtosVinculadosComNovosEans = filtrarProdutosVinculadosComNovosEans(produtos);
        setNotify("Incluindo novos EANs de produtos já importados...", produtosVinculadosComNovosEans.size());
        for (ProdutoIMP imp : produtosVinculadosComNovosEans) {
            processarProdutoIMPParaUnificacao(imp, unificarProdutoBalanca, idStack, dataHoraImportacao);
        }
        produtosVinculadosComNovosEans.clear();
        System.gc();
    }

    private void vincularProdutoComEanInvalido(List<ProdutoIMP> produtos, boolean unificarProdutoBalanca, ProdutoIDStack idStack, String dataHoraImportacao) throws Exception {
        List<ProdutoIMP> produtosComEanInvalido = filtrarProdutosComEanInvalido(produtos);
        setNotify("Registrando na codant ou forçando a gravação de EANs inválidos...", produtosComEanInvalido.size());
        for (ProdutoIMP imp : produtosComEanInvalido) {
            processarProdutoIMPParaUnificacao(imp, unificarProdutoBalanca, idStack, dataHoraImportacao);
        }
        produtosComEanInvalido.clear();
        System.gc();
    }

    private void verificarAliquotasNaoMapeadas(List<ProdutoIMP> produtos) throws Exception, NullPointerException {
        Set<IcmsWraper> icmss = new HashSet<>();
        for (ProdutoIMP imp : produtos) {
            icmss.add(new IcmsWraper(imp.getIcmsCstSaida(), imp.getIcmsAliqSaida(), imp.getIcmsReducaoSaida()));
        }
        StringBuilder builder = new StringBuilder();
        for (IcmsWraper icms : icmss) {
            Icms ret = Icms.getIcmsPorValor(icms.cst, icms.aliquota, icms.reducao);
            if (ret == null) {
                builder.append(String.format("(cst:%d aliq: %.2f red: %.2f)", icms.cst, icms.aliquota, icms.reducao)).append("\n");
            }
        }
        if (!builder.toString().isEmpty()) {
            String msg = "Os seguintes ICMSs não foram encontrados:\n" + builder.toString();
            System.out.println(msg);
            if (!Parametros.get().isImportarIcmsIsentoMigracaoProduto()) {
                throw new NullPointerException(msg);
            }
        }
    }

    private void verificarAliquotasMapeadas(List<ProdutoIMP> produtos) throws Exception, NullPointerException {
        Set<String> icmsIds = new HashSet<>();
        for (ProdutoIMP imp : produtos) {
            icmsIds.add(imp.getIcmsDebitoId());
            icmsIds.add(imp.getIcmsDebitoForaEstadoId());
            icmsIds.add(imp.getIcmsDebitoForaEstadoNfId());
            icmsIds.add(imp.getIcmsConsumidorId());
            icmsIds.add(imp.getIcmsCreditoId());
            icmsIds.add(imp.getIcmsCreditoForaEstadoId());
        }
        StringBuilder builder = new StringBuilder();
        for (String icmsId : icmsIds) {
            if (icmsId != null && !"".equals(icmsId.trim())) {
                Icms icms = this.provider.tributo().getAliquotaByMapaId(icmsId, true);
                if (icms == null) {
                    builder.append(icmsId).append(",");
                }
            }
        }
        if (!builder.toString().isEmpty()) {
            String msg = "Os seguintes ids de alíquota não foram encontrados no mapeamento:\n" + builder.toString();
            System.out.println(msg);
            if (!Parametros.get().isImportarIcmsIsentoMigracaoProduto()) {
                throw new NullPointerException(msg);
            }
        }
    }

    private static class IcmsWraper {

        private final int cst;
        private final double aliquota;
        private final double reducao;

        public IcmsWraper(int cst, double aliquota, double reducao) {
            this.cst = cst;
            this.aliquota = aliquota;
            this.reducao = reducao;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + this.cst;
            hash = 53 * hash + (int) (Double.doubleToLongBits(this.aliquota) ^ (Double.doubleToLongBits(this.aliquota) >>> 32));
            hash = 53 * hash + (int) (Double.doubleToLongBits(this.reducao) ^ (Double.doubleToLongBits(this.reducao) >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IcmsWraper other = (IcmsWraper) obj;
            if (this.cst != other.cst) {
                return false;
            }
            if (Double.doubleToLongBits(this.aliquota) != Double.doubleToLongBits(other.aliquota)) {
                return false;
            }
            return Double.doubleToLongBits(this.reducao) == Double.doubleToLongBits(other.reducao);
        }

    }

    List<ProdutoIMP> filtrarProdutosEEansJaMapeados(List<ProdutoIMP> produtos) {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp : produtos) {
            if (isEanEIdExistenteNaCodAnt(imp)) {
                continue;
            }
            result.add(imp);
        }
        return result;
    }

    private boolean isEanEIdExistenteNaCodAnt(ProdutoIMP imp) {
        Integer codigoatual = this.codigosAnterioresIdEan.get(imp.getImportId(), imp.getEan());
        return codigoatual != null;
    }

    List<ProdutoIMP> filtrarProdutosComEanInvalido(List<ProdutoIMP> produtos) {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp : produtos) {
            long ean = Utils.stringToLong(imp.getEan(), -2);
            if (ean > 999999) {
                continue;
            }
            result.add(imp);
        }
        produtos.removeAll(result);
        return result;
    }

    List<ProdutoIMP> filtrarProdutosVinculadosComNovosEans(List<ProdutoIMP> produtos) {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp : produtos) {
            if (!isProdutoVinculadoNaCodAnt(imp)) {
                continue;
            }
            if (isEanExistenteNoVR(imp)) {
                continue;
            }
            result.add(imp);
        }
        produtos.removeAll(result);
        return result;
    }

    private boolean isProdutoVinculadoNaCodAnt(ProdutoIMP imp) {
        return this.codant.containsKey(imp.getImportId());
    }

    private boolean isEanExistenteNoVR(ProdutoIMP imp) {
        return this.produtosPorEan.containsKey(Utils.stringToLong(imp.getEan()));
    }

    List<ProdutoIMP> filtrarProdutosNaoVinculadosComEansExistentes(List<ProdutoIMP> produtos) {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp : produtos) {
            long ean = Utils.stringToLong(imp.getEan());
            boolean eanNaoExisteNoVR = !this.produtosPorEan.containsKey(ean);

            if (eanNaoExisteNoVR) {
                continue;
            }

            result.add(imp);
        }
        produtos.removeAll(result);
        return result;
    }

    private void processarProdutoIMPParaUnificacao(ProdutoIMP imp, boolean unificarProdutoBalanca, ProdutoIDStack idStack, String dataHoraImportacao) throws Exception {
        String obsImportacao = "";
        String obsImportacaoEan = "";
        imp.setManterEAN(false);
        //<editor-fold defaultstate="collapsed" desc="Preparando variáveis">
        int id;
        long ean;
        String strID;
        boolean eBalanca;
        TipoEmbalagem unidade;
        {
            SetUpVariaveisTO to = setUpVariaveis(imp);
            ean = to.ean;
            strID = to.strID;
            eBalanca = to.eBalanca;
            unidade = to.unidade;
        }
        //</editor-fold>
        /**
         * Variaveis foram criadas para facilitar a leitura do código.
         */
        boolean forcarNovo = provider.anterior().forcarNovo(strID);
        boolean eanValido = unificarProdutoBalanca || (ean > 999999) || forcarNovo;
        int idProdutoExistente = provider.automacao().getIdProdutoPorEAN(ean);
        boolean eanExistente = idProdutoExistente > 0;
        ProdutoVO codigoAtual = null;
        if (eanValido) {
            if (!eanExistente || forcarNovo) {
                /**
                 * Mesmo que um determinado EAN não esteja cadastrado no sistema
                 * (pois o mesmo pode ter sido excluído por um usuário), é
                 * prudente verificar o código anterior para determinar se este
                 * produto foi importado anteriormente e gravar o EAN no produto
                 * correto e evitar duplicação.
                 */
                //Se o produto não foi importado, um novo produto é criado.
                if (!provider.anterior().cadastrado(imp.getImportId())) {
                    if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA) && eBalanca) {
                        strID = String.valueOf(ean);
                    }
                    strID = resetarIds(strID, eBalanca);

                    id = idStack.obterID(strID, eBalanca);

                    codigoAtual = converterIMP(imp, id, unidade, eBalanca);

                    ProdutoAliquotaVO aliquota = converterAliquota(imp);
                    aliquota.setProduto(codigoAtual);

                    ProdutoComplementoVO complemento = converterComplemento(imp);
                    complemento.setProduto(codigoAtual);
                    complemento.setIdAliquotaCredito(aliquota.getAliquotaCredito().getId());

                    provider.salvar(codigoAtual);
                    if (versao.igualOuMaiorQue(4, 1)) {
                        provider.salvarProdutoPisCofins(codigoAtual);
                    }
                    obsImportacao = "PRODUTO NOVO - INSERIDO PELO METODO unificar DA CLASSE " + ProdutoRepository.class.getName().toString();
                    obsImportacaoEan = "EAN NOVO - INSERIDO PELO METODO unificar DA CLASSE " + ProdutoRepository.class.getName().toString();;
                    //provider.anterior().salvar(anterior);
                    double estoque = complemento.getEstoque();
                    for (LojaVO loja : provider.getLojas()) {
                        complemento.setIdLoja(loja.getId());
                        if (loja.getId() == getLojaVR()) {
                            complemento.setEstoque(estoque);
                        } else {
                            complemento.setEstoque(0);
                        }
                        provider.complemento().salvar(complemento, false);

                    }
                    provider.aliquota().salvar(aliquota);
                } else {

                    codigoAtual = provider.anterior().get(
                            provider.getSistema(),
                            provider.getLoja(),
                            imp.getImportId()
                    ).getCodigoAtual();

                    if (codigoAtual == null) {

                        if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA) && eBalanca) {
                            strID = String.valueOf(ean);
                        }
                        strID = resetarIds(strID, eBalanca);

                        id = idStack.obterID(strID, eBalanca);

                        if (forcarNovo) {
                            ean = id;
                        }

                        codigoAtual = converterIMP(imp, id, unidade, eBalanca);

                        ProdutoAliquotaVO aliquota = converterAliquota(imp);
                        aliquota.setProduto(codigoAtual);

                        ProdutoComplementoVO complemento = converterComplemento(imp);
                        complemento.setProduto(codigoAtual);
                        complemento.setIdAliquotaCredito(aliquota.getAliquotaCredito().getId());

                        provider.salvar(codigoAtual);
                        provider.salvarProdutoPisCofins(codigoAtual);

                        obsImportacao = "PRODUTO NOVO - INSERIDO PELO METODO unificar DA CLASSE "
                                + ProdutoRepository.class.getName().toString();
                        obsImportacaoEan = "EAN NOVO - INSERIDO PELO METODO unificar DA CLASSE "
                                + ProdutoRepository.class.getName().toString();;

                        //provider.anterior().salvar(anterior);
                        double estoque = complemento.getEstoque();
                        for (LojaVO loja : provider.getLojas()) {
                            complemento.setIdLoja(loja.getId());
                            if (loja.getId() == getLojaVR()) {
                                complemento.setEstoque(estoque);
                            } else {
                                complemento.setEstoque(0);
                            }
                            provider.complemento().salvar(complemento, false);

                        }
                        provider.aliquota().salvar(aliquota);

                        if (forcarNovo) {
                            obsImportacao = "PRODUTO NOVO - INSERIDO PELO MAPEAMENTO (FORCAR NOVO)";
                            gravarCodigoAtual(provider.getSistema(), imp.getImportLoja(), imp.getImportId(), codigoAtual, obsImportacao);
                            ProdutoAutomacaoVO automacao = converterEAN(imp, ean, unidade);
                            automacao.setProduto(codigoAtual);
                            provider.automacao().salvar(automacao);
                        }
                    } else {
                        obsImportacao = "PRODUTO UNIFICADO - UNIFICADO PELO METODO unificar DA CLASSE " + ProdutoRepository.class.getName().toString();
                        obsImportacaoEan = "EAN UNIFICADO - UNIFICADO PELO METODO unificar DA CLASSE " + ProdutoRepository.class.getName().toString();
                    }
                }
                /**
                 * Cadastra o EAN no sistema.
                 */
                if (codigoAtual != null) {
                    ProdutoAutomacaoVO automacao = converterEAN(imp, ean, unidade);
                    automacao.setProduto(codigoAtual);
                    provider.automacao().salvar(automacao);

                    gravarCodigoAtual(provider.getSistema(), imp.getImportLoja(), imp.getImportId(), codigoAtual, obsImportacao);
                }
            } else {
                id = idProdutoExistente;
                codigoAtual = new ProdutoVO();
                codigoAtual.setId(id);
                obsImportacao = "PRODUTO UNIFICADO - UNIFICADO PELO METODO unificar DA CLASSE " + ProdutoRepository.class.getName().toString();
                obsImportacaoEan = "EAN UNIFICADO EM OUTRO PRODUTO - UNIFICADO AO PRODUTO DE CODIGO ATUAL " + codigoAtual.getId();

                // gravar codigo atual se for null
                gravarCodigoAtual(provider.getSistema(), imp.getImportLoja(), imp.getImportId(), codigoAtual, obsImportacao);
            }
        }
        /**
         * Independentemente se o produto foi gravado ou não, o código anterior
         * deve ser registrado.
         */
        if (!provider.anterior().cadastrado(imp.getImportId())) {
            ProdutoAnteriorVO anterior = converterImpEmAnterior(imp);
            anterior.setCodigoAtual(codigoAtual);
            anterior.setDataHora(dataHoraImportacao);
            anterior.setIdConexao(this.provider.getIdConexao());

            if (anterior.getCodigoAtual() == null) {
                obsImportacao = "PRODUTO NAO LOCALIZADO NA UNIFICACAO";
            }

            anterior.setObsImportacao(obsImportacao);
            provider.anterior().salvar(anterior);
        }
        if (!provider.eanAnterior().cadastrado(imp.getImportId(), imp.getEan())) {
            ProdutoAnteriorEanVO eanAnterior = converterAnteriorEAN(imp);

            provider.eanAnterior().salvar(eanAnterior, obsImportacaoEan);
        }
        notificar();
    }

    private int countSemAnterior = 0;

    public void gravarCodigoAtual(String impsistema, String imploja, String impid, ProdutoVO codigoAtual, String obsimportacao) throws Exception {
        try {
            Conexao.begin();

            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();

                sql.setSchema("implantacao");
                sql.setTableName("codant_produto");
                sql.setWhere("impid = " + Utils.quoteSQL(impid)
                        + " and imploja = '" + imploja + "'"
                        + " and impsistema = '" + impsistema + "'"
                        + " and codigoatual is null"
                );
                sql.put("codigoatual", codigoAtual.getId());
                sql.put("obsimportacao", obsimportacao);

                stm.execute(sql.getUpdate());
                ProdutoAnteriorVO anterior = provider.anterior().get(
                        impsistema,
                        imploja,
                        impid
                );
                if (anterior != null) {
                    anterior.setCodigoAtual(codigoAtual);
                } else {
                    countSemAnterior++;
                    System.out.println(countSemAnterior + " - Anterior não encontrado: " + String.format(
                            "%s-%s-%s - ca %d",
                            impsistema,
                            imploja,
                            impid,
                            codigoAtual.getId()
                    ));
                }
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * Converte um {@link ProdutoIMP} em {@link ProdutoAliquotaVO}.
     *
     * @param imp
     * @return
     * @throws Exception
     */
    public ProdutoAliquotaVO converterAliquota(ProdutoIMP imp) throws Exception {
        ProdutoAliquotaVO aliquota = new ProdutoAliquotaVO();
        aliquota.setEstado(provider.tributo().getUf(getLojaVR()));

        Icms aliqCredito;
        Icms aliqDebito;
        Icms debitoForaEstado;
        Icms creditoForaEstado;
        Icms debitoForaEstadoNfe;
        Icms consumidor;

        String idIcmsDebito = imp.getIcmsDebitoId();
        String idIcmsDebitoForaEstado = imp.getIcmsDebitoForaEstadoId();
        String idIcmsDebitoForaEstadoNf = imp.getIcmsDebitoForaEstadoNfId();
        String idIcmsCredito = imp.getIcmsCreditoId();
        String idIcmsCreditoForaEstado = imp.getIcmsCreditoForaEstadoId();
        String idIcmsCreditoFornecedor = imp.getIcmsCreditoId();
        String idIcmsConsumidor = imp.getIcmsConsumidorId();

        if (idIcmsDebito != null) {

            aliqDebito = provider.tributo().getAliquotaByMapaId(idIcmsDebito);
            debitoForaEstado = provider.tributo().getAliquotaByMapaId(idIcmsDebitoForaEstado);
            debitoForaEstadoNfe = provider.tributo().getAliquotaByMapaId(idIcmsDebitoForaEstadoNf);
            consumidor = provider.tributo().getAliquotaByMapaId(idIcmsConsumidor);

            if (debitoForaEstado == null) {
                debitoForaEstado = aliqDebito;
            }
            if (debitoForaEstadoNfe == null) {
                debitoForaEstadoNfe = aliqDebito;
            }

            int icmsCstSaida = aliqDebito.getCst();
            double icmsAliqSaida = aliqDebito.getAliquota();
            double icmsReducaoSaida = aliqDebito.getReduzido();

            if (consumidor == null) {
                if (icmsCstSaida == 20) {
                    /*
                     Comentado o calculo da alíquota consumidor por questão de impressora NFC-e
                     */
                    //double aliq = MathUtils.round(icmsAliqSaida - (icmsAliqSaida * (icmsReducaoSaida / 100)), 0);
                    consumidor = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, icmsReducaoSaida);
                } else {
                    consumidor = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, 0);
                }
            }
        } else {
            int icmsCstSaida = imp.getIcmsCstSaida();
            double icmsAliqSaida = 0;
            double icmsReducaoSaida = 0;

            int icmsCstSaidaForaEstado = imp.getIcmsCstSaidaForaEstado();
            double icmsAliqSaidaForaEstado = 0;
            double icmsReducaoSaidaForaEstado = 0;

            int icmsCstSaidaForaEstadoNF = imp.getIcmsCstSaidaForaEstadoNF();
            double icmsAliqSaidaForaEstadoNF = 0;
            double icmsReducaoSaidaForaEstadoNF = 0;

            int icmsCstConsumidor = imp.getIcmsCstConsumidor();
            double icmsAliqConsumidor = 0;
            double icmsReducaoConsumidor = 0;

            if (icmsCstSaida == 20 || icmsCstSaida == 0) {
                icmsAliqSaida = imp.getIcmsAliqSaida();
                icmsReducaoSaida = imp.getIcmsReducaoSaida();
            }
            if (icmsCstSaidaForaEstado == 20 || icmsCstSaidaForaEstado == 0) {
                icmsAliqSaidaForaEstado = imp.getIcmsAliqSaidaForaEstado();
                icmsReducaoSaidaForaEstado = imp.getIcmsReducaoSaidaForaEstado();
            }
            if (icmsCstSaidaForaEstadoNF == 20 || icmsCstSaidaForaEstadoNF == 0) {
                icmsAliqSaidaForaEstadoNF = imp.getIcmsAliqSaidaForaEstadoNF();
                icmsReducaoSaidaForaEstadoNF = imp.getIcmsReducaoSaidaForaEstadoNF();
            }
            if (icmsCstConsumidor == 20 || icmsCstConsumidor == 0) {
                icmsAliqConsumidor = imp.getIcmsAliqConsumidor();
                icmsReducaoConsumidor = imp.getIcmsReducaoConsumidor();
            }

            aliqDebito = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, icmsReducaoSaida);
            debitoForaEstado = provider.tributo().getIcms(icmsCstSaidaForaEstado, icmsAliqSaidaForaEstado, icmsReducaoSaidaForaEstado);
            debitoForaEstadoNfe = provider.tributo().getIcms(icmsCstSaidaForaEstadoNF, icmsAliqSaidaForaEstadoNF, icmsReducaoSaidaForaEstadoNF);
            consumidor = provider.tributo().getIcms(icmsCstConsumidor, icmsAliqConsumidor, icmsReducaoConsumidor);

            if (debitoForaEstado == null) {
                debitoForaEstado = aliqDebito;
            }
            if (debitoForaEstadoNfe == null) {
                debitoForaEstadoNfe = aliqDebito;
            }

            if (consumidor == null) {
                if (icmsCstSaida == 20) {
                    /*
                     Comentado o calculo da alíquota consumidor 
                     por questão de impressora NFC-e
                     */
                    //double aliq = MathUtils.round(icmsAliqSaida - (icmsAliqSaida * (icmsReducaoSaida / 100)), 1);
                    consumidor = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, icmsReducaoSaida);
                } else {
                    consumidor = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, 0);
                }
            }
        }

        if (copiarIcmsDebitoParaCredito) {
            aliqCredito = aliqDebito;
            creditoForaEstado = debitoForaEstado;
        } else {
            if (idIcmsCredito != null) {
                aliqCredito = provider.tributo().getAliquotaByMapaId(idIcmsCredito);
                creditoForaEstado = provider.tributo().getAliquotaByMapaId(idIcmsCreditoForaEstado);
                if (creditoForaEstado == null) {
                    creditoForaEstado = aliqCredito;
                }
            } else {

                int icmsCstEntrada = imp.getIcmsCstEntrada();
                double icmsAliqEntrada = 0;
                double icmsReducaoEntrada = 0;

                int icmsCstEntradaForaEstado = imp.getIcmsCstEntradaForaEstado();
                double icmsAliqEntradaForaEstado = 0;
                double icmsReducaoEntradaForaEstado = 0;

                if (icmsCstEntrada == 20 || icmsCstEntrada == 0) {
                    icmsAliqEntrada = imp.getIcmsAliqEntrada();
                    icmsReducaoEntrada = imp.getIcmsReducaoEntrada();
                }
                if (icmsCstEntradaForaEstado == 20 || icmsCstEntradaForaEstado == 0) {
                    icmsAliqEntradaForaEstado = imp.getIcmsAliqEntradaForaEstado();
                    icmsReducaoEntradaForaEstado = imp.getIcmsReducaoEntradaForaEstado();
                }

                aliqCredito = provider.tributo().getIcms(icmsCstEntrada, icmsAliqEntrada, icmsReducaoEntrada);
                creditoForaEstado = provider.tributo().getIcms(icmsCstEntradaForaEstado, icmsAliqEntradaForaEstado, icmsReducaoEntradaForaEstado);

                if (creditoForaEstado == null) {
                    creditoForaEstado = aliqCredito;
                }
            }
        }

        aliquota.setAliquotaCredito(aliqCredito);
        aliquota.setAliquotaDebito(aliqDebito);
        aliquota.setAliquotaDebitoForaEstado(debitoForaEstado);
        aliquota.setAliquotaCreditoForaEstado(creditoForaEstado);
        aliquota.setAliquotaDebitoForaEstadoNf(debitoForaEstadoNfe);
        aliquota.setAliquotaConsumidor(consumidor);

        if (idIcmsCreditoFornecedor != null) {
            aliquota.setAliquotaCreditoFornecedor(idIcmsCreditoFornecedor);
        }

        aliquota.setExcecao(obterPautaFiscal(imp.getPautaFiscalId()));

        int idBeneficio = provider.aliquota().getBeneficio(imp.getBeneficio());
        aliquota.setBeneficio(idBeneficio);

        /*
        if (idBeneficio != 0) {
            provider.aliquota().salvarAliquotaBeneficio(aliquota);
        }*/
        return aliquota;
    }

    /**
     * Converte um {@link ProdutoIMP} em um {@link ProdutoComplementoVO} e o
     * incluí no {@link ProdutoVO} informado.
     *
     * @param imp {@link ProdutoIMP} de origem.
     * @return {@link ProdutoComplementoVO} convertido.
     * @exception Exception
     */
    public ProdutoComplementoVO converterComplemento(ProdutoIMP imp) throws Exception {
        ProdutoComplementoVO complemento = new ProdutoComplementoVO();

        Calendar dataAtual = Calendar.getInstance();
        dataAtual.add(Calendar.DATE, -90);
        Date noventaDias = dataAtual.getTime();

        complemento.setIdLoja(getLojaVR());
        complemento.setEstoqueMinimo(imp.getEstoqueMinimo());
        complemento.setEstoqueMaximo(imp.getEstoqueMaximo());
        complemento.setEstoque(imp.getEstoque());
        complemento.setTroca(imp.getTroca());
        complemento.setPrecoDiaSeguinte(imp.getPrecovenda());
        complemento.setPrecoVenda(imp.getPrecovenda());
        complemento.setCustoSemImposto(imp.getCustoSemImposto());
        complemento.setCustoComImposto(imp.getCustoComImposto());
        complemento.setCustoAnteriorSemImposto(imp.getCustoAnteriorSemImposto());
        complemento.setCustoAnteriorComImposto(imp.getCustoAnteriorComImposto());
        complemento.setCustoMedioComImposto(imp.getCustoMedioComImposto());
        complemento.setCustoMedioSemImposto(imp.getCustoMedioSemImposto());
        complemento.setDescontinuado(imp.isDescontinuado());
        complemento.setSituacaoCadastro(imp.getSituacaoCadastro());
        complemento.setTipoProduto(imp.getTipoProduto());
        complemento.setTipoAtacado(imp.getTipoAtacado());
        complemento.setFabricacaoPropria(imp.isFabricacaoPropria());
        complemento.setEmiteEtiqueta(imp.isEmiteEtiqueta());
        complemento.setDataPrimeiraAlteracao(imp.getDataCadastro() == null ? noventaDias : imp.getDataCadastro());
        complemento.setNormaReposicao(imp.getNormaReposicao());
        complemento.setSetor(imp.getSetor());
        complemento.setPrateleira(imp.getPrateleira());
        complemento.setTeclaassociada((int) imp.getTeclaAssociada());
        complemento.setMargem(imp.getMargem());
        complemento.setMargemMinima(imp.getMargemMinima());
        complemento.setMargemMaxima(imp.getMargemMaxima());
        complemento.setOperacional(imp.getOperacional());
        complemento.setValidade(imp.getValidade());

        return complemento;
    }

    /**
     * Converte um {@link ProdutoIMP} em um {@link ProdutoAnteriorEanVO}.
     *
     * @param imp {@link ProdutoIMP} a ser convertido.
     * @return {@link ProdutoAnteriorEanVO} convertido.
     */
    public ProdutoAnteriorEanVO converterAnteriorEAN(ProdutoIMP imp) {
        ProdutoAnteriorEanVO eanAnterior = new ProdutoAnteriorEanVO();
        eanAnterior.setImportSistema(imp.getImportSistema());
        eanAnterior.setImportLoja(imp.getImportLoja());
        eanAnterior.setImportId(imp.getImportId());
        eanAnterior.setEan(imp.getEan());
        eanAnterior.setQtdEmbalagem(imp.getQtdEmbalagem());
        eanAnterior.setTipoEmbalagem(imp.getTipoEmbalagem());
        eanAnterior.setValor(0);
        return eanAnterior;
    }

    private Map<String, Integer> fabricantes = null;
    private Map<String, Integer> compradores = null;
    private Map<String, Integer> codigosAnp = null;

    private String fillNull(String value) {
        return value != null ? value : "";
    }

    /**
     * Converte um {@link ProdutoIMP} em um {@link ProdutoVO}.
     *
     * @param imp {@link ProdutoIMP} a ser convertido.
     * @param id ID que será utilizado pelo produto.
     * @param ean EAN do produto.
     * @param unidade Tipo da embalagem do produto.
     * @param eBalanca Se o produto é ou não de balança.
     * @return {@link ProdutoAnteriorEanVO} convertido.
     * @exception Exception
     */
    public ProdutoVO converterIMP(ProdutoIMP imp, int id,
            TipoEmbalagem unidade, boolean eBalanca) throws Exception {

        manterDescricao = provider.getOpcoes().contains(OpcaoProduto.MANTER_DESCRICAO_PRODUTO);

        if (fabricantes == null) {
            fabricantes = provider.getFornecedoresImportados();
        }
        if (compradores == null) {
            compradores = provider.getCompradores();
        }
        if (codigosAnp == null) {
            codigosAnp = provider.getCodigoAnp();
        }

        ProdutoVO vo = new ProdutoVO();

        vo.setManterDescricao(manterDescricao);
        vo.setId(id);
        vo.setDescricaoCompleta(imp.getDescricaoCompleta());
        if ("SEM DESCRICAO".equals(imp.getDescricaoReduzida())) {
            vo.setDescricaoReduzida(vo.getDescricaoCompleta());
        }
        if ("SEM DESCRICAO".equals(imp.getDescricaoGondola())) {
            vo.setDescricaoGondola(vo.getDescricaoCompleta());
        }
        vo.setDescricaoReduzida(imp.getDescricaoReduzida());
        vo.setDescricaoGondola(imp.getDescricaoGondola());

        if (vo.getId() == 1) {
            System.out.println("imp " + imp.getDescricaoReduzida());
            System.out.println("vo " + vo.getDescricaoReduzida());
        }

        vo.setQtdEmbalagem(imp.getQtdEmbalagemCotacao() == 0 ? 1 : imp.getQtdEmbalagemCotacao());
        vo.setSugestaoCotacao(imp.isSugestaoCotacao());
        vo.setSugestaoPedido(imp.isSugestaoPedido());
        vo.setCest(provider.tributo().getCest(imp.getCest()));
        if (vo.getCest().getId() == 0) {
            vo.setCest(null);
        }
        vo.setNcm(provider.tributo().getNcm(imp.getNcm()));
        vo.setDatacadastro(imp.getDataCadastro());
        if (vo.getDatacadastro() == null) {
            vo.setDatacadastro(new Date());
        }
        vo.setDataAlteracao(imp.getDataAlteracao());
        if (vo.getDataAlteracao() == null) {
            vo.setDataAlteracao(new Date());
        }
        Integer fornecedorFabricante = fabricantes.get(imp.getFornecedorFabricante());
        if (fornecedorFabricante != null) {
            vo.setIdFornecedorFabricante(fornecedorFabricante);
        } else {
            vo.setIdFornecedorFabricante(1);
        }

        vo.setFamiliaProduto(provider.getFamiliaProduto(imp.getIdFamiliaProduto()));
        vo.setMargem(imp.getMargem());
        vo.setMargemMinima(imp.getMargemMinima());
        vo.setMargemMaxima(imp.getMargemMaxima());
        MercadologicoVO merc = provider.getMercadologico(
                fillNull(imp.getCodMercadologico1()),
                fillNull(imp.getCodMercadologico2()),
                fillNull(imp.getCodMercadologico3()),
                fillNull(imp.getCodMercadologico4()),
                fillNull(imp.getCodMercadologico5())
        );

        if (merc == null) {
            LOG.severe("Mercadológico vazio no item " + imp.getImportId() + " - " + imp.getDescricaoCompleta());
        }
        if (merc.getNivel() != provider.getNivelMaximoMercadologico()) {
            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC)) {
                if (merc.getNivel() == 1) {
                    merc.setMercadologico2(1);
                    merc.setMercadologico3(1);
                    if (provider.getNivelMaximoMercadologico() >= 4) {
                        merc.setMercadologico4(1);
                    }
                    if (provider.getNivelMaximoMercadologico() == 5) {
                        merc.setMercadologico5(1);
                    }
                } else if (merc.getNivel() == 2) {
                    merc.setMercadologico3(1);
                    if (provider.getNivelMaximoMercadologico() >= 4) {
                        merc.setMercadologico4(1);
                    }
                    if (provider.getNivelMaximoMercadologico() == 5) {
                        merc.setMercadologico5(1);
                    }
                } else if (merc.getNivel() == 3) {
                    if (provider.getNivelMaximoMercadologico() >= 4) {
                        merc.setMercadologico4(1);
                    }
                    if (provider.getNivelMaximoMercadologico() == 5) {
                        merc.setMercadologico5(1);
                    }
                } else if (merc.getNivel() == 4) {
                    if (provider.getNivelMaximoMercadologico() == 5) {
                        merc.setMercadologico5(1);
                    }
                }
            } else {
                merc = provider.getMercadologico("", "", "", "", "");
            }
        }
        vo.setMercadologico(merc);

        if (vo.getMercadologico().getMercadologico2() == 0) {
            vo.getMercadologico().setMercadologico2(1);
        }
        if (vo.getMercadologico().getMercadologico3() == 0) {
            vo.getMercadologico().setMercadologico3(1);
        }
        //<editor-fold defaultstate="collapsed" desc="Tratamento dos produtos de Kilo e Unitário Pesável">

        if (eBalanca) {
            if (TipoEmbalagem.UN.equals(unidade)) {
                vo.setPesavel(true);
            } else {
                vo.setPesavel(false);
            }
            vo.setTipoEmbalagem(unidade);
        } else {
            TipoEmbalagem unidadeCotacao = TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagemCotacao(), true);
            if (unidadeCotacao != null) {
                vo.setTipoEmbalagem(unidadeCotacao);
            } else {
                vo.setTipoEmbalagem(TipoEmbalagem.CX);
            }
            vo.setPesavel(false);
        }

        vo.setPesoBruto(imp.getPesoBruto());
        vo.setPesoLiquido(imp.getPesoLiquido());

        convertPisCofins(imp, vo);

        vo.setValidade(imp.getValidade());
        vo.setExcecao(obterPautaFiscal(imp.getPautaFiscalId()));
        vo.setVendaPdv(imp.isVendaPdv());
        vo.setAceitaMultiplicacaoPDV(imp.isAceitaMultiplicacaoPDV());

        vo.setQtdDiasMinimoValidade(imp.getQtdDiasMinimoValidade());

        //Importação da divisão de fornecedores
        Entry<String, Integer> divisaoFornecedor = this.divisoes.get(imp.getDivisao());
        if (divisaoFornecedor != null && divisaoFornecedor.getValue() != null) {
            vo.setIdDivisaoFornecedor(divisaoFornecedor.getValue());
        }

        /**
         * Busca e se existir, relaciona o produto com o comprador.
         */
        Integer comprador = compradores.get(imp.getIdComprador());
        if (comprador != null) {
            vo.setIdComprador(comprador);
        }

        if (imp.getTipoEmbalagemVolume() == null || imp.getTipoEmbalagemVolume().trim().equals("")) {
            vo.setTipoEmbalagemVolume(vo.getTipoEmbalagem());
        } else {
            vo.setTipoEmbalagemVolume(TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagemVolume()));
        }
        vo.setVolume(imp.getVolume());
        vo.setVendaControlada(imp.isVendaControlada());
        vo.setProdutoecommerce(imp.isProdutoECommerce());
        Integer codigoANP = codigosAnp.get(imp.getCodigoAnp());
        if (codigoANP != null) {
            vo.setCodigoAnp(codigoANP);
        }

        vo.setNumeroparcela(imp.getNumeroparcela());

        ProdutoAnteriorVO anteriorVasilhame = provider.anterior().get(
                provider.getSistema(),
                provider.getLoja(),
                imp.getIdVasilhame());

        if (anteriorVasilhame != null && anteriorVasilhame.getCodigoAtual().getId() != 0) {
            vo.setIdVasilhame(anteriorVasilhame.getCodigoAtual().getId());
        }
        vo.setPercentualPerda(imp.getPercentualPerda());
        vo.setTipoCompra(imp.getTipoCompra());

        return vo;
    }

    public void convertPisCofins(ProdutoIMP imp, ProdutoVO vo) throws Exception {

        int pisCofinsDebito, pisCofinsCredito;
        if (imp.getPiscofinsCstDebito() != 0 && imp.getPiscofinsCstCredito() == 0) {
            pisCofinsDebito = imp.getPiscofinsCstDebito();
            pisCofinsCredito = converterDebitoParaCredito(imp.getPiscofinsCstDebito());
        } else if (imp.getPiscofinsCstDebito() == 0 && imp.getPiscofinsCstCredito() != 0) {
            pisCofinsDebito = converterCreditoParaDebito(imp.getPiscofinsCstCredito());
            pisCofinsCredito = imp.getPiscofinsCstCredito();
        } else {
            pisCofinsDebito = imp.getPiscofinsCstDebito();
            pisCofinsCredito = imp.getPiscofinsCstCredito();
        }
        PisCofinsVO pDeb = provider.tributo().getPisConfisDebito(pisCofinsDebito);
        PisCofinsVO pCre = provider.tributo().getPisConfisCredito(pisCofinsCredito);

        if (pDeb == null) {
            pDeb = provider.tributo().getPisConfisDebito(converterCreditoParaDebito(pisCofinsCredito));
        }
        if (pCre == null) {
            pCre = provider.tributo().getPisConfisCredito(converterDebitoParaCredito(pisCofinsDebito));
        }

        vo.setPisCofinsDebito(pDeb);
        vo.setPisCofinsCredito(pCre);
        vo.setPisCofinsNaturezaReceita(getNaturezaReceita(vo.getPisCofinsDebito().getCst(), imp.getPiscofinsNaturezaReceita()));

    }

    public NaturezaReceitaVO getNaturezaReceita(int cstDebito, int naturezaReceita) throws Exception {

        NaturezaReceitaVO result = provider.tributo().getNaturezaReceita(cstDebito, naturezaReceita);

        if (result == null) {
            if (cstDebito == 7) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 999);
            } else if (cstDebito == 5) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 409);
            } else if (cstDebito == 4) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 403);
            } else if (cstDebito == 9) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 999);
            } else if (cstDebito == 2) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 403);
            } else if (cstDebito == 3) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 940);
            } else if (cstDebito == 6) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 999);
            } else if (cstDebito == 8) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 999);
            }
        }

        return result;
    }

    /**
     * Transforma os dados de {@link ProdutoIMP} em {@link ProdutoAnteriorVO}
     *
     * @param imp Produto de importação a ser transformado.
     * @return Produto de importação transformado em produto anterior.
     */
    public ProdutoAnteriorVO converterImpEmAnterior(ProdutoIMP imp) {
        ProdutoAnteriorVO destino = new ProdutoAnteriorVO();

        destino.setImportSistema(imp.getImportSistema());
        destino.setImportLoja(imp.getImportLoja());
        destino.setImportId(imp.getImportId());
        destino.setIdConexao(provider.getIdConexao());
        destino.setDescricao(imp.getDescricaoCompleta());
        destino.setPisCofinsCredito(imp.getPiscofinsCstCredito());
        destino.setPisCofinsDebito(imp.getPiscofinsCstDebito());
        destino.setPisCofinsNaturezaReceita(imp.getPiscofinsNaturezaReceita());

        destino.setIcmsCst(imp.getIcmsCst());
        destino.setIcmsAliq(imp.getIcmsAliq());
        destino.setIcmsReducao(imp.getIcmsReducao());

        destino.setIcmsCstSaida(imp.getIcmsCstSaida());
        destino.setIcmsAliqSaida(imp.getIcmsAliqSaida());
        destino.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());

        destino.setIcmsCstSaidaForaEstado(imp.getIcmsCstSaidaForaEstado());
        destino.setIcmsAliqSaidaForaEstado(imp.getIcmsAliqSaidaForaEstado());
        destino.setIcmsReducaoSaidaForaEstado(imp.getIcmsReducaoSaidaForaEstado());

        destino.setIcmsCstSaidaForaEstadoNf(imp.getIcmsCstSaidaForaEstadoNF());
        destino.setIcmsAliqSaidaForaEstadoNf(imp.getIcmsAliqSaidaForaEstadoNF());
        destino.setIcmsReducaoSaidaForaEstadoNf(imp.getIcmsReducaoSaidaForaEstadoNF());

        destino.setIcmsCstConsumidor(imp.getIcmsCstConsumidor());
        destino.setIcmsAliqConsumidor(imp.getIcmsAliqConsumidor());
        destino.setIcmsReducaoConsumidor(imp.getIcmsReducaoConsumidor());

        destino.setIcmsCstEntrada(imp.getIcmsCstEntrada());
        destino.setIcmsAliqEntrada(imp.getIcmsAliqEntrada());
        destino.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());

        destino.setIcmsCstEntradaForaEstado(imp.getIcmsCstEntradaForaEstado());
        destino.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqEntradaForaEstado());
        destino.setIcmsReducaoEntradaForaEstado(imp.getIcmsReducaoEntradaForaEstado());

        destino.setIcmsDebitoId(imp.getIcmsDebitoId());
        destino.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoForaEstadoId());
        destino.setIcmsDebitoForaEstadoIdNf(imp.getIcmsDebitoForaEstadoId());

        destino.setIcmsCreditoId(imp.getIcmsCreditoId());
        destino.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoForaEstadoId());

        destino.setIcmsConsumidorId(imp.getIcmsConsumidorId());

        destino.setEstoque(imp.getEstoque());
        destino.seteBalanca(imp.isBalanca());
        destino.setCustosemimposto(imp.getCustoSemImposto());
        destino.setCustocomimposto(imp.getCustoComImposto());
        destino.setMargem(imp.getMargem());
        destino.setPrecovenda(imp.getPrecovenda());
        destino.setNcm(imp.getNcm());
        destino.setCest(imp.getCest());
        destino.setContadorImportacao(0);
        if (!"".equals(imp.getCodigoSped().trim())) {
            destino.setCodigoSped(imp.getCodigoSped());
        } else {
            destino.setCodigoSped(imp.getImportId());
        }
        destino.setSituacaoCadastro(imp.getSituacaoCadastro());
        destino.setDataCadastro(imp.getDataCadastro());
        return destino;
    }

    /**
     * Converte {@link ProdutoIMP} em {@link ProdutoAutomacaoVO} e inclui no
     * {@link ProdutoVO}.
     *
     * @param imp {@link ProdutoIMP} de origem.
     * @param ean EAN que será gravado.
     * @param unidade
     * @return {@link ProdutoAutomacaoVO} convertido;
     */
    public ProdutoAutomacaoVO converterEAN(ProdutoIMP imp, long ean, TipoEmbalagem unidade) {
        ProdutoAutomacaoVO automacao = new ProdutoAutomacaoVO();
        automacao.setCodigoBarras(ean);
        automacao.setPesoBruto(imp.getPesoBruto());
        automacao.setQtdEmbalagem(imp.getQtdEmbalagem());
        automacao.setTipoEmbalagem(unidade);
        automacao.setDun14(String.valueOf(automacao.getCodigoBarras()).length() > 13);
        return automacao;
    }

    public void notificar() throws Exception {
        provider.next();
    }

    public void setNotify(String descricao, int size) throws Exception {
        provider.setStatus(descricao);
        provider.setMaximum(size);
    }

    public int converterCreditoParaDebito(int piscofinsCstDebito) {
        switch (piscofinsCstDebito) {
            case 50:
                return 1; //"TRIBUTADO"
            case 60:
                return 2; //"TRIB - ALIQ DIFERENCIADA"
            case 51:
                return 3; //"TRIB - ALIQ POR PRODUTO"
            case 70:
                return 4; //"MONOFASICO"
            case 75:
                return 5; //"SUBSTITUIDO"
            case 73:
                return 6; //"TRIB - ALIQUOTA ZERO"
            case 74:
                return 8; //"SEM INCIDENCIA CONTRIBUICAO"
            case 72:
                return 9; //"SUSPENCAO"
            case 99:
                return 49;
            default:
                return 7; //"ISENTO"
        }
    }

    public int converterDebitoParaCredito(int piscofinsCstDebito) {
        switch (piscofinsCstDebito) {
            case 1:
                return 50; //"TRIBUTADO (E)"
            case 2:
                return 60; //"TRIB - ALIQ DIFERENCIADA(E)"
            case 3:
                return 51; //"TRIB - ALIQ POR PRODUTO(E)"
            case 4:
                return 70; //"MONOFASICO (E)"
            case 5:
                return 75; //"SUBSTITUIDO (E)"
            case 6:
                return 73; //"TRIB - ALIQUOTA ZERO(E)"
            case 8:
                return 74; //"SEM INCIDENCIA CONTRIBUICAO(E)"
            case 9:
                return 72; //"SUSPENCAO"
            case 49:
                return 99;
            default:
                return 71; //"ISENTO (E)"
        }
    }

    private ProdutoAutomacaoLojaVO converterProdutoAutomacaoLoja(ProdutoIMP imp) {
        ProdutoAutomacaoLojaVO vo = new ProdutoAutomacaoLojaVO();
        vo.setId_loja(provider.getLojaVR());
        vo.setCodigoBarras(Utils.stringToLong(imp.getEan()));
        vo.setPrecoVenda(imp.getAtacadoPreco());
        return vo;
    }

    private ProdutoAutomacaoDescontoVO converterProdutoAutomacaoDesconto(ProdutoIMP imp) {
        ProdutoAutomacaoDescontoVO vo = new ProdutoAutomacaoDescontoVO();
        vo.setId_loja(provider.getLojaVR());
        vo.setCodigoBarras(Utils.stringToLong(imp.getEan()));
        double desconto = imp.getAtacadoPorcentagem();
        if (desconto == 0 && imp.getAtacadoPreco() > 0 && imp.getAtacadoPreco() != imp.getPrecovenda()) {
            //desconto = MathUtils.round(100 - ((imp.getAtacadoPreco() / (imp.getPrecovenda() == 0 ? 1 : imp.getPrecovenda())) * 100), 2);
            desconto = (100 - ((imp.getAtacadoPreco() / (imp.getPrecovenda() == 0 ? 1 : imp.getPrecovenda())) * 100));
        }
        vo.setDesconto(desconto);
        return vo;
    }

    private AtacadoProdutoComplementoVO converterAtacadoProdutoComplemtnto(ProdutoIMP imp, int id) {
        AtacadoProdutoComplementoVO vo = new AtacadoProdutoComplementoVO();
        vo.setIdLoja(provider.getLojaVR());
        vo.setIdProduto(id);
        vo.setPrecoVenda(imp.getPrecovenda());
        return vo;
    }

    public void salvarOfertas(List<OfertaIMP> ofertas) throws Exception {
        setNotify("Ofertas...Carregando dados iniciais...", 0);
        MultiMap<String, OfertaIMP> filtrados = organizarOfertas(ofertas);
        MultiMap<Comparable, Void> cadastradas = provider.oferta().getCadastradas();
        ofertas.clear();
        System.gc();

        try {
            provider.begin();

            setNotify("Ofertas...Carregando anteriores do produto", 0);

            Map<String, Integer> anteriores = provider.anterior().getAnteriores();

            setNotify("Ofertas...Gravando...", filtrados.size());

            for (OfertaIMP imp : filtrados.values()) {
                System.out.print("0");
                //Produto existente
                Integer codigoAtual = anteriores.get(imp.getIdProduto());
                if (codigoAtual != null) {
                    System.out.print("1");
                    //Oferta não existe
                    if (!cadastradas.containsKey(
                            codigoAtual,
                            imp.getDataInicio(),
                            imp.getDataTermino(),
                            imp.getSituacaoOferta().getId()
                    )) {
                        System.out.print("2");
                        OfertaVO vo = converterOferta(imp);
                        ProdutoVO p = new ProdutoVO();
                        p.setId(codigoAtual);
                        vo.setProduto(p);

                        provider.oferta().gravar(vo);

                        cadastradas.put(
                                null,
                                vo.getProduto().getId(),
                                vo.getDataInicio(),
                                vo.getDataTermino(),
                                vo.getSituacaoOferta()
                        );
                    }
                }
                System.out.println("3");
                notificar();
            }
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    public void converterCest(List<ProdutoIMP> produtos) throws Exception {
        setNotify("Inserindo cests...", 0);

        provider.anterior().createCestInvalido();

        for (ProdutoIMP cest : produtos) {
            ProdutoVO vo = converterIMP(cest, 0, TipoEmbalagem.UN, false);

            if (vo.getCest() == null
                    && cest.getCest() != null
                    && !cest.getCest().equals("")) {

                ProdutoAnteriorVO anterior = new ProdutoAnteriorVO();

                anterior.setCest(cest.getCest());
                anterior.setImportId(cest.getImportId());

                if (cest.getDescricaoCest() != null && !cest.getDescricaoCest().isEmpty()) {
                    anterior.setDescricao(cest.getDescricaoCest());
                }

                provider.anterior().salvarCestInvalido(anterior);
            }
        }
    }

    public MultiMap<String, OfertaIMP> organizarOfertas(List<OfertaIMP> ofertas) throws Exception {
        MultiMap<String, OfertaIMP> result = new MultiMap<>();

        for (OfertaIMP imp : ofertas) {
            result.put(
                    imp,
                    imp.getIdProduto(),
                    DATA_FORMAT.format(imp.getDataInicio()),
                    DATA_FORMAT.format(imp.getDataTermino()),
                    String.valueOf(imp.getSituacaoOferta().getId())
            );
        }

        return result;
    }

    private OfertaVO converterOferta(OfertaIMP imp) {
        OfertaVO vo = new OfertaVO();

        vo.setDataInicio(imp.getDataInicio());
        vo.setDataTermino(imp.getDataTermino());
        vo.setIdLoja(provider.getLojaVR());
        vo.setPrecoOferta(imp.getPrecoOferta());
        vo.setPrecoNormal(imp.getPrecoNormal());
        vo.setSituacaoOferta(imp.getSituacaoOferta());
        vo.setTipoOferta(imp.getTipoOferta());

        return vo;
    }

    private Map<String, Integer> pautaExcecao;

    public int obterPautaFiscal(String pautaFiscalId) throws Exception {
        if (pautaExcecao == null) {
            pautaExcecao = provider.getPautaExcecao();
        }
        if (pautaFiscalId != null) {
            Integer excecao = pautaExcecao.get(pautaFiscalId);
            if (excecao != null) {
                return excecao;
            }
        }
        return 0;
    }

    public static class SetUpVariaveisTO {

        public long ean;
        public String strID;
        public boolean eBalanca;
        public boolean manterEAN;
        public TipoEmbalagem unidade;
    }

    @SuppressWarnings("UnusedAssignment")
    public SetUpVariaveisTO setUpVariaveis(ProdutoIMP imp) {

        SetUpVariaveisTO to = new SetUpVariaveisTO();

        to.ean = Utils.stringToLong(imp.getEan(), -2);
        to.strID = imp.getImportId();
        to.eBalanca = imp.isBalanca();
        to.unidade = TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagem());
        to.manterEAN = imp.isManterEAN();

        //<editor-fold defaultstate="collapsed" desc="Tratando EAN">  
        if (to.eBalanca || to.unidade == TipoEmbalagem.KG) {
            if (to.ean > 999999 && !naoTransformarEANemUN) {
                to.eBalanca = false;
                to.unidade = TipoEmbalagem.UN;
            } else {
                to.eBalanca = true;
            }
        } else if (to.manterEAN) {
            if (!(to.ean >= 1 && to.ean <= 999999)) {
                to.manterEAN = false;
                to.ean = -2;
            }
        } else {
            //Se a ordem for para manter os somente os EANs válidos
            if (!importarMenoresQue7Digitos && to.ean <= 999999) {
                to.ean = -2;
            }
        }
        //</editor-fold>

        return to;
    }

    public String getPrimeiraLojaMigrada() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct (impsistema||' '||imploja) sistema_loja \n"
                    + "from implantacao.codant_produto \n"
                    + "where dataimportacao in (select min(dataimportacao)"
                    + "                         from implantacao.codant_produto)"
            )) {
                if (rst.next()) {
                    return rst.getString("sistema_loja");
                } else {
                    return "";
                }
            }
        }
    }

    public void isDataAlteracaoCodAntProduto() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	*\n"
                    + "from \n"
                    + "	information_schema.columns c\n"
                    + "where\n"
                    + "	table_schema like 'implantacao'\n"
                    + "	and table_name like '%codant_produto%'\n"
                    + "	and column_name like '%dataalteracao%'\n"
                    + "order by\n"
                    + "	1,2,3,4"
            )) {
                if (!rst.next()) {
                    String sql = "alter table implantacao.codant_produto add column dataalteracao timestamp";
                    stm.execute(sql);
                }
            }
        }
    }
}
