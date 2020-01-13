package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class RensoftwareDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "Rensoftware";
        } else {
            return "Rensoftware - " + complemento;
        }
    }

    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CODIGO, NOME from empresa order by codigo"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(rst.getString("codigo"), rst.getString("nome"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO
        ));
    }
    
    /*
    --Produto - fornecedor sem código externo
    select * from 
            dbo.NFITENS nfi
            join dbo.NFITENS_TRIBUTOS nft on
                    nfi.NNF = nft.NUMERO_NF and
                    nfi.CODLOJA = nft.CODLOJA and
                    nfi.ITEM = nft.ITEM
            join dbo.NFISCAL nf on
                    nfi.NNF = nf.NF and
                    nfi.CODLOJA = nf.CODLOJA
    where
            nfi.produto = 967
            and nf.EMISSAO between '2019-01-01' and '2020-01-08'
    order by
            nf.EMISSAO

    --select * from dbo.NFISCAL

    --select * from NFITENS_TRIBUTOS where codigo = 1408
    */

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	p.CODIGO id,\n" +
                    "	coalesce(dt.DATA_INC, p.DATA_ALTERACAO) datacadastro,\n" +
                    "	coalesce(dt.data_alt, p.DATA_ALTERACAO) dataalteracao,\n" +
                    "	coalesce(pxc.CODIGOBARRAS, p.cbarra, p.cbarra2, p.cbarra3) ean,\n" +
                    "	p.embalagem / coalesce(nullif(pxc.fator,0), nullif(p.embalagem,0)) qtdembalagem,\n" +
                    "	coalesce(pxc.UNIDADE, p.unidade) unidadevenda,	\n" +
                    "	p.UNIDADE_COMPRA unidadecotacao,\n" +
                    "	p.EMBALAGEM qtdembalagemcotacao,\n" +
                    "	p.QTD_VOLUMES qtdembalagem,\n" +
                    "	p.BALANCA balanca,\n" +
                    "	p.VALIDIAS validade,\n" +
                    "	p.NOME descricaocompleta,\n" +
                    "	p.peso pesobruto,\n" +
                    "	p.PESO_LIQ pesoliquido,\n" +
                    "	pl.EST_MINIM estoqueminimo,\n" +
                    "	pl.EST_LOJA estoque,\n" +
                    "	pl.FORNECEDOR id_fabricante,\n" +
                    "	pl.PCO_COMPRA,\n" +
                    "	coalesce(pxl.PRECOSISTEMA, pl.PCO_VENDA) / (p.embalagem / coalesce(nullif(pxc.fator,0), nullif(p.embalagem, 0))) preco,\n" +
                    "	coalesce(p.ATIVO, 'S') ativo,\n" +
                    "	p.CODIGO_NCM ncm,\n" +
                    "	p.cod_cest cest,\n" +
                    "	p.COD_CSTPIS pis_saida,\n" +
                    "	p.COD_CSTPIS_ENTRADA pis_entrada,\n" +
                    "	pl.COD_FIG_FISCAL_ent icms_entrada_id,\n" +
                    "	pl.COD_FIG_FISCAL_sai icms_saida_id,\n" +
                    "	pl.PER_IVA iva,	\n" +
                    "	pxl.PRECOVENDA / (p.embalagem / coalesce(nullif(pxc.fator,0), nullif(p.embalagem,0))) precoatacado\n" +
                    "from\n" +
                    "	produtos p\n" +
                    "	join EMPRESA e on\n" +
                    "		e.CODIGO = " + getLojaOrigem() + "\n" +
                    "	left join PRODEXPL_CADASTRO PXC on\n" +
                    "		pxc.CODIGOPRODUTO = p.CODIGO\n" +
                    "	left join PRODEXPL_CADASTROLOJA pxl on\n" +
                    "		pxl.CODIGOPRODUTO = pxc.CODIGOPRODUTO and\n" +
                    "		pxl.ITEM = pxc.ITEM and\n" +
                    "		pxl.CODLOJA = e.codigo		\n" +
                    "	left join produtos_data dt on\n" +
                    "		dt.codigo_produto = p.codigo\n" +
                    "	join PRODLOJAS pl on\n" +
                    "		p.codigo = pl.CODIGO and\n" +
                    "		p.CODLOJA = e.CODIGO\n" +
                    "order by\n" +
                    "	p.CODIGO"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("unidadevenda"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcotacao"));
                    imp.setTipoEmbalagemCotacao(rs.getString("qtdembalagem"));
                    imp.seteBalanca("S".equals(rs.getString("balanca")));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setFornecedorFabricante(rs.getString("id_fabricante"));
                    imp.setCustoComImposto(rs.getDouble("PCO_COMPRA"));
                    imp.setCustoSemImposto(rs.getDouble("PCO_COMPRA"));
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setSituacaoCadastro("N".equals(rs.getString("ATIVO")) ? 0 : 1);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_saida"));
                    imp.setPiscofinsCstCredito(rs.getString("pis_entrada"));
                    imp.setIcmsCreditoId(rs.getString("icms_entrada_id"));
                    imp.setIcmsDebitoId(rs.getString("icms_saida_id"));
                    imp.setAtacadoPreco(rs.getDouble("precoatacado"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "  a.CODIGOPRODUTO as id, \n"
                        + "  a.ITEM, \n"
                        + "  a.UNIDADE, \n"
                        + "  a.PRINCIPAL, \n"
                        + "  a.CODIGOBARRAS as ean,\n"
                        + "  (select FATOR \n"
                        + "     from PRODEXPL_CADASTRO \n"
                        + "    where CODIGOPRODUTO = a.CODIGOPRODUTO\n"
                        + "      and FATOR > 1) as qtde,\n"
                        + "  b.PRECOSISTEMA,\n"
                        + "  b.PRECOVENDA,\n"
                        + "  (select b.PRECOVENDA / (select FATOR \n"
                        + "     from PRODEXPL_CADASTRO \n"
                        + "    where CODIGOPRODUTO = a.CODIGOPRODUTO\n"
                        + "      and FATOR > 1)) as precoatacado,\n"
                        + "(select b.PRECOSISTEMA / (select FATOR \n"
                        + "     from PRODEXPL_CADASTRO \n"
                        + "    where CODIGOPRODUTO = a.CODIGOPRODUTO\n"
                        + "      and FATOR > 1)) as preocvenda\n"
                        + "from PRODEXPL_CADASTRO a\n"
                        + "inner join PRODEXPL_CADASTROLOJA b on b.CODIGOPRODUTO = a.CODIGOPRODUTO\n"
                        + "and a.ITEM = b.ITEM\n"
                        + "and a.FATOR = 1\n"
                        + "and a.CODLOJA = " + getLojaOrigem() + "\n"
                        + "and b.CODLOJA = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtde"));
                        imp.setPrecovenda(rst.getDouble("preocvenda"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                        result.add(imp);
                    }
                }
                return result;
            }            
        }
        return null;
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "	nome,\n" +
                    "	CODIGO_CST_PADRAO cst\n" +
                    "from\n" +
                    "	figurafiscal\n" +
                    "order by\n" +
                    "	codigo"
            )) {
                while (rs.next()) {
                    result.add(
                            new MapaTributoIMP(
                                    rs.getString("codigo"), 
                                    rs.getString("nome"), 
                                    rs.getInt("cst"), 
                                    0, 
                                    0
                            )
                    );
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	f.codigo id,\n" +
                    "	f.RAZAO,\n" +
                    "	f.NOME fantasia,\n" +
                    "	f.cgc cnpj,\n" +
                    "	f.INSCR inscricaoestadual,\n" +
                    "	f.INSC_MUNICIPAL inscricaomunicipal,\n" +
                    "	f.ATIVO,\n" +
                    "	f.ENDERECO,\n" +
                    "	f.NUMERO_END,\n" +
                    "	f.BAIRRO,\n" +
                    "	f.CIDADE municipio,\n" +
                    "	f.ESTADO uf,\n" +
                    "	f.CEP,\n" +
                    "	f.FONE,\n" +
                    "	f.DATAC datacadastro,\n" +
                    "	f.OBJS,\n" +
                    "	f.INFOADICIONAIS,\n" +
                    "	f.P_DIASENTREG prazoentrega,\n" +
                    "	f.P_DIASFREQVI prazovisita,\n" +
                    "	f.TIPO\n" +
                    "from\n" +
                    "	cadfor f\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("RAZAO"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rs.getString("inscricaomunicipal"));
                    imp.setAtivo("S".equals(rs.getString("ATIVO")));
                    imp.setEndereco(rs.getString("ENDERECO"));
                    imp.setNumero(rs.getString("NUMERO_END"));
                    imp.setBairro(rs.getString("BAIRRO"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("CEP"));
                    imp.setTel_principal(rs.getString("FONE"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setObservacao(rs.getString("OBJS"));
                    //imp.set(rs.getString("INFOADICIONAIS"));
                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));
                    imp.setPrazoVisita(rs.getInt("prazovisita"));
                    switch (rs.getString("TIPO")) {
                        case "D": imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR); break;
                    }
                    
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
                    "	c.CODIGO id,\n" +
                    "	c.CGC_CPF cnpj_cpf,\n" +
                    "	c.INS_RG ie_rg,\n" +
                    "	c.INSC_MUNICIPAL inscricaomunicipal,\n" +
                    "	c.RAZAO,\n" +
                    "	c.NOME fantasia,\n" +
                    "	c.ECLIENTE,\n" +
                    "	c.ENDERECO,\n" +
                    "	c.NUMERO_END,\n" +
                    "	c.BAIRRO,\n" +
                    "	c.CIDADE,\n" +
                    "	c.ESTADO,\n" +
                    "	c.CEP,\n" +
                    "	c.EST_CIVIL,\n" +
                    "	c.DATA_NASC,\n" +
                    "	c.DATA_CAD,\n" +
                    "	c.SEXO,	\n" +
                    "	c.ONDE_TRB nomeempresa,\n" +
                    "	c.END_TRAB,\n" +
                    "	c.CAR_TRAB cargo,\n" +
                    "	c.SALARIO,\n" +
                    "	c.LIMITE_CR limite,\n" +
                    "	c.CONJUGE,\n" +
                    "	c.N_PAI nomepai,\n" +
                    "	c.N_MAE nomemae,\n" +
                    "	c.OBJS observacoes,\n" +
                    "	c.INFOADICIONAIS,\n" +
                    "	c.FONE,\n" +
                    "	c.FONE2,\n" +
                    "	c.FONE3,\n" +
                    "	c.CELULAR,\n" +
                    "	c.EMAIL,\n" +
                    "	c.FAX\n" +
                    "from\n" +
                    "	CLIENTES c\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO_END"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setEstadoCivil(rst.getString("EST_CIVIL"));
                    imp.setDataNascimento(rst.getDate("DATA_NASC"));
                    imp.setDataCadastro(rst.getDate("DATA_CAD"));
                    imp.setSexo("F".equals(rst.getString("SEXO")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rst.getString("nomeempresa"));
                    imp.setEmpresaEndereco(rst.getString("END_TRAB"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("SALARIO"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomeConjuge(rst.getString("CONJUGE"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setObservacao(rst.getString("observacoes"));
                    imp.setObservacao2(rst.getString("INFOADICIONAIS"));
                    imp.setTelefone(rst.getString("FONE"));
                    imp.addTelefone("FONE 2", rst.getString("FONE2"));
                    imp.addTelefone("FONE 3", rst.getString("FONE3"));
                    imp.setCelular(rst.getString("CELULAR"));
                    imp.setEmail(rst.getString("EMAIL"));
                    imp.setFax(rst.getString("FAX"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	ch.CODIGO id,\n" +
                    "	ch.NCGCPF cpf,\n" +
                    "	ch.NCHEQUE numerocheque,\n" +
                    "	ch.NBANCO banco,\n" +
                    "	ch.NCONTA conta,\n" +
                    "	ch.EMISSAO data,\n" +
                    "	ch.DATADIGT deposito,\n" +
                    "	ch.NFISCAL cupom,\n" +
                    "	ch.NUMCAIXAPDV pdv,\n" +
                    "	ch.VALOR,\n" +
                    "	ch.VLJUROS juros,\n" +
                    "	ch.VENCIMENTO,\n" +
                    "	ch.CREDOR id_cliente,\n" +
                    "	ch.OBSERVACAO\n" +
                    "from\n" +
                    "	TITCHEQUES ch\n" +
                    "where\n" +
                    "	ch.CODLOJA = 1\n" +
                    "order by\n" +
                    "	ch.codigo"
            )) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setCpf(rs.getString("cpf"));
                    imp.setNumeroCheque(rs.getString("numerocheque"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setConta(rs.getString("conta"));
                    imp.setDate(rs.getDate("VENCIMENTO"));
                    imp.setDataDeposito(rs.getDate("deposito"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setEcf(rs.getString("pdv"));
                    imp.setValor(rs.getDouble("VALOR"));
                    imp.setValorJuros(rs.getDouble("juros"));
                    imp.setObservacao(rs.getString("OBSERVACAO"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
