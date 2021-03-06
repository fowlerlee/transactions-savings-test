package com.qapital.test;

import com.qapital.bankdata.transaction.StandardTransactionsService;
import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import com.qapital.savings.rule.SavingsRule;
import com.qapital.savings.rule.SavingsRulesService;
import com.qapital.savings.rule.StandardSavingsRulesService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ComponentScan(basePackages = {"com.qapital", "com.qapital.savings.rule"})
@SpringBootTest
public class StandardSavingsRulesServiceTest {


    private SavingsRulesService savingsRulesService;

    private TransactionsService transactionsService;

    @Before
    public void setUp() {
        transactionsService = new StandardTransactionsService();
        savingsRulesService = new StandardSavingsRulesService(transactionsService);
    }

    @Test
    public void given_latestTransactionsAreLoaded_then_applyGuiltyPleasureSavingsRules() {

        Long userId = 1001L;


        Transaction transaction = Transaction.builder()
                .id(1L)
                .userId(userId)
                .amount(5.34d)      // same as Math.abs() of the first transaction
                .description("Starbucks")
                .date(LocalDate.of(2021, 1, 1))
                .build();

        SavingsRule savingsRule = SavingsRule.builder()
                .id(transaction.getId())
                .userId(userId)
                .amount(transaction.getAmount())
                .savingsGoalIds(new ArrayList<Long>(Arrays.asList(1L, 2L, 3L)))
                .ruleType(SavingsRule.RuleType.GUILTYPLEASURE)
                .build();


        List<SavingsEvent> savingsEvent = savingsRulesService.executeRule(savingsRule);

        Assert.assertNotNull(savingsEvent);
        Assert.assertEquals(transaction.getAmount(), savingsEvent.get(0).getAmount());


    }


    @Test(expected = NullPointerException.class)
    public void given_NullTransactions_then_ExecuteMethodReturnsNull() {
        savingsRulesService = new StandardSavingsRulesService(null);
        savingsRulesService.executeRule(new SavingsRule());

    }


    @Test(expected = NullPointerException.class)
    public void given_NullSavingsRule_then_ExecuteMethodReturnsNull() {
        TransactionsService transactionsService = new StandardTransactionsService();
        savingsRulesService = new StandardSavingsRulesService(transactionsService);
        savingsRulesService.executeRule(null);

    }
}