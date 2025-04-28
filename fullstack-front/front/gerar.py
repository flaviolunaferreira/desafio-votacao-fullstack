import os
from pathlib import Path

# Configurações personalizáveis
DIRETORIO_RAIZ = "C:/Users/Flavio/Desktop/Projeto/desafio-votacao-fullstack/fullstack-front/front/src"  # Substitua pelo caminho real da sua pasta
ARQUIVO_SAIDA = "todos_os_fronts.txt"
EXTENSOES_PERMITIDAS = [".html", ".scss", ".ts", ".json"]  # Extensões a serem incluídas
INCLUIR_CAMINHO_RELATIVO = True  # Define se o caminho relativo será incluído no nome do arquivo
CODIFICACOES_POSSIVEIS = ["utf-8", "latin-1"]  # Lista de codificações a tentar, em ordem de preferência

def escrever_cabecalho(saida, caminho_arquivo):
    """Escreve o cabeçalho com o nome ou caminho do arquivo no arquivo de saída."""
    saida.write(f"\n{'=' * 50}\n")
    saida.write(f"Arquivo: {caminho_arquivo}\n")
    saida.write(f"{'=' * 50}\n\n")

def tentar_ler_arquivo(caminho_completo):
    """Tenta ler o conteúdo do arquivo com diferentes codificações."""
    for codificacao in CODIFICACOES_POSSIVEIS:
        try:
            with open(caminho_completo, "r", encoding=codificacao) as f:
                return f.read(), None
        except UnicodeDecodeError:
            continue
        except Exception as e:
            return None, f"Erro ao ler o arquivo: {e}"
    return None, "Erro: Não foi possível decodificar o arquivo com as codificações fornecidas"

def processar_arquivos(diretorio, arquivo_saida):
    """Processa todos os arquivos no diretório e subdiretórios, salvando no arquivo de saída."""
    # Contadores para estatísticas
    total_arquivos = 0
    erros = 0

    # Abrir o arquivo de saída no modo de escrita
    with open(arquivo_saida, "w", encoding="utf-8") as saida:
        # Escrever um cabeçalho inicial
        saida.write(f"Relatório de arquivos do diretório: {diretorio}\n")
        saida.write(f"Data de geração: {os.path.getctime(diretorio)}\n")
        saida.write(f"Extensões permitidas: {', '.join(EXTENSOES_PERMITIDAS)}\n\n")

        # Percorrer o diretório raiz e subpastas
        for root, dirs, files in os.walk(diretorio):
            for arquivo in sorted(files):  # Ordenar arquivos para consistência
                # Verificar se o arquivo tem uma extensão permitida
                if any(arquivo.endswith(ext) for ext in EXTENSOES_PERMITIDAS):
                    total_arquivos += 1
                    caminho_completo = os.path.join(root, arquivo)

                    # Determinar o nome a ser exibido (nome simples ou caminho relativo)
                    if INCLUIR_CAMINHO_RELATIVO:
                        caminho_relativo = os.path.relpath(caminho_completo, DIRETORIO_RAIZ)
                    else:
                        caminho_relativo = arquivo

                    # Escrever o cabeçalho do arquivo
                    escrever_cabecalho(saida, caminho_relativo)

                    # Tentar ler e escrever o conteúdo
                    conteudo, erro = tentar_ler_arquivo(caminho_completo)
                    if conteudo is not None:
                        saida.write(conteudo)
                        saida.write("\n\n")
                    else:
                        saida.write(f"{erro}\n\n")
                        erros += 1

        # Escrever resumo no final
        saida.write(f"{'=' * 50}\n")
        saida.write(f"Resumo:\n")
        saida.write(f"Total de arquivos processados: {total_arquivos}\n")
        saida.write(f"Arquivos com erro: {erros}\n")
        saida.write(f"Arquivos processados com sucesso: {total_arquivos - erros}\n")
        saida.write(f"{'=' * 50}\n")

    return total_arquivos, erros

def main():
    """Função principal para executar o script."""
    # Verificar se o diretório existe
    if not os.path.isdir(DIRETORIO_RAIZ):
        print(f"Erro: O diretório '{DIRETORIO_RAIZ}' não existe.")
        return

    print(f"Iniciando processamento do diretório: {DIRETORIO_RAIZ}")
    total, erros = processar_arquivos(DIRETORIO_RAIZ, ARQUIVO_SAIDA)

    if total == 0:
        print("Nenhum arquivo encontrado com as extensões especificadas.")
    else:
        print(f"Processamento concluído!")
        print(f"Total de arquivos encontrados: {total}")
        print(f"Arquivos com erro: {erros}")
        print(f"Todos os arquivos foram salvos em: {ARQUIVO_SAIDA}")

if __name__ == "__main__":
    main()
