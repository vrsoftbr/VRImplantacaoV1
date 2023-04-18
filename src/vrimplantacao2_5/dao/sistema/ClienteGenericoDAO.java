/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

    public ClienteGenericoDAO() throws Exception {
        String bancoEscolhido = getBancoDados();
        setStatement(bancoEscolhido);
        this.stm = getStatement();
    }

    public ClienteGenericoDAO(ProviderGenericoDAO dao) throws Exception {
        String bancoEscolhido = dao.getBancoDados();
        dao.setStatement(bancoEscolhido);
        this.stm = dao.getStatement();
    }

    public Statement getStm() {
        return stm;
    }

    @Override
    public String getSistema() {
        return "GENERICO";
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        System.out.println(getStm());
        List<ClienteIMP> result = new ArrayList<>();
        try (ResultSet rs = stm.executeQuery(
                getSqlScript())) {
            while (rs.next()) {
                ClienteIMP imp = new ClienteIMP();

                imp.setId(rs.getString("id"));
                imp.setCnpj(rs.getString("cnpj"));
                imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                imp.setOrgaoemissor(rs.getString("orgaoemissor"));
                imp.setRazao(rs.getString("razao"));
                imp.setFantasia(rs.getString("fantasia"));
                imp.setAtivo(rs.getInt("id_situacaocadastro") == 1);
                imp.setBloqueado("S".equals(rs.getString("bloqueado")));
                imp.setDataBloqueio(rs.getDate("databloqueio"));
                imp.setEndereco(rs.getString("endereco"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setMunicipioIBGE(rs.getString("municipioibge"));
                imp.setMunicipio(rs.getString("municipio"));
                imp.setUfIBGE(rs.getInt("ufibge"));
                imp.setUf(rs.getString("uf"));
                imp.setCep(rs.getString("cep"));
                imp.setEstadoCivil(rs.getInt("id_estadocivil"));
                imp.setDataNascimento(rs.getDate("datanascimento"));
                imp.setDataCadastro(rs.getDate("datacadastro"));
                imp.setSexo("F".equals(rs.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                imp.setEmpresa(rs.getString("empresa"));
                imp.setEmpresaEndereco(rs.getString("empresaendereco"));
                imp.setEmpresaNumero(rs.getString("empresanumero"));
                imp.setEmpresaComplemento(rs.getString("empresacomplemento"));
                imp.setEmpresaBairro(rs.getString("empresabairro"));
                imp.setEmpresaMunicipioIBGE(rs.getInt("empresamunicipioibge"));
                imp.setEmpresaMunicipio(rs.getString("empresamunicipio"));
                imp.setEmpresaUfIBGE(rs.getInt("empresaufibge"));
                imp.setEmpresaUf(rs.getString("empresauf"));
                imp.setEmpresaCep(rs.getString("empresacep"));
                imp.setEmpresaTelefone(rs.getString("empresatelefone"));
                imp.setDataAdmissao(rs.getDate("dataadmissao"));
                imp.setCargo(rs.getString("cargo"));
                imp.setSalario(rs.getDouble("salario"));
                imp.setValorLimite(rs.getDouble("valorlimite"));
                imp.setNomeConjuge(rs.getString("nomeconjuge"));
                imp.setNomePai(rs.getString("nomepai"));
                imp.setNomeMae(rs.getString("nomemae"));
                imp.setEmail(rs.getString("email"));
                imp.setCobrancaTelefone(rs.getString("cobrancatelefone"));
                imp.setPrazoPagamento(rs.getInt("prazopagamento"));
                imp.setCobrancaEndereco(rs.getString("cobrancaendereco"));
                imp.setCobrancaNumero(rs.getString("cobrancanumero"));
                imp.setCobrancaComplemento(rs.getString("cobrancacomplemento"));
                imp.setCobrancaBairro(rs.getString("cobrancabairro"));
                imp.setCobrancaMunicipioIBGE(rs.getInt("cobrancamunicipioibge"));
                imp.setCobrancaMunicipio(rs.getString("cobrancamunicipio"));
                imp.setCobrancaUfIBGE(rs.getInt("cobrancaufibge"));
                imp.setCobrancaUf(rs.getString("cobrancauf"));
                imp.setCobrancaCep(rs.getString("cobrancacep"));
                imp.setInscricaoMunicipal(rs.getString("inscricaomunicipal"));
                imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);

                //getContatoCliente(imp);
                result.add(imp);
            }
        }
        return result;
    }

    private void getContatoCliente(ClienteIMP imp) throws SQLException {
        try (ResultSet rs = stm.executeQuery(
                "select \n"
                + "	cp.id,\n"
                + "	nome,\n"
                + "	telefone,\n"
                + "	celular,\n"
                + "	tc.descricao contato\n"
                + "from \n"
                + "	clientepreferencialcontato cp\n"
                + "join tipocontato tc on cp.id_tipocontato = tc.id \n"
                + "where 	\n"
                + "	cp.id_clientepreferencial = " + imp.getId())) {
            while (rs.next()) {
                imp.addContato(rs.getString("id"), rs.getString("nome"), rs.getString("telefone"), rs.getString("celular"), null);
            }
        }
    }

    public void setScript(String script) {
        this.sqlScript = script;
    }

    public String getSqlScript() {
        return sqlScript;
    }
}
