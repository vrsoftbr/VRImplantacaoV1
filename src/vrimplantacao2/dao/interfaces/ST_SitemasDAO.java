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
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class ST_SitemasDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ST Sitemas";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	t.GRFATURA as id,\n"
                    + "	t.DESCRICAO as descricao,\n"
                    + "	t.CODTRIBUTA as cst,\n"
                    + "	t.ICMS as icms,\n"
                    + "	t.REDUCAOBAS as reducao\n"
                    + "from GRFATURA t \n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.PRODUTOS_BALANCA,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.DESCONTINUADO
                }
        ));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.ITEM as id,\n"
                    + "	p.CODBARRAS as ean,\n"
                    + "	case p.UsaBalanca when 'S' then 1 else 0 end balanca,\n"
                    + "	CONCAT(p.DESCRICAO, ' ', p.EMBALAGEM)  as descricaocompleta,\n"
                    + "	case p.EXCLUIDO when 'S' then 0 else 1 end situacaocadastro,\n"
                    + "	p.UNIDADE as tipoembalagem,\n"
                    + "	p.PESOBRUTO as pesobruto,\n"
                    + "	p.PESOLIQUIDO as pesoliquido,\n"
                    + "	g.SETOR as mercadologico1,\n"
                    + "	g.GRUPO as mercadologico2,\n"
                    + "	'1' as mercadologico3,\n"
                    + "	g.DESCRICAO as descricaomercadologico,\n"
                    + " p.ESTMIN as estoqueminimo, \n"
                    + " p.ESTMAX as estoquemaximo, \n"
                    + "	e.ULTESTOQUE as estoque,\n"
                    + "	e.MargemPrecifica as margem,\n"
                    + "	p.PRCVENDA as precovenda,\n"
                    + "	p.CUSTOCD as custocomimposto,\n"
                    + "	p.CUSTOSD as custosemimposto,\n"
                    + "	p.COD_NCM as ncm,\n"
                    + "	p.cest as cest,\n"
                    + "	p.PisCofins,\n"
                    + "	p.GRFATURA as icms_id\n"
                    + "from ITENS p\n"
                    + "left join ESTOQUE e on e.ITEM = p.ITEM\n"
                    + "	and e.LOCAL = 2\n"
                    + "left join GRUPOS g on g.GRUPO = p.GRUPO\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsDebitoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms_id"));
                    imp.setIcmsCreditoId(rst.getString("icms_id"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("icms_id"));
                    imp.setIcmsConsumidorId(rst.getString("icms_id"));

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
                    "select \n"
                    + "	f.FORNEC as id,\n"
                    + "	f.CGC as cnpj,\n"
                    + "	f.INSCEST as inscricaoestadual,\n"
                    + "	f.INSCMUN as inscricaomunicipal,\n"
                    + "	f.RAZAO as razaosocial,\n"
                    + "	f.FANTASIA as nomefantasia,\n"
                    + "	f.ENDERECO as endereco,\n"
                    + "	coalesce(f.Numero, '') as numero,\n"
                    + "	coalesce(f.Complemento, '') as complemento,\n"
                    + "	f.BAIRRO as bairro,\n"
                    + "	f.CIDADE as municipio,\n"
                    + "	f.COD_MUNI as municipio_ibge,\n"
                    + "	f.COD_UF as uf_ibge,\n"
                    + "	f.ESTADO as uf,\n"
                    + "	f.CEP as cep,\n"
                    + "	coalesce(f.DDD, '') as ddd,\n"
                    + "	f.TELEFONE1 as telefoneprincipal,\n"
                    + "	f.TELEFONE2 as telefone2,\n"
                    + "	coalesce(f.TELEFONE3, '') as telefone3,\n"
                    + "	f.Fax as fax,\n"
                    + "	f.FAX1 as fax1,\n"
                    + "	coalesce(f.FAX2, '') as fax2,\n"
                    + "	coalesce(f.EMail, '') as email,\n"
                    + "	f.CONTATO1 as contato\n"
                    + "from FORNEC f\n"
                    + "order by 1"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	FORNECEDOR as idfornecedor,\n"
                    + "	ITEM as idproduto,\n"
                    + "	ITEMFORNEC as codigoexterno,\n"
                    + "	ULTCOMPRA as dataalteracao,\n"
                    + "	coalesce(Quantidade, 1) as qtdembalagem,\n"
                    + "	ULTVALOR as custo\n"
                    + "from FORITEM\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setCustoTabela(rst.getDouble("custo"));
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
                    "select \n"
                    + "	c.CLIENTE as id,\n"
                    + "	c.RAZAO as razaosocial,\n"
                    + "	c.FANTASIA as nomefantasia,\n"
                    + "	c.CGCCPF as cnpj,\n"
                    + "	c.INSCESTADUAL as incricaoestadual,\n"
                    + "	c.INSCMUNICIPAL as inscricaomunicipal,\n"
                    + "	c.ENDERECO as endereco,\n"
                    + "	c.BAIRRO as bairro,\n"
                    + "	c.CIDADE as municipio,\n"
                    + "	c.UF as uf,\n"
                    + "	c.CEP as cep,\n"
                    + "	c.CREDITO_TICKET,\n"
                    + "	c.Motivo_Credito,\n"
                    + "	c.Bloqueado as bloqueado,\n"
                    + "	c.ObsCred1,\n"
                    + "	c.ObsCred2,\n"
                    + "	c.ObsCred3,\n"
                    + "	c.END_FIS_ENDERE as endereco_fis,\n"
                    + "	c.END_FIS_NUMERO as numero_fis,\n"
                    + "	c.END_FIS_COMPLE as complemento_fis,\n"
                    + "	c.END_FIS_BAIRRO as bairro_fis,\n"
                    + "	c.END_N_FIS_MUNI as municipio_fis,\n"
                    + "	c.END_C_FIS_MUNI as municipio_ibge_fis,\n"
                    + "	c.END_N_FIS_UF as uf_fis,\n"
                    + "	c.END_C_FIS_UF as uf_ibge_fis,\n"
                    + "	c.END_N_FIS_CEP as cep_fis,\n"
                    + "	c.TELEFONE as telefone,\n"
                    + "	c.FAX as fax,\n"
                    + "	c.CEL as celular,\n"
                    + "	c.EMAIL1 as email1,\n"
                    + "	c.EMAIL2 as email2,\n"
                    + "	c.MSN as msn,\n"
                    + "	c.SKYPE as skype,\n"
                    + "	c.VOIP as voip,\n"
                    + "	c.DATAANIVER as datanascimento,\n"
                    + "	c.ESTCIVIL as estadocivil,\n"
                    + "	c.PAI as nomepai,\n"
                    + "	c.MAE as nomemae,\n"
                    + "	c.NOME_CONJ as nomeconjuge,\n"
                    + "	c.END_FIS_FONE as telefone,\n"
                    + "	c.END_FIS_FAX as fax,\n"
                    + "	c.END_COB_ENDERE as endereco_cobranca,\n"
                    + "	c.END_COB_NUMERO as numero_cobranca,\n"
                    + "	c.END_COB_BAIRRO as bairro_cobranca,\n"
                    + "	c.END_N_COB_CEP as cep_cobranca,\n"
                    + "	c.END_COB_FONE as telefone_cobranca,\n"
                    + "	c.END_COB_FAX as fax_cobranca,\n"
                    + "	c.END_N_COB_MUNI as municipio_cobranca,\n"
                    + "	c.END_C_COB_MUNI as municipio_ibge_cobranca,\n"
                    + "	c.END_C_COB_UF as uf_ibge_cobranca,\n"
                    + "	c.END_N_COB_UF as uf_cobranca,\n"
                    + "	c.END_N_COB_CEP as cep_cobranca,\n"
                    + "	c.END_TRA_ENDERE as endereco_trabalho,\n"
                    + "	c.END_TRA_BAIRRO as bairro_trabalho,\n"
                    + "	c.END_TRA_NUMERO as numero_trabalho,\n"
                    + "	c.END_TRA_COMPLE as complemento_trabalho,\n"
                    + "	c.END_C_TRA_MUNI as municipio_ibge_trabalho,\n"
                    + "	c.END_N_TRA_MUNI as municio_trabalho,\n"
                    + "	c.END_C_TRA_UF as uf_ibge_trabalho,\n"
                    + "	c.END_N_TRA_UF as uf_trabalho,\n"
                    + "	c.END_N_TRA_CEP as cep_trabalho,\n"
                    + "	c.END_TRA_FONE as telefone_trabalho,\n"
                    + "	c.END_TRA_FAX as fax_trabalho,\n"
                    + "	c.END_TRA_CELULA as celular_trabalho,\n"
                    + "	c.PROFISSAO as cargo,\n"
                    + "	c.LIMCREDITO as valorlimite,\n"
                    + "	c.RENDA_BASICA as salario,\n"
                    + "	c.SITUACAO as situacao,\n"
                    + "	c.DATA_CADASTRO as datacadastro,\n"
                    + "	c.OBSERVACAO1 as obs1,\n"
                    + "	c.OBSERVACAO2 as obs2\n"
                    + "from CLIENTES c\n"
                    + "order by 1"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
"	r.SEQUENCIAL as id,\n" +
"	r.CLIENTE as idcliente,\n" +
"	r.NUMDOC as numerodocumento,\n" +
"	r.EMISSAO as dataemissao,\n" +
"	r.VENCTO as datavencimento,\n" +
"	r.VALOR as valor,\n" +
"	r.VALPAG as valor_pagar,\n" +
"	r.OBS as observacao\n" +
"from TITLREC r\n" +
"where r.DATAPAG is null"
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        return null;
    } 
}
