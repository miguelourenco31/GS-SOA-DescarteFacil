-- Dados de EXEMPLO para demonstracao (substituir por dados abertos da Prefeitura / 156 em producao)

INSERT INTO tipo_residuo (nome, descricao) VALUES
    ('Sofá', 'Móveis grandes: sofás, colchões e estofados'),
    ('Entulho', 'Resíduos de pequenas obras e reformas'),
    ('Geladeira velha', 'Eletrodomésticos de grande porte (linha branca)');

INSERT INTO ecoponto (nome, endereco, bairro, latitude, longitude, ativo, horario_funcionamento) VALUES
    ('Ecoponto Pinheiros (exemplo)', 'Rua Exemplo, 100', 'Pinheiros', -23.5670, -46.7020, TRUE, 'Seg a Sáb, 6h às 18h'),
    ('Ecoponto Vila Mariana (exemplo)', 'Rua Exemplo, 200', 'Vila Mariana', -23.5890, -46.6350, TRUE, 'Seg a Sáb, 6h às 18h'),
    ('Ecoponto Mooca (exemplo)', 'Rua Exemplo, 300', 'Mooca', -23.5590, -46.5990, TRUE, 'Seg a Sáb, 6h às 18h'),
    ('Ecoponto Santana (exemplo)', 'Rua Exemplo, 400', 'Santana', -23.5020, -46.6290, TRUE, 'Seg a Sáb, 6h às 18h');

INSERT INTO cacamba (ecoponto_id, tipo_residuo_id, identificador, capacidade_m3) VALUES
    ((SELECT id FROM ecoponto WHERE nome = 'Ecoponto Pinheiros (exemplo)'),     (SELECT id FROM tipo_residuo WHERE nome = 'Sofá'),            'CAC-PIN-01', 30),
    ((SELECT id FROM ecoponto WHERE nome = 'Ecoponto Pinheiros (exemplo)'),     (SELECT id FROM tipo_residuo WHERE nome = 'Entulho'),         'CAC-PIN-02', 30),
    ((SELECT id FROM ecoponto WHERE nome = 'Ecoponto Vila Mariana (exemplo)'),  (SELECT id FROM tipo_residuo WHERE nome = 'Sofá'),            'CAC-VMA-01', 30),
    ((SELECT id FROM ecoponto WHERE nome = 'Ecoponto Vila Mariana (exemplo)'),  (SELECT id FROM tipo_residuo WHERE nome = 'Geladeira velha'), 'CAC-VMA-02', 20),
    ((SELECT id FROM ecoponto WHERE nome = 'Ecoponto Mooca (exemplo)'),         (SELECT id FROM tipo_residuo WHERE nome = 'Entulho'),         'CAC-MOO-01', 30),
    ((SELECT id FROM ecoponto WHERE nome = 'Ecoponto Mooca (exemplo)'),         (SELECT id FROM tipo_residuo WHERE nome = 'Geladeira velha'), 'CAC-MOO-02', 20),
    ((SELECT id FROM ecoponto WHERE nome = 'Ecoponto Santana (exemplo)'),       (SELECT id FROM tipo_residuo WHERE nome = 'Sofá'),            'CAC-SAN-01', 30),
    ((SELECT id FROM ecoponto WHERE nome = 'Ecoponto Santana (exemplo)'),       (SELECT id FROM tipo_residuo WHERE nome = 'Entulho'),         'CAC-SAN-02', 30),
    ((SELECT id FROM ecoponto WHERE nome = 'Ecoponto Santana (exemplo)'),       (SELECT id FROM tipo_residuo WHERE nome = 'Geladeira velha'), 'CAC-SAN-03', 20);

INSERT INTO leitura_cacamba (cacamba_id, nivel_percentual, registrada_em) VALUES
    ((SELECT id FROM cacamba WHERE identificador = 'CAC-PIN-01'), 95, '2026-11-13 08:00:00'),
    ((SELECT id FROM cacamba WHERE identificador = 'CAC-PIN-02'), 40, '2026-11-13 08:00:00'),
    ((SELECT id FROM cacamba WHERE identificador = 'CAC-VMA-01'), 30, '2026-11-13 08:00:00'),
    ((SELECT id FROM cacamba WHERE identificador = 'CAC-VMA-02'), 92, '2026-11-13 08:00:00'),
    ((SELECT id FROM cacamba WHERE identificador = 'CAC-MOO-01'), 60, '2026-11-13 08:00:00'),
    ((SELECT id FROM cacamba WHERE identificador = 'CAC-SAN-01'), 20, '2026-11-13 08:00:00'),
    ((SELECT id FROM cacamba WHERE identificador = 'CAC-SAN-02'), 75, '2026-11-13 08:00:00');

INSERT INTO rota_cata_bagulho (bairro, logradouro, dia_semana, hora_inicio, hora_fim) VALUES
    ('Pinheiros', 'Rua dos Pinheiros', 'MONDAY', '08:00:00', '12:00:00'),
    ('Mooca', 'Rua da Mooca', 'WEDNESDAY', '08:00:00', '12:00:00'),
    ('Mooca', 'Rua Taquari', 'FRIDAY', '13:00:00', '17:00:00'),
    ('Vila Mariana', 'Rua Domingos de Morais', 'TUESDAY', '08:00:00', '12:00:00'),
    ('Santana', 'Avenida Cruzeiro do Sul', 'THURSDAY', '13:00:00', '17:00:00');

INSERT INTO ocorrencia_descarte (protocolo, bairro, logradouro, latitude, longitude, tipo_residuo_id, status, data_abertura) VALUES
    ('156-2026-0001', 'Mooca', 'Rua Taquari', -23.5600, -46.6000, (SELECT id FROM tipo_residuo WHERE nome = 'Entulho'), 'ABERTA', '2026-10-02 09:30:00'),
    ('156-2026-0002', 'Mooca', 'Rua da Mooca', -23.5580, -46.5980, (SELECT id FROM tipo_residuo WHERE nome = 'Sofá'), 'RESOLVIDA', '2026-10-09 14:10:00'),
    ('156-2026-0003', 'Mooca', 'Rua Taquari', -23.5605, -46.6005, (SELECT id FROM tipo_residuo WHERE nome = 'Entulho'), 'EM_ATENDIMENTO', '2026-10-21 11:45:00'),
    ('156-2026-0004', 'Santana', 'Avenida Cruzeiro do Sul', -23.5010, -46.6280, (SELECT id FROM tipo_residuo WHERE nome = 'Geladeira velha'), 'ABERTA', '2026-10-15 16:20:00'),
    ('156-2026-0005', 'Santana', 'Rua Voluntários da Pátria', -23.5030, -46.6300, (SELECT id FROM tipo_residuo WHERE nome = 'Sofá'), 'ABERTA', '2026-11-03 08:05:00'),
    ('156-2026-0006', 'Pinheiros', 'Rua dos Pinheiros', -23.5665, -46.7015, (SELECT id FROM tipo_residuo WHERE nome = 'Entulho'), 'RESOLVIDA', '2026-09-28 10:00:00');
