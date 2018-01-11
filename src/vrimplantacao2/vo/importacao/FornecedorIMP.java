package vrimplantacao2.vo.importacao;

import java.util.Date;
import vrimplantacao2.utils.Factory;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoInscricao;

public class FornecedorIMP {

    private String importSistema;
    private String importLoja;
    private String importId;
    private String razao;
    private String fantasia;
    private String cnpj_cpf;
    private String ie_rg;
    private String insc_municipal;
    private String suframa;
    private boolean ativo = true;
    private TipoInscricao tipo_inscricao = TipoInscricao.VAZIO;

    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private int ibge_municipio;
    private String municipio;
    private int ibge_uf;
    private String uf;
    private String cep;

    private String cob_endereco;
    private String cob_numero;
    private String cob_complemento;
    private String cob_bairro;
    private int cob_ibge_municipio;
    private String cob_municipio;
    private int cob_ibge_uf;
    private String cob_uf;
    private String cob_cep;

    private String tel_principal;
    private int qtd_minima_pedido;
    private double valor_minimo_pedido;
    private Date datacadastro;
    private String observacao;
    
    private int prazoEntrega = 0;
    private int prazoVisita = 0;
    private int prazoSeguranca = 0;
    private int condicaoPagamento = 0;    
    
    private TipoFornecedor tipoFornecedor = TipoFornecedor.ATACADO;
    
    private TipoEmpresa tipoEmpresa = TipoEmpresa.LUCRO_REAL;

    private final MultiMap<String, FornecedorContatoIMP> contatos = new MultiMap<>(
            new Factory<FornecedorContatoIMP>() {
                @Override
                public FornecedorContatoIMP make() {
                    FornecedorContatoIMP ret = new FornecedorContatoIMP();
                    ret.setImportSistema(FornecedorIMP.this.getImportSistema());
                    ret.setImportLoja(FornecedorIMP.this.getImportLoja());
                    ret.setImportFornecedorId(FornecedorIMP.this.getImportId());

                    return ret;
                }
            }
    );
    
    private final MultiMap<String, FornecedorPagamentoIMP> pagamentos = new MultiMap<>(
            new Factory<FornecedorPagamentoIMP>() {
                @Override
                public FornecedorPagamentoIMP make() {
                    FornecedorPagamentoIMP ret = new FornecedorPagamentoIMP();
                    ret.setImportSistema(FornecedorIMP.this.getImportSistema());
                    ret.setImportLoja(FornecedorIMP.this.getImportLoja());
                    ret.setImportFornecedorId(FornecedorIMP.this.getImportId());
                    return ret;
                }                    
            }
    );    

