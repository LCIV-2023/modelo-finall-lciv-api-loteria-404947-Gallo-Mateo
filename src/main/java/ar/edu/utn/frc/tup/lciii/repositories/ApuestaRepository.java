package ar.edu.utn.frc.tup.lciii.repositories;

import ar.edu.utn.frc.tup.lciii.Entities.ApuestaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApuestaRepository extends JpaRepository<ApuestaEntity, Integer> {

    Optional<List<ApuestaEntity>> findAllByIdSorteo(Integer id_sorteo);
}
