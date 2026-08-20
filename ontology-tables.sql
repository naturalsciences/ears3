DROP TABLE IF EXISTS ontology_specific_event_definition;
DROP TABLE IF EXISTS ontology_generic_event_definition;
DROP TABLE IF EXISTS ontology_event_triggers;
DROP TABLE IF EXISTS ontology_event_properties;
DROP TABLE IF EXISTS ontology_event_definition;
DROP TABLE IF EXISTS ontology_tool_toolcategory;
DROP TABLE IF EXISTS ontology_tool_hosts;
DROP TABLE IF EXISTS ontology_tool_category;
DROP TABLE IF EXISTS ontology_tool;
DROP TABLE IF EXISTS ontology_process_action;
DROP TABLE IF EXISTS ontology_process;
DROP TABLE IF EXISTS ontology_action;
DROP TABLE IF EXISTS ontology_property;


CREATE TABLE ontology_tool_category
(
    id                    BIGSERIAL PRIMARY KEY,
    identifier            VARCHAR(200) NOT NULL UNIQUE,
    pref_label            VARCHAR(255) NOT NULL,
    alt_label             VARCHAR(255),
    definition            VARCHAR(2000),
    creator               VARCHAR(255),
    status                VARCHAR(50),
    version_info          VARCHAR(50),
    creation_date         TIMESTAMP WITH TIME ZONE,
    modif_date            TIMESTAMP WITH TIME ZONE,
    substitute_identifier VARCHAR(200)
);

CREATE TABLE ontology_tool
(
    id                    BIGSERIAL PRIMARY KEY,
    identifier            VARCHAR(200) NOT NULL UNIQUE,
    pref_label            VARCHAR(255) NOT NULL,
    alt_label             VARCHAR(255),
    definition            VARCHAR(2000),
    creator               VARCHAR(255),
    status                VARCHAR(50),
    version_info          VARCHAR(50),
    creation_date         TIMESTAMP WITH TIME ZONE,
    modif_date            TIMESTAMP WITH TIME ZONE,
    substitute_identifier VARCHAR(200),
    tool_identifier       VARCHAR(255),
    serial_number         VARCHAR(255)
);

CREATE TABLE ontology_tool_toolcategory
(
    tool_id         BIGINT NOT NULL REFERENCES ontology_tool (id) ON DELETE CASCADE,
    toolcategory_id BIGINT NOT NULL REFERENCES ontology_tool_category (id) ON DELETE CASCADE,
    PRIMARY KEY (tool_id, toolcategory_id)
);

CREATE TABLE ontology_tool_hosts
(
    host_tool_id   BIGINT NOT NULL REFERENCES ontology_tool (id) ON DELETE CASCADE,
    hosted_tool_id BIGINT NOT NULL REFERENCES ontology_tool (id) ON DELETE CASCADE,
    PRIMARY KEY (host_tool_id, hosted_tool_id)
);

CREATE TABLE ontology_process
(
    id                    BIGSERIAL PRIMARY KEY,
    identifier            VARCHAR(200) NOT NULL UNIQUE,
    pref_label            VARCHAR(255) NOT NULL,
    alt_label             VARCHAR(255),
    definition            VARCHAR(2000),
    creator               VARCHAR(255),
    status                VARCHAR(50),
    version_info          VARCHAR(50),
    creation_date         TIMESTAMP WITH TIME ZONE,
    modif_date            TIMESTAMP WITH TIME ZONE,
    substitute_identifier VARCHAR(200)
);

CREATE TABLE ontology_action
(
    id                    BIGSERIAL PRIMARY KEY,
    identifier            VARCHAR(200) NOT NULL UNIQUE,
    pref_label            VARCHAR(255) NOT NULL,
    alt_label             VARCHAR(255),
    definition            VARCHAR(2000),
    creator               VARCHAR(255),
    status                VARCHAR(50),
    version_info          VARCHAR(50),
    creation_date         TIMESTAMP WITH TIME ZONE,
    modif_date            TIMESTAMP WITH TIME ZONE,
    substitute_identifier VARCHAR(200)
);

CREATE TABLE ontology_property
(
    id                    BIGSERIAL PRIMARY KEY,
    identifier            VARCHAR(200) NOT NULL UNIQUE,
    pref_label            VARCHAR(255) NOT NULL,
    alt_label             VARCHAR(255),
    definition            VARCHAR(2000),
    creator               VARCHAR(255),
    status                VARCHAR(50),
    version_info          VARCHAR(50),
    creation_date         TIMESTAMP WITH TIME ZONE,
    modif_date            TIMESTAMP WITH TIME ZONE,
    substitute_identifier VARCHAR(200),
    mandatory             BOOLEAN      NOT NULL DEFAULT FALSE,
    multiple              BOOLEAN      NOT NULL DEFAULT FALSE,
    value_class           VARCHAR(50)
);

CREATE TABLE ontology_process_action
(
    id         BIGSERIAL PRIMARY KEY,
    process_id BIGINT NOT NULL REFERENCES ontology_process (id),
    action_id  BIGINT NOT NULL REFERENCES ontology_action (id),
    UNIQUE (process_id, action_id)
);

CREATE TABLE ontology_event_definition
(
    id                    BIGSERIAL PRIMARY KEY,
    identifier            VARCHAR(200) NOT NULL UNIQUE,
    pref_label            VARCHAR(255),
    alt_label             VARCHAR(255),
    definition            VARCHAR(2000),
    creator               VARCHAR(255),
    status                VARCHAR(50),
    version_info          VARCHAR(50),
    creation_date         TIMESTAMP WITH TIME ZONE,
    modif_date            TIMESTAMP WITH TIME ZONE,
    substitute_identifier VARCHAR(200),
    process_id            BIGINT       NOT NULL REFERENCES ontology_process (id),
    action_id             BIGINT       NOT NULL REFERENCES ontology_action (id),
    data_provider         BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE ontology_event_triggers
(
    event_definition_id           BIGINT NOT NULL REFERENCES ontology_event_definition (id) ON DELETE CASCADE,
    triggered_event_definition_id BIGINT NOT NULL REFERENCES ontology_event_definition (id) ON DELETE CASCADE,
    PRIMARY KEY (event_definition_id, triggered_event_definition_id)
);

CREATE TABLE ontology_event_properties
(
    event_definition_id BIGINT NOT NULL REFERENCES ontology_event_definition (id) ON DELETE CASCADE,
    property_id         BIGINT NOT NULL REFERENCES ontology_property (id) ON DELETE CASCADE,
    PRIMARY KEY (event_definition_id, property_id)
);

CREATE TABLE ontology_generic_event_definition
(
    id               BIGINT PRIMARY KEY REFERENCES ontology_event_definition (id) ON DELETE CASCADE,
    tool_category_id BIGINT NOT NULL REFERENCES ontology_tool_category (id)
);

CREATE TABLE ontology_specific_event_definition
(
    id          BIGINT PRIMARY KEY REFERENCES ontology_event_definition (id) ON DELETE CASCADE,
    tool_id     BIGINT NOT NULL REFERENCES ontology_tool (id),
    realizes_id BIGINT REFERENCES ontology_generic_event_definition (id)
);

CREATE INDEX idx_ontology_event_definition_process ON ontology_event_definition (process_id);
CREATE INDEX idx_ontology_event_definition_action ON ontology_event_definition (action_id);
CREATE INDEX idx_ontology_specific_event_definition_tool ON ontology_specific_event_definition (tool_id);
CREATE INDEX idx_ontology_generic_event_definition_toolcategory ON ontology_generic_event_definition (tool_category_id);
