package vrimplantacao2.dao.interfaces.hipermax;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class SupermercadoHipermaxDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SupermercadoHipermax";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	trim(grupo)::int as mercadologico,\n"
                    + "	trim(nomegrupo) as descricaomercadologico\n"
                    + "from implantacao.produtos_hipermax\n"
                    + "where trim(grupo)::int != 0\n"
                    + "order by trim(grupo)::int"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("mercadologico"));
                    imp.setMerc1Descricao(rst.getString("descricaomercadologico"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(rst.getString("descricaomercadologico"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("descricaomercadologico"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	trim(codigo) as codigo,\n"
                    + "	trim(codigointerno) as ean,\n"
                    + "	trim(descricao) as descricao,\n"
                    + "	trim(tipoembalagem) as tipoembalagem,\n"
                    + "	trim(replace(ncm, '.', '')) as ncm,\n"
                    + "	trim(trb) as codigotributacao,\n"
                    + "	trim(marca) as marca,\n"
                    + "	trim(grupo) as mercadologico,\n"
                    + "	trim(nomegrupo) as descricaomercadologico,\n"
                    + "	trim(replace(cest, '.', '')) as cest,\n"
                    + "	trim(ipi) as ipi,\n"
                    + "	trim(gtin) as gtin,\n"
                    + "	trim(at) as at,\n"
                    + "	trim(cp) as cp,\n"
                    + "	trim(csticms) as csticms,\n"
                    + "	trim(replace(preco, ',', '.')) as precovenda,\n"
                    + "	trim(custo) as custo\n"
                    + "from implantacao.produtos_hipermax"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("ean"));                    
                    imp.seteBalanca(rst.getString("ean").startsWith("B"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setCodMercadologico1(rst.getString("mercadologico"));
                    imp.setSituacaoCadastro("S".equals(rst.getString("at")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsCstSaida(rst.getInt("csticms"));
                    imp.setIcmsAliqSaida(0);
                    imp.setIcmsReducaoSaida(0);
                    imp.setIcmsCstSaidaForaEstado(rst.getInt("csticms"));
                    imp.setIcmsAliqSaidaForaEstado(0);
                    imp.setIcmsReducaoSaidaForaEstado(0);                   
                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("csticms"));
                    imp.setIcmsAliqSaidaForaEstadoNF(0);
                    imp.setIcmsReducaoSaidaForaEstadoNF(0);                    
                    imp.setIcmsCstEntrada(rst.getInt("csticms"));
                    imp.setIcmsAliqEntrada(0);
                    imp.setIcmsReducaoEntrada(0);
                    imp.setIcmsCstEntradaForaEstado(rst.getInt("csticms"));
                    imp.setIcmsAliqEntradaForaEstado(0);
                    imp.setIcmsReducaoEntradaForaEstado(0);
                    imp.setIcmsCstConsumidor(rst.getInt("csticms"));
                    imp.setIcmsAliqConsumidor(0);
                    imp.setIcmsReducaoConsumidor(0);
                    
                    result.add(imp);                    
                }
            }
        }
        return result;
    }
}
