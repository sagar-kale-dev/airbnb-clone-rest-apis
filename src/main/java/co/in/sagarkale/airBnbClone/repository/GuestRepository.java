package co.in.sagarkale.airBnbClone.repository;

import co.in.sagarkale.airBnbClone.entity.Guest;
import co.in.sagarkale.airBnbClone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);
}