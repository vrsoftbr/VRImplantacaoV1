package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoAnteriorVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;
import vrimplantacao2_5.controller.migracao.LogController;

public class DesmembramentoRepository {

    private static final Logger LOG = Logger.getLogger(DesmembramentoRepository.class.getName());

    private final DesmembramentoRepositoryProvider provider;
    
    public DesmembramentoRepository(DesmembramentoRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importarDesmembramento(List<DesmembramentoIMP> desmembramento) throws Exception {
        
        provider.begin();
        try {
            
            provider.setStatus("Desmembramentos...carregando listagens...");
            LOG.info("Carregando produtos anteriores");                
            Map<String, Integer> produtos = provider.getProdutosAnteriores();
            LOG.info("Carregando os Desmembramentos existentes e seus itens");
            Map<Integer, DesmembramentoVO> desmembramentosExistentes = provider.getDesmembramentosExistentes();
            provider.setStatus("Desmembramentos...gravando...", desmembramento.size());
            LOG.info("Iniciando gravação dos desmembramentos");    
            
            
            for (DesmembramentoIMP imp : desmembramento) {
                
                Integer produtoPai = produtos.get(imp.getId());
                
                if (produtoPai != null) {
                    DesmembramentoVO vo = desmembramentosExistentes.get(produtoPai);
                    if (vo == null) {
                        
                        vo = new DesmembramentoVO();
                        vo.setId(produtoPai);
                        provider.gravar(vo);
                        desmembramentosExistentes.put(vo.getIdProduto(), vo);
                        LOG.finest("Produto pai gravado com sucesso: " + vo.getId());
                    }
                    
                    Integer produtoFilho = produtos.get(imp.getProdutoFilho());
                }
            }

            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    private DesmembramentoAnteriorVO converterDesmembramentoAnteriorVO(DesmembramentoIMP imp) {

        //int idConexao = promocaoService.existeConexaoMigrada(this.provider.getIdConexao(), this.provider.getSistema());
        DesmembramentoAnteriorVO vo = new DesmembramentoAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLoja());
        vo.setIdConexao(provider.getIdConexao());
        vo.setId(imp.getId());
        vo.setProdutoPai(imp.getProdutoPai());
        vo.setProdutoFilho(imp.getProdutoFilho());
        vo.setPercentual(imp.getPercentual());

        return vo;
    }

    public void gravarDesmembramento(DesmembramentoVO desmem) throws Exception {
        provider.gravar(desmem);
    }

//    public void getDesmembramentoItens() throws Exception {
//        provider.getDesmembramentoItens();
//    }

}
