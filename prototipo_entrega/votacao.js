// Sistema de Votação Cooperativista - VotaCoop
// JavaScript para funcionalidades do protótipo

class VotaCoopApp {
    constructor() {
        this.votoSelecionado = null;
        this.timer = null;
        this.tempoRestante = 45 * 60 + 32; // 45:32 em segundos
        
        this.init();
    }

    init() {
        this.setupNavigation();
        this.setupVotingSystem();
        this.setupForms();
        this.setupTimer();
        this.setupCharts();
        this.formatCPFInput();
    }

    setupNavigation() {
        const tabs = document.querySelectorAll('[data-section]');
        const sections = document.querySelectorAll('.content-section');

        tabs.forEach(tab => {
            tab.addEventListener('click', (e) => {
                e.preventDefault();
                
                // Remove active class from all tabs and sections
                tabs.forEach(t => t.classList.remove('active'));
                sections.forEach(s => s.style.display = 'none');
                
                // Add active class to clicked tab
                tab.classList.add('active');
                
                // Show corresponding section
                const sectionId = tab.getAttribute('data-section') + '-section';
                const section = document.getElementById(sectionId);
                if (section) {
                    section.style.display = 'block';
                }
            });
        });
    }

    setupVotingSystem() {
        const voteOptions = document.querySelectorAll('.vote-option');
        const confirmarBtn = document.getElementById('confirmarVoto');

        voteOptions.forEach(option => {
            option.addEventListener('click', () => {
                // Remove previous selection
                voteOptions.forEach(opt => opt.classList.remove('selected'));
                
                // Add selection to clicked option
                option.classList.add('selected');
                this.votoSelecionado = option.getAttribute('data-vote');
                
                // Enable confirm button
                confirmarBtn.disabled = false;
            });
        });

        confirmarBtn.addEventListener('click', () => {
            this.confirmarVoto();
        });
    }

    confirmarVoto() {
        const cpf = document.getElementById('cpfAssociado').value;
        const id = document.getElementById('idAssociado').value;

        if (!cpf || !id) {
            alert('Por favor, preencha seu CPF e ID de associado.');
            return;
        }

        if (!this.votoSelecionado) {
            alert('Por favor, selecione uma opção de voto.');
            return;
        }

        // Simular registro do voto
        this.registrarVoto();
    }

    registrarVoto() {
        // Atualizar modal com informações do voto
        document.getElementById('votoRegistrado').textContent = 
            this.votoSelecionado.toUpperCase();
        document.getElementById('dataVoto').textContent = 
            new Date().toLocaleString('pt-BR');

        // Mostrar modal de sucesso
        const modal = new bootstrap.Modal(document.getElementById('sucessoModal'));
        modal.show();

        // Atualizar resultados (simulado)
        this.atualizarResultados();

        // Reset formulário
        setTimeout(() => {
            this.resetFormularioVoto();
        }, 2000);
    }

    resetFormularioVoto() {
        document.querySelectorAll('.vote-option').forEach(opt => 
            opt.classList.remove('selected'));
        document.getElementById('confirmarVoto').disabled = true;
        document.getElementById('cpfAssociado').value = '';
        document.getElementById('idAssociado').value = '';
        this.votoSelecionado = null;
    }

    atualizarResultados() {
        // Simular atualização dos números de votação
        const totalVotos = Math.floor(Math.random() * 10) + 438;
        const porcentagemSim = 78 + Math.floor(Math.random() * 5);
        const porcentagemNao = 100 - porcentagemSim;
        
        console.log(`Total de votos atualizado: ${totalVotos}`);
        console.log(`Sim: ${porcentagemSim}%, Não: ${porcentagemNao}%`);
    }

