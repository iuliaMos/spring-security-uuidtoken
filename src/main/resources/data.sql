INSERT INTO USER(id, username, password, description) VALUES
(nextval('user_seq'), 'aa', 'aa', 'description aa'),
(nextval('user_seq'), 'bb', 'bb', 'description bb');

INSERT INTO USER_TOKEN(id, user_id, uuid, active) VALUES
(nextval('token_seq'), 1, 'aa-vv-ff-ee-77', true);