package vrimplantacao2.dao.cadastro.cliente;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.venda.PdvVendaDAO;
import vrimplantacao2.dao.cadastro.venda.PdvVendaItemDAO;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialDependenteVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.cliente.food.ClienteFoodAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.food.ClienteFoodVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaItemVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaPromocaoPontuacaoVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.ClienteContatoIMP;
import vrimplantacao2.vo.importacao.ClienteDependenteIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2_5.classe.Global;
import vrimplantacao2_5.controller.migracao.LogController;
import vrimplantacao2_5.vo.enums.EOperacao;
import vrimplantacao2_5.service.migracao.ClienteEventualService;
import vrimplantacao2_5.service.migracao.ClientePreferencialService;

/**
 * Repositório do Cliente para efetuar a importação de Eventuais e
 * Preferenciais.
 *
 * @author Leandro
 */
public class ClienteRepository {

    private static final Logger LOG = Logger.getLogger(ClienteRepository.class.getName());

    private ClienteRepositoryProvider provider;
    private final LogController logController;
    private boolean forcarUnificacao = false;

    public ClienteRepository(ClienteRepositoryProvider provider) throws Exception {
        this.provider = provider;
        this.logController = new LogController();
    }

    public String getLoja() {
        return provider.getLojaOrigem();
    }

