package vrimplantacao2.vo.cadastro.cliente.food;

import java.util.HashSet;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 * VO da tabela food.cliente
 * @author Leandro
 */
public class ClienteFoodVO {
    
    private int id;// integer NOT NULL,
    private String nome;// character varying(60) NOT NULL,
    private String endereco;// character varying(50) NOT NULL,
    private String numero;// character varying(6) NOT NULL,
    private String bairro;// character varying(30) NOT NULL,
    private String observacao;// character varying(80) NOT NULL,
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;// id_situacaocadastro integer NOT NULL,
    private int id_municipio;// integer NOT NULL DEFAULT 0,
    private Set<Long> telefones = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 60);
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = Utils.acertarTexto(endereco, 50);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = Utils.acertarTexto(numero, 6);
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = Utils.acertarTexto(bairro, 30);
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 80);
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public Set<Long> getTelefones() {
        return telefones;
    }
    
}
