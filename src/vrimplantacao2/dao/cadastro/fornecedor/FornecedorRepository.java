package vrimplantacao2.dao.cadastro.fornecedor;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto2.DivisaoDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorContatoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorPagamentoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.vo.cadastro.fornecedor.ProdutoFornecedorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorDivisaoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.FornecedorPagamentoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2_5.service.migracao.FornecedorService;
import vrimplantacao2_5.controller.migracao.LogController;
import vrimplantacao2_5.vo.enums.EOperacao;

/**
 *
 * @author Leandro
 */
public class FornecedorRepository {

    private static final Logger LOG = Logger.getLogger(FornecedorRepository.class.getName());

    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

    private FornecedorRepositoryProvider provider;
    private MultiMap<String, Integer> contatos;
    private boolean forcarUnificacao = false;

    private final LogController logController;

    public FornecedorRepository(FornecedorRepositoryProvider provider) {
        this.provider = provider;
        this.logController = new LogController();
    }

    public String getLoja() {
        return provider.getLojaOrigem();
    }

    public void salvar2_5(List<FornecedorIMP> fornecedores) throws Exception {
        FornecedorService fornecedorService = new FornecedorService();
        this.forcarUnificacao = provider.getOpcoes().contains(OpcaoFornecedor.FORCAR_UNIFICACAO);

        int idConexao = fornecedorService.existeConexaoMigrada(this.provider.getIdConexao(), this.provider.getSistema()),
                registro = fornecedorService.verificaRegistro();

        String impSistema = fornecedorService.getImpSistemaInicial();

        if (this.forcarUnificacao) {
            unificar(fornecedores);
        } else {

            if (registro > 0 && idConexao == 0
                    || (!impSistema.isEmpty()
                    && !impSistema.equals(this.provider.getSistema()))) {
                unificar(fornecedores);
            } else {
                boolean existeConexao = fornecedorService.
                        verificaMigracaoMultiloja(this.provider.getLojaOrigem(),
                                this.provider.getSistema(),
                                this.provider.getIdConexao());

                String lojaModelo = fornecedorService.getLojaModelo(this.provider.getIdConexao(), this.provider.getSistema());

                if (registro > 0 && existeConexao && !getLoja().equals(lojaModelo)) {

                    this.provider.setStatus("Fornecedor - Copiando código anterior Fornecedor...");

                    fornecedorService.copiarCodantFornecedor(this.provider.getSistema(), lojaModelo, this.provider.getLojaOrigem());
                }

                salvar(fornecedores);
            }
        }
    }

