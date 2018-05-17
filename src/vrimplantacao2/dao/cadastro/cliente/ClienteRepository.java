package vrimplantacao2.dao.cadastro.cliente;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.ClienteContatoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;

/**
 * Repositório do Cliente para efetuar a importação de Eventuais e Preferenciais.
 * @author Leandro
 */
public class ClienteRepository {
    private ClienteRepositoryProvider provider;

    public ClienteRepository(ClienteRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }
    
    public void importarClientePreferencial(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {  
                
        int iniciarEm = 1;
        
        //Tratar opções
        boolean parametroValidos = true;
        for (OpcaoCliente opcao: opt) {            
            if (OpcaoCliente.IMP_REINICIAR_NUMERACAO.equals(opcao)) {
                iniciarEm = (int) opcao.getParametros().get("N_REINICIO");
            }            
            parametroValidos &= opcao.checkParametros();
        }
        
        if (parametroValidos) {
        
            //Eliminar duplicados, ordernar e identificar ids inválidos (> 999999)
            clientes = organizarListagem(clientes);        
            System.gc();

            if (opt.isEmpty() || (opt.size() == 1 && opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO))) {
                opt.add(OpcaoCliente.DADOS);
                opt.add(OpcaoCliente.CONTATOS);
                opt.add(OpcaoCliente.OBSERVACOES2);
                opt.add(OpcaoCliente.SITUACAO_CADASTRO);
                opt.add(OpcaoCliente.INSCRICAO_ESTADUAL);
                opt.add(OpcaoCliente.DATA_NASCIMENTO);
                opt.add(OpcaoCliente.CELULAR);
                opt.add(OpcaoCliente.PERMITE_CHEQUE);
                opt.add(OpcaoCliente.PERMITE_CREDITOROTATIVO);
            }

            this.provider.begin();
            try {
                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                setNotificacao("Preparando para gravar cliente preferêncial...", clientes.size());                
                
                ClientePreferencialIDStack ids = provider.getClientePreferencialIDStack(iniciarEm);
                Map<Long, Integer> cnpjCadastrados = provider.preferencial().getCnpjCadastrados();
                MultiMap<String, ClientePreferencialAnteriorVO> anteriores = provider.preferencial().getAnteriores();
                MultiMap<String, Void> contatos = provider.preferencial().getContatosExistentes();
                //</editor-fold>

                setNotificacao("Gravando cliente preferêncial...", clientes.size());
                
                boolean reiniciarID = opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO);
                
                for (ClienteIMP imp: clientes) {
                    ClientePreferencialAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
                    );

                    ClientePreferencialVO cliente = null;

                    //Se o cliente não tiver sido cadastrado anteriormente, executa.
                    if (anterior == null) {     
                        if (opt.contains(OpcaoCliente.DADOS)) {

                            //Obtem um ID válido.                    
                            int id = ids.obterID(reiniciarID ? "A": imp.getId());
                            
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
                        }
                    } else {
                        if ((opt.contains(OpcaoCliente.OBSERVACOES2))
                                || (opt.contains(OpcaoCliente.SITUACAO_CADASTRO))
                                || (opt.contains(OpcaoCliente.SITUACAO_CADASTRO))
                                || (opt.contains(OpcaoCliente.VALOR_LIMITE))
                                || (opt.contains(OpcaoCliente.INSCRICAO_ESTADUAL))
                                || (opt.contains(OpcaoCliente.DATA_NASCIMENTO))
                                || (opt.contains(OpcaoCliente.TELEFONE))
                                || (opt.contains(OpcaoCliente.CELULAR))
                                || (opt.contains(OpcaoCliente.PERMITE_CHEQUE))
                                || (opt.contains(OpcaoCliente.PERMITE_CREDITOROTATIVO))) {

                            cliente = converterClientePreferencial(imp);
                            cliente.setId(anterior.getCodigoAtual().getId());
                            atualizarClientePreferencial(cliente, opt);
                        } else {
                            cliente = anterior.getCodigoAtual();
                        }
                    }

                    if (opt.contains(OpcaoCliente.CONTATOS)) {
                        importarContatoPreferencial(cliente, imp, contatos);
                    }
                    notificar();
                }
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
                opt.add(OpcaoCliente.OBSERVACOES2);
                opt.add(OpcaoCliente.ENDERECO_COMPLETO);
            }
            provider.begin();
            
            //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
            setNotificacao("Preparando para gravar cliente preferêncial...", clientes.size());
            MultiMap<String, ClientePreferencialAnteriorVO> anteriores = provider.preferencial().getAnteriores();
            //</editor-fold>
            
