export interface SessaoVotacaoRequestDTO {
  pautaId: number;
  duracao?: number; // Alinhado com o backend
}

export interface SessaoVotacaoResponseDTO {
  id: number;
  pautaId: number;
  dataAbertura: string;
  dataFechamento: string;
  duracao?: number;
}

export interface SessaoAberta {
  id: number;
  pautaId: number;
  pautaTitulo: string;
  dataInicio: string;
  dataFim: string;
}
