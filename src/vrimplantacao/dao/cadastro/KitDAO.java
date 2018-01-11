package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.vo.administrativo.KitItemVO;
import vrimplantacao.vo.administrativo.KitVO;
import vrimplantacao.vo.loja.FornecedorVO;
import vrimplantacao.vo.loja.LojaVO;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

public class KitDAO {

    public KitVO carregar(long i_id, int idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT kit.*, produto.descricaocompleta AS produto");
        sql.append(" FROM kit");
        sql.append(" INNER JOIN produto ON produto.id = kit.id_produto");
        sql.append(" WHERE kit.id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Kit não encontrado!");
        }

        LojaVO oLoja = new LojaDAO().carregar(idLoja);

        FornecedorVO oFornecedor = new FornecedorDAO().carregar(oLoja.idFornecedor);

        KitVO oKit = new KitVO();
        oKit.id = rst.getLong("id");
        oKit.idProduto = rst.getInt("id_produto");
        oKit.produto = rst.getString("produto");
        oKit.preconormal = rst.getBoolean("preconormal");

        sql = new StringBuilder();
        sql.append("SELECT kit.id_produto, kit.precovenda, produto.descricaocompleta, complemento.custocomimposto,");
        sql.append(" (SELECT codigobarras FROM produtoautomacao where id_produto = produto.id AND LENGTH(codigobarras::varchar) <= 13 LIMIT 1) AS codigobarras,");
        sql.append(" complemento.custosemimposto, aliquota.porcentagem, aliquota.reduzido, kit.quantidade, produto.id_tipoembalagem,");
        sql.append(" produto.id_tipopiscofins, complemento.operacional, complemento.precovenda AS preconormal, tipoembalagem.descricao AS tipoembalagem");
        sql.append(" FROM kititem AS kit");
        sql.append(" INNER JOIN produto ON produto.id = kit.id_produto");
        sql.append(" INNER JOIN produtoaliquota ON produtoaliquota.id_produto = produto.id AND produtoaliquota.id_estado = " + oFornecedor.idEstado);
        sql.append(" INNER JOIN produtocomplemento AS complemento ON complemento.id_produto = produto.id AND complemento.id_loja = " + idLoja);
        sql.append(" INNER JOIN aliquota ON aliquota.id = produtoaliquota.id_aliquotadebito");
        sql.append(" INNER JOIN tipoembalagem ON tipoembalagem.id = produto.id_tipoembalagem");
        sql.append(" WHERE kit.id_kit = " + oKit.id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            KitItemVO oProduto = new KitItemVO();
            oProduto.idProduto = rst.getInt("id_produto");
            oProduto.produto = rst.getString("descricaocompleta");
            oProduto.precoVenda = rst.getDouble("precovenda");
            oProduto.quantidade = rst.getDouble("quantidade");
            oProduto.precoNormal = rst.getDouble("preconormal");
            oProduto.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
            oProduto.tipoEmbalagem = rst.getString("tipoembalagem");
            oProduto.custoComImposto = rst.getDouble("custocomimposto");
            oProduto.custoSemImposto = rst.getDouble("custosemimposto");
            oProduto.icmsDebito = rst.getDouble("porcentagem") * ((100 - rst.getDouble("reduzido")) / 100);
            oProduto.operacional = rst.getDouble("operacional");
            oProduto.idTipoPisCofins = rst.getInt("id_tipopiscofins");
            oProduto.codigoBarras = rst.getLong("codigobarras");

            oKit.vProduto.add(oProduto);
        }

        stm.close();

        return oKit;
    }

    public long verificar(int i_idProduto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id FROM kit WHERE id_produto = " + i_idProduto);

        if (rst.next()) {
            return rst.getLong("id");
        } else {
            return 0;
        }
    }
}
