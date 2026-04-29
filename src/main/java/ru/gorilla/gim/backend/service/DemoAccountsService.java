package ru.gorilla.gim.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gorilla.gim.backend.entity.AccountEntity;
import ru.gorilla.gim.backend.entity.PaymentEntity;
import ru.gorilla.gim.backend.repository.AccountRepository;
import ru.gorilla.gim.backend.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ru.gorilla.gim.backend.util.PeriodUtils.buildPeriodDescription;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemoAccountsService {

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

    private static final Period[] PERIODS = {
            Period.ofMonths(1),
            Period.ofMonths(3),
            Period.ofMonths(6),
            Period.ofYears(1)
    };

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    public int generateAccounts(int amount) {
        log.info("Generating {} demo accounts", amount);
        Random random = new Random();
        List<AccountEntity> accounts = new ArrayList<>(amount);
        LocalDateTime now = LocalDateTime.now();
        long offset = accountRepository.count();

        for (int i = 1; i <= amount; i++) {
            boolean isFemale = random.nextBoolean();

            String firstName = isFemale
                    ? FIRST_NAMES[15 + random.nextInt(10)]
                    : FIRST_NAMES[random.nextInt(15)];

            String[] lastNames = isFemale ? LAST_NAMES_FEMALE : LAST_NAMES_MALE;
            String lastName = lastNames[random.nextInt(lastNames.length)];

            String secondName = null;
            if (random.nextInt(4) != 0) {
                String[] secondNames = isFemale ? SECOND_NAMES_FEMALE : SECOND_NAMES_MALE;
                secondName = secondNames[random.nextInt(secondNames.length)];
            }

            int paidCase = random.nextInt(10);
            LocalDateTime paidUntil;
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
            account.setCardNumber(String.format("GIM-%05d", offset + i));
            account.setIsBlocked(random.nextInt(10) < 2);
            account.setPaidUntil(paidUntil);
            account.setCreated(created);
            account.setUpdated(updated);
            account.setDemo(true);

            accounts.add(account);
        }

        List<AccountEntity> saved = accountRepository.saveAll(accounts);
        log.info("Demo accounts generated: {}", saved.size());

        List<PaymentEntity> payments = new ArrayList<>();
        for (AccountEntity account : saved) {
            int paymentCount = account.getPaidUntil() != null
                    ? 1 + random.nextInt(4)
                    : random.nextInt(2);

            LocalDateTime paymentDate = account.getCreated();
            for (int j = 0; j < paymentCount; j++) {
                int periodIndex = random.nextInt(PERIODS.length);

                PaymentEntity payment = new PaymentEntity();
                payment.setAccount(account);
                payment.setPeriod(PERIODS[periodIndex]);
                payment.setDescription(buildPeriodDescription(PERIODS[periodIndex]));
                payment.setCreated(paymentDate);
                payment.setUpdated(paymentDate);

                payments.add(payment);
                paymentDate = paymentDate.plusDays(random.nextInt(90) + 30);
            }
        }

        paymentRepository.saveAll(payments);
        log.info("Demo payments generated: {}", payments.size());

        return saved.size();
    }

    @Transactional
    public int deleteAllDemo() {
        List<AccountEntity> demoAccounts = accountRepository.findAllByDemoTrue();
        if (demoAccounts.isEmpty()) return 0;

        paymentRepository.deleteAllByAccountIn(demoAccounts);
        accountRepository.deleteAllByDemoTrue();

        log.info("Deleted {} demo accounts and their payments", demoAccounts.size());
        return demoAccounts.size();
    }
}
