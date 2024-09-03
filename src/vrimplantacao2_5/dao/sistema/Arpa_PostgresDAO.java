package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.importacao.ClienteContatoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Bruno
 */
public class Arpa_PostgresDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Arpa_Postgres";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.ATIVO,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CEST,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MARGEM,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.NCM,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.PRECO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CONTATOS,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.DADOS,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.VALOR_LIMITE));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    ff.tabela::varchar as id,\n"
                    + "    f.nome as descricao,\n"
                    + "    cst ,\n"
                    + "    aliquota as aliq ,\n"
                    + "    reducao\n"
                    + "from\n"
                    + "    itens_class_fiscal ff\n"
                    + "join class_fiscal f on f.codigo = ff.tabela\n"
                    + "union\n"
                    + "select distinct\n"
                    + " icms||situacaotributaria id,\n"
                    + " icms||' - '||situacaotributaria descricao,\n"
                    + " situacaotributaria cst,\n"
                    + " icms aliq,\n"
                    + " 0 reducao\n"
                    + "from produtos\n"
                    + "where class_fiscal is null")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliq"),
                            rs.getDouble("reducao")));
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
                    ""
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);
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
                    "	select distinct \n"
                    + "	departamento as merc1,\n"
                    + "	d.nome as desc1,\n"
                    + "	coalesce (grupo, departamento) as merc2, \n"
                    + "	coalesce (gr.nome, d.nome )as desc2,\n"
                    + "	case \n"
                    + "	when subgrupo  is null and grupo is null then departamento \n"
                    + "	when subgrupo  is null  and grupo notnull then grupo \n"
                    + "	else subgrupo  end as merc3,\n"
                    + "		case \n"
                    + "	when sb.nome  is null and gr.nome is null then d.nome \n"
                    + "	when sb.nome  is null  and gr.nome notnull then gr.nome \n"
                    + "	else sb.nome  end as desc3\n"
                    + "	from produtos p \n"
                    + "	left join grupo_produtos gr on gr.codigo  = p.grupo \n"
                    + "	left join subgrupo_produtos sb on sb.codigo  = p.subgrupo \n"
                    + "	left join departamentos d on d.codigo = p.departamento \n"
                    + "	where departamento notnull \n"
                    + "	order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc3"));

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
                    "with margem as (\n"
                    + "select \n"
                    + " p.codigo, \n"
                    + " case when p.precovenda = 0 then 0.1 else p.precovenda end precovenda,\n"
                    + " case when p.precocusto = 0 then 0.1 else p.precocusto end precocusto \n"
                    + "from produtos p\n"
                    + ")"
                    + "select distinct\n"
                    + "p.codigo as id_produto,\n"
                    + "descricao as descricaocompleta,\n"
                    + "departamento as merc1,\n"
                    + "ativo,\n"
                    + "estoqueminimo ,\n"
                    + "estoquemaximo ,\n"
                    + "pc2.codbarra ,\n"
                    + "departamento ,\n"
                    + "coalesce (grupo,departamento) as merc2,\n"
                    + "case \n"
                    + "when subgrupo is null and grupo is null then departamento \n"
                    + "when subgrupo is null and grupo  notnull then grupo \n"
                    + "else subgrupo end as merc3,\n"
                    + "unidade ,\n"
                    + "p.precovenda ,\n"
                    + "precocontabil ,\n"
                    + "situacaotributaria ,\n"
                    + "p.precocusto ,\n"
                    + "quantidade as estoq_inicial ,\n"
                    + "cod_ncm ,\n"
                    + "cest ,\n"
                    + "pc3.codigo_receita as natreceita,\n"
                    + "pc.cst_pis_entrada,\n"
                    + "pc.cst_cofins,\n"
                    + "((m.precovenda - m.precocusto)/m.precocusto * 100)::numeric(10,2) margem,\n"
                    + "case when p.class_fiscal is null then icms||situacaotributaria\n"
                    + "else p.class_fiscal::varchar end icms\n"
                    + "from produtos p \n"
                    + "left join pis_cofins pc on pc.produto = p.codigo \n"
                    + "left join produtos_codbarra pc2 ON pc2.produto  = p.codigo \n"
                    + "left join margem m on m.codigo = p.codigo \n "
                    + "left join pis_cofins pc3 ON pc3.produto = p.codigo"
            )) {
                 Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_produto"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id_produto"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("codbarra"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(1);
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));

                    imp.setNcm(rst.getString("cod_ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setSituacaoCadastro(rst.getInt("ativo") == 0 ? 1 : 0);

                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoq_inicial"));

                    imp.setCodMercadologico1(rst.getString("departamento"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setTipoEmbalagemCotacao(rst.getString("unidade"));
                    imp.setCustoComImposto(rst.getDouble("precocusto"));
                    imp.setCustoSemImposto(rst.getDouble("precocusto"));
                    imp.setCustoMedioComImposto(rst.getDouble("precocontabil"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                   
                   

                    String idIcms = rst.getString("icms");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);

                    imp.setPiscofinsCstDebito(rst.getString("cst_cofins"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));

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
            try (ResultSet rs = stm.executeQuery(
                    "	select\n"
                    + "	cd_produto as id_produto,\n"
                    + "	cd_fornecedor as id_fornecedor,\n"
                    + "	no_fornecedor as codigoexterno,\n"
                    + "	fracao as qtd\n"
                    + "from\n"
                    + "	no_fornecedor nf "
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtd"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "produto as id_produto, \n"
                    + "c.codbarra as ean,\n"
                    + "p.unidade \n"
                    + "from produtos_codbarra c\n"
                    + "join produtos p on c.codigo  = p.codigo "
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));

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
                    + "codigo as id_fornecedor,\n"
                    + "nome as razao, \n"
                    + "fantasia as fantasia,\n"
                    + "case \n"
                    + "when f.cnpj = '' then  codigo::text \n"
                    + "when f.cnpj is null then   codigo::text\n"
                    + "else cnpj end,\n"
                    + "inscricaoest as ie,\n"
                    + "endereco as endereco,\n"
                    + "numero ,\n"
                    + "bairro ,\n"
                    + "c.cidade as municipio,\n"
                    + "c.uf as uf,\n"
                    + "f.cep ,\n"
                    + "complemento ,\n"
                    + "datacad as data_cadastro,\n"
                    + "observacoes ,\n"
                    + "fone,\n"
                    + "ativo,\n"
                    + "ramal ,\n"
                    + "simples ,\n"
                    + "produtorrural ,\n"
                    + "fax,\n"
                    + "email \n"
                    + "from\n"
                    + "fornecedores f\n"
                    + "left join  cidades c on f.cep = c.cep"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_fornecedor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    if (rst.getBoolean("simples") == true) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                    }
                    if (rst.getBoolean("produtorrural") == true) {
                        imp.setTipoEmpresa(TipoEmpresa.PRODUTOR_RURAL_FISICA);
                    }
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));
                    imp.setObservacao(rst.getString("observacoes"));

                    imp.setTel_principal(rst.getString("fone"));

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
                    "select\n"
                    + "	codigo as id, \n"
                    + "	fornecedor as id_fornecedor,\n"
                    + "	documento ,\n"
                    + "	data_emissao as emissao,\n"
                    + "	vencimento,\n"
                    + " valor,\n"
                    + "	complemento as observacao\n"
                    + "from\n"
                    + "	apagar"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("emissao"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getString("observacao"));

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
                    "select \n"
                    + "codigo as id,  \n"
                    + "nome as razao,  \n"
                    + "fantasia as fantasia, \n"
                    + "numero as numero,  \n"
                    + "endereco , \n"
                    + "cpf_cnpj , \n"
                    + "inscricaoest , \n"
                    + "case  \n"
                    + "when status  = 'I' then '0' \n"
                    + "else '1' end as status , \n"
                    + "bairro, \n"
                    + "c.cep , \n"
                    + "c2.cidade , \n"
                    + "c2.uf , \n"
                    + "complemento , \n"
                    + "fone, \n"
                    + "fone2 , \n"
                    + "fone_tipo , \n"
                    + "fone2_tipo , \n"
                    + "c.contato , \n"
                    + "cr.orgao_emissor , \n"
                    + "cc.credito , \n"
                    + "c.email  \n"
                    + "from \n"
                    + "clientes c  \n"
                    + "left join cidades c2 on c2.cep = c.cep  \n"
                    + "left join clientes_rg cr on cr.cliente  = c.codigo \n"
                    + "left join dados_financeiros_cliente cc on cc.cliente = c.codigo "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setAtivo(rst.getBoolean("status"));
                    imp.setObservacao(rst.getString("contato"));
                    imp.setValorLimite(rst.getDouble("credito"));

                    imp.setUf(rst.getString("uf"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoest"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setComplemento(rst.getString("complemento"));

                    if (rst.getInt("orgao_emissor") == 0) {
                        imp.setOrgaoemissor("SSP");
                    }
                    imp.setCelular(rst.getString("fone2"));

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
                    "select * from contas_a_receber_recebidas(0,'2000-01-01','9999-12-31','P','P',CURRENT_DATE) p\n"
                    + " where quite = 'N'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setIdCliente(rst.getString("cliente"));
                    imp.setNumeroCupom(rst.getString("nota"));
                    imp.setParcela(rst.getInt("parcela") == 0 ? 1 : rst.getInt("parcela"));
                    imp.setDataEmissao(rst.getDate("data_venda"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor_original"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

}
