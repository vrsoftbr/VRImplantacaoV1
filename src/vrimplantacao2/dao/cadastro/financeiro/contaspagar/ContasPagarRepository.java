package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.financeiro.ContaPagarAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVencimentoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.vo.enums.SituacaoPagarOutrasDespesas;
import vrimplantacao2.vo.enums.TipoEntrada;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;

/**
 *
 * @author Leandro
 */
public class ContasPagarRepository {
    
    private static final SimpleDateFormat FORMATER = new SimpleDateFormat("yyyy-MM-dd");
    
    private final ContasPagarProvider provider;

    public ContasPagarRepository(ContasPagarProvider provider) {
        this.provider = provider;        
    }

    public void salvar(List<ContaPagarIMP> contas, OpcaoContaPagar... opcoes) throws Exception {
        Set<OpcaoContaPagar> opt = new HashSet<>(Arrays.asList(opcoes));
        MultiMap<String, ContaPagarIMP> organizados = organizar(contas);
        provider.notificar("Contas à Pagar - Preparando a importação...");
        MultiMap<String, FornecedorAnteriorVO> fornecedores = provider.getFornecedores();
        MultiMap<String, ContaPagarAnteriorVO> anteriores = provider.getAnteriores();
        MultiMap<String, Void> pagamentos = provider.getPagamentos();
        
        System.out.println(String.format("SISTEMA: %s; LOJA: %s;", provider.getSistema(), provider.getAgrupador()));
        System.out.println(String.format(" forn_ant: %d; ant: %d; pagamentos: %d; organizados: %d", fornecedores.size(), anteriores.size(), pagamentos.size(), organizados.size()));
        
        provider.notificar("Contas à Pagar - Importando...", organizados.size());
        provider.begin();
        int fornecedorLoja = provider.getFornecedorLoja();
        try {
            int cont = 0;
            for (ContaPagarIMP imp: organizados.values()) {            
                ContaPagarAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getAgrupador(),
                        imp.getId()
                );
                FornecedorVO fornecedor = null;
                {
                    FornecedorAnteriorVO fornecedorAnterior = fornecedores.get(
                            provider.getSistema(),
                            provider.getAgrupador(),
                            imp.getIdFornecedor()
                    );
                    if (fornecedorAnterior != null) {
                        if (fornecedorAnterior.getCodigoAtual() != null) {
                            fornecedor = fornecedorAnterior.getCodigoAtual();
                        }
                    } else {
                        System.out.println(String.format("FORN. NAO ENCONTRADO: %s %s %s", 
                                provider.getSistema(),
                                provider.getAgrupador(),
                                imp.getIdFornecedor()
                                ));
                        cont++;
                    }
                }
                
                if (fornecedor == null && opt.contains(OpcaoContaPagar.IMPORTAR_SEM_FORNECEDOR)) {
                    fornecedor = new FornecedorVO();
                    fornecedor.setId(fornecedorLoja);
                }

                //Se for uma conta nova
                if (anterior == null) {
                    if (opt.contains(OpcaoContaPagar.NOVOS)) {
                        anterior = converterAnterior(imp);
                        //Se o fornecedor existir no cadastro
                        if (fornecedor != null) {
                            PagarOutrasDespesasVO vo = gravarNovaConta(imp, fornecedor.getId());
                            gravarVencimentos(vo, pagamentos);
                            anterior.setCodigoAtual(vo);
                        }
                        //Grava o código anterior e o registra na listagem.
                        provider.gravarAnterior(anterior);
                        anteriores.put(
                                anterior,
                                anterior.getSistema(),
                                anterior.getAgrupador(),
                                anterior.getId()
                        );
                    }
                } else {
                    //Se já estiver cadastrado, atualiza as informações da despesa existente.
                    if (anterior.getCodigoAtual() != null) {
                        if (fornecedor != null) {
                            PagarOutrasDespesasVO vo = atualizarConta(
                                    anterior.getCodigoAtual().getId(),
                                    imp, 
                                    fornecedor.getId(), 
                                    opt
                            );
                            gravarVencimentos(vo, pagamentos);
                            anterior = converterAnterior(imp);
                            anterior.setCodigoAtual(vo);
                            provider.atualizarAnterior(anterior);
                        }
                    } else {
                        if (opt.contains(OpcaoContaPagar.NOVOS)) {
                            if (fornecedor != null) {
                                PagarOutrasDespesasVO vo = gravarNovaConta(imp, fornecedor.getId());
                                gravarVencimentos(vo, pagamentos);
                                anterior.setCodigoAtual(vo);
                                provider.atualizarAnterior(anterior);
                                anteriores.put(
                                        anterior,
                                        anterior.getSistema(),
                                        anterior.getAgrupador(),
                                        anterior.getId()
                                );
                            }
                        }
                    }
                }
                provider.notificar();
            }
            System.out.println("Contagem: " + cont);
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
        
    }

