package com.logar.demo.controller;

import com.logar.demo.model.InfluencerResponse;
import com.logar.demo.model.LoginDTO;
import com.logar.demo.model.Influencer;
import com.logar.demo.security.JwtUtil;
import com.logar.demo.repository.InfluencerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/influencers")
public class InfluencerController {

    @Autowired
    private InfluencerRepository influencerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // ✅ injeta o utilitário de JWT

    private InfluencerResponse toResponse(Influencer influencer) {
        return new InfluencerResponse(
            influencer.getId(),
            influencer.getNome(),
            influencer.getEmail(),
            influencer.getPerfilInstagram(),
            influencer.getDataCadastro()
        );
    }

    // Criar influencer (POST)
    @PostMapping
    public ResponseEntity<InfluencerResponse> cadastrar(@RequestBody Influencer influencer) {
        if (influencer.getSenha() == null || influencer.getSenha().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        influencer.setSenha(passwordEncoder.encode(influencer.getSenha()));
        Influencer salvo = influencerRepository.save(influencer);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvo));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        Optional<Influencer> optionalInfluencer = Optional.ofNullable(influencerRepository.findByEmail(login.email()));

        if (optionalInfluencer.isPresent()) {
            Influencer influencer = optionalInfluencer.get();
            if (passwordEncoder.matches(login.senha(), influencer.getSenha())) {
                String token = jwtUtil.generateToken(influencer.getEmail());

                return ResponseEntity.ok(
                    Map.of(
                        "token", token,
                        "user", toResponse(influencer)
                    )
                );
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }

    // Listar todos
    @GetMapping
    public List<InfluencerResponse> getAllInfluencers() {
        return influencerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<InfluencerResponse> getInfluencerById(@PathVariable Long id) {
        return influencerRepository.findById(id)
                .map(influencer -> ResponseEntity.ok(toResponse(influencer)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<InfluencerResponse> updateInfluencer(@PathVariable Long id, @RequestBody Influencer updated) {
        return influencerRepository.findById(id).map(influencer -> {
            influencer.setNome(updated.getNome());
            influencer.setPerfilInstagram(updated.getPerfilInstagram());
            influencer.setEmail(updated.getEmail());
            if (updated.getSenha() != null && !updated.getSenha().isBlank()) {
                influencer.setSenha(passwordEncoder.encode(updated.getSenha()));
            }
            Influencer salvo = influencerRepository.save(influencer);
            return ResponseEntity.ok(toResponse(salvo));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInfluencer(@PathVariable Long id) {
        if (influencerRepository.existsById(id)) {
            influencerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
