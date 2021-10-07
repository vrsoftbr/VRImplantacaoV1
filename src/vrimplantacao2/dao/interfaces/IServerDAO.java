package vrimplantacao2.dao.interfaces;

import java.util.Set;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.logging.Level;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;

/**
 *
 * @author Alan
 */
public class IServerDAO extends InterfaceDAO implements MapaTributoProvider {

    private String txtComplemento;
    private boolean somenteProdutoAtivo = false;

    @Override
    public String getSistema() {
        return "IServer";
    }

    public void setComplemento(String complemento) {
        this.txtComplemento = complemento == null ? "" : complemento.trim();
    }
    
    public void setSomenteProdutosAtivos(boolean somenteProdutoAtivo) {
        this.somenteProdutoAtivo = somenteProdutoAtivo;
    }
    
    public boolean isSomenteProdutoAtivo() {
        return this.somenteProdutoAtivo;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
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
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	  Codigo empcod,\n"
                    + "	  NomeFantasia empnome\n"
                    + "from tbl_loja "
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getInt("empcod") + "", rst.getString("empnome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	 Cod_Classe codigo,\n"
                    + "	 Descricao_Classe descricao,\n"
                    + "	 Cst_Classe cst,\n"
                    + "	 Ecf_Aliquota_Classe aliquota,\n"
                    + "	 Nota_Reducao_Classe reducao\n"
                    + "from tbl_classe"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            Utils.stringToDouble(rs.getString("aliquota")),
                            Utils.stringToDouble(rs.getString("reducao"))));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	 g.Cod_Grupo m1,\n"
                    + "	 Descr_Grupo m1desc,\n"
                    + "	 sg.Cod_SubGrupo m2,\n"
                    + "	 sg.Descr_SubGrupo m2desc,\n"
                    + "	 sg.Cod_SubGrupo m3,\n"
                    + "	 sg.Descr_SubGrupo m3desc\n"
                    + "from tbl_grupo g\n"
                    + "  join tbl_subgrupo sg on g.Cod_Grupo = sg.Cod_Grupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1"));
                    imp.setMerc1Descricao(rst.getString("m1desc"));
                    imp.setMerc2ID(rst.getString("m2"));
                    imp.setMerc2Descricao(rst.getString("m2desc"));
                    imp.setMerc3ID(rst.getString("m3"));
                    imp.setMerc3Descricao(rst.getString("m3desc"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Codigo_Prod id,\n"
                    + "	CodBarra_Prod ean,\n"
                    + "	Unidade_Prod unidade,\n"
                    + "	Descr_Prod descricaocompleta,\n"
                    + "	Descr_Reduz_Prod descricaoreduzida,\n"
                    + "	Descr_Reduz_Prod descricaogondola,\n"
                    + "	cst_classe cstsaida,\n"
                    + "	replace(ecf_aliquota_classe,',','.') aliqsaida,\n"
                    + "	nota_reducao_classe redsaida,\n"
                    + "	cst_classe cstconsumidor,\n"
                    + "	replace(ecf_aliquota_classe,',','.') aliqconsumidor,\n"
                    + "	nota_reducao_classe redconsumidor,\n"
                    + "	Cod_Grupo_Prod merc1,\n"
                    + "	Cod_Subgrupo_Prod merc2,\n"
                    + "	Cod_Subgrupo_Prod merc3,\n"
                    + "	Preco_Prod precovenda,\n"
                    + "	Custo_Prod custocomimposto,\n"
                    + "	Custo_SN_Prod custosemimposto,\n"
                    + "	Margem_Prod margem,\n"
                    + "	Estoque_Prod estoque,\n"
                    + "	Estoque_Min_Prod estoquemin,\n"
                    + "	Estoque_Max_Prod estoquemax,\n"
                    + "	Ultima_Alteracao_Prod dataalteracao,\n"
                    + "	Quantidade_Embalagem_Prod qtdembalagem,\n"
                    + "	Ncm_Prod ncm,\n"
                    + "	Cest_Prod cest,\n"
                    + "	case when Servico_Prod = 'B' then 'S' else 'N' end balanca,\n"
                    + "	case when Pesavel_Prod = 'N' then 0 else 1 end pesavel,\n"
                    + "	case when Status_Prod = 'A' then 1 else 0 end situacaocadastro,\n"
                    + " icm.Cod_Classe as idIcms\n"        
                    + "from\n"
                    + "	tbl_produto p\n"
                    + "left join tbl_classe icm on icm.Cod_Classe = p.Classe_Prod\n"
                    + (isSomenteProdutoAtivo() ? " where Status_Prod = 'A'\n" : "")
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    
                    if (!produtosBalanca.isEmpty()) {

                        if (imp.getEan().trim().length() == 4) {

                            long codigoProduto;
                            codigoProduto = Long.parseLong(Utils.formataNumero(imp.getEan()));

                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }

                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca("S".equals(rst.getString("balanca")));
                        }                        
                    } else {
                        imp.seteBalanca("S".equals(rst.getString("balanca")));
                    }
                    
                    imp.setTipoEmbalagem(Utils.acertarTexto(rst.getString("unidade")));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemax"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    
                    imp.setIcmsDebitoId(rst.getString("idIcms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idIcms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idIcms"));
                    imp.setIcmsCreditoId(rst.getString("idIcms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idIcms"));
                    imp.setIcmsConsumidorId(rst.getString("idIcms"));
                    
                    /*imp.setIcmsCstSaida(rst.getInt("cstsaida"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliqsaida"));
                    imp.setIcmsReducaoSaida(Utils.stringToDouble(rst.getString("redsaida")));
                    imp.setIcmsCstConsumidor(rst.getInt("cstsaida"));
                    imp.setIcmsAliqConsumidor(rst.getDouble("aliqsaida"));
                    imp.setIcmsReducaoConsumidor(Utils.stringToDouble(rst.getString("redsaida")));*/
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Cod_Fornecedor id,\n"
                    + "	Cpf_Cnpj cnpj,\n"
                    + "	Rg_Ie ie,\n"
                    + "	Im,\n"
                    + "	razao,\n"
                    + "	fantasia,\n"
                    + "	Endereco,\n"
                    + "	Numero,\n"
                    + "	complemento,\n"
                    + "	Bairro,\n"
                    + "	Municipio,\n"
                    + "	uf,\n"
                    + "	Cep,\n"
                    + "	Telefone,\n"
                    + "	Telefone_Alt tel2,\n"
                    + "	Telefone_Alt2 tel3,\n"
                    + "	email,\n"
                    + "	observacao,\n"
                    + "	case when Status_Fornecedor = 'A' then 1 else 0 end situacao\n"
                    + "from\n"
                    + "	tbl_fornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("im"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("tel2") != null)
                            && (!rst.getString("tel2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("tel2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("tel3") != null)
                            && (!rst.getString("tel3").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 3",
                                rst.getString("tel3"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setAtivo(rst.getBoolean("situacao"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	  cod_fornecedor fornecedor,\n"
                    + "	  cod_produto_loja produto,\n"
                    + "	  cod_produto_fornecedor codexterno\n"
                    + "from tbl_produto_fornecedor\n"
                    + "	  order by 1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setCodigoExterno(rst.getString("codexterno"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Cod_Cliente id,\n"
                    + "	Cpf_Cnpj cnpj,\n"
                    + "	Rg_Ie ie,\n"
                    + "	Nome razao,\n"
                    + "	Nome_Fantasia fantasia,\n"
                    + "	Endereco,\n"
                    + "	Numero,\n"
                    + "	complemento,\n"
                    + "	Bairro,\n"
                    + "	Cidade,\n"
                    + "	Estado,\n"
                    + "	Cep,\n"
                    + "	Endereco_Cobranca endcobranca,\n"
                    + "	Numero_Cobranca numcobranca,\n"
                    + "	Telefone,\n"
                    + "	Celular,\n"
                    + "	Email,\n"
                    + "	Dt_Nascimento nascimento,\n"
                    + "	trabalho_local empresa,\n"
                    + "	profissao cargo,\n"
                    + "	salario,\n"
                    + "	observacao,\n"
                    + "	lim_cv1 limite,\n"
                    + "	Ref_Conj_Nome cjnome,\n"
                    + "	Ref_Conj_Cpf cjcpf,\n"
                    + "	Ref_Conj_Nasc cjnascimento,\n"
                    + "	Data_Cadastro cadastro,\n"
                    + "	case when Status_Cliente = 'A' then 1 else 0 end situacao\n"
                    + "from tbl_cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(Utils.acertarTexto(rst.getString("ie")));
                    imp.setRazao(Utils.acertarTexto(rst.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rst.getString("fantasia")));
                    imp.setEndereco(Utils.acertarTexto(rst.getString("endereco")));
                    imp.setNumero(Utils.acertarTexto(rst.getString("numero")));
                    imp.setComplemento(Utils.acertarTexto(rst.getString("complemento")));
                    imp.setBairro(Utils.acertarTexto(rst.getString("bairro")));
                    imp.setMunicipio(Utils.acertarTexto(rst.getString("cidade")));
                    imp.setUf(Utils.acertarTexto(rst.getString("estado")));
                    imp.setCep(rst.getString("cep"));

                    imp.setCobrancaEndereco(Utils.acertarTexto(rst.getString("endcobranca")));
                    imp.setCobrancaNumero(Utils.acertarTexto(rst.getString("numcobranca")));

                    imp.setTelefone(Utils.formataNumero(rst.getString("telefone")));
                    imp.setCelular(Utils.formataNumero(rst.getString("celular")));
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.setEmail(Utils.acertarTexto(rst.getString("email")).toLowerCase());
                    } else {
                        imp.setEmail("");
                    }
                    imp.setDataNascimento(rst.getDate("nascimento"));

                    imp.setEmpresa(Utils.acertarTexto(rst.getString("empresa")));
                    imp.setCargo(Utils.acertarTexto(rst.getString("cargo")));
                    imp.setSalario(rst.getDouble("salario"));

                    imp.setObservacao(Utils.acertarTexto(rst.getString("observacao")));
                    imp.setValorLimite(rst.getDouble("limite"));

                    imp.setNomeConjuge(Utils.acertarTexto(rst.getString("cjnome")));
                    imp.setCpfConjuge(Utils.acertarTexto(rst.getString("cjcpf")));
                    imp.setDataNascimentoConjuge(rst.getDate("cjnascimento"));
                    imp.setDataCadastro(rst.getDate("cadastro"));
                    imp.setAtivo(rst.getBoolean("situacao"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	a.Cod_All_Lancamento id,\n"
                    + "	a.Data_Emissao_Lancamento emissao,\n"
                    + "	b.Coo_Cupom cupom,\n"
                    + "	a.Ecf,\n"
                    + "	a.Valor_Lancamento valor,\n"
                    + "	a.Cliente_Lancamento idcliente,\n"
                    + "	a.Data_Vencimento_Lancamento vencimento,\n"
                    + "	c.Cpf_Cnpj cnpjcpf,\n"
                    + "	a.Obs_Lancamento obs\n"
                    + "from\n"
                    + "	tbl_all_lancamento a\n"
                    + "	join tbl_all_head b on a.Id_Documento = b.id_Documento\n"
                    + "	join tbl_cliente c on a.Cliente_Lancamento = c.Cod_Cliente \n"
                    + "where\n"
                    + "	Cliente_Lancamento != 0\n"
                    + "	and Situacao_Lancamento = 'A'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setCnpjCliente(rst.getString("cnpjcpf"));
                    imp.setObservacao(rst.getString("obs"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    /*@Override
     public List<ChequeIMP> getCheques() throws Exception {
     List<ChequeIMP> vResult = new ArrayList<>();
     try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
     try (ResultSet rst = stm.executeQuery(
     "select\n"
     + "seq, ag, cc, dcc, numero, valor,\n"
     + "nome, banco, documento, pre, fone\n"
     + "from tsl.tsm004 "
     + "where pre <> '0000-00-00'"
     )) {
     while (rst.next()) {
     ChequeIMP imp = new ChequeIMP();
     imp.setId(rst.getString("seq"));
     imp.setAgencia(rst.getString("ag"));
     imp.setConta(rst.getString("cc") + rst.getString("dcc"));
     imp.setNumeroCheque(rst.getString("numero"));
     imp.setValor(rst.getDouble("valor"));
     imp.setNome(rst.getString("nome"));
     imp.setBanco(Integer.parseInt(Utils.formataNumero(rst.getString("banco"))));
     imp.setCpf(rst.getString("documento"));
     imp.setTelefone(rst.getString("fone"));
     imp.setDate(rst.getDate("pre"));
     imp.setDataDeposito(rst.getDate("pre"));
     vResult.add(imp);
     }
     }
     }
     return vResult;
     }*/
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new IServerDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new IServerDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }


    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cpf"));

                        if (rst.getString("nomecliente") != null
                                && !rst.getString("nomecliente").trim().isEmpty()
                                && rst.getString("nomecliente").trim().length() > 45) {

                            next.setNomeCliente(rst.getString("nomecliente").substring(0, 45));
                        } else {
                            next.setNomeCliente(rst.getString("nomecliente"));
                        }

                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("Complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado"));
                        next.setEnderecoCliente(endereco);
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "select\n"
                    + "	cod_all_head id_venda,\n"
                    + "	Coo_Cupom numerocupom,\n"
                    + "	case when Status_Cupom = 'C' then 1 else 0 end cancelado,\n"
                    + "	v.Cod_Cliente idcliente,\n"
                    + "	Ecf,\n"
                    + "	Data_Emissao_Cupom data,\n"
                    + "	substring(Data_Emissao_Cupom,12,8) horainicio,\n"
                    + "	substring(Data_Emissao_Cupom,12,8) horatermino,\n"
                    + "	Valor_Liquido_Cupom subtotalimpressora,\n"
                    + "	Cpf_Consumidor cpf,\n"
                    + "	c.nome nomecliente,\n"
                    + "	c.Endereco,\n"
                    + "	c.Numero,\n"
                    + "	c.Complemento,\n"
                    + "	c.Bairro,\n"
                    + "	c.Cidade,\n"
                    + "	c.Estado \n"
                    + "from\n"
                    + "	tbl_all_head v\n"
                    + "	left join tbl_cliente c on v.Cod_Cliente = c.Cod_Cliente \n"
                    + "where\n"
                    + "	Data_Emissao_Cupom between '" + strDataInicio + "' and '" + strDataTermino + "'\n"
                    + " and v.Status_Cupom <> 'C'";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setCancelado(rst.getBoolean("cancelado"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	v.cod_all_head id_venda,\n"
                    + "	Cod_All_Item id_item,\n"
                    + "	Coo_Cupom numerocupom,\n"
                    + "	Posicao_Item_Cupom nroitem,\n"
                    + "	Cod_Interno_Produto produto,\n"
                    + "	Codigo_Barra_Produto codigobarras,\n"
                    + "	p.Unidade_Prod unidade,\n"
                    + "	i.Descricao_Produto descricao,\n"
                    + "	i.Quantidade_Produto quantidade,\n"
                    + "	i.Preco_Item precovenda,\n"
                    + "	i.Preco_Total_Item total,\n"
                    + "	case when i.Status_Cupom = 'C' then 1 else 0 end cancelado,\n"
                    + "	i.Ecf ecf,\n"
                    + "	i.Data_Emissao data\n"
                    + "from\n"
                    + "	tbl_all_item i\n"
                    + "join tbl_all_head v on\n"
                    + "	v.id_Documento = i.Id_Documento\n"
                    + "left join tbl_produto p on\n"
                    + "	p.Codigo_Prod = i.Cod_Interno_Produto\n"
                    + "where\n"
                    + "	v.Data_Emissao_Cupom between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + " and v.Status_Cupom <> 'C'";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

}
