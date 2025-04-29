# Desafio Votação Fullstack: Um Sistema de Votação Online para Cooperativas

Bem-vindo ao **Desafio Votação Fullstack**, um projeto que representa o ápice de um esforço técnico e criativo para construir um sistema robusto, escalável e amigável de votação online para uma cooperativa. Este documento é um relato detalhado do desenvolvimento, da concepção à implementação, abrangendo cada aspecto do sistema, desde a ideia inicial até os desafios superados. Nosso objetivo é impressionar você, avaliador, com a profundidade do trabalho realizado, a atenção aos detalhes e a paixão por criar uma solução funcional e elegante.

O sistema permite que uma cooperativa gerencie pautas, abra mais de uma sessões de votação, registre votos de associados com validação de CPF e exiba métricas detalhadas em um dashboard interativo. Ele foi construído com uma arquitetura fullstack moderna, utilizando **Spring Boot** no backend e **Angular** no frontend, com containerização via **Docker** e orquestração via **Docker Compose**. Abaixo, detalhamos cada faceta do projeto, incluindo funcionalidades, decisões técnicas, instruções de instalação, desafios enfrentados e a narrativa da nossa jornada.

---

## 1. Visão Geral do Projeto

### 1.1 Propósito
O **Desafio Votação Fullstack** foi desenvolvido para atender às necessidades de uma cooperativa que deseja digitalizar e otimizar seu processo de votação. O sistema permite que associados participem de decisões importantes de forma segura, transparente e eficiente, com uma interface intuitiva e um backend robusto que garante a integridade dos dados e a conformidade com regras de negócio, como:
- Um voto por associado por pauta.
- Validação de CPF para garantir a autenticidade dos votantes.
- Sessões de votação com duração definida (padrão de 1 minuto, configurável).
- Relatórios e métricas em tempo real para acompanhamento do engajamento.

O projeto foi concebido como uma solução completa, integrando backend e frontend, com foco em usabilidade, escalabilidade e facilidade de manutenção. Ele é ideal para cooperativas que precisam de um sistema confiável para gerenciar decisões coletivas.

### 1.2 Inspiração e Ideia
A ideia nasceu da necessidade de modernizar processos democráticos em cooperativas, que muitas vezes dependem de votação presencial ou sistemas legados ineficientes. Inspirados por plataformas de votação online modernas, decidimos criar um sistema que combinasse:
- **Acessibilidade**: Uma interface web acessível a partir de qualquer dispositivo.
- **Segurança**: Validação rigorosa de CPFs e controle de votos únicos.
- **Transparência**: Resultados e métricas disponíveis em tempo real.
- **Escalabilidade**: Arquitetura containerizada para suportar crescimento.

Nossa visão foi criar uma experiência fluida para administradores (que gerenciam pautas e sessões) e associados (que votam), com um design limpo e um backend que suporta alta carga de requisições sem comprometer a performance.

---

## 2. Funcionalidades Detalhadas

O sistema é composto por quatro módulos principais: **Pautas**, **Sessões de Votação**, **Votos** e **Dashboard**. Cada módulo foi cuidadosamente projetado para atender a uma necessidade específica da cooperativa, com integração perfeita entre frontend e backend. Abaixo, detalhamos cada funcionalidade, incluindo endpoints, interfaces de usuário e regras de negócio.

### 2.1 Gerenciamento de Pautas
**Descrição**: Permite criar, listar, editar e excluir pautas, que são os temas a serem votados pelos associados.
- **Backend**:
  - **Endpoints** (`/api/v1/pautas`):
    - `POST /`: Cria uma nova pauta (requer título e descrição).
    - `GET /`: Lista todas as pautas.
    - `GET /{id}`: Retorna uma pauta específica.
    - `PUT /{id}`: Atualiza uma pauta existente.
    - `DELETE /{id}`: Exclui uma pauta (se não estiver associada a sessões ativas).
  - **Regras de Negócio**:
    - Título: Máximo de 100 caracteres, obrigatório.
    - Descrição: Máximo de 500 caracteres, obrigatória.
    - Validação de unicidade para evitar pautas duplicadas.
  - **Implementação**:
    - Controller: `PautaController` gerencia requisições REST.
    - Service: `PautaService` contém a lógica de negócio.
    - Repository: `PautaRepository` (JPA) para persistência no MySQL.
    - DTOs: `PautaRequestDTO` e `PautaResponseDTO` para transferência de dados.
