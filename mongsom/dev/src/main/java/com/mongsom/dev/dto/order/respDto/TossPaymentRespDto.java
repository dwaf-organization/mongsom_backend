package com.mongsom.dev.dto.order.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentRespDto {
    
    private String mId;
    private String lastTransactionKey;
    private String paymentKey;
    private String orderId;  // mongsom_100023 형식
    private String orderName;
    private Integer taxExemptionAmount;
    private String status;  // DONE, CANCELED 등
    private String requestedAt;
    private String approvedAt;
    private Boolean useEscrow;
    private Boolean cultureExpense;
    
    private CardInfo card;
    
    private String type;
    private String country;
    private Boolean isPartialCancelable;
    
    private ReceiptInfo receipt;
    private CheckoutInfo checkout;
    
    private String currency;
    private Integer totalAmount;
    private Integer balanceAmount;
    private Integer suppliedAmount;
    private Integer vat;
    private Integer taxFreeAmount;
    private String method;  // 카드, 계좌이체, 가상계좌 등
    private String version;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardInfo {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private Integer installmentPlanMonths;
        private Boolean isInterestFree;
        private String approveNo;
        private Boolean useCardPoint;
        private String cardType;  // 체크, 신용
        private String ownerType;  // 개인, 법인
        private String acquireStatus;
        private Integer amount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptInfo {
        private String url;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckoutInfo {
        private String url;
    }
}