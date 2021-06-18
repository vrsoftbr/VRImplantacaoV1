package vrimplantacao2_5.dao.configuracao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoVO;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.enums.ESituacaoMigracao;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoSistemaDAO {

    public void inserir(ConfiguracaoBancoVO conexaoVO) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setTableName("conexao");
        sql.setSchema("implantacao2_5");

        sql.put("host", conexaoVO.getHost());
        sql.put("porta", conexaoVO.getPorta());
        sql.put("usuario", conexaoVO.getUsuario());
        sql.put("senha", conexaoVO.getSenha());
        sql.put("descricao", conexaoVO.getDescricao());
        sql.put("nomeschema", conexaoVO.getSchema());
        sql.put("id_sistema", conexaoVO.getIdSistema());
        sql.put("id_bancodados", conexaoVO.getIdBancoDados());

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

    public void alterar(ConfiguracaoBancoVO conexaoVO) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setTableName("conexao");
        sql.setSchema("implantacao2_5");

        sql.put("host", conexaoVO.getHost());
        sql.put("porta", conexaoVO.getPorta());
        sql.put("usuario", conexaoVO.getUsuario());
        sql.put("senha", conexaoVO.getSenha());
        sql.put("descricao", conexaoVO.getDescricao());
        sql.put("nomeschema", conexaoVO.getSchema());
        sql.put("id_sistema", conexaoVO.getIdSistema());
        sql.put("id_bancodados", conexaoVO.getIdBancoDados());

        sql.setWhere("id = " + conexaoVO.getId());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public void inserirLoja(ConfiguracaoBancoVO conexaoVO) throws Exception {
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
    
    public boolean verificaLojaMatriz(ConfiguracaoBancoVO configuracaoBancoVO) throws Exception {
        boolean retorno = true;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select "
                     + "id "
                 + "from "
                    + "implantacao2_5.conexaoloja "
                 + "where id_conexao = " + configuracaoBancoVO.getId())) {
                if (rs.next()) {
                    retorno = false;
                }
            }
        }
        
        return retorno;
    }
    
    public boolean existeLojaMapeada(String tipoLoja, ConfiguracaoBancoVO configuracaoBancoVO) throws Exception {
        boolean retorno = false;
        String sql = "", filtro = "";
        
        if(tipoLoja.equals("LOJAORIGEM")) {
            filtro = " and id_lojaorigem = '" + 
                    configuracaoBancoVO.getConfiguracaoBancoLoja().getIdLojaOrigem() + "'";
        } else {
            filtro = " and id_lojadestino = " + 
                    configuracaoBancoVO.getConfiguracaoBancoLoja().getIdLojaVR();
        }
        
        sql = "select "
                 + "id "
            + "from "
                + "implantacao2_5.conexaoloja "
            + "where id_conexao = " + configuracaoBancoVO.getId() + filtro;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(sql)) {
                if (rs.next()) {
                    retorno = true;
                }
            }
        }
        
        return retorno;
    }
    
    public List getLojaMapeada(int idConexao) throws Exception {
        List<ConfiguracaoBancoLojaVO> result = new ArrayList<>();
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	c.id,\n" +
                    "	id_conexao,\n" +
                    "	id_lojaorigem,\n" +
                    "	id_lojadestino,\n" +
                    "	l.descricao destino,\n" +
                    "	id_situacaomigracao,\n" +
                    "	datacadastro,\n" +
                    "	lojamatriz\n" +
                    "from\n" +
                    "	implantacao2_5.conexaoloja c\n" +
                    "join public.loja l on c.id_lojadestino = l.id\n" +
                    "where\n" +
                    "	id_conexao = " + idConexao)) {
                while(rs.next()) {
                    ConfiguracaoBancoLojaVO mapaLojaVO = new ConfiguracaoBancoLojaVO();
                    
                    mapaLojaVO.setId(rs.getInt("id"));
                    mapaLojaVO.setIdLojaOrigem(rs.getString("id_lojaorigem"));
                    mapaLojaVO.setIdLojaVR(rs.getInt("id_lojadestino"));
                    mapaLojaVO.setDescricaoVR(rs.getString("destino"));
                    mapaLojaVO.setLojaMatriz(rs.getBoolean("lojamatriz"));
                    mapaLojaVO.setSituacaoMigracao(ESituacaoMigracao.
                                                   getById(rs.getInt("id_situacaomigracao")));
                    mapaLojaVO.setDataCadastro(rs.getDate("datacadastro"));
                    
                    result.add(mapaLojaVO);
                }
            }
        }
        
        return result;
    }
}
