package com.logar.demo.repository;

import com.logar.demo.model.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerRepository extends JpaRepository<Influencer, Long> {

    // exemplo de consultas extras
    Influencer findByEmail(String email);
    Influencer findByPerfilInstagram(String perfilInstagram);
}