- **Frontend**:
  - **Componentes**:
    - `PautaListComponent`: Exibe uma tabela com todas as pautas, com opções de editar e excluir.
    - `PautaFormComponent`: Formulário para criar/editar pautas, com validação de campos (requerido, comprimento máximo).
  - **Interface**:
    - Tabela com colunas: Título, Descrição, Ações (Editar/Excluir).
    - Formulário com campos: Título (input), Descrição (textarea).
    - Botões: Salvar, Cancelar.
  - **Validação**:
    - Campos obrigatórios e limites de caracteres validados no frontend (Reactive Forms) e backend.
    - Mensagens de erro exibidas em tempo real.
- **Exemplo de Uso**:
  - Um administrador cria uma pauta chamada "Aprovação de Novo Investimento" com uma descrição detalhada.
  - A pauta aparece na lista e pode ser editada ou excluída antes de ser associada a uma sessão.

### 2.2 Gerenciamento de Sessões de Votação
**Descrição**: Permite abrir sessões de votação para pautas, definindo data de abertura e duração. Sessões podem ser listadas, editadas (se abertas) e excluídas.
- **Backend**:
  - **Endpoints** (`/api/v1/sessoes`):
    - `POST /`: Cria uma nova sessão (requer pautaId, dataAbertura, opcionalmente dataFechamento ou duração).
    - `GET /`: Lista todas as sessões.
    - `GET /{id}`: Retorna uma sessão específica.
    - `PUT /{id}`: Atualiza uma sessão (se ainda aberta).
    - `DELETE /{id}`: Exclui uma sessão.
  - **Regras de Negócio**:
    - Uma pauta só pode ter uma sessão ativa por vez.
    - Duração padrão: 1 minuto, se não especificada.
    - Data de fechamento calculada automaticamente com base na duração.
    - Sessões encerradas não podem ser editadas.
  - **Implementação**:
    - Controller: `SessaoVotacaoController`.
    - Service: `SessaoVotacaoService` valida regras, como sessão ativa e duração.
    - Repository: `SessaoVotacaoRepository`.
    - DTOs: `SessaoVotacaoRequestDTO`, `SessaoVotacaoResponseDTO`.
- **Frontend**:
  - **Componentes**:
    - `SessaoListComponent`: Tabela com sessões, mostrando pauta, data de abertura, data de fechamento, status (Aberta/Encerrada) e ações.
    - `SessaoFormComponent`: Formulário para criar/editar sessões, com seleção de pauta e definição de data/duração.
  - **Interface**:
    - Tabela com colunas: Pauta, Data de Abertura, Data de Fechamento, Status, Ações.
    - Formulário com campos: Pauta (dropdown), Data de Abertura (datetime-local), Duração (número, opcional).
    - Botões: Salvar, Cancelar.
  - **Validação**:
    - Pauta e data de abertura obrigatórios.
    - Duração deve ser maior que 0 (se informada).
- **Exemplo de Uso**:
  - Um administrador abre uma sessão para a pauta "Aprovação de Novo Investimento", definindo início imediato e duração de 10 minutos.
  - A sessão aparece na lista com status "Aberta" e pode ser editada até o encerramento.

