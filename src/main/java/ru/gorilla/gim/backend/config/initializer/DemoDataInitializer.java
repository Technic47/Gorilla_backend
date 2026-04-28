package ru.gorilla.gim.backend.config.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.gorilla.gim.backend.entity.AccountEntity;
import ru.gorilla.gim.backend.repository.AccountRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {

    @Value("${demo.data.enable}")
    private Boolean demoDataEnabled;
    @Value("${demo.data.amount}")
    private Integer demoDataAmount;

    private final AccountRepository accountRepository;

    private static final String[] FIRST_NAMES = {
            "Александр", "Михаил", "Иван", "Дмитрий", "Андрей",
            "Сергей", "Максим", "Артём", "Алексей", "Николай",
            "Павел", "Роман", "Денис", "Владимир", "Антон",
            "Екатерина", "Анна", "Мария", "Ольга", "Наталья",
            "Татьяна", "Елена", "Юлия", "Ирина", "Светлана"
    };

    private static final String[] SECOND_NAMES_MALE = {
            "Александрович", "Михайлович", "Иванович", "Дмитриевич",
            "Андреевич", "Сергеевич", "Максимович", "Алексеевич",
            "Павлович", "Романович", "Денисович", "Владимирович"
    };

    private static final String[] SECOND_NAMES_FEMALE = {
            "Александровна", "Михайловна", "Ивановна", "Дмитриевна",
            "Андреевна", "Сергеевна", "Максимовна", "Алексеевна",
            "Павловна", "Романовна", "Денисовна", "Владимировна"
    };

    private static final String[] LAST_NAMES_MALE = {
            "Иванов", "Смирнов", "Кузнецов", "Попов", "Васильев",
            "Петров", "Соколов", "Михайлов", "Новиков", "Фёдоров",
            "Морозов", "Волков", "Алексеев", "Лебедев", "Семёнов",
            "Егоров", "Павлов", "Козлов", "Степанов", "Николаев"
    };

    private static final String[] LAST_NAMES_FEMALE = {
            "Иванова", "Смирнова", "Кузнецова", "Попова", "Васильева",
            "Петрова", "Соколова", "Михайлова", "Новикова", "Фёдорова",
            "Морозова", "Волкова", "Алексеева", "Лебедева", "Семёнова",
            "Егорова", "Павлова", "Козлова", "Степанова", "Николаева"
    };

    @Override
    public void run(ApplicationArguments args) {
        if (!demoDataEnabled || accountRepository.count() >= demoDataAmount) {
            return;
        }

        log.info("Generating demo data");
        Random random = new Random(42);
        List<AccountEntity> accounts = new ArrayList<>(demoDataAmount);
        LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= demoDataAmount; i++) {
            boolean isFemale = random.nextBoolean();

            String firstName = isFemale
                    ? FIRST_NAMES[15 + random.nextInt(10)]   // indices 15-24 are female names
                    : FIRST_NAMES[random.nextInt(15)];        // indices 0-14 are male names

            String[] lastNames = isFemale ? LAST_NAMES_FEMALE : LAST_NAMES_MALE;
            String lastName = lastNames[random.nextInt(lastNames.length)];

            // ~25% of accounts have no patronymic
            String secondName = null;
            if (random.nextInt(4) != 0) {
                String[] secondNames = isFemale ? SECOND_NAMES_FEMALE : SECOND_NAMES_MALE;
                secondName = secondNames[random.nextInt(secondNames.length)];
            }

            // paidUntil: 20% null, 50% future (active), 30% past (expired)
            LocalDateTime paidUntil;
            int paidCase = random.nextInt(10);
            if (paidCase < 2) {
                paidUntil = null;
            } else if (paidCase < 7) {
                paidUntil = now.plusDays(random.nextInt(365) + 1);
            } else {
                paidUntil = now.minusDays(random.nextInt(180) + 1);
            }

            LocalDateTime created = now.minusDays(random.nextInt(730)).minusHours(random.nextInt(24));
            LocalDateTime updated = created.plusDays(random.nextInt(60));

            AccountEntity account = new AccountEntity();
            account.setFirstName(firstName);
            account.setSecondName(secondName);
            account.setLastName(lastName);
            account.setCardNumber(String.format("GIM-%05d", i));
            account.setIsBlocked(random.nextInt(10) < 2);  // ~20% blocked
            account.setPaidUntil(paidUntil);
            account.setCreated(created);
            account.setUpdated(updated);

            accounts.add(account);
        }

        accountRepository.saveAll(accounts);
        log.info("Demo data: {} accounts seeded", accounts.size());
    }
}
