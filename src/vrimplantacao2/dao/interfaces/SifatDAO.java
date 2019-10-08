package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemAnteriorDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemDAO;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
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
public class SifatDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Sifat";
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + " ID_FAMILIA as id,\n"
                    + "	DESCRICAO as descricao\n"
                    + "from ce27"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "sselect \n"
                    + "	ID_GRUPO as merc1, \n"
                    + "	NOME_GRUPO as merc1_desc,\n"
                    + "	ID_SUBGRUPO as merc2, \n"
                    + "	NOME_SUBGRUPO as merc2_desc\n"
                    + "from ce07\n"
                    + "order by ID_GRUPO, ID_SUBGRUPO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
                    "select \n"
                    + "	pro.ID_PRODUTO as id,\n"
                    + "	pro.CODIGO as ean,\n"
                    + "	pro.PESADO as balanca,\n"
                    + "	pro.FRACIONA as fracionado,\n"
                    + "	pro.validade as validade,\n"
                    + "	pro.DESCRICAO as descricaocompleta,\n"
                    + "	pro.ABREVIACAO as descricaoreduzida,\n"
                    + "	pro.UNIDADE as tipoembalagem,\n"
                    + "	pro.PESO as peso,\n"
                    + "	pro.FAMILIA as idfamilia,\n"
                    + "	pro.GRUPO as merc1,\n"
                    + "	pro.SUBGRUPO as merc2,\n"
                    + "	pro.NCM as ncm,\n"
                    + "	pro.CEST as cest,\n"
                    + "	pro.DT_CADASTRO as datacadastro,\n"
                    + "	pre.ATIVO as situacaocadastro,\n"
                    + "	pre.PRECO_CUSTO as custo,\n"
                    + "	pre.PRECO_VENDA as precovenda,\n"
                    + "	pre.ESTOQUE as estoque,\n"
                    + "	pre.ESTOQUE_MIN as estoqueminimo,\n"
                    + "	pre.ESTOQUE_MAX as estoquemaximo,\n"
                    + "	pre.DEPTO_PIS,\n"
                    + "	pis.CST as cstpis,\n"
                    + "	pis.nat_operacao as naturezaceita,\n"
                    + "	pis.DESCRICAO as descricaopis,\n"
                    + "	pre.DEPTO_COFINS,\n"
                    + "	cof.CST as cstcofins,\n"
                    + "	cof.DESCRICAO as descricaopis,\n"
                    + "	pre.DEPTO_ICMS,\n"
                    + "	icm.CST_ICMS as csticms,\n"
                    + "	icm.AL_ICMS as aliqicms,\n"
                    + "	icm.RED_BC_ICMS as reduicms,\n"
                    + "	icm.DESCRICAO as descricaoicms\n"
                    + "from ce01 pro\n"
                    + "left join ce01e pre on pre.ID_PRODUTO = pro.ID_PRODUTO and pre.LOJA = " + getLojaOrigem() + "\n"
                    + "left join ce61 pis on pis.ID = pre.DEPTO_PIS\n"
                    + "left join ce61 cof on cof.ID = pre.DEPTO_COFINS\n"
                    + "left join ce01t icm on icm.DEPTO_ICMS = pre.DEPTO_ICMS and icm.OPERACAO = 1\n"
                    + "order by pro.ID_PRODUTO"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("fracionado") == 1);
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rst.getString("idfamilia"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPiscofinsCstDebito(rst.getInt("cstpis"));
                    imp.setPiscofinsCstCredito(rst.getInt("cstcofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("naturezareceita"));
                    imp.setIcmsCst(rst.getInt("csticms"));
                    imp.setIcmsAliq(rst.getDouble("aliqicms"));
                    imp.setIcmsReducao(rst.getDouble("reduicms"));
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
                    "select\n"
                    + "	f.CODIGO as id,\n"
                    + "	f.ATIVO as situacaocadastro,\n"
                    + "	f.NOME as razao,\n"
                    + "	f.NOME_FANTASIA as fantasia,\n"
                    + "	f.INSC_FEDERAL as cnpj,\n"
                    + "	f.INSC_ESTADUAL as ie_rg,\n"
                    + "	f.ENDERECO as endereco,\n"
                    + "	f.END_NUMERO as numero,\n"
                    + "	f.END_COMPLEMENTO as complemento,\n"
                    + "	f.BAIRRO as bairro,\n"
                    + "	f.CIDADE as municipio,\n"
                    + "	f.UF as uf,\n"
                    + "	f.ID_MUNICIPIO as municipioibge,\n"
                    + "	f.CEP as cep,\n"
                    + "	f.TELEFONE as telefone,\n"
                    + "	f.FAX as fax,\n"
                    + "	f.CELULAR as celular,\n"
                    + "	f.EMAIL as email,\n"
                    + "	f.SITE as site,\n"
                    + "	f.DIA_VENCIMENTO as diavencimento,\n"
                    + "	f.OBSERVACAO as observacao,\n"
                    + "	f.PRAZO_ENTREGA as prazoentrega,\n"
                    + "	f.PRAZO_PGTO as prazopagto\n"
                    + "from cd02 f\n"
                    + "where f.E_FORNECEDOR = 1\n"
                    + "order by f.CODIGO"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setAtivo("1".equals(rst.getString("situacaocadastro")));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemnto"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "CELULAR",
                                null,
                                Utils.formataNumero(rst.getString("celular")),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email")
                        );
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "SITE",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("site")
                        );
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
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.CODIGO as id,\n"
                    + "	c.ATIVO as situacaocadastro,\n"
                    + "	c.NOME as razao,\n"
                    + "	c.NOME_FANTASIA as fantasia,\n"
                    + "	c.INSC_FEDERAL as cnpj,\n"
                    + "	c.INSC_ESTADUAL as ie_rg,\n"
                    + "	c.ENDERECO as endereco,\n"
                    + "	c.END_NUMERO as numero,\n"
                    + "	c.END_COMPLEMENTO as complemento,\n"
                    + "	c.BAIRRO as bairro,\n"
                    + "	c.CIDADE as municipio,\n"
                    + "	c.UF as uf,\n"
                    + "	c.ID_MUNICIPIO as municipioibge,\n"
                    + "	c.CEP as cep,\n"
                    + "	c.TELEFONE as telefone,\n"
                    + "	c.FAX as fax,\n"
                    + "	c.CELULAR as celular,\n"
                    + "	c.EMAIL as email,\n"
                    + "	c.SITE as site,\n"
                    + "	c.DIA_VENCIMENTO as diavencimento,\n"
                    + "	c.OBSERVACAO as observacao,\n"
                    + "	c.PRAZO_ENTREGA as prazoentrega,\n"
                    + "	c.PRAZO_PGTO as prazopagto,\n"
                    + "	c.LIMITE_CREDITO as valorlimite,\n"
                    + "	c.STATUS_CREDITO as statuscredito,\n"
                    + "	c.LIMITE_CHEQUE as limitecheque,\n"
                    + "	c.DT_NASCIMENTO as datanascimento,\n"
                    + "	c.FILIACAO as filiacao,\n"
                    + "	c.SEXO as sexo,\n"
                    + "	c.ESTADO_CIVIL as estadocivil,\n"
                    + "	c.NOME_CONJUGE as nomeconjuge,\n"
                    + "	c.TRABALHO as empresa,\n"
                    + "	c.DT_ADMISSAO as dataadmissao,\n"
                    + "	c.RENDA_MENSAL as salario,\n"
                    + "	c.PROFISSAO as cargo,\n"
                    + "	c.CPF_CONJUGE as cpfconjuge\n"
                    + "from cd02 c\n"
                    + "where c.E_CLIENTE  = 1\n"
                    + "order by c.CODIGO"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setAtivo(rst.getInt("situacaocadastro") == 1);
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setCargo(rst.getString("cargo"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax")),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "SITE",
                                null,
                                null,
                                Utils.formataNumero(rst.getString("site"))
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
                    "select id, cliente, loja, caixa,\n"
                    + "venda, data emissao, faturado, valor,\n"
                    + "ADDDATE(data, interval 30 day) vencimento\n"
                    + "from bdsifat.cf11\n"
                    + "where DC = 'D'\n"
                    + "and historico like '%VENDA%'\n"
                    + "and loja = 1 "
                    + "order by data"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("cliente"));
                    imp.setNumeroCupom(rst.getString("venda"));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public void importarPagamentoRotativo() throws Exception {
        Conexao.begin();
        try {
            Map<String, Double> pagamentos = new HashMap<>();

            ProgressBar.setStatus("Importando pagamentos...");
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select distinct a.cliente,\n"
                        + "(select sum(coalesce(valor, 0)) from bdsifat.cf11 where dc = 'C' and cliente = a.cliente) - "
                        + "(select sum(coalesce(valor, 0)) from bdsifat.cf11 where historico like '%ESTORNO%' and cliente = a.cliente)"
                        + " valor\n"
                        + "from bdsifat.cf11 a\n"
                        + "where loja = 1"
                )) {
                    while (rst.next()) {
                        double valor = rst.getDouble("valor");

                        if (valor < 0) {
                            valor *= -1;
                        }

                        pagamentos.put(rst.getString("cliente"), MathUtils.trunc(valor, 2));
                    }
                }
            }

            for (String id : pagamentos.keySet()) {
                double valorPagoTotal = pagamentos.get(id);
                System.out.println("ID: " + id + "  VALOR: " + valorPagoTotal);
            }

            CreditoRotativoDAO rotDao = new CreditoRotativoDAO();
            CreditoRotativoItemDAO dao = new CreditoRotativoItemDAO();
            CreditoRotativoItemAnteriorDAO antDao = new CreditoRotativoItemAnteriorDAO();
            MultiMap<String, CreditoRotativoItemAnteriorVO> baixasAnteriores = antDao.getBaixasAnteriores(null, null);

            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	 ant.sistema,\n"
                        + "    ant.loja,\n"
                        + "    ant.id_cliente,\n"
                        + "    ant.id,\n"
                        + "    ant.codigoatual,\n"
                        + "    r.id_loja,\n"
                        + "    r.valor,\n"
                        + "    r.datavencimento\n"
                        + "from \n"
                        + "	implantacao.codant_recebercreditorotativo ant\n"
                        + "    join recebercreditorotativo r on\n"
                        + "    	ant.codigoatual = r.id\n"
                        + " where ant.loja = '" + getLojaOrigem() + "' "
                        + " and ant.sistema = '" + getSistema() + "'"
                        + " order by\n"
                        + "	ant.id_cliente, r.datavencimento"
                )) {
                    int cont1 = 0, cont2 = 0;
                    while (rst.next()) {
                        String sistema = rst.getString("sistema");
                        String loja = rst.getString("loja");
                        String idCliente = rst.getString("id_cliente");
                        String idRotativo = rst.getString("id");
                        int codigoAtual = rst.getInt("codigoatual");
                        int id_loja = rst.getInt("id_loja");
                        double valor = rst.getDouble("valor");
                        Date vencimento = rst.getDate("datavencimento");

                        if (!baixasAnteriores.containsKey(sistema, loja, idRotativo, idRotativo)) {
                            if (pagamentos.containsKey(idCliente)) {
                                double valorPagoTotal = pagamentos.get(idCliente);
                                if (valorPagoTotal > 0) {
                                    System.out.println("CLIENTE: " + idCliente + " VAL_PAGO: " + valorPagoTotal);
                                    double valorParc;
                                    if (valorPagoTotal >= valor) {
                                        valorPagoTotal -= valor;
                                        valorParc = valor;
                                    } else {
                                        valorParc = valorPagoTotal;
                                        valorPagoTotal = 0;
                                    }

                                    CreditoRotativoItemVO pag = new CreditoRotativoItemVO();
                                    pag.setId_receberCreditoRotativo(codigoAtual);
                                    pag.setValor(valorParc);
                                    pag.setValorTotal(valorParc);
                                    pag.setDatabaixa(vencimento);
                                    pag.setDataPagamento(vencimento);
                                    pag.setObservacao("IMPORTADO VR");
                                    pag.setId_loja(id_loja);

                                    dao.gravarRotativoItem(pag);

                                    CreditoRotativoItemAnteriorVO ant = new CreditoRotativoItemAnteriorVO();
                                    ant.setSistema(sistema);
                                    ant.setLoja(loja);
                                    ant.setIdCreditoRotativo(idRotativo);
                                    ant.setId(idRotativo);
                                    ant.setCodigoAtual(pag.getId());
                                    ant.setDataPagamento(vencimento);
                                    ant.setValor(pag.getValor());

                                    antDao.gravarRotativoItemAnterior(ant);

                                    rotDao.verificarBaixado(codigoAtual);

                                    pagamentos.put(idCliente, valorPagoTotal);
                                    baixasAnteriores.put(ant,
                                            ant.getSistema(),
                                            ant.getLoja(),
                                            ant.getIdCreditoRotativo(),
                                            ant.getId()
                                    );
                                }
                            }
                        }
                        cont1++;
                        cont2++;

                        if (cont1 == 1000) {
                            cont1 = 0;
                            ProgressBar.setStatus("Importando pagamentos..." + cont2);
                        }
                    }
                }
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
}
