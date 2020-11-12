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
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class DSoftDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "DSoft";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    m1.codigo as merc1,\n"
                    + "    m1.nome as desc_merc1,\n"
                    + "    m2.codigo as merc2,\n"
                    + "    m2.nome as desc_merc2,\n"
                    + "    m3.id as merc3,\n"
                    + "    m3.descricao as desc_merc3\n"
                    + "from grupo m1\n"
                    + "join subgrupo m2 on m2.codgrupo = m1.codigo\n"
                    + "join subcategorias m3 on m3.codgrupo = m1.codigo\n"
                    + "    and m3.codsubgrupo = m2.codigo\n"
                    + "order by 1, 3, 5"
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
                    + "    e.codigo as id,\n"
                    + "    e.codbarra as ean,\n"
                    + "    e.balanca,\n"
                    + "    e.validade,\n"
                    + "    e.descricao as descricaocompleta,\n"
                    + "    e.descrifiscal as descricaoreduzida,\n"
                    + "    e.med as tipoembalagem,\n"
                    + "    e.codgrupo as merc1,\n"
                    + "    grp.nome as desc_merc1,\n"
                    + "    e.codsubgrupo as merc2,\n"
                    + "    sgr.nome as desc_merc2,\n"
                    + "    e.codsubcategoria as merc3,\n"
                    + "    sub.codgrupo as grupo_subcategoria,\n"
                    + "    sub.codsubgrupo as subgrupo_subcategoria,\n"
                    + "    sub.descricao as descricao_subcategoria,\n"
                    + "    coalesce(e.precocusto, 0) as custo,\n"
                    + "    coalesce(e.precovenda, 0) as precovenda,\n"
                    + "    coalesce(e.qtde, 0) as qtdestoque,\n"
                    + "    coalesce(e.qtdeminima, 0) as estoqueminimo,\n"
                    + "    e.datacadastro,\n"
                    + "    case e.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "    e.cod_ncm as ncm,\n"
                    + "    e.cod_cest as cest,\n"
                    + "    e.cst,\n"
                    + "    e.icm,\n"
                    + "    e.perc_reducao_icms,\n"
                    + "    e.cst_entrada,\n"
                    + "    e.icms_entrada,\n"
                    + "    e.red_bc_entrada,\n"
                    + "    e.codaliquota as icms_ecf,\n"
                    + "    ecf.nome as descricao_aliq_ecf,\n"
                    + "    ecf.cst as cst_ecf,\n"
                    + "    ecf.aliquota as aliquota_ecf,\n"
                    + "    e.cst_pis,\n"
                    + "    e.cst_cofins,\n"
                    + "    e.natureza_pis_cofins as naturezareceita\n"
                    + "from estoque e\n"
                    + "left join ecfaliquota ecf on ecf.codigo = e.codaliquota\n"
                    + "left join subcategorias sub on sub.id = e.codsubcategoria\n"
                    + "left join grupo grp on grp.codigo = e.codgrupo\n"
                    + "left join subgrupo sgr on sgr.codigo = e.codsubgrupo\n"
                    + "order by 1"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    b.cod_produto as idproduto,\n"
                    + "    b.cod_barras as ean\n"
                    + "from estoque_codbarras b\n"
                    + "order by 1"
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
                    + "    f.codigo as id,\n"
                    + "    f.razao,\n"
                    + "    f.nome as fantasia,\n"
                    + "    f.cgc as cnpj,\n"
                    + "    f.ie, \n"
                    + "    f.cpf,\n"
                    + "    f.rg, \n"
                    + "    f.contato,\n"
                    + "    f.endereco,\n"
                    + "    f.num_endereco,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade as municipio,\n"
                    + "    f.uf,\n"
                    + "    f.cep,\n"
                    + "    f.telefone,\n"
                    + "    f.fax, \n"
                    + "    f.celular,\n"
                    + "    f.email,\n"
                    + "    f.email_secundario,\n"
                    + "    f.email_cotacao_web,\n"
                    + "    f.homepage,\n"
                    + "    case f.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "    f.prazo_entrega,\n"
                    + "    f.obs as observacao,\n"
                    + "    f.vendedor,\n"
                    + "    f.fone_vend,\n"
                    + "    f.representante,\n"
                    + "    f.fone_rep\n"
                    + "from fornecedor f\n"
                    + "order by 1"
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
                    + "    codigo_fornecedor as idfornecedor,\n"
                    + "    codigo_interno_sistema as idproduto,\n"
                    + "    codigo_fabricante as codigoexterno\n"
                    + "from estoque_codigo_fabric\n"
                    + "order by 1, 2"
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.codigo as id,\n"
                    + "    c.nome as razao,\n"
                    + "    c.cpf,\n"
                    + "    c.rg,\n"
                    + "    c.cgc,\n"
                    + "    c.ie as inscricaoestadual,\n"
                    + "    c.endereco,\n"
                    + "    c.num_endereco,\n"
                    + "    c.complemento,\n"
                    + "    c.complemento_endereco,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade as municipio,\n"
                    + "    c.uf,\n"
                    + "    c.cep,\n"
                    + "    c.endereco_cobranca,\n"
                    + "    c.complemento_cobranca,\n"
                    + "    c.bairro_cobranca,\n"
                    + "    c.cidade_cobranca,\n"
                    + "    c.uf_cobranca,\n"
                    + "    c.cep_cobranca,\n"
                    + "    c.endereco_entrega,\n"
                    + "    c.num_end_entrega,\n"
                    + "    c.bairro_entrega,\n"
                    + "    c.cidade_entrega,\n"
                    + "    c.uf_entrega,\n"
                    + "    c.cep_entrega,\n"
                    + "    c.telefone,\n"
                    + "    c.telefone_2,\n"
                    + "    c.telefone_conjuge,\n"
                    + "    c.telefone_entrega,\n"
                    + "    c.celular,\n"
                    + "    c.fax,\n"
                    + "    c.email,\n"
                    + "    c.email_secundario,\n"
                    + "    c.email_cobranca,\n"
                    + "    c.email_financeiro,\n"
                    + "    c.limitecredito as valorlimite,\n"
                    + "    c.datanascto as datanascimento,\n"
                    + "    c.datacadastro,\n"
                    + "    c.ondetrabalha as empresa,\n"
                    + "    c.fonetrabalho as telefoneempresa,\n"
                    + "    c.endtrabalho as enderecotrabalho,\n"
                    + "    c.funcao as cargo,\n"
                    + "    c.salario,\n"
                    + "    c.estadocivil,\n"
                    + "    c.conjugue,\n"
                    + "    c.conjugue_cpf,\n"
                    + "    c.conjugue_rg,\n"
                    + "    c.datanasctoconjugue,\n"
                    + "    c.nomepai,\n"
                    + "    c.nomemae,\n"
                    + "    case c.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "    c.contato\n"
                    + "from cliente c\n"
                    + "order by 1"
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
                    + "    r.codigo,\n"
                    + "    r.codcliente,\n"
                    + "    r.documento,\n"
                    + "    r.numparcela,\n"
                    + "    r.dataemissao,\n"
                    + "    r.datavencimento,\n"
                    + "    r.historico,\n"
                    + "    r.valorrecebido\n"
                    + "from receber r\n"
                    + "where r.valorrecebido is null\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        return null;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pg.codigo as id,\n"
                    + "    pg.codfornecedor as idfornecedor,\n"
                    + "    pg.documento as numerodocumento,\n"
                    + "    pg.dataemissao,\n"
                    + "    pg.datavencimento,\n"
                    + "    pg.valor,\n"
                    + "    pg.parcela,\n"
                    + "    pg.historico as observacao\n"
                    + "from pagar pg\n"
                    + "where pg.datapagamento is null\n"
                    + "and pg.codfornecedor is not null"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
