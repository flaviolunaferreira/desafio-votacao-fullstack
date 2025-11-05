# Dockerfile para desenvolvimento Angular
FROM node:18-alpine
WORKDIR /app

# Copiar package.json e package-lock.json
COPY package*.json ./

# Instalar dependências
RUN npm ci

# Copiar código fonte
COPY . .

# Expor porta 4200 (padrão do Angular)
EXPOSE 4200

# Comando para rodar Angular em modo desenvolvimento
CMD ["npm", "run", "start", "--", "--host", "0.0.0.0", "--port", "4200"]
