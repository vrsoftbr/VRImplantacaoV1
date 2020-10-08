/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
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
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
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
                    "SELECT \n"
                    + "	id, descricao, codigo \n"
                    + "FROM grupo\n"
                    + "ORDER BY id"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("id"));
                    imp.setMerc1Descricao(rst.getString("descricao"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
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
                    "SELECT \n"
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
                    + "ORDER BY p.ID"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    String ean = Utils.formataNumero(rst.getString("GTIN"));

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
                    imp.setEan(ean);
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("mercadologico"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setSituacaoCadastro("ATIVO".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_grupo_pis_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_grupo_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                    /* icms dentro estado */
                    imp.setIcmsCstSaida(rst.getInt("COD_CST_DENTRO"));
                    imp.setIcmsCstEntrada(rst.getInt("COD_CST_DENTRO"));

                    imp.setIcmsAliqSaida(rst.getDouble("ALIQUOTA_ICMS_DENTRO"));
                    imp.setIcmsAliqEntrada(rst.getDouble("ALIQUOTA_ICMS_DENTRO"));

                    imp.setIcmsReducaoSaida(rst.getDouble("REDUCAO_BC_DENTRO"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("REDUCAO_BC_DENTRO"));

                    /* icms fora estado */
                    imp.setIcmsCstSaidaForaEstado(rst.getInt("COD_CST_FORA"));
                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("COD_CST_FORA"));
                    imp.setIcmsCstEntradaForaEstado(rst.getInt("COD_CST_FORA"));

                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("ALIQUOTA_ICMS_FORA"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("ALIQUOTA_ICMS_FORA"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("ALIQUOTA_ICMS_FORA"));

                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("REDUCAO_BC_FORA"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("REDUCAO_BC_FORA"));
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("REDUCAO_BC_FORA"));

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
                    + "from cliente c\n"
                    + "\n"
                    + "")) {
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
                    + "	and loja = " + getLojaOrigem() + ""
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
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	f.id_fornecedor,\n"
                    + "	f.razao_social,\n"
                    + "	f.nome_fantasia,\n"
                    + "	f.numero_documento AS cnpj,\n"
                    + "	f.ie,\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	f.cep,\n"
                    + "	f.municipio,\n"
                    + "	f.codigo_municipio,\n"
                    + "	f.uf,\n"
                    + "	f.contato,\n"
                    + "	f.email,\n"
                    + "	f.fax,\n"
                    + "	f.telefone,\n"
                    + "	f.ativo,\n"
                    + "	f.data_cadastro\n"
                    + "FROM fornecedor f\n"
                    + "ORDER BY f.id_fornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_fornecedor"));
                    imp.setRazao(rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("nome_fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("codigo_municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));
                    imp.setAtivo("1".equals(rst.getString("ativo")));

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
                    + "	fornecedor "
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
