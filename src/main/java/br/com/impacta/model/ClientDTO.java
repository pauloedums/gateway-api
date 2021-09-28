package br.com.impacta.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.config.ConfigMapping;

@Entity
@ConfigMapping(prefix = "client")
public class ClientDTO {

    @Id
    @ConfigProperty(name = "id", defaultValue = "1")
    public Long id;

    @Column(name = "client_name", length = 100)
    @ConfigProperty(name = "client_name", defaultValue = "Teste")
    private String nome;

    @Column(name = "client_birthdate", length = 10)
    @ConfigProperty(name = "client_birthdate", defaultValue = "01/07/1970")
    private String dataDeNascimento;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getDataDeNascimento() {
        return dataDeNascimento;
    }
    public void setDataDeNascimento(String dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    
}