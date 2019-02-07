package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
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
public class CFSoftSiaECFDAO extends InterfaceDAO {
    
    @Override
    public String getSistema() {
        return "CFSoftSiaECF";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    g.gcodigo id,\n" +
                    "    g.gnome mercadologico\n" +
                    "from\n" +
                    "    grupo g\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("id"));
                    imp.setMerc1Descricao(rst.getString("mercadologico"));
                    
                    result.add(imp);
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
                    "    p.itcod,\n" +
                    "    p.itdata datacadastro,\n" +
                    "    p.italterado dataalteracao,\n" +
                    "    p.itbarra ean,\n" +
                    "    1 qtdembalagem,\n" +
                    "    coalesce(nullif(p.qtcompra,0), 1) qtdembalagemcotacao,\n" +
                    "    p.itunidade unidade,\n" +
                    "    p.itnome descricao,\n" +
                    "    p.itgrupo merc1,\n" +
                    "    p.itqtd estoqueminimo,\n" +
                    "    p.peso,\n" +
                    "    p.itmargem margem,\n" +
                    "    coalesce((\n" +
                    "        select first 1\n" +
                    "            i.entvlruni\n" +
                    "        from\n" +
                    "            entitem i\n" +
                    "            join entrada e on i.entcodigo = e.encodigo\n" +
                    "        where\n" +
                    "            i.entcoditem = p.itcod\n" +
                    "        order by\n" +
                    "            e.endta_emi desc, i.entcodigo desc\n" +
                    "    ), 0) custosemimposto,\n" +
                    "    coalesce((\n" +
                    "        select first 1\n" +
                    "            i.entvlruni + i.icmscredito\n" +
                    "        from\n" +
                    "            entitem i\n" +
                    "            join entrada e on i.entcodigo = e.encodigo\n" +
                    "        where\n" +
                    "            i.entcoditem = p.itcod\n" +
                    "        order by\n" +
                    "            e.endta_emi desc, i.entcodigo desc\n" +
                    "    ), 0) custocomimposto,\n" +
                    "    p.itpreco preco,\n" +
                    "    p.status,\n" +
                    "    p.ncm,\n" +
                    "    p.cest,\n" +
                    "    p.cst_pis piscofins_saida,\n" +
                    "    p.cst_pise piscofins_entrada,\n" +
                    "    p.cod_cred piscofins_natureza_receita,\n" +
                    "    p.origem icms_cst,\n" +
                    "    case when p.iticms < 0 then 0 else p.iticms end icms_aliquota,\n" +
                    "    p.fabricante\n" +
                    "from\n" +
                    "    item p\n" +
                    "where p.itemp = 1\n" +
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("itcod"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(String.valueOf(Math.round(rst.getDouble("ean"))));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(Math.round(rst.getFloat("qtdembalagemcotacao")));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca("KG".equals(imp.getTipoEmbalagem()));
                    if (imp.isBalanca()) {
                        imp.setEan(imp.getImportId());
                    }
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro("2".equals(rst.getString("status")) ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));
                    imp.setIcmsCst(rst.getString("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));
                    
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
                    "    f.codigo,\n" +
                    "    f.razao,\n" +
                    "    f.fantasia,\n" +
                    "    f.cnpj_cpf,\n" +
                    "    f.insc_est,\n" +
                    "    f.status,\n" +
                    "    f.endereco,\n" +
                    "    f.num,\n" +
                    "    f.comp,\n" +
                    "    f.bairro,\n" +
                    "    f.cidade,\n" +
                    "    f.uf,\n" +
                    "    f.cep,\n" +
                    "    f.fone,\n" +
                    "    f.dta_cad,\n" +
                    "    f.fax,\n" +
                    "    f.vendedor,\n" +
                    "    f.contato celular,\n" +
                    "    f.e_mail\n" +
                    "from\n" +
                    "    forne f\n" +
                    "where f.cod_emp = 1\n" +
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("insc_est"));
                    imp.setAtivo(1 == rst.getInt("status"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("num"));
                    imp.setComplemento(rst.getString("comp"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.setDatacadastro(rst.getDate("dta_cad"));
                    imp.addTelefone("FAX", rst.getString("fax"));
                    imp.addCelular(
                            ("".equals(rst.getString("vendedor")) ? "CELULAR" : rst.getString("vendedor")),
                            rst.getString("celular")
                    );
                    imp.addEmail("EMAIL", rst.getString("e_mail"), TipoContato.COMERCIAL);
                    
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
                    "    f.codigo id_fornecedor,\n" +
                    "    p.itcod id_produto,\n" +
                    "    it.codigo\n" +
                    "from\n" +
                    "    item_fornecedor it\n" +
                    "    join forne f on\n" +
                    "        it.fornecedor = f.codigo\n" +
                    "    join item p on\n" +
                    "        it.item = p.itcod\n" +
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigo"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    c.codigo,\n" +
                    "    c.cnpj_cpf,\n" +
                    "    c.insc_est,\n" +
                    "    coalesce(nullif(trim(c.razao),''), c.fantasia) razao,\n" +
                    "    c.fantasia,\n" +
                    "    c.status,\n" +
                    "    c.endereco,\n" +
                    "    c.num,\n" +
                    "    c.comp,\n" +
                    "    c.bairro,\n" +
                    "    c.cidade,\n" +
                    "    c.uf,\n" +
                    "    c.cep,\n" +
                    "    c.estado_civil,\n" +
                    "    c.dta_cad,\n" +
                    "    c.nascimento,\n" +
                    "    upper(c.sexo) sexo,\n" +
                    "    c.trabalho,\n" +
                    "    c.profissao,\n" +
                    "    c.salario,\n" +
                    "    c.limite,\n" +
                    "    c.conjugue,\n" +
                    "    c.pai,\n" +
                    "    c.mae,\n" +
                    "    c.obs,\n" +
                    "    c.obs2,\n" +
                    "    c.contato,\n" +
                    "    c.e_mail,\n" +
                    "    c.fone,\n" +
                    "    c.fax,\n" +
                    "    c.site\n" +
                    "from\n" +
                    "    cliente c\n" +
                    "where\n" +
                    "    c.codigo > 0 and\n" +
                    "    c.cod_emp = 1\n" +
                    "order by\n" +
                    "    c.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("codigo"));
                    imp.setCnpj(rst.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rst.getString("insc_est"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getInt("status") == 1);
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("num"));
                    imp.setComplemento(rst.getString("comp"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(rst.getString("estado_civil"));
                    imp.setDataCadastro(rst.getDate("dta_cad"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rst.getString("trabalho"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomeConjuge(rst.getString("conjugue"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setObservacao2(rst.getString("obs2") + "OBS " + rst.getString("site"));
                    imp.addContato("A", "CONTATO", rst.getString("contato"), "", "");
                    imp.addEmail(rst.getString("e_mail"), TipoContato.COMERCIAL);
                    imp.setTelefone(rst.getString("fone"));
                    imp.addTelefone("FAX", rst.getString("fax"));
                    
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
                    "    d.codigo,\n" +
                    "    d.duplicata,\n" +
                    "    d.ecf,\n" +
                    "    d.debito_venda valor,\n" +
                    "    d.obs,\n" +
                    "    d.cod_cliente,\n" +
                    "    c.cnpj_cpf,\n" +
                    "    d.vencimento\n" +
                    "from\n" +
                    "    duplicata d\n" +
                    "    join cliente c on\n" +
                    "        d.emp = c.cod_emp and\n" +
                    "        d.cod_cliente = c.codigo\n" +
                    "where\n" +
                    "    c.codigo != 0 and\n" +
                    "    d.status = 'ABERTA' and\n" +
                    "    not d.codigo in (select duplicata from cheque) and\n" +
                    "    d.debito_venda > 0\n" +
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("codigo"));
                    imp.setNumeroCupom(rst.getString("duplicata"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setIdCliente(rst.getString("cod_cliente"));
                    imp.setCnpjCliente(rst.getString("cnpj_cpf"));
                    imp.setDataEmissao(rst.getDate("vencimento"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    ch.codigo,\n" +
                    "    c.cnpj_cpf,\n" +
                    "    ch.numero,\n" +
                    "    ch.banco,\n" +
                    "    ch.agencia,\n" +
                    "    ch.conta,\n" +
                    "    ch.emissao,\n" +
                    "    dp.duplicata,\n" +
                    "    dp.ecf,\n" +
                    "    c.insc_est,\n" +
                    "    ch.valor,\n" +
                    "    c.fone,\n" +
                    "    c.razao\n" +
                    "from\n" +
                    "    cheque ch\n" +
                    "    join duplicata dp on\n" +
                    "        ch.duplicata = dp.codigo\n" +
                    "    join cliente c on\n" +
                    "        ch.cliente = c.codigo\n" +
                    "where\n" +
                    "   dp.dtpago is null and\n" +
                    "   ch.cliente > 0 and\n" +
                    "   ch.valor > 0\n" +
                    "order by\n" +
                    "    ch.codigo"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rst.getString("codigo"));
                    imp.setCpf(rst.getString("cnpj_cpf"));
                    imp.setNumeroCheque(rst.getString("numero"));
                    imp.setBanco(Utils.stringToInt(rst.getString("banco")));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("duplicata"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setRg(rst.getString("insc_est"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setNome(rst.getString("razao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
