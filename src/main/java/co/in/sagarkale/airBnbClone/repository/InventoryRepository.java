package co.in.sagarkale.airBnbClone.repository;

import co.in.sagarkale.airBnbClone.dto.RoomPriceDto;
import co.in.sagarkale.airBnbClone.entity.Hotel;
import co.in.sagarkale.airBnbClone.entity.Inventory;
import co.in.sagarkale.airBnbClone.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);

    @Query("""
            SELECT DISTINCT i.hotel
            FROM Inventory i
            WHERE i.city = :city
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomCount
            GROUP BY i.hotel, i.room
            HAVING COUNT(i.date) = :dateCount
            """)
    Page<Hotel> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomCount") Integer roomCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount + :roomCount
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :checkInDate AND :checkOutDate
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomCount
                  AND i.closed = false
            """)
    void initBooking(@Param("roomId") Long roomId,
                     @Param("checkInDate") LocalDate checkInDate,
                     @Param("checkOutDate") LocalDate checkOutDate,
                     @Param("roomCount") Integer roomCount);


    @Query("""
           SELECT i
           FROM Inventory i
           WHERE i.room.id = :roomId
                AND i.date BETWEEN :checkInDate AND :checkOutDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomCount
           """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomCount") Integer roomCount
    );

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);

    @Query("""
           SELECT i
           FROM Inventory i
           WHERE i.room.id = :roomId
                AND i.date BETWEEN :checkInDate AND :checkOutDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount) >= :roomsCount
           """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(
            @Param("roomId") Long roomId,
            @Param("roomsCount") Integer roomsCount,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Modifying
    @Query("""
           UPDATE Inventory i
           SET i.bookedCount = i.totalCount - :roomsCount
           WHERE i.room.id = :roomId
                AND i.date BETWEEN :checkInDate AND :checkOutDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount) >= :roomsCount
           """
    )
    void cancelBooking(
            @Param("roomId") Long roomId,
            @Param("roomsCount") Integer roomsCount,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount - :roomsCount,
                    i.bookedCount = i.bookedCount + :roomsCount
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :checkInDate AND :checkOutDate
                  AND (i.totalCount - i.bookedCount) >= :roomsCount
                  AND i.reservedCount >= :roomsCount
                  AND i.closed = false
            """)
    void confirmBooking(
            @Param("roomId") Long roomId,
            @Param("roomsCount") Integer roomsCount,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    List<Inventory> findByRoomOrderByDate(Room room);

    @Query("""
           SELECT i
           FROM Inventory i
           WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed = false
           """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findInventoryAndLockBeforeUpdate(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.surgeFactor = :surgeFactor,
                    i.closed = :close
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
            """)
    void updateInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("surgeFactor") BigDecimal surgeFactor,
            @Param("close") Boolean close
    );

    @Query("""
       SELECT new co.in.sagarkale.airBnbClone.dto.RoomPriceDto(
            i.room,
            CASE
                WHEN COUNT(i) = :dateCount THEN AVG(i.price)
                ELSE NULL
            END
        )
       FROM Inventory i
       WHERE i.hotel.id = :hotelId
             AND i.date BETWEEN :startDate AND :endDate
             AND (i.totalCount - i.bookedCount) >= :roomsCount
             AND i.closed = false
       GROUP BY i.room
       """)
    List<RoomPriceDto> findRoomAveragePrice(
            @Param("hotelId") Long hotelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Long roomsCount,
            @Param("dateCount") Long dateCount
    );
}