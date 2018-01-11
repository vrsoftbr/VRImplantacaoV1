package vrimplantacao.vo.vrimplantacao;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import vrimplantacao.classe.Global;
import vrimplantacao.utils.Utils;

public class ClientePreferencialVO {
    
    private static final String NUMERO_DEFAULT = "0";
    private static final String ENDERECO_DEFAULT = "SEM ENDERECO";
    private static final String BAIRRO_DEFAULT = "SEM BAIRRO";
    private static final String INSC_ESTADUAL_DEFAULT = "ISENTO";
    
    public int id = 0;
    public long idLong = 0;
    public String nome = "";
    public int id_situacaocadastro = 1;
    public String endereco = ENDERECO_DEFAULT;
    public String bairro = BAIRRO_DEFAULT;
    public int id_estado = Global.idEstado;
    public int id_municipio = Global.idMunicipio;
    public long cep = Global.Cep;
    public String telefone = "";
    public String celular = "";
    public String email = "";
    public String inscricaoestadual = INSC_ESTADUAL_DEFAULT;
    public String orgaoemissor = "";
    public long cnpj = -1;
    public int id_tipoestadocivil = 0;
    public String datanascimento="";
    public String dataresidencia="1990/01/01";
    public String datacadastro="";
    public int id_tiporesidencia = 1;
    public int sexo = 0;
    public int id_banco = 804;
    public String agencia = "";
    public String conta = "";
    public String praca = "";
    public String observacao = "";
    public String empresa = "";
    public int id_estadoempresa = 0;
    public int id_municipioempresa = 0;
    public String enderecoempresa = "";
    public String bairroempresa = "";
    public double cepempresa = 0;
    public String telefoneempresa = "";
    public Date dataadmissao;
    public String cargo = "";
    public double salario = 0;
    public double outrarenda = 0;
    public double valorlimite = 0;
    public String nomeconjuge = "";
    public String datanascimentoconjuge="";
    public double cpfconjuge = 0;
    public String rgconjuge = "";
    public String orgaoemissorconjuge = "";
    public String empresaconjuge = "";
    public int id_estadoconjuge = 0;
    public int id_municipioconjuge = 0;
    public String enderecoempresaconjuge = "";
    public String bairroempresaconjuge = "";
    public double cepempresaconjuge = 0;
    public String telefoneempresaconjuge = "";
    public Date dataadmissaoconjuge;
    public String cargoconjuge = "";
    public double salarioconjuge = 0;
    public double outrarendaconjuge = 0;
    public int id_tipoinscricao = 0;
    public int vencimentocreditorotativo = 30;
    public String observacao2 = "";
    public boolean permitecreditorotativo = true;
    public boolean permitecheque = true;
    public String nomemae = "";
    public String nomepai = "";
    public Date datarestricao;
    public boolean bloqueado = false;
    public int id_plano = 1;
    public boolean bloqueadoautomatico = false;
    public String numero = NUMERO_DEFAULT;
    public int senha = 0;
    public int id_tiporestricaocliente = 0;
    public Date dataatualizacaocadastro = null;
    public String numeroempresa = "";
    public String numeroempresaconjuge = "";
    public String complemento = "";
    public String complementoempresa = "";
    public String complementoempresaconjuge = "";
    public int id_contacontabilfiscalpassivo = 0;
    public int id_contacontabilfiscalativo = 0;
    public boolean enviasms = false;
    public boolean enviaemail = false;   
    public String telefone2 = "";
    public String telefone3 = "";
    public String fax = "";
    public String homepage = "";
    public int id_grupo = 1;
    public int id_regiaocliente = 1;
    private int codigocidade_sistemaanterior = 0;
    
    /* sistema milenio */
    public long codigoAgente = 0;
    public long codigoanterior = 0;
    Utils util = new Utils();
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getIdLong() {
        return idLong;
    }

    public void setIdLong(long idLong) {
        this.idLong = idLong;
    }

