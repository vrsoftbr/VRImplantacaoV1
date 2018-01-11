
package vrimplantacao.dao.fiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.classe.Global;
import vrimplantacao.vo.fiscal.MapaResumoItemVO;
import vrimplantacao.vo.fiscal.MapaResumoVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;


public class MapaResumoDAO {

     public void salvar(MapaResumoVO i_mapaResumo) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            rst = stm.executeQuery("SELECT id FROM maparesumo WHERE id_loja = " + Global.idLoja + " AND ecf = " + i_mapaResumo.ecf + " AND data = '" + Util.formatDataBanco(i_mapaResumo.data) + "'");

            if (rst.next()) {
                i_mapaResumo.id = rst.getInt("id");
                
                sql = new StringBuilder();
                sql.append("UPDATE maparesumo SET");
                sql.append(" ecf = " + i_mapaResumo.ecf + ",");
                sql.append(" data = '" + Util.formatDataBanco(i_mapaResumo.data) + "',");
                sql.append(" reducao = " + i_mapaResumo.reducao + ",");
                sql.append(" contadorinicial = " + i_mapaResumo.contadorInicial + ",");
                sql.append(" contadorfinal = " + i_mapaResumo.contadorFinal + ",");
                sql.append(" contadorreinicio = " + i_mapaResumo.contadorReinicio + ",");
                sql.append(" gtinicial = " + i_mapaResumo.gtInicial + ",");
                sql.append(" gtfinal = " + i_mapaResumo.gtFinal + ",");
                sql.append(" cancelamento = " + i_mapaResumo.cancelamento + ",");
                sql.append(" desconto = " + i_mapaResumo.desconto + ",");
                sql.append(" acrescimo = " + i_mapaResumo.acrescimo + ",");
                sql.append(" datahoraemissaorz = '" + Util.formatDataHoraBanco(i_mapaResumo.dataHoraEmissaoRz) + "',");
                sql.append(" contadorgerencial = " + i_mapaResumo.contadorGerencial + ",");
                sql.append(" contadorcdc = " + i_mapaResumo.contadorCDC + ",");
                sql.append(" totalnaofiscal = " + i_mapaResumo.totalNaoFiscal + ",");
                sql.append(" lancamentomanual = " + i_mapaResumo.lancamentoManual);
                sql.append(" WHERE id = " + i_mapaResumo.id);

                stm.execute(sql.toString());

                String observacao = "DATA: " + i_mapaResumo.data;

            } else {
                i_mapaResumo.idLoja = Global.idLoja;

                sql = new StringBuilder();
                sql.append("INSERT INTO maparesumo(id_loja, data, ecf, reducao, contadorinicial, contadorfinal, gtinicial, gtfinal, cancelamento,");
                sql.append(" desconto, acrescimo, lancamentomanual, contadorreinicio, datahoraemissaorz, contadorgerencial, contadorcdc,");
                sql.append(" totalnaofiscal) VALUES (");
                sql.append(i_mapaResumo.idLoja + ", ");
                sql.append("'" + Util.formatDataBanco(i_mapaResumo.data) + "', ");
                sql.append(i_mapaResumo.ecf + ", ");
                sql.append(i_mapaResumo.reducao + ", ");
                sql.append(i_mapaResumo.contadorInicial + ", ");
                sql.append(i_mapaResumo.contadorFinal + ", ");
                sql.append(i_mapaResumo.gtInicial + ", ");
                sql.append(i_mapaResumo.gtFinal + ", ");
                sql.append(i_mapaResumo.cancelamento + ", ");
                sql.append(i_mapaResumo.desconto + ", ");
                sql.append(i_mapaResumo.acrescimo + ", ");
                sql.append(i_mapaResumo.lancamentoManual + ", ");
                sql.append(i_mapaResumo.contadorReinicio + ", ");
                sql.append("'" + Util.formatDataHoraBanco(i_mapaResumo.dataHoraEmissaoRz) + "', ");
                sql.append(i_mapaResumo.contadorGerencial + ", ");
                sql.append(i_mapaResumo.contadorCDC + ", ");
                sql.append(i_mapaResumo.totalNaoFiscal + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('maparesumo_id_seq') AS id");
                rst.next();

                i_mapaResumo.id = rst.getLong("id");

                String observacao = "DATA: " + i_mapaResumo.data;
            }

            stm.execute("DELETE FROM maparesumoitem WHERE id_maparesumo = " + i_mapaResumo.id);

            for (MapaResumoItemVO oItem : i_mapaResumo.vItem) {
                sql = new StringBuilder();
                sql.append("INSERT INTO maparesumoitem (id_maparesumo, id_aliquota, valor) VALUES(");
                sql.append(i_mapaResumo.id + ", ");
                sql.append(oItem.idAliquota + ", ");
                sql.append(oItem.valor + ")");

                stm.execute(sql.toString());
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

}
