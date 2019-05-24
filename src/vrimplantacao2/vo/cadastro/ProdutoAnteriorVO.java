package vrimplantacao2.vo.cadastro;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.Factory;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class ProdutoAnteriorVO {
    private String importSistema;
    private String importLoja;
    private String importId;
    private String descricao;
    private ProdutoVO codigoAtual;
    private int pisCofinsCredito = -1;
    private int pisCofinsDebito = -1;
    private int pisCofinsNaturezaReceita = -1;
    private int icmsCst = -1;
    private double icmsAliq = -1;
    private double icmsReducao = -1;
    private double estoque = -1;
    private boolean eBalanca = false;
    private double custosemimposto = 0;
    private double custocomimposto = 0;
    private double margem = 0;
    private double precovenda = 0;
    private String ncm;
    private String cest;
    private int contadorImportacao = 0;
    private boolean novo = false;
    private String codigoSped = "";
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private Date dataHora;
    
    private final MultiMap<String, ProdutoAnteriorEanVO> eans = new MultiMap<>(
        new Factory<ProdutoAnteriorEanVO>() {
            @Override
            public ProdutoAnteriorEanVO make() {
                ProdutoAnteriorEanVO ean = new ProdutoAnteriorEanVO();
                ean.setImportSistema(getImportSistema());
                ean.setImportLoja(getImportLoja());
                ean.setImportId(getImportId());
                return ean;
            }
        }, 4
    );

    public String getImportSistema() {
        return importSistema;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = Utils.acertarTexto(descricao);
    }

    public ProdutoVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(ProdutoVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public int getPisCofinsCredito() {
        return pisCofinsCredito;
    }

    public void setPisCofinsCredito(int pisCofinsCredito) {
        this.pisCofinsCredito = pisCofinsCredito;
    }

    public int getPisCofinsDebito() {
        return pisCofinsDebito;
    }

    public void setPisCofinsDebito(int pisCofinsDebito) {
        this.pisCofinsDebito = pisCofinsDebito;
    }

    public int getPisCofinsNaturezaReceita() {
        return pisCofinsNaturezaReceita;
    }

    public void setPisCofinsNaturezaReceita(int pisCofinsNaturezaReceita) {
        this.pisCofinsNaturezaReceita = pisCofinsNaturezaReceita;
    }

    public int getIcmsCst() {
        return icmsCst;
    }

    public void setIcmsCst(int icmsCst) {
        this.icmsCst = icmsCst;
    }

    public double getIcmsAliq() {
        return icmsAliq;
    }

    public void setIcmsAliq(double icmsAliq) {
        this.icmsAliq = icmsAliq;
    }

    public double getIcmsReducao() {
        return icmsReducao;
    }

    public void setIcmsReducao(double icmsReducao) {
        this.icmsReducao = icmsReducao;
    }

    public double getEstoque() {
        return estoque;
    }

    public void setEstoque(double estoque) {
        this.estoque = MathUtils.round(estoque, 4);
    }

    public boolean iseBalanca() {
        return eBalanca;
    }

    public void seteBalanca(boolean eBalanca) {
        this.eBalanca = eBalanca;
    }

    public double getCustosemimposto() {
        return custosemimposto;
    }

    public void setCustosemimposto(double custosemimposto) {
        this.custosemimposto = MathUtils.trunc(custosemimposto, 4);
    }

    public double getCustocomimposto() {
        return custocomimposto;
    }

    public void setCustocomimposto(double custocomimposto) {
        this.custocomimposto = MathUtils.trunc(custocomimposto, 4);
    }

    public double getMargem() {
        return margem;
    }

    public void setMargem(double margem) {
        this.margem = MathUtils.round(margem, 2);
    }

    public double getPrecovenda() {
        return precovenda;
    }

    public void setPrecovenda(double precovenda) {
        this.precovenda = MathUtils.round(precovenda, 2);
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getCest() {
        return cest;
    }

    public void setCest(String cest) {
        this.cest = cest;
    }

    public int getContadorImportacao() {
        return contadorImportacao;
    }

    public void setContadorImportacao(int contadorImportacao) {
        this.contadorImportacao = contadorImportacao;
    }

    public MultiMap<String, ProdutoAnteriorEanVO> getEans() {
        return eans;
    }

    public boolean isNovo() {
        return novo;
    }

    public void setNovo(boolean novo) {
        this.novo = novo;
    }

    public void setCodigoSped(String codigoSped) {
        if (codigoSped == null) {
            codigoSped = "";
        }
        this.codigoSped = codigoSped;
    }

    public String getCodigoSped() {
        return codigoSped;
    }
    public String[] getChave() {
        return new String[]{
            this.importSistema,
            this.importLoja,
            this.importId
        };
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }
}
