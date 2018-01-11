package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class TiTecnologiaDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Ti Tecnologia";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT empcod, EMPNOM FROM superbertola.tsc008a"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("empcod"), rst.getString("empnom")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	est.codigo id,\n" +
                    "	case est.data when '0000-00-00' then null else est.data end datacadastro,\n" +
                    "	est.codbar ean,\n" +
                    "	trim(est.uni) tipoembalagem,\n" +
                    "	case when trim(coalesce(est.bala,'')) != '' then 1 else 0 end e_balanca,\n" +
                    "	est.descpro descricaocompleta,\n" +
                    "	coalesce(est.descpdv) descricaoreduzida,\n" +
                    "	est.GRUPO merc1,\n" +
                    "	est.subgrupo merc2,\n" +
                    "	est.NETPESO pesobruto,\n" +
                    "	est.NETPESO pesoliquido,\n" +
                    "	est.estoque,\n" +
                    "	est.MIN estoqueminimo,\n" +
                    "	est.LUCRO margem,\n" +
                    "	est.CUSTOCOM custosemimposto,\n" +
                    "	est.preco1 precovenda,\n" +
                    "	est.ncm,\n" +
                    "	est.cest,\n" +
                    "	case est.inativo when 1 then 0 else 1 end ativo,\n" +
                    "	est.cstpis piscofins,\n" +
                    "	est.codst icms_st,\n" +
                    "	est.icms icms_aliq\n" +
                    "from \n" +
                    "	tslc003 est\n" +
                    "order by est.codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setCodigoSped(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setEan(rst.getString("ean"));
                    if (imp.isBalanca()) {
                        String ean = Utils.stringLong(imp.getEan());
                        if (ean.startsWith("2") && ean.length() > 6) {
                            imp.setEan(ean.substring(1, ean.length()));
                        }
                    }                    
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("ativo")));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofins"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins"));
                    imp.setIcmsCst(rst.getInt("icms_st"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliq"));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.CLICOD id,\n" +
                    "	case when trim(coalesce(c.CLICGC,'')) != '' then c.CLICGC else c.CLICPF end cnpj,\n" +
                    "	case when trim(coalesce(c.CLIINS,'')) != '' then c.CLIINS else c.CLIRG end inscricaoestadual,\n" +
                    "	c.CLINOM razao,\n" +
                    "	c.CLIFAN fantasia,\n" +
                    "	case c.BLOQUEIO when 'S' then 1 else 0 end bloqueado,\n" +
                    "	c.CLIEND endereco,\n" +
                    "	c.numero,\n" +
                    "	c.CLIBAI bairro,\n" +
                    "	c.CLICID cidade,\n" +
                    "	c.CLIEST uf,\n" +
                    "	c.CLICEP cep,\n" +
                    "	case c.DTNASC when '0000-00-00' then null else c.DTNASC end datanascimento,\n" +
                    "	case c.CADASTRO when '0000-00-00' then null else c.CADASTRO end datacadastro,\n" +
                    "	c.EMRPESA empresa,\n" +
                    "	c.limite,\n" +
                    "	c.CONJUJE conjuge,\n" +
                    "	c.OBS observacao,\n" +
                    "	c.diapgto diavencimento,\n" +
                    "	c.telent telefone,\n" +
                    "	c.CELULAR,\n" +
                    "	c.email,\n" +
                    "	c.faxent fax,\n" +
                    "	c.telcob,\n" +
                    "	c.ENDCOB cob_endereco,\n" +
                    "	c.BAICOB cob_bairro,\n" +
                    "	c.CIDCOB cob_cidade,\n" +
                    "	c.ESTCOB cob_estado,\n" +
                    "	c.CEPCOB cob_cep\n" +
                    "from\n" +
                    "	tslc001 c\n" +
                    "order by\n" +
                    "	c.clicod;"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("CELULAR"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCobrancaTelefone(rst.getString("telcob"));
                    imp.setCobrancaEndereco(rst.getString("cob_endereco"));
                    imp.setCobrancaBairro(rst.getString("cob_bairro"));
                    imp.setCobrancaMunicipio(rst.getString("cob_cidade"));
                    imp.setCobrancaUf(rst.getString("cob_estado"));
                    imp.setCobrancaCep(rst.getString("cob_cep"));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	cast(concat(r.recseq, '-', r.reccli) as char) id,\n" +
                    "	coalesce(case r.recemiss when '0000-00-00' then '2000-01-01' else r.recemiss end, current_date) dataemissao,\n" +
                    "	r.recibo numerocupom,\n" +
                    "	r.estacao ecf,\n" +
                    "	r.recvalor valor,\n" +
                    "	concat(trim(r.recobs), ' - ', trim(reccomple)) observacao,\n" +
                    "	r.reccli id_cliente,\n" +
                    "    c.CLICGC cnpj,\n" +
                    "    c.CLINOM,\n" +
                    "	r.recvenci vencimento,\n" +
                    "	r.recjuros juros,\n" +
                    "	coalesce(case r.recdatapag when '0000-00-00' then null else r.recdatapag end, r.recvenci) datapagamento,\n" +
                    "	r.recvalpag valorpago\n" +
                    "from \n" +
                    "	tsm003 r\n" +
                    "    join tslc001 c on r.reccli = c.clicod\n" +
                    "where\n" +
                    "	r.recest = 0\n" +
                    "    and r.recbaixa != 'F'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setJuros(rst.getDouble("juros"));
                    if (rst.getDouble("valorpago") > 0) {
                        imp.addPagamento(
                                imp.getId(), 
                                rst.getDouble("valorpago"), 
                                0, 
                                0, 
                                rst.getDate("datapagamento"),
                                "");
                    }
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
