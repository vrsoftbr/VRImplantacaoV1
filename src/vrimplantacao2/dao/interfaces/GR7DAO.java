/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class GR7DAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "GR7";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "d.codigo merc1,\n"
                    + "d.descricao merc1_desc,\n"
                    + "coalesce(g.codigo, 0) merc2,\n"
                    + "g.descricao merc2_desc,\n"
                    + "1 merc3, g.descricao merc3_desc\n"
                    + "from\n"
                    + "automacao.departamento d\n"
                    + "left join automacao.gondola g on d.codigo = g.codigo_depart\n"
                    + "where d.codigo > 0\n"
                    + "order by\n"
                    + "merc1, merc2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo,\n"
                    + "descricao\n"
                    + "from\n"
                    + "automacao.familia\n"
                    + "where\n"
                    + "codigo > 0\n"
                    + "order by\n"
                    + "codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.cod_produto id,\n"
                    + "p.produto descricaocompleta,\n"
                    + "p.produto_ecf descricaoreduzida,\n"
                    + "p.produto descricaogondola,\n"
                    + "case p.ativo when 'S' then 1 else 0 end as id_situacaocadastro,\n"
                    + "coalesce(data_cadastro, current_date()) datacadastro,\n"
                    + "p.cod_departamento merc1,\n"
                    + "p.cod_gondola merc2,\n"
                    + "p.nbm ncm,\n"
                    + "p.cest,\n"
                    + "p.cod_familia id_familia,\n"
                    + "p.lucro1 margem,\n"
                    + "p.cod_barras ean,\n"
                    + "p.qtd_emb,\n"
                    + "p.qtd_por_emb,\n"
                    + "p.cod_barras_cx ean_caixa,\n"
                    + "p.qtd_por_cx qtd_caixa,\n"
                    + "p.validade,\n"
                    + "p.unidade,\n"
                    + "p.peso_bruto,\n"
                    + "p.peso_liq,\n"
                    + "piscofins.pis_cst_sai piscof_cst_sai,\n"
                    + "piscofins.pis_cst_ent piscof_cst_ent,\n"
                    + "piscofins.nat_rec piscof_nat_rec,\n"
                    + "p.valor_venda1 preco,\n"
                    + "p.valor_compra custo,\n"
                    + "p.qtd_estoque estoque,\n"
                    + "p.qtd_minima estoque_minimo,\n"
                    + "p.cst_rev icms_cst,\n"
                    + "p.aliq_icms_interna icms_aliq,\n"
                    + "p.reduc_icms_rev icms_reducao,\n"
                    + "p.icms as icms_consumidor,\n"
                    + "case when p.pesavel != 'N' then 1 else 0 end pesavel\n"
                    + "from\n"
                    + "automacao.produto p\n"
                    + "left join\n"
                    + "automacao.pis_cofins piscofins on piscofins.codigo = p.cod_pis_cofins"                    /*+ "union all\n"
                    + "select\n"
                    + "p.cod_produto id,\n"
                    + "p.produto descricaocompleta,\n"
                    + "p.produto_ecf descricaoreduzida,\n"
                    + "p.produto descricaogondola,\n"
                    + "case p.ativo when 'S' then 1 else 0 end as id_situacaocadastro,\n"
                    + "coalesce(data_cadastro, current_date()) datacadastro,\n"
                    + "p.cod_departamento merc1,\n"
                    + "p.cod_gondola merc2,\n"
                    + "p.nbm ncm,\n"
                    + "p.cest,\n"
                    + "p.cod_familia id_familia,\n"
                    + "p.lucro1 margem,\n"
                    + "p.cod_barras_emb ean,\n"
                    + "p.qtd_por_emb qtd_embalagem,\n"
                    + "p.cod_barras_cx ean_caixa,\n"
                    + "p.qtd_por_cx qtd_caixa,\n"
                    + "p.validade,\n"
                    + "p.unid_da_emb unidade,\n"
                    + "p.peso_bruto,\n"
                    + "p.peso_liq,\n"
                    + "piscofins.pis_cst_sai piscof_cst_sai,\n"
                    + "piscofins.pis_cst_ent piscof_cst_ent,\n"
                    + "piscofins.nat_rec piscof_nat_rec,\n"
                    + "p.valor_venda1 preco,\n"
                    + "p.valor_compra custo,\n"
                    + "p.qtd_estoque estoque,\n"
                    + "p.qtd_minima estoque_minimo,\n"
                    + "p.cst_rev icms_cst,\n"
                    + "p.aliq_icms_interna icms_aliq,\n"
                    + "p.reduc_icms_rev icms_reducao,\n"
                    + "case when p.pesavel != 'N' then 1 else 0 end pesavel\n"
                    + "from\n"
                    + "automacao.produto p\n"
                    + "join\n"
                    + "automacao.pis_cofins piscofins on p.cod_pis_cofins = piscofins.codigo\n"
                    + "where cod_barras_emb <> ''"*/
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    
                    if ((rst.getString("ean") != null) &&
                            (!rst.getString("ean").trim().isEmpty())) {                        
                        imp.seteBalanca(Long.parseLong(Utils.formataNumero(rst.getString("ean").trim())) <= 999999);
                    } else {
                        imp.seteBalanca(false);
                    }
                    
                    //imp.seteBalanca((rst.getInt("pesavel") == 1));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setSituacaoCadastro(rst.getInt("id_situacaocadastro") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setTipoEmbalagem(rst.getString("unidade").contains("KG") ? "KG" : "UN");
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtd_emb"));
                    imp.setQtdEmbalagem(rst.getInt("qtd_emb"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liq"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscof_cst_sai"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscof_cst_ent"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscof_nat_rec"));
                    imp.setCest(rst.getString("cest"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducao(rst.getDouble("icms_reducao"));
                                        
                    if (rst.getString("icms_consumidor").contains("IS")) {
                        imp.setIcmsCstConsumidor("40");
                        imp.setIcmsAliqConsumidor(0);
                    } else if (rst.getString("icms_consumidor").contains("ST")) {
                        imp.setIcmsCstConsumidor("60");
                        imp.setIcmsAliqConsumidor(0);
                    } else if (rst.getString("icms_consumidor").contains("NT")) {
                        imp.setIcmsCstConsumidor("41");
                        imp.setIcmsAliqConsumidor(0);
                    } else {
                        imp.setIcmsCstConsumidor("0");
                        imp.setIcmsAliqConsumidor(Double.parseDouble(rst.getString("icms_consumidor").replace(",", ".")));
                    }
                    imp.setIcmsReducaoConsumidor(0);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        if (opcao == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "cod_produto id,\n"
                        + "cod_barras,\n"
                        + "cod_barras_emb ean,\n"
                        + "qtd_por_emb qtd_embalagem,"
                        + "valor_emb\n"
                        + "from\n"
                        + "automacao.produto\n"
                        + "where cod_barras_emb <> ''\n"
                        + "order by cod_barras_emb"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));
                        imp.setEan(rst.getString("ean"));
                        imp.setAtacadoPreco((rst.getDouble("valor_emb") / rst.getInt("qtd_embalagem")));
                        vResult.add(imp);
                    }
                }
                return vResult;
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        String cnpj;
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codigo id,\n"
                    + "coalesce(p.data_cadastro, current_date()) datacadastro,\n"
                    + "p.razao_nome razao,\n"
                    + "p.fantasia_apelido fantasia,\n"
                    + "trim(concat(p.tipo_logradouro ,' ', p.logradouro)) endereco,\n"
                    + "p.numero,\n"
                    + "p.complemento,\n"
                    + "p.bairro,\n"
                    + "c.codigo_cid id_municipio,\n"
                    + "c.codigo_est id_estado,\n"
                    + "c.cidade municipio,\n"
                    + "c.uf,\n"
                    + "p.cep,\n"
                    + "p.fone fone1,\n"
                    + "p.ie_rg inscricaoestadual,\n"
                    + "p.cnpj_cpf cnpj,\n"
                    + "p.obs observacao,\n"
                    + "p.fone_trab fone2,\n"
                    + "p.fone_fax fax,\n"
                    + "p.email,\n"
                    + "1 as id_situacaocadastro,\n"
                    + "p.tipo_empresa\n"
                    + "from\n"
                    + "automacao.participantes p\n"
                    + "left join automacao.cidades c on p.cod_cidade = c.codigo\n"
                    + "where\n"
                    + "p.tipo_participante like '%F%'\n"
                    + "order by\n"
                    + "p.codigo"
            )) {
                while (rst.next()) {
                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        cnpj = Utils.formataNumero(rst.getString("cnpj").trim());
                    } else {
                        cnpj = "";
                    }

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(Utils.formataNumero(rst.getString("fone1")));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setCnpj_cpf(cnpj);
                    imp.setTipo_inscricao(cnpj.length() > 12 ? TipoInscricao.JURIDICA : TipoInscricao.FISICA);
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setAtivo("1".equals(rst.getString("id_situacaocadastro")));
                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                Utils.formataNumero(rst.getString("fone2")),
                                "",
                                TipoContato.COMERCIAL,
                                ""
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                "",
                                TipoContato.COMERCIAL,
                                ""
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                rst.getString("email")
                        );
                    }

                    try (Statement stm2 = ConexaoMySQL.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select codigo, cod_partic, contato, fone, email\n"
                                + "from automacao.partic_contatos\n"
                                + "where cod_partic = " + imp.getImportId()
                        )) {
                            while (rst2.next()) {
                                imp.addContato(
                                        rst2.getString("codigo"),
                                        rst2.getString("contato"),
                                        rst2.getString("fone"),
                                        null,
                                        TipoContato.COMERCIAL,
                                        rst2.getString("email")
                                );
                            }
                        }
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "pf.cod_fornecedor id_fornecedor,\n"
                    + "pf.cod_produto id_produto,\n"
                    + "pf.cod_prod_fornec codigoexterno,\n"
                    + "p.qtd_por_emb as qtdembalagem\n"
                    + "from automacao.fornec_prod pf\n"
                    + "inner join automacao.produto p on p.cod_produto = pf.cod_produto\n"
                    + "order by id_fornecedor, id_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codigo id,\n"
                    + "p.razao_nome nome,\n"
                    + "trim(concat(p.tipo_logradouro ,' ', p.logradouro)) endereco,\n"
                    + "p.numero,\n"
                    + "p.complemento,\n"
                    + "p.bairro,\n"
                    + "c.codigo_cid id_municipio,\n"
                    + "c.codigo_est id_estado,\n"
                    + "c.cidade municipio,\n"
                    + "c.uf estado,\n"
                    + "p.cep,\n"
                    + "p.fone fone1,\n"
                    + "p.ie_rg inscricaoestadual,\n"
                    + "p.cnpj_cpf cnpj,\n"
                    + "1 as sexo,\n"
                    + "case p.data_cadastro when '0000-00-00' then current_date() else p.data_cadastro end as datacadastro,\n"
                    + "p.email,\n"
                    + "case when p.limite_geral > 0 then p.limite_geral else p.limite_credito end as limite,\n"
                    + "p.limite_cheque,\n"
                    + "p.fone_fax fax,\n"
                    + "p.status,\n"
                    + "p.obs observacao,\n"
                    + "case p.data_nascimento when '0000-00-00' then null else p.data_nascimento end as datanascimento,\n"
                    + "p.Pai nomePai,\n"
                    + "p.Mae nomeMae,\n"
                    + "p.local_trab empresa,\n"
                    + "p.fone_trab telempresa,\n"
                    + "p.orgao_exp,\n"
                    + "p.conjuge,\n"
                    + "p.conjuge_cpf,\n"
                    + "case p.conjuge_data_nasc when '0000-00-00' then null else p.conjuge_data_nasc end as conjuge_data_nasc,\n"
                    + "p.conjuge_orgao_exp,\n"
                    + "p.conjuge_rg\n"
                    + "                from\n"
                    + "                	automacao.participantes p \n"
                    + "left join automacao.cidades c on p.cod_cidade = c.codigo\n"
                    + "                where\n"
                    + "                	p.tipo_participante like '%C%'\n"
                    + "                order by\n"
                    + "                	p.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    if ((rst.getString("fone1") != null)
                            && (!rst.getString("fone1").trim().isEmpty())) {
                        if (rst.getString("fone1").trim().length() > 14) {
                            imp.setTelefone(Utils.formataNumero(rst.getString("fone1").substring(0, 14)));
                        } else {
                            imp.setTelefone(Utils.formataNumero(rst.getString("fone1")));
                        }
                    } else {
                        imp.setTelefone("000000000");
                    }
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    
                    
                    if ("5".equals(rst.getString("status"))) {
                        imp.setPermiteCreditoRotativo(false);
                        imp.setPermiteCheque(true);
                        imp.setValorLimite(rst.getDouble("limite_cheque"));
                    } else {
                        imp.setPermiteCreditoRotativo(true);
                        imp.setPermiteCheque(false);
                        imp.setValorLimite(rst.getDouble("limite"));
                    }
                    
                    if ((rst.getString("status") != null)
                            && (!rst.getString("status").trim().isEmpty())) {
                        if ("1".equals(rst.getString("status"))) { // ATIVO (LIBERADO)
                            imp.setAtivo(true);
                            imp.setBloqueado(false);
                        } else if ("3".equals(rst.getString("status"))) { // INATIVO (EXCLUIDO)
                            imp.setAtivo(false);
                            imp.setBloqueado(false);
                        } else if ("2".equals(rst.getString("status"))) { // NEGATIVADO (BLOQUEADO)
                            imp.setAtivo(true);
                            imp.setBloqueado(true);
                        } else {
                            imp.setAtivo(true);
                            imp.setBloqueado(false);
                        }
                    } else {
                        imp.setAtivo(false);
                        imp.setBloqueado(false);
                    }                 
                    
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomePai(rst.getString("nomePai"));
                    imp.setNomeMae(rst.getString("nomeMae"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaTelefone(rst.getString("telempresa"));
                    imp.setOrgaoemissor(rst.getString("orgao_exp"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setEmail(rst.getString("email"));
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())
                            && (rst.getString("fax").trim().length() > 14)) {
                        imp.addContato(
                                "2",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax").substring(0, 14)),
                                "",
                                ""
                        );
                    } else if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                "",
                                ""
                        );
                    }
                    String observacao2 = "";
                    try (Statement stm2 = ConexaoMySQL.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select codigo, cod_partic, contato, fone, email\n"
                                + "from automacao.partic_contatos\n"
                                + "where cod_partic = " + imp.getId()
                        )) {
                            while (rst2.next()) {
                                if ((rst2.getString("fone") != null)
                                        && (!rst2.getString("fone").trim().isEmpty())) {
                                    imp.addContato(
                                            rst2.getString("codigo"),
                                            rst2.getString("contato"),
                                            (rst2.getString("fone").length() > 14
                                                    ? rst2.getString("fone").substring(0, 14)
                                                    : rst2.getString("fone")),
                                            null,
                                            rst2.getString("email")
                                    );
                                }
                                if ((rst2.getString("email") != null)
                                        && (!rst2.getString("email").trim().isEmpty())) {
                                    observacao2 = observacao2 + rst2.getString("contato") + " - " + rst2.getString("email") + "   ";
                                    imp.setObservacao2(observacao2);
                                }
                            }
                        }
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "r.codigo,\n"
                    + "r.codigo_cliente,\n"
                    + "c.cnpj_cpf,\n"
                    + "r.data_venda,\n"
                    + "r.num_venda,\n"
                    + "r.valor_venda,\n"
                    + "coalesce(r.comprador,'') comprador,\n"
                    + "r.data_vcto,\n"
                    + "r.num_ecf\n"
                    + "from\n"
                    + "automacao.receber r\n"
                    + "join automacao.participantes c on r.codigo_cliente = c.codigo\n"
                    + "order by\n"
                    + "r.data_venda"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setIdCliente(rst.getString("codigo_cliente"));
                    imp.setDataEmissao(rst.getDate("data_venda"));
                    imp.setDataVencimento(rst.getDate("data_vcto"));
                    imp.setEcf(rst.getString("num_ecf"));
                    imp.setValor(rst.getDouble("valor_venda"));
                    imp.setNumeroCupom(rst.getString("num_venda"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        String comprador;
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "ch.codigo,\n"
                    + "ch.codigo_cliente,\n"
                    + "ch.cliente,\n"
                    + "c.razao_nome,\n"
                    + "c.cnpj_cpf,\n"
                    + "c.ie_rg,\n"
                    + "c.fone,\n"
                    + "ch.comprador,\n"
                    + "ch.obs,\n"
                    + "ch.num_venda,\n"
                    + "ch.num_cheque,\n"
                    + "ch.valor,\n"
                    + "ch.data,\n"
                    + "ch.data_vcto,\n"
                    + "b.banco,\n"
                    + "b.agencia,\n"
                    + "b.conta\n"
                    + "from\n"
                    + "automacao.cheque ch\n"
                    + "left join automacao.participantes c on c.codigo = ch.codigo_cliente\n"
                    + "left join automacao.partic_bancos b on b.codigo = ch.codigo_banco\n"
                    + "where\n"
                    + "ch.data_baixa1 is null\n"
                    + "and\n"
                    + "ch.data_baixa2 is null\n"
                    + "and\n"
                    + "ch.data_baixa3 is null\n"
                    + "and\n"
                    + "c.tipo_participante like '%C%'\n"
                    + "order by ch.codigo_cliente"
            )) {
                while (rst.next()) {
                    if ((rst.getString("comprador") != null)
                            && (!rst.getString("comprador").trim().isEmpty())) {
                        comprador = "COMPRADOR " + Utils.acertarTexto(rst.getString("comprador").trim()) + " ";
                    } else {
                        comprador = "";
                    }
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setNome(rst.getString("cliente"));
                    imp.setCpf(rst.getString("cnpj_cpf"));
                    imp.setRg(rst.getString("ie_rg"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setObservacao(comprador + rst.getString("obs"));
                    imp.setNumeroCheque(rst.getString("num_cheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setNumeroCupom(rst.getString("num_venda"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDate(rst.getDate("data"));
                    imp.setDataDeposito(rst.getDate("data_vcto"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    
    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        if (dataTermino == null) {
            dataTermino = new Date();
        }
        List<OfertaIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_produto,\n"
                    + "produto, \n"
                    + "cod_barras,\n"
                    + "valor_venda1,\n"
                    + "valor_promocional1,\n"
                    + "data_promo_inic,\n"
                    + "data_promo_final\n"
                    + "from produto\n"
                    + "where data_promo_inic is not null\n"
                    + "and data_promo_final is not null\n"
                    + "and data_promo_inic != '0000-00-00'\n"
                    + "and data_promo_final != '0000-00-00'\n"
                    + "and data_promo_final >= " + SQLUtils.stringSQL(
                            new SimpleDateFormat("yyyy-MM-dd").format(dataTermino))
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rst.getString("cod_produto"));
                    imp.setDataInicio(rst.getDate("data_promo_inic"));
                    imp.setDataFim(rst.getDate("data_promo_final"));
                    imp.setPrecoOferta(rst.getDouble("valor_promocional1"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);                    
                    result.add(imp);
                }
            }
        }        
        return result;
    }
    
}
