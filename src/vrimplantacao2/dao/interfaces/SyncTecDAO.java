package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SyncTecDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SyncTec";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.favorecido,\n" +
                    "    f.nome\n" +
                    "from\n" +
                    "    empresas emp\n" +
                    "    join favorecidos f on emp.favorecido = f.favorecido\n" +
                    "order by\n" +
                    "    f.favorecido"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("favorecido"), rst.getString("nome")));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    g.grupo codmercadologico,\n" +
                    "    g.codigo,\n" +
                    "    g.descricaogrupo descmercadologico,\n" +
                    "    g.desativado\n" +
                    "from\n" +
                    "    grupos g\n" +
                    "order by\n" +
                    "    g.descricaogrupo")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("codmercadologico"));
                    imp.setMerc1Descricao(rs.getString("descmercadologico"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    private static class Ean {
        public String item;
        public String ean;
        public String unidade;

        public Ean(String item, String ean, String unidade) {
            this.item = item;
            this.ean = ean;
            this.unidade = unidade;
        }
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            Map<String, List<Ean>> eans = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    i.item,\n" +
                    "    i.codigo,\n" +
                    "    i.unidadevarejo unidade\n" +
                    "from\n" +
                    "    itens i\n" +
                    "where\n" +
                    "    not i.codigo in (select codigo from itenscodigos) and\n" +
                    "    not i.codigo is null\n" +
                    "union\n" +
                    "select\n" +
                    "    ic.item,\n" +
                    "    ic.codigo,\n" +
                    "    coalesce(ic.unidade, 'UN') unidade\n" +
                    "from\n" +
                    "    itenscodigos ic\n" +
                    "where\n" +
                    "    not nullif(ic.codigo, '') is null"
            )) {
                while (rst.next()) {
                    List<Ean> list = eans.get(rst.getString("item"));
                    if (list == null) {
                        list = new ArrayList<>();
                        eans.put(rst.getString("item"), list);
                    }
                    list.add(new Ean(rst.getString("item"), rst.getString("codigo"), rst.getString("unidade")));
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.item id,\n" +
                    "    p.datacadastro,\n" +
                    "    p.codigo ean,\n" +
                    "    p.unidade,\n" +
                    "    case when p.pesavel != 'N' then 1 else 0 end e_balanca,\n" +
                    "    coalesce(p.tempomediovalidade, 0) validade,\n" +
                    "    p.descricao descricaocompleta,\n" +
                    "    p.grupo codmercadologico,\n" +
                    "    p.pesobruto,\n" +
                    "    p.pesoliquido,\n" +
                    "    p.qtdeminimo,\n" +
                    "    p.qtdemaximo,\n" +
                    "    p.estoque,\n" +
                    "    p.percentual,\n" +
                    "    p.fatorlucro,\n" +
                    "    p.comlucro margem, \n" +
                    "    p.customedio,\n" +
                    "    p.custocontabil custosemimposto,\n" +
                    "    p.custoproduto custocomimposto,\n" +
                    "    preco.preco,\n" +
                    "    case when p.desativado = 'S' then 0 else 1 end situacaocadastro,\n" +
                    "    nullif(p.clasfiscal,'') ncm,\n" +
                    "    nullif(p.cest_opc,'') cest,\n" +
                    "    p.cstpiscofinssaida piscofins_debito,\n" +
                    "    p.cstpiscofins piscofins_entrada,\n" +
                    "    nullif(p.naturazareceitapiscofins,0) naturezareceita,\n" +
                    "    p.cst icms_cst,\n" +
                    "    p.aliqicms icms_aliq,\n" +
                    "    p.reducaocst icms_reducao\n" +
                    "from\n" +
                    "    itens p\n" +
                    "    join produtospreco preco on p.item = preco.item and preco.tabelapreco = 0\n" +
                    "order by\n" +
                    "    p.item"
            )) {
                while (rst.next()) {
                    
                    List<Ean> eanList = eans.get(rst.getString("item"));
                    
                    if (eanList == null) {
                        eanList = new ArrayList<>();
                        eanList.add(new Ean(rst.getString("item"), rst.getString("item"), "UN"));
                    }
                    
                    for (Ean ean: eanList) {
                        
                        if (Utils.stringToLong(ean.ean) <= 999999) {
                            continue;
                        }
                    
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("item"));
                        imp.setEan(ean.ean);
                        imp.setTipoEmbalagem(ean.unidade);
                        imp.seteBalanca(rst.getBoolean("e_balanca"));
                        if (!imp.isBalanca() && "KG".equals(imp.getTipoEmbalagem())) {
                            imp.setTipoEmbalagem("UN");
                        }
                        imp.setValidade(rst.getInt("validade"));
                        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                        imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                        imp.setPesoBruto(rst.getDouble("pesobruto"));
                        imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                        imp.setEstoqueMinimo(rst.getDouble("qtdeminimo"));
                        imp.setEstoqueMaximo(rst.getDouble("qtdemaximo"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        imp.setMargem(rst.getDouble("margem"));
                        imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                        imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                        imp.setPrecovenda(rst.getDouble("preco"));
                        imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                        imp.setNcm(rst.getString("ncm"));
                        imp.setCest(rst.getString("cest"));
                        imp.setPiscofinsCstDebito(rst.getInt("piscofins_debito"));
                        imp.setPiscofinsCstCredito(rst.getInt("piscofins_entrada"));
                        imp.setPiscofinsNaturezaReceita(rst.getInt("naturezareceita"));
                        imp.setIcmsCst(rst.getInt("icms_cst"));
                        imp.setIcmsAliq(rst.getDouble("icms_aliq"));
                        imp.setIcmsReducao(rst.getDouble("icms_reducao"));

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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.item id,\n" +
                    "    p.datacadastro,\n" +
                    "    p.codigo ean,\n" +
                    "    p.unidade,\n" +
                    "    case when p.pesavel != 'N' then 1 else 0 end e_balanca,\n" +
                    "    coalesce(p.tempomediovalidade, 0) validade,\n" +
                    "    p.descricao descricaocompleta,\n" +
                    "    p.grupo codmercadologico,\n" +
                    "    p.pesobruto,\n" +
                    "    p.pesoliquido,\n" +
                    "    p.qtdeminimo,\n" +
                    "    p.qtdemaximo,\n" +
                    "    p.estoque,\n" +
                    "    p.percentual,\n" +
                    "    p.fatorlucro,\n" +
                    "    p.comlucro margem, \n" +
                    "    p.customedio,\n" +
                    "    p.custocontabil custosemimposto,\n" +
                    "    p.custoproduto custocomimposto,\n" +
                    "    preco.preco,\n" +
                    "    case when p.desativado = 'S' then 0 else 1 end situacaocadastro,\n" +
                    "    nullif(p.clasfiscal,'') ncm,\n" +
                    "    nullif(p.cest_opc,'') cest,\n" +
                    "    p.cstpiscofinssaida piscofins_debito,\n" +
                    "    p.cstpiscofins piscofins_entrada,\n" +
                    "    nullif(p.naturazareceitapiscofins,0) naturezareceita,\n" +
                    "    p.cst icms_cst,\n" +
                    "    p.aliqicms icms_aliq,\n" +
                    "    p.reducaocst icms_reducao\n" +
                    "from\n" +
                    "    itens p\n" +
                    "    join produtospreco preco on p.item = preco.item and preco.tabelapreco = 0\n" +
                    "order by\n" +
                    "    p.item"
            )) {
                while (rst.next()) {

                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("item"));
                    imp.setEan(rst.getString("codigo"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setCodMercadologico1(rst.getString("codmercadologico"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("qtdeminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("qtdemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_debito"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofins_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("naturezareceita"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducao(rst.getDouble("icms_reducao"));

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
                    "select\n" +
                    "    f.favorecido id,\n" +
                    "    coalesce(f.razao, f.nome) razao,\n" +
                    "    coalesce(f.nome, f.razao) fantasia,\n" +
                    "    f.cpf_cnpj cnpj,\n" +
                    "    f.inscricao_est,\n" +
                    "    f.inscricao_mun,\n" +
                    "    f.suframa,\n" +
                    "    case f.desativado when 'S' then 0 else 1 end ativo,\n" +
                    "    f.endereco,\n" +
                    "    f.nro,\n" +
                    "    f.bairro,\n" +
                    "    f.cidade,\n" +
                    "    f.uf,\n" +
                    "    f.municipio,\n" +
                    "    f.cep,\n" +
                    "    f.fone1,\n" +
                    "    f.fone2,\n" +
                    "    case f.permitircheque when 'N' then 0 else 1 end as permitircheque,\n" +
                    "    case f.permitirfiado when 'N' then 0 else 1 end as permitirrotativo,\n" +
                    "    f.datacadastro,\n" +
                    "    f.obs,\n" +
                    "    f.celular,\n" +
                    "    f.fax,\n" +
                    "    f.email,\n" +
                    "    coalesce(f.adddiasprazo,0) prazoentrega\n" +
                    "from\n" +
                    "    favorecidos f\n" +
                    "where\n" +
                    "    tipofavorecido = 2\n" +
                    "    and favorecido > 0\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricao_est"));
                    imp.setInsc_municipal(rst.getString("inscricao_mun"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("nro"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_municipio(rst.getInt("municipio"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone1"));
                    String fone2 = Utils.stringLong(rst.getString("fone2"));
                    if (!"0".equals(fone2) && !"".equals(fone2)) {
                        imp.addContato("A", "FONE2", fone2, "", TipoContato.COMERCIAL, "");
                    }
                    String fax = Utils.stringLong(rst.getString("fax"));
                    if (!"0".equals(fax) && !"".equals(fax)) {
                        imp.addContato("B", "FAX", fax, "", TipoContato.COMERCIAL, "");
                    }
                    String email = Utils.stringLong(rst.getString("email"));
                    if (!"0".equals(email) && !"".equals(email)) {
                        imp.addContato("C", "EMAIL", "", "", TipoContato.COMERCIAL, email);
                    }
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    
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
                    "select distinct\n" +
                    "    fornecedor,\n" +
                    "    item,\n" +
                    "    codigoforn,\n" +
                    "    data,\n" +
                    "    precocompra,\n" +
                    "    quantidade\n" +
                    "from\n" +
                    "    itensforn pf\n" +
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("item"));
                    imp.setCodigoExterno(rst.getString("codigoforn"));
                    imp.setDataAlteracao(rst.getDate("data"));
                    imp.setCustoTabela(rst.getDouble("precocompra"));
                    imp.setQtdEmbalagem(rst.getDouble("quantidade"));
                    
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
                    "    f.favorecido id,\n" +
                    "    f.cpf_cnpj cnpj,\n" +
                    "    coalesce(nullif(f.inscricao_est,''),f.rg) inscricaoestadual,\n" +
                    "    coalesce(f.razao, f.nome) razao,\n" +
                    "    coalesce(f.nome, f.razao) fantasia,\n" +
                    "    case when f.desativado = 'S' then 0 else 1 end ativo,\n" +
                    "    f.endereco,\n" +
                    "    f.nro,\n" +
                    "    f.bairro,\n" +
                    "    f.cidade,\n" +
                    "    f.uf,\n" +
                    "    f.municipio,\n" +
                    "    f.cep,\n" +
                    "    f.datanasc,\n" +
                    "    f.datacadastro,\n" +
                    "    f.dataadimissao,\n" +
                    "    cg.descricao cargo,\n" +
                    "    f.limitecredito,\n" +
                    "    f.obs,\n" +
                    "    case f.permitircheque when 'N' then 0 else 1 end as permitircheque,\n" +
                    "    case f.permitirfiado when 'N' then 0 else 1 end as permitirrotativo,\n" +
                    "    f.fone1,\n" +
                    "    f.fone2,\n" +
                    "    f.celular,\n" +
                    "    f.email,\n" +
                    "    f.fax,\n" +
                    "    f.inscricao_mun\n" +
                    "from\n" +
                    "    favorecidos f\n" +
                    "    left join cargos cg on f.cargo = cg.cargo\n" +
                    "where\n" +
                    "    f.tipofavorecido != 2\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("nro"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setMunicipioIBGE(rst.getInt("municipio"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataNascimento(rst.getDate("datanasc"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAdmissao(rst.getDate("dataadimissao"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setValorLimite(rst.getDouble("limitecredito"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setPermiteCheque(rst.getBoolean("permitircheque"));
                    imp.setPermiteCreditoRotativo(rst.getBoolean("permitirrotativo"));
                    imp.setTelefone(rst.getString("fone1"));
                    String fone2 = Utils.stringLong(rst.getString("fone2"));
                    if (!"0".equals(fone2) && !"".equals(fone2)) {
                        imp.addContato("A", "FONE2", fone2, "", "");
                    }
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setInscricaoMunicipal(rst.getString("inscricao_mun"));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    r.id,\n" +
                    "    r.competencia dataemissao,\n" +
                    "    r.notafiscal,\n" +
                    "    r.pdv,\n" +
                    "    r.valor,\n" +
                    "    r.obs,\n" +
                    "    r.cliente,\n" +
                    "    r.vencimento,\n" +
                    "    r.parcela,\n" +
                    "    cast(r.valor * (r.jurosplano/ 100) as numeric(10,2)) juros,\n" +
                    "    cast(r.valor * (r.percentualmulta/ 100) as numeric(10,2)) multa\n" +
                    "from\n" +
                    "    titulosareceber r\n" +
                    "    join favorecidos f on\n" +
                    "        f.favorecido = r.cliente\n" +
                    "where\n" +
                    "    (r.valorpago = 0 or r.datapago is null) and\n" +
                    "    r.empresa = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "    r.id"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("notafiscal"));
                    imp.setEcf(rst.getString("pdv"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setIdCliente(rst.getString("cliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setMulta(rst.getDouble("multa"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
}
