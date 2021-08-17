package vrimplantacao2_5.dao.cadastro.sistemabancodados;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
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
    
    public void salvar(SistemaBancoDadosVO vo) throws Exception {

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
}
