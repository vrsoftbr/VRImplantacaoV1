package vrimplantacao2.dao.cadastro.produto2.converter;

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;
import vr.core.utils.StringUtils;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;
import vrimplantacao2.dao.cadastro.produto2.UnificadorProdutoRepository;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.PisCofinsVO;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class ProdutoConverter {
    
    private static final Logger LOG = Logger.getLogger(UnificadorProdutoRepository.class.getName());
    private final ProdutoRepositoryProvider provider;
    private final ProdutoVoConverter produtoVoConverter;
        
    private Map<String, Map.Entry<String, Integer>> divisoes;
    private Map<String, Integer> pautaExcecao;

    public ProdutoConverter(ProdutoRepositoryProvider provider) throws Exception {
        this.provider = provider;
        this.produtoVoConverter = new ProdutoVoConverter(provider);
        this.divisoes = provider.getDivisoesAnteriores();
    }
    
    /**
     * Transforma os dados de {@link ProdutoIMP} em {@link ProdutoAnteriorVO}
     *
     * @param imp Produto de importação a ser transformado.
     * @return Produto de importação transformado em produto anterior.
     */
    public ProdutoAnteriorVO converterEmAnterior(ProdutoIMP imp) {
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
    
    /**
     * Converte {@link ProdutoIMP} em {@link ProdutoAutomacaoVO} e inclui no
     * {@link ProdutoVO}.
     *
     * @param imp {@link ProdutoIMP} de origem.
     * @param ean EAN que será gravado.
     * @param unidade
     * @return {@link ProdutoAutomacaoVO} convertido;
     */
    public ProdutoAutomacaoVO converterEAN(ProdutoIMP imp, long ean, TipoEmbalagem unidade) {
        ProdutoAutomacaoVO automacao = new ProdutoAutomacaoVO();
        automacao.setCodigoBarras(ean);
        automacao.setPesoBruto(imp.getPesoBruto());
        automacao.setQtdEmbalagem(imp.getQtdEmbalagem());
        automacao.setTipoEmbalagem(unidade);
        automacao.setDun14(String.valueOf(automacao.getCodigoBarras()).length() > 13);
        return automacao;
    }
    
    private Map<String, Integer> fabricantes = null;
    private Map<String, Integer> compradores = null;
    private Map<String, Integer> codigosAnp = null;

    public ProdutoVO converterEmProduto(ProdutoIMP imp) throws Exception {
        
        boolean manterDescricao = provider.getOpcoes().contains(OpcaoProduto.MANTER_DESCRICAO_PRODUTO);

        if (fabricantes == null) {
            fabricantes = provider.getFornecedoresImportados();
        }
        if (compradores == null) {
            compradores = provider.getCompradores();
        }
        if (codigosAnp == null) {
            codigosAnp = provider.getCodigoAnp();
        }

        ProdutoVO vo = new ProdutoVO();

        vo.setManterDescricao(manterDescricao);
        vo.setDescricaoCompleta(imp.getDescricaoCompleta());
        if ("SEM DESCRICAO".equals(imp.getDescricaoReduzida())) {
            vo.setDescricaoReduzida(vo.getDescricaoCompleta());
        }
        if ("SEM DESCRICAO".equals(imp.getDescricaoGondola())) {
            vo.setDescricaoGondola(vo.getDescricaoCompleta());
        }
        vo.setDescricaoReduzida(imp.getDescricaoReduzida());
        vo.setDescricaoGondola(imp.getDescricaoGondola());

        if (vo.getId() == 1) {
            System.out.println("imp " + imp.getDescricaoReduzida());
            System.out.println("vo " + vo.getDescricaoReduzida());
        }

        vo.setQtdEmbalagem(imp.getQtdEmbalagemCotacao() == 0 ? 1 : imp.getQtdEmbalagemCotacao());
        vo.setSugestaoCotacao(imp.isSugestaoCotacao());
        vo.setSugestaoPedido(imp.isSugestaoPedido());
        vo.setCest(provider.tributo().getCest(imp.getCest()));
        if (vo.getCest().getId() == 0) {
            vo.setCest(null);
        }
        vo.setNcm(provider.tributo().getNcm(imp.getNcm()));
        vo.setDatacadastro(imp.getDataCadastro());
        if (vo.getDatacadastro() == null) {
            vo.setDatacadastro(new Date());
        }
        vo.setDataAlteracao(imp.getDataAlteracao());
        if (vo.getDataAlteracao() == null) {
            vo.setDataAlteracao(new Date());
        }
        Integer fornecedorFabricante = fabricantes.get(imp.getFornecedorFabricante());
        if (fornecedorFabricante != null) {
            vo.setIdFornecedorFabricante(fornecedorFabricante);
        } else {
            vo.setIdFornecedorFabricante(1);
        }

        vo.setFamiliaProduto(provider.getFamiliaProduto(imp.getIdFamiliaProduto()));
        vo.setMargem(imp.getMargem());
        vo.setMargemMinima(imp.getMargemMinima());
        vo.setMargemMaxima(imp.getMargemMaxima());
        
        vo.setMercadologico(this.produtoVoConverter.converterMercadologico(imp));

        if (vo.getMercadologico().getMercadologico2() == 0) {
            vo.getMercadologico().setMercadologico2(1);
        }
        if (vo.getMercadologico().getMercadologico3() == 0) {
            vo.getMercadologico().setMercadologico3(1);
        }

        vo = this.produtoVoConverter.tratarSituacaoDaBalanca(imp, vo);

        vo.setPesoBruto(imp.getPesoBruto());
        vo.setPesoLiquido(imp.getPesoLiquido());

        vo = this.produtoVoConverter.convertPisCofins(imp, vo);

        vo.setValidade(imp.getValidade());
        vo.setExcecao(obterPautaFiscal(imp.getPautaFiscalId()));
        vo.setVendaPdv(imp.isVendaPdv());
        vo.setAceitaMultiplicacaoPDV(imp.isAceitaMultiplicacaoPDV());

        //Importação da divisão de fornecedores
        Map.Entry<String, Integer> divisaoFornecedor = this.divisoes.get(imp.getDivisao());
        if (divisaoFornecedor != null && divisaoFornecedor.getValue() != null) {
            vo.setIdDivisaoFornecedor(divisaoFornecedor.getValue());
        }

        /**
         * Busca e se existir, relaciona o produto com o comprador.
         */
        Integer comprador = compradores.get(imp.getIdComprador());
        if (comprador != null) {
            vo.setIdComprador(comprador);
        }

        if (imp.getTipoEmbalagemVolume() == null || imp.getTipoEmbalagemVolume().trim().equals("")) {
            vo.setTipoEmbalagemVolume(vo.getTipoEmbalagem());
        } else {
            vo.setTipoEmbalagemVolume(TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagemVolume()));
        }
        vo.setVolume(imp.getVolume());
        vo.setVendaControlada(imp.isVendaControlada());
        vo.setProdutoecommerce(imp.isProdutoECommerce());
        Integer codigoANP = codigosAnp.get(imp.getCodigoAnp());
        if (codigoANP != null) {
            vo.setCodigoAnp(codigoANP);
        }

        vo.setNumeroparcela(imp.getNumeroparcela());

        return vo;
        
    }

    public int obterPautaFiscal(String pautaFiscalId) throws Exception {
        if (pautaExcecao == null) {
            pautaExcecao = provider.getPautaExcecao();
        }
        if (pautaFiscalId != null) {
            Integer excecao = pautaExcecao.get(pautaFiscalId);
            if (excecao != null) {
                return excecao;
            }
        }
        return 0;
    }
    
}

