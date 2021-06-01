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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class LiteciDAO extends InterfaceDAO {

    private String idLoja = "";
    public String v_lojaMesmoId;
    public boolean gerarCodigoAtacado = false;
    private ProdutoRepositoryProvider repository = new ProdutoRepositoryProvider();

    public void setLojaCliente(String idLoja) {
        this.idLoja = idLoja;
    }

    @Override
    public String getSistema() {
        return "Liteci" + v_lojaMesmoId;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select codigo, fantasia from tbempresas order by codigo")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("fantasia")));
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
                    "select codigo, descricao from tbsecao order by codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("codigo"));
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
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ATACADO
        ));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.coditem,\n"
                    + "p.codbarra,\n"
                    + "p.codbalanca,\n"
                    + "p.debalanca,\n"
                    + "p.pesound,\n"
                    + "p.descricao,\n"
                    + "p.descabrev,\n"
                    + "p.validade,\n"
                    + "p.codundi as tipoembalagem,\n"
                    + "p.codgrupoi,\n"
                    + "p.codsubgrupoi,\n"
                    + "p.codsecaoi,\n"
                    + "p.codfamiliaprodutoi,\n"
                    + "p.dtcad,\n"
                    + "p.pesobruto,\n"
                    + "p.pesoliquido,\n"
                    + "p.margemideal as margem,\n"
                    + "p.valorultcompra as custo,\n"
                    + "p.valor as precovenda,\n"
                    + "p.qtdminima as estminimo,\n"
                    + "est.qtddisponivel,\n"
                    + "p.ativo,\n"
                    + "ncm.codncm,\n"
                    + "ncm.cstpis,\n"
                    + "ncm.cstcofins,\n"
                    + "ncm.cstpisentrada,\n"
                    + "ncm.cstcofinsentrada,\n"
                    + "ncm.codnaturezareceita,\n"
                    + "p.cest_st as cest,\n"
                    + "ncm.csticms,\n"
                    + "ncm.taxaicms\n"
                    + "from tbitem p\n"
                    + "left join tbncm ncm on ncm.chave = p.codncm\n"
                    + "left join tbestoque est on est.coditemi = p.coditem and est.codfiliali = " + getLojaOrigem()
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("coditem"));
                    imp.setEan(rst.getString("codbarra"));

                    if (rst.getInt("codbalanca") > 0) {
                        long codigoProduto;
                        codigoProduto = Long.parseLong(Utils.formataNumero(imp.getEan()));
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }

                        if (produtoBalanca != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade());
                        } else {
                            imp.setValidade(rst.getInt("validade"));
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.seteBalanca(false);
                        imp.setValidade(rst.getInt("validade"));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descabrev"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setCodMercadologico1(rst.getString("codsecaoi"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(rst.getDate("dtcad"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estminimo"));
                    imp.setEstoque(rst.getDouble("qtddisponivel"));
                    imp.setSituacaoCadastro("S".equals(rst.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("codncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpis"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpisentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("codnaturezareceita"));
                    imp.setIcmsCstSaida(rst.getInt("csticms"));
                    imp.setIcmsAliqSaida(rst.getDouble("taxaicms"));
                    imp.setIcmsReducaoSaida(0);
                    imp.setIcmsCstEntrada(rst.getInt("csticms"));
                    imp.setIcmsAliqEntrada(rst.getDouble("taxaicms"));
                    imp.setIcmsReducaoEntrada(0);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "b.coditemi,\n"
                    + "b.codbarra,\n"
                    + "b.qtd,\n"
                    + "b.percdesconto,\n"
                    + "p.valor as precovenda,\n"
                    + "b.valorunit as precoatacado,\n"
                    + "p.codundi as tipoembalagem\n"
                    + "from tbitemcodbarra b\n"
                    + "inner join tbitem p on p.coditem = b.coditemi\n"
                    + "where coalesce(b.qtd, 0) > 1\n"
                    + "and coalesce(b.percdesconto, 0) > 0\n"
                    + "order by b.coditemi"
            )) {
                while (rst.next()) {
                    
                    String strEan = "";
                    long ean;
                    int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("coditemi"));
                    
                    if (!gerarCodigoAtacado) {
                        if ((rst.getString("codbarra") != null)
                                && (!rst.getString("codbarra").trim().isEmpty())) {
                            ean = Long.parseLong(Utils.formataNumero(rst.getString("codbarra")));

                            if (ean <= 999999) {
                                strEan = "999999" + String.valueOf(codigoAtual);
                            } else {
                                strEan = rst.getString("codbarra");
                            }
                            
                        } else {
                            strEan = "999999" + String.valueOf(codigoAtual);
                        }
                    } else {
                        if ((rst.getString("codbarra") != null)
                                && (!rst.getString("codbarra").trim().isEmpty())) {
                            ean = Long.parseLong(Utils.formataNumero(rst.getString("codbarra")));

                            if (repository.automacao().cadastrado(ean)) {
                                if (!repository.automacao().getEanById(ean, codigoAtual)) {
                                    strEan = "999999" + String.valueOf(codigoAtual);
                                }
                            } else {
                                strEan = rst.getString("codbarra");
                            }
                        } else {
                            strEan = "999999" + String.valueOf(codigoAtual);
                        }
                    }

                    if (codigoAtual > 0) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("coditemi"));
                        imp.setEan(strEan);
                        imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                        imp.setQtdEmbalagem(rst.getInt("qtd"));
                        result.add(imp);
                    }
                }
            }
        }
        return result;  
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "b.coditemi,\n"
                        + "b.codbarra,\n"
                        + "b.qtd,\n"
                        + "b.percdesconto,\n"
                        + "p.valor as precovenda,\n"
                        + "b.valorunit as precoatacado\n"
                        + "from tbitemcodbarra b\n"
                        + "inner join tbitem p on p.coditem = b.coditemi\n"
                        + "where coalesce(b.qtd, 0) > 1\n"
                        + "and coalesce(b.percdesconto, 0) > 0\n"
                        + "order by b.coditemi"
                )) {
                    while (rst.next()) {
                        long ean;
                        String strEan = "";
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("coditemi"));

                        if (!gerarCodigoAtacado) {
                            if ((rst.getString("codbarra") != null)
                                    && (!rst.getString("codbarra").trim().isEmpty())) {
                                ean = Long.parseLong(Utils.formataNumero(rst.getString("codbarra")));

                                if (ean <= 999999) {
                                    strEan = "999999" + String.valueOf(codigoAtual);
                                } else {
                                    strEan = rst.getString("codbarra");
                                }
                            } else {
                                strEan = "999999" + String.valueOf(codigoAtual);
                            }
                        } else {                            
                            strEan = "999999" + String.valueOf(codigoAtual);
                        }

                        if (codigoAtual > 0) {
                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("coditemi"));
                            imp.setEan(strEan);
                            imp.setQtdEmbalagem(rst.getInt("qtd"));
                            imp.setPrecovenda(rst.getDouble("precovenda"));
                            imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                            result.add(imp);
                        }
                    }
                }
                return result;
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
                    + "f.codfor,\n"
                    + "f.nome as razao,\n"
                    + "f.apelidio as fantasia,\n"
                    + "f.endereco,\n"
                    + "f.numero,\n"
                    + "f.complemento,\n"
                    + "f.cep,\n"
                    + "f.bairro,\n"
                    + "f.uf,\n"
                    + "f.cidade,\n"
                    + "cid.descricao municipio,\n"
                    + "cid.codibge municipio_ibge,\n"
                    + "cid.codufibge as uf_ibge,\n"
                    + "f.fone,\n"
                    + "f.fax,\n"
                    + "f.celular,\n"
                    + "f.celularcotacao,\n"
                    + "f.email,\n"
                    + "f.emailcotacao,\n"
                    + "f.rg,\n"
                    + "f.cnpj,\n"
                    + "f.dtcad,\n"
                    + "f.obsgerais,\n"
                    + "f.contato\n"
                    + "from tbfor f\n"
                    + "left join tbcidade cid on cid.codigo = f.codcidadei"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codfor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("rg"));
                    imp.setDatacadastro(rst.getDate("dtcad"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_uf(rst.getInt("uf_ibge"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.setObservacao(rst.getString("obsgerais"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "CELULAR",
                                null,
                                rst.getString("celular"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("celularcotacao") != null)
                            && (!rst.getString("celularcotacao").trim().isEmpty())) {
                        imp.addContato(
                                "CEL COTACAO",
                                null,
                                rst.getString("celularcotacao"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                null,
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    if ((rst.getString("emailcotacao") != null)
                            && (!rst.getString("emailcotacao").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL COT",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("emailcotacao").toLowerCase()
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "coditemi as idproduto,\n"
                    + "codfori as idfornecedor,\n"
                    + "coditemforn as codexterno,\n"
                    + "qtdentrada as qtdembalagem\n"
                    + "from tbitemcodforn\n"
                    + "order by 2, 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
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
                    + "c.codcli,\n"
                    + "c.nome as razao,\n"
                    + "c.apelidio as fantasia,\n"
                    + "c.cpf,\n"
                    + "c.rg,\n"
                    + "c.orgemissorrg,\n"
                    + "c.dtcad,\n"
                    + "c.endereco,\n"
                    + "c.numero,\n"
                    + "c.complemento,\n"
                    + "c.bairro,\n"
                    + "cid.descricao as municipio,\n"
                    + "cid.codibge as municipio_ibge,\n"
                    + "cid.codufibge as uf_ibge,\n"
                    + "c.uf,\n"
                    + "c.cep,\n"
                    + "c.fone,\n"
                    + "c.fax,\n"
                    + "c.celular,\n"
                    + "c.email,\n"
                    + "c.renda,\n"
                    + "c.limite,\n"
                    + "c.dtnasc,\n"
                    + "c.obs,\n"
                    + "c.ativo,\n"
                    + "c.pai,\n"
                    + "c.mae,\n"
                    + "c.endentrega,\n"
                    + "c.numendentrega,\n"
                    + "c.complentrega,\n"
                    + "c.bairroentrega,\n"
                    + "c.cepentrega,\n"
                    + "c.codcidadeentrega,\n"
                    + "cide.descricao as municipio_ent,\n"
                    + "cide.codibge as municipio_ibge_ent,\n"
                    + "cide.codufibge as uf_ibge_ent,\n"
                    + "c.ufentrega as uf_ent,\n"
                    + "c.endcobranca,\n"
                    + "c.numendcobranca,\n"
                    + "c.complcobranca,\n"
                    + "c.cepcobranca,\n"
                    + "c.bairrocobranca,\n"
                    + "cidb.descricao as municipio_cob,\n"
                    + "cidb.codibge as municipio_ibge_cob,\n"
                    + "cidb.codufibge as uf_ibge_cob,\n"
                    + "c.ufcobranca as uf_cobranca,\n"
                    + "c.nomeempresatrabalho,\n"
                    + "c.contatoempresatrabalho,\n"
                    + "c.foneempresatrabalho,\n"
                    + "c.funcaotrabalho,\n"
                    + "c.sexo\n"
                    + "from tbcli c\n"
                    + "left join tbcidade cid on cid.codigo = c.codcidadei\n"
                    + "left join tbcidade cide on cide.codigo = c.codcidadeentrega\n"
                    + "left join tbcidade cidb on cidb.codigo = c.codcidadecobranca\n"
                    + "order by c.codcli"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codcli"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(rst.getString("rg"));
                    imp.setOrgaoemissor(rst.getString("orgemissorrg"));
                    imp.setDataCadastro(rst.getDate("dtcad"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep("cep");
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setUfIBGE(rst.getInt("uf_ibge"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setAtivo(rst.getString("ativo").contains("S"));
                    imp.setEmpresa(rst.getString("nomeempresatrabalho"));
                    imp.setEmpresaTelefone(rst.getString("foneempresatrabalho"));
                    imp.setCargo(rst.getString("funcaotrabalho"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setCobrancaEndereco(rst.getString("endcobranca"));
                    imp.setCobrancaNumero(rst.getString("numendcobranca"));
                    imp.setCobrancaComplemento(rst.getString("complcobranca"));
                    imp.setCobrancaCep(rst.getString("cepcobranca"));
                    imp.setCobrancaBairro(rst.getString("bairrocobranca"));
                    imp.setCobrancaMunicipio(rst.getString("municipio_cob"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("municipio_ibge_cob"));
                    imp.setCobrancaUf(rst.getString("uf_cobranca"));
                    imp.setCobrancaUfIBGE(rst.getInt("uf_ibge_cob"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
