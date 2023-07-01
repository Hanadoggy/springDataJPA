package dbwls.springDataJPA.repository;

import dbwls.springDataJPA.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
