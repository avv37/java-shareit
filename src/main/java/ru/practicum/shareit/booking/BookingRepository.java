package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, Status status);


    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndBookerIdAndEndBeforeOrderByEndDesc(Long itemId, Long bookerId, LocalDateTime end);
}