class ProdutoVoConverter {
    private static final Logger LOG = Logger.getLogger(ProdutoVoConverter.class.getName());
    private final ProdutoRepositoryProvider provider;

    public ProdutoVoConverter(ProdutoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public MercadologicoVO converterMercadologico(ProdutoIMP imp) throws Exception {
        MercadologicoVO merc = provider.getMercadologico(
                fillNull(imp.getCodMercadologico1()),
                fillNull(imp.getCodMercadologico2()),
                fillNull(imp.getCodMercadologico3()),
                fillNull(imp.getCodMercadologico4()),
                fillNull(imp.getCodMercadologico5())
        );
        if (merc == null) {
            LOG.severe("Mercadológico vazio no item " + imp.getImportId() + " - " + imp.getDescricaoCompleta());
        }
        if (merc.getNivel() != provider.getNivelMaximoMercadologico()) {
            if (provider.getOpcoes().contains(OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC)) {
                if (merc.getNivel() == 1) {
                    merc.setMercadologico2(1);
                    merc.setMercadologico3(1);
                    if (provider.getNivelMaximoMercadologico() >= 4) {
                        merc.setMercadologico4(1);
                    }
                    if (provider.getNivelMaximoMercadologico() == 5) {
                        merc.setMercadologico5(1);
                    }
                } else if (merc.getNivel() == 2) {
                    merc.setMercadologico3(1);
                    if (provider.getNivelMaximoMercadologico() >= 4) {
                        merc.setMercadologico4(1);
                    }
                    if (provider.getNivelMaximoMercadologico() == 5) {
                        merc.setMercadologico5(1);
                    }
                } else if (merc.getNivel() == 3) {
                    if (provider.getNivelMaximoMercadologico() >= 4) {
                        merc.setMercadologico4(1);
                    }
                    if (provider.getNivelMaximoMercadologico() == 5) {
                        merc.setMercadologico5(1);
                    }
                } else if (merc.getNivel() == 4) {
                    if (provider.getNivelMaximoMercadologico() == 5) {
                        merc.setMercadologico5(1);
                    }
                }
            } else {
                merc = provider.getMercadologico("", "", "", "", "");
            }
        }
        return merc;
    }

    private String fillNull(String value) {
        return value != null ? value : "";
    }
    
    public ProdutoVO tratarSituacaoDaBalanca(ProdutoIMP imp, ProdutoVO vo) {
        boolean produtoPesavel = imp.isBalanca();
        long ean = StringUtils.toLong(imp.getEan(), -2);
        TipoEmbalagem unidade = TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagem());
        TipoEmbalagem unidadeCotacao = TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagemCotacao(), true);
        boolean isProdutoKg = unidade == TipoEmbalagem.KG;
        boolean isEanValido = ean > 99999;
        boolean produtoQueSeEnviaParaBalanca = produtoPesavel || isProdutoKg;
        
