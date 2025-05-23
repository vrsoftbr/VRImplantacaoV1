package vrimplantacao2.dao.cadastro.usuario;

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
import vrimplantacao2.vo.cadastro.usuario.UsuarioVO;
import vrimplantacao2.vo.cadastro.fornecedor.ProdutoFornecedorVO;
import vrimplantacao2.vo.cadastro.usuario.UsuarioAnteriorVO;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorDivisaoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.FornecedorPagamentoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.UsuarioIMP;
import vrimplantacao2_5.service.migracao.FornecedorService;
import vrimplantacao2_5.controller.migracao.LogController;
import vrimplantacao2_5.service.migracao.UsuarioService;
import vrimplantacao2_5.vo.enums.EOperacao;

/**
 *
 * @author Leandro
 */
public class UsuarioRepository {

    private static final Logger LOG = Logger.getLogger(UsuarioRepository.class.getName());

//    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());
//
    private UsuarioRepositoryProvider provider;
//    private MultiMap<String, Integer> contatos;
    private boolean forcarUnificacao = false;
//
    private final LogController logController;

    public UsuarioRepository(UsuarioRepositoryProvider provider) {
        this.provider = provider;
        this.logController = new LogController();
    }

    public String getLoja() {
        return provider.getLojaOrigem();
    }

    public void salvar2_5(List<UsuarioIMP> usuarios) throws Exception {
        UsuarioService usuarioService = new UsuarioService();
        this.forcarUnificacao = provider.getOpcoes().contains(OpcaoUsuario.FORCAR_UNIFICACAO);

        int idConexao = usuarioService.existeConexaoMigrada(this.provider.getIdConexao(), this.provider.getSistema()),
                registro = usuarioService.verificaRegistro();

        String impSistema = usuarioService.getImpSistemaInicial();

        if (this.forcarUnificacao) {
//            unificar(fornecedores);
        } else {

            if (registro > 0 && idConexao == 0
                    || (!impSistema.isEmpty()
                    && !impSistema.equals(this.provider.getSistema()))) {

//                FAZER LOGICA DE UNIFICAÇÃO
//                unificar(fornecedores);
            } else {
                boolean existeConexao = usuarioService.
                        verificaMigracaoMultiloja(this.provider.getLojaOrigem(),
                                this.provider.getSistema(),
                                this.provider.getIdConexao());

                String lojaModelo = usuarioService.getLojaModelo(this.provider.getIdConexao(), this.provider.getSistema());

                if (registro > 0 && existeConexao && !getLoja().equals(lojaModelo)) {

                    this.provider.setStatus("Fornecedor - Copiando código anterior Fornecedor...");

                    usuarioService.copiarCodantUsuario(this.provider.getSistema(), lojaModelo, this.provider.getLojaOrigem());
                }

                salvar(usuarios);
            }
        }
    }

