CREATE EXTENSION IF NOT EXISTS pgcrypto;

create table IF NOT EXISTS user_details(

      id                UUID       PRIMARY KEY DEFAULT  gen_random_uuid(),
      firstname         VARCHAR(255)         NOT NULL,
      lastname          VARCHAR(255)         NOT NULL,
      fullname  TEXT    GENERATED ALWAYS AS (firstname || ' ' || lastname ) STORED,
      email             VARCHAR(255)         NOT NULL UNIQUE,
      phone             VARCHAR(255)         NOT NULL UNIQUE,
      role              VARCHAR(255)         NOT NULL,

      updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
      created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
      created_by  VARCHAR(255)               NOT NULL DEFAULT current_user,
      updated_by  VARCHAR(255)               NOT NULL
);


create table IF NOT EXISTS address(
          id                UUID       PRIMARY KEY DEFAULT  gen_random_uuid(),
          pincode              VARCHAR(255)         NOT NULL,
          village              VARCHAR(255)         NOT NULL,
          district             VARCHAR(255)         NOT NULL,
          state                VARCHAR(255)         NOT NULL,
          country              VARCHAR(255)         NOT NULL,
          user_id              UUID                 NOT NULL,
          updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
          created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
          created_by  VARCHAR(255)               NOT NULL DEFAULT current_user,
          updated_ay  VARCHAR(255)               NOT NULL,

          CONSTRAINT fk_user_details FOREIGN KEY (user_id)
          REFERENCES user_details (id)
);