package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class RCNetDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "RCNet";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CodLoja as Filial, concat(nome, \" - \", local) as Nome from loja"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("Filial"),
                                    rst.getString("Nome")
                            )
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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  g.codgrupo merc1,\n" +
                    "  g.descricao merc1_desc,\n" +
                    "  sg.codsubgrupo merc2,\n" +
                    "  sg.descricao merc2_desc,\n" +
                    "  gm.codmarca merc3,\n" +
                    "  gm.descricao merc3_desc\n" +
                    "FROM\n" +
                    "  grupo g\n" +
                    "  left join gruposub sg on g.codgrupo = sg.codgrupo\n" +
                    "  left join grupomarca gm on sg.codsubgrupo = gm.codsubgrupo\n" +
                    "order by\n" +
                    "  merc1,\n" +
                    "  merc2,\n" +
                    "  merc3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  nReg,\n" +
                    "  Nome\n" +
                    "FROM\n" +
                    "  familiapreco f\n" +
                    "order by\n" +
                    "  f.nReg"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("nReg"));
                    imp.setDescricao(rst.getString("Nome"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        String sql = "SELECT\n" +
                    "  p.ninterno id,\n" +
                    "  p.datahoraalteracao,\n" +
                    "  p.codigobarra ean,\n" +
                    "  case when coalesce(p.qtdemb,1) >= 1 then coalesce(p.qtdemb,1) else 1 end qtdembalagem,\n" +
                    "  case when p.peso = 1 and p.tipobalanca = 'P' then 'KG' else 'UN' end tipoembalagem,\n" +
                    "  p.peso ebalanca,\n" +
                    "  p.validade,\n" +
                    "  p.descricao descricaocompleta,\n" +
                    "  p.abreviacao descricaoreduzida,\n" +
                    "  p.codgrupo merc1,\n" +
                    "  p.codgruposub merc2,\n" +
                    "  p.codgrupomarca merc3,\n" +
                    "  (select codgrupo from familiaprecoproduto where codigobarra = p.codigobarra and p.coddesativado = 0 limit 1) idfamiliaproduto,\n" +
                    "  coalesce(est.qtd,0) estoque,\n" +
                    "  p.estmin" + getLojaOrigem() + " estoqueminimo,\n" +
                    "  p.estmax" + getLojaOrigem() + " estoquemaximo,\n" +
                    "  p.m1 margem,\n" +
                    "  p.p" + getLojaOrigem() + " preco,\n" +
                    "  coalesce(cus.custo, 0) custosemimposto,\n" +
                    "  coalesce(cus.custo, 0) custocomimposto,\n" +
                    //"  coalesce(cus.custoformacao, 0) custocomimposto,\n" +
                    "  case p.coddesativado when 1 then 0 else 1 end id_situacaocadastro,\n" +
                    "  p.codigoncm ncm,\n" +
                    "  p.cest,\n" +
                    "  p.codpiscofinssaida piscofins_saida,\n" +
                    "  p.tabelacstpiscofins piscofins_natreceita,\n" +
                    "  p.cst icms_cst,\n" +
                    "  p.icmssaida icms_aliq_saida,\n" +
                    "  p.icmsentrada icms_saliq_entrada,\n" +
                    "  p.st\n" +
                    "FROM\n" +
                    "  itens p\n" +
                    "  left join estoquen" + getLojaOrigem() + " est on p.codigobarra = est.codigobarra\n" +
                    "  left join custoloja" + getLojaOrigem() + " cus on p.codigobarra = cus.codigobarra";
                    
                    System.out.println(sql);
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  p.ninterno id,\n" +
                    "  p.datahoraalteracao,\n" +
                    "  p.codigobarra ean,\n" +
                    "  case when coalesce(p.qtdemb,1) >= 1 then coalesce(p.qtdemb,1) else 1 end qtdembalagem,\n" +
                    "  case when p.peso = 1 and p.tipobalanca = 'P' then 'KG' else 'UN' end tipoembalagem,\n" +
                    "  p.peso ebalanca,\n" +
                    "  p.validade,\n" +
                    "  p.descricao descricaocompleta,\n" +
                    "  p.abreviacao descricaoreduzida,\n" +
                    "  p.codgrupo merc1,\n" +
                    "  p.codgruposub merc2,\n" +
                    "  p.codgrupomarca merc3,\n" +
                    "  (select codgrupo from familiaprecoproduto where codigobarra = p.codigobarra and p.coddesativado = 0 limit 1) idfamiliaproduto,\n" +
                    "  coalesce(est.qtd,0) estoque,\n" +
                    "  p.estmin" + getLojaOrigem() + " estoqueminimo,\n" +
                    "  p.estmax" + getLojaOrigem() + " estoquemaximo,\n" +
                    "  p.m1 margem,\n" +
                    "  p.p" + getLojaOrigem() + " preco,\n" +
                    "  coalesce(cus.custo, 0) custosemimposto,\n" +
                    "  coalesce(cus.custo, 0) custocomimposto,\n" +
                    //"  coalesce(cus.custoformacao, 0) custocomimposto,\n" +
                    "  case p.coddesativado when 1 then 0 else 1 end id_situacaocadastro,\n" +
                    "  p.codigoncm ncm,\n" +
                    "  p.cest,\n" +
                    "  p.codpiscofinssaida piscofins_saida,\n" +
                    "  p.tabelacstpiscofins piscofins_natreceita,\n" +
                    "  p.cst icms_cst,\n" +
                    "  p.icmssaida icms_aliq_saida,\n" +
                    "  p.icmsentrada icms_saliq_entrada,\n" +
                    "  p.st\n" +
                    "FROM\n" +
                    "  itens p\n" +
                    "  left join estoquen" + getLojaOrigem() + " est on p.codigobarra = est.codigobarra\n" +
                    "  left join custoloja" + getLojaOrigem() + " cus on p.codigobarra = cus.codigobarra"
            )) {
                int c1 = 0,c2 = 0;
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    try {
                        imp.setDataCadastro(format.parse(rst.getString("datahoraalteracao")));
                    } catch (ParseException e) {
                        imp.setDataCadastro(new Date());
                    }
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setIdFamiliaProduto(rst.getString("idfamiliaproduto"));
                    imp.setEstoque(Utils.stringToDouble(rst.getString("estoque")));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setMargem(Utils.stringToDouble(rst.getString("margem"), 0));
                    imp.setPrecovenda(Utils.stringToDouble(rst.getString("preco")));
                    imp.setCustoSemImposto(Utils.stringToDouble(rst.getString("custosemimposto")));
                    imp.setCustoComImposto(Utils.stringToDouble(rst.getString("custocomimposto")));
                    imp.setSituacaoCadastro(rst.getInt("id_situacaocadastro") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natreceita"));
                    imp.setIcmsDebitoId(rst.getString("st"));                    
                    
                    result.add(imp);
                    
                    c1++;
                    c2++;
                    
                    if (c1 == 1000) {
                        c1 = 0;
                        ProgressBar.setStatus("Carregando produtos..." + c2);
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  f.codfornec id,\n" +
                    "  f.nomecompleto razao,\n" +
                    "  f.nome fantasia,\n" +
                    "  f.cnpj,\n" +
                    "  f.ie,\n" +
                    "  f.codsuframa suframa,\n" +
                    "  f.codativo ativo,\n" +
                    "  f.endereco,\n" +
                    "  f.numero,\n" +
                    "  f.complemento,\n" +
                    "  f.bairro,\n" +
                    "  f.cidade,\n" +
                    "  f.uf,\n" +
                    "  f.codmunicipio id_municipio,\n" +
                    "  f.cep,\n" +
                    "  f.fone,\n" +
                    "  f.celular,\n" +
                    "  f.fax,\n" +
                    "  f.email,\n" +
                    "  f.diasentrega,\n" +
                    "  f.contato\n" +
                    "from\n" +
                    "  fornecedor f\n" +
                    "order by\n" +
                    "  f.codfornec"
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
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_municipio(rst.getInt("id_municipio"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone"));
                    String celular = Utils.stringLong(rst.getString("celular"));
                    String fax = Utils.stringLong(rst.getString("fax"));
                    String email = Utils.formataNumero(rst.getString("email"));
                    String contato = Utils.acertarTexto(rst.getString("contato"));
                    imp.setObservacao(!"".equals(fax) ? "FAX " + fax : "");
                    imp.setPrazoEntrega(rst.getInt("diasentrega"));
                    imp.addContato(
                            "1", 
                            "".equals(contato) ? "CONTATO" : contato,
                            imp.getTel_principal(),
                            celular, 
                            TipoContato.COMERCIAL, 
                            email
                    );
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  f.codfornecedor idfornecedor,\n" +
                    "  p.ninterno idproduto,\n" +
                    "  f.codigofornec codigoexterno,\n" +
                    "  f.qtdemb\n" +
                    "FROM\n" +
                    "  fornecedoritens f\n" +
                    "  join itens p on f.codigobarra = p.codigobarra"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(Utils.stringToDouble(rst.getString("qtdemb")));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }    

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  st,\n" +
                    "  descricao\n" +
                    "FROM st s;"
            )) {
                while (rst.next()) {
                    result.add(
                        new MapaTributoIMP(
                            rst.getString("st"),
                            rst.getString("descricao")
                        )
                    );
                }
            }
        }
        
        return result;
    }
    
    
}
