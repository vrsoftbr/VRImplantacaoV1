package vrimplantacao.vo.vrimplantacao;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.ContaContabilFinanceiro;
import vrimplantacao2.vo.enums.TipoIndicadorIE;

public class FornecedorVO {
    public int id = 0;
    public String razaosocial = "";
    public String nomefantasia = "";
    public String endereco = "";
    public String bairro = "";
    public int id_municipio = 0;
    public long cep = 0;
    public int id_estado = 0;
    public String telefone = "";
    public int id_tipoinscricao = 0;
    public String inscricaoestadual = "";
    public long cnpj = 0;
    public boolean revenda = false;
    public int id_situacaocadastro = 1;
    public int id_tipopagamento = 1;
    public int numerodoc = 0;
    public int pedidominimoqtd = 0;
    public double pedidominimovalor = 0;
    public String serienf = "1";
    public boolean descontofunrural = false;
    public int senha = 0;
    public int id_tiporecebimento = 0;
    public String agencia = "";
    public String digitoagencia = "";
    public String conta = "";
    public String digitoconta = "";
    public int id_banco = -1;
    public int id_fornecedorfavorecido;
    public String enderecocobranca = "";
    public String bairrocobranca = "";
    public long cepcobranca = 0;
    public int id_municipiocobranca;
    public int id_estadocobranca;
    public boolean bloqueado = false;
    public int id_tipomotivofornecedor;
    public Timestamp datasintegra;
    public int id_tipoempresa = 3;
    public String inscricaosuframa = "0";
    public boolean utilizaiva = false;
    public int id_familiafornecedor;
    public int id_tipoinspecao;
    public int numeroinspecao = 0;
    public int id_tipotroca;
    public int id_tipofornecedor = 1;
    public ContaContabilFinanceiro id_contacontabilfinanceiro = ContaContabilFinanceiro.PAGAMENTO_FORNECEDOR;
    public boolean utilizanfe = true;
    public Date datacadastro;
    public String datacadastroStr;
    public boolean utilizaconferencia = false;
    public String numero = "";
    public boolean permitenfsempedido = false;
    public String modelonf = "55";
    public boolean emitenf = true;
    public int tiponegociacao = 0;
    public boolean utilizacrossdocking = false;
    public int id_lojacrossdocking;
    public String observacao = "";
    public int id_pais;
    public String inscricaomunicipal = "";
    public int id_contacontabilfiscalpassivo;
    public String numerocobranca = "";
    public String complemento = "";
    public String complementocobranca = "";
    public int id_contacontabilfiscalativo;
    public boolean utilizaedi = false;
    public int tiporegravencimento = -1;    
    /* dados adicionais*/
    public long codigoanterior = 0;
    public String telefone2 = "";
    public String telefone3 = "";
    public String telefone4 = "";
    public String celular = "";
    public String fax = "";
    public String email = "";
    public String email2 = "";
    public String representante = "";
    public String homepage = "";
    public int idLoja = 1;
    private int codigocidade_sistemaanterior = 0;
    public List<FornecedorContatoVO> vFornecedorContato = new ArrayList<>();
    
    
    private TipoIndicadorIE id_tipoindicadorie = TipoIndicadorIE.NAO_CONTRIBUINTE;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRazaosocial() {
        return razaosocial;
    }

    public void setRazaosocial(String razaosocial) throws UnsupportedEncodingException {        
        this.razaosocial = Utils.acertarTexto(razaosocial, 40, "SEM RAZAO SOCIAL");
    }

    public String getNomefantasia() {
        return nomefantasia;
    }

    public void setNomefantasia(String nomefantasia) throws UnsupportedEncodingException {               
        this.nomefantasia = Utils.acertarTexto(nomefantasia, 30, "SEM NOME FANTASIA");
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) throws UnsupportedEncodingException {       
        this.endereco = Utils.acertarTexto(endereco, 40, "SEM ENDERECO");
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) throws UnsupportedEncodingException {
        this.bairro = Utils.acertarTexto(bairro, 30, "SEM BAIRRO");
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public long getCep() {
        return cep;
    }

    public void setCep(long cep) {
        this.cep = cep;
    }

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        telefone = Utils.formataNumero(telefone, 14, "0000000000");
        if (telefone.length() == 10 || telefone.length() == 11) {
            this.telefone = telefone;
        } else {
            this.telefone = "0000000000";
        }
    }

