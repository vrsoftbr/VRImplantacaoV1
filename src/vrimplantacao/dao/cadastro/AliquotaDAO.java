package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vr.database.SQLBuilder;
import vrimplantacao.vo.cadastro.SituacaoCadastro;
import vrimplantacao.vo.cadastro.SituacaoTributaria;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.tributacao.AliquotaVO;

public class AliquotaDAO {

    public int getId(int i_situacaoTributaria, double i_porcentagem, double i_reduzido) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id FROM aliquota WHERE ROUND(porcentagem, 2) = " + Util.round(i_porcentagem, 2) + " AND ROUND(reduzido, 2) = " + Util.round(i_reduzido, 2) + " AND situacaotributaria = " + i_situacaoTributaria + " AND id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }

    public int getId(double i_porcentagem, double i_reduzido) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id FROM aliquota WHERE ROUND(porcentagem, 2) = " + Util.round(i_porcentagem, 2) + " AND ROUND(reduzido, 2) = " + Util.round(i_reduzido, 2) + " AND id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }

    public int getId(double i_porcentagem) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();
        sql = new StringBuilder();
        sql.append("SELECT id FROM aliquota WHERE ROUND(porcentagem, 2) = " + Util.round(i_porcentagem, 2));
        sql.append(" AND id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());
        sql.append(" ORDER BY porcentagem, reduzido");

        rst = stm.executeQuery(sql.toString());

        int idAliquota = -1;

        if (rst.next()) {
            idAliquota = rst.getInt("id");
        }

        stm.close();

        return idAliquota;
    }

    public int getIdOutras() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM aliquota WHERE situacaotributaria = " + SituacaoTributaria.OUTRAS.getId() + " AND id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Alíquota não encontrada!");
        }

        return rst.getInt("id");
    }

    public int getIdSubstituido() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM aliquota WHERE situacaotributaria = " + SituacaoTributaria.SUBSTITUIDO.getId() + " AND id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Alíquota não encontrada!");
        }

        return rst.getInt("id");
    }

    public int getIdIsento() throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM aliquota WHERE situacaotributaria = " + SituacaoTributaria.ISENTO.getId() + " AND id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Alíquota não encontrada!");
        }

        return rst.getInt("id");
    }

    public boolean isSubstituido(int i_situacaoTributaria) throws Exception {
        if (i_situacaoTributaria == SituacaoTributaria.SUBSTITUIDO.getId() || i_situacaoTributaria == SituacaoTributaria.REDUCAO_BASE_CALCULO_ICMS_ST.getId() || i_situacaoTributaria == SituacaoTributaria.TRIBUTADO_ICMS_ST.getId() || i_situacaoTributaria == SituacaoTributaria.ISENTO_ICMS_ST.getId()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIsento(int i_situacaoTributaria) throws Exception {
        if (i_situacaoTributaria == SituacaoTributaria.ISENTO.getId() || i_situacaoTributaria == SituacaoTributaria.NAO_TRIBUTADO.getId() || i_situacaoTributaria == SituacaoTributaria.SUSPENSAO.getId()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isOutras(int i_situacaoTributaria) throws Exception {
        if (i_situacaoTributaria == SituacaoTributaria.OUTRAS.getId() || i_situacaoTributaria == SituacaoTributaria.DIFERIMENTO.getId()) {
            return true;
        } else {
            return false;
        }
    }

    public int aliquota(int cst, double aliquota, double reduzido, String descricao, boolean gerarAliquotaPdv, double aliquotaFCP) throws Exception {        
        int result = -1;
        
        Conexao.begin();
        try {
            try (Statement stm = Conexao.createStatement()) {
                stm.execute(
                        "do $$\n" +
                        "declare\n" +
                        "	r record;\n" +
                        "	vid integer = -1;\n" +
                        "	vidpdv integer;\n" +
                        "	v_aliquotafinal numeric(11,2);\n" +
                        "\n" +
                        "	rcst integer = " + cst + ";\n" +
                        "	raliquota numeric(11,2) = " + String.format("%.2f", aliquota).replace(",", ".") + ";\n" +        
                        "	rreduzido numeric(13,3) = " + String.format("%.2f", reduzido).replace(",", ".") + ";\n" +
                        "	raliquotafcp numeric(13,3) = " + String.format("%.2f", aliquotaFCP).replace(",", ".") + ";\n" +        
                        "	rdescricao varchar(15) = '" + descricao + "';\n" +
                        "	rgeraaliquotapdv boolean = " + gerarAliquotaPdv + ";\n" +        
                        "begin\n" +
                        "		v_aliquotafinal = round(raliquota * ((100 - rreduzido) / 100), 2);\n" +
                        "		\n" +
                        "		select coalesce(max(id) + 1, 1) from aliquota into vid;\n" +
                        "		insert into aliquota (id, descricao, reduzido, porcentagem, id_situacaocadastro, situacaotributaria, id_aliquotapdv, mensagemnf, csosn, porcentagemfinal, porcentagemfcp) \n" +
                        "		values (vid, rdescricao, rreduzido, raliquota, 1, rcst, null, '' , 101, v_aliquotafinal, raliquotafcp);\n" +
                        "\n" +
                        "		if (rgeraaliquotapdv) then\n" +
                        "			select coalesce(max(id) + 1, 1) from pdv.aliquota into vidpdv;\n" +
                        "			insert into pdv.aliquota (id, descricao, porcentagem, id_aliquota)\n" +
                        "			values (vidpdv, rdescricao, v_aliquotafinal, vid);\n" +
                        "\n" +
                        "			update aliquota set id_aliquotapdv = vidpdv where id = vid;\n" +
                        "		end if;\n" +
                        "\n" +
                        "	create temp table tp_aliq on commit drop as select vid id;\n" +
                        "end;\n" +
                        "$$;"
                );
                try (ResultSet rst = stm.executeQuery(
                        "select id from tp_aliq"                    
                )) {
                    while (rst.next()) {
                        result = rst.getInt("id");
                    }
                }
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
        
        return result;
    }

    public int insert(AliquotaVO aliquota) throws Exception {
        SQLBuilder sql = new SQLBuilder("public", "aliquota")
                .putSql("id", "(select coalesce(max(id) + 1, 1) from aliquota)")
                .put("descricao", aliquota.getDescricao())
                .put("situacaotributaria", aliquota.getCst())
                .put("porcentagem", aliquota.getAliquota())
                .put("reduzido", aliquota.getReduzido())
                .put("porcentagemfinal", aliquota.getAliquotaFinal())
                .put("porcentagemfcp", aliquota.getFcp())
                .put("icmsdesonerado", aliquota.isDesonerado())
                .put("percentualicmsdesonerado", aliquota.getPorcentagemDesonerado())
                .put("id_situacaocadastro", 1)
                .putNull("id_aliquotapdv")
                .put("mensagemnf", "")
                .put("csosn", 101)
                .returning("id");
        try (
                Statement st = Conexao.createStatement();
                ResultSet rs = st.executeQuery(
                        sql.insert()
                )
        ) {
            rs.next();
            return rs.getInt("id");
                
        }
    }

    public MultiMap<Comparable, Integer> getIdAliquotasPorCstAliqReduz() throws Exception {
        MultiMap<Comparable, Integer> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id,\n" +
                    "	situacaotributaria,\n" +
                    "	porcentagem,\n" +
                    "	reduzido\n" +
                    "from\n" +
                    "	aliquota\n" +
                    "where\n" +
                    "	id_situacaocadastro = 1\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    result.put(
                        rst.getInt("id"),
                        rst.getInt("situacaotributaria"),
                        MathUtils.trunc(rst.getDouble("porcentagem"), 1),
                        MathUtils.trunc(rst.getInt("reduzido"), 1)
                    );
                }
            }
        }
        
        return result;
    }

    public Map<String, Integer> getAliquotaPorValor() throws Exception {
        Map<String, Integer> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id,\n" +
                    "	situacaotributaria cst,\n" +
                    "	case when situacaotributaria in (40,41,50,51,60) then 0 else porcentagem end aliquota,\n" +
                    "	case when situacaotributaria in (40,41,50,51,60) then 0 else reduzido end reduzido\n" +
                    "from\n" +
                    "	aliquota\n" +
                    "where\n" +
                    "	id_situacaocadastro = 1"
            )) {
                while (rst.next()) {
                    result.put(
                            String.format("%d-%.2f-%.2f", rst.getInt("cst"), rst.getDouble("aliquota"), rst.getDouble("reduzido")),
                            rst.getInt("id")
                    );
                }
            }
        }
        
        return result;
    }
}
