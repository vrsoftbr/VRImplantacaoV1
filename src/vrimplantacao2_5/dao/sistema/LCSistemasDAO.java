package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;

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
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/*
 *
 * @author Wagner
 *
 */
public class LCSistemasDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean geraCodigoAtacado = false;

    public void setGeraCodigoAtacado(boolean geraCodigoAtacado) {
        this.geraCodigoAtacado = geraCodigoAtacado;
    }

    @Override
    public String getSistema() {
        return "LCSistemas";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_CONTROLADA,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.NUMERO,
                OpcaoFornecedor.COMPLEMENTO,
                OpcaoFornecedor.BAIRRO,
                OpcaoFornecedor.MUNICIPIO,
                OpcaoFornecedor.UF,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.DATA_CADASTRO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.OBSERVACAO));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.RAZAO,
                OpcaoCliente.FANTASIA,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.ENDERECO_EMPRESA,
                OpcaoCliente.BAIRRO_EMPRESA,
                OpcaoCliente.COMPLEMENTO_EMPRESA,
                OpcaoCliente.MUNICIPIO_EMPRESA,
                OpcaoCliente.UF_EMPRESA,
                OpcaoCliente.CEP_EMPRESA,
                OpcaoCliente.TELEFONE_EMPRESA,
                OpcaoCliente.DATA_ADMISSAO,
                OpcaoCliente.CARGO,
                OpcaoCliente.SALARIO,
                OpcaoCliente.NOME_CONJUGE,
                OpcaoCliente.DATA_NASCIMENTO_CONJUGE,
                OpcaoCliente.NOME_PAI,
                OpcaoCliente.NOME_MAE,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " g.id,\n"
                    + " g.nome,\n"
                    + " c.codigotributario cst,\n"
                    + " g.icms_saida_aliquota aliquota,\n"
                    + " g.icms_saida_aliquota_red_base_calc redz\n"
                    + "from grupotributacao g\n"
                    + "join cst c on c.id = g.id_cst"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("nome"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("redz")));
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        String sql;
        sql = "select \n"
                + " id,\n"
                + " codigo ean,\n"
                + " qtd_embalagem\n"
                + "from produto \n"
                + "where \n"
                + " codigo <> ''\n"
                + " and\n"
                + " length(codigo) >= 7";
        if (geraCodigoAtacado) {
            sql = "select \n"
                    + " id,\n"
                    + " qtd_minimapv2 qtde\n"
                    + "from produto\n"
                    + "where qtd_minimapv2 > 1";
        }

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(sql)) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    if (geraCodigoAtacado) {
                        String sistema = (getSistema() + " - " + getLojaOrigem());
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(sistema, getLojaOrigem(), rs.getString("id"));

                        if (codigoAtual > 0) {
                            imp.setImportId(rs.getString("id"));
                            imp.setEan("99999" + String.valueOf(codigoAtual));
                            imp.setQtdEmbalagem(rs.getInt("qtde"));
                            result.add(imp);
                        }

                    } else {
                        imp.setImportId(rs.getString("id"));
                        imp.setEan(rs.getString("ean"));
                        imp.setQtdEmbalagem(1);

                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " p.id,\n"
                    + " p.codigo_barras ean,\n"
                    + " p.nome descricaocompleta,\n"
                    + " p.id_categoria mercid1,\n"
                    + " p.id_subcategoria mercid2,\n"
                    + " p.id_subcategoria mercid3,\n"
                    + " upper(u.descricao) tipoEmbalagem,\n"
                    + " p.pode_balanca ebalanca,\n"
                    + " SUBSTRING(p.datahora_cadastro,1,10) datacadastro,\n"
                    + " SUBSTRING(p.datahora_alteracao,1,10) dataalteracao,\n"
                    + " p.preco_compra,\n"
                    + " p.preco_custo,\n"
                    + " p.custo_medio,\n"
                    + " p.preco_venda,\n"
                    + " p.margem_ideal,\n"
                    + " p.margem_lucro,\n"
                    + " p.estoque,\n"
                    + " p.estoque_minimo,\n"
                    + " p.estoque_max,\n"
                    + " p.qtd_embalagem,\n"
                    + " p.qtd_diasvalidade,\n"
                    + " p.ativo,\n"
                    + " id_grupotributacao idicms,\n"
                    + " n.codigo ncm,\n"
                    + " ce.cest cest,\n"
                    + " p.trib_pissaida pis,\n"
                    + " p.trib_cofinssaida cofins,\n"
                    + " id_empresa loja\n"
                    + "from produto p\n"
                    + "left join unidade u on u.id =  p.id_unidade\n"
                    + "left join cest ce on ce.id = p.id_cest\n"
                    + "left join ncm n on n.id = p.id_ncm"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));
                    imp.seteBalanca(rs.getBoolean("ebalanca"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));

                    imp.setCodMercadologico1(rs.getString("mercid1"));
                    imp.setCodMercadologico2(rs.getString("mercid2"));
                    imp.setCodMercadologico3(rs.getString("mercid3"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_max"));

                    imp.setCustoSemImposto(rs.getDouble("preco_custo"));
                    imp.setCustoComImposto(rs.getDouble("preco_custo"));

                    imp.setCustoMedioComImposto(rs.getDouble("custo_medio"));
                    imp.setCustoMedioSemImposto(rs.getDouble("custo_medio"));

                    imp.setPrecovenda(rs.getDouble("preco_venda"));

                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setPiscofinsCstDebito(rs.getString("pis"));
                    imp.setPiscofinsCstCredito(rs.getString("pis"));

                    imp.setIcmsConsumidorId(rs.getString("idicms"));
                    imp.setIcmsDebitoId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsConsumidorId());

                    int codigoProduto = Utils.stringToInt(rs.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rs.getString("ean"));

                        String balanca = (rs.getString("ebalanca"));
                        if ("S".equals(balanca)) {
                            imp.seteBalanca(true);
                        } else {
                            imp.seteBalanca(false);
                        }

                        imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));
                        imp.setValidade(rs.getInt("qtd_diasvalidade"));
                        imp.setQtdEmbalagem(rs.getInt("qtd_embalagem"));
                    }
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
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + " id produto_id,\n"
                        + " preco_venda precovenda, \n"
                        + " preco_venda2 precoatacado,\n"
                        + " qtd_minimapv2 qtde\n"
                        + "from produto\n"
                        + "where qtd_minimapv2 > 1"
                )) {
                    while (rst.next()) {

                        String sistema = (getSistema() + " - " + getLojaOrigem());
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(sistema, getLojaOrigem(), rst.getString("produto_id"));

                        if (codigoAtual > 0) {
                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("produto_id"));
                            imp.setEan("99999" + String.valueOf(codigoAtual));
                            imp.setPrecovenda(rst.getDouble("precovenda"));
                            imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                            imp.setQtdEmbalagem(rst.getInt("qtde"));
                            result.add(imp);
                        }
                    }
                }
            }

        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " f.id fornecedorid,\n"
                    + " f.nome fantasia,\n"
                    + " f.razao_social razao,\n"
                    + " f.cnpj_cpf,\n"
                    + " f.ie ,\n"
                    + " f.endereco,\n"
                    + " f.numero,\n"
                    + " f.bairro,\n"
                    + " f.cep,\n"
                    + " f.fone telefone,\n"
                    + " f.fax,\n"
                    + " f.email_site,\n"
                    + " f.obs,\n"
                    + " e.nome estado,\n"
                    + " e.uf uf,\n"
                    + " c.nome cidade,\n"
                    + " f.id_empresa loja,\n"
                    + " f.ativo,\n"
                    + " f.tipo\n"
                    + "from fornecedor f\n"
                    + "left join estados e on e.id = f.id_estado\n"
                    + "left join cidades c on c.id = f.id_cidade"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("fornecedorid"));
                    imp.setRazao(rs.getString("fantasia"));
                    imp.setFantasia(rs.getString("razao"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setIe_rg(rs.getString("ie"));

                    String pessoa = (rs.getString("tipo"));
                    if ("F".equals(pessoa)) {
                        imp.setTipo_inscricao(TipoInscricao.FISICA);
                        imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    } else {
                        imp.setTipo_inscricao(TipoInscricao.JURIDICA);
                        imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + " p.id_categoria mercid1,\n"
                    + " c.nome desc1,\n"
                    + " p.id_subcategoria mercid2,\n"
                    + " s.nome desc2,\n"
                    + " p.id_subcategoria mercid3,\n"
                    + " s.nome desc3\n"
                    + "FROM produto p \n"
                    + "join categoria c on c.id = p.id_categoria\n"
                    + "join subcategoria s on s.id = p.id_subcategoria \n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("mercid1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("mercid2"));
                    imp.setMerc2Descricao(rs.getString("desc2"));
                    imp.setMerc3ID(rs.getString("mercid3"));
                    imp.setMerc3Descricao(rs.getString("desc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " pf.id_produto,\n"
                    + " pf.id_fornecedor,\n"
                    + " pf.codigo,\n"
                    + " pf.quantidade_embalagem,\n"
                    + " p.preco_custo \n"
                    + "from produtocodfornecedor pf\n"
                    + "join produto p on p.id = pf.id_produto"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("codigo"));
                    imp.setQtdEmbalagem(rs.getInt("quantidade_embalagem"));
                    imp.setCustoTabela(rs.getDouble("preco_custo"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " cli.id clienteid,\n"
                    + " cli.cpf_cnpj,\n"
                    + " cli.ie,\n"
                    + " cli.nome,\n"
                    + " cli.rg,\n"
                    + " cli.razao_social,\n"
                    + " cli.endereco,\n"
                    + " cli.numero,\n"
                    + " cli.referencia,\n"
                    + " cli.cep,\n"
                    + " cli.bairro,\n"
                    + " cli.telefone,\n"
                    + " cli.limite_credito limite,\n"
                    + " cli.ativo,\n"
                    + " cli.tipo,\n"
                    + " e.nome estado,\n"
                    + " e.uf uf,\n"
                    + " c.nome cidade ,\n"
                    + " id_empresa loja,\n"
                    + " cli.email_adi email,\n"
                    + " cli.data_cadastro\n"
                    + "from cliente cli\n"
                    + "left join estados e on e.id = cli.id_estado\n"
                    + "left join cidades c on c.id = cli.id_cidade"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("clienteid"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome"));

                    String pessoa = (rs.getString("tipo"));
                    if ("J".equals(pessoa)) {
                        imp.setCnpj(rs.getString("cpf_cnpj"));
                        imp.setInscricaoestadual(rs.getString("ie"));
                    }

                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("referencia"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setOrgaoemissor(rs.getString("uf"));

                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setEmail(rs.getString("email"));
                    imp.setTelefone(rs.getString("telefone"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " id,\n"
                    + " id_fornecedor,\n"
                    + " documento,\n"
                    + " numero_nf,\n"
                    + " lancamento,\n"
                    + " emissao,\n"
                    + " vencimento,\n"
                    + " parcela,\n"
                    + " valor_original,\n"
                    + " valor,\n"
                    + " multa,\n"
                    + " juros,\n"
                    + " numero_boleto,\n"
                    + " historico,\n"
                    + " id_empresa\n"
                    + "from pagar \n"
                    + "where \n"
                    + "data_pag is null\n"
                    + "and \n"
                    + "valor_pag = 0"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setDataEmissao(rs.getDate("lancamento"));
                    imp.setDataEntrada(rs.getTimestamp("lancamento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("historico"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " id,\n"
                    + " id_cliente,\n"
                    + " n_documento,\n"
                    + " numero_boleto,\n"
                    + " documento,\n"
                    + " lancamento,\n"
                    + " emissao,\n"
                    + " vencimento,\n"
                    + " parcela,\n"
                    + " valor_original,\n"
                    + " historico,\n"
                    + " obs\n"
                    + "from receber\n"
                    + "where \n"
                    + " data_rec is null"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setValor(rs.getDouble("valor_original"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("historico"));

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
        return new LCSistemasDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new LCSistemasDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("total"));
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
                    = "select \n"
                    + " id,\n"
                    + " id_caixa ecf,\n"
                    + " SUBSTR(data_hora, 1,10) data,\n"
                    + " SUBSTR(data_hora, 11,18) hora,\n"
                    + " total,\n"
                    + " case when datahora_cancelamento is not null then 1 else 0 end cancelado,\n"
                    + " status,\n"
                    + " case when coo is null then id else coo end numerocupom\n"
                    + "from venda \n"
                    + "where\n"
                    + " SUBSTR(data_hora, 1,10) BETWEEN '" + strDataInicio + "' and '" + strDataTermino + "'";

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
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("preco"));
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
                    = "select \n"
                    + " vi.id id_item,\n"
                    + " vi.id_venda,\n"
                    + " vi.id_produto,\n"
                    + " u.descricao unidade,\n"
                    + " vi.item sequencia,\n"
                    + " vi.preco,\n"
                    + " vi.quantidade,\n"
                    + " vi.quantidade_embalagem,\n"
                    + " vi.total,\n"
                    + " vi.sub_total,\n"
                    + " case when vi.status <> 'OK' then 1 else 0 end cancelado,\n"
                    + " SUBSTR(v.data_hora, 1,10) data\n"
                    + "from vendadet vi\n"
                    + "join venda v on v.id = vi.id_venda\n"
                    + "join produto p on p.id = vi.id_produto\n"
                    + "left join unidade u on u.id = p.id_unidade\n"
                    + "where \n"
                    + " SUBSTR(v.data_hora, 1,10) BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "';";

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