    private MultiMap<String, ContaPagarIMP> organizar(List<ContaPagarIMP> pagamentos) throws Exception {
        MultiMap<String, ContaPagarIMP> result = new MultiMap<>();
        
        for (ContaPagarIMP imp: pagamentos) {
            result.put(imp, imp.getId());
        }
        
        pagamentos.clear();
        System.gc();
        
        result = result.getSortedMap();
        System.gc();
        
        return result;
    }

    public PagarOutrasDespesasVO converterEmOutrasDispesas(ContaPagarIMP imp) {
        PagarOutrasDespesasVO vo = new PagarOutrasDespesasVO();
        
        vo.setId(-1);
        vo.setIdFornecedor(-1);
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDataEntrada(imp.getDataEntrada());
        vo.setDataHoraAlteracao(imp.getDataHoraAlteracao());
        vo.setId_loja(provider.getLojaVR());
        vo.setId_tipopiscofins(-1);
        vo.setNumeroDocumento(Utils.stringToInt(imp.getNumeroDocumento()));
        vo.setObservacao("IMPORTADO VR" + (imp.getObservacao() != null ? " " + imp.getObservacao() : ""));
        vo.setSituacaoPagarOutrasDespesas(imp.isFinalizada() ? SituacaoPagarOutrasDespesas.FINALIZADO : SituacaoPagarOutrasDespesas.NAO_FINALIZADO);
        vo.setTipoEntrada(TipoEntrada.OUTRAS);
        vo.setValor(imp.getValor());
        
        for (ContaPagarVencimentoIMP venc: imp.getVencimentos()) {
            vo.addVencimento(venc.getVencimento(), venc.getValor());
        }
        
        return vo;
    }

    public ContaPagarAnteriorVO converterAnterior(ContaPagarIMP imp) {
        ContaPagarAnteriorVO vo = new ContaPagarAnteriorVO();
        
        vo.setSistema(provider.getSistema());
        vo.setAgrupador(provider.getAgrupador());
        vo.setId(imp.getId());
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDocumento(imp.getNumeroDocumento());
        vo.setId_fornecedor(imp.getIdFornecedor());
        vo.setValor(imp.getValor());
        
        return vo;
    }

    public void gravarVencimentos(PagarOutrasDespesasVO vo, MultiMap<String, Void> parcelas) throws Exception {
        for (PagarOutrasDespesasVencimentoVO vc: vo.getVencimentos()) {
            KeyList<String> keys = new KeyList<> (
                    String.valueOf(vo.getId()),
                    FORMATER.format(vc.getDataVencimento()),
                    String.format("%.2f", vc.getValor())
            );
            if (!parcelas.containsKey(keys)) {
                provider.gravarVencimento(vc);
                parcelas.put(null, keys);
            }
        }
    }

    public PagarOutrasDespesasVO gravarNovaConta(ContaPagarIMP imp, int idFornecedor) throws Exception {
        
        PagarOutrasDespesasVO vo = converterEmOutrasDispesas(imp);
        vo.setIdFornecedor(idFornecedor);
        provider.gravar(vo);

        return vo;
    }

    public PagarOutrasDespesasVO atualizarConta(int id, ContaPagarIMP imp, int idFornecedor, Set<OpcaoContaPagar> opt) throws Exception {
        
        PagarOutrasDespesasVO vo = converterEmOutrasDispesas(imp);
        vo.setId(id);
        vo.setIdFornecedor(idFornecedor);
        provider.atualizar(vo, opt);
        
        return vo;
        
    }
}
