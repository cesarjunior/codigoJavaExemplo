package br.com.sisprog.service.tsd.cadastros;

import br.com.sisprog.auth.security.SecurityCheck;
import br.com.sisprog.auth.security.SecurityCheckInterceptor;
import br.com.sisprog.facades.ServiceLocator;
import br.com.sisprog.entities.tsd.cadastros.TsdEmail;
import br.com.sisprog.facades.tsd.cadastros.TsdEmailFacadeLocal;
import br.com.sisprog.facades.tsd.cadastros.TsdFacadeLocal;
import br.com.sisprog.virtual.tsd.cadastros.TsdEmailDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author César Júnior
 */
@Stateless
@Path("tsd/cadastros/tsdemail")
public class TsdEmailResource {

    private TsdEmailFacadeLocal tsdEmailFacade;
    private TsdFacadeLocal tsdFacade;

    private Gson gson;
    private Type typeMap;

    public TsdEmailResource() {
        try {
            tsdEmailFacade = (TsdEmailFacadeLocal) ServiceLocator.buscarEJB("TsdEmail");

            gson = new Gson();
            typeMap = new TypeToken<Map<String, Object>>() {}.getType();
        } catch (NamingException ex) {
            Logger.getLogger(TsdEmailResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PUT
    @SecurityCheck
    @Interceptors(SecurityCheckInterceptor.class)
    @Path("edit/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, TsdEmail entity) {
        tsdEmailFacade.edit(entity);
    }

    @PUT
    @SecurityCheck
    @Interceptors(SecurityCheckInterceptor.class)
    @Path("editlist/{idTsd}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<TsdEmailDTO> edit(List<TsdEmailDTO> emails, @PathParam("idTsd") int idTsd) {
        List<TsdEmail> emailsEntity = new ArrayList<>();
        List<TsdEmailDTO> emailsDTO = new ArrayList<>();
        emails.forEach(email -> emailsEntity.add(email.pegaEntity()));
        
        tsdEmailFacade.edit(emailsEntity, idTsd)
                .forEach(email -> {
                    email.setTsd(null);
                    emailsDTO.add(new TsdEmailDTO(email));
                });
        return emailsDTO;
    }
    
    @PUT
    @SecurityCheck
    @Interceptors(SecurityCheckInterceptor.class)
    @Path("editar-no-siscon/{idTsd}")
    @Consumes({MediaType.APPLICATION_JSON})
    public List<TsdEmailDTO> editSiscon(List<TsdEmailDTO> emails, @PathParam("idTsd") int idTsd) {
        List<TsdEmail> emailsEntity = new ArrayList<>();
        emails.forEach(email -> emailsEntity.add(email.pegaEntity()));
        tsdEmailFacade.editInSiscon(emailsEntity, idTsd);
        return emails;
    }

    @DELETE
    @SecurityCheck
    @Interceptors(SecurityCheckInterceptor.class)
    @Path("remove/{id}")
    public void remove(@PathParam("id") Integer id) {
        tsdEmailFacade.remove(id);
    }

    @GET
    @SecurityCheck
    @Interceptors(SecurityCheckInterceptor.class)
    @Path("find/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public TsdEmail find(@PathParam("id") Integer id) {
        return tsdEmailFacade.find(id);
    }

    @GET
    @SecurityCheck
    @Interceptors(SecurityCheckInterceptor.class)
    @Path("findall")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<TsdEmail> findAll() {
        return tsdEmailFacade.findAll();
    }

    @GET
    @SecurityCheck
    @Interceptors(SecurityCheckInterceptor.class)
    @Path("findbytsd/{idTsd}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<TsdEmailDTO> findbytsd(@PathParam("idTsd") Integer idTsd) {
        List<TsdEmailDTO> listaEmails = tsdEmailFacade.findByTsdReturnDTO(idTsd);
        return listaEmails;
    }

    @GET
    @SecurityCheck
    @Interceptors(SecurityCheckInterceptor.class)
    @Path("findbykey")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public TsdEmail findbykey(@QueryParam("jsonParams") String jsonParams) {
        Map<String, Object> jsonPars = gson.fromJson(jsonParams, typeMap);
        return tsdEmailFacade.findByKey(jsonPars);
    }

    @GET
    @SecurityCheck
    @Interceptors(SecurityCheckInterceptor.class)
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(tsdEmailFacade.count());
    }

}