        if (isEanValido && produtoQueSeEnviaParaBalanca) {
            produtoPesavel = false;
            unidade = TipoEmbalagem.UN;
        }
        
        if (produtoQueSeEnviaParaBalanca) {
            if (TipoEmbalagem.KG.equals(unidade)) {
                vo.setPesavel(true);
            } else {
                vo.setPesavel(false);
            }
            vo.setTipoEmbalagem(unidade);
        } else {
            if (unidadeCotacao != null) {
                vo.setTipoEmbalagem(unidadeCotacao);
            } else {
                vo.setTipoEmbalagem(unidade);
            }
            vo.setPesavel(false);
        }
        return vo;
    }
    
    public ProdutoVO convertPisCofins(ProdutoIMP imp, ProdutoVO vo) throws Exception {

        int pisCofinsDebito, pisCofinsCredito;
        if (imp.getPiscofinsCstDebito() != 0 && imp.getPiscofinsCstCredito() == 0) {
            pisCofinsDebito = imp.getPiscofinsCstDebito();
            pisCofinsCredito = converterDebitoParaCredito(imp.getPiscofinsCstDebito());
        } else if (imp.getPiscofinsCstDebito() == 0 && imp.getPiscofinsCstCredito() != 0) {
            pisCofinsDebito = converterCreditoParaDebito(imp.getPiscofinsCstCredito());
            pisCofinsCredito = imp.getPiscofinsCstCredito();
        } else {
            pisCofinsDebito = imp.getPiscofinsCstDebito();
            pisCofinsCredito = imp.getPiscofinsCstCredito();
        }
        PisCofinsVO pDeb = provider.tributo().getPisConfisDebito(pisCofinsDebito);
        PisCofinsVO pCre = provider.tributo().getPisConfisCredito(pisCofinsCredito);

        if (pDeb == null) {
            pDeb = provider.tributo().getPisConfisDebito(converterCreditoParaDebito(pisCofinsCredito));
        }
        if (pCre == null) {
            pCre = provider.tributo().getPisConfisCredito(converterDebitoParaCredito(pisCofinsDebito));
        }

        vo.setPisCofinsDebito(pDeb);
        vo.setPisCofinsCredito(pCre);
        vo.setPisCofinsNaturezaReceita(getNaturezaReceita(vo.getPisCofinsDebito().getCst(), imp.getPiscofinsNaturezaReceita()));
        
        return vo;
    }
    
    public NaturezaReceitaVO getNaturezaReceita(int cstDebito, int naturezaReceita) throws Exception {

        NaturezaReceitaVO result = provider.tributo().getNaturezaReceita(cstDebito, naturezaReceita);

        if (result == null) {
            if (cstDebito == 7) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 999);
            } else if (cstDebito == 5) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 409);
            } else if (cstDebito == 4) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 403);
            } else if (cstDebito == 9) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 999);
            } else if (cstDebito == 2) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 403);
            } else if (cstDebito == 3) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 940);
            } else if (cstDebito == 6) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 999);
            } else if (cstDebito == 8) {
                result = provider.tributo().getNaturezaReceita(cstDebito, 999);
            }
        }

        return result;
    }
    
    public int converterCreditoParaDebito(int piscofinsCstDebito) {
        switch (piscofinsCstDebito) {
            case 50:
                return 1; //"TRIBUTADO"
            case 60:
                return 2; //"TRIB - ALIQ DIFERENCIADA"
            case 51:
                return 3; //"TRIB - ALIQ POR PRODUTO"
            case 70:
                return 4; //"MONOFASICO"
            case 75:
                return 5; //"SUBSTITUIDO"
            case 73:
                return 6; //"TRIB - ALIQUOTA ZERO"
            case 74:
                return 8; //"SEM INCIDENCIA CONTRIBUICAO"
            case 72:
                return 9; //"SUSPENCAO"
            case 99:
                return 49;
            default:
                return 7; //"ISENTO"
        }
    }

    public int converterDebitoParaCredito(int piscofinsCstDebito) {
        switch (piscofinsCstDebito) {
            case 1:
                return 50; //"TRIBUTADO (E)"
            case 2:
                return 60; //"TRIB - ALIQ DIFERENCIADA(E)"
            case 3:
                return 51; //"TRIB - ALIQ POR PRODUTO(E)"
            case 4:
                return 70; //"MONOFASICO (E)"
            case 5:
                return 75; //"SUBSTITUIDO (E)"
            case 6:
                return 73; //"TRIB - ALIQUOTA ZERO(E)"
            case 8:
                return 74; //"SEM INCIDENCIA CONTRIBUICAO(E)"
            case 9:
                return 72; //"SUSPENCAO"
            case 49:
                return 99;
            default:
                return 71; //"ISENTO (E)"
        }
    }
}