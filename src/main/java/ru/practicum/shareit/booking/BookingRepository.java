package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
                                                          Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findByItemOwnerId(Long bookerId, Sort sort);

    List<Booking> findByItemOwnerIdAndEndBefore(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByItemOwnerIdAndStartAfter(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
                                                             Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long bookerId, Status status, Sort sort);


    Optional<Booking> findFirstByItemIdAndStartBefore(Long itemId, LocalDateTime start, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartAfter(Long itemId, LocalDateTime start, Sort sort);

    Optional<Booking> findFirstByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end, Sort sort);
}