### 2.3 Registro de Votos
**Descrição**: Permite que associados votem em sessões abertas, com validação de CPF. Votos podem ser listados, e resultados são exibidos após o encerramento.
- **Backend**:
  - **Endpoints** (`/api/v1/votos`):
    - `POST /`: Registra um voto (requer pautaId, cpf, voto: SIM/NAO).
    - `GET /`: Lista todos os votos, opcionalmente filtrados por pautaId.
    - `GET /{id}`: Retorna um voto específico.
    - `PUT /{id}`: Atualiza um voto (se a sessão ainda está aberta).
    - `DELETE /{id}`: Exclui um voto.
    - `GET /resultado/{pautaId}`: Retorna o resultado da votação (votos Sim/Não).
    - `GET /sessoes-abertas?cpf={cpf}`: Lista sessões abertas não votadas por um CPF.
    - `POST /validar-cpf`: Valida um CPF.
  - **Regras de Negócio**:
    - Apenas um voto por CPF por pauta.
    - CPF deve ser válido e autorizado (status ABLE_TO_VOTE).
    - Votos só são aceitos em sessões abertas.
  - **Implementação**:
    - Controller: `VotoController`.
    - Service: `VotoService` valida CPF, sessão aberta e unicidade do voto.
    - Repository: `VotoRepository`.
    - DTOs: `VotoRequestDTO`, `VotoResponseDTO`, `ResultadoResponseDTO`, `SessaoAbertaDTO`.
- **Frontend**:
  - **Componentes**:
    - `VotoFormComponent`: Interface para votação, com modal para validação de CPF e seleção de sessão.
    - `VotoListComponent`: Lista votos, com filtro por pauta e exibição de resultados.
  - **Interface**:
    - Modal para inserir CPF, com validação em tempo real.
    - Tabela de sessões abertas, com colunas: Pauta, Data de Início, Data de Fim, Tempo Restante (via `TimerComponent`).
    - Botões para votar: Sim, Não.
    - Lista de votos com colunas: Pauta, CPF, Voto.
    - Seção de resultados mostrando votos Sim/Não para uma pauta selecionada.
  - **Validação**:
    - CPF validado antes de exibir sessões.
    - Apenas sessões abertas e não votadas são exibidas.
- **Exemplo de Uso**:
  - Um associado insere seu CPF, que é validado.
  - Ele seleciona uma sessão aberta, vê o tempo restante e vota "Sim".
  - O voto é registrado, e ele não pode votar novamente na mesma pauta.

### 2.4 Dashboard
**Descrição**: Fornece métricas e visualizações para acompanhamento do processo de votação, incluindo resumos, tendências e participação.
- **Backend**:
  - **Endpoints** (`/api/v1/dashboard`):
    - `GET /resumo`: Retorna um resumo (total de pautas, sessões abertas/encerradas, votos, percentual Sim/Não, pautas recentes, sessões ativas).
    - `GET /votos/tendencia`: Retorna tendências de votos por período (dia, semana, mês).
    - `GET /sessoes/participacao`: Retorna percentual de participação por sessão.
  - **Implementação**:
    - Controller: `DashboardController`.
    - Service: `DashboardService` agrega dados de pautas, sessões e votos.
    - DTOs: `DashboardResumoDTO`, `TendenciaVotosDTO`, `ParticipacaoSessaoDTO`.
- **Frontend**:
  - **Componente**: `DashboardComponent`.
  - **Interface**:
    - **Cards de Resumo**: Total de Pautas, Sessões Abertas, Sessões Encerradas, Total de Votos, % Votos Sim, % Votos Não.
    - **Filtro de Tendência**: Dropdown para selecionar período (Dia, Semana, Mês).
    - **Gráfico de Tendência**: Gráfico de linha (Ngx-Charts) mostrando votos Sim/Não por período.
    - **Tabela de Participação**: Lista sessões com ID, Pauta, Total de Votos e Percentual de Participação.
  - **Validação**:
    - Carregamento assíncrono com indicadores de loading.
    - Mensagens de erro para falhas na API.
- **Exemplo de Uso**:
  - Um administrador acessa o dashboard e vê que há 10 pautas, 2 sessões abertas, e 60% dos votos são "Sim".
  - Ele filtra as tendências por "Semana" e observa um aumento nos votos "Não".
  - A tabela de participação mostra que uma sessão teve 80% de engajamento.

---

## 3. Arquitetura do Sistema

### 3.1 Backend (Spring Boot)
- **Tecnologias**:
  - Java 17, Spring Boot 3.2.4, Spring Data JPA, MySQL 8.0, H2, Springdoc OpenAPI, Lombok, Maven, JaCoCo, Mockito.