    public String getImportSistema() {
        return importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public String getImportId() {
        return importId;
    }

    public String getRazao() {
        return razao;
    }

    public String getFantasia() {
        return fantasia;
    }

    public String getCnpj_cpf() {
        return cnpj_cpf;
    }

    public String getIe_rg() {
        return ie_rg;
    }

    public String getInsc_municipal() {
        return insc_municipal;
    }

    public String getSuframa() {
        return suframa;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public int getIbge_municipio() {
        return ibge_municipio;
    }

    public String getMunicipio() {
        return municipio;
    }

    public int getIbge_uf() {
        return ibge_uf;
    }

    public String getUf() {
        return uf;
    }

    public String getCep() {
        return cep;
    }

    public String getCob_endereco() {
        return cob_endereco;
    }

    public String getCob_numero() {
        return cob_numero;
    }

    public String getCob_complemento() {
        return cob_complemento;
    }

    public String getCob_bairro() {
        return cob_bairro;
    }

    public int getCob_ibge_municipio() {
        return cob_ibge_municipio;
    }

    public String getCob_municipio() {
        return cob_municipio;
    }

    public int getCob_ibge_uf() {
        return cob_ibge_uf;
    }

    public String getCob_uf() {
        return cob_uf;
    }

    public String getCob_cep() {
        return cob_cep;
    }

    public String getTel_principal() {
        return tel_principal;
    }

    public int getQtd_minima_pedido() {
        return qtd_minima_pedido;
    }

    public double getValor_minimo_pedido() {
        return valor_minimo_pedido;
    }

    public Date getDatacadastro() {
        return datacadastro;
    }

    public String getObservacao() {
        return observacao;
    }

    public int getCondicaoPagamento() {
        return condicaoPagamento;
    }

    public int getPrazoEntrega() {
        return prazoEntrega;
    }

    public int getPrazoSeguranca() {
        return prazoSeguranca;
    }

    public int getPrazoVisita() {
        return prazoVisita;
    }

    public TipoFornecedor getTipoFornecedor() {
        return tipoFornecedor;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public void setRazao(String razao) {
        this.razao = razao;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public void setCnpj_cpf(String cnpj_cpf) {
        this.cnpj_cpf = cnpj_cpf;
    }

    public void setIe_rg(String ie_rg) {
        this.ie_rg = ie_rg;
    }

    public void setInsc_municipal(String insc_municipal) {
        this.insc_municipal = insc_municipal;
    }

    public void setSuframa(String suframa) {
        this.suframa = suframa;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setIbge_municipio(int ibge_municipio) {
        this.ibge_municipio = ibge_municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public void setIbge_uf(int ibge_uf) {
        this.ibge_uf = ibge_uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setCob_endereco(String cob_endereco) {
        this.cob_endereco = cob_endereco;
    }

    public void setCob_numero(String cob_numero) {
        this.cob_numero = cob_numero;
    }

    public void setCob_complemento(String cob_complemento) {
        this.cob_complemento = cob_complemento;
    }

    public void setCob_bairro(String cob_bairro) {
        this.cob_bairro = cob_bairro;
    }

    public void setCob_ibge_municipio(int cob_ibge_municipio) {
        this.cob_ibge_municipio = cob_ibge_municipio;
    }

    public void setCob_municipio(String cob_municipio) {
        this.cob_municipio = cob_municipio;
    }

    public void setCob_ibge_uf(int cob_ibge_uf) {
        this.cob_ibge_uf = cob_ibge_uf;
    }

    public void setCob_uf(String cob_uf) {
        this.cob_uf = cob_uf;
    }

    public void setCob_cep(String cob_cep) {
        this.cob_cep = cob_cep;
    }

    public void setTel_principal(String tel_principal) {
        this.tel_principal = tel_principal;
    }

    public void setQtd_minima_pedido(int qtd_minima_pedido) {
        this.qtd_minima_pedido = qtd_minima_pedido;
    }

    public void setValor_minimo_pedido(double valor_minimo_pedido) {
        this.valor_minimo_pedido = valor_minimo_pedido;
    }

    public void setDatacadastro(Date datacadastro) {
        this.datacadastro = datacadastro;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public MultiMap<String, FornecedorContatoIMP> getContatos() {
        return contatos;
    }
    
    public MultiMap<String, FornecedorPagamentoIMP> getPagamentos() {
        return pagamentos;
    }

    public void setCondicaoPagamento(int condicaoPagamento) {
        this.condicaoPagamento = condicaoPagamento < 0 ? 0 : condicaoPagamento;
    }

    public void setPrazoEntrega(int prazoEntrega) {
        this.prazoEntrega = prazoEntrega < 0 ? 0 : prazoEntrega;
    }

    public void setPrazoSeguranca(int prazoSeguranca) {
        this.prazoSeguranca = prazoSeguranca < 0 ? 0 : prazoSeguranca;
    }

    public void setPrazoVisita(int prazoVisita) {
        this.prazoVisita = prazoVisita < 0 ? 0 : prazoVisita;
    }

    public void setTipoFornecedor(TipoFornecedor tipoFornecedor) {
        this.tipoFornecedor = tipoFornecedor;
    }

    public TipoEmpresa getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(TipoEmpresa tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }
    
    

    public FornecedorContatoIMP addContato(String id, String nome, String telefone, String celular, TipoContato tipo, String email) {
        FornecedorContatoIMP cont = contatos.make(id);
        cont.setImportSistema(getImportSistema());
        cont.setImportLoja(getImportLoja());
        cont.setImportFornecedorId(getImportId());
        cont.setImportId(id);
        cont.setNome(nome);
        cont.setTelefone(telefone);
        cont.setCelular(celular);
        cont.setTipoContato(tipo);
        cont.setEmail(email);
        return cont;
    }
    
    public FornecedorPagamentoIMP addPagamento(String id, int vencimento) {
        FornecedorPagamentoIMP pag = pagamentos.make(id);
        pag.setImportSistema(getImportSistema());
        pag.setImportLoja(getImportLoja());
        pag.setImportFornecedorId(getImportId());
        pag.setImportId(id);
        pag.setVencimento(vencimento);
        return pag;
    }

    public String[] getChave() {
        return new String[]{
            getImportSistema(),
            getImportLoja(),
            getImportId()
        };
    }

    public TipoInscricao getTipo_inscricao() {
        return tipo_inscricao;
    }

    public void setTipo_inscricao(TipoInscricao tipo_inscricao) {
        this.tipo_inscricao = tipo_inscricao;
    }

}
