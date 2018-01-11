package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.vo.cadastro.SituacaoCadastro;
import vrimplantacao.vo.cadastro.SituacaoTributaria;
import vrimplantacao.vo.interfaces.AliquotaVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class AliquotaDAO {

    public AliquotaVO carregar(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT * FROM aliquota WHERE id = " + i_id);

        if (!rst.next()) {
            throw new VRException("Alíquota " + i_id + " não encontrada!");
        }

        AliquotaVO oAliquota = new AliquotaVO();
        oAliquota.descricao = rst.getString("descricao");
        oAliquota.id = rst.getInt("id");
        oAliquota.situacaoTributaria = rst.getInt("situacaotributaria");
        oAliquota.porcentagem = rst.getDouble("porcentagem");
        oAliquota.reduzido = rst.getDouble("reduzido");

        stm.close();

        return oAliquota;
    }

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
}
