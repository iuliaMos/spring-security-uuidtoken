INSERT INTO USER(id, username, password, description) VALUES
(nextval('user_seq'), 'aa', '$2a$10$noC1lzK.9BjhI.zO0ZDxxucRq3Zlh6oEkwnUUCPa1aiPeZD5QBS4a', 'description aa'),
(nextval('user_seq'), 'bb', 'bb', 'description bb');

INSERT INTO USER_TOKEN(id, user_id, uuid, active) VALUES
(nextval('token_seq'), 1, 'aa-vv-ff-ee-77', true),
(nextval('token_seq'), 2, 'bb-nn-cc-55', true);