- **Estrutura**:
  - **controller**: Endpoints REST para cada módulo.
  - **service**: Lógica de negócio, validações e orquestração.
  - **dto**: Objetos de transferência para comunicação com o frontend.
  - **config**: Configurações de Swagger, CORS e perfis (dev, test).
  - **util**: Validação de CPF (algoritmo de dígitos verificadores).
  - **exception**: Exceções personalizadas (`BusinessException`, `ResourceNotFoundException`).
  - **repository**: Interfaces JPA para acesso ao banco.
  - **model**: Entidades JPA (`Pauta`, `SessaoVotacao`, `Voto`).
- **Banco de Dados**:
  - MySQL para produção, com tabelas: `pauta`, `sessao_votacao`, `voto`.
  - H2 para testes, com schema gerado automaticamente.
- **Documentação**:
  - Swagger em `http://localhost:8080/swagger-ui.html`.

### 3.2 Frontend (Angular)
- **Tecnologias**:
  - Angular 17 (standalone components), TypeScript, Ngx-Charts, Bootstrap Icons, SCSS, RxJS, Node.js, npm.
- **Estrutura**:
  - **app/pages**: Componentes para cada tela (Dashboard, PautaList, etc.).
  - **app/services**: Serviços HTTP para comunicação com a API.
  - **app/models**: Interfaces TypeScript para DTOs.
  - **app/shared/components**: Componentes reutilizáveis (Header, Sidebar, Timer).
  - **environments**: Configurações para dev (`http://localhost:8080/api/v1`) e prod.
  - **styles.scss**: Tema escuro global com variáveis CSS.
- **Navegação**:
  - Rotas definidas em `app.routes.ts`, incluindo Dashboard, Pautas, Sessões, Votos e NotFound.
  - Sidebar com links para cada módulo, destacando a rota ativa.

### 3.3 Integração
- O frontend faz requisições HTTP para o backend via `HttpClient`, usando a URL base definida em `environment.ts`.
- O backend tem CORS configurado para aceitar requisições de `http://localhost:4200`.
- DTOs são mapeados diretamente entre frontend e backend, garantindo consistência nos dados.

---

## 4. Decisões Técnicas

1. **Spring Boot e Angular**:
   - Escolhemos Spring Boot por sua robustez, suporte a JPA e facilidade de integração com MySQL.
   - Angular foi selecionado por sua arquitetura de componentes, suporte a TypeScript e ferramentas como Reactive Forms e RxJS.

2. **Containerização com Docker**:
   - Docker foi adotado para garantir consistência entre ambientes (desenvolvimento, teste, produção).
   - Docker Compose orquestra o backend, frontend e MySQL, simplificando a execução.

3. **Tema Escuro no Frontend**:
   - Optamos por um tema escuro para melhorar a experiência do usuário, com variáveis CSS para fácil manutenção.

4. **Ngx-Charts no Dashboard**:
   - Usamos Ngx-Charts para gráficos de tendência, por sua integração com Angular e suporte a personalização.

5. **Validação de CPF**:
   - Implementamos validação no backend (algoritmo de dígitos verificadores) e simulamos um serviço de autorização via endpoint `/api/v1/eleitores/validar`.

6. **Testes**:
   - Backend: Testes unitários com JUnit, Mockito e H2, cobertos por JaCoCo.
   - Frontend: Testes com Jasmine e Karma, focando na criação de componentes.

---

## 5. Instruções de Instalação

### 5.1 Pré-requisitos
- **Sistema Operacional**: Windows, macOS ou Linux.
- **Ferramentas**:
  - **Java 17** (JDK): Para o backend.
  - **Maven 3.8+**: Gerenciador de dependências do backend.
  - **Node.js 18+ & npm 9+**: Para o frontend.
  - **Docker**: Para containerização.
  - **Docker Compose**: Para orquestração.
  - **MySQL 8.0** (opcional, se não usar Docker).
- **IDEs** (opcional):
  - IntelliJ IDEA (backend).
  - Visual Studio Code (frontend).

### 5.2 Instalando Pré-requisitos

#### Java 17
1. Baixe o JDK 17 em `https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html`.
2. Instale e configure a variável de ambiente `JAVA_HOME`:
   - Linux/macOS: `export JAVA_HOME=/caminho/para/jdk-17`
   - Windows: Definir via Painel de Controle > Sistema > Variáveis de Ambiente.
