package com.tibber.dev.revolutintegration.processor;

import com.tibber.dev.revolutintegration.model.FlattenTransactionData;
import com.tibber.dev.revolutintegration.model.TransactionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * A processor class to flatten transaction data from Revolut API.
 *
 * @auther Isami Mitani
 * @version 1.0
 */
public class FlattenTransactionDataProcessor implements ItemProcessor<TransactionData, FlattenTransactionData> {

    private static final Logger log = LoggerFactory.getLogger(FlattenTransactionDataProcessor.class);

    @Override
    public FlattenTransactionData process(final TransactionData transactionData) {
        final FlattenTransactionData flattenTransactionData = new FlattenTransactionData();
        flattenTransactionData.setId(transactionData.getId());
        flattenTransactionData.setType(transactionData.getType());
        flattenTransactionData.setRequestId(transactionData.getRequestId());
        flattenTransactionData.setState(transactionData.getState());
        flattenTransactionData.setReasonCode(transactionData.getReasonCode());
        flattenTransactionData.setCreatedAt(transactionData.getCreatedAt());
        flattenTransactionData.setUpdatedAt(transactionData.getUpdatedAt());
        flattenTransactionData.setCompletedAt(transactionData.getCompletedAt());
        flattenTransactionData.setScheduledFor(transactionData.getScheduledFor());
        flattenTransactionData.setRelatedTransactionId(transactionData.getRelatedTransactionId());
        flattenTransactionData.setReference(transactionData.getReference());
        if (transactionData.getLegs() != null && transactionData.getLegs().get(0) != null) {
            flattenTransactionData.setLegId(transactionData.getLegs().get(0).getLegId());
            flattenTransactionData.setLegAmount(transactionData.getLegs().get(0).getAmount());
            flattenTransactionData.setLegCurrency(transactionData.getLegs().get(0).getCurrency());
            flattenTransactionData.setLegBillAmount(transactionData.getLegs().get(0).getBillAmount());
            flattenTransactionData.setLegBillCurrency(transactionData.getLegs().get(0).getBillCurrency());
            flattenTransactionData.setLegAccountId(transactionData.getLegs().get(0).getAccountId());
            flattenTransactionData.setLegCounterpartyId((String) transactionData.getLegs().get(0).getCounterparty().get("id"));
            flattenTransactionData.setLegCounterpartyAccountId((String) transactionData.getLegs().get(0).getCounterparty().get("account_id"));
            flattenTransactionData.setLegCounterpartyAccountType((String) transactionData.getLegs().get(0).getCounterparty().get("account_type"));
            flattenTransactionData.setLegDescription(transactionData.getLegs().get(0).getDescription());
            flattenTransactionData.setLegBalance(transactionData.getLegs().get(0).getBalance());
            flattenTransactionData.setLegFee(transactionData.getLegs().get(0).getFee());
        }
        flattenTransactionData.setCardNumber((String) transactionData.getCard().get("card_number"));
        flattenTransactionData.setCardFirstName((String) transactionData.getCard().get("first_name"));
        flattenTransactionData.setCardLastName((String) transactionData.getCard().get("last_name"));
        flattenTransactionData.setCardPhone((String) transactionData.getCard().get("phone"));
        flattenTransactionData.setMerchantName((String) transactionData.getMerchant().get("name"));
        flattenTransactionData.setMerchantCity((String) transactionData.getMerchant().get("city"));
        flattenTransactionData.setMerchantCategoryCode((String) transactionData.getMerchant().get("category_code"));
        flattenTransactionData.setMerchantCountry((String) transactionData.getMerchant().get("country"));

        log.debug("Converting (" + transactionData + ") into (" + flattenTransactionData + ")");

        return flattenTransactionData;
    }
}

