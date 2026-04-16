# Regras de Negócio - Sistema de Gestão para Clínicas de Psicologia

## 1. Introdução, Propósito e Sustentabilidade
Este sistema nasce como uma iniciativa de apoio social a profissionais da psicologia e clínicas em fase de formação. O objetivo central é oferecer uma ferramenta robusta de gestão que reduza a carga administrativa e permita que o foco permaneça no atendimento clínico. 

Diferente de softwares comerciais tradicionais, este projeto opera sob um modelo de **Custeio Proporcional por Rateio**. A lógica por trás dessa solução é garantir a viabilidade financeira para pequenas unidades: o sistema gera relatórios detalhados de consumo de infraestrutura (armazenamento de mídias, tráfego de dados e envio de notificações), permitindo que os custos operacionais do servidor sejam divididos de forma justa entre as clínicas participantes, sem fins lucrativos sobre a plataforma em si.

## 2. Experiência do Usuário e Filosofia de Design
A interface do sistema é projetada com foco na **Usabilidade e Acessibilidade**. Entendemos que o ambiente clínico exige concentração e agilidade; por isso, a experiência de uso é simplificada para reduzir a curva de aprendizado e a carga cognitiva. O software deve ser funcional e responsivo em múltiplos dispositivos (computadores, tablets e smartphones), permitindo que o profissional consulte informações rapidamente entre uma sessão e outra, ou que o gestor monitore a clínica remotamente.

## 3. Governança de Dados e Segurança da Informação
A segurança em um sistema de saúde não é apenas uma funcionalidade técnica, mas uma obrigação ética e legal. Para atender às normas de sigilo profissional e à LGPD, o sistema implementa uma **Arquitetura de Isolamento Clínico**. 

O acesso ao **Registro Clínico (Tela do Paciente)** é **exclusivo e absoluto** do profissional de saúde responsável. Em nenhuma hipótese, sob qualquer justificativa administrativa ou de urgência, outras pessoas (incluindo gestores da clínica ou administradores do sistema) podem ter acesso ao conteúdo clínico. 

**Exceções Controladas de Acesso:**
1.  **Supervisão de Estagiários:** Quando o paciente é atendido por um **Estagiário**, o **Supervisor** designado tem acesso aos registros na **Tela do Paciente** para orientação técnica.
2.  **Uso Acadêmico (Clínica Escola):** Se a clínica for configurada como **Clínica Escola**, os dados do paciente podem ser acessados para fins de estudo e pesquisa, desde que haja um **Contrato de Autorização para Uso Acadêmico** assinado digitalmente ou com upload do documento assinado pelo paciente e pelo gestor/supervisor.

**Política de Retenção e Não-Exclusão Física:**
- Adotamos a política de **Exclusão Lógica Permanente**. **Nenhum dado é removido fisicamente do banco de dados**, garantindo a integridade dos logs de auditoria e do histórico de rateio de custos.
- O **Gestor da Clínica** tem a opção de marcar os dados de um paciente como "Desativado/Arquivado" após o prazo legal de **5 anos de inatividade**. Uma vez desativado, o dado torna-se invisível para operações do dia a dia, mas permanece na base de dados para fins jurídicos e de auditoria.

## 4. Estrutura Multi-Clínica e Isolamento Operacional
O sistema foi concebido sob o conceito de **Independência Multitenant**. Cada clínica opera em um ambiente lógico totalmente estanque. Uma unidade jamais tem visibilidade sobre os dados de outra.

## 5. Gestão de Perfis, Responsabilidades e Controle de Acesso
A hierarquia de acesso é estruturada para refletir a realidade operacional de uma clínica:

- **Administrador de Rede (Super-Admin):** Gestão de infraestrutura. Sem acesso a dados de pacientes.
- **Gestor da Clínica (Dono):** Autoridade administrativa máxima. Responsável por transicionar a modalidade da clínica, gerenciar finanças e monitorar a **Aba de Acompanhamento**.
- **Profissional de Saúde:** Custodiante técnico com acesso exclusivo aos seus pacientes na **Tela do Paciente**.
- **Atendente:** Logística de agendamentos. Impedido de acessar conteúdo clínico.
- **Estagiário / Aluno:** Registros na **Tela do Paciente** visíveis para si e para o supervisor.

## 6. Logística de Agendas, Recorrência e Priorização de Urgência
A gestão de horários suporta **Agendamentos Recorrentes**. Atendimentos de **Urgência Clínica** possuem prioridade, mas o acesso ao histórico permanece restrito ao profissional responsável.

## 7. Gestão Financeira, Integridade de Caixa e Selagem de Períodos
O módulo financeiro opera como um **Livro Caixa Digital** com **Selagem Retroativa** após o 5º dia útil do mês seguinte.

## 8. Governança Clínica e Ciclo de Vida do Registro do Paciente

### 8.1 Cadastro Integral e Triagem
A triagem biopsicossocial inicial, acessível na **Tela do Paciente**, deve obrigatoriamente registrar a ocorrência de **Surto Psicótico**. O upload do **TCLE** é obrigatório para iniciar qualquer registro de atendimento.

### 8.2 Registro Multimídia e Imutabilidade
O sistema permite anexos de mídia na **Tela do Paciente**. Após a **Janela de Retificação de 24 horas**, o registro torna-se imutável.

## 9. Gestão de Espaços e Sublocação
Gerenciamento de salas físicas com bloqueio estrito para evitar conflitos.

## 10. Inteligência Artificial e Estratégia de Crescimento
IA auxilia no marketing ético e alertas de evasão, respeitando o sigilo absoluto.

## 11. Gestão Estratégica e Aba de Acompanhamento (Supervisão)
O Gestor da Clínica monitora a qualidade da unidade através de indicadores quantitativos:

- **Monitoramento de Fluxo:** Visualização de faltas, pacientes atendidos e pagamentos realizados.
- **Status de Conformidade Documental:** O gestor vê se o **Registro Clínico (Tela do Paciente)** foi preenchido corretamente (indicador de "concluído/pendente"), sem nunca ter acesso ao texto escrito pelo profissional.
- **Reputação Digital:** O sistema busca consolidar o máximo de dados possíveis de redes sociais e Google Reviews para gerar um índice de percepção da clínica.
- **Currículo e Identificação:** Registro do currículo e número da carteira profissional (CRP/CRM) de todos os profissionais de saúde.

## 12. Modelo de Clínica Escola
- **Configuração:** O gestor escolhe entre Clínica Padrão ou Clínica Escola.
- **Contrato Acadêmico:** Para uso de dados em estudo, é obrigatório o upload do contrato assinado pelo paciente e pelo gestor no sistema. Sem este documento, o acesso acadêmico é bloqueado.
