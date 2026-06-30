package com.billing.platform.controller;

import com.billing.platform.dto.RecordPaymentRequest;
import com.billing.platform.entity.Payment;
import com.billing.platform.repository.PaymentRepository;
import com.billing.platform.service.billing.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/unit-bills/{unitBillId}/payments")
    public ResponseEntity<Payment> recordPayment(@PathVariable Long unitBillId, @RequestBody RecordPaymentRequest request) {
        Payment payment = paymentService.recordPayment(unitBillId, request.getAmount(), request.getMethod());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/unit-bills/{unitBillId}/payments")
    public ResponseEntity<List<Payment>> getPaymentsForBill(@PathVariable Long unitBillId) {
        return ResponseEntity.ok(paymentRepository.findByUnitBillId(unitBillId));
    }
}