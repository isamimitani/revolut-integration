CREATE TABLE IF NOT EXISTS trading_parameters.transaction_data
(
    id                            VARCHAR(50) NOT NULL PRIMARY KEY,
    type                          VARCHAR(20),
    state                         VARCHAR(20),
    request_id                    VARCHAR(50),
    reason_code                   VARCHAR(50),
    created_at                    VARCHAR(50),
    updated_at                    VARCHAR(50),
    completed_at                  VARCHAR(50),
    scheduled_for                 VARCHAR(50),
    related_transaction_id        VARCHAR(50),
    reference                     VARCHAR(50),
    leg_id                        VARCHAR(50),
    leg_amount                    VARCHAR(50),
    leg_currency                  VARCHAR(50),
    leg_bill_amount               VARCHAR(50),
    leg_bill_currency             VARCHAR(50),
    leg_account_id                VARCHAR(50),
    leg_counterparty_id           VARCHAR(50),
    leg_counterparty_account_id   VARCHAR(50),
    leg_counterparty_account_type VARCHAR(50),
    leg_description               VARCHAR(50),
    leg_balance                   VARCHAR(50),
    leg_fee                       VARCHAR(50),
    card_number                   VARCHAR(50),
    card_first_name               VARCHAR(50),
    card_last_name                VARCHAR(50),
    card_phone                    VARCHAR(50),
    merchant_name                 VARCHAR(50),
    merchant_city                 VARCHAR(50),
    merchant_category_code        VARCHAR(50),
    merchant_country              VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS trading_parameters.revolut_auth_info
(
    refresh_token VARCHAR(100) NOT NULL PRIMARY KEY,
    client_id     VARCHAR(50),
    jwt           VARCHAR(600)
);
