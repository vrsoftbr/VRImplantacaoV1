package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Bruno
 */
public class EmpresoftDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "EMPRESOFT";
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.CONVENIO_CONVENIADO));
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
                    OpcaoProduto.MARGEM_MAXIMA,
                    OpcaoProduto.MARGEM_MINIMA,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO
                }
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " codigo id,\n"
                    + " cst_t_i cst\n"
                    + "from tributo "
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("cst"))
                    );
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
                    "select \n"
                    + " g.codigo merc1,\n"
                    + " g.nome desc1,\n"
                    + " case when s.codigo is null then g.codigo \n"
                    + "     else s.codigo end merc2,\n"
                    + " case when s.nome is null then g.nome \n"
                    + "     else s.nome end desc2\n"
                    + "from grupo g\n"
                    + "left join subgrupo s on s.grupo = g.codigo "
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2"));
                    imp.setMerc3ID(imp.getMerc2ID());
                    imp.setMerc3Descricao(imp.getMerc2Descricao());

                    result.add(imp);
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
                    + " p.codigo id,\n"
                    + " p.barra ean,\n"
                    + " p.descricao descricaocompleta,\n"
                    + " p.balanca ebalanca,\n"
                    + " p.volume,\n"
                    + " p.embalage qtdeembalagem,\n"
                    + " p.tipo tipoembalagem,\n"
                    + " case when p.deletado  = 'F' then '1'\n"
                    + " else '0'\n"
                    + " end as ativo,\n"
                    + " p.grupo,\n"
                    + " case when p.subgrupo = '' then p.grupo \n"
                    + "         else p.subgrupo \n"
                    + "         end subgrupo,\n"
                    + " p.valor precovenda,\n"
                    + " p.custo,\n"
                    + " p.cureal,\n"
                    + " p.valor2,\n"
                    + " p.marg1 margem,\n"
                    + " coalesce(e.qtd,0) estoque,\n"
                    + " p.ncm,\n"
                    + " p.cest,\n"
                    + " p.tributo icms,\n"
                    + " p.tab_pis pis,\n"
                    + " p.tab_conf cofins\n"
                    + "from produto p\n"
                    + "left join estoque e on e.produto = p.codigo and e.codigo = '01'"
            )) {

                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.seteBalanca(rs.getBoolean("ebalanca"));
                    imp.setEan(rs.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rs.getString("id"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(bal.getValidade());
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());

                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.setTipoEmbalagemCotacao(rs.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rs.getInt("qtdeembalagem"));
                    imp.setTipoEmbalagemVolume(imp.getTipoEmbalagemCotacao());

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setSituacaoCadastro(rs.getInt("ativo"));

                    imp.setEstoque(rs.getDouble("estoque"));

                    imp.setCodMercadologico1(rs.getString("grupo"));
                    imp.setCodMercadologico2(rs.getString("subgrupo"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    String idIcms;

                    idIcms = rs.getString("icms");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);

                    result.add(imp);
                }
            }
        }

        return result;
    }

//    @Override
//    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
//        List<ProdutoFornecedorIMP> result = new ArrayList<>();
//
//        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
//            try (ResultSet rs = stm.executeQuery(
//                    ""
//            )) {
//                while (rs.next()) {
//                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
//
//                    imp.setImportLoja(getLojaOrigem());
//                    imp.setImportSistema(getSistema());
//                    imp.setIdProduto(rs.getString("produtoid"));
//                    imp.setIdFornecedor(rs.getString("fornecedorid"));
//                    imp.setCodigoExterno(rs.getString("codigoexterno"));
//
//                    result.add(imp);
//                }
//            }
//        }
//        return result;
//    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " codigo,\n"
                    + " razao,\n"
                    + " ie,\n"
                    + " fantasia,\n"
                    + " cnpj_cnpf,\n"
                    + " tipo,\n"
                    + " endereco,\n"
                    + " numero,\n"
                    + " complement,\n"
                    + " bairro,\n"
                    + " municipio,\n"
                    + " estado,\n"
                    + " cep,\n"
                    + " fax,\n"
                    + " celular1,\n"
                    + " celular2,\n"
                    + " contato\n"
                    + "from forne"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cnpf"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complement"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("celular1"));

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
                    + " cod_client,\n"
                    + " razao,\n"
                    + " fantasia,\n"
                    + " cnpj_cnpf,\n"
                    + " ie,\n"
                    + " endereco,\n"
                    + " numero,\n"
                    + " complement,\n"
                    + " bairro,\n"
                    + " municipio,\n"
                    + " estado,\n"
                    + " cep,\n"
                    + " telefone,\n"
                    + " tipo,\n"
                    + " cadastro \n"
                    + "from cliente "
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("cod_client"));
                    imp.setCnpj(rs.getString("cnpj_cnpf"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia").equalsIgnoreCase("") ? imp.getRazao() : rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complement"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setDataCadastro(rs.getDate("cadastro"));

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
                    + "pedido,\n"
                    + "cliente,\n"
                    + "emissao,\n"
                    + "vencto,\n"
                    + "valor,\n"
                    + "pago\n"
                    + "from\n"
                    + "parcela\n"
                    + "where pagto is null and valor >0\n"
                    + "order by 2"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("pedido"));
                    imp.setParcela(1);
                    imp.setIdCliente(rs.getString("cliente"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencto"));
                    imp.setValor(rs.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
