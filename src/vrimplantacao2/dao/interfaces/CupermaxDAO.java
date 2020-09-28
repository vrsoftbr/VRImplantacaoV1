package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
//import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
//import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
//import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
//import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
//import vrimplantacao2.vo.importacao.OfertaIMP;
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

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {

        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,1,3) m1,\n"
                    + "	DESCRICAO descricao\n"
                    + "FROM \n"
                    + "	CUPERMAX.nivel_mercadologico\n"
                    + "WHERE\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,5,3) IS NULL \n"
                    + "	AND SUBSTRB (CODIGO_ESTRUTURADO,9,3) IS NULL \n"
                    + "	ORDER BY 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP(rst.getString("m1"), rst.getString("descricao"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,1,3) m1,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,5,3) m2,\n"
                    + "	DESCRICAO descricao\n"
                    + "FROM \n"
                    + "	CUPERMAX.nivel_mercadologico\n"
                    + "WHERE\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,5,3) IS NOT NULL \n"
                    + "	AND SUBSTRB (CODIGO_ESTRUTURADO,9,3) IS NULL\n"
                    + "	AND COD_GRUPO IN (SELECT ID FROM CUPERMAX.NIVEL_MERCADOLOGICO) \n"
                    + "ORDER BY 1,2"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP pai = merc.get(rst.getString("m1"));
                    pai.addFilho(rst.getString("m2"), rst.getString("descricao"));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,1,3) m1,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,5,3) m2,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,9,3) m3,\n"
                    + "	DESCRICAO descricao\n"
                    + "FROM \n"
                    + "	CUPERMAX.nivel_mercadologico\n"
                    + "WHERE\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,5,3) IS NOT NULL\n"
                    + "	AND SUBSTRB (CODIGO_ESTRUTURADO,9,3) IS NOT NULL \n"
                    + "ORDER BY 1,2,3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP pai = merc.get(rst.getString("m1"));
                    pai = pai.getNiveis().get(rst.getString("m2"));
                    pai.addFilho(rst.getString("m3"), rst.getString("descricao"));
                }
            }
        }

        return new ArrayList<>(merc.values());
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vProduto = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	p.id importId,\n"
                    + "	P.data_cadastro dataCadastro,\n"
                    + "	codigo_barra ean,\n"
                    + "	CASE WHEN lengthb (codigo_barra) < 8 AND u.sigla = 'KG' THEN  1 ELSE 0 END ebalanca,\n"
                    + "	fator_conversao qtdEmbalagem,\n"
                    + "	u.sigla tipoEmbalagem,\n"
                    + "	P.descricao AS descricaoCompleta,\n"
                    + "	P.descricao AS descricaoReduzida,\n"
                    + "	P.descricao AS descricaoGondola,\n"
                    + "	peso_bruto pesoBruto,\n"
                    + "	peso_liquido pesoLiquido,\n"
                    + "	P.estoque_minimo estoqueMinimo,\n"
                    + "	estoque_geral estoque,\n"
                    + " perc_margem_lucro margem,\n"
                    + "	preco_compra custoSemImposto,\n"
                    + "	preco_venda_anterior custoAnteriorSemImposto,\n"
                    + "	preco_venda_final precovenda,\n"
                    + " CASE WHEN status = 'A' THEN 1 ELSE 0 END status,\n"
                    + " SUBSTRB (CODIGO_ESTRUTURADO,1,3) merc1,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,5,3) merc2,\n"
                    + "	SUBSTRB (CODIGO_ESTRUTURADO,9,3) merc3,\n"
                    + "	nomenclatura_com_mercosul ncm,\n"
                    + "	cest,\n"
                    + "	cst_pis_entrada piscofinsCstDebito,\n"
                    + "	cst_pis piscofinsCstCredito\n"
                    + "FROM\n"
                    + "	CUPERMAX.PRODUTO p\n"
                    + "		JOIN CUPERMAX.PRODUTO_EMBALAGEM pe\n"
                    + "			ON p.ID = pe.ID_PRODUTO \n"
                    + "		JOIN CUPERMAX.UNIDADE u \n"
                    + "			ON U.ID = PE.ID_UNIDADE\n"
                    + "         JOIN CUPERMAX.NIVEL_MERCADOLOGICO nm \n"
                    + "                 ON p.ID_NIVEL_MERCADOLOGICO = nm.ID\n"
                    + "ORDER BY 1\n"
            )) {

                while (rst.next()) {
                    //Instancia o produto
                    ProdutoIMP imp = new ProdutoIMP();
                    //Prepara as variáveis
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("importId"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setCustoAnteriorSemImposto(rst.getDouble("custoAnteriorSemImposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rst.getInt("status") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setPiscofinsCstDebito(rst.getInt("piscofinsCstDebito"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofinsCstCredito"));

                    
                     imp.setCodMercadologico1(rst.getString("merc1"));
                     imp.setCodMercadologico2(rst.getString("merc2"));
                     imp.setCodMercadologico3(rst.getString("merc3"));
                     
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
                    if ((rst.getString("ean") != null)
                            && (!rst.getString("ean").trim().isEmpty())
                            && (Long.parseLong(Utils.formataNumero(rst.getString("ean"))) > 999999)) {

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
}
