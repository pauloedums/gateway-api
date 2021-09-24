package br.com.impacta.repository;

import javax.enterprise.context.ApplicationScoped;

import br.com.impacta.model.ClientDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ClientRepository implements PanacheRepository<ClientDTO>  {

}
