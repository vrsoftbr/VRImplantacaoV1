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
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class DataSyncDAO extends InterfaceDAO {

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
            OpcaoProduto.EAN_EM_BRANCO
        }));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
                
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "declare @empresa integer = " + getLojaOrigem() + ";\n" +
                    "select\n" +
                    "	e.ESTOQUE_ID id,\n" +
                    "	e.DATA_CADASTRO,\n" +
                    "	ean.CODIGO_BARRA ean,\n" +
                    "	1 qtdembalagem,\n" +
                    "	un.DESCRICAO unidade,\n" +
                    "	e.BALANCA,\n" +
                    "	e.VALIDADE_DIAS validade,\n" +
                    "	e.DESCRICAO,\n" +
                    "	e.DESCRICAO_REDUZ,\n" +
                    "	e.GRUPO_ID,\n" +
                    "	e.SECAO_ID,\n" +
                    "	e.SETOR_ID,\n" +
                    "	e.ESTOQUE_ID_PAI,\n" +
                    "	e.PESO pesobruto,\n" +
                    "	e.PESO_LIQUIDO pesoliquodo,\n" +
                    "	coalesce(estoque.QUANTIDADE, 0) estoque,\n" +
                    "	pr.VALOR preco,\n" +
                    "	case e.INATIVO when 1 then 0 else 1 end ativo,\n" +
                    "	e.CODIGO_NCM ncm,\n" +
                    "	e.CODIGO_CEST cest,\n" +
                    "	f.SAI_CST_PIS piscofins_cst_sai,\n" +
                    "	f.ENT_CST_PIS piscofins_cst_ent,\n" +
                    "	f.SAI_PIS_NATUREZA_RECEITA_ID piscofins_natureza_receita,\n" +
                    "	f.SAI_CST_DENTRO_EST,\n" +
                    "	f.SAI_CST_FORA_EST,\n" +
                    "	f.SAI_ICMS_DENTRO_EST,\n" +
                    "	f.SAI_ICMS_FORA_EST,\n" +
                    "	e.FABRICANTE_ID\n" +
                    "from\n" +
                    "	ESTOQUE e\n" +
                    "	left join (\n" +
                    "		select\n" +
                    "			estoque_id,\n" +
                    "			CODIGO_BARRA\n" +
                    "		from\n" +
                    "			ESTOQUE\n" +
                    "		where\n" +
                    "			not CODIGO_BARRA is null\n" +
                    "		union\n" +
                    "		select\n" +
                    "			ESTOQUE_ID,\n" +
                    "			CODIGO_BARRA\n" +
                    "		from\n" +
                    "			ESTOQUE_CODIGOS_BARRAS\n" +
                    "		where\n" +
                    "			not CODIGO_BARRA is null\n" +
                    "	) ean on e.ESTOQUE_ID = ean.ESTOQUE_ID\n" +
                    "	left join ESTOQUE_UNIDADES un on\n" +
                    "		e.UNIDADE_ID_VENDA = un.UNIDADE_ID\n" +
                    "	left join (\n" +
                    "		SELECT\n" +
                    "			em.ESTOQUE_ID,\n" +
                    "			ISNULL(SUM(\n" +
                    "				case etm.operacao when 0 then em.quantidade else em.QUANTIDADE * (-1) end\n" +
                    "				), 0) AS QUANTIDADE\n" +
                    "		FROM\n" +
                    "			ESTOQUE_MOVIMENTOS em\n" +
                    "			LEFT JOIN (select tipo_id, case operacao when 'Entrada' then 0 else 1 end operacao from ESTOQUE_TIPOS_MOVIMENTOS) etm ON\n" +
                    "				etm.TIPO_ID = em.TIPO_ID\n" +
                    "		WHERE\n" +
                    "			(em.DEL IS NULL OR em.DEL = 0) and\n" +
                    "			em.EMPRESA_ID = @empresa	\n" +
                    "		group by\n" +
                    "			em.ESTOQUE_ID\n" +
                    "	) estoque on\n" +
                    "		estoque.ESTOQUE_ID = e.ESTOQUE_ID\n" +
                    "	left join ESTOQUE_TABELA_PRECOS pr on\n" +
                    "		pr.EMPRESA_ID = @empresa and\n" +
                    "		pr.ESTOQUE_ID = e.ESTOQUE_ID and\n" +
                    "		pr.DESCRICAO = 'VAREJO'\n" +
                    "	left join ESTOQUE_DADOS_FISCAIS f on\n" +
                    "		f.EMPRESA_ID = @empresa and\n" +
                    "		f.ESTOQUE_ID = e.ESTOQUE_ID\n" +
                    "order by\n" +
                    "	1"
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
                    imp.setPesoLiquido(rst.getDouble("pesoliquodo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getInt("ativo") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_sai"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_ent"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));
                    imp.setIcmsCstSaida(Utils.stringToInt(rst.getString("SAI_CST_DENTRO_EST")));
                    imp.setIcmsCstSaidaForaEstado(Utils.stringToInt(rst.getString("SAI_CST_FORA_EST")));
                    imp.setIcmsCstSaidaForaEstadoNF(Utils.stringToInt(rst.getString("SAI_CST_FORA_EST")));
                    imp.setIcmsAliqSaida(rst.getDouble("SAI_ICMS_DENTRO_EST"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("SAI_ICMS_FORA_EST"));
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
                    "select\n" +
                    "	f.FORNECEDOR_ID id,\n" +
                    "	f.NOME_RAZAO razao,\n" +
                    "	f.FANTASIA fantasia,\n" +
                    "	coalesce(f.CNPJ, f.CPF) cnpj,\n" +
                    "	f.IE,\n" +
                    "	f.ATIVO,\n" +
                    "	f.ENDERECO,\n" +
                    "	f.NUMERO,\n" +
                    "	f.COMPLEMENTO,\n" +
                    "	f.BAIRRO,\n" +
                    "	cd.CIDADE_ID ibge_municipio,\n" +
                    "	cd.NOME municipio,\n" +
                    "	cd.UF uf,\n" +
                    "	f.CEP,\n" +
                    "	f.FONE,\n" +
                    "	f.CELULAR,\n" +
                    "	f.FAX,\n" +
                    "	f.SUFRAMA,\n" +
                    "	f.OBS,	\n" +
                    "	f.PRAZO_ENTREGA\n" +
                    "from\n" +
                    "	FORNECEDORES f\n" +
                    "	left join CIDADES cd on\n" +
                    "		cd.CIDADE_ID = f.CIDADE_ID\n" +
                    "order by\n" +
                    "	f.FORNECEDOR_ID;"
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
                    "select\n" +
                    "	c.CLIENTE_ID id,\n" +
                    "	coalesce(nullif(c.CNPJ,''),c.CPF) cnpj,\n" +
                    "	c.IE,\n" +
                    "	c.NOME_RAZAO razao,\n" +
                    "	c.FANTASIA,\n" +
                    "	c.ATIVO,\n" +
                    "	c.ENDERECO,\n" +
                    "	c.NUMERO,\n" +
                    "	c.COMPLEMENTO,\n" +
                    "	c.BAIRRO,\n" +
                    "	c.CIDADE_ID,\n" +
                    "	cd.NOME cidade,\n" +
                    "	cd.UF,\n" +
                    "	c.CEP,\n" +
                    "	c.EST_CIVIL,\n" +
                    "	c.DATA_CADASTRO,\n" +
                    "	c.DATA_NASCIMENTO,\n" +
                    "	c.SEXO,\n" +
                    "	c.PROFISSAO,\n" +
                    "	c.VR_RENDA salario,\n" +
                    "	c.VR_LIMITE limite,\n" +
                    "	c.NOME_PAI,\n" +
                    "	c.NOME_MAE,\n" +
                    "	c.CONJ_NOME conjuge,\n" +
                    "	c.OBS,\n" +
                    "	c.FONE,\n" +
                    "	c.FAX,\n" +
                    "	c.CELULAR,\n" +
                    "	c.EMAIL\n" +
                    "from \n" +
                    "	CLIENTES c\n" +
                    "	left join CIDADES cd on\n" +
                    "		cd.CIDADE_ID = c.CIDADE_ID\n" +
                    "order by\n" +
                    "	id"
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
    
    
}
