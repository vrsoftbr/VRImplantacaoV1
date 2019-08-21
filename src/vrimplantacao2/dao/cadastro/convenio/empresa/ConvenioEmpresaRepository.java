package vrimplantacao2.dao.cadastro.convenio.empresa;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaVO;
import vrimplantacao2.vo.cadastro.convenio.empresa.TipoTerminoRenovacao;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;

/**
 *
 * @author Leandro
 */
public class ConvenioEmpresaRepository {
    private final ConvenioEmpresaRepositoryProvider provider;

    public ConvenioEmpresaRepository(ConvenioEmpresaRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }

    public void salvar(List<ConvenioEmpresaIMP> empresas) throws Exception {
        try {
            this.provider.setStatus("Gravando empresas (Convênio)...");
            this.provider.begin();
        
            Set<Long> cnpjExistentes = this.provider.getCnpjExistentes();
            ConvenioEmpresaIDStack ids = this.provider.getIds();
            Map<String, ConvenioEmpresaAnteriorVO> anteriores = this.provider.getAnteriores();
            
            Map<String, ConvenioEmpresaIMP> filtrados = filtrar(empresas, ids);
            empresas.clear();
            System.gc();
            this.provider.setMaximum(filtrados.size());            
            
            for (ConvenioEmpresaIMP imp: filtrados.values()) {
                ConvenioEmpresaAnteriorVO anterior = anteriores.get(imp.getId());
                
                if (anterior == null) {
                
                    long cnpj = Utils.stringToLong(imp.getCnpj());
                    if (cnpjExistentes.contains(cnpj)) {
                        cnpj = -2;
                    }

                    int id = ids.obterID(imp.getId());
                    if (cnpj < 0) {
                        cnpj = id;
                    }
                    
                    ConvenioEmpresaVO vo = converterEmpresa(imp);                                        
                    vo.setId(id);
                    vo.setCnpj(cnpj);                    
                    gravarEmpresa(vo);
                    
                    anterior = converterEmpresaAnterior(imp);
                    anterior.setCodigoAtual(vo.getId());
                    gravarEmpresaAnterior(anterior);
                    
                    cnpjExistentes.add(cnpj);
                    anteriores.put( imp.getId(), anterior );
                }
                                
                this.provider.next();
            }
            this.provider.commit();            
        } catch (Exception e) {
            this.provider.rollback();
            throw e;
        }
    }

    public ConvenioEmpresaAnteriorVO converterEmpresaAnterior(ConvenioEmpresaIMP imp) {
        ConvenioEmpresaAnteriorVO vo = new ConvenioEmpresaAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setId(imp.getId());
        vo.setCnpj(imp.getCnpj());
        vo.setRazao(imp.getRazao());
        return vo;
    }

    public ConvenioEmpresaVO converterEmpresa(ConvenioEmpresaIMP imp) throws Exception {
        ConvenioEmpresaVO vo = new ConvenioEmpresaVO();
        vo.setRazaoSocial(imp.getRazao());
        vo.setEndereco(imp.getEndereco());
        vo.setBairro(imp.getBairro());
        {
            MunicipioVO mun = provider.getMunicipio(imp.getIbgeMunicipio());
            if (mun == null) {
                mun = provider.getMunicipio(
                        Utils.acertarTexto(imp.getMunicipio()), 
                        Utils.acertarTexto(imp.getUf())
                );
                if (mun == null) {
                    mun = provider.getMunicipioPadrao();
                }
            }
            vo.setId_municipio(mun.getId());
            vo.setId_estado(mun.getEstado().getId());
        }
        vo.setTelefone(imp.getTelefone());
        vo.setCep(Utils.stringToInt(imp.getCep()));
        vo.setInscricaoEstadual(imp.getInscricaoEstadual());
        vo.setCnpj(Utils.stringToLong(imp.getCnpj()));
        vo.setDataInicio(imp.getDataInicio());
        vo.setDataTermino(imp.getDataTermino());
        vo.setSituacaoCadastro(imp.getSituacaoCadastro());
        if (imp.getDiaInicioRenovacao() > 0 && imp.getDiaFimRenovacao() == 0) {
            vo.setRenovacaoAutomatica(true);
            vo.setDiaInicioRenovacao(imp.getDiaInicioRenovacao());
            vo.setDiaTerminoRenovacao(0);
            vo.setTipoTerminoRenovacao(TipoTerminoRenovacao.FIM_MES);
        } else if (imp.getDiaInicioRenovacao() > 0 && imp.getDiaFimRenovacao() > 0) {
            vo.setRenovacaoAutomatica(true);
            vo.setDiaInicioRenovacao(imp.getDiaInicioRenovacao());
            vo.setDiaTerminoRenovacao(imp.getDiaFimRenovacao());
            vo.setTipoTerminoRenovacao(TipoTerminoRenovacao.FIXO);
        } else {
            vo.setRenovacaoAutomatica(false);
            vo.setDiaInicioRenovacao(0);
            vo.setDiaTerminoRenovacao(0);
            vo.setTipoTerminoRenovacao(TipoTerminoRenovacao.NENHUM);
        }
        
        vo.setPercentualDesconto(imp.getDesconto());
        vo.setDiaPagamento(imp.getDiaPagamento());
        vo.setBloqueado(imp.isBloqueado());
        vo.setDataBloqueio(imp.getDataBloqueio());
        vo.setObservacao("IMPORTADO VR " + Utils.acertarTexto(imp.getObservacoes()));

        return vo;
    }

    public void gravarEmpresa(ConvenioEmpresaVO vo) throws Exception {
        provider.gravarEmpresa(vo);
    }

    public void gravarEmpresaAnterior(ConvenioEmpresaAnteriorVO anterior) throws Exception {
        provider.gravarEmpresaAnterior(anterior);
    }

    public Map<String, ConvenioEmpresaIMP> filtrar(List<ConvenioEmpresaIMP> empresas, ConvenioEmpresaIDStack ids) throws Exception {
        Map<String, ConvenioEmpresaIMP> result = new LinkedHashMap<>();
        MultiMap<String, ConvenioEmpresaIMP> validos = new MultiMap<>(1);
        MultiMap<String, ConvenioEmpresaIMP> invalidos = new MultiMap<>(1);
        Set<Integer> existentes = ids.obterIdsExistentes();
        
        for (ConvenioEmpresaIMP imp: empresas) {
            try {
                int id = Integer.parseInt(imp.getId());
                if (existentes.contains(id)) {
                    invalidos.put(imp, imp.getId());
                } else {
                    validos.put(imp, imp.getId());
                }                
            } catch (NumberFormatException e) {
                invalidos.put(imp, imp.getId());
            }
        }
        
        validos = validos.getSortedMap();
        invalidos = invalidos.getSortedMap();
        
        for (ConvenioEmpresaIMP imp: validos.values()) {
            result.put(imp.getId(), imp);
        }
        for (ConvenioEmpresaIMP imp: invalidos.values()) {
            result.put(imp.getId(), imp);
        }
        
        return result;
    }

    
}
