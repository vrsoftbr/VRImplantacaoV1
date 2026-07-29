package vrimplantacao2.vo.cadastro.cliente;

import vrimplantacao.utils.Utils;

/**
 * Classe que representa o Endereço de Cliente Eventual.
 * @author Wesley
 */
public class ClienteEventualEnderecoVO {
    
    private int id;// integer NOT NULL,
    private ClienteEventualVO clienteEventual;//NOT NULL,
    private int idClienteEventual;//NOT NULL,
    private int tipo_endereco = 0;// integer NOT NULL,
    private String endereco = "SEM ENDERECO";// character varying(50) NOT NULL,
    private String numero = "0";// character varying(6) NOT NULL,
    private String bairro = "SEM BAIRRO";// character varying(30) NOT NULL,
    private int cep = 0;// numeric(8,0) NOT NULL,
    private int id_municipio = 0;// integer NOT NULL,
    private int id_estado = 0;// integer NOT NULL,
    private int id_pais = 1058;// integer NOT NULL,
    private String inscricao_estadual = "ISENTO"; //character varying(20) NOT NULL,
    private String inscricao_municipal = "ISENTO"; //character varying(20) NOT NULL,
    private int id_tipoindicadorie = 9;// integer NOT NULL,
    private String telefone = "0000000000";// character varying(14) NOT NULL,
    private String complemento = "SEM COMPLEMENTO";// character varying(30) NOT NULL DEFAULT ''::character varying,

    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }

    public ClienteEventualVO getClienteEventual() {
        return clienteEventual;
    }

    public void setClienteEventual(ClienteEventualVO clienteEventual) {
        this.clienteEventual = clienteEventual;
    }

    public int getIdClienteEventual() {
        return idClienteEventual;
    }

    public void setIdClienteEventual(int idClienteEventual) {
        this.idClienteEventual = idClienteEventual;
    }

    public int getTipo_endereco() {
        return tipo_endereco;
    }

    public void setTipo_endereco(int tipo_endereco) {
        this.tipo_endereco = tipo_endereco;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = Utils.acertarTexto(endereco, 50, "SEM ENDERECO");
    }

    public String getNumero() {
        return numero;
    }    

    public void setNumero(String numero) {
        this.numero = Utils.acertarTexto(numero, 6, "0");
    }    

    public String getBairro() {
        return bairro;
    }
    
    public void setBairro(String bairro) {
        this.bairro = Utils.acertarTexto(bairro, 30, "SEM BAIRRO");
    }
    
    public int getCep() {
        return cep;
    } 

    public void setCep(int cep) {
        this.cep = cep < 0 || cep > 99999999 ? 0 : cep;
    }
    
    public int getId_municipio() {
        return id_municipio;
    }
    
    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }
    
    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public int getId_pais() {
        return id_pais;
    }
    
    public void setId_pais(int id_pais) {
        this.id_pais = id_pais;
    }
    
    public String getInscricao_estadual() {
        return inscricao_estadual;
    }
   
    public void setInscricao_estadual(String inscricao_estadual) {
        this.inscricao_estadual = Utils.acertarTexto(inscricao_estadual, 20, "ISENTO");
    }

    public String getInscricao_municipal() {
        return inscricao_municipal;
    }

    public void setInscricao_municipal(String inscricao_municipal) {
        this.inscricao_municipal = inscricao_municipal;
    }

    public int getId_tipoindicadorie() {
        return id_tipoindicadorie;
    }

    public void setId_tipoindicadorie(int id_tipoindicadorie) {
        this.id_tipoindicadorie = id_tipoindicadorie;
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.acertarTexto(telefone, 14, "(00)0000-0000");
    }
    
    public String getTelefone() {
        return telefone;
    }

    public String getComplemento() {
        return complemento;
    }
    
    public void setComplemento(String complemento) {
        this.complemento = Utils.acertarTexto(complemento, 30, "SEM COMPLEMENTO");
    }   
}
