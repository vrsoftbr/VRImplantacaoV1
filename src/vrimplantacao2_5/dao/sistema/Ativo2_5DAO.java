package vrimplantacao2_5.dao.sistema;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 *
 * @author Alan
 */
public class Ativo2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Ativo";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TROCA,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV, // Libera produto para Venda no PDV
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.RECEITA,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PDV_VENDA // Habilita importacão de Vendas
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.VENCIMENTO_ROTATIVO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigo as id,\n"
                    + "	situacao as descricao,\n"
                    + "	case \n"
                    + "	when codigo  = '06' then 40\n"
                    + "	when codigo = '03' then 0\n"
                    + "	when codigo = '15' then 40\n"
                    + "	when codigo = '20' then 60\n"
                    + "	when codigo = '21' then 60\n"
                    + "	when codigo = '01' then 60\n"
                    + "	end as cst,\n"
                    + "	case\n"
                    + "	when codigo  = '06' then 0\n"
                    + "	when codigo = '03' then 20\n"
                    + "	when codigo = '15' then 0\n"
                    + "	when codigo = '20' then 0\n"
                    + "	when codigo = '21' then 0\n"
                    + "	when codigo = '01' then 12	\n"
                    + "	end as aliq	,\n"
                    + "	0 as red\n"
                    + "from\n"
                    + "	situacaotrib t \n"
                    + "	where codigo in ('06','03','15','20','21','01')"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            rst.getDouble("red"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    g.item,\n"
                    + "    g.nome\n"
                    + "FROM\n"
                    + "    grupos g\n"
                    + "WHERE\n"
                    + "    g.nome != ''"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("item"));
                    imp.setMerc1Descricao(rst.getString("nome"));
                    imp.setMerc2ID(rst.getString("item"));
                    imp.setMerc2Descricao(rst.getString("nome"));
                    imp.setMerc3ID(rst.getString("item"));
                    imp.setMerc3Descricao(rst.getString("nome"));

                    result.add(imp);

                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    codigo AS id,\n"
                    + "    descricao\n"
                    + "FROM\n"
                    + "    familiaproduto"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString(""));
                    imp.setDescricao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "produto as id_produto,\n"
                    + "codbarras as ean,\n"
                    + "p2.quantcx as qtdembalagem\n"
                    + "from produtosbarras p \n"
                    + "left join produtos p2 on p.produto  = p2.codigo\n"
                    + "where p.tipovenda = '01'"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(1);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.codigo as id,\n"
                    + "p.descricao  as descricao,\n"
                    + "case when u.unidade = 'KG' then p.codigo else p2.codbarras end as ean,\n"
                    + "u.unidade tipoembalagem,\n"
                    + "case when u.unidade = 'KG' then '1' else p2.unidade end as qtdembalagem,\n"
                    + "case when p5.custoaqui = '0' then \n"
                    + "replace(p5.custopre,',','.')::numeric \n"
                    + "else replace(p5.custoaqui ,',','.')::numeric  end as custosemimposto,\n"
                    + "replace(p5.custopre ,',','.')::numeric as custocomimposto,\n"
                    + "p4.premaximo as precovenda,\n"
                    + "p.grupo::int mercadologico1,\n"
                    + "p.familia AS familiaproduto,\n"
                    + "case when p.situacao = 'D' then 0 else 1 end as ativo,\n"
                    + "p.estminimo as estoque,\n"
                    + "p.peso as pesobruto,\n"
                    + "p.pesoliq  as pesoliquido,\n"
                    + "p. codfiscal as ncm,\n"
                    + "cest as cest,\n"
                    + "p.sittrib as idicms,\n"
                    + "p.cstpise as piscred ,\n"
                    + "p.cstpiss as pisdeb \n"
                    + "from\n"
                    + "    produtos p \n"
                    + "left join produtosbarras p2 on p.codigo = p2.produto and p2.tipovenda = '01'\n"
                    + "left join produtoslinha p3 on p.linha = p3.codigo \n"
                    + "left join  produtocusto p5 on p.codigo = p5.codigo and tabpreco = '01'\n"
                    + "left join produtospreco p4 on p.codigo = p4.produto  and p4.tipovenda = '01' and p4.unidade = '04' \n"
                    + "left join unidades u on u.codigo = p.unidpadrao "
            //                    "select\n"
            //                    + "p.codigo as id,\n"
            //                    + "p.descricao  as descricao,\n"
            //                    + "case when u.unidade = 'KG' then p.codigo else p2.codbarras end as ean,\n"
            //                    + "u.unidade tipoembalagem,\n"
            //                    + "p2.unidade as qtdembalagem,\n"
            //                    + "replace(p5.custoaqui,',','.')::numeric as custo,\n"
            //                    + "p4.preminimo as precovenda,\n"
            //                    + "case when p.situacao = 'A' then 1 else 0 end as ativo,\n"
            //                    + "p.estminimo as estoque,\n"
            //                    + "p.peso as pesobruto,\n"
            //                    + "p.pesoliq  as pesoliquido,\n"
            //                    + "p3.codfiscal as ncm,\n"
            //                    + "cest as cest,\n"
            //                    + "p.sittrib as idicms,\n"
            //                    + "p.cstpise as piscred ,\n"
            //                    + "p.cstpiss as pisdeb \n"
            //                    + "from\n"
            //                    + "	produtos p \n"
            //                    + "left join produtosbarras p2 on p.codigo = p2.produto and p2.tipovenda = '01'\n"
            //                    + "left join produtoslinha p3 on p.linha = p3.codigo \n"
            //                    + "left join  produtocusto p5 on p.codigo = p5.codigo and tabpreco = '01'\n"
            //                    + "left join produtospreco p4 on p.codigo = p4.produto \n"
            //                    + "and p4.unidade = '04' and p4.fator = 1 \n"
            //                    + "left join unidades u on u.codigo = p.unidpadrao"
            )) {
                //Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    //ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));
//                    if (bal != null) {
//                        imp.seteBalanca(true);
//                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
//                        imp.setEan(String.valueOf(bal.getCodigo()));
//                    }
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(1);
                    if (rst.getString("tipoembalagem").equalsIgnoreCase("KG")) {
                        imp.seteBalanca(true);
                    }

                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico1"));
                    imp.setCodMercadologico3(rst.getString("mercadologico1"));
                    imp.setIdFamiliaProduto(rst.getString("familiaproduto"));

                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    String idIcmsDebito = rst.getString("idicms");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);

                    imp.setIcmsCreditoId(idIcmsDebito);
                    imp.setIcmsCreditoForaEstadoId(idIcmsDebito);

                    imp.setPiscofinsCstDebito(rst.getString("pisdeb"));
                    imp.setPiscofinsCstCredito(rst.getString("piscred"));
                    //imp.setPiscofinsNaturezaReceita(rst.getString(""));

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigo as id,\n"
                    + "	nome as razao,\n"
                    + "	nomefanta as fantasia,\n"
                    + "	nroinsc as cnpj,\n"
                    + "	nroinsc as rgie,\n"
                    + "	endereco as endereco,\n"
                    + "	nroend as numero,\n"
                    + "	bairro as bairro,\n"
                    + "	cidade as municipio,\n"
                    + "	estado as uf,\n"
                    + "	cep as cep,\n"
                    + "	observacao as obs,\n"
                    + "	dataatual as datacadastro,\n"
                    + "	tel as tel\n"
                    + "from\n"
                    + "	fornecedores f"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("rgie"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setObservacao(rst.getString("obs"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setTel_principal(rst.getString("tel"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    fornecedor as id_fornecedor,\n"
                    + "    produto as id_produto,\n"
                    + "    codfor as codigoexterno,\n"
                    + "    CASE \n"
                    + "    WHEN unidade = '' THEN '1' ELSE fator END\n"
                    + "    as qtdembalagem\n"
                    + "from\n"
                    + "    produtosfornecedor p"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString(""));
                    imp.setIdFornecedor(rst.getString(""));
                    imp.setNumeroDocumento(rst.getString(""));
                    imp.setDataEmissao(rst.getDate(""));
                    imp.setDataEntrada(imp.getDataEmissao());
                    imp.addVencimento(rst.getDate(""), rst.getDouble(""), rst.getString(""));

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.codigo as id,\n"
                    + "	c.nome as razao,\n"
                    + "	nomefant as fantasia,\n"
                    + "	nroinsc as cpf,\n"
                    + "	inscest as rg,\n"
                    + "	endereco as endereco,\n"
                    + "	numero as numero,\n"
                    + "	bairro as bairro,\n"
                    + "	codcidade ,\n"
                    + "	cc.nome as cidade,\n"
                    + "	c.estado as uf,\n"
                    + "	cep \n"
                    + "from\n"
                    + "	clientes c\n"
                    + "	join cidades cc on cc.codigo = c.codcidade "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(rst.getString("rg"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Autowired
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "docpag as id,\n"
                    + "nroref as cupom,\n"
                    + "c.codigo as id_cliente,\n"
                    + "nroinsc as cpf,\n"
                    + "split_part(c.dataemi,'/',3)||'-'||split_part(c.dataemi,'/',1)||'-'||split_part(c.dataemi,'/',2) as emuissao,\n"
                    + "split_part(c.dataven,'/',3)||'-'||split_part(c.dataven,'/',1)||'-'||split_part(c.dataven,'/',2)  as vencimento,\n"
                    + "replace(valpre,',','.') as valor \n,"
                    + "c.observacao \n"
                    + "from contasareceber c \n"
                    + "join clientes c2 on c.codigo = c2.codigo \n"
                    + "where tipodoc = '05'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("cupom")));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setCnpjCliente(rst.getString("cpf"));
                    //imp.setEcf(rst.getString(""));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEmissao(rst.getDate("emuissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
