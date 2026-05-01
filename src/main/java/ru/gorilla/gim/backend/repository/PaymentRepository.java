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

//    @Query("SELECT p.account.id FROM PaymentEntity p WHERE p.account.id = :accountId")
    @Query(value = "Select * from payment_entity where account_id = :accountId", nativeQuery = true)
    List<PaymentEntity> findAllByAccount_Id(@Param("accountId") Long accountId);

    @Query("SELECT p.dateTo FROM PaymentEntity p WHERE p.account.id = :accountId ORDER BY p.dateTo DESC LIMIT 1")
    LocalDateTime findLastDateToByAccountId(@Param("accountId") Long accountId);

    @Modifying
    @Query("DELETE FROM PaymentEntity p WHERE p.account IN :accounts")
    void deleteAllByAccountIn(@Param("accounts") List<AccountEntity> accounts);

    long countByCreatedBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT p.created FROM PaymentEntity p WHERE p.created >= :from AND p.created < :to")
    List<LocalDateTime> findCreatedTimestamps(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
