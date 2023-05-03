/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;

/**
 *
 * @author Michael
 */
public class ClienteGenericoDAO extends ProviderGenericoDAO {

    private String sqlScript;
    private Statement stm;
    private int opcaoScript = 1;

    public Statement getStm() {
        return stm;
    }

    public void setScript(String script) {
        this.sqlScript = script;
    }

    public String getSqlScript() {
        return sqlScript;
    }

    public void setTipoScript(int opcaoScript) {
        this.opcaoScript = opcaoScript;
    }

    private int getOpcaoScript() {
        return opcaoScript;
    }

    @Override
    public String getSistema() {
        return "GENERICO";
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        String bancoEscolhido = getBancoDados();
        setStatement(bancoEscolhido);
        this.stm = getStatement();
        List<ClienteIMP> result = new ArrayList<>();
        try (ResultSet rs = stm.executeQuery(
                getSqlScript())) {
            while (rs.next()) {
                ClienteIMP imp = new ClienteIMP();

                if (getOpcaoScript() == 2) {
                    imp.setId(rs.getString("id") == null ? "-1" : rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj") == null ? "0" : rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual") == null ? "0" : rs.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rs.getString("orgaoemissor") == null ? "" : rs.getString("orgaoemissor"));
                    imp.setRazao(rs.getString("razao") == null ? "NAO CONSTA RAZAO SOCIAL" : rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia") == null ? "NAO CONSTA FANTASIA" : rs.getString("fantasia"));
                    imp.setAtivo(Integer.parseInt(rs.getString("id_situacaocadastro") == null ? "1" : rs.getString("id_situacaocadastro")) == 1);
                    imp.setBloqueado("S".equals(rs.getString("bloqueado") == null ? "N" : rs.getString("bloqueado")));
                    imp.setDataBloqueio(rs.getDate("databloqueio") instanceof Date ? rs.getDate("databloqueio") : null);
                    imp.setEndereco(rs.getString("endereco") == null ? "SEM ENDERECO" : rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero") == null ? "" : rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento") == null ? "" : rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro") == null ? "" : rs.getString("bairro"));
                    imp.setMunicipioIBGE(rs.getString("municipioibge") == null ? "" : rs.getString("municipioibge"));
                    imp.setMunicipio(rs.getString("municipio") == null ? "" : rs.getString("municipio"));
                    imp.setUfIBGE(Integer.parseInt(rs.getString("ufibge") == null || rs.getString("ufibge").isEmpty() ? "0" : rs.getString("ufibge")));
                    imp.setUf(rs.getString("uf") == null ? "" : rs.getString("uf"));
                    imp.setCep(rs.getString("cep") == null ? "" : rs.getString("cep"));
                    imp.setEstadoCivil(Integer.parseInt(rs.getString("id_estadocivil") == null || rs.getString("id_estadocivil").isEmpty() ? "0" : rs.getString("id_estadocivil")));
                    imp.setDataNascimento(rs.getDate("datanascimento") instanceof Date ? rs.getDate("datanascimento") : null);
                    imp.setDataCadastro(rs.getDate("datacadastro") instanceof Date ? rs.getDate("datacadastro") : null);
                    imp.setSexo("F".equals(rs.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rs.getString("empresa") == null ? "" : rs.getString("empresa"));
                    imp.setEmpresaEndereco(rs.getString("empresaendereco") == null ? "" : rs.getString("empresaendereco"));
                    imp.setEmpresaNumero(rs.getString("empresanumero") == null ? "" : rs.getString("empresanumero"));
                    imp.setEmpresaComplemento(rs.getString("empresacomplemento") == null ? "" : rs.getString("empresacomplemento"));
                    imp.setEmpresaBairro(rs.getString("empresabairro") == null ? "" : rs.getString("empresabairro"));
                    imp.setEmpresaMunicipioIBGE(Integer.parseInt(rs.getString("empresamunicipioibge") == null || rs.getString("empresamunicipioibge").isEmpty() ? "0" : rs.getString("empresamunicipioibge")));
                    imp.setEmpresaMunicipio(rs.getString("empresamunicipio") == null ? "" : rs.getString("empresamunicipio"));
                    imp.setEmpresaUfIBGE(Integer.parseInt(rs.getString("empresaufibge") == null || rs.getString("empresaufibge").isEmpty() ? "0" : rs.getString("empresaufibge")));
                    imp.setEmpresaUf(rs.getString("empresauf") == null ? "" : rs.getString("empresauf"));
                    imp.setEmpresaCep(rs.getString("empresacep") == null ? "" : rs.getString("empresacep"));
                    imp.setEmpresaTelefone(rs.getString("empresatelefone") == null ? "" : rs.getString("empresatelefone"));
                    imp.setDataAdmissao(rs.getDate("dataadmissao") instanceof Date ? rs.getDate("dataadmissao") : null);
                    imp.setCargo(rs.getString("cargo") == null ? "" : rs.getString("cargo"));
                    imp.setSalario(Double.parseDouble(rs.getString("salario") == null || rs.getString("salario").isEmpty() ? "0" : rs.getString("salario")));
                    imp.setValorLimite(Double.parseDouble(rs.getString("valorlimite") == null || rs.getString("valorlimite").isEmpty() ? "0" : rs.getString("valorlimite")));
                    imp.setNomeConjuge(rs.getString("nomeconjuge") == null ? "" : rs.getString("nomeconjuge"));
                    imp.setNomePai(rs.getString("nomepai") == null ? "" : rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae") == null ? "" : rs.getString("nomemae"));
                    imp.setEmail(rs.getString("email") == null ? "" : rs.getString("email"));
                    imp.setCobrancaTelefone(rs.getString("cobrancatelefone") == null ? "" : rs.getString("cobrancatelefone"));
                    imp.setPrazoPagamento(Integer.parseInt(rs.getString("prazopagamento") == null || rs.getString("prazopagamento").isEmpty() ? "0" : rs.getString("prazopagamento")));
                    imp.setCobrancaEndereco(rs.getString("cobrancaendereco") == null ? "" : rs.getString("cobrancaendereco"));
                    imp.setCobrancaNumero(rs.getString("cobrancanumero") == null ? "" : rs.getString("cobrancanumero"));
                    imp.setCobrancaComplemento(rs.getString("cobrancacomplemento") == null ? "" : rs.getString("cobrancacomplemento"));
                    imp.setCobrancaBairro(rs.getString("cobrancabairro") == null ? "" : rs.getString("cobrancabairro"));
                    imp.setCobrancaMunicipioIBGE(rs.getInt("cobrancamunicipioibge"));
                    imp.setCobrancaMunicipio(rs.getString("cobrancamunicipio") == null ? "" : rs.getString("cobrancamunicipio"));
                    imp.setCobrancaUfIBGE(Integer.parseInt(rs.getString("cobrancaufibge") == null || rs.getString("cobrancaufibge").isEmpty() ? "0" : rs.getString("cobrancaufibge")));
                    imp.setCobrancaUf(rs.getString("cobrancauf") == null ? "" : rs.getString("cobrancauf"));
                    imp.setCobrancaCep(rs.getString("cobrancacep") == null ? "" : rs.getString("cobrancacep"));
                    imp.setInscricaoMunicipal(rs.getString("inscricaomunicipal") == null ? "" : rs.getString("inscricaomunicipal"));
                    imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);
                    imp.setTelefone(rs.getString("telefone") == null ? "0" : rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular") == null ? "0" : rs.getString("celular"));

                    //imp.addEmail(rs.getString("email"), TipoContato.COMERCIAL);
                    imp.addCelular("CELULAR", rs.getString("celular2") == null ? "0" : rs.getString("celular2"));
                    imp.addTelefone("TELEFONE 2", rs.getString("telefone2") == null ? "0" : rs.getString("telefone2"));

                    result.add(imp);
                } else {
                    imp.setId(rs.getString("id") == null ? "-1" : rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj") == null ? "0" : rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual") == null ? "0" : rs.getString("inscricaoestadual"));
                    imp.setRazao(rs.getString("razao") == null ? "NAO CONSTA RAZAO SOCIAL" : rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia") == null ? "NAO CONSTA FANTASIA" : rs.getString("fantasia"));
                    imp.setAtivo(Integer.parseInt(rs.getString("id_situacaocadastro") == null ? "1" : rs.getString("id_situacaocadastro")) == 1);
                    imp.setEndereco(rs.getString("endereco") == null ? "SEM ENDERECO" : rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero") == null ? "" : rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento") == null ? "" : rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro") == null ? "" : rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio") == null ? "" : rs.getString("municipio"));
                    imp.setUf(rs.getString("uf") == null ? "" : rs.getString("uf"));
                    imp.setCep(rs.getString("cep") == null ? "" : rs.getString("cep"));
                    imp.setEstadoCivil(Integer.parseInt(rs.getString("id_estadocivil") == null || rs.getString("id_estadocivil").isEmpty() ? "0" : rs.getString("id_estadocivil")));
                    imp.setDataNascimento(rs.getDate("datanascimento") instanceof Date ? rs.getDate("datanascimento") : null);
                    imp.setDataCadastro(rs.getDate("datacadastro") instanceof Date ? rs.getDate("datacadastro") : null);
                    imp.setSalario(Double.parseDouble(rs.getString("salario") == null || rs.getString("salario").isEmpty() ? "0" : rs.getString("salario")));
                    imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);
                    imp.setTelefone(rs.getString("telefone") == null ? "0" : rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular") == null ? "0" : rs.getString("celular"));

                    imp.addTelefone("TELEFONE 2", rs.getString("telefone2") == null ? "0" : rs.getString("telefone2"));

                    result.add(imp);
                }
            }
        }
        return result;
    }     
}
