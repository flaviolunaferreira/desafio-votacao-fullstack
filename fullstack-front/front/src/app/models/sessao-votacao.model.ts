export interface SessaoVotacaoRequestDTO {
  pautaId: number;
  dataAbertura?: string;
  dataFechamento?: string;
}

export interface SessaoVotacaoResponseDTO {
  id: number;
  pautaId: number;
  dataAbertura: string;
  dataFechamento: string;
}

export interface SessaoAberta {
  id: number;
  pautaId: number;
  pautaTitulo: string;
  dataInicio: string;
  dataFim: string;
}
