package vrimplantacao2.dao.cadastro.usuario;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.usuario.UsuarioVO;
import vrimplantacao2.vo.cadastro.usuario.UsuarioAnteriorVO;
import vrimplantacao2.vo.importacao.UsuarioIMP;
import vrimplantacao2_5.controller.migracao.LogController;
import vrimplantacao2_5.service.migracao.UsuarioService;
import vrimplantacao2_5.vo.enums.EOperacao;

/**
 *
 * @author Wesley
 */
public class UsuarioRepository {

    private static final Logger LOG = Logger.getLogger(UsuarioRepository.class.getName());
    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

    private final UsuarioRepositoryProvider provider;
    private boolean forcarUnificacao = false;

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

                this.provider.setStatus("Usuários - Copiando código anterior dos Usuários...");

                usuarioService.copiarCodantUsuario(this.provider.getSistema(), lojaModelo, this.provider.getLojaOrigem());
            }

            salvar(usuarios);
        }
    }

    public void salvar(List<UsuarioIMP> usuarios) throws Exception {
        MultiMap<String, UsuarioIMP> filtrados = filtrar(usuarios);
        MultiMap<String, UsuarioVO> loginExistentes = provider.getLoginExistentes();
        boolean loginExistente = false;

        usuarios = null;
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
                    vo.setIdLoja(provider.getLojaVR());

                    if (imp.getIdTipoSetor() > 0) {
                        vo.setIdTipoSetor(provider.getTipoSetor(imp.getIdTipoSetor()).getId());
                    }

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
                        gravarUsuario(vo, versao);
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

    public UsuarioVO converter(UsuarioIMP imp) throws Exception {
        UsuarioVO vo = new UsuarioVO();

        vo.setLogin(imp.getLogin());
        vo.setNome(imp.getNome());
        vo.setSenha(imp.getSenha());
        vo.setSituacaoCadastro(imp.getSituacaoCadastro());
        vo.setVerificaAtualizacao(imp.isVerificaAtualizacao());

        return vo;
    }

    public void gravarUsuario(UsuarioVO vo, Versao versao) throws Exception {
        provider.gravarUsuario(vo, versao);
    }

    public UsuarioAnteriorVO converterAnterior(UsuarioIMP imp, String observacaoImportacao) {
        UsuarioAnteriorVO ant = new UsuarioAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(imp.getImportLoja());
        ant.setImportId(imp.getImportId());
        ant.setLogin(imp.getLogin());
        ant.setNome(imp.getNome());
        ant.setIdTipoSetor(imp.getIdTipoSetor());
        ant.setSituacaoCadastro(imp.getSituacaoCadastro());        ant.setObservacaoImportacao(observacaoImportacao);

        return ant;
    }

    public void gravarUsuarioAnterior(UsuarioAnteriorVO anterior) throws Exception {
        provider.gravarUsuarioAnterior(anterior);
    }
}