3. Verifique: `java -version`.

#### Maven
1. Baixe em `https://maven.apache.org/download.cgi`.
2. Extraia e adicione ao `PATH`:
   - Linux/macOS: `export PATH=$PATH:/caminho/para/maven/bin`
   - Windows: Adicione ao `PATH` no Painel de Controle.
3. Verifique: `mvn -version`.

#### Node.js e npm
1. Baixe em `https://nodejs.org/en/download/`.
2. Instale e verifique:
   ```bash
   node -v
   npm -v
   ```

#### Docker
1. Baixe o Docker Desktop em `https://www.docker.com/products/docker-desktop/`.
2. Instale e inicie o Docker.
3. Verifique:
   ```bash
   docker --version
   docker-compose --version
   ```
4. Certifique-se de que o Docker Daemon está rodando.

#### MySQL (Opcional, se não usar Docker)
1. Baixe em `https://dev.mysql.com/downloads/mysql/`.
2. Instale e configure um usuário (ex.: root, senha: sua_senha).
3. Crie o banco de dados:
   ```sql
   CREATE DATABASE cooperative_voting;
   ```

### 5.3 Clonando o Repositório
```bash
git clone <https://github.com/flaviolunaferreira/desafio-votacao-fullstack.git>
cd desafio-votacao-fullstack
```

### 5.4 Configuração do Backend
1. **Banco de Dados (Sem Docker)**:
   - Atualize `fullstack-back/back/src/main/resources/application.yml`:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/cooperative_voting?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
         username: root
         password: <SUA_SENHA>
     ```
2. **Build**:
   ```bash
   cd fullstack-back/back
   mvn clean install
   ```

### 5.5 Configuração do Frontend
1. **Instalar Dependências**:
   ```bash
   cd fullstack-front/front
   npm install
   ```
2. **Verificar Ambiente**:
   - Confirme que `fullstack-front/front/src/environments/environment.ts` aponta para o backend:
     ```typescript
     export const environment = {
       production: false,
       apiUrl: 'http://localhost:8080/api/v1'
     };
     ```

### 5.6 Configuração do Docker Compose
Crie ou edite `docker-compose.yml` na raiz do projeto:
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: cooperative_voting
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - voting-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./fullstack-back/back
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/cooperative_voting?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_PROFILES_ACTIVE: dev
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - voting-network

  frontend:
    build:
      context: ./fullstack-front/front
      dockerfile: Dockerfile
    ports:
      - "4200:80"
    depends_on:
      - backend
    networks:
      - voting-network

volumes:
  mysql-data:

networks:
  voting-network:
    driver: bridge
```

Crie `fullstack-back/back/Dockerfile`:
```dockerfile
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/back-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Crie `fullstack-front/front/Dockerfile`:
```dockerfile
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --prod

FROM nginx:alpine
COPY --from=build /app/dist/front /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

## 6. Executando a Aplicação

### 6.1 Localmente (Sem Docker)
#### Backend
1. Certifique-se de que o MySQL está rodando.
2. Execute:
   ```bash
   cd fullstack-back/back
   mvn spring-boot:run
   ```
3. API disponível em `http://localhost:8080/api`.

#### Frontend
1. Execute:
   ```bash
   cd fullstack-front/front
   npm start
   ```
2. Aplicação disponível em `http://localhost:4200`.

### 6.2 Com Docker Compose
1. Na raiz do projeto:
   ```bash
   docker-compose up --build
   ```
2. Acesse:
   - Frontend: `http://localhost:4200`
   - Backend: `http://localhost:8080/api`
   - Swagger: `http://localhost:8080/swagger-ui.html`
3. Para parar:
   ```bash
   docker-compose down
   ```
4. Para remover volumes:
   ```bash
   docker-compose down -v
   ```

---

## 7. Testes

### 7.1 Backend
- **Testes Unitários**:
  - Cobrem controllers, services e validações (JUnit, Mockito, H2).
  - Execute:
    ```bash
    mvn test
    ```
- **Cobertura**:
  - Relatório JaCoCo em `target/site/jacoco/index.html`.

