package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;

/**
 *
 * @author Leandro
 */
public class FortDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Fort";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    coalesce(p.tipo_aliquota, '') icms_cst,\n" +
                    "    coalesce(p.icms_ecf, 0) icms_aliquota\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "group by\n" +
                    "    1, 2\n" +
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    result.add(MapaTributoIMP.make(
                            getIcmsId(rst.getString("icms_cst"),rst.getDouble("icms_aliquota")),
                            rst.getString("icms_cst") + "-" + rst.getString("icms_aliquota")
                    ));
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_empresa, nome from empresa order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id_empresa"), rst.getString("nome")));
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
                    "select\n" +
                    "    p.id_produto id,\n" +
                    "    p.datainicial datacadastro,\n" +
                    "    p.data_at dataalteracao,\n" +
                    "    p.codigo ean,\n" +
                    "    1 qtdembalagem,\n" +
                    "    p.un unidade,\n" +
                    "    case p.exporta_balanca when 'S' then 1 else 0 end ebalanca,\n" +
                    "    case p.status_exclusao when 'S' then 0 else 1 end situacaocadastro,\n" +
                    "    p.descricao descricaocompleta,\n" +
                    "    p.nivel1,\n" +
                    "    p.nivel2,\n" +
                    "    p.nivel3,\n" +
                    "    p.quantidade estoque,\n" +
                    "    p.minimo estoqueminimo,\n" +
                    "    p.maximo estoquemaximo,\n" +
                    "    p.margem1 margem,\n" +
                    "    coalesce(p.precocompra, 0) custo,\n" +
                    "    coalesce(p.precovenda, 0) venda,\n" +
                    "    p.ncm,\n" +
                    "    p.cest,\n" +
                    "    p.pis_cst piscofins_cst_saida,\n" +
                    "    p.pis_cst_entrada piscofins_cst_entrada,\n" +
                    "    p.nat_rec_pis piscofins_nat_rec,\n" +
                    "    p.tipo_aliquota icms_cst,\n" +
                    "    coalesce(p.icms_ecf, 0) icms_aliquota\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("nivel1"));
                    imp.setCodMercadologico2(rst.getString("nivel2"));
                    imp.setCodMercadologico3(rst.getString("nivel3"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("venda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_nat_rec"));
                    imp.setIcmsDebitoId(getIcmsId(rst.getString("icms_cst"),rst.getDouble("icms_aliquota")));
                    imp.setIcmsCreditoId(getIcmsId(rst.getString("icms_cst"),rst.getDouble("icms_aliquota")));
                    
                    result.add(imp);
                    
                }
            }
        }
        
        return result;
    }

    private String getIcmsId(String cst, Double aliquota) {
        return String.format("%s-%.3f", cst, aliquota);
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.id_fornecedor id,\n" +
                    "    f.nome razao,\n" +
                    "    f.fantasia,\n" +
                    "    f.cnpj,\n" +
                    "    f.ie,\n" +
                    "    f.suframa,\n" +
                    "    f.status situacao,\n" +
                    "    r.nome endereco,\n" +
                    "    f.numero,\n" +
                    "    f.compl complemento,\n" +
                    "    b.nome bairro,\n" +
                    "    c.nome municipio,\n" +
                    "    c.uf,\n" +
                    "    f.cep,\n" +
                    "    fr.nome fat_endereco,\n" +
                    "    f.numerofat fat_numero,\n" +
                    "    f.complfat fat_complemento,\n" +
                    "    fb.nome fat_bairro,\n" +
                    "    fc.nome fat_municipio,\n" +
                    "    f.cepfat fat_cep,\n" +
                    "    fc.uf fat_uf,\n" +
                    "    f.fone,\n" +
                    "    f.fone2,\n" +
                    "    f.fone3,\n" +
                    "    f.email,\n" +
                    "    f.data_cad,\n" +
                    "    f.data_exclusao,\n" +
                    "    f.obs,\n" +
                    "    f.optante_simples\n" +
                    "from\n" +
                    "    fornecedores f\n" +
                    "    left join ruas r on f.id_rua = r.id_rua\n" +
                    "    left join bairros b on r.id_bairro = b.id_bairro\n" +
                    "    left join cidades c on b.id_cidade = c.id_cidade   \n" +
                    "    left join ruas fr on f.id_rua = fr.id_rua\n" +
                    "    left join bairros fb on r.id_bairro = fb.id_bairro\n" +
                    "    left join cidades fc on b.id_cidade = fc.id_cidade\n" +
                    "order by\n" +
                    "    f.id_fornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setAtivo("A".equals(rst.getString("situacao")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCob_endereco(rst.getString("fat_endereco"));
                    imp.setCob_numero(rst.getString("fat_numero"));
                    imp.setCob_complemento(rst.getString("fat_complemento"));
                    imp.setCob_bairro(rst.getString("fat_bairro"));
                    imp.setCob_municipio(rst.getString("fat_municipio"));
                    imp.setCob_cep(rst.getString("fat_cep"));
                    imp.setCob_uf(rst.getString("fat_uf"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.addTelefone("FONE 2", rst.getString("fone2"));
                    imp.addTelefone("FONE 3", rst.getString("fone3"));
                    imp.addEmail("E-MAIL", rst.getString("email"), TipoContato.COMERCIAL);
                    imp.setDatacadastro(rst.getDate("data_cad"));
                    imp.setObservacao(rst.getString("obs"));
                    
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
                    "select\n" +
                    "    c.id_cliente id,\n" +
                    "    c.cnpj,\n" +
                    "    c.ie,\n" +
                    "    c.nome razao,\n" +
                    "    c.fantasia,\n" +
                    "    case c.status when 'C' then 0 else 1 end situacaocadastro,\n" +
                    "    c.data_cancelado,\n" +
                    "    case c.bloqueio_limite when 'S' then 1 else 0 end bloqueado,\n" +
                    "    r.nome endereco,\n" +
                    "    c.numero,\n" +
                    "    c.compl complemento,\n" +
                    "    b.nome bairro,\n" +
                    "    cd.nome municipio,\n" +
                    "    cd.uf,\n" +
                    "    c.cep,\n" +
                    "    c.estado_civil,\n" +
                    "    c.data_nasc data_nascimento,\n" +
                    "    c.data_cad data_cadastro,\n" +
                    "    c.fone,\n" +
                    "    c.fone2,\n" +
                    "    c.fone3,\n" +
                    "    c.saldo_credito,\n" +
                    "    c.valor_limite,\n" +
                    "    c.nome_conjuge,\n" +
                    "    c.nome_pais nome_pai,\n" +
                    "    c.nome_pais nome_mae,\n" +
                    "    c.obs observacao2,\n" +
                    "    c.dia_fat diavencimento,\n" +
                    "    c.email,\n" +
                    "    cr.nome cob_endereco,\n" +
                    "    c.numerocob cob_numero,\n" +
                    "    c.complcob cob_complemento,\n" +
                    "    cb.nome cob_bairro,\n" +
                    "    ccd.nome cob_municipio,\n" +
                    "    ccd.uf cob_uf,\n" +
                    "    c.cepcob cob_cep\n" +
                    "from\n" +
                    "    clientes c\n" +
                    "    left join ruas r on c.id_rua = r.id_rua\n" +
                    "    left join bairros b on r.id_bairro = b.id_bairro\n" +
                    "    left join cidades cd on b.id_cidade = cd.id_cidade \n" +
                    "    left join ruas cr on c.id_ruacob = cr.id_rua\n" +
                    "    left join bairros cb on r.id_bairro = cb.id_bairro\n" +
                    "    left join cidades ccd on b.id_cidade = ccd.id_cidade\n" +
                    "order by\n" +
                    "    c.id_cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("situacaocadastro"));
                    imp.setDataBloqueio(rst.getDate("data_cancelado"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(TipoEstadoCivil.getByString(rst.getString("estado_civil")));
                    imp.setDataNascimento(rst.getDate("data_nascimento"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setTelefone(Utils.formataTelefone(19, rst.getString("fone")));
                    imp.addTelefone("FONE 2", Utils.formataTelefone(19, rst.getString("fone2")));
                    imp.addTelefone("FONE 3", Utils.formataTelefone(19, rst.getString("fone3")));
                    imp.setValorLimite(rst.getDouble("valor_limite"));
                    imp.setNomeConjuge(rst.getString("nome_conjuge"));
                    imp.setNomePai(rst.getString("nome_pai"));
                    imp.setNomeMae(rst.getString("nome_mae"));
                    imp.setObservacao2(rst.getString("observacao2"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCobrancaEndereco(rst.getString("cob_endereco"));
                    imp.setCobrancaNumero(rst.getString("cob_numero"));
                    imp.setCobrancaComplemento(rst.getString("cob_complemento"));
                    imp.setCobrancaBairro(rst.getString("cob_bairro"));
                    imp.setCobrancaMunicipio(rst.getString("cob_municipio"));
                    imp.setCobrancaUf(rst.getString("cob_uf"));
                    imp.setCobrancaCep(rst.getString("cob_cep"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private double ajuste(String valor) {
        if (valor != null) {
            valor = valor.replace(".", ",");
        }
        return MathUtils.round(Utils.stringToDouble(valor), 1);
    }
    
    private int ajusteInt(String valor) {
        if (valor != null) {
            valor = valor.replace(".", ",");
        }
        return (int) MathUtils.round(Utils.stringToDouble(valor), 0);
    }
    
    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    n.id_tabela_nutricional id,\n" +
                    "    n.bal_descricao descricao,\n" +
                    "    n.tbn_valcal caloria,\n" +
                    "    n.tbn_carb carboidrato,\n" +
                    "    n.tbn_prot proteina,\n" +
                    "    n.tbn_gord gordura,\n" +
                    "    n.tbn_gordsat gordura_saturada,\n" +
                    "    n.tbn_gordtrans gordura_trans,\n" +
                    "    n.tbn_colest colesterol,\n" +
                    "    n.tbn_fibra fibra,\n" +
                    "    n.tbn_calcio calcio,\n" +
                    "    n.tbn_ferro ferro,\n" +
                    "    n.tbn_sodio sodio,\n" +
                    "    n.tbn_calciop perc_caloria,\n" +
                    "    n.tbn_carbp perc_carboidrato,\n" +
                    "    n.tbn_protp perc_proteina,\n" +
                    "    n.tbn_gordp perc_gordura,\n" +
                    "    n.tbn_gordsatp perc_gordura_saturada,\n" +
                    "    n.tbn_fibrap perc_fibra,\n" +
                    "    n.tbn_calciop perc_calcio,\n" +
                    "    n.tbn_ferrop perc_ferro,\n" +
                    "    n.tbn_sodio perc_sodio,\n" +
                    "    n.tbn_qtd porcao,\n" +
                    "    n.tbn_un unidade,\n" +
                    "    n.id_produto\n" +
                    "from\n" +
                    "    tabela_nutricional n\n" +
                    "where\n" +
                    "    not n.tbn_qtd is null\n" +
                    "order by\n" +
                    "    id"
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setCaloria(ajusteInt(rst.getString("caloria")));
                    imp.setCarboidrato(ajuste(rst.getString("carboidrato")));
                    imp.setProteina(ajuste(rst.getString("proteina")));
                    imp.setGordura(ajuste(rst.getString("gordura")));
                    imp.setGorduraSaturada(ajuste(rst.getString("gordura_saturada")));
                    imp.setGorduraTrans(ajuste(rst.getString("gordura_trans")));
                    imp.setFibra(ajuste(rst.getString("fibra")));
                    imp.setCalcio(ajuste(rst.getString("calcio")));
                    imp.setFerro(ajuste(rst.getString("ferro")));
                    imp.setSodio(ajuste(rst.getString("sodio")));
                    imp.setPercentualCaloria(ajusteInt(rst.getString("perc_caloria")));
                    imp.setPercentualCarboidrato(ajusteInt(rst.getString("perc_carboidrato")));
                    imp.setPercentualProteina(ajusteInt(rst.getString("perc_proteina")));
                    imp.setPercentualGordura(ajusteInt(rst.getString("perc_gordura")));
                    imp.setPercentualGorduraSaturada(ajusteInt(rst.getString("perc_gordura_saturada")));
                    imp.setPercentualFibra(ajusteInt(rst.getString("perc_fibra")));
                    imp.setPercentualCalcio(ajusteInt(rst.getString("perc_calcio")));
                    imp.setPercentualFerro(ajusteInt(rst.getString("perc_ferro")));
                    imp.setPercentualSodio(ajusteInt(rst.getString("perc_sodio")));
                    imp.setPorcao(
                            Utils.acertarTexto(rst.getString("porcao"), "1") + " " +
                            Utils.acertarTexto(rst.getString("unidade"), "UN")
                    );
                    imp.addProduto(rst.getString("id_produto"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        List<ReceitaBalancaIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    r.id_tabela_nutricional id,\n" +
                    "    r.bal_descricao,\n" +
                    "    coalesce(trim(r.bal_ingred1),'') linha1,\n" +
                    "    coalesce(trim(r.bal_ingred2),'') linha2,\n" +
                    "    coalesce(trim(r.bal_ingred3),'') linha3,\n" +
                    "    coalesce(trim(r.bal_ingred4),'') linha4,\n" +
                    "    r.id_produto\n" +
                    "from\n" +
                    "    tabela_nutricional r\n" +
                    "where\n" +
                    "    trim(coalesce(trim(r.bal_ingred1),'') ||\n" +
                    "    coalesce(trim(r.bal_ingred2),'') ||\n" +
                    "    coalesce(trim(r.bal_ingred3),'') ||\n" +
                    "    coalesce(trim(r.bal_ingred4),'')) != ''\n" +
                    "order by\n" +
                    "    id"
            )) {
                while (rst.next()) {
                    ReceitaBalancaIMP imp = new ReceitaBalancaIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("bal_descricao"));
                    imp.setReceita(                            
                            (
                                    rst.getString("linha1") + "\n" +
                                    rst.getString("linha2") + "\n" +
                                    rst.getString("linha3") + "\n" +
                                    rst.getString("linha4")
                            ).trim()
                    );
                    imp.getProdutos().add(rst.getString("id_produto"));
                    
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
                    "select\n" +
                    "    pr.id_fornecedor,\n" +
                    "    pr.id_produto,\n" +
                    "    pr.codigo_ref codigoexterno\n" +
                    "from\n" +
                    "    produto_ref pr"
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
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    id_setores,\n" +
                "    nome\n" +
                "from\n" +
                "    setores\n" +
                "order by\n" +
                "    1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("id_setores"));
                    imp.setMerc1Descricao(rst.getString("nome"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
