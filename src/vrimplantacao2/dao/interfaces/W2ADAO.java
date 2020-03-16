package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class W2ADAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "W2A";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.ICMS,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.OFERTA
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    " codigo_do_icms as id,\n" +
                    " descricao_do_icms as descricao,\n" +
                    " aliquota,\n" +
                    " situacao_tributaria_tab_b as cst,\n" +
                    " percentual_base as reducao,\n" +
                    " percentual_base_st,\n" +
                    " aliquota_st \n" +
                    "from \n" +
                    " icms \n" +
                    "where \n" +
                    " codigo_do_icms in \n" +
                    "    (select codigo_do_icms from produtos)")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"), 
                            rs.getString("descricao"), 
                            rs.getInt("cst"), 
                            rs.getDouble("aliquota"), 
                            rs.getDouble("reducao")));
                }
            }
            
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    " codigo_do_icms as id,\n" +
                    " descricao_do_icms as descricao,\n" +
                    " aliquota,\n" +
                    " situacao_tributaria_tab_b as cst,\n" +
                    " percentual_base as reducao,\n" +
                    " percentual_base_st,\n" +
                    " aliquota_st \n" +
                    "from \n" +
                    " icms \n" +
                    "where \n" +
                    " codigo_do_icms in \n" +
                    "    (select codigo_icms_compra from produtos)")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"), 
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }
        
        return result;
    }
  
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    " codigo_da_empresa as id,\n" +
                    " nome_fantasia\n" +
                    "from \n" +
                    " empresas"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("nome_fantasia")));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    " distinct\n" +
                    " p.codigo_do_grupo as merc1,\n" +
                    " g.nome_do_grupo as descmerc1,\n" +
                    " s.codigo_subgrupo as merc2,\n" +
                    " s.nome_subgrupo as descmerc2\n" +
                    "from\n" +
                    " produtos p,\n" +
                    " grupos_de_produtos g,\n" +
                    " subgrupo s\n" +
                    "where\n" +
                    "  p.codigo_do_grupo = g.codigo_do_grupo and\n" +
                    "  p.codigo_subgrupo = s.codigo_subgrupo\n" +
                    "order by\n" +
                    "  2, 4"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());                   
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    " CInt(p.codigo_do_produto) as id,\n" +
                    " p.codigo_de_barras as ean,\n" +
                    " p.descricao_do_produto as descricaocompleta,\n" +
                    " p.descricao_abreviada as descricaoreduzida,\n" +
                    " p.codigo_do_grupo as merc1,\n" +
                    " p.codigo_subgrupo as merc2,\n" +
                    " p.unidade_de_medida as embalagem,\n" +
                    " p.valor_custo as custocomimposto,\n" +
                    " p.valor_custo_total as custototal,\n" +
                    " p.valor_venda as precovenda,\n" +
                    " p.preco_real_ant as precovendaant,\n" +
                    " p.valor_atacado as precoatacado,\n" +
                    " p.quantidade_estoque as estoque,\n" +
                    " p.codigo_do_icms as idicms_debito,\n" +
                    " p.codigo_icms_compra as idicms_credito,\n" +
                    " p.ativo as situacaocadastro,\n" +
                    " p.data_inclusao as datacadastro,\n" +
                    " p.balanca,\n" +
                    " p.validade1,\n" +
                    " p.codigo_da_classificacao as ncm,\n" +
                    " p.markup as margem,\n" +
                    " p.peso_bruto,\n" +
                    " p.peso_liquido,\n" +
                    " p.codigo_do_pis as idpis_debito,\n" +
                    " ps.situacao_tributaria_pis as pis_debito,\n" +
                    " p.codigo_pis_compra as pis_credito,\n" +
                    " p.codigo_do_cofins as idcofins_debito,\n" +
                    " p.codigo_cofins_compra as cofins_credito,\n" +
                    " p.mva_st as mva,\n" +
                    " p.aliquotastret as icms_st,\n" +
                    " p.codigo_do_cest as cest\n" +
                    "from\n" +
                    " produtos as p\n" +
                    "left join pis as ps on p.codigo_do_pis = ps.codigo_do_pis\n" +
                    "order by\n" +
                    " 3")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(String.valueOf(rs.getInt("id")));
                    imp.setEan(String.valueOf(rs.getLong("ean")));
                    imp.seteBalanca(rs.getBoolean("balanca"));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricaocompleta")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("descricaoreduzida")));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    
                    imp.setIcmsDebitoId(rs.getString("idicms_debito"));
                    imp.setIcmsCreditoId(rs.getString("idicms_credito"));
                    
                    imp.setSituacaoCadastro(rs.getBoolean("situacaocadastro") == true ? 1 : 0);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValidade(rs.getInt("validade1"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_debito"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPautaFiscalId(imp.getImportId());
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    " p.codigo_do_produto as id,\n" +
                    " p.codigo_de_barras as ean,\n" +
                    " p.codigo_do_icms as idicms_debito,\n" +
                    " p.codigo_icms_compra as idicms_credito,\n" +
                    " p.codigo_da_classificacao as ncm,\n" +
                    " p.mva_st as mva,\n" +
                    " p.codigo_do_cest as cest\n" +
                    "from\n" +
                    " produtos as p\n" +
                    "where\n" +
                    " p.mva_st > 0")) {
                while(rs.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setIva(rs.getDouble("mva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setAliquotaDebitoId(rs.getString("idicms_debito"));
                    imp.setAliquotaDebitoForaEstadoId(imp.getAliquotaDebitoId());
                    imp.setAliquotaCreditoId(rs.getString("idicms_credito"));
                    imp.setAliquotaCreditoForaEstadoId(imp.getAliquotaCreditoId());
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    " f.codigo_do_fornecedor as id,\n" +
                    " f.nome,\n" +
                    " f.nome_fantasia,\n" +
                    " f.fone,\n" +
                    " f.endereco,\n" +
                    " f.numero,\n" +
                    " f.complemento,\n" +
                    " f.bairro,\n" +
                    " c.nome_da_cidade as municipio,\n" +
                    " c.uf,\n" +
                    " f.cep,\n" +
                    " f.cgc,\n" +
                    " f.inscricao_estadual,\n" +
                    " f.contato,\n" +
                    " f.fax,\n" +
                    " f.website,\n" +
                    " f.cpf,\n" +
                    " f.rg,\n" +
                    " f.email_fiscal,\n" +
                    " f.inscricao_suframa\n" +
                    "from\n" +
                    " fornecedores f\n" +
                    "left join cidades c on \n" +
                    "    f.codigo_da_cidade = c.codigo_da_cidade")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("nome_fantasia")));
                    imp.setTel_principal(rs.getString("fone"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setCnpj_cpf(rs.getString("cgc"));
                    if(imp.getCnpj_cpf() == null && "".equals(imp.getCnpj_cpf())) {
                        imp.setCnpj_cpf(rs.getString("rg"));
                    }
                    imp.setIe_rg(rs.getString("inscricao_estadual"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    " codigo_do_produto as idproduto,\n" +
                    " codigo_do_fornecedor as idfornecedor,\n" +
                    " fornecedor_codigo as codigoexterno \n" +
                    "from \n" +
                    " fornecedores_do_produto")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    " data_inicial_promo as datainicio,\n" +
                    " data_final_promo as datatermino,\n" +
                    " codigo_do_produto as idproduto,\n" +
                    " valor_promo as precooferta,\n" +
                    " valor_venda as preconormal\n" +
                    "from\n" +
                    " produtos\n" +
                    "where\n" +
                    " data_inicial_promo is not null and\n" +
                    " data_final_promo > Date()\n" +
                    "order by\n" +
                    " data_final_promo")) {
                while(rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datatermino"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