    public int getId_tipoinscricao() {
        return id_tipoinscricao;
    }

    public void setId_tipoinscricao(int id_tipoinscricao) {
        this.id_tipoinscricao = id_tipoinscricao;
    }

    public void setId_tipoindicadorie(TipoIndicadorIE id_tipoindicadorie) {
        this.id_tipoindicadorie = id_tipoindicadorie;
    }
    
    public void setId_tipoindicadorie() {
        if (getInscricaoestadual() != null && !"".equals(getInscricaoestadual()) && !"ISENTO".equals(getInscricaoestadual())) {
            this.id_tipoindicadorie = TipoIndicadorIE.CONTRIBUINTE_ICMS;
        } else {
            this.id_tipoindicadorie = TipoIndicadorIE.NAO_CONTRIBUINTE;
        }
    }

    public TipoIndicadorIE getId_tipoindicadorie() {
        return id_tipoindicadorie;
    }

    public String getInscricaoestadual() {
        return inscricaoestadual;
    }

    public void setInscricaoestadual(String inscricaoestadual) {   
        this.inscricaoestadual = Utils.formataNumero(inscricaoestadual, 20, "ISENTO");
    }

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        if (Utils.isCnpjCpfValido(cnpj))
        {
            this.cnpj = cnpj;
        } else {
            this.cnpj = -1;
        }        
    }

    public boolean isRevenda() {
        return revenda;
    }

    public void setRevenda(boolean revenda) {
        this.revenda = revenda;
    }

    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public int getId_tipopagamento() {
        return id_tipopagamento;
    }

    public void setId_tipopagamento(int id_tipopagamento) {
        this.id_tipopagamento = id_tipopagamento;
    }

    public int getNumerodoc() {
        return numerodoc;
    }

    public void setNumerodoc(int numerodoc) {
        this.numerodoc = numerodoc;
    }

    public int getPedidominimoqtd() {
        return pedidominimoqtd;
    }

    public void setPedidominimoqtd(int pedidominimoqtd) {
        this.pedidominimoqtd = pedidominimoqtd;
    }

    public double getPedidominimovalor() {
        return pedidominimovalor;
    }

    public void setPedidominimovalor(double pedidominimovalor) {
        this.pedidominimovalor = pedidominimovalor;
    }

    public String getSerienf() {
        return serienf;
    }

    public void setSerienf(String serienf) {
        this.serienf = serienf;
    }

    public boolean isDescontofunrural() {
        return descontofunrural;
    }

    public void setDescontofunrural(boolean descontofunrural) {
        this.descontofunrural = descontofunrural;
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }

    public int getId_tiporecebimento() {
        return id_tiporecebimento;
    }

    public void setId_tiporecebimento(int id_tiporecebimento) {
        this.id_tiporecebimento = id_tiporecebimento;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        
        if ((agencia != null) &&
                (!agencia.trim().isEmpty())) {
            
            if (agencia.length() > 6) {
                agencia = agencia.substring(0, 6);
            }
        } else {
            agencia = "";
        }
        this.agencia = Utils.acertarTexto(agencia);
    }

    public String getDigitoagencia() {
        return digitoagencia;
    }

    public void setDigitoagencia(String digitoagencia) {
        
        if ((digitoagencia != null) &&
                (!digitoagencia.trim().isEmpty())) {
            
            if (digitoagencia.length() > 2) {
                digitoagencia = digitoagencia.substring(0, 2);
            }
        } else {
            digitoagencia = "";
        }
        this.digitoagencia = Utils.acertarTexto(digitoagencia);
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        
        if ((conta != null) &&
                (!conta.trim().isEmpty())) {
            
            if (conta.length() > 12) {
                conta = conta.substring(0, 12);
            }
        } else {
            conta = "";
        }
        this.conta = Utils.acertarTexto(conta);
    }

    public String getDigitoconta() {
        return digitoconta;
    }

    public void setDigitoconta(String digitoconta) {
        
        if ((digitoconta != null) &&
                (!digitoconta.trim().isEmpty())) {
            
            if (digitoconta.length() > 2) {
                digitoconta = digitoconta.substring(0, 2);
            }
        } else {
            digitoconta = "";
        }
        this.digitoconta = digitoconta;
    }

    public int getId_banco() {
        return id_banco;
    }

    public void setId_banco(int id_banco) {
        this.id_banco = id_banco;
    }

    public int getId_fornecedorfavorecido() {
        return id_fornecedorfavorecido;
    }

    public void setId_fornecedorfavorecido(int id_fornecedorfavorecido) {
        this.id_fornecedorfavorecido = id_fornecedorfavorecido;
    }

    public String getEnderecocobranca() {
        return enderecocobranca;
    }

    public void setEnderecocobranca(String enderecocobranca) throws UnsupportedEncodingException {
        this.enderecocobranca = Utils.acertarTexto(enderecocobranca, 40);
    }

    public String getBairrocobranca() {
        return bairrocobranca;
    }

    public void setBairrocobranca(String bairrocobranca) throws UnsupportedEncodingException {
        this.bairrocobranca = Utils.acertarTexto(bairrocobranca, 30, "");;
    }

    public long getCepcobranca() {
        return cepcobranca;
    }

    public void setCepcobranca(long cepcobranca) {
        this.cepcobranca = cepcobranca;
    }

    public int getId_municipiocobranca() {
        return id_municipiocobranca;
    }

    public void setId_municipiocobranca(int id_municipiocobranca) {
        this.id_municipiocobranca = id_municipiocobranca;
    }

    public int getId_estadocobranca() {
        return id_estadocobranca;
    }

    public void setId_estadocobranca(int id_estadocobranca) {
        this.id_estadocobranca = id_estadocobranca;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getId_tipomotivofornecedor() {   
        return id_tipomotivofornecedor;
    }

    public void setId_tipomotivofornecedor(int id_tipomotivofornecedor) {
        this.id_tipomotivofornecedor = id_tipomotivofornecedor;
    }

    public Timestamp getDatasintegra() {
        return datasintegra;
    }

    public void setDatasintegra(Timestamp datasintegra) {
        this.datasintegra = datasintegra;
    }

    public int getId_tipoempresa() {
        return id_tipoempresa;
    }

    public void setId_tipoempresa(int id_tipoempresa) {
        this.id_tipoempresa = id_tipoempresa;
    }

    public String getInscricaosuframa() {
        return inscricaosuframa;
    }

    public void setInscricaosuframa(String inscricaosuframa) {
        this.inscricaosuframa = inscricaosuframa;
    }

    public boolean isUtilizaiva() {
        return utilizaiva;
    }

    public void setUtilizaiva(boolean utilizaiva) {
        this.utilizaiva = utilizaiva;
    }

    public int getId_familiafornecedor() {
        return id_familiafornecedor;
    }

    public void setId_familiafornecedor(int id_familiafornecedor) {
        this.id_familiafornecedor = id_familiafornecedor;
    }

    public int getId_tipoinspecao() {
        return id_tipoinspecao;
    }

    public void setId_tipoinspecao(int id_tipoinspecao) {
        this.id_tipoinspecao = id_tipoinspecao;
    }

    public int getNumeroinspecao() {
        return numeroinspecao;
    }

    public void setNumeroinspecao(int numeroinspecao) {
        this.numeroinspecao = numeroinspecao;
    }

    public int getId_tipotroca() {
        return id_tipotroca;
    }

    public void setId_tipotroca(int id_tipotroca) {
        this.id_tipotroca = id_tipotroca;
    }

    public int getId_tipofornecedor() {
        return id_tipofornecedor;
    }

    public void setId_tipofornecedor(int id_tipofornecedor) {
        this.id_tipofornecedor = id_tipofornecedor;
    }

    public ContaContabilFinanceiro getId_contacontabilfinanceiro() {
        return id_contacontabilfinanceiro;
    }

    public void setId_contacontabilfinanceiro(ContaContabilFinanceiro id_contacontabilfinanceiro) {
        this.id_contacontabilfinanceiro = id_contacontabilfinanceiro;
    }

    public boolean isUtilizanfe() {
        return utilizanfe;
    }

    public void setUtilizanfe(boolean utilizanfe) {
        this.utilizanfe = utilizanfe;
    }

    public Date getDatacadastro() {
        return datacadastro;
    }

    public void setDatacadastro(Date datacadastro) {
        this.datacadastro = datacadastro;
    }

    public boolean isUtilizaconferencia() {
        return utilizaconferencia;
    }

    public void setUtilizaconferencia(boolean utilizaconferencia) {
        this.utilizaconferencia = utilizaconferencia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) throws UnsupportedEncodingException {
        this.numero = Utils.acertarTexto(numero, 6);
    }

    public boolean isPermitenfsempedido() {
        return permitenfsempedido;
    }

    public void setPermitenfsempedido(boolean permitenfsempedido) {
        this.permitenfsempedido = permitenfsempedido;
    }

    public String getModelonf() {
        return modelonf;
    }

    public void setModelonf(String modelonf) {
        this.modelonf = modelonf;
    }

    public boolean isEmitenf() {
        return emitenf;
    }

    public void setEmitenf(boolean emitenf) {
        this.emitenf = emitenf;
    }

    public int getTiponegociacao() {
        return tiponegociacao;
    }

    public void setTiponegociacao(int tiponegociacao) {
        this.tiponegociacao = tiponegociacao;
    }

    public boolean isUtilizacrossdocking() {
        return utilizacrossdocking;
    }

    public void setUtilizacrossdocking(boolean utilizacrossdocking) {
        this.utilizacrossdocking = utilizacrossdocking;
    }

    public int getId_lojacrossdocking() {
        return id_lojacrossdocking;
    }

    public void setId_lojacrossdocking(int id_lojacrossdocking) {
        this.id_lojacrossdocking = id_lojacrossdocking;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao);
    }

    public int getId_pais() {
        return id_pais;
    }

    public void setId_pais(int id_pais) {
        this.id_pais = id_pais;
    }

    public String getInscricaomunicipal() {
        return inscricaomunicipal;
    }

    public void setInscricaomunicipal(String inscricaomunicipal) {
        this.inscricaomunicipal = inscricaomunicipal;
    }

    public int getId_contacontabilfiscalpassivo() {
        return id_contacontabilfiscalpassivo;
    }

    public void setId_contacontabilfiscalpassivo(int id_contacontabilfiscalpassivo) {
        this.id_contacontabilfiscalpassivo = id_contacontabilfiscalpassivo;
    }

    public String getNumerocobranca() {
        return numerocobranca;
    }

    public void setNumerocobranca(String numerocobranca) throws UnsupportedEncodingException {
        this.numerocobranca = Utils.acertarTexto(numerocobranca, 6);
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) throws UnsupportedEncodingException {
        this.complemento = Utils.acertarTexto(complemento, 30);
    }

    public String getComplementocobranca() {
        return complementocobranca;
    }

    public void setComplementocobranca(String complementocobranca) throws UnsupportedEncodingException {
        this.complementocobranca = Utils.acertarTexto(complementocobranca, 30);
    }

    public int getId_contacontabilfiscalativo() {
        return id_contacontabilfiscalativo;
    }

    public void setId_contacontabilfiscalativo(int id_contacontabilfiscalativo) {
        this.id_contacontabilfiscalativo = id_contacontabilfiscalativo;
    }

    public boolean isUtilizaedi() {
        return utilizaedi;
    }

    public void setUtilizaedi(boolean utilizaedi) {
        this.utilizaedi = utilizaedi;
    }

    public int getTiporegravencimento() {
        return tiporegravencimento;
    }

    public void setTiporegravencimento(int tiporegravencimento) {
        this.tiporegravencimento = tiporegravencimento;
    }

    public long getCodigoanterior() {
        return codigoanterior;
    }

    public void setCodigoanterior(int codigoanterior) {
        this.codigoanterior = codigoanterior;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = Utils.formataNumero(telefone2, 14, "");
    }

    public String getTelefone3() {
        return telefone3;
    }

    public void setTelefone3(String telefone3) {
        this.telefone3 = Utils.formataNumero(telefone3, 14, "");
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = Utils.formataNumero(celular, 14, "");
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = Utils.formataNumero(fax, 14, "");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = Utils.acertarTexto(email, 50);
        this.email = email.toLowerCase();
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public int getCodigocidade_sistemaanterior() {
        return codigocidade_sistemaanterior;
    }

    public void setCodigocidade_sistemaanterior(int codigocidade_sistemaanterior) {
        this.codigocidade_sistemaanterior = codigocidade_sistemaanterior;
    }

    /**
     * @return the datacadastroStr
     */
    public String getDatacadastroStr() {
        return datacadastroStr;
    }

    /**
     * @param datacadastroStr the datacadastroStr to set
     */
    public void setDatacadastroStr(String datacadastroStr) {
        this.datacadastroStr = datacadastroStr;
    }
    
    
}