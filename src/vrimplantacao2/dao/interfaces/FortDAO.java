package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

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
                    ""
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        
        return result;
    }
    
}
