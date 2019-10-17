package vrimplantacao2.vo.cadastro;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao2.dao.cadastro.produto.PisCofinsDAO;
import vrimplantacao2.utils.Factory;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.NcmVO;
import vrimplantacao2.vo.enums.NormaReposicao;
import vrimplantacao2.vo.enums.PisCofinsVO;
import vrimplantacao2.vo.enums.TipoEmbalagem;

public class ProdutoVO {
    
    private int id = 0;
    private int idProduto2 = 0;
    private String impId = "";
    private String descricaoCompleta = "SEM DESCRICAO";
    private String descricaoReduzida = "SEM DESCRICAO";
    private String descricaoGondola = "SEM DESCRICAO";
    private MercadologicoVO mercadologico = new MercadologicoVO();
    private TipoEmbalagem tipoEmbalagem = TipoEmbalagem.UN;
    private FamiliaProdutoVO familiaProduto = null;
    private Date datacadastro = new Date();
    private Date dataalteracao = new Date();
    private int qtdEmbalagem = 1;
    private int validade = 0;
    private double pesoBruto = 0;
    private double pesoLiquido = 0;
    private double margem = 0;
    private NcmVO ncm = new NcmVO();
    private PisCofinsVO pisCofinsDebito = PisCofinsDAO.PISCOFINS_DEBITO_PADRAO;
    private PisCofinsVO pisCofinsCredito = PisCofinsDAO.PISCOFINS_CREDITO_PADRAO;
    private NaturezaReceitaVO pisCofinsNaturezaReceita;
    private boolean pesavel = false;
    private boolean vendaPdv = true;
    private CestVO cest = null;
    private NormaReposicao normaCompra = NormaReposicao.CAIXA;
    private NormaReposicao normaReposicao = NormaReposicao.CAIXA;
    private boolean sugestaoCotacao = true;
    private boolean sugestaoPedido = true;
    private int idFornecedorFabricante = 0;
    private int excecao = 0;
    private int idComprador = 1;
    private boolean aceitaMultiplicacaoPDV = true;
    private int idDivisaoFornecedor = 0;
    private TipoEmbalagem tipoEmbalagemVolume = TipoEmbalagem.UN;
    private double volume = 1.0;
    
    private final MultiMap<Long, ProdutoAutomacaoVO> eans = new MultiMap<>(
        new Factory<ProdutoAutomacaoVO>() {
            @Override
            public ProdutoAutomacaoVO make() {
                ProdutoAutomacaoVO produtoAutomacaoVO = new ProdutoAutomacaoVO();
                produtoAutomacaoVO.setProduto(ProdutoVO.this);
                return produtoAutomacaoVO;
            }            
        }, 1
    );
    private final MultiMap<String, ProdutoAnteriorVO> codigosAnteriorVOs = new MultiMap<>(
        new Factory<ProdutoAnteriorVO>() {
            @Override
            public ProdutoAnteriorVO make() {
                ProdutoAnteriorVO vo = new ProdutoAnteriorVO();
                vo.setCodigoAtual(ProdutoVO.this);
                return vo;
            }            
        }, 3
    );
    private final MultiMap<Integer, ProdutoComplementoVO> complementos = new MultiMap<>(
        new Factory<ProdutoComplementoVO>() {
            @Override
            public ProdutoComplementoVO make() {
                ProdutoComplementoVO vo = new ProdutoComplementoVO();
                vo.setProduto(ProdutoVO.this);
                return vo;
            }            
        }, 1
    );
    
    private final MultiMap<Integer, ProdutoAliquotaVO> aliquotas = new MultiMap<>(
        new Factory<ProdutoAliquotaVO>() {
            @Override
            public ProdutoAliquotaVO make() {
                ProdutoAliquotaVO vo = new ProdutoAliquotaVO();
                vo.setProduto(ProdutoVO.this);
                return vo;
            }            
        }, 2
    );

    
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProduto2() {
        return this.idProduto2;
    }

    public void setIdProduto2(int idProduto2) {
        this.idProduto2 = idProduto2;
    }

    public String getImpId() {
        return this.impId;
    }

