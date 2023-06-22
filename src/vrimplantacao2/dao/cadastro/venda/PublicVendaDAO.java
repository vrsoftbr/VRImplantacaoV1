/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.venda;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.venda.PublicVendaValoresAgrupado;

/**
 *
 * @author Desenvolvimento
 */
public class PublicVendaDAO {

    public PublicVendaDAO() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.publicvendasimportadas(\n" +
                    "	id_venda integer not null primary key\n" +
                    ");"
            );
        } 
    }    

    public Long gravarPublicVenda(PublicVendaValoresAgrupado item) throws Exception {
        try (Statement stm = Conexao.createStatement()) {

            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("public");
            sql.setTableName("venda");
            //id serial4 NOT NULL,
            sql.put("id_loja", item.getId_loja());//id_loja int4 NOT NULL,
            sql.put("id_produto", item.getId_produto());//id_produto int4 NOT NULL,
            sql.put("data", item.getData());//"data" date NOT NULL,
            sql.put("precovenda", item.getMediaPreco());//precovenda numeric(11, 2) NOT NULL,
            sql.put("quantidade", item.getSomaQuantidade());//quantidade numeric(12, 3) NOT NULL,
            sql.put("id_comprador", 1);//id_comprador int4 NOT NULL,
            sql.put("custocomimposto", item.getCustocomimposto());//custocomimposto numeric(13, 4) NOT NULL,
            sql.put("piscofins", 0.0);//piscofins numeric(11, 2) NOT NULL,
            sql.put("operacional", 0.0);//operacional numeric(11, 2) NOT NULL,
            sql.put("icmscredito", item.getIcmscredito());//icmscredito numeric(11, 2) NOT NULL,
            sql.put("icmsdebito", item.getIcmsdebito());//icmsdebito numeric(11, 2) NOT NULL,
            sql.put("valortotal", item.getSomaValoresTotal());//valortotal numeric(11, 2) NOT NULL,
            sql.put("custosemimposto", item.getCustocomimposto());//custosemimposto numeric(13, 4) NOT NULL,
            sql.put("oferta", false);//oferta bool NOT NULL,
            sql.put("perda", 0.0);//perda numeric(14, 2) NOT NULL,
            sql.put("customediosemimposto", item.getCustomediocomimposto());//customediosemimposto numeric(13, 4) NOT NULL DEFAULT 0,
            sql.put("customediocomimposto", item.getCustomediosemimposto());//customediocomimposto numeric(13, 4) NOT NULL DEFAULT 0,
            sql.put("piscofinscredito", 0);//piscofinscredito numeric(11, 2) NOT NULL DEFAULT 0,
            sql.put("cupomfiscal", true);//cupomfiscal bool NOT NULL DEFAULT true,           

            sql.getReturning().add("id");

            String strSQL = sql.getInsert();

            try (ResultSet rst = stm.executeQuery(
                    strSQL
            )) {
                if (rst.next()) {
                    return rst.getLong("id");
                }
            } catch (Exception e) {
                System.out.println("Erro em gravarPublicVenda " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public List<PublicVendaValoresAgrupado> carregaVendasExistentes(String dataAtual, int lojaVR) throws Exception {
        List<PublicVendaValoresAgrupado> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	*\n"
                    + "from\n"
                    + "	venda \n"
                    + "where\n"
                    + "	id not in (\n"
                    + "	select\n"
                    + "		*\n"
                    + "	from\n"
                    + "		implantacao.publicvendasimportadas)\n"
                    + "and data = '" + dataAtual + "'\n"
                    + "	and id_loja = " + lojaVR
            )) {
                while (rs.next()) {
                    PublicVendaValoresAgrupado vo = new PublicVendaValoresAgrupado(
                            rs.getInt("id_loja"),
                            rs.getInt("id_produto"),
                            rs.getDate("data"),
                            rs.getDouble("precovenda"),
                            rs.getDouble("quantidade"),
                            rs.getInt("id_comprador"),
                            rs.getDouble("custocomimposto"),
                            rs.getInt("piscofins"),
                            rs.getInt("operacional"),
                            rs.getDouble("icmscredito"),
                            rs.getDouble("icmsdebito"),
                            rs.getDouble("valortotal"),
                            rs.getDouble("custosemimposto"),
                            rs.getBoolean("oferta"),
                            rs.getDouble("perda"),
                            rs.getDouble("customediosemimposto"),
                            rs.getDouble("customediocomimposto"),
                            rs.getDouble("piscofins"),
                            rs.getBoolean("cupomfiscal"));

                    result.add(vo);
                }
            }
        }
        return result;
    }

    public Long atualizarpublicVenda(PublicVendaValoresAgrupado item) throws Exception {
        try (Statement stm = Conexao.createStatement()) {

            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("public");
            sql.setTableName("venda");
            //id serial4 NOT NULL,
            sql.put("id_loja", item.getId_loja());//id_loja int4 NOT NULL,
            sql.put("id_produto", item.getId_produto());//id_produto int4 NOT NULL,
            sql.put("data", item.getData());//"data" date NOT NULL,
            sql.put("precovenda", item.getMediaPreco());//precovenda numeric(11, 2) NOT NULL,
            sql.put("quantidade", item.getSomaQuantidade());//quantidade numeric(12, 3) NOT NULL,
            sql.put("id_comprador", item.getId_comprador());//id_comprador int4 NOT NULL,
            sql.put("custocomimposto", item.getCustocomimposto());//custocomimposto numeric(13, 4) NOT NULL,
            sql.put("piscofins", item.getPiscofins());//piscofins numeric(11, 2) NOT NULL,
            sql.put("operacional", item.getOperacional());//operacional numeric(11, 2) NOT NULL,
            sql.put("icmscredito", item.getIcmscredito());//icmscredito numeric(11, 2) NOT NULL,
            sql.put("icmsdebito", item.getIcmsdebito());//icmsdebito numeric(11, 2) NOT NULL,
            sql.put("valortotal", item.getSomaValoresTotal());//valortotal numeric(11, 2) NOT NULL,
            sql.put("custosemimposto", item.getCustocomimposto());//custosemimposto numeric(13, 4) NOT NULL,
            sql.put("oferta", item.isOferta());//oferta bool NOT NULL,
            sql.put("perda", item.getPerda());//perda numeric(14, 2) NOT NULL,
            sql.put("customediosemimposto", item.getCustomediocomimposto());//customediosemimposto numeric(13, 4) NOT NULL DEFAULT 0,
            sql.put("customediocomimposto", item.getCustomediosemimposto());//customediocomimposto numeric(13, 4) NOT NULL DEFAULT 0,
            sql.put("piscofinscredito", item.getPiscofinscredito());//piscofinscredito numeric(11, 2) NOT NULL DEFAULT 0,
            sql.put("cupomfiscal", item.isCupomfiscal());//cupomfiscal bool NOT NULL DEFAULT true,         
            sql.setWhere("id_produto = " + item.getId_produto() + " and data = '" + item.getData() + "' and id_loja = " + item.getId_loja());

            String strSQL = sql.getUpdate();

            try {
                stm.executeUpdate(strSQL);
                return idAtualizado(item);
            } catch (Exception e) {
                System.out.println("Erro em atualizarPublicVenda " + e.getMessage());
                System.out.println(strSQL);
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Long idAtualizado(PublicVendaValoresAgrupado item) throws Exception {
        Long idAtual = 0L;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	id\n"
                    + "from\n"
                    + "	public.venda \n"
                    + "where\n"
                    + "id_produto = " + item.getId_produto()
                    + " and data = '" + item.getData() + "' and id_loja = " + item.getId_loja())) {
                while (rs.next()) {
                    idAtual = rs.getLong("id");
                }
            }
        }
        return idAtual;
    }

    List<PublicVendaValoresAgrupado> carregarVendasImportadas(String menorData, String maiorData, int lojaVR) throws Exception {
        List<PublicVendaValoresAgrupado> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	*\n"
                    + "from\n"
                    + "	venda \n"
                    + "where\n"
                    + "	id in (\n"
                    + "	select\n"
                    + "		*\n"
                    + "	from\n"
                    + "		implantacao.publicvendasimportadas)\n"
                    + "and data between '" + menorData + "' and '" + maiorData+ "'\n"
                    + "and id_loja = " + lojaVR
            )) {
                while (rs.next()) {
                    PublicVendaValoresAgrupado vo = new PublicVendaValoresAgrupado(
                            rs.getInt("id_loja"),
                            rs.getInt("id_produto"),
                            rs.getDate("data"),
                            rs.getDouble("precovenda"),
                            rs.getDouble("quantidade"),
                            rs.getInt("id_comprador"),
                            rs.getDouble("custocomimposto"),
                            rs.getInt("piscofins"),
                            rs.getInt("operacional"),
                            rs.getDouble("icmscredito"),
                            rs.getDouble("icmsdebito"),
                            rs.getDouble("valortotal"),
                            rs.getDouble("custosemimposto"),
                            rs.getBoolean("oferta"),
                            rs.getDouble("perda"),
                            rs.getDouble("customediosemimposto"),
                            rs.getDouble("customediocomimposto"),
                            rs.getDouble("piscofins"),
                            rs.getBoolean("cupomfiscal"));

                    result.add(vo);
                }
            }
        }
        return result;
    }
    
    public void logarPublicVendaImportadas(long id_venda) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("insert into implantacao.publicvendasimportadas values (" + id_venda + ")");
        }
    }

}
