package com.alejandro.tarea7dwesalejandro.repositorios;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.alejandro.tarea7dwesalejandro.modelo.Plantas;

public interface PlantasRepository extends JpaRepository<Plantas, String> {
	Optional<Plantas> findByCodigo(String codigo);

	void deleteByCodigo(String codigo);

	boolean existsByCodigo(String codigo);
}
