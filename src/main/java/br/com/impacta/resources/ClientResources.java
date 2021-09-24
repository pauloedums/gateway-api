package br.com.impacta.resources;

import br.com.impacta.model.ClientDTO;
import br.com.impacta.repository.ClientRepository;
import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;

public interface ClientResources extends PanacheRepositoryResource<ClientRepository, ClientDTO, Long>{
    
}
