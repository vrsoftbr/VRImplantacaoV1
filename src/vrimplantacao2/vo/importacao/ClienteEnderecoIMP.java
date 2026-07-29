package vrimplantacao2.vo.importacao;

/**
 * Classe utilizada para importar as informações de endereços de cliente.
 * @author Wesley
 */
public class ClienteEnderecoIMP {
    
    private ClienteIMP clienteEventual;
    private int id;
    private int tipo_endereco;
    private String endereco;
    private String numero;
    private String bairro;
    private int cep;
    private int id_municipio;
    private int id_estado;
    private int id_pais;
    private String inscricao_estadual;
    private String inscricao_municipal;
    private int id_tipoindicadorie;
    private String telefone;
    private String complemento;


    public ClienteEnderecoIMP() {
    }

    public ClienteEnderecoIMP(int id, int tipo_endereco, String endereco, String numero, String bairro, int cep, int id_municipio, int id_estado, int id_pais, String inscricao_estadual, String inscricao_municipal, int id_tipoindicadorie, String telefone, String complemento) {
        this.id = id;
        this.tipo_endereco = tipo_endereco;
        this.endereco = endereco;
        this.numero = numero;
        this.bairro = bairro;
        this.cep = cep;
        this.id_municipio = id_municipio;
        this.id_estado = id_estado;
        this.id_pais = id_pais;
        this.inscricao_estadual = inscricao_estadual;
        this.inscricao_municipal = inscricao_municipal;
        this.id_tipoindicadorie = id_tipoindicadorie;
        this.telefone = telefone;
        this.complemento = complemento;
    }

    public ClienteIMP getClienteEventual() {
        return clienteEventual;
    }

    public int getTipo_endereco() {
        return tipo_endereco;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getNumero() {
        return numero;
    }

    public String getBairro() {
        return bairro;
    }

    public int getCep() {
        return cep;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public int getId_estado() {
        return id_estado;
    }

    public int getId_pais() {
        return id_pais;
    }

    public String getInscricao_estadual() {
        return inscricao_estadual;
    }

    public String getInscricao_municipal() {
        return inscricao_municipal;
    }

    public int getId_tipoindicadorie() {
        return id_tipoindicadorie;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getComplemento() {
        return complemento;
    }
}
