package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.vo.cadastro.ContratoAcordoExcecaoLojaVO;
import vrimplantacao.vo.cadastro.ContratoAcordoVO;
import vrimplantacao.vo.cadastro.ContratoEscalaCrescimentoVO;
import vrimplantacao.vo.cadastro.ContratoFornecedorExcecaoAcordoVO;
import vrimplantacao.vo.cadastro.ContratoFornecedorExcecaoVO;
import vrimplantacao.vo.cadastro.ContratoFornecedorVO;
import vrimplantacao.vo.cadastro.ContratoVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class ContratoDAO {

    public ContratoVO carregar(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT * FROM contrato WHERE id = " + i_id);

        if (!rst.next()) {
            throw new VRException(Util.MSG_REGISTRO_NAO_ENCONTRADO);
        }

        ContratoVO oContrato = new ContratoVO();
        oContrato.id = rst.getInt("id");
        oContrato.idTipoRecebimento = rst.getInt("id_tiporecebimento");
        oContrato.idComprador = rst.getInt("id_comprador");
        oContrato.imprimeDesconto = rst.getBoolean("imprimedesconto");
        oContrato.idSituacaoCadastro = rst.getInt("id_situacaocadastro");
        oContrato.dataInicio = Util.formatDataGUI(rst.getDate("datainicio"));
        oContrato.dataTermino = Util.formatDataGUI(rst.getDate("datatermino"));
        try {
            oContrato.diaVencimento = rst.getInt("diavencimento");
        } catch (Exception e) {
            oContrato.diaVencimento = 0;
        }
        oContrato.abatimentoPisCofins = rst.getBoolean("abatimentopiscofins");
        oContrato.abatimentoIpi = rst.getBoolean("abatimentoipi");
        oContrato.abatimentoIcms = rst.getBoolean("abatimentoicms");
        oContrato.abatimentoIcmsRetido = rst.getBoolean("abatimentoicmsretido");
        oContrato.tipoApuracao = rst.getObject("tipoapuracao") == null ? -1 : rst.getInt("tipoapuracao");
        oContrato.dataInicioApuracao = rst.getDate("datainicioapuracao") == null ? "" : Util.formatDataGUI(rst.getDate("datainicioapuracao"));

        sql = new StringBuilder();
        sql.append("SELECT ca.*, ta.descricao AS tipoacordo");
        sql.append(" FROM contratoacordo AS ca");
        sql.append(" INNER JOIN tipoacordo AS ta ON ta.id = ca.id_tipoacordo WHERE ca.id_contrato = " + oContrato.id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            ContratoAcordoVO oAcordo = new ContratoAcordoVO();
            oAcordo.id = rst.getLong("id");
            oAcordo.idTipoAcordo = rst.getInt("id_tipoacordo");
            oAcordo.tipoAcordo = rst.getString("tipoacordo");
            oAcordo.percentual = rst.getDouble("percentual");

            oContrato.vAcordo.add(oAcordo);
        }

        for (ContratoAcordoVO oAcordo : oContrato.vAcordo) {
            sql = new StringBuilder();
            sql.append("SELECT l.id, l.descricao, (ex.id IS NOT NULL) AS selecionado");
            sql.append(" FROM loja l");
            sql.append(" LEFT JOIN contratoacordoexcecaoloja ex ON ex.id_loja = l.id AND ex.id_contratoacordo = " + oAcordo.id);
            sql.append(" ORDER BY l.descricao");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                ContratoAcordoExcecaoLojaVO oExcecao = new ContratoAcordoExcecaoLojaVO();
                oExcecao.idLoja = rst.getInt("id");
                oExcecao.loja = rst.getString("descricao");
                oExcecao.selecionado = rst.getBoolean("selecionado");

                oAcordo.vExcecao.add(oExcecao);
            }
        }

        sql = new StringBuilder();
        sql.append("SELECT cf.*, f.razaosocial as fornecedor, fc.razaosocial as fornecedorcobranca FROM contratofornecedor AS cf");
        sql.append(" INNER JOIN fornecedor as f on f.id = cf.id_fornecedor");
        sql.append(" INNER JOIN fornecedor as fc on fc.id = cf.id_fornecedorcobranca WHERE cf.id_contrato = " + oContrato.id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            ContratoFornecedorVO oFornecedor = new ContratoFornecedorVO();
            oFornecedor.id = rst.getLong("id");
            oFornecedor.idFornecedor = rst.getInt("id_fornecedor");
            oFornecedor.fornecedor = rst.getString("fornecedor");
            oFornecedor.idFornecedorCobranca = rst.getInt("id_fornecedorcobranca");
            oFornecedor.fornecedorCobranca = rst.getString("fornecedorcobranca");
            oFornecedor.vExcecao = new ContratoDAO().carregarFornecedorExcecao(rst.getLong("id"));

            oContrato.vFornecedor.add(oFornecedor);
        }

        sql = new StringBuilder();
        sql.append("SELECT cec.* FROM contratoescalacrescimento AS cec");
        sql.append(" WHERE cec.id_contrato = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            ContratoEscalaCrescimentoVO oEscala = new ContratoEscalaCrescimentoVO();
            oEscala.regra = rst.getString("regra");
            oEscala.percentual = rst.getDouble("percentual");

            oContrato.vEscala.add(oEscala);
        }

        stm.close();

        return oContrato;
    }

    private List<ContratoFornecedorExcecaoVO> carregarFornecedorExcecao(long i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT cfe.*, p.descricaocompleta");
        sql.append(" FROM contratofornecedorexcecao AS cfe");
        sql.append(" INNER JOIN produto AS p on p.id = cfe.id_produto");
        sql.append(" WHERE id_contratofornecedor = " + i_id);

        rst = stm.executeQuery(sql.toString());

        List<ContratoFornecedorExcecaoVO> vContratoFornecedorExcecao = new ArrayList();

        while (rst.next()) {
            ContratoFornecedorExcecaoVO oFornecedorExcecao = new ContratoFornecedorExcecaoVO();
            oFornecedorExcecao.id = rst.getLong("id");
            oFornecedorExcecao.idProduto = rst.getInt("id_produto");
            oFornecedorExcecao.produto = rst.getString("descricaocompleta");
            oFornecedorExcecao.vAcordo = new ContratoDAO().carregarFornecedorExcecaoAcordo(rst.getLong("id"));

            vContratoFornecedorExcecao.add(oFornecedorExcecao);
        }

        stm.close();

        return vContratoFornecedorExcecao;
    }

    private List<ContratoFornecedorExcecaoAcordoVO> carregarFornecedorExcecaoAcordo(long i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT cfea.*, ta.descricao as tipoacordo");
        sql.append(" FROM contratofornecedorexcecaoacordo AS cfea");
        sql.append(" INNER JOIN tipoacordo AS ta ON ta.id = cfea.id_tipoacordo");
        sql.append(" WHERE id_contratofornecedorexcecao = " + i_id);

        rst = stm.executeQuery(sql.toString());

        List<ContratoFornecedorExcecaoAcordoVO> vContratoFornecedorExcecaoAcordo = new ArrayList();

        while (rst.next()) {
            ContratoFornecedorExcecaoAcordoVO oFornecedorExcecaoAcordo = new ContratoFornecedorExcecaoAcordoVO();
            oFornecedorExcecaoAcordo.idTipoAcordo = rst.getInt("id_tipoacordo");
            oFornecedorExcecaoAcordo.tipoAcordo = rst.getString("tipoacordo");
            oFornecedorExcecaoAcordo.percentual = rst.getDouble("percentual");

            vContratoFornecedorExcecaoAcordo.add(oFornecedorExcecaoAcordo);
        }

        stm.close();

        return vContratoFornecedorExcecaoAcordo;
    }
}
