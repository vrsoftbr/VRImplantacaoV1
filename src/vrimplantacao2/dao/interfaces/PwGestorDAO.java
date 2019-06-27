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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class PwGestorDAO extends InterfaceDAO {

    private String complementoSistema = "";
    private Date dtOfertas;

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.NCM,
                OpcaoProduto.ATIVO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ICMS,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.CEST,
                OpcaoProduto.ATACADO,
                OpcaoProduto.OFERTA
        ));
    }

    public void setComplementoSistema(String complementoSistema) {
        this.complementoSistema = complementoSistema == null ? "" : complementoSistema.trim();
    }

    @Override
    public String getSistema() {
        return (!"".equals(complementoSistema) ? this.complementoSistema + "-" : "") + "PwGestor";
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT razao, cgc FROM config"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cgc"), rst.getString("razao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descri from familia order by codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("descri"));
                    result.add(imp);
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
                    "select distinct\n"
                    + "d.cont,\n"
                    + "p.depar,\n"
                    + "g.codgrupo,\n"
                    + "p.nomegrupo\n"
                    + "from produtos p, departamentos d, grupos g\n"
                    + "where p.depar = d.nome\n"
                    + "and p.nomegrupo = g.nomegrupo\n"
                    + "and depar <> ''\n"
                    + "order by d.cont, g.codgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("cont"));
                    imp.setMerc1Descricao(rst.getString("depar"));
                    imp.setMerc2ID(rst.getString("codgrupo"));
                    imp.setMerc2Descricao(rst.getString("nomegrupo"));
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.ordem as id,\n"
                    + "p.balanca,\n"
                    + "p.barra as ean,\n"
                    + "p.descricao,\n"
                    + "p.unidade,\n"
                    + "p.estoque,\n"
                    + "p.estoqueminimo,\n"
                    + "p.estoquemax,\n"
                    + "p.custo,\n"
                    + "p.vista as precovenda,\n"
                    + "p.vista2 as precovenda2,\n"
                    + "p.marckup,\n"
                    + "p.markup_fixo,\n"
                    + "p.cst,\n"
                    + "p.icms,\n"
                    + "p.icms2,\n"
                    + "p.reducao_icms,\n"
                    + "p.pis,\n"
                    + "p.cofins,\n"
                    + "p.ncm,\n"
                    + "p.dtcad,\n"
                    + "p.pesol,\n"
                    + "p.pesob,\n"
                    + "d.cont,\n"
                    + "p.depar, \n"
                    + "g.codgrupo,\n"
                    + "p.nomegrupo,\n"
                    + "p.subordem as familiaproduto\n"
                    + "from produtos p\n"
                    + "left join departamentos d on d.nome = p.depar\n"
                    + "left join grupos g on g.nomegrupo = p.nomegrupo\n"
                    + "union all\n"
                    + "select\n"
                    + "p.ordem as id,\n"
                    + "p.balanca,\n"
                    + "p.barra2 as ean,\n"
                    + "p.descricao,\n"
                    + "p.unidade,\n"
                    + "p.estoque,\n"
                    + "p.estoqueminimo,\n"
                    + "p.estoquemax,\n"
                    + "p.custo,\n"
                    + "p.vista as precovenda,\n"
                    + "p.vista2 as precovenda2,\n"
                    + "p.marckup,\n"
                    + "p.markup_fixo,\n"
                    + "p.cst,\n"
                    + "p.icms,\n"
                    + "p.icms2,\n"
                    + "p.reducao_icms,\n"
                    + "p.pis,\n"
                    + "p.cofins,\n"
                    + "p.ncm,\n"
                    + "p.dtcad,\n"
                    + "p.pesol,\n"
                    + "p.pesob,\n"
                    + "d.cont,\n"
                    + "p.depar, \n"
                    + "g.codgrupo,\n"
                    + "p.nomegrupo,\n"
                    + "p.subordem as familiaproduto\n"
                    + "from produtos p\n"
                    + "left join departamentos d on d.nome = p.depar\n"
                    + "left join grupos g on g.nomegrupo = p.nomegrupo\n"
                    + "where p.barra2 is not null\n"
                    + "and p.barra2 <> ''"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getString("balanca").contains("Y"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDataCadastro(rst.getDate("dtcad"));
                    imp.setPesoBruto(rst.getDouble("pesob"));
                    imp.setPesoLiquido(rst.getDouble("pesol"));
                    imp.setIdFamiliaProduto(rst.getString("familiaproduto"));
                    imp.setCodMercadologico1(rst.getString("cont"));
                    imp.setCodMercadologico2(rst.getString("codgrupo"));
                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("marckup"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemax"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cofins"));

                    if ((rst.getString("cst") != null)
                            && (!rst.getString("cst").trim().isEmpty())) {

                        if (rst.getString("cst").contains("500")) {
                            imp.setIcmsCst(60);
                        } else if (rst.getString("cst").contains("102")) {
                            imp.setIcmsCst(40);
                        } else {
                            imp.setIcmsCst(rst.getInt("cst"));
                        }
                    } else {
                        imp.setIcmsCst(Integer.parseInt(Utils.formataNumero(rst.getString("cst"))));
                    }

                    imp.setIcmsAliq(rst.getDouble("icms"));
                    imp.setIcmsReducao(rst.getDouble("reducao_icms"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.codigo,\n"
                    + "f.razao,\n"
                    + "f.fantasia,\n"
                    + "f.cnpj,\n"
                    + "f.ie,\n"
                    + "f.ende,\n"
                    + "f.end_num,\n"
                    + "f.bairro,\n"
                    + "f.codmunic,\n"
                    + "f.cidade,\n"
                    + "f.uf,\n"
                    + "f.cep,\n"
                    + "f.fone1,\n"
                    + "f.fone2,\n"
                    + "f.fax,\n"
                    + "f.celular,\n"
                    + "f.email,\n"
                    + "f.site,\n"
                    + "f.obs,\n"
                    + "f.contato,\n"
                    + "f.fonecont,\n"
                    + "f.celularcont,\n"
                    + "f.faxcont,\n"
                    + "f.emailcont,\n"
                    + "f.repre,\n"
                    + "f.fonerepre,\n"
                    + "f.prazo\n"
                    + "from fornecedores f"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("ende"));
                    imp.setNumero(rst.getString("end_num"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone1"));
                    imp.setObservacao(rst.getString("obs"));

                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
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
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "SITE",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("site").toLowerCase()
                        );
                    }
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
                    + "c.codigo,\n"
                    + "c.razao,\n"
                    + "c.nome,\n"
                    + "c.datacadastro,\n"
                    + "c.cgc,\n"
                    + "c.ie,\n"
                    + "c.endereco,\n"
                    + "c.end_num,\n"
                    + "c.bairro,\n"
                    + "c.cidade,\n"
                    + "c.uf,\n"
                    + "c.fone,\n"
                    + "c.fax,\n"
                    + "c.cep,\n"
                    + "c.email,\n"
                    + "c.endentrega,\n"
                    + "c.bairroentrega,\n"
                    + "c.fone2,\n"
                    + "c.celular,\n"
                    + "c.credito,\n"
                    + "c.bloq,\n"
                    + "c.pai,\n"
                    + "c.mae,\n"
                    + "c.conjuge,\n"
                    + "c.rg_conj,\n"
                    + "c.cpf_conj,\n"
                    + "c.renda,\n"
                    + "c.estcivil,\n"
                    + "c.profissao,\n"
                    + "c.empresa,\n"
                    + "c.empresafone,\n"
                    + "c.banco,\n"
                    + "c.conta,\n"
                    + "c.agencia\n"
                    + "from clientes c"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cgc"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("end_num"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email") != null ? rst.getString("email").toLowerCase() : null);
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaTelefone(rst.getString("empresafone"));
                    imp.setValorLimite(rst.getDouble("credito"));

                    if ((rst.getString("bloq") != null)
                            && (!rst.getString("bloq").trim().isEmpty())) {
                        if (rst.getString("bloq").contains("True")) {
                            imp.setBloqueado(true);
                            imp.setPermiteCheque(false);
                            imp.setPermiteCreditoRotativo(false);
                        } else {
                            imp.setBloqueado(false);
                            imp.setPermiteCheque(true);
                            imp.setPermiteCreditoRotativo(true);
                        }
                    } else {
                        imp.setBloqueado(false);
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                    }

                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("fone2"),
                                null,
                                null,
                                null
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.cont,\n"
                    + "c.codigo as idcliente,\n"
                    + "f.nome as nomecliente,\n"
                    + "f.data as emissao,\n"
                    + "f.venc as vencimento,\n"
                    + "f.valor,\n"
                    + "f.observacao,\n"
                    + "f.cupom\n"
                    + "from fiado f, clientes c\n"
                    + "where f.nome = c.nome\n"
                    + "and pago = 'N'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("cont"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
