# Regras de Negócio - Clínica de Psicologia (Comunitário e Multi-Clínica)

## 1. Objetivo e Contexto
O sistema é uma iniciativa de apoio a psicólogos e clínicas em formação para facilitar a gestão profissional. Caso existam custos, estes serão divididos proporcionalmente entre as clínicas participantes para cobrir apenas os gastos operacionais de manutenção.

## 2. Experiência do Usuário
- **Acessibilidade:** O sistema deve ser acessível por diferentes dispositivos para que profissionais e pacientes consultem informações rapidamente.
- **Facilidade de Uso:** A interface deve ser simples e intuitiva, adequada para pessoas com diferentes níveis de experiência com ferramentas digitais.

## 3. Confidencialidade e Privacidade (Prioridade Máxima)
- **Sigilo Profissional:** As notas de sessões e informações clínicas pertencem ao paciente e ao profissional responsável, não sendo acessíveis por outros membros da clínica sem autorização.
- **Proteção de Dados:** Todas as informações dos pacientes devem ser tratadas com o mais alto nível de segurança e privacidade.
- **Rastreabilidade:** Deve haver um registro de quem visualizou ou alterou informações sensíveis.

## 4. Estrutura das Clínicas
- **Independência:** Cada clínica funciona como uma unidade isolada. Uma clínica não tem visibilidade sobre os dados, pacientes ou agendas de outra clínica.
- **Personalização:** Cada unidade pode utilizar sua própria marca e cores para se identificar com seus pacientes.

## 5. Gestão de Pessoas e Perfis de Acesso

### 5.1 Administrador Geral do Sistema (Super-Admin / Gestor da Rede)
- Possui visão global de todas as clínicas cadastradas.
- Responsável por cadastrar e gerenciar os **Gestores da Clínica**.
- Monitora a utilização geral do sistema e suporte técnico.

### 5.2 Gestor da Clínica (Dono / Proprietário)
- Possui controle administrativo total sobre sua unidade específica.
- Pode atuar também como **Profissional de Saúde** (realizando atendimentos).
- Responsável pela gestão financeira, cadastro de funcionários e configuração da identidade visual da clínica.

### 5.3 Profissional de Saúde (Psicólogo / Terapeuta / Parceiro)
- Responsável pelos atendimentos e preenchimento de prontuários.
- Tem acesso exclusivo aos dados clínicos dos seus próprios pacientes (salvo em casos de compartilhamento autorizado).
- Pode ser um funcionário fixo ou um profissional que subloca o espaço.

### 5.4 Atendente da Clínica (Secretário / Administrativo)
- Responsável pela recepção, agendamentos e controle básico de entradas financeiras (recebimento de consultas).
- Não possui acesso aos prontuários ou notas clínicas dos pacientes.

### 5.5 Estagiário (Em Formação)
- Atua sob a responsabilidade direta de um **Supervisor** (Profissional de Saúde ou Gestor).
- Deve estar obrigatoriamente vinculado a um supervisor no sistema para que suas notas e atendimentos sejam validados e monitorados.
- O acesso aos dados é restrito aos pacientes designados pelo seu supervisor.

## 6. Agendas e Consultas
- **Agendas Individuais:** Cada profissional gerencia seus próprios horários.
- **Controle de Conflitos:** O sistema impede a marcação de dois eventos no mesmo horário para o mesmo profissional ou para o mesmo paciente.

## 7. Gestão Financeira
- **Controle de Caixa:** Registro detalhado de todos os pagamentos recebidos por atendimentos e todas as despesas pagas pela clínica.
- **Visibilidade Financeira:** O resumo financeiro da clínica é restrito aos gestores e funcionários administrativos.
- **Divisão de Custos:** Possibilidade de calcular a divisão das despesas de manutenção entre os membros da clínica ou entre as clínicas participantes.
