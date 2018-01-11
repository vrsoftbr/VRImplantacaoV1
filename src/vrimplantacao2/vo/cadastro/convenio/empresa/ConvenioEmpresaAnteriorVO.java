package vrimplantacao2.vo.cadastro.convenio.empresa;

/**
 *
 * @author Leandro
 */
public class ConvenioEmpresaAnteriorVO {
    
    private String sistema;
    private String loja;
    private String id;
    private ConvenioEmpresaVO codigoAtual;
    private String cnpj;
    private String razao;

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoAtual(ConvenioEmpresaVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setRazao(String razao) {
        this.razao = razao;
    }    

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public String getId() {
        return id;
    }

    public ConvenioEmpresaVO getCodigoAtual() {
        return codigoAtual;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getRazao() {
        return razao;
    }
    
}
