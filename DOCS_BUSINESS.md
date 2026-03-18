# Regras de Negócio - Sistema de Gestão para Clínicas de Psicologia

## 1. Introdução, Propósito e Sustentabilidade
Este sistema nasce como uma iniciativa de apoio social a profissionais da psicologia e clínicas em fase de formação. O objetivo central é oferecer uma ferramenta robusta de gestão que reduza a carga administrativa e permita que o foco permaneça no atendimento clínico. 

Diferente de softwares comerciais tradicionais, este projeto opera sob um modelo de **Custeio Proporcional por Rateio**. A lógica por trás dessa solução é garantir a viabilidade financeira para pequenas unidades: o sistema gera relatórios detalhados de consumo de infraestrutura (armazenamento de mídias, tráfego de dados e envio de notificações), permitindo que os custos operacionais do servidor sejam divididos de forma justa entre as clínicas participantes, sem fins lucrativos sobre a plataforma em si.

## 2. Experiência do Usuário e Filosofia de Design
A interface do sistema é projetada com foco na **Usabilidade e Acessibilidade**. Entendemos que o ambiente clínico exige concentração e agilidade; por isso, a experiência de uso é simplificada para reduzir a curva de aprendizado e a carga cognitiva. O software deve ser funcional e responsivo em múltiplos dispositivos (computadores, tablets e smartphones), permitindo que o profissional consulte informações rapidamente entre uma sessão e outra, ou que o gestor monitore a clínica remotamente.

## 3. Governança de Dados e Segurança da Informação
A segurança em um sistema de saúde não é apenas uma funcionalidade técnica, mas uma obrigação ética e legal. Para atender às normas de sigilo profissional e à LGPD, o sistema implementa uma **Arquitetura de Auditoria Integral**. 

O motivo dessa solução é garantir a rastreabilidade total: cada visualização de prontuário, alteração de dado financeiro ou tentativa de acesso é registrada em logs imutáveis. Em situações de exceção, o sistema exige uma justificativa formal, criando uma camada de responsabilidade sobre quem acessa dados sensíveis. Além disso, adotamos a política de **Exclusão Lógica (Soft Delete)**, onde nenhum dado é removido permanentemente da base. Isso assegura que a clínica sempre possua o histórico completo para fins de auditoria jurídica, mesmo que a informação não seja mais exibida nas telas operacionais do dia a dia.

## 4. Estrutura Multi-Clínica e Isolamento Operacional
O sistema foi concebido sob o conceito de **Independência Multitenant**. Isso significa que, embora múltiplas clínicas compartilhem a mesma infraestrutura, cada uma opera em um ambiente lógico totalmente estanque. 

O motivo para o isolamento rigoroso é garantir que uma unidade jamais tenha visibilidade sobre os dados, pacientes, agendas ou finanças de outra. Cada clínica possui sua própria configuração de funcionamento, onde o Gestor define os dias e horários de operação, garantindo que as regras de negócio de uma unidade (como horários de pico ou feriados locais) não interfiram na logística das demais.

## 5. Gestão de Perfis, Responsabilidades e Controle de Acesso
A hierarquia de acesso é estruturada para refletir a realidade operacional de uma clínica, garantindo que cada colaborador acesse apenas o necessário para sua função:

- **Administrador de Rede (Super-Admin):** Atua no nível de infraestrutura, gerenciando a criação de novas unidades e monitorando o relatório global de consumo para o rateio de custos.
- **Gestor da Clínica (Dono):** Detém a autoridade máxima administrativa da unidade. Ele é o responsável por configurar as regras de conformidade, gerenciar o corpo clínico e ter a visão macro das finanças.
- **Profissional de Saúde:** É o custodiante técnico do prontuário. Ele possui autonomia sobre sua agenda e acesso exclusivo aos dados clínicos dos seus pacientes, respeitando o sigilo profissional.
- **Atendente:** Focado na logística de recepção e agendamentos. Para proteger a privacidade do paciente, este perfil é impedido por padrão de acessar qualquer conteúdo clínico ou evoluções de prontuário.
- **Estagiário:** Como profissional em formação, suas ações são sempre vinculadas a um supervisor. O sistema permite um "Nível de Confiança" configurável, onde o supervisor decide se as reservas de sala do estagiário precisam de aprovação prévia ou se podem ser automatizadas, equilibrando autonomia e monitoramento pedagógico.

## 6. Logística de Agendas, Recorrência e Priorização de Urgência
A gestão de horários no sistema vai além de um simples calendário. Implementamos o suporte a **Agendamentos Recorrentes** para refletir a prática comum da psicoterapia, onde o paciente mantém o mesmo horário semanalmente. Uma vez confirmada, a recorrência bloqueia automaticamente a disponibilidade da sala e do profissional para o futuro.

Para lidar com a realidade de crises clínicas, estabelecemos uma **Matriz de Precedência de Atendimento**. O motivo é simples: a vida e a integridade do paciente vêm primeiro. Atendimentos de **Urgência Clínica** possuem prioridade absoluta, permitindo o remanejamento imediato de salas e agendas. O sistema automatiza a comunicação para que pacientes e profissionais afetados por um remanejamento de urgência sejam notificados instantaneamente, reduzindo o atrito na recepção.

