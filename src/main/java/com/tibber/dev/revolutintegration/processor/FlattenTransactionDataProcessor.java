package com.tibber.dev.revolutintegration.processor;

import com.tibber.dev.revolutintegration.model.FlattenTransactionData;
import com.tibber.dev.revolutintegration.model.TransactionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * A processor class to flatten transaction data from Revolut API.
 *
 * @version 1.0
 * @auther Isami Mitani
 */
public class FlattenTransactionDataProcessor implements ItemProcessor<TransactionData, FlattenTransactionData> {

    private static final Logger log = LoggerFactory.getLogger(FlattenTransactionDataProcessor.class);

    /**
     * Converts and flatten transaction data from Revolut API.
     * Assumes that Legs array contains only one onject
     *
     * @param transactionData
     * @return {@code FlattenTransactionData}
     */
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
        if (transactionData.getLegs() != null && transactionData.getLegs().size() > 0) {
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
            if (transactionData.getType().equalsIgnoreCase("exchange") && transactionData.getLegs().size() > 1) {
                flattenTransactionData.setLegId2(transactionData.getLegs().get(1).getLegId());
                flattenTransactionData.setLegAccountId2(transactionData.getLegs().get(1).getAccountId());
                flattenTransactionData.setLegAmount2(transactionData.getLegs().get(1).getAmount());
                flattenTransactionData.setLegCurrency2(transactionData.getLegs().get(1).getCurrency());
                flattenTransactionData.setLegDescription2(transactionData.getLegs().get(1).getDescription());
                flattenTransactionData.setLegBalance2(transactionData.getLegs().get(1).getBalance());
            }
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