    public void salvarClientePreferencial2_5(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {
        ClientePreferencialService clienteService = new ClientePreferencialService();
        this.forcarUnificacao = opt.contains(OpcaoCliente.FORCAR_UNIFICACAO);

        int idConexao = clienteService.existeConexaoMigrada(this.provider.getIdConexao(), this.provider.getSistema()),
                registro = clienteService.verificaRegistro();

        String impSistema = clienteService.getImpSistemaInicial();

        if (this.forcarUnificacao) {
            unificarClientePreferencial(clientes, opt);
        } else {
            if (registro > 0 && idConexao == 0 || (!impSistema.isEmpty() && !impSistema.equals(this.provider.getSistema()))) {
                unificarClientePreferencial(clientes, opt);
            } else {
                boolean existeConexao = clienteService.
                        verificaMigracaoMultiloja(this.provider.getLojaOrigem(), this.provider.getSistema(), this.provider.getIdConexao());

                String lojaModelo = clienteService.getLojaModelo(this.provider.getIdConexao(), this.provider.getSistema());

                if (registro > 0 && existeConexao && !getLoja().equals(lojaModelo)) {

                    this.provider.setNotificacao("Cliente Preferencial - Copiando código anterior Cliente...", clientes.size());
                    clienteService.copiarCodantClientePreferencial(this.provider.getSistema(), lojaModelo, this.provider.getLojaOrigem());
                }

                importarClientePreferencial(clientes, opt);
            }
        }
    }

    public void salvarClienteEventual2_5(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {
        ClienteEventualService clienteService = new ClienteEventualService();
        this.forcarUnificacao = opt.contains(OpcaoCliente.FORCAR_UNIFICACAO);

        int idConexao = clienteService.existeConexaoMigrada(this.provider.getIdConexao(), this.provider.getSistema()),
                registro = clienteService.verificaRegistro();

        String impSistema = clienteService.getImpSistemaInicial();

        if (this.forcarUnificacao) {
            unificarClienteEventual(clientes, opt);
        } else {

            if (registro > 0 && idConexao == 0 || (!impSistema.isEmpty() && !impSistema.equals(this.provider.getSistema()))) {
                unificarClienteEventual(clientes, opt);
            } else {
                boolean existeConexao = clienteService.
                        verificaMigracaoMultiloja(this.provider.getLojaOrigem(), this.provider.getSistema(), this.provider.getIdConexao());

                String lojaModelo = clienteService.getLojaModelo(this.provider.getIdConexao(), this.provider.getSistema());

                if (registro > 0 && existeConexao && !getLoja().equals(lojaModelo)) {

                    this.provider.setNotificacao("Cliente Eventual - Copiando código anterior Cliente...", clientes.size());
                    clienteService.copiarCodantClienteEventual(this.provider.getSistema(), lojaModelo, this.provider.getLojaOrigem());
                }

                importarClienteEventual(clientes, opt);
            }
        }
    }

    public void importarClientePreferencial(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {

        int iniciarEm = 1;

        //Tratar opções
        boolean parametroValidos = true;
        for (OpcaoCliente opcao : opt) {
            if (OpcaoCliente.IMP_REINICIAR_NUMERACAO.equals(opcao)) {
                iniciarEm = (int) opcao.getParametros().get("N_REINICIO");
            }
            parametroValidos &= opcao.checkParametros();
        }

        if (parametroValidos) {

            //Eliminar duplicados, ordernar e identificar ids inválidos (> 999999)
            clientes = organizarListagem(clientes, opt);
            System.gc();

            this.provider.begin();
            try {
                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                provider.setNotificacao("Preparando para gravar cliente preferêncial...", clientes.size());

                ClientePreferencialIDStack ids = provider.getClientePreferencialIDStack(iniciarEm);
                Map<Long, Integer> cnpjCadastrados = provider.preferencial().getCnpjCadastrados();
                MultiMap<String, ClientePreferencialAnteriorVO> anteriores = provider.preferencial().getAnteriores();
                MultiMap<String, Void> contatos = provider.preferencial().getContatosExistentes();
                MultiMap<String, Void> dependentes = provider.preferencial().getContatosExistentes();

                //</editor-fold>
                provider.setNotificacao("Gravando cliente preferêncial...", clientes.size());

                boolean reiniciarID = opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO);

                for (ClienteIMP imp : clientes) {
                    ClientePreferencialAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
                    );

                    ClientePreferencialVO cliente = null;

                    //Se o cliente não tiver sido cadastrado anteriormente, executa.
                    if (anterior == null) {
                        //Obtem um ID válido.                    
                        int id = ids.obterID(reiniciarID ? "A" : imp.getId());

                        //Trata o cnpj
                        long cnpj = Utils.stringToLong(imp.getCnpj(), -2);
                        //Se o cnpj já estiver cadastrado, coloca -2 para gerar um novo.
                        if (cnpjCadastrados.containsKey(cnpj)) {
                            if (cnpjCadastrados.containsKey((long) id)) {
                                cnpj = -id;
                            } else {
                                cnpj = id;
                            }
                        }

                        //Converte os dados.
                        cliente = converterClientePreferencial(imp);
                        cliente.setId(id);
                        cliente.setCnpj(cnpj);

                        anterior = converterClientePreferencialAnterior(imp);
                        anterior.setCodigoAtual(cliente);
                        anterior.setIdConexao(provider.getIdConexao());

                        //Grava as informações
                        gravarClientePreferencial(cliente);
                        gravarClientePreferencialAnterior(anterior);

                        //Incluindo o produto nas listagens
                        cnpjCadastrados.put(cnpj, id);
                        anteriores.put(
                                anterior,
                                provider.getSistema(),
                                provider.getLojaOrigem(),
                                imp.getId()
                        );
                    } else {
                        cliente = anterior.getCodigoAtual();
                    }

                    if (opt.contains(OpcaoCliente.CONTATOS)) {
                        importarContatoPreferencial(cliente, imp, contatos);
                    }

                    if (opt.contains(OpcaoCliente.DEPENDENTES)) {
                        importarDependenteClientePreferencial(cliente, imp, dependentes);
                    }
                    provider.notificar();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                //Executa log de operação
                logController.executar(EOperacao.SALVAR_CLIENTE_PREFERENCIAL.getId(),
                        sdf.format(new Date()),
                        provider.getLojaVR());

                this.provider.commit();

                System.gc();
            } catch (Exception e) {
                this.provider.rollback();
                throw e;
            }

        } else {
            throw new Exception("Há valores incorretos nos parametros.");
        }
    }

    public void atualizarClientePreferencial(List<ClienteIMP> clientes, OpcaoCliente... opcoes) throws Exception {
        Set<OpcaoCliente> opt = new HashSet<>(Arrays.asList(opcoes));
        clientes = organizarListagem(clientes);
        System.gc();

        try {
            if (opt.isEmpty()) {
                opt.add(OpcaoCliente.DADOS);
                opt.add(OpcaoCliente.CONTATOS);
                opt.add(OpcaoCliente.DEPENDENTES);
                opt.add(OpcaoCliente.OBSERVACOES2);
                opt.add(OpcaoCliente.ENDERECO_COMPLETO);
            }
            provider.begin();

            //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
            provider.setNotificacao("Preparando para gravar cliente preferêncial...", clientes.size());
            MultiMap<String, ClientePreferencialAnteriorVO> anteriores = provider.preferencial().getAnteriores();
            //</editor-fold>

            provider.setNotificacao("Atualizando cliente preferêncial...", clientes.size());

            for (ClienteIMP imp : clientes) {
                ClientePreferencialAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getId()
                );

                if (anterior != null && anterior.getCodigoAtual() != null) {

                    ClientePreferencialVO vo = converterClientePreferencial(imp);
                    vo.setId(anterior.getCodigoAtual().getId());

                    if (opt.contains(OpcaoCliente.OBSERVACOES2)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.VALOR_LIMITE)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.DATA_NASCIMENTO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.ENDERECO_COMPLETO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.TIPO_INSCRICAO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.INSCRICAO_ESTADUAL)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.CNPJ)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.BLOQUEADO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.CEP)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.COMPLEMENTO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.ESTADO_CIVIL)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.PERMITE_CHEQUE)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.PERMITE_CREDITOROTATIVO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.TELEFONE)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.CELULAR)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.VENCIMENTO_ROTATIVO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.SEXO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.DATA_CADASTRO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.OBSERVACOES)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.RAZAO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.BAIRRO)) {
                        atualizarClientePreferencial(vo, opt);
                    }

                    if (opt.contains(OpcaoCliente.DATA_NASCIMENTO_CONJUGE)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.SITUACAO_CADASTRO)) {
                        atualizarClientePreferencial(vo, opt);
                    }

                    if (opt.contains(OpcaoCliente.EMPRESA)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.ENDERECO_EMPRESA)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.NUMERO_EMPRESA)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.COMPLEMENTO_EMPRESA)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.BAIRRO_EMPRESA)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.MUNICIPIO_EMPRESA)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.UF_EMPRESA)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.CEP_EMPRESA)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.TELEFONE_EMPRESA)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.CARGO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.SALARIO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                    if (opt.contains(OpcaoCliente.DATA_ADMISSAO)) {
                        atualizarClientePreferencial(vo, opt);
                    }
                }
                provider.notificar();
            }

            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    private void importarContatoPreferencial(ClientePreferencialVO cliente, ClienteIMP imp, MultiMap<String, Void> contatos) throws Exception {
        //Gravando os contatos se o código atual estiver preenchido.
        if (cliente != null) {
            for (ClienteContatoIMP impCont : imp.getContatos()) {
                //Converte o IMP em VO
                ClientePreferencialContatoVO contato = converterContatoPreferencial(impCont);
                contato.setIdClientePreferencial(cliente.getId());
                //Se houver algum contato cadastrado com essa assinatura,
                //Não executa a rotina
                if (!contatos.containsKey(
                        String.valueOf(contato.getIdClientePreferencial()),
                        contato.getNome(),
                        contato.getTelefone(),
                        contato.getCelular()
                )) {
                    gravarClientePreferencialContato(contato);
                    contatos.put(
                            null,
                            String.valueOf(cliente.getId()),
                            contato.getNome(),
                            contato.getTelefone(),
                            contato.getCelular()
                    );
                }
            }
        }
    }

    private void importarDependenteClientePreferencial(ClientePreferencialVO cliente, ClienteIMP imp, MultiMap<String, Void> dependentes) throws Exception {
        //Gravando os depententes se o código atual estiver preenchido.
        if (cliente != null) {
            for (ClienteDependenteIMP impDep : imp.getDependentes()) {
                //Converte o IMP em VO
                ClientePreferencialDependenteVO dependente = converterDependentePreferencial(impDep);
                dependente.setIdClientePreferencial(cliente.getId());
                //Se houver algum contato cadastrado com essa assinatura,
                //Não executa a rotina
                if (!dependentes.containsKey(
                        String.valueOf(dependente.getIdClientePreferencial()),
                        String.valueOf(dependente.getCpf())
                )) {
                    gravarClientePreferencialDependente(dependente);
                    dependentes.put(
                            null,
                            String.valueOf(cliente.getId()),
                            dependente.getNome(),
                            String.valueOf(dependente.getCpf()),
                            dependente.getTipoDependente()
                    );
                } else {
                    atualizaClientePreferencialDependente(dependente);
                }
            }
        }
    }

    public void importarClienteEventual(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {

        int iniciarEm = 1;

        //Tratar opções
        boolean parametroValidos = true;
        for (OpcaoCliente opcao : opt) {
            if (OpcaoCliente.IMP_REINICIAR_NUMERACAO.equals(opcao)) {
                iniciarEm = (int) opcao.getParametros().get("N_REINICIO");
            }
            parametroValidos &= opcao.checkParametros();
        }

        if (parametroValidos) {

            //Eliminar duplicados, ordernar e identificar ids inválidos (> 999999)
            clientes = organizarListagem(clientes, opt);
            System.gc();

            if (opt.isEmpty() || (opt.size() == 1 && opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO))) {
                opt.add(OpcaoCliente.DADOS);
                opt.add(OpcaoCliente.CONTATOS);
            }

            this.provider.begin();
            try {
                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                provider.setNotificacao("Preparando para gravar cliente eventuais...", clientes.size());
                ClienteEventualIDStack ids = provider.getClienteEventualIDStack(iniciarEm);
                Map<Long, Integer> cnpjCadastrados = provider.eventual().getCnpjCadastrados();
                MultiMap<String, ClienteEventualAnteriorVO> anteriores = provider.eventual().getAnteriores();
                MultiMap<String, Void> contatos = provider.eventual().getContatosExistentes();
                //</editor-fold>

                boolean reiniciarID = opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO);

                provider.setNotificacao("Gravando cliente eventual...", clientes.size());
                for (ClienteIMP imp : clientes) {
                    ClienteEventualAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
                    );

                    ClienteEventualVO cliente = null;

                    //Se o cliente não tiver sido cadastrado anteriormente, executa.
                    if (anterior == null) {
                        //Trata o cnpj
                        long cnpj = Utils.stringToLong(imp.getCnpj(), -2);
                        //Se o cnpj já estiver cadastrado, coloca -2 para gerar um novo.
                        if (cnpjCadastrados.containsKey(cnpj)) {
                            cnpj = -2;
                        }

                        //Obtem um ID válido.                    
                        int id = ids.obterID(reiniciarID ? "A" : imp.getId());

                        if (cnpj < 0) {
                            cnpj = id;
                        }

                        //Converte os dados.
                        cliente = converterClienteEventual(imp);
                        cliente.setId(id);
                        cliente.setCnpj(cnpj);

                        anterior = converterClienteEventualAnterior(imp);
                        anterior.setCodigoAtual(cliente);
                        anterior.setIdConexao(provider.getIdConexao());

                        //Grava as informações
                        gravarClienteEventual(cliente);
                        gravarClienteEventualAnterior(anterior);

                        //Incluindo o produto nas listagens
                        cnpjCadastrados.put(cnpj, id);
                        anteriores.put(
                                anterior,
                                provider.getSistema(),
                                provider.getLojaOrigem(),
                                imp.getId()
                        );
                    } else {
                        cliente = anterior.getCodigoAtual();
                    }

                    if (opt.contains(OpcaoCliente.CONTATOS)) {
                        importarContatoEventual(cliente, imp, contatos);
                    }

                    provider.notificar();
                }
                this.provider.commit();

                System.gc();
            } catch (Exception e) {
                this.provider.rollback();
                throw e;
            }
        }
    }

    private void importarContatoEventual(ClienteEventualVO cliente, ClienteIMP imp, MultiMap<String, Void> contatos) throws Exception {
        //Gravando os contatos se o código atual estiver preenchido.
        if (cliente != null) {
            for (ClienteContatoIMP impCont : imp.getContatos()) {
                //Converte o IMP em VO
                ClienteEventualContatoVO contato = converterContatoEventual(impCont);
                contato.setIdClienteEventual(cliente.getId());
                //Se houver algum contato cadastrado com essa assinatura,
                //Não executa a rotina
                if (!contatos.containsKey(
                        String.valueOf(contato.getIdClienteEventual()),
                        contato.getNome(),
                        contato.getTelefone(),
                        contato.getCelular(),
                        contato.getEmail()
                )) {
                    gravarClienteEventualContato(contato);
                    contatos.put(
                            null,
                            String.valueOf(cliente.getId()),
                            contato.getNome(),
                            contato.getTelefone(),
                            contato.getCelular(),
                            contato.getEmail()
                    );
                }
            }
        }
    }

    public List<ClienteIMP> organizarListagem(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) {
        LOG.fine("Organizando a listagem dos clientes");

        List<ClienteIMP> result = new ArrayList<>();
        Map<String, ClienteIMP> validos = new LinkedHashMap<>();
        Map<String, ClienteIMP> invalidos = new LinkedHashMap<>();

        boolean importarSomenteAtivos = opt.contains(OpcaoCliente.IMPORTAR_SOMENTE_ATIVO);

        clientes.forEach((imp) -> {
            //Verifica se o ID é válido para organizar a listagem;

            if (importarSomenteAtivos) { // Verificar se será migado somente clientes ativos
                if (imp.isAtivo()) {
                    try {
                        long id = Long.parseLong(imp.getId());

                        if (id > 0 && id <= 999999) {
                            validos.put(imp.getId(), imp);
                        } else {
                            invalidos.put(imp.getId(), imp);
                        }
                    } catch (NumberFormatException e) {
                        invalidos.put(imp.getId(), imp);
                    }
                }
            } else {
                try {
                    long id = Long.parseLong(imp.getId());

                    if (id > 0 && id <= 999999) {
                        validos.put(imp.getId(), imp);
                    } else {
                        invalidos.put(imp.getId(), imp);
                    }
                } catch (NumberFormatException e) {
                    invalidos.put(imp.getId(), imp);
                }
            }
        });

        /**
         * Unifica os resultados, colocando primeiro os clientes com IDs válidos
         * e depois os inválidos que receberão um novo id posteriormente.
         */
        result.addAll(validos.values());
        result.addAll(invalidos.values());

        LOG.fine("Quantidade de registros validos: " + validos.size() + ", inválidos: " + invalidos.size());

        //Liberar memória
        validos.clear();
        invalidos.clear();

        return result;
    }

    public List<ClienteIMP> organizarListagem(List<ClienteIMP> clientes) {

        LOG.fine("Organizando a listagem dos clientes");

        List<ClienteIMP> result = new ArrayList<>();
        Map<String, ClienteIMP> validos = new LinkedHashMap<>();
        Map<String, ClienteIMP> invalidos = new LinkedHashMap<>();

        for (ClienteIMP imp : clientes) {
            //Verifica se o ID é válido para organizar a listagem;
            try {
                long id = Long.parseLong(imp.getId());

                if (id > 0 && id <= 999999) {
                    validos.put(imp.getId(), imp);
                } else {
                    invalidos.put(imp.getId(), imp);
                }
            } catch (NumberFormatException e) {
                invalidos.put(imp.getId(), imp);
            }
        }

        /**
         * Unifica os resultados, colocando primeiro os clientes com IDs válidos
         * e depois os inválidos que receberão um novo id posteriormente.
         */
        result.addAll(validos.values());
        result.addAll(invalidos.values());

        LOG.fine("Quantidade de registros validos: " + validos.size() + ", inválidos: " + invalidos.size());

        //Liberar memória
        validos.clear();
        invalidos.clear();

        return result;
    }

    public ClientePreferencialVO converterClientePreferencial(ClienteIMP imp) throws Exception {
        ClientePreferencialVO vo = new ClientePreferencialVO();

        vo.setCnpj(Utils.stringToLong(imp.getCnpj()));

        if (imp.getTipoInscricao() == TipoInscricao.VAZIO) {
            vo.setTipoInscricao(TipoInscricao.analisarCnpjCpf(vo.getCnpj()));
        } else {
            vo.setTipoInscricao(imp.getTipoInscricao());
        }

        vo.setInscricaoEstadual(imp.getInscricaoestadual());
        vo.setOrgaoEmissor(imp.getOrgaoemissor());
        vo.setNome(imp.getRazao());
        vo.setSituacaocadastro(imp.isAtivo() ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
        vo.setBloqueado(imp.isBloqueado());
        vo.setDataRestricao(imp.getDataBloqueio());

        vo.setEndereco(imp.getEndereco());
        vo.setNumero(imp.getNumero());
        vo.setComplemento(imp.getComplemento());
        vo.setBairro(imp.getBairro());

        {
            MunicipioVO mun = provider.getMunicipioById(imp.getMunicipioIBGE());
            if (mun == null) {
                mun = provider.getMunicipioByNomeUf(
                        Utils.acertarTexto(imp.getMunicipio()),
                        Utils.acertarTexto(imp.getUf())
                );
                if (mun == null) {
                    mun = provider.getMunicipioPadrao();
                }
            }
            vo.setId_municipio(mun.getId());
            vo.setId_estado(mun.getEstado().getId());
        }

        vo.setCep(Utils.stringToInt(imp.getCep()));
        vo.setTipoEstadoCivil(imp.getEstadoCivil());
        vo.setDataNascimento(imp.getDataNascimento());
        vo.setDataCadastro(imp.getDataCadastro());
        vo.setSexo(imp.getSexo());
        vo.setEmpresa(imp.getEmpresa());
        vo.setEnderecoEmpresa(imp.getEmpresaEndereco());
        vo.setNumeroEmpresa(imp.getEmpresaNumero());
        vo.setComplementoEmpresa(imp.getEmpresaComplemento());
        vo.setBairroEmpresa(imp.getEmpresaBairro());

        {
            MunicipioVO mun = provider.getMunicipioById(imp.getEmpresaMunicipioIBGE());
            if (mun == null) {
                mun = provider.getMunicipioByNomeUf(
                        Utils.acertarTexto(imp.getEmpresaMunicipio()),
                        Utils.acertarTexto(imp.getEmpresaUf())
                );
                if (mun == null) {
                    mun = provider.getMunicipioPadrao();
                }
            }
            vo.setId_municipioEmpresa(mun.getId());
            vo.setId_estadoEmpresa(mun.getEstado().getId());
        }

        vo.setCepEmpresa(Utils.stringToInt(imp.getEmpresaCep()));
        vo.setTelefoneEmpresa(imp.getEmpresaTelefone());
        vo.setDataAdmissao(imp.getDataAdmissao());
        vo.setCargo(imp.getCargo());
        vo.setSalario(imp.getSalario());
        vo.setValorLimite(imp.getValorLimite());
        vo.setNomeConjuge(imp.getNomeConjuge());
        vo.setCpfConjuge(imp.getCpfConjuge());
        vo.setDataNascimentoConjuge(imp.getDataNascimentoConjuge());
        vo.setNomePai(imp.getNomePai());
        vo.setNomeMae(imp.getNomeMae());
        vo.setObservacao("IMPORTACAO VR" + imp.getObservacao() == null ? "" : " - " + imp.getObservacao());
        vo.setObservacao2(imp.getObservacao2() == null ? "" : imp.getObservacao2());
        vo.setVencimentoCreditoRotativo(imp.getDiaVencimento());
        vo.setPermiteCreditoRotativo(imp.isPermiteCreditoRotativo());
        vo.setPermiteCheque(imp.isPermiteCheque());
        vo.setPermiteChequeAVista(imp.isPermiteChequeAVista());
        vo.setTelefone(imp.getTelefone());
        vo.setCelular(imp.getCelular());
        vo.setEmail(imp.getEmail());
        vo.setSenha(imp.getSenha());
        vo.setGrupo(imp.getGrupo());

        return vo;
    }

    public ClientePreferencialAnteriorVO converterClientePreferencialAnterior(ClienteIMP imp) {
        ClientePreferencialAnteriorVO vo = new ClientePreferencialAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setId(imp.getId());
        vo.setCnpj(imp.getCnpj());
        vo.setIe(imp.getInscricaoestadual());
        vo.setNome(imp.getRazao());
        vo.setIdConexao(provider.getIdConexao());

        return vo;
    }

    public void gravarClientePreferencial(ClientePreferencialVO cliente) throws Exception {
        provider.preferencial().salvar(cliente);
    }

    public void gravarClientePreferencialAnterior(ClientePreferencialAnteriorVO anterior) throws Exception {
        provider.preferencial().salvar(anterior);
    }

    public ClienteEventualVO converterClienteEventual(ClienteIMP imp) throws Exception {
        ClienteEventualVO vo = new ClienteEventualVO();

        vo.setNome(imp.getRazao());

        vo.setEndereco(imp.getEndereco());
        vo.setNumero(imp.getNumero());
        vo.setComplemento(imp.getComplemento());
        vo.setBairro(imp.getBairro());
        {
            MunicipioVO mun = provider.getMunicipioById(imp.getMunicipioIBGE());
            if (mun == null) {
                mun = provider.getMunicipioByNomeUf(
                        Utils.acertarTexto(imp.getMunicipio()),
                        Utils.acertarTexto(imp.getUf())
                );
                if (mun == null) {
                    mun = provider.getMunicipioPadrao();
                }
            }
            vo.setId_municipio(mun.getId());
            vo.setId_estado(mun.getEstado().getId());
        }
        vo.setCep(Utils.stringToInt(imp.getCep()));

        vo.setTelefone(imp.getTelefone());

        vo.setCnpj(Utils.stringToLong(imp.getCnpj()));
        if (imp.getTipoInscricao() == TipoInscricao.VAZIO) {
            vo.setTipoInscricao(TipoInscricao.analisarCnpjCpf(vo.getCnpj()));
        } else {
            vo.setTipoInscricao(imp.getTipoInscricao());
        }

        vo.setInscricaoEstadual(imp.getInscricaoestadual());
        vo.setSituacaoCadastro(imp.isAtivo() ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
        vo.setFax(imp.getFax());

        vo.setEnderecoCobranca(imp.getCobrancaEndereco());
        vo.setNumeroCobranca(imp.getCobrancaNumero());
        vo.setComplementoCobranca(imp.getCobrancaComplemento());
        vo.setBairroCobranca(imp.getCobrancaBairro());
        {
            MunicipioVO mun = provider.getMunicipioById(imp.getCobrancaMunicipioIBGE());
            if (mun == null) {
                mun = provider.getMunicipioByNomeUf(
                        Utils.acertarTexto(imp.getCobrancaMunicipio()),
                        Utils.acertarTexto(imp.getCobrancaUf())
                );
                if (mun == null) {
                    mun = provider.getMunicipioPadrao();
                }
            }
            vo.setId_municipioCobranca(mun.getId());
            vo.setId_estadoCobranca(mun.getEstado().getId());
        }
        vo.setCepCobranca(Utils.stringToInt(imp.getCobrancaCep()));

        vo.setTelefoneCobranca(imp.getCobrancaTelefone());
        vo.setPrazoPagamento(imp.getPrazoPagamento());
        vo.setTipoOrgaoPublico(imp.getTipoOrgaoPublico());
        vo.setDataCadastro(imp.getDataCadastro() == null ? new Date() : imp.getDataCadastro());
        vo.setLimiteCompra(imp.getLimiteCompra());
        vo.setCobraTaxaNotaFiscal(false);

        vo.setId_tiporecebimento(0);
        vo.setBloqueado(imp.isBloqueado());
        vo.setObservacao("IMPORTADO VR " + imp.getObservacao());
        vo.setId_pais(1058);
        vo.setInscricaoMunicipal(imp.getInscricaoMunicipal());
        vo.setId_contaContabilFiscalPassivo(0);
        vo.setId_contaContabilFiscalAtivo(0);
        vo.setTipoIndicadorIe(imp.getTipoIndicadorIe());
        vo.setId_classeRisco(0);

        return vo;
    }

    public ClienteEventualAnteriorVO converterClienteEventualAnterior(ClienteIMP imp) {
        ClienteEventualAnteriorVO vo = new ClienteEventualAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setId(imp.getId());
        vo.setCnpj(imp.getCnpj());
        vo.setIe(imp.getInscricaoestadual());
        vo.setNome(imp.getRazao());
        return vo;
    }

    public void gravarClienteEventual(ClienteEventualVO cliente) throws Exception {
        provider.eventual().salvar(cliente);
    }

    public void gravarClienteEventualAnterior(ClienteEventualAnteriorVO anterior) throws Exception {
        provider.eventual().salvar(anterior);
    }

    /**
     * Efetuar a unificação de clientes preferênciais. (NÃO TESTADO)
     *
     * @param clientes
     * @param opt
     * @throws Exception
     */
    public void unificarClientePreferencial(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {

        int iniciarEm = 1;

        //Tratar opções
        boolean parametroValidos = true;
        for (OpcaoCliente opcao : opt) {
            if (OpcaoCliente.IMP_REINICIAR_NUMERACAO.equals(opcao)) {
                iniciarEm = (int) opcao.getParametros().get("N_REINICIO");
            }
            parametroValidos &= opcao.checkParametros();
        }

        if (parametroValidos) {

            //Eliminar duplicados, ordernar e identificar ids inválidos (> 999999)
            clientes = organizarListagem(clientes, opt);
            System.gc();

            this.provider.begin();
            try {
                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                provider.setNotificacao("Preparando para gravar cliente preferêncial (Unificação)...", clientes.size());
                ClientePreferencialIDStack ids = provider.getClientePreferencialIDStack(iniciarEm);
                Map<Long, Integer> cnpjCadastrados = provider.preferencial().getCnpjCadastrados();
                MultiMap<String, ClientePreferencialAnteriorVO> anteriores = provider.preferencial().getAnteriores();
                MultiMap<String, Void> contatos = provider.preferencial().getContatosExistentes();
                MultiMap<String, Void> dependentes = provider.preferencial().getDependentesExistentes();
                //</editor-fold>

                boolean reiniciarID = opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO);

                provider.setNotificacao("Gravando cliente preferêncial (Unificação)...", clientes.size());
                for (ClienteIMP imp : clientes) {
                    ClientePreferencialAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
                    );
                    //Se o cliente não tiver sido cadastrado anteriormente, executa.
                    if (anterior == null) {
                        //Trata o cnpj
                        long cnpj = Utils.stringToLong(imp.getCnpj(), -2);
                        /**
                         * Se o CNPJ/CPF é inválido, não importa o cliente, mas
                         * grava o cliente anterior.
                         */
                        if (String.valueOf(cnpj).length() < 8) {
                            anterior = converterClientePreferencialAnterior(imp);
                            gravarClientePreferencialAnterior(anterior);
                            continue;
                        }

                        /**
                         * Se o cnpj já estiver cadastrado, grava o código
                         * anterior relacionando com o id do cliente já
                         * cadastrado.
                         */
                        if (cnpjCadastrados.containsKey(cnpj)) {
                            anterior = converterClientePreferencialAnterior(imp);
                            ClientePreferencialVO cliente = new ClientePreferencialVO();
                            cliente.setId(cnpjCadastrados.get(cnpj));
                            anterior.setCodigoAtual(cliente);
                            gravarClientePreferencialAnterior(anterior);
                            importarContatoPreferencial(anterior.getCodigoAtual(), imp, contatos);
                            importarDependenteClientePreferencial(anterior.getCodigoAtual(), imp, dependentes);
                            continue;
                        }

                        /**
                         * Se passar por todas as validações, ou seja, o cnpj é
                         * válido não e existe no VR.
                         */
                        //Obtem um ID válido.                    
                        int id = ids.obterID(reiniciarID ? "A" : imp.getId());

                        if (cnpj < 0) {
                            cnpj = id;
                        }

                        //Converte os dados.
                        ClientePreferencialVO cliente = converterClientePreferencial(imp);
                        cliente.setId(id);
                        cliente.setCnpj(cnpj);

                        anterior = converterClientePreferencialAnterior(imp);
                        anterior.setCodigoAtual(cliente);
                        anterior.setIdConexao(provider.getIdConexao());

                        //Grava as informações
                        gravarClientePreferencial(cliente);
                        gravarClientePreferencialAnterior(anterior);

                        importarContatoPreferencial(cliente, imp, contatos);

                        //Incluindo o produto nas listagens
                        cnpjCadastrados.put(cnpj, id);
                        anteriores.put(
                                anterior,
                                provider.getSistema(),
                                provider.getLojaOrigem(),
                                imp.getId()
                        );
                    } else {
                        if (anterior.getCodigoAtual() != null) {
                            importarContatoPreferencial(anterior.getCodigoAtual(), imp, contatos);
                            importarDependenteClientePreferencial(anterior.getCodigoAtual(), imp, dependentes);
                        }
                    }

                    provider.notificar();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                //Executa log de operação
                logController.executar(EOperacao.UNIFICAR_CLIENTE_PREFERENCIAL.getId(),
                        sdf.format(new Date()),
                        provider.getLojaVR());

                this.provider.commit();

                System.gc();
            } catch (Exception e) {
                this.provider.rollback();
                throw e;
            }
        }
    }

    /**
     * Efetua a unificação do cliente eventual. (NÃO TESTADO)
     *
     * @param clientes Listagem de clientes.
     * @param opt
     * @throws Exception
     */
    public void unificarClienteEventual(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {

        int iniciarEm = 1;

        //Tratar opções
        boolean parametroValidos = true;
        for (OpcaoCliente opcao : opt) {
            if (OpcaoCliente.IMP_REINICIAR_NUMERACAO.equals(opcao)) {
                iniciarEm = (int) opcao.getParametros().get("N_REINICIO");
            }
            parametroValidos &= opcao.checkParametros();
        }

        if (parametroValidos) {

            //Eliminar duplicados, ordernar e identificar ids inválidos (> 999999)
            clientes = organizarListagem(clientes, opt);
            System.gc();

            this.provider.begin();
            try {
                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                provider.setNotificacao("Preparando para gravar cliente eventual (Unificação)...", clientes.size());
                ClienteEventualIDStack ids = provider.getClienteEventualIDStack(iniciarEm);
                Map<Long, Integer> cnpjCadastrados = provider.eventual().getCnpjCadastrados();
                MultiMap<String, ClienteEventualAnteriorVO> anteriores = provider.eventual().getAnteriores();
                MultiMap<String, Void> contatos = provider.eventual().getContatosExistentes();
                //</editor-fold>

                boolean reiniciarID = opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO);

                provider.setNotificacao("Gravando cliente eventual (Unificação)...", clientes.size());
                for (ClienteIMP imp : clientes) {
                    ClienteEventualAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
                    );
                    //Se o cliente não tiver sido cadastrado anteriormente, executa.
                    if (anterior == null) {
                        //Trata o cnpj
                        long cnpj = Utils.stringToLong(imp.getCnpj(), -2);
                        /**
                         * Se o CNPJ/CPF é inválido, não importa o cliente, mas
                         * grava o cliente anterior.
                         */
                        if (String.valueOf(cnpj).length() < 8) {
                            anterior = converterClienteEventualAnterior(imp);
                            gravarClienteEventualAnterior(anterior);
                            continue;
                        }

                        /**
                         * Se o cnpj já estiver cadastrado, grava o código
                         * anterior relacionando com o id do cliente já
                         * cadastrado.
                         */
                        if (cnpjCadastrados.containsKey(cnpj)) {
                            anterior = converterClienteEventualAnterior(imp);
                            ClienteEventualVO cliente = new ClienteEventualVO();
                            cliente.setId(cnpjCadastrados.get(cnpj));
                            anterior.setCodigoAtual(cliente);
                            gravarClienteEventualAnterior(anterior);
                            importarContatoEventual(anterior.getCodigoAtual(), imp, contatos);
                            continue;
                        }

                        /**
                         * Se passar por todas as validações, ou seja, o cnpj é
                         * válido não e existe no VR.
                         */
                        //Obtem um ID válido.                    
                        int id = ids.obterID(reiniciarID ? "A" : imp.getId());

                        if (cnpj < 0) {
                            cnpj = id;
                        }

                        //Converte os dados.
                        ClienteEventualVO cliente = converterClienteEventual(imp);
                        cliente.setId(id);
                        cliente.setCnpj(cnpj);

                        anterior = converterClienteEventualAnterior(imp);
                        anterior.setCodigoAtual(cliente);
                        anterior.setIdConexao(provider.getIdConexao());

                        //Grava as informações
                        gravarClienteEventual(cliente);
                        gravarClienteEventualAnterior(anterior);

                        importarContatoEventual(cliente, imp, contatos);

                        //Incluindo o produto nas listagens
                        cnpjCadastrados.put(cnpj, id);
                        anteriores.put(
                                anterior,
                                provider.getSistema(),
                                provider.getLojaOrigem(),
                                imp.getId()
                        );

                    } else {
                        if (anterior.getCodigoAtual() != null) {
                            importarContatoEventual(anterior.getCodigoAtual(), imp, contatos);
                        }
                    }

                    provider.notificar();
                }
                this.provider.commit();

                System.gc();
            } catch (Exception e) {
                this.provider.rollback();
                throw e;
            }

        }
    }

    public ClientePreferencialContatoVO converterContatoPreferencial(ClienteContatoIMP impCont) {
        ClientePreferencialContatoVO cont = new ClientePreferencialContatoVO();
        cont.setNome(impCont.getNome());
        cont.setTelefone(impCont.getTelefone());
        cont.setCelular(impCont.getCelular());
        return cont;
    }

    public ClientePreferencialDependenteVO converterDependentePreferencial(ClienteDependenteIMP impDep) {
        ClientePreferencialDependenteVO dep = new ClientePreferencialDependenteVO();
        dep.setNome(impDep.getNome());
        dep.setCpf(impDep.getCpf());
        dep.setTipoDependente(impDep.getTipodependente());
        return dep;
    }

    public ClienteEventualContatoVO converterContatoEventual(ClienteContatoIMP impCont) {
        ClienteEventualContatoVO cont = new ClienteEventualContatoVO();

        String celular = "", telefone = "";

        if (impCont.getTelefone() != null && !"".equals(impCont.getTelefone())) {
            if (impCont.getTelefone().length() > 14) {
                telefone = impCont.getTelefone().substring(0, 14);
            }
        }

        if (impCont.getCelular() != null && !"".equals(impCont.getCelular())) {
            if (impCont.getCelular().length() > 14) {
                celular = impCont.getCelular().substring(0, 14);
            }
        }

        cont.setNome(impCont.getNome());
        cont.setTelefone(telefone);
        cont.setCelular(celular);
        cont.setEmail(impCont.getEmail());
        return cont;
    }

    public void gravarClientePreferencialContato(ClientePreferencialContatoVO contato) throws Exception {
        provider.preferencial().salvar(contato);
    }

    public void gravarClientePreferencialDependente(ClientePreferencialDependenteVO dependente) throws Exception {
        provider.preferencial().salvar(dependente);
    }

    public void atualizaClientePreferencialDependente(ClientePreferencialDependenteVO dependente) throws Exception {
        provider.preferencial().atualizar(dependente);
    }

    public void gravarClienteEventualContato(ClienteEventualContatoVO contato) throws Exception {
        provider.eventual().salvar(contato);
    }

    private void atualizarClientePreferencial(ClientePreferencialVO vo, Set<OpcaoCliente> opt) throws Exception {
        provider.atualizarClientePreferencial(vo, opt);
    }

    public void importarClienteVRFood(List<ClienteIMP> clientes, HashSet<OpcaoCliente> opt) throws Exception {

        provider.begin();
        try {
            provider.setNotificacao("Cliente (VRFood)...Carregando dados necessários...", 0);
            LOG.info("Carregando informações necessárias para importar os clientes VRFood");

            Map<String, ClienteFoodAnteriorVO> anteriores = provider.food().getAnteriores();
            Map<Long, ClienteFoodVO> telefones = provider.food().getTelefones();
            IDStack ids = provider.food().getClienteVrFoodIds();

            provider.setNotificacao("Cliente (VRFood)...Gravando...", clientes.size());
            LOG.info("Iniciando gravação das informações: " + clientes.size() + " registro(s)");
            for (ClienteIMP imp : organizarListagem(clientes)) {

                ClienteFoodAnteriorVO anterior = anteriores.get(imp.getId());

                //Organiza a listagem de telefones
                Set<Long> impTelefones = converterTelefones(imp);

                //Verifica se o telefone já existe e retorna o código do cliente 
                //que o possuí ou nulo.
                ClienteFoodVO codigoAtual = null;
                for (Long telefone : impTelefones) {
                    codigoAtual = telefones.get(telefone);
                    if (codigoAtual != null) {
                        break;
                    }
                }

                //Não existe código anterior
                if (anterior == null) {

                    anterior = converterClienteFoodAnterior(imp);
                    //Se um dos telefones já existir, joga o código atual deste cliente.
                    if (codigoAtual != null && opt.contains(OpcaoCliente.IMP_CORRIGIR_TELEFONE)) {
                        int id = codigoAtual.getId();
                        //Atualizo o código anterior
                        anterior.setCodigoAtual(id);
                        provider.food().gravarClienteFoodAnterior(anterior);
                        anteriores.put(imp.getId(), anterior); //Inclui o anterior na lista.

                        //Atualiza o cliente food
                        codigoAtual = converterClienteFood(imp);
                        codigoAtual.setId(id);
                        provider.food().atualizarClienteFood(codigoAtual, opt);

                        //Inclui os telefones
                        for (Long telefone : impTelefones) {
                            if (!codigoAtual.getTelefones().contains(telefone)) {
                                codigoAtual.getTelefones().add(telefone);
                                provider.food().incluirTelefoneFood(codigoAtual.getId(), telefone);
                                telefones.put(telefone, codigoAtual); //Inclui o telefone na lista
                            }
                        }

                    } else if (opt.contains(OpcaoCliente.NOVOS)) {
                        //Se não existir nenhuma informação do cliente no banco.

                        //Converte e grava o cliente no banco.
                        codigoAtual = converterClienteFood(imp);
                        codigoAtual.setId((int) ids.pop(imp.getId()));
                        provider.food().gravarClienteFood(codigoAtual);

                        //Converte e grava o anterior
                        anterior.setCodigoAtual(codigoAtual.getId());
                        provider.food().gravarClienteFoodAnterior(anterior);
                        anteriores.put(imp.getId(), anterior);

                        //Inclui os telefones
                        for (Long telefone : impTelefones) {
                            if (!codigoAtual.getTelefones().contains(telefone)) {
                                codigoAtual.getTelefones().add(telefone);
                                provider.food().incluirTelefoneFood(codigoAtual.getId(), telefone);
                                telefones.put(telefone, codigoAtual); //Inclui o telefone na lista
                            }
                        }
                    }

                } else {
                    // Se o produto já houver sido importado, verifica se há alguma
                    //opção de atualização.
                    if (anterior.getCodigoAtual() != 0) {

                        //Atualiza o cliente food
                        codigoAtual = converterClienteFood(imp);
                        codigoAtual.setId(anterior.getCodigoAtual());
                        provider.food().atualizarClienteFood(codigoAtual, opt);

                        //Inclui os telefones
                        for (Long telefone : impTelefones) {
                            if (!codigoAtual.getTelefones().contains(telefone)) {
                                codigoAtual.getTelefones().add(telefone);
                                provider.food().incluirTelefoneFood(codigoAtual.getId(), telefone);
                                telefones.put(telefone, codigoAtual); //Inclui o telefone na lista
                            }
                        }

                    } else if (anterior.isForcarGravacao()) {
                        //Se o código atual for nulo

                        //Converte e grava o cliente no banco.
                        codigoAtual = converterClienteFood(imp);
                        codigoAtual.setId((int) ids.pop(imp.getId()));
                        provider.food().gravarClienteFood(codigoAtual);

                        //Atualizo o código anterior
                        anterior.setCodigoAtual(codigoAtual.getId());
                        provider.food().atualizarClienteFoodAnterior(anterior);
                        anteriores.put(imp.getId(), anterior); //Inclui o anterior na lista.                    

                        //Inclui os telefones
                        for (Long telefone : impTelefones) {
                            if (!codigoAtual.getTelefones().contains(telefone)) {
                                codigoAtual.getTelefones().add(telefone);
                                provider.food().incluirTelefoneFood(codigoAtual.getId(), telefone);
                                telefones.put(telefone, codigoAtual); //Inclui o telefone na lista
                            }
                        }

                    }

                }

                provider.notificar();
            }

            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }

    }

    public Set<Long> converterTelefones(ClienteIMP imp) {
        Set<Long> result = new HashSet<>();

        if (Utils.stringToLong(imp.getTelefone()) != 0) {
            result.add(Utils.stringToLong(imp.getTelefone()));
        }
        if (Utils.stringToLong(imp.getCelular()) != 0) {
            result.add(Utils.stringToLong(imp.getCelular()));
        }
        if (Utils.stringToLong(imp.getFax()) != 0) {
            result.add(Utils.stringToLong(imp.getFax()));
        }
        for (ClienteContatoIMP contato : imp.getContatos()) {
            if (Utils.stringToLong(contato.getTelefone()) != 0) {
                result.add(Utils.stringToLong(contato.getTelefone()));
            }
            if (Utils.stringToLong(contato.getCelular()) != 0) {
                result.add(Utils.stringToLong(contato.getCelular()));
            }
        }

        for (Iterator<Long> iterator = result.iterator(); iterator.hasNext();) {
            long telefone = iterator.next();
            if (telefone > 99999999999999L || telefone < 1) {
                iterator.remove();
            }
        }

        return result;
    }

    private ClienteFoodAnteriorVO converterClienteFoodAnterior(ClienteIMP imp) throws Exception {
        ClienteFoodAnteriorVO ant = new ClienteFoodAnteriorVO();

        ant.setSistema(provider.getSistema());
        ant.setLoja(provider.getLojaOrigem());
        ant.setId(imp.getId());
        ant.setNome(imp.getRazao());
        ant.setForcarGravacao(false);

        return ant;
    }

    private ClienteFoodVO converterClienteFood(ClienteIMP imp) throws Exception {
        ClienteFoodVO vo = new ClienteFoodVO();

        vo.setNome(imp.getRazao());
        vo.setEndereco(imp.getEndereco());
        vo.setNumero(imp.getNumero());
        vo.setBairro(imp.getBairro());
        {
            MunicipioVO mun = provider.getMunicipioById(imp.getMunicipioIBGE());
            if (mun == null) {
                mun = provider.getMunicipioByNomeUf(
                        Utils.acertarTexto(imp.getMunicipio()),
                        Utils.acertarTexto(imp.getUf())
                );
                if (mun == null) {
                    mun = provider.getMunicipioPadrao();
                }
            }
            vo.setId_municipio(mun.getId());
        }
        vo.setObservacao(imp.getObservacao());
        vo.setSituacaoCadastro(imp.isAtivo() ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

        return vo;
    }

    public void importarClientePontuacao(List<ClienteIMP> clientes) throws Exception {

        PdvVendaDAO vendaDAO = new PdvVendaDAO();
        PdvVendaItemDAO vendaItemDAO = new PdvVendaItemDAO(provider.getSistema(), provider.getLojaOrigem());
        ClientePreferencialAnteriorDAO clienteAnteriorDAO = new ClientePreferencialAnteriorDAO();

        clienteAnteriorDAO.createTablePontuacao();
        clienteAnteriorDAO.deletarPontuacao();

        //Eliminar duplicados, ordernar e identificar ids inválidos (> 999999)
        clientes = organizarListagem(clientes);
        System.gc();

        this.provider.begin();
        try {
            //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
            provider.setNotificacao("Preparando para gravar cliente pontuação...", clientes.size());
            MultiMap<String, ClientePreferencialAnteriorVO> anteriores = provider.preferencial().getAnteriores();

            provider.setNotificacao("Gravando cliente pontuação...", clientes.size());

            java.sql.Date dataPontuacao = Utils.getDataAtual();
            int numeroCupom = 1;

            for (ClienteIMP imp : clientes) {
                ClientePreferencialAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getId()
                );

                ClientePreferencialVO cliente = null;

                //Se o cliente não tiver sido cadastrado anteriormente, executa.
                if (anterior == null) {
                    System.out.println("Sistema: " + provider.getSistema() + " - id: " + imp.getId() + " - nome: " + imp.getRazao() + " não encontrado!");
                    continue;
                } else {
                    cliente = anterior.getCodigoAtual();

                    if (cliente == null) {
                        System.out.println("id: " + imp.getId() + " - nome: " + imp.getRazao() + " não encontrado!");
                        continue;
                    }

                    if (imp.getPonto() > 0) {

                        PdvVendaVO vendaVO = new PdvVendaVO();
                        PdvVendaItemVO itemVO = new PdvVendaItemVO();
                        PdvVendaPromocaoPontuacaoVO pontuacaoVO = new PdvVendaPromocaoPontuacaoVO();
                        ClientePreferencialAnteriorVO pontuacaoAnteriorVO = new ClientePreferencialAnteriorVO();

                        vendaVO.setId_loja(provider.getLojaVR());
                        vendaVO.setNumeroCupom(numeroCupom);
                        vendaVO.setEcf(0);
                        vendaVO.setData(dataPontuacao);
                        vendaVO.setId_clientePreferencial(cliente.getId());
                        vendaVO.setHoraInicio(new Time(new Date().getTime()));
                        vendaVO.setHoraTermino(new Time(new Date().getTime()));
                        vendaVO.setCancelado(true);
                        vendaVO.setSubTotalImpressora(0);
                        vendaVO.setCpf(Utils.stringToLong(imp.getCnpj()));
                        vendaVO.setContadorDoc(0);
                        vendaVO.setValorDesconto(0);
                        vendaVO.setValorAcrescimo(0);
                        vendaVO.setCanceladoEmVenda(true);
                        vendaVO.setNumeroSerie("");
                        vendaVO.setMfAdicional(0);
                        vendaVO.setModeloImpressora("");
                        vendaVO.setNumeroUsuario(0);

                        if (imp.getRazao() != null && imp.getRazao().length() > 45) {
                            vendaVO.setNomeCliente(imp.getRazao().substring(0, 45));
                        }

                        String endereco = imp.getEndereco() + ","
                                + imp.getNumero() + ","
                                + imp.getBairro() + ","
                                + imp.getMunicipio();

                        if (endereco != null && endereco.length() > 50) {
                            endereco = endereco.substring(0, 50);
                        }

                        vendaVO.setEnderecoCliente(endereco);

                        //Gerar venda
                        vendaDAO.gerarVendaPontuacao(vendaVO);

                        itemVO.setVenda(vendaVO);
                        //itemVO.setId_produto(provider.getProduto());
                        itemVO.setId_produto(17456);
                        itemVO.setQuantidade(1);
                        itemVO.setPrecoVenda(0);
                        itemVO.setId_aliquota(6);
                        itemVO.setCancelado(true);
                        itemVO.setValorCancelado(0);
                        itemVO.setContadorDoc(0);
                        itemVO.setValorDesconto(0);
                        itemVO.setValorDescontoCupom(0);
                        itemVO.setValorAcrescimo(0);
                        itemVO.setValorAcrescimoCupom(0);
                        itemVO.setRegraCalculo("");
                        //itemVO.setCodigoBarras(provider.getEan());
                        itemVO.setCodigoBarras(7897649731005l);
                        itemVO.setUnidadeMedida("");
                        itemVO.setTotalizadorParcial("");
                        itemVO.setSequencia(1);

                        //Gerar Venda Item
                        vendaItemDAO.gravarItemPontuacao(itemVO);

                        pontuacaoVO.setIdVenda(vendaVO.getId());
                        pontuacaoVO.setIdPromocao(1);
                        pontuacaoVO.setPonto(imp.getPonto());
                        pontuacaoVO.setCnpj(Utils.stringToLong(imp.getCnpj()));
                        pontuacaoVO.setIdSituacaoPromocaoPontuacao(1);
                        pontuacaoVO.setLancamentoManual(false);
                        pontuacaoVO.setIdLoja(provider.getLojaVR());
                        pontuacaoVO.setDataCompra(dataPontuacao);
                        pontuacaoVO.setDataExpiracao(Utils.getDataAtual());

                        //Gerar Venda Promoção Pontuação
                        vendaDAO.gerarVendaPromocaoPontuacao(pontuacaoVO);
                        numeroCupom++;

                        pontuacaoAnteriorVO.setSistema(provider.getSistema());
                        pontuacaoAnteriorVO.setLoja(provider.getLojaOrigem());
                        pontuacaoAnteriorVO.setId(String.valueOf(cliente.getId()));
                        pontuacaoAnteriorVO.setNome(imp.getRazao());
                        pontuacaoAnteriorVO.setPonto((int) imp.getPonto());

                        //Gravar Código anterior
                        clienteAnteriorDAO.salvarPontuacao(pontuacaoAnteriorVO, vendaVO.getId());
                    }
                }

                provider.notificar();
            }
            this.provider.commit();

            System.gc();
        } catch (Exception e) {
            this.provider.rollback();
            throw e;
        }
    }
}
