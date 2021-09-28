package br.com.impacta.resources;

import br.com.impacta.model.ClientDTO;
import br.com.impacta.repository.ClientRepository;
import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;
import io.quarkus.rest.data.panache.ResourceProperties;

@ResourceProperties(exposed = false)
public interface ClientResources extends PanacheRepositoryResource<ClientRepository, ClientDTO, Long>{
    
}
