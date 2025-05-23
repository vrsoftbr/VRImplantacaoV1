package vrimplantacao2.dao.cadastro.usuario;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Stack;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.usuario.TipoSetorVO;
import vrimplantacao2.vo.importacao.TipoSetorIMP;

public class TipoSetorDAO {

    private boolean gerarCodigo = false;
    private String sistema;
    private MultiMap<String, TipoSetorVO> anteriores;
    private MultiMap<String, Integer> setoresAtuais;

    public boolean isGerarCodigo() {
        return gerarCodigo;
    }

    public String getSistema() {
        return this.sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    /**
     * Cria a tabela no banco.
     *
     * @throws Exception
     */
    private void createTable() throws Exception {
        Conexao.createStatement().execute(
                "do $$\n"
                + "declare\n"
                + "begin\n"
                + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_tiposetor') then\n"
                + "		create table implantacao.codant_tiposetor (\n"
                + "			impsistema varchar, \n"
                + "			imploja varchar, \n"
                + "			impid varchar,\n"
                + "			codigoatual integer,\n"
                + "                     descricao varchar, \n"
                + "			primary key (impsistema, imploja, impid)\n"
                + "		);\n"
                + "		raise notice 'tabela criada';\n"
                + "	end if;\n"
                + "end;\n"
                + "$$;"
        );
    }

    /**
     * Gera uma listagem com os ids disponíveis para uso nos setores.
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
                    + "tiposetor order by id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }

    /**
     * Método que grava uma lista de setores. O difierencial deste método é que
     * ele trabalha com a tabela implantacao.codant_tiposetor e permite importar
     * setores com ids não inteiros.
     *
     * @param setores Listagem dos setores.
     * @throws Exception
     */
    public void salvar(List<TipoSetorIMP> setores) throws Exception {

        MultiMap<String, TipoSetorIMP> aux = organizarSetores(setores);

        Stack<Integer> idsLivres = carregarIdsLivres(1000);

        boolean setorExistente = false;

        ProgressBar.setStatus("Gravando os tipos de setores....");
        ProgressBar.setMaximum(aux.size());

        try {
            Conexao.begin();

            createTable();

            try (Statement stm = Conexao.createStatement()) {
                for (TipoSetorIMP oTipoSetor : aux.values()) {
                    if (!getAnteriores().containsKey(
                            getSistema(),
                            oTipoSetor.getImportLoja(),
                            oTipoSetor.getImportId()
                    )) {

                        TipoSetorVO vo = new TipoSetorVO();
                        vo.setDescricao(oTipoSetor.getDescricao());

                        if (gerarCodigo) {
                            vo.setId(idsLivres.pop());
                        } else {
                            boolean geraID = false;
                            //Verifica se o id dos sistema anterior é um id válido
                            //ou seja inteiro < que 1000
                            try {
                                vo.setId(Integer.parseInt(oTipoSetor.getImportId()));
                            } catch (NumberFormatException e) {
                                vo.setId(-1);
                            }
                            //Gera um novo se as condições forem atingidas.
                            if (vo.getId() < 1 || vo.getId() > 999) {
                                //Se for um id inválido
                                geraID = true;
                            } else if (!idsLivres.contains(vo.getId())) {
                                //Se o ID do produto não estiver disponível
                                geraID = true;
                            }

                            if (geraID) {
                                //Gera um novo id
                                vo.setId(idsLivres.pop());
                            } else {
                                //Se o id informado for válido, remove ele da 
                                //listagem de disponíveis
                                idsLivres.remove((Integer) vo.getId());
                            }
                        }

                        if (getIdSetoresAtuais().containsKey(
                                oTipoSetor.getDescricao())) {

                            Integer idExistente = setoresAtuais.get(oTipoSetor.getDescricao());
                            vo.setId(idExistente);
                            setorExistente = true;
                        }

                        if (!setorExistente) {
                            stm.execute(
                                    "INSERT INTO tiposetor (id, descricao) values ("
                                    + vo.getId() + ","
                                    + Utils.quoteSQL(vo.getDescricao())
                                    + ");");
                        }

                        stm.execute("insert into implantacao.codant_tiposetor ("
                                + "impsistema, "
                                + "imploja, "
                                + "impid, "
                                + "codigoatual,"
                                + "descricao ) values ("
                                + Utils.quoteSQL(getSistema()) + ", "
                                + Utils.quoteSQL(oTipoSetor.getImportLoja()) + ", "
                                + Utils.quoteSQL(oTipoSetor.getImportId()) + ", "
                                + vo.getId() + ", "
                                + Utils.quoteSQL(oTipoSetor.getDescricao()) + ""
                                + ");");

                        getAnteriores().put(
                                vo,
                                getSistema(),
                                oTipoSetor.getImportLoja(),
                                oTipoSetor.getImportId()
                        );
                    }
                    setorExistente = false;
                    ProgressBar.next();
                }
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    private MultiMap<String, TipoSetorIMP> organizarSetores(List<TipoSetorIMP> setores) {
        MultiMap<String, TipoSetorIMP> organizados = new MultiMap<>();
        for (TipoSetorIMP setor : setores) {
            organizados.put(setor, setor.getChave());
        }
        return organizados;
    }

    public MultiMap<String, TipoSetorVO> getAnteriores() throws Exception {
        if (anteriores == null) {
            atualizaAnteriores();
        }
        return anteriores;
    }

    private void atualizaAnteriores() throws Exception {
        anteriores = new MultiMap<>(3);
        try (Statement stm = Conexao.createStatement()) {
            //Se a tabela existir executa a consulta.
            if (Utils.existeTabela("implantacao", "codant_tiposetor")) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	cf.impsistema, \n"
                        + "	cf.imploja, \n"
                        + "	cf.impid, \n"
                        + "	cf.codigoatual, \n"
                        + "	ts.descricao\n"
                        + "from \n"
                        + "	implantacao.codant_tiposetor cf \n"
                        + "	left join tiposetor ts on \n"
                        + "		cf.codigoatual = ts.id"
                )) {
                    while (rst.next()) {
                        TipoSetorVO vo = new TipoSetorVO();
                        vo.setId(rst.getInt("codigoatual"));
                        vo.setDescricao(rst.getString("descricao"));
                        anteriores.put(
                                vo,
                                rst.getString("impsistema"),
                                rst.getString("imploja"),
                                rst.getString("impid"));
                    }
                }
            }
        }
    }

    private MultiMap<String, Integer> getIdSetoresAtuais() throws Exception {
        if (setoresAtuais == null) {
            atualizaSetoresAtuais();
        }
        return setoresAtuais;
    }

    private void atualizaSetoresAtuais() throws Exception {
        setoresAtuais = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            //Se a tabela existir executa a consulta.
            if (Utils.existeTabela("public", "tiposetor")) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	id, \n"
                        + "	descricao \n"
                        + "from \n"
                        + "	tiposetor"
                )) {
                    while (rst.next()) {
                        setoresAtuais.put(
                                rst.getInt("id"),
                                rst.getString("descricao"));
                    }
                }
            }
        }
    }
}
