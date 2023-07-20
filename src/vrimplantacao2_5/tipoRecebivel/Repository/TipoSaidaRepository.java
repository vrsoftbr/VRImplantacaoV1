package vrimplantacao2_5.tipoRecebivel.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.tipoRecebivel.IMP.TipoSaidaIMP;
import vrimplantacao2_5.tipoRecebivel.Provider.TipoSaidaRepositoryProvider;
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
        vo.setId_contaContabilFiscalDebito(imp.getId_contaContabilFiscalDebito());
        vo.setId_historicoPadrao(imp.getId_historicoPadrao());
        vo.setEntraEstoque(imp.isEntraEstoque());
        vo.setVendaIndustria(imp.isVendaIndustria());
        vo.setId_notaSaidaMensagem(imp.getId_notaSaidaMensagem());
        vo.setGeraContrato(imp.isGeraContrato());
        vo.setContabilidadePadrao(imp.isContabilidadePadrao());
        vo.setContabiliza(imp.isContabiliza());
        vo.setAtualizaTroca(imp.isAtualizaTroca());
        vo.setCreditaPisCofins(imp.isCreditaPisCofins());
        vo.setConsumidorFinal(imp.isConsumidorFinal());
        vo.setId_tipoPisCofins(imp.getId_tipoPisCofins());
        vo.setId_aliquota(imp.getId_aliquota());
        vo.setFabricacaoPropria(imp.isFabricacaoPropria());
        vo.setUtilizaPrecoCusto(imp.isUtilizaPrecoCusto());
        vo.setConverterTodasAliquotas(imp.isConverterTodasAliquotas());
        vo.setId_tipoSaida(imp.getId_tipoSaida());
        vo.setGeraExportacao(imp.isGeraExportacao());
        vo.setId_produto(imp.getId_produto());
        vo.setUtilizaTributoCadastroDebito(imp.isUtilizaTributoCadastroDebito());
        vo.setConverterAliquota(imp.isConverterAliquota());
        vo.setPlanoConta1(imp.getPlanoConta1());
        vo.setPlanoConta2(imp.getPlanoConta2());
        vo.setNotaMei(imp.isNotaMei());
        vo.setUtilizaCustoMedio(imp.isUtilizaCustoMedio());

        return vo;

    }

    public void importarTipoSaida(List<TipoSaidaIMP> saida) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Saida existentes...", saida.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (TipoSaidaIMP imp : saida) {
                TipoSaidaVO vo = new TipoSaidaVO();
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
