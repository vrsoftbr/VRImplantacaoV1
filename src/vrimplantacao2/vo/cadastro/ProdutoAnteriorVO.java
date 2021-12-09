package vrimplantacao2.vo.cadastro;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.Factory;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class ProdutoAnteriorVO {

    /**
     * @return the dataCadastro
     */
    public Date getDataCadastro() {
        return dataCadastro;
    }

    /**
     * @param dataCadastro the dataCadastro to set
     */
    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    /**
     * @return the dataHoraAlteracao
     */
    public Date getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    /**
     * @param dataHoraAlteracao the dataHoraAlteracao to set
     */
    public void setDataHoraAlteracao(Date dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao;
    }

    /**
     * @return the icmsDebitoId
     */
    public String getIcmsDebitoId() {
        return icmsDebitoId;
    }

    /**
     * @param icmsDebitoId the icmsDebitoId to set
     */
    public void setIcmsDebitoId(String icmsDebitoId) {
        this.icmsDebitoId = icmsDebitoId;
    }

    /**
     * @return the icmsDebitoForaEstadoId
     */
    public String getIcmsDebitoForaEstadoId() {
        return icmsDebitoForaEstadoId;
    }

    /**
     * @param icmsDebitoForaEstadoId the icmsDebitoForaEstadoId to set
     */
    public void setIcmsDebitoForaEstadoId(String icmsDebitoForaEstadoId) {
        this.icmsDebitoForaEstadoId = icmsDebitoForaEstadoId;
    }

    /**
     * @return the icmsDebitoForaEstadoIdNf
     */
    public String getIcmsDebitoForaEstadoIdNf() {
        return icmsDebitoForaEstadoIdNf;
    }

    /**
     * @param icmsDebitoForaEstadoIdNf the icmsDebitoForaEstadoIdNf to set
     */
    public void setIcmsDebitoForaEstadoIdNf(String icmsDebitoForaEstadoIdNf) {
        this.icmsDebitoForaEstadoIdNf = icmsDebitoForaEstadoIdNf;
    }

    /**
     * @return the icmsCreditoId
     */
    public String getIcmsCreditoId() {
        return icmsCreditoId;
    }

    /**
     * @param icmsCreditoId the icmsCreditoId to set
     */
    public void setIcmsCreditoId(String icmsCreditoId) {
        this.icmsCreditoId = icmsCreditoId;
    }

    /**
     * @return the icmsCreditoForaEstadoId
     */
    public String getIcmsCreditoForaEstadoId() {
        return icmsCreditoForaEstadoId;
    }

    /**
     * @param icmsCreditoForaEstadoId the icmsCreditoForaEstadoId to set
     */
    public void setIcmsCreditoForaEstadoId(String icmsCreditoForaEstadoId) {
        this.icmsCreditoForaEstadoId = icmsCreditoForaEstadoId;
    }

    /**
     * @return the icmsConsumidorId
     */
    public String getIcmsConsumidorId() {
        return icmsConsumidorId;
    }

    /**
     * @param icmsConsumidorId the icmsConsumidorId to set
     */
    public void setIcmsConsumidorId(String icmsConsumidorId) {
        this.icmsConsumidorId = icmsConsumidorId;
    }

    /**
     * @return the icmsCstSaida
     */
    public int getIcmsCstSaida() {
        return icmsCstSaida;
    }

    /**
     * @param icmsCstSaida the icmsCstSaida to set
     */
    public void setIcmsCstSaida(int icmsCstSaida) {
        this.icmsCstSaida = icmsCstSaida;
    }

    /**
     * @return the icmsAliqSaida
     */
    public double getIcmsAliqSaida() {
        return icmsAliqSaida;
    }

    /**
     * @param icmsAliqSaida the icmsAliqSaida to set
     */
    public void setIcmsAliqSaida(double icmsAliqSaida) {
        this.icmsAliqSaida = icmsAliqSaida;
    }

    /**
     * @return the icmsReducaoSaida
     */
    public double getIcmsReducaoSaida() {
        return icmsReducaoSaida;
    }

    /**
     * @param icmsReducaoSaida the icmsReducaoSaida to set
     */
    public void setIcmsReducaoSaida(double icmsReducaoSaida) {
        this.icmsReducaoSaida = icmsReducaoSaida;
    }

    /**
     * @return the icmsCstSaidaForaEstado
     */
    public int getIcmsCstSaidaForaEstado() {
        return icmsCstSaidaForaEstado;
    }

    /**
     * @param icmsCstSaidaForaEstado the icmsCstSaidaForaEstado to set
     */
    public void setIcmsCstSaidaForaEstado(int icmsCstSaidaForaEstado) {
        this.icmsCstSaidaForaEstado = icmsCstSaidaForaEstado;
    }

    /**
     * @return the icmsAliqSaidaForaEstado
     */
    public double getIcmsAliqSaidaForaEstado() {
        return icmsAliqSaidaForaEstado;
    }

    /**
     * @param icmsAliqSaidaForaEstado the icmsAliqSaidaForaEstado to set
     */
    public void setIcmsAliqSaidaForaEstado(double icmsAliqSaidaForaEstado) {
        this.icmsAliqSaidaForaEstado = icmsAliqSaidaForaEstado;
    }

    /**
     * @return the icmsReducaoSaidaEstado
     */
    public double getIcmsReducaoSaidaForaEstado() {
        return icmsReducaoSaidaForaEstado;
    }

    /**
     * @param icmsReducaoSaidaForaEstado the icmsReducaoSaidaEstado to set
     */
    public void setIcmsReducaoSaidaForaEstado(double icmsReducaoSaidaForaEstado) {
        this.icmsReducaoSaidaForaEstado = icmsReducaoSaidaForaEstado;
    }

    /**
     * @return the icmsCstSaidaForaEstadoNf
     */
    public int getIcmsCstSaidaForaEstadoNf() {
        return icmsCstSaidaForaEstadoNf;
    }

    /**
     * @param icmsCstSaidaForaEstadoNf the icmsCstSaidaForaEstadoNf to set
     */
    public void setIcmsCstSaidaForaEstadoNf(int icmsCstSaidaForaEstadoNf) {
        this.icmsCstSaidaForaEstadoNf = icmsCstSaidaForaEstadoNf;
    }

    /**
     * @return the icmsAliqSaidaForaEstadoNf
     */
    public double getIcmsAliqSaidaForaEstadoNf() {
        return icmsAliqSaidaForaEstadoNf;
    }

    /**
     * @param icmsAliqSaidaForaEstadoNf the icmsAliqSaidaForaEstadoNf to set
     */
    public void setIcmsAliqSaidaForaEstadoNf(double icmsAliqSaidaForaEstadoNf) {
        this.icmsAliqSaidaForaEstadoNf = icmsAliqSaidaForaEstadoNf;
    }

    /**
     * @return the icmsReducaoSaidaForaEstadoNf
     */
    public double getIcmsReducaoSaidaForaEstadoNf() {
        return icmsReducaoSaidaForaEstadoNf;
    }

    /**
     * @param icmsReducaoSaidaForaEstadoNf the icmsReducaoSaidaForaEstadoNf to set
     */
    public void setIcmsReducaoSaidaForaEstadoNf(double icmsReducaoSaidaForaEstadoNf) {
        this.icmsReducaoSaidaForaEstadoNf = icmsReducaoSaidaForaEstadoNf;
    }

    /**
     * @return the icmsCstConsumidor
     */
    public int getIcmsCstConsumidor() {
        return icmsCstConsumidor;
    }

    /**
     * @param icmsCstConsumidor the icmsCstConsumidor to set
     */
    public void setIcmsCstConsumidor(int icmsCstConsumidor) {
        this.icmsCstConsumidor = icmsCstConsumidor;
    }

    /**
     * @return the icmsAliqConsumidor
     */
    public double getIcmsAliqConsumidor() {
        return icmsAliqConsumidor;
    }

    /**
     * @param icmsAliqConsumidor the icmsAliqConsumidor to set
     */
    public void setIcmsAliqConsumidor(double icmsAliqConsumidor) {
        this.icmsAliqConsumidor = icmsAliqConsumidor;
    }

    /**
     * @return the icmsReducaoConsumidor
     */
    public double getIcmsReducaoConsumidor() {
        return icmsReducaoConsumidor;
    }

    /**
     * @param icmsReducaoConsumidor the icmsReducaoConsumidor to set
     */
    public void setIcmsReducaoConsumidor(double icmsReducaoConsumidor) {
        this.icmsReducaoConsumidor = icmsReducaoConsumidor;
    }

    /**
     * @return the icmsCstEntrada
     */
    public int getIcmsCstEntrada() {
        return icmsCstEntrada;
    }

    /**
     * @param icmsCstEntrada the icmsCstEntrada to set
     */
    public void setIcmsCstEntrada(int icmsCstEntrada) {
        this.icmsCstEntrada = icmsCstEntrada;
    }

    /**
     * @return the icmsAliqEntrada
     */
    public double getIcmsAliqEntrada() {
        return icmsAliqEntrada;
    }

    /**
     * @param icmsAliqEntrada the icmsAliqEntrada to set
     */
    public void setIcmsAliqEntrada(double icmsAliqEntrada) {
        this.icmsAliqEntrada = icmsAliqEntrada;
    }

    /**
     * @return the icmsReducaoEntrada
     */
    public double getIcmsReducaoEntrada() {
        return icmsReducaoEntrada;
    }

    /**
     * @param icmsReducaoEntrada the icmsReducaoEntrada to set
     */
    public void setIcmsReducaoEntrada(double icmsReducaoEntrada) {
        this.icmsReducaoEntrada = icmsReducaoEntrada;
    }

    /**
     * @return the icmsCstEntradaForaEstado
     */
    public int getIcmsCstEntradaForaEstado() {
        return icmsCstEntradaForaEstado;
    }

    /**
     * @param icmsCstEntradaForaEstado the icmsCstEntradaForaEstado to set
     */
    public void setIcmsCstEntradaForaEstado(int icmsCstEntradaForaEstado) {
        this.icmsCstEntradaForaEstado = icmsCstEntradaForaEstado;
    }

    /**
     * @return the icmsAliqEntradaForaEstado
     */
    public double getIcmsAliqEntradaForaEstado() {
        return icmsAliqEntradaForaEstado;
    }

    /**
     * @param icmsAliqEntradaForaEstado the icmsAliqEntradaForaEstado to set
     */
    public void setIcmsAliqEntradaForaEstado(double icmsAliqEntradaForaEstado) {
        this.icmsAliqEntradaForaEstado = icmsAliqEntradaForaEstado;
    }

    /**
     * @return the icmsReducaoEntradaEstado
     */
    public double getIcmsReducaoEntradaForaEstado() {
        return icmsReducaoEntradaForaEstado;
    }

    /**
     * @param icmsReducaoEntradaForaEstado the icmsReducaoEntradaEstado to set
     */
    public void setIcmsReducaoEntradaForaEstado(double icmsReducaoEntradaForaEstado) {
        this.icmsReducaoEntradaForaEstado = icmsReducaoEntradaForaEstado;
    }
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
    private String dataHora;
    private String obsImportacao = "";
    
    private String icmsDebitoId = "";
    private String icmsDebitoForaEstadoId = "";
    private String icmsDebitoForaEstadoIdNf = "";
    private String icmsCreditoId = "";
    private String icmsCreditoForaEstadoId = "";
    private String icmsConsumidorId = "";
    
    private int icmsCstSaida = -1;
    private double icmsAliqSaida = -1;
    private double icmsReducaoSaida = -1;
    private int icmsCstSaidaForaEstado = -1;
    private double icmsAliqSaidaForaEstado = -1;
    private double icmsReducaoSaidaForaEstado = -1;
    private int icmsCstSaidaForaEstadoNf = -1;
    private double icmsAliqSaidaForaEstadoNf = -1;
    private double icmsReducaoSaidaForaEstadoNf = -1;

    private int icmsCstConsumidor = -1;
    private double icmsAliqConsumidor = -1;
    private double icmsReducaoConsumidor = -1;
    
    private int icmsCstEntrada = -1;
    private double icmsAliqEntrada = -1;
    private double icmsReducaoEntrada = -1;
    private int icmsCstEntradaForaEstado = -1;
    private double icmsAliqEntradaForaEstado = -1;
    private double icmsReducaoEntradaForaEstado = -1;
    private Date dataHoraAlteracao;
    private Date dataCadastro;
    private int idConexao = 0;
    
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

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public String getObsImportacao() {
        return obsImportacao;
    }

    public void setObsImportacao(String obsImportacao) {
        this.obsImportacao = obsImportacao;
    }

    public int getIdConexao() {
        return idConexao;
    }

    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }    
}
