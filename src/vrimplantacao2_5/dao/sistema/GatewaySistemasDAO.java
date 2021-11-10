/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class GatewaySistemasDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Gateway Sistemas";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	l.COD_EMPRESA AS id, \n"
                    + "	(l.NOME||' - '||l.CNPJ) AS empresa \n"
                    + "FROM EMITENTE l \n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("empresa")));
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
                    "SELECT \n"
                    + "	e.CODIGO  AS id,\n"
                    + "	e.BARRAS  AS ean,\n"
                    + "	e.PADRAO_BARRAS AS tipo,\n"
                    + "	e.UND_C  AS tipoembalagemcotacao,\n"
                    + "	e.UND_V  AS tipoembalagem,\n"
                    + "	e.ATIVO AS situacaocadastro,\n"
                    + "	e.NOME AS descricaocompleta,\n"
                    + "	e.QTD AS estoque,\n"
                    + "	e.QTD_MAXIMA AS estoquemaximo,\n"
                    + "	e.QTD_MINIMA AS estoqueminimo,\n"
                    + "	e.PRECO_CUSTO AS custo,\n"
                    + "	e.PRECO_VENDA AS precovenda,\n"
                    + "	e.PESO_BRUTO AS pesobruto,\n"
                    + "	e.PESO_LIQUIDO AS pesoliquido,\n"
                    + "	e.NCM,\n"
                    + "	et.CEST AS cest,	\n"
                    + "	et.TIPO_TRIBUTACAO,\n"
                    + "	et.ST AS cst,\n"
                    + "	et.ICMS AS icms,\n"
                    + "	et.REDUCAO AS reducao,\n"
                    + "	et.MVA AS mva,\n"
                    + "	et.PIS_ST AS cstpis,\n"
                    + "	et.COFINS_ST AS cstcofins,	\n"
                    + "	et.ALIQ_FCP AS fcp\n"
                    + "FROM ESTOQUE e\n"
                    + "LEFT JOIN EST_TRIBUTACAO et ON et.CODIGO = e.CODIGO\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpis"));
                    imp.setPiscofinsCstCredito(rst.getString("cstcofins"));

                    imp.setIcmsCstSaida(rst.getInt("cst"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaida(rst.getDouble("reducao"));
                    imp.setIcmsCstSaidaForaEstado(rst.getInt("cst"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("reducao"));
                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("cst"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("reducao"));

                    imp.setIcmsCstEntrada(rst.getInt("cst"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("reducao"));
                    imp.setIcmsCstEntradaForaEstado(rst.getInt("cst"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("icms"));
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("reducao"));

                    imp.setIcmsCstConsumidor(rst.getInt("cst"));
                    imp.setIcmsAliqConsumidor(rst.getDouble("icms"));
                    imp.setIcmsReducaoConsumidor(rst.getDouble("reducao"));

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
                    "SELECT \n"
                    + "	f.CODIGO AS id,\n"
                    + "	f.nome AS razao,\n"
                    + "	COALESCE(f.FANTASIA, f.NOME) AS fantasia,\n"
                    + "	f.CNPJ AS cnpj,\n"
                    + "	f.IE AS ie_rg,\n"
                    + "	f.CPF AS cpf,\n"
                    + "	f.RG AS rg,\n"
                    + "	f.ENDERECO AS enderenco,\n"
                    + "	f.NUMERO AS numero,\n"
                    + "	f.COMPLEMENTO AS complemento,\n"
                    + "	f.BAIRRO AS bairro,\n"
                    + "	f.CEP AS cep,\n"
                    + "	f.UF AS uf,\n"
                    + "	f.CIDADE AS municipio,\n"
                    + "	f.COD_CIDADE AS muinicioibge,\n"
                    + "	f.TELEFONE AS telefone,\n"
                    + "	f.CELULAR AS celular,\n"
                    + "	f.FAX AS fax,\n"
                    + "	f.EMAIL AS email,\n"
                    + "	f.SITE AS site\n"
                    + "FROM FORNECEDORES f \n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));

                    if (rst.getString("cnpj") != null && !rst.getString("cnpj").trim().isEmpty()) {
                        imp.setCnpj_cpf(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj_cpf(rst.getString("cpf"));
                    }

                    if (rst.getString("ie_rg") != null && !rst.getString("ie_rg").trim().isEmpty()) {
                        imp.setIe_rg(rst.getString("ie_rg"));
                    } else {
                        imp.setIe_rg(rst.getString("rg"));
                    }

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));

                    if (rst.getString("celular") != null && !rst.getString("celular").trim().isEmpty()) {
                        imp.addCelular("CELULAR", rst.getString("celular"));
                    }
                    if (rst.getString("fax") != null && !rst.getString("fax").trim().isEmpty()) {
                        imp.addTelefone("FAX", rst.getString("Fax"));
                    }
                    if (rst.getString("email") != null && !rst.getString("email").trim().isEmpty()) {
                        imp.addEmail("EMAIL", rst.getString("email"), TipoContato.COMERCIAL);
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
                    "SELECT\n"
                    + "     ea.COD_FORNECEDOR AS idfornecedor,\n"
                    + "     f.NOME AS descricaofornecedor,\n"
                    + "     ea.CODIGO AS idproduto,\n"
                    + "     p.NOME AS descricaoproduto,\n"
                    + "     ea.COD_FABRICANTE AS codigoexterno,\n"
                    + "     ea.DATA_CADASTRO AS dataalteracao\n"
                    + "FROM EST_ADICIONAIS ea\n"
                    + "JOIN ESTOQUE p ON p.CODIGO = ea.CODIGO \n"
                    + "JOIN FORNECEDORES f ON f.CODIGO = ea.COD_FORNECEDOR \n"
                    + "	AND ea.COD_FORNECEDOR != ''\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
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
                    "SELECT \n"
                    + "	c.CODIGO AS id,\n"
                    + "	c.NOME AS razao,\n"
                    + "	COALESCE(c.FANTASIA, c.NOME) AS fantasia,\n"
                    + "	c.CNPJ AS cnpj,\n"
                    + "	c.IE AS inscricaoestadual,\n"
                    + "	c.CPF AS cpf,\n"
                    + "	c.RG AS rg,\n"
                    + "	c.ATIVO AS ativo,\n"
                    + "	c.ENDERECO AS endereco,\n"
                    + "	c.NUMERO AS numero,\n"
                    + "	c.COMPLEMENTO AS complemento,\n"
                    + "	c.BAIRRO AS bairro,\n"
                    + "	c.CEP AS cep,\n"
                    + "	c.UF AS uf,\n"
                    + "	c.CIDADE AS municipio,\n"
                    + "	c.COD_CIDADE AS municipioibge,\n"
                    + "	c.TELEFONE AS telefone,\n"
                    + "	c.CELULAR AS celular,\n"
                    + "	c.FAX AS fax,\n"
                    + "	c.EMAIL AS email,\n"
                    + "	c.OBSERVACOES AS obs,\n"
                    + "	c.NOME_MAE AS nomemae,\n"
                    + "	c.NOME_PAI AS nomepai,\n"
                    + "	c.CONJUGUE AS nomeconjuge,\n"
                    + "	c.PROFISSAO AS cargo,\n"
                    + "	c.NASCIMENTO AS datanascimento,\n"
                    + "	c.LIMITE_CREDITO AS valorlimite,\n"
                    + "	c.DATA_CADASTRO AS datacadastro,\n"
                    + "	c.BLOQUEADO AS bloqueado\n"
                    + "FROM CLIENTES c \n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));

                    if (rst.getString("cnpj") != null && !rst.getString("cnpj").trim().isEmpty()) {
                        imp.setCnpj(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj(rst.getString("cpf"));
                    }

                    if (rst.getString("inscricaoestadual") != null && !rst.getString("inscricaoestadual").trim().isEmpty()) {
                        imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    } else {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    }

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setBloqueado(rst.getInt("bloqueado") == 1);
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setObservacao(rst.getString("obs"));

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
                    "SELECT \n"
                    + "	r.CODIGO AS id,\n"
                    + "	substring(r.DOCUMENTO FROM 4) AS numerodocumento,\n"
                    + "	r.EMISSAO AS dataemissao,\n"
                    + "	r.VENCIMENTO AS datavencimento,\n"
                    + "	r.CLIENTE AS idcliente,\n"
                    + "	r.CAIXA AS ecf,\n"
                    + " r.VALOR as valor, \n"        
                    + "	r.DESCRICAO AS obs\n"
                    + "FROM RECEBER r\n"
                    + "WHERE r.CLIENTE IS NOT NULL\n"
                    + "AND r.CONVENIO IS NULL\n"
                    + "AND r.VALOR_RECEBIDO  < VALOR\n"
                    + "AND r.RECEBIMENTO IS NULL "
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
