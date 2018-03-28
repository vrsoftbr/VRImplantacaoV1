package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.json.JSONObject;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.TipoLocalCobranca;
import vrimplantacao2.vo.enums.TipoReceita;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ContaReceberPagamentoIMP;

/**
 *
 * @author Leandro
 */
public class OutraReceitaRepository {

    private static final Logger LOG = Logger.getLogger(OutraReceitaRepository.class.getName());
    
    private final OutraReceitaRepositoryProvider provider;

    public OutraReceitaRepository(OutraReceitaRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importar(List<ContaReceberIMP> contas, Set<OpcaoContaReceber> opt) throws Exception {
        
        provider.setStatus("Contas a Receber...Carregando dados");        
        
        Map<String, Integer> fornecedores = provider.getFornecedores();
        LOG.fine("Fornecedores carregados: " + fornecedores.size());
        Map<String, Integer> eventuais = provider.getEventuais();
        LOG.fine("Clientes Eventuais carregados: " + fornecedores.size());
        Map<String, ContaReceberAnteriorVO> anteriores = provider.getAnteriores();
        LOG.fine("Anteriores carregados: " + anteriores.size());
        MultiMap<String, ContaReceberItemAnteriorVO> itemAnteriores = provider.getItemAnteriores();
        LOG.fine("Itens anteriores carregados: " + anteriores.size());
        
        provider.setStatus("Contas a Receber...Gravando...", contas.size());
        
        
        try {
            provider.begin();
            
            for (ContaReceberIMP imp: contas) {
                
                JSONObject log = new JSONObject();
                try {
                    ContaReceberAnteriorVO anterior = anteriores.get(imp.getId());
                    
                    JSONObject logImp = new JSONObject();
                    logImp.put("id", imp.getId());
                    logImp.put("emissao", imp.getDataEmissao());
                    logImp.put("vencimento", imp.getDataVencimento());
                    logImp.put("valor", imp.getValor());
                    log.put("imp", logImp);

                    if (anterior == null) {                        
                        anterior = converterAnterior(imp);
                        OutraReceitaVO vo = converterOutraReceita(imp);
                        
                        //Localiza o cliente eventual no VR.
                        if (imp.getIdClienteEventual() != null) {
                            vo.setIdClienteEventual(eventuais.get(imp.getIdClienteEventual()));
                            if (vo.getIdClienteEventual() == 0) {
                                LOG.severe("Cliente Eventual " + imp.getIdClienteEventual() + " não localizado!");
                                throw new Exception("Cliente Eventual " + imp.getIdClienteEventual() + " não localizado!");
                            }
                        }
                        
                        //Localiza o fornecedor no VR.
                        if (imp.getIdFornecedor()!= null) {
                            vo.setIdFornecedor(fornecedores.get(imp.getIdFornecedor()));
                            if (vo.getIdFornecedor() == 0) {
                                LOG.severe("Fornecedor " + imp.getIdFornecedor()+ " não localizado!");
                                throw new Exception("Fornecedor " + imp.getIdFornecedor()+ " não localizado!");
                            }
                        }
                        
                        JSONObject logVo = new JSONObject();
                        logVo.put("emissao", vo.getDataEmissao());
                        logVo.put("vencimento", vo.getDataVencimento());
                        logVo.put("valor", vo.getValor());
                        logVo.put("fornecedor", vo.getIdFornecedor());
                        logVo.put("cli_eventual", vo.getIdClienteEventual());
                        log.put("convertido", logVo);
                        
                        provider.gravar(vo);
                        log.put("receita_gravada", true);
                        anterior.setCodigoAtual(vo.getId());
                        provider.gravar(anterior);      
                        log.put("anterior_gravado", true);
                        anteriores.put(anterior.getId(), anterior);
                    }

                    JSONObject pagLog = new JSONObject();
                    log.put("pagamentos", pagLog);
                    if (anterior.getCodigoAtual() > 0) {
                        for (ContaReceberPagamentoIMP impPag: imp.getPagamentos()) {
                            
                            ContaReceberItemAnteriorVO antItem = itemAnteriores.get(imp.getId(), impPag.getId());
                            
                            if (antItem == null) {                                
                                OutraReceitaItemVO item = converterPagamento(impPag);
                                item.setIdReceberOutrasReceitas(anterior.getCodigoAtual());
                                provider.gravar(item);
                                pagLog.put("tipo", item.getTipoRecebimento().toString());
                                pagLog.put("pagamento", item.getDataPagamento());
                                pagLog.put("valor", item.getValor());
                                
                                antItem = converterItemAnterior(impPag);
                                antItem.setIdContaReceber(imp.getId());
                                antItem.setCodigoAtual(item.getId());
                                provider.gravar(antItem);
                                itemAnteriores.put(antItem, antItem.getIdContaReceber(), antItem.getId());
                            }
                            
                        }
                    }
                    log.put("pagamentos_importados", true);

                    provider.setStatus();
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    LOG.finest(log.toString());
                }
            }
            
            provider.commit();            
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }

    public ContaReceberAnteriorVO converterAnterior(ContaReceberIMP imp) {
        ContaReceberAnteriorVO vo = new ContaReceberAnteriorVO();
        
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLoja());
        vo.setId(imp.getId());
        vo.setData(imp.getDataEmissao());
        vo.setIdFornecedor(imp.getIdFornecedor());
        vo.setIdClienteEventual(imp.getIdClienteEventual());
        vo.setVencimento(imp.getDataVencimento());
        vo.setValor(imp.getValor());
        
        return vo;
    }

    public OutraReceitaVO converterOutraReceita(ContaReceberIMP imp) {
        OutraReceitaVO vo = new OutraReceitaVO();
        
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDataHoraAlteracao(new Timestamp(new Date().getTime()));
        vo.setDataVencimento(imp.getDataVencimento());
        vo.setDataExportacao(null);
        vo.setExportado(false);
        vo.setIdLoja(provider.getLojaVR());
        vo.setObservacao(imp.getObservacao());
        vo.setSituacao(SituacaoReceberOutrasReceitas.ABERTO);
        vo.setTipoLocalCobranca(TipoLocalCobranca.CARTEIRA);
        vo.setTipoReceita(TipoReceita.CR_OUTRAS_UNIDADES);
        vo.setValor(imp.getValor());
        
        return vo;
    }

    public OutraReceitaItemVO converterPagamento(ContaReceberPagamentoIMP imp) {
        OutraReceitaItemVO vo = new OutraReceitaItemVO();
        
        vo.setValor(imp.getValor());
        vo.setValorDesconto(imp.getValorDesconto());
        vo.setValorJuros(imp.getValorJuros());
        vo.setValorMulta(imp.getValorMulta());
        vo.setDataPagamento(imp.getDataPagamento());
        vo.setObservacao(imp.getObservacao());
        vo.setIdBanco(imp.getBanco());
        vo.setAgencia(imp.getAgencia());
        vo.setConta(imp.getConta());
        vo.setTipoRecebimento(imp.getTipoRecebimento());
        
        return vo;
    }

    private ContaReceberItemAnteriorVO converterItemAnterior(ContaReceberPagamentoIMP impPag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
