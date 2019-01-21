package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
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

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    e.codigo,\n" +
                    "    e.nome\n" +
                    "from\n" +
                    "    EMPRESA e\n" +
                    "order by\n" +
                    "    e.codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("nome")));
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
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                
                }
            }
        }
        
        return result;
    }

    private String getCodigo(String cod1, String cod2) {
        return cod1 + "-" + cod2;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.itemp,\n" +
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
                    "    p.ituni custo,\n" +
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
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(getCodigo(rst.getString("itemp"), rst.getString("itcod")));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(String.valueOf(rst.getDouble("ean")));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(Math.round(rst.getFloat("qtdembalagemcotacao")));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
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
                    "    f.cod_emp,\n" +
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
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(getCodigo(rst.getString("cod_emp"), rst.getString("codigo")));
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
                    imp.setIdFornecedor(getCodigo("1", rst.getString("id_fornecedor")));
                    imp.setIdProduto(getCodigo("1", rst.getString("id_produto")));
                    imp.setCodigoExterno(rst.getString("codigo"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
