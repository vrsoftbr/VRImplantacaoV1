/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLUtils;

/**
 *
 * @author lucasrafael
 */
public class FamiliaProdutoDAO {

    public boolean gerarCodigo = false;
    public boolean verificarDescricao = false;
    public boolean verificarCodigo = false;

    public FamiliaProdutoDAO setGerarCodigo(boolean gerarCodigo) {
        this.gerarCodigo = gerarCodigo;
        return this;
    }

    public FamiliaProdutoDAO setVerificarCodigo(boolean verificarCodigo) {
        this.verificarCodigo = verificarCodigo;
        return this;
    }

    public int getCodAnt(long i_codigo) throws Exception {
        int retorno = -1;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id "
                    + "from familiaproduto "
                    + "where codigoant = " + i_codigo
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("id");
                }
            }
        }

        return retorno;
    }
    
    public int getIdByCodAnt(long i_codigo) throws Exception {
        int retorno = -1;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select f.id, ant.codigoatual "
                    + "from implantacao.codant_familiaproduto ant "
                    + "inner join familiaproduto f on f.id = ant.codigoatual "
                    + "where cast(ant.impid as integer) = " + i_codigo + " "
                    + "and ant.impid is not null "
                    + "and ant.impid not like '%NULL%'"
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("codigoatual");
                }
            }
        }

        return retorno;
    }
    
    public int getIdByCodAnt(String i_codigo) throws Exception {
        int retorno = -1;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select f.id, ant.codigoatual "
                    + "from implantacao.codant_familiaproduto ant "
                    + "inner join familiaproduto f on f.id = ant.codigoatual "
                    + "where ant.impid = " + SQLUtils.stringSQL(i_codigo) + " "
                    + "and ant.impid is not null "
                    + "and ant.impid not like '%NULL%'"
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("codigoatual");
                }
            }
        }

        return retorno;
    }

    public int gerarId() throws Exception {
        int retorno = -1;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT COALESCE(MIN(id + 1), 1) AS id "
                    + "FROM familiaproduto "
                    + "WHERE id + 1 NOT IN (SELECT id FROM familiaproduto)"
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("id");
                }
            }
        }
        return retorno;
    }

    public int getIdByDescricao(String i_descricao) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id "
                    + "from familiaproduto "
                    + "where descricao like '%" + i_descricao + "%'"
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                } else {
                    return -1;
                }
            }
        }
    }

    public Map<Long, FamiliaProdutoVO> carregarAnteriores() throws Exception {
        Map<Long, FamiliaProdutoVO> map = new LinkedHashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	codigoanterior, \n"
                    + "	codigoatual, \n"
                    + "	descricao, \n"
                    + "	id_situacaocadastro\n"
                    + "from \n"
                    + "	implantacao.codigoanterior_familiaproduto fp \n"
                    + "	join familiaproduto p on p.id = fp.codigoatual\n"
                    + "order by\n"
                    + "	fp.codigoanterior"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO vo = new FamiliaProdutoVO();
                    vo.setId(rst.getInt("codigoatual"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setCodigoant(rst.getInt("codigoanterior"));
                    vo.setIdLong(rst.getLong("codigoanterior"));
                    vo.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));

                    map.put(vo.getIdLong(), vo);
                }
            }
        }

        return map;
    }

    /**
     * Apaga todos os registros do mercadológico
     *
     * @throws Exception
     */
    public void deleteAll() throws Exception {
        Conexao.begin();
        try {

            Conexao.createStatement().executeUpdate(
                    "delete from familiaproduto; "
                    + "delete from implantacao.codigoanterior_familiaproduto;"
            );
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public void salvar(List<FamiliaProdutoVO> v_familiaProduto) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        String descricao = "";

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_familiaProduto.size());

            ProgressBar.setStatus("Importando Familia de Produto...");

            //Obtem a lista de códigos anteriores
            Map<Long, FamiliaProdutoVO> anteriores = carregarAnteriores();

            for (FamiliaProdutoVO i_familiaProduto : v_familiaProduto) {

                int id;

                if (gerarCodigo) {
                    id = new CodigoInternoDAO().get("familiaproduto");
                } else {
                    if (i_familiaProduto.getIdLong() != 0) {
                        if ((i_familiaProduto.getIdLong() > 0)
                                && (i_familiaProduto.getIdLong() > 999999)) {
                            id = new CodigoInternoDAO().get("familiaproduto");
                        } else {
                            id = (int) i_familiaProduto.getIdLong();
                        }

                        if (i_familiaProduto.getCodigoant() == 0
                                && i_familiaProduto.getIdLong() <= Integer.MAX_VALUE) {
                            i_familiaProduto.setCodigoant((int) i_familiaProduto.getIdLong());
                        }

                    } else if (i_familiaProduto.getCodigoant() != 0) {
                        if ((i_familiaProduto.getCodigoant() > 0)
                                && (i_familiaProduto.getCodigoant() > 999999)) {
                            id = new CodigoInternoDAO().get("familiaproduto");
                        } else {
                            id = i_familiaProduto.getCodigoant();
                        }

                        if (i_familiaProduto.getIdLong() == 0) {
                            i_familiaProduto.setIdLong(i_familiaProduto.getCodigoant());
                        }
                    } else if (i_familiaProduto.getId() != 0) {
                        if ((i_familiaProduto.getId() > 0)
                                && (i_familiaProduto.getId() > 999999)) {
                            id = new CodigoInternoDAO().get("familiaproduto");
                        } else {
                            id = i_familiaProduto.getId();
                        }

                        i_familiaProduto.setIdLong(i_familiaProduto.getId());
                        i_familiaProduto.setCodigoant(i_familiaProduto.getId());

                    } else {
                        id = new CodigoInternoDAO().get("familiaproduto");
                    }
                }

                i_familiaProduto.setId(id);

                descricao = i_familiaProduto.getDescricao();

                if (descricao.length() > 40) {
                    descricao = descricao.substring(0, 40);
                }

                if (!verificarCodigo) {

                    sql = new StringBuilder();
                    sql.append("INSERT INTO familiaproduto (");
                    sql.append("id, descricao, id_situacaocadastro, codigoant) ");
                    sql.append("VALUES (");
                    sql.append(i_familiaProduto.getId() + ", ");
                    sql.append("'" + descricao + "', ");
                    sql.append("1, ");
                    sql.append(i_familiaProduto.getCodigoant() + " ");
                    sql.append(");");
                    sql.append("insert into implantacao.codigoanterior_familiaproduto (");
                    sql.append("codigoanterior, codigoatual) values (");
                    sql.append((i_familiaProduto.getIdLong() > 0 ? i_familiaProduto.getIdLong() : i_familiaProduto.getId()) + ", ");
                    sql.append((i_familiaProduto.getId() > 0 ? i_familiaProduto.getId() : i_familiaProduto.getIdLong()) + "); ");
                    stm.execute(sql.toString());
                } else {

                    if (!anteriores.containsKey(i_familiaProduto.getIdLong())) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO familiaproduto (");
                        sql.append("id, descricao, id_situacaocadastro, codigoant) ");
                        sql.append("VALUES (");
                        sql.append(i_familiaProduto.getId() + ", ");
                        sql.append("'" + descricao + "', ");
                        sql.append("1, ");
                        sql.append(i_familiaProduto.getCodigoant() + " ");
                        sql.append(");");
                        sql.append("insert into implantacao.codigoanterior_familiaproduto (");
                        sql.append("codigoanterior, codigoatual) values (");
                        sql.append((i_familiaProduto.getIdLong() > 0 ? i_familiaProduto.getIdLong() : i_familiaProduto.getId()) + ", ");
                        sql.append((i_familiaProduto.getId() > 0 ? i_familiaProduto.getId() : i_familiaProduto.getIdLong()) + "); ");
                        anteriores.put(i_familiaProduto.getIdLong(), i_familiaProduto);
                        stm.execute(sql.toString());
                        /*
                         TODO Criar uma rotina que executa um update caso encontre.
                         */

                    }

                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {

            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * Carrega uma Map de famílias relacionando os códigos anteriores da tabela
     * implantacao.codantfamilia2.
     *
     * @return Listagem com os códigos anteriores.
     * @throws Exception
     */
    public MultiMap<String, FamiliaProdutoVO> carregarAnteriores2() throws Exception {
        MultiMap<String, FamiliaProdutoVO> result = new MultiMap<>(3);
        try (Statement stm = Conexao.createStatement()) {
            //Se a tabela existir executa a consulta.
            if (Utils.existeTabela("implantacao", "codantfamilia2")) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	ca.impsistema, \n"
                        + "	ca.imploja, \n"
                        + "	ca.impid, \n"
                        + "	ca.codigoatual, \n"
                        + "	fa.descricao, \n"
                        + "	fa.id_situacaocadastro,\n"
                        + "	fa.codigoant\n"
                        + "from \n"
                        + "	implantacao.codantfamilia2 ca \n"
                        + "	left join familiaproduto fa on \n"
                        + "		ca.codigoatual = fa.id"
                )) {
                    while (rst.next()) {
                        FamiliaProdutoVO vo = new FamiliaProdutoVO();
                        vo.setId(rst.getInt("codigoatual"));
                        vo.setDescricao(rst.getString("descricao"));
                        vo.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                        vo.setCodigoant(rst.getInt("codigoant"));
                        result.put(
                                vo,
                                rst.getString("impsistema"),
                                rst.getString("imploja"),
                                rst.getString("impid"));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Método que grava uma lista de famílias. O difierencial deste método é que
     * ele trabalha com a tabela implantacao.codantfamilia2 e permite importar
     * famílias com ids não inteiros.
     *
     * @param familias Listagem das famílias.
     * @throws Exception
     */
    public void salvar2(List<FamiliaProdutoVO> familias) throws Exception {

        createTable();

        MultiMap<String, FamiliaProdutoVO> aux = new MultiMap<>(3);
        for (FamiliaProdutoVO familia : familias) {
            aux.put(familia, familia.getImpSistema(), familia.getImpLoja(), familia.getImpId());
        }

        MultiMap<String, FamiliaProdutoVO> anteriores = carregarAnteriores2();
        Stack<Integer> idsLivres = carregarIdsLivres(50000);

        ProgressBar.setStatus("Gravando as famílias....");
        ProgressBar.setMaximum(aux.size());
        try {
            Conexao.begin();

            try (Statement stm = Conexao.createStatement()) {
                for (FamiliaProdutoVO oFamilia : aux.values()) {
                    if (!anteriores.containsKey(
                            oFamilia.getImpSistema(),
                            oFamilia.getImpLoja(),
                            oFamilia.getImpId()
                    )) {

                        //<editor-fold defaultstate="collapsed" desc="Valida o id do produto">
                        if (gerarCodigo) {
                            oFamilia.setId(idsLivres.pop());
                        } else {
                            boolean geraID = false;
                            //Gera um novo se as condições forem atingidas.
                            if (oFamilia.getId() < 1 || oFamilia.getId() > 999999) {
                                //Se for um id inválido
                                geraID = true;
                            } else if (!idsLivres.contains(oFamilia.getId())) {
                                //Se o ID do produto não estiver disponível
                                geraID = true;
                            }

                            if (geraID) {
                                //Gera um novo id
                                oFamilia.setId(idsLivres.pop());
                            } else {
                                //Se o id informado for válido, remove ele da 
                                //listagem de disponíveis
                                idsLivres.remove((Integer) oFamilia.getId());
                            }
                        }
                        //</editor-fold>

                        stm.execute(
                                "INSERT INTO familiaproduto (id, descricao, id_situacaocadastro, codigoant) values ("
                                + oFamilia.getId() + ","
                                + Utils.quoteSQL(oFamilia.getDescricao()) + ","
                                + oFamilia.getId_situacaocadastro() + ","
                                + oFamilia.getCodigoant() + ""
                                + ");");

                        stm.execute("insert into implantacao.codantfamilia2 ("
                                + "impsistema, "
                                + "imploja, "
                                + "impid, "
                                + "codigoatual ) values ("
                                + Utils.quoteSQL(oFamilia.getImpSistema()) + ", "
                                + Utils.quoteSQL(oFamilia.getImpLoja()) + ", "
                                + Utils.quoteSQL(oFamilia.getImpId()) + ", "
                                + oFamilia.getId() + ""
                                + ");");

                        anteriores.put(
                                oFamilia,
                                oFamilia.getImpSistema(),
                                oFamilia.getImpLoja(),
                                oFamilia.getImpId()
                        );

                    }
                    ProgressBar.next();
                }
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }

    }

    /**
     * Cria a tabela no banco.
     *
     * @throws Exception
     */
    private void createTable() throws Exception {
        Conexao.createStatement().execute(
                "create table if not exists implantacao.codantfamilia2 (\n"
                + "	impsistema varchar, \n"
                + "	imploja varchar, \n"
                + "	impid varchar,\n"
                + "	codigoatual integer,\n"
                + "	primary key (impsistema, imploja, impid)\n"
                + ");"
        );
    }

    /**
     * Gera uma listagem com os ids disponíveis para uso nas famílias.
     *
     * @param limit Valor máximo de ids para serem gerados.
     * @return Pilha com todos os ids disponíveis.
     * @throws Exception
     */
    private Stack<Integer> carregarIdsLivres(int limit) throws Exception {
        Stack<Integer> result = new Stack<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id FROM generate_series(1, " + limit + ") "
                    + "AS s(id) except select id from "
                    + "familiaproduto order by id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }
}
