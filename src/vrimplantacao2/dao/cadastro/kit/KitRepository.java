package vrimplantacao2.dao.cadastro.kit;

import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2.vo.importacao.KitIMP;
import vrimplantacao2.vo.cadastro.kit.KitVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.kit.KitItemVO;
import vrimplantacao2.vo.cadastro.kit.KitAnteriorVO;
import vrimplantacao2.vo.cadastro.kit.KitItemAnteriorVO;

/**
 *
 * @author Wesley
 */
public class KitRepository {

    private static final Logger LOG = Logger.getLogger(KitRepository.class.getName());

    private final KitRepositoryProvider provider;

    public KitRepository(KitRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importarKit(List<KitIMP> kits) throws Exception {

        provider.begin();
        try {
            provider.createTable();

            provider.setStatus("Kits......carregando listagens...");
            LOG.info("Carregando produtos anteriores");
            Map<String, Integer> produtos = provider.getProdutosAnteriores();

            LOG.info("Carregando os kits importados anteriormente");
            MultiMap<String, KitAnteriorVO> kitsAnteriores = provider.getAnteriores();
            MultiMap<String, KitItemAnteriorVO> kitItensAnterior = provider.getItemsAnteriores();

            provider.setStatus("Kits...gravando...", kits.size());
            LOG.info("Iniciando gravação dos kits");

            for (KitIMP imp : kits) {

                KitVO vo;
                KitAnteriorVO kitAnterior;
                KitItemAnteriorVO kitItemAnterior;

                //Verifica a existência do produto na tabela codant_produto.
                Integer produtoPrincipal = produtos.get(imp.getImpid_produto_principal());
                Integer produtoItem = produtos.get(imp.getImpid_produto_item());

                //TODO: Incluir uma rotina que verifica os itens pelo EAN.
                if (produtoPrincipal != null && produtoItem != null) {

                    kitAnterior = kitsAnteriores.get(
                            provider.getSistema(),
                            provider.getLoja(),
                            imp.getImpid_kit()
                    );
                    
                    vo = new KitVO();
                    
                    if (kitAnterior == null) {

                        //GRAVA KIT NO VR CASO NAO EXISTA.
                        vo = new KitVO();
                        vo.setIdProduto(produtoPrincipal);
                        vo.setPrecoNormal(imp.isPreco_normal());

                        provider.gravar(vo);
                        
                        kitAnterior = converterAnterior(imp);
                        kitAnterior.setCodigoAtual(vo);
                        kitAnterior.setCodigoAtualProduto(vo.getIdProduto());
                        kitAnterior.setObservacaoImportacao("KIT INSERIDO COMO NOVO");
                        
                        //gravar kitloja
                        provider.gravarKitLoja(vo);

                        gravarKitAnterior(kitAnterior);

                        kitsAnteriores.put(
                                kitAnterior,
                                provider.getSistema(),
                                provider.getLoja(),
                                imp.getImpid_kit());
                    } else {

                        //VERIFICA SE VEM A DESCRICAO
                        vo.setId(kitAnterior.getCodigoAtual().getId());
//                        vo = produtoSimilarAnterior.getCodigoAtual();
                    }

                    //IMPORTAR ITEM KIT.
                    kitItemAnterior = kitItensAnterior.get(
                            provider.getSistema(),
                            provider.getLoja(),
                            imp.getImpid_kit_item(),
                            imp.getImpid_kit()
                    );

                    if (kitItemAnterior == null && vo.getId() > 0) {

                        KitItemVO vItem = new KitItemVO();

                        vItem.setKit(vo);
                        vItem.setIdProduto(produtoItem);
                        vItem.setPrecoVenda(imp.getPreco());
                        vItem.setQuantidade(imp.getQuantidade());

                        provider.gravar(vItem);

                        kitItemAnterior = converterItemAnterior(imp);
                        kitItemAnterior.setCodigoAtual(vItem);
                        kitItemAnterior.setCodigoAtualKit(vo);
                        kitItemAnterior.setCodigoAtualProduto(vItem.getIdProduto());
                        
                        kitItemAnterior.setObservacaoImportacao("KIT ITEM INSERIDO COMO NOVO");

                        gravarKitItemAnterior(kitItemAnterior);

                        kitItensAnterior.put(
                                kitItemAnterior,
                                provider.getSistema(),
                                provider.getLoja(),
                                imp.getImpid_kit_item(),
                                imp.getImpid_kit()
                        );

                    } else {
                        //POSSIBILIDADE DE GRAVAR CODANT ITEM SEM CODIGO ATUAL 
//                            LOG.warning(imp.getId() + " '" + imp.getDescricao() + " -> Produto filho " + imp.getProdutoAssociadoId() + " '" + imp.getDescricaoProdutoAssociado() + "' não foi encontrado!");
                    }

                } else {
                    //POSSIBILIDADE DE GRAVAR CODANT SEM CODIGO ATUAL
//                    LOG.warning("Produto pai " + imp.getId() + " '" + imp.getDescricao() + "' não foi encontrado!");
                }

                provider.setStatus();
            }

            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }

    }

    public KitAnteriorVO converterAnterior(KitIMP imp) {
        KitAnteriorVO ant = new KitAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(provider.getLoja());
        ant.setImportId(imp.getImpid_kit());
        ant.setImportIdProduto(imp.getImpid_produto_principal());

        return ant;
    }

    public KitItemAnteriorVO converterItemAnterior(KitIMP imp) {
        KitItemAnteriorVO ant = new KitItemAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(provider.getLoja());
        ant.setImportId(imp.getImpid_kit_item());
        ant.setImportIdKit(imp.getImpid_kit());
        ant.setImportIdProduto(imp.getImpid_produto_item());

        return ant;
    }

    public void gravarKitAnterior(KitAnteriorVO anterior) throws Exception {
        provider.gravarKitAnterior(anterior);
    }

    public void gravarKitItemAnterior(KitItemAnteriorVO anterior) throws Exception {
        provider.gravarKitItemAnterior(anterior);
    }
}
