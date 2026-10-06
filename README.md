# Gerenciador de Estoque (Kardex)

Um sistema completo de gerenciamento de estoque para pequenas e médias empresas, projetado para controlar movimentações, categorias, produtos, e fornecer relatórios em tempo real. O sistema é robusto, seguro e foi preparado para fácil deploy em serviços de nuvem (PaaS).

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17** com **Spring Boot 3.x**
- **Spring Security + JWT** (Autenticação e Autorização via Token)
- **Spring Data JPA** & **Hibernate**
- **PostgreSQL** (Banco de Dados)
- **Flyway** (Migrations do Banco de Dados)
- **Maven** (Gerenciamento de dependências)

### Frontend
- **React 19** com **TypeScript**
- **Vite** (Build tool rápida)
- **Tailwind CSS** (Estilização utilitária e responsiva)
- **React Router Dom** (Navegação SPA)
- **Axios** (Integração com API REST)
- **Zod & React Hook Form** (Validação de formulários)
- **Lucide React** (Ícones)
- **Recharts** (Gráficos e dashboards)

## 📦 Como rodar localmente (Desenvolvimento)

### Pré-requisitos
- [Java 17+](https://adoptium.net/)
- [Node.js 18+](https://nodejs.org/)
- Banco de Dados PostgreSQL rodando localmente na porta `5432` com usuário/senha `estoque`. (Você pode alterar isso no arquivo `application.yml`).

### 1. Backend
Abra o terminal na pasta `backend/`:
```bash
# Baixe as dependências e compile o projeto
./mvnw clean install

# Rode a aplicação
./mvnw spring-boot:run
```
A API estará rodando em `http://localhost:8080`.

### 2. Frontend
Abra o terminal na pasta `frontend/`:
```bash
# Instale as dependências
npm install

# Rode o servidor de desenvolvimento
npm run dev
```
O frontend estará rodando em `http://localhost:5173`.

## ☁️ Deploy em Produção (PaaS)

O projeto já está estruturado para deploy simplificado em plataformas como **Railway**, **Render** (para o Backend) e **Vercel** ou **Netlify** (para o Frontend).

### Deploy do Backend (Railway/Render)
1. Conecte sua conta do GitHub na plataforma escolhida (ex: Railway).
2. Crie um novo projeto importando o repositório.
3. Configure o _Root Directory_ para `backend`.
4. Defina as seguintes variáveis de ambiente:
   - `DB_URL` (URL do banco de dados provisionado, ex: `jdbc:postgresql://host:porta/banco`)
   - `DB_USER` e `DB_PASSWORD`
   - `JWT_SECRET` (Sua chave secreta para geração de tokens)
   - `CORS_ORIGINS` (A URL de produção do seu frontend, ex: `https://meu-estoque.vercel.app`)

### Deploy do Frontend (Vercel)
1. Crie um projeto na [Vercel](https://vercel.com/) e importe este repositório.
2. Defina o _Framework Preset_ para **Vite**.
3. Defina o _Root Directory_ para `frontend`.
4. Adicione a variável de ambiente:
   - `VITE_API_URL` apontando para a URL do seu backend gerada no passo anterior (ex: `https://meu-backend.railway.app/api`).
5. A Vercel fará o build com `npm run build` e aplicará a configuração do `vercel.json` automaticamente para garantir o funcionamento correto das rotas do React.

## 🤝 Contribuindo
Sinta-se livre para abrir _Issues_ e enviar _Pull Requests_. Ao contribuir com o repositório, garanta que o código passe pelos linters (`npm run lint` e no build do Vite) e que os testes do backend (se aplicável) passem sem erros.

---
_Desenvolvido com dedicação para otimizar operações logísticas._
