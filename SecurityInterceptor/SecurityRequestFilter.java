package br.com.sisprog.auth.security;

import br.com.sisprog.auth.entity.SisgerOperacaoEnum;
import br.com.sisprog.common.utils.GsonUtil;
import br.com.sisprog.facades.ServiceLocator;
import br.com.sisprog.common.exceptions.GenericException;
import br.com.sisprog.facades.sisger.comuns.SessaoFacadeLocal;
import br.com.sisprog.facades.sisger.operacoes.SisgerOperacaoAutoFacadeLocal;
import br.com.sisprog.virtual.sisger.grupos.SisgerGruDTO;
import br.com.sisprog.virtual.sisger.locais.SisgerAplicacaoDTO;
import br.com.sisprog.virtual.sisger.locais.SisgerEmpresaDTO;
import br.com.sisprog.virtual.sisger.locais.SisgerLocalDTO;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

/**
 *
 * @author César Júnior
 */
public class SecurityRequestFilter implements ContainerRequestFilter {
    
    private final SecurityCheck check;
    
    public SecurityRequestFilter(SecurityCheck check) {
        this.check = check;
    }

    @Override
    public void filter(ContainerRequestContext crc) {
        MultivaluedMap<String, String> headers = crc.getHeaders();
        
        if (!headers.containsKey("X-Auth-Token")) {            
            throw new WebApplicationException(499);
        }
        
        if (check != null) {
            String token = headers.get("X-Auth-Token").get(0);
            Integer storageApp = -1;
            String  persistenceUnitName = "";
            
            if (headers.containsKey("X-Storage-User")) {
                String[] users = headers.get("X-Storage-User").get(0).split(",");
                storageApp = Integer.parseInt(users[0].trim());
                persistenceUnitName = users[1].trim();
            }
            
            if (token != null && !token.equals("0") ) {
                try (MongoClient mongo = new MongoClient(new MongoClientURI("mongodb://localhost:27017"))) {
                    
                    MongoDatabase db = mongo.getDatabase("meubanco");
                    MongoCollection<Document> coll = db.getCollection("Authentication");
                    Document doc = coll.find(new BsonDocument("token", new BsonString(token))).first();
                    
                    if (doc == null) {
                        throw new WebApplicationException(498);
                    }
                    
                    SessaoFacadeLocal sessaoFacade = (SessaoFacadeLocal) ServiceLocator.buscarEJB("Sessao");
                    sessaoFacade.setControlRequest(true);
                    this.setSessao(doc, sessaoFacade);
                    
                    Long old = doc.getLong("date");
                    Long now = new Date().getTime();
                    Long variancia = now - old;
                    Long tempo = (1000l * 60 * 30);
                    
                    if (variancia > tempo) {
                        coll.deleteOne(doc);
                        throw new WebApplicationException(498);
                    }
                    
                    /* if (!doc.getInteger("idUsuario").equals(storageUser) || !doc.getInteger("idEmpresa").equals(storageEmpresa)) {
                        coll.deleteOne(doc);
                        throw new WebApplicationException(Response.Status.FORBIDDEN);
                    } */
                    
                    if (check.Operacao() != SisgerOperacaoEnum.CONSULTAR) {
                        SisgerOperacaoAutoFacadeLocal facade;
                        
                        try {
                            facade = (SisgerOperacaoAutoFacadeLocal) ServiceLocator.buscarEJB("SisgerOperacaoAuto");
                        } catch (NamingException ex) {
                            throw new WebApplicationException(500);
                        }
                        
                        if (!facade.findByUsuarioMenuStatusAplicacaoOperacao(sessaoFacade.getUsuario().getId(), check.CodMenu(), storageApp, sessaoFacade.getLocal().getId(), check.Operacao().getCodigo())) {
                            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
                        }
                    }
                } catch (NamingException ex) {
                    Logger.getLogger(SecurityRequestFilter.class.getName()).log(Level.SEVERE, null, ex);
                    throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
                }
            } else {
                if (persistenceUnitName != null) {
                    try {
                        System.out.println(persistenceUnitName);
                        SessaoFacadeLocal sessaoFacade = (SessaoFacadeLocal) ServiceLocator.buscarEJB("Sessao");
                        sessaoFacade.setPersistenceUnitName(persistenceUnitName);
                        sessaoFacade.setControlRequest(true);
                    } catch (Exception e) {
                        throw new GenericException("Erro ao inicializar a sessÃ£o!", e);
                    }
                    
                }
            }
        }
    }
    
    private void setSessao(Document doc, SessaoFacadeLocal sessaoFacade) {
        try {
            String empresa = doc.getString("empresa");
            String local = doc.getString("local");
            String persistenceUnitName = doc.getString("persistenceUnitName");
            String usuario = doc.getString("usuario");
//            String aplicacao = doc.getString("aplicacao");
            
            if (empresa != null) {
                sessaoFacade.setEmpresa((SisgerEmpresaDTO) GsonUtil.getGson().fromJson(empresa, new TypeToken<SisgerEmpresaDTO>() {}.getType()));
            }
            
            if (local != null) {
                sessaoFacade.setLocal((SisgerLocalDTO) GsonUtil.getGson().fromJson(local, new TypeToken<SisgerLocalDTO>() {}.getType()));
            }
            
            if (persistenceUnitName != null) {
                sessaoFacade.setPersistenceUnitName(persistenceUnitName);
            }
            
            if (usuario != null) {
                sessaoFacade.setUsuario((SisgerGruDTO) GsonUtil.getGson().fromJson(usuario, new TypeToken<SisgerGruDTO>() {}.getType()));
            }
//            if (aplicacao != null) {
//                authFacade.setAplicacao((SisgerAplicacaoDTO) GsonUtil.getGson().fromJson(doc.getString("aplicacao"), new TypeToken<SisgerAplicacaoDTO>() {}.getType()));
//            }
            
        } catch(Exception e) {
            throw new GenericException("Erro ao inicializar a Sessao!", e);
        }
    }

}
