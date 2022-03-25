package vrimplantacao2.dao.cadastro.promocao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoAnteriorVO;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoVO;
import vrimplantacao2.vo.importacao.PromocaoIMP;

/**
 * Dao para gravar Promoção.
 *
 * @author Michael
 */
public class PromocaoDAO {

    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

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
            } catch (Exception e) {
                System.out.println(sql.getInsert());
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void apagarTudo() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from promocaofinalizadora;");
            stm.execute("delete from promocaoitem;");
            stm.execute("delete from promoca;");
            stm.execute("drop table if exists implantacao.codant_promocao;");
            stm.execute("alter sequence promocao_id_seq restart with 1;");
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
            } catch (Exception e) {
                System.out.println(sql.getInsert());
                e.printStackTrace();
                throw e;
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
                    + "	p.id_promocao,\n"
                    + "	f.id id_finalizadora\n"
                    + "from\n"
                    + "	promocaoitem p, pdv.finalizadora f "
            )) {
                while (rst.next()) {
                    PromocaoIMP imp = new PromocaoIMP();
                    imp.setId_promocao(rst.getString("id_promocao"));
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
                    + "	distinct\n"
                    + "	p.id_promocao,\n"
                    + "	pr.ID id_produto,\n"
                    + "	p.paga\n"
                    + "from\n"
                    + "	implantacao.codant_promocao p\n"
                    + "inner join implantacao.codant_produto imp on\n"
                    + "	p.id_produto = imp.impid\n"
                    + "inner join produto pr on\n"
                    + "	imp.codigoatual = pr.id\n"
                    + "order by\n"
                    + "	2"
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
}