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
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class SysAutDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SysAut";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
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
                    OpcaoProduto.DESCONTINUADO,
                    OpcaoProduto.ATACADO
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	sequencia as id,\n"
                    + "	fantasia as nome,\n"
                    + "	cnpj\n"
                    + "from Config"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("id"),
                            rst.getString("nome") + " - " + rst.getString("cnpj")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	a.cdAliquota as id,\n"
                    + "	a.dsAliquota as descricao,\n"
                    + "	c.dsCST as cst,\n"
                    + "	a.pICMS as aliquota,\n"
                    + "	0 as reducao\n"
                    + "from tbAliquotasECF a\n"
                    + "join tbCST as c on c.cdCST = a.cdCST\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	m1.codigo as merc1,\n"
                    + "	m1.descricao as desc_merc1,\n"
                    + "	m2.codigo as merc2,\n"
                    + "	m2.descricao as desc_merc2\n"
                    + "from Produtos p\n"
                    + "join Grupo m1 on m1.codigo = p.grupo\n"
                    + "join Secoes m2 on m2.codigo = p.secao\n"
                    + "order by 1, 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.codigo as id,\n"
                    + "	ean.dsBarras as ean,\n"
                    + "	p.referencia,\n"
                    + "	p.Balanca as balanca,\n"
                    + "	p.descricao as descricaocompleta,\n"
                    + "	p.descricao2 as descricaoreduzida,\n"
                    + "	p.unidade as tipoembalagem,\n"
                    + "	p.nuQtdeEmbalagem as qtdembalagem,\n"
                    + "	p.datacad as datacadastro,\n"
                    + "	p.dtAlteracao as dataalteracao,\n"
                    + "	p.grupo as merc1,\n"
                    + "	p.secao as merc2,"
                    + "	p.cdColecao,\n"
                    + "	p.peso as pesobruto,\n"
                    + "	p.PesoLiquido as pesoliquido,\n"
                    + "	tp.dsIDTipoProduto as tipoproduto,\n"
                    + "	p.estoque,\n"
                    + "	p.custo,\n"
                    + "	p.vlCustoUnitarioCompra as custocompra,\n"
                    + "	p.vlCustoMedio as customedio,\n"
                    + "	p.venda as precovenda,\n"
                    + "	p.vlPVendaCusto as margem,\n"
                    + "	p.vlPVendaCustoMedio as margemmedia,\n"
                    + "	p.ativo as situacaocadastro,\n"
                    + "	p.cdCSTPisCofins_Entrada as cst_piscofins_entrada,\n"
                    + "	p.cdCSTPisCofins_Saida as cst_piscofins_saida,\n"
                    + "	ncm.dsNBM as ncm,\n"
                    + "	cest.cdCEST as cest,\n"
                    + "	p.cdAliquota as idIcms\n"
                    + "from Produtos p\n"
                    + "left join tbProdutoBarras ean on ean.cdProduto = p.codigo\n"
                    + "left join tbCEST cest on cest.idCEST = p.cdCEST\n"
                    + "left join tbClasseFiscal ncm on ncm.cdClasseFiscal = p.nuFiscalClasse\n"
                    + "left join tbTipoProduto tp on tp.cdTipoProduto = p.cdTipoProduto \n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca("S".equals(rst.getString("balanca")));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setTipoProduto(rst.getInt("tipoproduto"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_piscofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_piscofins_entrada"));
                    imp.setIcmsDebitoId(rst.getString("idIcms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idIcms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idIcms"));
                    imp.setIcmsCreditoId(rst.getString("idIcms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idIcms"));
                    imp.setIcmsConsumidorId(rst.getString("idIcms"));
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
                    + "	f.codigo as id,\n"
                    + "	f.razao,\n"
                    + "	f.fantasia,\n"
                    + "	f.contato,\n"
                    + "	f.cgc as cnpj,\n"
                    + "	f.insc as ie,\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.bairro,\n"
                    + "	f.cidade as municipio,\n"
                    + "	c.dsCidade as municipio2,\n"
                    + "	f.uf,\n"
                    + "	uf.dsUF as uf2,\n"
                    + "	f.cep,\n"
                    + "	f.fone as tefefone,\n"
                    + "	f.fone2 as telefone2,\n"
                    + "	f.celular,\n"
                    + "	f.fax,\n"
                    + "	f.email,\n"
                    + "	f.homepage,\n"
                    + "	f.banco,\n"
                    + "	f.agencia,\n"
                    + "	f.conta,\n"
                    + "	f.datacad as datacadastro,\n"
                    + "	f.ativo as situacaodastro,\n"
                    + "	f.idProdutorRural as produtorrural\n"
                    + "from Fornecedor f \n"
                    + "left join tbCidade c on c.cdCidade = f.cdCidade\n"
                    + "left join tbUF uf on uf.cdUF = f.cdUF \n"
                    + "order by 1"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.codigo as id,\n"
                    + "	c.nome as razao,\n"
                    + "	c.nome2 as fantasia,\n"
                    + "	c.endereco,\n"
                    + "	c.Numero as numero,\n"
                    + "	c.Compl as complemento,\n"
                    + "	cid.dsCidade as municipio,\n"
                    + "	uf.dsUF as uf,\n"
                    + "	c.fone as telefone1,\n"
                    + "	c.fone2 as telefone2,\n"
                    + "	c.fax,\n"
                    + "	c.nasc as datanascimento,\n"
                    + "	c.data as datacadastro,\n"
                    + "	c.cpf as cnpj,\n"
                    + "	c.rg as inscricaoestadual,\n"
                    + "	c.orgao as orgaoemissor,\n"
                    + "	c.ufOrgao,\n"
                    + "	c.ativo as situacaocadastro,\n"
                    + "	c.email,\n"
                    + "	c.Pai as nomepai,\n"
                    + "	c.Mae as nomemae,\n"
                    + "	c.Conjuge as nomeconjuge,\n"
                    + "	c.Civil as estadocivil,\n"
                    + "	c.Contato as contato,\n"
                    + "	c.permitirVendaPrazo as permitecreditorotativo,\n"
                    + "	c.obs as observacao,\n"
                    + "	c.nuDiasConsultaCredito\n"
                    + "from Cliente c\n"
                    + "left join tbCidade cid on cid.cdCidade = c.cidade\n"
                    + "left join tbUF uf on uf.cdUF = c.uf \n"
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
                    "select \n"
                    + "	r.sequencia as id,\n"
                    + "	r.codcli as idcliente,\n"
                    + "	r.valor,\n"
                    + "	r.dataLancamento as emissao,\n"
                    + "	r.vencimento as vencimento,\n"
                    + "	r.parcela,\n"
                    + "	r.nvenda as cupom,\n"
                    + "	r.historico as observacao\n"
                    + "from Receber r\n"
                    + "where pagamento is null"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
