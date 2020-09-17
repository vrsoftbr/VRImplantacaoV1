package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
//import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class CupermaxDAO extends InterfaceDAO {

    public static final String HOST = "localhost";
    public static final String PORT = "1521";
    public static final String USER = "SYSTEM";
    public static final String DATABASE = "CUPERMAX";
    public static final String PASSWORD = "cup204468";

    public String v_codEmpresaConv;

    @Override
    public String getSistema() {
        return "Cupermax";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT ID, RAZAO_SOCIAL AS descricao FROM CUPERMAX.EMPRESA ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    /*
     @Override
     public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
     List<FamiliaProdutoIMP> result = new ArrayList<>();

     try (Statement stm = ConexaoOracle.createStatement()) {
     try (ResultSet rst = stm.executeQuery(
     "select\n"
     + "  p.codigorea id,\n"
     + "  p2.nome1 descricao\n"
     + "from\n"
     + "(select p.codigorea from produtos p where p.codigorea > 0 group by codigorea) p\n"
     + "join produtos p2 on p2.codigo = p.codigorea\n"
     + "order by\n"
     + "  p.codigorea"
     )) {
     while (rst.next()) {
     FamiliaProdutoIMP familiaVO = new FamiliaProdutoIMP();

     familiaVO.setImportSistema(getSistema());
     familiaVO.setImportLoja(getLojaOrigem());
     familiaVO.setImportId(rst.getString("id"));
     familiaVO.setDescricao(rst.getString("descricao"));

     result.add(familiaVO);
     }
     }
     }

     return result;
     }
     */
    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {

        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "WITH merc AS (\n"
                    + "SELECT\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,1,3) depto,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,5,3) secao,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,9,3) grupo,\n"
                    + "	DESCRICAO descritivo\n"
                    + "FROM\n"
                    + "	CUPERMAX.nivel_mercadologico\n"
                    + "	)\n"
                    + "	SELECT depto,DESCRITIVO \n"
                    + "FROM MERC \n"
                    + "	WHERE secao IS NULL AND grupo IS NULL \n"
                    + "ORDER BY\n"
                    + "	1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP(rst.getString("depto"), rst.getString("descritivo"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "	WITH merc AS (\n"
                    + "SELECT\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,1,3) depto,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,5,3) secao,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,9,3) grupo,\n"
                    + "	DESCRICAO descritivo\n"
                    + "FROM\n"
                    + "	CUPERMAX.nivel_mercadologico\n"
                    + "	)\n"
                    + "	SELECT secao,DESCRITIVO \n"
                    + "FROM MERC \n"
                    + "	WHERE secao IS NOT NULL AND grupo IS NULL \n"
                    + "ORDER BY\n"
                    + "	1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP pai = merc.get(rst.getString("depto"));
                    pai.addFilho(rst.getString("secao"), rst.getString("descritivo"));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "	WITH merc AS (\n"
                    + "SELECT\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,1,3) depto,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,5,3) secao,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,9,3) grupo,\n"
                    + "	DESCRICAO descritivo\n"
                    + "FROM\n"
                    + "	CUPERMAX.nivel_mercadologico\n"
                    + "	)\n"
                    + "	SELECT grupo,DESCRITIVO \n"
                    + "FROM MERC \n"
                    + "	WHERE grupo IS NOT NULL \n"
                    + "ORDER BY\n"
                    + "	1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP pai = merc.get(rst.getString("depto"));
                    pai = pai.getNiveis().get(rst.getString("secao"));
                    pai.addFilho(rst.getString("grupo"), rst.getString("descritivo"));
                }
            }
        }

        return new ArrayList<>(merc.values());
    }

    /*@Override
     public List<MercadologicoIMP> getMercadologicos() throws Exception {
     List<MercadologicoIMP> result = new ArrayList<>();

     try (Statement stm = ConexaoOracle.createStatement()) {
     try (ResultSet rst = stm.executeQuery(
     "select\n"
     + "  s.codigo merc1,\n"
     + "  s.nome merc1_desc,\n"
     + "  coalesce(g.codigo,1) merc2,\n"
     + "  coalesce(g.nome, s.nome) merc2_desc,\n"
     + "  coalesce(sg.CODIGO,1) merc3,\n"
     + "  coalesce(sg.nome, coalesce(g.nome, s.nome)) merc3_desc\n"
     + "from \n"
     + "  SETOR s\n"
     + "  left join GRUPO g on g.SETOR = s.CODIGO\n"
     + "  left join SUBGRUPO sg on g.CODIGO = sg.GRUPO and s.CODIGO = sg.SETOR\n"
     + "order by\n"
     + "  s.codigo"
     )) {
     while (rst.next()) {
     MercadologicoIMP merc = new MercadologicoIMP();
     merc.setImportSistema(getSistema());
     merc.setImportLoja(getLojaOrigem());
     merc.setMerc1ID(Utils.acertarTexto(rst.getString("merc1")));
     merc.setMerc1Descricao(Utils.acertarTexto(rst.getString("merc1_desc")));
     merc.setMerc2ID(Utils.acertarTexto(rst.getString("merc2")));
     merc.setMerc2Descricao(Utils.acertarTexto(rst.getString("merc2_desc")));
     merc.setMerc3ID(Utils.acertarTexto(rst.getString("merc3")));
     merc.setMerc3Descricao(Utils.acertarTexto(rst.getString("merc3_desc")));

     result.add(merc);
     }
     }
     }

     return result;
     }
     */
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vProduto = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  p.codigo id,\n"
                    + "  ean.codbarra ean,\n"
                    + "  ean.quantidade qtdEmbalagem,\n"
                    + "  case p.pesvar when 'S' then 1 else 0 end as e_balanca,\n"
                    + "  case when p.pesvar = 'S' and p.tipo = 'P' then 'KG' else 'UN' end as tipoembalagem,\n"
                    + "  val.validade,  \n"
                    + "  p.nome descricaocompleta,\n"
                    + "  p.nome2 descricaoreduzida,\n"
                    + "  p.nome1 descricaogondola,\n"
                    + "  case p.inativo when 'S' then 0 else 1 end as id_situacaocadastro,\n"
                    + "  p.flaginc datacadastro,\n"
                    + "  p.setor mercadologico1,\n"
                    + "  p.grupo mercadologico2,\n"
                    + "  p.subgrupo mercadologico3,\n"
                    + "  c.nome ncm,\n"
                    + "  c.cest cest,\n"
                    + "  case p.CODIGOREA when 0 then null else p.CODIGOREA end as id_familia,\n"
                    + "  preco.lucro margem,\n"
                    + "  p.pesoliquido,\n"
                    + "  p.peso pesobruto,\n"
                    + "  tp.cst_pis piscofins_cst_sai,\n"
                    + "  tp.cstpisent piscofins_cst_ent,\n"
                    + "  tc.codigo piscofins_natrec,\n"
                    + "  preco.precovenda preco,\n"
                    + "  preco.custobruto custosemimposto,\n"
                    + "  preco.custoliquido custocomimposto,\n"
                    + "  estoq.quantidade estoque,\n"
                    + "  estoq.minimo,\n"
                    + "  estoq.maximo,\n"
                    + "  icms.cst icms_cst,\n"
                    + "  icms.ALIQUOTA icms_aliq,\n"
                    + "  icms.REDUCAO icms_reducao\n"
                    + "from\n"
                    + "  produtos p\n"
                    + "  join produtos_impostos imp on p.codigo = imp.codigo and imp.loja = " + getLojaOrigem() + "\n"
                    + "  join classificacao c on imp.classificacao = c.codigo\n"
                    + "  left join produtos_ean ean on p.codigo = ean.codigo and ean.codbarra > 0 \n"
                    + "  and ean.vendapadrao = 'S'\n"
                    + "  left join classificacao ncm on imp.classificacao = ncm.codigo\n"
                    + "  join produtos_precos preco on p.codigo = preco.codigo and preco.loja = " + getLojaOrigem() + "\n"
                    + "  left join (select a.codigoprodutostipos, a.cstpisent, a.cst_pis, a.TabelaCodigo from \n"
                    + "          produtos_tipos_vigencia a\n"
                    + "          join (select \n"
                    + "              codigoprodutostipos, \n"
                    + "              max(iniciovigencia) iniciovigencia\n"
                    + "            from produtos_tipos_vigencia group by codigoprodutostipos) b\n"
                    + "            on a.codigoprodutostipos = b.codigoprodutostipos and\n"
                    + "            a.iniciovigencia = b.iniciovigencia) tp on p.tipoproduto = tp.codigoprodutostipos\n"
                    + "  left join tabela_codigo tc on tp.TabelaCodigo = tc.Chave\n"
                    + "  join produtos_estoque estoq on estoq.CODIGO = p.codigo and estoq.loja = " + getLojaOrigem() + "\n"
                    + "  join aliquota icms on imp.icms = icms.codigo\n"
                    + "  left join produtos_loja val on p.codigo = val.codigo and val.loja = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "  e_balanca desc, p.codigo"
            )) {

                while (rst.next()) {
                    //Instancia o produto
                    ProdutoIMP imp = new ProdutoIMP();
                    //Prepara as variáveis
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("id_Situacaocadastro")));
                    imp.setDataCadastro(rst.getDate("datacadastro"));

                    imp.setCodMercadologico1(String.valueOf(rst.getInt("mercadologico1") == 0 ? 1 : rst.getInt("mercadologico1")));
                    imp.setCodMercadologico2(String.valueOf(rst.getInt("mercadologico2") == 0 ? 1 : rst.getInt("mercadologico2")));
                    imp.setCodMercadologico3(String.valueOf(rst.getInt("mercadologico3") == 0 ? 1 : rst.getInt("mercadologico3")));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setMargem(rst.getDouble("margem"));

                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));

                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_cst_sai"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofins_cst_ent"));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofins_natrec")));

                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("maximo"));

                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducao(rst.getDouble("icms_reducao"));

                    vProduto.add(imp);
                }
            }
        }

        return vProduto;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "     id_produto codigo,\n"
                    + "     codigo_barra ean,\n"
                    + "     fator_entrada quantidade\n"
                    + "FROM CUPERMAX.PRODUTO_EMBALAGEM"
            )) {
                while (rst.next()) {
                    if ((rst.getString("codbarra") != null)
                            && (!rst.getString("codbarra").trim().isEmpty())
                            && (Long.parseLong(Utils.formataNumero(rst.getString("codbarra"))) > 999999)) {

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo"));
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("quantidade"));
                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id_entidade importId,\n"
                    + "	nome_razao_social razao,\n"
                    + "	nome_fantasia fantasia,\n"
                    + "	cpf_cnpj cnpj,\n"
                    + "	rg_inscricao_estadual ie_rg,\n"
                    + "	inscricao_municipal insc_municipal,\n"
                    + "	case when status = 'A' then 1 else 0 end ativo,\n"
                    + "	endereco,\n"
                    + "	numero_endereco numero,\n"
                    + "	complemento_endereco complemento,\n"
                    + "	bairro,\n"
                    + "	cid.codigo_ibge ibge_municipio,\n"
                    + "	cid.nome cidade,\n"
                    + "	est.codigo_ibge ibge_uf,\n"
                    + "	est.sigla estado,\n"
                    + "	cep,\n"
                    + "	temp_fone tel_principal,\n"
                    + "	f.data_cadastro datacadastro,\n"
                    + "	f.observacao observacao\n"
                    + "from CUPERMAX.ENTIDADE f\n"
                    + "	JOIN CUPERMAX.LOGRADOURO ende\n"
                    + "	ON ende.id = f.LOGRADOURO_ID \n"
                    + "JOIN CUPERMAX.MUNICIPIO cid\n"
                    + "	ON cid.ID = f.MUNICIPIO_ID \n"
                    + "JOIN CUPERMAX.ESTADO est\n"
                    + "	ON est.ID = cid.ID_ESTADO\n"
                    + "WHERE TEMP_DESCRICAO_CATG IN ('FORNECEDORES','EMPRESA')\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("importId"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setInsc_municipal(rst.getString("insc_municipal"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("ibge_municipio"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setIbge_uf(rst.getInt("ibge_uf"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao("IMPORTADO VR");

                    /*
                     if (rst.getString("fone1") != null && !"".equals(rst.getString("fone1"))) {
                     imp.setTel_principal(rst.getString("fone1"));
                     imp.addContato(
                     "1",
                     "FONE 2",
                     rst.getString("fone2"),
                     "",
                     TipoContato.COMERCIAL,
                     ""
                     );
                     } else {
                     imp.setTel_principal(rst.getString("fone2"));
                     }
                     if (!"".equals(Utils.acertarTexto(rst.getString("celular")))) {
                     imp.addContato(
                     "2",
                     "CELULAR",
                     "",
                     rst.getString("celular"),
                     TipoContato.COMERCIAL,
                     ""
                     );
                     }
                     if (!"".equals(Utils.acertarTexto(rst.getString("email")))) {
                     imp.addContato(
                     "3",
                     "EMAIL",
                     "",
                     "",
                     TipoContato.COMERCIAL,
                     rst.getString("email")
                     );
                     }
                     */
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	 id_entidade idFornecedor,\n"
                    + "	 id_produto idProduto,\n"
                    + "	 cod_prod_fornecedor codigoExterno,\n"
                    + "	 data_ultima_compra dataAlteracao,\n"
                    + "	 valor_ultima_compra custoTabela\n"
                    + "FROM CUPERMAX.PRODUTO_FORNECEDOR pf\n"
                    + "WHERE cod_prod_fornecedor IS NOT NULL"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP vo = new ProdutoFornecedorIMP();
                    vo.setImportSistema(getSistema());
                    vo.setImportLoja(getLojaOrigem());
                    vo.setIdFornecedor(rst.getString("idFornecedor"));
                    vo.setIdProduto(rst.getString("idProduto"));
                    vo.setCodigoExterno(rst.getString("codigoExterno"));
                    vo.setDataAlteracao(rst.getDate("dataAlteracao"));
                    vo.setCustoTabela(rst.getDouble("custoTabela"));

                    result.add(vo);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	cli.id_entidade id,\n"
                    + "	cpf_cnpj cnpj,\n"
                    + "	rg_inscricao_estadual inscricaoestadual,\n"
                    + "	orgao_emissor_rg orgaoemissor,\n"
                    + "	nome_razao_social razao,\n"
                    + "	nome_fantasia fantasia,\n"
                    + "	CASE WHEN status = 'A' THEN 1 ELSE 0 END ativo,\n"
                    + "	endereco,\n"
                    + "	numero_endereco numero,\n"
                    + "	complemento_endereco complemento,\n"
                    + "	bairro,\n"
                    + "	cid.codigo_ibge municipioIBGE,\n"
                    + "	cid.nome municipio,\n"
                    + "	est.codigo_ibge ufIBGE,\n"
                    + "	est.sigla uf,\n"
                    + "	cep,\n"
                    + "	data_nascimento dataNascimento,\n"
                    + "	cli.data_cadastro dataCadastro,\n"
                    + "	case when sexo = 'M' then 1 else 0 end sexo,\n"
                    + "	nome_empresa_trabalho empresa,\n"
                    + "	numero_endereco_trabalho empresaNumero,\n"
                    + "	complemento_endereco_trabalho empresaComplemento,\n"
                    + "	telefone_trabalho empresaTelefone,\n"
                    + "	data_admissao dataAdmissao,\n"
                    + "	valor_renda salario,\n"
                    + "	nome_conjuge nomeConjuge,\n"
                    + "	cpf_conjuge cpfConjuge,\n"
                    + "	data_nascimento_conjuge dataNascimentoConjuge,\n"
                    + "	nome_pai nomePai,\n"
                    + "	nome_mae nomeMae,\n"
                    + "	cli.observacao observacao,\n"
                    + "	tel.ddd||tel.numero telefone,\n"
                    + "	temp_fone celular,\n"
                    + "	email\n"
                    + "FROM CUPERMAX.ENTIDADE cli\n"
                    + "	LEFT JOIN CUPERMAX.LOGRADOURO ende\n"
                    + "		ON ende.id = cli.LOGRADOURO_ID \n"
                    + "	LEFT JOIN CUPERMAX.MUNICIPIO cid\n"
                    + "		ON cid.ID = ende.ID_MUNICIPIO \n"
                    + "	LEFT JOIN CUPERMAX.ESTADO est\n"
                    + "		ON est.ID = cid.ID_ESTADO\n"
                    + "	LEFT JOIN CUPERMAX.ENTIDADE_TELEFONE tel\n"
                    + "		ON CLI.ID_ENTIDADE = tel.ID_ENTIDADE\n"
                    + "WHERE TEMP_DESCRICAO_CATG NOT IN ('EMPRESA','FORNECEDORES')\n"
                    + "	ORDER BY 1"
            )) {
                while (rst.next()) {
                    ClienteIMP cli = new ClienteIMP();

                    cli.setId(rst.getString("id"));
                    cli.setCnpj(rst.getString("cnpj"));
                    cli.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    cli.setOrgaoemissor(rst.getString("orgaoemissor"));
                    cli.setRazao(rst.getString("razao"));
                    cli.setFantasia(rst.getString("fantasia"));
                    cli.setAtivo(rst.getBoolean("ativo"));
                    cli.setEndereco(rst.getString("endereco"));
                    cli.setNumero(rst.getString("numero"));
                    cli.setComplemento(rst.getString("complemento"));
                    cli.setBairro(rst.getString("bairro"));
                    cli.setMunicipioIBGE(rst.getInt("municipioIBGE"));
                    cli.setMunicipio(rst.getString("municipio"));
                    cli.setUfIBGE(rst.getInt("ufIBGE"));
                    cli.setUf(rst.getString("uf"));
                    cli.setCep(rst.getString("cep"));
                    cli.setDataNascimento(rst.getDate("dataNascimento"));
                    cli.setDataCadastro(rst.getDate("datacadastro"));

                    cli.setSexo(rst.getInt("sexo") == 1 ? TipoSexo.MASCULINO : TipoSexo.FEMININO);

                    cli.setEmpresa(rst.getString("empresa"));
                    cli.setEmpresaNumero(rst.getString("empresaNumero"));
                    cli.setEmpresaComplemento(rst.getString("empresaComplemento"));
                    cli.setEmpresaTelefone(rst.getString("empresaTelefone"));
                    cli.setDataAdmissao(rst.getDate("dataAdmissao"));
                    cli.setSalario(rst.getDouble("salario"));

                    cli.setNomeConjuge(rst.getString("nomeConjuge"));
                    cli.setCpfConjuge(rst.getString("cpfConjuge"));
                    cli.setDataNascimentoConjuge(rst.getDate("dataNascimentoConjuge"));
                    cli.setNomePai(rst.getString("nomePai"));
                    cli.setNomeMae(rst.getString("nomeMae"));
                    cli.setObservacao(rst.getString("observacao"));

                    cli.setTelefone(rst.getString("telefone"));
                    cli.setCelular(rst.getString("celular"));
                    cli.setEmail(rst.getString("email"));

                    vClientePreferencial.add(cli);
                }
            }
        }
        return vClientePreferencial;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "     id,\n"
                    + "     data_documento dataEmissao,\n"
                    + "     numero_documento numeroCupom,\n"
                    + "     vlr_total_aberto valor,\n"
                    + "     cr.observacao,\n"
                    + "     entidade_id idCliente,\n"
                    + "     data_documento dataVencimento,\n"
                    + "     juros,\n"
                    + "     multa,\n"
                    + "     cpf_cnpj cnpjCliente\n"
                    + "FROM\n"
                    + "	cupermax.LANCAMENTO_CPR cr\n"
                    + "    JOIN cupermax.entidade cli\n"
                    + "    ON cli.id_entidade = cr.entidade_id\n"
                    + "WHERE\n"
                    + "     VLR_TOTAL_PAGO = 0\n"
                    + "     AND cr.TIPO = 'R'\n"
                    + "     AND empresa_id = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numeroCupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("idCliente"));
                    imp.setDataVencimento(rst.getDate("dataVencimento"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setMulta(rst.getDouble("multa"));
                    imp.setCnpjCliente(rst.getString("cnpjCliente"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    /*
     @Override
     public List<ChequeIMP> getCheques() throws Exception {
     List<ChequeIMP> result = new ArrayList<>();
     try (Statement stm = ConexaoOracle.createStatement()) {
     try (ResultSet rst = stm.executeQuery(
     "select\n"
     + "ch.chave id,\n"
     + "ch.codigo as id_cliente, \n"
     + "p.nome,\n"
     + "p.ie, \n"
     + "p.rg, \n"
     + "trim((select case when coalesce(trim(ddd),'') = '' then telefone else ddd||telefone end as asd from telefones where codigo = p.codigo and lower(tipo) = 'residencial' and rownum = 1 )) fone,\n"
     + "ch.agencia, \n"
     + "ch.cheque, \n"
     + "ch.cpf, \n"
     + "ch.cnpj, \n"
     + "ch.emissao,\n"
     + "ch.chave, \n"
     + "ch.vencimento, \n"
     + "ch.serie, \n"
     + "ch.banco, \n"
     + "ch.valor, \n"
     + "ch.pagamento,\n"
     + "ch.observacao, \n"
     + "ch.loja, \n"
     + "ch.conta, \n"
     + "ch.pdv, \n"
     + "ch.cupom, \n"
     + "ch.status, \n"
     + "ch.cmc7, \n"
     + "ch.flagalt, \n"
     + "ch.historico\n"
     + "from CHEQUESRECEBER ch\n"
     + "left join pessoas p on p.codigo = ch.codigo\n"
     + "inner join clientes c on c.codigo = p.codigo\n"
     + "where ch.pagamento = 'N'\n"
     + "and ch.loja = " + getLojaOrigem()
     )) {
     while (rst.next()) {
     ChequeIMP imp = new ChequeIMP();
     imp.setId(rst.getString("id"));
     if ((rst.getString("cpf") != null)
     && (!rst.getString("cpf").trim().isEmpty())) {
     imp.setCpf(rst.getString("cpf"));
     } else if ((rst.getString("cnpj") != null)
     && (!rst.getString("cnpj").trim().isEmpty())) {
     imp.setCpf(rst.getString("cnpj"));
     } else {
     imp.setCpf("");
     }

     imp.setNome(rst.getString("nome"));
     imp.setTelefone(rst.getString("fone"));
     imp.setDate(rst.getDate("emissao"));
     imp.setDataDeposito(rst.getDate("vencimento"));
     imp.setDataHoraAlteracao(rst.getTimestamp("flagalt"));
     imp.setBanco(rst.getInt("banco"));
     imp.setAgencia(rst.getString("agencia"));
     imp.setConta(rst.getString("conta"));
     imp.setValor(rst.getDouble("valor"));
     imp.setEcf(rst.getString("pdv"));
     imp.setNumeroCupom(rst.getString("cupom"));
     imp.setNumeroCheque(rst.getString("cheque"));
     imp.setObservacao("IMPORTADO VR " + rst.getString("observacao") + " " + rst.getString("historico"));
     imp.setAlinea(0);
     imp.setCmc7(rst.getString("cmc7"));
     result.add(imp);
     }
     }
     }
     return result;
     }
     */

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " loja,\n"
                    + " codigo,\n"
                    + " datainicial,\n"
                    + " datafinal,\n"
                    + " precopromocao \n"
                    + "from PROMOCAO \n"
                    + "where loja = " + getLojaOrigem() + "\n"
                    + "and datafinal > sysdate \n"
                    + "order by datafinal desc"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("codigo"));
                    imp.setDataInicio(rst.getDate("datainicial"));
                    imp.setDataFim(rst.getDate("datafinal"));
                    imp.setPrecoOferta(rst.getDouble("precopromocao"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "p.codigo, \n"
                    + "p.razao,\n"
                    + "p.nome, \n"
                    + "p.estado,\n"
                    + "p.cpf,\n"
                    + "p.cnpj,\n"
                    + "p.bairro,\n"
                    + "p.cep,\n"
                    + "p.cidade,\n"
                    + "p.endereco,\n"
                    + "p.estado,\n"
                    + "p.ie,\n"
                    + "p.rg,\n"
                    + "p.flaginc,\n"
                    + "c.dataencerramento,\n"
                    + "c.datarecebimento,\n"
                    + "c.prazodias,\n"
                    + "c.ultimofechamento,\n"
                    + "  trim((select case when coalesce(trim(ddd),'') = '' then telefone else ddd||telefone end as asd "
                    + "from telefones "
                    + "where codigo = p.codigo "
                    + "and lower(tipo) = 'residencial' "
                    + "and rownum = 1 )) fone1, sysdate as datainicio_atual, sysdate as datafinal_atual\n"
                    + "FROM pessoas p,convenios c\n"
                    + "WHERE p.codigo = c.codigo "
                    + ("".equals(v_codEmpresaConv) ? "and c.codigo > 0" : "and c.codigo in (" + v_codEmpresaConv) + ")"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));

                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cpf"));
                    } else if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj("");
                    }

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoEstadual(rst.getString("rg"));
                    } else if ((rst.getString("ie") != null)
                            && (!rst.getString("ie").trim().isEmpty())) {
                        imp.setInscricaoEstadual(rst.getString("ie"));
                    } else {
                        imp.setInscricaoEstadual("");
                    }

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero("0");
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTelefone(rst.getString("fone1"));
                    imp.setDataInicio(rst.getDate("datainicio_atual"));
                    imp.setDataTermino(rst.getDate("datafinal_atual"));
                    imp.setDesconto(0);
                    imp.setDiaPagamento(rst.getInt("prazodias"));
                    imp.setDiaInicioRenovacao(1);
                    imp.setBloqueado(false);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.codigo,\n"
                    + "p.cpf,\n"
                    + "p.nome,\n"
                    + "p.razao,\n"
                    + "p.cnpj,\n"
                    + "p.ie,\n"
                    + "p.rg,\n"
                    + "c.bloqueado,\n"
                    + "c.codigoconvenio,\n"
                    + "c.limite    \n"
                    + "from pessoas p\n"
                    + "inner join clientes c on c.codigo = p.codigo\n"
                    + "where p.cliente = 'S'\n"
                    + "and p.convenio = 'N'\n"
                    + ("".equals(v_codEmpresaConv) ? "and c.codigoconvenio > 0" : "and c.codigoconvenio in (" + v_codEmpresaConv) + ")"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setNome(rst.getString("razao"));
                    imp.setIdEmpresa(rst.getString("codigoconvenio"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setConvenioLimite(rst.getDouble("limite"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  rot.chave id,\n"
                    + "  ROT.CODIGO id_cliente,\n"
                    + "  coalesce(c.cnpj, c.cpf) cnpj,\n"
                    + "  ROT.LOJA id_loja,\n"
                    + "  ROT.EMISSAO dataemissao,\n"
                    + "  rot.cupom,\n"
                    + "  ROT.VALOR,\n"
                    + "  round((((rot.taxa / 30) * floor(current_date - rot.vencimento)) / 100) * rot.valor, 2) juros,\n"
                    + "  ROT.HISTORICO observacao,\n"
                    + "  ROT.VENCIMENTO,\n"
                    + "  ROT.PDV ecf\n"
                    + "FROM RECEBER_CONTAS  ROT  \n"
                    + "INNER JOIN PESSOAS C ON C.CODIGO = ROT.CODIGO\n"
                    + "INNER JOIN CLIENTES CLI ON CLI.CODIGO = C.CODIGO\n"
                    + "where rot.chaverecebimento = 0 and rot.loja = " + getLojaOrigem()
                    + " and CLI.codigoconvenio > 0"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("id_cliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setDataHora(rst.getTimestamp("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
