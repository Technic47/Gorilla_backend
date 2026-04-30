package ru.gorilla.gim.backend.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gorilla.gim.backend.entity.AccountEntity;
import ru.gorilla.gim.backend.entity.PaymentEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends AbstractRepository<PaymentEntity> {
    List<PaymentEntity> findAllByAccount_Id(Long accountId);

    PaymentEntity findTopByAccount_IdOrderByCreatedDesc(Long accountId);

    @Modifying
    @Query("DELETE FROM PaymentEntity p WHERE p.account IN :accounts")
    void deleteAllByAccountIn(@Param("accounts") List<AccountEntity> accounts);

    long countByCreatedBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT p.created FROM PaymentEntity p WHERE p.created >= :from AND p.created < :to")
    List<LocalDateTime> findCreatedTimestamps(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
