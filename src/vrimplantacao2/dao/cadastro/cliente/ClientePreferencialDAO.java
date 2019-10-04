package vrimplantacao2.dao.cadastro.cliente;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.parametro.Versao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;

/**
 * Dao para gravar o cliente preferencial.
 *
 * @author Leandro
 */
public class ClientePreferencialDAO {

    public void salvar(ClientePreferencialVO cliente) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("clientepreferencial");
            sql.put("id", cliente.getId());
            sql.put("nome", cliente.getNome());
            sql.put("id_situacaocadastro", cliente.getSituacaocadastro().getId());
            sql.put("endereco", cliente.getEndereco());
            sql.put("bairro", cliente.getBairro());
            sql.put("id_estado", cliente.getId_estado());
            sql.put("id_municipio", cliente.getId_municipio());
            sql.put("cep", cliente.getCep());
            sql.put("telefone", cliente.getTelefone());
            sql.put("celular", cliente.getCelular());
            sql.put("email", cliente.getEmail());
            sql.put("inscricaoEstadual", cliente.getInscricaoEstadual());
            sql.put("orgaoEmissor", cliente.getOrgaoEmissor());
            sql.put("cnpj", cliente.getCnpj());
            sql.put("id_tipoEstadoCivil", cliente.getTipoEstadoCivil().getID());
            sql.put("dataNascimento", cliente.getDataNascimento());
            sql.put("dataCadastro", cliente.getDataCadastro());
            sql.put("sexo", cliente.getSexo().getID());
            sql.put("observacao", cliente.getObservacao());
            sql.put("empresa", cliente.getEmpresa());
            sql.put("id_estadoEmpresa", cliente.getId_estadoEmpresa());
            sql.put("id_municipioEmpresa", cliente.getId_municipioEmpresa());
            sql.put("enderecoEmpresa", cliente.getEnderecoEmpresa());
            sql.put("bairroEmpresa", cliente.getBairroEmpresa());
            sql.put("cepEmpresa", cliente.getCepEmpresa());
            sql.put("telefoneEmpresa", cliente.getTelefoneEmpresa());
            sql.put("dataAdmissao", cliente.getDataAdmissao());
            sql.put("cargo", cliente.getCargo());
            sql.put("salario", cliente.getSalario());
            sql.put("valorLimite", cliente.getValorLimite());
            sql.put("nomeConjuge", cliente.getNomeConjuge());
            sql.put("id_tipoInscricao", cliente.getTipoInscricao().getId());
            sql.put("vencimentoCreditoRotativo", cliente.getVencimentoCreditoRotativo());
            sql.put("observacao2", cliente.getObservacao2());
            sql.put("permiteCreditoRotativo", cliente.isPermiteCreditoRotativo());
            sql.put("permiteCheque", cliente.isPermiteCheque());
            sql.put("nomeMae", cliente.getNomeMae());
            sql.put("nomePai", cliente.getNomePai());
            sql.put("dataRestricao", cliente.getDataRestricao());
            sql.put("bloqueado", cliente.isBloqueado());
            sql.put("numero", cliente.getNumero());
            sql.put("numeroEmpresa", cliente.getNumeroEmpresa());
            sql.put("complemento", cliente.getComplemento());
            sql.put("complementoEmpresa", cliente.getComplementoEmpresa());

            sql.put("dataresidencia", "1900-01-01");
            sql.put("id_tiporesidencia", 5);
            sql.put("senha", cliente.getSenha());
            sql.put("id_tiporestricaocliente", 0);
            sql.put("id_regiaocliente", 1);
            sql.put("agencia", "");
            sql.put("conta", "");
            sql.put("praca", "");
            sql.put("outrarenda", 0);
            sql.put("cpfconjuge", 0);
            sql.put("rgconjuge", "");
            sql.put("orgaoemissorconjuge", "");
            sql.put("empresaconjuge", "");
            sql.put("enderecoempresaconjuge", "SEM ENDERECO");
            sql.put("numeroempresaconjuge", "0");
            sql.put("complementoempresaconjuge", "");
            sql.put("bairroempresaconjuge", "SEM BAIRRO");
            sql.put("cepempresaconjuge", 0);
            sql.put("telefoneempresaconjuge", "(00)0000-0000");
            sql.put("cargoconjuge", "");
            sql.put("salarioconjuge", 0);
            sql.put("outrarendaconjuge", 0);
            if (Versao.maiorQue(3,17)) {
                sql.put("utilizaappdescontos", false);
            }
            sql.putNull("id_grupo");

