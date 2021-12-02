package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */

public class SygmaDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.PESO_BRUTO,
                    OpcaoProduto.PESO_LIQUIDO,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.NUTRICIONAL,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    @Override
    public String getSistema() {
        return "Sygma";
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    e.cod_empresa,\n"
                    + "    e.razao_social\n"
                    + "from\n"
                    + "    empresa e\n"
                    + "order by\n"
                    + "    e.cod_empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cod_empresa"), rst.getString("razao_social")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("codmerc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("codmerc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("codmerc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date datatermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	d.CODPRO id_produto,\n"
                    + "	o.DTAINI data_inicial,\n"
                    + "	o.DTAFIM data_final,\n"
                    + "	d.PRECO_VIST preco_venda,\n"
                    + "	i.PRECOPROMOCIONAL preco_oferta\n"
                    + "FROM\n"
                    + "	TPROMOCAO o\n"
                    + "	JOIN TITPROMOCAO i ON i.CODPROMOCAO = o.SEQ\n"
                    + "	JOIN TDERIVACAO d ON i.CODDER = d.CODDER \n"
                    + "WHERE\n"
                    + "	DTAFIM >= 'now'"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setDataInicio(rst.getDate("data_inicial"));
                    imp.setDataFim(rst.getDate("data_final"));
                    imp.setPrecoNormal(rst.getDouble("preco_venda"));
                    imp.setPrecoOferta(rst.getDouble("preco_oferta"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + " f.cod_familia,\n"
                    + " f.descricao\n"
                    + "from\n"
                    + " familia f\n"
                    + "order by\n"
                    + " f.cod_familia,\n"
                    + " f.descricao"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("cod_familia"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    result.add(imp);
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
                    "select\n"
                    + "    p.cod_produto id,   \n"
                    + "    p.data_cadastro datacadastro,\n"
                    + "    p.ean,\n"
                    + "    1 as qtdEmbalagem,\n"
                    + "    un.unidade tipoEmbalagem,\n"
                    + "    case un.unidade when 'KG' then 'S' else 'N' end eBalanca,\n"
                    + "    coalesce(p.val_balanca, 0) validade,\n"
                    + "    p.descricao descricaoCompleta,\n"
                    + "    coalesce(p.descricao_abreviada, p.descricao) descricaoReduzida,\n"
                    + "    p.descricao descricaoGondola,\n"
                    + "    g.cod_grupo codMercadologico1,\n"
                    + "    sg.cod_gruposub codMercadologico2,\n"
                    + "    p.cod_familia idFamiliaProduto,\n"
                    + "    p.peso_bruto pesoBruto,\n"
                    + "    p.peso_liquido pesoLiquido, \n"
                    + "    p.estoque_maximo estoqueMaximo,\n"
                    + "    p.estoque_minimo estoqueMinimo,\n"
                    + "    est.saldo_atual estoque,\n"
                    + "    p.margem_1 margem,\n"
                    + "    p.preco_reposicao custoSemImposto,\n"
                    + "    p.preco_custo custoComImposto,\n"
                    + "    p.valor_tabela_1 precovenda,\n"
                    + "    case when upper(p.situacao) = 'I' then 0 else 1 end situacaoCadastro,\n"
                    + "    p.conta_ncm ncm,\n"
                    + "    p.cest,\n"
                    + "    pis_deb.cst piscofinscstdebito,\n"
                    + "    pis_cred.cst piscofinscstcredito,\n"
                    + "    null as piscofinsNaturezaReceita,\n"
                    + "    icms.cod_classificacao id_icms,\n"
                    + "    icms.cod_classificacao icms_cst,\n"
                    + "    icms.aliq_icms_i icms_aliq,\n"
                    + "    0 icms_reducao\n"
                    + "from\n"
                    + "    produto p\n"
                    + "    left join unidade_medida un on p.cod_unidade = un.cod_unidade\n"
                    + "    left join vestoque /*OU SD_ESTOQUE*/ est on p.cod_produto = est.cod_produto and\n"
                    + "        est.cod_empresa = " + getLojaOrigem().split("-")[0] + "\n"
                    + "    left join cst_pis_saida pis_deb on p.cod_tp_aliq_piscofins = pis_deb.codigo\n"
                    + "    left join cst_pis_entrada pis_cred on p.cod_tp_aliq_piscofins = pis_cred.codigo\n"
                    + "    left join classificacao_fiscal icms on p.cod_classificacao = icms.cod_classificacao\n"
                    + "    left join grupo g on p.cod_grupo = g.cod_grupo\n"
                    + "    left join grupo_sub sg on p.cod_subgrupo = sg.cod_gruposub\n"
                    + "order by\n"
                    + "    p.cod_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.seteBalanca(!"N".equals(rst.getString("eBalanca")));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setCodMercadologico1(rst.getString("codMercadologico1"));
                    imp.setCodMercadologico2(rst.getString("codMercadologico2"));
                    imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaoGondola"));
                    imp.setIdFamiliaProduto(rst.getString("idFamiliaProduto"));
                    imp.setPesoBruto(rst.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoLiquido"));
                    imp.setEstoqueMaximo(rst.getDouble("estoqueMaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueMinimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setCustoComImposto(rst.getDouble("custoSemImposto"));

                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaoCadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofinscstdebito"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofinscstcredito"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofinsNaturezaReceita"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    imp.setManterEAN(Utils.stringToLong(imp.getEan()) <= 999999);

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
                    "SELECT\n"
                    + "	COD_FORNECEDOR id,\n"
                    + "	DES_RAZAO razao,\n"
                    + "	DES_FANTASIA fantasia,\n"
                    + "	NUM_CNPJ_CPF cnpj,\n"
                    + "	NUM_INSCRICAO_RG ie_rg,\n"
                    + "	LOGRADOURO endereco,\n"
                    + "	LOGRA_NUM numero,\n"
                    + "	LOGRA_COMPL complemento,\n"
                    + "	NOM_BAIRRO bairro,\n"
                    + "	c.NOM_CIDADE cidade,\n"
                    + "	c.SGL_ESTADO uf,\n"
                    + "	NUM_CEP cep,\n"
                    + "	NUM_TELEFONE fone1,\n"
                    + "	NUM_TELEFONE2 fone2,\n"
                    + "	NUM_FAX fax,\n"
                    + "	E_MAIL email,\n"
                    + "	LIMITECRED limite,\n"
                    + "	DTA_INCLUSAO data_cadastro,\n"
                    + "	DES_OBSERVACAO observacao\n"
                    + "FROM\n"
                    + "	TFORNECEDOR f\n"
                    + "	JOIN TCIDADE c ON f.COD_CIDADE = c.COD_CIDADE\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    
                    imp.setTel_principal(rst.getString("fone1"));
                    
                    String fone2 = rst.getString("fone2");
                    if (!"".equals(fone2)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("1");
                        cont.setImportId("1");
                        cont.setNome("FONE 2");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fone2);
                    }

                    String fax = rst.getString("fax");
                    if (!"".equals(fax)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportId("2");
                        cont.setNome("FAX");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fax);
                    }

                    String email = rst.getString("email");
                    if (!"".equals(email)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportId("3");
                        cont.setNome("EMAIL");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setEmail(email);
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
                    + "	pf.COD_FORNECEDOR id_fornecedor,\n"
                    + "	d.CODPRO id_produto,\n"
                    + "	pf.REFFORNEC cod_externo\n"
                    + "FROM\n"
                    + "	FORNECPROD pf\n"
                    + "	JOIN TDERIVACAO d ON d.CODDER = pf.CODDER\n"
                    + "WHERE\n"
                    + "	pf.REFFORNEC != '' AND pf.REFFORNEC IS NOT NULL\n"
                    + "ORDER BY 1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	SEQ_CHEQUE id,\n"
                    + "	DTA_ENTRADA DATA,\n"
                    + "	DTA_DEPOSITO deposito,\n"
                    + "	NUM_CHEQUE numero_cheque,\n"
                    + "	COD_BANCO banco,\n"
                    + "	COD_AGENCIA agencia,\n"
                    + "	NUM_CONTA_CHEQUE conta,\n"
                    + "	CNPJCPF,\n"
                    + "	NOM_EMITENTE nome,\n"
                    + "	VAL_CHEQUE valor,\n"
                    + "	CODLAN observacao\n"
                    + "FROM\n"
                    + "	TCHEQUE_PREDATADO"
            )) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDate(rs.getDate("data"));
                    imp.setDataDeposito(rs.getDate("deposito"));
                    imp.setNumeroCheque(rs.getString("numero_cheque"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("conta"));
                    imp.setCpf(rs.getString("cnpjcpf"));
                    imp.setNome(rs.getString("nome"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));

                    Result.add(imp);
                }
            }
        }
        
        return Result;
    }

    @Override
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.cod_cliente id,\n"
                    + "    c.razao_social nome,\n"
                    + "    c.logradouro res_endereco,\n"
                    + "    c.numero res_numero,\n"
                    + "    c.complemento res_complemento,\n"
                    + "    c.bairro res_bairro,\n"
                    + "    c.municipio res_municipio,\n"
                    + "    c.uf res_uf,\n"
                    + "    c.cep res_cep,\n"
                    + "    c.fone_1 fone1,\n"
                    + "    trim(coalesce(c.fone_2,'')) fone2,\n"
                    + "    c.celular,\n"
                    + "    c.insc_estadual inscricaoestadual,\n"
                    + "    c.cnpj_cpf cnpj,\n"
                    + "    1 sexo,\n"
                    + "    c.dias_carencia prazodias,\n"
                    + "    c.email,\n"
                    + "    c.data_cadastro datacadastro,\n"
                    + "    c.limite_credito limite,\n"
                    + "    case c.situacao when 'B' then 1 else 0 end bloqueado,\n"
                    + "    c.obs observacao,\n"
                    + "    c.data_nascimento datanascimento,\n"
                    + "    null nomePai,\n"
                    + "    null nomeMae,\n"
                    + "    null empresa,\n"
                    + "    null telEmpresa,\n"
                    + "    null cargo,\n"
                    + "    0 salario,\n"
                    + "    0 estadoCivil,\n"
                    + "    null conjuge,\n"
                    + "    c.orgao orgaoemissor\n"
                    + "from\n"
                    + "    cliente c\n"
                    + "where\n"
                    + "    not c.razao_social is null\n"
                    + "order by\n"
                    + "    c.cod_cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setEndereco(rst.getString("res_endereco"));
                    imp.setNumero(rst.getString("res_numero"));
                    imp.setComplemento(rst.getString("res_complemento"));
                    imp.setBairro(rst.getString("res_bairro"));
                    imp.setMunicipio(rst.getString("res_municipio"));
                    imp.setUf(rst.getString("res_uf"));
                    imp.setCep(rst.getString("res_cep"));
                    imp.setTelefone(rst.getString("fone1"));
                    if (!"".equals(rst.getString("fone2"))) {
                        imp.addContato("1", "FONE2", rst.getString("fone2"), "", "");
                    }
                    imp.setCelular(rst.getString("celular"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setPrazoPagamento(rst.getInt("prazodias"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

   @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        int cont = 0;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    r.codigo id,\n"
                    + "    r.codcliente id_clientepreferencial,\n"
                    + "    c.cnpj_cpf cnpj,\n"
                    + "    r.dataemissao emissao,\n"
                    + "    r.historico,\n"
                    + "    r.saldo valor,\n"
                    + "    r.JUROS,\n"
                    + "    r.MORA,\n"
                    + "    r.datavencimento venc,\n"
                    + "    r.datapagamento datapag,\n"
                    + "    r.valorrecebido,\n"
                    + "    r.documento cupom\n"
                    + "from\n"
                    + "    receber r\n"
                    + "    join cliente c on r.codcliente = c.cod_cliente\n"
                    + "where\n"
                    + "    r.saldo > 0"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("id_clientepreferencial"));
                    imp.setCnpjCliente(rst.getString("cnpj"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setValor(rst.getDouble("valor"));
                                      
                    imp.setDataVencimento(rst.getDate("venc"));
                    imp.setObservacao(rst.getString("historico"));

                    result.add(imp);

                    cont++;
                    ProgressBar.setStatus("Carregando créditorotativo..." + cont);
                }
            }
        }

        return result;
    }
    

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    cf.cod_classificacao as id,\n"
                    + "    cf.aliq_icms_i as aliquota,\n"
                    + "    cf.base_icms_i as reducao,\n"
                    + "    cf.descricao as descricao\n"
                    + "from classificacao_fiscal cf\n"
                    + "where cf.uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "order by cf.cod_classificacao"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("id"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }
        
        return result;
    }

}