            setNotificacao("Atualizando cliente preferêncial...", clientes.size());
            for (ClienteIMP imp: clientes) {                
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
                }
                notificar();
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
            for (ClienteContatoIMP impCont: imp.getContatos()) {
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

    public void importarClienteEventual(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {
        
        int iniciarEm = 1;
        
        //Tratar opções
        boolean parametroValidos = true;
        for (OpcaoCliente opcao: opt) {            
            if (OpcaoCliente.IMP_REINICIAR_NUMERACAO.equals(opcao)) {
                iniciarEm = (int) opcao.getParametros().get("N_REINICIO");
            }            
            parametroValidos &= opcao.checkParametros();
        }
        
        if (parametroValidos) {

            //Eliminar duplicados, ordernar e identificar ids inválidos (> 999999)
            clientes = organizarListagem(clientes);        
            System.gc();

            if (opt.isEmpty() || (opt.size() == 1 && opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO))) {
                opt.add(OpcaoCliente.DADOS);
                opt.add(OpcaoCliente.CONTATOS);
            }

            this.provider.begin();
            try {
                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                setNotificacao("Preparando para gravar cliente eventuais...", clientes.size());
                ClienteEventualIDStack ids = provider.getClienteEventualIDStack(iniciarEm);
                Map<Long, Integer> cnpjCadastrados = provider.eventual().getCnpjCadastrados();
                MultiMap<String, ClienteEventualAnteriorVO> anteriores = provider.eventual().getAnteriores();
                MultiMap<String, Void> contatos = provider.eventual().getContatosExistentes();
                //</editor-fold>

                boolean reiniciarID = opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO);
                
                setNotificacao("Gravando cliente eventual...", clientes.size());
                for (ClienteIMP imp: clientes) {
                    ClienteEventualAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
                    );

                    ClienteEventualVO cliente = null;

                    //Se o cliente não tiver sido cadastrado anteriormente, executa.
                    if (anterior == null) { 
                        if (opt.contains(OpcaoCliente.DADOS)) {
                            //Trata o cnpj
                            long cnpj = Utils.stringToLong(imp.getCnpj(), -2);
                            //Se o cnpj já estiver cadastrado, coloca -2 para gerar um novo.
                            if (cnpjCadastrados.containsKey(cnpj)) {
                                cnpj = -2;
                            }

                            //Obtem um ID válido.                    
                            int id = ids.obterID(reiniciarID ? "A": imp.getId());

                            if (cnpj < 0) {
                                cnpj = id;
                            }

                            //Converte os dados.
                            cliente = converterClienteEventual(imp);                    
                            cliente.setId(id);
                            cliente.setCnpj(cnpj);

                            anterior = converterClienteEventualAnterior(imp);
                            anterior.setCodigoAtual(cliente);

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
                        }
                    } else {
                        cliente = anterior.getCodigoAtual();
                    }

                    if (opt.contains(OpcaoCliente.CONTATOS)) {
                        importarContatoEventual(cliente, imp, contatos);
                    }

                    notificar();
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
            for (ClienteContatoIMP impCont: imp.getContatos()) {
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

    public void setNotificacao(String mensagem, int qtd) throws Exception {
        ProgressBar.setStatus(mensagem);
        ProgressBar.setMaximum(qtd);
    }

    public void notificar() throws Exception {
        ProgressBar.next();
    }

    public List<ClienteIMP> organizarListagem(List<ClienteIMP> clientes) {
        List<ClienteIMP> result = new ArrayList<>();
        Map<String, ClienteIMP> validos = new LinkedHashMap<>();
        Map<String, ClienteIMP> invalidos = new LinkedHashMap<>();
        
        for (ClienteIMP imp: clientes) {
            //Verifica se o ID é válido para organizar a listagem;
            try {
                long id = Long.parseLong(imp.getId());
                
                if (id <= 999999) {
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
        vo.setNomePai(imp.getNomePai());
        vo.setNomeMae(imp.getNomeMae());
        vo.setObservacao("IMPORTACAO VR");
        vo.setObservacao2(imp.getObservacao2());
        vo.setVencimentoCreditoRotativo(imp.getDiaVencimento());
        vo.setPermiteCreditoRotativo(imp.isPermiteCreditoRotativo());
        vo.setPermiteCheque(imp.isPermiteCheque());
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
     * @param clientes
     * @param opt
     * @throws Exception 
     */
    public void unificarClientePreferencial(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {
        
        int iniciarEm = 1;
        
        //Tratar opções
        boolean parametroValidos = true;
        for (OpcaoCliente opcao: opt) {            
            if (OpcaoCliente.IMP_REINICIAR_NUMERACAO.equals(opcao)) {
                iniciarEm = (int) opcao.getParametros().get("N_REINICIO");
            }            
            parametroValidos &= opcao.checkParametros();
        }
        
        if (parametroValidos) {        
        
            //Eliminar duplicados, ordernar e identificar ids inválidos (> 999999)
            clientes = organizarListagem(clientes);        
            System.gc();

            this.provider.begin();
            try {
                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                setNotificacao("Preparando para gravar cliente preferêncial (Unificação)...", clientes.size());
                ClientePreferencialIDStack ids = provider.getClientePreferencialIDStack(iniciarEm);
                Map<Long, Integer> cnpjCadastrados = provider.preferencial().getCnpjCadastrados();
                MultiMap<String, ClientePreferencialAnteriorVO> anteriores = provider.preferencial().getAnteriores();
                MultiMap<String, Void> contatos = provider.preferencial().getContatosExistentes();
                //</editor-fold>

                boolean reiniciarID = opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO); 
                
                setNotificacao("Gravando cliente preferêncial (Unificação)...", clientes.size());
                for (ClienteIMP imp: clientes) {
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
                         * Se o CNPJ/CPF é inválido, não importa o cliente, mas grava
                         * o cliente anterior.
                         */
                        if (String.valueOf(cnpj).length() < 8) {
                            anterior = converterClientePreferencialAnterior(imp);
                            gravarClientePreferencialAnterior(anterior);  
                            continue;
                        }                    

                        /**
                         * Se o cnpj já estiver cadastrado, grava o código anterior 
                         * relacionando com o id do cliente já cadastrado.
                        */
                        if (cnpjCadastrados.containsKey(cnpj)) {
                            anterior = converterClientePreferencialAnterior(imp);
                            ClientePreferencialVO cliente = new ClientePreferencialVO();
                            cliente.setId(cnpjCadastrados.get(cnpj));
                            anterior.setCodigoAtual(cliente);
                            gravarClientePreferencialAnterior(anterior);
                            importarContatoPreferencial(anterior.getCodigoAtual(), imp, contatos);
                            continue;
                        }

                        /**
                         * Se passar por todas as validações, ou seja, o cnpj é 
                         * válido não e existe no VR.
                         */

                        //Obtem um ID válido.                    
                        int id = ids.obterID(reiniciarID ? "A": imp.getId());

                        if (cnpj < 0) {
                            cnpj = id;
                        }

                        //Converte os dados.
                        ClientePreferencialVO cliente = converterClientePreferencial(imp);                    
                        cliente.setId(id);
                        cliente.setCnpj(cnpj);

                        anterior = converterClientePreferencialAnterior(imp);
                        anterior.setCodigoAtual(cliente);

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
                        }
                    }

                    notificar();
                }
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
     * @param clientes Listagem de clientes.
     * @param opt
     * @throws Exception 
     */
    public void unificarClienteEventual(List<ClienteIMP> clientes, Set<OpcaoCliente> opt) throws Exception {
        
        int iniciarEm = 1;
        
        //Tratar opções
        boolean parametroValidos = true;
        for (OpcaoCliente opcao: opt) {            
            if (OpcaoCliente.IMP_REINICIAR_NUMERACAO.equals(opcao)) {
                iniciarEm = (int) opcao.getParametros().get("N_REINICIO");
            }            
            parametroValidos &= opcao.checkParametros();
        }
        
        if (parametroValidos) {
        
            //Eliminar duplicados, ordernar e identificar ids inválidos (> 999999)
            clientes = organizarListagem(clientes);        
            System.gc();

            this.provider.begin();
            try {
                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                setNotificacao("Preparando para gravar cliente eventual (Unificação)...", clientes.size());
                ClienteEventualIDStack ids = provider.getClienteEventualIDStack(iniciarEm);
                Map<Long, Integer> cnpjCadastrados = provider.eventual().getCnpjCadastrados();
                MultiMap<String, ClienteEventualAnteriorVO> anteriores = provider.eventual().getAnteriores();
                MultiMap<String, Void> contatos = provider.eventual().getContatosExistentes();
                //</editor-fold>
                
                boolean reiniciarID = opt.contains(OpcaoCliente.IMP_REINICIAR_NUMERACAO);

                setNotificacao("Gravando cliente eventual (Unificação)...", clientes.size());
                for (ClienteIMP imp: clientes) {
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
                         * Se o CNPJ/CPF é inválido, não importa o cliente, mas grava
                         * o cliente anterior.
                         */
                        if (String.valueOf(cnpj).length() < 8) {
                            anterior = converterClienteEventualAnterior(imp);
                            gravarClienteEventualAnterior(anterior);  
                            continue;
                        }                    

                        /**
                         * Se o cnpj já estiver cadastrado, grava o código anterior 
                         * relacionando com o id do cliente já cadastrado.
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
                        int id = ids.obterID(reiniciarID ? "A": imp.getId());

                        if (cnpj < 0) {
                            cnpj = id;
                        }

                        //Converte os dados.
                        ClienteEventualVO cliente = converterClienteEventual(imp);                    
                        cliente.setId(id);
                        cliente.setCnpj(cnpj);

                        anterior = converterClienteEventualAnterior(imp);
                        anterior.setCodigoAtual(cliente);

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

                    notificar();
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
    
    public ClienteEventualContatoVO converterContatoEventual(ClienteContatoIMP impCont) {
        ClienteEventualContatoVO cont = new ClienteEventualContatoVO();
        cont.setNome(impCont.getNome());
        cont.setTelefone(impCont.getTelefone());
        cont.setCelular(impCont.getCelular());
        cont.setEmail(impCont.getEmail());
        return cont;
    }

    public void gravarClientePreferencialContato(ClientePreferencialContatoVO contato) throws Exception {
        provider.preferencial().salvar(contato);
    }    
    
    public void gravarClienteEventualContato(ClienteEventualContatoVO contato) throws Exception {
        provider.eventual().salvar(contato);
    }    

    private void atualizarClientePreferencial(ClientePreferencialVO vo, Set<OpcaoCliente> opt) throws Exception {
        provider.atualizarClientePreferencial(vo, opt);
    }
}
