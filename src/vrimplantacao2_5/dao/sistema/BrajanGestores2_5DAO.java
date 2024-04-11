/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.remote.ItemComboVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoProduto;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *SISTEMA REFATORADO DA 2.0 PARA 2.5 E AINDA NÃO TESTADO FAVOR VALIDAR OS METODOS
 * @author Lucas
 */
public class BrajanGestores2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    public int idLocalEstoque;

    @Override
    public String getSistema() {
        return "BrajanGestores";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        Map<String, Estabelecimento> result = new LinkedHashMap<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cod_filial as id, (fantasia ||' - '|| cnpj) as loja from cad_filiais order by cod_filial"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id"), new Estabelecimento(rst.getString("id"), rst.getString("loja")));
                }
            }
        }
        return new ArrayList<>(result.values());
    }

    public List<ItemComboVO> getLocalEstoque() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	id_local as id, \n"
                    + "	desc_local \n"
                    + "from est_local_estoque"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"),
                            rst.getString("id") + " - "
                            + rst.getString("desc_local")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	icm.id_calc_icms as id, \n"
                    + "	icm.desc_calc_icms as descricao,\n"
                    + "	icmuf.cst as csticms,\n"
                    + "	icmuf.aliq_icms as aliqicms,\n"
                    + "	icmuf.aliq_reducao as reducaoicms\n"
                    + "from cal_icms icm\n"
                    + "inner join cal_icms_uf icmuf \n"
                    + "	on icmuf.id_calc_icms = icm.id_calc_icms\n"
                    + "		and icmuf.uf_origem = '" + Parametros.get().getUfPadraoV2().getSigla() + "' and icmuf.uf_destino = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("csticms"),
                            rs.getDouble("aliqicms"),
                            rs.getDouble("reducaoicms")));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m1.id_grupo as merc1,\n"
                    + "	m1.desc_grupo as desc_merc1,\n"
                    + "	m2.id_subgrupo as merc2,\n"
                    + "	m2.desc_subgrupo as desc_merc2\n"
                    + "from est_grupo m1\n"
                    + "inner join est_subgrupo m2 on m2.id_grupo = m1.id_grupo\n"
                    + "order by 1, 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.id_produto as id,\n"
                    + "	p.cod_produto as codigo,\n"
                    + "	p.cod_barra as codigobarras,\n"
                    + "	u.sigla_un as tipoembalagem,\n"
                    + "	case p.exp_balanca when 'S' then 1 else 0 end balanca,\n"
                    + "	p.dias_validade as validade,\n"
                    + "	p.desc_produto as descricaocompleta,\n"
                    + "	p.desc_pdv as descricaoreduzida,\n"
                    + "	p.id_grupo as mercadologico1,\n"
                    + "	p.id_subgrupo as mercadologico2,\n"
                    + "	tp.desc_tipo_produto as desctipoproduto,\n"
                    + " tp.id_tipo_produto as tipoproduto,"
                    + "	p.data_cadastro as datacadastro,\n"
                    + "	p.preco_custo as custo,\n"
                    + "	pr.preco_custo,\n"
                    + "	p.preco_venda as preco,\n"
                    + "	pr.preco_venda,\n"
                    + "	p.markup as margem,\n"
                    + "	p.peso_bruto as pesobruto,\n"
                    + "	p.peso_liquido as pesoliquido,\n"
                    + "	p.ncm,\n"
                    + "	p.cest,\n"
                    + "	fi.cst_venda as cst_pis_saida,\n"
                    + "	fi.cst_dev_cliente as cst_pis_entrada,\n"
                    + "	fi.nat_rec as naturezareceita,\n"
                    + "	icm.id_calc_icms, \n"
                    + "	icm.desc_calc_icms,\n"
                    + "	icmuf.cst,\n"
                    + "	icmuf.aliq_icms,\n"
                    + "	icmuf.aliq_reducao,\n"
                    + "	case p.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "	p.est_minimo as estoqueminimo,\n"
                    + "	est.saldo_atual as estoque\n"
                    + "from est_produto p\n"
                    + "left join est_estoque est on est.id_produto = p.id_produto \n"
                    + "	and est.cod_filial = " + getLojaOrigem() + " \n"
                    + "	and est.id_local = " + idLocalEstoque + "\n"
                    + "left join cad_unidade u on u.id_unidade = p.id_unidade \n"
                    + "	and u.cod_filial = " + getLojaOrigem() + "\n"
                    + "left join est_tipo_produto tp on tp.id_tipo_produto = p.id_tipo_produto \n"
                    + "	and tp.cod_filial = " + getLojaOrigem() + "\n"
                    + "left join est_produto_preco pr on pr.id_produto = p.id_produto	\n"
                    + "	and pr.cod_filial = " + getLojaOrigem() + "\n"
                    + "left join fis_figura fi on fi.id_figura = p.id_figura\n"
                    + "	and fi.cod_filial = " + getLojaOrigem() + "\n"
                    + "left join cal_icms icm on icm.id_calc_icms = p.id_calc_icms\n"
                    + "inner join cal_icms_uf icmuf \n"
                    + "	on icmuf.id_calc_icms = icm.id_calc_icms\n"
                    + "		and icmuf.uf_origem = '" + Parametros.get().getUfPadraoV2().getSigla() + "' and icmuf.uf_destino = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "order by p.id_produto		"
            )) {
                while (rst.next()) {

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));

                    if ((rst.getString("descricaoreduzida") != null)
                            && (!rst.getString("descricaoreduzida").trim().isEmpty())) {

                        imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    } else {
                        imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    }

                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(rst.getDate("datacadastro"));

                    if (rst.getInt("tipoproduto") == 0) {
                        imp.setTipoProduto(TipoProduto.MERCADORIA_REVENDA);
                    } else {
                        switch (rst.getInt("tipoproduto")) {
                            case 1:
                                imp.setTipoProduto(TipoProduto.MERCADORIA_REVENDA);
                                break;
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
                            case 10:
                                imp.setTipoProduto(TipoProduto.SERVICOS);
                                break;
                            case 11:
                                imp.setTipoProduto(TipoProduto.OUTROS_INSUMOS);
                                break;
                            case 12:
                                imp.setTipoProduto(TipoProduto.OUTROS);
                                break;
                            case 9:
                                imp.setTipoProduto(TipoProduto.ATIVO_IMOBILIZADO);
                                break;
                            default:
                                imp.setTipoProduto(TipoProduto.MERCADORIA_REVENDA);
                                break;
                        }
                    }

                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    //imp.setMargem(rst.getDouble("margem"));
                    imp.setMargem(0);
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsDebitoId(rst.getString("id_calc_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_calc_icms"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.cod_produto as idproduto,\n"
                    + "	ean.cod_barra as codigobarras,\n"
                    + "	ean.embalagem as qtdembalagem,\n"
                    + "	u.sigla_un as tipoembalagem\n"
                    + "from est_produto_mult_unid ean\n"
                    + "inner join est_produto p on p.id_produto = ean.id_produto\n"
                    + "inner join cad_unidade u on u.id_unidade = p.id_unidade \n"
                    + "	and u.cod_filial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.id_pessoa as id,\n"
                    + "	p.cod_pessoa as codigo,\n"
                    + "	p.razao_social as razao,\n"
                    + "	p.fantasia as fantasia,\n"
                    + "	p.data_cadastro as datacadastro,\n"
                    + "	p.pessoa as tipoinscricao,\n"
                    + "	p.sit as ativo,\n"
                    + "	ende.endereco,\n"
                    + "	ende.numero,\n"
                    + "	ende.complemento,\n"
                    + "	ende.bairro,\n"
                    + "	ende.cep,\n"
                    + "	ende.cidade as municipio,\n"
                    + "	ende.ibge as municipioibge,\n"
                    + "	ende.uf,\n"
                    + "	p.email,\n"
                    + "	p.sexo,\n"
                    + "	p.estado_civil as estadocivil,\n"
                    + "	p.nascimento as datanascimento,\n"
                    + "	p.conj_nome as conjuge,\n"
                    + "	p.conj_cpf as conjugecpf,\n"
                    + "	p.fil_pai as nomepai,\n"
                    + "	p.fil_mae as nomemae,\n"
                    + "	p.emp_empresa as empresa,\n"
                    + "	p.emp_admissao as dataadmissao,\n"
                    + "	p.emp_telefone as telefoneempresa,\n"
                    + "	p.emp_salario as salario,\n"
                    + "	p.emp_endereco as enderecoempresa,\n"
                    + "	p.emp_bairro as bairroempresa,\n"
                    + "	p.emp_cep as cepempresa,\n"
                    + "	p.emp_cidade as municipioempresa,\n"
                    + "	p.emp_uf as ufempresa,\n"
                    + "	p.emp_complemento as complementoempresa,\n"
                    + "	p.emp_numero as numeroempresa,\n"
                    + "	p.limite as valorlimite,\n"
                    + "	p.observacao,\n"
                    + "	sit.desc_situacao as situacao,\n"
                    + "	sit.bloquear as bloqueado\n"
                    + "from pes_pessoa p\n"
                    + "left join cad_situacao sit on sit.id_situacao = p.id_situacao\n"
                    + "left join pes_endereco ende on ende.id_pessoa = p.id_pessoa\n"
                    + "where p.tipo = 'F'\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setAtivo("A".equals(rst.getString("ativo")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setBloqueado("N".equals(rst.getString("bloqueado")));

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail(
                                "EMAIL",
                                rst.getString("email").toLowerCase(),
                                TipoContato.NFE
                        );
                    }

                    try (Statement stmDoc = ConexaoPostgres.getConexao().createStatement()) {
                        try (ResultSet rstDoc = stmDoc.executeQuery(
                                "select \n"
                                + "	doc.id_pessoa,\n"
                                + "	tdoc.sql_tip_doc,\n"
                                + "	doc.num_doc,\n"
                                + "	doc.orgao,\n"
                                + "	doc.emissao,\n"
                                + "	doc.uf\n"
                                + "from pes_documento doc\n"
                                + "inner join tab_tipo_documento tdoc on tdoc.id_tip_documento = doc.id_tip_documento\n"
                                + "	and sql_tip_doc in ('CPF', 'RG', 'CNPJ', 'IE', 'IM')\n"
                                + "where doc.id_pessoa = " + rst.getString("id")
                                + "order by id_pessoa"
                        )) {
                            while (rstDoc.next()) {

                                if ("CPF".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setCnpj_cpf(rstDoc.getString("num_doc"));
                                } else if ("CNPJ".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setCnpj_cpf(rstDoc.getString("num_doc"));
                                } else if ("IE".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setIe_rg(rstDoc.getString("num_doc"));
                                } else if ("RG".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setIe_rg(rstDoc.getString("num_doc"));
                                } else if ("IM".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setInsc_municipal(rstDoc.getString("num_doc"));
                                }
                            }
                        }
                    }

                    try (Statement stmTel = ConexaoPostgres.getConexao().createStatement()) {
                        try (ResultSet rstTel = stmTel.executeQuery(
                                "select \n"
                                + "	tel.id_pessoa,\n"
                                + "	tel.num_tel as telefone,\n"
                                + "	tpTel.nom_tip_tel as tipo\n"
                                + "from pes_telefone tel\n"
                                + "inner join tab_tipo_telefone tpTel on tpTel.id_tip_telefone = tel.id_tip_telefone\n"
                                + "where tel.id_pessoa = " + rst.getString("id")
                        )) {
                            while (rstTel.next()) {

                                if (("RESIDENCIAL".equals(rstTel.getString("tipo")))
                                        || ("COMERCIAL".equals(rstTel.getString("tipo")))) {
                                    imp.setTel_principal(rstTel.getString("telefone"));
                                }

                                imp.addContato(
                                        rstTel.getString("tipo"),
                                        !"CELULAR".equals(rstTel.getString("tipo")) ? rstTel.getString("telefone") : null,
                                        "CELULAR".equals(rstTel.getString("tipo")) ? rstTel.getString("telefone") : null,
                                        TipoContato.COMERCIAL,
                                        null
                                );
                            }
                        }
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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pro.cod_produto as idproduto,\n"
                    + "	fo.cod_pessoa as idfornecedor,\n"
                    + "	pf.cod_estfornecedor as codigoexterno,\n"
                    + "	pf.valor_unitario as custo,\n"
                    + "	pf.data as datalteracao\n"
                    + "from est_fornecedor pf\n"
                    + "inner join est_produto pro on pro.id_produto = pf.id_produto\n"
                    + "inner join pes_pessoa fo on fo.id_pessoa = pf.id_pessoa"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setCustoTabela(rst.getDouble("custo"));
                    imp.setDataAlteracao(rst.getDate("datalteracao"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.id_pessoa as id,\n"
                    + "	p.cod_pessoa as codigo,\n"
                    + "	p.razao_social as razao,\n"
                    + "	p.fantasia as fantasia,\n"
                    + "	p.data_cadastro as datacadastro,\n"
                    + "	p.pessoa as tipoinscricao,\n"
                    + "	p.sit as ativo,\n"
                    + "	ende.endereco,\n"
                    + "	ende.numero,\n"
                    + "	ende.complemento,\n"
                    + "	ende.bairro,\n"
                    + "	ende.cep,\n"
                    + "	ende.cidade as municipio,\n"
                    + "	ende.ibge as municipioibge,\n"
                    + "	ende.uf,\n"
                    + "	p.email,\n"
                    + "	p.sexo,\n"
                    + "	p.estado_civil as estadocivil,\n"
                    + "	p.nascimento as datanascimento,\n"
                    + "	p.conj_nome as conjuge,\n"
                    + "	p.conj_cpf as conjugecpf,\n"
                    + "	p.fil_pai as nomepai,\n"
                    + "	p.fil_mae as nomemae,\n"
                    + "	p.emp_empresa as empresa,\n"
                    + "	p.emp_admissao as dataadmissao,\n"
                    + "	p.emp_telefone as telefoneempresa,\n"
                    + "	p.emp_salario as salario,\n"
                    + "	p.emp_endereco as enderecoempresa,\n"
                    + "	p.emp_bairro as bairroempresa,\n"
                    + "	p.emp_cep as cepempresa,\n"
                    + "	p.emp_cidade as municipioempresa,\n"
                    + "	p.emp_uf as ufempresa,\n"
                    + "	p.emp_complemento as complementoempresa,\n"
                    + "	p.emp_numero as numeroempresa,\n"
                    + "	p.limite as valorlimite,\n"
                    + "	p.observacao,\n"
                    + "	sit.desc_situacao as situacao,\n"
                    + "	sit.bloquear as bloqueado\n"
                    + "from pes_pessoa p\n"
                    + "left join cad_situacao sit on sit.id_situacao = p.id_situacao\n"
                    + "left join pes_endereco ende on ende.id_pessoa = p.id_pessoa\n"
                    + "where p.tipo = 'C'\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setAtivo("A".equals(rst.getString("ativo")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getString("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setEmpresaTelefone(rst.getString("telefoneempresa"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setEmpresaEndereco(rst.getString("enderecoempresa"));
                    imp.setEmpresaBairro(rst.getString("bairroempresa"));
                    imp.setEmpresaCep(rst.getString("cepempresa"));
                    imp.setEmpresaMunicipio(rst.getString("municipioempresa"));
                    imp.setEmpresaUf(rst.getString("ufempresa"));
                    imp.setEmpresaComplemento(rst.getString("complementoempresa"));
                    imp.setEmpresaNumero(rst.getString("numeroempresa"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setBloqueado(!"N".equals(rst.getString("bloqueado")));

                    try (Statement stmDoc = ConexaoPostgres.getConexao().createStatement()) {
                        try (ResultSet rstDoc = stmDoc.executeQuery(
                                "select \n"
                                + "	doc.id_pessoa,\n"
                                + "	tdoc.sql_tip_doc,\n"
                                + "	doc.num_doc,\n"
                                + "	doc.orgao,\n"
                                + "	doc.emissao,\n"
                                + "	doc.uf\n"
                                + "from pes_documento doc\n"
                                + "inner join tab_tipo_documento tdoc on tdoc.id_tip_documento = doc.id_tip_documento\n"
                                + "	and sql_tip_doc in ('CPF', 'RG', 'CNPJ', 'IE', 'IM')\n"
                                + "where doc.id_pessoa = " + rst.getString("id")
                                + "order by id_pessoa"
                        )) {
                            while (rstDoc.next()) {

                                if ("CPF".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setCnpj(rstDoc.getString("num_doc"));
                                } else if ("CNPJ".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setCnpj(rstDoc.getString("num_doc"));
                                } else if ("IE".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setInscricaoestadual(rstDoc.getString("num_doc"));
                                } else if ("RG".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setInscricaoestadual(rstDoc.getString("num_doc"));
                                } else if ("IM".equals(rstDoc.getString("sql_tip_doc"))) {
                                    imp.setInscricaoMunicipal(rstDoc.getString("num_doc"));
                                }
                            }
                        }
                    }

                    try (Statement stmTel = ConexaoPostgres.getConexao().createStatement()) {
                        try (ResultSet rstTel = stmTel.executeQuery(
                                "select \n"
                                + "	tel.id_pessoa,\n"
                                + "	tel.num_tel as telefone,\n"
                                + "	tpTel.nom_tip_tel as tipo\n"
                                + "from pes_telefone tel\n"
                                + "inner join tab_tipo_telefone tpTel on tpTel.id_tip_telefone = tel.id_tip_telefone\n"
                                + "where tel.id_pessoa = " + rst.getString("id")
                        )) {
                            while (rstTel.next()) {

                                if (("RESIDENCIAL".equals(rstTel.getString("tipo")))
                                        || ("COMERCIAL".equals(rstTel.getString("tipo")))) {
                                    imp.setTelefone(rstTel.getString("telefone"));

                                    imp.addTelefone(rstTel.getString("tipo"), rstTel.getString("telefone"));

                                }
                                if ("CELULAR".equals(rstTel.getString("tipo"))) {
                                    imp.setCelular(rstTel.getString("telefone"));
                                }
                                if ("FAX".equals(rstTel.getString("tipo"))) {
                                    imp.setFax(rstTel.getString("telefone"));
                                }

                                if ("RECADO".equals(rstTel.getString("tipo"))) {
                                    imp.addTelefone(rstTel.getString("tipo"), rstTel.getString("telefone"));
                                }

                                if ("0800".equals(rstTel.getString("tipo"))) {
                                    imp.addTelefone(rstTel.getString("tipo"), rstTel.getString("telefone"));
                                }
                            }
                        }
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	a.data_inicial, \n"
                    + "	a.data_final,\n"
                    + "	p.cod_produto,\n"
                    + "	pr.preco_venda,\n"
                    + "	b.preco_promocao\n"
                    + "from est_promocao a\n"
                    + "inner join est_promocao_produtos b on b.id_pro = b.id_pro\n"
                    + "inner join est_produto p on p.id_produto = b.id_produto\n"
                    + "inner join est_produto_preco pr on pr.id_produto = p.id_produto	\n"
                    + "	and pr.cod_filial = " + getLojaOrigem() + "\n"
                    + "	and a.cod_filial = " + getLojaOrigem() + "\n"
                    + "	and b.cod_filial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("cod_produto"));
                    imp.setDataInicio(rst.getDate("data_inicial"));
                    imp.setDataFim(rst.getDate("data_final"));
                    imp.setPrecoNormal(rst.getDouble("preco_venda"));
                    imp.setPrecoOferta(rst.getDouble("preco_promocao"));
                    imp.setTipoOferta(TipoOfertaVO.CAPA);
                    result.add(imp);
                }
            }
        }
        return result;
    }

}
