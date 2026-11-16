package br.com.fiap.descartefacil.repository;

import br.com.fiap.descartefacil.domain.RotaCataBagulho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RotaCataBagulhoRepository extends JpaRepository<RotaCataBagulho, Long> {

    List<RotaCataBagulho> findByBairroIgnoreCase(String bairro);

    List<RotaCataBagulho> findByBairroIgnoreCaseAndLogradouroContainingIgnoreCase(String bairro, String logradouro);
}
