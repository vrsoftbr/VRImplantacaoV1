package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.financeiro.ContaPagarAnteriorTipo;
import vrimplantacao2.vo.cadastro.financeiro.ContaPagarAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarFornecedorParcelaVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarFornecedorVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVencimentoVO;
import vrimplantacao2.vo.cadastro.financeiro.SituacaoPagarFornecedorParcela;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.vo.enums.SituacaoPagarOutrasDespesas;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;

/**
 *
 * @author Leandro
 */
public class ContasPagarRepository {
    
    private static final SimpleDateFormat FORMATER = new SimpleDateFormat("yyyy-MM-dd");
    
    private final ContasPagarProvider provider;
    private boolean importarOutrasDespesas;

    public ContasPagarRepository(ContasPagarProvider provider) {
        this.provider = provider;        
    }

    public void salvar(List<ContaPagarIMP> contas, OpcaoContaPagar... opcoes) throws Exception {
        
        Set<OpcaoContaPagar> opt = new HashSet<>(Arrays.asList(opcoes));
        MultiMap<String, ContaPagarIMP> organizados = organizar(contas);
        provider.notificar("Contas à Pagar - Preparando a importação...");
        MultiMap<String, FornecedorAnteriorVO> fornecedores = provider.getFornecedores();
        MultiMap<String, ContaPagarAnteriorVO> anteriores = provider.getAnteriores();
        Set<Integer> bancosExistentes = provider.getBancosExistentes();
        
        provider.notificar("Contas à Pagar - Importando...", organizados.size());
        provider.begin();
        int fornecedorLoja = provider.getFornecedorLoja();
        try {
            int cont = 0;
            this.importarOutrasDespesas = opt.contains(OpcaoContaPagar.IMPORTAR_OUTRASDESPESAS);
            boolean importarSemFornecedor = opt.contains(OpcaoContaPagar.IMPORTAR_SEM_FORNECEDOR);            
            
            MultiMap<String, Void> pagamentos = provider.getPagamentos(importarOutrasDespesas);
            
            System.out.println(String.format("SISTEMA: %s; LOJA: %s;", provider.getSistema(), provider.getAgrupador()));
            System.out.println(String.format(" forn_ant: %d; ant: %d; pagamentos: %d; organizados: %d", fornecedores.size(), anteriores.size(), pagamentos.size(), organizados.size()));
            
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
                
                if (fornecedor == null && importarSemFornecedor) {
                    fornecedor = new FornecedorVO();
                    fornecedor.setId(fornecedorLoja);
                }
                
                if (fornecedor == null) {                    
                    provider.notificar();
                    continue;
                }

                //Se for uma conta nova
                if (anterior == null || anterior.getCodigoAtual() == null) {
                    if (opt.contains(OpcaoContaPagar.NOVOS)) {
                        boolean anteriorExistente = anterior != null;
                        
                        if (!anteriorExistente) {
                            anterior = converterAnterior(imp);
                        }
                        
                        if (importarOutrasDespesas) {
                            anterior.setTipo(ContaPagarAnteriorTipo.OUTRASDESPESAS);
                            System.out.println("OUTRASDESPESAS");
                        } else {
                            System.out.println("PAGARFORNECEDOR");
                            anterior.setTipo(ContaPagarAnteriorTipo.PAGARFORNECEDOR);
                        }
                        
                        //Se o fornecedor existir no cadastro
                        if (fornecedor != null) {
                            if (importarOutrasDespesas) {
                                //Gravando o outras despesas
                                PagarOutrasDespesasVO vo = converterEmOutrasDispesas(imp);
                                vo.setIdFornecedor(fornecedor.getId());
                                provider.gravar(vo);
                                //Set código anterior
                                anterior.setCodigoAtual(vo.getId());
                            } else {
                                //Converte e grava o pagar fornecedor
                                PagarFornecedorVO vo = converterPagarFornecedor(imp);
                                vo.setId_fornecedor(fornecedor.getId());
                                provider.gravar(vo);
                                anterior.setCodigoAtual(vo.getId());
                            }
                        }
                        //Grava o código anterior e o registra na listagem.
                        if (!anteriorExistente) {
                            provider.gravarAnterior(anterior);
                        } else {
                            provider.atualizarAnterior(anterior);
                        }
                        anteriores.put(
                                anterior,
                                anterior.getSistema(),
                                anterior.getAgrupador(),
                                anterior.getId()
                        );
                    }
                }
                
                for (ContaPagarVencimentoIMP cp: imp.getVencimentos()) {
                    KeyList<String> keys = getKeys(
                            anterior.getCodigoAtual(),
                            cp.getVencimento(),
                            cp.getValor()
                    );
                    if (!pagamentos.containsKey(keys)) {
                        if (importarOutrasDespesas) {
                            //Converte e grava a parcela
                            PagarOutrasDespesasVencimentoVO parc = converterEmOutrasDespesasVencimento(cp);
                            parc.setIdPagarOutrasDespesas(anterior.getCodigoAtual());
                            provider.gravarVencimento(parc);
                        } else {
                            //Converte e grava a parcela do pagar fornecedor
                            PagarFornecedorParcelaVO parc = converterPagarFornecedorParcela(cp);
                            parc.setId_pagarfornecedor(anterior.getCodigoAtual());
                            provider.gravarVencimento(parc);
                        }

                        //Inclui na listagem de parcelas (UK)
                        pagamentos.put(null, keys);
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
    
    private KeyList<String> getKeys(int id, Date vencimento, double valor) {
        return new KeyList<> (
                String.valueOf(id),
                FORMATER.format(vencimento),
                String.format("%.2f", valor)
        );
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
        vo.setSituacaoPagarOutrasDespesas(SituacaoPagarOutrasDespesas.NAO_FINALIZADO);
        vo.setIdTipoEntrada(imp.getIdTipoEntradaVR() == null ? 210 : imp.getIdTipoEntradaVR());
        vo.setValor(imp.getValor());
        
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

    public PagarOutrasDespesasVO atualizarConta(int id, ContaPagarIMP imp, int idFornecedor, Set<OpcaoContaPagar> opt) throws Exception {
        
        PagarOutrasDespesasVO vo = converterEmOutrasDispesas(imp);
        vo.setId(id);
        vo.setIdFornecedor(idFornecedor);
        provider.atualizar(vo, opt);
        
        return vo;
        
    }

    private PagarFornecedorVO converterPagarFornecedor(ContaPagarIMP imp) {
        PagarFornecedorVO vo = new PagarFornecedorVO();
        
        vo.setId_loja(provider.getLojaVR());
        vo.setId_tipoentrada(imp.getIdTipoEntradaVR() == null ? 210 : imp.getIdTipoEntradaVR());
        vo.setDataemissao(imp.getDataEmissao());
        vo.setDataentrada(imp.getDataEntrada() == null ? imp.getDataEmissao() : imp.getDataEntrada());
        vo.setNumerodocumento(Utils.stringToInt(imp.getNumeroDocumento()));
        
        if (imp.getValor() == 0) {

            double total = 0;
            for (ContaPagarVencimentoIMP vc : imp.getVencimentos()) {
                total += vc.getValor();
            }

            vo.setValor(total);
        } else {
            vo.setValor(imp.getValor());
        }
        
        return vo;        
    }

    private PagarFornecedorParcelaVO converterPagarFornecedorParcela(ContaPagarVencimentoIMP cp) {
        PagarFornecedorParcelaVO vo = new PagarFornecedorParcelaVO();
        
        vo.setAgencia(cp.getAgencia());
        vo.setConferido(cp.isConferido());
        vo.setConta(cp.getConta());
        vo.setDatapagamento(cp.getDataPagamento());
        vo.setDatavencimento(cp.getVencimento());
        vo.setId_banco(cp.getId_banco());
        vo.setId_tipopagamento(cp.getTipoPagamento().getId());
        vo.setNumerocheque(cp.getNumerocheque());
        vo.setNumeroparcela(cp.getNumeroParcela() == 0 ? 1 : cp.getNumeroParcela());
        vo.setObservacao("IMPORTADO VR" + (cp.getObservacao() == null ? "" : " " + cp.getObservacao()));
        vo.setSituacaopagarfornecedorparcela(cp.isPago() ? SituacaoPagarFornecedorParcela.PAGO : SituacaoPagarFornecedorParcela.ABERTO);
        vo.setValor(cp.getValor());
        vo.setDatahoraalteracao(new Date());
        
        return vo;
    }

    private PagarOutrasDespesasVencimentoVO converterEmOutrasDespesasVencimento(ContaPagarVencimentoIMP cp) {
        PagarOutrasDespesasVencimentoVO venc = new PagarOutrasDespesasVencimentoVO();
        
        venc.setDataVencimento(cp.getVencimento());
        venc.setValor(cp.getValor());
        
        return venc;
    }
}
