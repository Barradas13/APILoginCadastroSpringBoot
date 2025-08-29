package com.logar.demo.controller;

import com.logar.demo.model.Influencer;
import com.logar.demo.repository.InfluencerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/influencers")
public class InfluencerController {

    @Autowired
    private InfluencerRepository influencerRepository;

    // Criar influencer (POST)
    @PostMapping
    public Influencer createInfluencer(@RequestBody Influencer influencer) {
        return influencerRepository.save(influencer);
    }

    // Listar todos (GET)
    @GetMapping
    public List<Influencer> getAllInfluencers() {
        return influencerRepository.findAll();
    }

    // Buscar por id (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Influencer> getInfluencerById(@PathVariable Long id) {
        Optional<Influencer> influencer = influencerRepository.findById(id);
        return influencer.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Atualizar (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Influencer> updateInfluencer(@PathVariable Long id, @RequestBody Influencer updated) {
        return influencerRepository.findById(id).map(influencer -> {
            influencer.setNome(updated.getNome());
            influencer.setPerfilInstagram(updated.getPerfilInstagram());
            influencer.setEmail(updated.getEmail());
            influencer.setSenha(updated.getSenha());
            return ResponseEntity.ok(influencerRepository.save(influencer));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Deletar (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInfluencer(@PathVariable Long id) {
        if (influencerRepository.existsById(id)) {
            influencerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
