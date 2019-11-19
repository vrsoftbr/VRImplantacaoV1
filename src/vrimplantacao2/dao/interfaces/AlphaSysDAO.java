package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoItemIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class AlphaSysDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(AlphaSysDAO.class.getName());

    @Override
    public String getSistema() {
        return "AlphaSys";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.NCM,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cod_empresa, cod_empresa||' - '||razao descricao from empresa order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cod_empresa"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    cod_grupo merc1,\n"
                    + "    nome merc1_desc\n"
                    + "from\n"
                    + "    grupo g\n"
                    + "where\n"
                    + "    nivel = 0\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP(rst.getString("merc1"), rst.getString("merc1_desc"));

                    mercNivel2(imp);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private void mercNivel2(MercadologicoNivelIMP pai) throws Exception {
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    cod_grupo merc2,\n"
                    + "    nome merc2_desc\n"
                    + "from\n"
                    + "    grupo g\n"
                    + "where\n"
                    + "    g.nivel = 1 and\n"
                    + "    g.cod_juncao = " + pai.getId() + "\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    pai.addFilho(rst.getString("merc2"), rst.getString("merc2_desc"));
                }
            }
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.cod_produto id,\n"
                    + "    p.dt_atualizacao_preco datacadastro,\n"
                    + "    p.dt_atualizacao_preco dataalteracao,\n"
                    + "    p.cod_barras ean,\n"
                    + "    p.cod_busca_preco,\n"
                    + "    p.fator_conversao qtdemb_cotacao,\n"
                    + "    p.cod_unidade_saida unidade,\n"
                    + "    pc.validade,\n"
                    + "    case upper(p.produto_balanca) when 'TRUE' then 1 else 0 end e_balanca,\n"
                    + "    p.nome descricaocompleta,\n"
                    + "    p.nome_pdv descricaoreduzida,\n"
                    + "    p.cod_grupo mercadologico1,\n"
                    + "    p.cod_subgrupo mercadologico2,\n"
                    + "    pc.peso_bruto,\n"
                    + "    pc.peso_liquido,\n"
                    + "    pc.estoque_minimo,\n"
                    + "    pc.estoque_maximo,\n"
                    + "    pc.custo_lucro margem,\n"
                    + "    pc.preco_compra custocomimposto,\n"
                    + "    pc.preco_compra - coalesce(pc.custo_imposto, 0) custosemimposto,\n"
                    + "    p.preco_vista precovenda,\n"
                    + "    p.ncm,\n"
                    + "    pc.situacao_tributaria_pis piscofins_saida,\n"
                    + "    pc.situacao_tributaria_cfe icms_cst,\n"
                    + "    pc.cfe_cst_percentual icms_aliquota,\n"
                    + "    pc.reducao_bc_icms_cfe icms_reduzido\n"
                    + "from\n"
                    + "    produto p\n"
                    + "    join produto_complemento pc on\n"
                    + "        pc.cod_produto = p.cod_produto and\n"
                    + "        pc.cod_empresa = p.cod_empresa\n"
                    + "order by\n"
                    + "    1"
            )) {
                int cont = 0;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                                
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    
                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean")));
                    if (bal != null) {
                        imp.setEan(bal.getCodigo() + "");
                        imp.setQtdEmbalagem(1);
                        imp.setValidade(bal.getValidade());
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(1);
                        imp.setValidade(rst.getInt("validade"));
                        imp.seteBalanca("S".equals(rst.getString("e_balanca")));
                        imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                    }
                    
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    //imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdemb_cotacao"));
                    //imp.setTipoEmbalagem(rst.getString("unidade"));
                    //imp.setValidade(rst.getInt("validade"));
                    //imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            double juros, multa;
            int diasInicial, diasFinal;
            try (ResultSet rst = stm.executeQuery(
                    "select multa, juros, dias_inicial, dias_final from CONTAS_RECEBER_MULTA_JUROS where cod_empresa = " + getLojaOrigem()
            )) {
                rst.next();
                juros = rst.getDouble("juros");
                multa = rst.getDouble("multa");
                diasInicial = rst.getInt("dias_inicial");
                diasFinal = rst.getInt("dias_final");
            }
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    vc.COD_EMPRESA,\n" +
                    "    vc.cod_contas_receber,\n" +
                    "    vc.PARCELA,    \n" +
                    "    cr.dt_emissao as dataemissao,\n" +
                    "    cr.documento as numerocupom,\n" +
                    "    cx.COD_SAT as ecf,\n" +
                    "    vc.VALOR,\n" +
                    "    vc.VALOR_PAGO,\n" +
                    "    cr.observacao,\n" +
                    "    cr.cod_colaborador as idcliente,\n" +
                    "    vc.dt_vencimento as datavencimento,\n" +
                    "    current_timestamp - vc.DT_VENCIMENTO dias_atraso\n" +
                    "from\n" +
                    "    CONTAS_RECEBER_VENCIMENTO vc\n" +
                    "    join CONTAS_RECEBER cr on\n" +
                    "        cr.COD_EMPRESA = vc.COD_EMPRESA and\n" +
                    "        cr.COD_CONTAS_RECEBER = vc.COD_CONTAS_RECEBER\n" +
                    "    left join CONTAS_RECEBER_PAGAMENTO cp on\n" +
                    "        cp.COD_EMPRESA = vc.COD_EMPRESA and\n" +
                    "        cp.COD_CONTAS_RECEBER = vc.COD_CONTAS_RECEBER and\n" +
                    "        cp.PARCELA = vc.PARCELA\n" +
                    "    left join caixa cx\n" +
                    "		on cr.cod_caixa = cx.cod_caixa\n" +
                    "where\n" +
                    "    cr.COD_EMPRESA = " + getLojaOrigem() + " and\n" +
                    "    cr.COD_DOCUMENTO = 'CDN' and\n" +
                    "    coalesce(round(vc.VALOR_PAGO, 2), 0) < round(vc.VALOR, 2) and\n" +
                    "    cr.COD_RELACIONAMENTO is null\n" +
                    "order by\n" +
                    "    cr.COD_CONTAS_RECEBER"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(
                            rst.getString("COD_EMPRESA") + "-" +
                            rst.getString("cod_contas_receber") + "-" +
                            rst.getString("PARCELA")
                    );
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    int diasAtraso = rst.getInt("dias_atraso");
                    
                    if (diasAtraso >= diasInicial && diasAtraso <= diasFinal) {
                        double valorRestante = rst.getDouble("valor") - rst.getDouble("valor_pago");                    
                        imp.setJuros(valorRestante * ((juros / 100) * diasAtraso));
                        imp.setMulta(valorRestante * (multa / 100));
                    }
                    
                    if (rst.getDouble("valor_pago") > 0) {
                        imp.addPagamento(imp.getId(), rst.getDouble("valor_pago"), 0, 0, imp.getDataVencimento(), "");
                    }
                    
                    result.add(imp);
                    ProgressBar.next();
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
                    "select\n" +
                    "	c.cod_colaborador as id,\n" +
                    "	c.cgc as cnpj,\n" +
                    "	c.ies as inscricaoestadual,\n" +
                    "	c.razao,\n" +
                    "	c.fantasia,\n" +
                    "	tl.nome||' '||l.nome as endereco,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	b.nome as bairro,\n" +
                    "	m.nome as municipio,\n" +
                    "	c.cod_estado as uf,\n" +
                    "	c.cep,\n" +
                    "	c.dt_cadastro as dataCadastro,\n" +
                    "	c.fone as telefone,\n" +
                    "	c.celular,\n" +
                    "	c.email,\n" +
                    "	c.fax,\n" +
                    "	cc.limite_credito,\n" +
                    "	cc.dia_pagto_1,\n" +
                    "	cc.dt_nascimento,\n" +
                    "	cc.sexo,\n" +
                    "	cc.numero_cartao,\n" +
                    "	cc.salario\n" +
                    "from colaborador C\n" +
                    "	join logradouro l\n" +
                    "		on c.cod_logradouro = l.cod_logradouro\n" +
                    "	join logradouro_tipo tl\n" +
                    "		on c.cod_logradouro_tipo = tl.cod_logradouro_tipo\n" +
                    "	join bairro b\n" +
                    "		on c.cod_bairro = b.cod_bairro\n" +
                    "	join municipio m\n" +
                    "		on c.cod_municipio = m.cod_municipio\n" +
                    "	left join CLIENTE_COMPLEMENTO cc on\n" +
                    "       cc.cod_empresa = c.cod_empresa and\n" +
                    "       cc.cod_colaborador = c.cod_colaborador\n" +
                    "where c.tipo = 1\n" +
                    "	order by c.cod_colaborador, c.tipo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
                    imp.setDiaVencimento(rst.getInt("dia_pagto_1"));
                    imp.setDataNascimento(rst.getDate("dt_nascimento"));
                    imp.setSexo(rst.getString("sexo"));
                    imp.setObservacao("NUMERO CARTAO " + rst.getString("numero_cartao"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setPermiteCreditoRotativo(true);

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
                    "select \n"
                    + "	cod_colaborador importid,\n"
                    + "	razao,\n"
                    + "	fantasia,\n"
                    + "	cgc cnpj_cpf,\n"
                    + "	ies ie_rg,\n"
                    + "	tl.nome||' '||l.nome endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	b.nome bairro,\n"
                    + "	m.nome municipio,\n"
                    + "	c.cod_estado uf,\n"
                    + "	c.cep,\n"
                    + "	fone tel_principal,\n"
                    + "	dt_cadastro datacadastro\n"
                    + "from colaborador C\n"
                    + "	join logradouro l\n"
                    + "		on c.cod_logradouro = l.cod_logradouro\n"
                    + "	join logradouro_tipo tl\n"
                    + "		on c.cod_logradouro_tipo = tl.cod_logradouro_tipo\n"
                    + "	join bairro b\n"
                    + "		on c.cod_bairro = b.cod_bairro\n"
                    + "	join municipio m\n"
                    + "		on c.cod_municipio = m.cod_municipio\n"
                    + "where tipo = 2\n"
                    + "	order by cod_colaborador, tipo")) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("importid"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cod_colaborador	as idFornecedor,\n"
                    + "	cod_produto	as idProduto,\n"
                    + "	cod_produto_fornecedor as codigoexterno,\n"
                    + "	dt_compra as dataalteracao,\n"
                    + "	preco_compra custotabela\n"
                    + "from produto_fornecedor pf\n"
                    + "	where cod_produto_fornecedor is not null"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("idFornecedor"));
                    imp.setIdProduto(rs.getString("idProduto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setCustoTabela(rs.getDouble("custotabela"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

}
        
