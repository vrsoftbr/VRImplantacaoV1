package vrimplantacao2.dao.cadastro.nutricional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.nutricional.NutricionalAnteriorVO;
import vrimplantacao2.vo.importacao.NutricionalIMP;

/**
 *
 * @author Leandro
 */
public class NutricionalRepository {
    
    private static final Logger LOG = Logger.getLogger(NutricionalRepository.class.getName());
    
    private NutricionalRepositoryProvider provider;

    public NutricionalRepository(NutricionalRepositoryProvider repository) {
        this.provider = repository;
    }

    public void importar(List<NutricionalIMP> nutricionais, Set<OpcaoNutricional> opt) throws Exception {
        
        boolean resetarIds = opt.contains(OpcaoNutricional.RESETAR_IDS);
        
        provider.setStatus("Nutricionais...Carregando dados");
        LOG.fine("Carregando os preliminares necessários");
        Map<String, NutricionalAnteriorVO> anteriores = provider.getAnteriores();
        Map<String, Integer> produtos = provider.getProdutos();
        MultiMap<Integer, Void> nutricionaisFilizola = null;
        MultiMap<Integer, Void> nutricionaisToledo = null;
        
        IDStack idsFilizola = provider.getIdsVagosFilizola();
        IDStack idsToledo = provider.getIdsVagosToledo();
        
        LOG.finest("idsFilizola: " + idsFilizola.size());
        LOG.finest("idsToledo: " + idsToledo.size());
        
        if (opt.contains(OpcaoNutricional.FILIZOLA)) {
            nutricionaisFilizola = provider.getNutricionaisFilizola();
        }
        if (opt.contains(OpcaoNutricional.TOLEDO)) {
            nutricionaisToledo = provider.getNutricionaisToledo();
        }
        
        provider.begin();
        try {
        
            provider.setStatus("Nutricionais...Gravando...", nutricionais.size());
            for (NutricionalIMP imp: nutricionais) {
                LOG.finer("Nutricional: " + imp.getId() + " - " + imp.getDescricao());
                NutricionalAnteriorVO anterior = anteriores.get(imp.getId());
                
                if (anterior == null) {
                    
                    anterior = converterAnterior(imp);
                    
                    if (opt.contains(OpcaoNutricional.FILIZOLA)) {
                        NutricionalFilizolaVO vo = converterNutricionalFilizola(imp);
                        
                        if (resetarIds) {
                            vo.setId((int) idsFilizola.pop());
                        } else {
                            vo.setId((int) idsFilizola.pop(imp.getId()));
                        }
                        
                        provider.gravar(vo);
                        
                        anterior.setCodigoAtualFilizola(vo.getId());
                        LOG.finest("Nutricional Filizola gravado");
                    }
                    
                    if (opt.contains(OpcaoNutricional.TOLEDO)) {
                        NutricionalToledoVO vo = converterNutricionalToledo(imp);
                        
                        if (resetarIds) {
                            vo.setId((int) idsToledo.pop());
                        } else {
                            vo.setId((int) idsToledo.pop(imp.getId()));
                        }
                        
                        provider.gravar(vo);
                        
                        anterior.setCodigoAtualToledo(vo.getId());
                        LOG.finest("Nutricional Toledo gravado");
                    }
                    
                    if (!opt.isEmpty()) {
                        provider.gravar(anterior);
                        anteriores.put(imp.getId(), anterior);
                        LOG.finest("Nutricional anterior gravado");
                    }
                    
                }
                
                for (String produto: imp.getProdutos()) {
                    Integer idProduto = produtos.get(produto);
                    if (idProduto != null) {
                        if (opt.contains(OpcaoNutricional.FILIZOLA)) {
                            if (anterior.getCodigoAtualFilizola() != null) {
                                if (nutricionaisFilizola != null && !nutricionaisFilizola.containsKey(anterior.getCodigoAtualFilizola(), idProduto)) {
                                    provider.gravarItemFilizola(anterior.getCodigoAtualFilizola(), idProduto);    
                                    nutricionaisFilizola.put(null, anterior.getCodigoAtualFilizola(), idProduto);
                                    LOG.finest("ID Filizola Produto " + idProduto + " gravado no nutricional " + anterior.getCodigoAtualFilizola()); 
                                }
                            }
                        }
                        if (opt.contains(OpcaoNutricional.TOLEDO)) {
                            if (anterior.getCodigoAtualToledo() != null) {
                                if (nutricionaisToledo != null && !nutricionaisToledo.containsKey(anterior.getCodigoAtualToledo(), idProduto)) {
                                    provider.gravarItemToledo(anterior.getCodigoAtualToledo(), idProduto);    
                                    nutricionaisToledo.put(null, anterior.getCodigoAtualToledo(), idProduto);
                                    LOG.finest("ID Toledo Produto " + idProduto + " gravado no nutricional " + anterior.getCodigoAtualToledo()); 
                                }
                            }
                        }
                    }
                }
                
                provider.setStatus();
            }
            
            provider.commit();
            LOG.fine("Nutricionais importados com sucesso!");
        } catch (Exception e) {
            provider.rollback();
            LOG.log(Level.SEVERE, "Erro ao importar os nutricionais", e);
            throw e;            
        }
        
    }

