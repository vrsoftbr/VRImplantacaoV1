package vrimplantacao2_5.tipoRecebivel.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.tipoRecebivel.IMP.TipoEntradaIMP;
import vrimplantacao2_5.tipoRecebivel.Provider.TipoEntradaRepositoryProvider;
import vrimplantacao2_5.tipoRecebivel.VO.TipoEntradaVO;

public class TipoEntradaRepository {
    
    private static final Logger LOG = Logger.getLogger(TipoEntradaRepository.class.getName());
    
    private final TipoEntradaRepositoryProvider provider;
    
    public TipoEntradaRepository(TipoEntradaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public TipoEntradaVO converter(TipoEntradaIMP imp) {
        TipoEntradaVO vo = new TipoEntradaVO();
        
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setId_situacaoCadastro(imp.getId_situacaoCadastro());
        vo.setTipo(imp.getTipo());
        vo.setAtualizaCusto(imp.isAtualizaCusto());
        vo.setAtualizaEstoque(imp.isAtualizaCusto());
        vo.setAtualizaPedido(imp.isAtualizaPedido());
        vo.setImprimeGuiaCega(imp.isImprimeGuiaCega());
        vo.setImprimeDivergencia(imp.isImprimeDivergencia());
        vo.setAtualizaPerda(imp.isAtualizaPerda());
        vo.setNotaProdutor(imp.isNotaProdutor());
        vo.setGeraContrato(imp.isGeraContrato());
        vo.setAtualizaDataEntrada(imp.isAtualizaDataEntrada());
        vo.setUtilizaCustoTabela(imp.isUtilizaCustoTabela());
        vo.setBonificacao(imp.isBonificacao());
        vo.setAtualizaDivergenciaCusto(imp.isAtualizaDivergenciaCusto());
        vo.setAtualizaAdministracao(imp.isAtualizaAdministracao());
        vo.setAtualizaFiscal(imp.isAtualizaFiscal());
        vo.setAtualizaPagar(imp.isAtualizaPagar());
        vo.setAtualizaTroca(imp.isAtualizaTroca());
        vo.setSerie(imp.getSerie());
        vo.setEspecie(imp.getEspecie());
        vo.setAtualizaEscrita(imp.isAtualizaEscrita());
        vo.setId_contaContabilFiscalDebito(imp.getId_contaContabilFiscalDebito());
        vo.setId_historicoPadrao(imp.getId_historicoPadrao());
        vo.setId_contaContabilFiscalCredito(imp.getId_contaContabilFiscalCredito());
        vo.setSubstituicao(imp.isSubstituicao());
        vo.setForaEstado(imp.isForaEstado());
        vo.setId_produto(imp.getId_produto());
        vo.setVerificaPedido(imp.isVerificaPedido());
        vo.setPlanoConta1(imp.getPlanoConta1());
        vo.setPlanoConta2(imp.getPlanoConta2());
        vo.setGeraVerba(imp.isGeraVerba());
        vo.setContabilidadePadrao(imp.isContabilidadePadrao());
        vo.setContabiliza(imp.isContabiliza());
        vo.setCreditaPisCofins(imp.isCreditaPisCofins());
        vo.setId_tipoBaseCalculoCredito(imp.getId_tipoBaseCalculoCredito());
        vo.setNaoCreditaIcms(imp.isNaoCreditaIcms());
        vo.setDescargaPalete(imp.isDescargaPalete());
        vo.setAtivoImobilizado(imp.isAtivoImobilizado());
        vo.setId_ativoGrupo(imp.getId_ativoGrupo());
        vo.setUtilizaCentroCusto(imp.isUtilizaCentroCusto());
        vo.setId_aliquota(imp.getId_aliquota());
        vo.setNotaMei(imp.isNotaMei());
        vo.setContabilizaController360(imp.isContabilizaController360());
        vo.setUtilizaCustoOrigem(imp.isUtilizaCustoOrigem());
        
        return vo;
        
    }
    
    public void importarTipoEntrada(List<TipoEntradaIMP> entrada) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Entrada existentes...", entrada.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (TipoEntradaIMP imp : entrada) {
                TipoEntradaVO vo = new TipoEntradaVO();
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
