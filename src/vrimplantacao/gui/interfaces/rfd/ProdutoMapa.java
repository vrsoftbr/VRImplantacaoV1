package vrimplantacao.gui.interfaces.rfd;

public class ProdutoMapa {
    
    public static enum TipoMapa {
        EAN
    }
    
    private TipoMapa tipo;
    private String codrfd;
    private String descricao;
    private int codigoAtual;
    private boolean novo;

    public ProdutoMapa(TipoMapa tipo, String codrfd, String descricao, int codigoAtual, boolean novo) {
        this.tipo = tipo;
        this.codrfd = codrfd;
        this.descricao = descricao;
        this.codigoAtual = codigoAtual;
        this.novo = novo;
    }

    public ProdutoMapa() {
    }

    public String getCodrfd() {
        return codrfd;
    }

    public void setCodrfd(String codrfd) {
        this.codrfd = codrfd;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(int codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public TipoMapa getTipo() {
        return tipo;
    }

    public void setTipo(TipoMapa tipo) {
        this.tipo = tipo;
    }

    public boolean isNovo() {
        return novo;
    }

    public void setNovo(boolean novo) {
        this.novo = novo;
    }
    
    
}
