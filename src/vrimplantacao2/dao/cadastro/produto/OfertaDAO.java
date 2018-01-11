package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.oferta.OfertaVO;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;

/**
 * DAO das ofertas do VR.
 * @author Leandro
 */
public class OfertaDAO {

    public MultiMap<Comparable, Void> getCadastradas(int lojaVR) throws Exception {
        MultiMap<Comparable, Void> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	id_produto,\n" +
                    "	datainicio,\n" +
                    "	datatermino,\n" +
                    "	id_situacaooferta\n" +
                    "from oferta"
            )) {
                while (rst.next()) {
                    result.put(
                            null, 
                            rst.getInt("id_produto"),
                            rst.getDate("datainicio"),
                            rst.getDate("datatermino"),
                            SituacaoOferta.getById(rst.getInt("id_situacaooferta"))
                    );
                }
            }
        }
        
        return result;
    }

    public void gravar(OfertaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("oferta");
            sql.put("id_loja", vo.getIdLoja());
            sql.put("id_produto", vo.getProduto().getId());
            sql.put("datainicio", vo.getDataInicio());
            sql.put("datatermino", vo.getDataTermino());
            sql.put("precooferta", vo.getPrecoOferta());
            if (vo.getPrecoNormal() == 0) {
                sql.putSql("preconormal", "(select precovenda from "
                        + "produtocomplemento where id_loja = " + vo.getIdLoja() 
                        + " and id_produto = " + vo.getProduto().getId() + ")");
            } else {
                sql.put("preconormal", vo.getPrecoNormal());
            }
            sql.put("id_situacaooferta", vo.getSituacaoOferta().getId());
            sql.put("id_tipooferta", vo.getTipoOferta().getId());
            sql.put("precoimediato", vo.isPrecoImediato());
            sql.put("ofertafamilia", vo.isOfertaFamilia());
            sql.put("ofertaassociado", vo.isOfertaAssociado());
            sql.put("controle", vo.getControle());
            sql.put("aplicapercentualprecoassociado", vo.isAplicaPercentualPrecoAssociado());
            sql.put("encerraoferta", vo.isEncerraOferta());
            sql.put("encerraofertaitens", vo.getEncerraOfertaItem());
            sql.put("bloquearvenda", vo.isBloquearVenda());
            sql.put("bloquearvendaitens", vo.getBloquearVendaItens());
            sql.getReturning().add("id");
            sql.getReturning().add("preconormal");
            
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                while (rst.next()) {
                    vo.setPrecoNormal(rst.getDouble("preconormal"));
                    vo.setId(rst.getInt("id"));
                }
            }
            
        }
    }
    
}
