package vrimplantacao2_5.tipoRecebivel.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.tipoRecebivel.IMP.TipoEntradaIMP;
import vrimplantacao2_5.tipoRecebivel.IMP.TipoSaidaIMP;
import vrimplantacao2_5.tipoRecebivel.Provider.TipoSaidaRepositoryProvider;
import vrimplantacao2_5.tipoRecebivel.VO.TipoEntradaVO;
import vrimplantacao2_5.tipoRecebivel.VO.TipoSaidaVO;

public class TipoSaidaRepository {
    
    private static final Logger LOG = Logger.getLogger(TipoSaidaRepository.class.getName());
    
    private final TipoSaidaRepositoryProvider provider;
    
    public TipoSaidaRepository(TipoSaidaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public TipoSaidaVO converter(TipoSaidaIMP imp) {
        TipoSaidaVO vo = new TipoSaidaVO();
        
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setId_situacaoCadastro(imp.getId_situacaoCadastro());
        vo.setBaixaEstoque(imp.isBaixaEstoque());
        vo.setGeraDevolucao(imp.isGeraDevolucao());
        vo.setEspecie(imp.getEspecie());
        vo.setTransportadorProprio(imp.isTransportadorProprio());
        vo.setDestinatarioCliente(imp.isDestinatarioCliente());
        vo.setSubsituicao(imp.isSubsituicao());
        vo.setForaEstado(imp.isForaEstado());
        vo.setConsultaPedido(imp.isConsultaPedido());
        vo.setImprimeBoleto(imp.isImprimeBoleto());
        vo.setAtualizaEscrita(imp.isAtualizaEscrita());
        vo.setUtilizaIcmsCredito(imp.isUtilizaIcmsCredito());
        vo.setNaoCreditaIcms(imp.isNaoCreditaIcms());
        vo.setDesabilitaValor(imp.isDesabilitaValor());
        vo.setAdicionaVenda(imp.isAdicionaVenda());
        vo.setGeraReceber(imp.isGeraReceber());
        vo.setUtilizaPrecoVenda(imp.isUtilizaPrecoVenda());
        vo.setNotaProdutor(imp.isNotaProdutor());
        vo.setTransferencia(imp.isTransferencia());
        vo.setId_tipoEntrada(imp.getId_tipoEntrada());
        vo.setTipo(imp.getTipo());
        vo.setCalculaIva(imp.isCalculaIva());
        vo.setUtilizaIcmsEntrada(imp.isUtilizaIcmsEntrada());
        vo.setId_contaContabilFiscalCredito(imp.getId_contaContabilFiscalCredito());
        
        
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
