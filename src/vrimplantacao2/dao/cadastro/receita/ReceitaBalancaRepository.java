package vrimplantacao2.dao.cadastro.receita;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaAnteriorVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaFilizolaVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaToledoVO;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;

/**
 *
 * @author Leandro
 */
public class ReceitaBalancaRepository {
    
    private static final Logger LOG = Logger.getLogger(ReceitaBalancaRepository.class.getName());
    private final ReceitaBalancaRepositoryProvider provider;

    public ReceitaBalancaRepository(ReceitaBalancaRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importar(List<ReceitaBalancaIMP> receita, Set<OpcaoReceitaBalanca> opt) throws Exception {
        provider.setMessage("Receita Balança...Carregando dados...");
        
        Map<String, Integer> produtos = provider.getProdutos();
        Map<String, ReceitaBalancaAnteriorVO> anteriores = provider.getAnteriores();
        IDStack idsVagosFilizola = provider.getIdsVagosFilizola();
        IDStack idsVagosToledo = provider.getIdsVagosToledo();
        MultiMap<Integer, Void> receitasFilizola = provider.getReceitasFilizola();
        MultiMap<Integer, Void> receitasToledo = provider.getReceitasToledo();
        
        provider.setMessage("Receita Balanca...Gravando receitas...", receita.size());
        
        provider.begin();
        try {
            
            for (ReceitaBalancaIMP imp: receita) {
                
                ReceitaBalancaAnteriorVO anterior = anteriores.get(imp.getId());
                
                if (anterior == null) {
                
                    anterior = converterAnterior(imp);
                    
                    if (opt.contains(OpcaoReceitaBalanca.FILIZOLA)) {

                        ReceitaBalancaFilizolaVO vo = converterFilizola(imp);
                        vo.setId((int) idsVagosFilizola.pop(imp.getId()));
                        provider.gravar(vo);
                        anterior.setCodigoAtualFilizola(vo.getId());

                    }
                    
                    if (opt.contains(OpcaoReceitaBalanca.TOLEDO)) {

                        ReceitaBalancaToledoVO vo = converterToledo(imp);
                        vo.setId((int) idsVagosToledo.pop(imp.getId()));
                        provider.gravar(vo);
                        anterior.setCodigoAtualToledo(vo.getId());

                    }

                    if (!opt.isEmpty()) {
                        provider.gravar(anterior);
                        anteriores.put(anterior.getId(), anterior);
                    }
                
                }
                
                for (String produto: imp.getProdutos()) {
                    Integer idProduto = produtos.get(produto);
                    if (idProduto != null) {
                        if (opt.contains(OpcaoReceitaBalanca.FILIZOLA)) {
                            if (!receitasFilizola.containsKey(anterior.getCodigoAtualFilizola(), idProduto)) {
                                provider.gravarItemFilizola(anterior.getCodigoAtualFilizola(), idProduto);    
                                receitasFilizola.put(null, anterior.getCodigoAtualFilizola(), idProduto);
                                LOG.finest("ID Filizola Produto " + idProduto + " gravado na receita " + anterior.getCodigoAtualFilizola()); 
                            }
                        }
                        if (opt.contains(OpcaoReceitaBalanca.TOLEDO)) {
                            if (!receitasToledo.containsKey(anterior.getCodigoAtualToledo(), idProduto)) {
                                provider.gravarItemToledo(anterior.getCodigoAtualToledo(), idProduto);    
                                receitasToledo.put(null, anterior.getCodigoAtualToledo(), idProduto);
                                LOG.finest("ID Toledo Produto " + idProduto + " gravado na receita " + anterior.getCodigoAtualToledo()); 
                            }
                        }
                    } else {
                        LOG.warning("Produto código " + produto + " não foi encontrado!");
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

    public ReceitaBalancaAnteriorVO converterAnterior(ReceitaBalancaIMP imp) {
        ReceitaBalancaAnteriorVO ant = new ReceitaBalancaAnteriorVO();
        
        ant.setSistema(provider.getSistema());
        ant.setLoja(provider.getLoja());
        ant.setId(imp.getId());
        ant.setDescricao(imp.getDescricao());
        
        return ant;
    }

    private ReceitaBalancaFilizolaVO converterFilizola(ReceitaBalancaIMP imp) {
        ReceitaBalancaFilizolaVO vo = new ReceitaBalancaFilizolaVO();
        
        vo.setDescricao(imp.getDescricao());
        vo.setReceita(imp.getReceita());
        
        LOG.finest("FILIZOLA: " + vo.getReceita());
        
        return vo;
    }

    public ReceitaBalancaToledoVO converterToledo(ReceitaBalancaIMP imp) {
        ReceitaBalancaToledoVO vo = new ReceitaBalancaToledoVO();
        
        vo.setDescricao(imp.getDescricao());
        vo.setObservacao(imp.getObservacao());
        
        String receita = imp.getReceita() != null ? imp.getReceita() : "";        
        receita = Utils.acertarTexto(receita.replaceAll("\\r?\\n", " "));
        
        int cont = 1;
        
        while (!"".equals(receita) && cont <= 15) {
            String linha;
            
            if (receita.length() < 56) {
                linha = receita.substring(0, receita.length());
                receita = "";
            } else {
                linha = receita.substring(0, 56);
                receita = receita.substring(56);
            }
            
            switch (cont) {
                case 1: vo.setReceitaLinha1(linha); break;
                case 2: vo.setReceitaLinha2(linha); break;
                case 3: vo.setReceitaLinha3(linha); break;
                case 4: vo.setReceitaLinha4(linha); break;
                case 5: vo.setReceitaLinha5(linha); break;
                case 6: vo.setReceitaLinha6(linha); break;
                case 7: vo.setReceitaLinha7(linha); break;
                case 8: vo.setReceitaLinha8(linha); break;
                case 9: vo.setReceitaLinha9(linha); break;
                case 10: vo.setReceitaLinha10(linha); break;
                case 11: vo.setReceitaLinha11(linha); break;
                case 12: vo.setReceitaLinha12(linha); break;
                case 13: vo.setReceitaLinha13(linha); break;
                case 14: vo.setReceitaLinha14(linha); break;
                case 15: vo.setReceitaLinha15(linha); break;
            }
            
            cont++;
        }
        
        LOG.finest("TOLEDO: " + imp.getReceita());
        
        return vo;
    }
    
}
