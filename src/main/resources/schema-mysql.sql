CREATE TABLE IF NOT EXISTS transaction_data
(
    id                            VARCHAR(256) NOT NULL PRIMARY KEY,
    type                          VARCHAR(256),
    state                         VARCHAR(256),
    request_id                    VARCHAR(256),
    reason_code                   VARCHAR(256),
    created_at                    TIMESTAMP,
    updated_at                    VARCHAR(256),
    completed_at                  VARCHAR(256),
    scheduled_for                 VARCHAR(256),
    related_transaction_id        VARCHAR(256),
    reference                     VARCHAR(256),
    leg_id                        VARCHAR(256),
    leg_amount                    DECIMAL(18,8),
    leg_currency                  VARCHAR(3),
    leg_bill_amount               DECIMAL(18,8),
    leg_bill_currency             VARCHAR(3),
    leg_account_id                VARCHAR(256),
    leg_counterparty_id           VARCHAR(256),
    leg_counterparty_account_id   VARCHAR(256),
    leg_counterparty_account_type VARCHAR(256),
    leg_description               VARCHAR(256),
    leg_balance                   DECIMAL(18,8),
    leg_fee                       DECIMAL(18,8),
    leg_id2                       VARCHAR(256),
    leg_amount2                   DECIMAL(18, 8),
    leg_currency2                 VARCHAR(3),
    leg_account_id2               VARCHAR(256),
    leg_description2              VARCHAR(256),
    leg_balance2                  DECIMAL(18, 8),
    card_number                   VARCHAR(256),
    card_first_name               VARCHAR(256),
    card_last_name                VARCHAR(256),
    card_phone                    VARCHAR(256),
    merchant_name                 VARCHAR(256),
    merchant_city                 VARCHAR(256),
    merchant_category_code        VARCHAR(256),
    merchant_country              VARCHAR(3)
);

CREATE TABLE IF NOT EXISTS revolut_auth_info
(
    refresh_token VARCHAR(256) NOT NULL PRIMARY KEY,
    client_id     VARCHAR(256),
    jwt           VARCHAR(1024)
);