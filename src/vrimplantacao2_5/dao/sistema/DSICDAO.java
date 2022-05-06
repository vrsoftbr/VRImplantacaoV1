package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Alan
 */
public class DSICDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "DSIC";
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
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.DADOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	 csosn_codigo id,\n"
                    + "	 csosn_descricao descricao,\n"
                    + "	 csosn_clasfiscaltabb cst,\n"
                    + "	 0 icms,\n"
                    + "	 0 reducao\n"
                    + "from\n"
                    + "	 csosn\n"
                    + "order by 1")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("icms"),
                            rs.getDouble("reducao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	m1.grp_id merc1,\n"
                    + "	m1.grp_descricao descmerc1,\n"
                    + "	m2.sgr_id merc2,\n"
                    + "	m2.sgr_descricao descmerc2,\n"
                    + "	m2.sgr_id merc3,\n"
                    + "	m2.sgr_descricao descmerc3\n"
                    + "from\n"
                    + "	grupo_produto m1\n"
                    + "join subgrupo_produto m2 on m1.grp_id = m2.grp_id\n"
                    + "order by 1,3")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	pro_codigo id,\n"
                    + "	pro_codigobarra ean,\n"
                    + "	pro_descricao descricao,\n"
                    + "	un.unp_siglautilizada tipo_emb,\n"
                    + " grp_id merc1,\n"
                    + "	sgr_id merc2,\n"
                    + "	pro_estoqueminimo est_minimo,\n"
                    + "	pro_qtdestoque estoque,\n"
                    + "	pro_precoreposicao custo,\n"
                    + "	pro_mrglucro margem,\n"
                    + "	pro_precovenda precovenda,\n"
                    + "	t.tip_codigo ncm,\n"
                    + "	pro_cest cest,\n"
                    + "	case when pro_pesavel = true then 1 else 0 end e_balanca,\n"
                    + "	case when pro_regdel is null then 1 else 0 end ativo,\n"
                    + "	p.csosn_codigo_estadual id_icms,\n"
                    + " pro_qtddias_validade validade\n"
                    + "from\n"
                    + "	produto p\n"
                    + "	  left join unidade_produto un on un.unp_id = p.unp_id \n"
                    + "	  left join tipi t on p.tip_id = t.tip_id \n"
                    + "where\n"
                    + "	emp_id = " + getLojaOrigem() + "\n"
                    + "order by 1")) {
                 Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());

                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    imp.setTipoEmbalagemCotacao(rs.getString("tipo_emb"));
                    imp.setTipoEmbalagemVolume(rs.getString("tipo_emb"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setEstoqueMinimo(rs.getDouble("est_minimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setSituacaoCadastro(rs.getInt("ativo"));

                    imp.setIcmsConsumidorId(rs.getString("id_icms"));
                    imp.setIcmsDebitoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsConsumidorId());

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    ProdutoBalancaVO balanca = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

                    if (balanca != null) {
                        imp.setEan(String.valueOf(balanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(balanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(balanca.getValidade() > 1
                                ? balanca.getValidade() : 0);
                    } else {
                        imp.setValidade(Utils.stringToInt(rs.getString("validade")));
                        imp.seteBalanca(rs.getString("e_balanca").trim().equals("S"));
                        imp.setTipoEmbalagem(rs.getString("tipo_emb"));
                    }

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
                    + "	pro_codigo id,\n"
                    + "	pro_codigobarra ean,\n"
                    + " 1 quantidade\n"
                    + "from\n"
                    + "	produto p\n"
                    + "order by 1")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("quantidade"));

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
                    + "	 c.clf_id id,\n"
                    + "	 c.clf_nome razao,\n"
                    + "	 cf.cff_apelido as fantasia,\n"
                    + "	 c.clf_statusfj tipo_pessoa,\n"
                    + "	 cf.cff_cpf as cpf_cnpj,\n"
                    + "	 cf.cff_rg as rg_ie,\n"
                    + "	 o.out_endereco endereco,\n"
                    + "	 o.out_complemento complemento,\n"
                    + "	 o.out_numero numero,\n"
                    + "	 o.out_bairro bairro,\n"
                    + "	 cd.cid_nome cidade,\n"
                    + "	 cd.cid_uf uf,\n"
                    + "	 o.out_cep cep,\n"
                    + "	 c.clf_telefone1 telefone\n"
                    + "from\n"
                    + "	 clifor_pf cf,\n"
                    + "	 cliente_fornecedor c\n"
                    + "left join outraslocalidades_clifor o on o.clf_id = c.clf_id and o.out_tipoendereco = 'N'::bpchar\n"
                    + "left join cidade cd on o.cid_id = cd.cid_id\n"
                    + "left join paises p on o.pai_id = p.pai_id\n"
                    + "where\n"
                    + "	 c.clf_id = cf.clf_id\n"
                    + "	 and c.clf_regdel is null\n"
                    + "	 and c.clf_cliforambos = 'F'\n"
                    + "union\n"
                    + "select\n"
                    + "	 c.clf_id id,\n"
                    + "	 cj.cfj_razaosocial as razao,\n"
                    + "	 c.clf_nome as fantasia,\n"
                    + "	 c.clf_statusfj tipo_pessoa,\n"
                    + "	 cj.cfj_cnpj as cpf_cnpj,\n"
                    + "	 cj.cfj_inscestadual as rg_ie,\n"
                    + "	 o.out_endereco endereco,\n"
                    + "	 o.out_complemento complemento,\n"
                    + "	 o.out_numero numero,\n"
                    + "	 o.out_bairro bairro,\n"
                    + "	 cd.cid_nome cidade,\n"
                    + "	 cd.cid_uf uf,\n"
                    + "	 o.out_cep cep,\n"
                    + "	 c.clf_telefone1 telefone\n"
                    + "from\n"
                    + "	 clifor_pj cj,\n"
                    + "	 cliente_fornecedor c\n"
                    + "left join outraslocalidades_clifor o on o.clf_id = c.clf_id and o.out_tipoendereco = 'N'::bpchar\n"
                    + "left join cidade cd on o.cid_id = cd.cid_id\n"
                    + "left join paises p on o.pai_id = p.pai_id\n"
                    + "where\n"
                    + "	 c.clf_id = cj.clf_id\n"
                    + "	 and c.clf_regdel is null\n"
                    + "	 and c.clf_cliforambos = 'F'\n"
                    + "order by 1")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cpf_cnpj"));
                    imp.setIe_rg(rs.getString("rg_ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));

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
                    "select\n"
                    + "	clf_id idfornecedor,\n"
                    + "	pro_id idproduto\n"
                    + "from\n"
                    + "	fornecedores_e_seus_produtos\n"
                    + "order by 1,2")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cp.ctp_id id,\n"
                    + "	cp.clf_id idfornecedor,\n"
                    + "	cf.cfj_cnpj cnpj,\n"
                    + "	ctp_dtemissao emissao,\n"
                    + "	ctp_dtemissao entrada,\n"
                    + "	ctp_dtvencimento vencimento,\n"
                    + "	ctp_valdocumento valor,\n"
                    + "	ctp_historico observacao\n"
                    + "from\n"
                    + "	contas_depagamento cp\n"
                    + "	left join clifor_pj cf on cf.clf_id = cp.clf_id\n"
                    + "where\n"
                    + "	emp_id = " + getLojaOrigem() + "\n"
                    + "	and ctp_dtpagamento is null"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataEntrada(rs.getDate("entrada"));
                    imp.setVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));

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
                    "select\n"
                    + "	 c.clf_id id,\n"
                    + "	 c.clf_nome razao,\n"
                    + "	 cf.cff_apelido as fantasia,\n"
                    + "	 c.clf_statusfj tipo_pessoa,\n"
                    + "	 cf.cff_cpf as cpf_cnpj,\n"
                    + "	 cf.cff_rg as rg_ie,\n"
                    + "	 o.out_endereco endereco,\n"
                    + "	 o.out_complemento complemento,\n"
                    + "	 o.out_numero numero,\n"
                    + "	 o.out_bairro bairro,\n"
                    + "	 cd.cid_nome cidade,\n"
                    + "	 cd.cid_uf uf,\n"
                    + "	 o.out_cep cep,\n"
                    + "  c.clf_telefone1 telefone\n"
                    + "from\n"
                    + "	 clifor_pf cf,\n"
                    + "	 cliente_fornecedor c\n"
                    + "left join outraslocalidades_clifor o on o.clf_id = c.clf_id and o.out_tipoendereco = 'N'::bpchar\n"
                    + "left join cidade cd on o.cid_id = cd.cid_id\n"
                    + "left join paises p on o.pai_id = p.pai_id\n"
                    + "where\n"
                    + "	 c.clf_id = cf.clf_id\n"
                    + "	 and c.clf_regdel is null\n"
                    + "	 and c.clf_cliforambos = 'C'\n"
                    + "union\n"
                    + "select\n"
                    + "	 c.clf_id id,\n"
                    + "	 cj.cfj_razaosocial as razao,\n"
                    + "	 c.clf_nome as fantasia,\n"
                    + "	 c.clf_statusfj tipo_pessoa,\n"
                    + "	 cj.cfj_cnpj as cpf_cnpj,\n"
                    + "	 cj.cfj_inscestadual as rg_ie,\n"
                    + "	 o.out_endereco endereco,\n"
                    + "	 o.out_complemento complemento,\n"
                    + "	 o.out_numero numero,\n"
                    + "	 o.out_bairro bairro,\n"
                    + "	 cd.cid_nome cidade,\n"
                    + "	 cd.cid_uf uf,\n"
                    + "	 o.out_cep cep,\n"
                    + "  c.clf_telefone1 telefone\n"
                    + "from\n"
                    + "	 clifor_pj cj,\n"
                    + "	 cliente_fornecedor c\n"
                    + "left join outraslocalidades_clifor o on o.clf_id = c.clf_id and o.out_tipoendereco = 'N'::bpchar\n"
                    + "left join cidade cd on o.cid_id = cd.cid_id\n"
                    + "left join paises p on o.pai_id = p.pai_id\n"
                    + "where\n"
                    + "	 c.clf_id = cj.clf_id\n"
                    + "	 and c.clf_regdel is null\n"
                    + "	 and c.clf_cliforambos = 'C'\n"
                    + "order by 1")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(Utils.formataNumero(rs.getString("rg_ie")));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	 ctr_id id,\n"
                    + "	 ctr_dtemissao emissao,\n"
                    + "	 clf_id idcliente,\n"
                    + "	 ctr_numdocto numcupom,\n"
                    + "	 ctr_valdocumento valor,\n"
                    + "	 substring(ctr_parcela,5,4)::int parcela,\n"
                    + "	 ctr_dtvencimento vencimento,\n"
                    + "	 ctr_historico observacao\n"
                    + "from\n"
                    + "	 contas_derecebimento cr\n"
                    + "where\n"
                    + "	 emp_id = " + getLojaOrigem() + "\n"
                    + "	 and ctr_dtrecebimento is null\n"
                    + "	 and clf_id is not null"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(rs.getString("numcupom"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

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
        return new DSICDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new DSICDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
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
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numcupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setNumeroSerie(rst.getString("serie"));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setCpf(rst.getString("cpf_cnpj"));
                        next.setCancelado(rst.getBoolean("cancelado"));
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
                    + "	cfe_id id_venda,\n"
                    + "	case when cfe_numerocfe is null\n"
                    + "     then cfe_id else cfe_numerocfe end numcupom,\n"
                    + "	1 ecf,\n"
                    + "	substring(cfe_dtautorizacao::varchar,1,11) as data,\n"
                    + "	substring(cfe_dtautorizacao::varchar,12,8) hora,\n"
                    + "	cfe_serie serie,\n"
                    + "	cfe_valtotalprodutos valor,\n"
                    + "	cfe_valdesconto desconto,\n"
                    + "	v.clf_id id_cliente,\n"
                    + "	c.cfj_razaosocial nomecliente,\n"
                    + "	c.cfj_cnpj cpf_cnpj,\n"
                    + "	case when cfe_motivocancelamento is null\n"
                    + "     then 0 else 1 end cancelado\n"
                    + "from\n"
                    + "	cfe_emitida v\n"
                    + "	left join clifor_pj c on c.clf_id = v.clf_id\n"
                    + "where\n"
                    + "	v.emp_id = " + idLojaCliente + "\n"
                    + "	and cfe_dtautorizacao between '" + strDataInicio + "' and '" + strDataTermino + "'";
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

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
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
                        next.setSequencia(rst.getInt("nritem"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setValorDesconto(rst.getDouble("desconto"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "Select\n"
                    + "	vi.cfe_id id_venda,\n"
                    + "	cfi_id id_item,\n"
                    + "	cfi_sequenciaitem nritem,\n"
                    + "	vi.pro_id id_produto,\n"
                    + "	vi.cfi_unidade unidade,\n"
                    + "	cfi_descricao descricao,\n"
                    + "	p.pro_codigobarra codigobarras,\n"
                    + "	cfi_quantidade quantidade,\n"
                    + "	cfi_precounitario precovenda,\n"
                    + "	cfi_valacrescimo acrescimo,\n"
                    + "	cfi_valdesconto desconto\n"
                    + "from\n"
                    + "	cfe_emitida_itens vi\n"
                    + "	join cfe_emitida v on v.cfe_id = vi.cfe_id\n"
                    + "	left join produto p on p.pro_id = vi.pro_id\n"
                    + "where\n"
                    + "	v.emp_id = " + idLojaCliente + "\n"
                    + "	and v.cfe_dtautorizacao between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "order by 1,2";
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
