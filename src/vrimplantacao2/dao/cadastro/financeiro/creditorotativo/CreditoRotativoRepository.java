package vrimplantacao2.dao.cadastro.financeiro.creditorotativo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.ClientePreferencialDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoVO;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoItemIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;

/**
 * Repositório para executar operações com o crédito rotativo;
 * @author Leandro
 */
public class CreditoRotativoRepository {
    
    private CreditoRotativoProvider provider;

    public CreditoRotativoRepository(CreditoRotativoProvider provider) {
        this.provider = provider;
    }   

    public void unificarCreditoRotativo(List<CreditoRotativoIMP> rotativo) throws Exception {
        provider.setStatus("Crédito Rotativo...(Unificação)...Carregando dados");
        provider.begin();
        try {
            Map<String, CreditoRotativoIMP> filtrados = filtrarRotativo(rotativo);
            rotativo.clear();
            System.gc();

            Map<String, CreditoRotativoAnteriorVO> anteriores = provider.getAnteriores();
            MultiMap<String, CreditoRotativoItemAnteriorVO> baixas = provider.getBaixasAnteriores();
            
            provider.setStatus("Crédito Rotativo...(Unificação)...Gravando", filtrados.size());
            for (CreditoRotativoIMP imp : filtrados.values()) {
                CreditoRotativoAnteriorVO anterior = anteriores.get(
                        imp.getId()
                );
                
                if (imp.getCnpjCliente().length() >= 9) {

                    int idCliente = new ClientePreferencialDAO().getId(Long.parseLong(imp.getCnpjCliente()));

                    if (anterior == null) {
                        anterior = converterRotativoAnterior(imp);
                        if (idCliente > 0) {                            
                            CreditoRotativoVO cred = converterRotativo(imp);
                            cred.setId_clientePreferencial(idCliente);
                            provider.gravarRotativo(cred);
                            anterior.setCodigoAtual(cred);
                        }
                        provider.gravarRotativoAnterior(anterior);
                        anteriores.put(
                                imp.getId(),
                                anterior
                        );
                    }
                    
                    //Gravando as baixas
                    for (CreditoRotativoItemIMP impParc : imp.getPagamentos()) {
                        CreditoRotativoItemAnteriorVO parcAnt = baixas.get(
                                provider.getSistema(),
                                provider.getLoja(),
                                imp.getId(),
                                impParc.getId()
                        );

                        if (parcAnt == null) {
                            parcAnt = converterCreditoRotativoItemAnterior(impParc);

                            CreditoRotativoItemVO item = converterCreditoRotativoItem(impParc);
                            item.setId_receberCreditoRotativo(anterior.getCodigoAtual().getId());
                            provider.gravarRotativoItem(item);
                            parcAnt.setCodigoAtual(item.getId());

                            provider.gravarRotativoItemAnterior(parcAnt);
                            baixas.put(
                                    parcAnt,
                                    provider.getSistema(),
                                    provider.getLoja(),
                                    imp.getId(),
                                    impParc.getId()
                            );
                        }
                    }
                    if (anterior.getCodigoAtual() != null) {
                        provider.verificarBaixado(anterior.getCodigoAtual().getId());
                    }
                }
                provider.setStatus();
            }
            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }
    
    public void importarCreditoRotativo(List<CreditoRotativoIMP> rotativo) throws Exception {
        provider.setStatus("Importando crédito rotativo...Carregando dados");
        provider.begin();
        try {
            Map<String, CreditoRotativoIMP> filtrados = filtrarRotativo(rotativo);
            rotativo.clear();
            System.gc();
            
            Map<String, CreditoRotativoAnteriorVO> anteriores = provider.getAnteriores();
            MultiMap<String, CreditoRotativoItemAnteriorVO> baixas = provider.getBaixasAnteriores();
            MultiMap<String, ClientePreferencialAnteriorVO> clientes = provider.getClientesAnteriores();
            
            provider.setStatus("Importando crédito rotativo...Gravando", filtrados.size());
            for (CreditoRotativoIMP imp: filtrados.values()) {
                CreditoRotativoAnteriorVO anterior = anteriores.get(
                        imp.getId()
                );
                ClientePreferencialAnteriorVO preferencial = clientes.get(
                        provider.getSistema(),
                        provider.getLoja(),
                        imp.getIdCliente()
                );
                if (anterior == null) {
                    anterior = converterRotativoAnterior(imp);
                    if (preferencial != null && preferencial.getCodigoAtual() != null) {
                        CreditoRotativoVO cred = converterRotativo(imp);
                        cred.setId_clientePreferencial(preferencial.getCodigoAtual().getId());
                        provider.gravarRotativo(cred);
                        anterior.setCodigoAtual(cred);
                    }
                    provider.gravarRotativoAnterior(anterior);
                    anteriores.put(
                            imp.getId(),
                            anterior
                    );
                }               
                
                if (anterior.getCodigoAtual() != null) {
                    //Gravando as baixas
                    for (CreditoRotativoItemIMP impParc: imp.getPagamentos()) {
                        CreditoRotativoItemAnteriorVO parcAnt = baixas.get(
                                provider.getSistema(),
                                provider.getLoja(),
                                imp.getId(),
                                impParc.getId()
                        );

                        if (parcAnt == null) {
                            parcAnt = converterCreditoRotativoItemAnterior(impParc);

                            CreditoRotativoItemVO item = converterCreditoRotativoItem(impParc);
                            item.setId_receberCreditoRotativo(anterior.getCodigoAtual().getId());
                            provider.gravarRotativoItem(item);
                            parcAnt.setCodigoAtual(item.getId());

                            provider.gravarRotativoItemAnterior(parcAnt);
                            baixas.put(
                                    parcAnt,
                                    provider.getSistema(),
                                    provider.getLoja(),
                                    imp.getId(),
                                    impParc.getId()
                            );
                        }
                    }                    
                }
                if (anterior.getCodigoAtual() != null) {
                    provider.verificarBaixado(anterior.getCodigoAtual().getId());
                }
                
                provider.setStatus();
            }
            
            
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    public Map<String, CreditoRotativoIMP> filtrarRotativo(List<CreditoRotativoIMP> rotativo) {
        Map<String, CreditoRotativoIMP> result = new LinkedHashMap<>();
        for (CreditoRotativoIMP imp: rotativo) {
            result.put(imp.getId(), imp);
            Map<String, CreditoRotativoItemIMP> pags = new LinkedHashMap<>();
            for (CreditoRotativoItemIMP pag: imp.getPagamentos()) {
                pags.put(pag.getId(), pag);
            }
            imp.getPagamentos().clear();
            imp.getPagamentos().addAll(pags.values());
        }
        return result;
    }

    public CreditoRotativoVO converterRotativo(CreditoRotativoIMP imp) {
        CreditoRotativoVO vo = new CreditoRotativoVO();
        vo.setId_clientePreferencial(Integer.parseInt(imp.getIdCliente()));
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDataVencimento(imp.getDataVencimento());
        vo.setEcf(Utils.stringToInt(imp.getEcf()));
        vo.setId_loja(provider.getLojaVR());
        vo.setNumeroCupom(Utils.stringToInt(imp.getNumeroCupom()));
        vo.setObservacao("IMPORTADO VR " + Utils.acertarTexto(imp.getObservacao()));
        vo.setParcela(imp.getParcela());
        vo.setValor(imp.getValor());
        vo.setValorJuros(imp.getJuros());
        vo.setValorMulta(imp.getMulta());
        return vo;
    }
    
    public CreditoRotativoAnteriorVO converterRotativoAnterior(CreditoRotativoIMP imp) {
        CreditoRotativoAnteriorVO ant = new CreditoRotativoAnteriorVO();
        ant.setSistema(provider.getSistema());
        ant.setLoja(provider.getLoja());
        ant.setId(imp.getId());
        ant.setIdCliente(imp.getIdCliente());
        ant.setValor(imp.getValor());
        ant.setVencimento(imp.getDataVencimento());
        return ant;
    }

    public CreditoRotativoItemAnteriorVO converterCreditoRotativoItemAnterior(CreditoRotativoItemIMP impParc) {
        CreditoRotativoItemAnteriorVO vo = new CreditoRotativoItemAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLoja());
        vo.setIdCreditoRotativo(impParc.getCreditoRotativo().getId());
        vo.setId(impParc.getId());
        vo.setDataPagamento(impParc.getDataPagamento());
        vo.setValor(impParc.getValor());
        vo.setValorMulta(impParc.getMulta());
        vo.setValorDesconto(impParc.getDesconto());
        return vo;
    }

    public CreditoRotativoItemVO converterCreditoRotativoItem(CreditoRotativoItemIMP impParc) {
        CreditoRotativoItemVO vo = new CreditoRotativoItemVO();
        
        vo.setDataPagamento(impParc.getDataPagamento());
        vo.setDatabaixa(impParc.getDataPagamento());
        vo.setId_loja(provider.getLojaVR());
        vo.setObservacao("IMPORTADO VR " + impParc.getObservacao());
        vo.setValor(impParc.getValor());
        vo.setValorDesconto(impParc.getDesconto());
        vo.setValorMulta(impParc.getMulta());
        vo.setValorTotal(impParc.getTotal());
        
        return vo;
    }

    public void salvarPagamentosAgrupados(List<CreditoRotativoPagamentoAgrupadoIMP> pags, OpcaoCreditoRotativo... opcoes) throws Exception {
        
        Set<OpcaoCreditoRotativo> opt = new HashSet<>(Arrays.asList(opcoes));
        
        provider.setStatus("Importando pagamentos (agrupados)...Carregando dados");
        
        Map<String, Double> somado = agruparPagamentos(pags);
        pags.clear();
        System.gc();
        
        MultiMap<String, CreditoRotativoAnteriorVO> rotativosAnteriores = provider.getTodoCreditoRotativoAnterior();
        MultiMap<String, CreditoRotativoItemAnteriorVO> baixasAnteriores = provider.getTodaBaixaAnterior();
        Map<Integer, Double> baixas = provider.getBaixas();
        
        System.gc();
                
        try {
            provider.begin();
            
            provider.setStatus("Importando pagamentos (agrupados)...Gravando", somado.size());            
            
            //Retorno TODOS os crédito importados de todas as lojas deste cliente.
            for (CreditoRotativoAnteriorVO rotativoAnterior: rotativosAnteriores.values()) {
                //Se o código anterior do rotativo tiver código atual, continua.
                if (rotativoAnterior.getCodigoAtual() != null) {
                    
                    CreditoRotativoVO rotativo = rotativoAnterior.getCodigoAtual();
                    Double pagoNaOrigem = somado.get(rotativoAnterior.getIdCliente());
                    Double totalBaixado = baixas.get(rotativo.getId());
                    
                    if (pagoNaOrigem != null) {
                    
                        CreditoRotativoItemAnteriorVO baixaAnterior = baixasAnteriores.get(
                                provider.getSistema(),
                                provider.getLoja(),
                                rotativoAnterior.getId(),
                                rotativoAnterior.getId()
                        );
                        //Se este pagamento não foi importado, executa a importação.
                        if (baixaAnterior == null) {                             

                            if (totalBaixado == null) {
                                totalBaixado = 0d;
                            }

                            Double totalABaixar = rotativo.getTotal() - totalBaixado;

                            //Se ainda houver valor para baixar, executa
                            if (totalABaixar > 0 && pagoNaOrigem > 0) {
                                double valorPagamento;
                                //Determina o valor da parcela e debita do total pago na origem.
                                if (pagoNaOrigem < totalABaixar) {
                                    valorPagamento = pagoNaOrigem;
                                    pagoNaOrigem = 0d;
                                } else {                                
                                    valorPagamento = totalABaixar;
                                    pagoNaOrigem = pagoNaOrigem - totalABaixar;
                                }
                                //Atualiza o valor na listagem.
                                somado.put(rotativoAnterior.getIdCliente(), pagoNaOrigem);

                                //Converte o pagamento do crédito rotativo.
                                CreditoRotativoItemVO pag = new CreditoRotativoItemVO();
                                pag.setId_receberCreditoRotativo(rotativo.getId());
                                pag.setValor(valorPagamento);
                                pag.setValorTotal(valorPagamento);
                                pag.setDatabaixa(rotativo.getDataVencimento());
                                pag.setDataPagamento(rotativo.getDataVencimento());
                                pag.setObservacao("IMPORTADO VR");
                                pag.setId_loja(rotativo.getId_loja());

                                //Grava o pagamento no banco.
                                provider.gravarRotativoItem(pag);

                                //Converte o código anterior.
                                baixaAnterior = new CreditoRotativoItemAnteriorVO();
                                baixaAnterior.setSistema(provider.getSistema());
                                baixaAnterior.setLoja(provider.getLoja());
                                baixaAnterior.setIdCreditoRotativo(rotativoAnterior.getId());
                                baixaAnterior.setId(rotativoAnterior.getId());
                                baixaAnterior.setCodigoAtual(pag.getId());
                                baixaAnterior.setDataPagamento(pag.getDataPagamento());
                                baixaAnterior.setValor(pag.getValor());

                                provider.gravarRotativoItemAnterior(baixaAnterior);

                                //Verifica e atualiza o crédito rotativo.
                                provider.verificarBaixado(pag.getId());

                                //Grava na listagem de pagamentos anteriores.
                                baixasAnteriores.put(
                                        baixaAnterior,
                                        provider.getSistema(),
                                        provider.getLoja(),
                                        baixaAnterior.getId(),
                                        baixaAnterior.getId()
                                );

                            }

                        }
                    }
                }
                
                provider.setStatus();

            }         
            
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
        
    }

    public Map<String, Double> agruparPagamentos(List<CreditoRotativoPagamentoAgrupadoIMP> pags) {
        Map<String, Double> result = new HashMap<>();
        for (CreditoRotativoPagamentoAgrupadoIMP imp: pags) {
            Double valor = result.get(imp.getIdCliente());
            if (valor != null) {
                valor += imp.getValor();
            } else {
                valor = imp.getValor();
            }
            result.put(imp.getIdCliente(), valor);
        }
        return result;
    }
    
    
}
