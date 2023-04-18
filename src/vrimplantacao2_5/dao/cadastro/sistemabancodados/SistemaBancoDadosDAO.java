package vrimplantacao2_5.dao.cadastro.sistemabancodados;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.vo.cadastro.ScriptLojaOrigemVO;
import vrimplantacao2_5.vo.cadastro.SistemaBancoDadosVO;

/**
 *
 * @author Desenvolvimento
 */
public class SistemaBancoDadosDAO {

    private String filtro = "\n";

    public String getFiltro() {
        return this.filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public void inserir(SistemaBancoDadosVO vo) throws Exception {

        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("implantacao2_5");
        sql.setTableName("sistemabancodados");

        sql.put("id_sistema", vo.getIdSistema());
        sql.put("id_bancodados", vo.getIdBancoDados());
        sql.put("nomeschema", vo.getNomeSchema());
        sql.put("usuario", vo.getUsuario());
        sql.put("senha", vo.getSenha());
        sql.put("porta", vo.getPorta());

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

    public void alterar(SistemaBancoDadosVO vo) throws Exception {

        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("implantacao2_5");
        sql.setTableName("sistemabancodados");

        sql.put("id_sistema", vo.getIdSistema());
        sql.put("id_bancodados", vo.getIdBancoDados());
        sql.put("nomeschema", vo.getNomeSchema());
        sql.put("usuario", vo.getUsuario());
        sql.put("senha", vo.getSenha());
        sql.put("porta", vo.getPorta());

        sql.setWhere("id = " + vo.getId());

        if (!sql.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public List<SistemaBancoDadosVO> consultar(SistemaBancoDadosVO vo) throws Exception {
        List<SistemaBancoDadosVO> result = new ArrayList<>();

        if (vo != null) {
            if (vo.getNomeSistema() != null && !vo.getNomeSistema().trim().isEmpty() && vo.getIdBancoDados() > 0) {
                setFiltro("where s.nome like '%" + vo.getNomeSistema() + "%' and b.id = " + vo.getIdBancoDados() + "\n");
            } else if (vo.getNomeSistema() != null && !vo.getNomeSistema().trim().isEmpty() && vo.getIdBancoDados() <= 0) {
                setFiltro("where s.nome like '%" + vo.getNomeSistema() + "%' \n");
            } else if ((vo.getNomeSistema() == null || vo.getNomeSistema().trim().isEmpty()) && vo.getIdBancoDados() > 0) {
                setFiltro("and b.id = " + vo.getIdBancoDados() + "\n");
            } else {
                setFiltro("\n");
            }
        }

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + " sb.id, \n"
                    + "	b.id as id_bancodados,\n"
                    + "	b.nome as nome_bancodados,\n"
                    + "	s.id as id_sistema,\n"
                    + "	s.nome as nome_sistema,\n"
                    + "	sb.nomeschema,\n"
                    + "	sb.usuario,\n"
                    + "	sb.senha,\n"
                    + "	sb.porta\n"
                    + "from implantacao2_5.sistemabancodados sb\n"
                    + "join implantacao2_5.sistema s on s.id = sb.id_sistema\n"
                    + "join implantacao2_5.bancodados b on b.id = sb.id_bancodados\n"
                    + getFiltro()
                    + "order by b.nome, s.nome"
            )) {
                while (rst.next()) {
                    SistemaBancoDadosVO sistemaBancoDadosVO = new SistemaBancoDadosVO();
                    sistemaBancoDadosVO.setId(rst.getInt("id"));
                    sistemaBancoDadosVO.setIdSistema(rst.getInt("id_sistema"));
                    sistemaBancoDadosVO.setNomeSistema(rst.getString("nome_sistema"));
                    sistemaBancoDadosVO.setIdBancoDados(rst.getInt("id_bancodados"));
                    sistemaBancoDadosVO.setNomeBancoDados(rst.getString("nome_bancodados"));
                    sistemaBancoDadosVO.setNomeSchema(rst.getString("nomeschema"));
                    sistemaBancoDadosVO.setUsuario(rst.getString("usuario"));
                    sistemaBancoDadosVO.setSenha(rst.getString("senha"));
                    sistemaBancoDadosVO.setPorta(rst.getInt("porta"));
                    result.add(sistemaBancoDadosVO);
                }
            }
        }
        return result;
    }

    public List<ScriptLojaOrigemVO> getScriptsLojaOrigem() throws Exception {
        List<ScriptLojaOrigemVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	bd.id,\n"
                    + "	bd.nome banco,\n"
                    + "	sistema.nome sistema,\n"
                    + "	script.script_getlojas script\n"
                    + "from\n"
                    + "	implantacao2_5.sistemabancodadosscripts script\n"
                    + "left join implantacao2_5.sistema sistema on\n"
                    + "	script.id_sistema = sistema.id\n"
                    + "left join implantacao2_5.bancodados bd on\n"
                    + "	script.id_bancodados = bd.id"
            )) {
                while (rst.next()) {
                    ScriptLojaOrigemVO script = new ScriptLojaOrigemVO();
                    script.setIdBanco(rst.getInt("id"));
                    script.setBanco(rst.getString("banco"));
                    script.setSistema(rst.getString("sistema"));
                    script.setScript(rst.getString("script"));
                    result.add(script);
                }
            }
        }
        return result;
    }

    public List<ScriptLojaOrigemVO> getBancoDados() throws Exception {
        List<ScriptLojaOrigemVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	bd.id,\n"
                    + "	bd.nome banco\n"
                    + "from\n"
                    + "	implantacao2_5.bancodados bd "
            )) {
                while (rst.next()) {
                    ScriptLojaOrigemVO script = new ScriptLojaOrigemVO();
                    script.setIdBanco(rst.getInt("id"));
                    script.setBanco(rst.getString("banco"));
                    result.add(script);
                }
            }
        }
        return result;
    }

    public void atualizarScriptLojaOrigemGenerico(String script, String idBanco, String nomeBanco) {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "update\n"
                    + "	implantacao2_5.sistemabancodadosscripts\n"
                    + "set\n"
                    + "	script_getlojas = '" + script + "',\n"
                    + " id_bancodados = " + Integer.parseInt(idBanco) + "\n"
                    + "where\n"
                    + "	id_sistema = 252"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizaDadosSistemaGenerico(String id_banco, String banco, String script) {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "update\n"
                    + "	implantacao2_5.dadossistemagenerico\n"
                    + "set\n"
                    + "	id_banco = '" + id_banco + "',\n"
                    + "	banco = '" + banco + "',\n"
                    + "	script_getlojas = '" + script + "'\n"
                    + "where\n"
                    + "	id_sistema = 252"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String verificaDadosSistemaGenerico() throws Exception {
        String result = null;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select banco || ' ' ||script_getlojas dados from implantacao2_5.dadossistemagenerico"
            )) {
                while (rst.next()) {
                    result = rst.getString("dados");
                }
            }
        }
        return result;
    }

    public void InsereDadosSistemaGenerico() {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert\n"
                    + "	into\n"
                    + "	implantacao2_5.dadossistemagenerico\n"
                    + "values('11', 'POSTGRESQL', 'GENERICO', 252, 'vazio')"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
