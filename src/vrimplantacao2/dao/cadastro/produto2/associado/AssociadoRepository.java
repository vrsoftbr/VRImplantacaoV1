package vrimplantacao2.dao.cadastro.produto2.associado;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoAnteriorVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoItemVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoVO;
import vrimplantacao2.vo.importacao.AssociadoIMP;

/**
 *
 * @author Leandro
 */
public class AssociadoRepository {

    private static final Logger LOG = Logger.getLogger(AssociadoRepository.class.getName());

    private final AssociadoRepositoryProvider provider;

    public AssociadoRepository(AssociadoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    

    public void importarAssociado(List<AssociadoIMP> associados, Set<OpcaoAssociado> opt) throws Exception {

        provider.begin();
        try {
            provider.createTable();

            provider.setStatus("Associados...carregando listagens...");
            LOG.info("Carregando produtos anteriores");
            Map<String, Integer> produtos = provider.getProdutosAnteriores();
            LOG.info("Carregando os associados existentes e seus itens");
            Map<Integer, AssociadoVO> associadosExistentes = provider.getAssociadosExistentes();
            LOG.info("Carregando os associados importados anteriormente");
            MultiMap<String, AssociadoAnteriorVO> associadosAnteriores = provider.getAnteriores();
            MultiMap<String, AssociadoItemAnteriorVO> associadosItemsAnteriores = provider.getItemsAnteriores();
            LOG.info("Carregando produtos ativos");
            Set<Integer> produtosAtivos;

            boolean somenteAtivos = opt.contains(OpcaoAssociado.IMP_SOMENTE_ATIVOS);
            if (somenteAtivos) {
                produtosAtivos = provider.getProdutosAtivos();
            } else {
                produtosAtivos = new HashSet<>();
            }

            provider.setStatus("Associados...gravando...", associados.size());
            LOG.info("Iniciando gravação dos associados");

            AssociadoAnteriorVO associadoAnterior;
            AssociadoItemAnteriorVO associadoItemAnterior;

            //Para cada associado, execute.
            for (AssociadoIMP imp : associados) {
                //Verifica a existência do produto pai na tabela codant_produto.
                Integer produtoPai = produtos.get(imp.getImpIdProduto());
                //TODO: Incluir uma rotina que verifica os itens pelo EAN.
                if (produtoPai != null) {
                    if (!somenteAtivos || (somenteAtivos && produtosAtivos.contains(produtoPai))) {
                        //Verifica se existe algum associado com este produto.
                        AssociadoVO vo = associadosExistentes.get(produtoPai);
                        if (vo == null) {
                            //Se não existir cria um novo associado e o cadastra no VR.
                            vo = new AssociadoVO();
                            vo.setIdProduto(produtoPai);
                            vo.setQtdEmbalagem(imp.getQtdEmbalagem());
                            provider.gravar(vo);
                            associadosExistentes.put(vo.getIdProduto(), vo);
                            LOG.finest("Produto pai gravado com sucesso: " + vo.getId());

                            associadoAnterior = converterAnterior(imp);
                            associadoAnterior.setCodigoAtual(vo);

                            ProdutoVO produtoAtual = new ProdutoVO();
                            produtoAtual.setId(vo.getIdProduto());
                            associadoAnterior.setCodigoAtualProdutoAssociado(produtoAtual);
                            associadoAnterior.setObservacaoImportacao("ASSOCIADO INSERIDO COMO NOVO");

                            gravarAssociadoAnterior(associadoAnterior);
                            associadosAnteriores.put(
                                    associadoAnterior,
                                    provider.getSistema(),
                                    provider.getLoja(),
                                    imp.getImpIdAssociado()
                            );
                        }

                        //Importa os itens do associados.
                        //Verifica a existencia do produto filho na tabela codant_produto.
                        Integer produtoFilho = produtos.get(imp.getImpIdProdutoItem());
                        //TODO: Incluir uma rotina que verifica os itens pelo EAN.
                        if (produtoFilho != null) {
                            //Se o produto filho existir, verifica se já está cadastrado no associado.
                            AssociadoItemVO vItem = vo.getItens().get(produtoFilho);
                            if (vItem == null) {
                                //Se não estiver cadastrado, converte e cadastra.
                                vItem = new AssociadoItemVO();
                                vItem.setIdAssociado(vo.getId());
                                vItem.setIdProduto(produtoFilho);
                                vItem.setQtdEmbalagem(imp.getQtdEmbalagemItem());
                                vItem.setAplicaEstoque(imp.isAplicaEstoque());
                                vItem.setAplicaCusto(imp.isAplicaCusto());
                                vItem.setAplicaPreco(imp.isAplicaPreco());
                                vItem.setPercentualCustoEstoque(imp.getPercentualcustoestoque());
                                vItem.setPercentualPreco(imp.getPercentualPreco());
                                provider.gravar(vItem);
                                vo.getItens().put(vItem.getIdProduto(), vItem);

                                associadoItemAnterior = converterItemAnterior(imp);
                                associadoItemAnterior.setCodigoAtual(vItem);

                                AssociadoVO associadoAtual = new AssociadoVO();
                                associadoAtual.setId(vItem.getIdAssociado());
                                associadoItemAnterior.setCodigoAtualAssociado(associadoAtual);

                                ProdutoVO produtoAtual = new ProdutoVO();
                                produtoAtual.setId(vItem.getIdProduto());
                                associadoItemAnterior.setCodigoAtualProdutoAssociadoItem(produtoAtual);

                                associadoItemAnterior.setObservacaoImportacao("ITEM ASSOCIADO INSERIDO COMO NOVO");

                                gravarAssociadoItemAnterior(associadoItemAnterior);
                                associadosItemsAnteriores.put(
                                        associadoItemAnterior,
                                        provider.getSistema(),
                                        provider.getLoja(),
                                        imp.getImpIdAssociadoItem()
                                );

                            }

                            //Gera a inversão se não existir
//                            if (opt.contains(OpcaoAssociado.IMP_INVERTER)) { 
//
//                                AssociadoVO invertVo = associadosExistentes.get(vItem.getIdProduto());
//
//                                //Verifica se o associado já existe.
//                                if (invertVo == null) {                                    
//                                    //Coloca o item do associado como pai.
//                                    invertVo = new AssociadoVO();
//                                    invertVo.setIdProduto(vItem.getIdProduto());
//                                    invertVo.setQtdEmbalagem(1);
//                                    provider.gravar(invertVo);
//                                    associadosExistentes.put(invertVo.getIdProduto(), invertVo);
//                                }
//
//                                if (!invertVo.getItens().containsKey(vo.getIdProduto())) {
//                                    //Coloca o pai como filho.
//                                    AssociadoItemVO invertItem = new AssociadoItemVO();
//                                    invertItem.setIdAssociado(invertVo.getId());
//                                    invertItem.setIdProduto(vo.getIdProduto());
//                                    invertItem.setQtdEmbalagem(vo.getQtdEmbalagem());
//                                    invertItem.setAplicaEstoque(false);
//                                    invertItem.setAplicaCusto(true);
//                                    invertItem.setAplicaPreco(true);
//                                    invertItem.setPercentualCustoEstoque(imp.getPercentualcustoestoque());
//                                    invertItem.setPercentualPreco(imp.getPercentualPreco());
//                                    provider.gravar(invertItem);
//                                    invertVo.getItens().put(invertItem.getIdProduto(), invertItem);                                        
//                                }
//
//                            }
                        } else {
//                            LOG.warning(imp.getId() + " '" + imp.getDescricao() + " -> Produto filho " + imp.getProdutoAssociadoId() + " '" + imp.getDescricaoProdutoAssociado() + "' não foi encontrado!");
                        }
                    }

                } else {
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

    public AssociadoAnteriorVO converterAnterior(AssociadoIMP imp) {
        AssociadoAnteriorVO ant = new AssociadoAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(provider.getLoja());
        ant.setImportId(imp.getImpIdAssociado());
        ant.setImportIdProduto(imp.getImpIdProduto());
        ant.setQtdEmbalagem(imp.getQtdEmbalagem());

        return ant;
    }

    public AssociadoItemAnteriorVO converterItemAnterior(AssociadoIMP imp) {
        AssociadoItemAnteriorVO ant = new AssociadoItemAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(provider.getLoja());
        ant.setImportId(imp.getImpIdAssociadoItem());
        ant.setImportIdProduto(imp.getImpIdProdutoItem());
        ant.setImportIdAssociado(imp.getImpIdAssociado());
        ant.setQtdEmbalagemItem(imp.getQtdEmbalagemItem());
        ant.setPercentualPreco(imp.getPercentualPreco());
        ant.setAplicaPreco(imp.isAplicaPreco());
        ant.setAplicaCusto(imp.isAplicaCusto());
        ant.setAplicaEstoque(imp.isAplicaEstoque());
        ant.setPercentualcustoestoque(imp.getPercentualPreco());

        return ant;
    }

    public void gravarAssociadoAnterior(AssociadoAnteriorVO anterior) throws Exception {
        provider.gravarAssociadoAnterior(anterior);
    }

    public void gravarAssociadoItemAnterior(AssociadoItemAnteriorVO anterior) throws Exception {
        provider.gravarAssociadoItemAnterior(anterior);
    }
}
