/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SolutionSuperaDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SolutionSupera";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo_emp as id, razaosocial from empresa ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("razaosocial")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "m1.codigo_grp as merc1,\n"
                    + "m1.grupo as desc_merc1,\n"
                    + "m2.codigo_sgp as merc2,\n"
                    + "m2.subgrupo as desc_merc2,\n"
                    + "'1' as merc3,\n"
                    + "m2.subgrupo as desc_merc3\n"
                    + "from grupos m1\n"
                    + "inner join subgrupos m2 on m2.codigo_grp = m1.codigo_grp\n"
                    + "order by m1.codigo_grp, m2.codigo_sgp"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codigo_pro as id,\n"
                    + "p.codigo_grp as merc1,\n"
                    + "p.codigo_sgp as merc2,\n"
                    + "p.codigo_ean as ean,\n"
                    + "p.descricao as descricaoproduto,\n"
                    + "p.cod_ncm as ncm,\n"
                    + "p.custo_unitario as custo,\n"
                    + "p.preco_venda as preco,\n"
                    + "p.unidade_entrada as embcompra,\n"
                    + "p.unidade_venda as embvenda,\n"
                    + "p.quanti_embalagem as qtdembalagem,\n"
                    + "p.estoque,\n"
                    + "p.peso_bruto,\n"
                    + "p.peso_liquido,\n"
                    + "p.data_cadastro,\n"
                    + "p.status as situacaocadastro,\n"
                    + "p.margemlucro as margem1,\n"
                    + "p.margemlucro2 as margem2,\n"
                    + "p.estoque_max,\n"
                    + "p.produto_balanca as balanca,\n"
                    + "p.cod_nat_receita as naturezareceita,\n"
                    + "p.cest, \n"
                    + "p.cst_pis_saida,\n"
                    + "p.cst_pis_entrada,\n"
                    + "p.cst_icms_saida_interno as cst_icms_debito,\n"
                    + "p.alq_icm_interna as aliq_icms_debito,\n"
                    + "p.reducao_interna as red_icms_debito,\n"
                    + "p.cst_icms_saida_externo as cst_icms_debito_fora_estado,\n"
                    + "p.cst_icms_entrada_interno as cst_icms_credito,\n"
                    + "p.alq_icm_externa as aliq_icms_credito,\n"
                    + "p.reducao_externa as red_icms_credito,\n"
                    + "p.cst_icms_entrada_externo as cst_icms_credito_fora_estado\n"
                    + "from produtos p\n"
                    + "order by p.codigo_pro"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.codigo_for as id,\n"
                    + "f.codigo_cid as ibge_municipio,\n"
                    + "upper(m.municipio) as municipio,\n"
                    + "m.cod_uf_ibge,\n"
                    + "u.sigla as uf,\n"
                    + "f.razao_soc as razao,\n"
                    + "f.fantasia,\n"
                    + "f.endereco,\n"
                    + "f.numero, \n"
                    + "f.bairro,\n"
                    + "f.cep,\n"
                    + "f.contato,\n"
                    + "f.telefone,\n"
                    + "f.fax,\n"
                    + "f.cnpj,\n"
                    + "f.inscricao as ie_rg,\n"
                    + "f.situacao as situacaocadastro,\n"
                    + "f.emaill, \n"
                    + "f.representante,\n"
                    + "f.endereco_rep,\n"
                    + "f.numero_rep,\n"
                    + "f.bairro_rep,\n"
                    + "f.cidade_rep,\n"
                    + "f.cep_rep,\n"
                    + "f.estado_rep,\n"
                    + "f.telefone_rep, \n"
                    + "f.fax_rep,\n"
                    + "f.celular, \n"
                    + "f.emaill_rep\n"
                    + "from fornecedores f\n"
                    + "left join municipios_ibge m on m.cod_municipio_ibge = f.codigo_cid\n"
                    + "inner join uf_ibge u on u.cod_uf = m.cod_uf_ibge\n"
                    + "order by f.codigo_for"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id_fornecedor,\n"
                    + "id_produto,\n"
                    + "id\n"
                    + "from produto_fornecedores\n"
                    + "order by id_fornecedor, id_produto"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "c.codigo_cli as id,\n"
                    + "c.codigo_cid as ibge_municipio,\n"
                    + "c.cod_tipo_logradouro,\n"
                    + "c.codigo_bco as id_banco,\n"
                    + "c.razaosocial as razao,\n"
                    + "c.fantasia,\n"
                    + "c.endereco,\n"
                    + "c.bairro,\n"
                    + "c.cep, \n"
                    + "c.telefone, \n"
                    + "c.telefax as fax,\n"
                    + "c.contato, \n"
                    + "c.cnpjcpf,\n"
                    + "c.inscricao,\n"
                    + "c.identidade as ie_rg,\n"
                    + "c.endereco_cob,\n"
                    + "c.bairro_cob,\n"
                    + "c.cidade_cob,\n"
                    + "c.cep_cob,\n"
                    + "c.telefone_cob,\n"
                    + "c.telefax_cob,\n"
                    + "c.contato_cob,\n"
                    + "c.filiacao_pai as nome_pai,\n"
                    + "c.filiacao_mae as nome_mae,\n"
                    + "c.conjugue,\n"
                    + "c.cpf as cpf_conjuge,\n"
                    + "c.identidade_con as ierg_conjuge,\n"
                    + "c.data_cadastro,\n"
                    + "c.data_nascimento,\n"
                    + "c.status as situacaocadastro,\n"
                    + "c.observacao, \n"
                    + "c.emaill,\n"
                    + "c.celular, \n"
                    + "c.limite_cred,\n"
                    + "c.numero,\n"
                    + "c.complemento_endereco,\n"
                    + "c.sexo\n"
                    + "from clientes c\n"
                    + "left join municipios_ibge m on m.cod_municipio_ibge = c.codigo_cid\n"
                    + "inner join uf_ibge u on u.cod_uf = m.cod_uf_ibge\n"
                    + "order by c.codigo_cli"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_crc,\n"
                    + "titulo as cupom,\n"
                    + "codigo_cli as id_cliente,\n"
                    + "fatura as numerocupom,\n"
                    + "data_emi as emissao,\n"
                    + "data_ven as vencimento,\n"
                    + "valor_tit as valor,\n"
                    + "observacao\n"
                    + "from contasreceber\n"
                    + "where data_pgt is null\n"
                    + "order by data_emi asc"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
