package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.TransactionDto;
import com.wallet_api_clane.models.Transaction;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.repositories.TransactionRepository;
import com.wallet_api_clane.services.TransactionServices;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.wallet_api_clane.enums.TransactionStatus.APPROVED;
import static com.wallet_api_clane.enums.TransactionType.TRANSFER;
import static com.wallet_api_clane.enums.TransactionType.WITHDRAWAL;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionServices {
    private final TransactionRepository transactionRepository;
    private final ModelMapper mapper;

    @Override
    public void saveNewTransaction(TransactionDto transactionDto) {
        Transaction transaction = mapper.map(transactionDto, Transaction.class);
        transactionRepository.save(transaction);
    }

    @Override
    public double checkTransactionsForTheDay(User user) {
        LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
        List<Transaction> transactionList =
                transactionRepository.findTransactionsByUserAndCreatedAtIsAfterAndStatus(user, localDateTime, APPROVED);

        return transactionList.stream()
                .filter(transaction -> transaction.getType().equals(TRANSFER)
                        || transaction.getType().equals(WITHDRAWAL))
                .mapToDouble(Transaction::getAmount).sum();
    }
}
