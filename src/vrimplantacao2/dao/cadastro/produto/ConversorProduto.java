package vrimplantacao2.dao.cadastro.produto;

import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao2.dao.cadastro.MercadologicoDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.cadastro.ProdutoAliquotaVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * Nesta classe ficam as operações necessárias para converter {@link ProdutoIMP}
 * em {@link ProdutoVO}.
 * @author Leandro
 */
class ConversorProduto {
    
    public static final int GERAR_ID_BALANCA = -1;
    public static final int GERAR_ID_NORMAL = -2;
    public static final int ID_INVALIDO = -3;
    
    private final ProdutoDAO produtoDAO;
    private final PisCofinsDAO pisCofinsDAO = new PisCofinsDAO();
    private final FamiliaProdutoDAO familiaDAO = new FamiliaProdutoDAO();
    private final MercadologicoDAO mercadologicoDAO = new MercadologicoDAO();
    private final NcmDAO ncmDAO = new NcmDAO();
    private final CestDAO cestDAO = new CestDAO();
    private List<LojaVO> lojas;

    public ConversorProduto(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;  
    }

    /**
     * Transfere e converte as informações de um {@link ProdutoIMP} para um
     * {@link ProdutoVO}
     * @param vo VO de destino.
     * @param imp IMP de origem.
     * @throws Exception
     */
    void converterImpEmProduto(ProdutoVO vo, ProdutoIMP imp) throws Exception {
        
        try {            
            vo.setId(Integer.parseInt(imp.getImportId()));            
        } catch (NumberFormatException e) {
            vo.setId(ID_INVALIDO);
        }
        
        vo.setDescricaoCompleta(imp.getDescricaoCompleta());
        vo.setDescricaoReduzida(imp.getDescricaoReduzida());
        vo.setDescricaoGondola(imp.getDescricaoGondola());
        String cestStr = String.format("%07d", Utils.stringToInt(imp.getCest()));
        vo.setCest(cestDAO.getCestValido(cestStr));
        if (vo.getCest().getId() == 0) {
            vo.setCest(null);
        }
        String ncmStr = String.format("%08d", Utils.stringToInt(imp.getNcm()));
        
        vo.setNcm(ncmDAO.getNcm(ncmStr));
        vo.setDatacadastro(imp.getDataCadastro());
        vo.setFamiliaProduto(familiaDAO.getAnteriores().get(imp.getImportSistema(), imp.getImportLoja(), imp.getIdFamiliaProduto()));
        vo.setMargem(imp.getMargem());
        MercadologicoVO merc = mercadologicoDAO.getMercadologico(
                imp.getImportSistema(), 
                imp.getImportLoja(), 
                imp.getCodMercadologico1(), 
                imp.getCodMercadologico2(), 
                imp.getCodMercadologico3(), 
                imp.getCodMercadologico4(), 
                imp.getCodMercadologico5()
        );
        vo.setMercadologico(merc);
        if (vo.getMercadologico().getMercadologico2() == 0) {
            vo.getMercadologico().setMercadologico2(1);
        }
        if (vo.getMercadologico().getMercadologico3() == 0) {
            vo.getMercadologico().setMercadologico3(1);
        }
        //<editor-fold defaultstate="collapsed" desc="Tratamento dos produtos de Kilo e Unitário Pesável">
        
        vo.setTipoEmbalagem(TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagem()));
        if (imp.isBalanca()) {
            if (TipoEmbalagem.UN.equals(vo.getTipoEmbalagem())) {
                vo.setPesavel(true);
            } else {
                vo.setPesavel(false);
            }
        } else {
            vo.setPesavel(false);
        }        
        
