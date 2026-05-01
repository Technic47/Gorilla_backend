package ru.gorilla.gim.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gorilla.gim.backend.entity.AccountEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountRepository extends AbstractRepository<AccountEntity> {

    @Modifying
    @Query("UPDATE AccountEntity " +
            "SET avatar.id = :avatarId " +
            "WHERE id = :accountId")
    Integer setAccountAvatar(@Param("accountId") Long accountId, @Param("avatarId") Long avatarId);

@Query("SELECT a FROM AccountEntity a WHERE " +
            "LOWER(a.firstName)   LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(a.secondName)  LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(a.lastName)    LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(a.cardNumber)  LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(a.phone)       LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<AccountEntity> searchByQuery(@Param("q") String q, Pageable pageable);

    List<AccountEntity> findAllByDemoTrue();

    @Modifying
    @Query("DELETE FROM AccountEntity WHERE demo = true")
    void deleteAllByDemoTrue();

    long countByCreatedBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT a.created FROM AccountEntity a WHERE a.created >= :from AND a.created < :to")
    List<LocalDateTime> findCreatedTimestamps(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
