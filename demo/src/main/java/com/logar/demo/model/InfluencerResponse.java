package com.logar.demo.model;
import java.time.LocalDateTime;

public record InfluencerResponse(Long id, String nome, String email, String perfilInstagram, LocalDateTime dataCadastro) {}
