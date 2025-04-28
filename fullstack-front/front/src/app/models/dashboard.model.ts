export interface DashboardResumoDTO {
  totalPautas: number;
  totalSessoesAbertas: number;
  totalSessoesEncerradas: number;
  totalVotos: number;
  percentualVotosSim: number;
  percentualVotosNao: number;
  pautasRecentes: PautaResumoDTO[];
  sessoesAtivas: SessaoResumoDTO[];
}

export interface PautaResumoDTO {
  id: number;
  titulo: string;
  totalVotos: number;
}

export interface SessaoResumoDTO {
  id: number;
  pautaId: number;
  pautaTitulo: string;
  tempoRestante: string;
}

export interface ParticipacaoSessaoDTO {
  sessaoId: number;
  pautaId: number;
  pautaTitulo: string;
  totalVotos: number;
  percentualParticipacao: number;
}

export interface TendenciaVotosDTO {
  periodo: string;
  votosSim: number;
  votosNao: number;
}
