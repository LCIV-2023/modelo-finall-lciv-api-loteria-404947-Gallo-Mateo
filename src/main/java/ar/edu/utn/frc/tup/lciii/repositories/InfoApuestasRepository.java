package ar.edu.utn.frc.tup.lciii.repositories;

import ar.edu.utn.frc.tup.lciii.Entities.InfoApuestasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoApuestasRepository extends JpaRepository<InfoApuestasEntity, Integer> {
}
