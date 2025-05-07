package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.bson.types.ObjectId;

@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {
    
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {

        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO competitor) {

        JSONObject rta = new JSONObject();
        EntityManager em = null;

        try {
            em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();

            Competitor c = new Competitor();
            c.setId(new ObjectId().toHexString());
            c.setAddress(competitor.getAddress());
            c.setAge(competitor.getAge());
            c.setCellphone(competitor.getCellphone());
            c.setCity(competitor.getCity());
            c.setCountry(competitor.getCountry());
            c.setName(competitor.getName());
            c.setSurname(competitor.getSurname());
            c.setTelephone(competitor.getTelephone());

            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();

            System.out.println("ID asignado por Mongo: " + c.getId());
            rta.put("competitor_id", c.getId());

        } catch (Throwable t) {
            t.printStackTrace();
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            rta.put("error", "No se pudo crear el competidor.");
        } finally {
            if (em != null) {
                em.clear();
                em.close();
            }
        }

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .entity(rta.toJSONString())
                .build();
    }

    
    @OPTIONS
    public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
        return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }

}
