package br.com.impacta;

import java.net.URI;
import java.util.function.Function;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.impacta.model.ClientDTO;
import br.com.impacta.repository.ClientRepository;
import br.com.impacta.resources.ClientResources;

@Path("/clients")
@Tag(name="Clientes", description="API de Clientes")
@SecurityScheme(
    securitySchemeName = "quarkus",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        password = @OAuthFlow(
            tokenUrl = "http://localhost:10520/auth/realms/Quarkus/protocol/openid-connect/token"
        )
    )
)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    @Inject
    ClientRepository clientRepository;

    @Inject
    ClientResources clientResources;

    @GET
    @Path("/name/{name}")
    public Response getClientByName(@PathParam("name") String name){
        return clientRepository
        .find("client_name", name)
        .singleResultOptional()
        .map(client -> Response.ok(client).build())
        .orElse(Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}")
    public Response getClientById(@PathParam("id") Long id){
        return clientRepository
                .findByIdOptional(id)
                .map(client -> Response.ok(client).build())
                .orElse(Response.status(Status.NOT_FOUND).build());
    }


    @POST
    @Transactional
    @Path("/add")
    @RolesAllowed("admin")
    public Response addClient(ClientDTO client){
        clientRepository.persist(client);
        if (clientRepository.isPersistent(client)) {
        return Response.created(URI.create("/client/" + client.getId())).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }


    @DELETE
    @Transactional
    @Path("/delete/{id}")
    @RolesAllowed("admin")
    public Response deleteClient(@PathParam("id") Long id){
        boolean deleted = clientRepository.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Status.BAD_REQUEST).build();
    }


    @PUT
    @Path("/update/{id}")
    @Transactional
    @RolesAllowed("admin")
    public Response updateClient(@PathParam("id") Long id, ClientDTO client_updates){

        if (clientResources.get(id) == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        ClientDTO client = clientResources.update(id, client_updates);

        return clientRepository
                .findByIdOptional(client.getId())
                .map(clientRepo -> Response.ok(clientRepo).build())
                .orElse(Response.status(Status.NOT_FOUND).build());
    }

}
