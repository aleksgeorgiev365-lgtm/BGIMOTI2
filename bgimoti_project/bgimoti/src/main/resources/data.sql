-- ══════════════════════════════════════════════════
--  БГИмоти — Initial Seed Data
--  Паролите са BCrypt хеширани (plaintext: "password123")
-- ══════════════════════════════════════════════════

USE bgimoti;

-- Потребители (паролата е: password123)
INSERT IGNORE INTO users (full_name, email, password, role) VALUES
('Администратор', 'admin@bgimoti.bg',
 '$2a$10$N.zmdr9zkooEtAhzY2WNDO9IDpNlGfRlxRrMST7c6TPlOxoWXWzuC', 'ADMIN'),
('Мария Иванова', 'maria@bgimoti.bg',
 '$2a$10$N.zmdr9zkooEtAhzY2WNDO9IDpNlGfRlxRrMST7c6TPlOxoWXWzuC', 'BROKER'),
('Петър Стоянов', 'petar@bgimoti.bg',
 '$2a$10$N.zmdr9zkooEtAhzY2WNDO9IDpNlGfRlxRrMST7c6TPlOxoWXWzuC', 'BROKER'),
('Иван Клиент', 'ivan@example.com',
 '$2a$10$N.zmdr9zkooEtAhzY2WNDO9IDpNlGfRlxRrMST7c6TPlOxoWXWzuC', 'CLIENT');

-- Имоти
INSERT IGNORE INTO properties (title, type, transaction, price, area, rooms, floor, total_floors, city, district, description, features, badge, broker_id) VALUES
('Просторен тристаен апартамент, кв. Лозенец', 'APARTMENT', 'SALE', 185000, 95, 3, 5, 8, 'София', 'Лозенец',
 'Луксозен апартамент в сърцето на Лозенец. Южно изложение, изглед към Витоша.',
 '["Гараж","Асансьор","Климатик","Обзаведен"]', '', 2),
('Самостоятелна къща с двор, Бояна', 'HOUSE', 'SALE', 380000, 280, 5, NULL, 2, 'София', 'Бояна',
 'Луксозна самостоятелна къща в квартал Бояна. Три нива, басейн, голям двор.',
 '["Гараж","Градина","Басейн","Климатик"]', 'new', 3),
('Двустаен апартамент, Младост 1', 'APARTMENT', 'SALE', 98000, 58, 2, 3, 7, 'София', 'Младост',
 'Светъл двустаен в кв. Младост 1. Близо до метростанция.',
 '["Асансьор","Интернет","Паркомясто"]', '', 2);