        vo.setPesoBruto(imp.getPesoBruto());
        vo.setPesoLiquido(imp.getPesoLiquido());
        vo.setSituacaoCadastro(imp.getSituacaoCadastro());
        
        
        //<editor-fold defaultstate="collapsed" desc="Conversão do PIS/COFINS">
        vo.setPisCofinsCredito(pisCofinsDAO.getPisConfisCredito(imp.getPiscofinsCstCredito()));
        vo.setPisCofinsDebito(pisCofinsDAO.getPisConfisDebito(imp.getPiscofinsCstDebito()));
        vo.setPisCofinsNaturezaReceita(pisCofinsDAO.getNaturezaReceita(imp.getPiscofinsCstDebito(), imp.getPiscofinsNaturezaReceita()));
        //</editor-fold>
    }
    

    /**
     * Converte um {@link ProdutoIMP} em um {@link ProdutoComplementoVO} e o
     * incluí no {@link ProdutoVO} informado.
     * @param vo {@link ProdutoVO} de destino.
     * @param imp {@link ProdutoIMP} de origem.
     */
    void converterComplemento(ProdutoVO vo, ProdutoIMP imp, boolean somenteLojaSelecionada) throws Exception {
        
        if (lojas == null) {            
            lojas = new LojaDAO().carregar();
        }
        
        for (LojaVO loja: lojas) {
            boolean gerar = true;
            if (somenteLojaSelecionada) {
                if (loja.id != produtoDAO.getIdLojaVR()) {
                    gerar = false;
                }
            }
            if (gerar) {
                ProdutoComplementoVO complemento = vo.getComplementos().get(loja.id);
                if (complemento == null) {
                    complemento = vo.getComplementos().make(loja.id);
                    complemento.setIdLoja(loja.id);
                    complemento.setEstoqueMinimo(imp.getEstoqueMinimo());
                    complemento.setEstoqueMaximo(imp.getEstoqueMaximo());
                    if (loja.id == produtoDAO.getIdLojaVR()) {
                        complemento.setEstoque(imp.getEstoque());
                    } else {
                        complemento.setEstoque(0);
                    }
                    complemento.setPrecoDiaSeguinte(imp.getPrecovenda());
                    complemento.setPrecoVenda(imp.getPrecovenda());
                    complemento.setCustoSemImposto(imp.getCustoSemImposto());
                    complemento.setCustoComImposto(imp.getCustoComImposto());
                }
            }
        }
    }

    /**
     * Converte {@link ProdutoIMP} em {@link ProdutoAutomacaoVO} e inclui no
     * {@link ProdutoVO}.
     * @param imp {@link ProdutoIMP} de origem.
     * @param vo {@link ProdutoVO} de destino.
     */
    void converterEan(ProdutoIMP imp, ProdutoVO vo) {
        long ean = Utils.stringToLong(imp.getEan());
        if (ean != 0) {
            ProdutoAutomacaoVO automacao = vo.getEans().get(ean);
            if (automacao == null) {
                automacao = vo.getEans().make(ean);
                automacao.setCodigoBarras(ean);
                automacao.setPesoBruto(imp.getPesoBruto());
                automacao.setQtdEmbalagem(imp.getQtdEmbalagem());
                automacao.setTipoEmbalagem(TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagem()));
                automacao.setDun14(String.valueOf(automacao.getCodigoBarras()).length() > 13);
            }
        }
    }

    void converterAliquota(ProdutoVO vo, ProdutoIMP imp) throws Exception {
        ProdutoAliquotaVO aliquota = vo.getAliquotas().get(produtoDAO.getIdLojaVR(), Parametros.get().getUfPadrao().getId());
        if (aliquota == null) {
            aliquota = vo.getAliquotas().make(produtoDAO.getIdLojaVR(), Parametros.get().getUfPadrao().getId());
            aliquota.setEstado(Parametros.get().getUfPadrao());
            int icmsCst = imp.getIcmsCst();
            double icmsAliq = 0;
            double icmsReducao = 0;
            
            if (icmsCst == 20 || icmsCst == 0) {
                icmsAliq = imp.getIcmsAliq();
                icmsReducao = imp.getIcmsReducao();                
            }
            
            aliquota.setAliquotaCredito(Icms.getIcms(icmsCst, icmsAliq, icmsReducao));
            aliquota.setAliquotaDebito(Icms.getIcms(icmsCst, icmsAliq, icmsReducao));
            aliquota.setAliquotaDebitoForaEstado(Icms.getIcms(icmsCst, icmsAliq, icmsReducao));
            aliquota.setAliquotaCreditoForaEstado(Icms.getIcms(icmsCst, icmsAliq, icmsReducao));
            aliquota.setAliquotaDebitoForaEstadoNf(Icms.getIcms(icmsCst, icmsAliq, icmsReducao));
            if (icmsCst == 20) {
                double aliq = icmsAliq - (icmsAliq * (icmsReducao / 100));
                aliquota.setAliquotaConsumidor(Icms.getIcms(0, MathUtils.round(aliq, 0), 0));
            } else {
                aliquota.setAliquotaConsumidor(Icms.getIcms(icmsCst, icmsAliq, 0));
            }
        }
    }

    /**
     * Transforma os dados de {@link ProdutoIMP} em {@link ProdutoAnteriorVO}
     * @param imp Produto de importação a ser transformado.
     * @param destino Objeto de destino.
     * @return Produto de importação transformado em produto anterior.
     */
    void converterImpEmAnterior(ProdutoIMP imp, ProdutoAnteriorVO destino) {
        if (imp != null && destino != null) {
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
            destino.setEstoque(imp.getEstoque());
            destino.seteBalanca(imp.isBalanca());
            destino.setCustosemimposto(imp.getCustoSemImposto());
            destino.setCustocomimposto(imp.getCustoComImposto());
            destino.setMargem(imp.getMargem());
            destino.setPrecovenda(imp.getPrecovenda());
            destino.setNcm(imp.getNcm());
            destino.setCest(imp.getCest());
            destino.setContadorImportacao(0);
        }
    }    
    
    void converterImpEmProdutoPart2(ProdutoIMP imp, ProdutoVO vo) throws Exception {
        
        String[] chave = new String[]{
            imp.getImportSistema(), 
            imp.getImportLoja(), 
            imp.getImportId()
        };       
        
        ProdutoAnteriorVO anterior = vo.getCodigosAnteriores().get(chave);
        if (anterior == null) {
            anterior = vo.getCodigosAnteriores().make(chave);
            converterImpEmAnterior(imp, anterior);
        }
        
        converterEan(imp, vo);
        converterComplemento(vo, imp, false);
        converterAliquota(vo, imp);
        
        String[] chaveEAN = new String[]{
            imp.getImportSistema(), 
            imp.getImportLoja(), 
            imp.getImportId(), 
            imp.getEan()
        };
        
        ProdutoAnteriorEanVO eanAnterior = anterior.getEans().get(chaveEAN);
        if (eanAnterior == null) {
            eanAnterior = anterior.getEans().make(chaveEAN);
            eanAnterior.setEan(imp.getEan());
            eanAnterior.setQtdEmbalagem(imp.getQtdEmbalagem());
            eanAnterior.setTipoEmbalagem(imp.getTipoEmbalagem());
            eanAnterior.setValor(0);
        }
    }

    MultiMap<String, ProdutoVO> converterListagem(MultiMap<String, ProdutoIMP> organizados) throws Exception {        
        
        ProgressBar.setStatus("Produtos - Convertendo a listagem...");
        MultiMap<String, ProdutoVO> result = new MultiMap<>(3);
        ProgressBar.setMaximum(organizados.size());
         
        for (KeyList<String> key : organizados.keySet()) {
            String[] chave = new String[]{
                key.get(0), 
                key.get(1), 
                key.get(2)
            };
            ProdutoIMP imp = organizados.get(key);
            
            ProdutoVO vo = result.get(chave);
            if (vo == null) {
                vo = new ProdutoVO();
                result.put(vo, chave);
                converterImpEmProduto(vo, imp);
            }
            converterImpEmProdutoPart2(imp, vo);
            ProgressBar.next();
        }
        
        return result;
    }
    
}
