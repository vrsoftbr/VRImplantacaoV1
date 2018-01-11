package vrimplantacao2.vo.cadastro.fornecedor;

import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.Factory;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.TipoContato;

public class FornecedorContatoVO {
    private int id = -1;
    private FornecedorVO fornecedor;
    private String nome = "SEM NOME";
    private String telefone = "";
    private TipoContato tipoContato = TipoContato.COMERCIAL;
    private String email = "";
    private String celular = "";
    private final MultiMap<String, FornecedorContatoAnteriorVO> anteriores = new MultiMap<>(
        new Factory<FornecedorContatoAnteriorVO>() {
            @Override
            public FornecedorContatoAnteriorVO make() {
                FornecedorContatoAnteriorVO result = new FornecedorContatoAnteriorVO();                
                result.setCodigoAtual(FornecedorContatoVO.this);
                return result;
            }
        }
    );

    public void setId(int id) {
        this.id = id;
    }

    public void setFornecedor(FornecedorVO fornecedor) {
        this.fornecedor = fornecedor;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 30, "SEM NOME");
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.formataNumero(telefone, 14, "");
    }

    public void setTipoContato(TipoContato tipoContato) {
        this.tipoContato = tipoContato;
    }

    public void setEmail(String email) {
        this.email = Utils.acertarTexto(email, 50, "").trim();
    }

    public void setCelular(String celular) {
        this.celular = Utils.formataNumero(celular, 14, "");;
    }

    public int getId() {
        return id;
    }

    public FornecedorVO getFornecedor() {
        return fornecedor;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public TipoContato getTipoContato() {
        return tipoContato;
    }

    public String getEmail() {
        return email;
    }

    public String getCelular() {
        return celular;
    }

    public MultiMap<String, FornecedorContatoAnteriorVO> getAnteriores() {
        return anteriores;
    }
    
}
