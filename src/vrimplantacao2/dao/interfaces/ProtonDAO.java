package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class ProtonDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "PROTON";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.SUGESTAO_COTACAO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.OFERTA,
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA
        ));
    }
    
    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "       tund_unidade_pk id,\n" +
                    "       tund_fantasia fantasia\n" +
                    "from \n" +
                    "       TUND_UNIDADE")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  distinct\n" +
                    "  g.tmer_grupo_mercadoria_pk merc1,\n" +
                    "  g.tmer_descricao descmerc1,\n" +
                    "  s.tmer_subgrupo_mercadoria_pk merc2,\n" +
                    "  s.tmer_descricao descmerc2\n" +
                    "from\n" +
                    "  tmer_mercadoria p\n" +
                    "join tmer_grupo_mercadoria g \n" +
                    "     on p.tmer_grupo_mercadoria_fk = g.tmer_grupo_mercadoria_pk\n" +
                    "join tmer_subgrupo_mercadoria s \n" +
                    "     on p.tmer_subgrupo_mercadoria_fk = s.tmer_subgrupo_mercadoria_pk\n" +
                    "order by\n" +
                    "  2, 4")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc2"));
                    imp.setMerc3Descricao(rs.getString("descmerc2"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "  tmer_familia_pk id,\n" +
                    "  tmer_descricao descricao \n" +
                    "from \n" +
                    "  tmer_familia\n" +
                    "where            \n" +
                    "  tmer_unidade_fk_pk = 1\n" +
                    "order by\n" +
                    "  2")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  p.tmer_codigo_pri_pk id,\n" +
                    "  p.tmer_codigo_merc_balanca_ukn codigo_balanca,\n" +
                    "  p.tmer_tipo_quantidade pesavel,\n" +
                    "  p.tmer_nome descricaocompleta,\n" +
                    "  p.tmer_grupo_mercadoria_fk merc1,\n" +
                    "  p.tmer_subgrupo_mercadoria_fk merc2,\n" +
                    "  e.tmer_familia_fkn familia,\n" +
                    "  p.tmer_unidade_fisica_fk unidade,\n" +
                    "  p.tmer_codigo_barras_ukn ean,\n" +
                    "  p.tmer_qtde_emb_venda qtdembalagem_venda,\n" +
                    "  p.tmer_qtde_emb_forn qtdembalagem_compra,\n" +
                    "  p.tmer_data_cadastro datacadastro,\n" +
                    "  p.tmer_peso_liquido peso_liquido,\n" +
                    "  p.tmer_peso_bruto peso_bruto,\n" +
                    "  p.tmer_codigo_ncm_fkn ncm,\n" +
                    "  ncm.tmer_nat_receita natureza_receita,\n" +
                    "  p.tmer_codigo_cest_fkn cest,\n" +
                    "  e.tmer_prazo_mercadoria validade_estoque,\n" +
                    "  e.tmer_preco_venda preco_venda,\n" +
                    "  e.tmer_custo_ultimo_real_final custo,\n" +
                    "  e.tmer_margem_lucro margem,\n" +
                    "  e.tmer_estoque_atual estoque,\n" +
                    "  e.tmer_estoque_minimo estoque_minimo,\n" +
                    "  e.tmer_estoque_maximo estoque_maximo,\n" +
                    "  e.tmer_ativo_compra ativo_compra,\n" +
                    "  e.tmer_ativo_venda ativo_venda,\n" +
                    "  e.tmer_data_alteracao dataalteracao,\n" +
                    "  e.tmer_cst_pis cst_pis,\n" +
                    "  e.tmer_cst_cofins cst_cofins,\n" +
                    "  e.tmer_grupo_icms_entrada_fk grupo_icms_entrada,\n" +
                    "  e.tmer_grupo_icms_saida_fk grupo_icms_saida,\n" +
                    "  icms_deb.icms icms_saida,\n" +
                    "  icms_deb.cst icms_cst_saida,\n" +
                    "  icms_deb.icms_reducao icms_red_saida,\n" +
                    "  icms_cred.icms icms_entrada,\n" +
                    "  icms_cred.cst cst_entrada,\n" +
                    "  icms_cred.icms_reducao icms_red_entrada\n" +
                    "from\n" +
                    "  tmer_mercadoria p\n" +
                    "join tmer_estoque e on p.tmer_codigo_pri_pk = e.tmer_codigo_pri_fk_pk and\n" +
                    "  p.tmer_codigo_sec_pk = e.tmer_codigo_sec_fk_pk\n" +
                    "left join tmer_ncm ncm on p.tmer_codigo_ncm_fkn = ncm.tmer_codigo_ncm_pk\n" +
                    "left join\n" +
                    "     (select \n" +
                    "        t.tbas_grupo_icms_fk_pk grp,\n" +
                    "        t.tbas_situacao_tributaria st,\n" +
                    "        t.tbas_codigo_tributacao cst,\n" +
                    "        t.tbas_aliquota_icms_normal icms,\n" +
                    "        t.tbas_reducao_base_normal icms_reducao,\n" +
                    "        t.tbas_unidade_fk_pk loja \n" +
                    "      from \n" +
                    "        DBAUSER.TBAS_ALIQUOTA_ICMS t\n" +
                    "      where\n" +
                    "        tbas_uf_origem_fk_pk = 'BA' and\n" +
                    "        tbas_uf_destino_fk_pk = 'BA' and\n" +
                    "        tbas_tipo_movimentacao_pk = 'VM' and\n" +
                    "        tbas_classificacao_fiscal_pk = 'NO') icms_deb \n" +
                    "                         on e.tmer_grupo_icms_saida_fk = icms_deb.grp and\n" +
                    "     icms_deb.loja = e.tmer_unidade_fk_pk\n" +
                    "left join\n" +
                    "     (select \n" +
                    "        t.tbas_grupo_icms_fk_pk grp,\n" +
                    "        t.tbas_situacao_tributaria st,\n" +
                    "        t.tbas_codigo_tributacao cst,\n" +
                    "        t.tbas_aliquota_icms_normal icms,\n" +
                    "        t.tbas_reducao_base_normal icms_reducao,\n" +
                    "        t.tbas_unidade_fk_pk loja \n" +
                    "      from \n" +
                    "        DBAUSER.TBAS_ALIQUOTA_ICMS t\n" +
                    "      where\n" +
                    "        tbas_uf_origem_fk_pk = 'BA' and\n" +
                    "        tbas_uf_destino_fk_pk = 'BA' and\n" +
                    "        tbas_tipo_movimentacao_pk = 'VM' and\n" +
                    "        tbas_classificacao_fiscal_pk = 'NO') icms_cred\n" +
                    "                         on e.tmer_grupo_icms_entrada_fk = icms_cred.grp and\n" +
                    "    icms_cred.loja = e.tmer_unidade_fk_pk \n" +
                    "where\n" +
                    "  e.tmer_unidade_fk_pk = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "  1")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.seteBalanca("P".equals(rs.getString("pesavel")));
                    imp.setEan(rs.getString("ean"));
                    if(imp.isBalanca() && rs.getString("codigo_balanca") != null){ 
                        imp.setEan(rs.getString("codigo_balanca"));
                    }
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rs.getString("familia"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem_venda"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagem_compra"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("natureza_receita"));
                    imp.setCest(rs.getString("cest"));
                    imp.setValidade(rs.getInt("validade_estoque"));
                    imp.setPrecovenda(rs.getDouble("preco_venda"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_maximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setSituacaoCadastro("S".equals(rs.getString("ativo_venda")) ? 1 : 0);
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setPiscofinsCstCredito(rs.getString("cst_pis"));
                    imp.setIcmsAliqSaida(rs.getDouble("icms_saida"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_red_saida"));
                    imp.setIcmsCstSaida(rs.getInt("icms_cst_saida"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_entrada"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icms_red_entrada"));
                    imp.setIcmsCstEntrada(rs.getInt("icms_cst_entrada"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  tlnk_fornec_mercad_fk_pk id_fornecedor,\n" +
                    "  tlnk_mercad_pri_fk_pk id_produto\n" +
                    "from\n" +
                    "  tlnk_fornecedor_mercadoria\n" +
                    "where\n" +
                    "  tlnk_unidade_fk_pk = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  f.tfor_fornecedor_pk id,\n" +
                    "  f.tfor_nome_razao razao,\n" +
                    "  f.tfor_fantasia fantasia,\n" +
                    "  f.tfor_num_documento cnpj,\n" +
                    "  f.tfor_inscr_estadual ie,\n" +
                    "  f.tfor_endereco endereco,\n" +
                    "  f.tfor_endereco_numero nr,\n" +
                    "  f.tfor_endereco_logradouro logradouro,\n" +
                    "  f.tfor_bairro bairro,\n" +
                    "  f.tfor_complemento complemento,\n" +
                    "  c.tloc_cidade_cep_pk cidade_ibge,\n" +
                    "  c.tloc_nome cidade,\n" +
                    "  c.tloc_uf_fk uf,\n" +
                    "  f.tfor_fone_ddd || '' || \n" +
                    "  f.tfor_fone_prefixo || '' || f.tfor_fone_final telefone,\n" +
                    "  f.tfor_fone2_ddd || '' || \n" +
                    "  f.tfor_fone2_prefixo || '' || f.tfor_fone2_final telefone2,\n" +
                    "  f.tfor_fax_ddd || '' || \n" +
                    "  f.tfor_fax_prefixo || '' || f.tfor_fax_final fax,\n" +
                    "  f.tfor_email email,\n" +
                    "  f.tfor_data_cadastro datacadastro\n" +
                    "from\n" +
                    "  tfor_fornecedor f\n" +
                    "left join \n" +
                    "  tloc_cidade_cep c on f.tfor_cidade_cep_fk = c.tloc_cidade_cep_pk")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("logradouro"));
                    imp.setNumero(rs.getString("nr"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if(rs.getString("telefone2") != null && !"".equals(rs.getString("telefone2"))) {
                        imp.addContato("1", "TELEFONE2", rs.getString("telefone2"), null, TipoContato.COMERCIAL, null);
                    }
                    if(rs.getString("fax") != null && !"".equals(rs.getString("fax"))) {
                        imp.addContato("2", "FAX", rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    if(rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        imp.addContato("3", "EMAIL", null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
