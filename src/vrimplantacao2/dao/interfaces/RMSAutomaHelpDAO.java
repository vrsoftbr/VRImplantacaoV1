package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author Importacao
 */
public class RMSAutomaHelpDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "RMS AutomaHelp";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> lojas = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "loja::integer as id, \n"
                    + "loja::integer ||' - '|| cnpj ||' - '|| nome_fantasia as descricao\n"
                    + "from "
                    + "parametros"
            )) {
                while (rst.next()) {
                    lojas.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return lojas;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	c.codigo as id,\n"
                    + "	(case when c.inscricao_estadual = '' then null else c.inscricao_estadual end) as ie,\n"
                    + "	c.rg,\n"
                    + "	c.nome as razao,\n"
                    + "	case \n"
                    + "	  when inativo = 'N' then 1 else 0 end as ativo,\n"
                    + "	c.endereco,\n"
                    + "	c.numero,\n"
                    + "	c.complemento,\n"
                    + "	c.bairro,\n"
                    + "	c.codigo_municipio::integer as municipioIBGE,\n"
                    + "	c.cidade as municipio,\n"
                    + "	c.estado as uf,\n"
                    + "	c.cep,\n"
                    + "	(case \n"
                    + "	  when estado_civil = 'Solteiro(a)' then 1\n"
                    + "	  when estado_civil = 'Casado(a)' then 2\n"
                    + "	  when estado_civil = 'Viúvo(a)' then 3\n"
                    + "	  when estado_civil = 'Desquitado' then 6\n"
                    + "	  when estado_civil = 'Divorciado' then 6\n"
                    + "	  when estado_civil = 'Outros...' then 5\n"
                    + "	end) as estadocivil,\n"
                    + "	c.datanas::date as datanascimento,\n"
                    + "	c.datacadastro::date as datacadastro,\n"
                    + "	case sexo when 'Masculino' then 1 else 0 end as sexo,\n"
                    + "	c.nome_empresa as empresa,\n"
                    + "	c.endereco_empresa as empresaendereco,\n"
                    + "	c.numero_empresa as empresanumero,\n"
                    + "	c.bairro_empresa as empresabairro,\n"
                    + "	c.cidade_empresa as empresamunicipio,\n"
                    + "	c.estado_empresa as empresaestado,\n"
                    + "	c.cep_empresa as empresacep,\n"
                    + "	c.telefone_empresa as empresatelefone,\n"
                    + "	c.cargo_empresa as cargo,\n"
                    + "	c.telefone,\n"
                    + "	c.celular,\n"
                    + "	c.email\n"
                    + "from 	clientes c\n"
                    + "where 	c.codigo >= 1\n"
                    + "order by 1")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    if ((rs.getString("rg") != null)
                            && (!rs.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else if ((rs.getString("ie") != null)
                            && (!rs.getString("ie").trim().isEmpty())) {
                        imp.setInscricaoestadual(rs.getString("ie"));
                    } else {
                        imp.setInscricaoestadual("isento");
                    }
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipioIBGE(rs.getInt("municipioIBGE"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    if ((rs.getString("estadocivil") != null)
                            && (!rs.getString("estadocivil").trim().isEmpty())) {
                        if (null != rs.getString("estadocivil")) {
                            switch (rs.getString("estadocivil")) {
                                case "1":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "2":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                case "3":
                                    imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                    break;
                                case "4":
                                    imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                    break;
                                case "5":
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                            }
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                    }
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setSexo("1".equals(rs.getString("SEXO")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaEndereco(rs.getString("empresaendereco"));
                    imp.setEmpresaNumero(rs.getString("empresanumero"));
                    imp.setEmpresaBairro(rs.getString("empresabairro"));
                    imp.setEmpresaMunicipio(rs.getString("empresamunicipio"));
                    imp.setEmpresaUf(rs.getString("empresaestado"));
                    imp.setEmpresaCep(rs.getString("empresacep"));
                    imp.setEmpresaTelefone(rs.getString("empresatelefone"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws SQLException {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigo as id,\n"
                    + "	trim(razao_social) as razao,\n"
                    + "	trim(nome_fantasia) as fantasia,\n"
                    + "	telefone as telefoneprincipal,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	codigo_municipio as ibge_municipio,\n"
                    + "	estado,\n"
                    + "	cep,\n"
                    + "	complemento,\n"
                    + "	observacao,\n"
                    + "	trim(cpf) as cnpj_cpf,\n"
                    + "	trim(rg) as ie_rg,\n"
                    + "	fax,\n"
                    + "	email_nfe,\n"
                    + "	celular,\n"
                    + "	data_alteracao as datacadastro,\n"
                    + "	case\n"
                    + "	when status = 'A' then 1 else 0 end as ativo\n"
                    + "from fornecedores\n"
                    + "order by codigo")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setTel_principal(rs.getString("telefoneprincipal"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    if ((rs.getString("fax") != null)
                            && (!rs.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "fax",
                                rs.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    String email = Utils.acertarTexto(rs.getString("email_nfe")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("2", "email nfe", "", "", TipoContato.COMERCIAL, email);
                    }
                    if ((rs.getString("celular") != null)
                            && (!rs.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "Celular",
                                null,
                                rs.getString("celular"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
