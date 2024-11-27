package vrimplantacao2.vo.importacao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.Factory;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoPagamento;

public class FornecedorIMP {

    private String importSistema;
    private String importLoja;
    private String importId;
    private String razao;
    private String fantasia;
    private String cnpj_cpf;
    private String ie_rg;
    private String insc_municipal = "";
    private String suframa;
    private boolean ativo = true;
    private boolean bloqueado = false;
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
    
    private int prazoPedido = 15;
    private String idDivisao;
    
    private Set<Integer> condicoesPagamentos = new LinkedHashSet<>();
    
    private TipoFornecedor tipoFornecedor = TipoFornecedor.ATACADO;
    
    private TipoEmpresa tipoEmpresa = TipoEmpresa.LUCRO_REAL;
    private TipoPagamento tipoPagamento;
    private int idBanco;
    private boolean emiteNfe = false;
    private boolean permiteNfSemPedido = false;
    
    private TipoIndicadorIE tipoIndicadorIe;
    private String utilizaiva = "0";
    private boolean revenda = false;
    private Integer idPais;
    
    public TipoIndicadorIE getTipoIndicadorIe() {
        return tipoIndicadorIe;
    }

    public void setTipoIndicadorIe(TipoIndicadorIE tipoIndicadorIe) {
        this.tipoIndicadorIe = tipoIndicadorIe;
    }
    
    

    public Set<Integer> getCondicoesPagamentos() {
        return condicoesPagamentos;
    }
    
    public void addCondicaoPagamento(int condicaoPagamento) {
        this.condicoesPagamentos.add(condicaoPagamento < 0 ? 0 : condicaoPagamento);
    }
    
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
    
    private final List<FornecedorDivisaoIMP> divisoes = new ArrayList<>();
    
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

