# 📚 Sistema de Gestão de Biblioteca 🚀

Bem-vindo ao Sistema de Gestão de Biblioteca! Uma aplicação robusta e amigável desenvolvida em JavaFX para simplificar o controlo e a organização de todos os recursos da sua biblioteca.

---

## ✨ Visão Geral do Projeto

Este sistema foi concebido para oferecer uma experiência de utilizador fluida e eficiente na gestão de livros, leitores, exemplares e registos de empréstimos. Com uma interface gráfica intuitiva, o controlo da sua biblioteca nunca foi tão fácil!

---

## 🌟 Funcionalidades Principais

* **📖 Gestão de Livros:**
    * Crie, atualize e remova registos de livros com facilidade.
    * Campos como título, ISBN, editora (usado como género), ano de publicação e **nome do autor (texto livre)** para máxima flexibilidade. Não é necessário pré-cadastrar autores!
* **👤 Gestão de Leitores:**
    * Mantenha um registo completo dos seus leitores, incluindo nome, morada, telefone e email.
    * Operações de criação, atualização e remoção disponíveis.
* **🤝 Gestão de Empréstimos:**
    * Registe novos empréstimos de forma intuitiva, selecionando leitores e livros através de `ComboBoxes` inteligentes que facilitam a pesquisa.
    * Controlo automático da disponibilidade dos exemplares: a quantidade é decrementada ao emprestar e incrementada ao devolver.
    * Registe devoluções de livros com um clique.
    * Opção para excluir registos de empréstimo.
* **📊 Visão Geral (Dashboard):**
    * Acompanhe o total de livros disponíveis na sua biblioteca num relance.
    * Visualize rapidamente os leitores e os seus empréstimos ativos.
* **🖥️ Navegação Dinâmica:**
    * A janela da aplicação adapta-se automaticamente ao tamanho ideal de cada ecrã (FXML) ao navegar, garantindo uma experiência visual otimizada e sem espaços em branco desnecessários.

---

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído com as seguintes ferramentas e linguagens:

* **Java 21:** A espinha dorsal da aplicação, garantindo performance e robustez.
* **JavaFX 21:** Para uma interface gráfica de utilizador moderna e responsiva.
* **MySQL:** O sistema de gestão de base de dados relacional para armazenamento seguro e eficiente dos dados.
* **JDBC (Java Database Connectivity):** A ponte entre o Java e o MySQL.
* **Maven:** Ferramenta essencial para gestão de dependências e automação do processo de `build`.

---

## ⚙️ Requisitos do Sistema

Para colocar este sistema a funcionar no seu ambiente, certifique-se de que tem instalado:

* **JDK (Java Development Kit) 21 ou superior.**
* **MySQL Server** (com um utilizador `root` e a senha configurada no `SistemaBiblioteca.java`).
* **Apache Maven 3.x**

---

## 🚀 Configuração e Execução

### 1. Configuração da Base de Dados

Primeiro, crie a base de dados e as tabelas necessárias no seu MySQL:

```sql
CREATE DATABASE biblioteca;
USE biblioteca;

CREATE TABLE Usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

INSERT INTO Usuarios (username, password) VALUES ('admin', 'admin'); -- Exemplo de credenciais de login

CREATE TABLE Livro (
    id_livro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    ISBN VARCHAR(20) NOT NULL UNIQUE, -- Ajustado para VARCHAR(20) para ISBN-13 com hífens
    editora VARCHAR(100), -- No modelo Java, é usado como 'genero'
    ano_publicacao INT,
    disponivel INT, -- Indica a disponibilidade geral do livro (pode ser 0 ou 1, mas a quantidade do exemplar é o que importa)
    nome_autor_livro VARCHAR(255) -- Nome do autor como texto livre
);

CREATE TABLE Autor (
    id_autor INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    nacionalidade VARCHAR(100)
);

-- Tabela de junção para relacionamento muitos-para-muitos (Livro_Autor)
-- Mantida para compatibilidade, mas o CRUD de Livros agora usa 'nome_autor_livro'
CREATE TABLE Livro_Autor (
    id_livro INT,
    id_autor INT,
    PRIMARY KEY (id_livro, id_autor),
    FOREIGN KEY (id_livro) REFERENCES Livro(id_livro),
    FOREIGN KEY (id_autor) REFERENCES Autor(id_autor)
);

CREATE TABLE Exemplar (
    id_exemplar INT AUTO_INCREMENT PRIMARY KEY,
    id_livro INT NOT NULL,
    estado VARCHAR(50), -- Ex: 'Novo', 'Bom', 'Usado'
    localizacao VARCHAR(100), -- Ex: 'Prateleira A1'
    quantidade INT NOT NULL DEFAULT 1, -- Quantidade de cópias disponíveis
    status VARCHAR(50), -- Ex: 'disponivel', 'emprestado' (status geral do exemplar)
    FOREIGN KEY (id_livro) REFERENCES Livro(id_livro)
);

CREATE TABLE Leitor (
    id_leitor INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(255),
    telefone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE Emprestimo (
    id_emprestimo INT AUTO_INCREMENT PRIMARY KEY,
    id_exemplar INT NOT NULL,
    id_leitor INT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE NOT NULL, -- Data de devolução prevista
    data_devolvida DATE, -- Data de devolução real (NULL se ainda não devolvido)
    FOREIGN KEY (id_exemplar) REFERENCES Exemplar(id_exemplar),
    FOREIGN KEY (id_leitor) REFERENCES Leitor(id_leitor)
);