package vrimplantacao2.dao.cadastro.produto2;

import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.importacao.ProdutoIMP;

class ProdutoConverter {
    
    private final ProdutoRepositoryProvider provider;

    ProdutoConverter(ProdutoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    /**
     * Transforma os dados de {@link ProdutoIMP} em {@link ProdutoAnteriorVO}
     *
     * @param imp Produto de importação a ser transformado.
     * @return Produto de importação transformado em produto anterior.
     */
    public ProdutoAnteriorVO converterImpEmAnterior(ProdutoIMP imp) {
        ProdutoAnteriorVO destino = new ProdutoAnteriorVO();
        destino.setImportSistema(imp.getImportSistema());
        destino.setImportLoja(imp.getImportLoja());
        destino.setImportId(imp.getImportId());
        destino.setDescricao(imp.getDescricaoCompleta());
        destino.setPisCofinsCredito(imp.getPiscofinsCstCredito());
        destino.setPisCofinsDebito(imp.getPiscofinsCstDebito());
        destino.setPisCofinsNaturezaReceita(imp.getPiscofinsNaturezaReceita());

        destino.setIcmsCst(imp.getIcmsCst());
        destino.setIcmsAliq(imp.getIcmsAliq());
        destino.setIcmsReducao(imp.getIcmsReducao());

        destino.setIcmsCstSaida(imp.getIcmsCstSaida());
        destino.setIcmsAliqSaida(imp.getIcmsAliqSaida());
        destino.setIcmsReducaoSaida(imp.getIcmsReducaoSaida());

        destino.setIcmsCstSaidaForaEstado(imp.getIcmsCstSaidaForaEstado());
        destino.setIcmsAliqSaidaForaEstado(imp.getIcmsAliqSaidaForaEstado());
        destino.setIcmsReducaoSaidaForaEstado(imp.getIcmsReducaoSaidaForaEstado());

        destino.setIcmsCstSaidaForaEstadoNf(imp.getIcmsCstSaidaForaEstadoNF());
        destino.setIcmsAliqSaidaForaEstadoNf(imp.getIcmsAliqSaidaForaEstadoNF());
        destino.setIcmsReducaoSaidaForaEstadoNf(imp.getIcmsReducaoSaidaForaEstadoNF());

        destino.setIcmsCstConsumidor(imp.getIcmsCstConsumidor());
        destino.setIcmsAliqConsumidor(imp.getIcmsAliqConsumidor());
        destino.setIcmsReducaoConsumidor(imp.getIcmsReducaoConsumidor());
        
        destino.setIcmsCstEntrada(imp.getIcmsCstEntrada());
        destino.setIcmsAliqEntrada(imp.getIcmsAliqEntrada());
        destino.setIcmsReducaoEntrada(imp.getIcmsReducaoEntrada());

        destino.setIcmsCstEntradaForaEstado(imp.getIcmsCstEntradaForaEstado());
        destino.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqEntradaForaEstado());
        destino.setIcmsReducaoEntradaForaEstado(imp.getIcmsReducaoEntradaForaEstado());

        destino.setIcmsDebitoId(imp.getIcmsDebitoId());
        destino.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoForaEstadoId());
        destino.setIcmsDebitoForaEstadoIdNf(imp.getIcmsDebitoForaEstadoId());

        destino.setIcmsCreditoId(imp.getIcmsCreditoId());
        destino.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoForaEstadoId());

        destino.setIcmsConsumidorId(imp.getIcmsConsumidorId());

        destino.setEstoque(imp.getEstoque());
        destino.seteBalanca(imp.isBalanca());
        destino.setCustosemimposto(imp.getCustoSemImposto());
        destino.setCustocomimposto(imp.getCustoComImposto());
        destino.setMargem(imp.getMargem());
        destino.setPrecovenda(imp.getPrecovenda());
        destino.setNcm(imp.getNcm());
        destino.setCest(imp.getCest());
        destino.setContadorImportacao(0);
        if (!"".equals(imp.getCodigoSped().trim())) {
            destino.setCodigoSped(imp.getCodigoSped());
        } else {
            destino.setCodigoSped(imp.getImportId());
        }
        destino.setSituacaoCadastro(imp.getSituacaoCadastro());
        return destino;
    }
    
    /**
     * Converte um {@link ProdutoIMP} em um {@link ProdutoAnteriorEanVO}.
     *
     * @param imp {@link ProdutoIMP} a ser convertido.
     * @return {@link ProdutoAnteriorEanVO} convertido.
     */
    public ProdutoAnteriorEanVO converterAnteriorEAN(ProdutoIMP imp) {
        ProdutoAnteriorEanVO eanAnterior = new ProdutoAnteriorEanVO();
        eanAnterior.setImportSistema(imp.getImportSistema());
        eanAnterior.setImportLoja(imp.getImportLoja());
        eanAnterior.setImportId(imp.getImportId());
        eanAnterior.setEan(imp.getEan());
        eanAnterior.setQtdEmbalagem(imp.getQtdEmbalagem());
        eanAnterior.setTipoEmbalagem(imp.getTipoEmbalagem());
        eanAnterior.setValor(0);
        return eanAnterior;
    }
    
}
