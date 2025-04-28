export interface VotoRequestDTO {
  pautaId: number;
  cpf: string;
  voto: 'SIM' | 'NAO';
}

export interface VotoResponseDTO {
  id: number;
  pautaId: number;
  cpf: string;
  voto: 'SIM' | 'NAO';
}

export interface ResultadoResponseDTO {
  pautaId: number;
  titulo: string;
  votosSim: number;
  votosNao: number;
}

export interface SessaoAbertaDTO {
  id: number;
  pautaId: number;
  pautaTitulo: string;
  dataAbertura: string;
  dataFechamento: string;
  tempoRestante: string;
}
