/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.AcumuladorIMP;
import vrimplantacao2.vo.importacao.AcumuladorLayoutIMP;
import vrimplantacao2.vo.importacao.AcumuladorLayoutRetornoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OperadorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class PdvVrDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "PdvVr";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id_loja, "
                    + "razaosocial "
                    + "from informacao"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id_loja"), rst.getString("razaosocial")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id,\n"
                    + "descricao,\n"
                    + "situacaotributaria cst,\n"
                    + "porcentagem aliq,\n"
                    + "0 reducao\n"
                    + "from aliquota\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }

        return result;
    }
    
    private void insertCodAnt_Produto() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.executeQuery(
                    "insert into implantacao.codant_produto "
                    + "(impsistema, "
                    + "imploja, "
                    + "impid, "
                    + "descricao, "
                    + "codigoatual)\n"
                    + "(select "
                    + "'" + getSistema() + "', "
                    + "'" + getLojaOrigem() + "', "
                    + "id::varchar, "
                    + "descricaocompleta, "
                    + "id "
                    + "from produto);\n"
                    + "insert into implantacao.codant_ean "
                    + "(importsistema,"
                    + "importloja,"
                    + "importid,"
                    + "ean) \n"
                    + "(select "
                    + "'" + getSistema() + "', "
                    + "'" + getLojaOrigem() + "', "
                    + "id_produto::varchar, "
                    + "codigobarras::varchar "
                    + "from produtoautomacao)"
            );
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            
            this.insertCodAnt_Produto();
                        
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.id,\n"
                    + "pa.codigobarras,\n"
                    + "pa.qtdembalagem,\n"
                    + "p.precovenda,\n"
                    + "p.descricaocompleta,\n"
                    + "p.descricaoreduzida,\n"
                    + "p.precovenda,\n"
                    + "p.id_aliquota,\n"
                    + "p.id_situacaocadastro,\n"
                    + "p.aceitamultiplicacaopdv,\n"
                    + "p.tipoembalagem,\n"
                    + "p.ncm,\n"
                    + "p.id_tipopiscofins,\n"
                    + "p.cest,\n"
                    + "a.situacaotributaria cstIcms,\n"
                    + "a.porcentagem,\n"
                    + "pis.cst cstPis,\n"
                    + "p.id_aliquota\n"
                    + "from produto p\n"
                    + "left join produtoautomacao pa on pa.id_produto = p.id\n"
                    + "inner join aliquota a on a.id_aliquota = p.id_aliquota\n"
                    + "inner join tipopiscofins pis on pis.id = p.id_tipopiscofins\n"
                    + "order by p.id"
            )) {                
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstPis"));
                    imp.setPiscofinsCstCredito(rst.getString("cstPis"));
                    imp.setIcmsDebitoId(rst.getString("id_aliquota"));
                    imp.setIcmsCreditoId(rst.getString("id_aliquota"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OperadorIMP> getOperadores() throws Exception {
        List<OperadorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "matricula,\n"
                    + "nome,\n"
                    + "senha,\n"
                    + "id_tiponiveloperador,\n"
                    + "id_situacaocadastro\n"
                    + "from operador\n"
                    + "where matricula <> 500001\n"
                    + "order by matricula"
            )) {
                while (rst.next()) {
                    OperadorIMP imp = new OperadorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportarMatricula(rst.getString("matricula"));
                    imp.setNome(rst.getString("nome"));
                    imp.setSenha(rst.getString("senha"));
                    imp.setId_tiponiveloperador(rst.getString("id_tiponiveloperador"));
                    imp.setId_situacadastro(rst.getString("id_situacaocadastro"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<AcumuladorIMP> getAcumuladores() throws Exception {
        List<AcumuladorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id,\n"
                    + "descricao\n"
                    + "from acumulador\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    AcumuladorIMP imp = new AcumuladorIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<AcumuladorLayoutIMP> getAcumuladorLayout() throws Exception {
        List<AcumuladorLayoutIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "distinct\n"
                    + "id_acumuladorlayout as id, ('IMPORTADO VR ACUMULADOR '||id_acumuladorlayout) as descricao\n"
                    + "from acumuladorlayout"
            )) {
                while (rst.next()) {
                    AcumuladorLayoutIMP imp = new AcumuladorLayoutIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<AcumuladorLayoutRetornoIMP> getAcumuladorLayoutReorno() throws Exception {
        List<AcumuladorLayoutRetornoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id_acumuladorlayout,\n"
                    + "id_acumulador,\n"
                    + "retorno,\n"
                    + "titulo\n"
                    + "from acumuladorlayout"
            )) {
                while (rst.next()) {
                    AcumuladorLayoutRetornoIMP imp = new AcumuladorLayoutRetornoIMP();
                    imp.setIdAcumuladorLayout(rst.getString("id_acumuladorlayout"));
                    imp.setIdAcumulador(rst.getString("id_acumulador"));
                    imp.setRetorno(rst.getString("retorno"));
                    imp.setTitulo(rst.getString("titulo"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
