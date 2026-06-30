package com.billing.platform.service.billing;

import com.billing.platform.entity.Payment;
import com.billing.platform.entity.UnitBill;
import com.billing.platform.repository.PaymentRepository;
import com.billing.platform.repository.UnitBillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UnitBillRepository unitBillRepository;

    public PaymentService(PaymentRepository paymentRepository, UnitBillRepository unitBillRepository) {
        this.paymentRepository = paymentRepository;
        this.unitBillRepository = unitBillRepository;
    }

    @Transactional
    public Payment recordPayment(Long unitBillId, BigDecimal amount, String method) {

        UnitBill bill = unitBillRepository.findById(unitBillId)
                .orElseThrow(() -> new IllegalArgumentException("Unit bill not found: " + unitBillId));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        BigDecimal newAmountPaid = bill.getAmountPaid().add(amount);

        if (newAmountPaid.compareTo(bill.getAmountDue()) > 0) {
            throw new IllegalStateException(
                    "Payment of " + amount + " would exceed amount due. "
                            + "Already paid: " + bill.getAmountPaid()
                            + ", Amount due: " + bill.getAmountDue()
                            + ", Remaining balance: " + bill.getAmountDue().subtract(bill.getAmountPaid())
            );
        }

        Payment payment = new Payment();
        payment.setUnitBillId(unitBillId);
        payment.setAmount(amount);
        payment.setMethod(method);
        Payment savedPayment = paymentRepository.save(payment);

        bill.setAmountPaid(newAmountPaid);
        if (newAmountPaid.compareTo(bill.getAmountDue()) == 0) {
            bill.setStatus("PAID");
        } else if (newAmountPaid.compareTo(BigDecimal.ZERO) > 0) {
            bill.setStatus("PARTIAL");
        }
        unitBillRepository.save(bill);

        return savedPayment;
    }
}