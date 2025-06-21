# 🏆 Titule - Plugin de Títulos Personalizados
Titule é um plugin para Minecraft que permite aos jogadores terem títulos personalizados e coloridos exibidos antes de seus nomes no chat. Ele é totalmente compatível com servidores Folia, garantindo desempenho otimizado em ambientes multithread.
# 🔥 Principais Recursos

   ✅ Banco de Dados SQLite: Salva e recupera títulos automaticamente.
   
   ✅ Compatibilidade com Folia: Usa player.getScheduler() para segurança em multi-threading.
   
   ✅ Títulos Aleatórios: Jogadores recebem um título colorido e aleatório ao usar /titulo.
   
   ✅ Sistema de Cores do Arco-Íris: Cada título tem uma cor única para estilo visual.
   
   ✅ Recuperação Automática: Jogadores mantêm seus títulos ao entrar no servidor.
   
# ⚙️ Como Funciona
   1️⃣ Jogador entra no servidor → O plugin busca o título no banco de dados.
   
   2️⃣ Se o título existe → Exibe o nome do jogador com o título salvo.
   
   3️⃣ Se não existe → Define um título padrão.
   
   4️⃣ Jogador usa /titulo → O plugin escolhe um título aleatório e uma cor do arco-íris.
   
   5️⃣ O título é salvo no SQLite → Garantindo que permaneça no próximo login.
   
   6️⃣ O título aparece no chat e na tag do jogador.

O fluxograma do funcionamento do plugin está chegando! Espero que ajude a organizar a estrutura do projeto! 🚀💡
Se precisar de mais ajustes, me avise! 😃🔥

suporte de um assistente de IA. Copilot

![A flowchart illustra](https://github.com/user-attachments/assets/406cb6d0-d64a-433a-be1d-3430bd1780c2)

#  Explicação resumida por comando 
| Comando | O que faz | 

| /tnt <nome> | Registra a fábrica na posição da TNT e calcula o OBSERVER automático | 

| /verificatnt <nome> |  TNT Cuidado este comando e invertido Nao use so se quiser fazer tudo de novo| 

| /resetarfabricas | Apaga todos os registros no banco (DELETE + VACUUM) | 

| /listfabric | Mostra todas as fábricas salvas com coordenadas e status de agendamento | 

| /ativarfabrica <nome> | Marca a fábrica para que seja agendada automaticamente | 

| /desativarfabrica <nome> | Impede que a fábrica seja agendada automaticamente | 

| /titulo | (Comando decorativo) Dá um título aleatório ao jogador | 


![fluxograma dos coman](https://github.com/user-attachments/assets/607277e6-fd03-471d-bb7b-230a7d7ac77a)


             +----------------------+
             |  /tnt <nome>         |
             |  ⤷ Registra fábrica  |
             +----------------------+
                        |
                        v
           +------------------------------+
           |  Salva nome, mundo, coords  |
           |  da TNT e Observer no banco |
           +------------------------------+

                        ↓

        +---------------------------+        +---------------------------+
        | /verificatnt <nome>       |        | /resetarfabricas          |
        | ⤷ destroi a fábrica       |        | ⤷ Limpa TODAS as fábricas |
        +---------------------------+        +---------------------------+
                   |                                   |
                   v                                   v
     Lê info do banco e coloca:             Deleta registros e faz limpeza
     - Cuidado na posição 
     - TNT explode


                        ↓
          +------------------------------+
          |  /listfabric                 |
          |  ⤷ Lista todas as fábricas  |
          +------------------------------+
                  Exibe:
                  - Nome
                  - Mundo
                  - TNT (X, Y, Z)
                  - Status: ✅ Ativa | ⛔ Inativa


                        ↓
         +------------------------------+
         |  /ativarfabrica <nome>       |
         |  ⤷ Habilita agendamento      |
         +------------------------------+

         +------------------------------+
         |  /desativarfabrica <nome>    |
         |  ⤷ Desativa agendamento      |
         +------------------------------+

       ⚙ Executado automaticamente no `onEnable()`:  
       → `AgendadorTNT` agenda TNTs de fábricas ativas

eu revivi o metodo deste neste video

![image](https://github.com/user-attachments/assets/252b1e72-fbe3-4f47-8e9f-c5cf0bfc3341)

      https://www.youtube.com/watch?v=ecdRaJMA130

   ![image](https://github.com/user-attachments/assets/cb6cd373-957f-44e3-ac79-e82575f171e9)

# No meu caso eu chamei a fabrica de f1

/tnt f1

/ativarfabrica f1

/desativarfabrica f1

