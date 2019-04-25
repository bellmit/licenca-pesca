CREATE TABLE carteira_pesca.retorno_convenio
(
  id SERIAL NOT NULL,
  id_arquivo INTEGER NOT NULL,
  dt_geracao_arquivo DATE,
  dt_processamento TIMESTAMP,
  numero_arquivo INTEGER,
  qtd_registros INTEGER,
  valor_recebido DOUBLE PRECISION,
  CONSTRAINT pk_retorno_convenio PRIMARY KEY(id),
  CONSTRAINT fk_rc_arquivo FOREIGN KEY (id_arquivo)
    REFERENCES carteira_pesca.arquivo (id) MATCH SIMPLE
    ON UPDATE RESTRICT ON DELETE RESTRICT
) WITH (
  OIDS=FALSE
);

ALTER TABLE carteira_pesca.retorno_convenio OWNER TO postgres;
GRANT ALL ON TABLE carteira_pesca.retorno_convenio TO postgres;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE carteira_pesca.retorno_convenio TO carteira_pesca;
COMMENT ON TABLE carteira_pesca.retorno_convenio IS 'Entidade responsável por armazenar os dados do retorno do convenio.';
COMMENT ON COLUMN carteira_pesca.retorno_convenio.id IS 'Identificador único da entidade.';
COMMENT ON COLUMN carteira_pesca.retorno_convenio.id_arquivo IS 'Arquivo de retorno que foi gerado.';
COMMENT ON COLUMN carteira_pesca.retorno_convenio.dt_geracao_arquivo IS 'Data que o arquivo de retorno foi gerado pelo banco.';
COMMENT ON COLUMN carteira_pesca.retorno_convenio.dt_processamento IS 'Data de processamento do arquivo.';
COMMENT ON COLUMN carteira_pesca.retorno_convenio.numero_arquivo IS 'Número seqüencial do arquivo.';
COMMENT ON COLUMN carteira_pesca.retorno_convenio.qtd_registros IS 'Quantidade de registros (linhas) do arquivo.';
COMMENT ON COLUMN carteira_pesca.retorno_convenio.valor_recebido IS 'Valor total recebido dos registros do arquivo.';

INSERT INTO carteira_pesca.tipo_arquivo (id, codigo, descricao) VALUES (5, 'RETORNO_ARRECADACAO', 'Arquivo com os dados dos boletos que foram processador pelo banco');

ALTER TABLE carteira_pesca.pagamento_convenio ALTER COLUMN id_tipo_pagamento DROP NOT NULL;

ALTER TABLE carteira_pesca.pagamento_convenio DROP COLUMN id_retorno;

ALTER TABLE carteira_pesca.pagamento_convenio ADD COLUMN id_retorno INTEGER;

ALTER TABLE carteira_pesca.pagamento_convenio ADD CONSTRAINT fk_pc_retorno FOREIGN KEY (id_retorno)
  REFERENCES carteira_pesca.retorno_convenio (id) MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT;
