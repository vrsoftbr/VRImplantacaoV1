package vrimplantacao2.dao.cadastro.promocao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoAnteriorVO;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoVO;
import vrimplantacao2.vo.importacao.PromocaoIMP;
import vrimplantacao2_5.controller.migracao.LogController;

/**
 * Repositório do promocao
 *
 * @author Michael
 */
public class PromocaoRepository {

    private final PromocaoRepositoryProvider provider;
    private final LogController logController;

    public PromocaoRepository(PromocaoRepositoryProvider provider) {
        this.provider = provider;
        this.logController = new LogController();
    }

    public void salvar(List<PromocaoIMP> promocoes) throws Exception {
        Map<String, PromocaoIMP> filtrados = filtrar(promocoes);
        MultiMap<String, PromocaoAnteriorVO> anteriores = provider.getAnteriores();
        try {

            provider.setStatus("Gravando promocoes...");
            provider.setMaximo(promocoes.size());
            for (PromocaoIMP imp : promocoes) {
                PromocaoAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getId()
                );

                if (anterior == null) {
                    anterior = converterPromocarAnteriorVO(imp);
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

            anteriores = provider.getAnteriores();
            provider.setStatus("Gravando cabeçalho promocoes...");
            provider.setMaximo(filtrados.size());
            for (PromocaoIMP imp : filtrados.values()) {
                PromocaoAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getId()
                );

                if (anterior != null) {
                    PromocaoVO promo = converterPromocao(imp);
                    anterior = converterPromocarAnteriorVO(imp);
                    anterior.setCodigoAtual(promo);

                    gravarPromocao(promo);
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
                        imp.getId()
                );

                if (anterior != null) {
                    anterior = converterPromocarAnteriorVO(imp);
                    gravarPromocaoItens(anterior);
                    anteriores.put(
                            anterior,
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
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
            List<PromocaoIMP> filtraFinalizacoes = provider.getFinalizadora();
            anteriores = provider.getAnteriores();

            provider.setStatus("Finalizando promocoes...");
            provider.setMaximo(filtraFinalizacoes.size());
            for (PromocaoIMP imp : filtraFinalizacoes) {
                PromocaoAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getId()
                );

                if (anterior == null) {
                    anterior = converterPromocaoFinalizaVO(imp);
                    gravarPromocaoFinalizadora(anterior);
                    anteriores.put(
                            anterior,
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
                    );
                }
                provider.next();
            }
            provider.commit();
            promocoes.clear();
            System.gc();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    public Map<String, PromocaoIMP> filtrar(List<PromocaoIMP> promocoes) throws Exception {
        Map<String, PromocaoIMP> result = new LinkedHashMap<>();
        for (PromocaoIMP imp : promocoes) {
            result.put(imp.getId_promocao(), imp);
        }
        return result;
    }

    public PromocaoVO converterPromocao(PromocaoIMP imp) {
        PromocaoVO vo = new PromocaoVO();
        vo.setId(imp.getId_promocao());
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

    public PromocaoAnteriorVO converterPromocarAnteriorVO(PromocaoIMP imp) throws Exception {
        PromocaoService promocaoService = new PromocaoService();
        int idConexao = promocaoService.existeConexaoMigrada(this.provider.getIdConexao(), this.provider.getSistema());
        PromocaoAnteriorVO vo = new PromocaoAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setIdConexao(idConexao);
        vo.setId_promocao(imp.getId_promocao());
        vo.setDataInicio(imp.getDataInicio());
        vo.setDataTermino(imp.getDataTermino());
        vo.setDescricao(imp.getDescricao());
        vo.setEan(imp.getEan());
        vo.setId_produto(imp.getId_produto());
        vo.setDescricaoCompleta(imp.getDescricaoCompleta());
        vo.setQuantidade(imp.getQuantidade());
        vo.setPaga(imp.getPaga());

        return vo;
    }

    private PromocaoAnteriorVO converterPromocaoFinalizaVO(PromocaoIMP imp) throws Exception {
        PromocaoService promocaoService = new PromocaoService();
        int idConexao = promocaoService.existeConexaoMigrada(this.provider.getLojaVR(), this.provider.getSistema());
        PromocaoAnteriorVO vo = new PromocaoAnteriorVO();
        vo.setId_promocao(imp.getId_promocao());
        vo.setId_finalizadora(imp.getId_finalizadora());
        vo.setLoja(Integer.toString(idConexao));

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
}
