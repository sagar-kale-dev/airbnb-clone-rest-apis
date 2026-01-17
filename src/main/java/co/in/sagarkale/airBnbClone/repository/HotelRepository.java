package co.in.sagarkale.airBnbClone.repository;

import co.in.sagarkale.airBnbClone.entity.Hotel;
import co.in.sagarkale.airBnbClone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User user);
}