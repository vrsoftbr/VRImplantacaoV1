/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.promocao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoAnteriorVO;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoVO;
import vrimplantacao2.vo.importacao.PromocaoIMP;

/**
 * Dao para gravar Promoção.
 *
 * @author Michael
 */
public class PromocaoDAO {

    public void salvar(PromocaoVO promocao) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("promocao");
            sql.setSchema("public");
            sql.put("id", promocao.getId());
            sql.put("id_loja", promocao.getIdLoja());
            sql.put("descricao", promocao.getDescricao());
            sql.put("datainicio", promocao.getDataInicio());
            sql.put("datatermino", promocao.getDataTermino());
            sql.put("pontuacao", promocao.getPontuacao());
            sql.put("quantidade", promocao.getQuantidade());
            sql.put("qtdcupom", promocao.getQtdcupom());
            sql.put("id_situacaocadastro", promocao.getIdSituacaocadastro());
            sql.put("id_tipopromocao", promocao.getIdTipopromocao());
            sql.put("valor", promocao.getValor());
            sql.put("controle", promocao.getControle());
            sql.put("id_tipopercentualvalor", promocao.getIdTipopercentualvalor());
            sql.put("id_tipoquantidade", promocao.getIdTipoquantidade());
            sql.put("aplicatodos", promocao.isAplicatodos());
            sql.put("cupom", promocao.getCupom());
            sql.put("valordesconto", promocao.getValordesconto());
            sql.put("valorreferenteitenslista", promocao.isValorReferenteItensLista());
            sql.put("verificaprodutosauditados", promocao.isVerificaProdutosAuditados());
            sql.put("datalimiteresgatecupom", promocao.getDataLimiteResgateCupom());
            sql.put("id_tipopercentualvalordesconto", promocao.getIdTipoPercentualValorDesconto());
            sql.put("valorpaga", promocao.getValorPaga());
            sql.put("desconsideraritem", promocao.isDesconsiderarItem());
            sql.put("qtdlimite", promocao.getQtdLimite());
            sql.put("somenteclubevantagens", promocao.isSomenteClubeVantagens());
            sql.put("diasexpiracao", promocao.getDiasExpiracao());
            sql.put("utilizaquantidadeproporcional", promocao.isUtilizaQuantidadeProporcional());

