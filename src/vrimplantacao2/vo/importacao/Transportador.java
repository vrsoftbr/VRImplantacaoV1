package vrimplantacao2.vo.importacao;

/**
 * Transportador da nota fiscal.
 */
public class Transportador {
    
    private NotaTipoTransportador tipoTransportador = NotaTipoTransportador.FORNECEDOR;
    private String id;
    private String cnpjCpf;
    private String razaoSocial;
    private String ie;
    private String endereco;
    private String placa = "";

    public NotaTipoTransportador getTipoTransportador() {
        return tipoTransportador;
    }

    public void setTipoTransportador(NotaTipoTransportador tipoTransportador) {
        this.tipoTransportador = tipoTransportador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getIe() {
        return ie;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
    
}
