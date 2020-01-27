package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class GDoorDAO extends InterfaceDAO {

    private String idLoja = "";
    public boolean utilizaArquivoBalanca = false;

    public void setLojaCliente(String idLoja) {
        this.idLoja = idLoja;
    }

    @Override
    public String getSistema() {
        return "GDoor";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + (!"".equals(idLoja) ? idLoja + " id, " : "1 id, ")
                    + "    fantasia razao\n"
                    + "from\n"
                    + "    emitente")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.codigo id,\n" +
                    "    case when p.barras = '' then p.codigo else p.barras end ean,\n" +
                    "    p.descricao descricaocompleta,\n" +
                    "    p.descricao descricaoreduzida,\n" +
                    "    p.descricao descricaogondola,\n" +
                    "    case upper(p.situacao) when 'INATIVO' then 0 else 1 end as id_situacaocadastro,\n" +
                    "    p.data_cadastro datacadastro,\n" +
                    "    case when p.grupo = 'Balança' then 1 else 0 end e_balanca,\n" +
                    "    p.grupo,\n" +
                    "    14 mercadologico1,\n" +
                    "    1 mercadologico2,\n" +
                    "    1 mercadologico3,\n" +
                    "    p.cod_ncm ncm,\n" +
                    "    c.codigo cest,\n" +
                    "    p.familia id_familia,\n" +
                    "    p.margem_lucro margem,\n" +
                    "    p.validade_dias validade,\n" +
                    "    p.und id_tipoembalagem,\n" +
                    "    p.peso pesobruto,\n" +
                    "    p.peso pesoliquido,\n" +
                    "    p.pis_codigo piscofins_cst_sai,\n" +
                    "    p.pise_codigo piscofins_cst_ent,\n" +
                    "    '' as piscofins_natrec,\n" +
                    "    p.preco_venda preco,\n" +
                    "    p.preco_custo custocomimposto,\n" +
                    "    p.preco_custo custosemimposto,\n" +
                    "    p.qtd estoque,\n" +
                    "    p.qtd_ideal minimo,\n" +
                    "    0 maximo,\n" +
                    "    case elo when '101' then 18\n" +
                    "    when '102' then 18 else 0 end as icms,\n" +
                    "    st icmscst,\n" +
                    "    0 icms_reducao\n" +
                    "from\n" +
                    "    estoque p\n" +
                    "left join\n" +
                    "    cest c on p.cod_cest = c.id\n" +
                    "order by\n" +
                    "    p.codigo")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setSituacaoCadastro(rs.getInt("id_situacaocadastro"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("mercadologico3"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIdFamiliaProduto(rs.getString("id_familia"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setTipoEmbalagem(rs.getString("id_tipoembalagem"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofins_cst_sai"));
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("minimo"));
                    imp.setEstoqueMaximo(rs.getDouble("maximo"));
                    imp.setIcmsAliq(rs.getDouble("icms"));
                    imp.setIcmsAliqConsumidor(rs.getDouble("icms"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icms"));
                    imp.setIcmsAliqSaida(rs.getDouble("icms"));
                    imp.setIcmsReducao(rs.getDouble("icms_reducao"));
                    imp.setIcmsReducaoConsumidor(rs.getDouble("icms_reducao"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icms_reducao"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_reducao"));
                    imp.setIcmsCst(Utils.formataNumero(rs.getString("icmscst")));
                    imp.setIcmsCstConsumidor(Utils.stringToInt(rs.getString("icmscst")));
                    String ean = imp.getEan().trim();
                    if ("SEM GTIN".equals(ean)) {
                        ean = ean.replace("SEM GTIN", imp.getImportId());
                    }

                    if (rs.getInt("e_balanca") == 1
                            && rs.getString("ean") != null
                            && !" ".equals(rs.getString("ean").trim())) {
                        ean = ean.substring(1, ean.length());
                        imp.seteBalanca(true);
                    }
                    imp.setEan(ean);
                    imp.setValidade(rs.getInt("validade"));

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
                    "select\n"
                    + "    codigo idproduto,\n"
                    + "    cod_fornecedor idfornecedor,\n"
                    + "    cod_item_fornecedor codigoexterno\n"
                    + "from\n"
                    + "    rel_est_fornecedor\n"
                    + "order by\n"
                    + "    1, 2")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));

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
                    "select\n"
                    + "    f.codigo id,\n"
                    + "    null as datacadastro,\n"
                    + "    f.nome razao,\n"
                    + "    f.fantasia,\n"
                    + "    f.endereco,\n"
                    + "    f.numero,\n"
                    + "    f.complemento,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade,\n"
                    + "    f.uf,\n"
                    + "    f.cep,\n"
                    + "    f.telefone fone1,\n"
                    + "    null as fone2,\n"
                    + "    f.celular,\n"
                    + "    f.ie_rg inscricaoestadual,\n"
                    + "    f.cnpj_cnpf cnpj,\n"
                    + "    f.observacoes,\n"
                    + "    f.email,\n"
                    + "    f.fax,\n"
                    + "    case upper(f.situacao) when 'INATIVO' then 0 else 1 end as id_situacaocadastro\n"
                    + "from\n"
                    + "    fornecedor f\n"
                    + "order by\n"
                    + "    f.codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone1"));
                    if (rst.getString("celular") != null && !"".equals(rst.getString("celular"))) {
                        imp.addContato("CELULAR", null, rst.getString("celular"), TipoContato.COMERCIAL, null);
                    }

                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setObservacao(rst.getString("observacoes"));
                    if (rst.getString("email") != null && !"".equals(rst.getString("email"))) {
                        imp.addContato("EMAIL", null, null, TipoContato.FISCAL, rst.getString("email"));
                    }
                    if (rst.getString("fax") != null && !"".equals(rst.getString("fax"))) {
                        imp.addContato("FAX", rst.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    imp.setAtivo(rst.getInt("id_situacaocadastro") == 0 ? false : true);

                    result.add(imp);
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
                    "select\n"
                    + "    c.codigo id,\n"
                    + "    c.nome,\n"
                    + "    c.endereco res_endereco,\n"
                    + "    c.numero res_numero,\n"
                    + "    c.complemento res_complemento,\n"
                    + "    c.bairro res_bairro,\n"
                    + "    c.cidade res_cidade,\n"
                    + "    c.uf res_uf,\n"
                    + "    c.cep res_cep,\n"
                    + "    c.telefone fone1,\n"
                    + "    c.celular,\n"
                    + "    c.fax,\n"
                    + "    c.ie_rg inscricaoestadual,\n"
                    + "    c.cnpj_cnpf cnpj,\n"
                    + "    1 as sexo,\n"
                    + "    c.dia_de_acerto prazodias,\n"
                    + "    c.email,\n"
                    + "    c.cadastro datacadastro,\n"
                    + "    c.limite_credito limitepreferencial,\n"
                    + "    case upper(c.situacao) when 'INATIVO' then 1 else 0 end as bloqueado,\n"
                    + "    c.observacoes,\n"
                    + "    c.pai nomepai,\n"
                    + "    c.mae nomemae,\n"
                    + "    null empresa,\n"
                    + "    null telempresa,\n"
                    + "    c.profissao cargo,\n"
                    + "    c.renda salario,\n"
                    + "    case\n"
                    + "        when upper(c.est_civil) containing 'CASAD' then 2\n"
                    + "        when upper(c.est_civil) containing 'SOLT' then 1\n"
                    + "        else 0 end as estadocivil,\n"
                    + "    C.conjuge,\n"
                    + "    null orgaoemissor,\n"
                    + "    c.nascimento datanascimento\n"
                    + "from\n"
                    + "    cliente c\n"
                    + "where\n"
                    + "    c.codigo > 0\n"
                    + "order by\n"
                    + "    c.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("res_endereco"));
                    imp.setNumero(rst.getString("res_numero"));
                    imp.setComplemento(rst.getString("res_complemento"));
                    imp.setBairro(rst.getString("res_bairro"));
                    imp.setMunicipio(rst.getString("res_cidade"));
                    imp.setUf(rst.getString("res_uf"));
                    imp.setCep(rst.getString("res_cep"));
                    imp.setTelefone(rst.getString("fone1"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setSexo(rst.getInt("sexo") == 1 ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setValorLimite(rst.getDouble("limitepreferencial"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setObservacao(rst.getString("observacoes"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomePai(rst.getString("nomePai"));
                    imp.setNomeMae(rst.getString("nomeMae"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaTelefone(rst.getString("telEmpresa"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setEstadoCivil(rst.getInt("estadoCivil"));
                    imp.setNomeConjuge(rst.getString("conjuge"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    vendas_id,\n"
                    + "    documento,\n"
                    + "    num_parcela parcela,\n"
                    + "    historico observacao,\n"
                    + "    cod_cliente idcliente,\n"
                    + "    c.cnpj_cnpf cnpj,\n"
                    + "    nom_cliente nome,\n"
                    + "    emissao,\n"
                    + "    vencimento,\n"
                    + "    valor_dup valor\n"
                    + "from\n"
                    + "    receber r\n"
                    + "join cliente c on (r.cod_cliente = c.codigo)\n"
                    + "where\n"
                    + "    recebimento is null\n"
                    + "order by\n"
                    + "    emissao")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("documento"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
