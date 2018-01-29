package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class SuperDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Super";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cd_loja id,\n" +
                    "    cd_loja || ' - ' || nm_fantazia descricao\n" +
                    "from\n" +
                    "    loja\n" +
                    "order by\n" +
                    "    id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    seg.cd_segmt,\n" +
                    "    seg.dsc_segmt,\n" +
                    "    d.cd_depart,\n" +
                    "    d.dsc_depart,\n" +
                    "    g.cd_grupo,\n" +
                    "    g.dsc_grupo,\n" +
                    "    sb.cd_subgrupo,\n" +
                    "    sb.dsc_subgrupo\n" +
                    "from\n" +
                    "    subgrupo sb\n" +
                    "    join grupo g on\n" +
                    "        sb.cd_grupo = g.cd_grupo\n" +
                    "    join secao s on\n" +
                    "        g.cd_secao = s.cd_secao\n" +
                    "    join departamento d on\n" +
                    "        s.cd_depart = d.cd_depart\n" +
                    "    join segmento seg on\n" +
                    "        d.cd_segmt = seg.cd_segmt\n" +
                    "order by\n" +
                    "    1,3,5,7"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("cd_segmt"));
                    imp.setMerc1Descricao(rst.getString("dsc_segmt"));
                    imp.setMerc2ID(rst.getString("cd_depart"));
                    imp.setMerc2Descricao(rst.getString("dsc_depart"));
                    imp.setMerc3ID(rst.getString("cd_grupo"));
                    imp.setMerc3Descricao(rst.getString("dsc_grupo"));
                    imp.setMerc4ID(rst.getString("cd_subgrupo"));
                    imp.setMerc4Descricao(rst.getString("dsc_subgrupo"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.cd_prod id,\n" +
                    "    p.dt_cdto datacadastro,\n" +
                    "    ean.cd_brr ean,\n" +
                    "    ean.qtd_emb qtdembalagem,\n" +
                    "    coalesce(emb.sigla, emb.dsc_emb) tipoembalagem,\n" +
                    "    case when p.flag_balanca = 'S' then 1 else 0 end ebalanca,\n" +
                    "    coalesce(p.dia_validade,0) validade,\n" +
                    "    p.dsc_prod descricaocompleta,\n" +
                    "    p.dsc_reduz descricaoreduzida,\n" +
                    "    sg.cd_segmt,\n" +
                    "    sg.cd_depart,\n" +
                    "    sg.cd_secao,\n" +
                    "    sg.cd_grupo,\n" +
                    "    p.cd_sub_grupo,\n" +
                    "    p.peso,\n" +
                    "    pe.qtd_maxima estoquemaximo,\n" +
                    "    pe.qtd_minima estoqueminimo,\n" +
                    "    pe.qtd_estq estoque,\n" +
                    "    pe.mrg_lucr margem,\n" +
                    "    pe.custo_ult_cmp custocomimposto,\n" +
                    "    pe.custo_ult_cmp_icms custosemimposto,\n" +
                    "    pe.prc_venda precovenda,\n" +
                    "    case when p.flag_ativo = 'S' then 1 else 0 end situacaocadastro,\n" +
                    "    p.cd_ncm ncm,\n" +
                    "    p.cest cest,\n" +
                    "    piscofins.cst_saida_pis piscofinssaida,\n" +
                    "    piscofins.cst_entrada_pis piscofinsentrada,\n" +
                    "    p.nat_rec piscofinsnatrec,\n" +
                    "    trib.cst icmscst,\n" +
                    "    trib.aliquota icsmaliq,\n" +
                    "    coalesce(pe.preco_atac, 0) precoatacado,\n" +
                    "    coalesce(pe.qtd_atac, 0) qtdatacado,\n" +
                    "    (select cd_fornec from fornecedor where cpfcnpj = p.cnpjfab) fabricante\n" +
                    "from\n" +
                    "    produto p\n" +
                    "    join loja lj on lj.cd_loja = " + getLojaOrigem() + "\n" +
                    "    left join codigo_barra ean on\n" +
                    "        p.cd_prod = ean.cd_prod\n" +
                    "    join tipo_embalagem emb on\n" +
                    "        emb.cd_emb = p.cd_emb_venda\n" +
                    "    left join produto_empresa pe on\n" +
                    "        pe.cd_emp = lj.cd_emp\n" +
                    "        and pe.cd_loja = lj.cd_loja\n" +
                    "        and pe.cd_prod = p.cd_prod\n" +
                    "    join pis_cofins piscofins on\n" +
                    "        p.cd_piscofins = piscofins.cd_piscofins\n" +
                    "    left join tributacao_venda trib on\n" +
                    "        trib.cd_emp = lj.cd_emp\n" +
                    "        and trib.cd_loja = lj.cd_loja\n" +
                    "        and pe.cd_tribut = trib.cd_tribut\n" +
                    "    left join subgrupo sg on\n" +
                    "        p.cd_sub_grupo = sg.cd_subgrupo\n" +
                    "order by\n" +
                    "    p.cd_prod"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("cd_segmt"));
                    imp.setCodMercadologico2(rst.getString("cd_depart"));
                    imp.setCodMercadologico3(rst.getString("cd_secao"));
                    imp.setCodMercadologico4(rst.getString("cd_grupo"));
                    imp.setCodMercadologico5(rst.getString("cd_sub_grupo"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofinsnatrec"));
                    imp.setIcmsCst(rst.getInt("icmscst"));
                    imp.setIcmsAliq(rst.getDouble("icsmaliq"));
                    imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
