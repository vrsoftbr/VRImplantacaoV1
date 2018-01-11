package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.ParametroDAO;
import vrimplantacao.vo.administrativo.ReceitaItemVO;
import vrimplantacao.vo.administrativo.ReceitaProdutoVO;
import vrimplantacao.vo.administrativo.ReceitaVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class ReceitaDAO {

    public int verificar(int i_idProduto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id_receita FROM receitaproduto WHERE id_produto = " + i_idProduto);

        if (rst.next()) {
            return rst.getInt("id_receita");
        } else {
            return -1;
        }
    }

    public ReceitaVO carregar(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        boolean utilizaCustoMedio = new ParametroDAO().get(217).getBoolean();

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT * FROM receita WHERE id = " + i_id);

        if (!rst.next()) {
            throw new VRException("Receita não encontrada!");
        }

        ReceitaVO oReceita = new ReceitaVO();
        oReceita.id = rst.getInt("id");
        oReceita.descricao = rst.getString("descricao");
        oReceita.idSituacaoCadastro = rst.getInt("id_situacaocadastro");

        sql = new StringBuilder();
        sql.append("SELECT rp.id, rp.id_receita, rp.id_produto, rp.rendimento, p.descricaocompleta AS descricao");
        sql.append(" FROM receitaproduto AS rp");
        sql.append(" INNER JOIN produto as p on p.id = rp.id_produto");
        sql.append(" WHERE rp.id_receita = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            ReceitaProdutoVO oReceitaProduto = new ReceitaProdutoVO();
            oReceitaProduto.idProduto = rst.getInt("id_produto");
            oReceitaProduto.produto = rst.getString("descricao");
            oReceitaProduto.rendimento = rst.getDouble("rendimento");

            oReceita.vProduto.add(oReceitaProduto);
        }

        sql = new StringBuilder();
        sql.append("SELECT ri.id, ri.id_receita, ri.id_produto, ri.qtdembalagemproduto, ri.fatorconversao, ri.qtdembalagemreceita, ri.baixaestoque,");
        sql.append(" p.descricaocompleta AS descricao, p.id_tipoembalagem,");

        if (utilizaCustoMedio) {
            sql.append(" pc.customediocomimposto AS custocomimposto");
        } else {
            sql.append(" pc.custocomimposto");
        }

        sql.append(" FROM receitaitem AS ri");
        sql.append(" INNER JOIN produto as p on p.id = ri.id_produto");
        sql.append(" INNER JOIN produtocomplemento as pc on pc.id_produto = p.id and pc.id_loja = " + Global.idLoja);
        sql.append(" WHERE ri.id_receita = " + i_id);
        sql.append(" ORDER BY p.descricaocompleta");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            ReceitaItemVO oReceitaItem = new ReceitaItemVO();
            oReceitaItem.idProduto = rst.getInt("id_produto");
            oReceitaItem.produto = rst.getString("descricao");
            oReceitaItem.qtdEmbalagemProduto = rst.getInt("qtdembalagemproduto");
            oReceitaItem.qtdEmbalagemReceita = rst.getInt("qtdembalagemreceita");
            oReceitaItem.custoEmbalagem = rst.getDouble("custocomimposto");
            oReceitaItem.fatorConversao = rst.getDouble("fatorconversao");
            oReceitaItem.custoFinal = Util.round(((rst.getDouble("custocomimposto") * oReceitaItem.qtdEmbalagemReceita) / oReceitaItem.qtdEmbalagemProduto) / oReceitaItem.fatorConversao, 3);
            oReceitaItem.baixaEstoque = rst.getBoolean("baixaestoque");
            oReceitaItem.idTipoEmbalagem = rst.getInt("id_tipoembalagem");

            oReceita.vItem.add(oReceitaItem);
        }

        stm.close();

        return oReceita;
    }
    
    public ReceitaVO carregar(int i_id, int idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT * FROM receita WHERE id = " + i_id);

        if (!rst.next()) {
            throw new VRException("Receita não encontrada!");
        }

        ReceitaVO oReceita = new ReceitaVO();
        oReceita.id = rst.getInt("id");
        oReceita.descricao = rst.getString("descricao");
        oReceita.idSituacaoCadastro = rst.getInt("id_situacaocadastro");

        sql = new StringBuilder();
        sql.append("SELECT rp.id, rp.id_receita,rp.id_produto,rp.rendimento,p.descricaocompleta as descricao");
        sql.append(" FROM receitaproduto AS rp");
        sql.append(" INNER JOIN produto as p on p.id = rp.id_produto");
        sql.append(" WHERE rp.id_receita = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            ReceitaProdutoVO oReceitaProduto = new ReceitaProdutoVO();
            oReceitaProduto.idProduto = rst.getInt("id_produto");
            oReceitaProduto.produto = rst.getString("descricao");
            oReceitaProduto.rendimento = rst.getDouble("rendimento");

            oReceita.vProduto.add(oReceitaProduto);
        }

        sql = new StringBuilder();
        sql.append("SELECT ri.id, ri.id_receita, ri.id_produto, ri.qtdembalagemproduto, ri.fatorconversao, ri.qtdembalagemreceita, ri.baixaestoque,");
        sql.append(" p.descricaocompleta AS descricao, pc.custocomimposto, p.id_tipoembalagem");
        sql.append(" FROM receitaitem AS ri");
        sql.append(" INNER JOIN produto as p on p.id = ri.id_produto");
        sql.append(" INNER JOIN produtocomplemento as pc on pc.id_produto = p.id and pc.id_loja = " + idLoja);
        sql.append(" WHERE ri.id_receita = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            ReceitaItemVO oReceitaItem = new ReceitaItemVO();
            oReceitaItem.idProduto = rst.getInt("id_produto");
            oReceitaItem.produto = rst.getString("descricao");
            oReceitaItem.qtdEmbalagemProduto = rst.getInt("qtdembalagemproduto");
            oReceitaItem.qtdEmbalagemReceita = rst.getInt("qtdembalagemreceita");
            oReceitaItem.custoEmbalagem = rst.getDouble("custocomimposto");
            oReceitaItem.fatorConversao = rst.getDouble("fatorconversao");
            oReceitaItem.custoFinal = Util.round(((rst.getDouble("custocomimposto") * oReceitaItem.qtdEmbalagemReceita) / oReceitaItem.qtdEmbalagemProduto) / oReceitaItem.fatorConversao, 3);
            oReceitaItem.baixaEstoque = rst.getBoolean("baixaestoque");
            oReceitaItem.idTipoEmbalagem = rst.getInt("id_tipoembalagem");

            oReceita.vItem.add(oReceitaItem);
        }

        stm.close();

        return oReceita;
    }
}
