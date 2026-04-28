package ru.gorilla.gim.backend.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gorilla.gim.backend.entity.AccountEntity;

@Repository
public interface AccountRepository extends AbstractRepository<AccountEntity> {

    @Modifying
    @Query("UPDATE AccountEntity " +
            "SET avatar.id = :avatarId " +
            "WHERE id = :accountId")
    Integer setAccountAvatar(@Param("accountId") Long accountId, @Param("avatarId") Long avatarId);
}
