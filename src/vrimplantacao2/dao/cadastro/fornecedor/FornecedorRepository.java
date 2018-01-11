package vrimplantacao2.dao.cadastro.fornecedor;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorContatoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorPagamentoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.FornecedorPagamentoIMP;

/**
 *
 * @author Leandro
 */
public class FornecedorRepository {
    private FornecedorRepositoryProvider provider;

    public FornecedorRepository(FornecedorRepositoryProvider provider) {
        this.provider = provider;
    }

    public void salvar(List<FornecedorIMP> fornecedores) throws Exception {
        MultiMap<String, FornecedorIMP> filtrados = filtrar(fornecedores);
        fornecedores = null;
        System.gc();
        organizar(filtrados);
        
        try {
            provider.begin();
            
            MultiMap<String, FornecedorAnteriorVO> anteriores = provider.getAnteriores();
            Map<Long, FornecedorVO> cnpjExistentes = provider.getCnpjExistentes();
            FornecedorIDStack ids = provider.getIdsExistentes();
            MultiMap<String, Void> contatos = provider.getContatos();
            MultiMap<String, Void> pagamentos = provider.getPagamentos();
            
            provider.setStatus("Fornecedores - Gravando...");
            provider.setMaximum(filtrados.size());
            for (FornecedorIMP imp: filtrados.values()) {                
                FornecedorAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getImportId()
                );
                
                FornecedorVO vo;
                
                if (anterior == null) {
                    
                    vo = converter(imp);
                    
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
                    processarContatos(imp, vo, contatos);
                    
                    if (imp.getCondicaoPagamento() > 0) {
                        provider.gravarCondicaoPagamento(vo.getId(), imp.getCondicaoPagamento());
                    }

                    if (imp.getPrazoEntrega() > 0 || imp.getPrazoSeguranca() > 0 || imp.getPrazoVisita() > 0) {
                        provider.gravarPrazoFornecedor(vo.getId(), imp.getPrazoEntrega(), imp.getPrazoVisita(), imp.getPrazoSeguranca());
                    }
                }
                
                provider.next();
            }
            
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
            MultiMap<String, Void> contatos = provider.getContatos();
            MultiMap<String, Void> pagamentos = provider.getPagamentos();
            
            provider.setStatus("Fornecedores - Gravando...");
            provider.setMaximum(filtrados.size());
            
            if (opt.contains(OpcaoFornecedor.CNPJ_CPF)) {            
                provider.resetCnpjCpf();
            }
             
            Map<Long, FornecedorVO> cnpjExistentes = provider.getCnpjExistentes();
            
            for (FornecedorIMP imp: filtrados.values()) {                
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
                    
                    if (opt.contains(OpcaoFornecedor.TELEFONE)
                            || (opt.contains(OpcaoFornecedor.TIPO_INSCRICAO))
                            || (opt.contains(OpcaoFornecedor.RAZAO_SOCIAL))
                            || (opt.contains(OpcaoFornecedor.NOME_FANTASIA))
                            || (opt.contains(OpcaoFornecedor.ENDERECO))
                            || (opt.contains(OpcaoFornecedor.BAIRRO))
                            || (opt.contains(OpcaoFornecedor.SITUACAO_CADASTRO))
                            || (opt.contains(OpcaoFornecedor.TIPO_EMPRESA))
                            || (opt.contains(OpcaoFornecedor.CNPJ_CPF))
                            || (opt.contains(OpcaoFornecedor.BAIRRO))) {
                        atualizarFornecedor(vo, opt);
                    }
                    
                    if (opt.contains(OpcaoFornecedor.CONTATOS)) {
                        processarContatos(imp, vo, contatos);
                    }
                    
                    if (opt.contains(OpcaoFornecedor.CONDICAO_PAGAMENTO2)) {
                        processarPagamentos(imp, vo, pagamentos);
                    }
                    
                    if (opt.contains(OpcaoFornecedor.CONDICAO_PAGAMENTO)) {
                        if (imp.getCondicaoPagamento() > 0) {
                            provider.gravarCondicaoPagamento(vo.getId(), imp.getCondicaoPagamento());
                        }
                    }

                    if (opt.contains(OpcaoFornecedor.PRAZO_FORNECEDOR)) {
                        if (imp.getPrazoEntrega() > 0 || imp.getPrazoSeguranca() > 0 || imp.getPrazoVisita() > 0) {
                            provider.gravarPrazoFornecedor(vo.getId(), imp.getPrazoEntrega(), imp.getPrazoVisita(), imp.getPrazoSeguranca());
                        }
                    }                    
                }
                provider.next();
            }
            
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    public void unificar(List<FornecedorIMP> fornecedores) throws Exception {
        MultiMap<String, FornecedorIMP> filtrados = filtrar(fornecedores);
        fornecedores = null;
        System.gc();
        organizar(filtrados);
        
        try {
            provider.begin();
            
            MultiMap<String, FornecedorAnteriorVO> anteriores = provider.getAnteriores();
            Map<Long, FornecedorVO> cnpjExistentes = provider.getCnpjExistentes();
            FornecedorIDStack ids = provider.getIdsExistentes();
            MultiMap<String, Void> contatos = provider.getContatos();
            
            provider.setStatus("Fornecedores - Gravando...");
            provider.setMaximum(filtrados.size());
            for (FornecedorIMP imp: filtrados.values()) {
                //Localiza as referencias dos fornecedores (anteriores e por cnpj/cpf)
                FornecedorAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getImportId()
                );
                FornecedorVO fornecedorPorCnpj = cnpjExistentes.get(Utils.stringToLong(imp.getCnpj_cpf()));
                
                FornecedorVO vo = null;                
                if (anterior == null) {                    
                    vo = converter(imp); 
                    
                    if (fornecedorPorCnpj == null) {
                        int id = ids.obterID(imp.getImportId());

                        //Obtem um ID válido.
                        if (vo.getCnpj() < 0) {
                            vo.setCnpj(id);
                        }

                        vo.setId(id);
                        gravarFornecedor(vo);
                        cnpjExistentes.put(vo.getCnpj(), vo);
                    } else {
                        vo.setId(fornecedorPorCnpj.getId());
                    }

                    anterior = converterAnterior(imp);
                    anterior.setCodigoAtual(vo);
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
                    processarContatos(imp, vo, contatos);
                    
                    if (imp.getCondicaoPagamento() > 0) {
                        provider.gravarCondicaoPagamento(vo.getId(), imp.getCondicaoPagamento());
                    }

                    if (imp.getPrazoEntrega() > 0 || imp.getPrazoSeguranca() > 0 || imp.getPrazoVisita() > 0) {
                        provider.gravarPrazoFornecedor(vo.getId(), imp.getPrazoEntrega(), imp.getPrazoVisita(), imp.getPrazoSeguranca());
                    }
                }
                
                provider.next();
            }
            
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }
    