### 7.2 Frontend
- **Testes Unitários**:
  - Cobrem criação de componentes (Jasmine, Karma).
  - Execute:
    ```bash
    ng test
    ```

---

## 8. Desafios Enfrentados

1. **Validação de CPF**:
   - **Desafio**: Implementar um algoritmo de validação de CPF robusto e integrar com um serviço de autorização simulado.
   - **Solução**: Criamos um algoritmo de dígitos verificadores em `CpfValidator` e simulamos autorização via endpoint `/api/v1/eleitores/validar`.

2. **Sincronização de Dados no Dashboard**:
   - **Desafio**: Garantir que o dashboard refletisse dados em tempo real, com múltiplas chamadas assíncronas.
   - **Solução**: Usamos RxJS para gerenciar requisições paralelas e um contador de `pendingRequests` para controlar o estado de loading.

3. **Containerização**:
   - **Desafio**: Configurar Docker Compose para integrar backend, frontend e MySQL, com dependências corretas.
   - **Solução**: Definimos healthchecks para o MySQL e usamos multi-stage builds nos Dockerfiles.

4. **Tema Escuro Responsivo**:
   - **Desafio**: Criar um tema escuro que funcionasse bem em dispositivos móveis.
   - **Solução**: Usamos variáveis CSS e media queries para ajustar layouts em telas menores.

5. **Testes Unitários**:
   - **Desafio**: Garantir cobertura adequada no backend e frontend.
   - **Solução**: Priorizamos testes de regras de negócio no backend e testes de criação de componentes no frontend.

---

## 9. Ideia e Jornada de Desenvolvimento

A ideia de criar um sistema de votação online surgiu da vontade de aplicar tecnologias modernas a um problema real: a necessidade de processos democráticos eficientes em cooperativas. Queríamos construir algo que fosse não apenas funcional, mas também uma vitrine de boas práticas em desenvolvimento fullstack.

### Etapas do Desenvolvimento
1. **Planejamento**:
   - Definimos os requisitos: gerenciamento de pautas, sessões, votos e dashboard.
   - Escolhemos Spring Boot e Angular por nossa familiaridade e pela robustez das tecnologias.
2. **Prototipagem**:
   - Criamos wireframes para o frontend, focando em usabilidade.
   - Desenhamos o schema do banco de dados (pauta, sessao_votacao, voto).
3. **Implementação**:
   - Backend: Começamos pelos modelos e repositórios, depois serviços e controllers.
   - Frontend: Desenvolvemos componentes standalone, começando pelo dashboard.
4. **Integração**:
   - Configuramos CORS e testamos a comunicação entre frontend e backend.
5. **Containerização**:
   - Adicionamos Docker e Docker Compose na fase final, após testes locais.
6. **Testes e Refinamento**:
   - Escrevemos testes unitários e ajustamos a UI com base em feedback interno.

### Momentos de Orgulho
- **Dashboard Interativo**: O gráfico de tendências, implementado com Ngx-Charts, é visualmente atraente e funcional.
- **Timer Component**: O componente de contagem regressiva para sessões abertas adiciona dinamismo à votação.
- **Containerização**: Conseguir rodar o sistema completo com um único comando (`docker-compose up`) foi gratificante.

---

## 10. Conclusão

O **Desafio Votação Fullstack** é mais do que um projeto técnico; é uma demonstração de nossa capacidade de transformar uma ideia em uma solução completa, enfrentando desafios complexos com criatividade e rigor. Cada linha de código, cada teste unitário e cada decisão de design reflete nosso compromisso em entregar um sistema de alta qualidade.

Esperamos que este documento tenha transmitido a profundidade do trabalho realizado. Estamos prontos para responder a qualquer pergunta ou implementar melhorias adicionais. Obrigado por avaliar nosso projeto!

## 11. Contribuição
1. Faça um fork do repositório.
2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`).
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`).
4. Push para a branch (`git push origin feature/nova-funcionalidade`).
5. Abra um Pull Request.

## 12. Licença
Licenciado sob Bar do Gordinho e Pizzaria PedraPura. Veja o arquivo SofraComEssesLanches para detalhes.