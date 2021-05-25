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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
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
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = Conexao.createStatement()) {
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

        try (Statement stm = Conexao.createStatement()) {
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
                    + "	p.pr_ult_precocusto as custocomimposto,\n"
                    + "	p.pr_custo_sem_icms as custosemimposto,\n"
                    + "	p.pr_precovenda_atual as precovenda,\n"
                    + "	p.ncm as ncm\n"
                    + "from implantacao.produtos_ondas p \n"
                    + "left join implantacao.produtoscodigobarras_ondas ean\n"
                    + "	on ean.pr_codint = p.pr_codint\n"
                    + "order by p.pr_codint::bigint"
            )) {
                while (rst.next()) {

                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
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

                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
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

                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
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
                    + "select * from implantacao.clientes_fornecedores_ondas c\n"
                    + "where c.cf_tipo = 'C'\n"
                    + "order by c.cf_codig::bigint"
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        return result;
    }
}