    setupForms() {
        const novaPautaForm = document.getElementById('novaPautaForm');
        if (novaPautaForm) {
            novaPautaForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.criarNovaPauta();
            });
        }

        const abrirSessaoBtn = document.getElementById('abrirSessao');
        if (abrirSessaoBtn) {
            abrirSessaoBtn.addEventListener('click', () => {
                this.abrirSessaoVotacao();
            });
        }

        const fecharSessaoBtn = document.getElementById('fecharSessao');
        if (fecharSessaoBtn) {
            fecharSessaoBtn.addEventListener('click', () => {
                this.fecharSessaoVotacao();
            });
        }
    }

    criarNovaPauta() {
        const titulo = document.getElementById('tituloPauta').value;
        const descricao = document.getElementById('descricaoPauta').value;
        const dataInicio = document.getElementById('dataInicio').value;
        const duracao = document.getElementById('duracaoVotacao').value;

        if (!titulo || !descricao) {
            alert('Por favor, preencha todos os campos obrigatórios.');
            return;
        }

        alert(`Pauta "${titulo}" criada com sucesso!`);
        
        // Reset form
        document.getElementById('novaPautaForm').reset();
    }

    abrirSessaoVotacao() {
        const pauta = document.getElementById('pautaParaAbrir').value;
        if (!pauta) {
            alert('Selecione uma pauta para abrir.');
            return;
        }

        alert('Sessão de votação aberta com sucesso!');
    }

    fecharSessaoVotacao() {
        if (confirm('Tem certeza que deseja fechar a sessão atual?')) {
            alert('Sessão de votação encerrada.');
        }
    }

    setupTimer() {
        this.updateTimer();
        this.timer = setInterval(() => {
            this.tempoRestante--;
            this.updateTimer();
            
            if (this.tempoRestante <= 0) {
                clearInterval(this.timer);
                alert('Tempo de votação esgotado!');
            }
        }, 1000);
    }

    updateTimer() {
        const minutos = Math.floor(this.tempoRestante / 60);
        const segundos = this.tempoRestante % 60;
        const tempoFormatado = `${minutos}:${segundos.toString().padStart(2, '0')}`;
        
        document.querySelectorAll('.voting-session-timer').forEach(timer => {
            timer.innerHTML = `<i class="bi bi-clock"></i> Tempo restante: ${tempoFormatado}`;
        });
    }

    setupCharts() {
        // Chart 1 - Votação em andamento
        const ctx1 = document.getElementById('chartResultado1');
        if (ctx1) {
            new Chart(ctx1, {
                type: 'doughnut',
                data: {
                    labels: ['Sim', 'Não'],
                    datasets: [{
                        data: [78, 22],
                        backgroundColor: ['#198754', '#dc3545'],
                        borderWidth: 2,
                        borderColor: '#fff'
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
        }

        // Chart 2 - Votação finalizada
        const ctx2 = document.getElementById('chartResultado2');
        if (ctx2) {
            new Chart(ctx2, {
                type: 'doughnut',
                data: {
                    labels: ['Sim', 'Não'],
                    datasets: [{
                        data: [87, 13],
                        backgroundColor: ['#198754', '#dc3545'],
                        borderWidth: 2,
                        borderColor: '#fff'
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
        }
    }

    formatCPFInput() {
        const cpfInput = document.getElementById('cpfAssociado');
        if (cpfInput) {
            cpfInput.addEventListener('input', (e) => {
                let value = e.target.value.replace(/\D/g, '');
                value = value.replace(/(\d{3})(\d)/, '$1.$2');
                value = value.replace(/(\d{3})(\d)/, '$1.$2');
                value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
                e.target.value = value;
            });
        }
    }
}

// Inicializar aplicação quando DOM estiver carregado
document.addEventListener('DOMContentLoaded', () => {
    new VotaCoopApp();
});

// Dados de demonstração para testes
const dadosDemonstracao = {
    pautas: [
        {
            id: 1,
            titulo: "Aprovação do Relatório Anual 2024",
            descricao: "Aprovação dos demonstrativos financeiros e relatório de atividades do exercício 2024 da cooperativa.",
            dataCriacao: "01/11/2025",
            status: "aberta",
            tempoRestante: "45:32"
        },
        {
            id: 2,
            titulo: "Eleição do Conselho Fiscal",
            descricao: "Eleição dos membros do conselho fiscal para o biênio 2025-2026.",
            dataCriacao: "30/10/2025",
            status: "agendada",
            dataInicio: "05/11/2025 14:00"
        },
        {
            id: 3,
            titulo: "Aprovação de Investimento em Tecnologia",
            descricao: "Aprovação do investimento de R$ 2.500.000 em modernização de sistemas e infraestrutura tecnológica.",
            dataCriacao: "29/10/2025",
            status: "encerrada",
            resultado: "aprovada",
            porcentagemSim: 87
        }
    ],
    estatisticas: {
        totalAssociados: 1247,
        pautasAtivas: 1,
        proximaVotacao: "05/11/2025",
        participacaoMedia: 78
    }
};

// Funções auxiliares para demonstração
function simularValidacaoCPF(cpf) {
    // Simula validação de CPF
    const cpfLimpo = cpf.replace(/\D/g, '');
    return cpfLimpo.length === 11;
}

function simularElegibilidade(cpf) {
    // Simula verificação de elegibilidade (aleatória)
    return Math.random() > 0.1; // 90% de chance de ser elegível
}

function logarAcao(acao, detalhes) {
    // Simula logging de ações
    console.log(`[${new Date().toISOString()}] ${acao}:`, detalhes);
}

// Exportar para uso global
window.VotaCoopApp = VotaCoopApp;
window.dadosDemonstracao = dadosDemonstracao;