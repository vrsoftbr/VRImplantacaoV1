package vrimplantacao.gui.interfaces.rfd;

class ProdutoVR {
    private int id;
    private String descricaoCompleta;
    private String descricaoReduzida;
    private long ean;

    public ProdutoVR(int id, String descricaoCompleta, String descricaoReduzida, long ean) {
        this.id = id;
        this.descricaoCompleta = descricaoCompleta;
        this.descricaoReduzida = descricaoReduzida;
        this.ean = ean;
    }

    public ProdutoVR() {
    }

    public int getId() {
        return id;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public long getEan() {
        return ean;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = descricaoCompleta;
    }

    public void setEan(long ean) {
        this.ean = ean;
    }    

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }
    
}
