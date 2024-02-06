package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoProduto;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Alan
 */
public class NereusDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Nereus";
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
                OpcaoProduto.TIPO_PRODUTO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CLIENTE_EVENTUAL));
    }

    /*@Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	aliq.id_grade_trib id,\n"
                    + "	cst.codigo||'-'||aliq.per_icms||'-'||aliq.per_red_icms descricao,\n"
                    + "	cst.codigo cst_saida,\n"
                    + "	aliq.per_icms aliquota_saida,\n"
                    + "	aliq.per_red_icms reducao_saida\n"
                    + "from\n"
                    + "	fs_grade_trib_aliq aliq\n"
                    + "	join tb_cst cst on aliq.id_cst = cst.id_cst and cst.tipo_imposto = 'ICMS'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliquota_saida"),
                            rs.getDouble("reducao_saida"))
                    );
                }
            }
        }

        return result;
    }*/
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct \n"
                    + "	ts.id_simbologia id,\n"
                    + "	case\n"
                    + "	  when ts.tipo_cf = 'TRIBUTADO'\n"
                    + "	  then ts.tipo_cf||' '||ts.per_aliquota||'%'\n"
                    + "	  else ts.tipo_cf\n"
                    + "	end descricao,\n"
                    + "	cst.codigo cst_icms,\n"
                    + "	ts.per_aliquota aliq_icms,\n"
                    + "	0 red_icms\n"
                    + "from\n"
                    + "	fs_grade_trib_aliq al\n"
                    + "	left join tb_simbologia ts on ts.id_simbologia = al.id_simbologia\n"
                    + "	left join tb_cst cst on al.id_cst = cst.id_cst and cst.tipo_imposto = 'ICMS'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_icms"),
                            rs.getDouble("aliq_icms"),
                            rs.getDouble("red_icms"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	e.id_prod id_produto,\n"
                    + "	ean13 ean,\n"
                    + "	coalesce(f.fator,1) qtde_emb,\n"
                    + "	u.sigla tipo_emb\n"
                    + "from\n"
                    + "	eq_prod_ean e\n"
                    + "	join eq_prod p on p.id_prod = e.id_prod \n"
                    + "	join tb_unid u on u.id_unid = p.id_unid_v \n"
                    + "	left join tb_fatorcx f on f.id_fatorcx = e.id_fatorcx\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtde_emb"));
                    imp.setTipoEmbalagem(rs.getString("tipo_emb"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	produto.id_prod idproduto,\n"
                    + " case when balanca = 'SIM' then produto.cd_auxiliar else ean.ean13 end ean,\n"
                    + "	produto.descricao desc_completa,\n"
                    + "	descricaor desc_reduzida,\n"
                    + "	case when balanca = 'SIM' then 1 else 0 end e_balanca,\n"
                    + "	uv.sigla emb_venda,\n"
                    + "	uc.sigla emb_compra,\n"
                    + "	ncm.codigo ncm,\n"
                    + "	cest.cd_cest cest,\n"
                    + "	dt_cad data_cadastro,\n"
                    + "	peso pesobruto,\n"
                    + "	peso_l pesoliquido,\n"
                    + " produto.id_tipo_prod tipoproduto,\n"
                    + "	estoque.qtde estoque,\n"
                    + "	estoque.qtde_minima est_min,\n"
                    + "	estoque.qtde_maxima est_max,\n"
                    + " round(preco.vr_custo_aquisicao,3) custosemimposto,\n"
                    + "	round(preco.vr_custo_reposicao,3) custocomimposto,\n"
                    + " preco.per_lucro_projetado margem,\n"
                    + "	round(preco.vr_venda_atual,3) precovenda,\n"
                    + " simbol.id_simbologia id_icms,\n"
                    + " pc.codigo piscofins\n"
                    + "from\n"
                    + "	eq_prod produto\n"
                    + "	left join eq_prod_com preco on produto.id_prod = preco.id_prod \n"
                    + "	left join eq_prod_ean ean on produto.id_prod = ean.id_prod\n"
                    + "	left join eq_prod_qtde estoque on produto.id_prod = estoque.id_prod and estoque.id_emp = " + getLojaOrigem() + " and estoque.id_tipo_estoque = 1\n"
                    + "	left join fs_grade_trib_aliq aliq on aliq.id_grade_trib = preco.id_grade_trib\n"
                    + " join fs_grade_trib_dcto dcto on dcto.id_grade_trib_aliq = aliq.id_grade_trib_aliq and dcto.id_tipo_dcto = 59\n"
                    + " left join tb_cst pc on aliq.id_cst_pis = pc.id_cst and pc.tipo_imposto = 'PIS'\n"
                    + "	left join tb_cst cst on aliq.id_cst = cst.id_cst\n"
                    + "	left join tb_simbologia simbol on aliq.id_simbologia = simbol.id_simbologia\n"
                    + "	left join tb_unid uv on produto.id_unid_v = uv.id_unid\n"
                    + "	left join tb_unid uc on produto.id_unid_c = uc.id_unid\n"
                    + "	left join tb_ncm ncm on produto.id_ncm = ncm.id_ncm\n"
                    + "	left join tb_cest cest on ncm.id_cest = cest.id_cest\n"
                    + "where preco.id_emp = " + getLojaOrigem()
            /*
                    "select distinct\n"
                    + "	produto.id_prod idproduto,\n"
                    //                  + "	ean.ean13 ean,\n
                    + " case when balanca = 'SIM' then produto.cd_auxiliar else ean.ean13 end ean,\n"
                    + "	produto.descricao desc_completa,\n"
                    + "	descricaor desc_reduzida,\n"
                    + "	case when balanca = 'SIM' then 1 else 0 end e_balanca,\n"
                    //                  + "	coalesce(f.fator, 1) qtde_emb,\n"
                    + "	uv.sigla emb_venda,\n"
                    + "	uc.sigla emb_compra,\n"
                    + "	ncm.codigo ncm,\n"
                    + "	cest.cd_cest cest,\n"
                    + "	dt_cad data_cadastro,\n"
                    + "	peso pesobruto,\n"
                    + "	peso_l pesoliquido,\n"
                    + " produto.id_tipo_prod tipoproduto,\n"
                    + "	estoque.qtde estoque,\n"
                    + "	estoque.qtde_minima est_min,\n"
                    + "	estoque.qtde_maxima est_max,\n"
                    + " round(preco.vr_custo_aquisicao,3) custosemimposto,\n"
                    + "	round(preco.vr_custo_reposicao,3) custocomimposto,\n"
                    + " preco.per_lucro_projetado margem,\n"
                    + "	round(preco.vr_venda_atual,3) precovenda,\n"
                    + " simbol.id_simbologia id_icms,\n"
                    + " pc.codigo piscofins\n"
                    + "from\n"
                    + "	eq_prod produto\n"
                    + "	left join eq_prod_com preco on produto.id_prod = preco.id_prod \n"
                    + "	left join eq_prod_ean ean on produto.id_prod = ean.id_prod\n"
                    + "	left join eq_prod_qtde estoque on produto.id_prod = estoque.id_prod and estoque.id_emp = " + getLojaOrigem() + "\n"
                    + "	left join fs_grade_trib_aliq aliq on aliq.id_grade_trib = preco.id_grade_trib\n"
                    + " join fs_grade_trib_dcto dcto on dcto.id_grade_trib_aliq = aliq.id_grade_trib_aliq and dcto.id_tipo_dcto = 59"
                    + " left join tb_cst pc on aliq.id_cst_pis = pc.id_cst and pc.tipo_imposto = 'PIS'\n"
                    + "	left join tb_cst cst on aliq.id_cst = cst.id_cst\n"
                    + "	left join tb_simbologia simbol on aliq.id_simbologia = simbol.id_simbologia\n"
                    //                  + "	join tb_fatorcx f on f.id_fatorcx = ean.id_fatorcx\n"
                    + "	left join tb_unid uv on produto.id_unid_v = uv.id_unid\n"
                    + "	left join tb_unid uc on produto.id_unid_c = uc.id_unid\n"
                    + "	left join tb_ncm ncm on produto.id_ncm = ncm.id_ncm\n"
                    + "	left join tb_cest cest on ncm.id_cest = cest.id_cest\n"
                    + "order by 1"
             */
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));

                    imp.setDescricaoCompleta(rst.getString("desc_completa"));
                    imp.setDescricaoReduzida(rst.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.seteBalanca(rst.getBoolean("e_balanca"));

                    imp.setTipoEmbalagem(rst.getString("emb_venda"));
                    imp.setTipoEmbalagemCotacao(rst.getString("emb_compra"));
//                  imp.setQtdEmbalagem(rst.getInt("qtde_emb"));

                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));

                    imp.setEstoqueMinimo(rst.getDouble("est_min"));
                    imp.setEstoqueMaximo(rst.getDouble("est_max"));
                    imp.setEstoque(rst.getDouble("estoque"));

                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    if (rst.getInt("tipoproduto") == 1) {
                        imp.setTipoProduto(TipoProduto.MERCADORIA_REVENDA);
                    } else {
                        switch (rst.getInt("tipoproduto")) {
                            case 2:
                                imp.setTipoProduto(TipoProduto.MATERIA_PRIMA);
                                break;
                            case 3:
                                imp.setTipoProduto(TipoProduto.EMBALAGEM);
                                break;
                            case 4:
                                imp.setTipoProduto(TipoProduto.PRODUTO_EM_PROCESSO);
                                break;
                            case 5:
                                imp.setTipoProduto(TipoProduto.PRODUTO_ACABADO);
                                break;
                            case 6:
                                imp.setTipoProduto(TipoProduto.SUBPRODUTO);
                                break;
                            case 7:
                                imp.setTipoProduto(TipoProduto.PRODUTO_INTERMEDIARIO);
                                break;
                            case 8:
                                imp.setTipoProduto(TipoProduto.MATERIAL_USO_E_CONSUMO);
                                break;
                            case 9:
                                imp.setTipoProduto(TipoProduto.ATIVO_IMOBILIZADO);
                                break;
                            case 10:
                                imp.setTipoProduto(TipoProduto.SERVICOS);
                                break;
                            case 11:
                                imp.setTipoProduto(TipoProduto.OUTROS_INSUMOS);
                                break;
                            case 12:
                                imp.setTipoProduto(TipoProduto.OUTROS);
                                break;
                            default:
                                imp.setTipoProduto(TipoProduto.MERCADORIA_REVENDA);
                                break;
                        }
                    }

                    String idIcms;

                    idIcms = rst.getString("id_icms");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);

                    imp.setPiscofinsCstCredito(rst.getString("piscofins"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	 c.id_pes id,\n"
                    + "	 razao,\n"
                    + "	 fantasia,\n"
                    + "	 case when tipo_fj = 'F' then cpf else cnpj end cnpj,\n"
                    + "	 case when tipo_fj = 'F' then rg else insc end ie,\n"
                    + "	 e.n_endereco endereco,\n"
                    + "	 e.n_nro_endereco numero,\n"
                    + "	 e.n_complemento complemento,\n"
                    + "	 e.n_bairro bairro,\n"
                    + "	 m.municipio cidade,\n"
                    + "	 u.uf uf,\n"
                    + "	 e.n_cep cep,\n"
                    + "	 e.n_fone telefone,\n"
                    + "	 email,\n"
                    + "	 dt_cad data_cadastro,\n"
                    + "	 case id_tipo_situacao when 4  then 1 else 0 end ativo,\n"
                    + "  obs_v observacao\n"
                    + "from\n"
                    + "	 fn_pes c\n"
                    + "	 join fn_pes_tipo tp on tp.id_pes = c.id_pes\n"
                    + "	 join fn_pes_end e on e.id_pes = c.id_pes\n"
                    + "	 join tb_municipio m on m.id_municipio = e.id_n_municipio\n"
                    + "	 join tb_uf u on u.id_uf = m.id_uf\n"
                    + "where\n"
                    + "	 tp.tipo = 'FOR'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(Utils.acertarTexto(rs.getString("telefone")));

                    String email = Utils.acertarTexto(rs.getString("email")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("1", "Email", "", "", TipoContato.COMERCIAL,
                                (email.length() > 50 ? email.substring(0, 50) : email));
                    }

                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setObservacao(rs.getString("observacao"));

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
                    + "	id_prod id_produto,\n"
                    + "	id_pes id_fornecedor,\n"
                    + "	codigo codexterno,\n"
                    + "	f.fator qtde_embalagem\n"
                    + "from\n"
                    + "	eq_prod_ref pf\n"
                    + "	join tb_fatorcx f on pf.id_fatorcx = f.id_fatorcx\n"
                    + "order by 2,1"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtde_embalagem"));

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
                    + "	 c.id_pes id,\n"
                    + "	 case when tipo_fj = 'F' then cpf else cnpj end cpf_cnpj,\n"
                    + "	 case when tipo_fj = 'F' then rg else insc end rg_ie,\n"
                    + "	 razao,\n"
                    + "	 case when fantasia = '(ATUALIZAR)' then razao else fantasia end fantasia,\n"
                    + "	 e.n_endereco endereco,\n"
                    + "	 e.n_nro_endereco numero,\n"
                    + "	 e.n_complemento complemento,\n"
                    + "	 e.n_bairro bairro,\n"
                    + "	 m.municipio cidade,\n"
                    + "	 u.uf uf,\n"
                    + "	 e.n_cep cep,\n"
                    + "	 e.n_fone telefone,\n"
                    + "	 e.n_celular celular,\n"
                    + "	 email,\n"
                    + "	 cargo,\n"
                    + "	 vr_renda salario,\n"
                    + "	 trabalho_local empresa,\n"
                    + "	 trabalho_fone tel_empresa,\n"
                    + "	 dt_nasc data_nasc,\n"
                    + "	 dt_cad data_cadastro,\n"
                    + "	 conjuge,\n"
                    + "	 dt_nasc_conjuge nasc_conjuge,\n"
                    + "	 mae nomemae,\n"
                    + "	 pai nomepai,\n"
                    + "	 case id_tipo_situacao when 4  then 1 else 0 end ativo,\n"
                    + "	 case id_tipo_situacao when 5 then 1 else 0 end bloqueado,\n"
                    + "  obs_v observacao\n"
                    + "from\n"
                    + "	 fn_pes c\n"
                    + "	 join fn_pes_tipo tp on tp.id_pes = c.id_pes\n"
                    + "	 join fn_pes_end e on e.id_pes = c.id_pes\n"
                    + "	 join tb_municipio m on m.id_municipio = e.id_n_municipio\n"
                    + "	 join tb_uf u on u.id_uf = m.id_uf\n"
                    + "where\n"
                    + "	 tp.tipo in ('CLI','CRC','FUN')\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("tel_empresa"));

                    imp.setDataNascimento(rs.getDate("data_nasc"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setDataNascimentoConjuge(rs.getDate("nasc_conjuge"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));

                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setObservacao(rs.getString("observacao"));

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
            try (ResultSet rs = stm.executeQuery(
                    /*  id_tipo_situacao = 1 - em aberto
                        id_cxa_cta = 3  - venda a prazo
                     */
                    "select\n"
                    + "	id_titulo as id,\n"
                    + "	id_pes as idCliente,\n"
                    + "	id_pdv as ecf,\n"
                    + "	nro_dcto as numerocupom,\n"
                    + "	dt_emissao as dataemissao,\n"
                    + "	vr_titulo as valor,\n"
                    + "	dt_vencimento as datavencimento\n"
                    + "from\n"
                    + "	fn_titulo ft\n"
                    + "where\n"
                    + "	 id_emp = " + getLojaOrigem() + "\n"
                    + "	and id_tipo_situacao = 1\n"
                    + "	and id_cxa_cta = 3")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("IdCliente"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setNumeroCupom(rs.getString("NumeroCupom"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setValor(rs.getDouble("VALOR"));

                    imp.setDataVencimento(rs.getDate("datavencimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new NereusDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new NereusDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setSubTotalImpressora(rst.getDouble("total"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "select\n"
                    + "	id_cupom id_venda,\n"
                    + "	nro_ecf ecf,\n"
                    + "	nro_coo numerocupom,\n"
                    + "	dt_venda as data,\n"
                    + "	substring(hr_final::varchar, 1, 8) hora,\n"
                    + "	vr_total total,\n"
                    + "	vr_desconto desconto,\n"
                    + "	vr_acrescimo acrescimo\n"
                    + "from\n"
                    + "	vnd_cupom\n"
                    + "where\n"
                    + "	id_emp = " + idLojaCliente + "\n"
                    + " and cnc != 'SIM'\n"
                    + " and status_nfc = 'ENVIADO'\n"
                    + "	and dt_venda between '" + strDataInicio + "' and '" + strDataTermino + "'\n"
                    + "	order by dt_venda, nro_coo";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	 vi.id_cupom id_venda,\n"
                    + "	 id_cupom_prod id_item,\n"
                    + "	 sequencia nroitem,\n"
                    + "	 vi.id_prod produto,\n"
                    + "	 u.sigla unidade,\n"
                    + "	 ean13 codigobarras,\n"
                    + "	 p.descricao,\n"
                    + "	 qtde quantidade,\n"
                    + "	 vr_venda precovenda,\n"
                    + "	 vi.vr_total total,\n"
                    + "	 vi.vr_desconto desconto,\n"
                    + "	 vi.vr_acrescimo acrescimo,\n"
                    + "	 case when vi.cnc = 'SIM' then 1 else 0 end cancelado\n"
                    + "from\n"
                    + "	 vnd_cupom_prod vi\n"
                    + "	 join vnd_cupom v on v.id_cupom = vi.id_cupom\n"
                    + "	 left join eq_prod p on p.id_prod = vi.id_prod\n"
                    + "	 left join tb_unid u on u.id_unid = p.id_unid_v \n"
                    + "where\n"
                    + "	 v.dt_venda between '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "	 order by 1,3";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