    public void salvar(List<FornecedorIMP> fornecedores) throws Exception {
        MultiMap<String, FornecedorIMP> filtrados = filtrar(fornecedores);
        fornecedores = null;
        //Map<String, Map.Entry<String, Integer>> divisoes = new DivisaoDAO().getAnteriores(provider.getSistema(), provider.getLojaOrigem());
        System.gc();
        organizar(filtrados);

        try {
            provider.begin();

            MultiMap<String, FornecedorAnteriorVO> anteriores = provider.getAnteriores();
            Map<Long, FornecedorVO> cnpjExistentes = provider.getCnpjExistentes();
            FornecedorIDStack ids = provider.getIdsExistentes();
            this.contatos = provider.getContatos();
            MultiMap<String, Void> pagamentos = provider.getPagamentos();
            MultiMap<String, Void> divisoes = provider.getDivisoes();
            HashSet opt = new HashSet(Arrays.asList(new OpcaoFornecedor[]{OpcaoFornecedor.CONTATOS}));

            provider.setStatus("Fornecedores - Gravando...");
            provider.setMaximum(filtrados.size());

            for (FornecedorIMP imp : filtrados.values()) {
                FornecedorAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getImportId()
                );

                FornecedorVO vo;

                if (anterior == null) {

                    vo = converter(imp);

                    //Se existir Familia Fornecedor
                    if (imp.getIdFamiliaFornecedor() != null) {
                        vo.setFamiliaFornecedor(provider.getFamiliaFornecedor(imp.getIdFamiliaFornecedor()));
                    }

                    //Se o CNPJ/CPF existir, gera um novo.
                    if (cnpjExistentes.containsKey(Utils.stringToLong(imp.getCnpj_cpf()))) {
                        vo.setCnpj(-2);
                    }

                    int id = ids.obterID(imp.getImportId());

                    //Obtem um ID válido.
                    if (vo.getCnpj() < 0) {
                        vo.setCnpj(id);
                    }

                    vo.setId(id);
                    gravarFornecedor(vo);
                    cnpjExistentes.put(vo.getCnpj(), vo);

                    anterior = converterAnterior(imp);
                    anterior.setCodigoAtual(vo);
                    anterior.setIdConexao(provider.getIdConexao());
                    gravarFornecedorAnterior(anterior);
                    anteriores.put(
                            anterior,
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getImportId()
                    );
                } else {
                    vo = anterior.getCodigoAtual();
                }

                if (vo != null) {
                    processarContatos(imp, vo, opt);

                    for (Integer condicao : imp.getCondicoesPagamentos()) {
                        provider.gravarCondicaoPagamento(vo.getId(), condicao);
                    }

                    if (imp.getPrazoEntrega() > 0 || imp.getPrazoSeguranca() > 0 || imp.getPrazoVisita() > 0) {

                        processarDivisoes(imp, vo, divisoes);
                    }

                    if (imp.getPrazoPedido() > 0) {
                        provider.gravarPrazoPedidoFornecedor(vo.getId(), imp.getPrazoPedido());
                    }
                }

                provider.next();
            }

            if (versao.igualOuMaiorQue(4, 1, 39)) {
                provider.gravarFornecedorEndereco();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //Executa log de operação
            logController.executar(EOperacao.SALVAR_FORNECEDOR.getId(),
                    sdf.format(new Date()),
                    provider.getLojaVR());

            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    public void atualizar(List<FornecedorIMP> fornecedores, OpcaoFornecedor... opcoes) throws Exception {
        Set<OpcaoFornecedor> opt = new HashSet<>(Arrays.asList(opcoes));
        MultiMap<String, FornecedorIMP> filtrados = filtrar(fornecedores);
        fornecedores = null;
        System.gc();
        organizar(filtrados);

        try {
            provider.begin();

            MultiMap<String, FornecedorAnteriorVO> anteriores = provider.getAnteriores();
            this.contatos = provider.getContatos();
            MultiMap<String, Void> pagamentos = provider.getPagamentos();
            MultiMap<String, Void> divisoes = provider.getDivisoes();

            provider.setStatus("Fornecedores - Gravando...");
            provider.setMaximum(filtrados.size());

            if (opt.contains(OpcaoFornecedor.CNPJ_CPF)) {
                provider.resetCnpjCpf();
            }

            Map<Long, FornecedorVO> cnpjExistentes = provider.getCnpjExistentes();

            for (FornecedorIMP imp : filtrados.values()) {
                FornecedorAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getImportId()
                );

                if (anterior != null && anterior.getCodigoAtual() != null) {

                    FornecedorVO vo = converter(imp);
                    vo.setId(anterior.getCodigoAtual().getId());
                    long cnpj = Utils.stringToLong(imp.getCnpj_cpf());

                    if (cnpj <= 9999999) {
                        cnpj = anterior.getCodigoAtual().getId();
                    }

                    //Se o CNPJ/CPF existir, gera um novo.
                    if (cnpjExistentes.containsKey(cnpj)) {
                        vo.setCnpj(anterior.getCodigoAtual().getId());
                    }

                    atualizarFornecedor(vo, opt);
                    processarContatos(imp, vo, opt);

                    if (opt.contains(OpcaoFornecedor.CONDICAO_PAGAMENTO2)) {
                        processarPagamentos(imp, vo, pagamentos);
                    }

                    if (opt.contains(OpcaoFornecedor.CONDICAO_PAGAMENTO)) {
                        for (Integer condicao : imp.getCondicoesPagamentos()) {
                            provider.gravarCondicaoPagamento(vo.getId(), condicao);
                        }
                    }

                    if (opt.contains(OpcaoFornecedor.PRAZO_FORNECEDOR)) {
                        if (imp.getPrazoEntrega() > 0 || imp.getPrazoSeguranca() > 0 || imp.getPrazoVisita() > 0) {
                            processarDivisoes(imp, vo, divisoes);
                        }
                    }

                    if (opt.contains(OpcaoFornecedor.PRAZO_PEDIDO_FORNECEDOR)) {
                        if (imp.getPrazoPedido() > 0) {
                            provider.gravarPrazoPedidoFornecedor(vo.getId(), imp.getPrazoPedido());
                        }
                    }
                }
                provider.next();
            }

            if (versao.igualOuMaiorQue(4, 1, 39)) {
                provider.atualizarFornecedorEndereco();
            }

            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    public void atualizarProdFornecedor(List<ProdutoFornecedorIMP> produtoFornecedores, OpcaoProdutoFornecedor... opc) throws Exception {
        Set<OpcaoProdutoFornecedor> opt = new HashSet<>(Arrays.asList(opc));
        MultiMap<String, ProdutoFornecedorIMP> filtrados = filtrarProdFornecedor(produtoFornecedores);
        produtoFornecedores = null;
        System.gc();
        organizarProdutoFornecedor(filtrados);

        try {
            provider.begin();
            provider.setStatus("Produtos Fornecedores - Gravando...");
            provider.setMaximum(filtrados.size());

            for (ProdutoFornecedorIMP imp : filtrados.values()) {

                ProdutoFornecedorVO vo = converterProdutoFornecedor(imp);

                if (opt.contains(OpcaoProdutoFornecedor.IPI)) {
                    atualizarProdutoFornecedor(vo, opt);
                }
                if (opt.contains(OpcaoProdutoFornecedor.QTDEMBALAGEM)) {
                    atualizarProdutoFornecedor(vo, opt);
                }

                provider.next();
            }

            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }

    public void unificar(List<FornecedorIMP> fornecedores) throws Exception {
        MultiMap<String, FornecedorIMP> filtrados = filtrar(fornecedores);
        fornecedores = null;
        Map<String, Map.Entry<String, Integer>> divisoes = new DivisaoDAO().getAnteriores(provider.getSistema(), provider.getLojaOrigem());
        System.gc();
        organizar(filtrados);

        try {
            provider.begin();

            MultiMap<String, FornecedorAnteriorVO> anteriores = provider.getAnteriores();
            Map<Long, FornecedorVO> cnpjExistentes = provider.getCnpjExistentes();
            FornecedorIDStack ids = provider.getIdsExistentes();
            this.contatos = provider.getContatos();
            HashSet opt = new HashSet(Arrays.asList(new OpcaoFornecedor[]{OpcaoFornecedor.CONTATOS}));

            provider.setStatus("Fornecedores - Gravando Unificação...");
            provider.setMaximum(filtrados.size());
            for (FornecedorIMP imp : filtrados.values()) {
                //Localiza as referencias dos fornecedores (anteriores e por cnpj/cpf)
                FornecedorAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getImportId()
                );
                FornecedorVO fornecedorPorCnpj = cnpjExistentes.get(Utils.stringToLong(imp.getCnpj_cpf()));

                if (Utils.stringToLong(imp.getCnpj_cpf()) == 0) {
                    fornecedorPorCnpj = null;
                }

                FornecedorVO vo = null;
                if (anterior == null) {
                    vo = converter(imp);

                    if (fornecedorPorCnpj == null && vo.getCnpj() >= 99999999) {
                        int id = ids.obterID(imp.getImportId());

                        //Obtem um ID válido.
                        if (vo.getCnpj() < 0) {
                            vo.setCnpj(id);
                        }

                        vo.setId(id);
                        gravarFornecedor(vo);
                        cnpjExistentes.put(vo.getCnpj(), vo);
                    } else if (fornecedorPorCnpj != null && vo.getCnpj() >= 99999999) {
                        vo.setId(fornecedorPorCnpj.getId());
                    } else {
                        vo = null;
                    }

                    anterior = converterAnterior(imp);
                    anterior.setCodigoAtual(vo);
                    anterior.setIdConexao(provider.getIdConexao());
                    gravarFornecedorAnterior(anterior);
                    anteriores.put(
                            anterior,
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getImportId()
                    );
                } else {
                    vo = anterior.getCodigoAtual();
                    if (vo == null) {
                        vo = fornecedorPorCnpj;
                    }
                }

                if (vo != null) {
                    processarContatos(imp, vo, opt);

                    for (Integer condicao : imp.getCondicoesPagamentos()) {
                        provider.gravarCondicaoPagamento(vo.getId(), condicao);
                    }

                    if (imp.getPrazoEntrega() > 0 || imp.getPrazoSeguranca() > 0 || imp.getPrazoVisita() > 0) {

                    }

                    if (imp.getPrazoPedido() > 0) {
                        provider.gravarPrazoPedidoFornecedor(vo.getId(), imp.getPrazoPedido());
                    }
                }

                provider.next();
            }

            if (versao.igualOuMaiorQue(4, 1, 39)) {
                provider.gravarFornecedorEndereco();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //Executa log de operação
            logController.executar(EOperacao.UNIFICAR_FORNECEDOR.getId(),
                    sdf.format(new Date()),
                    provider.getLojaVR());

            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    public void processarContatos(FornecedorIMP imp, FornecedorVO vo, Set<OpcaoFornecedor> opt) throws Exception {
        for (FornecedorContatoIMP impCont : imp.getContatos().values()) {
            StringBuilder log = new StringBuilder("|Fornecedor|").append(imp.getImportId()).append("|");
            //Converte o IMP em VO
            FornecedorContatoVO contato = converterContatoFornecedor(impCont);
            contato.setFornecedor(vo);

            //Se houver algum contato cadastrado com essa assinatura,
            //Não executa a rotina
            if (!contatos.containsKey(
                    String.valueOf(contato.getFornecedor().getId()),
                    contato.getNome(),
                    contato.getTelefone(),
                    contato.getCelular(),
                    contato.getEmail()
            )) {
                log.append("contato não existe||");
                if (opt.contains(OpcaoFornecedor.CONTATOS)) {
                    gravarFornecedorContato(contato);
                    contatos.put(
                            contato.getId(),
                            String.valueOf(contato.getFornecedor().getId()),
                            contato.getNome(),
                            contato.getTelefone(),
                            contato.getCelular(),
                            contato.getEmail()
                    );
                    log.append("inserido|");
                }
            } else {
                contato.setId(contatos.get(
                        String.valueOf(contato.getFornecedor().getId()),
                        contato.getNome(),
                        contato.getTelefone(),
                        contato.getCelular(),
                        contato.getEmail()
                ));
                log.append("contato existe|").append(contato.getId()).append("|");
                provider.atualizarContato(contato, opt);
                log.append("atualizado|");
            }
            LOG.fine(log.toString());
        }
    }

    public void processarPagamentos(FornecedorIMP imp, FornecedorVO vo, MultiMap<String, Void> pagamentos) throws Exception {
        for (FornecedorPagamentoIMP impPag : imp.getPagamentos().values()) {
            FornecedorPagamentoVO pagamento = converterPagamentoFornecedor(impPag);
            pagamento.setFornecedor(vo);
            //Se houver algum contato cadastrado com essa assinatura,
            //Não executa a rotina
            if (!pagamentos.containsKey(
                    String.valueOf(pagamento.getFornecedor().getId()),
                    String.valueOf(pagamento.getVencimento())
            )) {
                gravarFornecedorPagamento(pagamento);
                pagamentos.put(
                        null,
                        String.valueOf(pagamento.getFornecedor().getId()),
                        String.valueOf(pagamento.getVencimento())
                );
            }
        }
    }

    public void processarDivisoes(FornecedorIMP imp, FornecedorVO vo, MultiMap<String, Void> div) throws Exception {
        Map<String, Map.Entry<String, Integer>> divisoes = new DivisaoDAO().getAnteriores(provider.getSistema(), provider.getLojaOrigem());

        if (imp.getDivisoes().isEmpty()) {
            provider.gravarPrazoFornecedor(
                    vo.getId(),
                    0,
                    7,
                    7,
                    7);
        } else {
            for (FornecedorDivisaoIMP impDiv : imp.getDivisoes()) {

                Map.Entry<String, Integer> divisao = divisoes.get(impDiv.getImportId());
                int idDivisao;
                if (divisao != null) {
                    idDivisao = divisao.getValue();
                } else {
                    idDivisao = 0;
                }

                if (!div.containsKey(
                        String.valueOf(vo.getId()),
                        String.valueOf(idDivisao)
                )) {
                    provider.gravarPrazoFornecedor(vo.getId(), idDivisao, impDiv.getPrazoEntrega(), impDiv.getPrazoVisita(), impDiv.getPrazoSeguranca());
                    div.put(null,
                            String.valueOf(vo.getId()),
                            String.valueOf(idDivisao)
                    );
                }
            }
        }
    }

    public MultiMap<String, FornecedorIMP> filtrar(List<FornecedorIMP> fornecedores) throws Exception {
        MultiMap<String, FornecedorIMP> result = new MultiMap<>();

        for (FornecedorIMP imp : fornecedores) {
            result.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }

        boolean importarSomenteOsAtivos = provider.getOpcoes().contains(OpcaoFornecedor.IMPORTAR_SOMENTE_ATIVOS);
        if (importarSomenteOsAtivos) {
            MultiMap<String, FornecedorIMP> ativos = new MultiMap<>();
            for (FornecedorIMP imp : result.values()) {
                if (imp.isAtivo()) {
                    ativos.put(
                            imp,
                            imp.getImportSistema(),
                            imp.getImportLoja(),
                            imp.getImportId()
                    );
                }
            }
            result = ativos;
        }

        return result;
    }

    public MultiMap<String, ProdutoFornecedorIMP> filtrarProdFornecedor(List<ProdutoFornecedorIMP> produtoFornecedores) throws Exception {
        MultiMap<String, ProdutoFornecedorIMP> result = new MultiMap<>();

        for (ProdutoFornecedorIMP imp : produtoFornecedores) {
            result.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getIdProduto()
            );
        }

        return result;
    }

    public void organizar(MultiMap<String, FornecedorIMP> filtrados) {
        MultiMap<String, FornecedorIMP> idsValidos = new MultiMap<>(3);
        MultiMap<String, FornecedorIMP> idsInvalidos = new MultiMap<>(3);

        for (FornecedorIMP imp : filtrados.values()) {
            String[] chave = new String[]{
                imp.getImportSistema(),
                imp.getImportLoja(),
                imp.getImportId()
            };
            try {
                int id = Integer.parseInt(imp.getImportId());
                if (id > 1 && id <= 999999) {
                    idsValidos.put(imp, chave);
                } else {
                    idsInvalidos.put(imp, chave);
                }
            } catch (NumberFormatException ex) {
                idsInvalidos.put(imp, chave);
            }
        }

        filtrados.clear();
        for (FornecedorIMP imp : idsValidos.getSortedMap().values()) {
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }
        for (FornecedorIMP imp : idsInvalidos.getSortedMap().values()) {
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }
    }

    public void organizarProdutoFornecedor(MultiMap<String, ProdutoFornecedorIMP> filtrados) {
        MultiMap<String, ProdutoFornecedorIMP> idsValidos = new MultiMap<>(3);
        MultiMap<String, ProdutoFornecedorIMP> idsInvalidos = new MultiMap<>(3);

        for (ProdutoFornecedorIMP imp : filtrados.values()) {
            String[] chave = new String[]{
                imp.getImportSistema(),
                imp.getImportLoja(),
                imp.getIdProduto()
            };
            try {
                int id = Integer.parseInt(imp.getIdProduto());
                if (id > 1 && id <= 999999) {
                    idsValidos.put(imp, chave);
                } else {
                    idsInvalidos.put(imp, chave);
                }
            } catch (NumberFormatException ex) {
                idsInvalidos.put(imp, chave);
            }
        }

        filtrados.clear();
        for (ProdutoFornecedorIMP imp : idsValidos.getSortedMap().values()) {
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getIdProduto()
            );
        }
        for (ProdutoFornecedorIMP imp : idsInvalidos.getSortedMap().values()) {
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getIdProduto()
            );
        }
    }

    public FornecedorVO converter(FornecedorIMP imp) throws Exception {
        FornecedorVO vo = new FornecedorVO();

        vo.setRazaoSocial(imp.getRazao());
        vo.setNomeFantasia(imp.getFantasia());
        vo.setCnpj(Utils.stringToLong(imp.getCnpj_cpf(), -1));
        vo.setInscricaoEstadual(imp.getIe_rg());
        vo.setInscricaoMunicipal(imp.getInsc_municipal());
        vo.setInscricaoSuframa(imp.getSuframa());
        vo.setSituacaoCadastro(imp.isAtivo() ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
        vo.setBloqueado(imp.isBloqueado());
        vo.setTelefone(imp.getTel_principal());
        vo.setPedidoMinimoQtd(imp.getQtd_minima_pedido());
        vo.setPedidoMinimoValor(imp.getValor_minimo_pedido());
        vo.setDataCadastro(imp.getDatacadastro() != null ? imp.getDatacadastro() : new Date());
        vo.setObservacao(imp.getObservacao());
        if (imp.getTipo_inscricao() == TipoInscricao.VAZIO) {
            vo.setTipoInscricao(TipoInscricao.analisarCnpjCpf(vo.getCnpj()));
        } else {
            vo.setTipoInscricao(imp.getTipo_inscricao());
        }
        vo.setTipoFornecedor(imp.getTipoFornecedor());
        vo.setTipoEmpresa(imp.getTipoEmpresa());
        vo.setTipoPagamento(imp.getTipoPagamento());
        vo.setIdBanco(imp.getIdBanco() == 0 ? 804 : imp.getIdBanco());
        vo.setUtilizaNfe(imp.isEmiteNfe());
        vo.setPermiteNfSemPedido(imp.isPermiteNfSemPedido());
        vo.setTipoIndicadorIe(imp.getTipoIndicadorIe());
        vo.setUtilizaiva(imp.getUtilizaiva() == null ? false : !"0".equals(imp.getUtilizaiva().trim()));
        vo.setRevenda(imp.getRevenda());
        vo.setIdPais(imp.getIdPais());

        if (imp.getIdFamiliaFornecedor() != null) {
            vo.setFamiliaFornecedor(provider.getFamiliaFornecedor(imp.getIdFamiliaFornecedor()));
        }

        //<editor-fold defaultstate="collapsed" desc="ENDEREÇO">
        vo.setEndereco(imp.getEndereco());
        vo.setNumero(imp.getNumero());
        vo.setComplemento(imp.getComplemento());
        vo.setBairro(imp.getBairro());
        vo.setMunicipio(provider.getMunicipio(imp.getIbge_municipio()));
        if (vo.getMunicipio() == null) {
            vo.setMunicipio(provider.getMunicipio(imp.getMunicipio(), imp.getUf()));
            if (vo.getMunicipio() == null) {
                vo.setMunicipio(provider.getMunicipioPadrao());
            }
        }
        vo.setEstado(vo.getMunicipio().getEstado());
        vo.setCep(Utils.stringToInt(imp.getCep()));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="ENDEREÇO COBRANÇA">
        vo.setEnderecoCobranca(imp.getCob_endereco());
        vo.setNumeroCobranca(imp.getCob_numero());
        vo.setComplementoCobranca(imp.getCob_complemento());
        vo.setBairroCobranca(imp.getCob_bairro());
        vo.setMunicipioCobranca(provider.getMunicipio(imp.getCob_ibge_municipio()));
        if (vo.getMunicipioCobranca() == null) {
            vo.setMunicipioCobranca(provider.getMunicipio(imp.getCob_municipio(), imp.getCob_uf()));
            if (vo.getMunicipioCobranca() == null) {
                vo.setMunicipioCobranca(provider.getMunicipioPadrao());
            }
        }
        vo.setEstadoCobranca(vo.getMunicipioCobranca().getEstado());
        vo.setCepCobranca(Utils.stringToInt(imp.getCob_cep()));
        //</editor-fold>

        return vo;
    }

    public ProdutoFornecedorVO converterProdutoFornecedor(ProdutoFornecedorIMP imp) {
        ProdutoFornecedorVO vo = new ProdutoFornecedorVO();

        ProdutoVO prodVO = new ProdutoVO();
        prodVO.setId(Integer.parseInt(imp.getIdProduto()));
        vo.setProduto(prodVO);
        FornecedorVO forVO = new FornecedorVO();
        forVO.setId(Integer.parseInt(imp.getIdFornecedor()));
        vo.setFornecedor(forVO);
        vo.setIpi(imp.getIpi());
        vo.setTipoIpi(imp.getTipoIpi());
        vo.setQtdEmbalagem(imp.getQtdEmbalagem());

        return vo;
    }

    public void gravarFornecedor(FornecedorVO vo) throws Exception {
        provider.gravarFornecedor(vo);
    }

    public FornecedorAnteriorVO converterAnterior(FornecedorIMP imp) {
        FornecedorAnteriorVO ant = new FornecedorAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(imp.getImportLoja());
        ant.setImportId(imp.getImportId());
        ant.setRazao(Utils.acertarTexto(imp.getRazao()));
        ant.setFantasia(Utils.acertarTexto(imp.getFantasia()));
        ant.setCnpjCpf(Utils.acertarTexto(imp.getCnpj_cpf()));
        return ant;
    }

    public void gravarFornecedorAnterior(FornecedorAnteriorVO anterior) throws Exception {
        provider.gravarFornecedorAnterior(anterior);
    }

    public FornecedorContatoVO converterContatoFornecedor(FornecedorContatoIMP imp) {
        FornecedorContatoVO contato = new FornecedorContatoVO();
        contato.setNome(imp.getNome());
        contato.setTelefone(imp.getTelefone());
        contato.setCelular(imp.getCelular());
        contato.setEmail(imp.getEmail());
        contato.setTipoContato(imp.getTipoContato());
        return contato;
    }

    public FornecedorPagamentoVO converterPagamentoFornecedor(FornecedorPagamentoIMP imp) throws Exception {
        FornecedorPagamentoVO pagamento = new FornecedorPagamentoVO();
        pagamento.setVencimento(imp.getVencimento());
        return pagamento;
    }

    public void gravarFornecedorContato(FornecedorContatoVO contato) throws Exception {
        provider.gravarFornecedorContato(contato);
    }

    public void gravarFornecedorPagamento(FornecedorPagamentoVO pagamento) throws Exception {
        provider.gravarCondicaoPagamento(pagamento);
    }

    private void atualizarFornecedor(FornecedorVO vo, Set<OpcaoFornecedor> opt) throws Exception {
        provider.atualizarFornecedor(vo, opt);
    }

    public void atualizarProdutoFornecedor(ProdutoFornecedorVO vo, Set<OpcaoProdutoFornecedor> opt) throws Exception {
        provider.atualizarProdutoFornecedor(vo, opt);
    }

}
