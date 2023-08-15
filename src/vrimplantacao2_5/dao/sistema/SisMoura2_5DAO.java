package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 *
 * @author Alan
 */
public class SisMoura2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SisMoura";
    }

    public boolean apenasProdutoAtivo = false;

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
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
                OpcaoProduto.VOLUME_QTD
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
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"select\n"
                    + "	Codigo id,\n"
                    + "	Descricao,\n"
                    + "	'0' cst,\n"
                    + "	COALESCE (Icms,0) aliq,\n"
                    + "	'0' red\n"
                    + "from\n"
                    + "	Taxa_Tributaria"*/
                    "select\n"
                    + "	Tipo_Regra_Imposto id,\n"
                    + "	CONCAT(Tipo_Regra_Imposto,'-',cst,'-',Aliquota_ICMS) descricao,\n"
                    + "	cast (CST as int) cst,\n"
                    + "	Aliquota_ICMS aliq,\n"
                    + "	0 red\n"
                    + "from\n"
                    + "	Fiscal_Regra_Imposto\n"
                    + "where\n"
                    + "	UF_Origem = 'SP' and UF_Destino = 'SP'\n"
                    + "order by 1"
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "	Grupo merc1,\n"
                    + "	GRUPO.DESCRICAO AS descmerc1,\n"
                    + "	SubGrupo merc2,\n"
                    + "	SUBGRUPO.DESCRICAO AS descmerc2\n"
                    + "FROM\n"
                    + "	Produto\n"
                    + "	INNER JOIN GRUPO_PRODUTO AS GRUPO ON GRUPO.Codigo = Produto.Grupo\n"
                    + "	INNER JOIN SubGrupo AS SUBGRUPO ON SUBGRUPO.Codigo = Produto.SubGrupo\n"
                    + "ORDER BY GRUPO, SUBGRUPO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("merc2"));
                    imp.setMerc3Descricao(rst.getString("descmerc2"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	codigo,\n"
                    + "	ean,\n"
                    + "	unidade\n"
                    + "from\n"
                    + "	(\n"
                    + "	select\n"
                    + "		codigo,\n"
                    + "		case when Balanca = 1 then Codigo else replace(codigo_barra,'C','') end ean,\n"
                    + "		p.unidade\n"
                    + "	from\n"
                    + "		produto p\n"
                    + "union\n"
                    + "	select\n"
                    + "		pb.produto codigo,\n"
                    + "		pb.codigo_barra ean,\n"
                    + "		p.unidade\n"
                    + "	from\n"
                    + "		produto_barra pb\n"
                    + "	join produto p on pb.produto = p.codigo) eans\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("ean"));
                    //imp.setQtdEmbalagem(1);
                    imp.setTipoEmbalagem(rst.getString("unidade"));

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
                    //    "declare @primeirocadastro date;\n"
                    //    + "select @primeirocadastro = min(p.Data_Cadastro) from produto p\n"
                    "select\n"
                    + "p.codigo id,\n"
                    + "p.Taxa_Tributaria,\n"
                    + "p.nome descricaocompleta,\n"
                    + "case when ltrim(rtrim(p.Descricao_Reduzida)) = '' then p.nome else p.Descricao_Reduzida end descricaoreduzida,\n"
                    + "p.nome descricaogondola,\n"
                    + "p.status id_situacaocadastral,\n"
                    //    + "isnull(p.Data_Cadastro, @primeirocadastro) datacadastro,\n"
                    + "p.grupo mercadologico1,\n"
                    + "isnull(p.SubGrupo, 1) mercadologico2,\n"
                    + "p.ncm,\n"
                    + "p.Codigo_CEST cest,\n"
                    + "c.Codigo_CEST,\n"
                    + "p.Margem,\n"
                    + "p.Quantidade qtdEmbalagem,\n"
                    + "case when p.Balanca = 1 then cast(p.Codigo as varchar) else replace(cast(p.codigo_barra as varchar),'C','') end ean,\n"
                    + "p.balanca e_balanca,\n"
                    + "p.Validade,\n"
                    + "p.Unidade id_tipoembalagem,\n"
                    + "p.Peso_Produto peso_bruto,\n"
                    + "p.Peso_Produto peso_liquido,\n"
                    + "fs.ST_PIS piscof_debito,\n"
                    + "fs.ST_PIS_Entrada piscof_credito,\n"
                    + "p.Codigo_Incidencia_Monofasica nat_rec,\n"
                    + "imp.tipo_regra_imposto id_icms,"
                    + "est.Qtde estoque,\n"
                    + "p.Estoque_maximo,\n"
                    + "p.Estoque_minimo,\n"
                    + "p.Preco_Produto preco,\n"
                    + "p.Preco_Custo custo,\n"
                    + "p.Inativo,\n"
                    + "p.Unidade\n"
                    + "from\n"
                    + "Produto p\n"
                    + "left join Produto_Regra_Imposto imp on p.Codigo = imp.Produto and imp.empresa = " + getLojaOrigem() + "\n"
                    + "left join Fiscal_Regra_Imposto fs on imp.Tipo_Regra_Imposto = fs.Tipo_Regra_Imposto and UF_Origem = UF_Destino\n"
                    + "left join Estoque est on p.Codigo = est.Produto and est.Deposito = 1 \n"
                    + "left join Produto_CEST c on c.Codigo = p.Codigo_CEST\n"
                    + (apenasProdutoAtivo == true ? " where p.Inativo = 'N'" : "")
                    + "order by p.codigo"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca((rst.getInt("e_balanca") == 1));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida((rst.getString("descricaoreduzida") == null ? imp.getDescricaoCompleta() : rst.getString("descricaoreduzida")));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));

                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    //  imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSituacaoCadastro(("N".equals(rst.getString("Inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
                    imp.setTipoEmbalagem(rst.getString("Unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setValidade(rst.getInt("Validade"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setMargem(rst.getDouble("Margem"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("Estoque_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("Estoque_maximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("Codigo_CEST"));

                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(rst.getString("id_icms"));

                    imp.setPiscofinsCstDebito(rst.getInt("piscof_debito"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscof_credito"));
                    imp.setPiscofinsNaturezaReceita(Integer.parseInt(Utils.formataNumero(rst.getString("nat_rec"))));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Codigo,\n"
                    + "	Produto,\n"
                    + "	Validade_Inicial,\n"
                    + "	Validade_Final,\n"
                    + "	Preco_Produto,\n"
                    + "	Preco_Promocao\n"
                    + "from\n"
                    + "	Produto_Promocao\n"
                    + "where\n"
                    + "	Validade_Final > GETDATE()"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("Produto"));
                    imp.setDataInicio(rst.getDate("Validade_Inicial"));
                    imp.setDataFim(rst.getDate("Validade_Final"));
                    imp.setPrecoOferta(rst.getDouble("Preco_Promocao"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);

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
                    "select\n"
                    + "	p.Codigo id,\n"
                    + "	p.cpf cnpj,\n"
                    + "	p.RG ie,\n"
                    + "	p.Nome razao,\n"
                    + "	p.Nome_Fantasia fantasia,\n"
                    + "	p.Endereco_Nome endereco,\n"
                    + "	p.Numero,\n"
                    + "	p.Complemento,\n"
                    + "	p.Bairro,\n"
                    + "	cast(cd.Codigo_Cidade_IBGE as integer) id_municipio,\n"
                    + "	cd.Codigo_UF_IBGE id_estado,\n"
                    + "	p.Cep,\n"
                    + "	cast(p.Data_Cadastro as date) dataCadastro,\n"
                    + "	p.Endereco_Pagamento cob_endereco,\n"
                    + "	0 as cob_numero,\n"
                    + "	p.Bairro_Pagamento cob_bairro,\n"
                    + "	cast(cob_cd.Codigo_Cidade_IBGE as integer) cob_id_municipio,\n"
                    + "	cob_cd.Estado cob_id_estado,\n"
                    + "	p.Cep_Pagamento cob_cep,\n"
                    + "	p.Fone fone1,\n"
                    + "	p.Fone2,\n"
                    + "	p.Contato fone3,\n"
                    + "	p.Fax,\n"
                    + "	coalesce(p.Email, p.email_utilizado, p.Emails_Promocionais) email,\n"
                    + "	p.Obs,\n"
                    + "	case when p.Inativo = 'N' then 1 else 0 end ativo\n"
                    + "from\n"
                    + "	Fornecedor f\n"
                    + "	join Pessoa p on f.Pessoa = p.Codigo\n"
                    + "	left join Cidade cd on p.Cidade = cd.Codigo\n"
                    + "	left join Cidade cob_cd on p.Cidade = cob_cd.Codigo\n"
                    + "order by p.Codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setIbge_municipio(rst.getInt("id_municipio"));
                    imp.setIbge_uf(rst.getInt("id_estado"));

                    imp.setCob_endereco(rst.getString("cob_endereco"));
                    imp.setCob_numero(rst.getString("cob_numero"));
                    imp.setCob_bairro(rst.getString("cob_bairro"));
                    imp.setCob_ibge_municipio(rst.getInt("cob_id_municipio"));
                    imp.setCob_uf(rst.getString("cob_id_estado"));
                    imp.setCob_cep(rst.getString("cob_cep"));

                    imp.setTel_principal(rst.getString("fone1"));

                    if ((rst.getString("Fone2") != null)
                            && (!rst.getString("Fone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("Fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fone3") != null)
                            && (!rst.getString("fone3").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 3",
                                rst.getString("Fone3"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "FAX",
                                rst.getString("Fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email").toLowerCase()
                        );
                    }

                    imp.setDatacadastro(rst.getDate("dataCadastro"));
                    imp.setObservacao(rst.getString("Obs"));
                    imp.setAtivo(rst.getBoolean("ativo"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select DISTINCT\n"
                    + "	Fornecedor id_fornecedor,\n"
                    + "	Produto id_produto,\n"
                    + "	Codigo_Produto_Fornecedor cod_externo\n"
                    + "from\n"
                    + "	Produto_Fornecedor\n"
                    + "order by Fornecedor, Produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("cod_externo"));
                    //imp.setQtdEmbalagem(rst.getDouble(""));

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
                    "select\n"
                    + "	p.Codigo id,\n"
                    + "	p.Nome,\n"
                    + "	p.Endereco,\n"
                    + "	p.Numero,\n"
                    + "	p.Complemento,\n"
                    + "	p.Bairro,\n"
                    + "	cd.Estado uf,\n"
                    + "	cd.Codigo_UF_IBGE,\n"
                    + "	cd.Codigo_Cidade_IBGE id_municipio,\n"
                    + "	cd.Cidade,\n"
                    + "	p.Cep,\n"
                    + "	p.Fone fone1,\n"
                    + "	p.RG inscricaoestadual,\n"
                    + "	p.cpf cnpj,\n"
                    + "	case p.Sexo when 'M' then 2 else 1 end Sexo,\n"
                    + "	p.Data_Cadastro datacadastro,\n"
                    + "	p.Email,\n"
                    + "	p.Limite_Credito limite,\n"
                    + "	p.Fax,\n"
                    + "	case  when p.Inativo = 'N' then 1 else 0 end id_situacaocadastro,\n"
                    + "	p.Inativo,\n"
                    + "	p.Fone2 telefone2,\n"
                    + "	p.Observacao,\n"
                    + "	p.Data_Nasc datanascimento,\n"
                    + "	p.Pai nomePai,\n"
                    + "	p.Mae nomeMae,\n"
                    + "	p.Empresa,\n"
                    + "	p.Fone_Trabalho telEmpresa,\n"
                    + "	null as cargo,\n"
                    + "	null as enderecoEmpresa,\n"
                    + "	0 as salario,\n"
                    + "	p.Estado_Civil estadocivil,\n"
                    + "	p.Conjuge\n"
                    + "from\n"
                    + "	Cliente c\n"
                    + "	join Pessoa p on c.Pessoa = p.Codigo\n"
                    + "	left join Cidade cd on p.Cidade = cd.Codigo\n"
                    + "order by p.Codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("Nome"));
                    imp.setFantasia(rst.getString("Nome"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setAtivo(rst.getBoolean("id_situacaocadastro"));
                    imp.setUf(rst.getString("uf"));
                    imp.setUfIBGE(rst.getInt("Codigo_UF_IBGE"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setMunicipioIBGE(rst.getInt("id_municipio"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setTelefone(Utils.formataNumero(rst.getString("fone1")));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setSexo((rst.getInt("Sexo") == 1 ? TipoSexo.FEMININO : TipoSexo.MASCULINO));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmail(rst.getString("Email") == null ? "" : rst.getString("Email").toLowerCase());
                    imp.setValorLimite(rst.getDouble("limite"));

                    imp.setObservacao(rst.getString("Observacao"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomePai(rst.getString("nomePai"));
                    imp.setNomeMae(rst.getString("nomeMae"));
                    imp.setNomeConjuge(rst.getString("Conjuge"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setEmpresa(rst.getString("Empresa"));
                    imp.setEmpresaTelefone(rst.getString("telEmpresa"));
                    imp.setEmpresaEndereco(rst.getString("enderecoEmpresa"));
                    imp.setSalario(rst.getDouble("salario"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                rst.getString("Fax"),
                                null,
                                null
                        );
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	r.Codigo,\n"
                    + "	r.Codigo_Cliente,\n"
                    + "	r.Numero_Documento,\n"
                    + "	r.Doc_Merc,\n"
                    + "	r.N_Doc_Merc,\n"
                    + "	r.Doc_Bancario,\n"
                    + "	r.N_Doc_Bancario,\n"
                    + "	r.Parcela,\n"
                    + "	r.Data_Emissao,\n"
                    + "	r.Data_Vencimento,\n"
                    + "	r.Valor,\n"
                    + "	r.Observacao,\n"
                    + "	r.Valor_Recebido,\n"
                    + "	r.PDV,\n"
                    + "	r.Caixa,\n"
                    + "	r.Data_Baixa\n"
                    + "from\n"
                    + "	Contas_Receber r\n"
                    + "where\n"
                    + "	r.Empresa = " + getLojaOrigem() + "\n"
                    + "	and r.Data_Baixa is null"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("Codigo"));
                    imp.setIdCliente(rst.getString("Codigo_Cliente"));
                    imp.setNumeroCupom(rst.getString("Numero_Documento"));
                    imp.setParcela(rst.getInt("Parcela"));
                    imp.setDataEmissao(rst.getDate("Data_Emissao"));
                    imp.setDataVencimento(rst.getDate("Data_Vencimento"));
                    imp.setValor(rst.getDouble("Valor"));
                    imp.setObservacao(rst.getString("Observacao"));
                    imp.setEcf(rst.getString("PDV"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Codigo as id, \n"
                    + "	Codigo_Fornecedor as id_fornecedor,\n"
                    + "	N_Doc_Merc as numeroDocumento,\n"
                    + "	Data_Emissao as dataemissao,\n"
                    + "	Parcela ,\n"
                    + "	Observacao as observacao,\n"
                    + "	Data_Vencimento as vencimento,\n"
                    + "	valor as valor\n"
                    + "from\n"
                    + "	contas_pagar\n"
                    + "	where Pago = 'N'"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("numeroDocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setObservacao("PARCELA " + rst.getString("parcela") + " OBS " + rst.getString("observacao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Codigo,\n"
                    + "	Nome_Cliente,\n"
                    + "	Fone_Cliente,\n"
                    + "	CPF,\n"
                    + "	Venda,\n"
                    + "	Valor_Cheque,\n"
                    + "	Banco,\n"
                    + "	Cheque,\n"
                    + "	Vencimento,\n"
                    + "	Observacao,\n"
                    + "	Agencia,\n"
                    + "	Conta,\n"
                    + "	Data_Cadastro\n"
                    + "from\n"
                    + "	Cheques\n"
                    + "where\n"
                    + "	CodEmpresa = " + getLojaOrigem() + "\n"
                    + "	and Baixa_Data is null"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("Codigo"));
                    imp.setNome(rst.getString("Nome_Cliente"));
                    imp.setCpf(rst.getString("CPF"));
                    imp.setValor(rst.getDouble("Valor_Cheque"));
                    imp.setNumeroCheque(rst.getString("Cheque"));
                    imp.setBanco(rst.getInt("Banco"));
                    imp.setAgencia(rst.getString("Agencia"));
                    imp.setConta(rst.getString("Conta"));
                    imp.setNumeroCupom(rst.getString("Venda"));
                    imp.setObservacao(rst.getString("Observacao"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("Data_Cadastro"));
                    imp.setAlinea(0);

                    result.add(imp);
                }
            }
        }
        return result;
    }

}
