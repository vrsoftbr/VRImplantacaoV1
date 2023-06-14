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
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Bruno
 */
public class Acom2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    // SISTEMA REFATORADO DA 2.0 E NÃO VALIDADO, FAVOR REVER TODOS OS CAMPOS INCLUSIVE ESCRIPTLOJAORIGEM -- SELECT LOJA.

    @Override
    public String getSistema() {
        return "ACOM";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATACADO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.MARGEM_MAXIMA,
                OpcaoProduto.MARGEM_MINIMA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PDV_VENDA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.CONDICAO_PAGAMENTO,
                OpcaoFornecedor.CONDICAO_PAGAMENTO2,
                OpcaoFornecedor.TELEFONE
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
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	p.Pro_sittrib id,\n"
                    + "	p.Pro_aliquota_ecf aliquota\n"
                    + "from\n"
                    + "	Produto p\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    String id = rst.getString("cst") + "-" + rst.getString("icms") + "-" + rst.getString("reducao");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	m1.Tgi_item,\n"
                    + "	m1.Tgi_nome,\n"
                    + "	m2.Tgs_sitem,\n"
                    + "	m2.Tgs_nome\n"
                    + "from \n"
                    + "	Tabela_generica_item m1\n"
                    + "	left join Tabela_generica_subitem m2 on\n"
                    + "		m1.tgi_cod = m2.Tgs_cod and\n"
                    + "		m1.Tgi_item = m2.Tgs_item\n"
                    + "where\n"
                    //                   + "	m1.Tgi_cod = '" + this.codigoMercadologico + "'"
                    + //"	m1.Filial = '" + getLojaOrigem() + "'\n" +
                    "order by\n"
                    + "	1,3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("Tgi_item"));
                    imp.setMerc1Descricao(rst.getString("Tgi_nome"));
                    imp.setMerc2ID(rst.getString("Tgs_sitem"));
                    imp.setMerc2Descricao(rst.getString("Tgs_nome"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = vrimplantacao.classe.ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Grp_id,\n"
                    + "	Grp_nome\n"
                    + "from\n"
                    + "	Grupo_preco\n"
                    + "order by\n"
                    + "	Grp_nome"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("Grp_id"));
                    imp.setDescricao(rst.getString("Grp_nome"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {

        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = vrimplantacao.classe.ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.Pro_cod id,\n"
                    + "	p.Dtinc datacadastro,\n"
                    + "	p.Dtalt dataalteracao,\n"
                    + "	p.Pro_codean ean,\n"
                    + "	p.Pro_qtdembcom qtdcotacao,\n"
                    + "	p.Pro_qtdemvem qtdembalagem,\n"
                    + "	p.Pro_um unidade,\n"
                    + "	case when p.Pro_um = 'KG' or p.Pro_fracionado = 'Sim' then 1 else 0 end e_balanca,\n"
                    + "	p.Pro_validade validade,\n"
                    + "	p.Pro_nome descricaocompleta,\n"
                    + "	p.Pro_descpdv descricaoreduzida,\n"
                    + "	p.Pro_grupo merc1,\n"
                    + "	p.Pro_subgrp merc2,\n"
                    + "	p.Pro_fornecedor,\n"
                    + "	(select top 1 grpi_id from Grupo_preco_item where Grpi_codint = p.Pro_cod) id_familia,\n"
                    + "	p.Pro_pbruto pesobruto,\n"
                    + "	p.Pro_pliquido pesoliquido,\n"
                    + "	p.Pro_estoqminimo estoqueminimo,\n"
                    + "	p.Pro_estoqmaximo estoquemaximo,\n"
                    + "	est.Alm_disponivel estoque,\n"
                    + "	p.Pro_p_margem margem,\n"
                    + "	p.Pro_custo_aquisicao custocomimposto,\n"
                    + "	p.Pro_preco_base,\n"
                    + "	p.Pro_preco_venda precovenda,\n"
                    + "	case when p.Pro_status = 1 then 0 else 1 end situacaocadastro,\n"
                    + "	p.Pro_ncm ncm,\n"
                    + "	p.Pro_cest cest,\n"
                    + "	p.Pro_cst_pis_ent piscofins_entrada,\n"
                    + "	p.Pro_cst_pis piscofins_saida,\n"
                    + "	p.Pro_natureza_receita piscofins_natureza_receita,\n"
                    + "	p.Pro_te,\n"
                    + "	p.Pro_ts,\n"
                    + "	p.Pro_sittrib,\n"
                    + "	p.Pro_aliquota_ecf\n"
                    + "from\n"
                    + "	Filiais f\n"
                    + "	join Produto p on	 \n"
                    + "		f.Fil_cod = p.Filial\n"
                    + "	join Almoxarifado est on \n"
                    + "		p.Pro_cod = est.Alm_cod13 and\n"
                    + "		est.Filial = f.Fil_cod\n"
                    + "where	\n"
                    + "	f.Fil_cod = '" + getLojaOrigem() + "'"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdcotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));
                    imp.setIcmsDebitoId(String.format(
                            "%s - %.2f",
                            rst.getString("Pro_sittrib"),
                            rst.getDouble("Pro_aliquota_ecf")
                    ));
                    imp.setIcmsCreditoId(String.format(
                            "%s - %.2f",
                            rst.getString("Pro_sittrib"),
                            rst.getDouble("Pro_aliquota_ecf")
                    ));
                    long ean = Utils.stringToLong(imp.getEan());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.Pes_cod6 id,\n"
                    + "	f.Pes_nome45 razao,\n"
                    + "	f.Pes_nome25 fantasia,\n"
                    + "	coalesce(f.Pes_cnpj, f.Pes_cpf) cnpj,\n"
                    + "	coalesce(f.Pes_ie, f.Pes_rg) ie,\n"
                    + "	f.Pes_suframa suframa,\n"
                    + "	case f.Pes_bloqueado\n"
                    + "	when 1 then 1\n"
                    + "	else 0\n"
                    + "	end bloqueado,\n"
                    + "	case f.Pes_bloqueado\n"
                    + "	when 0 then 1\n"
                    + "	when 1 then 1\n"
                    + "	when 2 then 0\n"
                    + "	end situacaocadastro,\n"
                    + "	f.Pes_end endereco,\n"
                    + "	f.Pes_nrend numero,\n"
                    + "	f.Pes_compl complemento,\n"
                    + "	f.Pes_bai bairro,\n"
                    + "	f.Pes_cidade cidade,\n"
                    + "	f.Pes_uf uf,\n"
                    + "	f.Pes_cep cep,\n"
                    + "	f.Pes_endcob cob_endereco,\n"
                    + "	f.Pes_nrendcob cob_numero,\n"
                    + "	'' cob_complemento,\n"
                    + "	f.Pes_baicob cob_bairro,\n"
                    + "	f.Pes_cidcob cob_cidade,\n"
                    + "	f.Pes_ufcob cob_uf,\n"
                    + "	f.Pes_cepcob cob_cep,\n"
                    + "	f.Pes_fone1,\n"
                    + "	f.Pes_fone2,\n"
                    + "	f.Pes_fone3,\n"
                    + "	f.Pes_celular,\n"
                    + "	f.Pes_maxcompra compra,\n"
                    + "	f.Pes_ptoref email,\n"
                    + "	f.Pes_email,\n"
                    + "	f.Dtinc datacadastro,\n"
                    + "	f.Dtalt dataalteracao,\n"
                    + "	f.Pes_obs observacao,\n"
                    + "	f.Pes_prazoentrega prazoentrega\n"
                    + "from\n"
                    + "	Pessoal f\n"
                    + "where\n"
                    + "	Pes_tipo = '002'\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setAtivo(rst.getBoolean("situacaocadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCob_endereco(rst.getString("cob_endereco"));
                    imp.setCob_numero(rst.getString("cob_numero"));
                    imp.setCob_complemento(rst.getString("cob_complemento"));
                    imp.setCob_bairro(rst.getString("cob_bairro"));
                    imp.setCob_municipio(rst.getString("cob_cidade"));
                    imp.setCob_uf(rst.getString("cob_uf"));
                    imp.setCob_cep(rst.getString("cob_cep"));
                    imp.setTel_principal(rst.getString("Pes_fone1"));
                    imp.addTelefone("FONE 2", rst.getString("Pes_fone2"));
                    imp.addTelefone("FONE 3", rst.getString("Pes_fone3"));
                    imp.addCelular("CELULAR", rst.getString("Pes_celular"));
                    imp.addEmail("E-MAIL", rst.getString("email"), TipoContato.COMERCIAL);
                    imp.addEmail("E-MAIL", rst.getString("Pes_email"), TipoContato.NFE);
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    if (Utils.stringToLong(imp.getCnpj_cpf()) > 99999999999L) {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    } else {
                        imp.setTipoEmpresa(TipoEmpresa.PESSOA_FISICA);
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
        try (Statement stm = vrimplantacao.classe.ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	Ext_codint idproduto,\n"
                    + "	Ext_codfor idfornecedor,\n"
                    + "	Ext_codext codigoexterno,\n"
                    + "	Ext_qembcom qtdembalagem \n"
                    + "from \n"
                    + "	Codigo_externo\n"
                    + "where\n"
                    + "	Filial = '" + getLojaOrigem() + "'\n"
                    + "order by\n"
                    + "	2, 1")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.Pes_cod6 id,\n"
                    + "	case when p.Pes_cnpj = '' then p.Pes_cpf else p.Pes_cnpj end cnpjcpf,\n"
                    + "	p.Pes_nome45 razaosocial,\n"
                    + "	p.Pes_natural nomefantasia,\n"
                    + "	coalesce(p.Pes_ie, p.Pes_rg) ie_rg,\n"
                    + "	p.Pes_emissor emissor,\n"
                    + "	case p.Pes_bloqueado\n"
                    + "	when 1 then 1\n"
                    + "	else 0\n"
                    + "	end bloqueado,\n"
                    + "	case p.Pes_bloqueado\n"
                    + "	when 0 then 1\n"
                    + "	when 1 then 1\n"
                    + "	when 2 then 0\n"
                    + "	end situacaocadastro,\n"
                    + "	p.Pes_end endereco,\n"
                    + "	p.Pes_nrend numero,\n"
                    + "	p.Pes_compl complemento,\n"
                    + "	p.Pes_bai bairro,\n"
                    + "	p.Pes_cidade municipio,\n"
                    + "	p.Pes_uf uf,\n"
                    + "	p.Pes_cep cep,\n"
                    + "	p.Pes_estciv estadocivil,\n"
                    + "	p.Pes_dtnasc datanascimento,\n"
                    + "	p.Dtinc datacadastro,\n"
                    + "	p.Dtalt dataalteracao,\n"
                    + "	p.Pes_sexo sexo,\n"
                    + "	p.Pes_loctrab empresa,\n"
                    + "	p.Pes_endtrab empresaendereco,\n"
                    + "	p.Pes_nrendtra empresanumero,\n"
                    + "	p.Pes_baicob empresabairro,\n"
                    + "	p.Pes_cidcob empresacidade,\n"
                    + "	p.Pes_ufcob empresauf,\n"
                    + "	p.Pes_cepcob empresacep,\n"
                    + "	p.Pes_fonetrab empresatelefone,\n"
                    + "	p.Pes_dtadmis dataadmissao,\n"
                    + "	p.Pes_profissao cargo,\n"
                    + "	p.Pes_renda salario,\n"
                    + "	p.Pes_saldorestante + p.Pes_saldodisponivel valorlimite,\n"
                    + "	p.Pes_conjuge conjuge,\n"
                    + "	p.Pes_pai pai,\n"
                    + "	p.Pes_mae mae,\n"
                    + "	p.Pes_obs observacao,\n"
                    + "	p.Pes_diavenc diavencimento,\n"
                    + "	p.Pes_fone1,\n"
                    + "	p.Pes_fone2,\n"
                    + "	p.Pes_fone3,\n"
                    + "	p.Pes_celular,\n"
                    + "	p.Pes_email,\n"
                    + "	p.Pes_fax\n"
                    + "from\n"
                    + "	Pessoal p\n"
                    + "where\n"
                    + "	Pes_tipo = '001'\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpjcpf"));
                    if (imp.getCnpj() == null || "".equals(imp.getCnpj())) {
                        imp.setCnpj(imp.getId());
                    }
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setAtivo(rst.getBoolean("situacaocadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(rst.getString("estadocivil"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSexo(rst.getInt("sexo") == 0 ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("empresaendereco"));
                    imp.setEmpresaNumero(rst.getString("empresanumero"));
                    imp.setEmpresaBairro(rst.getString("empresabairro"));
                    imp.setEmpresaMunicipio(rst.getString("empresacidade"));
                    imp.setEmpresaUf(rst.getString("empresauf"));
                    imp.setEmpresaCep(rst.getString("empresacep"));
                    imp.setEmpresaTelefone(rst.getString("empresatelefone"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDiaVencimento(Utils.stringToInt(rst.getString("diavencimento")));
                    imp.setTelefone(rst.getString("Pes_fone1"));
                    imp.addTelefone("FONE 2", rst.getString("Pes_fone2"));
                    imp.addTelefone("FONE 3", rst.getString("Pes_fone3"));
                    imp.setCelular(rst.getString("Pes_celular"));
                    imp.setEmail(rst.getString("Pes_email"));
                    imp.setFax(rst.getString("Pes_fax"));

                    result.add(imp);

                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.Cre_prefixo,\n"
                    + "	c.Cre_numero,\n"
                    + "	c.Cre_parcela,\n"
                    + "	c.Cre_tipo,\n"
                    + "	c.Cre_dtemissao emissao,\n"
                    + "	c.Cre_caixa ecf,\n"
                    + "	c.Cre_total,\n"
                    + "	c.Cre_desconto,\n"
                    + "	c.Cre_vlpago,\n"
                    + "	c.Cre_juros juros,\n"
                    + "	c.Cre_clifor id_cliente,\n"
                    + "	c.Cre_historico obs,\n"
                    + "	c.Cre_dtvenc vencimento,\n"
                    + "	c.Cre_multap,\n"
                    + "	c.Cre_multav\n"
                    + "from\n"
                    + "	Contas_receber c\n"
                    + "where\n"
                    + "	c.Filial = '" + getLojaOrigem() + "' and \n"
                    + "	(c.Cre_desconto + c.Cre_vlpago) < c.Cre_total\n"
                    + "order by vencimento"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(String.format(
                            "%s-%s-%s-%s",
                            rst.getString("Cre_prefixo"),
                            rst.getString("Cre_numero"),
                            rst.getString("Cre_parcela"),
                            rst.getString("Cre_tipo")
                    ));
                    imp.setNumeroCupom(rst.getString("Cre_numero"));
                    imp.setParcela(Utils.stringToInt(rst.getString("Cre_parcela")));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("Cre_total"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setMulta(rst.getDouble("Cre_multav"));
                    if (rst.getDouble("Cre_vlpago") > 0) {
                        imp.addPagamento(
                                imp.getId(),
                                rst.getDouble("Cre_vlpago"),
                                rst.getDouble("Cre_desconto"),
                                0,
                                rst.getDate("vencimento"),
                                ""
                        );
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private int gerarCodigoAtacado() {
        Object[] options = {"ean atacado", "ean13", "ean14", "Cancelar"};
        int decisao = JOptionPane.showOptionDialog(null, "Escolha uma opção de ean",
                "Gerar eans", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return decisao;
    }

}
