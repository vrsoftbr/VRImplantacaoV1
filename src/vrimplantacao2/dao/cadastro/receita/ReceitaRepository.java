/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.receita;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.cadastro.receita.ReceitaVO;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaAnteriorVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaItemVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaLojaVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaProdutoVO;
import vrimplantacao2.vo.importacao.ReceitaIMP;

/**
 *
 * @author lucasrafael
 */
public class ReceitaRepository {

    private static final Logger LOG = Logger.getLogger(ReceitaBalancaRepository.class.getName());
    private final ReceitaRepositoryProvider provider;

    public ReceitaRepository(ReceitaRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importar(List<ReceitaIMP> receita) throws Exception {
        provider.setMessage("Receita...Carregando dados...");

        int iniciarEm = 1;
        ReceitaIDStack ids = provider.getReceitaIDStack(iniciarEm);
        Map<String, Integer> produtos = provider.getProdutos();
        Map<String, ReceitaAnteriorVO> anteriores = provider.getAnteriores();
        IDStack idsVagos = provider.getIdsVagos();
        MultiMap<Integer, Void> receitaItem = provider.getReceitaItem();
        MultiMap<Integer, Void> receitaProduto = provider.getReceitaProduto();
        MultiMap<Integer, Void> receitaLoja = provider.getReceitaLoja();
        ProdutoAnteriorDAO prodAntDAO = new ProdutoAnteriorDAO();
        prodAntDAO.setImportSistema(provider.getSistema());
        prodAntDAO.setImportLoja(provider.getLoja());

        provider.setMessage("Receita...Gravando receitas...", receita.size());

        provider.begin();

        try {

            for (ReceitaIMP imp : receita) {

                ReceitaAnteriorVO anterior = anteriores.get(imp.getImportid());

                if (anterior == null) {

                    /* gravando cabeçalho */
                    int id = ids.obterID("A");
                    ReceitaVO vo = converterReceita(imp);
                    vo.setId(id);
                    provider.gravar(vo);

                    if (!receitaLoja.containsKey(vo.getId(), provider.getLojaVR())) {
                        ReceitaLojaVO voLoja = new ReceitaLojaVO();
                        voLoja.setId_receita(vo.getId());
                        voLoja.setId_loja(provider.getLojaVR());
                        provider.gravarReceitaLoja(voLoja);
                    }

                    receitaLoja.put(null, vo.getId(), provider.getLojaVR());

                    anterior = converterAnterior(imp);
                    anterior.setCodigoAtual(vo.getId());
                    provider.gravar(anterior);
                    anteriores.put(anterior.getImportid(), anterior);
                }

                if (anterior.getCodigoAtual() > 0) {

                    /* gravando item */
                    ReceitaItemVO voItem = converterReceitaItem(imp);
                    voItem.setId_receita(anterior.getCodigoAtual());

                    /* gavando produtos */
                    ReceitaProdutoVO voProduto = converterReceitaProduto(imp);
                    voProduto.setId_receita(anterior.getCodigoAtual());

                    ProdutoVO prodItemVO = null;
                    for (String produto : imp.getProdutos()) {
                        Integer idProduto = null;
                        idProduto = produtos.get(produto);
                        prodItemVO = prodAntDAO.getCodigoAnterior().get(
                                imp.getImportsistema(),
                                imp.getImportloja(),
                                produto
                        ).getCodigoAtual();

                        Integer prodItem = prodItemVO.getId();

                        if (idProduto != null) {

                            if (!receitaItem.containsKey(anterior.getCodigoAtual(), prodItem)) {
                                voItem.setId_produto(idProduto);
                                provider.gravarItem(voItem);
                            }
                            receitaItem.put(null, anterior.getCodigoAtual(), prodItem);
                        }
                    }

                    ProdutoVO prodReceVO = null;
                    prodReceVO = prodAntDAO.getCodigoAnterior().get(
                            imp.getImportsistema(),
                            imp.getImportloja(),
                            imp.getIdproduto()
                    ).getCodigoAtual();

                    Integer prodReceita = prodReceVO.getId();
                    if (!receitaProduto.containsKey(prodReceita, prodReceita)) {
                        voProduto.setId_produto(prodReceita);
                        provider.gravarProduto(voProduto);
                        receitaProduto.put(null, prodReceita, prodReceita);
                    }
                }
                provider.setMessage();
            }
            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }

    public ReceitaAnteriorVO converterAnterior(ReceitaIMP imp) throws Exception {
        ReceitaAnteriorVO ant = new ReceitaAnteriorVO();

        ant.setImportsistema(imp.getImportsistema());
        ant.setImportloja(imp.getImportloja());
        ant.setImportid(imp.getImportid());
        ant.setIdproduto(imp.getIdproduto());
        ant.setDescricao(imp.getDescricao());
        ant.setFichatecnica(imp.getFichatecnica());
        ant.setQtdembalagemreceita(imp.getQtdembalagemreceita());
        ant.setQtdembalagemproduto(imp.getQtdembalagemproduto());
        ant.setRendimento(imp.getRendimento());

        return ant;
    }

    private ReceitaVO converterReceita(ReceitaIMP imp) throws Exception {
        ReceitaVO vo = new ReceitaVO();

        vo.setDescricao(imp.getDescricao());
        vo.setFichatecnica(imp.getFichatecnica());
        vo.setId_situacaocadastro(1);

        LOG.fine("RECEITA: " + vo.getDescricao());
        return vo;
    }

    private ReceitaItemVO converterReceitaItem(ReceitaIMP imp) throws Exception {
        ReceitaItemVO vo = new ReceitaItemVO();

        vo.setQtdembalagemproduto(imp.getQtdembalagemproduto());
        vo.setQtdembalagemreceita(imp.getQtdembalagemreceita());
        vo.setFatorconversao(imp.getFator());
        return vo;
    }

    private ReceitaProdutoVO converterReceitaProduto(ReceitaIMP imp) throws Exception {
        ReceitaProdutoVO vo = new ReceitaProdutoVO();

        vo.setRendimento(imp.getRendimento());
        return vo;
    }
}
