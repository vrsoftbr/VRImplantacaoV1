package vrimplantacao2_5.dao.cadastro.bancodados;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author guilhermegomes
 */
public class BancoDadosDAO {

    public List getBancoDadosPorSistema(int idSistema) throws Exception {
        List<BancoDadosVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "   sb.id,\n"
                    + "	s.id id_sistema,\n"
                    + "	s.nome sistema,\n"
                    + "	b.id id_banco,\n"
                    + "	b.nome bancodados\n"
                    + "from \n"
                    + "	implantacao2_5.sistemabancodados sb\n"
                    + "join implantacao2_5.sistema s on sb.id_sistema = s.id \n"
                    + "join implantacao2_5.bancodados b on sb.id_bancodados = b.id\n"
                    + "where \n"
                    + "	s.id = " + idSistema + " order by b.nome")) {
                while (rs.next()) {
                    BancoDadosVO bdVO = new BancoDadosVO();

                    bdVO.setId(rs.getInt("id_banco"));
                    bdVO.setNome(rs.getString("bancodados"));

                    result.add(bdVO);
                }
            }
        }

        return result;
    }

    public BancoDadosVO getInformacaoBancoDados(int idSistema, int idBanco) throws Exception {
        BancoDadosVO bdVO = null;

        String sql
                = "select \n"
                + "	s.id id_sistema,\n"
                + "	s.nome sistema,\n"
                + "	b.id id_banco,\n"
                + "	b.nome bancodados,\n"
                + "   sb.nomeschema,\n"
                + "	sb.usuario,\n"
                + "	sb.senha,\n"
                + "   sb.porta\n"
                + "from \n"
                + "	implantacao2_5.sistemabancodados sb\n"
                + "join implantacao2_5.sistema s on sb.id_sistema = s.id \n"
                + "join implantacao2_5.bancodados b on sb.id_bancodados = b.id\n"
                + "where \n"
                + "	s.id = " + idSistema + " and b.id = " + idBanco + "\n"
                + "order by b.nome";

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(sql)) {
                if (rs.next()) {
                    bdVO = new BancoDadosVO();

                    bdVO.setId(rs.getInt("id_banco"));
                    bdVO.setNome(rs.getString("bancodados"));
                    bdVO.setSchema(rs.getString("nomeschema"));
                    bdVO.setSenha(rs.getString("senha"));
                    bdVO.setUsuario(rs.getString("usuario"));
                    bdVO.setPorta(rs.getInt("porta"));
                }
            }
        }

        return bdVO;
    }

    public void salvar(BancoDadosVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setSchema("implantacao2_5");
        sql.setTableName("bancodados");

        sql.put("nome", vo.getNome());
        
        sql.getReturning().add("id");

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                    if (rst.next()) {
                        vo.setId(rst.getInt("id"));
                    }
                }                
            }
        }
    }

    public void alterar(BancoDadosVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();

        sql.setTableName("bancodados");
        sql.setSchema("implantacao2_5");

        sql.put("nome", vo.getNome());

        sql.setWhere("id = " + vo.getId());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }        
    }
    
    public boolean existeBancoDados(String nome) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select nome\n"
                    + "from implantacao2_5.bancodados\n"
                    + "where nome = '" + nome + "'"
            )) {
                return rst.next();
            }
        }
    }

    public List<BancoDadosVO> consultar(String nome) throws Exception {
        List<BancoDadosVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id,\n"
                    + "nome\n"
                    + "from implantacao2_5.bancodados\n"
                    + "where nome like '%"+nome+"%' \n"        
                    + "order by 2"
            )) {
                while (rst.next()) {
                    BancoDadosVO vo = new BancoDadosVO();
                    vo.setId(rst.getInt("id"));
                    vo.setNome(rst.getString("nome"));
                    result.add(vo);
                }
            }
            return result;
        }
    }
}
