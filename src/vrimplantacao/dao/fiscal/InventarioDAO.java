package vrimplantacao.dao.fiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.PisCofinsDAO;
import vrimplantacao.vo.fiscal.InventarioVO;

public class InventarioDAO {

    public List<InventarioVO> carregar(List<InventarioVO> v_inventario, int idLojaVR) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<InventarioVO> vInventario = new ArrayList<>();
        stm = Conexao.createStatement();

        ProgressBar.setStatus("Carregando produtos...");
        ProgressBar.setMaximum(v_inventario.size());

        for (InventarioVO i_inventario : v_inventario) {
            sql = new StringBuilder();
            sql.append("SELECT p.descricaocompleta, pc.custocomimposto, pc.custosemimposto, pc.customediocomimposto, pc.customediosemimposto,");
            sql.append(" pc.precovenda, pa.id_aliquotacredito, pa.id_aliquotadebito, p.id_tipopiscofins, p.id_tipoembalagem,");
            sql.append(" pc.valoricmssubstituicao, pc.valoripi");
            sql.append(" FROM produto AS p");
            sql.append(" INNER JOIN produtocomplemento AS pc ON pc.id_produto = p.id AND pc.id_loja = " + idLojaVR);
            sql.append(" INNER JOIN produtoaliquota AS pa ON pa.id_produto = p.id AND pa.id_estado = " + Global.idEstado);
            sql.append(" WHERE p.id = " + i_inventario.getIdProduto());
            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                InventarioVO oInventario = i_inventario;
                oInventario.setProduto(rst.getString("descricaocompleta"));
                oInventario.setCustoComImposto(rst.getDouble("custocomimposto"));
                oInventario.setPrecoVenda(rst.getDouble("precovenda"));
                oInventario.setIdAliquotaDebito(rst.getInt("id_aliquotadebito"));
                oInventario.setIdAliquotaCredito(rst.getInt("id_aliquotacredito"));
                oInventario.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));
                oInventario.setPis(new PisCofinsDAO().getPis(rst.getInt("id_tipopiscofins")));
                oInventario.setCofins(new PisCofinsDAO().getCofins(rst.getInt("id_tipopiscofins")));
                oInventario.setValorIcmsSubstituicao(rst.getDouble("valoricmssubstituicao"));
                oInventario.setValorIpi(rst.getDouble("valoripi"));
                vInventario.add(oInventario);
            }

            ProgressBar.next();
        }

        stm.close();
        return vInventario;
    }

    public void salvar(List<InventarioVO> v_inventario, int idLojaVR) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        List<InventarioVO> vInventario = carregar(v_inventario, idLojaVR);

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando Inventário...Loja " + idLojaVR);
            ProgressBar.setMaximum(vInventario.size());

            for (InventarioVO i_inventario : vInventario) {

                if (i_inventario.getIdProduto() != -1) {

                    sql = new StringBuilder();
                    sql.append("select id from inventario "
                            + "where id_produto = " + i_inventario.getIdProduto() + ""
                            + "and data = '" + i_inventario.getData() + "' "
                            + "and id_loja = " + idLojaVR);
                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("update inventario set "
                                + "quantidade = quantidade + " + i_inventario.getQuantidade() + ""
                                + "where id_loja = " + idLojaVR + ""
                                + "and id_produto = " + i_inventario.getIdProduto() + " "
                                + "and data = '" + i_inventario.data + "';");

                        if (i_inventario.getCodigoAnterior() != 0) {
                            sql.append("INSERT INTO implantacao.codigoanterior (");
                            sql.append("codigoanterior, codigoatual, id) ");
                            sql.append("VALUES (");
                            sql.append(i_inventario.getCodigoAnterior() + ", ");
                            sql.append(i_inventario.getIdProduto() + ", ");
                            sql.append(i_inventario.getCodigoAnterior() + ");");

                            sql.append("INSERT INTO sped.produtoalteracao (");
                            sql.append("id_produto, datainicial, datafinal, descricaoanterior, id_produtoanterior) ");
                            sql.append("VALUES (");
                            sql.append(i_inventario.getIdProduto() + ", ");
                            sql.append("now(), now(), ");
                            sql.append("'" + i_inventario.getProduto() + "', ");
                            sql.append(i_inventario.getCodigoAnterior() + ");");
                        }

                        stm.execute(sql.toString());
                    } else {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO inventario (id_loja, id_produto, data, datageracao, descricao, precovenda, quantidade,");
                        sql.append(" custocomimposto, custosemimposto, customediocomimposto, customediosemimposto,");
                        sql.append(" id_aliquotacredito, id_aliquotadebito, pis, cofins) VALUES (");
                        sql.append(i_inventario.getIdLoja() + ", ");
                        sql.append(i_inventario.getIdProduto() + ", ");
                        sql.append("'" + i_inventario.getData() + "', ");
                        sql.append("'" + i_inventario.getDataGeracao() + "', ");
                        sql.append("'" + i_inventario.getProduto() + "', ");
                        sql.append(i_inventario.getPrecoVenda() + ", ");
                        sql.append(i_inventario.getQuantidade() + ", ");
                        sql.append(i_inventario.getCustoComImposto() + ", ");
                        sql.append(i_inventario.getCustoSemImposto() + ", ");
                        sql.append(i_inventario.getCustoMedioComImposto() + ", ");
                        sql.append(i_inventario.getCustoMedioSemImposto() + ", ");
                        sql.append(i_inventario.getIdAliquotaCredito() + ", ");
                        sql.append(i_inventario.getIdAliquotaDebito() + ", ");
                        sql.append(i_inventario.getPis() + ", ");
                        sql.append(i_inventario.getCofins() + ");");

                        if (i_inventario.getCodigoAnterior() != 0) {
                            sql.append("INSERT INTO implantacao.codigoanterior (");
                            sql.append("codigoanterior, codigoatual, id) ");
                            sql.append("VALUES (");
                            sql.append(i_inventario.getCodigoAnterior() + ", ");
                            sql.append(i_inventario.getIdProduto() + ", ");
                            sql.append(i_inventario.getCodigoAnterior() + ");");

                            sql.append("INSERT INTO sped.produtoalteracao (");
                            sql.append("id_produto, datainicial, datafinal, descricaoanterior, id_produtoanterior) ");
                            sql.append("VALUES (");
                            sql.append(i_inventario.getIdProduto() + ", ");
                            sql.append("now(), now(), ");
                            sql.append("'" + i_inventario.getProduto() + "', ");
                            sql.append(i_inventario.getCodigoAnterior() + ");");
                        }

                        stm.execute(sql.toString());
                    }
                    ProgressBar.next();
                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
