package ar.edu.utn.frc.tup.lciii.repositories;

import ar.edu.utn.frc.tup.lciii.Entities.SorteoEntity;
import ar.edu.utn.frc.tup.lciii.domain.Sorteo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SorteoRepository extends JpaRepository<SorteoEntity, Integer> {
}