            try {
                stm.execute(sql.getInsert());
            } catch (Exception a) {
                try {
                    stm.execute(sql.getUpdate());
                } catch (Exception e) {
                    System.out.println(sql.getInsert());
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }

    public void apagarTudo() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from promocaofinalizadora;");
            stm.execute("delete from promocaoitem;");
            stm.execute("delete from promoca;");
            stm.execute("drop table if exists implantacao.codant_promocao;");
            stm.execute("alter sequence promocaoitem_id_seq restart with 1;");
            stm.execute("alter sequence promocaofinalizadora_id_seq restart with 1;");
        }
    }

    public void salvarPromocaoItens(PromocaoAnteriorVO itens) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("promocaoitem");
            sql.setSchema("public");
            sql.put("id_promocao", itens.getId_promocao());
            sql.put("id_produto", itens.getId_produto());
            sql.put("precovenda", itens.getPaga());
            try {
                stm.execute(sql.getInsert());
            } catch (Exception a) {
                try {
                    stm.execute(sql.getUpdate());
                } catch (Exception e) {
                    System.out.println(sql.getInsert());
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }

    public int getId() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id "
                    + "from promocao"
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                } else {
                    return 0;
                }
            }
        }
    }

    public List<PromocaoIMP> getFinalizadora() throws Exception {
        List<PromocaoIMP> Result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct \n"
                    + "	p.id_promocao id,\n"
                    + "	p.loja id_loja,\n"
                    + "	p.descricao descricao,\n"
                    + "	p.datainicio datainicio,\n"
                    + "	p.datatermino datatermino,\n"
                    + "	p.quantidade quantidade,\n"
                    + "	p.id_promocao controle,\n"
                    + "	p.paga valorpaga,\n"
                    + "	f.id id_finalizadora\n"
                    + "from\n"
                    + "	implantacao.codant_promocao p, pdv.finalizadora f "
            )) {
                while (rst.next()) {
                    PromocaoIMP imp = new PromocaoIMP();
                    imp.setId_promocao(rst.getString("id"));
                    imp.setIdLoja(rst.getInt("id_loja"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataTermino(rst.getDate("datatermino"));
                    imp.setQuantidade(rst.getDouble("quantidade"));
                    imp.setControle(rst.getInt("controle"));
                    imp.setPaga(rst.getDouble("valorpaga"));
                    imp.setId_finalizadora(rst.getInt("id_finalizadora"));
                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    public List<PromocaoIMP> getPromocaoItens() throws Exception {
        List<PromocaoIMP> Result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "distinct \n"
                    + "	p.id_promocao,\n"
                    + "	imp.codigoatual id_produto,\n"
                    + "	p.paga\n"
                    + "from\n"
                    + "	implantacao.codant_promocao p\n"
                    + "inner join implantacao.codant_produto imp on\n"
                    + "	p.id_produto = imp.impid\n"
                    + "where p.sistema = imp.impsistema \n"
                    + "and p.loja = imp.imploja \n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    PromocaoIMP imp = new PromocaoIMP();
                    imp.setId_promocao(rst.getString("id_promocao"));
                    imp.setId_produto(rst.getString("id_produto"));
                    imp.setPaga(rst.getDouble("paga"));
                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    public void salvarFinalizadora(PromocaoAnteriorVO finaliza) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("promocaofinalizadora");
            sql.setSchema("public");
            sql.put("id_promocao", finaliza.getId_promocao());
            sql.put("id_finalizadora", finaliza.getId_finalizadora());
            try {
                stm.execute(sql.getInsert());
            } catch (Exception e) {
                System.out.println(sql.getInsert());
                e.printStackTrace();
                throw e;
            }
        }
    }

    public List<PromocaoIMP> getValoresFinalizadora(String loja) throws Exception {
        List<PromocaoIMP> Result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct pf.* from promocaofinalizadora pf\n"
                    + "join promocao p on pf.id_promocao = pf.id_promocao \n"
                    + "join loja l on l.id = " + loja + ""
            )) {
                while (rst.next()) {
                    PromocaoIMP imp = new PromocaoIMP();
                    imp.setId(rst.getString("id"));
                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    public void limparCodantPromocao(String lojaOrigem, String sistema) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from implantacao.codant_promocao\n"
                    + "where sistema = '" + sistema + "'\n"
                    + "and loja = '" + lojaOrigem + "'");
            stm.execute("do $$\n"
                    + "declare maxid int;\n"
                    + "begin\n"
                    + "    select max(codigoatual)+1 from implantacao.codant_promocao into maxid;\n"
                    + "   	if maxid = 0 then maxid := 1;\n"
                    + "   	elseif maxid is null then maxid := 1;\n"
                    + "   	end if;\n"
                    + "    execute 'alter SEQUENCE implantacao.codant_promocao_codigoatual_seq RESTART with '|| maxid;   \n"
                    + "end;\n"
                    + "$$ language plpgsql");
        }
    }

    public void limparPromocao(String lojaOrigem, String sistema) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete\n"
                    + "from\n"
                    + "	promocaofinalizadora\n"
                    + "where\n"
                    + "	id_promocao in (\n"
                    + "	select\n"
                    + "		distinct id_promocao::int\n"
                    + "	from\n"
                    + "		implantacao.codant_promocao\n"
                    + "	where\n"
                    + "		sistema = '" + sistema + "'\n"
                    + "		and loja = '" + lojaOrigem + "'" +")");
            stm.execute("do $$\n"
                    + "declare maxid int;\n"
                    + "begin\n"
                    + "    select max(id)+1 from promocaofinalizadora into maxid;\n"
                    + "   	if maxid = 0 then maxid := 1;\n"
                    + "   	elseif maxid is null then maxid := 1;\n"
                    + "   	end if;\n"
                    + "    execute 'alter SEQUENCE promocaofinalizadora_id_seq RESTART with '|| maxid;   \n"
                    + "end;\n"
                    + "$$ language plpgsql");
            stm.execute("delete\n"
                    + "from\n"
                    + "	promocaoitem\n"
                    + "where\n"
                    + "	id_promocao in (\n"
                    + "	select\n"
                    + "		distinct id_promocao::int\n"
                    + "	from\n"
                    + "		implantacao.codant_promocao\n"
                    + "	where\n"
                    + "		sistema = '" + sistema + "'\n"
                    + "		and loja = '" + lojaOrigem + "'"+ ")");
            stm.execute("do $$\n"
                    + "declare maxid int;\n"
                    + "begin\n"
                    + "    select max(id)+1 from promocaoitem into maxid;\n"
                    + "   	if maxid = 0 then maxid := 1;\n"
                    + "   	elseif maxid is null then maxid := 1;\n"
                    + "   	end if;\n"
                    + "    execute 'alter SEQUENCE promocaoitem_id_seq RESTART with '|| maxid;   \n"
                    + "end;\n"
                    + "$$ language plpgsql");
            stm.execute("delete\n"
                    + "from\n"
                    + "	promocao\n"
                    + "where\n"
                    + "	id in (\n"
                    + "	select\n"
                    + "		distinct id_promocao::int\n"
                    + "	from\n"
                    + "		implantacao.codant_promocao\n"
                    + "	where\n"
                    + "		sistema = '" + sistema + "'\n"
                    + "		and loja = '" + lojaOrigem + "'" + ")");
        }
    }
}