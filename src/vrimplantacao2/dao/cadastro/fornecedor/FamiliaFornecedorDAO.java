package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Stack;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fornecedor.FamiliaFornecedorVO;
import vrimplantacao2.vo.importacao.FamiliaFornecedorIMP;

public class FamiliaFornecedorDAO {

    private boolean gerarCodigo = false;
    private String sistema;

    public boolean isGerarCodigo() {
        return gerarCodigo;
    }

    public String getSistema() {
        return this.sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    private void atualizaAnteriores() throws Exception {
        anteriores = new MultiMap<>(3);
        try (Statement stm = Conexao.createStatement()) {
            //Se a tabela existir executa a consulta.
            if (Utils.existeTabela("implantacao", "codant_familiafornecedor")) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	cf.impsistema, \n"
                        + "	cf.imploja, \n"
                        + "	cf.impid, \n"
                        + "	cf.codigoatual, \n"
                        + "	ff.descricao, \n"
                        + "	ff.id_situacaocadastro\n"
                        + "from \n"
                        + "	implantacao.codant_familiafornecedor cf \n"
                        + "	left join familiafornecedor ff on \n"
                        + "		cf.codigoatual = ff.id"
                )) {
                    while (rst.next()) {
                        FamiliaFornecedorVO vo = new FamiliaFornecedorVO();
                        vo.setId(rst.getInt("codigoatual"));
                        vo.setDescricao(rst.getString("descricao"));
                        vo.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("id_situacaocadastro")));
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
                + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_familiafornecedor') then\n"
                + "		create table implantacao.codant_familiafornecedor (\n"
                + "			impsistema varchar, \n"
                + "			imploja varchar, \n"
                + "			impid varchar,\n"
                + "			codigoatual integer,\n"
                + "			primary key (impsistema, imploja, impid)\n"
                + "		);\n"
                + "		raise notice 'tabela criada';\n"
                + "	end if;\n"
                + "end;\n"
                + "$$;"
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
                    + "familiafornecedor order by id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
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
    public void salvar(List<FamiliaFornecedorIMP> familias) throws Exception {

        MultiMap<String, FamiliaFornecedorIMP> aux = organizarFamilias(familias);

        Stack<Integer> idsLivres = carregarIdsLivres(100000);

        ProgressBar.setStatus("Gravando as famílias de fornecedores....");
        ProgressBar.setMaximum(aux.size());
        try {
            Conexao.begin();

            createTable();

            try (Statement stm = Conexao.createStatement()) {
                for (FamiliaFornecedorIMP oFamilia : aux.values()) {
                    if (!getAnteriores().containsKey(
                            getSistema(),
                            oFamilia.getImportLoja(),
                            oFamilia.getImportId()
                    )) {

                        FamiliaFornecedorVO vo = new FamiliaFornecedorVO();
                        vo.setDescricao(oFamilia.getDescricao());
                        vo.setSituacaoCadastro(oFamilia.getSituacaoCadastro());

                        if (gerarCodigo) {
                            vo.setId(idsLivres.pop());
                        } else {
                            boolean geraID = false;
                            //Verifica se o id dos sistema anterior é um id válido
                            //ou seja inteiro < que 100000
                            try {
                                vo.setId(Integer.parseInt(oFamilia.getImportId()));
                            } catch (NumberFormatException e) {
                                vo.setId(-1);
                            }
                            //Gera um novo se as condições forem atingidas.
                            if (vo.getId() < 1 || vo.getId() > 999999) {
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

                        stm.execute(
                                "INSERT INTO familiafornecedor (id, descricao, id_situacaocadastro) values ("
                                + vo.getId() + ","
                                + Utils.quoteSQL(vo.getDescricao()) + ","
                                + vo.getSituacaoCadastro().getId()
                                + ");");

                        stm.execute("insert into implantacao.codant_familiafornecedor ("
                                + "impsistema, "
                                + "imploja, "
                                + "impid, "
                                + "codigoatual ) values ("
                                + Utils.quoteSQL(getSistema()) + ", "
                                + Utils.quoteSQL(oFamilia.getImportLoja()) + ", "
                                + Utils.quoteSQL(oFamilia.getImportId()) + ", "
                                + vo.getId() + ""
                                + ");");

                        getAnteriores().put(
                                vo,
                                getSistema(),
                                oFamilia.getImportLoja(),
                                oFamilia.getImportId()
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

    private MultiMap<String, FamiliaFornecedorIMP> organizarFamilias(List<FamiliaFornecedorIMP> familias) {
        MultiMap<String, FamiliaFornecedorIMP> organizados = new MultiMap<>();
        for (FamiliaFornecedorIMP familia : familias) {
            organizados.put(familia, familia.getChave());
        }
        return organizados;
    }

    public void apagarFamilia() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "delete from familiafornecedor; "
                    + "alter sequence familiafornecedor_id_seq restart with 1;"
                    + "drop table if exists implantacao.codant_familiafornecedor;");
        }
    }

    private MultiMap<String, FamiliaFornecedorVO> anteriores;

    public MultiMap<String, FamiliaFornecedorVO> getAnteriores() throws Exception {
        if (anteriores == null) {
            atualizaAnteriores();
        }
        return anteriores;
    }
}
