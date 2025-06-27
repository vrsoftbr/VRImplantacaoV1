package vrimplantacao2_5.dao.configuracao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;
import vrimplantacao2_5.vo.enums.ESituacaoMigracao;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosDAO {

    public void inserir(ConfiguracaoBaseDadosVO conexaoVO) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        String schema = conexaoVO.getSchema().replace("\\", "/");
        
        sql.setTableName("conexao");
        sql.setSchema("implantacao2_5");

        sql.put("host", conexaoVO.getHost());
        sql.put("porta", conexaoVO.getPorta());
        sql.put("usuario", conexaoVO.getUsuario());
        sql.put("senha", conexaoVO.getSenha());
        sql.put("descricao", conexaoVO.getDescricao());
        sql.put("nomeschema", schema);
        sql.put("id_sistema", conexaoVO.getSistema().getId());
        sql.put("id_bancodados", conexaoVO.getBancoDados().getId());
        sql.put("complemento", conexaoVO.getComplemento());

        sql.getReturning().add("id");

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                    if (rst.next()) {
                        conexaoVO.setId(rst.getInt("id"));
                    }
                }
            }
        }
    }

    public void alterar(ConfiguracaoBaseDadosVO conexaoVO) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setTableName("conexao");
        sql.setSchema("implantacao2_5");
        
        String schema = conexaoVO.getSchema().replace("\\", "/");
        
        sql.put("host", conexaoVO.getHost());
        sql.put("porta", conexaoVO.getPorta());
        sql.put("usuario", conexaoVO.getUsuario());
        sql.put("senha", conexaoVO.getSenha());
        sql.put("descricao", conexaoVO.getDescricao());
        sql.put("nomeschema", schema);
        sql.put("id_sistema", conexaoVO.getSistema().getId());
        sql.put("id_bancodados", conexaoVO.getBancoDados().getId());
        sql.put("complemento", conexaoVO.getComplemento());

        sql.setWhere("id = " + conexaoVO.getId());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public void inserirLoja(ConfiguracaoBaseDadosVO conexaoVO) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setSchema("implantacao2_5");
        sql.setTableName("conexaoloja");

        sql.put("id_conexao", conexaoVO.getId());
        sql.put("id_lojaorigem", conexaoVO.getConfiguracaoBancoLoja().getIdLojaOrigem());
        sql.put("id_lojadestino", conexaoVO.getConfiguracaoBancoLoja().getIdLojaVR());
        sql.put("datacadastro", conexaoVO.getConfiguracaoBancoLoja().getDataCadastro());
        sql.put("id_situacaomigracao", conexaoVO.getConfiguracaoBancoLoja().getSituacaoMigracao().getId());
        sql.put("lojamatriz", conexaoVO.getConfiguracaoBancoLoja().isLojaMatriz());

        sql.getReturning().add("id");

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                    if (rst.next()) {
                        conexaoVO.getConfiguracaoBancoLoja().setId(rst.getInt("id"));
                    }
                }
            }
        }
    }

    public void inserirLojaOrigem(ConfiguracaoBaseDadosVO conexaoVO) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setSchema("implantacao2_5");
        sql.setTableName("lojaorigem");

        sql.put("id", conexaoVO.getConfiguracaoBancoLoja().getIdLojaOrigem());
        sql.put("descricao", conexaoVO.getConfiguracaoBancoLoja().getDescricaoLojaOrigem());
        sql.put("mixprincipal", conexaoVO.getConfiguracaoBancoLoja().isLojaMatriz());
        sql.put("id_conexaoloja", conexaoVO.getConfiguracaoBancoLoja().getId());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getInsert());
            }
        }
    }
    
    public String verificaLojaMatriz(ConfiguracaoBaseDadosVO configuracaoBancoVO) throws Exception {
        String retorno = "";

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select "
                    + "id_lojaorigem "
                    + "from "
                    + "implantacao2_5.conexaoloja "
                    + "where id_conexao = " + configuracaoBancoVO.getId()
                    + " and lojamatriz is true")) {
                if (rs.next()) {
                    retorno = rs.getString("id_lojaorigem");
                }
            }
        }

        return retorno;
    }
    
    public boolean verificaLojaMatrizMigracao(int idConexao, String idLojaOrigem) throws Exception {
        boolean retorno = false;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select "
                    + "lojamatriz "
                    + "from "
                    + "implantacao2_5.conexaoloja "
                    + "where id_conexao = " + idConexao
                    + " and id_lojaorigem = '" + idLojaOrigem + "'")) {
                if (rs.next()) {
                    retorno = rs.getBoolean("lojamatriz");
                }
            }
        }

        return retorno;
    }
    
    public boolean existeLojaMapeada(String tipoLoja,
            ConfiguracaoBaseDadosVO configuracaoBancoVO) throws Exception {
        boolean retorno = false;
        String sql = "", filtro = "";

        if (tipoLoja.equals("LOJA_ORIGEM")) {
            filtro = " and id_lojaorigem = '"
                    + configuracaoBancoVO.getConfiguracaoBancoLoja().getIdLojaOrigem() + "'";
        } else {
            filtro = " and id_lojadestino = "
                    + configuracaoBancoVO.getConfiguracaoBancoLoja().getIdLojaVR();
        }

        sql = "select "
                + "id "
                + "from "
                + "implantacao2_5.conexaoloja "
                + "where id_conexao = " + configuracaoBancoVO.getId() + filtro;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(sql)) {
                if (rs.next()) {
                    retorno = true;
                }
            }
        }

        return retorno;
    }

    public List getLojaMapeada(int idConexao) throws Exception {
        List<ConfiguracaoBancoLojaVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	c.id,\n"
                    + "	id_conexao,\n"
                    + "	id_lojaorigem,\n"
                    //+ " substring(ljo.descricao, 5) as descricaolojadestino,"
                    + " ljo.descricao as descricaolojadestino,"                            
                    + "	id_lojadestino,\n"
                    + "	l.descricao destino,\n"
                    + "	id_situacaomigracao,\n"
                    + "	datacadastro,\n"
                    + "	lojamatriz\n"
                    + "from\n"
                    + "	implantacao2_5.conexaoloja c\n"
                    + "join public.loja l on c.id_lojadestino = l.id\n"
                    + "join implantacao2_5.lojaorigem ljo on ljo.id = c.id_lojaorigem and\n"        
                    + "ljo.id_conexaoloja = c.id\n"        
                    + "where\n"
                    + "	id_conexao = " + idConexao)) {
                while (rs.next()) {
                    ConfiguracaoBancoLojaVO mapaLojaVO = new ConfiguracaoBancoLojaVO();

                    mapaLojaVO.setId(rs.getInt("id"));
                    mapaLojaVO.setIdLojaOrigem(rs.getString("id_lojaorigem"));
                    mapaLojaVO.setDescricaoLojaOrigem(rs.getString("descricaolojadestino"));
                    mapaLojaVO.setIdLojaVR(rs.getInt("id_lojadestino"));
                    mapaLojaVO.setDescricaoVR(rs.getString("destino"));
                    mapaLojaVO.setLojaMatriz(rs.getBoolean("lojamatriz"));
                    mapaLojaVO.setSituacaoMigracao(ESituacaoMigracao.
                            getById(rs.getInt("id_situacaomigracao")));
                    mapaLojaVO.setDataCadastro(rs.getDate("datacadastro"));
                    mapaLojaVO.setIdConexao(rs.getInt("id_conexao"));

                    result.add(mapaLojaVO);
                }
            }
        }

        return result;
    }

    public void excluirLojaMapeada(ConfiguracaoBancoLojaVO configuracaoBancoLojaVO) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from implantacao2_5.lojaorigem where id_conexaoloja = "
                    + configuracaoBancoLojaVO.getId() + "; \n"
                    + "delete from implantacao2_5.conexaoloja where id = "
                    + configuracaoBancoLojaVO.getId());
        }
    }
    
    public List consultar() throws Exception {
        return consultar(0);
    } 

    public List consultar(int idSistema) throws Exception {
        List<ConfiguracaoBaseDadosVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	c.id,\n"
                    + "	c.descricao,\n"
                    + "	s.id id_sistema,\n"
                    + "	s.nome sistema,\n"
                    + "	b.id id_bancodados,\n"
                    + "	b.nome bancodados,\n"
                    + "	c.host,\n"
                    + "	c.porta,\n"
                    + "	c.usuario,\n"
                    + "	c.nomeschema,\n"
                    + " c.senha,\n"
                    + " c.complemento\n"        
                    + "from \n"
                    + "	implantacao2_5.conexao c\n"
                    + "join implantacao2_5.sistema s\n"
                    + "		on c.id_sistema = s.id\n"
                    + "join implantacao2_5.bancodados b\n"
                    + "		on c.id_bancodados = b.id\n"
                    + (idSistema != 0 ? " where s.id = " + idSistema : "")        
                    + " order by c.descricao")) {
                while (rs.next()) {
                    ConfiguracaoBaseDadosVO configuracaoVO = new ConfiguracaoBaseDadosVO();
                    SistemaVO sistemaVO = new SistemaVO();
                    BancoDadosVO bancoDadosVO = new BancoDadosVO();

                    configuracaoVO.setId(rs.getInt("id"));
                    configuracaoVO.setDescricao(rs.getString("descricao"));
                    sistemaVO.setId(rs.getInt("id_sistema"));
                    sistemaVO.setNome(rs.getString("sistema"));
                    configuracaoVO.setSistema(sistemaVO);
                    bancoDadosVO.setId(rs.getInt("id_bancodados"));
                    bancoDadosVO.setNome(rs.getString("bancodados"));
                    configuracaoVO.setBancoDados(bancoDadosVO);
                    configuracaoVO.setHost(rs.getString("host"));
                    configuracaoVO.setPorta(rs.getInt("porta"));
                    configuracaoVO.setUsuario(rs.getString("usuario"));
                    configuracaoVO.setSchema(rs.getString("nomeschema"));
                    configuracaoVO.setSenha(rs.getString("senha"));
                    configuracaoVO.setComplemento(rs.getString("complemento"));
                        
                    result.add(configuracaoVO);
                }
            }
        }

        return result;
    }
    
    public ConfiguracaoBaseDadosVO getConexao(int idConexao) throws Exception {
        ConfiguracaoBaseDadosVO configuracaoVO = null;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	c.id,\n"
                    + "	c.descricao,\n"
                    + "	s.id id_sistema,\n"
                    + "	s.nome sistema,\n"
                    + "	b.id id_bancodados,\n"
                    + "	b.nome bancodados,\n"
                    + "	c.host,\n"
                    + "	c.porta,\n"
                    + "	c.usuario,\n"
                    + "	c.nomeschema,\n"
                    + " c.senha,\n"
                    + " c.complemento\n"        
                    + "from \n"
                    + "	implantacao2_5.conexao c\n"
                    + "join implantacao2_5.sistema s\n"
                    + "		on c.id_sistema = s.id\n"
                    + "join implantacao2_5.bancodados b\n"
                    + "		on c.id_bancodados = b.id\n"
                    + " where c.id = " + idConexao)) {
                while (rs.next()) {
                    configuracaoVO = new ConfiguracaoBaseDadosVO();
                    SistemaVO sistemaVO = new SistemaVO();
                    BancoDadosVO bancoDadosVO = new BancoDadosVO();

                    configuracaoVO.setId(rs.getInt("id"));
                    configuracaoVO.setDescricao(rs.getString("descricao"));
                    sistemaVO.setId(rs.getInt("id_sistema"));
                    sistemaVO.setNome(rs.getString("sistema"));
                    configuracaoVO.setSistema(sistemaVO);
                    bancoDadosVO.setId(rs.getInt("id_bancodados"));
                    bancoDadosVO.setNome(rs.getString("bancodados"));
                    configuracaoVO.setBancoDados(bancoDadosVO);
                    configuracaoVO.setHost(rs.getString("host"));
                    configuracaoVO.setPorta(rs.getInt("porta"));
                    configuracaoVO.setUsuario(rs.getString("usuario"));
                    configuracaoVO.setSchema(rs.getString("nomeschema"));
                    configuracaoVO.setSenha(rs.getString("senha"));
                    configuracaoVO.setComplemento(rs.getString("complemento"));
                }
            }
        }

        return configuracaoVO;
    }
    
    public boolean existeConexao(ConfiguracaoBaseDadosVO configuracaoVO) throws Exception {
        boolean retorno = false;

//        try (Statement stm = Conexao.createStatement()) {
//            try (ResultSet rs = stm.executeQuery(
//                      "select \n"
//                    + "	id\n"
//                    + "from \n"
//                    + "	implantacao2_5.conexao\n"
//                    + "where \n"
//                    + "	id_sistema = " + configuracaoVO.getSistema().getId() + " and \n"
//                    + "	id_bancodados = " + configuracaoVO.getBancoDados().getId() + " and \n"
//                    + "	nomeschema = '" + configuracaoVO.getSchema() + "' and\n"
//                    + " host = '" + configuracaoVO.getHost() + "'")) {
        String sql = "SELECT id " +
                     "FROM implantacao2_5.conexao " +
                     "WHERE id_sistema = ? " +
                     "AND id_bancodados = ? " +
                     "AND nomeschema = ? " +
                     "AND host = ?";

        try (PreparedStatement ps = Conexao.prepareStatement(sql)) {
            ps.setInt(1, configuracaoVO.getSistema().getId());
            ps.setInt(2, configuracaoVO.getBancoDados().getId());
            ps.setString(3, configuracaoVO.getSchema());
            ps.setString(4, configuracaoVO.getHost());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    retorno = true;
                }
            }
        }

        return retorno;
    }
    
    public void alterarSituacaoMigracao(String idLojaOrigem, int idLojaVR, int situacaoMigracao) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        
        sql.setSchema("implantacao2_5");
        sql.setTableName("conexaoloja");
        
        sql.put("id_situacaomigracao", situacaoMigracao);
        
        sql.setWhere("id_lojaorigem = '" + idLojaOrigem+ "' and id_lojadestino = " + idLojaVR);
        
        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }
    }
}
