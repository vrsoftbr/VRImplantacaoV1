package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.enums.TipoVistaPrazo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Importacao
 */
public class UniplusDAO extends InterfaceDAO {

    private String complemento = "";
    private TabelaPreco tabelaPreco = TabelaPreco.TABELA_FORMACAO_PRECO_PRODUTO;

    public boolean DUN14Atacado = false;
    public boolean NewEan = false;
    public boolean ProdutoFornecedorNotas = false;
    public boolean usar_arquivoBalanca;
    public boolean temProdutoAssociado;
    public boolean produtosNaoAtualizados;
    public boolean outrasDespesas = false;

    public void setProdutosNaoAtualizados(boolean produtosNaoAtualizados) {
        this.produtosNaoAtualizados = produtosNaoAtualizados;
    }

    public void setTemProdutoAssociado(boolean temProdutoAssociado) {
        this.temProdutoAssociado = temProdutoAssociado;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento != null ? complemento.trim() : "";
    }

    @Override
    public String getSistema() {
        return "Uniplus" + ("".equals(this.complemento) ? "" : " - " + this.complemento);
    }

    public void setTabelaPreco(TabelaPreco tabelaPreco) {
        this.tabelaPreco = tabelaPreco == null ? TabelaPreco.TABELA_FORMACAO_PRECO_PRODUTO : tabelaPreco;
    }

