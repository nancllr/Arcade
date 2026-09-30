# 🕹️ Arcade Terminal

Projeto desenvolvido em **Java** para a disciplina de **Projeto de Programação** no **IntelliJ IDEA**, reunindo jogos clássicos em linha de comando (CLI) através de uma interface simples e modular.

---

## 📁 Estrutura de Pastas

```
Arcade/
├── src/                                # Código-fonte do projeto
│   └── main/
│       └── java/                       # Arquivos de código Java
│           ├── Main.java               # Menu principal e controle de fluxo do sistema
│           └── jogos/                  # Pacote contendo as lógicas e recursos dos jogos
│               ├── Forca.java          # Implementação e lógica do Jogo da Forca
│               └── palavras.txt        # Banco de dados de palavras para a Forca
├── .gitignore                          # Arquivos e pastas ignorados pelo Git
└── README.md                           # Descrição e documentação do projeto
```

---

🎯 Objetivo
* **Controle de Fluxo com `switch`:** Estruturar a navegação do menu principal para direcionar o usuário entre as opções do arcade de forma clara e organizada.
* **Tratamento de Exceções (`try-catch`):** Capturar falhas de conversão de dados e entradas inválidas do usuário, garantindo a estabilidade da aplicação sem travamentos no terminal.
* **Gerenciamento do `Scanner`:** Utilizar uma única instância da classe `Scanner` compartilhada entre os módulos para evitar o fechamento acidental da entrada de dados (`System.in`).
* **Leitura de Arquivos Externos:** Manipular arquivos de texto (`palavras.txt`) para carregar dinamicamente o banco de palavras do jogo da forca.

---

🕹️ Jogos Incluídos
* **Jogo da Forca:** Adivinhação de palavras com leitura dinâmica a partir de um arquivo `.txt`.
* **Demais Jogos**
