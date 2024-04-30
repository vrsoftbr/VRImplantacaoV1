package vrimplantacao2.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2_5.controller.migracao.LogController;
import vrimplantacao2_5.vo.enums.EOperacao;

/**
 * DAO responsável por gravar os dados do mercadológico no banco de dados.
 *
 * @author Leandro
 */
public class MercadologicoDAO {

    private static final Logger LOG = Logger.getLogger(MercadologicoDAO.class.getName());

    /**
     * Listagem do mapeamento dos mercadológicos
     */
    private MultiMap<String, MercadologicoVO> anteriores;
    private MercadologicoVO aAcertar;
    private final LogController logController = new LogController();
    private int idLojaVR;
    
    private String sistema;
    
    public String getSistema() {
        return this.sistema;
    }
    
    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    /**
     * Cria a tabela no banco de dados caso ela não exista.
     *
     * @throws Exception
     */
    private void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n"
                    + "declare\n"
                    + "begin\n"
                    + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_mercadologico') then\n"
                    + "		create table implantacao.codant_mercadologico (\n"
                    + "                	imp_sistema varchar not null,\n"
                    + "                	imp_loja varchar not null,\n"
                    + "                	ant_merc1 varchar not null,\n"
                    + "                	ant_merc2 varchar not null,\n"
                    + "                	ant_merc3 varchar not null,\n"
                    + "                	ant_merc4 varchar not null,\n"
                    + "                	ant_merc5 varchar not null,\n"
                    + "                	merc1 integer,\n"
                    + "                	merc2 integer,\n"
                    + "                	merc3 integer,\n"
                    + "                	merc4 integer,\n"
                    + "                	merc5 integer,\n"
                    + "                	descricao varchar,\n"
                    + "                        nivel integer,\n"
                    + "                	primary key (imp_sistema, imp_loja, ant_merc1, ant_merc2, ant_merc3, ant_merc4, ant_merc5)\n"
                    + "                );\n"
                    + "	end if;\n"
                    + "end;\n"
                    + "$$;"
            );
        }
    }
    
    public void salvar(List<MercadologicoIMP> mercadologicos) throws Exception {
        salvar(mercadologicos, EnumSet.noneOf(OpcaoProduto.class));
    }

    /**
     * Grava uma listagem de mercadológicos no VR.
     *
     * @param mercadologicos Lista de mercadológicos.
     * @param opt
     * @throws Exception
     */
    public void salvar(List<MercadologicoIMP> mercadologicos, Set<OpcaoProduto> opt) throws Exception {
        //Organizaria o mercadologico informado, deixando pronto para incluir;       
        MultiMap<String, MercadologicoAuxiliar> organizados = organizarMercadologico(mercadologicos);
        boolean manterMercadologico = opt.contains(OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO);
        
        Conexao.begin();
        try {
            //Cria a tabela;
            createTable();

            int nivelMax = gravarCodigosAnteriores(organizados, opt);
            try (Statement stm = Conexao.createStatement()) {
                ProgressBar.setStatus("Gravando os mercadológicos...");
                ProgressBar.setMaximum(getCodigoAnterior().size());
                for (KeyList<String> imp : getCodigoAnterior().keySet()) {
                    String[] chave = imp.toArray();
                    chave[0] = getSistema();
                    MercadologicoVO vo = getCodigoAnterior().get(chave);
                    MercadologicoAuxiliar item = organizados.get(chave);

                    if (vo == null && item != null) {
                        vo = new MercadologicoVO();
                        vo.setDescricao(item.descricao);
                        vo.setNivel(item.nivel);
                        String sql = null;

                        MercadologicoVO pai = getCodigoAnterior().get(
                                getSistema(),
                                chave[1],
                                chave[2],
                                vo.getNivel() > 2 ? chave[3] : "",
                                vo.getNivel() > 3 ? chave[4] : "",
                                vo.getNivel() > 4 ? chave[5] : "",
                                "");

                        if (vo.getNivel() == 1) {
                            boolean validId = Utils.stringToInt(chave[2]) <= 999;
                            sql = "insert into mercadologico ("
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5,"
                                    + "descricao,"
                                    + "nivel"
                                    + ") values ("
                                    + (!manterMercadologico || !validId ? "(select id from generate_series(1,999) s(id) except select mercadologico1 from mercadologico where nivel = 1 order by id limit 1)," : Utils.stringToInt(chave[2]) + ",")
                                    + "0,"
                                    + "0,"
                                    + "0,"
                                    + "0,"
                                    + Utils.quoteSQL(vo.getDescricao()) + ","
                                    + vo.getNivel() + ") returning "
                                    + "id,"
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5;";
                        } else if (vo.getNivel() == 2) {
                            boolean validId = Utils.stringToInt(chave[3]) <= 999;
                            sql = "insert into mercadologico ("
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5,"
                                    + "descricao,"
                                    + "nivel"
                                    + ") values ("
                                    + pai.getMercadologico1() + ","
                                    + (!manterMercadologico || !validId ? "(select id from generate_series(1,999) s(id) except select mercadologico2 from mercadologico where mercadologico1 = " + pai.getMercadologico1() + " and nivel = 2 order by id limit 1)," : Utils.stringToInt(chave[3]) + ",")
                                    + "0,"
                                    + "0,"
                                    + "0,"
                                    + Utils.quoteSQL(vo.getDescricao()) + ","
                                    + vo.getNivel() + ") returning "
                                    + "id,"
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5;";
                        } else if (vo.getNivel() == 3) {
                            boolean validId = Utils.stringToInt(chave[4]) <= 999;
                            sql = "insert into mercadologico ("
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5,"
                                    + "descricao,"
                                    + "nivel"
                                    + ") values ("
                                    + pai.getMercadologico1() + ","
                                    + pai.getMercadologico2() + ","
                                    + (!manterMercadologico || !validId ? "(select id from generate_series(1,999) s(id) except select mercadologico3 from mercadologico where mercadologico1 = " + pai.getMercadologico1() + " and mercadologico2 = " + pai.getMercadologico2() + " and nivel = 3 order by id limit 1)," : Utils.stringToInt(chave[4]) + ",")
                                    + "0,"
                                    + "0,"
                                    + Utils.quoteSQL(vo.getDescricao()) + ","
                                    + vo.getNivel() + ") returning "
                                    + "id,"
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5;";
                        } else if (vo.getNivel() == 4 && nivelMax > 3) {
                            boolean validId = Utils.stringToInt(chave[5]) <= 999;
                            sql = "insert into mercadologico ("
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5,"
                                    + "descricao,"
                                    + "nivel"
                                    + ") values ("
                                    + pai.getMercadologico1() + ","
                                    + pai.getMercadologico2() + ","
                                    + pai.getMercadologico3() + ","
                                    + (!manterMercadologico || !validId ? "(select id from generate_series(1,999) s(id) except select mercadologico4 from mercadologico where mercadologico1 = " + pai.getMercadologico1() + " and mercadologico2 = " + pai.getMercadologico2() + " and mercadologico3 = " + pai.getMercadologico3() + " and nivel = 4 order by id limit 1)," : Utils.stringToInt(chave[5]) + ",")
                                    + "0,"
                                    + Utils.quoteSQL(vo.getDescricao()) + ","
                                    + vo.getNivel() + ") returning "
                                    + "id,"
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5;";
                        } else if (vo.getNivel() == 5 && nivelMax > 4) {
                            boolean validId = Utils.stringToInt(chave[6]) <= 999;
                            sql = "insert into mercadologico ("
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5,"
                                    + "descricao,"
                                    + "nivel"
                                    + ") values ("
                                    + pai.getMercadologico1() + ","
                                    + pai.getMercadologico2() + ","
                                    + pai.getMercadologico3() + ","
                                    + pai.getMercadologico4() + ","
                                    + (!manterMercadologico || !validId ? "(select id from generate_series(1,999) s(id) except select mercadologico5 from mercadologico where mercadologico1 = " + pai.getMercadologico1() + " and mercadologico2 = " + pai.getMercadologico2() + " and mercadologico3 = " + pai.getMercadologico3() + " and mercadologico4 = " + pai.getMercadologico4() + " and nivel = 5 order by id limit 1)," : Utils.stringToInt(chave[6]) + ",")
                                    + Utils.quoteSQL(vo.getDescricao()) + ","
                                    + vo.getNivel() + ") returning "
                                    + "id,"
                                    + "mercadologico1,"
                                    + "mercadologico2,"
                                    + "mercadologico3,"
                                    + "mercadologico4,"
                                    + "mercadologico5;";
                        }

                        try (ResultSet rst = stm.executeQuery(sql)) {
                            rst.next();
                            vo.setId(rst.getInt("id"));
                            vo.setMercadologico1(rst.getInt("mercadologico1"));
                            vo.setMercadologico2(rst.getInt("mercadologico2"));
                            vo.setMercadologico3(rst.getInt("mercadologico3"));
                            vo.setMercadologico4(rst.getInt("mercadologico4"));
                            vo.setMercadologico5(rst.getInt("mercadologico5"));

                            String sqlCodigoAnterior = "update implantacao.codant_mercadologico set "
                                    + "merc1 = " + vo.getMercadologico1() + ","
                                    + "merc2 = " + vo.getMercadologico2() + ","
                                    + "merc3 = " + vo.getMercadologico3() + ","
                                    + "merc4 = " + vo.getMercadologico4() + ","
                                    + "merc5 = " + vo.getMercadologico5()
                                    + " where "
                                    + "imp_sistema = " + Utils.quoteSQL(getSistema()) + " and "
                                    + "imp_loja = " + Utils.quoteSQL(chave[1]) + " and "
                                    + "ant_merc1 = " + Utils.quoteSQL(chave[2]) + " and "
                                    + "ant_merc2 = " + Utils.quoteSQL(chave[3]) + " and "
                                    + "ant_merc3 = " + Utils.quoteSQL(chave[4]) + " and "
                                    + "ant_merc4 = " + Utils.quoteSQL(chave[5]) + " and "
                                    + "ant_merc5 = " + Utils.quoteSQL(chave[6]) + ";";

                            stm.execute(sqlCodigoAnterior);
                            getCodigoAnterior().put(vo, chave);
                        }

                    }
                    ProgressBar.next();
                }
            }

            gerarAAcertar(nivelMax);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            //Executa log de operação
            logController.executar(EOperacao.SALVAR_MERCADOLOGICO.getId(),
                    sdf.format(new Date()),
                    getIdLojaVR());

            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * Cria o mercadológico 'A ACERTAR' no banco de dados.
     *
     * @param nivelMax Quantidade de níveis que o mercadologico terá.
     * @throws Exception
     */
    private void gerarAAcertar(int nivelMax) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            //Verifica se existe o mercadologico à acertar
            if (getAAcertar() == null) {
                //Inclui o nível 1 do A Acertar
                String sql = "insert into mercadologico ("
                        + "mercadologico1,"
                        + "mercadologico2,"
                        + "mercadologico3,"
                        + "mercadologico4,"
                        + "mercadologico5,"
                        + "descricao,"
                        + "nivel"
                        + ") values ("
                        + "(select id from generate_series(1,999) s(id) except select mercadologico1 from mercadologico where nivel = 1 order by id limit 1),"
                        + "0,"
                        + "0,"
                        + "0,"
                        + "0,"
                        + "'A ACERTAR',"
                        + "1) returning mercadologico1;";
                int idAcertar;
                try (ResultSet rst = stm.executeQuery(sql)) {
                    rst.next();
                    idAcertar = rst.getInt("mercadologico1");
                }

                sql = "insert into mercadologico ("
                        + "mercadologico1,"
                        + "mercadologico2,"
                        + "mercadologico3,"
                        + "mercadologico4,"
                        + "mercadologico5,"
                        + "descricao,"
                        + "nivel"
                        + ") values ("
                        + idAcertar + ","
                        + "1,"
                        + "0,"
                        + "0,"
                        + "0,"
                        + "'A ACERTAR',"
                        + "2);";

                sql += "insert into mercadologico ("
                        + "mercadologico1,"
                        + "mercadologico2,"
                        + "mercadologico3,"
                        + "mercadologico4,"
                        + "mercadologico5,"
                        + "descricao,"
                        + "nivel"
                        + ") values ("
                        + idAcertar + ","
                        + "1,"
                        + "1,"
                        + "0,"
                        + "0,"
                        + "'A ACERTAR',"
                        + "3);";
                if (nivelMax > 3) {
                    sql += "insert into mercadologico ("
                            + "mercadologico1,"
                            + "mercadologico2,"
                            + "mercadologico3,"
                            + "mercadologico4,"
                            + "mercadologico5,"
                            + "descricao,"
                            + "nivel"
                            + ") values ("
                            + idAcertar + ","
                            + "1,"
                            + "1,"
                            + "1,"
                            + "0,"
                            + "'A ACERTAR',"
                            + "4);";
                }
                if (nivelMax > 4) {
                    sql += "insert into mercadologico ("
                            + "mercadologico1,"
                            + "mercadologico2,"
                            + "mercadologico3,"
                            + "mercadologico4,"
                            + "mercadologico5,"
                            + "descricao,"
                            + "nivel"
                            + ") values ("
                            + idAcertar + ","
                            + "1,"
                            + "1,"
                            + "1,"
                            + "1,"
                            + "'A ACERTAR',"
                            + "5);";
                }

                stm.execute(sql);
            }
        }
    }

    /**
     * Inclui a listagem dos mercadológicos organizados no banco de dados.
     *
     * @param organizados Listagem dos códigos anteriores ligados.
     * @return O nível máximo de mercadológicos da listagem.
     * @throws Exception
     */
    private int gravarCodigosAnteriores(MultiMap<String, MercadologicoAuxiliar> organizados, Set<OpcaoProduto> opt) throws Exception {
        int result = 1;
        try (Statement stm = Conexao.createStatement()) {
            if (!opt.contains(OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR)) {
                stm.execute("delete from mercadologico where mercadologico1 > 0; delete from implantacao.codant_mercadologico;");
            }
            for (KeyList<String> key : organizados.keySet()) {
                String[] chave = key.toArray();
                MercadologicoAuxiliar descricao = organizados.get(chave);

                if (!getCodigoAnterior().containsKey(chave)) {
                    String sql = "INSERT INTO implantacao.codant_mercadologico(\n"
                            + "	imp_sistema, \n"
                            + "	imp_loja, \n"
                            + "	ant_merc1, \n"
                            + "	ant_merc2, \n"
                            + "	ant_merc3, \n"
                            + "	ant_merc4, \n"
                            + "	ant_merc5, \n"
                            + "	descricao,\n"
                            + "	nivel\n"
                            + ") VALUES (\n"
                            + "	" + Utils.quoteSQL(getSistema()) + ", \n"
                            + "	" + Utils.quoteSQL(chave[1]) + ", \n"
                            + "	" + Utils.quoteSQL(chave[2]) + ", \n"
                            + "	" + Utils.quoteSQL(chave[3]) + ", \n"
                            + "	" + Utils.quoteSQL(chave[4]) + ", \n"
                            + "	" + Utils.quoteSQL(chave[5]) + ", \n"
                            + "       " + Utils.quoteSQL(chave[6]) + ", \n"
                            + "       " + Utils.quoteSQL(descricao.descricao) + ", \n"
                            + "       " + descricao.nivel + " \n"
                            + ");";

                    stm.execute(sql);
                    getCodigoAnterior().put(null, chave);
                }
                if (descricao.nivel > result) {
                    result = descricao.nivel;
                }
            }
        }
        return result;
    }

    /**
     * Retorna um {@link MultiMap} com os mercadológicos mapeados pelo código
     * anterior em outros sistemas.
     *
     * @return
     * @throws Exception
     */
    public MultiMap<String, MercadologicoVO> getCodigoAnterior() throws Exception {
        if (anteriores == null) {
            atualizarAnteriores();
        }
        return anteriores;
    }

    /**
     * Atualiza a listagem de codigos anteriores no sistema.
     *
     * @throws Exception
     */
    public void atualizarAnteriores() throws Exception {
        if (anteriores == null) {
            anteriores = new MultiMap<>();
        }
        anteriores.clear();
        //Cria a tabela se não existir;
        createTable();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ant.imp_sistema,\n"
                    + "	ant.imp_loja,\n"
                    + "	ant.ant_merc1,\n"
                    + "	ant.ant_merc2,\n"
                    + "	ant.ant_merc3,\n"
                    + "	ant.ant_merc4,\n"
                    + "	ant.ant_merc5,\n"
                    + "	coalesce(m.id,-1) codigoatual,\n"
                    + "	m.mercadologico1,\n"
                    + "	m.mercadologico2,\n"
                    + "	m.mercadologico3,\n"
                    + "	m.mercadologico4,\n"
                    + "	m.mercadologico5,\n"
                    + "	m.descricao,\n"
                    + "	m.nivel\n"
                    + "from\n"
                    + "	implantacao.codant_mercadologico ant\n"
                    + "	left join mercadologico m on\n"
                    + "       ant.merc1 = m.mercadologico1 and\n"
                    + "       ant.merc2 = m.mercadologico2 and\n"
                    + "       ant.merc3 = m.mercadologico3 and\n"
                    + "       ant.merc4 = m.mercadologico4 and\n"
                    + "       ant.merc5 = m.mercadologico5\n"
                    + "order by\n"
                    + "	ant.imp_sistema,\n"
                    + "	ant.imp_loja,\n"
                    + "	ant.ant_merc1,\n"
                    + "	ant.ant_merc2,\n"
                    + "	ant.ant_merc3,\n"
                    + "	ant.ant_merc4,\n"
                    + "	ant.ant_merc5"
            )) {
                while (rst.next()) {
                    MercadologicoVO vo = null;

                    //Caso não exista um mercadológico mapeado inclui null.
                    if (rst.getInt("codigoatual") > 0) {
                        vo = new MercadologicoVO();
                        vo.setId(rst.getInt("codigoatual"));
                        vo.setMercadologico1(rst.getInt("mercadologico1"));
                        vo.setMercadologico2(rst.getInt("mercadologico2"));
                        vo.setMercadologico3(rst.getInt("mercadologico3"));
                        vo.setMercadologico4(rst.getInt("mercadologico4"));
                        vo.setMercadologico5(rst.getInt("mercadologico5"));
                        vo.setDescricao(rst.getString("descricao"));
                        vo.setNivel(rst.getInt("nivel"));
                    }

                    anteriores.put(vo,
                            rst.getString("imp_sistema"),
                            rst.getString("imp_loja"),
                            rst.getString("ant_merc1"),
                            rst.getString("ant_merc2"),
                            rst.getString("ant_merc3"),
                            rst.getString("ant_merc4"),
                            rst.getString("ant_merc5")
                    );
                }
            }
        }
        aAcertar = getAAcertar();
    }

    /**
     * Organiza os mercadológicos, eliminando duplicados e criando os cabeçalhos
     * de nível.
     *
     * @param mercadologicos Lista de mercadológicos a serem organizadas.
     * @return MultiMap com os mercadológicos tratados.
     */
    private MultiMap<String, MercadologicoAuxiliar> organizarMercadologico(List<MercadologicoIMP> mercadologicos) {
        MultiMap<String, MercadologicoAuxiliar> result = new MultiMap<>();

        //<editor-fold defaultstate="collapsed" desc="Elimina os mercadológicos duplicados">        
        MultiMap<String, MercadologicoIMP> unico = new MultiMap<>();
        for (MercadologicoIMP merc : mercadologicos) {            
            merc.setImportSistema(getSistema());
            unico.put(merc,
                    merc.getImportSistema(),
                    merc.getImportLoja(),
                    merc.getMerc1ID(),
                    merc.getMerc2ID(),
                    merc.getMerc3ID(),
                    merc.getMerc4ID(),
                    merc.getMerc5ID()
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Cria na listagem o nivel 1 do mercadológico">
        for (MercadologicoIMP merc : unico.values()) {
            if (!"".equals(merc.getMerc1ID())) {
                merc.setImportSistema(getSistema());
                result.put(
                        new MercadologicoAuxiliar(1, merc.getMerc1Descricao()),
                        merc.getImportSistema(),
                        merc.getImportLoja(),
                        merc.getMerc1ID(),
                        "",
                        "",
                        "",
                        "");
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Cria na listagem o nivel 2 do mercadológico">
        for (MercadologicoIMP merc : unico.values()) {
            merc.setImportSistema(getSistema());
            if (!"".equals(merc.getMerc2ID())) {
                result.put(
                        new MercadologicoAuxiliar(2, merc.getMerc2Descricao()),
                        merc.getImportSistema(),
                        merc.getImportLoja(),
                        merc.getMerc1ID(),
                        merc.getMerc2ID(),
                        "",
                        "",
                        "");
            } else {
                if (!"".equals(merc.getMerc1ID().trim())) {
                    MercadologicoAuxiliar descricao;
                    if (!"".equals(merc.getMerc2ID().trim())) {
                        descricao = new MercadologicoAuxiliar(2, merc.getMerc2Descricao());
                    } else {
                        descricao = new MercadologicoAuxiliar(2, merc.getMerc1Descricao());
                    }
                    result.put(
                            descricao,
                            merc.getImportSistema(),
                            merc.getImportLoja(),
                            merc.getMerc1ID(),
                            "1",
                            "",
                            "",
                            "");
                }
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Cria na listagem o nivel 3 do mercadológico">
        for (MercadologicoIMP merc : unico.values()) {
            merc.setImportSistema(getSistema());
            if (!"".equals(merc.getMerc3ID())) {
                result.put(
                        new MercadologicoAuxiliar(3, merc.getMerc3Descricao()),
                        merc.getImportSistema(),
                        merc.getImportLoja(),
                        merc.getMerc1ID(),
                        merc.getMerc2ID(),
                        merc.getMerc3ID(),
                        "",
                        "");
            } else {
                if (!"".equals(merc.getMerc1ID().trim()) || !"".equals(merc.getMerc2ID().trim())) {
                    MercadologicoAuxiliar descricao;
                    if (!"".equals(merc.getMerc3ID().trim())) {
                        descricao = new MercadologicoAuxiliar(3, merc.getMerc3Descricao());
                    } else if (!"".equals(merc.getMerc2ID().trim())) {
                        descricao = new MercadologicoAuxiliar(3, merc.getMerc2Descricao());
                    } else {
                        descricao = new MercadologicoAuxiliar(3, merc.getMerc1Descricao());
                    }
                    result.put(
                            descricao,
                            merc.getImportSistema(),
                            merc.getImportLoja(),
                            merc.getMerc1ID(),
                            !"".equals(merc.getMerc2ID().trim()) ? merc.getMerc2ID() : "1",
                            "1",
                            "",
                            "");
                }
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Cria na listagem o nivel 4 do mercadológico">
        boolean existeNivel4 = false;
        for (MercadologicoIMP merc : unico.values()) {
            existeNivel4 |= !"".equals(merc.getMerc4ID());
        }

        if (existeNivel4) {
            for (MercadologicoIMP merc : unico.values()) {
                merc.setImportSistema(getSistema());
                if (!"".equals(merc.getMerc4ID())) {
                    result.put(
                            new MercadologicoAuxiliar(4, merc.getMerc4Descricao()),
                            merc.getImportSistema(),
                            merc.getImportLoja(),
                            merc.getMerc1ID(),
                            merc.getMerc2ID(),
                            merc.getMerc3ID(),
                            merc.getMerc4ID(),
                            "");
                } else {
                    if (!"".equals(merc.getMerc1ID().trim()) || !"".equals(merc.getMerc2ID().trim()) || !"".equals(merc.getMerc3ID().trim())) {
                        MercadologicoAuxiliar descricao;
                        if (!"".equals(merc.getMerc4ID().trim())) {
                            descricao = new MercadologicoAuxiliar(4, merc.getMerc4Descricao());
                        } else if (!"".equals(merc.getMerc3ID().trim())) {
                            descricao = new MercadologicoAuxiliar(4, merc.getMerc3Descricao());
                        } else if (!"".equals(merc.getMerc2ID().trim())) {
                            descricao = new MercadologicoAuxiliar(4, merc.getMerc2Descricao());
                        } else {
                            descricao = new MercadologicoAuxiliar(4, merc.getMerc1Descricao());
                        }
                        result.put(
                                descricao,
                                merc.getImportSistema(),
                                merc.getImportLoja(),
                                merc.getMerc1ID(),
                                !"".equals(merc.getMerc2ID().trim()) ? merc.getMerc2ID() : "1",
                                !"".equals(merc.getMerc3ID().trim()) ? merc.getMerc3ID() : "1",
                                "1",
                                "");
                    }
                }
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Cria na listagem o nivel 5 do mercadológico">
        boolean existeNivel5 = false;
        for (MercadologicoIMP merc : unico.values()) {
            existeNivel5 |= !"".equals(merc.getMerc5ID());
        }
        if (existeNivel5) {
            for (MercadologicoIMP merc : unico.values()) {
                merc.setImportSistema(getSistema());
                if (!"".equals(merc.getMerc5ID())) {
                    result.put(
                            new MercadologicoAuxiliar(5, merc.getMerc5Descricao()),
                            merc.getImportSistema(),
                            merc.getImportLoja(),
                            merc.getMerc1ID(),
                            merc.getMerc2ID(),
                            merc.getMerc3ID(),
                            merc.getMerc4ID(),
                            merc.getMerc5ID());
                } else {
                    if (!"".equals(merc.getMerc1ID().trim()) || !"".equals(merc.getMerc2ID().trim()) || !"".equals(merc.getMerc3ID().trim()) || !"".equals(merc.getMerc4ID().trim())) {
                        MercadologicoAuxiliar descricao;
                        if (!"".equals(merc.getMerc5ID().trim())) {
                            descricao = new MercadologicoAuxiliar(5, merc.getMerc5Descricao());
                        } else if (!"".equals(merc.getMerc4ID().trim())) {
                            descricao = new MercadologicoAuxiliar(5, merc.getMerc4Descricao());
                        } else if (!"".equals(merc.getMerc3ID().trim())) {
                            descricao = new MercadologicoAuxiliar(5, merc.getMerc3Descricao());
                        } else if (!"".equals(merc.getMerc2ID().trim())) {
                            descricao = new MercadologicoAuxiliar(5, merc.getMerc2Descricao());
                        } else {
                            descricao = new MercadologicoAuxiliar(5, merc.getMerc1Descricao());
                        }
                        result.put(descricao,
                                merc.getImportSistema(),
                                merc.getImportLoja(),
                                merc.getMerc1ID(),
                                !"".equals(merc.getMerc2ID().trim()) ? merc.getMerc2ID() : "1",
                                !"".equals(merc.getMerc3ID().trim()) ? merc.getMerc3ID() : "1",
                                !"".equals(merc.getMerc4ID().trim()) ? merc.getMerc4ID() : "1",
                                "1");
                    }
                }
            }
        }
        //</editor-fold>

        return result;
    }

    /**
     * Limpa as listagens do mercadológico.
     *
     * @throws Exception
     */
    public void apagarMercadologico() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "delete from mercadologico;"
                    + "drop table if exists implantacao.codant_mercadologico;"
                    + "alter sequence mercadologico_id_seq restart with 1;");
        }
    }

    /**
     * Retorna os dados do mercadológico 'A ACERTAR' do VR.
     *
     * @return Informações do mercadológico.
     * @throws Exception
     */
    public MercadologicoVO getAAcertar() throws Exception {
        MercadologicoVO result = null;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from mercadologico where descricao like '%ACERTAR%' and nivel = ( select max(nivel) from mercadologico where descricao like '%ACERTAR%' ) limit 1"
            )) {
                if (rst.next()) {
                    result = new MercadologicoVO();
                    result.setId(rst.getInt("id"));
                    result.setMercadologico1(rst.getInt("mercadologico1"));
                    result.setMercadologico2(rst.getInt("mercadologico2"));
                    result.setMercadologico3(rst.getInt("mercadologico3"));
                    result.setMercadologico4(rst.getInt("mercadologico4"));
                    result.setMercadologico5(rst.getInt("mercadologico5"));
                    result.setDescricao(rst.getString("descricao"));
                    result.setNivel(rst.getInt("nivel"));
                }
            }
        }
        return result;
    }

    public MercadologicoVO getMercadologico(String... chaves) throws Exception {
        MercadologicoVO mercadologico = getCodigoAnterior().get(chaves);
        if (mercadologico == null) {
            LOG.finest(Arrays.toString(chaves) + " mercadológico não encontrado");
            if (aAcertar == null) {
                aAcertar = getAAcertar();
                LOG.finer("A Acertar localizado " + aAcertar.toString());
            }
            mercadologico = aAcertar;
        }
        return mercadologico;
    }

    public int getNivelMaximoMercadologico() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select coalesce(max(nivel),1) merc from mercadologico"
            )) {
                rst.next();
                return rst.getInt("merc");
            }
        }
    }

    /**
     * Classe utilizada auxiliar na importação.
     */
    private static class MercadologicoAuxiliar {

        public int nivel;
        public String descricao;

        public MercadologicoAuxiliar(int nivel, String descricao) {
            this.nivel = nivel;
            this.descricao = descricao;
        }
    }

    /**
     * Grava uma listagem de mercadológicos no VR.
     *
     * @param mercadologicos Lista de mercadológicos.
     * @param opt
     * @throws Exception
     */
    public void salvarMerc1(List<MercadologicoIMP> mercadologicos, Set<OpcaoProduto> opt) throws Exception {
        //Organizaria o mercadologico informado, deixando pronto para incluir;       
        MultiMap<String, MercadologicoAuxiliar> organizados = organizarMercadologico(mercadologicos);

        Conexao.begin();
        try {
            //Cria a tabela;
            createTable();

            int nivelMax = gravarCodigosAnteriores(organizados, opt);

            try (Statement stm = Conexao.createStatement()) {
                ProgressBar.setStatus("Gravando os mercadológicos nivel 1...");
                ProgressBar.setMaximum(mercadologicos.size());
                for (KeyList<String> imp : getCodigoAnterior().keySet()) {
                    String[] chave = imp.toArray();
                    MercadologicoVO vo = getCodigoAnterior().get(chave);
                    MercadologicoAuxiliar item = organizados.get(chave);

                    if (vo == null && item != null) {
                        vo = new MercadologicoVO();
                        vo.setDescricao(item.descricao);
                        vo.setNivel(item.nivel);
                        String sql;

                        sql = "insert into mercadologico ("
                                + "mercadologico1,"
                                + "mercadologico2,"
                                + "mercadologico3,"
                                + "mercadologico4,"
                                + "mercadologico5,"
                                + "descricao,"
                                + "nivel"
                                + ") values ("
                                + "(select id from generate_series(1,999) s(id) except select mercadologico1 from mercadologico where nivel = 1 order by id limit 1),"
                                + "0,"
                                + "0,"
                                + "0,"
                                + "0,"
                                + Utils.quoteSQL(vo.getDescricao()) + ","
                                + vo.getNivel() + ") returning "
                                + "id,"
                                + "mercadologico1,"
                                + "mercadologico2,"
                                + "mercadologico3,"
                                + "mercadologico4,"
                                + "mercadologico5;";

                        LOG.fine("SQL: " + sql);
                        
                        try (ResultSet rst = stm.executeQuery(sql)) {
                            rst.next();
                            vo.setId(rst.getInt("id"));
                            vo.setMercadologico1(rst.getInt("mercadologico1"));
                            vo.setMercadologico2(rst.getInt("mercadologico2"));
                            vo.setMercadologico3(rst.getInt("mercadologico3"));
                            vo.setMercadologico4(rst.getInt("mercadologico4"));
                            vo.setMercadologico5(rst.getInt("mercadologico5"));

                            String sqlCodigoAnterior = "update implantacao.codant_mercadologico set "
                                    + "merc1 = " + vo.getMercadologico1() + ","
                                    + "merc2 = " + vo.getMercadologico2() + ","
                                    + "merc3 = " + vo.getMercadologico3() + ","
                                    + "merc4 = " + vo.getMercadologico4() + ","
                                    + "merc5 = " + vo.getMercadologico5()
                                    + "where "
                                    + "imp_sistema = " + Utils.quoteSQL(getSistema()) + " and "
                                    + "imp_loja = " + Utils.quoteSQL(chave[1]) + " and "
                                    + "ant_merc1 = " + Utils.quoteSQL(chave[2]) + " and "
                                    + "ant_merc2 = " + Utils.quoteSQL(chave[3]) + " and "
                                    + "ant_merc3 = " + Utils.quoteSQL(chave[4]) + " and "
                                    + "ant_merc4 = " + Utils.quoteSQL(chave[5]) + " and "
                                    + "ant_merc5 = " + Utils.quoteSQL(chave[6]) + ";";

                            stm.execute(sqlCodigoAnterior);
                            getCodigoAnterior().put(vo, chave);
                        }

                    }
                    ProgressBar.next();
                }
            }

            gerarAAcertar(nivelMax);

            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public int getIdLojaVR() {
        return idLojaVR;
    }

    public void setIdLojaVR(int idLojaVR) {
        this.idLojaVR = idLojaVR;
    }
}
