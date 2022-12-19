package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.ClienteContatoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Wagner
 */
public class BlueSoftDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "BLUESOFT";
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
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_FORNECEDOR));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.EMAIL,
                OpcaoCliente.CELULAR
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct --saida\n"
                    + " (cst_saida_pdv||'.'||replace(aliquota_saida_pdv,',','.')||'.'||replace(reducao_saida_pdv,',','.')||'.S') id,\n"
                    + " descricao_saida_pdv descricao,\n"
                    + " cst_saida_pdv cst,\n"
                    + " replace(aliquota_saida_pdv,',','.') aliquota,\n"
                    + " replace(reducao_saida_pdv,',','.')::numeric(10,2)  reducao\n"
                    + "from tributacao_produto \n"
                    + "union\n"
                    + "select distinct --entrada\n"
                    + " (cst_entrada||'.'||replace(aliquota_entrada,',','.')||'.'||replace(reducao_entrada,',','.')||'.E') id,\n"
                    + " descricao_entrada descricao,\n"
                    + " cst_entrada cst,\n"
                    + " replace(aliquota_entrada,',','.') aliquota,\n"
                    + " replace(reducao_entrada,',','.')::numeric(10,2) reducao\n"
                    + "from tributacao_produto "
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
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
                    "select \n"
                    + " departamento_key merc1,\n"
                    + " departamento desc1,\n"
                    + " secao_key merc2,\n"
                    + " secao desc2,\n"
                    + " grupo_produto_key merc3,\n"
                    + " grupo desc3,\n"
                    + " sub_grupo_produto_key merc4,\n"
                    + " subgrupo desc4 \n"
                    + "from mercadologico \n"
                    + "order by 1,3,5,7"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("desc3"));
                    imp.setMerc4ID(rs.getString("merc4"));
                    imp.setMerc4Descricao(rs.getString("merc4"));

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
                    "select \n"
                    + " p.produto_key id,\n"
                    + " p.descricao descricaocompleta,\n"
                    + " p.descricao_gondola descricaogondola,\n"
                    + " p.descricao_cupom descricaoreduzida,\n"
                    + " p.descricao_balanca,\n"
                    + " p.embalagem_key embalagem,\n"
                    + " p.fator_estoque,\n"
                    + " replace(p.estoque_gerencial,',','.') estoque,\n"
                    + " case when p.status = 'Inativo' then 0 else 1 end situacao,\n"
                    + " case when p.exporta_balanca = 'Sim' then 1 else 0 end ebalanca,\n"
                    + " p.peso_bruto,\n"
                    + " p.peso_liquido,\n"
                    + " p.gtin_principal ean,\n"
                    + " replace(p.preco_venda,',','.') precovenda,\n"
                    + " replace(p.custo_liquido,',','.') custosemimposto,\n"
                    + " replace(p.custo_bruto,',','.') custocomimposto,\n"
                    + " p.custo_contabil,\n"
                    + " p.departamento_key merc1,\n"
                    + " p.secao_key merc2,\n"
                    + " p.grupo_produto_key merc3,\n"
                    + " p.sub_grupo_produto_key merc4,\n"
                    + " t.ncm,\n"
                    + " t.cest,\n"
                    + " t.cst_pis_saida,\n"
                    + " t.cst_pis_entrada,\n"
                    + " (t.cst_saida_pdv||'.'||replace(t.aliquota_saida_pdv,',','.')||'.'||replace(t.reducao_saida_pdv,',','.')||'.S') idtributacao_saida,\n"
                    + " (t.cst_entrada||'.'||replace(t.aliquota_entrada,',','.')||'.'||replace(t.reducao_entrada,',','.')||'.E') idtributacao_entrada\n"
                    + "from produtos p\n"
                    + "left join tributacao_produto t on t.produto_key = p.produto_key;"
            )) {
                //Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));

                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));

                    imp.setTipoEmbalagemCotacao(rs.getString("embalagem"));
                    imp.setTipoEmbalagemVolume(rs.getString("embalagem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setSituacaoCadastro(rs.getInt("situacao"));

                    imp.setIcmsConsumidorId(rs.getString("idtributacao_saida"));
                    imp.setIcmsDebitoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsConsumidorId());

                    imp.setIcmsCreditoId(rs.getString("idtributacao_entrada"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("idtributacao_entrada"));

                    imp.setPiscofinsCstDebito(rs.getInt("cst_pis_saida"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.seteBalanca(rs.getBoolean("ebalanca"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));

                    /*ProdutoBalancaVO balanca = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

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
                    }*/
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
                    "select \n"
                    + " produto_key produtoid,\n"
                    + " ean\n"
                    + "from barras;"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("produtoid"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(1);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " n.produto_key id,\n"
                    + " p.descricao descricao,\n"
                    + " n.qtde_porcao porcao,\n"
                    + " n.medida_caseira_inteira medidaInteira,\n"
                    + " n.q_calorias calorias,\n"
                    + " n.q_gorduras_totais gordurastotais,\n"
                    + " n.q_gorduras_trans gordurastrans,\n"
                    + " n.q_gorduras_saturadas gordurassaturadas,\n"
                    + " n.q_carboidratos carboidratos,\n"
                    + " n.q_proteinas proteinas,\n"
                    + " n.q_colesterol colesterol,\n"
                    + " n.q_fibra fibra,\n"
                    + " n.q_calcio calcio,\n"
                    + " n.q_ferro ferro,\n"
                    + " n.q_sodio sodio\n"
                    + "from nutricional n\n"
                    + "join produtos p on p.produto_key = n.produto_key;"
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setCaloria(rst.getInt("calorias"));
                    imp.setCarboidrato(rst.getDouble("carboidratos"));
                    imp.setProteina(rst.getDouble("proteinas"));
                    imp.setGordura(rst.getDouble("gordurastotais"));
                    imp.setGorduraSaturada(rst.getDouble("gordurassaturadas"));
                    imp.setGorduraTrans(rst.getDouble("gordurastrans"));
                    imp.setFibra(rst.getDouble("fibra"));
                    imp.setSodio(rst.getDouble("sodio"));
                    imp.setPorcao(rst.getString("porcao"));
                    imp.setCalcio(rst.getDouble("calcio"));
                    imp.setFerro(rst.getDouble("ferro"));
                    imp.setMedidaInteira(rst.getInt("medidaInteira"));

                    imp.addProduto(rst.getString("id"));

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
                    "select distinct on (fornecedor_key)\n"
                    + " fornecedor_key id,\n"
                    + " nome_razao razao,\n"
                    + " nome_fantasia fantasia,\n"
                    + " cpf_cnpj cpfcnpj,\n"
                    + " endereco_rua endereco,\n"
                    + " endereco_numero numero,\n"
                    + " endereco_complemento complemento,\n"
                    + " cidade,\n"
                    + " bairro,\n"
                    + " estado_sigla uf,\n"
                    + " cep,\n"
                    + " codigo_ibge,\n"
                    + " sexo,\n"
                    + " insc,\n"
                    + " case when status = 'Inativo' then 0 else 1 end situacao,\n"
                    + " produtor_rural\n"
                    + "from fornecedor;"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cpfcnpj"));
                    imp.setIe_rg(rs.getString("insc"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    if ("Ativo".equals(rs.getString("produtor_rural"))) {
                        imp.setTipoFornecedor(TipoFornecedor.PRODUTORRURAL);
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " fornecedor_produto_key id,\n"
                    + " fornecedor_key fornecedorid,\n"
                    + " produto_key produtoid,\n"
                    + " codigo_referencia referencia,\n"
                    + " divisao_key quantidade \n"
                    + "from produto_fornecedor "
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("produtoid"));
                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setCodigoExterno(rs.getString("referencia"));

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
                    "select distinct on (c.pessoa_key)\n"
                    + " c.pessoa_key id,\n"
                    + " c.nome_razao razao,\n"
                    + " c.nome_fantasia fantasia,\n"
                    + " c.nome_abreviado obs,\n"
                    + " c.cpf_cnpj cpfcnpj,\n"
                    + " c.endereco_rua endereco,\n"
                    + " c.endereco_numero numero,\n"
                    + " c.endereco_complemento complemento,\n"
                    + " c.cidade,\n"
                    + " c.bairro, \n"
                    + " c.estado_sigla uf,\n"
                    + " c.cep,\n"
                    + " c.codigo_ibge,\n"
                    + " c.sexo,\n"
                    + " c.rg,\n"
                    + " replace(c.data_nascimento,'/','-') datanascimento,\n"
                    + " c.insc ie,\n"
                    + " case when c.status = 'Inativo' then 0 else 1 end situacao,\n"
                    + " ct.descritivo celular,\n"
                    + " ct2.descritivo email,\n"
                    + " ct3.descritivo telefone\n"
                    + "from cliente c\n"
                    + "left join contatocliente ct on ct.pessoa_key = c.pessoa_key and ct.tipo_contato = 'Celular Particular'\n"
                    + "left join contatocliente ct2 on ct2.pessoa_key = c.pessoa_key and ct2.tipo_contato = 'Email Comercial'\n"
                    + "left join contatocliente ct3 on ct3.pessoa_key = c.pessoa_key and ct3.tipo_contato = 'Telefone Residencial'"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setTelefone(rs.getString("telefone"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

}