    public TabelaPreco getTabelaPreco() {
        return tabelaPreco;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	id,\n"
                    + "	nome,\n"
                    + "	cnpj\n"
                    + "from \n"
                    + "	filial")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
            OpcaoProduto.VOLUME_QTD,
            OpcaoProduto.CUSTO,
            OpcaoProduto.CUSTO_COM_IMPOSTO,
            OpcaoProduto.CUSTO_SEM_IMPOSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.ICMS_CONSUMIDOR,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.FABRICANTE,
            OpcaoProduto.RECEITA,
            OpcaoProduto.ASSOCIADO
        }));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CELULAR,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DADOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.OUTRAS_RECEITAS,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.SEXO,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.BLOQUEADO));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    /*"with merc as (\n"
                    + "select\n"
                    + "	codigo,\n"
                    + "	trim(substring(rpad(codigo,30,' '),1,6)) merc1,\n"
                    + "	trim(substring(rpad(codigo,30,' '),7,6)) merc2,\n"
                    + "	trim(substring(rpad(codigo,30,' '),13,6)) merc3,\n"
                    + "	--trim(substring(rpad(codigo,30,' '),19,6)) merc4,\n"
                    + "	--trim(substring(rpad(codigo,30,' '),25,6)) merc5,\n"
                    + "	nome\n"
                    + "from\n"
                    + "	hierarquia\n"
                    + "order by\n"
                    + "	codigo\n"
                    + "),\n"
                    + "m1 as (select * from merc where merc2 = ''),\n"
                    + "m2 as (select * from merc where merc2 != '' and merc3 = ''),\n"
                    + "m3 as (select * from merc where merc3 != '')\n"
                    + "--m4 as (select * from merc where merc4 != '' and merc5 = ''),\n"
                    + "--m5 as (select * from merc where merc5 != '')\n"
                    + "--select * from m1\n"
                    + "select\n"
                    + "	m1.merc1,\n"
                    + "	m1.nome merc1_desc,\n"
                    + "	m2.merc2,\n"
                    + "	m2.nome merc2_desc,\n"
                    + "	m3.merc3,\n"
                    + "	m3.nome merc3_desc\n"
                    + "	--m4.merc4,\n"
                    + "	--m4.nome merc4_desc,\n"
                    + "	--m5.merc5,\n"
                    + "	--m5.nome merc5_desc\n"
                    + "from\n"
                    + "	m1\n"
                    + "	left join m2 on\n"
                    + "		m1.merc1 = m2.merc1\n"
                    + "	left join m3 on\n"
                    + "		m2.merc1 = m3.merc1 and\n"
                    + "		m2.merc2 = m3.merc2"*/
                    "with teste as (	\n"
                    + "select\n"
                    + "	codigo,\n"
                    + "	trim(substring(rpad(codigo,30,' '),1,6)) merc1,\n"
                    + "	trim(substring(rpad(codigo,30,' '),7,6)) merc2,\n"
                    + "	trim(substring(rpad(codigo,30,' '),13,6)) merc3,\n"
                    + "	trim(substring(rpad(codigo,30,' '),19,6)) merc4,\n"
                    + "	trim(substring(rpad(codigo,30,' '),25,6)) merc5,\n"
                    + "	nome\n"
                    + "from hierarquia\n"
                    + "order by codigo\n"
                    + ")\n"
                    + "select \n"
                    + " a.merc1,\n"
                    + " a.nome merc1_desc,\n"
                    + " b.merc2,\n"
                    + " b.nome merc2_desc,\n"
                    + " c.merc3,\n"
                    + " c.nome merc3_desc,\n"
                    + " d.merc4,\n"
                    + " d.nome merc4_desc\n"
                    + "from teste a\n"
                    + "left join teste b on b.merc1 = a.merc1\n"
                    + "    and length(b.codigo) = 12\n"
                    + "left join teste c on c.merc1 = b.merc1\n"
                    + "    and c.merc2 = b.merc2\n"
                    + "    and length(c.codigo) = 18\n"
                    + "left join teste d on d.merc1 = c.merc1\n"
                    + "    and d.merc2 = c.merc2\n"
                    + "    and d.merc3 = c.merc3\n"
                    + "    and length(d.codigo) = 24\n"
                    + "where \n"
                    + " length(a.codigo) = 6;"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("merc1_desc"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("merc2_desc"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("merc3_desc"));
                    imp.setMerc4ID(rs.getString("merc4"));
                    imp.setMerc4Descricao(rs.getString("merc4_desc"));
                    /*imp.setMerc5ID(rs.getString("merc5"));
                    imp.setMerc5Descricao(rs.getString("merc5_desc"));*/

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	 codigo codigofamilia,\n"
                    + "  nome descricao\n"
                    + "from \n"
                    + "	 familiaproduto"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigofamilia"));
                    imp.setDescricao(rst.getString("descricao"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {

            String sql = "with \n"
                    + "saldoestoque as (\n"
                    + "	select\n"
                    + "	distinct on (idfilial, idproduto,codigoproduto)\n"
                    + "		idfilial,\n"
                    + "		idproduto,\n"
                    + "		codigoproduto,\n"
                    + "		TO_CHAR(TO_TIMESTAMP(currenttimemillis / 1000), 'DD/MM/YYYY HH24:MI:SS') ultimaalteracao,\n"
                    + "		quantidade\n"
                    + "	from\n"
                    + "		saldoestoque\n"
                    + "	order by \n"
                    + "		idfilial,\n"
                    + "		idproduto,\n"
                    + "		codigoproduto,\n"
                    + "		currenttimemillis desc\n"
                    + ")\n"
//                    + "valores as (\n"
//                    + "select \n"
//                    + " id, \n"
//                    + " codigo, \n"
//                    + " nome, \n"
//                    + " inativo, \n"
//                    + " custo_sem_imposto, \n"
//                    + " custo_com_imposto,\n"
//                    + " preco_venda,\n"
//                    + " margem,\n"
//                    + " estoque\n"
//                    + "from lista_VR(" + getLojaOrigem() + ")\n"
//                    + ")\n"
                    + "select \n"
                    + "	p.id,\n"
                    + "	p.codigo, \n"
                    + " p.ean,"
                    //+ " case when p.pesavel = 1 then p.ean else coalesce(nullif(trim(p.ean),''), p.codigo) end ean, \n"
                    + "	p.inativo, \n"
                    + "	p.diasvencimento as validade,\n"
                    + "	p.nome as descricaocompleta, \n"
                    + "	p.nomeecf as descricaoreduzida, \n"
                    + "	p.nome as descricaogondola, \n"
                    + "	p.datacadastro, \n"
                    + "	p.unidademedida as unidade, \n"
                    + "	1 qtdembalagem, \n"
                    + "	p.custoindireto custooperacional,\n"
                    + "	p.lucrobruto margemlucro,\n"
                    + "	fp.codigo codigofamilia,\n"
                    + "	p.percentualmarkupajustado margem,\n"
                    + "	pr1.precoultimacompra csimp,\n"
                    + "	pr1.precocusto ccimp,\n"
                    + "	p.precocusto precoproduto,\n"
                    //+ "	busca_custo_produto(p.id, 1, now()::timestamp) custosemimposto,\n"
                    + "	trunc(round(((p.aliquotapis + p.aliquotacofins) / 100 * p.preco) + \n"
                    + "    busca_custo_produto(p.id, " + getLojaOrigem() + ", now()::timestamp), 2), 2) custocomimposto,\n"
                    + "	pr1.preco p1,\n"
                    + "	pr2.preco p2,\n"
                    + "	p.preco pbkp,\n"
                    + "	coalesce(pr1.preco, pr2.preco, p.preco) as precovenda1,\n"
                    + "	coalesce(pr2.preco, pr1.preco, p.preco) as precovenda2,\n"
                    + "	p.quantidademinima, \n"
                    + "	p.quantidademaxima, \n"
                    + "	e.quantidade, \n"
                    + "	p.tributacao, \n"
                    + "	p.situacaotributaria as cst, \n"
                    + " case when \n"
                    + " p.tributacaoespecialnfcesat is null then '00'\n"
                    + " when p.tributacaoespecialnfcesat = '' then '00'\n"
                    + " else p.tributacaoespecialnfcesat end cst_consumidor,\n"
                    + "	p.cstpis, \n"
                    + "	p.cstcofins, \n"
                    + "	p.cstpisentrada, \n"
                    + "	p.icmsentrada as icmscredito, \n"
                    + "	p.icmssaida as icmsdebito, \n"
                    + "	p.aliquotaicmsinterna, \n"
                    + "	p.pesavel, \n"
                    + "	p.ncm, \n"
                    + "	p.idcest, \n"
                    + "	cest.codigo as cest, \n"
                    + "	p.cstpisentrada, \n"
                    + "	p.cstpis, \n"
                    + "	p.idfamilia, \n"
                    + " coalesce(prc.percentuallucromargem,prc2.percentuallucromargem) margem_atual,\n"
                    + "	coalesce(prc.customercadoriavendida,prc2.customercadoriavendida) custo_atual,\n"
                    + "	coalesce(prc.custo,prc2.custo) custosemimposto,\n"
                    + "	coalesce(prc.preco, prc2.preco) preco_atual,\n"
                    /*+ " p.idhierarquia merc1,\n"
                    + " p.idhierarquia merc2,\n"
                    + " p.idhierarquia merc3,\n"*/
                    + "	trim(substring(rpad(merc.codigo,30,' '),1,6)) merc1,\n"
                    + "	trim(substring(rpad(merc.codigo,30,' '),7,6)) merc2,\n"
                    + "	trim(substring(rpad(merc.codigo,30,' '),13,6)) merc3,\n"
                    + "	trim(substring(rpad(merc.codigo,30,' '),19,6)) merc4,\n"
                    //+ "	trim(substring(rpad(merc.codigo,30,' '),25,6)) merc5,\n"
                    + "	r.codigo naturezareceita,\n"
                    + " en.codigo fornecedor\n"
//                    + " v.custo_sem_imposto, \n"
//                    + " v.custo_com_imposto,\n"
//                    + " v.preco_venda,\n"
//                    + " v.margem,\n"
//                    + " v.estoque\n"
                    + "from \n"
                    + "	produto p\n"
                    + "	join filial f on\n"
                    + "		f.id = " + getLojaOrigem() + "\n"
                    //+ " left join precoproduto prc on prc.id = p.idultimoprecoprodutoaplicado \n"
                    + " left join custoproduto c on p.id = c.idproduto \n"
                    //+ "		and p.dataultimacomposicaopreco::varchar = substring(c.datahora::varchar,1,10) \n"
                    + "		and c.status = 2 and c.idfilial = " + getLojaOrigem() + "\n"
                    + "	left join precoproduto prc on prc.idproduto = p.id and prc.idcusto = c.id \n"
                    + "	left join formacaoprecoproduto pr1 on\n"
                    + "		pr1.idproduto = p.id and\n"
                    + "		pr1.idfilial = f.id\n"
                    + "	left join preco pr2 on\n"
                    + "		pr2.produto = p.codigo and\n"
                    + "		pr2.filial = f.codigo \n"
                    + " left join precoproduto prc2 on prc2.id = pr1.idultimoprecoprodutoaplicado\n"
                    + "	left join saldoestoque e on\n"
                    + "		e.idproduto = p.id and\n"
                    + "		e.codigoproduto = p.codigo and\n"
                    + "		e.idfilial = f.id\n"
                    + "	left join cest on\n"
                    + "		cest.id = p.idcest\n"
                    + "	left join receitasemcontribuicao r on\n"
                    + "		p.idreceitasemcontribuicao = r.id\n"
                    + "	left join familiaproduto fp on\n"
                    + "	        p.idfamilia = fp.id \n"//replace(fp.codigo, ' ', '')::integer\n"
                    + "    left join hierarquia merc on\n"
                    + "    	p.idhierarquia = merc.id\n"
                    + " left join entidade en on \n"
                    + "    	p.idfornecedor = en.id\n"
//                    + " left join valores v on v.id = p.id\n"
                    + "order by \n"
                    + "	c.id asc";

            /*if (produtosNaoAtualizados) {
                sql = "with \n"
                        + "saldoestoque as (\n"
                        + "	select\n"
                        + "	distinct on (idfilial, idproduto,codigoproduto)\n"
                        + "		idfilial,\n"
                        + "		idproduto,\n"
                        + "		codigoproduto,\n"
                        + "		TO_CHAR(TO_TIMESTAMP(currenttimemillis / 1000), 'DD/MM/YYYY HH24:MI:SS') ultimaalteracao,\n"
                        + "		quantidade\n"
                        + "	from\n"
                        + "		saldoestoque\n"
                        + "	order by \n"
                        + "		idfilial,\n"
                        + "		idproduto,\n"
                        + "		codigoproduto,\n"
                        + "		currenttimemillis desc\n"
                        + ")\n"
                        + "select \n"
                        + "	p.id,\n"
                        + "	p.codigo, \n"
                        + "    p.ean,\n"
                        + "	p.inativo, \n"
                        + "	p.diasvencimento as validade,\n"
                        + "	p.nome as descricaocompleta, \n"
                        + "	p.nomeecf as descricaoreduzida, \n"
                        + "	p.nome as descricaogondola, \n"
                        + "	p.datacadastro, \n"
                        + "	p.unidademedida as unidade, \n"
                        + "	1 qtdembalagem, \n"
                        + "	p.custoindireto custooperacional,\n"
                        + "	p.lucrobruto margemlucro,\n"
                        + "	fp.codigo codigofamilia,\n"
                        + "	p.percentualmarkupajustado margem,\n"
                        + "	pr1.precoultimacompra csimp,\n"
                        + "	pr1.precocusto ccimp,\n"
                        + "	p.precocusto precoproduto,\n"
                        + "	pr1.preco p1,\n"
                        + "	pr2.preco p2,\n"
                        + "	p.preco pbkp,\n"
                        + "	coalesce(pr1.preco, pr2.preco, p.preco) as precovenda1,\n"
                        + "	coalesce(pr2.preco, pr1.preco, p.preco) as precovenda2,\n"
                        + "	p.quantidademinima, \n"
                        + "	p.quantidademaxima, \n"
                        + "	e.quantidade, \n"
                        + "	p.tributacao, \n"
                        + "	p.situacaotributaria as cst, \n"
                        + " 	case when \n"
                        + " 	p.tributacaoespecialnfcesat is null then '00'\n"
                        + " 	when p.tributacaoespecialnfcesat = '' then '00'\n"
                        + " 	else p.tributacaoespecialnfcesat end cst_consumidor,\n"
                        + "	p.cstpis, \n"
                        + "	p.cstcofins, \n"
                        + "	p.cstpisentrada, \n"
                        + "	p.icmsentrada as icmscredito, \n"
                        + "	p.icmssaida as icmsdebito, \n"
                        + "	p.aliquotaicmsinterna, \n"
                        + "	p.pesavel, \n"
                        + "	p.ncm, \n"
                        + "	p.idcest, \n"
                        + "	cest.codigo as cest, \n"
                        + "	p.cstpisentrada, \n"
                        + "	p.cstpis, \n"
                        + "	p.idfamilia, \n"
                        + " 	p.diasvencimento validade,\n"
                        + " 	prc3.percentuallucromargem margem_atual,\n"
                        + "	prc3.customercadoriavendida custo_atual,\n"
                        + "	prc3.custo custosemimposto,\n"
                        + "	prc3.preco preco_atual,\n"
                        + "	trim(substring(rpad(merc.codigo,30,' '),1,6)) merc1,\n"
                        + "	trim(substring(rpad(merc.codigo,30,' '),7,6)) merc2,\n"
                        + "	trim(substring(rpad(merc.codigo,30,' '),13,6)) merc3,\n"
                        + "	trim(substring(rpad(merc.codigo,30,' '),19,6)) merc4,\n"
                        + "	r.codigo naturezareceita,\n"
                        + " 	en.codigo fornecedor\n"
                        + "from \n"
                        + "	produto p\n"
                        + "	join filial f on f.id = " + getLojaOrigem() + "\n"
                        + " 	left join precoproduto prc3 on prc3.idproduto = p.id\n"
                        + "	left join formacaoprecoproduto pr1 on pr1.idproduto = p.id and\n"
                        + "		pr1.idfilial = f.id\n"
                        + "	left join preco pr2 on pr2.produto = p.codigo and\n"
                        + "		pr2.filial = f.codigo \n"
                        + "	left join saldoestoque e on	e.idproduto = p.id and\n"
                        + "		e.codigoproduto = p.codigo and	e.idfilial = f.id\n"
                        + "	left join cest on cest.id = p.idcest\n"
                        + "	left join receitasemcontribuicao r on p.idreceitasemcontribuicao = r.id\n"
                        + "	left join familiaproduto fp on p.idfamilia = fp.id \n"
                        + "    left join hierarquia merc on p.idhierarquia = merc.id\n"
                        + " 	left join entidade en on p.idfornecedor = en.id\n"
                        + " 	where p.id not in (\n"
                        + "					 	select \n"
                        + "							p.id\n"
                        + "						from produto p\n"
                        + "						join filial f on f.id = " + getLojaOrigem() + "\n"
                        + "					 	left join custoproduto c on p.id = c.idproduto \n"
                        + "							and c.status = 2 and c.idfilial = " + getLojaOrigem() + "\n"
                        + "						left join precoproduto prc on prc.idproduto = p.id and prc.idcusto = c.id \n"
                        + "					 	where prc.preco is not null\n"
                        + "							order by c.id asc\n"
                        + "							)\n"
                        + "		order by  prc3.datahora  asc";

            }*/
            if (temProdutoAssociado) {
                sql = "select \n"
                        + "	e.id,\n"
                        + "	e.codigoproduto, \n"
                        + " 	e.codigobarras codigo,\n"
                        + " 	e.codigobarras ean,\n"
                        + "	e.inativo, \n"
                        + "	p.diasvencimento as validade,\n"
                        + "	p.nome||' '||e.descricao as descricaocompleta, \n"
                        + "	p.nomeecf||' '||e.descricao as descricaoreduzida, \n"
                        + "	p.nome||' '||e.descricao as descricaogondola, \n"
                        + "	p.datacadastro, \n"
                        + "	u.codigo as unidade, \n"
                        + "	1 qtdembalagem, \n"
                        + "	p.custoindireto custooperacional,\n"
                        + "	p.lucrobruto margemlucro,\n"
                        + "	fp.codigo codigofamilia,\n"
                        + "	p.percentualmarkupajustado margem,\n"
                        + "	p.precocusto precoproduto,\n"
                        + "	p.quantidademinima, \n"
                        + "	p.quantidademaxima, \n"
                        + "	0 quantidade, \n"
                        + "	p.tributacao, \n"
                        + "	p.situacaotributaria as cst, \n"
                        + "	case when p.tributacaoespecialnfcesat is null then '00'\n"
                        + " 		 when p.tributacaoespecialnfcesat = '' then '00'\n"
                        + " 		else p.tributacaoespecialnfcesat end cst_consumidor,\n"
                        + "	p.cstpis, \n"
                        + "	p.cstcofins, \n"
                        + "	p.cstpisentrada, \n"
                        + "	p.icmsentrada as icmscredito, \n"
                        + "	p.icmssaida as icmsdebito, \n"
                        + "	p.aliquotaicmsinterna, \n"
                        + "	p.pesavel, \n"
                        + "	p.ncm, \n"
                        + "	p.idcest, \n"
                        + "	cest.codigo as cest, \n"
                        + "	p.cstpisentrada, \n"
                        + "	p.cstpis, \n"
                        + "	p.idfamilia, \n"
                        + " 	prc.percentuallucromargem margem_atual,\n"
                        + " 	prc.customercadoriavendida custo_atual,\n"
                        + " 	prc.custo custosemimposto,\n"
                        + " 	prc.preco preco_atual,\n"
                        + " 	prc.preco precovenda1,\n"
                        + "	trim(substring(rpad(merc.codigo,30,' '),1,6)) merc1,\n"
                        + "	trim(substring(rpad(merc.codigo,30,' '),7,6)) merc2,\n"
                        + "	trim(substring(rpad(merc.codigo,30,' '),13,6)) merc3,\n"
                        + "	trim(substring(rpad(merc.codigo,30,' '),19,6)) merc4,\n"
                        + "	r.codigo naturezareceita,\n"
                        + " 	en.codigo fornecedor\n"
                        + "from \n"
                        + "	produto p\n"
                        + "	join filial f on f.id =  " + getLojaOrigem() + "\n"
                        + "	join embalagem e on e.idproduto = p.id and e.exportarbalanca = 1\n"
                        + "	join unidademedida u on u.id = e.idunidademedida \n"
                        + "	join precoprodutoderivado prc on prc.ean = e.codigobarras\n"
                        + " left join cest on cest.id = p.idcest\n"
                        + " left join receitasemcontribuicao r on p.idreceitasemcontribuicao = r.id\n"
                        + " left join familiaproduto fp on p.idfamilia = fp.id \n"
                        + " left join hierarquia merc on p.idhierarquia = merc.id\n"
                        + " left join entidade en on p.idfornecedor = en.id\n"
                        + " order by \n"
                        + "	prc.id";
            }

            try (ResultSet rs = stm.executeQuery(sql)) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();

                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    //imp.setEan(rs.getString("ean").substring(0, 14));
                    if (rs.getString("ean").length() > 1 && rs.getString("ean").length() < 14) {
                        imp.setEan(rs.getString("ean").substring(0, rs.getString("ean").length()));
                    } else {
                        imp.setEan(rs.getString("ean"));
                    }

                    imp.setIdFamiliaProduto(rs.getString("codigofamilia"));

                    imp.setSituacaoCadastro(rs.getInt("inativo") == 1
                            ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);

                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));

                    if (usar_arquivoBalanca) {
                        ProdutoBalancaVO bal = null;
                        if (imp.getEan() != null && !imp.getEan().isEmpty()) {
                            bal = balanca.get(Utils.stringToInt(imp.getEan(), -2));
                        } else {
                            bal = balanca.get(Utils.stringToInt(rs.getString("codigo"), -2));
                        }

                        if (bal != null) {
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                            imp.setValidade(bal.getValidade());
                            imp.setTipoEmbalagemCotacao(imp.getTipoEmbalagem());
                            imp.setTipoEmbalagemVolume(imp.getTipoEmbalagem());

                        } else {
                            imp.setTipoEmbalagem(rs.getString("unidade"));
                            imp.setTipoEmbalagemCotacao(rs.getString("unidade"));
                            imp.setTipoEmbalagemVolume(rs.getString("unidade"));
                            imp.setValidade(rs.getInt("validade"));

                            imp.seteBalanca(false);
                        }
                    }

                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValidade(rs.getInt("validade"));

                    if (priorizarPrecoDaTabelaFormacaoPrecoProduto()) {
                        imp.setPrecovenda(rs.getDouble("precovenda1"));
                    } else {
                        imp.setPrecovenda(rs.getDouble("preco_venda"));
                    }

//                    if (temProdutoAssociado) {
//                        imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
//                        imp.setCustoComImposto(rs.getDouble("custo_atual"));
//                        imp.setPrecovenda(rs.getDouble("preco_atual"));
//                        imp.setMargem(rs.getDouble("margem_atual"));
//                    } else {
//                        imp.setCustoSemImposto(rs.getDouble("custo_sem_imposto"));
//                        imp.setCustoComImposto(rs.getDouble("custo_com_imposto"));
//                        imp.setMargem(rs.getDouble("margem"));
//                    }

                    imp.setEstoqueMinimo(rs.getDouble("quantidademinima"));
                    imp.setEstoqueMaximo(rs.getDouble("quantidademaxima"));
                    imp.setEstoque(rs.getDouble("quantidade"));

                    imp.setIcmsCst(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("aliquotaicmsinterna"));
                    //imp.setIcmsAliqEntrada(rs.getDouble("icmscredito"));
                    imp.setIcmsAliqEntrada(rs.getDouble("aliquotaicmsinterna"));
                    imp.setIcmsAliqSaidaForaEstado(rs.getDouble("aliquotaicmsinterna"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rs.getDouble("aliquotaicmsinterna"));

                    imp.setIcmsAliqConsumidor(rs.getDouble("aliquotaicmsinterna"));
                    imp.setIcmsCstConsumidor(rs.getInt("cst_consumidor"));
                    imp.setIcmsReducaoConsumidor(0);

                    imp.setPiscofinsCstCredito(rs.getString("cstpisentrada"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    //imp.setCodMercadologico5(rs.getString("merc5"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    //imp.setFornecedorFabricante(rs.getString("fornecedor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private boolean priorizarPrecoDaTabelaFormacaoPrecoProduto() {
        return tabelaPreco == TabelaPreco.TABELA_FORMACAO_PRECO_PRODUTO;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {

        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	codigo,\n"
                        + "	quantidadepauta1,\n"
                        + "	precopauta1,\n"
                        + "	preco,\n"
                        + "	unidademedida\n"
                        + "from produto\n"
                        + "where\n"
                        + "	quantidadepauta1 > 0\n"
                        + "	and length(ean) > 6\n"
                        + "	and length(ean) < 14\n"
                        + "order by \n"
                        + "	codigo"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo"));
                        imp.setEan(
                                String.format(
                                        "555%06d",
                                        Utils.stringToInt(rst.getString("codigo"))
                                )
                        );
                        imp.setQtdEmbalagem(rst.getInt("quantidadepauta1"));
                        imp.setAtacadoPreco(rst.getDouble("precopauta1"));
                        imp.setPrecovenda(rst.getDouble("preco"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  codigobarras id_pai,\n"
                    + "  codigoproduto id_filho,\n"
                    + "  fatorconversao qtde\n"
                    + " from embalagem \n"
                    + " where exportarbalanca = 1"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setImpIdProduto(rst.getString("id_pai"));
                    imp.setQtdEmbalagem(rst.getInt("qtde"));
                    imp.setImpIdProdutoItem(rst.getString("id_filho"));
                    imp.setQtdEmbalagemItem(rst.getInt("qtde"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct \n"
                    + "	f.codigo idfornecedor,\n"
                    + "	f.nome,\n"
                    + "	nfi.produto idproduto,	\n"
                    + "	nfi.referenciafornecedor referenciafornecedor\n"
                    + "from \n"
                    + "	notafiscalitem nfi\n"
                    + "	join notafiscal nf on nf.id = nfi.idnotafiscal\n"
                    + "	join entidade f	on f.id = nf.identidade\n"
                    + "where\n"
                    + "	nfi.referenciafornecedor != ''\n"
                    + "order by\n"
                    + "	f.codigo, nfi.produto")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.getImportLoja();
                    imp.getImportSistema();
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("referenciafornecedor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }*/
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        if (ProdutoFornecedorNotas == true) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select distinct \n"
                        + "	f.codigo idfornecedor,\n"
                        + "	f.nome,\n"
                        + "	nfi.produto idproduto,	\n"
                        + "     e.fatorconversao as qtdembalagem,\n"
                        + "	nfi.referenciafornecedor referenciafornecedor\n"
                        + "from \n"
                        + "	notafiscalitem nfi\n"
                        + "	join notafiscal nf on nf.id = nfi.idnotafiscal\n"
                        + "	join entidade f	on f.id = nf.identidade\n"
                        + "	join embalagem e on e.id = nfi.idembalagem\n"
                        + "where\n"
                        + "	nfi.referenciafornecedor != ''\n"
                        + "	and\n"
                        + "	f.fornecedor = 1\n"
                        + "order by\n"
                        + "	f.codigo, nfi.produto"
                )) {
                    while (rs.next()) {
                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setIdFornecedor(rs.getString("idfornecedor"));
                        imp.setIdProduto(rs.getString("idproduto"));
                        imp.setCodigoExterno(rs.getString("referenciafornecedor"));
                        imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));

                        result.add(imp);
                    }
                }
            }
        } else {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "    p.codigo idproduto,\n"
                        + "    e.codigo idfornecedor,\n"
                        + "    u.codigo as unidade,\n"
                        + "    pf.fatorconversao qtdembalagem,\n"
                        + "    pf.referenciafornecedor referenciafornecedor\n"
                        + "from\n"
                        + "	produtofornecedor pf\n"
                        + "join produto p on p.id = pf.idproduto\n"
                        + "join entidade e on e.id = pf.idfornecedor \n"
                        + "join unidademedida u on u.id = pf.idunidademedida\n"
                        + "where\n"
                        + "	e.fornecedor = 1\n"
                        + "order by\n"
                        + "	pf.idproduto, pf.idfornecedor"
                )) {
                    while (rs.next()) {
                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setIdFornecedor(rs.getString("idfornecedor"));
                        imp.setIdProduto(rs.getString("idproduto"));
                        imp.setCodigoExterno(rs.getString("referenciafornecedor"));
                        imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));

                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (DUN14Atacado == true) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	codigo,\n"
                        + "	ean,\n"
                        + "	'1'||ean dun14,\n"
                        + "	quantidadepauta1,\n"
                        + "	precopauta1,\n"
                        + "     unidademedida\n"
                        + "from produto\n"
                        + "where\n"
                        + "	quantidadepauta1 > 0\n"
                        + "	and length(ean) > 6\n"
                        + "	and length(ean) < 14\n"
                        + "order by \n"
                        + "	codigo"
                )) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("codigo"));
                        imp.setEan(rs.getString("dun14"));
                        imp.setQtdEmbalagem(rs.getInt("quantidadepauta1"));
                        imp.setTipoEmbalagem(rs.getString("unidademedida"));

                        result.add(imp);
                    }
                }
                return result;
            }
        } else if (NewEan) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	codigo,\n"
                        + "	quantidadepauta1,\n"
                        + "	precopauta1,\n"
                        + "	 unidademedida\n"
                        + "from produto\n"
                        + "where\n"
                        + "	quantidadepauta1 > 0\n"
                        + "	and length(ean) > 6\n"
                        + "	and length(ean) < 14\n"
                        + "order by \n"
                        + "	codigo"
                )) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("codigo"));
                        imp.setEan(String.format("555%06d", Utils.stringToInt(rs.getString("codigo"))));
                        imp.setQtdEmbalagem(rs.getInt("quantidadepauta1"));
                        imp.setTipoEmbalagem(rs.getString("unidademedida"));

                        result.add(imp);
                    }
                }
                return result;
            }

        } else {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select \n"
                        + "	p.codigo idproduto,\n"
                        + "	ean.ean,\n"
                        + "	1 qtdembalagem \n"
                        + "from \n"
                        + "	produtoean ean\n"
                        + "join\n"
                        + "	produto p on p.id = ean.idproduto\n"
                        + "order by\n"
                        + "	idproduto")) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rs.getString("idproduto"));
                        if (rs.getString("ean") != null && !"".equals(rs.getString("ean"))) {
                            if (rs.getString("ean").length() > 14) {
                                imp.setEan(rs.getString("ean").substring(0, 14));
                            } else {
                                imp.setEan(rs.getString("ean"));
                            }
                        }
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

                        result.add(imp);
                    }
                }
                return result;
            }
        }
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	ft.id,\n"
                    + "	pr.codigo produtopai,\n"
                    + "	u.codigo unidade,\n"
                    + "	pr.nome,\n"
                    + "	prf.codigo produtofilho,\n"
                    + "	prf.nome filho,\n"
                    + "	ft.quantidade\n"
                    + "from \n"
                    + "	fichatecnicaproduto ft \n"
                    + "join produto pr on ft.idprodutopai = pr.id \n"
                    + "join produto prf on ft.idproduto = prf.id \n"
                    + "left join unidademedida u on ft.idunidademedida = u.id")) {
                while (rs.next()) {
                    ReceitaIMP imp = new ReceitaIMP();

                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportid(rs.getString("id"));
                    imp.setIdproduto(rs.getString("produtopai"));
                    imp.setDescricao(rs.getString("nome"));
                    imp.getProdutos().add(rs.getString("produtofilho"));
                    imp.setFator(rs.getDouble("quantidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	e.codigo,\n"
                    + "	e.nome,\n"
                    + "	e.razaosocial,\n"
                    + "	e.tipopessoa,\n"
                    + "	e.cnpjcpf,\n"
                    + "	case when e.inscricaoestadual = '' then e.rg else e.inscricaoestadual end,\n"
                    + "	e.rg,\n"
                    + "	e.endereco,\n"
                    + "	e.numeroendereco,\n"
                    + "	e.complemento,\n"
                    + "	e.bairro,\n"
                    + "	e.idestado,\n"
                    + "	est.nome as estado,\n"
                    + "	est.codigoibge as ibgeestado,\n"
                    + "	e.idcidade,\n"
                    + "	c.nome as municipio,\n"
                    + "	c.codigoibge as ibgemunicipio,\n"
                    + "	e.cep,\n"
                    + "	replace (e.telefone,'0xx','') telefone,\n"
                    + "	replace (e.celular,'0xx','') celular,\n"
                    + "	e.fax,\n"
                    + "	e.email,\n"
                    + "	e.nascimento,\n"
                    + "	e.limitecredito,\n"
                    + "	e.enderecoentrega,\n"
                    + "	e.numeroenderecoentrega,\n"
                    + "	e.complementoentrega,\n"
                    + "	e.bairroentrega,\n"
                    + "	e.idcidadeentrega,\n"
                    + "	e.cepentrega,\n"
                    + "	e.estadocivil,\n"
                    + "	e.datacadastro,\n"
                    + "	e.inativo\n"
                    + "from\n"
                    + "	entidade e\n"
                    + "left join cidade c on c.id = e.idcidade\n"
                    + "left join estado est on est.id = e.idestado\n"
                    + "where\n"
                    + "	e.fornecedor = " + getLojaOrigem() + "\n"
                    + " and e.fornecedor = 1 or e.transportadora = 1\n"
                    //+ "	or e.id in (select distinct identidade from financeiro where tipo = 'P')\n"
                    + "order by\n"
                    + "	e.codigo::bigint")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("razaosocial"));
                    imp.setCnpj_cpf(rs.getString("cnpjcpf"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numeroendereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("ibgemunicipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if ((rs.getString("celular") != null)
                            && (!"".equals(rs.getString("celular")))) {
                        imp.addContato("Celular", null, rs.getString("celular"), TipoContato.COMERCIAL, null);
                    }
                    if ((rs.getString("fax") != null)
                            && (!"".equals(rs.getString("fax")))) {
                        imp.addContato("Fax", rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    if ((rs.getString("email") != null)
                            && (!"".equals(rs.getString("email")))) {
                        imp.addContato("Email", null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo((rs.getInt("inativo") == 0));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	e.codigo,\n"
                    + "	e.nome,\n"
                    + "	e.razaosocial,\n"
                    + "	e.tipopessoa,\n"
                    + "	e.cnpjcpf,\n"
                    + "	case when e.inscricaoestadual = '' then e.rg else e.inscricaoestadual end inscricaoestadual,\n"
                    + "	e.rg,\n"
                    + "	e.endereco,\n"
                    + "	e.numeroendereco,\n"
                    + "	e.complemento,\n"
                    + "	e.bairro,\n"
                    + "	c.codigo municipioibge,\n"
                    + "	c.nome municipio,\n"
                    + "     es.codigoibge estadoibge,\n"
                    + "     es.codigo estado,\n"
                    + "	e.cep,\n"
                    + "	replace (e.telefone,'0xx','') telefone,\n"
                    + "	replace (e.celular,'0xx','') celular,\n"
                    + "	e.fax,\n"
                    + "	e.email,\n"
                    + "	e.nascimento,\n"
                    + "	e.limitecredito,\n"
                    + "	e.datacadastro,\n"
                    + "	e.inativo,\n"
                    + " case when e.sexo = 2 then 0 else 1 end sexo,\n"
                    + " e.creditorestrito bloqueado,\n"
                    + " e.estadocivil\n"
                    + "from \n"
                    + "	entidade e\n"
                    + "left join cidade c on c.id = e.idcidade\n"
                    + "left join estado es on c.idestado = es.id\n"
                    + "where\n"
                    //+ "	e.idfilialcadastro = " + getLojaOrigem() + "\n"
                    + " e.cliente = 1\n"
                    + "order by 1")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj(rs.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numeroendereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setMunicipioIBGE(rs.getInt("municipioibge"));
                    imp.setUf(rs.getString("estado"));
                    imp.setUfIBGE(rs.getInt("estadoibge"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setValorLimite(rs.getDouble("limitecredito"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setAtivo((rs.getInt("inativo") == 0));

                    int sexo = rs.getInt("sexo");

                    if (sexo == 0) {
                        imp.setSexo(TipoSexo.FEMININO);
                    }

                    int bloqueado = rs.getInt("bloqueado");
                    if (bloqueado == 1) {
                        imp.setBloqueado(true);
                    }

                    switch (rs.getInt("estadocivil")) {
                        case 6:
                            imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                            break;
                        case 1:
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                            break;
                        case 0:
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                            break;
                        case 4:
                            imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                            break;
                        case 5:
                            imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                            break;
                        case 2:
                            imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                            break;
                        default:
                            break;
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.id,\n"
                    + "	f.emissao,\n"
                    + "	f.documento cupom,\n"
                    + "	0 ecf,\n"
                    + "	f.valor,\n"
                    + "	f.saldo,\n"
                    + "	f.historico observacao,\n"
                    + "	e.codigo id_cliente,\n"
                    + "	f.vencimento,\n"
                    + "	f.parcela,\n"
                    + "	f.juros,\n"
                    + "	f.multa\n"
                    + "from\n"
                    + "	financeiro f\n"
                    + "	join entidade e on\n"
                    + "           f.identidade = e.id\n"
                    + "where\n"
                    + "	f.tipo = 'R'\n"
                    + "	and f.idfilial = " + getLojaOrigem() + "\n"
                    + "	and f.idtipodocumentofinanceiro in (1,8,100,115)\n"
                    + " and f.status = 'A'\n"
                    + "order by\n"
                    + "	f.id"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(complemento);
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("saldo"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setParcela(rst.getInt("parcela"));
//                    imp.setJuros(rst.getDouble("juros"));
//                    imp.setMulta(rst.getDouble("multa"));

                    //incluirLancamentos(imp);
                    result.add(imp);
                }
            }
        }

        return result;
    }

    /*private void incluirLancamentos(CreditoRotativoIMP imp) throws Exception {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	fl.id,\n"
                    + "	fl.valor,\n"
                    + "	fl.desconto,\n"
                    + "	fl.multa,\n"
                    + "	fl.baixa datapagamento,\n"
                    + "	fl.historico observacao\n"
                    + "from\n"
                    + "	financeirolancamento fl\n"
                    + "where\n"
                    + "	fl.idfinanceiro = " + imp.getId() + "\n"
                    + "order by\n"
                    + "	fl.id"
            )) {
                while (rst.next()) {
                    imp.addPagamento(
                            rst.getString("id"),
                            rst.getDouble("valor"),
                            rst.getDouble("desconto"),
                            rst.getDouble("multa"),
                            rst.getDate("datapagamento"),
                            rst.getString("observacao")
                    );
                }
            }
        }
    }*/
    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.id,\n"
                    + "	e.cnpjcpf cpf,\n"
                    + "	f.documento numerocheque,\n"
                    + "	regexp_replace(b.codigo,'[^0-9]','0','g') banco,\n"
                    + "	f.agencia,\n"
                    + "	f.numerocontacorrente,\n"
                    + "	f.numerocheque num,\n"
                    + "	f.emissao date,\n"
                    + "	f.baixa datadeposito,\n"
                    + "	f.vencimento,\n"
                    + "	0 ecf,\n"
                    + "	e.rg,\n"
                    + "	e.telefone,\n"
                    + "	e.nome,\n"
                    + "	f.historico observacao,\n"
                    + "	f.valor,\n"
                    + "	f.valorpagorecebido,\n"
                    + "	f.saldo,\n"
                    + "	f.juros,\n"
                    + "	f.pagamento,\n"
                    + " f.idtipodocumentofinanceiro tipo,\n"
                    + " f.idtipocobranca \n"
                    + "from\n"
                    + "	financeiro f\n"
                    + "	left join entidade e on f.identidade = e.id\n"
                    + "	left join banco b on f.idbanco = b.id\n"
                    + "where\n"
                    + "	f.tipo = 'R'\n"
                    + "	and f.idfilial = " + getLojaOrigem() + "\n"
                    + "	and f.idtipodocumentofinanceiro in (-2,5,6)\n"
                    + " and f.status = 'A' \n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("numerocontacorrente"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setDate(rst.getDate("date"));
                    imp.setDataDeposito(rst.getDate("vencimento"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao("NUM. CHEQUE: " + rst.getString("numerocheque") + "\r\n" + rst.getString("observacao"));
                    imp.setValor(rst.getDouble("saldo"));
                    imp.setValorJuros(rst.getDouble("juros"));

                    if (rst.getInt("idtipocobranca") > 0) {
                        imp.setAlinea(15);
                    }

                    if (rst.getInt("tipo") == 6) {
                        imp.setVistaPrazo(TipoVistaPrazo.PRAZO);
                    } else {
                        imp.setVistaPrazo(TipoVistaPrazo.A_VISTA);
                    }

                    /*if (rst.getString("pagamento") == null || rst.getString("pagamento").trim().equals("")) {
                        imp.setSituacaoCheque(SituacaoCheque.ABERTO);
                    } else {
                        imp.setSituacaoCheque(SituacaoCheque.BAIXADO);
                    }*/
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        String sql = "with contas as (\n"
                + "select\n"
                + "	f.id,\n"
                + "	e.codigo identidade,\n"
                + "	f.documento,\n"
                + "	f.emissao,\n"
                + "	f.entrada,\n"
                + "	f.historico observacao,\n"
                + "	doc.descricao tipodocumento,\n"
                + "	f.vencimento,\n"
                + "	f.valor,\n"
                + "	f.saldo,\n"
                + " f.parcela\n"
                + "from\n"
                + "	financeiro f\n"
                + "	join entidade e on\n"
                + "		f.identidade = e.id\n"
                + "	left join tipodocumentofinanceiro doc on\n"
                + "		f.idtipodocumentofinanceiro = doc.id\n"
                + "where\n"
                + "	f.tipo = 'P'\n"
                + "	and f.idfilial = " + getLojaOrigem() + "\n"
                + "	and (select sum(valor) from financeirolancamento where idfinanceiro = f.id) < f.valor\n"
                + " and f.status = 'A'\n"
                + "union\n"
                + "select\n"
                + "	f.id,\n"
                + "	e.codigo identidade,\n"
                + "	f.documento,\n"
                + "	f.emissao,\n"
                + "	f.entrada,\n"
                + "	f.historico observacao,\n"
                + "	doc.descricao tipodocumento,\n"
                + "	f.vencimento,\n"
                + "	f.valor,\n"
                + "	f.saldo,\n"
                + " f.parcela\n"
                + "from\n"
                + "	financeiro f\n"
                + "	join entidade e on\n"
                + "		f.identidade = e.id\n"
                + "	left join tipodocumentofinanceiro doc on\n"
                + "		f.idtipodocumentofinanceiro = doc.id\n"
                + "	left join financeirolancamento fi on fi.idfinanceiro = f.id \n"
                + "where\n"
                + "	f.tipo = 'P'\n"
                + "	and f.idfilial = " + getLojaOrigem() + "\n"
                + "	and fi.idfinanceiro is null\n"
                + " and f.status = 'A'\n"
                + ")\n"
                + "select * from contas ";

        if (outrasDespesas) {
            sql = "with contas as (\n"
                    + "select\n"
                    + "	f.id,\n"
                    + "	e.codigo identidade,\n"
                    + "	f.documento,\n"
                    + "	f.emissao,\n"
                    + "	f.entrada,\n"
                    + "	f.historico observacao,\n"
                    + "	doc.descricao tipodocumento,\n"
                    + "	f.vencimento,\n"
                    + "	f.valor,\n"
                    + "	f.saldo,\n"
                    + " f.parcela\n"
                    + "from\n"
                    + "	financeiro f\n"
                    + "	join entidade e on\n"
                    + "		f.identidade = e.id\n"
                    + "	left join tipodocumentofinanceiro doc on\n"
                    + "		f.idtipodocumentofinanceiro = doc.id\n"
                    + "where\n"
                    + "	f.tipo = 'P'\n"
                    + "	and f.idfilial = " + getLojaOrigem() + "\n"
                    + "	and (select sum(valor) from financeirolancamento where idfinanceiro = f.id) < f.valor\n"
                    + " and f.status = 'A'\n"
                    + "union\n"
                    + "select\n"
                    + "	f.id,\n"
                    + "	e.codigo identidade,\n"
                    + "	f.documento,\n"
                    + "	f.emissao,\n"
                    + "	f.entrada,\n"
                    + "	f.historico observacao,\n"
                    + "	doc.descricao tipodocumento,\n"
                    + "	f.vencimento,\n"
                    + "	f.valor,\n"
                    + "	f.saldo,\n"
                    + " f.parcela\n"
                    + "from\n"
                    + "	financeiro f\n"
                    + "	join entidade e on\n"
                    + "		f.identidade = e.id\n"
                    + "	left join tipodocumentofinanceiro doc on\n"
                    + "		f.idtipodocumentofinanceiro = doc.id\n"
                    + "	left join financeirolancamento fi on fi.idfinanceiro = f.id \n"
                    + "where\n"
                    + "	f.tipo = 'P'\n"
                    + "	and f.idfilial = " + getLojaOrigem() + "\n"
                    + "	and fi.idfinanceiro is null\n"
                    + " and f.status = 'A'\n"
                    + ")\n"
                    + "select * from contas where identidade in ('5840','5904','6224','6227') order by id";
        }

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                   sql
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("identidade"));

                    if (outrasDespesas) {
                        imp.setIdTipoEntradaVR(99);
                        imp.setNumeroDocumento(rst.getString("id"));
                    } else {
                        imp.setNumeroDocumento(rst.getString("documento"));
                    }

                    //imp.setIdTipoEntradaVR(210);
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("entrada"));
                    imp.setValor(rst.getDouble("saldo"));

                    //imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("saldo"), rst.getString("observacao"));
                    imp.addVencimento(
                            rst.getDate("vencimento"),
                            rst.getDouble("saldo"),
                            rst.getString("observacao"),
                            rst.getInt("parcela"));
                    imp.setObservacao(rst.getString("observacao"));

                    /*imp.setObservacao(
                            new StringBuilder(rst.getString("tipodocumento"))
                                    .append(rst.getDouble("saldo") > 0 ? " - Valor original RS" + rst.getDouble("valor") : "")
                                    .append(" - ")
                                    .append(rst.getString("observacao"))
                                    .toString()
                    );
                    imp.addVencimento(
                            rst.getDate("vencimento"),
                            (rst.getDouble("saldo") > 0 ? rst.getDouble("saldo") : rst.getDouble("valor"))
                    );*/
                    result.add(imp);
                }
            }
        }

        return result;
    }

    public static enum TabelaPreco {
        TABELA_FORMACAO_PRECO_PRODUTO,
        TABELA_PRECO;

        public static TabelaPreco getByOrdinal(int ordinal) {
            for (TabelaPreco preco : values()) {
                if (preco.ordinal() == ordinal) {
                    return preco;
                }
            }
            return null;
        }
    }

}
