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
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class GondolaDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Gondola";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.FABRICANTE
        ));
    }
    
    public List<Estabelecimento> getLoja() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select fi_cod id, fi_razsoc razao from gondola.filial")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "    ai_cod id,\n" +
                    "    ai_nom descricao,\n" +
                    "    ai_per_icms icms,\n" +
                    "    ai_per_reduc_icms reducao,\n" +
                    "    ai_flg_tip_sitrib cst\n" +
                    "from \n" +
                    " gondola.aliq_icms")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), 
                            rs.getString("descricao"), 
                            rs.getInt("cst"), 
                            rs.getDouble("icms"), 
                            rs.getDouble("reducao")));
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
                    "  p.pr_cod id,\n" +
                    "  p.pr_cod_altiv idalter,\n" +
                    "  ean.bapr_cod_barra ean,\n" +
                    "  p.um_sig_compra unidadecompra,\n" +
                    "  decode(p.pr_flg_balanca, 'S', 1, 0) balanca,\n" +
                    "  p.um_sig_venda unidadevenda,\n" +
                    "  p.pr_nom descricao,\n" +
                    "  p.pr_nom_reduz descricaoreduzida,\n" +
                    "  p.pr_pesobr pesobruto,\n" +
                    "  p.pr_pesolq pesoliquido,\n" +
                    "  p.pr_flg_peso_variav variavel,\n" +
                    "  decode(p.pr_flg_ativo, 'S', 1, 0) ativo,\n" +
                    "  p.pr_val_preco_custo custosemimposto,\n" +
                    "  decode(p.pr_val_compra_final, 0, \n" +
                    "        p.pr_val_preco_custo, \n" +
                    "        p.pr_val_compra_final) custocomimposto,\n" +
                    "  p.pr_margem_venda margem,\n" +
                    "  prv.pvpr_val_prvda precovenda,\n" +
                    "  est.esla_qtd_estoq estoque,\n" +
                    "  p.pr_dia_valid validade,\n" +
                    "  p.pr_dat_cadstr cadastro,\n" +
                    "  p.depr_cod departamento,\n" +
                    "  p.grpr_cod grupo,\n" +
                    "  p.capr_cod categoria,\n" +
                    "  p.clfi_cod ncm,\n" +
                    "  p.cest_cod cest,\n" +
                    "  p.cpc_cod_ent piscredito,\n" +
                    "  p.cpc_cod_sai pisdebito,\n" +
                    "  p.cpcn_cod_sai naturezareceita,\n" +
                    "  picm.ai_cod idicms,\n" +
                    "  picm.PUAI_PER_MVA_ST percmva,\n" +
                    "  picm.PUAI_PER_REDUC_MVA_ST redmva,\n" +
                    "  picm.PUAI_VAL_PAUTA_ST mva\n" +
                    "from \n" +
                    "  gondola.prduto p\n" +
                    "left join gondola.barra_prduto ean on p.pr_cod = ean.pr_cod\n" +
                    "left join gondola.prvda_prduto prv on p.pr_cod = prv.pr_cod\n" +
                    "left join gondola.prduto_uf_aliq_icms picm on p.pr_cod = picm.pr_cod\n" +
                    "left join gondola.estoq_local_armaz est on p.pr_cod = est.pr_cod and \n" +
                    "     prv.fi_cod = est.fi_cod\n" +
                    "where \n" +
                    "     prv.fi_cod = 1 and \n" +
                    "     est.la_cod =" + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("unidadevenda"));
                    imp.setTipoEmbalagemCotacao(rs.getString("unidadecompra"));
                    imp.seteBalanca(rs.getInt("balanca") == 1);
                    
                    if(imp.isBalanca()) {
                        imp.setEan(rs.getString("idalter"));
                    }
                    
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("pisdebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   pr_cod id,\n" +
                    "   bapr_cod_barra ean,\n" +
                    "   bapr_qtd_unid qtd,\n" +
                    "   um_sig embalagem\n" +
                    "from\n" +
                    "   gondola.barra_prduto")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setQtdEmbalagem(rs.getInt("qtd"));
                    
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
                    "select \n" +
                    "  en_cod idfornecedor,\n" +
                    "  pr_cod idproduto,\n" +
                    "  enpr_cod_prduto_entid externo,\n" +
                    "  um_sig emb,\n" +
                    "  enpr_fator_conv qtdemb\n" +
                    "from \n" +
                    "  gondola.entid_prduto")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("externo"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdemb"));
                    
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
                    "select \n" +
                    "  f.en_cod id,\n" +
                    "  f.en_nom razao,\n" +
                    "  f.en_nom_fantsa fantasia,\n" +
                    "  f.en_cpf_cnpj cnpj,\n" +
                    "  f.en_rg_insest ie,\n" +
                    "  f.en_dat_cadstr cadastro,\n" +
                    "  f.en_flg_ativo ativo,\n" +
                    "  ened_ddd_tel ddd_tel,\n" +
                    "  ened_ddd_fax ddd_fax,\n" +
                    "  ened_ddd_cel ddd_cel,\n" +
                    "  ened_ender_num numero,\n" +
                    "  ened_ender endereco,\n" +
                    "  ened_comple_ender complemento,\n" +
                    "  ened_nom_bairro bairro,\n" +
                    "  ened_caixa_postal caixa_postal,\n" +
                    "  ened_nom_cidade cidade,\n" +
                    "  ened_num_cep cep,\n" +
                    "  ened_num_tel telefone,\n" +
                    "  ened_num_fax fax,\n" +
                    "  e.uf_sig uf,\n" +
                    "  ened_num_cel celular,\n" +
                    "  ened_email email,\n" +
                    "  ened_home_page home_page\n" +
                    "from \n" +
                    "  gondola.entid f\n" +
                    "left join gondola.entid_ender e on f.en_cod = e.en_cod\n" +
                    "where \n" +
                    "  f.en_flg_fornec = 'S' and \n" +
                    "  e.ened_flg_tip = 'E'")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setDatacadastro(rs.getDate("cadastro"));
                    imp.setAtivo("S".equals(rs.getString("ativo")));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTel_principal(rs.getString("ddd_tel") + rs.getString("telefone"));
                  
                    String email = rs.getString("email");
                    
                    if(email != null && !"".equals(email)) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "  f.en_cod id,\n" +
                    "  f.en_nom razao,\n" +
                    "  f.en_nom_fantsa fantasia,\n" +
                    "  f.en_cpf_cnpj cnpj,\n" +
                    "  f.en_rg_insest ie,\n" +
                    "  f.en_dat_cadstr cadastro,\n" +
                    "  c.encl_val_limite_cred limite,\n" +
                    "  c.encl_val_limite_cred_cheque limitecheque,\n" +
                    "  f.en_flg_ativo ativo,\n" +
                    "  ened_ddd_tel ddd_tel,\n" +
                    "  ened_ddd_fax ddd_fax,\n" +
                    "  ened_ddd_cel ddd_cel,\n" +
                    "  ened_ender_num numero,\n" +
                    "  ened_ender endereco,\n" +
                    "  ened_comple_ender complemento,\n" +
                    "  ened_nom_bairro bairro,\n" +
                    "  ened_caixa_postal caixa_postal,\n" +
                    "  ened_nom_cidade cidade,\n" +
                    "  ened_num_cep cep,\n" +
                    "  ened_num_tel telefone,\n" +
                    "  ened_num_fax fax,\n" +
                    "  e.uf_sig uf,\n" +
                    "  ened_num_cel celular,\n" +
                    "  ened_email email,\n" +
                    "  ened_home_page home_page\n" +
                    "from \n" +
                    "  gondola.entid f\n" +
                    "left join gondola.entid_client c on f.en_cod = c.en_cod\n" +
                    "left join gondola.entid_ender e on f.en_cod = e.en_cod\n" +
                    "where \n" +
                    "  f.en_flg_client = 'S' and \n" +
                    "  e.ened_flg_tip = 'E' and \n" +
                    "  c.em_cod = 1")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setAtivo("S".equals(rs.getString("ativo")));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("ddd_tel") + rs.getString("telefone"));
                    imp.setCelular(rs.getString("ddd_cel") + rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "      v_df.fi_cod empresa,\n" +
                    "      v_df.df_num_seq id,\n" +
                    "      nvl(v_df.df_num_docto, v_df.df_num_seq) doc, \n" +
                    "      v_df.df_dat_emis dtemissao,\n" +
                    "      v_df.df_dat_entrad dtentrada,\n" +
                    "      v_df.df_dat_compet dtcomp,\n" +
                    "      v_df.padf_dat_vencto vencimento,\n" +
                    "      v_df.df_val_docto valor,\n" +
                    "      v_df.en_cod idcliente,\n" +
                    "      v_df.en_nom razao,\n" +
                    "      v_df.saldo_atual valor,\n" +
                    "      v_df.df_observ obs\n" +
                    "  from \n" +
                    "      gondola.v_sbi_doc_financ v_df, \n" +
                    "      gondola.convn_deb_ccor conv, \n" +
                    "      gondola.convn_deb_ccor_condic_pagto convcp\n" +
                    "where conv.codc_cod(+) = convcp.codc_cod\n" +
                    "  and v_df.cp_cod = convcp.cp_cod(+)\n" +
                    "  and v_df.df_flg_tip_lanc = 'R'\n" +
                    "  and v_df.saldo_capital > 0\n" +
                    "  and v_df.fi_cod = " + getLojaOrigem())) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setNumeroCupom(rs.getString("doc"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("obs"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
