package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class FlatanDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Flatan";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "    coalesce(esci01.e01grpo, '1') codm1,\n"
                    + "    coalesce(esci05.es05dgrp, 'SEM DESCRICAO') descm1,\n"
                    + "    coalesce(esci01.e01sgrp, '1') codm2,\n"
                    + "    coalesce(esci02.es02dsgp, 'SEM DESCRICAO') descm2,\n"
                    + "    1 codm3, \n"
                    + "    coalesce(esci02.es02dsgp, 'SEM DESCRICAO') descm3\n"
                    + "   FROM es.esci01\n"
                    + "     LEFT JOIN es.esci05 ON esci01.e01empr = esci05.es05empr AND esci01.e01grpo::text = esci05.es05grpo::text\n"
                    + "     LEFT JOIN es.esci02 ON esci05.es05id = esci02.es02id AND esci01.e01sgrp::text = esci02.es02sgrp::text\n"
                    + "ORDER BY codm1, codm2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP merc = new MercadologicoIMP();
                    merc.setImportSistema(getSistema());
                    merc.setImportLoja(getLojaOrigem());
                    merc.setMerc1ID(rst.getString("codm1"));
                    merc.setMerc1Descricao(rst.getString("descm1"));
                    merc.setMerc2ID(rst.getString("codm2"));
                    merc.setMerc2Descricao(rst.getString("descm2"));
                    merc.setMerc3ID(rst.getString("codm3"));
                    merc.setMerc3Descricao(rst.getString("descm3"));
                    vResult.add(merc);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {

            EstadoVO ufP = Parametros.get().getUfPadrao();
            String uf = ufP.getSigla();

            try (ResultSet rst = stm.executeQuery(
                    "SELECT esci01.e01id,\n"
                    + "    esci01.e01empr,\n"
                    + "    esci01.e01codp as codigo,\n"
                    + "    coalesce(esci01.e01cbar, '-2') as codigobarras, \n"
                    + "    esci01.e01tpro,\n"
                    + "    coalesce(esci01.e01nome, 'SEM DESCRICAO') as descricaocompleta,\n"
                    + "    coalesce(esci01.e01dred, 'SEM DESCRICAO') as descricaoreduzida,\n"
                    + "    coalesce(esci01.e01cfis, '04029900') as ncm,\n"
                    + "    case esci01.e01unid when 'KG' then 1 else 0 end e_balanca,\n"
                    + "    esci01.e01unid as tipoembalagem,\n"
                    + "    esci01.e01estq as estoque,\n"
                    + "    esci01.e01qmin as estoqueminimo,\n"
                    + "    esci01_fabr.e01cpf,\n"
                    + "    esci01.e01grpo as mercadologico1,\n"
                    + "    esci05.es05dgrp,\n"
                    + "    esci01.e01sgrp as mercadologico2,\n"
                    + "    esci02.es02dsgp,\n"
                    + "    esci01.e01apli,\n"
                    + "    esci01.e01ccnt,\n"
                    + "    esci01.e01crep as custocomimposto,\n"
                    + "    esci01.e01cref as custo,\n"
                    + "    esci01.e01vend,\n"
                    + "    esci01.e01dias as validade,\n"
                    + "    nz(p1.e01ppub) AS precovenda,\n"
                    + "    nz(p2.e01ppub) AS e01ppub2,\n"
                    + "    nz(p3.e01ppub) AS e01ppub3,\n"
                    + "    esci01.e01ultc as datacadastro,\n"
                    + "    esci01.e01uven,\n"
                    + "    esci01.e01peso as pesobruto,\n"
                    + "    esci01.e01pliq as pesoliquido,\n"
                    + "    esci01.e01klus,\n"
                    + "    esci01.e01corre,\n"
                    + "    esci01.e01prati,\n"
                    + "    esci01.e01nrvao,\n"
                    + "    esci17.e17abct AS cst_icms,\n"
                    + "    esci17.e17csosncf AS csosn_cons,\n"
                    + "    esci17.e17csosnnr AS csosn_nor,\n"
                    + "    esci17.e17icm AS aliq_icms,\n"
                    + "    esci17.e17bicm AS red_icms,\n"
                    + "    esci17.e17subtr AS iva_st,\n"
                    + "    esci17.e17est,\n"
                    + "    esci01.e01stpis as cst_pis,\n"
                    + "    esci01.e01stcof as cst_cofins        \n"
                    + "   FROM es.esci01\n"
                    + "     JOIN mn.esci00 ON esci01.e01empr = esci00.e00empr\n"
                    + "     LEFT JOIN es.esci01_prec p1 ON esci01.e01id = p1.e01preid AND p1.e01item = 1\n"
                    + "     LEFT JOIN es.esci01_prec p2 ON esci01.e01id = p2.e01preid AND p2.e01item = 2\n"
                    + "     LEFT JOIN es.esci01_prec p3 ON esci01.e01id = p3.e01preid AND p3.e01item = 3\n"
                    + "     LEFT JOIN es.esci01_fabr ON esci01.e01id = esci01_fabr.e01fabid\n"
                    + "     LEFT JOIN es.esci05 ON esci01.e01empr = esci05.es05empr AND esci01.e01grpo::text = esci05.es05grpo::text\n"
                    + "     LEFT JOIN es.esci02 ON esci05.es05id = esci02.es02id AND esci01.e01sgrp::text = esci02.es02sgrp::text\n"
                    + "     LEFT JOIN es.esci17 ON esci01.e01id = esci17.e17id AND esci00.e00est::text = esci17.e17est::text\n"
                    + "    where esci01.e01empr = " + getLojaOrigem() + " \n"
                    + "    order by esci01.e01nome asc"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(Utils.formataNumero(rst.getString("codigo")));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setQtdEmbalagem(1);
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());

                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));

                    imp.setSituacaoCadastro(SituacaoCadastro.getById(1));
                    imp.setDataCadastro(rst.getDate("datacadastro"));

                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3("1");

                    imp.setNcm(rst.getString("ncm"));

                    imp.setPiscofinsCstDebito(rst.getInt("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getInt("cst_cofins"));

                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custocomimposto"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));

                    imp.setIcmsCst(rst.getInt("cst_icms"));
                    imp.setIcmsAliq(rst.getDouble("aliq_icms"));
                    imp.setIcmsReducao(rst.getDouble("red_icms"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT es03id, es03empr, es03codf as codigo, \n"
                    + "	es03nome as razaosocial, es03ncgc as cnpj, \n"
                    + "	es03dcgc as dig_cnpj, es03insc as inscricaoestadual, \n"
                    + "	es03ende as endereco, es03bair as bairro, es03cida as cidade, \n"
                    + "	es03est as estado, es03cep as cep, es03fone as telefone, \n"
                    + "	es03fax as fax, es03nfan as nomefantasia, es03cidc as cod_cidade\n"
                    + "  FROM es.esci03\n"
                    + "WHERE es03empr = " + getLojaOrigem() + " "
                    + " ORDER BY es03codf"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setCnpj_cpf(rst.getString("cnpj") + rst.getString("dig_cnpj"));
                    imp.setObservacao("IMPORTADO VR");
                    imp.setAtivo(true);

                    if ((rst.getString("fax") != null) && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("fax"),
                                "",
                                TipoContato.COMERCIAL,
                                ""
                        );
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select a.e01codp as produto, b.e01codf as fornecedor\n"
                    + "  FROM es.esci01 a\n"
                    + " INNER JOIN es.esci01_forn b ON b.e01forid = a.e01id"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setCodigoExterno(rst.getString("fornecedor") + rst.getString("produto"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT cr01id, cr01empr, cr01codc as codigo, cr01nome as nome, cr01nfan as fantasia,\n"
                    + "       cr01ende as endereco, cr01bair as bairro, cr01cidc as cod_cidade,\n"
                    + "       cr01cida as cidade, cr01est as estado, cr01fone as fone, cr01fon2 as fone2,\n"
                    + "       cr01fon3 as fone3, cr01fax as fax, cr01endc as enderecocob, cr01enen,\n"
                    + "       cr01lmte as limite, cr01cgc as cnpj, cr01dacc, cr01insc as inscricaoestadual,\n"
                    + "       cr01cep as cep, cr01dnas as datanascimento, cr01banc as banco,\n"
                    + "       cr01npai as nomepai, cr01nmae as nomemae, cr01emai as email,\n"
                    + "       cr01dtad as datacadastro\n"
                    + "  FROM fa.crci01\n"
                    + " WHERE cr01empr = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    if ((rst.getString("fone") != null) && (!rst.getString("fone").trim().isEmpty())) {
                        imp.setTelefone(rst.getString("fone"));
                    }
                    if ((rst.getString("fone2") != null) && (!rst.getString("fone2").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FONE 2",
                                rst.getString("fone2"),
                                "",
                                ""
                        );
                    }
                    if ((rst.getString("fone3") != null) && (!rst.getString("fone3").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "FONE 3",
                                rst.getString("fone3"),
                                "",
                                ""
                        );
                    }
                    if ((rst.getString("fax") != null) && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "FAX",
                                rst.getString("fax"),
                                "",
                                ""
                        );
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cr03dupl, cr03emis, cr03venc, cr03codc, cr03valr total, (cr03valr - cr03vpgt) cr03valr \n"
                    + "from vw.liberacao_duplicatas \n"
            )) {
                int contador = 1;
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(String.valueOf(contador));
                    imp.setNumeroCupom(rst.getString("cr03dupl"));
                    imp.setIdCliente(rst.getString("cr03codc"));
                    imp.setValor(rst.getDouble("cr03valr"));
                    imp.setDataEmissao(rst.getDate("cr03emis"));
                    imp.setDataVencimento(rst.getDate("cr03venc"));
                    imp.setObservacao("IMPORTADO VR");
                    vResult.add(imp);
                    contador++;
                }
            }
        }
        return vResult;
    }
}
