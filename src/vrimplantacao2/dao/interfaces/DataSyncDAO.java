package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class DataSyncDAO extends InterfaceDAO {

    private String tipoPreco = "VAREJO";

    public void setTipoPreco(String tipoPreco) {
        this.tipoPreco = tipoPreco == null ? "VAREJO" : tipoPreco;
    }

    @Override
    public String getSistema() {
        return "DataSync";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select EMPRESA_ID, RAZAO_SOCIAL from MAXIMUS_BASE.dbo.EMPRESAS order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("EMPRESA_ID"), rst.getString("RAZAO_SOCIAL")));
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.PRECO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.MARGEM,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.ATIVO,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ICMS
        }));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "declare @empresa integer =  " + getLojaOrigem() + ";\n"
                    + "select\n"
                    + "	e.ESTOQUE_ID id,\n"
                    + "	e.DATA_CADASTRO,\n"
                    + "	ean.CODIGO_BARRA ean,\n"
                    + "	1 qtdembalagem,\n"
                    + "	un.DESCRICAO unidade,\n"
                    + "	e.BALANCA,\n"
                    + "	e.VALIDADE_DIAS validade,\n"
                    + "	replace(e.descricao,'***',' RC') descricao,\n"
                    + "	replace(e.descricao_reduz,'***',' RC') descricao_reduz,\n"
                    + "	e.GRUPO_ID,\n"
                    + "	e.SECAO_ID,\n"
                    + "	e.SETOR_ID,\n"
                    + "	e.ESTOQUE_ID_PAI,\n"
                    + "	e.PESO pesobruto,\n"
                    + "	e.PESO_LIQUIDO pesoliquido,\n"
                    + "	coalesce(estoque.QUANTIDADE, 0) estoque,\n"
                    + "	round(pr.VALOR, 2) preco,\n"
                    + "	coalesce(round(\n"
                    + "		(select top 1 a.custo_bruto from \n"
                    + "			ESTOQUE_FORMACAO_PRECOS a\n"
                    + "		where\n"
                    + "			a.ESTOQUE_ID = f.ESTOQUE_ID and\n"
                    + "			a.EMPRESA_ID = @empresa\n"
                    + "		order by a.NF_ID desc)\n"
                    + "	,2),0) custo,\n"
                    + "	coalesce(round(\n"
                    + "		(select top 1 a.MARGEN_LUCRO from \n"
                    + "			ESTOQUE_FORMACAO_PRECOS a\n"
                    + "			join NOTA_FISCAL_COMPRA nf on a.NF_ID = nf.NF_ID\n"
                    + "		where\n"
                    + "			a.ESTOQUE_ID = f.ESTOQUE_ID and\n"
                    + "			a.EMPRESA_ID = @empresa\n"
                    + "		order by nf.DATA_EMISSAO desc)\n"
                    + "	,2),0) margem,\n"
                    + "	case e.INATIVO when 1 then 0 else 1 end descontinuado,\n"
                    + "	case when e.DEL = 1 or e.inativo =1 then 0 else 1 end situacaocadastro,\n"
                    + "	e.CODIGO_NCM ncm,\n"
                    + "	e.CODIGO_CEST cest,\n"
                    + "	f.SAI_CST_PIS piscofins_cst_sai,\n"
                    + "	f.ENT_CST_PIS piscofins_cst_ent,\n"
                    + "	nr.codigo piscofins_natureza_receita,\n"
                    + "	f.SAI_CST_DENTRO_EST,\n"
                    + "	f.SAI_CST_FORA_EST,\n"
                    + "	f.SAI_ICMS_DENTRO_EST,\n"
                    + "	f.SAI_ICMS_FORA_EST,\n"
                    + "	e.FABRICANTE_ID\n"
                    + "from\n"
                    + "	ESTOQUE e\n"
                    + "	left join (\n"
                    + "		select\n"
                    + "			estoque_id,\n"
                    + "			CODIGO_BARRA\n"
                    + "		from\n"
                    + "			ESTOQUE\n"
                    + "		where\n"
                    + "			not CODIGO_BARRA is null\n"
                    + "		union\n"
                    + "		select\n"
                    + "			ESTOQUE_ID,\n"
                    + "			CODIGO_BARRA\n"
                    + "		from\n"
                    + "			ESTOQUE_CODIGOS_BARRAS\n"
                    + "		where\n"
                    + "			not CODIGO_BARRA is null\n"
                    + "	) ean on e.ESTOQUE_ID = ean.ESTOQUE_ID\n"
                    + "	left join ESTOQUE_UNIDADES un on\n"
                    + "		e.UNIDADE_ID_VENDA = un.UNIDADE_ID\n"
                    + "	left join (\n"
                    + "		SELECT\n"
                    + "		  e1.ESTOQUE_ID,\n"
                    + "		  ISNULL(dbo.FN_ESTOQUE_CONTA(e1.ESTOQUE_ID, ec.EMPRESA_ID, ec.CONTA_EST_ID), 0) + \n"
                    + "		  ISNULL(dbo.FN_ESTOQUE_COMPROMETIDO(e1.ESTOQUE_ID, ec.EMPRESA_ID, ec.CONTA_EST_ID, 1), 0) AS QUANTIDADE\n"
                    + "		FROM ESTOQUE e1\n"
                    + "		LEFT JOIN ESTOQUE_CONTAS ec\n"
                    + "		  ON (ec.DEL IS NULL\n"
                    + "		  OR ec.DEL = 0)\n"
                    + "		where\n"
                    + "			ec.EMPRESA_ID = @empresa\n"
                    + "	) estoque on\n"
                    + "		estoque.ESTOQUE_ID = e.ESTOQUE_ID\n"
                    + "	left join ESTOQUE_TABELA_PRECOS pr on\n"
                    + "		pr.EMPRESA_ID = @empresa and\n"
                    + "		pr.ESTOQUE_ID = e.ESTOQUE_ID and\n"
                    + "		pr.DESCRICAO = 'ATACADO'\n"
                    + "	left join ESTOQUE_DADOS_FISCAIS f on\n"
                    + "		f.EMPRESA_ID = @empresa and\n"
                    + "		f.ESTOQUE_ID = e.ESTOQUE_ID\n"
                    + "   left join ESTOQUE_NATUREZA_RECEITA nr on\n"
                    + "           nr.REGISTRO_ID = f.sai_pis_natureza_receita_id\n"
                    + "order by\n"
                    + "	1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("DATA_CADASTRO"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("BALANCA"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                    imp.setDescricaoGondola(rst.getString("DESCRICAO"));
                    imp.setDescricaoReduzida(rst.getString("DESCRICAO_REDUZ"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_sai"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_ent"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));
                    imp.setIcmsCstSaida(Utils.stringToInt(rst.getString("SAI_CST_DENTRO_EST")));
                    imp.setIcmsCstEntrada(Utils.stringToInt(rst.getString("SAI_CST_DENTRO_EST")));
                    imp.setIcmsCstSaidaForaEstado(Utils.stringToInt(rst.getString("SAI_CST_FORA_EST")));
                    imp.setIcmsCstEntradaForaEstado(Utils.stringToInt(rst.getString("SAI_CST_FORA_EST")));
                    imp.setIcmsCstSaidaForaEstadoNF(Utils.stringToInt(rst.getString("SAI_CST_FORA_EST")));
                    imp.setIcmsAliqSaida(rst.getDouble("SAI_ICMS_DENTRO_EST"));
                    imp.setIcmsAliqEntrada(rst.getDouble("SAI_ICMS_DENTRO_EST"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("SAI_ICMS_FORA_EST"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("SAI_ICMS_FORA_EST"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("SAI_ICMS_FORA_EST"));
                    imp.setFornecedorFabricante(rst.getString("FABRICANTE_ID"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.FORNECEDOR_ID id,\n"
                    + "	f.NOME_RAZAO razao,\n"
                    + "	f.FANTASIA fantasia,\n"
                    + "	coalesce(f.CNPJ, f.CPF) cnpj,\n"
                    + "	f.IE,\n"
                    + "	f.ATIVO,\n"
                    + "	f.ENDERECO,\n"
                    + "	f.NUMERO,\n"
                    + "	f.COMPLEMENTO,\n"
                    + "	f.BAIRRO,\n"
                    + "	cd.CIDADE_ID ibge_municipio,\n"
                    + "	cd.NOME municipio,\n"
                    + "	cd.UF uf,\n"
                    + "	f.CEP,\n"
                    + "	f.FONE,\n"
                    + "	f.CELULAR,\n"
                    + "	f.FAX,\n"
                    + "	f.SUFRAMA,\n"
                    + "	f.OBS,	\n"
                    + "	f.PRAZO_ENTREGA\n"
                    + "from\n"
                    + "	FORNECEDORES f\n"
                    + "	left join CIDADES cd on\n"
                    + "		cd.CIDADE_ID = f.CIDADE_ID\n"
                    + "order by\n"
                    + "	f.FORNECEDOR_ID;"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("CNPJ"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setAtivo(rst.getBoolean("ATIVO"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setIbge_municipio(rst.getInt("ibge_municipio"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTel_principal(rst.getString("FONE"));
                    imp.addCelular("CELULAR", rst.getString("CELULAR"));
                    imp.addTelefone("FAX", rst.getString("FAX"));
                    imp.setSuframa(rst.getString("SUFRAMA"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setPrazoEntrega(rst.getInt("PRAZO_ENTREGA"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.CLIENTE_ID id,\n"
                    + "	coalesce(nullif(c.CNPJ,''),c.CPF) cnpj,\n"
                    + "	c.IE,\n"
                    + "	c.NOME_RAZAO razao,\n"
                    + "	c.FANTASIA,\n"
                    + "	c.ATIVO,\n"
                    + "	c.ENDERECO,\n"
                    + "	c.NUMERO,\n"
                    + "	c.COMPLEMENTO,\n"
                    + "	c.BAIRRO,\n"
                    + "	c.CIDADE_ID,\n"
                    + "	cd.NOME cidade,\n"
                    + "	cd.UF,\n"
                    + "	c.CEP,\n"
                    + "	c.EST_CIVIL,\n"
                    + "	c.DATA_CADASTRO,\n"
                    + "	c.DATA_NASCIMENTO,\n"
                    + "	c.SEXO,\n"
                    + "	c.PROFISSAO,\n"
                    + "	c.VR_RENDA salario,\n"
                    + "	c.VR_LIMITE limite,\n"
                    + "	c.NOME_PAI,\n"
                    + "	c.NOME_MAE,\n"
                    + "	c.CONJ_NOME conjuge,\n"
                    + "	c.OBS,\n"
                    + "	c.FONE,\n"
                    + "	c.FAX,\n"
                    + "	c.CELULAR,\n"
                    + "	c.EMAIL\n"
                    + "from \n"
                    + "	CLIENTES c\n"
                    + "	left join CIDADES cd on\n"
                    + "		cd.CIDADE_ID = c.CIDADE_ID\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("IE"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setAtivo(rst.getBoolean("ATIVO"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipioIBGE(rst.getInt("CIDADE_ID"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("UF"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setEstadoCivil(rst.getString("EST_CIVIL"));
                    imp.setDataCadastro(rst.getDate("DATA_CADASTRO"));
                    imp.setDataNascimento(rst.getDate("DATA_NASCIMENTO"));
                    imp.setSexo(Utils.acertarTexto(rst.getString("SEXO")).startsWith("M") ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setCargo(rst.getString("PROFISSAO"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomePai(rst.getString("NOME_PAI"));
                    imp.setNomeMae(rst.getString("NOME_MAE"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setTelefone(rst.getString("FONE"));
                    imp.setFax(rst.getString("FAX"));
                    imp.setCelular(rst.getString("CELULAR"));
                    imp.setEmail(rst.getString("EMAIL"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	receber_id as id,\n"
                    + "	c.cpf as cpf,\n"
                    + "	cheque_numero as numerocheque,\n"
                    + "	cheque_agencia as agencia,\n"
                    + "	cheque_cc as conta,\n"
                    + "	data_emissao as date,\n"
                    + "	desconto_data as datadeposito,\n"
                    + "	documento as numerocupom,\n"
                    + "	documento as ecf,\n"
                    + "	valor_bruto as valor,\n"
                    + "	c.rg as rg,\n"
                    + "	c.fone as telefone,\n"
                    + "	c.nome_razao as nome,\n"
                    + "	cheque_banco+' '+historico+' '+cr.obs as  observacao,\n"
                    + "	status as situacaocheque,\n"
                    + "	juros_dia as valorjuros\n"
                    + "from contas_receber cr\n"
                    + "	left join clientes c\n"
                    + "		on c.cliente_id = cr.devedor_id\n"
                    + "where cheque_numero is not null and EMPRESA_ID = " + getLojaOrigem())) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setCpf(rst.getString("numerocheque"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setDate(rst.getDate("date"));
                    imp.setDataDeposito(rst.getDate("datadeposito"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setSituacaoCheque("Pendente".equals(rst.getString("situacaocheque")) ? SituacaoCheque.ABERTO : SituacaoCheque.BAIXADO);
                    imp.setValorJuros(rst.getDouble("valorjuros"));

                    result.add(imp);

                }
            }
        }
        return result; //To change body of generated methods, choose Tools | Templates.
    }

}
