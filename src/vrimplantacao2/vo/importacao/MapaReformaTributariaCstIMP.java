package vrimplantacao2.vo.importacao;

/**
 *
 * @author wesley
 */
public class MapaReformaTributariaCstIMP {
    
    private String sistema;
    private String agrupador;
    private String origId;
    private int origCst;
    private String origDescricao;
    private boolean grupoIbsCbs;
    private boolean grupoReducao;
    private boolean grupoDiferimento;
    private boolean grupoTribRegular;
    private boolean grupoIbsCbsMono;
    private boolean grupoCreditoPresumido;
    private boolean grupoEstornoCredito;
    private boolean grupoTransfCredito;
    private boolean grupoAjusteCompetencia;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }

    public String getOrigId() {
        return origId;
    }

    public void setOrigId(String origId) {
        this.origId = origId;
    }

    public int getOrigCst() {
        return origCst;
    }

    public void setOrigCst(int origCst) {
        this.origCst = origCst;
    }

    public String getOrigDescricao() {
        return origDescricao;
    }

    public void setOrigDescricao(String origDescricao) {
        this.origDescricao = origDescricao;
    }

    public boolean isGrupoIbsCbs() {
        return grupoIbsCbs;
    }

    public void setGrupoIbsCbs(boolean grupoIbsCbs) {
        this.grupoIbsCbs = grupoIbsCbs;
    }

    public boolean isGrupoReducao() {
        return grupoReducao;
    }

    public void setGrupoReducao(boolean grupoReducao) {
        this.grupoReducao = grupoReducao;
    }

    public boolean isGrupoDiferimento() {
        return grupoDiferimento;
    }

    public void setGrupoDiferimento(boolean grupoDiferimento) {
        this.grupoDiferimento = grupoDiferimento;
    }

    public boolean isGrupoTribRegular() {
        return grupoTribRegular;
    }

    public void setGrupoTribRegular(boolean grupoTribRegular) {
        this.grupoTribRegular = grupoTribRegular;
    }

    public boolean isGrupoIbsCbsMono() {
        return grupoIbsCbsMono;
    }

    public void setGrupoIbsCbsMono(boolean grupoIbsCbsMono) {
        this.grupoIbsCbsMono = grupoIbsCbsMono;
    }

    public boolean isGrupoCreditoPresumido() {
        return grupoCreditoPresumido;
    }

    public void setGrupoCreditoPresumido(boolean grupoCreditoPresumido) {
        this.grupoCreditoPresumido = grupoCreditoPresumido;
    }

    public boolean isGrupoEstornoCredito() {
        return grupoEstornoCredito;
    }

    public void setGrupoEstornoCredito(boolean grupoEstornoCredito) {
        this.grupoEstornoCredito = grupoEstornoCredito;
    }

    public boolean isGrupoTransfCredito() {
        return grupoTransfCredito;
    }

    public void setGrupoTransfCredito(boolean grupoTransfCredito) {
        this.grupoTransfCredito = grupoTransfCredito;
    }

    public boolean isGrupoAjusteCompetencia() {
        return grupoAjusteCompetencia;
    }

    public void setGrupoAjusteCompetencia(boolean grupoAjusteCompetencia) {
        this.grupoAjusteCompetencia = grupoAjusteCompetencia;
    }
}