    public String getNome()  {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome,40,"SEM NOME VR " + id);
    }

    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = Utils.acertarTexto(endereco, 40, ENDERECO_DEFAULT);
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = Utils.acertarTexto(bairro, 30, BAIRRO_DEFAULT);        
    }

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {      
        this.id_estado = id_estado;
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
    
    public void setCep(String cep) {
        cep = Utils.formataNumero(cep, 8, "0");
        this.cep = Long.parseLong(cep);
    }    

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        telefone = Utils.formataNumero(telefone, 14, "0000000000");
        this.telefone = telefone;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = Utils.formataNumero(celular, 14, "0000000000");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Utils.formataEmail(email, 50);
    }

    public String getInscricaoestadual() {
        return inscricaoestadual;
    }

    public void setInscricaoestadual(String inscricaoestadual) {
        this.inscricaoestadual = Utils.formataNumero(inscricaoestadual, 18, INSC_ESTADUAL_DEFAULT);
    }

    public String getOrgaoemissor() {
        return orgaoemissor;
    }

    public void setOrgaoemissor(String orgaoemissor) {
        this.orgaoemissor = Utils.acertarTexto(orgaoemissor, 6, "");
    }

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj;
    }

    public void setCnpj(String cnpj) {
        cnpj = Utils.formataNumero(cnpj);
        
        if ((!"".equals(cnpj.trim())) &&
                (Long.parseLong(cnpj) > 999999)) {
            this.cnpj = Utils.stringToLong(cnpj); 
        } else {
            this.cnpj = Utils.stringToLong("-1");
        }
        
        //if (("".equals(cnpj)) || cnpj.length() > 14){
        //    cnpj = "-1";
        //}
    }

    public int getId_tipoestadocivil() {
        return id_tipoestadocivil;
    }

    public void setId_tipoestadocivil(int id_tipoestadocivil) {
        this.id_tipoestadocivil = id_tipoestadocivil;
    }

    public String getDatanascimento() {
        return datanascimento;
    }

    public void setDatanascimento(String datanascimento) {
        this.datanascimento = (datanascimento==null ? "" : datanascimento);
    }
    
    public void setDatanascimento(Date datanascimento) {
        this.datanascimento = Utils.formatDate(datanascimento);
    }

    public String getDataresidencia() {
        return dataresidencia;
    }

    public void setDataresidencia(String dataresidencia) {
        this.dataresidencia = (dataresidencia==null ? "" : dataresidencia);        
    }

    public String getDatacadastro() {
        return datacadastro;
    }

    public void setDatacadastro(String datacadastro) {
        this.datacadastro = (datacadastro==null ? "" : datacadastro);
    }
    
    public void setDatacadastro(Date datacadastro) {
        this.datacadastro = Utils.formatDate(datacadastro);
    }

    public int getId_tiporesidencia() {
        return id_tiporesidencia;
    }

    public void setId_tiporesidencia(int id_tiporesidencia) {
        this.id_tiporesidencia = id_tiporesidencia;
    }

    public int getSexo() {
        return sexo;
    }

    public void setSexo(int sexo) {
        if (sexo == 0 || sexo == 1) {
            this.sexo = sexo;
        } else {
            this.sexo = 1;
        }
    }

    public int getId_banco() {
        return id_banco;
    }

    public void setId_banco(int id_banco) {
        this.id_banco = id_banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = Utils.acertarTexto(agencia, 30, "");        
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getPraca() {
        return praca;
    }

    public void setPraca(String praca) {
        this.praca = praca;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 80);
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = Utils.acertarTexto(empresa,35,"");
    }

    public int getId_estadoempresa() {
        return id_estadoempresa;
    }

    public void setId_estadoempresa(int id_estadoempresa) {
        this.id_estadoempresa = id_estadoempresa;
    }

    public int getId_municipioempresa() {
        return id_municipioempresa;
    }

    public void setId_municipioempresa(int id_municipioempresa) {
        this.id_municipioempresa = id_municipioempresa;
    }

    public String getEnderecoempresa() {
        return enderecoempresa;
    }

    public void setEnderecoempresa(String enderecoempresa) {
        this.enderecoempresa = Utils.acertarTexto(enderecoempresa, 30, "");
    }

    public String getBairroempresa() {
        return bairroempresa;
    }

    public void setBairroempresa(String bairroempresa) {
        this.bairroempresa = Utils.acertarTexto(bairroempresa, 30, "");;
    }

    public double getCepempresa() {
        return cepempresa;
    }

    public void setCepempresa(double cepempresa) {
        this.cepempresa = cepempresa;
    }

    public String getTelefoneempresa() {
        return telefoneempresa;
    }

    public void setTelefoneempresa(String telefoneempresa) {
        this.telefoneempresa = telefoneempresa;
    }

    public Date getDataadmissao() {
        return dataadmissao;
    }

    public void setDataadmissao(Date dataadmissao) {
        this.dataadmissao = dataadmissao;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = Utils.acertarTexto(cargo, 25, "");
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public double getOutrarenda() {
        return outrarenda;
    }

    public void setOutrarenda(double outrarenda) {
        this.outrarenda = outrarenda;
    }

    public double getValorlimite() {
        return valorlimite;
    }

    public void setValorlimite(double valorlimite) {
        // TODO Encontrar uma forma de tratar tamanho máximo de campos numéricos.
        if (valorlimite > 999999999.99d) {
            valorlimite = 999999999.99d;
        }
        this.valorlimite = valorlimite >= 0 ? valorlimite : 0;
    }

    public String getNomeconjuge() {
        return nomeconjuge;
    }

    public void setNomeconjuge(String nomeconjuge) {
        this.nomeconjuge = Utils.acertarTexto(nomeconjuge, 35, "");
    }

    public String getDatanascimentoconjuge() {
        return datanascimentoconjuge;
    }

    public void setDatanascimentoconjuge(String datanascimentoconjuge) {
        this.datanascimentoconjuge = datanascimentoconjuge;
    }

    public double getCpfconjuge() {
        return cpfconjuge;
    }

    public void setCpfconjuge(double cpfconjuge) {
        this.cpfconjuge = cpfconjuge;
    }

    public String getRgconjuge() {
        return rgconjuge;
    }

    public void setRgconjuge(String rgconjuge) {
        this.rgconjuge = Utils.acertarTexto(rgconjuge, 18, "");
    }

    public String getOrgaoemissorconjuge() {
        return orgaoemissorconjuge;
    }

    public void setOrgaoemissorconjuge(String orgaoemissorconjuge) {
        this.orgaoemissorconjuge = orgaoemissorconjuge;
    }

    public String getEmpresaconjuge() {
        return empresaconjuge;
    } 

    public void setEmpresaconjuge(String empresaconjuge) {
        this.empresaconjuge = Utils.acertarTexto(empresaconjuge,40,"");
    }

    public int getId_estadoconjuge() {
        return id_estadoconjuge;
    }

    public void setId_estadoconjuge(int id_estadoconjuge) {
        this.id_estadoconjuge = id_estadoconjuge;
    }

    public int getId_municipioconjuge() {
        return id_municipioconjuge;
    }

    public void setId_municipioconjuge(int id_municipioconjuge) {
        this.id_municipioconjuge = id_municipioconjuge;
    }

    public String getEnderecoempresaconjuge() {
        return enderecoempresaconjuge;
    }

    public void setEnderecoempresaconjuge(String enderecoempresaconjuge) {
        this.enderecoempresaconjuge = enderecoempresaconjuge;
    }

    public String getBairroempresaconjuge() {
        return bairroempresaconjuge;
    }

    public void setBairroempresaconjuge(String bairroempresaconjuge) {
        this.bairroempresaconjuge = bairroempresaconjuge;
    }

    public double getCepempresaconjuge() {
        return cepempresaconjuge;
    }

    public void setCepempresaconjuge(double cepempresaconjuge) {
        this.cepempresaconjuge = cepempresaconjuge;
    }

    public String getTelefoneempresaconjuge() {
        return telefoneempresaconjuge;
    }

    public void setTelefoneempresaconjuge(String telefoneempresaconjuge) {
        this.telefoneempresaconjuge = telefoneempresaconjuge;
    }

    public Date getDataadmissaoconjuge() {
        return dataadmissaoconjuge;
    }

    public void setDataadmissaoconjuge(Date dataadmissaoconjuge) {
        this.dataadmissaoconjuge = dataadmissaoconjuge;
    }

    public String getCargoconjuge() {
        return cargoconjuge;
    }

    public void setCargoconjuge(String cargoconjuge) {
        this.cargoconjuge = Utils.acertarTexto(cargoconjuge, 25, "");        
    }

    public double getSalarioconjuge() {
        return salarioconjuge;
    }

    public void setSalarioconjuge(double salarioconjuge) {
        this.salarioconjuge = salarioconjuge;
    }

    public double getOutrarendaconjuge() {
        return outrarendaconjuge;
    }

    public void setOutrarendaconjuge(double outrarendaconjuge) {
        this.outrarendaconjuge = outrarendaconjuge;
    }

    public int getId_tipoinscricao() {
        return this.id_tipoinscricao;
    }

    public void setId_tipoinscricao(int id_tipoinscricao) {
        this.id_tipoinscricao = id_tipoinscricao == 0 || id_tipoinscricao == 1 ? id_tipoinscricao : 0;
    }

    public int getVencimentocreditorotativo() {
        return vencimentocreditorotativo;
    }

    public void setVencimentocreditorotativo(int vencimentocreditorotativo) {
        this.vencimentocreditorotativo = vencimentocreditorotativo;
    }

    public String getObservacao2() {
        return observacao2;
    }

    public void setObservacao2(String observacao2) {
        this.observacao2 = Utils.acertarTexto(observacao2, 2500, "");
    }

    public boolean isPermitecreditorotativo() {
        return permitecreditorotativo;
    }

    public void setPermitecreditorotativo(boolean permitecreditorotativo) {
        this.permitecreditorotativo = permitecreditorotativo;
    }

    public boolean isPermitecheque() {
        return permitecheque;
    }

    public void setPermitecheque(boolean permitecheque) {
        this.permitecheque = permitecheque;
    }

    public String getNomemae() {
        return nomemae;
    }

    public void setNomemae(String nomemae) {
        this.nomemae = Utils.acertarTexto(nomemae,40,"");
    }

    public String getNomepai() {
        return nomepai;
    }

    public void setNomepai(String nomepai)  {
        this.nomepai = Utils.acertarTexto(nomepai,40,"");        
    }

    public Date getDatarestricao() {
        return datarestricao;
    }

    public void setDatarestricao(Date datarestricao) {
        this.datarestricao = datarestricao;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getId_plano() {
        return id_plano;
    }

    public void setId_plano(int id_plano) {
        this.id_plano = id_plano;
    }

    public boolean isBloqueadoautomatico() {
        return bloqueadoautomatico;
    }

    public void setBloqueadoautomatico(boolean bloqueadoautomatico) {
        this.bloqueadoautomatico = bloqueadoautomatico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        if (("S/N".equals(numero) || "SN".equals(numero))) {
            this.numero = "0";
        } else {
            this.numero = Utils.acertarTexto(numero, 6, NUMERO_DEFAULT);
        }
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }

    public int getId_tiporestricaocliente() {
        return id_tiporestricaocliente;
    }

    public void setId_tiporestricaocliente(int id_tiporestricaocliente) {
        this.id_tiporestricaocliente = id_tiporestricaocliente;
    }

    public Date getDataatualizacaocadastro() {
        return dataatualizacaocadastro;
    }

    public void setDataatualizacaocadastro(Date dataatualizacaocadastro) {
        this.dataatualizacaocadastro = dataatualizacaocadastro;
    }

    public String getNumeroempresa() {
        return numeroempresa;
    }

    public void setNumeroempresa(String numeroempresa) {
        this.numeroempresa = numeroempresa;
    }

    public String getNumeroempresaconjuge() {
        return numeroempresaconjuge;
    }

    public void setNumeroempresaconjuge(String numeroempresaconjuge) {
        this.numeroempresaconjuge = numeroempresaconjuge;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) throws UnsupportedEncodingException {
        this.complemento = Utils.acertarTexto(complemento, 30);
    }

    public String getComplementoempresa() {
        return complementoempresa;
    }

    public void setComplementoempresa(String complementoempresa) {
        this.complementoempresa = complementoempresa;
    }

    public String getComplementoempresaconjuge() {
        return complementoempresaconjuge;
    }

    public void setComplementoempresaconjuge(String complementoempresaconjuge) {
        this.complementoempresaconjuge = complementoempresaconjuge;
    }

    public int getId_contacontabilfiscalpassivo() {
        return id_contacontabilfiscalpassivo;
    }

    public void setId_contacontabilfiscalpassivo(int id_contacontabilfiscalpassivo) {
        this.id_contacontabilfiscalpassivo = id_contacontabilfiscalpassivo;
    }

    public int getId_contacontabilfiscalativo() {
        return id_contacontabilfiscalativo;
    }

    public void setId_contacontabilfiscalativo(int id_contacontabilfiscalativo) {
        this.id_contacontabilfiscalativo = id_contacontabilfiscalativo;
    }

    public boolean isEnviasms() {
        return enviasms;
    }

    public void setEnviasms(boolean enviasms) {
        this.enviasms = enviasms;
    }

    public boolean isEnviaemail() {
        return enviaemail;
    }

    public void setEnviaemail(boolean enviaemail) {
        this.enviaemail = enviaemail;
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

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = Utils.formataNumero(fax, 14);
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(int id_grupo) {
        this.id_grupo = id_grupo;
    }

    public int getId_regiaocliente() {
        return id_regiaocliente;
    }

    public void setId_regiaocliente(int id_regiaocliente) {
        this.id_regiaocliente = id_regiaocliente;
    }

    public long getCodigoAgente() {
        return codigoAgente;
    }

    public void setCodigoAgente(long codigoAgente) {
        this.codigoAgente = codigoAgente;
    }

    public long getCodigoanterior() {
        return codigoanterior;
    }

    public void setCodigoanterior(long codigoanterior) {
        this.codigoanterior = codigoanterior;
    }

    public int getCodigocidade_sistemaanterior() {
        return codigocidade_sistemaanterior;
    }

    public void setCodigocidade_sistemaanterior(int codigocidade_sistemaanterior) {
        this.codigocidade_sistemaanterior = codigocidade_sistemaanterior;
    }
}