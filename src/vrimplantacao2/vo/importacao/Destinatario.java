package vrimplantacao2.vo.importacao;

/**
 * Destinatário da nota fiscal.
 * @author Leandro
 */
public class Destinatario {
    
    private TipoDestinatario tipo = TipoDestinatario.FORNECEDOR;
    private String id; //Pode ser Cliente Eventual ou Fornecedor
    private String razaoSocial;
    private String cnpjCpf;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private int municipioIBGE;
    private String municipio;
    private String estado;
    private String cep;

    public TipoDestinatario getTipo() {
        return tipo;
    }

    public void setTipo(TipoDestinatario tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public int getMunicipioIBGE() {
        return municipioIBGE;
    }

    public void setMunicipioIBGE(int municipioIBGE) {
        this.municipioIBGE = municipioIBGE;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public static enum TipoDestinatario {
        
        FORNECEDOR (0),
        CLIENTE_EVENTUAL (1);

        public static TipoDestinatario get(int tipo) {
            for (TipoDestinatario td: values()) {
                if (td.getId() == tipo) {
                    return td;
                }
            }
            return null;
        }
        
        private int id;

        private TipoDestinatario(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
        
    }
    
}
