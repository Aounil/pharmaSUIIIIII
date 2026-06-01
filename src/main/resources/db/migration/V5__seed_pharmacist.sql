-- V5: Seed a default pharmacy and pharmacist account for local development.
--
-- Pharmacist login:
--   Email: pharmacist@pharmacy.ma
--   Password: Pharmacist@1234
--   Pharmacy ID: 1

INSERT INTO pharmacies (id, name, address, latitude, longitude, phone, email, active)
VALUES (
    1,
    'Pharmacie Centrale',
    'Avenue Mohammed V, Casablanca',
    33.5731,
    -7.5898,
    '+212522000000',
    'centrale@pharmacy.ma',
    1
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    address = VALUES(address),
    latitude = VALUES(latitude),
    longitude = VALUES(longitude),
    phone = VALUES(phone),
    email = VALUES(email),
    active = VALUES(active);

INSERT INTO users (email, password, role)
VALUES (
    'pharmacist@pharmacy.ma',
    '$2a$12$oghImSsnLsGNarHx.3k33Oq0riQMsG4VJ7AQbvk1wEtbbdSW2j4qe',
    'ROLE_PHARMACIST'
)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    role = VALUES(role);
