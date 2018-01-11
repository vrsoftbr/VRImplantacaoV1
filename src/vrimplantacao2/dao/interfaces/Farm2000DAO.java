package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import vrimplantacao.classe.ConexaoParadox;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * DAO do sistema Farm2000.
 * @author Leandro
 */
public class Farm2000DAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Farm2000";
    }

    public List<Estabelecimento> getLojasCliente() {
        return new ArrayList<>(Arrays.asList(new Estabelecimento("1", "FARM 2000")));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  nm2_gr2,\n" +
                "  nom_gr2\n" +
                "from \n" +
                "  sub_grup s"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("nm2_gr2"));
                    imp.setMerc1Descricao(rst.getString("nom_gr2"));
                    
                   result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  cast(trim(cast(p.codi_dsc as varchar(50)) || '-' || p.tip_dsc || '-' || p.codi_apre || '-' || cast(p.cod_lab as varchar(50)) || '-' || cast(p.tip_lab as varchar(50))) as varchar(30)) id,\n" +
                "  p.dat_pro datacadastro,\n" +
                "  p.bar_pro ean,\n" +
                "  1 qtdEmbalagem,\n" +
                "  p.und_pro unidade,\n" +
                "  p.gen_pro e_balanca,\n" +
                "  p.qtdedias_validade validade,\n" +
                "  p.nom_pro descricaocompleta,\n" +
                "  dsc.desc_dsc descricaoreduzida,\n" +
                "  p.nm2_sgr cod_mercadologico1,\n" +
                "  p.peso,\n" +
                "  p.qte_pro estoque,\n" +
                "  p.prc_pro custo,\n" +
                "  p.prt_pro preco,\n" +
                "  p.ativo_inativo ativo,\n" +
                "  p.cod_ncm ncm,\n" +
                "  p.cest_pro cest,\n" +
                "  p.fm_cod_pis piscofins,\n" +
                "  p.cod_nat_rec_pis piscofins_natrec,\n" +
                "  p.nm2_stb icms\n" +
                "from \n" +
                "  produtos p\n" +
                "  join prod_dsc dsc on p.codi_dsc = dsc.codi_dsc and p.tip_dsc = dsc.tip_dsc"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    
                    String ean = rst.getString("ean");
                    if (imp.isBalanca()) {
                        //Fazer um tratamento de todos os produtos de balança.
                    }
                    
                    imp.setEan(ean);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("cod_mercadologico1"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro("A".equals(rst.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natrec"));
                    imp.setIcmsDebitoId(rst.getString("icms"));
                    imp.setIcmsCreditoId(rst.getString("icms"));
                    
                   result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
