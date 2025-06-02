![Diagrama](teamtacles_banner.jpg)
# Teamtacles-API
**Seu gerenciador de tarefas em equipe, direto do fundo do mar. 🦑🌊**

---
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white) 	![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)

O **Teamtacles-API** é uma API RESTful desenvolvida utilizando **Java, Spring Boot, Flyway para migrações de banco de dados, MySQL como banco de dados e Spring Security com autenticação baseada em JWT**.

A API visa **auxiliar o gerenciamento de tarefas em equipe**, promovendo a colaboração e a produtividade durante a realização de projetos. O **Teamtacles-API** é ideal para times que estão se sentindo **afogados no mar de tarefas** e desejam o **apoio de tentáculos** na organização e gerenciamento, tornando o fluxo de trabalho mais **eficiente e colaborativo**.

## Assuntos
---
- [✨ Funcionalidades Implementadas](#-funcionalidades-implementadas)
- [🖥️ Instruções de Execução Local](#-instruções-de-execução-local)
- [🔑 Como obter o token JWT e testar os endpoints](#-como-obter-o-token-jwt-e-testar-os-endpoints)
- [🗃️ Resumo do modelo de dados e regras de validação](#-resumo-do-modelo-de-dados-e-regras-de-validação)
- [🛡️ Descrição do Funcionamento da Autenticação e Autorização](#-descrição-do-funcionamento-da-autenticação-e-autorização)
## ✨ Funcionalidades Implementadas
---

**🔐 Autenticação e Autorização**
- Cadastro de usuários com nome, e-mail e senha;
- Login com geração de token JWT;
- Controle de acesso por papéis: USER e ADMIN.

**🗂️ Gestão de Projetos e Tarefas**
- Criação de projetos;
- CRUD de tarefas vinculadas a projetos: título, descrição, prazo e status;
- Controle de permissões: usuários só editam/excluem suas tarefas;
- Visualização das tarefas dos projetos onde os usuários participam;
- Alteração de status das tarefas (ex: "em andamento" → "concluída");
- Administradores podem visualizar e excluir qualquer tarefa ou projeto;

**📊 Filtros e Relatórios**
- Filtros de tarefa por status, prazo e projeto;
- Listagem de tarefas por status e projeto;
- Consulta de tarefas de usuários específicos para análise de carga de trabalho.

## 🖥️ Instruções de Execução Local
---
**Pré-requisitos**
- Java JDK 21 ou superior
- Maven 3.6 ou superior
- MySQL

**Build**
1. Clone o repositório:
```bash 
git clone https://github.com/TeamTacles/Teamtacles-API.git
```

2. Configurar as variáveis de ambiente no `application.propierties`

3. Executar o build
```bash
mvn clean install
```

**Run**
1. Executar o projeto
```bash 
mvn spring-boot:run
```

2. Acessar a API em [http://localhost:8080](http://localhost:8080)

## 🔑 Como obter o token JWT e testar os endpoints
---
### 1. Registrar um novo usuário
Para criar uma conta nova, envie uma requisição `POST` para o endpoint de registro:
```POST /api/user/register```

**Exemplo de corpo JSON:**

```
{
  "userName": "seu_usuario",
  "email": "seu_usuario@example.com",
  "password": "senhaSegura123",
  "passwordConfirm": "senhaSegura123"
}
```

### 2. Fazer login para obter o token JWT
Depois de registrar (ou usando o usuário ADMIN padrão, se preferir), faça login enviando uma requisição `POST` para:
```POST /api/auth/authenticate```

**Exemplo de corpo JSON:**
```
{
  "userName": "seu_usuario",
  "password": "senhaSegura123"
}
```

Se as credenciais estiverem corretas, você receberá um **token JWT** na resposta:
```
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. Testar endpoints protegidos com o token JWT
Para acessar os endpoints que exigem autenticação, envie o token no cabeçalho ```Authorization``` da requisição:
```
Authorization: Bearer <seu_token_aqui>
```

### 4. Usuário ADMIN padrão (para testes)
O projeto já inclui um **usuário ADMIN** criado no banco com as seguintes credenciais:

- Usuário: admin
- Senha: admin123

Você pode usar esse usuário para fazer login e testar a API sem precisar registrar.

**Observação:**
Sempre que o token expirar, faça login novamente para obter um novo token.

## 🗃️ Resumo do modelo de dados e regras de validação
---
### 1. User

**Campos:**

- userId (Long) — Gerado automaticamente.
- userName (String) — Obrigatório, tamanho entre 3 e 50 caracteres.
- email (String) — Obrigatório, válido, tamanho entre 8 e 50 caracteres.
- password (String) — Obrigatório, tamanho entre 5 e 100 caracteres.
- task (List\<Task>) — Lista de tarefas que o usuário possui.
- createdProjects (List\<Project>) — Lista de projetos que o usuário criou.
- projects (List\<Project>) — Lista de projetos que o usuário participa.
- roles (Set\<Role>) — Papel (roles) atribuido ao usuário.

**Relacionamentos:**

- 1:N com Task (como dono).
- 1:N com Project (como criador).
- N:M com Project (como membro da equipe).
- N:M com Role.

### 2. Project

**Campos:**

- id (Long) — Gerado automaticamente.
- title (String) — Obrigatório, máximo de 50 caracteres.
- description (String) — Opcional, máximo de 50 caracteres.
- tasks (List\<Task>) — Lista de tarefas associadas.
- creator (User) — Usuário criador (obrigatório).

team — Lista de usuários que participam do projeto.

**Relacionamentos:**
- 1:N com Task.
- N:1 com User (criador).
- N:M com User (equipe).

### 3. Task

**Campos:**

- id (Long) — Gerado automaticamente.
- title (String) — Obrigatório, máximo de 50 caracteres.
- description (String) — Opcional, máximo de 250 caracteres.
- dueDate (LocalDateTime) — Obrigatório, deve ser uma data futura.
- status (Enum Status) — Obrigatório (ver valores possíveis).
- owner (User) — Usuário dono da task (obrigatório).
- usersResponsability (List\<User>) — Lista de usuários responsáveis.
- project (Project) — Projeto associado (obrigatório).

**Relacionamentos:**

- N:1 com User (owner).
- N:M com User (responsáveis).
- N:1 com Project.

**Validações:**
- dueDate precisa ser no futuro.
- title e description com limites de caracteres.

### 4. Role

**Campos:**
- id (Long) — Gerado automaticamente.
- roleName (Enum ERole) — Obrigatório e único (ver valores possíveis).

**Relacionamentos:**
- N:M com User.

### Enumerações
1. ERole - Define os papéis (roles) de um usuário:
    - USER
    - ADMIN

2. Status - Define o status de uma tarefa (Task):
    - TODO — A fazer
    - INPROGRESS — Em andamento
    - DONE — Concluído

**Observações importantes:**
- Chaves primárias são geradas automaticamente (`@GeneratedValue`).

- Validações são feitas via Bean Validation (`@NotBlank`, `@Size`, `@Email`, `@Future`).
- Serialização cuida de problemas de referência cíclica com `@JsonManagedReference` e `@JsonBackReference`.
- O modelo utiliza JPA para persistência e Spring Security para autenticação e autorização com UserDetails.

## 🛡️ Descrição do Funcionamento da Autenticação e Autorização
---
### 🔒 Autenticação
É utilizado o JWT (JSON Web Token) para autenticar os usuários.

- Após o login bem-sucedido, é retornado um token JWT, que deve ser incluído no header de todas as requisições aos endpoints protegidos.
- O token deve ser enviado da seguinte forma:
```
Authorization: Bearer <seu_token_jwt>
```
O usuário precisa estar previamente cadastrado no sistema para realizar o login e obter o token.

### 🛡️ Autorização
O controle de acesso aos endpoints é baseado nos papéis (roles) atribuídos aos usuários:
- **USER:** Acesso a recursos básicos.
- **ADMIN:** Acesso total, incluindo recursos administrativos.

A autorização é feita automaticamente pelo Spring Security, que verifica:
- Se o token JWT é válido.
- Se o usuário possui o papel necessário para acessar determinado endpoint.

### 🔑 Papéis e permissões
**Usuário comum (USER):**
- Pode visualizar e gerenciar suas próprias tarefas e projetos.
- Pode participar de projetos como membro da equipe.

**Administrador (ADMIN):**
- Pode realizar todas as operações do sistema, incluindo criar, atualizar e excluir qualquer recurso.