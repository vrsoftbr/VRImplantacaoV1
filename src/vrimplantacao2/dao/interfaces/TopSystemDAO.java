/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class TopSystemDAO extends InterfaceDAO {

    public boolean mercadologico;
    public boolean mercadologicoNivel;
    public String id_loja;

    @Override
    public String getSistema() {
        if ((id_loja != null) && (!id_loja.trim().isEmpty())) {
            return "TopSystem" + id_loja;
        } else {
            return "TopSystem";
        }
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from cad_familia_produto order by codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "nivel1 merc1, "
                    + "descricao merc1_descricao\n"
                    + "from cad_produto_estru\n"
                    + "where nivel2 = 0\n"
                    + "and nivel3 = 0\n"
                    + "order by nivel1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("merc1_descricao"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "nivel1 merc1, \n"
                    + "coalesce(nivel2, 0) merc2,\n"
                    + "descricao merc2_descricao\n"
                    + "from cad_produto_estru\n"
                    + "where coalesce(nivel2, 0) > 0\n"
                    + "and nivel3 = 0\n"
                    + "order by nivel1, coalesce(nivel2, 0)"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("merc2"),
                                rst.getString("merc2_descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "nivel1 merc1, \n"
                    + "coalesce(nivel2, 0) merc2,\n"
                    + "coalesce(nivel3, 0) merc3,\n"
                    + "descricao merc3_descricao\n"
                    + "from cad_produto_estru\n"
                    + "where coalesce(nivel2, 0) > 0\n"
                    + "and coalesce(nivel3, 0) > 0\n"
                    + "order by nivel1, \n"
                    + "coalesce(nivel2, 0), \n"
                    + "coalesce(nivel3, 0)"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("merc3_descricao")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  distinct\n" +
                    "  s.codigo merc1,\n" +
                    "  s.descricao descmerc1,\n" +
                    "  f.codigo merc2,\n" +
                    "  f.descricao descmerc2,\n" +
                    "  1 merc3,\n" +
                    "  coalesce(f.descricao, s.descricao) descmerc3\n" +
                    "from\n" +
                    "  cad_produto p\n" +
                    "join cad_setor s on (p.grupo = s.codigo)\n" +
                    "join tab_familia_produto f on (p.familia = f.codigo)\n" +
                    "order by\n" +
                    "  s.codigo, f.codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.codigo codproduto,\n"
                    + "	p.codigo as codBarras,\n"
                    + "	p.descricao,\n"
                    + "	p.descricao_complementar,\n"
                    + "	case trim(coalesce(p.cean,'')) when '' then p.codigo else trim(coalesce(p.cean,'')) end codbarras,\n"
                    + "	case when p.pesavel in ('S', 'U') then 1 else 0 end pesavel,\n"
                    + "	case p.pesavel when 'S' then 'KG' when 'U' then 'UN' else  p.unidade end unidade,\n"
                    + "	p.peso_liquido_embalagem,\n"
                    + "	p.peso_bruto_embalagem,\n"
                    + "	p.grupo merc1,\n"
                    + "	p.familia merc2,\n"
                    + "	1 merc3,\n"
                    + "	p.nivel1, \n"
                    + "	p.nivel2, \n"
                    + "	p.nivel3, \n"
                    + "	p.class_fiscal_mercosul,\n"
                    + "	ven.preco_venda,\n"
                    + "	cus.preco_custo,\n"
                    + "	ven.margem_lucro,\n"
                    + "	p.validade,\n"
                    + "	p.familia,\n"
                    + "	p.inativo,\n"
                    + "	p.familiaproduto,\n"
                    + "	p.cest,\n"
                    + "	p.estoque_minimo,\n"
                    + "	est.qtde_atual,\n"
                    + "	p.tribcontrib,\n"
                    + "	p.contrmonaliqdif,\n"
                    + "	p.contrmonaliqund,\n"
                    + "	p.contrsubsttrib,\n"
                    + "	p.contraliqzero,\n"
                    + "	pis.cstcontrib_cod,\n"
                    + "	p.st_ecf,\n"
                    + "	trib.sit_trib,\n"
                    + "	trib.pct_red_calc_icms,\n"
                    + "	trib.aliq_icms\n"
                    + "from\n"
                    + "	cad_produto p\n"
                    + "	join cad_empresa emp on emp.codigo = " + getLojaOrigem() + "\n"
                    + "	inner join ger_tribcontribitem pis on pis.cod = p.tribcontrib\n"
                    + "	left join trib_estado trib on trib.cod_prod = p.codigo and trib.uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "	left join cad_estoque est on est.empresa = emp.codigo and est.codigo = p.codigo and est.tipo_estoque = 1\n"
                    + " left join cad_produto_pcusto cus on cus.cod_produto = p.codigo and cus.empresa = " + getLojaOrigem()
                    + " left join cad_produto_pvenda ven on ven.cod_produto = p.codigo and ven.empresa = " + getLojaOrigem() + "\n"
                    + "and ven.tabela_preco = 1"                    
            )) {
                while (rst.next()) {

                    int cst;
                    double aliquota = 0, reducao = 0;

                    String icms = rst.getString("st_ecf");
                    if (icms == null) {
                        icms = "";
                    }
                    icms = icms.trim().toUpperCase();

                    if (!"".equals(icms)) {
                        switch (icms) {
                            case "FF":
                                cst = 60;
                                break;
                            case "II":
                                cst = 40;
                                break;
                            case "NN":
                                cst = 90;
                                break;
                            case "7,0":
                                cst = 0;
                                aliquota = 7;
                                break;
                            case "07":
                                cst = 0;
                                aliquota = 7;
                                break;
                            case "8,4":
                                cst = 0;
                                aliquota = 8.4;
                                break;
                            case "12,0":
                                cst = 0;
                                aliquota = 12;
                                break;
                            case "12":
                                cst = 0;
                                aliquota = 12;
                                break;
                            case "18,0":
                                cst = 0;
                                aliquota = 18;
                                break;
                            case "18":
                                cst = 0;
                                aliquota = 18;
                                break;
                            case "25,0":
                                cst = 0;
                                aliquota = 25;
                                break;
                            case "25":
                                cst = 0;
                                aliquota = 25;
                                break;
                            case "0,0":
                                cst = 90;
                                break;
                            case "00":
                                cst = 90;
                                break;
                            default:
                                cst = 90;
                                break;
                        }
                    } else {
                        cst = 90;
                        aliquota = 0;
                        reducao = 0;
                    }

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codproduto"));
                    imp.setDescricaoCompleta(rst.getString("Descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setEan(rst.getString("codBarras"));
                    imp.seteBalanca(rst.getBoolean("pesavel"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido_embalagem"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto_embalagem"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setNcm(rst.getString("class_fiscal_mercosul"));
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rst.getDouble("Margem_Lucro"));
                    imp.setValidade(rst.getInt("Validade"));
                    imp.setSituacaoCadastro((rst.getInt("inativo") == 0 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));

                    if (mercadologico) {
                        imp.setCodMercadologico1(rst.getString("merc1"));
                        imp.setCodMercadologico2(rst.getString("merc2"));
                        imp.setCodMercadologico3(rst.getString("merc3"));
                    }

                    if (mercadologicoNivel) {
                        imp.setCodMercadologico1(rst.getString("nivel1"));
                        imp.setCodMercadologico2(rst.getString("nivel2"));
                        imp.setCodMercadologico3(rst.getString("nivel3"));
                    }

                    imp.setIdFamiliaProduto(rst.getString("familiaproduto"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoque(rst.getDouble("qtde_atual"));
                    imp.setPiscofinsCstDebito(rst.getInt("cstcontrib_cod"));
                    imp.setPiscofinsCstCredito(rst.getInt("cstcontrib_cod"));
                    if (!"0".equals(rst.getString("contraliqzero").trim())) {
                        imp.setPiscofinsNaturezaReceita(rst.getInt("contraliqzero"));
                    } else {
                        imp.setPiscofinsNaturezaReceita(rst.getInt("contrmonaliqdif"));
                    }
                    imp.setIcmsCst(cst);
                    imp.setIcmsAliq(aliquota);
                    imp.setIcmsReducao(reducao);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codigo_interno as codProduto, \n"
                    + "Codigoa as codBarras\n"
                    + "from cad_produto "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codProduto"));
                    imp.setEan(rst.getString("codBarras"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Nome, Razao_Social, Endereco, Numero,\n"
                    + "       Complemento, Bairro, Cep, CPF_CNPJ, Insc_Estadual, Insc_Municipal, \n"
                    + "       DDD, Prefixo, Telefone, Fax, E_Mail, Site,\n"
                    + "       Contato, municipio, uf, cod_oficial_mun, Celular, produtor_rural\n"
                    + "  from cad_fornecedor  \n"
                    + " order by Codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("Razao_Social"));
                    imp.setFantasia(rst.getString("Nome"));
                    imp.setCnpj_cpf(rst.getString("CPF_CNPJ"));
                    imp.setIe_rg(rst.getString("Insc_Estadual"));
                    imp.setInsc_municipal(rst.getString("Insc_Municipal"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("DDD") + rst.getString("Telefone"));
                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())
                            && (rst.getString("Fax").trim().length() > 5)) {
                        imp.addContato("1",
                                "FAX",
                                Utils.formataNumero(rst.getString("Fax").trim()),
                                "",
                                TipoContato.COMERCIAL,
                                "");
                    }
                    if ((rst.getString("E_Mail") != null)
                            && (!rst.getString("E_Mail").trim().isEmpty())) {
                        imp.addContato("2",
                                "EMAIL",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                rst.getString("E_mail").trim()
                        );
                    }
                    if ((rst.getString("Site") != null)
                            && (!rst.getString("Site").trim().isEmpty())) {
                        imp.addContato("3",
                                "HOME PAGE",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                rst.getString("Site").trim()
                        );
                    }
                    if ((rst.getString("Celular") != null)
                            && (!rst.getString("Celular").trim().isEmpty())
                            && (rst.getString("Celular").trim().length() > 5)) {
                        imp.addContato("4",
                                "CELULAR",
                                "",
                                Utils.formataNumero(rst.getString("Celular").trim()),
                                TipoContato.COMERCIAL,
                                "");
                    }
                    if((rst.getString("produtor_rural") != null) 
                            && ("S".equals(rst.getString("produtor_rural").trim()))) {
                        imp.setProdutorRural();
                    }
                    if ((rst.getString("Contato") != null)
                            && (!rst.getString("Contato").trim().isEmpty())) {
                        imp.addContato("5",
                                rst.getString("Contato"),
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                "");
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
                    "select \n"
                    + "     pf.CodForn id_fornecedor,\n"
                    + "     pf.Produto id_produto,     \n"
                    + "     pf.ProdForn codigoexterno,     \n"
                    + "     (select cv.FatorConv from cad_produto_fatconv cv where cv.codigo_produto = pf.Produto and cv.UnidMedida = 'CX' limit 1) qtdCX,     \n"
                    + "     (select cv.FatorConv from cad_produto_fatconv cv where cv.codigo_produto = pf.Produto and cv.UnidMedida = 'DP' limit 1) qtdDP,     \n"
                    + "     (select cv.FatorConv from cad_produto_fatconv cv where cv.codigo_produto = pf.Produto and cv.UnidMedida = 'PT' limit 1) qtdPT,     \n"
                    + "     (select cv.FatorConv from cad_produto_fatconv cv where cv.codigo_produto = pf.Produto and cv.UnidMedida in ('PC', 'PCT') limit 1) qtdPCT,\n"
                    + "     (select concat(cv.UnidMedida,'&&',cv.FatorConv) from cad_produto_fatconv cv where cv.codigo_produto = pf.Produto limit 1) qtdOutras\n"
                    + "from \n"
                    + "	cad_forn_prod pf\n"
                    + "order by Produto "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    if (rst.getString("qtdCX") != null) {
                        imp.setQtdEmbalagem(rst.getInt("qtdCX"));
                    } else if (rst.getString("qtdDP") != null) {
                        imp.setQtdEmbalagem(rst.getInt("qtdDP"));
                    } else if (rst.getString("qtdPT") != null) {
                        imp.setQtdEmbalagem(rst.getInt("qtdPT"));
                    } else if (rst.getString("qtdPCT") != null) {
                        imp.setQtdEmbalagem(rst.getInt("qtdPCT"));
                    } else if (rst.getString("qtdOutras") != null) {
                        String[] val = rst.getString("qtdOutras").split("&&");
                        imp.setQtdEmbalagem(Utils.stringToDouble(val[1]));
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        java.sql.Date dataCadastro = new Date(new java.util.Date().getTime());
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Nome_Reduzido, Razao_Social, Endereco, Numero, \n"
                    + "       Complemento, Bairro, CEP, CPF_CNPJ, Data_Nascimento, \n"
                    + "       Nome_Pai, Nome_Mae, Insc_Estadual_Rg, Insc_Municipal, \n"
                    + "       DDD, Telefone, Ramal, Fax, E_Mail, Limite_Credito, "
                    + "       Contato, Data_Cadastramento, Data_Nascimento, observacao, \n"
                    + "       Bloqueado, Celular, municipio, uf, cod_oficial_mun, Ativo, DDD_Celular\n"
                    + "  from cad_cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("Razao_Social"));
                    imp.setFantasia(rst.getString("Nome_Reduzido"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCnpj(rst.getString("CPF_CNPJ"));
                    imp.setInscricaoestadual(rst.getString("Insc_Municipal"));
                    imp.setNomePai(rst.getString("Nome_Pai"));
                    imp.setNomeMae(rst.getString("Nome_Mae"));
                    imp.setTelefone(rst.getString("DDD") + rst.getString("Telefone"));
                    imp.setCelular(rst.getString("DDD_Celular") + rst.getString("Celular"));
                    imp.setEmail(rst.getString("E_Mail"));
                    imp.setValorLimite(rst.getDouble("Limite_Credito"));
                    imp.setBloqueado(!"N".equals(rst.getString("Bloqueado")));
                    imp.setDataNascimento(rst.getDate("Data_Nascimento"));

                    if (imp.isBloqueado()) {
                        imp.setPermiteCreditoRotativo(false);
                        imp.setPermiteCheque(false);
                    } else {
                        imp.setPermiteCreditoRotativo(true);
                        imp.setPermiteCheque(true);
                    }

                    imp.setAtivo("S".equals(rst.getString("Ativo")));
                    imp.setObservacao(rst.getString("observacao"));
                    if ((rst.getString("Data_Cadastramento") != null)
                            && (!rst.getString("Data_Cadastramento").trim().isEmpty())) {
                        imp.setDataCadastro(rst.getDate("Data_Cadastramento"));
                    } else {
                        imp.setDataCadastro(dataCadastro);
                    }
                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())
                            && (rst.getString("Fax").trim().length() > 5)) {
                        imp.addContato("1",
                                "FAX",
                                Utils.formataNumero(rst.getString("Fax")),
                                "",
                                "");
                    }
                    if ((rst.getString("Contato") != null)
                            && (!rst.getString("Contato").trim().isEmpty())) {
                        imp.addContato("2",
                                "CONTATO",
                                "",
                                "",
                                rst.getString("Contato").trim()
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
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select empresa, tipo_doc, serie, numero_doc, sequencia, cliente, data_emissao,\n"
                    + "       data_vencimento, saldo, observacao\n"
                    + "  from fiscal.mov_car\n"
                    + " where empresa = " + getLojaOrigem() + " "
                    + " and data_liquidacao is null;"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP vo = new CreditoRotativoIMP();
                    vo.setId(
                            rst.getString("empresa") + "-"
                            + rst.getString("tipo_doc") + "-"
                            + rst.getString("serie") + "-"
                            + rst.getString("numero_doc") + "-"
                            + rst.getString("sequencia")
                    );
                    vo.setDataEmissao(rst.getDate("data_emissao"));
                    vo.setDataVencimento(rst.getDate("data_vencimento"));
                    vo.setNumeroCupom(rst.getString("numero_doc"));
                    vo.setIdCliente(rst.getString("cliente"));
                    vo.setValor(rst.getDouble("saldo"));

                    if ((rst.getString("observacao") != null)
                            && (!rst.getString("observacao").trim().isEmpty())) {
                        vo.setObservacao(rst.getString("observacao").trim());
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "ch.Empresa, ch.Banco, ch.Codigo_Oficial, ch.Codigo_Agencia, ch.CC_Numero,\n"
                    + "ch.CC_Dv, ch.Numero_Cheque, ch.Numero_Documentov, ch.Valor_Cheque,\n"
                    + "ch.Data_Entrada, ch.Observacao, ch.Emitente, ch.Data_Devolucao1, ch.Data_Devolucao2,\n"
                    + "c.Razao_Social, c.CPF_CNPJ, c.Insc_Municipal, c.Telefone, ch.Data_Original_PDeposito\n"
                    + "from cad_cheque3 ch\n"
                    + "left join cad_cliente c on c.Codigo = ch.Codigo_Cliente\n"
                    + "where ch.Codigo_Cliente = 1 \n"
                    + "and ch.Data_Baixa1Deposito is null\n"
                    + "and ch.Data_Baixa2Deposito is null\n"
                    + "and ch.Data_Baixa_Uso is null"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(
                            rst.getString("Empresa")
                            + " - " + rst.getString("Banco")
                            + " - " + rst.getString("Codigo_Oficial")
                            + " - " + rst.getString("Codigo_Agencia")
                            + " - " + rst.getString("CC_Numero")
                            + " - " + rst.getString("CC_Dv")
                            + " - " + rst.getString("Numero_Cheque")
                    );

                    imp.setDate(rst.getDate("Data_Entrada"));
                    imp.setDataDeposito(rst.getDate("Data_Original_PDeposito"));
                    imp.setNome(rst.getString("Emitente"));
                    imp.setValor(rst.getDouble("Valor_Cheque"));
                    imp.setAgencia(rst.getString("Codigo_Agencia"));
                    imp.setConta(rst.getString("CC_Numero"));
                    imp.setNumeroCheque(rst.getString("Numero_Cheque"));
                    imp.setObservacao(rst.getString("Observacao"));

                    if ((rst.getString("Data_Devolucao1") != null)
                            && (!rst.getString("Data_Devolucao1").trim().isEmpty())
                            || (rst.getString("Data_Devolucao2") != null)
                            && (!rst.getString("Data_Devolucao2").trim().isEmpty())) {
                        imp.setAlinea(11);
                    } else {
                        imp.setAlinea(0);
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(java.util.Date dataTermino) throws Exception {
        if (dataTermino == null) {
            dataTermino = new java.util.Date();
        }
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "produto, "
                    + "dt_inicio, "
                    + "dt_final, "
                    + "preco_venda\n"
                    + "from cad_produto_prom "
                    + "where filial = " + getLojaOrigem()
                    + " and dt_final > " + SQLUtils.stringSQL(new SimpleDateFormat("yyyy-MM-dd").format(dataTermino))
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setDataInicio(rst.getDate("dt_inicio"));
                    imp.setDataFim(rst.getDate("dt_final"));
                    imp.setPrecoOferta(rst.getDouble("preco_venda"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select empresa, Razao_Social  from cad_filial order by empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("empresa"), rst.getString("razao_social")));
                }
            }
        }
        return result;
    }
}