    public void salvar(List<UsuarioIMP> usuarios) throws Exception {
        MultiMap<String, UsuarioIMP> filtrados = filtrar(usuarios);
        MultiMap<String, UsuarioVO> loginExistentes = provider.getLoginExistentes();
        boolean loginExistente = false;

        usuarios = null;
//        //Map<String, Map.Entry<String, Integer>> divisoes = new DivisaoDAO().getAnteriores(provider.getSistema(), provider.getLojaOrigem());
//        System.gc();
        organizar(filtrados);

        try {
            provider.begin();

            MultiMap<String, UsuarioAnteriorVO> anteriores = provider.getAnteriores();

            UsuarioIDStack ids = provider.getIdsExistentes();

            provider.setStatus("Usuários - Gravando...");
            provider.setMaximum(filtrados.size());

            for (UsuarioIMP imp : filtrados.values()) {
                UsuarioAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getImportId()
                );

                UsuarioVO vo;

                if (anterior == null) {

                    vo = converter(imp);

                    //Se existir Tipo Setor
                    if (imp.getIdTipoSetor() > 0) {
                        vo.setIdTipoSetor(provider.getTipoSetor(imp.getIdTipoSetor()).getId());
                    }
//
                    int id = ids.obterID(imp.getImportId());

                    vo.setId(id);

                    String observacaoImportacao = "";

                    //Se existir um usuário com o login, não gravar!
                    if (loginExistentes.get(vo.getLogin()) != null) {
                        loginExistente = true;
                        observacaoImportacao = "USUÁRIO NÃO IMPORTADO - LOGIN " + vo.getLogin() + " JÁ EXISTENTE";
                    }

                    anterior = converterAnterior(imp, observacaoImportacao);

                    if (!loginExistente) {
                        gravarUsuario(vo);
                        anterior.setObservacaoImportacao("USUÁRIO INSERIDO COMO NOVO");
                        anterior.setCodigoAtual(vo);
                        loginExistentes.put(vo, vo.getLogin());
                    }

//                    anterior.setIdConexao(provider.getIdConexao());
                    gravarUsuarioAnterior(anterior);
                    anteriores.put(
                            anterior,
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getImportId()
                    );
                } else {
                    vo = anterior.getCodigoAtual();
                }

                loginExistente = false;
                provider.next();
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
//
//    public void atualizar(List<FornecedorIMP> fornecedores, OpcaoFornecedor... opcoes) throws Exception {
//        Set<OpcaoFornecedor> opt = new HashSet<>(Arrays.asList(opcoes));
//        MultiMap<String, FornecedorIMP> filtrados = filtrar(fornecedores);
//        fornecedores = null;
//        System.gc();
//        organizar(filtrados);
//
//        try {
//            provider.begin();
//
//            MultiMap<String, FornecedorAnteriorVO> anteriores = provider.getAnteriores();
//            this.contatos = provider.getContatos();
//            MultiMap<String, Void> pagamentos = provider.getPagamentos();
//            MultiMap<String, Void> divisoes = provider.getDivisoes();
//
//            provider.setStatus("Fornecedores - Gravando...");
//            provider.setMaximum(filtrados.size());
//
//            if (opt.contains(OpcaoFornecedor.CNPJ_CPF)) {
//                provider.resetCnpjCpf();
//            }
//
//            Map<Long, FornecedorVO> cnpjExistentes = provider.getCnpjExistentes();
//
//            for (FornecedorIMP imp : filtrados.values()) {
//                FornecedorAnteriorVO anterior = anteriores.get(
//                        provider.getSistema(),
//                        provider.getLojaOrigem(),
//                        imp.getImportId()
//                );
//
//                if (anterior != null && anterior.getCodigoAtual() != null) {
//
//                    FornecedorVO vo = converter(imp);
//                    vo.setId(anterior.getCodigoAtual().getId());
//                    long cnpj = Utils.stringToLong(imp.getCnpj_cpf());
//
//                    if (cnpj <= 9999999) {
//                        cnpj = anterior.getCodigoAtual().getId();
//                    }
//
//                    //Se o CNPJ/CPF existir, gera um novo.
//                    if (cnpjExistentes.containsKey(cnpj)) {
//                        vo.setCnpj(anterior.getCodigoAtual().getId());
//                    }
//
//                    atualizarFornecedor(vo, opt);
//                    processarContatos(imp, vo, opt);
//
//                    if (opt.contains(OpcaoFornecedor.CONDICAO_PAGAMENTO2)) {
//                        processarPagamentos(imp, vo, pagamentos);
//                    }
//
//                    if (opt.contains(OpcaoFornecedor.CONDICAO_PAGAMENTO)) {
//                        for (Integer condicao : imp.getCondicoesPagamentos()) {
//                            provider.gravarCondicaoPagamento(vo.getId(), condicao);
//                        }
//                    }
//
//                    if (opt.contains(OpcaoFornecedor.PRAZO_FORNECEDOR)) {
//                        if (imp.getPrazoEntrega() > 0 || imp.getPrazoSeguranca() > 0 || imp.getPrazoVisita() > 0) {
//                            processarDivisoes(imp, vo, divisoes);
//                        }
//                    }
//
//                    if (opt.contains(OpcaoFornecedor.PRAZO_PEDIDO_FORNECEDOR)) {
//                        if (imp.getPrazoPedido() > 0) {
//                            provider.gravarPrazoPedidoFornecedor(vo.getId(), imp.getPrazoPedido());
//                        }
//                    }
//                }
//                provider.next();
//            }
//            
//            if (versao.igualOuMaiorQue(4, 1, 39)){
//                provider.atualizarFornecedorEndereco();
//            }
//
//            provider.commit();
//        } catch (Exception e) {
//            provider.rollback();
//            throw e;
//        }
//    }
//
//    public void atualizarProdFornecedor(List<ProdutoFornecedorIMP> produtoFornecedores, OpcaoProdutoFornecedor... opc) throws Exception {
//        Set<OpcaoProdutoFornecedor> opt = new HashSet<>(Arrays.asList(opc));
//        MultiMap<String, ProdutoFornecedorIMP> filtrados = filtrarProdFornecedor(produtoFornecedores);
//        produtoFornecedores = null;
//        System.gc();
//        organizarProdutoFornecedor(filtrados);
//
//        try {
//            provider.begin();
//            provider.setStatus("Produtos Fornecedores - Gravando...");
//            provider.setMaximum(filtrados.size());
//
//            for (ProdutoFornecedorIMP imp : filtrados.values()) {
//
//                ProdutoFornecedorVO vo = converterProdutoFornecedor(imp);
//                
//                if(opt.contains(OpcaoProdutoFornecedor.IPI)){
//                    atualizarProdutoFornecedor(vo, opt);
//                }
//                if(opt.contains(OpcaoProdutoFornecedor.QTDEMBALAGEM)){
//                    atualizarProdutoFornecedor(vo, opt);
//                }
//                
//                provider.next();
//            }
//
//            provider.commit();
//        } catch (Exception ex) {
//            provider.rollback();
//            throw ex;
//        }
//    }
//
//    public void unificar(List<FornecedorIMP> fornecedores) throws Exception {
//        MultiMap<String, FornecedorIMP> filtrados = filtrar(fornecedores);
//        fornecedores = null;
//        Map<String, Map.Entry<String, Integer>> divisoes = new DivisaoDAO().getAnteriores(provider.getSistema(), provider.getLojaOrigem());
//        System.gc();
//        organizar(filtrados);
//
//        try {
//            provider.begin();
//
//            MultiMap<String, FornecedorAnteriorVO> anteriores = provider.getAnteriores();
//            Map<Long, FornecedorVO> cnpjExistentes = provider.getCnpjExistentes();
//            FornecedorIDStack ids = provider.getIdsExistentes();
//            this.contatos = provider.getContatos();
//            HashSet opt = new HashSet(Arrays.asList(new OpcaoFornecedor[]{ OpcaoFornecedor.CONTATOS }));
//
//            provider.setStatus("Fornecedores - Gravando Unificação...");
//            provider.setMaximum(filtrados.size());
//            for (FornecedorIMP imp : filtrados.values()) {
//                //Localiza as referencias dos fornecedores (anteriores e por cnpj/cpf)
//                FornecedorAnteriorVO anterior = anteriores.get(
//                        provider.getSistema(),
//                        provider.getLojaOrigem(),
//                        imp.getImportId()
//                );
//                FornecedorVO fornecedorPorCnpj = cnpjExistentes.get(Utils.stringToLong(imp.getCnpj_cpf()));
//                
//                if(Utils.stringToLong(imp.getCnpj_cpf()) == 0) {
//                    fornecedorPorCnpj = null;
//                }
//
//                FornecedorVO vo = null;
//                if (anterior == null) {
//                    vo = converter(imp);
//
//                    if (fornecedorPorCnpj == null && vo.getCnpj() >= 99999999) {
//                        int id = ids.obterID(imp.getImportId());
//
//                        //Obtem um ID válido.
//                        if (vo.getCnpj() < 0) {
//                            vo.setCnpj(id);
//                        }
//
//                        vo.setId(id);
//                        gravarFornecedor(vo);
//                        cnpjExistentes.put(vo.getCnpj(), vo);
//                    } else if (fornecedorPorCnpj != null && vo.getCnpj() >= 99999999) {
//                        vo.setId(fornecedorPorCnpj.getId());
//                    } else {
//                        vo = null;
//                    }
//
//                    anterior = converterAnterior(imp);
//                    anterior.setCodigoAtual(vo);
//                    anterior.setIdConexao(provider.getIdConexao());
//                    gravarFornecedorAnterior(anterior);
//                    anteriores.put(
//                            anterior,
//                            provider.getSistema(),
//                            provider.getLojaOrigem(),
//                            imp.getImportId()
//                    );
//                } else {
//                    vo = anterior.getCodigoAtual();
//                    if (vo == null) {
//                        vo = fornecedorPorCnpj;
//                    }
//                }
//
//                if (vo != null) {
//                    processarContatos(imp, vo, opt);
//
//                    for (Integer condicao : imp.getCondicoesPagamentos()) {
//                        provider.gravarCondicaoPagamento(vo.getId(), condicao);
//                    }
//
//                    if (imp.getPrazoEntrega() > 0 || imp.getPrazoSeguranca() > 0 || imp.getPrazoVisita() > 0) {
//                        
//                    }
//                    
//                    if (imp.getPrazoPedido() > 0) {
//                        provider.gravarPrazoPedidoFornecedor(vo.getId(), imp.getPrazoPedido());
//                    }
//                }
//
//                provider.next();
//            }
//            
//            if (versao.igualOuMaiorQue(4, 1, 39)){
//                provider.gravarFornecedorEndereco();
//            }
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            
//            //Executa log de operação
//            logController.executar(EOperacao.UNIFICAR_FORNECEDOR.getId(),
//                    sdf.format(new Date()),
//                    provider.getLojaVR());
//                    
//            provider.commit();
//        } catch (Exception e) {
//            provider.rollback();
//            throw e;
//        }
//    }
//
//    public void processarContatos(FornecedorIMP imp, FornecedorVO vo, Set<OpcaoFornecedor> opt) throws Exception {
//        for (FornecedorContatoIMP impCont : imp.getContatos().values()) {
//            StringBuilder log = new StringBuilder("|Fornecedor|").append(imp.getImportId()).append("|");
//            //Converte o IMP em VO
//            FornecedorContatoVO contato = converterContatoFornecedor(impCont);
//            contato.setFornecedor(vo);
//            
//            //Se houver algum contato cadastrado com essa assinatura,
//            //Não executa a rotina
//            if (!contatos.containsKey(
//                    String.valueOf(contato.getFornecedor().getId()),
//                    contato.getNome(),
//                    contato.getTelefone(),
//                    contato.getCelular(),
//                    contato.getEmail()
//            )) {
//                log.append("contato não existe||");
//                if (opt.contains(OpcaoFornecedor.CONTATOS)) {
//                    gravarFornecedorContato(contato);
//                    contatos.put(
//                            contato.getId(),
//                            String.valueOf(contato.getFornecedor().getId()),
//                            contato.getNome(),
//                            contato.getTelefone(),
//                            contato.getCelular(),
//                            contato.getEmail()
//                    );
//                    log.append("inserido|");
//                }
//            } else {
//                contato.setId(contatos.get(
//                        String.valueOf(contato.getFornecedor().getId()),
//                        contato.getNome(),
//                        contato.getTelefone(),
//                        contato.getCelular(),
//                        contato.getEmail()
//                ));
//                log.append("contato existe|").append(contato.getId()).append("|");
//                provider.atualizarContato(contato, opt);
//                log.append("atualizado|");
//            }
//            LOG.fine(log.toString());
//        }
//    }
//
//    public void processarPagamentos(FornecedorIMP imp, FornecedorVO vo, MultiMap<String, Void> pagamentos) throws Exception {
//        for (FornecedorPagamentoIMP impPag : imp.getPagamentos().values()) {
//            FornecedorPagamentoVO pagamento = converterPagamentoFornecedor(impPag);
//            pagamento.setFornecedor(vo);
//            //Se houver algum contato cadastrado com essa assinatura,
//            //Não executa a rotina
//            if (!pagamentos.containsKey(
//                    String.valueOf(pagamento.getFornecedor().getId()),
//                    String.valueOf(pagamento.getVencimento())
//            )) {
//                gravarFornecedorPagamento(pagamento);
//                pagamentos.put(
//                        null,
//                        String.valueOf(pagamento.getFornecedor().getId()),
//                        String.valueOf(pagamento.getVencimento())
//                );
//            }
//        }
//    }
//    
//    public void processarDivisoes(FornecedorIMP imp, FornecedorVO vo, MultiMap<String, Void> div) throws Exception {
//        Map<String, Map.Entry<String, Integer>> divisoes = new DivisaoDAO().getAnteriores(provider.getSistema(), provider.getLojaOrigem());
//        
//        if (imp.getDivisoes().isEmpty()) {
//            provider.gravarPrazoFornecedor(
//                    vo.getId(),
//                    0, 
//                    7, 
//                    7, 
//                    7);
//        } else {
//            for (FornecedorDivisaoIMP impDiv : imp.getDivisoes()) {
//
//                Map.Entry<String, Integer> divisao = divisoes.get(impDiv.getImportId());
//                int idDivisao;
//                if (divisao != null) {
//                    idDivisao = divisao.getValue();
//                } else {
//                    idDivisao = 0;
//                }
//
//                if (!div.containsKey(
//                        String.valueOf(vo.getId()),
//                        String.valueOf(idDivisao)                    
//                )) {
//                    provider.gravarPrazoFornecedor(vo.getId(), idDivisao, impDiv.getPrazoEntrega(), impDiv.getPrazoVisita(), impDiv.getPrazoSeguranca());
//                    div.put(null, 
//                        String.valueOf(vo.getId()),
//                        String.valueOf(idDivisao)                    
//                    );
//                }            
//            }
//        }
//    }

    public MultiMap<String, UsuarioIMP> filtrar(List<UsuarioIMP> usuarios) throws Exception {
        MultiMap<String, UsuarioIMP> result = new MultiMap<>();

        for (UsuarioIMP imp : usuarios) {
            result.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }
        return result;
    }

//    public MultiMap<String, ProdutoFornecedorIMP> filtrarProdFornecedor(List<ProdutoFornecedorIMP> produtoFornecedores) throws Exception {
//        MultiMap<String, ProdutoFornecedorIMP> result = new MultiMap<>();
//
//        for (ProdutoFornecedorIMP imp : produtoFornecedores) {
//            result.put(
//                    imp,
//                    imp.getImportSistema(),
//                    imp.getImportLoja(),
//                    imp.getIdProduto()
//            );
//        }
//
//        return result;
//    }
    public void organizar(MultiMap<String, UsuarioIMP> filtrados) {
        MultiMap<String, UsuarioIMP> idsValidos = new MultiMap<>(3);
        MultiMap<String, UsuarioIMP> idsInvalidos = new MultiMap<>(3);

        for (UsuarioIMP imp : filtrados.values()) {
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
        for (UsuarioIMP imp : idsValidos.getSortedMap().values()) {
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }
        for (UsuarioIMP imp : idsInvalidos.getSortedMap().values()) {
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }
    }

//    public void organizarProdutoFornecedor(MultiMap<String, ProdutoFornecedorIMP> filtrados) {
//        MultiMap<String, ProdutoFornecedorIMP> idsValidos = new MultiMap<>(3);
//        MultiMap<String, ProdutoFornecedorIMP> idsInvalidos = new MultiMap<>(3);
//
//        for (ProdutoFornecedorIMP imp : filtrados.values()) {
//            String[] chave = new String[]{
//                imp.getImportSistema(),
//                imp.getImportLoja(),
//                imp.getIdProduto()
//            };
//            try {
//                int id = Integer.parseInt(imp.getIdProduto());
//                if (id > 1 && id <= 999999) {
//                    idsValidos.put(imp, chave);
//                } else {
//                    idsInvalidos.put(imp, chave);
//                }
//            } catch (NumberFormatException ex) {
//                idsInvalidos.put(imp, chave);
//            }
//        }
//
//        filtrados.clear();
//        for (ProdutoFornecedorIMP imp : idsValidos.getSortedMap().values()) {
//            filtrados.put(
//                    imp,
//                    imp.getImportSistema(),
//                    imp.getImportLoja(),
//                    imp.getIdProduto()
//            );
//        }
//        for (ProdutoFornecedorIMP imp : idsInvalidos.getSortedMap().values()) {
//            filtrados.put(
//                    imp,
//                    imp.getImportSistema(),
//                    imp.getImportLoja(),
//                    imp.getIdProduto()
//            );
//        }
//    }
    public UsuarioVO converter(UsuarioIMP imp) throws Exception {
        UsuarioVO vo = new UsuarioVO();

        vo.setLogin(imp.getLogin());
        vo.setNome(imp.getNome());
        vo.setSenha(imp.getSenha());
        vo.setSituacaoCadastro(imp.getSituacaoCadastro());

//        vo.setFamiliaFornecedor(provider.getFamiliaFornecedor(imp.getIdFamiliaFornecedor()));
        return vo;
    }
//
//    public ProdutoFornecedorVO converterProdutoFornecedor(ProdutoFornecedorIMP imp) {
//        ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
//
//        ProdutoVO prodVO = new ProdutoVO();
//        prodVO.setId(Integer.parseInt(imp.getIdProduto()));
//        vo.setProduto(prodVO);
//        FornecedorVO forVO = new FornecedorVO();
//        forVO.setId(Integer.parseInt(imp.getIdFornecedor()));
//        vo.setFornecedor(forVO);
//        vo.setIpi(imp.getIpi());
//        vo.setTipoIpi(imp.getTipoIpi());
//        vo.setQtdEmbalagem(imp.getQtdEmbalagem());
//
//        return vo;
//    }

    public void gravarUsuario(UsuarioVO vo) throws Exception {
        provider.gravarUsuario(vo);
    }

    public UsuarioAnteriorVO converterAnterior(UsuarioIMP imp, String observacaoImportacao) {
        UsuarioAnteriorVO ant = new UsuarioAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(imp.getImportLoja());
        ant.setImportId(imp.getImportId());
        ant.setLogin(imp.getLogin());
        ant.setNome(imp.getNome());
        ant.setIdTipoSetor(imp.getIdTipoSetor());
        ant.setSituacaoCadastro(imp.getSituacaoCadastro());
        ant.setObservacaoImportacao(observacaoImportacao);

        return ant;
    }

    public void gravarUsuarioAnterior(UsuarioAnteriorVO anterior) throws Exception {
        provider.gravarUsuarioAnterior(anterior);
    }

//    public FornecedorContatoVO converterContatoFornecedor(FornecedorContatoIMP imp) {
//        FornecedorContatoVO contato = new FornecedorContatoVO();
//        contato.setNome(imp.getNome());
//        contato.setTelefone(imp.getTelefone());
//        contato.setCelular(imp.getCelular());
//        contato.setEmail(imp.getEmail());
//        contato.setTipoContato(imp.getTipoContato());
//        return contato;
//    }
//
//    public FornecedorPagamentoVO converterPagamentoFornecedor(FornecedorPagamentoIMP imp) throws Exception {
//        FornecedorPagamentoVO pagamento = new FornecedorPagamentoVO();
//        pagamento.setVencimento(imp.getVencimento());
//        return pagamento;
//    }
//
//    public void gravarFornecedorContato(FornecedorContatoVO contato) throws Exception {
//        provider.gravarFornecedorContato(contato);
//    }
//
//    public void gravarFornecedorPagamento(FornecedorPagamentoVO pagamento) throws Exception {
//        provider.gravarCondicaoPagamento(pagamento);
//    }
//
//    private void atualizarFornecedor(FornecedorVO vo, Set<OpcaoFornecedor> opt) throws Exception {
//        provider.atualizarFornecedor(vo, opt);
//    }
//
//    public void atualizarProdutoFornecedor(ProdutoFornecedorVO vo, Set<OpcaoProdutoFornecedor> opt) throws Exception {
//        provider.atualizarProdutoFornecedor(vo, opt);
//    }
}