    public NutricionalAnteriorVO converterAnterior(NutricionalIMP imp) {
        NutricionalAnteriorVO ant = new NutricionalAnteriorVO();
        
        ant.setSistema(provider.getSistema());
        ant.setLoja(provider.getLoja());
        ant.setId(imp.getId());
        
        return ant;
    }

    public NutricionalFilizolaVO converterNutricionalFilizola(NutricionalIMP imp) {
        NutricionalFilizolaVO nut = new NutricionalFilizolaVO();
        
        nut.setDescricao(imp.getDescricao());
        nut.setId_situacaocadastro(imp.getSituacaoCadastro().getId());
        nut.setCaloria(imp.getCaloria());
        nut.setCarboidrato(imp.getCarboidrato());
        nut.setCarboidratoinferior(imp.isCarboidratoInferior());
        nut.setProteina(imp.getProteina());
        nut.setProteinainferior(imp.isProteinaInferior());
        nut.setGordura(imp.getGordura());
        nut.setGordurasaturada(imp.getGorduraSaturada());
        nut.setColesterolinferior(imp.isColesterolInferior());
        nut.setFibra(imp.getFibra());
        nut.setFibrainferior(imp.isFibraInferior());
        nut.setCalcio(imp.getCalcio());
        nut.setFerro(imp.getFerro());
        nut.setSodio(imp.getSodio());
        nut.setPercentualcaloria(imp.getPercentualCaloria());
        nut.setPercentualcarboidrato(imp.getPercentualCarboidrato());
        nut.setPercentualproteina(imp.getPercentualProteina());
        nut.setPercentualgordura(imp.getPercentualGordura());
        nut.setPercentualgordurasaturada(imp.getPercentualGorduraSaturada());
        nut.setPercentualfibra(imp.getPercentualFibra());
        nut.setPercentualcalcio(imp.getPercentualCalcio());
        nut.setPercentualferro(imp.getPercentualFerro());
        nut.setPercentualsodio(imp.getPercentualSodio());
        nut.setPorcao(imp.getPorcao());
        nut.setQuantidade(imp.getQuantidade());
        
        StringBuilder string = new StringBuilder();
        for (String linha: imp.getMensagemAlergico()) {
            string.append(linha);
        }
        
        nut.setMensagemAlergico(string.toString());
        
        return nut;        
    }

    public NutricionalToledoVO converterNutricionalToledo(NutricionalIMP imp) {
        NutricionalToledoVO nut = new NutricionalToledoVO();
        
        nut.setDescricao(imp.getDescricao());
        nut.setId_situacaocadastro(imp.getSituacaoCadastro().getId());
        nut.setCaloria(imp.getCaloria());
        nut.setCarboidrato(imp.getCarboidrato());
        nut.setCarboidratoinferior(imp.isCarboidratoInferior());
        nut.setProteina(imp.getProteina());
        nut.setProteinainferior(imp.isProteinaInferior());
        nut.setGordura(imp.getGordura());
        nut.setGordurasaturada(imp.getGorduraSaturada());
        nut.setGorduratrans(imp.getGorduraTrans());
        nut.setColesterolinferior(imp.isColesterolInferior());
        nut.setFibra(imp.getFibra());
        nut.setFibrainferior(imp.isFibraInferior());
        nut.setCalcio(imp.getCalcio());
        nut.setFerro(imp.getFerro());
        nut.setSodio(imp.getSodio());
        nut.setPercentualcaloria(imp.getPercentualCaloria());
        nut.setPercentualcarboidrato(imp.getPercentualCarboidrato());
        nut.setPercentualproteina(imp.getPercentualProteina());
        nut.setPercentualgordura(imp.getPercentualGordura());
        nut.setPercentualgordurasaturada(imp.getPercentualGorduraSaturada());
        nut.setPercentualfibra(imp.getPercentualFibra());
        nut.setPercentualcalcio(imp.getPercentualCalcio());
        nut.setPercentualferro(imp.getPercentualFerro());
        nut.setPercentualsodio(imp.getPercentualSodio());
        nut.setQuantidade(imp.getQuantidade());
        nut.setId_tipomedidadecimal(imp.getId_tipomedidadecimal());
        nut.setId_tipounidadeporcao(imp.getId_tipounidadeporcao());
        nut.setAcucaresAdicionados(imp.getAcucaresadicionados());
        nut.setAcucaresTotais(imp.getAcucarestotais());
        if (imp.getIdTipoMedida() > 0) {
            nut.setId_tipomedida(imp.getIdTipoMedida());
        }
        nut.setMedidainteira(imp.getMedidaInteira());
        
        for (String linha: imp.getMensagemAlergico()) {
            nut.addMensagemAlergico(linha);
        }
        
        return nut;
    }
    
}
