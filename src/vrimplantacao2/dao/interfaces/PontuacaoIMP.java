/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoOrgaoPublico;
import vrimplantacao2.vo.enums.TipoSexo;
/**
 * Classe utilizada para importar pontuação do Zoombox
 * @author Alan
 */
public class PontuacaoIMP {
    private int id;
    private int id_venda;
    private String id_promocao;
    private String idcliente;
    private int pontos;
    private String cnpj;
    private Date dataexpiracao;
    private Date datacompra;
    private boolean lancamentomanual = false;
    private String id_loja;
    private String numerocupom;
    private int ecf;
    
    
}
