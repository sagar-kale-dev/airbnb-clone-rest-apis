package co.in.sagarkale.airBnbClone.repository;

import co.in.sagarkale.airBnbClone.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}