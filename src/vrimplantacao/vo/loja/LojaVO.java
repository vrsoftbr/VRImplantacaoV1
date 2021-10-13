package vrimplantacao.vo.loja;

public class LojaVO {

    public int id = -1;
    public int idCopiarLoja = 0;
    public int idSituacaoCadastro = 0;
    public String descricao = "";
    public int idFornecedor = 0;
    public String nomeServidor = "";
    public boolean servidorCentral = false;
    public String situacaoCadastro = "";
    public int idRegiao = -1;
    public boolean geraConcentrador = false;
    public String regiao = "";
    public boolean copiaPrecoVenda = false;
    public boolean copiaCusto = false;
    public boolean copiaTecladoLayout = false;
    private boolean copiaMargem = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCopiarLoja() {
        return idCopiarLoja;
    }

    public void setIdCopiarLoja(int idCopiarLoja) {
        this.idCopiarLoja = idCopiarLoja;
    }

    public int getIdSituacaoCadastro() {
        return idSituacaoCadastro;
    }

    public void setIdSituacaoCadastro(int idSituacaoCadastro) {
        this.idSituacaoCadastro = idSituacaoCadastro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNomeServidor() {
        return nomeServidor;
    }

    public void setNomeServidor(String nomeServidor) {
        this.nomeServidor = nomeServidor;
    }

    public boolean isServidorCentral() {
        return servidorCentral;
    }

    public void setServidorCentral(boolean servidorCentral) {
        this.servidorCentral = servidorCentral;
    }

    public String getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(String situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public int getIdRegiao() {
        return idRegiao;
    }

    public void setIdRegiao(int idRegiao) {
        this.idRegiao = idRegiao;
    }

    public boolean isGeraConcentrador() {
        return geraConcentrador;
    }

    public void setGeraConcentrador(boolean geraConcentrador) {
        this.geraConcentrador = geraConcentrador;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public boolean isCopiaPrecoVenda() {
        return copiaPrecoVenda;
    }

    public void setCopiaPrecoVenda(boolean copiaPrecoVenda) {
        this.copiaPrecoVenda = copiaPrecoVenda;
    }

    public boolean isCopiaCusto() {
        return copiaCusto;
    }

    public void setCopiaCusto(boolean copiaCusto) {
        this.copiaCusto = copiaCusto;
    }

    public boolean isCopiaTecladoLayout() {
        return copiaTecladoLayout;
    }

    public void setCopiaTecladoLayout(boolean copiaTecladoLayout) {
        this.copiaTecladoLayout = copiaTecladoLayout;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
    
    public boolean isCopiaMargem() {
        return this.copiaMargem;
    }
    
    public void setCopiaMargem(boolean copiaMargem) {
        this.copiaMargem = copiaMargem;
    }
}