    public void setImpId(String impId) {
        this.impId = impId;
    }
    
    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = Utils.acertarTexto(descricaoCompleta, 60, "PRODUTO SEM DESCRICAO");
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = Utils.acertarTexto(descricaoReduzida, 22, "PRODUTO SEM DESCRICAO");
    }

    public void setDescricaoGondola(String descricaoGondola) {
        this.descricaoGondola = Utils.acertarTexto(descricaoGondola, 60, "PRODUTO SEM DESCRICAO");
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public void setMercadologico(MercadologicoVO mercadologico) {
        this.mercadologico = mercadologico;
    }
    
    public void setTipoEmbalagem(TipoEmbalagem tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public void setFamiliaProduto(FamiliaProdutoVO familiaProduto) {
        this.familiaProduto = familiaProduto;
    }

    public void setDatacadastro(Date datacadastro) {
        this.datacadastro = datacadastro;
    }
    
    public void setDataAlteracao (Date dataalteracao) {
        this.dataalteracao = dataalteracao;
    }

    public void setValidade(int validade) {
        this.validade = validade > 0 ? validade : 0;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = Utils.truncar2((pesoBruto > 0 ? pesoBruto : 0), 4);
    }

    public void setPesoLiquido(double pesoLiquido) {
        this.pesoLiquido = Utils.truncar2((pesoLiquido > 0 ? pesoLiquido : 0), 4);
    }

    public void setMargem(double margem) {
        this.margem = MathUtils.trunc(margem, 2, 999999.99D);
    }

    public void setNcm(NcmVO ncm) {
        this.ncm = ncm;
    }

    public void setPisCofinsDebito(PisCofinsVO pisCofinsDebito) {
        this.pisCofinsDebito = pisCofinsDebito == null ? PisCofinsDAO.PISCOFINS_DEBITO_PADRAO : pisCofinsDebito;
    }

    public void setPisCofinsCredito(PisCofinsVO pisCofinsCredito) {
        this.pisCofinsCredito = pisCofinsCredito == null ? PisCofinsDAO.PISCOFINS_CREDITO_PADRAO : pisCofinsCredito;
    }

    public void setPisCofinsNaturezaReceita(NaturezaReceitaVO pisCofinsNaturezaReceita) {
        this.pisCofinsNaturezaReceita = pisCofinsNaturezaReceita;
    }

    public void setPesavel(boolean pesavel) {
        this.pesavel = pesavel;
    }

    public void setVendaPdv(boolean vendaPdv) {
        this.vendaPdv = vendaPdv;
    }

    public void setCest(CestVO cest) {
        this.cest = cest;
    }

    public void setNormaCompra(NormaReposicao normaCompra) {
        this.normaCompra = normaCompra;
    }

    public void setNormaReposicao(NormaReposicao normaReposicao) {
        this.normaReposicao = normaReposicao;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public String getDescricaoGondola() {
        return descricaoGondola;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public MercadologicoVO getMercadologico() {
        return mercadologico;
    }

    public TipoEmbalagem getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public FamiliaProdutoVO getFamiliaProduto() {
        return familiaProduto;
    }

    public Date getDatacadastro() {
        return datacadastro;
    }
    
    public Date getDataAlteracao() {
        return dataalteracao;
    }

    public int getValidade() {
        return validade;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public double getPesoLiquido() {
        return pesoLiquido;
    }

    public double getMargem() {
        return margem;
    }

    public NcmVO getNcm() {
        return ncm;
    }

    public PisCofinsVO getPisCofinsDebito() {
        return pisCofinsDebito;
    }

    public PisCofinsVO getPisCofinsCredito() {
        return pisCofinsCredito;
    }

    public NaturezaReceitaVO getPisCofinsNaturezaReceita() {
        return pisCofinsNaturezaReceita;
    }

    public boolean isPesavel() {
        return pesavel;
    }

    public boolean isVendaPdv() {
        return vendaPdv;
    }

    public CestVO getCest() {
        return cest;
    }

    public NormaReposicao getNormaCompra() {
        return normaCompra;
    }

    public NormaReposicao getNormaReposicao() {
        return normaReposicao;
    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public MultiMap<String, ProdutoAnteriorVO> getCodigosAnteriores() {
        return codigosAnteriorVOs;
    }

    public MultiMap<Long, ProdutoAutomacaoVO> getEans() {
        return eans;
    }

    public MultiMap<Integer, ProdutoComplementoVO> getComplementos() {
        return complementos;
    }

    public MultiMap<Integer, ProdutoAliquotaVO> getAliquotas() {
        return aliquotas;
    }

    public boolean isSugestaoCotacao() {
        return sugestaoCotacao;
    }

    public void setSugestaoCotacao(boolean sugestaoCotacao) {
        this.sugestaoCotacao = sugestaoCotacao;
    }

    public boolean isSugestaoPedido() {
        return sugestaoPedido;
    }

    public void setSugestaoPedido(boolean sugestaoPedido) {
        this.sugestaoPedido = sugestaoPedido;
    }    

    public int getIdFornecedorFabricante() {
        return idFornecedorFabricante;
    }

    public void setIdFornecedorFabricante(int idFornecedorFabricante) {
        this.idFornecedorFabricante = idFornecedorFabricante;
    }

    public int getExcecao() {
        return excecao;
    }

    public void setExcecao(int excecao) {
        this.excecao = excecao;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public boolean isAceitaMultiplicacaoPDV() {
        return aceitaMultiplicacaoPDV;
    }

    public void setAceitaMultiplicacaoPDV(boolean aceitaMultiplicacaoPDV) {
        this.aceitaMultiplicacaoPDV = aceitaMultiplicacaoPDV;
    }

    public int getIdDivisaoFornecedor() {
        return idDivisaoFornecedor;
    }

    public void setIdDivisaoFornecedor(int idDivisaoFornecedor) {
        this.idDivisaoFornecedor = idDivisaoFornecedor;
    }

    public TipoEmbalagem getTipoEmbalagemVolume() {
        return tipoEmbalagemVolume;
    }

    public void setTipoEmbalagemVolume(TipoEmbalagem tipoEmbalagemVolume) {
        this.tipoEmbalagemVolume = tipoEmbalagemVolume == null ? TipoEmbalagem.UN : tipoEmbalagemVolume;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
    
}
