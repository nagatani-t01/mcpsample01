INSERT INTO vets VALUES (1, '健', '田中');
INSERT INTO vets VALUES (2, '美和', '鈴木');
INSERT INTO vets VALUES (3, '誠', '高橋');
INSERT INTO vets VALUES (4, '裕子', '伊藤');
INSERT INTO vets VALUES (5, '大輔', '渡辺');
INSERT INTO vets VALUES (6, '香織', '山本');

INSERT INTO specialties VALUES (1, '放射線科');
INSERT INTO specialties VALUES (2, '外科');
INSERT INTO specialties VALUES (3, '歯科');

INSERT INTO vet_specialties VALUES (2, 1);
INSERT INTO vet_specialties VALUES (3, 2);
INSERT INTO vet_specialties VALUES (3, 3);
INSERT INTO vet_specialties VALUES (4, 2);
INSERT INTO vet_specialties VALUES (5, 1);

INSERT INTO types VALUES (1, '猫');
INSERT INTO types VALUES (2, '犬');
INSERT INTO types VALUES (3, 'トカゲ');
INSERT INTO types VALUES (4, 'ヘビ');
INSERT INTO types VALUES (5, '鳥');
INSERT INTO types VALUES (6, 'ハムスター');

INSERT INTO owners VALUES (1, '太郎', '山田', '千代田区丸の内1-1-1', '東京', '03011112222');
INSERT INTO owners VALUES (2, '花子', '鈴木', '北区梅田2-2-2', '大阪', '06011112222');
INSERT INTO owners VALUES (3, '一郎', '佐藤', '中区本町3-3-3', '横浜', '04511112222');
INSERT INTO owners VALUES (4, '次郎', '鈴木', '中区栄4-4-4', '名古屋', '05211112222');
INSERT INTO owners VALUES (5, '美咲', '高橋', '博多区博多駅前5-5-5', '福岡', '09211112222');
INSERT INTO owners VALUES (6, '健太', '伊藤', '中央区大通西6-6-6', '札幌', '01111112222');
INSERT INTO owners VALUES (7, 'あゆみ', '渡辺', '青葉区中央7-7-7', '仙台', '02211112222');
INSERT INTO owners VALUES (8, '直樹', '中村', '中区紙屋町8-8-8', '広島', '08211112222');
INSERT INTO owners VALUES (9, '恵', '小林', '下京区四条通9-9-9', '京都', '07511112222');
INSERT INTO owners VALUES (10, '翔太', '加藤', 'おもろまち10-10-10', '那覇', '09811112222');

INSERT INTO pets VALUES (1, 'レオ', '2010-09-07', 1, 1);
INSERT INTO pets VALUES (2, 'バジル', '2012-08-06', 6, 2);
INSERT INTO pets VALUES (3, 'ロージー', '2011-04-17', 2, 3);
INSERT INTO pets VALUES (4, 'ジュエル', '2010-03-07', 2, 3);
INSERT INTO pets VALUES (5, 'イギー', '2010-11-30', 3, 4);
INSERT INTO pets VALUES (6, 'ジョージ', '2010-01-20', 4, 5);
INSERT INTO pets VALUES (7, 'サマンサ', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (8, 'マックス', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (9, 'ラッキー', '2011-08-06', 5, 7);
INSERT INTO pets VALUES (10, 'マリガン', '2007-02-24', 2, 8);
INSERT INTO pets VALUES (11, 'フレディ', '2010-03-09', 5, 9);
INSERT INTO pets VALUES (12, 'ラッキー', '2010-06-24', 2, 10);
INSERT INTO pets VALUES (13, 'スライ', '2012-06-08', 1, 10);

INSERT INTO visits VALUES (1, 7, '2013-01-01', '狂犬病予防接種');
INSERT INTO visits VALUES (2, 8, '2013-01-02', '狂犬病予防接種');
INSERT INTO visits VALUES (3, 8, '2013-01-03', '去勢手術');
INSERT INTO visits VALUES (4, 7, '2013-01-04', '避妊手術');

INSERT INTO users(username,password,enabled) VALUES ('admin','{noop}admin', true);

INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_OWNER_ADMIN');
INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_VET_ADMIN');
INSERT INTO roles (username, role) VALUES ('admin', 'ROLE_ADMIN');
