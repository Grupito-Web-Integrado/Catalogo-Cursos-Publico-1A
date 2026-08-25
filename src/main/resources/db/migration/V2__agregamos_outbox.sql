CREATE TABLE outbox_events (
                               id              UUID            NOT NULL DEFAULT gen_random_uuid(),
                               aggregatetype   VARCHAR(255)    NOT NULL,
                               aggregateid     VARCHAR(255)    NOT NULL,
                               type            VARCHAR(255)    NOT NULL,
                               payload         JSONB           NOT NULL,
                               tracingspancontext VARCHAR(256),
                               timestamp       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

                               CONSTRAINT pk_outbox_events PRIMARY KEY (id)
);