    public boolean isBloqueado() {
        return bloqueado;
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

    public int getPrazoEntrega() {
        if (getDivisoes().isEmpty()) return 7;
        return getDivisoes().get(0).getPrazoEntrega();
    }

    public int getPrazoSeguranca() {
        if (getDivisoes().isEmpty()) return 7;
        return getDivisoes().get(0).getPrazoSeguranca();
    }

    public int getPrazoVisita() {
        if (getDivisoes().isEmpty()) return 7;
        return getDivisoes().get(0).getPrazoVisita();
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

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
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
        this.tel_principal = Utils.formataNumero(tel_principal);
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
    
    public List<FornecedorDivisaoIMP> getDivisoes() {
        return divisoes;
    }

    public void setCondicaoPagamento(int condicaoPagamento) {
        addCondicaoPagamento(condicaoPagamento);
    }

    public void setPrazoEntrega(int prazoEntrega) {
        if (getDivisoes().isEmpty()) {
            addDivisao("VR", 7, 7, 7);
        }
        getDivisoes().get(0).setPrazoEntrega(prazoEntrega < 0 ? 0 : prazoEntrega);
    }

    public void setPrazoSeguranca(int prazoSeguranca) {
        if (getDivisoes().isEmpty()) {
            addDivisao("VR", 7, 7, 7);
        }
        getDivisoes().get(0).setPrazoSeguranca(prazoSeguranca < 0 ? 0 : prazoSeguranca);
    }

    public void setPrazoVisita(int prazoVisita) {
        if (getDivisoes().isEmpty()) {
            addDivisao("VR", 7, 7, 7);
        }
        getDivisoes().get(0).setPrazoVisita(prazoVisita < 0 ? 0 : prazoVisita);
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
    
    /**
     * Inclui um novo contato no fornecedor. Se o nome estiver vazio ou nulo, 
     * então o contato não é incluiso e null é retornado.
     * @param id ID que identificará o contato na lista de contatos do fornecedor.
     * @param nome Nome do contato.
     * @param telefone Telefone do contato.
     * @param celular Celular do contato.
     * @param tipo Tipo do contato.
     * @param email E-mail do contato.
     * @return Contato cadastrado ou null quando o nome for vazio.
     */
    public FornecedorContatoIMP addContato(String id, String nome, String telefone, String celular, TipoContato tipo, String email) {
        
        if (nome != null && !"".equals(nome.trim())) {
            
            if (id == null) {
                int cont = 1;
                while (contatos.containsKey("CONTATO " + cont)) {
                    cont++;
                }
                id = "CONTATO " + cont;
            }
            
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
        } else {
            return null;
        }        
    }
    
    /**
     * Inclui as divisões dos fornecedor
     * 
     * @param id
     * @param prazoVisita
     * @param prazoEntrega
     * @param prazoSeguranca
     * @return Divisão cadastrada ou null quando o nome for vazio.
    */    
    public FornecedorDivisaoIMP addDivisao(String id, int prazoVisita, int prazoEntrega, int prazoSeguranca) {
        
        FornecedorDivisaoIMP div = new FornecedorDivisaoIMP();
        
        div.setImportSistema(getImportSistema());
        div.setImportLoja(getImportLoja());
        div.setImportFornecedorId(getImportId());
        div.setImportId(id);
        div.setPrazoEntrega(prazoEntrega);
        div.setPrazoSeguranca(prazoSeguranca);
        div.setPrazoVisita(prazoVisita);
        
        this.divisoes.add(div);
        
        return div;
    }
    
    /**
     * Inclui um novo contato no fornecedor. Se o nome estiver vazio ou nulo, 
     * então o contato não é incluiso e null é retornado.
     * @param nome Nome do contato.
     * @param telefone Telefone do contato.
     * @param celular Celular do contato.
     * @param tipo Tipo do contato.
     * @param email E-mail do contato.
     * @return Contato cadastrado ou null quando o nome for vazio.
     */
    public FornecedorContatoIMP addContato(String nome, String telefone, String celular, TipoContato tipo, String email) {        
        return addContato(null, nome, telefone, celular, tipo, email);
    }
    
    /**
     * Incluí uma forma de pagamento no cadastro do fornecedor.
     * @param id  ID da condição de pagamento.
     * @param vencimento Dia de vencimento.
     * @return Forma de pagamento do fornecedor que foi armazenada.
     */
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

    /**
     * Método criado para facilitar a inclusão de celular nos contatos do fornecedor.
     * @param descricao Descricão que será utilizada no contato.
     * @param celular Celular que será gravado.
     * @return Contato incluso.
     */
    public FornecedorContatoIMP addCelular(String descricao, String celular) {
        celular = Utils.stringLong(celular);
        if (celular != null && !"0".equals(celular.trim()) && celular.trim().length() > 3) {
            return addContato(descricao, "", celular, TipoContato.COMERCIAL, "");
        } else {
            return null;
        }
    }

    /**
     * Método criado para facilitar a inclusão de telefones adicionais nos
     * contatos do fornecedor.
     * @param descricao Descrição que será utilizada nos contatos.
     * @param telefone Telefone a ser incluso.
     * @return Contato incluso.
     */
    public FornecedorContatoIMP addTelefone(String descricao, String telefone) {
        telefone = Utils.stringLong(telefone);
        if (telefone != null && !"0".equals(telefone.trim()) && telefone.trim().length() > 3) {
            return addContato(descricao, telefone, "", TipoContato.COMERCIAL, "");
        } else {
            return null;
        }
    }

    /**
     * Método criado para facilitar a inclusão de e-mails no fornecedor como contato.
     * @param descricao Descrição que será utilizada no cadastro.
     * @param email E-mail do fornecedor.
     * @param tipo Tipo de e-mail.
     * @return Contato incluso.
     */
    public FornecedorContatoIMP addEmail(String descricao, String email, TipoContato tipo) {
        email = Utils.formataEmail(email, 60);
        if (email != null && !"".equals(email.trim())) {
            return addContato(descricao, "", "", tipo, email);
        } else {
            return null;
        }
    }

    /**
     * Copia todo o endereço para o endereço de cobrança.
     */
    public void copiarEnderecoParaCobranca() {
        this.cob_endereco = this.endereco;
        this.cob_numero = this.numero;
        this.cob_complemento = this.complemento;
        this.cob_bairro = this.bairro;
        this.cob_municipio = this.municipio;
        this.cob_uf = this.uf;
        this.cob_ibge_municipio = this.ibge_municipio;
        this.cob_ibge_uf = this.ibge_uf;
        this.cob_cep = this.cep;
    }

    /**
     * Método de conveniência para atribuir um tipo produtor rural ao
     * fornecedor. Ao acionar este método é verificado o cnpj do fornecedor e se
     * for pessoa física atribui o valor {@link TipoEmpresa#PRODUTOR_RURAL_FISICA}
     * senão {@link TipoEmpresa#PRODUTOR_RURAL_JURIDICO}.
     */
    public void setProdutorRural() {
        if (Utils.stringToLong(this.cnpj_cpf) <= 99999999999L) {
            this.tipoEmpresa = TipoEmpresa.PRODUTOR_RURAL_FISICA;
        } else {
            this.tipoEmpresa = TipoEmpresa.PRODUTOR_RURAL_JURIDICO;
        }
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    /**
     * @return the prazoPedido
     */
    public int getPrazoPedido() {
        return prazoPedido;
    }

    /**
     * @param prazoPedido the prazoPedido to set
     */
    public void setPrazoPedido(int prazoPedido) {
        this.prazoPedido = prazoPedido;
    }

    public void setEmiteNfe(boolean emiteNfe) {
        this.emiteNfe = emiteNfe;
    }

    public boolean isEmiteNfe() {
        return emiteNfe;
    }

    public void setPermiteNfSemPedido(boolean permiteNfSemPedido) {
        this.permiteNfSemPedido = permiteNfSemPedido;
    }

    public boolean isPermiteNfSemPedido() {
        return permiteNfSemPedido;
    }

    /**
     * @return the idDivisao
     */
    public String getIdDivisao() {
        return idDivisao;
    }

    /**
     * @param idDivisao the idDivisao to set
     */
    public void setIdDivisao(String idDivisao) {
        this.idDivisao = idDivisao;
    }

    public String getUtilizaiva() {
        return utilizaiva;
    }

    public void setUtilizaiva(String utilizaiva) {
        this.utilizaiva = utilizaiva;
    }    
    
    public boolean getRevenda() {
        return revenda;
    }

    public void setRevenda(boolean revenda) {
        this.revenda = revenda;
    }    

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }
    
    
}
