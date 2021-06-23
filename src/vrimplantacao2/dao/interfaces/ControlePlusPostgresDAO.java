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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class ControlePlusPostgresDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "ControlePlus";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
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
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));        
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.RAZAO,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.PERMITE_CREDITOROTATIVO,
                OpcaoCliente.PERMITE_CHEQUE,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.OBSERVACOES
        ));
    }

    public List<Estabelecimento> getLojaCliente() throws SQLException {
        return Arrays.asList(new Estabelecimento("1", "LOJA 01"));
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	substring(se_codig, 1, 2) as merc1,\n"
                    + "	se_nome as descricao\n"
                    + "from implantacao.mercadologico_ondas\n"
                    + "where se_tipo = '1'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("descricao"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	substring(se_codig, 1, 2) as merc1,\n"
                    + "	substring(se_codig, 3, 2) as merc2,\n"
                    + "	se_nome as descricao\n"
                    + "from implantacao.mercadologico_ondas\n"
                    + "where se_tipo = '2'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc2 = merc.get(rst.getString("merc1"));
                    if (merc2 != null) {
                        merc2.addFilho(
                                rst.getString("merc2"),
                                rst.getString("descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	substring(se_codig, 1, 2) as merc1,\n"
                    + "	substring(se_codig, 3, 2) as merc2,\n"
                    + "	substring(se_codig, 5, 2) as merc3,\n"
                    + "	se_nome as descricao\n"
                    + "from implantacao.mercadologico_ondas\n"
                    + "where se_tipo = '3'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("descricao")
                            );
                        }
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	substring(se_codig, 1, 2) as merc1,\n"
                    + "	substring(se_codig, 3, 2) as merc2,\n"
                    + "	substring(se_codig, 5, 2) as merc3,\n"
                    + "	substring(se_codig, 7, 2) as merc4,\n"
                    + "	se_nome as descricao\n"
                    + "from implantacao.mercadologico_ondas\n"
                    + "where se_tipo = '4'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            MercadologicoNivelIMP merc3 = merc2.getNiveis().get(rst.getString("merc3"));
                            if (merc3 != null) {
                                merc3.addFilho(
                                        rst.getString("merc4"),
                                        rst.getString("descricao")
                                );
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        Double qtdEmbalagem, qtdEmbalagemCotacao;

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.pr_codint as id,\n"
                    + "	ean.pr_cbarra as codigobarras,\n"
                    + "	p.pr_peso_variavel as balanca,\n"
                    + "	p.pr_dias_validade as validade,\n"
                    + "	p.uni_venda as tipoembalagem,\n"
                    + "	ean.pr_qtde as qtdembalagem,\n"
                    + "	p.pr_venda_peso_unidade,\n"
                    + "	p.tc_codig as tipoembalagemcotacao,\n"
                    + "	p.pr_qtde_caixa as qtdembalagemcotacao,\n"
                    + "	p.pr_nome as descricaocompleta,\n"
                    + "	p.pr_nomeabreviado as descricaoreduzida,\n"
                    + "	p.pr_nomegondola as descricaogondola,\n"
                    + "	substring(p.se_codig, 1, 2) as mercadologico1,\n"
                    + "	substring(p.se_codig, 3, 2) as mercadologico2,\n"
                    + "	substring(p.se_codig, 5, 2) as mercadologico3,\n"
                    + "	substring(p.se_codig, 7, 2) as mercadologico4,\n"
                    + "	case p.pr_ativo when 'S' then 1 else 0 end situacaocadastro,\n"
                    + "	p.pr_data_alteracao as dataalteracao,\n"
                    + "	p.data_inc as datacadastro,\n"
                    + " p.pr_margem_bruta_scusto as margem, \n"
                    + "	p.pr_ult_precocusto as custocomimposto,\n"
                    + "	p.pr_custo_sem_icms as custosemimposto,\n"
                    + "	p.pr_precovenda_atual as precovenda,\n"
                    + " p.estoque_minimo as estoqueminimo, \n"
                    + "	p.ncm as ncm,\n"
                    + " pis.pis_cst_e as piscofinsentrada,\n"
                    + "	pis.pis_cst_s as piscofinssaida, \n"
                    + " pis.cod_natureza_receita as naturezareceita, \n"
                    + " icm_s.sac_cst as cstdebito,\n"
                    + " icm_s.sac_alq as aliquotadebito, \n"
                    + " icm_s.sac_rbc as reducaodebito, \n"
                    + " icm_e.ei_cst as cstcredito, \n"
                    + " icm_e.ei_alq as aliquotacredito, \n"
                    + " icm_e.ei_rbc as reducaocredito \n"
                    + "from implantacao.produtos_ondas p \n"
                    + "left join implantacao.produtoscodigobarras_ondas ean\n"
                    + "	on ean.pr_codint = p.pr_codint\n"
                    + "left join implantacao.produtos_piscofins pis\n"
                    + "	on pis.codigo_produto = p.pr_codint \n"
                    + "left join implantacao.produtos_icms_saida icm_s\n"
                    + "	on icm_s.codigo_produto = p.pr_codint\n"
                    + "left join implantacao.produtos_icms_entrada icm_e \n"
                    + "	on icm_e.codigo_produto = p.pr_codint "
                    + "order by p.pr_codint::bigint"
            )) {
                while (rst.next()) {

                    qtdEmbalagem = Double.parseDouble(rst.getString("qtdembalagem").replace(".", "").replace(",", "."));
                    qtdEmbalagemCotacao = Double.parseDouble(rst.getString("qtdembalagemcotacao").replace(".", "").replace(",", "."));

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.seteBalanca("S".equals(rst.getString("balanca")));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagemcotacao"));
                    imp.setQtdEmbalagemCotacao(qtdEmbalagemCotacao.intValue());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(qtdEmbalagem.intValue());
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setCodMercadologico4(rst.getString("mercadologico4"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));

                    if (rst.getString("estoqueminimo") != null && !rst.getString("estoqueminimo").trim().isEmpty()) {
                        imp.setEstoqueMinimo(Double.parseDouble(rst.getString("estoqueminimo").replace(".", "").replace(",", ".")));
                    }

                    if (rst.getString("margem") != null && !rst.getString("margem").trim().isEmpty()) {
                        imp.setMargem(Double.parseDouble(rst.getString("margem").replace(".", ",").replace(",", ".")));
                    }

                    if (rst.getString("custocomimposto") != null && !rst.getString("custocomimposto").trim().isEmpty()) {
                        imp.setCustoComImposto(Double.parseDouble(rst.getString("custocomimposto").replace(".", "").replace(",", ".")));
                    }

                    if (rst.getString("custosemimposto") != null && !rst.getString("custosemimposto").trim().isEmpty()) {
                        imp.setCustoSemImposto(Double.parseDouble(rst.getString("custosemimposto").replace(".", "").replace(",", ".")));
                    }

                    if (rst.getString("precovenda") != null && !rst.getString("precovenda").trim().isEmpty()) {
                        imp.setPrecovenda(Double.parseDouble(rst.getString("precovenda").replace(".", "").replace(",", ".")));
                    }

                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                    if (rst.getString("cstdebito") != null && !rst.getString("cstdebito").trim().isEmpty()) {
                        imp.setIcmsCstSaida(rst.getInt("cstdebito"));
                        imp.setIcmsCstSaidaForaEstado(rst.getInt("cstdebito"));
                        imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("cstdebito"));
                        imp.setIcmsCstConsumidor(rst.getInt("cstdebito"));
                    }

                    if (rst.getString("aliquotadebito") != null && !rst.getString("aliquotadebito").trim().isEmpty()) {
                        imp.setIcmsAliqSaida(Double.parseDouble(rst.getString("aliquotadebito").replace(",", ".")));
                        imp.setIcmsAliqSaidaForaEstado(Double.parseDouble(rst.getString("aliquotadebito").replace(",", ".")));
                        imp.setIcmsAliqSaidaForaEstadoNF(Double.parseDouble(rst.getString("aliquotadebito").replace(",", ".")));
                        imp.setIcmsAliqConsumidor(Double.parseDouble(rst.getString("aliquotadebito").replace(",", ".")));
                    }

                    if (rst.getString("reducaodebito") != null && !rst.getString("reducaodebito").trim().isEmpty()) {
                        imp.setIcmsReducaoSaida(Double.parseDouble(rst.getString("reducaodebito").replace(",", ".")));
                        imp.setIcmsReducaoSaidaForaEstado(Double.parseDouble(rst.getString("reducaodebito").replace(",", ".")));
                        imp.setIcmsReducaoSaidaForaEstadoNF(Double.parseDouble(rst.getString("reducaodebito").replace(",", ".")));
                        imp.setIcmsReducaoConsumidor(Double.parseDouble(rst.getString("reducaodebito").replace(",", ".")));
                    }

                    if (rst.getString("cstcredito") != null && !rst.getString("cstcredito").trim().isEmpty()) {
                        imp.setIcmsCstEntrada(rst.getInt("cstcredito"));
                        imp.setIcmsCstEntradaForaEstado(rst.getInt("cstcredito"));
                    }

                    if (rst.getString("aliquotacredito") != null && !rst.getString("aliquotacredito").trim().isEmpty()) {
                        imp.setIcmsAliqEntrada(Double.parseDouble(rst.getString("aliquotacredito").replace(",", ".")));
                        imp.setIcmsAliqEntradaForaEstado(Double.parseDouble(rst.getString("aliquotacredito").replace(",", ".")));
                    }

                    if (rst.getString("reducaocredito") != null && !rst.getString("reducaocredito").trim().isEmpty()) {
                        imp.setIcmsReducaoEntrada(Double.parseDouble(rst.getString("reducaocredito").replace(",", ".")));
                        imp.setIcmsReducaoEntradaForaEstado(Double.parseDouble(rst.getString("reducaocredito").replace(",", ".")));
                    }
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
                    + "	f.cf_codig as id,\n"
                    + "	f.cf_cgc as cnpj,\n"
                    + "	f.cf_inscr as inscricaoestadual,\n"
                    + "	f.im as inscricaomunicipal,\n"
                    + "	f.cnae,\n"
                    + "	f.cf_razao as razao,\n"
                    + "	f.cf_fanta as fantasia,\n"
                    + "	f.cf_ender as endereco,\n"
                    + "	f.cf_numero_endereco as numero,\n"
                    + "	f.cf_complemento as complemento,\n"
                    + "	f.cf_bairr as bairro,\n"
                    + "	f.cf_cidad as municipio,\n"
                    + "	f.mnc_codig as municipioibge,\n"
                    + "	f.cf_uf as uf,\n"
                    + "	f.cf_cep as cep,\n"
                    + "	f.data_inc as datadadastro,\n"
                    + "	case f.cf_inativo when 'F' then 1 else 0 end ativo,\n"
                    + "	f.cf_observ as observacao,\n"
                    + "	f.cf_simples_nacional,\n"
                    + "	f.flg_indiedest,\n"
                    + "	f.cf_telef1 as telefone,\n"
                    + "	f.cf_telef2 as telefone2,\n"
                    + "	f.cf_fax as fax\n"
                    + "from implantacao.clientes_fornecedores_ondas f\n"
                    + "where f.cf_tipo = 'F'\n"
                    + "order by f.cf_codig::bigint"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rst.getString("inscricaomunicipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));
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
                    + "	cf_codig as idfornecedor,\n"
                    + "	pr_codint as idproduto,\n"
                    + "	codigo_prd_for as codigoexterno,\n"
                    + "	data_inc as datalteracao\n"
                    + "from implantacao.produtosfornecedores_ondas\n"
                    + "order by cf_codig::bigint, pr_codint::bigint"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
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
                    + "	c.cf_codig as id,\n"
                    + "	c.cf_cgc as cnpj,\n"
                    + "	c.cf_inscr as inscricaoestadual,\n"
                    + "	c.im as inscricaomunicipal,\n"
                    + "	c.cnae,\n"
                    + "	c.cf_razao as razao,\n"
                    + "	c.cf_fanta as fantasia,\n"
                    + "	c.cf_ender as endereco,\n"
                    + "	c.cf_numero_endereco as numero,\n"
                    + "	c.cf_complemento as complemento,\n"
                    + "	c.cf_bairr as bairro,\n"
                    + "	c.cf_cidad as municipio,\n"
                    + "	c.mnc_codig as municipioibge,\n"
                    + "	c.cf_uf as uf,\n"
                    + "	c.cf_cep as cep,\n"
                    + "	c.data_inc as datadadastro,\n"
                    + "	case c.cf_inativo when 'F' then 1 else 0 end ativo,\n"
                    + "	c.cf_observ as observacao,\n"
                    + "	c.cf_simples_nacional,\n"
                    + "	c.flg_indiedest,\n"
                    + "	c.cf_telef1 as telefone,\n"
                    + "	c.cf_telef2 as telefone2,\n"
                    + "	c.cf_fax as fax\n"
                    + "from implantacao.clientes_fornecedores_ondas c\n"
                    + "where c.cf_tipo = 'C'\n"
                    + "order by c.cf_codig::bigint"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