    public void processarContatos(FornecedorIMP imp, FornecedorVO vo, MultiMap<String, Void> contatos) throws Exception {
        for (FornecedorContatoIMP impCont: imp.getContatos().values()) {
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
                gravarFornecedorContato(contato);
                contatos.put(
                        null,
                        String.valueOf(contato.getFornecedor().getId()),
                        contato.getNome(),
                        contato.getTelefone(),
                        contato.getCelular(),
                        contato.getEmail()
                );
            }
        }
    }
    
    public void processarPagamentos(FornecedorIMP imp, FornecedorVO vo, MultiMap<String, Void> pagamentos) throws Exception {
        for (FornecedorPagamentoIMP impPag: imp.getPagamentos().values()) {
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
    
    public MultiMap<String, FornecedorIMP> filtrar(List<FornecedorIMP> fornecedores) throws Exception {
        MultiMap<String, FornecedorIMP> result = new MultiMap<>();
        
        for (FornecedorIMP imp: fornecedores) {
            result.put(
                imp, 
                imp.getImportSistema(), 
                imp.getImportLoja(), 
                imp.getImportId()
            );
        }
        
        return result;
    }
    
    public void organizar(MultiMap<String, FornecedorIMP> filtrados) {
        MultiMap<String, FornecedorIMP> idsValidos = new MultiMap<>(3);
        MultiMap<String, FornecedorIMP> idsInvalidos = new MultiMap<>(3);
        
        for (FornecedorIMP imp: filtrados.values()) {
            String[] chave = new String[] {
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
        for (FornecedorIMP imp: idsValidos.getSortedMap().values()) {
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }
        for (FornecedorIMP imp: idsInvalidos.getSortedMap().values()) {
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
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
        vo.setBloqueado(!imp.isAtivo());
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

    public void gravarFornecedor(FornecedorVO vo) throws Exception {
        provider.gravarFornecedor(vo);
    }

    public FornecedorAnteriorVO converterAnterior(FornecedorIMP imp) {
        FornecedorAnteriorVO ant = new FornecedorAnteriorVO();
        ant.setImportSistema(imp.getImportSistema());
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
    
    public void gravarFornecedorPagamento(FornecedorPagamentoVO pagamento)throws Exception {
        provider.gravarCondicaoPagamento(pagamento);
    }

    private void atualizarFornecedor(FornecedorVO vo, Set<OpcaoFornecedor> opt) throws Exception {
        provider.atualizarFornecedor(vo, opt);
    }
    
}