            try {
                stm.execute(sql.getInsert());
            } catch (Exception e) {
                System.out.println(sql.getInsert());
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Returna os CNPJs cadastrados nos clientes eventuais;
     *
     * @return
     * @throws Exception
     */
    public Map<Long, Integer> getCnpjCadastrados() throws Exception {
        Map<Long, Integer> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, cnpj from clientepreferencial;"
            )) {
                while (rst.next()) {
                    result.put(rst.getLong("cnpj"), rst.getInt("id"));
                }
            }
        }
        return result;
    }

    public void apagarContatos() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from clientepreferencialcontato;");
            stm.execute("alter sequence clientepreferencialcontato_id_seq restart with 1;");
        }
    }

    public void apagarTudo() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from clientepreferencialcontato;");
            stm.execute("delete from clientepreferencial;");
            stm.execute("drop table if exists implantacao.codant_clientepreferencial;");
            stm.execute("alter sequence clientepreferencialcontato_id_seq restart with 1;");
            stm.execute("alter sequence clientepreferencial_id_seq restart with 1;");
        }
    }

    public int getId(long i_cnpj) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id "
                    + "from clientepreferencial "
                    + "where cnpj = " + i_cnpj
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                } else {
                    return 0;
                }
            }
        }
    }

    public int getIdByCodAnt(String i_codigo, String i_loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select c.id "
                    + "from clientepreferencial c "
                    + "inner join implantacao.codant_clientepreferencial ant "
                    + "on ant.codigoatual = c.id "
                    + "where ant.id = '" + i_codigo + "' "
                    + "and ant.loja = '" + i_loja + "'"
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                } else {
                    return 0;
                }
            }
        }
    }

    public long getCnpjByCodAnt(String i_codigo, String i_loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select c.cnpj "
                    + "from clientepreferencial c "
                    + "inner join implantacao.codant_clientepreferencial ant "
                    + "on ant.codigoatual = c.id "
                    + "where ant.id = '" + i_codigo + "' "
                    + "and ant.loja = '" + i_loja + "'"
            )) {
                if (rst.next()) {
                    return rst.getLong("cnpj");
                } else {
                    return 0;
                }
            }
        }
    }

    public String getNomeByCodAnt(String i_codigo, String i_loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select c.nome "
                    + "from clientepreferencial c "
                    + "inner join implantacao.codant_clientepreferencial ant "
                    + "on ant.codigoatual = c.id "
                    + "where ant.id = '" + i_codigo + "' "
                    + "and ant.loja = '" + i_loja + "'"
            )) {
                if (rst.next()) {
                    return rst.getString("nome");
                } else {
                    return "SEM NOME";
                }
            }
        }
    }

    public String getTelefoneByCodAnt(String i_codigo, String i_loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select c.telefone "
                    + "from clientepreferencial c "
                    + "inner join implantacao.codant_clientepreferencial ant "
                    + "on ant.codigoatual = c.id "
                    + "where ant.id = '" + i_codigo + "' "
                    + "and ant.loja = '" + i_loja + "'"
            )) {
                if (rst.next()) {
                    return rst.getString("telefone");
                } else {
                    return "SEM TELEFONE";
                }
            }
        }
    }

    public List<ClientePreferencialVO> getClienteChequeByCodAnt(String i_codigo, String i_sistema, String i_loja) throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "c.nome,\n"
                    + "c.cnpj,\n"
                    + "c.inscricaoestadual,"
                    + "c.telefone\n"
                    + "from clientepreferencial c\n"
                    + "inner join implantacao.codant_clientepreferencial ant\n"
                    + "on ant.codigoatual = c.id\n"
                    + "where ant.id = '" + i_codigo + "'\n"
                    + "and ant.sistema = '" + i_sistema + "'\n"
                    + "and ant.loja = '" + i_loja + "'"
            )) {
                if (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    vo.setNome(rst.getString("nome"));
                    vo.setCnpj(rst.getLong("cnpj"));
                    vo.setInscricaoEstadual(rst.getString("inscricaoestadual"));
                    vo.setTelefone(rst.getString("telefone"));
                    result.add(vo);
                }
            }
        }
        return result;
    }

    public void atualizarClientePreferencial(ClientePreferencialVO vo, Set<OpcaoCliente> opt) throws Exception {
        if (!opt.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("clientepreferencial");
                if (opt.contains(OpcaoCliente.OBSERVACOES2)) {
                    sql.put("observacao2", vo.getObservacao2());                    
                }
                if (opt.contains(OpcaoCliente.OBSERVACOES)) {
                    sql.put("observacao", vo.getObservacao());
                }
                if (opt.contains(OpcaoCliente.SITUACAO_CADASTRO)) {
                    sql.put("permitecreditorotativo", vo.isPermiteCreditoRotativo());
                    sql.put("permitecheque", vo.isPermiteCheque());
                    sql.put("bloqueado", vo.isBloqueado());
                    sql.put("id_situacaocadastro", vo.getSituacaocadastro().getId());
                }
                if (opt.contains(OpcaoCliente.VALOR_LIMITE)) {
                    sql.put("valorlimite", vo.getValorLimite());
                }
                if (opt.contains(OpcaoCliente.INSCRICAO_ESTADUAL)) {
                    sql.put("inscricaoestadual", vo.getInscricaoEstadual());
                }
                if (opt.contains(OpcaoCliente.DATA_NASCIMENTO)) {
                    sql.put("datanascimento", vo.getDataNascimento());
                }
                if (opt.contains(OpcaoCliente.TELEFONE)) {
                    sql.put("telefone", vo.getTelefone());
                }
                if (opt.contains(OpcaoCliente.CELULAR)) {
                    sql.put("celular", vo.getCelular());
                }
                if (opt.contains(OpcaoCliente.TIPO_INSCRICAO)) {
                    sql.put("id_tipoinscricao", vo.getTipoInscricao());
                }
                if (opt.contains(OpcaoCliente.ENDERECO_COMPLETO)) {
                    sql.put("endereco", vo.getEndereco());
                    sql.put("numero", vo.getNumero());
                    sql.put("bairro", vo.getBairro());
                    sql.put("id_municipio", vo.getId_municipio());
                    sql.put("id_estado", vo.getId_estado());
                }
                if (opt.contains(OpcaoCliente.PERMITE_CHEQUE)) {
                    sql.put("permitecheque", vo.isPermiteCheque());
                }
                if (opt.contains(OpcaoCliente.PERMITE_CREDITOROTATIVO)) {
                    sql.put("permitecreditorotativo", vo.isPermiteCreditoRotativo());
                }
                if (opt.contains(OpcaoCliente.RAZAO)) {
                    sql.put("nome", vo.getNome());
                }
                if (opt.contains(OpcaoCliente.CNPJ)) {
                    sql.put("cnpj", vo.getCnpj());
		}
                if (opt.contains(OpcaoCliente.EMAIL)) {
                    sql.put("email", vo.getEmail());
                }
                if (opt.contains(OpcaoCliente.BLOQUEADO)) {
                    sql.put("bloqueado", vo.isBloqueado());
                }
                if (opt.contains(OpcaoCliente.CEP)) {
                    sql.put("cep", vo.getCep());
                }
                if(opt.contains(OpcaoCliente.COMPLEMENTO)) {
                    sql.put("complemento", vo.getComplemento());
                }
                if(opt.contains(OpcaoCliente.ESTADO_CIVIL)) {
                    sql.put("id_tipoestadocivil", vo.getTipoEstadoCivil().getID());
                }
                if (opt.contains(OpcaoCliente.SEXO)) {
                    sql.put("sexo", vo.getSexo().getID());
                }

                sql.setWhere("id = " + vo.getId());
                stm.execute(sql.getUpdate());
            }
        }
    }

    public Map<Long, ClientePreferencialVO> getClientesPorCnpj() throws Exception {
        Map<Long, ClientePreferencialVO> result = new LinkedHashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.id,\n"
                    + "	c.nome,\n"
                    + "	c.cnpj,\n"
                    + "	c.endereco,\n"
                    + "	c.numero,\n"
                    + "	c.complemento,\n"
                    + "	c.bairro,\n"
                    + "	c.id_municipio,\n"
                    + "	c.id_estado,\n"
                    + "	c.cep\n"
                    + "from\n"
                    + "	clientepreferencial c\n"
                    + "order by\n"
                    + "	c.id"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();

                    vo.setId(rst.getInt("id"));
                    vo.setNome(rst.getString("nome"));
                    vo.setCnpj(rst.getLong("cnpj"));
                    vo.setEndereco(rst.getString("endereco"));
                    vo.setNumero(rst.getString("numero"));
                    vo.setComplemento(rst.getString("complemento"));
                    vo.setBairro(rst.getString("bairro"));
                    vo.setId_municipio(rst.getInt("id_municipio"));
                    vo.setId_estado(rst.getInt("id_estado"));
                    vo.setCep(rst.getInt("cep"));

                    result.put(vo.getCnpj(), vo);
                }
            }
        }

        return result;
    }
}
