package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

//import ru.practicum.events.dto.NewDto;

import ru.practicum.requests.EventRequestStatus;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestShort;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Optional<ParticipationRequest> findByIdAndRequesterId(Long requestId, Long requesterId);

    Optional<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventIdAndIdIn(Long eventId, Set<Long> requestIds);

    @Query(value = "SELECT COUNT(*) " +
            "FROM requests " +
            "WHERE event_id = ?1 and status = 'CONFIRMED'", nativeQuery = true)
    Integer getConfirmedRequestsByEventId(Long eventId);

    //    List<NewDto> findByEventIdInAndStatus(Set<Long> longs, EventRequestStatus eventRequestStatus);
//
//    @Query("select new ru.practicum.model.hit.dto.ViewHitStatsDto(e.id, count(r.id)) " +
//            "from Request as r " +
//            "where r.id in :eventIds and r.status like 'CONFIRMED' " +
//            "group by e.id ")
//    List<RequestStat> getConfirmedRequestsForEvents(@Param("eventIds") List<Integer> eventIds);
    @Query(
            "select new ru.practicum.requests.model.RequestShort(pr.event.id, count(pr.id)) " +
                    "from ParticipationRequest as pr " +
                    "where pr.event.id in :eventIds " +
                    "and pr.status = :eventRequestStatus " +
                    "group by pr.event.id"
    )
    List<RequestShort> findByEventIdInAndStatus(Set<Long> eventIds, EventRequestStatus eventRequestStatus);

}