/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class G3DAO extends InterfaceDAO {

    private boolean lite = false;

    public void setLite(boolean lite) {
        this.lite = lite;
    }

    @Override
    public String getSistema() {
        return "G3";
    }

    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "   idempresa id,\n"
                    + "	  RazaoSocial razao\n"
                    + "from\n"
                    + "	  empresa e ")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getInt("id") + "", rs.getString("razao")));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.ICMS_SAIDA,
            OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
            OpcaoProduto.ICMS_ENTRADA,
            OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
            OpcaoProduto.ICMS_CONSUMIDOR,
            OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO
        }));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT \n"
                     + "	idgrupo, nome\n"
                     + "FROM grupo\n"
                     + "ORDER BY idgrupo"*/
                    "select\n"
                    + "     m1.idgrupo as m1grupo,\n"
                    + "     m1.nome as m1desc,\n"
                    + "     m2.idSubGrupo as m2subgrupo,\n"
                    + "     m2.Nome as m2desc,\n"
                    + "     m3.idsubgrupo1 as m3subgrupo2,\n"
                    + "     m3.nome as m3desc\n"
                    + "from grupo m1 \n"
                    + "	left join subgrupo m2\n"
                    + "		on m2.idGrupo = m1.idgrupo \n"
                    + "	left join subgrupo1 m3\n"
                    + "		on m3.idsubgrupo = m2.idSubGrupo and m3.idsubgrupo = m2.idSubGrupo \n"
                    + "order by m1grupo, m2subgrupo, m3subgrupo2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1grupo"));
                    imp.setMerc1Descricao(rst.getString("m1desc"));
                    imp.setMerc2ID(rst.getString("m2subgrupo"));
                    imp.setMerc2Descricao(rst.getString("m2desc"));
                    imp.setMerc3ID(rst.getString("m3subgrupo2"));
                    imp.setMerc3Descricao(rst.getString("m3desc"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT \n"
                     + "	p.ID AS id,\n"
                     + "	p.DESCRICAO_PDV AS descricao,\n"
                     + "	p.ID_GRUPO AS mercadologico,\n"
                     + "	TRUNCATE(p.lucro,2) margem,\n"
                     + "	TRUNCATE(p.valor_compra, 2) custosemimposto,\n"
                     + "	TRUNCATE(p.valor_custo, 2) custocomimposto,\n"
                     + "	TRUNCATE(p.VALOR_VENDA, 2) precovenda,\n"
                     + "	p.DATA_CADASTRO AS datacadastro,\n"
                     + "	TRUNCATE(p.ESTOQUE_MAX, 0) estoquemaximo,\n"
                     + "	TRUNCATE(p.ESTOQUE_MIN, 0) estoqueminimo,\n"
                     + "	p.QTD_ESTOQUE AS estoque,\n"
                     + "	p.GTIN,\n"
                     + "	p.EAN,\n"
                     + "	u.NOME AS tipoembalagem,\n"
                     + "	p.NCM AS ncm,\n"
                     + "	p.CEST AS cest,\n"
                     + "	p.CST_PIS_SAIDA,\n"
                     + "	p.CST_PIS_ENTRADA,\n"
                     + "	p.CST_COFINS_SAIDA,\n"
                     + "	p.CST_COFINS_ENTRADA,\n"
                     + "	gps.cst AS cst_grupo_pis_saida,\n"
                     + "	gpe.cst AS cst_grupo_pis_entrada,\n"
                     + "	gcs.cst AS cst_grupo_cofins_saida,\n"
                     + "	gcs.cst AS cst_grupo_cofins_entrada,\n"
                     + "	p.cod_nat_rec AS naturezareceita,\n"
                     + "	p.COD_CST_DENTRO,\n"
                     + "	p.COD_CST_FORA,\n"
                     + "	p.ALIQUOTA_ICMS_DENTRO,\n"
                     + "	p.ALIQUOTA_ICMS_FORA,\n"
                     + "	p.REDUCAO_BC_DENTRO,\n"
                     + "	p.REDUCAO_BC_FORA,\n"
                     + "	p.ECF_ICMS_ST AS aliquotaconsumidor,\n"
                     + "	case p.DESATIVADO when 0 then 'ATIVO' ELSE 'INATIVO' end situacaocadastro\n"
                     + "FROM produto p\n"
                     + "LEFT JOIN unidade_produto u ON u.ID = p.ID_UNIDADE_PRODUTO\n"
                     + "LEFT JOIN grupopis gps ON gps.id = p.id_grupo_pis_saida\n"
                     + "LEFT JOIN grupopis gpe ON gpe.id = p.id_grupo_pis_entrada\n"
                     + "LEFT JOIN grupocofins gcs ON gcs.id = p.id_grupo_cofins_saida\n"
                     + "LEFT JOIN grupocofins gce ON gce.id = p.id_grupo_cofins_entrada\n"
                     + "ORDER BY p.ID"*/
                    "select \n"
                    + "	p.idproduto AS id,\n"
                    + "	p.descricao,\n"
                    + "	p.descrred reduzida,\n"
                    + "	descricaoetq gondola,\n"
                    + "	p.idgrupo AS mercadologico1,\n"
                    + "	p.idsubgrupo as mercadologico2,\n"
                    + "	p.idsubgrupo1 as mercadologico3,\n"
                    + "	pp.margem,\n"
                    + "	pp.custo custosemimposto,\n"
                    + "	pp.custo custocomimposto,\n"
                    + "	pp.venda1 precovenda,\n"
                    + "	dtcadastro datacadastro,\n"
                    + "	coalesce(pesoproduto,0) pesobruto,\n"
                    + "	coalesce(pesovariavel,0) pesoliquido,\n"
                    + "	estmax estoquemaximo,\n"
                    + "	estmin estoqueminimo,\n"
                    + "	estoque_atual AS estoque,\n"
                    + "	ean,\n"
                    + "	unidsaida tipoembalagem,\n"
                    + "	classfiscal AS ncm,\n"
                    + "	p.cest as cest,\n"
                    + "	case p.idsituacao when 1 then 'ATIVO' ELSE 'INATIVO' end situacaocadastro,\n"
                    + "	substring(tabicmsprodentrada,1,2) as icmscstentrada,\n"
                    + "	icmscompra icmsaliqentrada,\n"
                    + "	redbase icmsreducaoentrada,\n"
                    + "	substring(tabicmsprod,1,2) as icmsCstSaida,\n"
                    + "	ct.aliquotaIcms icmsAliqSaida,\n"
                    + "	redbase icmsReducaoSaida,\n"
                    + "	substring(tabicmsprod,1,2) as icmsCstConsumidor,\n"
                    + "	ct.aliquotaIcms aliquotaconsumidor,\n"
                    + "	redbase icmsReducaoConsumidor,\n"
                    + "	substring(cst_pis,1,2) as piscofinsCstCredito,\n"
                    + "	substring(cst_pis_saida,1,2) as piscofinsCstDebito,\n"
                    + "	coalesce(nat_receita,'') naturezareceita "
                    + "FROM produto p \n"
                    + "	left join produto_estoque pe\n"
                    + "		on pe.idproduto = p.idproduto\n"
                    + "	left join produto_preco pp\n"
                    + "		on pp.idproduto = p.idproduto and pe.id_loja = pp.id_loja\n"
                    + " left join cadtributacao ct\n"
                    + "		on p.SitTrib = ct.idCadTributacao\n"
                    + "where pe.id_loja =  " + getLojaOrigem() + "\n"
                    + "	group by p.idProduto"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    String ean = Utils.formataNumero(rst.getString("EAN"));

                    long codigoProduto;

                    if ((ean != null)
                            && (!ean.trim().isEmpty())) {

                        if (ean.trim().length() == 4) {

                            codigoProduto = Long.parseLong(ean);
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }

                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }

                        } else {
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.seteBalanca(false);
                    }

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("reduzida"));
                    imp.setDescricaoGondola(rst.getString("gondola"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEan(ean);
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setSituacaoCadastro("ATIVO".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    // PIS COFINS
                    imp.setPiscofinsCstDebito(rst.getString("PiscofinsCstDebito"));
                    imp.setPiscofinsCstCredito(rst.getString("PiscofinsCstCredito"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                    // ICMS SAIDA DENTRO ESTADO
                    imp.setIcmsCstSaida(rst.getInt("icmsCstSaida"));
                    imp.setIcmsAliqSaida(rst.getDouble("icmsAliqSaida"));
                    imp.setIcmsReducaoSaida(rst.getDouble("icmsReducaoSaida"));

                    // ICMS ENTRADA DENTRO ESTADO
                    imp.setIcmsCstEntrada(rst.getInt("icmscstentrada"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icmsaliqentrada"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("icmsreducaoentrada"));

                    /*
                     imp.setIcmsCstEntrada(rst.getInt("COD_CST_DENTRO"));
                     imp.setIcmsAliqEntrada(rst.getDouble("ALIQUOTA_ICMS_DENTRO"));
                     imp.setIcmsReducaoEntrada(rst.getDouble("REDUCAO_BC_DENTRO"));

                     // ICMS FORA ESTADO
                     imp.setIcmsCstSaidaForaEstado(rst.getInt("COD_CST_FORA"));
                     imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("COD_CST_FORA"));
                     imp.setIcmsCstEntradaForaEstado(rst.getInt("COD_CST_FORA"));

                     imp.setIcmsAliqSaidaForaEstado(rst.getDouble("ALIQUOTA_ICMS_FORA"));
                     imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("ALIQUOTA_ICMS_FORA"));
                     imp.setIcmsAliqEntradaForaEstado(rst.getDouble("ALIQUOTA_ICMS_FORA"));

                     imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("REDUCAO_BC_FORA"));
                     imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("REDUCAO_BC_FORA"));
                     imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("REDUCAO_BC_FORA"));
                     */
                    if (rst.getString("aliquotaconsumidor").contains("18")) {
                        imp.setIcmsCstConsumidor(0);
                        imp.setIcmsAliqConsumidor(18);
                        imp.setIcmsReducaoConsumidor(0);
                    } else if (rst.getString("aliquotaconsumidor").contains("25")) {
                        imp.setIcmsCstConsumidor(0);
                        imp.setIcmsAliqConsumidor(25);
                        imp.setIcmsReducaoConsumidor(0);
                    } else if (rst.getString("aliquotaconsumidor").contains("27")) {
                        imp.setIcmsCstConsumidor(0);
                        imp.setIcmsAliqConsumidor(27);
                        imp.setIcmsReducaoConsumidor(0);
                    } else if (rst.getString("aliquotaconsumidor").contains("FF")) {
                        imp.setIcmsCstConsumidor(60);
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                    } else if (rst.getString("aliquotaconsumidor").contains("II")) {
                        imp.setIcmsCstConsumidor(40);
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                    } else if (rst.getString("aliquotaconsumidor").contains("NN")) {
                        imp.setIcmsCstConsumidor(41);
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                    } else {
                        imp.setIcmsCstConsumidor(40);
                        imp.setIcmsAliqConsumidor(0);
                        imp.setIcmsReducaoConsumidor(0);
                    }

                    imp.setManterEAN(Utils.stringToLong(imp.getEan()) <= 999999);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "	id, \n"
                        + "	qtd_atacado,\n"
                        + "	TRUNCATE(valor_venda_atacado, 2) precoatacado,\n"
                        + "	truncate(valor_venda, 2) precovenda\n"
                        + "FROM produto \n"
                        + "WHERE qtd_atacado > 1\n"
                        + "AND coalesce(valor_venda_atacado, 0) > 0"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("id"));

                        if (codigoAtual > 0) {

                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("id"));
                            imp.setEan("999999" + String.valueOf(codigoAtual));
                            imp.setQtdEmbalagem(rst.getInt("qtd_atacado"));
                            imp.setPrecovenda(rst.getDouble("precovenda"));
                            imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                            result.add(imp);
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	idcliente id,\n"
                    + "	cpf,\n"
                    + "	nome razao,\n"
                    + "	nome fantasia,\n"
                    + "	status_cadastro ativo,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	codmunicipio municipioIBGE,\n"
                    + "	cidade,\n"
                    + "	cUf ufIBGE,\n"
                    + "	uf estado,\n"
                    + "	cep,\n"
                    + "	dt_nasc dataNascimento,\n"
                    + "	dtabertura dataCadastro,\n"
                    + "	coalesce(empresa,'') empresa,\n"
                    + "	coalesce(fone_emp,'') empresaTelefone,\n"
                    + "	salario,\n"
                    + "	limite valorLimite,\n"
                    + "	coalesce(conjuge,'') nomeConjuge,\n"
                    + "	obs observacao,\n"
                    + "	coalesce(vencimento,'') diaVencimento,\n"
                    + "	fone telefone,\n"
                    + "	celular,\n"
                    + "	coalesce(email,'') email,\n"
                    + "	enderecocob cobrancaEndereco,\n"
                    + "	numerocob cobrancaNumero,\n"
                    + "	complementocob cobrancaComplemento,\n"
                    + "	bairrocob cobrancaBairro,\n"
                    + "	cidadecob cobrancaMunicipio,\n"
                    + "	ufcob cobrancaUf,\n"
                    + "	cepcob cobrancaCep\n"
                    + "from cliente c")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipioIBGE(rs.getString("municipioIBGE"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getString("ufIBGE"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataNascimento(rs.getDate("dataNascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("empresaTelefone"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setNomeConjuge(rs.getString("nomeconjuge"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setDiaVencimento(rs.getInt("diavencimento"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

                    imp.setCobrancaEndereco(rs.getString("cobrancaEndereco"));
                    imp.setCobrancaNumero(rs.getString("cobrancaNumero"));
                    imp.setCobrancaComplemento(rs.getString("cobrancaComplemento"));
                    imp.setCobrancaBairro(rs.getString("cobrancaBairro"));
                    imp.setCobrancaMunicipio(rs.getString("cobrancaMunicipio"));
                    imp.setCobrancaUf(rs.getString("cobrancaUf"));
                    imp.setCobrancaCep(rs.getString("cobrancaCep"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	iddebito id,\n"
                    + "	dt_venda dataEmissao,\n"
                    + "	nr_venda numeroCupom,\n"
                    + "	ecf,\n"
                    + "	vl_vista valor,\n"
                    + "	observacao,\n"
                    + "	r.idCliente,\n"
                    + "	cpf cnpjCliente,\n"
                    + "	dt_venc dataVencimento\n"
                    + "from debito r \n"
                    + "	left join cliente c\n"
                    + "		on r.IDCLIENTE = c.idCliente \n"
                    + "where SITUACAO != 'P'\n"
                    + "	and r.loja = " + getLojaOrigem() + ""
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("idCliente"));
                    imp.setCnpjCliente(rst.getString("cnpjCliente"));
                    imp.setDataVencimento(rst.getDate("dataVencimento"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " \n"
                    + "select\n"
                    + "	idchequepre id,\n"
                    + "	cgc_cpf cpf,\n"
                    + "	cheque numerocheque,\n"
                    + "	banco,\n"
                    + "	ch.agencia,\n"
                    + "	ch.conta,\n"
                    + "	emissao,\n"
                    + "	dt_baixa datadeposito,\n"
                    + "	cupom numerocupom,\n"
                    + "	ecf,\n"
                    + "	valor,\n"
                    + "	c.rg,\n"
                    + "	c.fone telefone,\n"
                    + "	c.nome,\n"
                    + "	ch.obs observacao,\n"
                    + "	situacao situacaocheque,\n"
                    + "	datahora_alteracao alteracao\n"
                    + "from\n"
                    + "	chequepre ch\n"
                    + "	left join cliente c\n"
                    + "		on c.idCliente = ch.idCliente \n"
                    + "where Situacao != 'P'\n"
                    + "and ch.loja = " + getLojaOrigem() + ""
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setDate(rst.getDate("datadeposito"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setSituacaoCheque(("A".equals(rst.getString("situacaocheque")) ? SituacaoCheque.ABERTO : SituacaoCheque.BAIXADO));
                    imp.setDataHoraAlteracao(rst.getTimestamp("alteracao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	f.idfornecedor,\n"
                    + "	f.nome,\n"
                    + "	f.fantasia,\n"
                    + "	f.CPF_CGC AS cnpj,\n"
                    + "	f.RG_IE ie,\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	f.cep,\n"
                    + "	f.CIDADE,\n"
                    + "	f.codmunicipio,\n"
                    + "	f.uf,\n"
                    + "	f.contato,\n"
                    + "	f.email,\n"
                    + "	f.fax,\n"
                    + "	f.telefone,\n"
                    + "	f.DTCADASTRO,\n"
                    + " f.obs\n"
                    + "FROM fornecedor f\n"
                    + "ORDER BY f.idfornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idfornecedor"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setIbge_municipio(rst.getInt("codmunicipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("DTCADASTRO"));
                    imp.setObservacao(rst.getString("obs"));
                    //imp.setAtivo("1".equals(rst.getString("ativo")));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao("CONTATO - " + rst.getString("contato"));
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail(
                                "EMAIL",
                                rst.getString("email").toLowerCase(),
                                TipoContato.NFE
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone(
                                "FAX",
                                rst.getString("fax").toLowerCase()
                        );
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  idFornecedor,\n"
                    + "  idProduto,\n"
                    + "  Referencia codexterno,\n"
                    + "  Embalagem qtdembalagem\n"
                    + "from\n"
                    + "  itensfornecedor\n"
                    + "order by \n"
                    + "	idfornecedor "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idProduto"));
                    imp.setIdFornecedor(rst.getString("idFornecedor"));
                    imp.setCodigoExterno(rst.getString("codexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " select\n"
                    + "   idPagar,\n"
                    + "   p.idFornecedor,\n"
                    + "   f.CPF_CGC cnpj,\n"
                    + "   nr_docto documento,\n"
                    + "   dt_entrada dtentrada,\n"
                    + "   dt_emissao dtemissao,\n"
                    + "   dt_entrada dtalteracao,\n"
                    + "   vl_docto valor,\n"
                    + "   dt_vencto dtvencto,\n"
                    + "   p.obs,\n"
                    + "   historico ob2"
                    + " from pagar p\n"
                    + "     join fornecedor f\n"
                    + "     on f.IDFORNECEDOR = p.idFornecedor \n"
                    + "  where dt_pagto is null \n"
                    + "  and loja = " + getLojaOrigem() + ""
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("idPagar"));
                    imp.setIdFornecedor(rst.getString("idFornecedor"));
                    imp.setCnpj(rst.getString("cnpj"));

                    String doc = Utils.formataNumero(rst.getString("documento"));

                    imp.setNumeroDocumento(doc);

                    if (doc != null && !"".equals(doc)) {
                        if (doc.length() > 6) {
                            imp.setNumeroDocumento(doc.substring(0, 6));
                        }
                    }

                    imp.setDataEntrada(rst.getDate("dtentrada"));
                    imp.setDataEmissao(rst.getDate("dtemissao"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dtalteracao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao((rst.getString("obs") == null ? "" : rst.getString("obs")) + " "
                            + (rst.getString("ob2") == null ? "" : rst.getString("ob2")));
                    imp.addVencimento(rst.getDate("dtvencto"), imp.getValor());

                    Result.add(imp);
                }
            }
        }
        return Result;
    }
}
