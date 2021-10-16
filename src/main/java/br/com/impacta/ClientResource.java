package br.com.impacta;

import java.net.URI;

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

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
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
    ClientDTO clientDTOConfig;

    @Inject
    ClientRepository clientRepository;

    @Inject
    ClientResources clientResources;

    @GET
    @Fallback(fallbackMethod = "getClientByNameFallback")
    @Retry(maxRetries = 5)
    @Path("/name/{name}")
    @Operation(
        operationId = "getClientByName",
        summary = "Busca de cliente por nome",
        description = "Busca de cliente por nome")
    @APIResponse(
      responseCode = "200",
      description = "Cliente encontrado",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ClientDTO.class)))
    @APIResponse(
        responseCode = "404",
        description = "Cliente não encontrado",
        content = @Content(mediaType = "application/json"))
    public Response getClientByName(
        @Parameter(
            description = "Busca o cliente pelo nome",
            required = true,
            example = "Cliente",
            schema = @Schema(implementation = String.class))
        @PathParam("name") String name){
        return clientRepository
        .find("client_name", name)
        .singleResultOptional()
        .map(client -> Response.ok(client).build())
        .orElse(Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}")
    @Operation(
        operationId = "getClientById",
        summary = "Busca de cliente pelo ID",
        description = "Busca de cliente pelo ID")
    @APIResponse(
      responseCode = "200",
      description = "Cliente encontrado",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ClientDTO.class)))
    @APIResponse(
        responseCode = "404",
        description = "Cliente não encontrado",
        content = @Content(mediaType = "application/json"))
    public Response getClientById(
        @Parameter(
            description = "Busca o cliente pelo ID",
            required = true,
            example = "1",
            schema = @Schema(implementation = Long.class))
        @PathParam("id") Long id){
        return clientRepository
                .findByIdOptional(id)
                .map(client -> Response.ok(client).build())
                .orElse(Response.status(Status.NOT_FOUND).build());
    }


    @POST
    @Transactional
    @Path("/add")
    @RolesAllowed("admin")
    @Operation(
        operationId = "addClient",
        summary = "Cadastra um novo cliente",
        description = "Cadastra um novo cliente")
    @APIResponse(
      responseCode = "201",
      description = "Cliente Cadastrado",
      content = @Content(
        mediaType = "application/json"))
    @APIResponse(
        responseCode = "400",
        description = "Cadastro fora das especificações",
        content = @Content(mediaType = "application/json"))
    @APIResponse(
        responseCode = "401",
        description = "Sem autorização",
        content = @Content(mediaType = "application/json"))
    public Response addClient(
        @Parameter(
            description = "Cadastra um novo Cliente",
            required = true,
            schema = @Schema(implementation = ClientDTO.class))
        ClientDTO client){
        if(client.getDataDeNascimento().length() > 10 || client.getNome().length() > 100){
            return Response.status(Status.BAD_REQUEST).build();    
        }

        clientRepository.persist(client);
        if (clientRepository.isPersistent(client)) {
            return Response.created(URI.create("/clients/" + client.getId())).build();
        }

        return Response.status(Status.BAD_REQUEST).build();
    }


    @DELETE
    @Transactional
    @Path("/delete/{id}")
    @RolesAllowed("admin")
    @Operation(
        operationId = "deleteClient",
        summary = "Deleta um cliente pelo ID",
        description = "Deleta um cliente")
    @APIResponse(
        responseCode = "200",
        description = "Cliente deletado",
        content = @Content(
            mediaType = "application/json"))
    @APIResponse(
        responseCode = "400",
        description = "Request fora das especificações",
        content = @Content(mediaType = "application/json"))
    @APIResponse(
        responseCode = "401",
        description = "Sem autorização",
        content = @Content(mediaType = "application/json"))
    public Response deleteClient(
        @Parameter(
            description = "Deleta o cliente pelo ID",
            required = true,
            example = "1",
            schema = @Schema(implementation = Long.class))
        @PathParam("id") Long id){
        boolean deleted = clientRepository.deleteById(id);
        return deleted ? Response.ok().build() : Response.status(Status.BAD_REQUEST).build();
    }


    @PUT
    @Path("/update/{id}")
    @Transactional
    @RolesAllowed("admin")
    @Operation(
        operationId = "updateClient",
        summary = "Atualiza um cliente pelo ID",
        description = "Atualiza um cliente")
    @APIResponse(
        responseCode = "200",
        description = "Cliente atualizado",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ClientDTO.class)))    
    @APIResponse(
        responseCode = "404",
        description = "Cliente não encontrado",
        content = @Content(mediaType = "application/json"))
    @APIResponse(
        responseCode = "401",
        description = "Sem autorização",
        content = @Content(mediaType = "application/json"))  
    public Response updateClient(
        @Parameter(
            description = "Atualiza o cliente pelo ID",
            required = true,
            example = "1",
            schema = @Schema(implementation = Long.class))
        @PathParam("id") Long id, 
        @Parameter(
            description = "Atualiza um Cliente",
            required = true,
            schema = @Schema(implementation = ClientDTO.class))
        ClientDTO client_updates){

        if (clientResources.get(id) == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        ClientDTO client = clientResources.update(id, client_updates);

        return clientRepository
                .findByIdOptional(client.getId())
                .map(clientRepo -> Response.ok(clientRepo).build())
                .orElse(Response.status(Status.NOT_FOUND).build());
    }


    // MÉTODOS FALLBACK

    private Response getClientByNameFallback(@PathParam("name") String name){
        return Response.ok(clientDTOConfig).build();
    }

}
