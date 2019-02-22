CREATE TABLE carteira_pesca.retorno
(
  id SERIAL NOT NULL,
  id_arquivo INTEGER NOT NULL,
  dt_gravacao_banco DATE,
  dt_processamento TIMESTAMP,
  qtd_titulo_cobranca INTEGER,
  valor_titulo_cobranca DOUBLE PRECISION,
  qtd_confirmacao_entrada INTEGER,
  valor_confirmacao_entrada DOUBLE PRECISION,
  qtd_liquidacao INTEGER,
  valor_liquidacao DOUBLE PRECISION,
  qtd_baixados INTEGER,
  valor_baixados DOUBLE PRECISION,
  qtd_abatimento_cancelamento INTEGER,
  valor_abatimento_cancelamento DOUBLE PRECISION,
  qtd_vencimento_alterado INTEGER,
  valor_vencimento_alterado DOUBLE PRECISION,
  qtd_abatimento_concedido INTEGER,
  valor_abatimento_concedido DOUBLE PRECISION,
  qtd_confirmacao_instrucao_protesto INTEGER,
  valor_confirmacao_instrucao_protesto DOUBLE PRECISION,
  qtd_total_rateio_efetuado INTEGER,
  valor_total_rateio_efetuado DOUBLE PRECISION,
  numero_aviso_bancario INTEGER,
  CONSTRAINT pk_retorno PRIMARY KEY(id),
  CONSTRAINT fk_r_arquivo FOREIGN KEY (id_arquivo)
    REFERENCES carteira_pesca.arquivo (id) MATCH SIMPLE
    ON UPDATE RESTRICT ON DELETE RESTRICT
) WITH (
  OIDS=FALSE
);

ALTER TABLE carteira_pesca.retorno OWNER TO postgres;
GRANT ALL ON TABLE carteira_pesca.retorno TO postgres;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE carteira_pesca.retorno TO carteira_pesca;
COMMENT ON TABLE carteira_pesca.retorno IS 'Entidade responsável por armazenar os dados do retorno.';
COMMENT ON COLUMN carteira_pesca.retorno.id IS 'Identificador único da entidade.';
COMMENT ON COLUMN carteira_pesca.retorno.id_arquivo IS 'Arquivo de retorno que foi gerado.';
COMMENT ON COLUMN carteira_pesca.retorno.dt_gravacao_banco IS 'Data que o arquivo de retorno foi gerado pelo banco.';
COMMENT ON COLUMN carteira_pesca.retorno.dt_processamento IS 'Data de processamento do arquivo.';
COMMENT ON COLUMN carteira_pesca.retorno.qtd_titulo_cobranca IS 'Quantidade de títulos em cobraca contidos no retorno.';
COMMENT ON COLUMN carteira_pesca.retorno.valor_titulo_cobranca IS 'Valor dos títulos em cobraca contidos no retorno.';
COMMENT ON COLUMN carteira_pesca.retorno.qtd_confirmacao_entrada IS 'Quantidade de títulos em confirmação de entrada.';
COMMENT ON COLUMN carteira_pesca.retorno.valor_confirmacao_entrada IS 'Valor dos títulos em confirmação de entrada.';
COMMENT ON COLUMN carteira_pesca.retorno.qtd_liquidacao IS 'Quantidade de títulos em liquidação.';
COMMENT ON COLUMN carteira_pesca.retorno.valor_liquidacao IS 'Valor dos títulos em liquidação.';
COMMENT ON COLUMN carteira_pesca.retorno.qtd_baixados IS 'Quantidade de títulos baixados.';
COMMENT ON COLUMN carteira_pesca.retorno.valor_baixados IS 'Valor dos títulos baixados.';
COMMENT ON COLUMN carteira_pesca.retorno.qtd_abatimento_cancelamento IS 'Quantidade de títulos abatidos de cancelamento.';
COMMENT ON COLUMN carteira_pesca.retorno.valor_abatimento_cancelamento IS 'Valor dos títulos abatidos de cancelamento.';
COMMENT ON COLUMN carteira_pesca.retorno.qtd_vencimento_alterado IS 'Quantidade de títulos com vencimento alterado.';
COMMENT ON COLUMN carteira_pesca.retorno.valor_vencimento_alterado IS 'Valor dos títulos com vencimento alterado.';
COMMENT ON COLUMN carteira_pesca.retorno.qtd_abatimento_concedido IS 'Quantidade de títulos que tiveram abatimento concedido.';
COMMENT ON COLUMN carteira_pesca.retorno.valor_abatimento_concedido IS 'Valor dos títulos que tiveram abatimento concedido.';
COMMENT ON COLUMN carteira_pesca.retorno.qtd_confirmacao_instrucao_protesto IS 'Quantidade de títulos que tiveram confirmação de instrução de protesto.';
COMMENT ON COLUMN carteira_pesca.retorno.valor_confirmacao_instrucao_protesto IS 'Valor dos títulos que tiveram confirmação de instrução de protesto.';
COMMENT ON COLUMN carteira_pesca.retorno.qtd_total_rateio_efetuado IS 'Quantidade de títulos que tiveram rateio efetuado.';
COMMENT ON COLUMN carteira_pesca.retorno.valor_total_rateio_efetuado IS 'Valor dos títulos que tiveram rateio efetuado.';
COMMENT ON COLUMN carteira_pesca.retorno.numero_aviso_bancario IS 'Número do aviso registrado pelo banco.';

ALTER TABLE carteira_pesca.titulo DROP COLUMN dt_geracao_remessa;

ALTER TABLE carteira_pesca.titulo ADD COLUMN valor_pago DOUBLE PRECISION;
COMMENT ON COLUMN carteira_pesca.titulo.valor_pago IS 'Valor que foi pago pelo titulo título.';

ALTER TABLE carteira_pesca.titulo ADD COLUMN id_remessa INTEGER;
ALTER TABLE carteira_pesca.titulo ADD CONSTRAINT fk_remessa_titulo FOREIGN KEY (id_remessa)
  REFERENCES carteira_pesca.remessa (id) MATCH SIMPLE
  ON UPDATE RESTRICT ON DELETE RESTRICT;

COMMENT ON COLUMN carteira_pesca.titulo.id_remessa IS 'Remessa gerada para o título.';

ALTER TABLE carteira_pesca.titulo ADD COLUMN id_retorno INTEGER NULL;
ALTER TABLE carteira_pesca.titulo ADD CONSTRAINT fk_retorno_titulo FOREIGN KEY (id_retorno)
  REFERENCES carteira_pesca.retorno (id) MATCH SIMPLE
  ON UPDATE RESTRICT ON DELETE RESTRICT;

COMMENT ON COLUMN carteira_pesca.titulo.id_retorno IS 'Retorno que o titulo foi processado.';

ALTER TABLE carteira_pesca.remessa DROP COLUMN sequencia_nome_arquivo;
ALTER TABLE carteira_pesca.remessa ADD COLUMN sequencia_nome_arquivo INTEGER;

COMMENT ON COLUMN carteira_pesca.remessa.sequencia_nome_arquivo IS 'Sequência com nome do arquivo.';