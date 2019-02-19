package vrimplantacao2.dao.cadastro.produto2;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.parametro.Versao;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
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
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class ProdutoRepository {

    private static final Logger LOG = Logger.getLogger(ProdutoRepository.class.getName());

    private final ProdutoRepositoryProvider provider;
    private static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private boolean naoTransformarEANemUN = false;
    private boolean usarConversaoDeAliquotaSimples = true;
    
    public ProdutoRepository(ProdutoRepositoryProvider provider) {
        this.provider = provider;
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

    public void begin() throws Exception {
        Conexao.begin();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    public Set<OpcaoProduto> getOpcoes() {
        return provider.getOpcoes();
    }

    public void salvar(List<ProdutoIMP> produtos) throws Exception {
        usarConversaoDeAliquotaSimples = !provider.getOpcoes().contains(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA);
        
        LOG.finest("Abrindo a transação");
        begin();        
        try {
            /**
             * Organizando a listagem de dados antes de efetuar a gravação.
             */
            System.gc();
            MultiMap<String, ProdutoIMP> organizados = new Organizador(this).organizarListagem(produtos);
            produtos.clear();
            System.gc();

            ProdutoIDStack idStack = provider.getIDStack();
            
            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN)) {
                this.naoTransformarEANemUN = true;
            }

            setNotify("Gravando os produtos...", organizados.size());
            for (KeyList<String> keys : organizados.keySet()) {
                StringBuilder rep = new StringBuilder();
                try {
                    ProdutoIMP imp = organizados.get(keys);
                    
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
                    {
                        SetUpVariaveisTO to = setUpVariaveis(imp);
                        ean = to.ean;
                        strID = to.strID;
                        eBalanca = to.eBalanca;
                        unidade = to.unidade;
                    }
                    //</editor-fold>
                    
                    ProdutoAnteriorVO anterior = provider.anterior().get(keys.get(0), keys.get(1), keys.get(2));
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
                            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_RESETAR_BALANCA)) {
                                try {
                                    int idValido = Integer.parseInt(strID);
                                    if (eBalanca || idValido < 10000) {
                                        strID = "-1";
                                    }
                                } catch (NumberFormatException e) {
                                    //Se não for um numero inteiro, não faz nada pois um
                                    //novo id será gerado para ele.
                                }
                            } else if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA) && eBalanca) {
                                strID = String.valueOf(ean);
                            }
                            
                            id = idStack.obterID(strID, eBalanca);
                        }
                        
                        ProdutoVO prod = converterIMP(imp, id, ean, unidade, eBalanca);

                        anterior = converterImpEmAnterior(imp);
                        anterior.setCodigoAtual(prod);
                        ProdutoComplementoVO complemento = converterComplemento(imp);
                        complemento.setProduto(prod);
                        ProdutoAliquotaVO aliquota = converterAliquota(imp);
                        aliquota.setProduto(prod);

                        provider.salvar(prod);
                        provider.anterior().salvar(anterior);
                        provider.complemento().salvar(complemento, false);
                        provider.aliquota().salvar(aliquota);
                    } else if (anterior.getCodigoAtual() != null) {
                        id = anterior.getCodigoAtual().getId();
                        rep.append("01|Produto importado anteriormente (").append("codigoatual:").append(id).append("\n");
                    } else {
                        rep.append("01|Produto sem código atual no VR");
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
                        }
                    }

                    if (!provider.eanAnterior().cadastrado(imp.getImportId(), imp.getEan())) {
                        ProdutoAnteriorEanVO eanAnterior = converterAnteriorEAN(imp);
                        provider.eanAnterior().salvar(eanAnterior);
                    }

                    notificar();
                    LOG.finer("Produto importado: " + rep.toString());
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
            commit();
        } catch (Exception e) {
            rollback();
            LOG.log(Level.SEVERE, "Erro ao importar os produtos", e);
            throw e;
        }
    }
    
    public void atualizar(List<ProdutoIMP> produtos, OpcaoProduto... opcoes) throws Exception {
        usarConversaoDeAliquotaSimples = !provider.getOpcoes().contains(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA);
        
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

        if (!optSimples.isEmpty()) {

            ProgressBar.setStatus("Produtos - Organizando produtos");
            LOG.finer("Lista de produtos antes do Garbage Collector: " + produtos.size());
            System.gc();
            MultiMap<String, ProdutoIMP> organizados = new Organizador(this).organizarListagem(produtos);
            MultiMap<Integer, Void> aliquotas = provider.aliquota().getAliquotas();

            produtos.clear();
            System.gc();

            try {
                Conexao.begin();
                
                LOG.info("Produtos a serem atualizados: " + organizados.size());

                StringBuilder strOpt = new StringBuilder();
                for (Iterator<OpcaoProduto> iterator = optSimples.iterator(); iterator.hasNext();) {
                    OpcaoProduto next = iterator.next();
                    strOpt.append(next.toString()).append(iterator.hasNext() ? ", " : "");
                }

                ProgressBar.setStatus("Produtos - Gravando alterações - " + strOpt);
                ProgressBar.setMaximum(organizados.size());

                if (optSimples.contains(OpcaoProduto.ESTOQUE)) {
                    provider.complemento().criarEstoqueAnteriorTemporario();
                }

                for (KeyList<String> keys : organizados.keySet()) {
                    String[] chave;
                    String[] chaveProd;
                    if (keys.size() == 3) {
                        chave = new String[]{
                            keys.get(0),
                            keys.get(1),
                            keys.get(2)
                        };
                        chaveProd = chave;
                    } else {
                        chave = new String[]{
                            keys.get(0),
                            keys.get(1),
                            keys.get(2),
                            keys.get(3)
                        };
                        chaveProd = new String[]{
                            keys.get(0),
                            keys.get(1),
                            keys.get(2)
                        };
                    }
                    ProdutoIMP imp = organizados.get(chave);

                    ProdutoAnteriorVO anterior = provider.anterior().get(chaveProd);

                    if (anterior != null && anterior.getCodigoAtual() != null) {

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

                        ProdutoVO prod = converterIMP(imp, id, ean, unidade, eBalanca);

                        anterior = converterImpEmAnterior(imp);
                        anterior.setCodigoAtual(prod);

                        ProdutoComplementoVO complemento = converterComplemento(imp);
                        complemento.setProduto(prod);

                        ProdutoAliquotaVO aliquota = converterAliquota(imp);
                        aliquota.setProduto(prod);

                        ProdutoAutomacaoVO automacao = converterEAN(imp, ean, unidade);
                        automacao.setProduto(prod);

                        ProdutoAutomacaoLojaVO precoAtacadoLoja = converterProdutoAutomacaoLoja(imp);
                        ProdutoAutomacaoDescontoVO precoAtacadoDesconto = converterProdutoAutomacaoDesconto(imp);
                        precoAtacadoDesconto.setProduto(prod);

                        provider.atualizar(prod, optSimples);
                        provider.complemento().atualizar(complemento, optSimples);
                        provider.automacao().atualizar(automacao, optSimples);

                        if (aliquotas.containsKey(prod.getId(), aliquota.getEstado().getId())) {
                            provider.aliquota().atualizar(aliquota, optSimples);
                        } else {
                            provider.aliquota().salvar(aliquota);
                            aliquotas.put(null, prod.getId(), aliquota.getEstado().getId());
                        }
                        
                        if (Versao.menorQue(3, 18, 1)) {
                            if (precoAtacadoLoja.getPrecoVenda() > 0 && precoAtacadoLoja.getPrecoVenda() != complemento.getPrecoVenda()) {
                                provider.atacado().atualizarLoja(precoAtacadoLoja, optSimples);
                            }
                        }
                        
                        if (precoAtacadoDesconto.getDesconto() > 0) {
                            provider.atacado().atualizarDesconto(precoAtacadoDesconto, optSimples);
                        }
                    }
                    ProgressBar.next();
                }

                if (optSimples.contains(OpcaoProduto.ESTOQUE)) {
                    provider.complemento().gerarLogDeImportacaoDeEstoque();
                }

                Conexao.commit();
            } catch (Exception e) {
                Conexao.rollback();
                throw e;
            }
        }

        //Executa as opções que possuem lista, transformando-as em "Opcoes Simples".
        for (OpcaoProduto opt : optComLista) {
            List<ProdutoIMP> listaEspecial = opt.getListaEspecial();
            opt.setListaEspecial(null);
            atualizar(listaEspecial, opt);
        }
    }

    /**
     * Unifica uma listagem de produtos no sistema.
     *
     * @param produtos Listagem de {@link ProdutoIMP} a ser unificada.
     * @throws Exception
     */
    public void unificar(List<ProdutoIMP> produtos) throws Exception {
        usarConversaoDeAliquotaSimples = !provider.getOpcoes().contains(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA);
        
        begin();
        try {
            System.gc();
            MultiMap<String, ProdutoIMP> organizados = new Organizador(this).organizarListagem(produtos);
            produtos.clear();
            System.gc();

            ProdutoIDStack idStack = provider.getIDStack();

            boolean unificarProdutoBalanca = provider.getOpcoes().contains(OpcaoProduto.UNIFICAR_PRODUTO_BALANCA);
            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN)) {
                this.naoTransformarEANemUN = true;
            }

            setNotify("Gravando os produtos...", organizados.size());
            for (KeyList<String> keys : organizados.keySet()) {
                ProdutoIMP imp = organizados.get(keys);

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
                boolean eanValido = unificarProdutoBalanca || (ean > 999999);
                int idProdutoExistente = provider.automacao().getIdProdutoPorEAN(ean);
                boolean eanExistente = idProdutoExistente > 0;
                ProdutoVO codigoAtual = null;

                if (eanValido) {
                    if (eanExistente) {
                        /**
                         * Se o produto já existir, apenas atualiza o produto
                         * complemento (preço, custo e estoque)
                         */
                        id = idProdutoExistente;
                        ProdutoComplementoVO complemento = converterComplemento(imp);
                        codigoAtual = new ProdutoVO();
                        codigoAtual.setId(id);
                        complemento.setProduto(codigoAtual);

                        provider.complemento().atualizar(complemento, new HashSet<>(
                                Arrays.asList(OpcaoProduto.CUSTO, OpcaoProduto.PRECO, OpcaoProduto.ESTOQUE)
                        ));
                    } else {
                        /**
                         * Mesmo que um determinado EAN não esteja cadastrado no
                         * sistema (pois o mesmo pode ter sido excluído por um
                         * usuário), é prudente verificar o código anterior para
                         * determinar se este produto foi importado
                         * anteriormente e gravar o EAN no produto correto e
                         * evitar duplicação.
                         */
                        //Se o produto não foi importado, um novo produto é criado.
                        if (!provider.anterior().cadastrado(keys.get(2))) {
                            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA) && eBalanca) {
                                strID = String.valueOf(ean);
                            }

                            id = idStack.obterID(strID, eBalanca);

                            codigoAtual = converterIMP(imp, id, ean, unidade, eBalanca);

                            ProdutoComplementoVO complemento = converterComplemento(imp);
                            complemento.setProduto(codigoAtual);
                            ProdutoAliquotaVO aliquota = converterAliquota(imp);
                            aliquota.setProduto(codigoAtual);

                            provider.salvar(codigoAtual);
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
                                    keys.get(2)
                            ).getCodigoAtual();
                        }
                        /**
                         * Cadastra o EAN no sistema.
                         */
                        if (codigoAtual != null) {
                            ProdutoAutomacaoVO automacao = converterEAN(imp, ean, unidade);
                            automacao.setProduto(codigoAtual);
                            provider.automacao().salvar(automacao);
                        }
                    }
                }

                /**
                 * Independentemente se o produto foi gravado ou não, o código
                 * anterior deve ser registrado.
                 */
                if (!provider.anterior().cadastrado(keys.get(2))) {
                    ProdutoAnteriorVO anterior = converterImpEmAnterior(imp);
                    anterior.setCodigoAtual(codigoAtual);
                    provider.anterior().salvar(anterior);
                }
                if (!provider.eanAnterior().cadastrado(imp.getImportId(), imp.getEan())) {
                    ProdutoAnteriorEanVO eanAnterior = converterAnteriorEAN(imp);
                    provider.eanAnterior().salvar(eanAnterior);
                }

                notificar();
            }
            commit();
        } catch (Exception e) {
            rollback();
            throw e;
        }
    }

    /**
     * Unifica uma listagem de produtos no sistema.
     *
     * @param produtos Listagem de {@link ProdutoIMP} a ser unificada.
     * @throws Exception
     */
    public void unificar2(List<ProdutoIMP> produtos) throws Exception {
        usarConversaoDeAliquotaSimples = !provider.getOpcoes().contains(OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA);
        
        begin();
        try {
            System.gc();
            MultiMap<String, ProdutoIMP> organizados = new Organizador(this).organizarListagem(produtos);
            produtos.clear();
            System.gc();

            ProdutoIDStack idStack = provider.getIDStack();

            boolean unificarProdutoBalanca = provider.getOpcoes().contains(OpcaoProduto.UNIFICAR_PRODUTO_BALANCA);
            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN)) {
                this.naoTransformarEANemUN = true;
            }

            setNotify("Gravando os produtos 2...", organizados.size());
            for (KeyList<String> keys : organizados.keySet()) {
                ProdutoIMP imp = organizados.get(keys);

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
                boolean eanValido = unificarProdutoBalanca || (ean > 999999);
                int idProdutoExistente = provider.automacao().getIdProdutoPorEAN(ean);
                boolean eanExistente = idProdutoExistente > 0;
                ProdutoVO codigoAtual = null;

                if (eanValido) {
                    if (eanExistente) {
                        /**
                         * Se o produto já existir, apenas atualiza o produto
                         * complemento (preço, custo e estoque)
                         */
                        id = idProdutoExistente;
                        ProdutoComplementoVO complemento = converterComplemento(imp);
                        codigoAtual = new ProdutoVO();
                        codigoAtual.setId(id);
                        complemento.setProduto(codigoAtual);

                        provider.complemento().atualizar(complemento, new HashSet<>(
                                Arrays.asList(OpcaoProduto.CUSTO, OpcaoProduto.PRECO, OpcaoProduto.ESTOQUE)
                        ));
                    } else {
                        /**
                         * Mesmo que um determinado EAN não esteja cadastrado no
                         * sistema (pois o mesmo pode ter sido excluído por um
                         * usuário), é prudente verificar o código anterior para
                         * determinar se este produto foi importado
                         * anteriormente e gravar o EAN no produto correto e
                         * evitar duplicação.
                         */
                        //Se o produto não foi importado, um novo produto é criado.
                        if (!provider.anterior().cadastrado(keys.get(2))) {
                            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_MANTER_BALANCA) && eBalanca) {
                                strID = String.valueOf(ean);
                            }

                            id = idStack.obterID(strID, eBalanca);

                            codigoAtual = converterIMP(imp, id, ean, unidade, eBalanca);

                            ProdutoComplementoVO complemento = converterComplemento(imp);
                            complemento.setProduto(codigoAtual);
                            ProdutoAliquotaVO aliquota = converterAliquota(imp);
                            aliquota.setProduto(codigoAtual);

                            provider.salvar(codigoAtual);
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
                                    keys.get(2)
                            ).getCodigoAtual();
                            
                            if ((!eBalanca) && (codigoAtual == null)) {
                                strID = String.valueOf(ean);
                                id = idStack.obterID(strID, eBalanca);

                                codigoAtual = converterIMP(imp, id, ean, unidade, eBalanca);

                                ProdutoComplementoVO complemento = converterComplemento(imp);
                                complemento.setProduto(codigoAtual);
                                ProdutoAliquotaVO aliquota = converterAliquota(imp);
                                aliquota.setProduto(codigoAtual);

                                provider.salvar(codigoAtual);
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
                            }
                        }
                        /**
                         * Cadastra o EAN no sistema.
                         */
                        if (codigoAtual != null) {
                            ProdutoAutomacaoVO automacao = converterEAN(imp, ean, unidade);
                            automacao.setProduto(codigoAtual);
                            provider.automacao().salvar(automacao);
                        }
                    }
                }

                /**
                 * Independentemente se o produto foi gravado ou não, o código
                 * anterior deve ser registrado.
                 */
                if (!provider.anterior().cadastrado(keys.get(2))) {
                    ProdutoAnteriorVO anterior = converterImpEmAnterior(imp);
                    anterior.setCodigoAtual(codigoAtual);
                    provider.anterior().salvar(anterior);
                } else {
                    ProdutoAnteriorVO anterior = converterImpEmAnterior(imp);
                    anterior.setCodigoAtual(codigoAtual);
                    
                    if (!eBalanca) {
                        provider.anterior().alterar(anterior);
                    }
                }
                if (!provider.eanAnterior().cadastrado(imp.getImportId(), imp.getEan())) {
                    ProdutoAnteriorEanVO eanAnterior = converterAnteriorEAN(imp);
                    provider.eanAnterior().salvar(eanAnterior);
                }

                notificar();
            }
            commit();
        } catch (Exception e) {
            rollback();
            throw e;
        }
    }
    
    public ProdutoAliquotaVO converterAliquota(ProdutoIMP imp) throws Exception {
        if (usarConversaoDeAliquotaSimples) {
            return converterAliquotaSimples(imp);
        } else {
            return converterAliquotaCompleta(imp);
        }
    }
    
    /**
     * Converte um {@link ProdutoIMP} em {@link ProdutoAliquotaVO}.
     *
     * @param imp
     * @return
     * @throws Exception
     */
    public ProdutoAliquotaVO converterAliquotaSimples(ProdutoIMP imp) throws Exception {
        ProdutoAliquotaVO aliquota = new ProdutoAliquotaVO();
        aliquota.setEstado(provider.tributo().getUf(getLojaVR()));

        Icms aliqCredito;
        Icms aliqDebito;
        Icms debitoForaEstado;
        Icms creditoForaEstado;
        Icms debitoForaEstadoNfe;
        Icms consumidor;

        String idIcmsDebito = imp.getIcmsDebitoId();
        String idIcmsCredito = imp.getIcmsCreditoId();
        String idIcmsCreditoFornecedor = imp.getIcmsCreditoId();

        if (idIcmsDebito != null) {

            aliqDebito = provider.tributo().getAliquotaByMapaId(idIcmsDebito);
            debitoForaEstado = provider.tributo().getAliquotaByMapaId(idIcmsDebito);
            debitoForaEstadoNfe = provider.tributo().getAliquotaByMapaId(idIcmsDebito);

            int icmsCstSaida = aliqDebito.getCst();
            double icmsAliqSaida = aliqDebito.getAliquota();
            double icmsReducaoSaida = aliqDebito.getReduzido();

            if (icmsCstSaida == 20) {
                double aliq = MathUtils.round(icmsAliqSaida - (icmsAliqSaida * (icmsReducaoSaida / 100)), 0);
                consumidor = provider.tributo().getIcms(0, aliq, 0);
            } else {
                consumidor = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, 0);
            }
        } else {
            int icmsCstSaida = imp.getIcmsCstSaida();            
            double icmsAliqSaida = 0;
            double icmsReducaoSaida = 0;

            if (icmsCstSaida == 20 || icmsCstSaida == 0) {
                icmsAliqSaida = imp.getIcmsAliqSaida();
                icmsReducaoSaida = imp.getIcmsReducaoSaida();
            }
            
            aliqDebito = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, icmsReducaoSaida);
            debitoForaEstado = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, icmsReducaoSaida);
            debitoForaEstadoNfe = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, icmsReducaoSaida);

            if (icmsCstSaida == 20) {
                double aliq = MathUtils.round(icmsAliqSaida - (icmsAliqSaida * (icmsReducaoSaida / 100)), 1);
                consumidor = provider.tributo().getIcms(0, aliq, 0);
            } else {
                consumidor = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, 0);
            }
        }
        
        
        if (idIcmsCredito != null) {
            aliqCredito = provider.tributo().getAliquotaByMapaId(idIcmsCredito);
            creditoForaEstado = provider.tributo().getAliquotaByMapaId(idIcmsCredito);            
        } else {
            int icmsCstEntrada = imp.getIcmsCstEntrada();
            double icmsAliqEntrada = 0;
            double icmsReducaoEntrada = 0;

            if (icmsCstEntrada == 20 || icmsCstEntrada == 0) {
                icmsAliqEntrada = imp.getIcmsAliqEntrada();
                icmsReducaoEntrada = imp.getIcmsReducaoEntrada();
            }
            
            aliqCredito = provider.tributo().getIcms(icmsCstEntrada, icmsAliqEntrada, icmsReducaoEntrada);
            creditoForaEstado = provider.tributo().getIcms(icmsCstEntrada, icmsAliqEntrada, icmsReducaoEntrada);
        }

        aliquota.setAliquotaCredito(aliqCredito);
        aliquota.setAliquotaDebito(aliqDebito);
        aliquota.setAliquotaDebitoForaEstado(debitoForaEstado);
        aliquota.setAliquotaCreditoForaEstado(creditoForaEstado);
        aliquota.setAliquotaDebitoForaEstadoNf(debitoForaEstadoNfe);
        aliquota.setAliquotaConsumidor(consumidor);
        
        if(idIcmsCreditoFornecedor != null) {
            aliquota.setAliquotaCreditoFornecedor(idIcmsCreditoFornecedor);
        }
        
        return aliquota;
    }

    /**
     * Converte um {@link ProdutoIMP} em {@link ProdutoAliquotaVO}.
     *
     * @param imp
     * @return
     * @throws Exception
     */
    public ProdutoAliquotaVO converterAliquotaCompleta(ProdutoIMP imp) throws Exception {
        ProdutoAliquotaVO aliquota = new ProdutoAliquotaVO();
        aliquota.setEstado(provider.tributo().getUf(getLojaVR()));

        Icms aliqCredito;
        Icms aliqDebito;
        Icms debitoForaEstado;
        Icms creditoForaEstado;
        Icms debitoForaEstadoNfe;
        Icms consumidor;

        String idIcmsDebito = imp.getIcmsDebitoId();
        String idIcmsCredito = imp.getIcmsCreditoId();
        String idIcmsCreditoFornecedor = imp.getIcmsCreditoId();

        if (idIcmsDebito != null) {

            aliqDebito = provider.tributo().getAliquotaByMapaId(idIcmsDebito);
            debitoForaEstado = provider.tributo().getAliquotaByMapaId(idIcmsDebito);
            debitoForaEstadoNfe = provider.tributo().getAliquotaByMapaId(idIcmsDebito);

            int icmsCstSaida = aliqDebito.getCst();
            double icmsAliqSaida = aliqDebito.getAliquota();
            double icmsReducaoSaida = aliqDebito.getReduzido();

            if (icmsCstSaida == 20) {
                double aliq = MathUtils.round(icmsAliqSaida - (icmsAliqSaida * (icmsReducaoSaida / 100)), 0);
                consumidor = provider.tributo().getIcms(0, aliq, 0);
            } else {
                consumidor = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, 0);
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

            
            aliqDebito = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, icmsReducaoSaida);
            debitoForaEstado = provider.tributo().getIcms(icmsCstSaidaForaEstado, icmsAliqSaidaForaEstado, icmsReducaoSaidaForaEstado);
            debitoForaEstadoNfe = provider.tributo().getIcms(icmsCstSaidaForaEstadoNF, icmsAliqSaidaForaEstadoNF, icmsReducaoSaidaForaEstadoNF);

            if (icmsCstSaida == 20) {
                double aliq = MathUtils.round(icmsAliqSaida - (icmsAliqSaida * (icmsReducaoSaida / 100)), 1);
                consumidor = provider.tributo().getIcms(0, aliq, 0);
            } else {
                consumidor = provider.tributo().getIcms(icmsCstSaida, icmsAliqSaida, 0);
            }
        }
        
        
        if (idIcmsCredito != null) {
            aliqCredito = provider.tributo().getAliquotaByMapaId(idIcmsCredito);
            creditoForaEstado = provider.tributo().getAliquotaByMapaId(idIcmsCredito);            
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
        }

        aliquota.setAliquotaCredito(aliqCredito);
        aliquota.setAliquotaDebito(aliqDebito);
        aliquota.setAliquotaDebitoForaEstado(debitoForaEstado);
        aliquota.setAliquotaCreditoForaEstado(creditoForaEstado);
        aliquota.setAliquotaDebitoForaEstadoNf(debitoForaEstadoNfe);
        aliquota.setAliquotaConsumidor(consumidor);
        
        if(idIcmsCreditoFornecedor != null) {
            aliquota.setAliquotaCreditoFornecedor(idIcmsCreditoFornecedor);
        }
        
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

        complemento.setIdLoja(getLojaVR());
        complemento.setEstoqueMinimo(imp.getEstoqueMinimo());
        complemento.setEstoqueMaximo(imp.getEstoqueMaximo());
        complemento.setEstoque(imp.getEstoque());
        complemento.setPrecoDiaSeguinte(imp.getPrecovenda());
        complemento.setPrecoVenda(imp.getPrecovenda());
        complemento.setCustoSemImposto(imp.getCustoSemImposto());
        complemento.setCustoComImposto(imp.getCustoComImposto());
        complemento.setDescontinuado(imp.isDescontinuado());
        complemento.setSituacaoCadastro(imp.getSituacaoCadastro());
        complemento.setTipoProduto(imp.getTipoProduto());
        complemento.setFabricacaoPropria(imp.isFabricacaoPropria());

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
    public ProdutoVO converterIMP(ProdutoIMP imp, int id, long ean,
            TipoEmbalagem unidade, boolean eBalanca) throws Exception {

        if (fabricantes == null) {
            fabricantes = provider.getFornecedoresImportados();
        }
        if (compradores == null) {
            compradores = provider.getCompradores();
        }

        ProdutoVO vo = new ProdutoVO();

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
        vo.setQtdEmbalagem(imp.getQtdEmbalagemCotacao());
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
        if(vo.getDataAlteracao() == null) {
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

        vo.setTipoEmbalagem(unidade);
        if (eBalanca) {
            if (TipoEmbalagem.UN.equals(unidade)) {
                vo.setPesavel(true);
            } else {
                vo.setPesavel(false);
            }
        } else {
            vo.setPesavel(false);
        }

        vo.setPesoBruto(imp.getPesoBruto());
        vo.setPesoLiquido(imp.getPesoLiquido());

        //<editor-fold defaultstate="collapsed" desc="Conversão do PIS/COFINS">
        convertPisCofins(imp, vo);
        //</editor-fold>

        vo.setValidade(imp.getValidade());
        vo.setExcecao(obterPautaFiscal(imp.getPautaFiscalId()));
        vo.setVendaPdv(imp.isVendaPdv());

        /**
         * Busca e se existir, relaciona o produto com o comprador.
         */
        Integer comprador = compradores.get(imp.getIdComprador());
        if (comprador != null) {
            vo.setIdComprador(comprador);
        }

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
        destino.setDescricao(imp.getDescricaoCompleta());
        destino.setPisCofinsCredito(imp.getPiscofinsCstCredito());
        destino.setPisCofinsDebito(imp.getPiscofinsCstDebito());
        destino.setPisCofinsNaturezaReceita(imp.getPiscofinsNaturezaReceita());
        destino.setIcmsCst(imp.getIcmsCst());
        destino.setIcmsAliq(imp.getIcmsAliq());
        destino.setIcmsReducao(imp.getIcmsReducao());
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
        ProgressBar.next();
    }

    public void setNotify(String descricao, int size) throws Exception {
        ProgressBar.setStatus(descricao);
        ProgressBar.setMaximum(size);
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
            desconto = MathUtils.round(100 - ((imp.getAtacadoPreco() / (imp.getPrecovenda() == 0 ? 1 : imp.getPrecovenda())) * 100), 2);
        }
        vo.setDesconto(desconto);
        return vo;
    }

    public void salvarOfertas(List<OfertaIMP> ofertas) throws Exception {
        setNotify("Ofertas...Carregando dados iniciais...", 0);
        MultiMap<String, OfertaIMP> filtrados = organizarOfertas(ofertas);
        MultiMap<Comparable, Void> cadastradas = provider.oferta().getCadastradas();
        ofertas.clear();
        System.gc();

        try {
            begin();
            setNotify("Ofertas...Gravando...", filtrados.size());

            for (OfertaIMP imp : filtrados.values()) {
                System.out.print("0");
                //Produto existente
                ProdutoAnteriorVO anterior = provider.anterior().get(
                        provider.getSistema(),
                        provider.getLoja(),
                        imp.getIdProduto()
                );
                if (anterior != null && anterior.getCodigoAtual() != null) {
                    System.out.print("1");
                    //Oferta não existe
                    if (!cadastradas.containsKey(
                            anterior.getCodigoAtual().getId(),
                            imp.getDataInicio(),
                            imp.getDataTermino(),
                            imp.getSituacaoOferta().getId()
                    )) {
                        System.out.print("2");
                        OfertaVO vo = converterOferta(imp);
                        vo.setProduto(anterior.getCodigoAtual());

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
            commit();
        } catch (Exception e) {
            rollback();
            throw e;
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
        public TipoEmbalagem unidade;
    }

    @SuppressWarnings("UnusedAssignment")
    public SetUpVariaveisTO setUpVariaveis(ProdutoIMP imp) {

        SetUpVariaveisTO to = new SetUpVariaveisTO();

        to.ean = Utils.stringToLong(imp.getEan(), -2);
        to.strID = imp.getImportId();
        to.eBalanca = imp.isBalanca();
        to.unidade = TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagem());

        //<editor-fold defaultstate="collapsed" desc="Tratando EAN">  
        if (to.eBalanca || to.unidade == TipoEmbalagem.KG) {
            if (to.ean > 999999 && !naoTransformarEANemUN) {
                to.eBalanca = false;
                to.unidade = TipoEmbalagem.UN;
            } else {
                to.eBalanca = true;
            }
        } else {
            if (to.ean <= 999999) {
                to.ean = -2;
            }
        }
        //</editor-fold>

        return to;
    }
}
