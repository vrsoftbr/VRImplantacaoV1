package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class ResultMaisDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    private static final Logger LOG = Logger.getLogger(ResultMaisDAO.class.getName());

    @Override
    public String getSistema() {
        return "RMSistemas" + complemento;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.PRODUTOS_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
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
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.OFERTA,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.CODIGO_BENEFICIO
        ));
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	st || '-' || valor_taxa || '-' || valor_reducao codigo,\n"
                    + "	valor_taxa || '%' ||\n"
                    + "	case\n"
                    + "	  when st = '20' then ' RED'\n"
                    + "	  when st = '60' then ' SUBS'\n"
                    + "	  else ''\n"
                    + "	end descricao,\n"
                    + "	st cst,\n"
                    + "	valor_taxa aliquota,\n"
                    + "	valor_reducao reducao\n"
                    + "from\n"
                    + "	produto_tributo pt\n"
                    + "order by 1,2"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getInt("aliquota"),
                            rs.getInt("reducao")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cd_empresa id,\n"
                    + " razao_social nome\n"
                    + "from empresa\n"
                    + "	order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getInt("id") + " - " + rst.getString("nome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cd_grupo_vinculo_preco id,\n"
                    + "	nome\n"
                    + "from grupo_vinculo_preco\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("nome"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cd_grupo_produto m1,\n"
                    + "	descricao merc1,\n"
                    + "	cd_grupo_produto m2,\n"
                    + "	descricao merc2,\n"
                    + "	cd_grupo_produto m3,\n"
                    + "	descricao merc3\n"
                    + "from grupo_produto\n"
                    + " order by 1,3,5"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("m1"));
                    imp.setMerc1Descricao(rs.getString("merc1"));
                    imp.setMerc2ID(rs.getString("m2"));
                    imp.setMerc2Descricao(rs.getString("merc2"));
                    imp.setMerc3ID(rs.getString("m3"));
                    imp.setMerc3Descricao(rs.getString("merc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + " p.codigo, \n"        
                    + "	ncm,\n"
                    + "	p_mva_st per_mva,\n"
                    + "	p_st_ret aliquota,\n"
                    + "	p_red_bc_efet reducao\n"
                    + "from\n"
                    + "	produto p\n"
                    + "left join tributo t on p.cd_tributo = t.cd_tributo\n"
                    + "where\n"
                    + "	p_mva_st > 0\n"
                    + "order by\n"
                    + "	p.cd_produto"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rst.getString("codigo"));
                    imp.setTipoIva(TipoIva.PERCENTUAL);
                    imp.setIva(rst.getDouble("per_mva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rst.getString("ncm"));

                    // DÉBITO
                    if ((rst.getDouble("aliquota") > 0) && (rst.getDouble("reducao") == 0)) {

                        imp.setAliquotaDebito(0, rst.getDouble("aliquota"), rst.getDouble("reducao"));
                        imp.setAliquotaDebitoForaEstado(0, rst.getDouble("aliquota"), rst.getDouble("reducao"));

                    } else if ((rst.getDouble("aliquota") > 0) && (rst.getDouble("reducao") > 0)) {

                        imp.setAliquotaDebito(20, rst.getDouble("aliquota"), rst.getDouble("reducao"));
                        imp.setAliquotaDebitoForaEstado(20, rst.getDouble("aliquota"), rst.getDouble("reducao"));

                    }

                    // CRÉDITO
                    if ((rst.getDouble("aliquota") > 0) && (rst.getDouble("reducao") == 0)) {

                        imp.setAliquotaCredito(0, rst.getDouble("aliquota"), rst.getDouble("reducao"));
                        imp.setAliquotaCreditoForaEstado(0, rst.getDouble("aliquota"), rst.getDouble("reducao"));

                    } else if ((rst.getDouble("aliquota") > 0) && (rst.getDouble("reducao") > 0)) {

                        imp.setAliquotaCredito(20, rst.getDouble("aliquota"), rst.getDouble("reducao"));
                        imp.setAliquotaCreditoForaEstado(20, rst.getDouble("aliquota"), rst.getDouble("reducao"));

                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutosBalanca() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with est as (\n"
                    + "select\n"
                    + "	cd_produto idproduto,\n"
                    + "	saldo_fisico estoque\n"
                    + "from\n"
                    + "	saldo_prod sp\n"
                    + "where\n"
                    + "	ano_mes = (select max(ano_mes) from saldo_prod est where sp.cd_produto = est.cd_produto)\n"
                    + "order by cd_produto)\n"
                    + " select\n"
                    + "	p.cd_produto idproduto,\n"
                    + "	p.codigo,\n"
                    + " p.ean, \n"        
                    + "	upper(p.descricao) descricao,\n"
                    + "	u.simbolo embalagem,\n"
                    + "	case\n"
                    + "		when length(p.codigo) <= 6 then 1\n"
                    + "		else 0\n"
                    + "	end e_balanca,\n"
                    + "	cd_grupo merc1,\n"
                    + "	cd_grupo merc2,\n"
                    + "	cd_grupo merc3,\n"
                    + "	round(perc_lucro, 2) margem,\n"
                    + "	pr_compra custosemimposto,\n"
                    + "	pr_custo custocomimposto,\n"
                    + "	pr_venda precovenda,\n"
                    + "	situacao situacaocadastro,\n"
                    + "	dt_cadastro datacadastro,\n"
                    + "	p.dh_ult_alteracao dataalteracao,\n"
                    + " est.estoque estoque,\n"
                    + "	est_minimo estoquemin,\n"
                    + "	est_maximo estoquemax,\n"
                    + "	peso,\n"
                    + "	pt.valor_taxa aliqicms,\n"
                    + "	pt.st cst,\n"
                    + "	pt.valor_reducao reducao,\n"
                    + "	p.cod_pis pc_saida,\n"
                    + "	p.cod_pis_ent pc_entrada,\n"
                    + "	ncm,\n"
                    + "	t.cest, \n"
                    + " pt.st || '-' || pt.valor_taxa || '-' || pt.valor_reducao as codigo_trib, \n"
                    + " bene.codigo as codbeneficio \n"        
                    + "from\n"
                    + "	produto p\n"
                    + "left join est on est.idproduto = p.cd_produto\n"
                    + "left join unidade u on u.cd_unidade = p.cd_unidade\n"
                    + "left join tributo t on p.cd_tributo = t.cd_tributo\n"
                    + "left join produto_tributo pt on p.cd_produto = pt.cd_produto\n"
                    + "left join beneficio_fiscal bene on bene.cd_beneficio_fiscal = p.cd_beneficio_fiscal \n"        
                    + "where char_length(p.codigo) = 6 \n"
                    + "and p.codigo != '000000' \n"
                    + "and p.codigo like '%0' \n"        
                    + "order by\n"
                    + "	p.codigo"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                                        
                    String ean = rs.getString("codigo");                    
                    
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    
                    if (ean!= null && !ean.trim().isEmpty()) {
                        
                        if (ean.trim().length() == 6 && !"000000".equals(ean.trim())) {
                            
                            if (ean.startsWith("00") && ean.endsWith("0")) {
                                imp.setEan(ean.substring(2, ean.trim().length() - 1));
                            } else if (ean.startsWith("0") && ean.endsWith("0")) {
                                imp.setEan(ean.substring(1, ean.trim().length() - 1));
                            } else if (ean.endsWith("0")) {
                                imp.setEan(ean.substring(0, ean.trim().length() - 1));
                            } 
                        }
                    }
                    
                    imp.seteBalanca(rs.getBoolean("e_balanca"));
                                        
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setPesoBruto(rs.getDouble("peso"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setPiscofinsCstCredito(rs.getString("pc_entrada"));
                    imp.setPiscofinsCstDebito(rs.getString("pc_saida"));

                    imp.setIcmsDebitoId(rs.getString("codigo_trib"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("codigo_trib"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("codigo_trib"));
                    imp.setIcmsCreditoId(rs.getString("codigo_trib"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("codigo_trib"));
                    imp.setIcmsConsumidorId(rs.getString("codigo_trib"));
                    
                    imp.setBeneficio(rs.getString("codbeneficio"));
                    
                    result.add(imp);
                }
                
                return result;
            }
        }
    }
       
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with est as (\n" +
                    "	select\n" +
                    "		cd_produto idproduto,\n" +
                    "		saldo_fisico estoque\n" +
                    "	from\n" +
                    "		saldo_prod sp\n" +
                    "	where\n" +
                    "		ano_mes = (\n" +
                    "		select\n" +
                    "			max(ano_mes)\n" +
                    "		from\n" +
                    "			saldo_prod est\n" +
                    "		where\n" +
                    "			sp.cd_produto = est.cd_produto)\n" +
                    "	order by\n" +
                    "		cd_produto\n" +
                    ")\n" +
                    "select\n" +
                    "	p.cd_produto idproduto,\n" +
                    "	p.codigo,\n" +
                    "	p.ean,\n" +
                    "	upper(p.descricao) descricao,\n" +
                    "	u.simbolo embalagem,\n" +
                    "	case\n" +
                    "		when length(p.codigo) <= 6 then 1\n" +
                    "		else 0\n" +
                    "	end e_balanca,\n" +
                    "	cd_grupo merc1,\n" +
                    "	cd_grupo merc2,\n" +
                    "	cd_grupo merc3,\n" +
                    "	p.cd_grupo_vinculo_preco idfamilia,\n" +
                    "	round(perc_lucro, 2) margem,\n" +
                    "	pr_compra custosemimposto,\n" +
                    "	pr_custo custocomimposto,\n" +
                    "	pr_venda precovenda,\n" +
                    "	situacao situacaocadastro,\n" +
                    "	dt_cadastro datacadastro,\n" +
                    "	p.dh_ult_alteracao dataalteracao,\n" +
                    "	est.estoque estoque,\n" +
                    "	est_minimo estoquemin,\n" +
                    "	est_maximo estoquemax,\n" +
                    "	peso,\n" +
                    "	pt.valor_taxa aliqicms,\n" +
                    "	pt.st cst,\n" +
                    "	pt.valor_reducao reducao,\n" +
                    "	p.cod_pis pc_saida,\n" +
                    "	p.cod_pis_ent pc_entrada,\n" +
                    "	ncm,\n" +
                    "	t.cest,\n" +
                    "	pt.st || '-' || pt.valor_taxa || '-' || pt.valor_reducao as codigo_trib, \n" +
                    "   bene.codigo as codbeneficio \n" + 
                    "from\n" +
                    "	produto p\n" +
                    "	left join est on\n" +
                    "		est.idproduto = p.cd_produto\n" +
                    "	left join unidade u on\n" +
                    "		u.cd_unidade = p.cd_unidade\n" +
                    "	left join tributo t on\n" +
                    "		p.cd_tributo = t.cd_tributo\n" +
                    "	left join produto_tributo pt on\n" +
                    "		p.cd_produto = pt.cd_produto\n" +
                    "   left join beneficio_fiscal bene on bene.cd_beneficio_fiscal = p.cd_beneficio_fiscal " + 
                    "order by\n" +
                    "	p.codigo"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));                    
                    imp.setEan(rs.getString("codigo"));                    
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("embalagem"));                    
                    imp.seteBalanca(rs.getBoolean("e_balanca"));                                        
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setPesoBruto(rs.getDouble("peso"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setPiscofinsCstCredito(rs.getString("pc_entrada"));
                    imp.setPiscofinsCstDebito(rs.getString("pc_saida"));

                    imp.setIcmsDebitoId(rs.getString("codigo_trib"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("codigo_trib"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("codigo_trib"));
                    imp.setIcmsCreditoId(rs.getString("codigo_trib"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("codigo_trib"));
                    imp.setIcmsConsumidorId(rs.getString("codigo_trib"));
                    
                    imp.setBeneficio(rs.getString("codbeneficio"));
                    
                    result.add(imp);
                }
                
                return result;
            }
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cd_pessoa id,\n"
                    + "	case when cnpj = '' then cpf else cnpj end cnpj_cpf,\n"
                    + "	inscricao_e inscricaoestadual,\n"
                    + "	inscricao_m inscricaomunicipal,\n"
                    + "	nome razao,\n"
                    + "	n_fantasia fantasia,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	upper(c.descricao) cidade,\n"
                    + "	c.cod_ibge cidade_ibge,\n"
                    + "	c.estado,\n"
                    + "	cep,\n"
                    + "	dt_cadastro datacadastro,\n"
                    + "	situacao,\n"
                    + "	telefone,\n"
                    + "	fax,\n"
                    + "	email,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	pessoa p\n"
                    + "	join cidade c on c.cd_cidade = p.cd_cidade\n"
                    + "where\n"
                    + "	fornecedor = true\n"
                    + "order by cd_pessoa"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rs.getString("inscricaomunicipal"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("cidade_ibge"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo(rs.getBoolean("situacao"));
                    imp.setObservacao(rs.getString("observacao"));

                    imp.setTel_principal(Utils.formataNumero(rs.getString("telefone")));
                    if (rs.getString("fax") != null && !rs.getString("fax").isEmpty()) {
                        imp.addContato("1", "FAX", rs.getString("fax"), "", TipoContato.COMERCIAL, "");
                    }
                    if (rs.getString("email") != null && !rs.getString("email").isEmpty()) {
                        imp.addContato("2", "EMAIL", "", "", TipoContato.COMERCIAL, rs.getString("email"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	pf.cd_pessoa fornecedor,\n"
                    + "	p.codigo idproduto,\n"
                    + "	cd_fornec_prod codexterno\n"
                    + "from\n"
                    + "	fornec_prod pf\n"
                    + " join produto p on p.cd_produto = pf.cd_produto \n"        
                    + "	join pessoa f on f.cd_pessoa = pf.cd_pessoa\n"
                    + "order by pf.cd_pessoa, p.cd_produto")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("fornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codexterno"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cd_pessoa id,\n"
                    + "	case when cnpj = '' then cpf else cnpj end cnpj_cpf,\n"
                    + "	inscricao_e inscricaoestadual,\n"
                    + "	inscricao_m inscricaomunicipal,\n"
                    + "	nome razao,\n"
                    + "	n_fantasia fantasia,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	upper(c.descricao) cidade,\n"
                    + " c.cod_ibge cidade_ibge,\n"
                    + "	c.estado,\n"
                    + "	cep,\n"
                    + "	dt_cadastro datacadastro,\n"
                    + "	dt_nasc datanascimento,\n"
                    + "	situacao,\n"
                    + "	telefone,\n"
                    + "	fax,\n"
                    + "	email,\n"
                    + "	nome_mae nomemae,\n"
                    + "	nome_pai nomepai,\n"
                    + " emp_trab empresa,\n"
                    + " fone_emp empresa_tel,\n"
                    + " cargo,\n"
                    + " remuneracao salario,\n"
                    + " vl_limite limite,\n"
                    + " conjuge,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	pessoa p\n"
                    + "	join cidade c on c.cd_cidade = p.cd_cidade\n"
                    + "where\n"
                    + "	cliente = true\n"
                    + "order by cd_pessoa")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rs.getString("inscricaomunicipal"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getInt("cidade_ibge"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));

                    imp.setAtivo(rs.getInt("situacao") == 1);
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));

                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("empresa_tel"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.EXCECAO) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.codigo idproduto,\n"
                    + "	ncm,\n"
                    + "	p_mva_st per_mva,\n"
                    + "	pt.st cst_debito,\n"
                    + "	pt.valor_taxa aliquota_debito,\n"
                    + "	pt.valor_reducao reducao_debito,\n"
                    + "	case\n"
                    + "		when pt.valor_reducao > 0 then 20\n"
                    + "		else 0\n"
                    + "	end cst_credito,\n"
                    + "	p_st_ret aliquota_credito,\n"
                    + "	p_red_bc_efet reducao_credito\n"
                    + "from\n"
                    + "	produto p\n"
                    + "left join tributo t on p.cd_tributo = t.cd_tributo\n"
                    + "left join produto_tributo pt on p.cd_tributo = pt.cd_produto\n"
                    + "where\n"
                    + "	p_mva_st > 0\n"
                    + "order by\n"
                    + "	p.cd_produto"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setPautaFiscalId(rst.getString("idproduto"));

                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	codigo as idproduto,\n"
                    + "	pr_venda as precovenda, \n"
                    + "	pr_venda2 as precooferta, \n"
                    + "	current_date + 1 as datainicio,\n"
                    + "	dt_limite_pr2 as datafim \n"
                    + "from produto \n"
                    + "where pr_venda2 > 0\n"
                    + "and dt_limite_pr2 > now()"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataFim(rst.getDate("datafim"));
                    imp.setPrecoNormal(rst.getDouble("precovenda"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	r.cd_mov_cpr as id,\n"
                    + "	r.dt_movto as dataemissao,\n"
                    + "	r.dt_vcto as datavencimento,\n"
                    + "	r.valor,\n"
                    + "	r.nr_docto as numerodocumento,\n"
                    + "	r.nr_parcela as parcela,\n"
                    + "	r.tipo,\n"
                    + "	r.cd_caixa as ecf,\n"
                    + "	r.cd_pessoa as idcliente,\n"
                    + " r.total_parcelas \n"        
                    + "from mov_cpr r\n"
                    + "join pessoa p on p.cd_pessoa = r.cd_pessoa \n"
                    + "	and p.cliente = true\n"
                    + "where r.cd_empresa = " + getLojaOrigem() + "\n"
                    + "and r.tipo = 'R'\n"
                    + "and r.dt_pagto is null\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setObservacao("TOTAL DE PARCELAS " + rst.getString("total_parcelas"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
}