## 7. Gestão Financeira, Integridade de Caixa e Selagem de Períodos
O módulo financeiro opera como um **Livro Caixa Digital**, focado na simplicidade do registro manual de entradas e saídas. A lógica é permitir que o atendente registre o pagamento real no momento em que ele ocorre, diferenciando a data em que o serviço foi prestado da data em que o dinheiro entrou no caixa.

Para evitar fraudes e garantir a segurança do gestor, implementamos a **Selagem Retroativa de Registros**. Após o 5º dia útil do mês seguinte, o período anterior é "fechado" e torna-se somente leitura. O motivo dessa regra é impedir que valores passados sejam alterados para mascarar desvios. Caso um pagamento antigo seja recebido após o fechamento, ele deve ser registrado como um "Recebimento Extemporâneo" no mês atual, mantendo o histórico de saldos intocado e auditável.

## 8. Governança Clínica e Ciclo de Vida do Prontuário

### 8.1 Cadastro Integral, Triagem e Menores de Idade
O cadastro do paciente é o ponto de partida para um atendimento seguro. Para menores de idade, o sistema exige obrigatoriamente os dados do **Responsável Legal**, garantindo que documentos e cobranças tenham validade jurídica. 

A inclusão de um **Perfil de Triagem e Anamnese** (hábitos de vida, saúde sexual, sono e medicamentos) serve para que o profissional tenha um panorama biopsicossocial do paciente desde o primeiro contato. O upload do **Termo de Consentimento Livre e Esclarecido (TCLE)** é o gatilho de segurança do sistema: sem ele, protocolos de compartilhamento de dados ou acessos de urgência permanecem bloqueados, protegendo a clínica contra processos por quebra de sigilo.

### 8.2 Prontuário Multimídia e Prazos de Conformidade
O prontuário é desenhado para ser uma linha do tempo fiel da evolução do paciente. Permitimos o anexo de mídias (fotos de desenhos, áudios de sessões ou vídeos) para enriquecer a análise clínica. 

Para garantir que o profissional mantenha a documentação em dia, estabelecemos um **Fluxo de Estados de Prontuário**. O registro inicial deve ocorrer em até 7 dias, mas o sistema alerta o gestor em 24 horas se nada for escrito, incentivando a disciplina documental. Após o primeiro salvamento, oferecemos uma **Janela de Retificação de 24 horas** para correções de erros de digitação; passado este prazo, o registro é consolidado e torna-se imutável, protegendo a integridade do documento clínico contra alterações retroativas indevidas.

## 9. Gestão de Espaços, Sublocação e Sustentabilidade
A gestão de salas físicas é um componente crítico para a saúde financeira da unidade. O sistema permite categorizar salas por especialidade (ex: Infantil, Grupo, Online), garantindo que o recurso certo seja usado para a finalidade correta. 

A lógica da **Sublocação de Salas** permite que o gestor maximize a ocupação da clínica, alugando espaços ociosos para parceiros externos. O sistema gerencia essas reservas com bloqueio estrito, impedindo que dois profissionais tentem usar o mesmo espaço simultaneamente, eliminando conflitos logísticos.

## 10. Inteligência Artificial e Estratégia de Crescimento
A Inteligência Artificial no sistema atua como um braço estratégico para o profissional e para a clínica. No marketing, ela auxilia na criação de conteúdos para redes sociais, mas com um **Filtro de Conformidade Ética** que impede a publicação de promessas de cura ou sensacionalismo. 

Na parte clínica, a IA analisa indicadores de humor e frequência para emitir **Alertas Preditivos de Evasão**, permitindo que o profissional intervenha antes que o paciente abandone o tratamento. Para o gestor, a IA oferece uma interface conversacional para consultar dados complexos, transformando números de faturamento e ociosidade em decisões inteligentes de negócio.

---

## 🛡️ 11. Procedimentos de Contingência e Gestão de Riscos Operacionais

Estes procedimentos são as "cláusulas de segurança" do sistema para garantir que a clínica nunca pare e que o profissional esteja protegido:

1.  **Custódia Compartilhada de Urgência:** Permite que, em caso de afastamento súbito do profissional titular, o Gestor autorize um substituto a acessar o histórico do paciente. O motivo é a continuidade do cuidado, sempre condicionada à existência do TCLE e auditada por justificativa formal.
2.  **Protocolo de Desligamento e Guarda Legal:** O sistema impede que um profissional seja desativado sem que ele exporte todos os seus prontuários. Isso garante que o psicólogo cumpra sua obrigação legal de guarda dos dados por anos, mesmo após sair da clínica.
3.  **Inatividade por Interrupção (Abandono):** O sistema identifica automaticamente pacientes que pararam de frequentar e não possuem agendamentos futuros. Ao atingir o prazo limite, o prontuário é "congelado", evitando que registros sejam inseridos fora do período real de atendimento e protegendo a integridade histórica do processo terapêutico.
