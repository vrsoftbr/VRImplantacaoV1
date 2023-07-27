package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.EcfIMP;
import vrimplantacao2_5.Financeiro.Provider.EcfRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.EcfVO;

public class EcPdvRepository {
    
    private static final Logger LOG = Logger.getLogger(EcPdvRepository.class.getName());
    
    private final EcfRepositoryProvider provider;
    
    public EcPdvRepository(EcfRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public EcfVO converter(EcfIMP imp) {
        EcfVO vo = new EcfVO();
        
        vo.setId(imp.getId());
        vo.setId_loja(imp.getId_loja());
        vo.setEcf(imp.getEcf());
        vo.setDescricao(imp.getDescricao());
        vo.setId_tipoMarca(imp.getId_tipoMarca());
        vo.setId_tipoModelo(imp.getId_tipoModelo());
        vo.setId_situacaoCadastro(imp.getId_situacaoCadastro());
        vo.setNumeroSerie(imp.getNumeroSerie());
        vo.setMfAdicional(imp.getMfAdicional());
        vo.setNumeroUsuario(imp.getNumeroUsuario());
        vo.setTipoEcf(imp.getTipoEcf());
        vo.setVersaoSb(imp.getVersaoSb());
        vo.setDatHoraGravacaoSb(imp.getDatHoraGravacaoSb());
        vo.setDataHoraCadastro(imp.getDataHoraCadastro());
        vo.setIncidenciaDesconto(imp.isIncidenciaDesconto());
        vo.setVersaoBiblioteca(imp.getVersaoBiblioteca());
        vo.setGeraNfPaulista(imp.isGeraNfPaulista());
        vo.setId_tipoEstado(imp.getId_tipoEstado());
        vo.setVersao(imp.getVersao());
        vo.setDataMovimento(imp.getDataMovimento());
        vo.setCargaGData(imp.isCargaGData());
        vo.setCargaParam(imp.isCargaParam());
        vo.setCargaLayout(imp.isCargaLayout());
        vo.setCargaImagem(imp.isCargaImagem());
        vo.setId_tipoLayoutNotaPaulista(imp.getId_tipoLayoutNotaPaulista());
        vo.setTouch(imp.isTouch());
        vo.setAlteradoPaf(imp.isAlteradoPaf());
        vo.setHoraMovimento(imp.getHoraMovimento());
        vo.setId_modeloPdv(imp.getId_modeloPdv());
        
        return vo;
        
    }
    
    public void importarEcf(List<EcfIMP> ecf) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando ECF existentes...", ecf.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (EcfIMP imp : ecf) {
                EcfVO vo = new EcfVO();
                vo = converter(imp);
                provider.gravar(vo);
            }
            
            LOG.finest("");
            
            provider.setStatus();
            
            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }
}
