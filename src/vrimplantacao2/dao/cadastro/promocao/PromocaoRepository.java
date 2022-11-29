/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.promocao;

import java.util.List;
import vrimplantacao2.dao.cadastro.promocao.PromocaoRepositoryProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoAnteriorVO;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoVO;
import vrimplantacao2.vo.importacao.PromocaoIMP;

/**
 * Repositório do promocao
 *
 * @author Michael
 */
public class PromocaoRepository {

    private final PromocaoRepositoryProvider provider;

    public PromocaoRepository(PromocaoRepositoryProvider provider) {
        this.provider = provider;
    }

    public void salvar(List<PromocaoIMP> promocoes) throws Exception {

            MultiMap<String, PromocaoAnteriorVO> anteriores = provider.getAnteriores();
            try {
                provider.setStatus("Eliminando cadastros anteriores.");
                limparCodantPromocao(provider.getLojaOrigem(), provider.getSistema());
                provider.setStatus("Gravando promocoes...");
                provider.setMaximo(promocoes.size());
                for (PromocaoIMP imp : promocoes) {
                    PromocaoAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
                    );

                    if (anterior == null) {
                        anterior = converterPromocaoAnteriorVO(imp);
                        gravarPromocaoAnterior(anterior);
                        anteriores.put(
                                anterior,
                                provider.getSistema(),
                                provider.getLojaOrigem(),
                                imp.getId_promocao()
                        );
                    }
                    provider.next();
                }

                provider.commit();
            } catch (Exception e) {
                provider.rollback();
                throw e;
            }

            try {
                provider.setStatus("Eliminando cadastros de promoções anteriores.");
                limparPromocao(provider.getLojaOrigem(), provider.getSistema());
                List<PromocaoIMP> filtrarFinalizadoresCabecalhoPromocao = provider.getFinalizadora();
                anteriores = provider.getAnteriores();
                provider.setStatus("Gravando tipos de pagamentos por promoção");
                provider.setMaximo(filtrarFinalizadoresCabecalhoPromocao.size());
                for (PromocaoIMP imp : filtrarFinalizadoresCabecalhoPromocao) {
                    PromocaoAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId_produto()
                    );

                    if (anterior == null) {
                        PromocaoVO promo = converterPromocao(imp);
                        anterior = converterPromocaoAnteriorVO(imp);
                        anterior.setCodigoAtual(promo);

                        gravarPromocao(promo);
                        anterior = converterPromocaoAnteriorVO(imp);
                        gravarPromocaoFinalizadora(anterior);
                        anteriores.put(
                                anterior,
                                provider.getSistema(),
                                provider.getLojaOrigem(),
                                imp.getId_promocao()
                        );
                    }
                    provider.next();
                }

                provider.commit();
            } catch (Exception e) {
                provider.rollback();
                throw e;
            }

            try {
                List<PromocaoIMP> filtraItens = provider.getItens();
                anteriores = provider.getAnteriores();

                provider.setStatus("Gravando itens promocoes...");
                provider.setMaximo(filtraItens.size());
                for (PromocaoIMP imp : filtraItens) {
                    PromocaoAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId_produto()
                    );

                    if (anterior == null) {
                        anterior = converterPromocaoAnteriorVO(imp);
                        gravarPromocaoItens(anterior);
                        anteriores.put(
                                anterior,
                                provider.getSistema(),
                                provider.getLojaOrigem(),
                                imp.getId_promocao()
                        );
                    }
                    provider.next();
                }
                provider.commit();
            } catch (Exception e) {
                provider.rollback();
                throw e;
            }
            promocoes.clear();
            System.gc();
    }

    public PromocaoVO converterPromocao(PromocaoIMP imp) {
        PromocaoVO vo = new PromocaoVO();
        if (imp.getId_promocao() == null){
            vo.setId(imp.getId());
        } else vo.setId(imp.getId_promocao());        
        vo.setIdLoja(provider.getLojaVR());
        vo.setDescricao(imp.getDescricao());
        vo.setDataInicio(imp.getDataInicio());
        vo.setDataTermino(imp.getDataTermino());
        vo.setControle(imp.getId_promocao());
        vo.setPontuacao(imp.getPontuacao());
        vo.setQuantidade(imp.getQuantidade());
        vo.setIdTipopromocao(imp.getIdTipopromocao());
        vo.setValorPaga(imp.getPaga());
        return vo;
    }

    public PromocaoAnteriorVO converterPromocaoAnteriorVO(PromocaoIMP imp) throws Exception {
        PromocaoAnteriorVO vo = new PromocaoAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        if (imp.getId_promocao() == null){
            vo.setId(imp.getId());
            vo.setId_promocao(imp.getId());
        } else vo.setId_promocao(imp.getId_promocao());
        vo.setDataInicio(imp.getDataInicio());
        vo.setDataTermino(imp.getDataTermino());
        vo.setDescricao(imp.getDescricao());
        vo.setEan(imp.getEan());
        vo.setId_produto(imp.getId_produto());
        vo.setDescricaoCompleta(imp.getDescricaoCompleta());
        vo.setQuantidade(imp.getQuantidade());
        vo.setPaga(imp.getPaga());
        vo.setId_finalizadora(imp.getId_finalizadora());

        return vo;
    }

    public void gravarPromocao(PromocaoVO promo) throws Exception {
        provider.gravarPromocao(promo);
    }

    public void gravarPromocaoAnterior(PromocaoAnteriorVO anterior) throws Exception {
        provider.gravarPromocaoAnterior(anterior);
    }

    public void getPromocaoItens() throws Exception {
        provider.getPromocaoItens();
    }

    private void gravarPromocaoItens(PromocaoAnteriorVO anterior) throws Exception {
        provider.gravarPromocaoItens(anterior);
    }

    private void gravarPromocaoFinalizadora(PromocaoAnteriorVO finalizadora) throws Exception {
        provider.gravarPromocaoFinalizadora(finalizadora);
    }

    private void limparCodantPromocao(String lojaOrigem, String sistema) throws Exception {
        provider.limparCodantPromocao(lojaOrigem, sistema);
    }

    private void limparPromocao(String lojaOrigem, String sistema) throws Exception {
        provider.limparPromocao(lojaOrigem, sistema);
    }
}