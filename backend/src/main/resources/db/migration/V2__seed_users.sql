-- V2: 用户与班级种子（密码均为 123456）
INSERT INTO `class` (id, name, grade, teacher_id) VALUES
  (1, '软件工程 2023 级 2 班', '2023', 1),
  (2, '软件工程 2023 级 1 班', '2023', 1);

INSERT INTO `user` (id, username, password_hash, real_name, role, student_no, class_id, created_at, last_login_at) VALUES
  (1,  'teacher', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '陈老师', 'TEACHER', NULL, NULL, NOW(), NOW()),
  (2,  'student', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '张明',   'STUDENT', '2023211024', 1, NOW(), NOW()),
  (3,  's02', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '李思远', 'STUDENT', '2023211025', 1, NOW(), NOW()),
  (4,  's03', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '王雨欣', 'STUDENT', '2023211026', 1, NOW(), NOW()),
  (5,  's04', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '陈子豪', 'STUDENT', '2023211027', 1, NOW(), NOW()),
  (6,  's05', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '刘佳怡', 'STUDENT', '2023211028', 1, NOW(), NOW()),
  (7,  's06', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '赵启明', 'STUDENT', '2023211029', 1, NOW(), DATE_SUB(NOW(), INTERVAL 5 DAY)),
  (8,  's07', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '孙悦',   'STUDENT', '2023211030', 1, NOW(), NOW()),
  (9,  's08', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '周凯',   'STUDENT', '2023211031', 1, NOW(), NOW()),
  (10, 's09', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '吴静',   'STUDENT', '2023211032', 1, NOW(), NOW()),
  (11, 's10', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '郑浩',   'STUDENT', '2023211033', 1, NOW(), DATE_SUB(NOW(), INTERVAL 4 DAY)),
  (12, 's11', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '冯晓',   'STUDENT', '2023211034', 1, NOW(), NOW()),
  (13, 's12', '$2a$10$lRIP406sPNtpi3ZeEntMm.Eu0fp9ZCIESRYdsL8Hlay0O0CMjaMDu', '蒋楠',   'STUDENT', '2023211035', 1, NOW(), NOW());
