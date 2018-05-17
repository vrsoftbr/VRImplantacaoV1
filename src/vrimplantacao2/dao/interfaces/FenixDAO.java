package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class FenixDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Fenix";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.id_fam,\n" +
                    "    f.ds_fam\n" +
                    "from\n" +
                    "    familia f\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("id_fam"));
                    imp.setMerc1Descricao(rst.getString("ds_fam"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    id_empresa,\n" +
                    "    nm_fantasia\n" +
                    "from\n" +
                    "    empresa\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id_empresa"), rst.getString("nm_fantasia")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "    p.cd_cst_sai_pro,\n" +
                    "    p.vl_aliquota_sai_pro,\n" +
                    "    p.aliquota_reduz_pro\n" +
                    "from\n" +
                    "    produto p"
            )) {
                while (rst.next()) {
                    
                    int cst = Utils.stringToInt(rst.getString("cd_cst_sai_pro"));
                    double aliquota = Utils.stringToDouble(rst.getString("vl_aliquota_sai_pro"), 0.00);
                    double red = Utils.stringToDouble(rst.getString("aliquota_reduz_pro"), 0.00);
                    
                    String id =  cst + "-" + aliquota + "-" + red;
                    
                    double reduzido = MathUtils.round((red * 100) / (aliquota == 0 ? 1 : aliquota), 3);
                                        
                    if (!(cst == 0 || cst == 20)) {
                        aliquota = 0;
                        reduzido = 0;
                    }
  
                    if (reduzido == 100) {
                        reduzido = 0;
                    }                    
                    
                    result.add(
                            new MapaTributoIMP(
                                    id,
                                    String.format(
                                            "CST: %s; ALIQ: %.2f; RED: %.2f",
                                            cst,
                                            aliquota,
                                            reduzido
                                    ),
                                    cst,
                                    aliquota,
                                    reduzido
                            )
                    );
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
                    "    p.cd_pro id,\n" +
                    "    p.cd_pro ean,\n" +
                    "    1 as qtdembalagem,\n" +
                    "    p.un_pro unidade,\n" +
                    "    coalesce(p.fl_balanca_pro, 0) ebalanca,\n" +
                    "    coalesce(p.val_balanca_pro, 0) validade,\n" +
                    "    coalesce(trim(p.ds_pro), '') || ' ' || coalesce(trim(p.marca_pro), '') || ' ' || coalesce(trim(p.un_pro),'') descricaocompleta,\n" +
                    "    coalesce(trim(p.ds_pro), '') || ' ' || coalesce(trim(p.un_pro),'') descricaoreduzida,\n" +
                    "    p.cdfam_pro mercadologico,\n" +
                    "    coalesce(p.qt_est_min_pro, 0) estoqueminimo,\n" +
                    "    coalesce(p.qt_est_ideal_pro, 0) estoquemaximo,\n" +
                    "    coalesce(p.qt_est_atual_pro, 0) estoque,\n" +
                    "    coalesce(p.markup_pro, 0) margem,\n" +
                    "    coalesce(p.vl_compra_pro, 0) custocomimposto,\n" +
                    "    coalesce(p.vl_com_bruto_pro, 0) custosemimposto,\n" +
                    "    coalesce(p.vl_venda_pro, 0) preco,\n" +
                    "    p.fl_ativo_pro ativo,\n" +
                    "    p.ncm_pro ncm,\n" +
                    "    p.cest_st_pro cest,\n" +
                    "    p.cd_cst_sai_pro,\n" +
                    "    p.vl_aliquota_sai_pro,\n" +
                    "    p.aliquota_reduz_pro,\n" +
                    "    p.cst_pis_sai_pro piscofins_saida,\n" +
                    "    p.cst_pis_ent_pro piscofins_entrada,\n" +
                    "    p.nat_rec_pis_pro piscofins_natrec,\n" +
                    "    p.idfor_pro id_fornecedor\n" +
                    "from\n" +
                    "    produto p\n" +
                    "order by\n" +
                    "    id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("mercadologico"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getInt("ativo") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    
                    int cst = Utils.stringToInt(rst.getString("cd_cst_sai_pro"));
                    double aliquota = Utils.stringToDouble(rst.getString("vl_aliquota_sai_pro"), 0.00);
                    double red = Utils.stringToDouble(rst.getString("aliquota_reduz_pro"), 0.00);
                    
                    String idIcms =  cst + "-" + aliquota + "-" + red;
                    
                    imp.setIcmsDebitoId(idIcms);
                            
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natrec"));
                    imp.setFornecedorFabricante(rst.getString("id_fornecedor"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.id_for id,\n" +
                    "    f.rs_for razao,\n" +
                    "    f.nf_for fantasia,\n" +
                    "    f.cnpj_for,\n" +
                    "    f.ie_for,\n" +
                    "    f.end_for endereco,\n" +
                    "    f.numero_for numero,\n" +
                    "    f.bairro_for bairro,\n" +
                    "    f.cidade_for cidade,\n" +
                    "    f.uf_for uf,\n" +
                    "    f.cep_for cep,\n" +
                    "    f.fone1_for fone1,\n" +
                    "    f.fone2_for fone2,\n" +
                    "    f.contato_for contato,\n" +
                    "    f.email_for email,\n" +
                    "    f.site_for site,\n" +
                    "    f.fl_ativo_for ativo\n" +
                    "from\n" +
                    "    fornecedor f\n" +
                    "order by\n" +
                    "    id"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_for"));
                    imp.setIe_rg(rst.getString("ie_for"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone1"));
                    imp.addTelefone("TELEFONE 2", rst.getString("fone2"));
                    imp.addContato(rst.getString("contato"), "", "", "", TipoContato.COMERCIAL, "");
                    imp.addEmail("CONTATO", rst.getString("email"), TipoContato.COMERCIAL);
                    imp.setAtivo(rst.getBoolean("ativo"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    pf.cdfor_pfor id_fornecedor,\n" +
                    "    pf.cdpro_produto_pfor id_produto,\n" +
                    "    pf.cdprofor_pfor codigoexterno\n" +
                    "from\n" +
                    "    produto_fornecedor pf\n" +
                    "order by\n" +
                    "    1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.cd_pro,\n" +
                    "    p.dt_ini_promocao_pro,\n" +
                    "    p.dt_fim_promocao_pro,\n" +
                    "    p.vl_promocao_pro\n" +
                    "from\n" +
                    "    produto p\n" +
                    "where\n" +
                    "    not p.dt_ini_promocao_pro is null and\n" +
                    "    p.dt_ini_promocao_pro <= current_date and\n" +
                    "    p.dt_fim_promocao_pro >= current_date\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rst.getString("cd_pro"));
                    imp.setDataInicio(rst.getDate("dt_ini_promocao_pro"));
                    imp.setDataFim(rst.getDate("dt_fim_promocao_pro"));
                    imp.setPrecoOferta(rst.getDouble("vl_promocao_pro"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
}
