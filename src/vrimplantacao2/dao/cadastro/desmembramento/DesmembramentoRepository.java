package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoItemVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;

public class DesmembramentoRepository {

    private static final Logger LOG = Logger.getLogger(DesmembramentoRepository.class.getName());

    private final DesmembramentoRepositoryProvider provider;

    public DesmembramentoRepository(DesmembramentoRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importarDesmembramento(List<DesmembramentoIMP> desmembramentos) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Desmembramentos...carregando listagens...");
            LOG.info("Carregando produtos anteriores");
            Map<String, Integer> produtos = provider.getProdutosAnteriores();
            LOG.info("Carregando os Desmembramentos existentes e seus itens");
            Map<Integer, DesmembramentoVO> desmembramentosExistentes = provider.getDesmembramentosExistentes();
            LOG.info("Carregando produtos ativos");
            Set<Integer> produtosAtivos;

            produtosAtivos = provider.getProdutosAtivos();

            provider.setStatus("Desmembramentos...gravando...", desmembramentos.size());
            LOG.info("Iniciando gravação dos desmembramentos");

            for (DesmembramentoIMP imp : desmembramentos) {

                Integer produtoPai = produtos.get(imp.getProdutoPai());

                if (produtoPai != null) {
                    if (produtosAtivos.contains(produtoPai)) {

                        DesmembramentoVO vo = desmembramentosExistentes.get(produtoPai);
                        if (vo == null) {

                            vo = new DesmembramentoVO();
                            vo.setIdProduto(produtoPai);
                            provider.gravar(vo);
                            desmembramentosExistentes.put(vo.getIdProduto(), vo);
                            LOG.finest("Produto pai gravado com sucesso: " + vo.getId());
                        }

                        Integer produtoFilho = produtos.get(imp.getProdutoFilho());

                        if (produtoFilho != null) {
                            DesmembramentoItemVO vItem = vo.getItens().get(produtoFilho);
                            if (vItem == null) {

                                vItem = new DesmembramentoItemVO();
                                vItem.setIdDesmembramento(vo.getId());
                                vItem.setIdProduto(produtoFilho);
                                vItem.setPercentualEstoque(imp.getPercentual());
                                vItem.setPercentualCusto(0.0d);
                                vItem.setPercentualDesossa(0.0d);
                                vItem.setPercentualPerda(0.0d);

                                provider.gravar(vItem);
                                vo.getItens().put(vItem.getIdProduto(), vItem);
                            }
                        } else {
                            LOG.warning(" Produto Filho : " + imp.getProdutoFilho() + " não foi encontrado!");
                        }
                    }
                } else {
                    LOG.warning("Produto Pai: " + imp.getProdutoPai() + " não foi encontrado!");
                }

                provider.setStatus();
            }

            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }
}
